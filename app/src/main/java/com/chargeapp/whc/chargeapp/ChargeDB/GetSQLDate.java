package com.chargeapp.whc.chargeapp.ChargeDB;


import android.os.AsyncTask;


import android.util.Log;


import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.EleSetCarrier;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetSQLDate extends AsyncTask<Object, Integer, String> {
    private final static String TAG = "GetSQLDate";
    private Object object;
    private Gson gson=new Gson();
    private ChargeAPPDB chargeAPPDB;
    private int isNoExist=0,year,month,day;
    private InvoiceDB invoiceDB;
    private CarrierDB carrierDB;
    private String user,password;
    private SimpleDateFormat sd=new SimpleDateFormat("yyyy/MM/dd");

    public GetSQLDate(Object object, ChargeAPPDB chargeAPPDB) {
        this.object=object;
        this.chargeAPPDB=chargeAPPDB;
        invoiceDB=new InvoiceDB(chargeAPPDB.getReadableDatabase());
        carrierDB=new CarrierDB(chargeAPPDB.getReadableDatabase());
    }


    @Override
    protected String doInBackground(Object... params) {
        String action = params[0].toString();
        String jsonIn=null;
        String url;
        HashMap<String,String> data;
        try {
            if(action.equals("getInvoice"))
            {
                Calendar cal=Calendar.getInstance();
                SimpleDateFormat sf=new SimpleDateFormat("yyyy/MM/dd");
                sf.setCalendar(cal);
                year=cal.get(Calendar.YEAR);
                month=cal.get(Calendar.MONTH);
                day=cal.get(Calendar.DAY_OF_MONTH);
                user=params[1].toString();
                password=params[2].toString();
                String endDate=sf.format(new Date(cal.getTimeInMillis()));
                cal.set(year,month,1);
                String startDate=sf.format(new Date(cal.getTimeInMillis()));
//                while (isNoExist<3)
//                {
                    Log.d(TAG, "startDate: "+startDate+"endDate"+endDate+"isNoExist"+isNoExist);
                    data=getInvoice(user,password,"2017/05/01","2017/05/31");
                    month=month-1;
                    if(month<=0)
                    {
                        month=12;
                        year=year-1;
                    }
                    cal.set(year,month,1);
                    startDate=sf.format(cal.getTime());
                    cal.set(year,month,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    endDate=sf.format(cal.getTime());
                    url="https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?";
                    jsonIn = getRemoteData(url,data);
                    if(jsonIn.indexOf("919")!=-1)
                    {
                        jsonIn="error";
//                        break;
                    }
                    if(jsonIn.indexOf("200")==-1)
                    {
                        isNoExist++;
//                        continue;
                    }
                    getjsonIn(jsonIn,password,user);
//                }
            }else if(action.equals("GetToday"))
            {
                Calendar today=Calendar.getInstance();
                long maxtime=invoiceDB.findIVByMaxDate();
                Calendar last=Calendar.getInstance();
                last.setTime(new Date(maxtime));
                int todayyear=today.get(Calendar.YEAR);
                int todaymonth=today.get(Calendar.MONTH);
                int todayday=today.get(Calendar.DAY_OF_MONTH);
                int lastyear=last.get(Calendar.YEAR);
                int lastmonth=last.get(Calendar.MONTH);
                int lastday=last.get(Calendar.DAY_OF_MONTH);
                if(todayyear==lastyear&&todaymonth==lastmonth)
                {
                    searchTodayDate(last,today);
                }

            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return jsonIn;
    }

    public void searchTodayDate(Calendar last,Calendar today) throws IOException {
        SimpleDateFormat sf=new SimpleDateFormat("yyyy/MM/dd");
        sf.setCalendar(last);
        String startday=sf.format(last.getTime());
        sf.setCalendar(today);
        String endday=sf.format(today.getTime());
        List<CarrierVO> carrierVOS=carrierDB.getAll();
        HashMap data;
        for(CarrierVO c:carrierVOS)
        {
            Log.d(TAG, "startDate: "+startday+"endDate"+endday+"user"+c.getCarNul());
            data=getInvoice(c.getCarNul(),c.getPassword(),startday,endday);
            String url="https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?";
            String jsonIn = getRemoteData(url,data);

            if(jsonIn.indexOf("919")!=-1)
            {
                jsonIn="error";
                break;
            }
            if(jsonIn.indexOf("200")==-1)
            {
                continue;
            }
            Calendar cal=last;
            Timestamp start=new Timestamp(cal.getTimeInMillis()-86400000);
            Timestamp end=new Timestamp(cal.getTimeInMillis()+86400000);
            List<InvoiceVO> newInvoicelist=todayjsonIn(jsonIn,c.getPassword(),c.getCarNul());
            List<InvoiceVO> oldInvoicelist=invoiceDB.getInvoiceBytime(start,end,c.getCarNul());
            for(InvoiceVO i:newInvoicelist)
            {
                 for (InvoiceVO old:oldInvoicelist)
                 {
                     if(old.getInvNum().equals(i.getInvNum()))
                     {continue;}
                     invoiceDB.insert(i);
                 }
            }
        }
    }

    private List<InvoiceVO> todayjsonIn(String jsonIn,String password,String user) {
        List<InvoiceVO> list=new ArrayList<>();
        try {
            InvoiceVO invoiceVO;
            JsonObject js=gson.fromJson(jsonIn,JsonObject.class);
            Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
            String s=js.get("details").toString();
            List<JsonObject> b=gson.fromJson(s,cdType);
            for (JsonObject j:b)
            {
                invoiceVO=jsonToInVoice(j,password,user);
                list.add(invoiceVO);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }


    private void getjsonIn(String jsonIn,String password,String user) {
        try {
            InvoiceVO invoiceVO;
            JsonObject js=gson.fromJson(jsonIn,JsonObject.class);
            Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
            String s=js.get("details").toString();
            List<JsonObject> b=gson.fromJson(s,cdType);
            for (JsonObject j:b)
            {
                Log.d(TAG,j.toString());
                invoiceVO=jsonToInVoice(j,password,user);
                invoiceDB.insert(invoiceVO);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private InvoiceVO jsonToInVoice(JsonObject j,String password,String user) throws IOException {
        InvoiceVO  invoiceVO=new InvoiceVO();
        invoiceVO.setAmount(j.get("amount").getAsString());
        invoiceVO.setCardEncrypt(password);
        invoiceVO.setCardNo(j.get("cardNo").getAsString());
        invoiceVO.setCardType(j.get("cardType").getAsString());
        invoiceVO.setDonateMark(j.get("donateMark").getAsString());
        invoiceVO.setInvNum(j.get("invNum").getAsString());
        invoiceVO.setInvDonatable(j.get("invDonatable").getAsString());
        invoiceVO.setSellerName(j.get("sellerName").getAsString());
        JsonObject jtime=gson.fromJson(j.get("invDate").toString(),JsonObject.class);
        Log.d(TAG,j.toString());
        String time=String.valueOf(jtime.get("year").getAsInt()+1911)+"-"+jtime.get("month").getAsString()+"-"+jtime.get("date").getAsString()+" "+j.get("invoiceTime").getAsString();

        invoiceVO.setTime(Timestamp.valueOf(time));
        invoiceVO.setCarrier(user);
        invoiceVO.setCardEncrypt(password);
        HashMap<String,String> data=getInvoicedetail(invoiceVO);
        String urldetail="https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?";
        String detailjs=getRemoteData(urldetail,data);
        JsonObject jsonObject=gson.fromJson(detailjs,JsonObject.class);
        invoiceVO.setDetail(jsonObject.get("details").toString());
        invoiceVO.setMaintype("0");
        invoiceVO.setSecondtype("0");
        return invoiceVO;
    }


    private String getRemoteData(String url,HashMap data) throws IOException {

        StringBuilder jsonIn = new StringBuilder();
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(getPostDataString(data));
        writer.flush();
        writer.close();
        os.close();
        int responseCode=conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                jsonIn.append(line);
            }
            Log.d(TAG, "jsonin " + jsonIn);
        } else {
            Log.d(TAG, "response code: " + responseCode);
        }
        conn.disconnect();
        return jsonIn.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(object instanceof EleSetCarrier)
        {
            EleSetCarrier eleSetCarrier= (EleSetCarrier) object;
            if(s.equals("error"))
            {
                Common.showToast(eleSetCarrier.getActivity(),"手機條碼或驗證碼有誤");
            }else{
                CarrierVO carrierVO=new CarrierVO();
                carrierVO.setCarNul(user);
                carrierVO.setPassword(password);
                carrierDB.insert(carrierVO);
                eleSetCarrier.setListAdapt();
                Common.showToast(eleSetCarrier.getActivity(),"新增成功");
            }
        }
    }

    private HashMap<String,String> getInvoicedetail(InvoiceVO invoiceVO)
    {
        HashMap<String,String> hashMap=new HashMap();
        hashMap.put("version","0.3");
        hashMap.put("cardType","3J0002");
        hashMap.put("cardNo",invoiceVO.getCarrier());
        hashMap.put("expTimeStamp","2147483647");
        hashMap.put("action","carrierInvDetail");
        hashMap.put("timeStamp",String.valueOf(System.currentTimeMillis()));
        hashMap.put("invNum",invoiceVO.getInvNum());
        hashMap.put("invDate",sd.format(new Date(invoiceVO.getTime().getTime())));
        hashMap.put("uuid","second");
        hashMap.put("sellerName",invoiceVO.getSellerName());
        hashMap.put("amount",invoiceVO.getAmount());
        hashMap.put("appID","EINV3201711184648");
        hashMap.put("cardEncrypt",invoiceVO.getCardEncrypt());
        return hashMap;
    }

    private HashMap<String,String> getInvoice(String user,String password,String startDate,String endDate)
    {
        HashMap<String,String> hashMap=new HashMap();
        hashMap.put("version","0.3");
        hashMap.put("cardType","3J0002");
        hashMap.put("cardNo",user);
        hashMap.put("expTimeStamp","2147483647");
        hashMap.put("action","carrierInvChk");
        hashMap.put("timeStamp",String.valueOf(System.currentTimeMillis()));
        hashMap.put("startDate",startDate);
        hashMap.put("endDate",endDate);
        hashMap.put("onlyWinningInv","N");
        hashMap.put("uuid","second");
        hashMap.put("appID","EINV3201711184648");
        hashMap.put("cardEncrypt",password);
      return hashMap;
    }
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
//            Log.d("tag",entry.getKey()+":"+entry.getValue());
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }


}
package com.chargeapp.whc.chargeapp.ChargeDB;


import android.os.AsyncTask;


import android.util.Log;
import android.view.View;


import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.EleDonate;
import com.chargeapp.whc.chargeapp.Control.EleSetCarrier;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.PriceActivity;
import com.chargeapp.whc.chargeapp.Control.PriceHand;
import com.chargeapp.whc.chargeapp.Control.PriceInvoice;
import com.chargeapp.whc.chargeapp.Control.SelectChartAll;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
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
    private Gson gson = new Gson();
    private int isNoExist = 0, year, month, day;
    private InvoiceDB invoiceDB;
    private CarrierDB carrierDB;
    private String user, password;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
    private TypeDetail typeDetail;
    private String action;
    private List<TypeDetailVO> typeDetailVOS;

    public GetSQLDate(Object object) {
        this.object = object;
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetail=new TypeDetail(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailVOS=typeDetail.getHaveDetailTypdAll();
    }

    @Override
    protected String doInBackground(Object... params) {
        action = params[0].toString();
        String jsonIn = null;
        String url;
        HashMap<String, String> data;
        boolean first =true;
        try {
            if (action.equals("getInvoice")) {
                String startDate,endDate ;
                Calendar cal = Calendar.getInstance();
                int nowyear=cal.get(Calendar.YEAR);
                int nowmonth=cal.get(Calendar.MONTH);
                cal.set(nowyear, nowmonth-6, 1);
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                user = params[1].toString();
                password = params[2].toString();
//                while (isNoExist < 3) {
                    cal.set(year, month, 1);
                    startDate = sf.format(new Date(cal.getTimeInMillis()));
                    cal.set(year, month, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    endDate = sf.format(new Date(cal.getTimeInMillis()));
                    Log.d(TAG, "startDate: " + startDate + "endDate" + endDate + "isNoExist" + isNoExist);
                    startDate="2017/08/01";
                    endDate="2017/08/31";
                    data = getInvoice(user, password, startDate, endDate,"N");
                    month = month+1;
                    if (month >11) {
                        month = 0;
                        year = year + 1;
                    }
                    url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?";
                    jsonIn = getRemoteData(url, data);
//                    if(jsonIn.equals("InternerError"))
//                    {
//                        break;
//                    }
//                    if (jsonIn.indexOf("919") != -1) {
//                        jsonIn = "error";
//                        break;
//                    }
//                    if (jsonIn.indexOf("200") == -1) {
//                        isNoExist++;
//                        continue;
//                    }
                    getjsonIn(jsonIn, password, user);

                    if(first)
                    {
                        CarrierVO carrierVO = new CarrierVO();
                        carrierVO.setCarNul(user);
                        carrierVO.setPassword(password);
                        carrierDB.insert(carrierVO);
                    }
                    first=false;
//                    if(year==nowyear&&month>nowmonth)
//                    {
//                        Log.d(TAG, "End startDate: " + startDate + "endDate" + endDate + "isNoExist" + isNoExist);
//                        break;
//                    }
//                }

            } else if (action.equals("GetToday")) {
                List<CarrierVO> carrierVOS = carrierDB.getAll();
                int todayyear, todaymonth, todayday, lastyear, lastmonth, lastday;
                long maxtime;
                Calendar today = Calendar.getInstance();
                Calendar last = Calendar.getInstance();
                for (CarrierVO c : carrierVOS) {
                    maxtime = invoiceDB.findIVByMaxDate(c.getCarNul());
                    last.setTime(new Date(maxtime));
                    todayyear = today.get(Calendar.YEAR);
                    todaymonth = today.get(Calendar.MONTH);
                    lastyear = last.get(Calendar.YEAR);
                    lastmonth = last.get(Calendar.MONTH);
                    if (todayyear == lastyear && todaymonth == lastmonth) {
                        jsonIn=searchTodayDate(last, today, c.getCarNul(), c.getPassword());
                    } else {
                        jsonIn=searchtomonth(last, today, c.getCarNul(), c.getPassword());
                    }
                }
            }else if(action.equals("searchHeartyTeam"))
            {
                String keyworld = params[1].toString();
                jsonIn=searchHeartyTeam(keyworld);
            }else if(action.equals("getAllPriceNul"))
            {
                 jsonIn=searchPriceNul();
            }else if(action.equals("getNeWPrice"))
            {
                jsonIn=searchNewPriceNul();
            }else if(action.equals("GetAllInvoice"))
            {
                jsonIn=getAllInvoiceDetail();
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonIn="InternerError";
            }
        return jsonIn;
    }



    private String searchNewPriceNul() {
        boolean end=true;
        PriceDB priceDB=new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        String url="https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invapp/InvApp?";
        String jsonin="";
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        if(month%2==1)
        {
            month=month-1;
        }
        StringBuffer period;
        String max=priceDB.findMaxPeriod();
        while (end)
        {
            period=new StringBuffer();
            if(month<=0)
            {
                month=12+month;
                year=year-1;
            }
            period.append((year-1911));
            if(String.valueOf(month).length()==1)
            {
                period.append("0");
            }
            period.append(month);
            Log.d("XXXXXXXX",max+" "+period.toString()+" "+period.toString().equals(max));
            if(max.equals(period.toString().trim()))
            {
                end=false;
                break;
            }
            HashMap<String,String> data=getPriceMap(period.toString());
            jsonin=getRemoteData(url,data);
            if(jsonin.indexOf("200")!=-1)
            {
                PriceVO priceVO=jsonToPriceVO(jsonin);
                priceDB.insert(priceVO);
            }
            month=month-2;
        }
        Log.d("XXXXXx", String.valueOf(priceDB.getAll().size()));
        return jsonin;
    }
    private String searchPriceNul() {
        PriceDB priceDB=new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        String url="https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invapp/InvApp?";
        String jsonin="";
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        if(month%2==1)
        {
           month=month-1;
        }
        StringBuffer period;
       for (int i=0;i<7;i++)
       {
           period=new StringBuffer();
           if(month<=0)
           {
               month=12+month;
               year=year-1;
           }
           period.append((year-1911));
           if(String.valueOf(month).length()==1)
           {
               period.append("0");
           }
           period.append(month);
           HashMap<String,String> data=getPriceMap(period.toString());
           jsonin=getRemoteData(url,data);
           if(jsonin.indexOf("200")!=-1)
           {
               PriceVO priceVO=jsonToPriceVO(jsonin);
               priceDB.insert(priceVO);
           }
           month=month-2;
           Log.d("XXXXXX", period.toString());
       }
       Log.d("XXXXXx", String.valueOf(priceDB.getAll().size()));
        return jsonin;
    }

    private PriceVO jsonToPriceVO(String jsonin) {
        Gson gson=new Gson();
        JsonObject js=gson.fromJson(jsonin,JsonObject.class);
        PriceVO priceVO=new PriceVO();
        priceVO.setInvoYm(js.get("invoYm").getAsString());
        priceVO.setSuperPrizeNo(js.get("superPrizeNo").getAsString());
        priceVO.setSpcPrizeNo(js.get("spcPrizeNo").getAsString());
        priceVO.setFirstPrizeNo1(js.get("firstPrizeNo1").getAsString());
        priceVO.setFirstPrizeNo2(js.get("firstPrizeNo2").getAsString());
        priceVO.setFirstPrizeNo3(js.get("firstPrizeNo3").getAsString());
        priceVO.setSixthPrizeNo1(ifnull(js.get("sixthPrizeNo1").getAsString()));
        priceVO.setSixthPrizeNo2(ifnull(js.get("sixthPrizeNo2").getAsString()));
        priceVO.setSixthPrizeNo3(ifnull(js.get("sixthPrizeNo3").getAsString()));
        priceVO.setSuperPrizeAmt(js.get("superPrizeAmt").getAsString());
        priceVO.setSpcPrizeAmt(js.get("spcPrizeAmt").getAsString());
        priceVO.setFirstPrizeAmt(js.get("firstPrizeAmt").getAsString());
        priceVO.setSecondPrizeAmt(js.get("secondPrizeAmt").getAsString());
        priceVO.setThirdPrizeAmt(js.get("thirdPrizeAmt").getAsString());
        priceVO.setFourthPrizeAmt(js.get("fourthPrizeAmt").getAsString());
        priceVO.setFifthPrizeAmt(js.get("fifthPrizeAmt").getAsString());
        priceVO.setSixthPrizeAmt(js.get("sixthPrizeAmt").getAsString());
        priceVO.setSixthPrizeNo4(ifnull(js.get("sixthPrizeNo4").getAsString()));
        priceVO.setSixthPrizeNo5(ifnull(js.get("sixthPrizeNo5").getAsString()));
        priceVO.setSixthPrizeNo6(ifnull(js.get("sixthPrizeNo6").getAsString()));
        return priceVO;
    }

    private String ifnull(String a)
    {
        if(a==null||a.trim().length()==0)
        {
            return "0";
        }
        return a;
    }


    private HashMap<String,String> getPriceMap(String date)
    {
        HashMap<String,String> data=new HashMap();
        data.put("version","0.2");
        data.put("action","QryWinningList");
        data.put("invTerm",date);
        data.put("UUID","second");
        data.put("appID","EINV3201711184648");
        return data;
    }


    private String searchHeartyTeam(String keyworld) throws IOException {
        String url="https://api.einvoice.nat.gov.tw/PB2CAPIVAN/loveCodeapp/qryLoveCode?";
        String jsonIn;
        HashMap <String,String> data=new HashMap<>();
        data.put("version","0.2");
        data.put("qKey",keyworld);
        data.put("action","qryLoveCode");
        data.put("UUID","second852");
        data.put("appID","EINV3201711184648");
        jsonIn=getRemoteData(url,data);
        return jsonIn;
    }

    private String searchtomonth(Calendar last, Calendar today, String user, String password) throws IOException {
        String startDay, endDay,url,jsonIn;
        HashMap<String, String> data;
        int todayMonth = today.get(Calendar.MONTH);
        int todayYear = today.get(Calendar.YEAR);
        int lastMonth = last.get(Calendar.MONTH);
        endDay = sf.format(new Date(today.getTimeInMillis()));
        today.set(todayYear, todayMonth, 1);
        startDay = sf.format(new Date(today.getTimeInMillis()));
        while (isNoExist < 3) {
            data = getInvoice(user, password, startDay, endDay,"N");
            Log.d(TAG, "GetToday" + startDay + ":" + endDay);
            url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?";
            jsonIn = getRemoteData(url, data);
            if (jsonIn.indexOf("919") != -1 || jsonIn.indexOf("200") == -1) {
                jsonIn = "InternerError";
                return jsonIn;
            }
            getjsonIn(jsonIn, password, user);
            todayMonth = todayMonth - 1;
            if (todayMonth <= 0) {
                todayMonth = 11;
                todayYear = todayYear - 1;
            }
            today.set(todayYear, todayMonth, 1);
            startDay = sf.format(new Date(today.getTimeInMillis()));
            today.set(todayYear, todayMonth, today.getActualMaximum(Calendar.DAY_OF_MONTH));
            endDay = sf.format(new Date(today.getTimeInMillis()));
            if (todayMonth == lastMonth) {
                jsonIn=searchTodayDate(last,today,user,password);
                return jsonIn;
            }
        }
        return "Success";
    }




    public String searchTodayDate(Calendar last, Calendar today, String user, String password) throws IOException {
        String startday = sf.format(last.getTime());
        String endday = sf.format(today.getTime());
        HashMap data;
        Log.d(TAG, "startDate: " + startday + "endDate" + endday + "user" + user);
        data = getInvoice(user, password, startday, endday,"N");
        String url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?";
        String jsonIn = getRemoteData(url, data);
        if (jsonIn.indexOf("919") != -1 || jsonIn.indexOf("200") == -1) {
            jsonIn = "InternerError";
            return jsonIn;
        }
        Calendar cal = last;
        Timestamp start = new Timestamp(cal.getTimeInMillis() - 86400000);
        Timestamp end = new Timestamp(cal.getTimeInMillis() + 86400000);
        List<InvoiceVO> newInvoicelist = todayjsonIn(jsonIn, password, user);
        List<InvoiceVO> oldInvoicelist = invoiceDB.getInvoiceBytime(start, end, user);
        for (InvoiceVO i : newInvoicelist) {
            boolean isequals=false;
            for (InvoiceVO old : oldInvoicelist) {
                Log.d("XXXXXX",i.getInvNum()+" : "+old.getInvNum()+" : "+old.getInvNum().equals(i.getInvNum()));
                if (old.getInvNum().equals(i.getInvNum())) {
                    isequals=true;
                    break;
                }
            }
            if(!isequals)
            {
                invoiceDB.insert(i);
                Log.d("XXXXXinsert",i.getInvNum());
            }

        }
        return "Success";
    }


    private List<InvoiceVO> todayjsonIn(String jsonIn, String password, String user) {
        List<InvoiceVO> list = new ArrayList<>();
        try {
            InvoiceVO invoiceVO;
            JsonObject js = gson.fromJson(jsonIn, JsonObject.class);
            Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
            String s = js.get("details").toString();
            List<JsonObject> b = gson.fromJson(s, cdType);
            for (JsonObject j : b) {
                invoiceVO = jsonToInVoice(j, password, user);
                list.add(invoiceVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    private void getjsonIn(String jsonIn, String password, String user) {
        try {
            InvoiceVO invoiceVO;
            JsonObject js = gson.fromJson(jsonIn, JsonObject.class);
            Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
            String s = js.get("details").toString();
            List<JsonObject> b = gson.fromJson(s, cdType);
            for (JsonObject j : b) {
                    invoiceVO = jsonToInVoice(j, password, user);
                    invoiceVO.setDonateTime(invoiceVO.getTime());
                    invoiceDB.insert(invoiceVO);
                    Log.d("insert",invoiceVO.getInvNum());
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private InvoiceVO jsonToInVoice(JsonObject j, String password, String user) throws IOException {
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO.setAmount(j.get("amount").getAsString());
        invoiceVO.setCardEncrypt(password);
        invoiceVO.setCardNo(j.get("cardNo").getAsString());
        invoiceVO.setCardType(j.get("cardType").getAsString());
        invoiceVO.setDonateMark(String.valueOf(j.get("donateMark").getAsInt()));
        invoiceVO.setInvNum(j.get("invNum").getAsString());
        invoiceVO.setInvDonatable(String.valueOf(j.get("invDonatable").getAsBoolean()));
        invoiceVO.setSellerName(j.get("sellerName").getAsString());
        String ass="0";
        if(j.get("sellerAddress")!=null)
        {
            ass=j.get("sellerAddress").getAsString();
        }
        invoiceVO.setSellerAddress(ass);
        invoiceVO.setSellerBan(j.get("sellerBan").getAsString());
        JsonObject jtime = gson.fromJson(j.get("invDate").toString(), JsonObject.class);
        String hhmmss=j.get("invoiceTime").getAsString();
        if(hhmmss.indexOf("null")!=-1)
        {
            hhmmss="00:00:00";
        }
        String time = String.valueOf(jtime.get("year").getAsInt() + 1911) + "-" + lengthlowtwo(jtime.get("month").getAsString()) + "-" + lengthlowtwo(jtime.get("date").getAsString()) + " " +hhmmss;
        invoiceVO.setTime(Timestamp.valueOf(time));
        invoiceVO.setDonateTime(Timestamp.valueOf(time));
        invoiceVO.setCarrier(user);
        invoiceVO.setCardEncrypt(password);
        invoiceVO.setDetail("0");
        invoiceVO.setMaintype("0");
        invoiceVO.setSecondtype("0");
        invoiceVO.setIswin("0");
        Log.d("sss",invoiceVO.getInvNum());
        return invoiceVO;
    }

    public String lengthlowtwo(String a)
    {

        if(a.length()<2)
        {
            a="0"+a;
        }
        return a;
    }



    private String getRemoteData(String url, HashMap data)  {
        StringBuilder jsonIn = new StringBuilder();
        HttpURLConnection conn=null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
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
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    jsonIn.append(line);
                }
                Log.d(TAG, "jsonin " + jsonIn);
            }
        }catch (Exception e)
        {
            jsonIn=new StringBuilder();
            jsonIn.append("InternerError");
            if(object instanceof EleSetCarrier) {
                List<CarrierVO> carrierVOS = carrierDB.getAll();
                if (carrierVOS != null) {
                    jsonIn.append("setCarrier");
                }
            }
        }finally {
            conn.disconnect();
        }
        return jsonIn.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            if (object instanceof EleSetCarrier) {
                EleSetCarrier eleSetCarrier = (EleSetCarrier) object;
                if (s.equals("error")) {
                    Common.showLongToast(eleSetCarrier.getActivity(), "手機條碼或驗證碼有誤");
                    eleSetCarrier.closeDialog();
                    return;
                } else if(s.equals("InternerError"))
                {
                    Common.showLongToast(eleSetCarrier.getActivity(), "財政部網路忙線中，請稍候使用!");
                    eleSetCarrier.closeDialog();
                    return;
                }
                else{
                    eleSetCarrier.setListAdapt();
                    Common.showToast(eleSetCarrier.getActivity(), "新增成功");
                    return;
                }
            }if(object instanceof EleDonate)
            {
                EleDonate eleDonate= (EleDonate) object;
                if(s.equals("InternerError"))
                {
                    Common.showLongToast(eleDonate.getActivity(), "財政部網路忙線中，請稍候使用!");
                    eleDonate.cancelDialog();
                    return;
                }
                if(action.equals("GetToday"))
                {
                    eleDonate.setlayout(); eleDonate.setlayout();
                }
                if(action.equals("searchHeartyTeam"))
                {
                    eleDonate.setlistTeam(s);
                }
            }else if(object instanceof PriceInvoice)
            {
                PriceInvoice priceInvoice= (PriceInvoice) object;
                if(s.equals("InternerError"))
                {
                    priceInvoice.noconnect();
                    return;
                }
                if(action.equals("getNeWPrice"))
                {
                    priceInvoice.AutoSetCMPrice();
                }else{
                    priceInvoice.AutoSetInPrice();
                }
            }else if(object instanceof SelectChartAll)
            {
                SelectChartAll selectChartAll= (SelectChartAll) object;
                if(action.equals("GetToday"))
                {
                    selectChartAll.getAllInvoiceDetail();
                }else {
                    selectChartAll.cancel();
                }
            }
        }catch (Exception e)
        {
            this.cancel(true);
        }
    }

    public String getAllInvoiceDetail()
    {
        String a=null;
        List<InvoiceVO> invoiceVOS=invoiceDB.getNoDetailAll();
        for (InvoiceVO i:invoiceVOS)
        {
            a=getInvoicedetail(i);
        }
        return a;
    }

    private String getInvoicedetail(InvoiceVO invoiceVO) {
            String urldetail = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?";
            HashMap<String, String> hashMap = new HashMap();
            hashMap.put("version", "0.3");
            hashMap.put("cardType", "3J0002");
            hashMap.put("cardNo", invoiceVO.getCarrier());
            hashMap.put("expTimeStamp", "2147483647");
            hashMap.put("action", "carrierInvDetail");
            hashMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
            hashMap.put("invNum", invoiceVO.getInvNum());
            hashMap.put("invDate", sf.format(new Date(invoiceVO.getTime().getTime())));
            hashMap.put("uuid", "second");
            hashMap.put("sellerName", invoiceVO.getSellerName());
            hashMap.put("amount", invoiceVO.getAmount());
            hashMap.put("appID", "EINV3201711184648");
            hashMap.put("cardEncrypt", invoiceVO.getCardEncrypt());
            String detailjs = getRemoteData(urldetail, hashMap);
            JsonObject jsonObject = gson.fromJson(detailjs, JsonObject.class);
            invoiceVO.setDetail(jsonObject.get("details").toString());
            InvoiceVO type=getType(invoiceVO);
            invoiceDB.update(type);
        return detailjs;
    }
    private SimpleDateFormat sd=new SimpleDateFormat("HH");
    private InvoiceVO getType(InvoiceVO invoiceVO) {
        String main="O",second="O";
        int x=0,total=0;
        for(TypeDetailVO t:typeDetailVOS)
        {
            x=0;
            main="O";
            second="O";
            String[] key=t.getKeyword().split(" ");
            for(int i=0;i<key.length;i++)
            {
                if(invoiceVO.getDetail().indexOf(key[i])!=-1)
                {
                    x++;
                }
            }
            if(x>total)
            {
                total=x;
                main=t.getGroupNumber();
                second=t.getName();
            }
        }
        if(second.indexOf("餐")!=-1)
        {
            int hour=Integer.valueOf(sd.format(new Date(invoiceVO.getTime().getTime())));
            if(hour>0&&hour<11)
            {
                second="早餐";
            }else if(hour>=11&&hour<18)
            {
                second="午餐";
            }else {
                second="晚餐";
            }
        }
        invoiceVO.setMaintype(main);
        invoiceVO.setSecondtype(second);
        return invoiceVO;
    }

    private HashMap<String, String> getInvoice(String user, String password, String startDate, String endDate,String iswin) {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("version", "0.3");
        hashMap.put("cardType", "3J0002");
        hashMap.put("cardNo", user);
        hashMap.put("expTimeStamp", "2147483647");
        hashMap.put("action", "carrierInvChk");
        hashMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("startDate", startDate);
        hashMap.put("endDate", endDate);
        hashMap.put("onlyWinningInv",iswin);
        hashMap.put("uuid", "second");
        hashMap.put("appID", "EINV3201711184648");
        hashMap.put("cardEncrypt", password);
        return hashMap;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
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
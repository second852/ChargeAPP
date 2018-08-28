package com.chargeapp.whc.chargeapp.ChargeDB;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.EleDonate;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.SearchByQrCode;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.ui.BarcodeGraphic;
import com.chargeapp.whc.chargeapp.ui.MultiTrackerActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SetupDateBase64 extends AsyncTask<Object, Integer, String> {
    private final static String TAG = "GetSQLDate";
    private final static String key ="YzQ4R1gzaTRIbTRJMzNnOA==";
    private final static String appId="EINV3201711184648";
    private SimpleDateFormat sd=new SimpleDateFormat("yyyy/MM/dd");
    private Object object;
    private InvoiceDB invoiceDB= new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private ConsumeVO consumeVO;

    public SetupDateBase64(Object object)
    {
          this.object=object;
    }




    @Override
    protected String doInBackground(Object... params) {

        String action = params[0].toString();
        String jsonIn="";
        String url="";
        try {
            if(action.equals("DonateTeam"))
            {
               HashMap<String,String> data;
               int seria=0;
               InvoiceVO invoiceVO;
               for(String s:EleDonate.donateMap.keySet())
               {
                   invoiceVO=EleDonate.donateMap.get(s);
                   url="https://api.einvoice.nat.gov.tw/PB2CAPIVAN/CarInv/Donate?";
                   data=getupdateHeartyTeam(seria,invoiceVO);
                   jsonIn=getRemoteData(url,data);
                   if(jsonIn.indexOf("200")!=-1)
                   {
                       invoiceVO.setDonateMark(String.valueOf(false));
                       invoiceVO.setInvDonatable(String.valueOf(false));
                       invoiceVO.setHeartyteam(EleDonate.teamTitle);
                       invoiceVO.setDonateTime(new Timestamp(System.currentTimeMillis()));
                       invoiceDB.update(invoiceVO);
                   }
                   seria++;
               }
            }else if("getThisDetail".equals(action))
            {
                jsonIn=getNeTDatei1();
            }else if("getNetDetail".equals(action))
            {
                jsonIn=getThisData();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return "InterError";
        }
        return jsonIn;
    }


    private String getNeTDatei1() throws IOException {
        String date=Common.sTwo.format(consumeVO.getDate());
        String[] dateS=date.split("/");
        int year=new Integer(dateS[0])-1911;
        int day=new Integer(dateS[1]);
        StringBuilder period=new StringBuilder();
        period.append(year);
        if(day%2!=0)
        {
            day=day+1;
        }
        if(day<10)
        {
            period.append("0");
        }
        period.append(day);
        HashMap<String,String> data=new HashMap<>();
        data.put("version","0.4");
        data.put("type","Barcode");
        data.put("invNum",consumeVO.getNumber());
        data.put("action","qryInvDetail");
        data.put("generation","V2");
        data.put("invTerm",period.toString());
        data.put("invDate",Common.sTwo.format(consumeVO.getDate()));
        data.put("encrypt","");
        data.put("sellerID","");
        data.put("UUID","second");
        data.put("randomNumber",consumeVO.getRdNumber());
        data.put("appID",appId);
        String url="https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invapp/InvApp?";
        String j=getRemoteData(url,data);
        return j;
    }


    //QRCode 讀不到掃描
    private String getThisData() throws IOException {
        HashMap<String,String> data=new HashMap<>();
        String imformation= BarcodeGraphic.hashMap.get(1);
        String period=imformation.substring(10, 17);
        String date=(Integer.valueOf(period.substring(0,3))+1911)+"/"+period.substring(3,5)+"/"+period.substring(5);
        int mon = Integer.parseInt(period.substring(3, 5));
        if (mon % 2 == 1) {
            if (mon == 11) {
                period = period.substring(0, 4) + "2";
            } else if (mon == 10) {
                period = period.substring(0, 4) + "1";
            }  else if (mon == 9) {
                period = period.substring(0, 3) + "10";
            }else {
                mon = mon + 1;
                period = period.substring(0, 4) + String.valueOf(mon);
            }
        } else {
            period = period.substring(0, 5);
        }
        data.put("version","0.4");
        data.put("type","Barcode");
        data.put("invNum",imformation.substring(0,10));
        data.put("action","qryInvDetail");
        data.put("generation","V2");
        data.put("invTerm",period);
        data.put("invDate",date);
        data.put("encrypt"," ");
        data.put("sellerID",imformation.substring(45,53));
        data.put("UUID","second");
        data.put("randomNumber",imformation.substring(17,21));
        data.put("appID",appId);
        String url="https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invapp/InvApp?";
        String j=getRemoteData(url,data);
       return j;
    }


    private HashMap<String,String> getupdateHeartyTeam(int seriel,InvoiceVO invoiceVO) throws Exception {
        HashMap<String,String> data=new HashMap<>();
        data.put("version","0.1");
        data.put("serial",String.valueOf(seriel));
        data.put("cardType","3J0002");
        data.put("cardNo",invoiceVO.getCarrier());
        data.put("expTimeStamp","2147483647");
        data.put("action","carrierInvDnt");
        data.put("timeStamp",String.valueOf(System.currentTimeMillis()+100));
        data.put("invDate",sd.format(new Date(invoiceVO.getTime().getTime())));
        data.put("invNum",invoiceVO.getInvNum());
        data.put("npoBan",EleDonate.teamNumber);
        data.put("uuid","second");
        data.put("appID",appId);
        data.put("cardEncrypt",invoiceVO.getCardEncrypt());
        data.put("signature",encodeSHA256(singure(data)));
        return data;
    }



    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(object instanceof EleDonate)
        {
            EleDonate eleDonate= (EleDonate) object;
           if(s.indexOf("200")!=-1)
           {
               eleDonate.donateOK();
               Common.showToast(eleDonate.getActivity(),"捐贈成功");
           }else {
                Common.showToast(eleDonate.getActivity(),"捐贈失敗");
                eleDonate.setlayout();
            }
        }else if(object instanceof SearchByQrCode)
        {
            SearchByQrCode searchByQrCode= (SearchByQrCode) object;
            searchByQrCode.resultD(s);
        }
//        else if(object instanceof BarcodeGraphic)
//        {
//            BarcodeGraphic barcodeGraphic= (BarcodeGraphic) object;
//            barcodeGraphic.QRCodeNetResult(s);
//        }
    }



    public String singure(Map<String,String> data)
    {
        StringBuffer sb=new StringBuffer();
        String[] ss = new String[data.size()];
        for (String d : data.keySet()) {
            int i = 0;
            for (String dd : data.keySet()) {
                if (compareA(d, dd)) {
                    i++;
                }
            }
            ss[i - 1] = d;
        }
        for (int i = 0; i < ss.length; i++) {
            sb.append(ss[i] + "=" + data.get(ss[i]));
            if ((i + 1) < ss.length) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    public boolean compareA(String s1, String s2) {
        int length = (s1.length() > s2.length()) ? s2.length() : s1.length();
        for (int i = 0; i < length; i++) {
            if (s1.charAt(i) > s2.charAt(i)) {
                return true;
            }
            if (s1.charAt(i) < s2.charAt(i)) {
                return false;
            }
        }
        return true;
    }



    private String getRemoteData(String url,HashMap<String,String> data)  {
        StringBuilder jsonIn = new StringBuilder();
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);
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
            } else {
                Log.d(TAG, "response code: " + responseCode);
                return String.valueOf(responseCode);
            }
            conn.disconnect();
            Log.d(TAG, "jsonIn: " + jsonIn);
        }catch (IOException e){
            jsonIn.append("InternetError");
        }
        return jsonIn.toString();
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

    public  String sha1(String s) throws
            UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {
        SecretKeySpec key = new SecretKeySpec(SetupDateBase64.key.getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);
        byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));
        return Base64.encodeToString(bytes,2);
    }

    public  String encodeSHA256(String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Base64.encodeToString(sha256_HMAC.doFinal(data.getBytes("UTF-8")),2);
    }

    public ConsumeVO getConsumeVO() {
        return consumeVO;
    }

    public void setConsumeVO(ConsumeVO consumeVO) {
        this.consumeVO = consumeVO;
    }
}
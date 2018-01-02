package com.chargeapp.whc.chargeapp.ChargeDB;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.chargeapp.whc.chargeapp.Control.EleActivity;
import com.chargeapp.whc.chargeapp.Control.EleDonate;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;

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
    private int seriel=0;



    @Override
    protected String doInBackground(Object... params) {

        String action = params[0].toString();
        String jsonIn="";
        String url="";
        try {
            if(action.equals("DonateTeam"))
            {
               HashMap<String,String> data;
               for(String s:EleDonate.donateMap.keySet())
               {
                   url="https://api.einvoice.nat.gov.tw/PB2CAPIVAN/CarInv/Donate?";
                   data=updateTeamNumber(EleDonate.donateMap.get(s));
                   data=getTeamhearty(data);
                   jsonIn=getRemoteData(url,data);
               }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return "InterError";
        }
        return jsonIn;
    }

    private HashMap<String,String> getTeamhearty(HashMap<String, String> data) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
        HashMap<String,String> params=new HashMap<>();
        params.put("version",data.get("version"));
        params.put("serial",data.get("serial"));
        params.put("cardType",data.get("cardType"));
        params.put("cardNo",data.get("cardNo"));
        params.put("expTimeStamp",data.get("expTimeStamp"));
        params.put("action",data.get("action"));
        params.put("timeStamp",data.get("timeStamp"));
        params.put("invDate",data.get("invDate"));
        params.put("invNum",data.get("invNum"));
        params.put("npoBan",data.get("npoBan"));
        params.put("uuid",data.get("uuid"));
        params.put("appID",data.get("appID"));
        params.put("cardEncrypt",data.get("cardEncrypt"));
        params.put("signature",sha1(getPostDataString(data)));
        return params;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }

    private HashMap<String,String> updateTeamNumber(InvoiceVO invoiceVO) {
        String jsonin="";
        long timstamp=System.currentTimeMillis();
        long timestamp=timstamp+100;
        HashMap<String, String> data = new HashMap<>();
        data.put("action","carrierInvDnt");
        data.put("appID",appId);
        data.put("cardEncrypt",invoiceVO.getCardEncrypt());
        data.put("cardNo",invoiceVO.getCardNo());
        data.put("cardType",invoiceVO.getCardType());
        data.put("expTimeStamp","2147483647");
        data.put("invDate",sd.format(new Date(invoiceVO.getTime().getTime())));
        data.put("invNum",invoiceVO.getInvNum());
        data.put("npoBan",EleDonate.teamNumber);
        data.put("serial",String.valueOf(seriel));
        data.put("timeStamp",String.valueOf(timestamp));
        data.put("uuid","secod852");
        data.put("version","0.1");
        return data;
    }

    private void showCarrierAll()
    {
        String h="";
        HashMap<String, String> data = new HashMap<>();
        long a = System.currentTimeMillis();
        a = a + 100;
        String date ="action=qryCarrierAgg&appID=EINV3201711184648&cardEncrypt=531d&cardNo=/2RDO8+P&cardType=3J0002&serial=0000001001&timeStamp="+String.valueOf(a)+"&uuid=second&version=1.0";
        try {
            HashMap<String, String> hash = new HashMap<>();
            hash.put("action","qryCarrierAgg");
            hash.put("appID","EINV3201711184648");
            hash.put("cardEncrypt","531d");
            hash.put("cardNo","/2RDO8+P");
            hash.put("cardType","3J0002");
            hash.put("serial","0000000001");
            hash.put("timeStamp",String.valueOf(a));
            hash.put("uuid","second");
            hash.put("version","1.0");
            h=sha1(getPostDataString(hash));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try{
            data.put("version","1.0");
            data.put("serial","0000001001");
            data.put("action","qryCarrierAgg");
            data.put("cardType","3J0002");
            data.put("cardNo","/2RDO8+P");
            data.put("cardEncrypt","531d");
            data.put("appID","EINV3201711184648");
            data.put("timeStamp",String.valueOf(a));
            data.put("uuid","second");
            data.put("signature",sha1(date));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private String getRemoteData(String url,HashMap<String,String> data) throws IOException {
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
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                jsonIn.append(line);
            }
        } else {
            Log.d(TAG, "response code: " + responseCode);
        }
        conn.disconnect();
        Log.d(TAG, "jsonIn: " + jsonIn);
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


}
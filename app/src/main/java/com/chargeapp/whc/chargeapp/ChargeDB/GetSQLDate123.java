package com.chargeapp.whc.chargeapp.ChargeDB;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.chargeapp.whc.chargeapp.Control.EleActivity;
import com.chargeapp.whc.chargeapp.Control.EleDonate;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class GetSQLDate123 extends AsyncTask<Object, Integer, String> {
    private final static String TAG = "GetSQLDate";


    @Override
    protected String doInBackground(Object... params) {

        String url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/Carrier/Aggregate?";
        url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/CarInv/Donate?";
        String jsonIn;
        try {
            jsonIn = getRemoteData(url);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        return jsonIn;
    }

    private String getRemoteData(String url) throws IOException {
        StringBuilder jsonIn = new StringBuilder();
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setReadTimeout(150000);
        conn.setConnectTimeout(150000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));


        String h = " ";
        String has = " ";
        HashMap<String, String> data = new HashMap<>();
        long a = System.currentTimeMillis();
        a = a + 100;
        String date = "action=qryCarrierAgg&appID=EINV3201711184648&cardEncrypt=531d&cardNo=/2RDO8+P&cardType=3J0002&serial=0000001001&timeStamp=" + String.valueOf(a) + "&uuid=second&version=1.0";
        String key = "YzQ4R1gzaTRIbTRJMzNnOA==";

        try {
//            data.put("version", "1.0");
//            data.put("serial", "0000001001");
//            data.put("action", "qryCarrierAgg");
//            data.put("cardType", "3J0002");
//            data.put("cardNo", "/2RDO8+P");
//            data.put("cardEncrypt", "531d");
//            data.put("appID", "EINV3201711184648");
//            data.put("timeStamp", String.valueOf(a));
//            data.put("uuid", "second");
            data.put("version","0.1");
            data.put("serial", "0000001001");
            data.put("cardType", "3J0002");
            data.put("cardNo", "/2RDO8+P");
            data.put("expTimeStamp","2147483647");
            data.put("action","carrierInvDnt");
            data.put("timeStamp",String.valueOf(System.currentTimeMillis()+100));
            data.put("invDate","2017/12/13");
            data.put("invNum","XT23921547");
            data.put("npoBan","05200169");
            data.put("uuid","second");
            data.put("appID", "EINV3201711184648");
            data.put("cardEncrypt","531d");
            data.put("signature", sha1(singure(data), key));
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public static String sha1(String s, String keyString) throws
            UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {

        SecretKeySpec key = new SecretKeySpec(keyString.getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);
        byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));
        return Base64.encodeToString(bytes, 2);
    }


}
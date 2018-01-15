package com.chargeapp.whc.chargeapp.ChargeDB;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.EleActivity;
import com.chargeapp.whc.chargeapp.Control.EleDonate;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
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
    private int seriel=0;
    private Object object;
    private InvoiceDB invoiceDB= new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());


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
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return "InterError";
        }
        return jsonIn;
    }

    private HashMap<String,String> getupdateHeartyTeam(int seriel,InvoiceVO invoiceVO) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
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
        data.put("signature",sha1(singure(data)));
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
               eleDonate.setlayout();
               Common.showLongToast(eleDonate.getActivity(),"捐贈成功");
           }else {
                Common.showLongToast(eleDonate.getActivity(),"捐贈失敗");
                eleDonate.cancelDialog();
            }
        }

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
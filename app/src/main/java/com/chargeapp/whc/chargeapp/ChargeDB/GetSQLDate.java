package com.chargeapp.whc.chargeapp.ChargeDB;



import android.app.job.JobService;
import android.os.AsyncTask;


import android.util.Log;
import android.widget.TextView;


import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.DowloadNewDataJob;
import com.chargeapp.whc.chargeapp.Control.Download;
import com.chargeapp.whc.chargeapp.Control.EleDonate;
import com.chargeapp.whc.chargeapp.Control.EleSetCarrier;
import com.chargeapp.whc.chargeapp.Control.EleUpdateCarrier;
import com.chargeapp.whc.chargeapp.Control.HomePagetList;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.SelectDetList;
import com.chargeapp.whc.chargeapp.Control.SelectListModelCom;
import com.chargeapp.whc.chargeapp.Control.SelectShowCircleDe;
import com.chargeapp.whc.chargeapp.Control.SelectShowCircleDeList;
import com.chargeapp.whc.chargeapp.Control.UpdateInvoice;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ElePeriod;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetSQLDate extends AsyncTask<Object, Integer, String> {
    private final static String TAG = "GetSQLDate";
    private Object object;
    private Gson gson = new Gson();
    private int year, month;
    private InvoiceDB invoiceDB;
    private CarrierDB carrierDB;
    private String user, password;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
    private TypeDetailDB typeDetailDB;
    private String action;
    private SimpleDateFormat sd = new SimpleDateFormat("HH");
    private InvoiceVO invoiceVO;
    private TextView percentage, progressT;
    private String downloadS;
    private double total;
    private ElePeriodDB elePeriodDB;
    private HashMap<Integer, String> priceMonth;

    public GetSQLDate(Object object) {
        total = 0;
        this.object = object;
        if(object instanceof JobService)
        {
            JobService jobService= (JobService) object;
            Common.setChargeDB(jobService);
        }
//        Common.setChargeDB((Activity)object);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        elePeriodDB = new ElePeriodDB(MainActivity.chargeAPPDB.getReadableDatabase());
//        invoiceDB.deleteBytime(Timestamp.valueOf("2018-09-01 00:00:00"));
    }

    public GetSQLDate(Object object, InvoiceVO invoiceVO) {
        this.object = object;
        this.invoiceVO = invoiceVO;
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
    }


    @Override
    protected String doInBackground(Object... params) {
        action = params[0].toString();
        String jsonIn = null;
        try {

            //最初下載
            if (action.equals("getInvoice")) {
                //設定初始下載時間這個月
                user = params[1].toString();
                password = params[2].toString();
                Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                jsonIn = findMonthHead(year, month, user, password);
                //Exception 處理
                if (jsonIn.equals("timeout") || jsonIn.equals("error")) {
                    jsonIn = againMethod();
                    return jsonIn;
                } else {
                    //檢查回傳號碼
                    JsonObject js = gson.fromJson(jsonIn, JsonObject.class);
                    String code = js.get("code").getAsString().trim();
                    //成功
                    if (code.equals("200")) {
                        CarrierVO carrierVO = new CarrierVO();
                        carrierVO.setCarNul(user);
                        carrierVO.setPassword(password);
                        carrierDB.insert(carrierVO);
                        //insert 最新的月發票
                        jsonIn=getjsonIn(jsonIn, password, user);
                        //最新的月
                        if(jsonIn.equals("error"))
                        {
                            elePeriodDB.insert(new ElePeriod(year, month, user, false));
                        }else {
                            elePeriodDB.insert(new ElePeriod(year, month, user, true));
                        }
                        //insert 6 month
                        for (int i = 1; i <= 6; i++) {
                            int year = this.year;
                            int month = this.month - i;
                            if (month < 0) {
                                month = 12 + month;
                                year = this.year - 1;
                            }
                            elePeriodDB.insert(new ElePeriod(year, month, user, false));
                        }
                        jsonIn = downLoadOtherMon(carrierVO);
                        return jsonIn;
                    } else {
                        //失敗
                        jsonIn = "noUser";
                        return jsonIn;
                    }
                }
            } else if (action.equals("download")) {
                PriceDB priceDB = new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
                priceMonth = Common.getPriceMonth();
                if (priceDB.getAll().size() <= 0) {
                    searchPriceNul();
                } else {
                    searchNewPriceNul();
                }
                jsonIn=updateInvoice();
                return jsonIn;
            } else if (action.equals("searchHeartyTeam")) {
                String keyworld = params[1].toString();
                jsonIn = searchHeartyTeam(keyworld);
                return jsonIn;
            } else if (action.equals("reDownload")) {
                jsonIn = getInvoiceDetail(invoiceVO);
            }else if (action.equals("checkCarrier")) {
                user = params[1].toString();
                password = params[2].toString();
                Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                jsonIn = findMonthHead(year, month, user, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonIn = "error";
        }
        return jsonIn;
    }

    private void updateErrorDonateMarK(Set<ElePeriod> elePeriods,CarrierVO carrierVO) {
        String jsonIn;
        for(ElePeriod elePeriod:elePeriods)
        {
           try {
               jsonIn=findMonthHead(elePeriod.getYear(),elePeriod.getMonth(),carrierVO.getCarNul(),carrierVO.getPassword());
               JsonObject js = gson.fromJson(jsonIn, JsonObject.class);
               String code = js.get("code").getAsString().trim();
               if(code.equals("200"))
               {
                   Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
                   String s = js.get("details").toString();
                   if(s!=null&&s.length()>0)
                   {
                       List<JsonObject> jsonObjects = gson.fromJson(s, cdType);
                       String inNul,donateMark;
                       int amount;
                       for(JsonObject jsonObject:jsonObjects)
                       {
                            donateMark=jsonObject.get("donateMark").getAsString();
                            inNul=jsonObject.get("invNum").getAsString();
                            amount=jsonObject.get("amount").getAsInt();
                            InvoiceVO invoiceVO=invoiceDB.findOldByNulAmount(inNul,amount);
                            invoiceVO.setDonateMark(donateMark);
                            invoiceDB.update(invoiceVO);
                       }
                   }
               }
           }catch (Exception e)
           {

           }
        }
    }

    private String updateInvoice() throws IOException {
        downloadS = "invoice";
        String jsonIn=null;
        List<CarrierVO> carrierVOS = carrierDB.getAll();
        //沒有載具不用更新
        if (carrierVOS.size() <= 0) {
            return "NoCarrier";
        }
        for (CarrierVO carrierVO : carrierVOS) {
            //確認帳密
            user = carrierVO.getCarNul();
            password = carrierVO.getPassword();
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            jsonIn = findMonthHead(year, month, user, password);
            if(jsonIn.indexOf("919")!=-1)
            {
                if(Common.lostCarrier==null)
                {
                    Common.lostCarrier=new ArrayList<>();
                }
                Common.lostCarrier.add(carrierVO);
                continue;
            }

            //更新errorDonateMark
            List<InvoiceVO> invoiceVOS=invoiceDB.getErrorDonateMark(carrierVO.getCarNul());
            if(invoiceVOS.size()>0)
            {
                //大於6個月發票 DonateMark=1
                Calendar nowCal=Calendar.getInstance();
                nowCal.add(Calendar.MONTH,-6);
                nowCal.set(Calendar.DAY_OF_WEEK,1);
                nowCal.set(Calendar.SECOND,0);
                nowCal.set(Calendar.HOUR_OF_DAY,0);
                nowCal.set(Calendar.MINUTE,0);

                Set<ElePeriod> elePeriods=new HashSet<>();
                Calendar period=new GregorianCalendar();
                for (InvoiceVO invoiceVO:invoiceVOS)
                {
                    if(invoiceVO.getTime().getTime()<nowCal.getTimeInMillis())
                    {
                        invoiceVO.setDonateMark("99");
                        invoiceDB.update(invoiceVO);
                    }else {
                        period.setTime(new Date(invoiceVO.getTime().getTime()));
                        elePeriods.add(new ElePeriod(period.get(Calendar.YEAR),period.get(Calendar.MONTH)));
                    }
                }
                updateErrorDonateMarK(elePeriods,carrierVO);
            }

            //更新載具-找載具最新的月
            Calendar differCal = new GregorianCalendar();
            long maxTime = invoiceDB.findIVByMaxDate(carrierVO.getCarNul());
            long differTime = System.currentTimeMillis() - maxTime;
            differCal.setTime(new Date(differTime));
            if (differCal.get(Calendar.MONTH) >= 6 && differTime > 0) {
                //超過6個月
                jsonIn=searchNewInvoice(carrierVO);
            } else {
                //未超過6個月
                jsonIn=searchToMonth(carrierVO, maxTime);
            }
        }
        return jsonIn;
    }

    private String searchNewInvoice(CarrierVO carrierVO) {
        user = carrierVO.getCarNul();
        password = carrierVO.getPassword();
        //處理old period
        List<ElePeriod> elePeriods = elePeriodDB.getCarrierAll(carrierVO.getCarNul());
        for (ElePeriod elePeriod : elePeriods) {
            //因為超過6個月無法下載
            elePeriod.setDownload(true);
            elePeriodDB.update(elePeriod);
        }
        //找尋新的6個月
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        String jsonIn = findMonthHead(year, month, user, password);
        //Exception 處理
        if (jsonIn.equals("timeout") || jsonIn.equals("error")) {
            againMethod();
        } else {
            //檢查回傳號碼
            JsonObject js = gson.fromJson(jsonIn, JsonObject.class);
            String code = js.get("code").getAsString().trim();
            //成功
            if (code.equals("200")) {
                jsonIn = getjsonIn(jsonIn, password, user);
                elePeriodDB.insert(new ElePeriod(year, month, user, true));
                if (jsonIn.equals("success")) {
                    //insert 6 month
                    for (int i = 1; i <= 6; i++) {
                        int year = this.year;
                        int month = this.month - i;
                        if (month < 0) {
                            month = 12 + month;
                            year = this.year - 1;
                        }
                        elePeriodDB.insert(new ElePeriod(year, month, user, false));
                    }
                    downLoadOtherMon(carrierVO);
                }
            }
        }
        return jsonIn;
    }

    private String againMethod() {
        String jsonIn = findMonthHead(year, month, user, password);
        if (jsonIn.equals("timeout") || jsonIn.equals("error")) {
            return "timeout";
        }
        JsonObject js = gson.fromJson(jsonIn, JsonObject.class);
        String code = js.get("code").getAsString().trim();
        //成功
        if (code.equals("200")) {
            CarrierVO carrierVO = new CarrierVO();
            carrierVO.setCarNul(user);
            carrierVO.setPassword(password);
            carrierDB.insert(carrierVO);

            jsonIn = getjsonIn(jsonIn, password, user);
            if (jsonIn.equals("error")) {
                elePeriodDB.insert(new ElePeriod(year, month, user, false));
            }else{
                elePeriodDB.insert(new ElePeriod(year, month, user, true));
            }
            //insert 6 month
            for (int i = 1; i <= 6; i++) {
                    int year = this.year;
                    int month = this.month - i;
                    if (month < 0) {
                        month = 12 + month;
                        year = this.year - 1;
                    }
                    elePeriodDB.insert(new ElePeriod(year, month, user, false));
            }
                jsonIn = downLoadOtherMon(carrierVO);
            return jsonIn;
        } else {
            //失敗
            jsonIn = "noUser";
            return jsonIn;
        }
    }

    private String findMonthHead(int year, int month, String user, String password) {
        String startDate, endDate, url, jsonIn;
        HashMap<String, String> data;
        Calendar cal = new GregorianCalendar(year, month, 1);
        startDate = Common.sTwo.format(new Date(cal.getTimeInMillis()));
        cal.set(year, month, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDate = Common.sTwo.format(new Date(cal.getTimeInMillis()));
        //設定傳遞參數
        data = getInvoice(user, password, startDate, endDate, "N");
        url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?";
        jsonIn = getRemoteData(url, data);
        return jsonIn;
    }

    private String downLoadOtherMon(CarrierVO carrierVO) {
        String user = carrierVO.getCarNul();
        String password = carrierVO.getPassword();
        String jsonIn = "";
        List<ElePeriod> elePeriods = elePeriodDB.getCarrierAll(user);
        for (ElePeriod elePeriod : elePeriods) {
            year = elePeriod.getYear();
            month = elePeriod.getMonth();
            jsonIn = findMonthHead(year, month, user, password);
            if (jsonIn.indexOf("code") != -1) {
                JsonObject js = gson.fromJson(jsonIn, JsonObject.class);
                String code = js.get("code").getAsString().trim();
                //success
                if (code.equals("200")) {
                    elePeriod.setDownload(true);
                    elePeriodDB.update(elePeriod);
                    jsonIn = getjsonIn(jsonIn, password, user);
                } else if (code.equals("901") || code.equals("902")) {
                    elePeriod.setDownload(true);
                    elePeriodDB.update(elePeriod);
                }
            }
        }
        return jsonIn;
    }


    private String searchNewPriceNul() {
        boolean end = true;
        PriceDB priceDB = new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        String url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invapp/InvApp?";
        String jsonin = "";
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        if (month % 2 == 1) {
            month = month - 1;
        }
        StringBuffer period;
        String max = priceDB.findMaxPeriod();

        while (end) {
            period = new StringBuffer();
            if (month <= 0) {
                month = 12 + month;
                year = year - 1;
            }
            period.append((year - 1911));
            if (String.valueOf(month).length() == 1) {
                period.append("0");
            }
            period.append(month);
            Log.d(TAG, "searchPrice" + max + " " + period.toString() + " " + period.toString().equals(max));
            if (max.equals(period.toString().trim())) {
                return "isNew";
            }
            HashMap<String, String> data = getPriceMap(period.toString());
            jsonin = getRemoteData(url, data);
            if (jsonin.indexOf("200") != -1) {
                PriceVO priceVO = jsonToPriceVO(jsonin);
                priceDB.insert(priceVO);
                Log.d(TAG, "insert" + priceVO.getInvoYm());
            }
            if (jsonin.indexOf("901") != -1) {
                break;
            }
            month = month - 2;
        }
        return jsonin;
    }

    private String searchPriceNul() {
        action = "price";
        PriceDB priceDB = new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        String url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invapp/InvApp?";
        String jsonin = "";
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        if (month % 2 == 1) {
            month = month - 1;
        }
        StringBuffer period;
        for (int i = 0; i < 7; i++) {
            period = new StringBuffer();
            if (month <= 0) {
                month = 12 + month;
                year = year - 1;
            }
            period.append((year - 1911));
            if (String.valueOf(month).length() == 1) {
                period.append("0");
            }
            period.append(month);
            Log.d(TAG, "insert period :" + period.toString());
            HashMap<String, String> data = getPriceMap(period.toString());
            jsonin = getRemoteData(url, data);
            if (jsonin.equals("timeout") || jsonin.equals("error")) {
                publishProgress(2, month, (int) total);
                return jsonin;
            }
            JsonObject js = gson.fromJson(jsonin, JsonObject.class);
            String code = js.get("code").getAsString().trim();
            if (code.equals("200")) {
                PriceVO priceVO = jsonToPriceVO(jsonin);
                priceDB.insert(priceVO);
                total = total + 1;
                publishProgress(0, month, (int) total);
                Log.d(TAG, "insert price :" + priceVO.getInvoYm());
            }
            month = month - 2;
        }
        return jsonin;
    }

    private PriceVO jsonToPriceVO(String jsonin) {
        Gson gson = new Gson();
        JsonObject js = gson.fromJson(jsonin, JsonObject.class);
        PriceVO priceVO = new PriceVO();
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

    private String ifnull(String a) {
        if (a == null || a.trim().length() == 0) {
            return "0";
        }
        return a;
    }


    private HashMap<String, String> getPriceMap(String date) {
        HashMap<String, String> data = new HashMap();
        data.put("version", "0.2");
        data.put("action", "QryWinningList");
        data.put("invTerm", date);
        data.put("UUID", "second");
        data.put("appID", "EINV3201711184648");
        return data;
    }


    private String searchHeartyTeam(String keyworld) throws IOException {
        String url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/loveCodeapp/qryLoveCode?";
        String jsonIn;
        HashMap<String, String> data = new HashMap<>();
        data.put("version", "0.2");
        data.put("qKey", keyworld);
        data.put("action", "qryLoveCode");
        data.put("UUID", "second852");
        data.put("appID", "EINV3201711184648");
        jsonIn = getRemoteData(url, data);
        return jsonIn;
    }

    private String searchToMonth(CarrierVO carrierVO, long maxTime) throws IOException {
        String jsonIn;
        Calendar oldMax = new GregorianCalendar();
        oldMax.setTime(new Date(maxTime));
        Calendar today = Calendar.getInstance();
        int todayMonth = today.get(Calendar.MONTH);
        int todayYear = today.get(Calendar.YEAR);
        int lastMonth = oldMax.get(Calendar.MONTH);
        int lastYear = oldMax.get(Calendar.YEAR);
        if (todayMonth == lastMonth && lastYear == todayYear) {
            jsonIn = searchTodayDate(oldMax, today, carrierVO.getCarNul(), carrierVO.getPassword());
            return jsonIn;
        } else {
            //先insert ElePeriod 在一次找尋 false
            while (true) {
                elePeriodDB.insert(new ElePeriod(todayYear, todayMonth, carrierVO.getCarNul(), false));
                todayMonth = todayMonth - 1;
                if (todayMonth < 0) {
                    todayMonth = 12 + todayMonth;
                    todayYear = todayYear - 1;
                }
                Log.d("XXXXXXx", todayMonth + ":" + todayYear);
                //到最大個月為止
                if (todayMonth <= lastMonth && todayYear<=lastYear) {
                    break;
                }
            }
            downLoadOtherMon(carrierVO);
            jsonIn = searchTodayDate(oldMax, today, carrierVO.getCarNul(), carrierVO.getPassword());
            //detail = 0
            List<InvoiceVO> invoiceVOS = invoiceDB.getNoDetailAll();
            for (InvoiceVO invoiceVO : invoiceVOS) {
                updateInvoiceDetail(invoiceVO);
            }
        }
        return jsonIn;
    }


    public String searchTodayDate(Calendar last, Calendar today, String user, String password) throws IOException {
        String startday = sf.format(last.getTime());
        String endday = sf.format(today.getTime());
        HashMap data;
        Log.d(TAG, "startDate: " + startday + "endDate" + endday + "user" + user);
        data = getInvoice(user, password, startday, endday, "N");
        String url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?";
        String jsonIn = getRemoteData(url, data);
        if (jsonIn.indexOf("200") == -1) {
            return jsonIn;
        }
        jsonIn = getjsonIn(jsonIn, password, user);
//        Calendar cal = last;
//        Timestamp start = new Timestamp(cal.getTimeInMillis() - 86400000);
//        Timestamp end = new Timestamp(cal.getTimeInMillis() + 86400000);
//        List<InvoiceVO> newInvoicelist = todayjsonIn(jsonIn, password, user);
//        List<InvoiceVO> oldInvoicelist = invoiceDB.getInvoiceBytime(start, end, user);
//        for (InvoiceVO i : newInvoicelist) {
//            boolean isequals = false;
//            for (InvoiceVO old : oldInvoicelist) {
//                Log.d(TAG, "check : " + i.getInvNum() + " : " + old.getInvNum() + " : " + old.getInvNum().equals(i.getInvNum()));
//                if (old.getInvNum().equals(i.getInvNum())) {
//                    isequals = true;
//                    break;
//                }
//            }
//            if (!isequals) {
//                getInvoiceDetail(i);
//                Log.d(TAG, "insert new :" + i.getInvNum());
//            }
//        }
        return jsonIn;
    }


    private List<InvoiceVO> todayjsonIn(String jsonIn, String password, String user) {
        List<InvoiceVO> list = new ArrayList<>();
        try {
            InvoiceVO invoiceVO;
            JsonObject js = gson.fromJson(jsonIn, JsonObject.class);
            Type cdType = new TypeToken<List<JsonObject>>() {
            }.getType();
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


    private String getjsonIn(String jsonIn, String password, String user) {
        try {
            InvoiceVO invoiceVO;
            List<InvoiceVO> oldInvoiceList;
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            JsonObject js = gson.fromJson(jsonIn, JsonObject.class);
            Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
            String s = js.get("details").toString();
            List<JsonObject> b = gson.fromJson(s, cdType);
            //設定processBar process
            double divide = 0.0;
            if (b.size() != 0) {
                divide = 16 / b.size();
            }
            String result="";
            for (JsonObject j : b) {
                invoiceVO = jsonToInVoice(j, password, user);
                if (invoiceVO != null) {
                    //確認有無重複
                    start.setTime(new Date(invoiceVO.getTime().getTime()));
                    start.add(Calendar.MONTH, -1);
                    end.setTime(new Date(invoiceVO.getTime().getTime()));
                    end.add(Calendar.MONTH, 1);
                    oldInvoiceList = invoiceDB.checkInvoiceRepeat(start, end, invoiceVO.getInvNum());
                    if (oldInvoiceList.size() > 0) {
                        continue;
                    }
                    //查詢電子發票明細和insert invoice
                    result = getInvoiceDetail(invoiceVO);
                    total = total + divide;
                    publishProgress(0, month, (int) total);
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    private InvoiceVO jsonToInVoice(JsonObject j, String password, String user) {
        try {
            InvoiceVO invoiceVO = new InvoiceVO();
            invoiceVO.setRealAmount(j.get("amount").getAsString());
            invoiceVO.setCardEncrypt(password);
            invoiceVO.setCardNo(j.get("cardNo").getAsString());
            invoiceVO.setCardType(j.get("cardType").getAsString());
            invoiceVO.setDonateMark(String.valueOf(j.get("donateMark").getAsInt()));
            invoiceVO.setInvNum(j.get("invNum").getAsString());
            invoiceVO.setInvDonatable(String.valueOf(j.get("invDonatable").getAsBoolean()));
            invoiceVO.setSellerName(j.get("sellerName").getAsString());
            String ass = "0";
            if (j.get("sellerAddress") != null) {
                ass = j.get("sellerAddress").getAsString();
            }
            invoiceVO.setSellerAddress(ass);
            invoiceVO.setSellerBan(j.get("sellerBan").getAsString());
            JsonObject jtime = gson.fromJson(j.get("invDate").toString(), JsonObject.class);
            String hhmmss = j.get("invoiceTime").getAsString();
            if (hhmmss.indexOf("null") != -1) {
                hhmmss = "00:00:00";
            }
            String time = String.valueOf(jtime.get("year").getAsInt() + 1911) + "-" + lengthlowtwo(jtime.get("month").getAsString()) + "-" + lengthlowtwo(jtime.get("date").getAsString()) + " " + hhmmss;
            invoiceVO.setTime(Timestamp.valueOf(time));
            invoiceVO.setDonateTime(Timestamp.valueOf(time));
            invoiceVO.setCarrier(user);
            invoiceVO.setCardEncrypt(password);
            invoiceVO.setDetail("0");
            invoiceVO.setMaintype("0");
            invoiceVO.setSecondtype("0");
            invoiceVO.setIswin("0");
            invoiceVO.setDonateTime(invoiceVO.getTime());
            invoiceVO.setCurrency(j.get("currency").getAsString());
            return invoiceVO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String lengthlowtwo(String a) {

        if (a.length() < 2) {
            a = "0" + a;
        }
        return a;
    }


    private String getRemoteData(String url, HashMap data) {
        StringBuilder jsonIn = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            byte[] postData = getPostDataString(data).getBytes(StandardCharsets.UTF_8 );
            conn= (HttpURLConnection) new URL( url ).openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setUseCaches(false);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.flush();
            wr.close();
            conn.getOutputStream().close();
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    jsonIn.append(line);
                }
                Log.d("jsonIn",jsonIn.toString());
            }else{
                jsonIn = new StringBuilder();
                jsonIn.append("timeout");
            }
        } catch (SocketTimeoutException e) {
            jsonIn = new StringBuilder();
            jsonIn.append("timeout");
        } catch (Exception e) {
            Log.d(TAG, "error" + e.getMessage());
            jsonIn = new StringBuilder();
            jsonIn.append("error");
        } finally {

            if(conn!=null)
            {
                conn.disconnect();
            }

        }
        return jsonIn.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            if (object instanceof EleSetCarrier) {
                EleSetCarrier eleSetCarrier = (EleSetCarrier) object;
                if (s.equals("noUser")) {
                    Common.showToast(eleSetCarrier.getActivity(), "手機條碼或驗證碼有誤!");
                    eleSetCarrier.closeDialog();
                    return;
                } else if (s.equals("timeout")) {

                    if (carrierDB.getAll().size() > 0) {
                        (new Common()).AutoSetPrice();
                        eleSetCarrier.setListAdapt();
                        percentage.setText("100%");
                        progressT.setText("新增成功");
                        Common.showToast(eleSetCarrier.getActivity(), "新增成功");
                        eleSetCarrier.SucessDownload();
                        return;
                    } else {
                        Common.showToast(eleSetCarrier.getActivity(), "財政部網路忙線中，請稍候使用!");
                        eleSetCarrier.closeDialog();
                        return;
                    }
                } else {
                    percentage.setText("100%");
                    progressT.setText("下載完成!\n更新中");
                    (new Common()).AutoSetPrice();
                    eleSetCarrier.setListAdapt();
                    Common.showToast(eleSetCarrier.getActivity(), "新增成功");
                    eleSetCarrier.SucessDownload();
                    return;
                }
            }
            if (object instanceof EleDonate) {
                EleDonate eleDonate = (EleDonate) object;
                if (s.equals("timeout") || s.equals("error")) {
                    Common.showToast(eleDonate.getActivity(), "財政部網路忙線中，請稍候使用!");
                    eleDonate.cancelDialog();
                    return;
                } else if (action.equals("searchHeartyTeam")) {
                    eleDonate.setlistTeam(s);
                } else if (action.equals("reDownload")) {
                    eleDonate.setlayout();
                }
            } else if (object instanceof Download) {
                Download download = (Download) object;
                percentage.setText("100%");
                progressT.setText("下載完成!\n更新中");
                if(Common.lostCarrier!=null)
                {
                    if(Common.lostCarrier.size()>0)
                    {
                        StringBuffer sb=new StringBuffer();
                        for(CarrierVO c:Common.lostCarrier)
                        {
                            sb.append(c.getCarNul()+" ");
                        }
                        sb.append("驗證碼錯誤，請到雲端發票 : \n\"綁定/取消載具修改\"");
                        Common.showToast(download.activity,sb.toString());
                    }
                }
                download.tonNewActivity();
            } else if (object instanceof SelectDetList) {
                SelectDetList selectDetList = (SelectDetList) object;
                if (s.equals("timeout") || s.equals("error")) {
                    selectDetList.cancelshow();
                } else {
                    selectDetList.setLayout();
                }
            } else if (object instanceof UpdateInvoice) {
                UpdateInvoice updateInvoice = (UpdateInvoice) object;
                if (s.equals("timeout") || s.equals("error")) {
                    updateInvoice.cancelShow();
                } else {
                    updateInvoice.setLayout();
                }
            } else if (object instanceof SelectShowCircleDe) {
                SelectShowCircleDe selectShowCircleDe = (SelectShowCircleDe) object;
                if (s.equals("timeout") || s.equals("error")) {
                    selectShowCircleDe.cancelshow();
                } else {
                    selectShowCircleDe.setLayout();
                }
            } else if (object instanceof SelectListModelCom) {
                SelectListModelCom selectListModelCom = (SelectListModelCom) object;
                if (s.equals("timeout") || s.equals("error")) {
                    selectListModelCom.cancelshow();
                } else {
                    selectListModelCom.setLayout();
                }
            } else if (object instanceof SelectShowCircleDeList) {
                SelectShowCircleDeList selectShowCircleDeList = (SelectShowCircleDeList) object;
                if (s.equals("timeout") || s.equals("error")) {
                    selectShowCircleDeList.cancelshow();
                } else {
                    selectShowCircleDeList.choiceLayout();
                }
            } else if (object instanceof HomePagetList) {
                HomePagetList homePagetList = (HomePagetList) object;
                if (s.equals("timeout") || s.equals("error")) {
                    homePagetList.cancelshow();
                } else {
                    homePagetList.setChoiceLayout();
                }
            }else if (object instanceof EleUpdateCarrier) {
                EleUpdateCarrier eleUpdateCarrier = (EleUpdateCarrier) object;
                eleUpdateCarrier.check(s);
            }else if (object instanceof DowloadNewDataJob) {
                new Common().AutoSetPrice();
            }
        } catch (Exception e) {
           Log.d(TAG,"onPostExecute"+e.getMessage());
        }finally {
            this.cancel(true);
        }
    }

    private String getUpdateInvoiceDetail(InvoiceVO invoiceVO) {
        String urldetail = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?";
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("version", "0.4");
        hashMap.put("cardType", "3J0002");
        hashMap.put("cardNo", invoiceVO.getCarrier());
        hashMap.put("expTimeStamp", "2147483647");
        hashMap.put("action", "carrierInvDetail");
        hashMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("invNum", invoiceVO.getInvNum());
        hashMap.put("invDate", sf.format(new Date(invoiceVO.getTime().getTime())));
        hashMap.put("uuid", "second");
        hashMap.put("sellerName", invoiceVO.getSellerName());
        hashMap.put("amount", String.valueOf(invoiceVO.getAmount()));
        hashMap.put("appID", "EINV3201711184648");
        hashMap.put("cardEncrypt", invoiceVO.getCardEncrypt());
        String detailjs = getRemoteData(urldetail, hashMap);
        if (detailjs == null || detailjs.equals("error") || detailjs.equals("timeout")) {
            return "timeout";
        }
        JsonObject jsonObject = null;
        try {
            jsonObject = gson.fromJson(detailjs, JsonObject.class);
        } catch (Exception e) {
            Log.d("XXXXXX", detailjs);
            Log.d("XXXXXX", e.getMessage());
        }

        if (jsonObject != null) {
            invoiceVO.setDetail(jsonObject.get("details").toString());
            InvoiceVO type = getType(invoiceVO);
            invoiceDB.update(type);
            detailjs = "success";
        }
        return detailjs;
    }


    private String getInvoiceDetail(InvoiceVO invoiceVO) {
        String urldetail = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?";
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("version", "0.5");
        hashMap.put("cardType", "3J0002");
        hashMap.put("cardNo", invoiceVO.getCarrier());
        hashMap.put("expTimeStamp", "2147483647");
        hashMap.put("action", "carrierInvDetail");
        hashMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("invNum", invoiceVO.getInvNum());
        hashMap.put("invDate", sf.format(new Date(invoiceVO.getTime().getTime())));
        hashMap.put("uuid", "second");
        hashMap.put("sellerName", invoiceVO.getSellerName());
        hashMap.put("amount", String.valueOf(invoiceVO.getAmount()));
        hashMap.put("appID", "EINV3201711184648");
        hashMap.put("cardEncrypt", invoiceVO.getCardEncrypt());
        String detailjs = getRemoteData(urldetail, hashMap);
        if (detailjs == null || detailjs.equals("error") || detailjs.equals("timeout")) {
            invoiceDB.insert(invoiceVO);
            return "fail";
        }
        JsonObject jsonObject = null;
        try {
            jsonObject = gson.fromJson(detailjs, JsonObject.class);
        } catch (Exception e) {
            Log.d("XXXXXX", detailjs);
            Log.d("XXXXXX", e.getMessage());
        }

        if (jsonObject != null) {
            invoiceVO.setDetail(jsonObject.get("details").toString());
            InvoiceVO type = getType(invoiceVO);
            invoiceDB.insert(type);
            Log.d("total :", Common.sDay.format(new Date(invoiceVO.getTime().getTime())) + " : " + invoiceVO.getInvNum());
            detailjs = "success";
        }
        return detailjs;
    }

    private String updateInvoiceDetail(InvoiceVO invoiceVO) {
        String urldetail = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?";
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("version", "0.5");
        hashMap.put("cardType", "3J0002");
        hashMap.put("cardNo", invoiceVO.getCarrier());
        hashMap.put("expTimeStamp", "2147483647");
        hashMap.put("action", "carrierInvDetail");
        hashMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("invNum", invoiceVO.getInvNum());
        hashMap.put("invDate", sf.format(new Date(invoiceVO.getTime().getTime())));
        hashMap.put("uuid", "second");
        hashMap.put("sellerName", invoiceVO.getSellerName());
        hashMap.put("amount", String.valueOf(invoiceVO.getAmount()));
        hashMap.put("appID", "EINV3201711184648");
        hashMap.put("cardEncrypt", invoiceVO.getCardEncrypt());
        String detailjs = getRemoteData(urldetail, hashMap);
        if (detailjs == null || detailjs.equals("error") || detailjs.equals("timeout")) {
            return "fail";
        }
        JsonObject jsonObject = gson.fromJson(detailjs, JsonObject.class);
        invoiceVO.setDetail(jsonObject.get("details").toString());
        InvoiceVO type = getType(invoiceVO);
        invoiceDB.update(type);
        Log.d("total :", Common.sDay.format(new Date(invoiceVO.getTime().getTime())) + " : " + invoiceVO.getInvNum());
        detailjs = "success";
        return detailjs;
    }


    private InvoiceVO getType(InvoiceVO invoiceVO) {
        List<TypeDetailVO> typeDetailVOS = typeDetailDB.getTypdAll();
        String main = "O", second = "O";
        int x = 0, total = 0;
        for (TypeDetailVO t : typeDetailVOS) {
            x = 0;
            String[] key = t.getKeyword().split(" ");
            for (int i = 0; i < key.length; i++) {
                if (invoiceVO.getDetail().indexOf(key[i].trim()) != -1) {
                    x = x + key[i].length();
                }
            }
            if (x > total) {
                total = x;
                main = t.getGroupNumber();
                second = t.getName();
            }
        }
        if (second.indexOf("餐") != -1) {
            int hour = Integer.valueOf(sd.format(new Date(invoiceVO.getTime().getTime())));
            if (hour > 0 && hour < 11) {
                second = "早餐";
            } else if (hour >= 11 && hour < 18) {
                second = "午餐";
            } else {
                second = "晚餐";
            }
        }
        invoiceVO.setMaintype(main);
        invoiceVO.setSecondtype(second);
        invoiceDB.update(invoiceVO);
        Log.d(TAG, invoiceVO.getInvNum() + " : " + main + " : " + second);
        return invoiceVO;
    }


    private HashMap<String, String> getInvoice(String user, String password, String startDate, String endDate, String iswin) {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("version", "0.5");
        hashMap.put("cardType", "3J0002");
        hashMap.put("cardNo", user);
        hashMap.put("expTimeStamp", "2147483647");
        hashMap.put("action", "carrierInvChk");
        hashMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("startDate", startDate);
        hashMap.put("endDate", endDate);
        hashMap.put("onlyWinningInv", iswin);
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

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);


        //避免job null
        if(progressT==null||percentage==null)
        {
            return;
        }

        int statue = values[0].intValue();
        int month = values[1].intValue();
        int percent = values[2].intValue();
        String s, totalS = "";

        if (statue == 0) {
            s = "下載中";
        } else if (statue == 1) {
            s = "重新下載";
        } else {
            s = "下載失敗";
        }


        if (action.equals("download")) {
            if (downloadS.equals("price")) {
                totalS = (year - 1911) + "年" + priceMonth.get(month) + s;
            } else {
                if (percent > 100) {
                    percent = 100;
                }
                totalS = (year - 1911) + "年" + (month + 1) + "月雲端發票\n" + s;
            }
            progressT.setText(totalS);
            percentage.setText(String.valueOf(percent) + "%");
        } else {
            Calendar now = new GregorianCalendar((year - 1911), month, 1);
            progressT.setText(Common.sYear.format(new Date(now.getTimeInMillis())) + s);
            percentage.setText(String.valueOf(percent) + "%");
        }
    }


    //傳遞View

    public void setPercentage(TextView percentage) {
        this.percentage = percentage;
    }

    public void setProgressT(TextView progressT) {
        this.progressT = progressT;
    }
}
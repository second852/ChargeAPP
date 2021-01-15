package com.chargeapp.whc.chargeapp.ChargeDB;


import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.Download;
import com.chargeapp.whc.chargeapp.Control.EleInvoice.EleDonate;
import com.chargeapp.whc.chargeapp.Control.EleInvoice.EleSetCarrier;
import com.chargeapp.whc.chargeapp.Control.EleInvoice.EleUpdateCarrier;
import com.chargeapp.whc.chargeapp.Control.HomePage.HomePagetList;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.SelectList.SelectListModelCom;
import com.chargeapp.whc.chargeapp.Control.SelectList.SelectListModelIM;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectDetList;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectShowCircleDe;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectShowCircleDeList;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateInvoice;
import com.chargeapp.whc.chargeapp.Control.Welcome;
import com.chargeapp.whc.chargeapp.Job.DownloadNewDataJob;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ElePeriod;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.TypeCode.PriceNotify;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
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
import java.util.Vector;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

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
    private ElePeriodDB elePeriodDB;
    private HashMap<Integer, String> priceMonth;
    private PriceDB priceDB;
    private BigDecimal one=BigDecimal.ONE;
    private BigDecimal hundred=new BigDecimal(100);
    private BigDecimal sixteen=new BigDecimal(16);
    private BigDecimal total;
    private Context context;
    private int timeout;

    public GetSQLDate(Object object,Context context) {
        total =new BigDecimal(0);
        this.object = object;
        if (object instanceof JobService) {
            JobService jobService = (JobService) object;
            Common.setChargeDB(jobService);
        }
//        Common.setChargeDB((Activity)object);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB);
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB);
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB);
        elePeriodDB = new ElePeriodDB(MainActivity.chargeAPPDB);
        priceDB = new PriceDB(MainActivity.chargeAPPDB);
        this.context=context;
//        invoiceDB.deleteBytime(Timestamp.valueOf("2018-09-01 00:00:00"));
    }

    public GetSQLDate(Object object, InvoiceVO invoiceVO,Context context) {
        this.object = object;
        this.invoiceVO = invoiceVO;
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB);
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB);
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB);
        this.context=context;
    }


    @Override
    protected String doInBackground(Object... params) {
        action = params[0].toString();
        timeout=10;
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

                        //insert 6 month
                        for (int i = 0; i <= 6; i++) {
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
                timeout=3;
                PriceDB priceDB = new PriceDB(MainActivity.chargeAPPDB);
                priceMonth = Common.getPriceMonth();
                if (priceDB.getAll().size() <= 0) {
                    searchPriceNul();
                } else {
                    searchNewPriceNul();
                }
                jsonIn = updateInvoice();
                return jsonIn;
            } else if (action.equals("searchHeartyTeam")) {
                String keyworld = params[1].toString();
                jsonIn = searchHeartyTeam(keyworld);
                return jsonIn;
            } else if (action.equals("reDownload")) {
                jsonIn = getInvoiceDetail(invoiceVO);
            } else if (action.equals("checkCarrier")) {
                user = params[1].toString();
                password = params[2].toString();
                Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                jsonIn = findMonthHead(year, month, user, password);
            } else if (action.equals("getWinInvoice")) {

                Context downloadNewDataJob = (Context) object;
                List<CarrierVO> carrierVOS = carrierDB.getAll();
                List<PriceVO> priceVOS = priceDB.getNotCheckAll();
                List<PriceVO> notifyPriceVOS = new ArrayList<>();
                int year, oneM, twoM, length;
                String period;
                boolean checkOne = true, checkTwo = true;
                for (PriceVO priceVO : priceVOS) {
                    for (CarrierVO carrierVO : carrierVOS) {
                        period = priceVO.getInVoYm();
                        length = period.length();
                        year = Integer.valueOf(period.substring(0, length - 2)) + 1911;

                        twoM = Integer.valueOf(period.substring(length - 2, length));
                        jsonIn = findMonthHead(year, twoM, carrierVO.getCarNul(), carrierVO.getPassword(), "Y");
                        checkOne = checkWinInvoice(jsonIn, priceVO, carrierVO.getCarNul(), carrierVO.getPassword());

                        oneM = twoM - 1;
                        jsonIn = findMonthHead(year, oneM, carrierVO.getCarNul(), carrierVO.getPassword(), "Y");
                        checkTwo = checkWinInvoice(jsonIn, priceVO, carrierVO.getCarNul(), carrierVO.getPassword());

                    }

                    if (checkOne && checkTwo) {
                        priceVO.setCheck(true);
                    }

                    if (PriceNotify.Special.equals(priceVO.getNeedNotify())) {
                        notifyPriceVOS.add(priceVO);
                    }
                    priceDB.update(priceVO);
                }


                if (!notifyPriceVOS.isEmpty()) {
                    String nYear, nMonth;
                    StringBuilder sb = new StringBuilder();
                    for (PriceVO priceVO : notifyPriceVOS) {
                        period = priceVO.getInVoYm();
                        length = period.length();
                        nYear = period.substring(0, length - 2);
                        nMonth = Common.priceMonth().get(period.substring(length - 2, length));
                        sb.append(nYear + "年" + nMonth + " ");
                    }
                    sb.append("期別中獎!");
                    Intent activeI = new Intent(downloadNewDataJob, Welcome.class);
                    activeI.setAction("nulPriceNotify");
                    Common.showNotification("恭喜發票中獎!", sb.toString(), downloadNewDataJob, 999, activeI);
                }
            }else if(action.equals("checkId"))
            {
                checkId();
            }else if(action.equals("getThisMonth"))
            {
                timeout=3;
                Integer year=Integer.valueOf(params[1].toString());
                Integer month=Integer.valueOf(params[2].toString());
                jsonIn=getThisMonth(year,month);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonIn = "error";
        }
        return jsonIn;
    }

    private String getThisMonth(Integer year,Integer month){
        String jsonIn=null;
        for(CarrierVO carrierVO:carrierDB.getAll()){
            String user=carrierVO.getCarNul();
            String password=carrierVO.getPassword();
            jsonIn = findMonthHead(year, month, user, password);
            if (jsonIn.contains("code")) {
                JsonObject js = gson.fromJson(jsonIn, JsonObject.class);
                String code = js.get("code").getAsString().trim();
                //success
                if (code.equals("200")) {
                    jsonIn = getjsonIn(jsonIn, password, user);
                }
            }
        }
        return jsonIn;
    }



    private boolean checkWinInvoice(String jsonIn, PriceVO priceVO, String user, String password) {
        if (jsonIn.indexOf("200") == -1) {
            return false;
        }
        JsonObject js = gson.fromJson(jsonIn, JsonObject.class);

        if (js.get("code").isJsonNull() || (!js.get("code").getAsString().equals("200"))) {
            return false;
        }

        Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
        String s = js.get("details").toString();
        List<JsonObject> b = gson.fromJson(s, cdType);
        if (b.isEmpty()) {
            return true;
        }

        priceVO.setNeedNotify(PriceNotify.Normal);
        for (JsonObject jsonObject : b) {

            String invNum = jsonObject.get("invNum").getAsString();
            String amount = jsonObject.get("amount").getAsString();
            InvoiceVO invoiceVO = invoiceDB.findOldByNulAmount(invNum, amount);
            if (invoiceVO == null) {

                invoiceVO = jsonToInVoice(jsonObject, user, password);
                getInvoiceDetail(invoiceVO);
                invoiceVO = invoiceDB.findOldByNulAmount(invNum, amount);
                List<String> inWin = Common.answer(invNum, priceVO);
                invoiceVO.setIswin(inWin.get(0));
                invoiceVO.setIsWinNul(inWin.get(1));
                invoiceDB.update(invoiceVO);


                BankDB bankDB = new BankDB(MainActivity.chargeAPPDB);
                BankVO bankVO = new BankVO();
                bankVO.setFixDate("false");
                bankVO.setMoney(Common.getIntPrice().get(invoiceVO.getIswin()));
                bankVO.setDate(new java.sql.Date(System.currentTimeMillis()));
                bankVO.setMaintype("中獎");
                int month = Integer.parseInt(priceVO.getInVoYm().substring(3));
                String detail = priceVO.getInVoYm().substring(0, 3) + "年" + Common.getPriceMonth().get(month)
                        + Common.getPriceName().get(invoiceVO.getIswin()) + " : " + Common.getPrice().get(invoiceVO.getIswin());
                bankVO.setDetailname(detail);
                bankDB.insert(bankVO);


            } else {
                List<String> inWin = Common.answer(invNum.substring(2), priceVO);
                invoiceVO.setIswin(inWin.get(0));
                invoiceVO.setIsWinNul(inWin.get(1));
                //專屬特別獎
                if (invoiceVO.getIswin().equals("N")) {
                    invoiceVO.setIswin("other");
                    invoiceVO.setIsWinNul(invNum);
                    priceVO.setNeedNotify(PriceNotify.Special);
                    invoiceDB.update(invoiceVO);
                }
            }


        }

        return true;
    }


    private void updateErrorDonateMarK(Set<ElePeriod> elePeriods, CarrierVO carrierVO) {
        String jsonIn;
        for (ElePeriod elePeriod : elePeriods) {
            try {
                jsonIn = findMonthHead(elePeriod.getYear(), elePeriod.getMonth(), carrierVO.getCarNul(), carrierVO.getPassword());
                JsonObject js = gson.fromJson(jsonIn, JsonObject.class);
                String code = js.get("code").getAsString().trim();
                if (code.equals("200")) {
                    Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
                    String s = js.get("details").toString();
                    if (s != null && s.length() > 0) {
                        List<JsonObject> jsonObjects = gson.fromJson(s, cdType);
                        String inNul, donateMark;
                        String amount;
                        for (JsonObject jsonObject : jsonObjects) {
                            donateMark = jsonObject.get("donateMark").getAsString();
                            inNul = jsonObject.get("invNum").getAsString();
                            amount = jsonObject.get("amount").getAsString();
                            InvoiceVO invoiceVO = invoiceDB.findOldByNulAmount(inNul, amount);
                            invoiceVO.setDonateMark(donateMark);
                            invoiceDB.update(invoiceVO);
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    }


    private void checkId()
    {
        List<CarrierVO> carrierVOS = carrierDB.getAll();
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        for (CarrierVO carrierVO : carrierVOS) {
            String jsonIn = findMonthHead(year, month, carrierVO.getCarNul(), carrierVO.getPassword());
            JsonObject js = gson.fromJson(jsonIn, JsonObject.class);
            if (js.get("code").getAsString().equals("919")) {
                if (Common.lostCarrier == null) {
                    Common.lostCarrier = new Vector<>();
                }
                Common.lostCarrier.add(carrierVO);
            }
        }
    }



    private String updateInvoice() throws IOException {
        downloadS = "invoice";
        String jsonIn = null;
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


            //更新errorDonateMark
            List<InvoiceVO> invoiceVOS = invoiceDB.getErrorDonateMark(carrierVO.getCarNul());
            if (invoiceVOS.size() > 0) {
                //大於6個月發票 DonateMark=1
                Calendar nowCal = Calendar.getInstance();
                nowCal.add(Calendar.MONTH, -6);
                nowCal.set(Calendar.DAY_OF_WEEK, 1);
                nowCal.set(Calendar.SECOND, 0);
                nowCal.set(Calendar.HOUR_OF_DAY, 0);
                nowCal.set(Calendar.MINUTE, 0);

                Set<ElePeriod> elePeriods = new HashSet<>();
                Calendar period = new GregorianCalendar();
                for (InvoiceVO invoiceVO : invoiceVOS) {
                    if (invoiceVO.getTime().getTime() < nowCal.getTimeInMillis()) {
                        invoiceVO.setDonateMark("99");
                        invoiceDB.update(invoiceVO);
                    } else {
                        period.setTime(new Date(invoiceVO.getTime().getTime()));
                        elePeriods.add(new ElePeriod(period.get(Calendar.YEAR), period.get(Calendar.MONTH)));
                    }
                }
                updateErrorDonateMarK(elePeriods, carrierVO);
            }

            //更新載具-找載具最新的月
            Calendar differCal = new GregorianCalendar();
            long maxTime = invoiceDB.findIVByMaxDate(carrierVO.getCarNul());
            long differTime = System.currentTimeMillis() - maxTime;
            differCal.setTime(new Date(differTime));
            if (differCal.get(Calendar.MONTH) >= 6 && differTime > 0) {
                //超過6個月
                jsonIn = searchNewInvoice(carrierVO);
            } else {
                //未超過6個月
                jsonIn = searchToMonth(carrierVO, maxTime);
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
            } else {
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
        Log.d(TAG, "startDay" + startDate + "endDate" + endDate);
        //設定傳遞參數
        data = getInvoice(user, password, startDate, endDate, "N");
        url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?";
        jsonIn = getRemoteData(url, data);
        return jsonIn;
    }


    private String findMonthHead(int year, int month, String user, String password, String isWin) {
        String startDate, endDate, url, jsonIn;
        HashMap<String, String> data;
        Calendar cal = new GregorianCalendar(year, month - 1, 1);
        startDate = Common.sTwo.format(new Date(cal.getTimeInMillis()));
        cal.set(year, month - 1, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDate = Common.sTwo.format(new Date(cal.getTimeInMillis()));
        Log.d(TAG, "startDay" + startDate + "endDate" + endDate);
        //設定傳遞參數
        data = getInvoice(user, password, startDate, endDate, isWin);
        url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?";
        jsonIn = getRemoteData(url, data);
        return jsonIn;
    }

    private String downLoadOtherMon(CarrierVO carrierVO) {
        String user = carrierVO.getCarNul();
        String password = carrierVO.getPassword();
        String jsonIn = "";
        List<ElePeriod> elePeriods = elePeriodDB.getCarrierAll(user);

        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH,-6);
        int minYear=calendar.get(Calendar.YEAR);
        int minMon=calendar.get(Calendar.MONTH);


        for (ElePeriod elePeriod : elePeriods) {
            year = elePeriod.getYear();
            month = elePeriod.getMonth();
            boolean isAlreadyDownload=false;
            if(minYear==year&&minMon>month)
            {
                isAlreadyDownload=true;
            }else if(minYear>year)
            {
                isAlreadyDownload=true;
            }


            if(isAlreadyDownload)
            {
                elePeriod.setDownload(true);
                elePeriodDB.update(elePeriod);
                continue;
            }



            jsonIn = findMonthHead(year, month, user, password);
            if (jsonIn.contains("code")) {
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
        PriceDB priceDB = new PriceDB(MainActivity.chargeAPPDB);
        String url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invapp/InvApp?";
        String jsonIn = "";
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        if(month%2!=0)
        {
            month=month+1;
        }

        StringBuffer period;
        String max = priceDB.findMaxPeriod();
        //抓6個月
        for(int i=0;i<6;i++)
        {
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
                break;
            }
            HashMap<String, String> data = getPriceMap(period.toString());
            jsonIn = getRemoteData(url, data);
            if (jsonIn.contains("200") ) {
                PriceVO priceVO = jsonToPriceVO(jsonIn);
                priceDB.insert(priceVO);
                Log.d(TAG, "insert" + priceVO.getInVoYm());
            }
            month=month-2;
        }
        return jsonIn;
    }

    private String searchPriceNul() {
        action = "price";
        PriceDB priceDB = new PriceDB(MainActivity.chargeAPPDB);
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
                publishProgress(2, month, total.intValue());
                return jsonin;
            }
            JsonObject js = gson.fromJson(jsonin, JsonObject.class);
            String code = js.get("code").getAsString().trim();
            if (code.equals("200")) {
                PriceVO priceVO = jsonToPriceVO(jsonin);
                priceDB.insert(priceVO);
                total = total.add(one);
                publishProgress(0, month,total.intValue());
                Log.d(TAG, "insert price :" + priceVO.getInVoYm());
            }
            month = month - 2;
        }
        return jsonin;
    }

    private PriceVO jsonToPriceVO(String jsonin) {
        Gson gson = new Gson();
        JsonObject js = gson.fromJson(jsonin, JsonObject.class);
        PriceVO priceVO = new PriceVO();
        priceVO.setInVoYm(js.get("invoYm").getAsString());
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
            int max = 6, i = 0;//超過6個月不下載
            while (true) {
                //如果有舊的
                ElePeriod elePeriod = elePeriodDB.OldElePeriod(new ElePeriod(todayYear, todayMonth, carrierVO.getCarNul(), false));
                if (elePeriod != null) {
                    elePeriod.setDownload(false);
                    elePeriodDB.update(elePeriod);
                } else {
                    elePeriodDB.insert(new ElePeriod(todayYear, todayMonth, carrierVO.getCarNul(), false));
                }


                todayMonth = todayMonth - 1;
                if (todayMonth < 0) {
                    todayMonth = 12 + todayMonth;
                    todayYear = todayYear - 1;
                }
                //到最大個月為止
                if ((todayMonth < lastMonth && todayYear <= lastYear) || i >= max) {
                    break;
                }
                i++;
            }
            jsonIn = downLoadOtherMon(carrierVO);
            //detail = 0
            List<InvoiceVO> invoiceVOS = invoiceDB.getNoDetailAll();
            for (InvoiceVO invoiceVO : invoiceVOS) {
                updateInvoiceDetail(invoiceVO);
            }
        }
        return jsonIn;
    }


    public String searchTodayDate(Calendar last, Calendar today, String user, String password) throws IOException {
        String jsonIn;
        int finalDay = last.get(Calendar.DAY_OF_MONTH);
        Calendar searchStart = new GregorianCalendar(last.get(Calendar.YEAR), last.get(Calendar.MONTH) - 1, 1);
        Calendar searchEnd = new GregorianCalendar(last.get(Calendar.YEAR), last.get(Calendar.MONTH) - 1, searchStart.getActualMaximum(Calendar.DAY_OF_MONTH));
        switch (finalDay) {
            case 1:

                //前三天
                searchStart.set(Calendar.DAY_OF_MONTH, searchEnd.get(Calendar.DAY_OF_MONTH) - 3);
                searchInvoiceData(searchStart, searchEnd, user, password);
                //更新
                jsonIn = searchInvoiceData(last, today, user, password);
                break;
            case 2:
                //前三天
                searchStart.set(Calendar.DAY_OF_MONTH, searchEnd.get(Calendar.DAY_OF_MONTH) - 2);
                searchInvoiceData(searchStart, searchEnd, user, password);
                last.set(Calendar.DAY_OF_MONTH, 1);
                //更新
                jsonIn = searchInvoiceData(last, today, user, password);
                break;
            case 3:
                //前三天
                searchStart.set(Calendar.DAY_OF_MONTH, searchEnd.get(Calendar.DAY_OF_MONTH) - 1);
                searchInvoiceData(searchStart, searchEnd, user, password);
                last.set(Calendar.DAY_OF_MONTH, 1);
                //更新
                jsonIn = searchInvoiceData(last, today, user, password);
                Log.d(TAG, "startDay" + finalDay);
                break;
            default:
                last.add(Calendar.DAY_OF_MONTH, -3);
                //更新
                jsonIn = searchInvoiceData(last, today, user, password);
                Log.d(TAG, "startDay default" + finalDay);
                break;
        }
        return jsonIn;
    }


    public String searchInvoiceData(Calendar last, Calendar today, String user, String password) throws IOException {
        String startDay = sf.format(last.getTime());
        String endDay = sf.format(today.getTime());
        HashMap data;
        Log.d(TAG, "startDate: " + startDay + "endDate" + endDay + "user" + user);
        data = getInvoice(user, password, startDay, endDay, "N");
        String url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?";
        String jsonIn = getRemoteData(url, data);
        if (jsonIn.indexOf("200") == -1) {
            return jsonIn;
        }
        jsonIn = getjsonIn(jsonIn, password, user);
        return jsonIn;
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


    private String getjsonIn(String jsonIn, String password, String user) {
        try {
            InvoiceVO invoiceVO;
            List<InvoiceVO> oldInvoiceList;
            JsonObject js = gson.fromJson(jsonIn, JsonObject.class);
            Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
            String s = js.get("details").toString();
            List<JsonObject> b = gson.fromJson(s, cdType);
            //設定processBar process
            BigDecimal one=BigDecimal.ONE;
            BigDecimal max=new BigDecimal(b.size());

            if (b.size() != 0) {
                one = one.divide(max,4,ROUND_HALF_DOWN).multiply(sixteen).multiply(hundred);
            }
            String result = "";
            for (JsonObject j : b) {
                invoiceVO = jsonToInVoice(j, password, user);
                if (invoiceVO != null) {
                    //確認有無重複
                    oldInvoiceList = invoiceDB.checkInvoiceRepeat(invoiceVO.getInvNum(), invoiceVO.getRealAmount(), new java.sql.Date(invoiceVO.getTime().getTime()));
                    if (!oldInvoiceList.isEmpty()) {
                        continue;
                    }
                    //查詢電子發票明細和insert invoice
                    result= getInvoiceDetail(invoiceVO);
                    total=total.add(one);
                    publishProgress(0, month, total.intValue());
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
        Log.d(" jsonIn","action"+ action);
        StringBuilder jsonIn = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            byte[] postData = getPostDataString(data).getBytes(StandardCharsets.UTF_8);
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setUseCaches(false);
            conn.setConnectTimeout(timeout);
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
                Log.d(" jsonIn", jsonIn.toString());
                Log.d(" jsonIn", data.toString());
            } else {
                jsonIn = new StringBuilder();
                jsonIn.append("timeout");
            }
        } catch (SocketTimeoutException e) {
            jsonIn = new StringBuilder();
            jsonIn.append("timeout");
        } catch (Exception e) {
            Log.d(TAG, "error" + e.toString());
            jsonIn = new StringBuilder();
            jsonIn.append("error");
        } finally {

            if (conn != null) {
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
                if (Common.lostCarrier != null) {
                    if (!Common.lostCarrier.isEmpty()) {
                        StringBuffer sb = new StringBuffer();
                        for (CarrierVO c : Common.lostCarrier) {
                            sb.append(c.getCarNul() + " ");
                        }
                        sb.append("驗證碼錯誤，請到雲端發票 : \n\"綁定/取消載具修改\"");
                        Common.showToast(download.activity, sb.toString());
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
                    Common.showToast(context, "財政部網路忙線~");
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
            } else if (object instanceof EleUpdateCarrier) {
                EleUpdateCarrier eleUpdateCarrier = (EleUpdateCarrier) object;
                eleUpdateCarrier.check(s);
            } else if (object instanceof DownloadNewDataJob||object instanceof Welcome) {
                new Common().AutoSetPrice();
            }
        } catch (Exception e) {
            Log.d(TAG, "onPostExecute" + e.getMessage());
        } finally {
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
            InvoiceVO type = getType(invoiceVO,context);
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
//        hashMap.put("sellerName", invoiceVO.getSellerName());
//        hashMap.put("amount", invoiceVO.getRealAmount());
        hashMap.put("appID", "EINV3201711184648");
        hashMap.put("cardEncrypt", invoiceVO.getCardEncrypt());
        String detailjs = getRemoteData(urldetail, hashMap);
        if (detailjs == null || detailjs.equals("error") || detailjs.equals("timeout")) {

            if(invoiceVO.getId()==0)
            {
                invoiceDB.insert(invoiceVO);
            }else{
                invoiceDB.update(invoiceVO);
            }
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
            InvoiceVO type = getType(invoiceVO,context);

            if(invoiceVO.getId()==0)
            {
                invoiceDB.insert(type);
            }else{
                invoiceDB.update(type);
            }

            Log.d("total :", Common.sDay.format(new Date(invoiceVO.getTime().getTime())) + " : " + invoiceVO.getRealAmount() + " : " + invoiceVO.getAmount());
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
        InvoiceVO type = getType(invoiceVO,context);
        invoiceDB.update(type);
        Log.d("total :", Common.sDay.format(new Date(invoiceVO.getTime().getTime())) + " : " + invoiceVO.getInvNum());
        detailjs = "success";
        return detailjs;
    }


    private InvoiceVO getType(InvoiceVO invoiceVO,Context context) {
        SharedPreferences sharedPreferences=context.getSharedPreferences("Charge_User",Context.MODE_PRIVATE);
        boolean autoCategory=sharedPreferences.getBoolean("autoCategory",true);
        if(autoCategory){
            List<TypeDetailVO> typeDetailVOS = typeDetailDB.getTypdAll();
            String main = "其他", second = "其他";
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
        }else{
            invoiceVO.setMaintype("未分類");
            invoiceVO.setSecondtype("未分類");
        }
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


    private HashMap<String, String> getWinTotal(String user, String password, String startDate, String endDate) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("version", "1.0");
        hashMap.put("appID", "EINV3201711184648");
        hashMap.put("barcode", user);
        hashMap.put("verifyCode", password);
        hashMap.put("invoiceDateS", "20190801");
        hashMap.put("invoiceDateE", "20190830");
//        hashMap.put("hsnNm", "桃園市");
//        hashMap.put("townNm", "八德區");
//        hashMap.put("cardCodeNm", "共通性載具");
//        hashMap.put("cardTypeNm", "手機條碼");
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
        if (progressT == null || percentage == null) {
            return;
        }

        int statue = values[0].intValue();
        int month = values[1].intValue();
        BigDecimal percent=new BigDecimal(values[2]).divide(hundred,1, ROUND_HALF_DOWN);//取到小數點第一位
        String s, totalS = "";

        if (statue == 0) {
            s = "下載中";
        } else if (statue == 1) {
            s = "重新下載";
        } else {
            s = "下載失敗";
        }

        if (percent.compareTo(hundred)>0) {
            percent = hundred;
        }

        if (action.equals("download")) {
            if (downloadS.equals("price")) {
                totalS = (year - 1911) + "年" + priceMonth.get(month) + s;
            } else {

                totalS = (year - 1911) + "年" + (month + 1) + "月雲端發票\n" + s;
            }
            progressT.setText(totalS);
            percentage.setText(hundred.toString() + "%");
        } else {
            Calendar now = new GregorianCalendar((year - 1911), month, 1);
            progressT.setText(Common.sYear.format(new Date(now.getTimeInMillis())) + s);
            percentage.setText(percent.toString() + "%");
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
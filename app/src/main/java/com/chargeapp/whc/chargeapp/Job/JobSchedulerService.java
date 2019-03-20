package com.chargeapp.whc.chargeapp.Job;


import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 1709008NB01 on 2018/3/29.
 */

public class JobSchedulerService extends JobService {



    private ConsumeDB consumeDB;
    private BankDB bankDB;
    private Gson gson;
    private SimpleDateFormat sf;
    private Calendar setNewTime;
    private GoalDB goalDB;



    @Override
    public boolean onStartJob(JobParameters jobParameters) {


        try {
            Log.d("service","service start");
            gson = new Gson();
            Calendar calendar = Calendar.getInstance();
            sf = new SimpleDateFormat("yyyy-MM-dd");
            SharedPreferences sharedPreferences = getSharedPreferences("Charge_User", Context.MODE_PRIVATE);
            String setTime = sharedPreferences.getString("userTime", "6:00 p.m.").trim();


            //確認今天是否重設過
            boolean todaySet = sharedPreferences.getBoolean(sf.format(new Date(calendar.getTimeInMillis())), false);
            if (todaySet) {
                return true;
            }
            int result = ContextCompat.checkSelfPermission(JobSchedulerService.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(result != PackageManager.PERMISSION_GRANTED)
            {
                return true;
            }
            result = ContextCompat.checkSelfPermission(JobSchedulerService.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if(result != PackageManager.PERMISSION_GRANTED)
            {
                return true;
            }
            Common.setChargeDB(this);
            Common.insertNewTableCol();
            consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
            bankDB = new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
            goalDB = new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());

            List<BankVO> bankVOS = bankDB.getFixDate();
            List<ConsumeVO> consumerVOS = consumeDB.getFixdate();
            int hour, min;
            if (setTime.indexOf("p") == -1) {
                hour = new Integer(setTime.substring(0, setTime.indexOf(":")));
                min = new Integer(setTime.substring(setTime.indexOf(":") + 1, setTime.indexOf("a")).trim());
            } else {
                hour = new Integer(setTime.substring(0, setTime.indexOf(":"))) + 12;
                min = new Integer(setTime.substring(setTime.indexOf(":") + 1, setTime.indexOf("p")).trim());
            }

            JsonObject jsonObject;
            Calendar date = Calendar.getInstance();
            int year = date.get(Calendar.YEAR);
            int month = date.get(Calendar.MONTH);
            int day = date.get(Calendar.DAY_OF_MONTH);
            int dweek = date.get(Calendar.DAY_OF_WEEK);
            setNewTime = new GregorianCalendar(year, month, day, hour, min, 0);


            if (consumerVOS.size() > 0 && consumerVOS != null) {
                for (ConsumeVO consumeVO : consumerVOS) {
                    //避免紀錄當天重複
                    if(sf.format(consumeVO.getDate()).equals(sf.format(new Date(setNewTime.getTimeInMillis()))))
                    {
                        continue;
                    }

                    String detail = consumeVO.getFixDateDetail();
                    jsonObject = gson.fromJson(detail, JsonObject.class);
                    String action = jsonObject.get("choicestatue").getAsString().trim();

                    if ("每天".equals(action)) {
                        Log.d("service", "consumeVO");
                        boolean noWeekend = jsonObject.get("noweek").getAsBoolean();
                        if (noWeekend && dweek == 7) {
                            continue;
                        }
                        if (noWeekend && dweek == 1) {
                            continue;
                        }
                        Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                        Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                        ConsumeVO cccc = consumeDB.getAutoTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), consumeVO.getId());
                        if (cccc == null) {
                            consumeVO.setNotify("false");
                            consumeVO.setFixDate("false");
                            consumeVO.setAuto(true);
                            consumeVO.setIsWin("0");
                            consumeVO.setNumber("");
                            consumeVO.setIsWinNul("0");
                            consumeVO.setAutoId(consumeVO.getId());
                            consumeVO.setDate(new Date(setNewTime.getTimeInMillis()));
                            consumeDB.insert(consumeVO);
                        }

                    } else if ("每周".equals(action)) {
                        String fixdetail = jsonObject.get("choicedate").getAsString().trim();
                        HashMap<String, Integer> change = getStringtoInt();
                        if (date.get(Calendar.DAY_OF_WEEK) == change.get(fixdetail)) {
                            Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                            Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                            ConsumeVO cccc = consumeDB.getAutoTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), consumeVO.getId());
                            if (cccc == null) {
                                consumeVO.setNotify("false");
                                consumeVO.setFixDate("false");
                                consumeVO.setAuto(true);
                                consumeVO.setIsWin("0");
                                consumeVO.setNumber("");
                                consumeVO.setIsWinNul("0");
                                consumeVO.setAutoId(consumeVO.getId());
                                consumeVO.setDate(new Date((date.getTimeInMillis())));
                                consumeDB.insert(consumeVO);
                            }

                        }
                    } else if ("每月".equals(action)) {
                        int Maxday = date.getActualMaximum(Calendar.DAY_OF_MONTH);
                        String fixdate = jsonObject.get("choicedate").getAsString().trim();
                        fixdate = fixdate.substring(0, fixdate.indexOf("日"));
                        if (fixdate.equals(String.valueOf(day))) {
                            Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                            Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                            ConsumeVO cccc = consumeDB.getAutoTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), consumeVO.getId());
                            if (cccc == null) {
                                consumeVO.setNotify("false");
                                consumeVO.setFixDate("false");
                                consumeVO.setAuto(true);
                                consumeVO.setIsWin("0");
                                consumeVO.setNumber("");
                                consumeVO.setIsWinNul("0");
                                consumeVO.setAutoId(consumeVO.getId());
                                consumeVO.setDate(new Date((date.getTimeInMillis())));
                                consumeDB.insert(consumeVO);
                            }


                        }
                        if (Maxday < Integer.valueOf(fixdate) && day == Maxday) {
                            Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                            Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                            ConsumeVO cccc = consumeDB.getAutoTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), consumeVO.getId());
                            if (cccc == null) {
                                consumeVO.setNotify("false");
                                consumeVO.setFixDate("false");
                                consumeVO.setAuto(true);
                                consumeVO.setIsWin("0");
                                consumeVO.setNumber("");
                                consumeVO.setIsWinNul("0");
                                consumeVO.setAutoId(consumeVO.getId());
                                consumeVO.setDate(new Date((date.getTimeInMillis())));
                                consumeDB.insert(consumeVO);
                            }


                        }
                    }else{
                        //每年
                        String fixdate = jsonObject.get("choicedate").getAsString().trim();
                        fixdate = fixdate.substring(0, fixdate.indexOf("月"));
                        int d = Integer.valueOf(fixdate) - 1;
                        if (month == d && day == 1) {
                            Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                            Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                            ConsumeVO cccc = consumeDB.getAutoTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), consumeVO.getId());
                            if (cccc == null) {
                                consumeVO.setNumber("");
                                consumeVO.setNotify("false");
                                consumeVO.setFixDate("false");
                                consumeVO.setAuto(true);
                                consumeVO.setIsWin("0");
                                consumeVO.setIsWinNul("0");
                                consumeVO.setAutoId(consumeVO.getId());
                                consumeVO.setDate(new Date((date.getTimeInMillis())));
                                consumeDB.insert(consumeVO);
                            }

                        }
                    }
                }
            }


            for (BankVO b : bankVOS) {
                Log.d("service", "bankVOS");
                //避免紀錄當天重複
                if(sf.format(b.getDate()).equals(sf.format(new Date(setNewTime.getTimeInMillis()))))
                {
                    continue;
                }

                String detail = b.getFixDateDetail();
                jsonObject = gson.fromJson(detail, JsonObject.class);
                String action = jsonObject.get("choicestatue").getAsString().trim();
                if ("每天".equals(action)) {
                    Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                    Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                    BankVO bb = bankDB.getAutoBank(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), b.getId());
                    if (bb == null) {
                        b.setFixDate("false");
                        b.setAuto(true);
                        b.setAutoId(b.getId());
                        b.setDate(new Date(setNewTime.getTimeInMillis()));
                        bankDB.insert(b);
                    }

                } else if ("每周".equals(action)) {
                    String fixdetail = jsonObject.get("choicedate").getAsString().trim();
                    HashMap<String, Integer> change = getStringtoInt();
                    if (date.get(Calendar.DAY_OF_WEEK) == change.get(fixdetail)) {
                        Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                        Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                        BankVO bb = bankDB.getAutoBank(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), b.getId());
                        if (bb == null) {
                            b.setFixDate("false");
                            b.setAuto(true);
                            b.setAutoId(b.getId());
                            b.setDate(new Date((date.getTimeInMillis())));
                            bankDB.insert(b);
                        }
                    }
                } else if ("每月".equals(action)) {
                    int Maxday = date.getActualMaximum(Calendar.DAY_OF_MONTH);
                    String fixdate = jsonObject.get("choicedate").getAsString().trim();
                    fixdate = fixdate.substring(0, fixdate.indexOf("日"));
                    if (fixdate.equals(String.valueOf(day))) {


                        Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                        Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                        BankVO bb = bankDB.getAutoBank(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), b.getId());
                        if (bb == null) {
                            b.setFixDate("false");
                            b.setAuto(true);
                            b.setAutoId(b.getId());
                            b.setDate(new Date((setNewTime.getTimeInMillis())));
                            bankDB.insert(b);
                        }

                    }
                    if (Maxday < Integer.valueOf(fixdate) && day == Maxday) {


                        Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                        Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                        BankVO bb = bankDB.getAutoBank(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), b.getId());
                        if (bb == null) {
                            b.setFixDate("false");
                            b.setAuto(true);
                            b.setAutoId(b.getId());
                            b.setDate(new Date((setNewTime.getTimeInMillis())));
                            bankDB.insert(b);
                        }

                    }
                } else {
                    String fixdate = jsonObject.get("choicedate").getAsString().trim();
                    fixdate = fixdate.substring(0, fixdate.indexOf("月"));
                    int d = Integer.valueOf(fixdate) - 1;
                    if (d == month && day == 1) {

                        Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                        Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                        BankVO bb = bankDB.getAutoBank(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), b.getId());
                        if (bb == null) {
                            b.setFixDate("false");
                            b.setAuto(true);
                            b.setAutoId(b.getId());
                            b.setDate(new Date((setNewTime.getTimeInMillis())));
                            bankDB.insert(b);
                        }
                    }
                }
            }

            //測試使用現在時間
//        setNewTime=Calendar.getInstance();

            NotifyUse(this,setNewTime.getTimeInMillis());
            //今天設定過 存檔
            sharedPreferences.edit().putBoolean(sf.format(new Date(calendar.getTimeInMillis())), true).apply();
            return true;
        }catch (Exception e)
        {
            return false;
        }

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }


    //判斷今天有沒有開獎
    public boolean notifyLottery() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 9 || month == 11) {
            if (day == 25) {
                return true;
            }
        }
        return false;
    }

    public void NotifyUse(Context context,long settime) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent;
        //版本判斷
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("service","ThirdReceiver");
            alarmIntent = new Intent(this, ThirdReceiver.class);
        }else{
            Log.d("service","SecondReceiver");
            alarmIntent = new Intent(this, SecondReceiver.class);
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        manager.set(AlarmManager.RTC_WAKEUP, settime, pendingIntent);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
        Log.d("service time",simpleDateFormat.format(new java.util.Date(settime)));
    }

    public HashMap<String, Integer> getStringtoInt() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("星期一", 2);
        hashMap.put("星期二", 3);
        hashMap.put("星期三", 4);
        hashMap.put("星期四", 5);
        hashMap.put("星期五", 6);
        hashMap.put("星期六", 7);
        hashMap.put("星期日", 1);
        return hashMap;
    }
}

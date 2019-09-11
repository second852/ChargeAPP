package com.chargeapp.whc.chargeapp.Job;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;


public class BootReceiver extends BroadcastReceiver {

    private ChargeAPPDB chargeAPPDB;
    private ConsumeDB consumeDB;
    private BankDB bankDB;
    private Gson gson;
    private SimpleDateFormat sf;
    private Calendar setNewTime;
    private int id;
    private GoalDB goalDB;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)||intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("BootReceiver","ACTION_DATE_CHANGED");
            gson = new Gson();
            id = 0;
            sf = new SimpleDateFormat("yyyy-MM-dd");
            chargeAPPDB = new ChargeAPPDB(context);
            consumeDB = new ConsumeDB(chargeAPPDB);
            bankDB = new BankDB(chargeAPPDB);
            goalDB=new GoalDB(chargeAPPDB);
            List<BankVO> bankVOS = bankDB.getFixDate();
            SharedPreferences sharedPreferences = context.getSharedPreferences("Charge_User", Context.MODE_PRIVATE);
            boolean setNotify = sharedPreferences.getBoolean("notify", true);

            String setTime = sharedPreferences.getString("userTime", "6:00 p.m.").trim();
            int hour, min;
            if (setTime.indexOf("p") == -1) {
                hour = Integer.valueOf(setTime.substring(0, setTime.indexOf(":")));
                min = Integer.valueOf(setTime.substring(setTime.indexOf(":") + 1, setTime.indexOf("a")).trim());
            } else {
                hour = Integer.valueOf(setTime.substring(0, setTime.indexOf(":"))) + 12;
                min = Integer.valueOf(setTime.substring(setTime.indexOf(":") + 1, setTime.indexOf("p")).trim());
            }
            List<ConsumeVO> consumerVOS = consumeDB.getFixdate();
            JsonObject jsonObject;
            Calendar date = Calendar.getInstance();
            int year = date.get(Calendar.YEAR);
            int month = date.get(Calendar.MONTH);
            int day = date.get(Calendar.DAY_OF_MONTH);
            int dweek = date.get(Calendar.DAY_OF_WEEK);
            setNewTime = new GregorianCalendar(year, month, day, hour, min, 0);

            //確認今天是否重設過
            boolean todaySet=sharedPreferences.getBoolean(sf.format(new Date(setNewTime.getTimeInMillis())), false);
            //表示今天已經跑過 不用再設定通知
            if(todaySet)
            {
                return;
            }

            if (consumerVOS.size() > 0 && consumerVOS != null) {
                for (ConsumeVO consumeVO : consumerVOS) {
                    String detail = consumeVO.getFixDateDetail();
                    jsonObject = gson.fromJson(detail, JsonObject.class);
                    String action = jsonObject.get("choicestatue").getAsString().trim();
                    boolean notify = Boolean.valueOf(consumeVO.getNotify());
                    if ("每天".equals(action)) {
                        boolean noWeekend = jsonObject.get("noweek").getAsBoolean();
                        if (noWeekend && dweek == 7) {
                            return;
                        }
                        if (noWeekend && dweek == 1) {
                            return;
                        }
                        Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                        Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                        ConsumeVO cccc = consumeDB.getAutoTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), consumeVO.getFkKey());
                        if (cccc == null) {
                            consumeVO.setNotify("false");
                            consumeVO.setFixDate("false");
                            consumeVO.setAuto(true);
                            consumeVO.setAutoId(consumeVO.getId());
                            consumeVO.setDate(new Date(setNewTime.getTimeInMillis()));
                            consumeDB.insert(consumeVO);

                        }
                        if (notify && setNotify) {
                            NotifyUse(consumeVO, context, setNewTime.getTimeInMillis());
                        }
                    } else if ("每周".equals(action)) {
                        String fixdetail = jsonObject.get("choicedate").getAsString().trim();
                        HashMap<String, Integer> change = getStringtoInt();
                        if (date.get(Calendar.DAY_OF_WEEK) == change.get(fixdetail)) {
                            Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                            Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                            ConsumeVO cccc = consumeDB.getAutoTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), consumeVO.getFkKey());
                            if (cccc == null) {
                                consumeVO.setNotify("false");
                                consumeVO.setFixDate("false");
                                consumeVO.setAuto(true);
                                consumeVO.setAutoId(consumeVO.getId());
                                consumeVO.setDate(new Date((date.getTimeInMillis())));
                                consumeDB.insert(consumeVO);
                                if (notify && setNotify) {
                                    NotifyUse(consumeVO, context, setNewTime.getTimeInMillis());
                                }
                            }
                        }
                    } else if ("每月".equals(action)) {
                        int Maxday = date.getActualMaximum(Calendar.DAY_OF_MONTH);
                        String fixdate = jsonObject.get("choicedate").getAsString().trim();
                        fixdate = fixdate.substring(0, fixdate.indexOf("日"));
                        if (fixdate.equals(String.valueOf(day))) {

                            Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                            Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                            ConsumeVO cccc = consumeDB.getAutoTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), consumeVO.getFkKey());
                            if (cccc == null) {
                                consumeVO.setNotify("false");
                                consumeVO.setFixDate("false");
                                consumeVO.setAuto(true);
                                consumeVO.setAutoId(consumeVO.getId());
                                consumeVO.setDate(new Date((date.getTimeInMillis())));
                                consumeDB.insert(consumeVO);
                                if (notify && setNotify) {
                                    NotifyUse(consumeVO, context, setNewTime.getTimeInMillis());
                                }
                            }
                        }
                        if (Maxday < Integer.valueOf(fixdate) && day == Maxday) {
                            Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                            Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                            ConsumeVO cccc = consumeDB.getAutoTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), consumeVO.getFkKey());
                            if (cccc == null) {
                                consumeVO.setNotify("false");
                                consumeVO.setFixDate("false");
                                consumeVO.setAuto(true);
                                consumeVO.setAutoId(consumeVO.getId());
                                consumeVO.setDate(new Date((date.getTimeInMillis())));
                                consumeDB.insert(consumeVO);
                                if (notify && setNotify) {
                                    NotifyUse(consumeVO, context, setNewTime.getTimeInMillis());
                                }
                            }
                        }
                    } else
                    id++;
                }
            }
            for (BankVO b : bankVOS) {
                String detail = b.getFixDateDetail();
                jsonObject = gson.fromJson(detail, JsonObject.class);
                String action = jsonObject.get("choicestatue").getAsString().trim();
                if ("每天".equals(action)) {
                    Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                    Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                    BankVO bb = bankDB.getAutoBank(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), b.getFkKey());
                    if(bb==null)
                    {
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
                        BankVO bb = bankDB.getAutoBank(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), b.getFkKey());
                        if(bb==null)
                        {
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
                        BankVO bb = bankDB.getAutoBank(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), b.getFkKey());
                        if(bb==null)
                        {
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
                        BankVO bb = bankDB.getAutoBank(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), b.getFkKey());
                        if(bb==null)
                        {
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
                        BankVO bb = bankDB.getAutoBank(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), b.getFkKey());
                        if(bb==null)
                        {
                            b.setFixDate("false");
                            b.setAuto(true);
                            b.setAutoId(b.getId());
                            b.setDate(new Date((setNewTime.getTimeInMillis())));
                            bankDB.insert(b);
                        }
                    }
                }
            }
            List<GoalVO> goalVOS=goalDB.getNotify();

            for(GoalVO goalVO:goalVOS)
            {
                String statue=goalVO.getNotifyStatue().trim();
                if(statue.equals("每天"))
                {
                   if(goalVO.isNoWeekend())
                   {
                       if(dweek==1||dweek==7)
                       {
                           return;
                       }
                   }
                   if(setNotify)
                   {
                       NotifyUse(context,goalVO);
                   }
                }else if(statue.equals("每周"))
                {
                    HashMap<String, Integer> change = getStringtoInt();
                    String dateStatue=goalVO.getNotifyDate().trim();
                    if(dweek==change.get(dateStatue))
                    {
                        if(setNotify)
                        {
                            NotifyUse(context,goalVO);
                        }
                    }
                }else if(statue.equals("每月"))
                {
                    int max=date.getActualMaximum(Calendar.DAY_OF_MONTH);
                    String dateStatue=goalVO.getNotifyDate().trim();
                    if(dateStatue.equals(String.valueOf(day)))
                    {
                        if(setNotify)
                        {
                            NotifyUse(context,goalVO);
                        }
                    }
                    if(day==max&&Integer.valueOf(dateStatue)>day)
                    {
                        if(setNotify)
                        {
                            NotifyUse(context,goalVO);
                        }
                    }
                }else {
                    String fixdate =goalVO.getNotifyDate().trim();
                    fixdate = fixdate.substring(0, fixdate.indexOf("月"));
                    int d = Integer.valueOf(fixdate) - 1;
                    if(month==d&&day==1)
                    {
                        if(setNotify)
                        {
                            NotifyUse(context,goalVO);
                        }
                    }
                }
                id++;
            }
            notifyLottery(context);

            //今天設定過 存檔
            sharedPreferences.edit().putBoolean(sf.format(new Date(setNewTime.getTimeInMillis())),true).apply();
        }
    }

    public void notifyLottery(Context context) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 9 || month == 11) {
            if (day == 25) {
                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Bundle bundle = new Bundle();
                bundle.putSerializable("action", "notifyNul");
                bundle.putSerializable("id", id);
                Intent alarmIntent = new Intent(context, SecondReceiver.class);
                alarmIntent.putExtras(bundle);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                manager.set(AlarmManager.RTC_WAKEUP, setNewTime.getTimeInMillis(), pendingIntent);
            }
        }
    }

    public void NotifyUse(ConsumeVO consumeVO, Context context, long settime) {
        String message = "繳納" + consumeVO.getSecondType() + "費用:" + consumeVO.getMoney();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Bundle bundle = new Bundle();
        bundle.putSerializable("action", "notifyC");
        bundle.putSerializable("comsumer", message);
        bundle.putSerializable("id", id);
        Intent alarmIntent = new Intent(context, SecondReceiver.class);
        alarmIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, id);
        manager.set(AlarmManager.RTC_WAKEUP, settime, pendingIntent);
        Log.d("xxxx", message);
    }

    public void NotifyUse( Context context,GoalVO goalVO) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Bundle bundle = new Bundle();
        bundle.putSerializable("action", "goalC");
        bundle.putSerializable("id", id);
        bundle.putSerializable("goal",goalVO.getId());
        Intent alarmIntent = new Intent(context, SecondReceiver.class);
        alarmIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, id);
        manager.set(AlarmManager.RTC_WAKEUP, setNewTime.getTimeInMillis(), pendingIntent);
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



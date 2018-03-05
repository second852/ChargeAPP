package com.chargeapp.whc.chargeapp.Control;


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
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
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
    private Gson gson = new Gson();
    private SimpleDateFormat sf;
    private Calendar setNewTime;
    private int id;

    @Override
    public void onReceive(Context context, Intent intent) {
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
            id = 0;
            chargeAPPDB = new ChargeAPPDB(context);
            consumeDB = new ConsumeDB(chargeAPPDB.getReadableDatabase());
            bankDB = new BankDB(chargeAPPDB.getReadableDatabase());
            List<BankVO> bankVOS = bankDB.getFixDate();
            SharedPreferences sharedPreferences = context.getSharedPreferences("Charge_User", Context.MODE_PRIVATE);
            boolean setNotify = sharedPreferences.getBoolean("notify", true);
            String setTime = sharedPreferences.getString("userTime", "6:00 p.m.").trim();
            int hour, min;
            if (setTime.indexOf("p") == -1) {
                hour = new Integer(setTime.substring(0, setTime.indexOf(":")));
                min = new Integer(setTime.substring(setTime.indexOf(":") + 1, setTime.indexOf("a")).trim());
            } else {
                hour = new Integer(setTime.substring(0, setTime.indexOf(":"))) + 12;
                min = new Integer(setTime.substring(setTime.indexOf(":") + 1, setTime.indexOf("p")).trim());
            }
            List<ConsumeVO> consumerVOS = consumeDB.getFixdate();
            JsonObject jsonObject;
            Calendar date = Calendar.getInstance();
            int year = date.get(Calendar.YEAR);
            int month = date.get(Calendar.MONTH);
            int day = date.get(Calendar.DAY_OF_MONTH);
            int dweek = date.get(Calendar.DAY_OF_WEEK);
            setNewTime = new GregorianCalendar(year, month, day, hour, min, 0);
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
                        ConsumeVO cccc = consumeDB.getAutoTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), consumeVO.getId());
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
                            Log.d("XXXXX", sf.format(setNewTime.getTimeInMillis()));
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
                                consumeVO.setAutoId(consumeVO.getId());
                                consumeVO.setDate(new Date((date.getTimeInMillis())));
                                consumeDB.insert(consumeVO);
                            }
                            if (notify && setNotify) {
                                NotifyUse(consumeVO, context, setNewTime.getTimeInMillis());
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
                                consumeVO.setAutoId(consumeVO.getId());
                                consumeVO.setDate(new Date((date.getTimeInMillis())));
                                consumeDB.insert(consumeVO);
                            }

                            if (notify && setNotify) {
                                NotifyUse(consumeVO, context, setNewTime.getTimeInMillis());
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
                                consumeVO.setAutoId(consumeVO.getId());
                                consumeVO.setDate(new Date((date.getTimeInMillis())));
                                consumeDB.insert(consumeVO);
                            }

                            if (notify && setNotify) {
                                NotifyUse(consumeVO, context, setNewTime.getTimeInMillis());
                            }
                        }
                    } else {
                        String fixdate = jsonObject.get("choicedate").getAsString().trim();
                        fixdate = fixdate.substring(0, fixdate.indexOf("月"));
                        int d = Integer.valueOf(fixdate) - 1;
                        if (d == month && day == 1) {

                            Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                            Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                            ConsumeVO cccc = consumeDB.getAutoTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), consumeVO.getId());
                            if (cccc == null) {
                                consumeVO.setNotify("false");
                                consumeVO.setFixDate("false");
                                consumeVO.setAuto(true);
                                consumeVO.setAutoId(consumeVO.getId());
                                consumeVO.setDate(new Date((date.getTimeInMillis())));
                                consumeDB.insert(consumeVO);
                            }

                            if (notify && setNotify) {
                                NotifyUse(consumeVO, context, setNewTime.getTimeInMillis());
                            }
                        }
                    }
                    id = consumeVO.getId();
                }
            }
            for (BankVO b : bankVOS) {
                String detail = b.getFixDateDetail();
                jsonObject = gson.fromJson(detail, JsonObject.class);
                String action = jsonObject.get("choicestatue").getAsString().trim();
                if ("每天".equals(action)) {

                    Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                    Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                    BankVO bb = bankDB.getAutoBank(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), b.getId());
                    if(bb==null)
                    {
                        b.setFixDate("false");
                        b.setAuto(true);
                        b.setAutoId(b.getId());
                        b.setDate(new Date(setNewTime.getTimeInMillis()));
                        bankDB.insert(b);

                        Log.d("XXXXXxbefore", String.valueOf(b.isAuto()));
                        List<BankVO> tt=bankDB.getAutoSetting(b.getId());
                        for(BankVO bv:tt)
                        {
                            Log.d("XXXXXxafter", String.valueOf(bv.isAuto()));
                        }

                    }

                } else if ("每周".equals(action)) {
                    String fixdetail = jsonObject.get("choicedate").getAsString().trim();
                    HashMap<String, Integer> change = getStringtoInt();
                    if (date.get(Calendar.DAY_OF_WEEK) == change.get(fixdetail)) {


                        Calendar start = new GregorianCalendar(year, month, day, 0, 0, 0);
                        Calendar end = new GregorianCalendar(year, month, day, 23, 59, 0);
                        BankVO bb = bankDB.getAutoBank(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), b.getId());
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
                        BankVO bb = bankDB.getAutoBank(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), b.getId());
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
                        BankVO bb = bankDB.getAutoBank(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), b.getId());
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
                        BankVO bb = bankDB.getAutoBank(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), b.getId());
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
        }
        notifyLottery(context);
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
                Intent alarmIntent = new Intent(context, secondReceiver.class);
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
        bundle.putSerializable("id", consumeVO.getId());
        Intent alarmIntent = new Intent(context, secondReceiver.class);
        alarmIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, consumeVO.getId());
        manager.set(AlarmManager.RTC_WAKEUP, settime, pendingIntent);
        Log.d("xxxx", message);
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



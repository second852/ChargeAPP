package com.chargeapp.whc.chargeapp.Control;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;


public class ThirdReceiver extends BroadcastReceiver {

    private SimpleDateFormat sf;
    private int id;
    private String title;
    private ChargeAPPDB chargeAPPDB;
    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private BankDB bankDB;
    private GoalDB goalDB;
    private JsonObject jsonObject;
    private String detail;
    private Gson gson;
    private int year, month, day, dweek;

    @Override
    public void onReceive(Context context, Intent intent) {
        sf = new SimpleDateFormat("yyyy-MM-dd");

        SharedPreferences sharedPreferences = context.getSharedPreferences("Charge_User", Context.MODE_PRIVATE);
        boolean setNotify = sharedPreferences.getBoolean("notify", true);

        chargeAPPDB = new ChargeAPPDB(context);
        consumeDB = new ConsumeDB(chargeAPPDB.getReadableDatabase());
        consumeDB.colExist("rdNumber");
        invoiceDB = new InvoiceDB(chargeAPPDB.getReadableDatabase());
        bankDB = new BankDB(chargeAPPDB.getReadableDatabase());
        goalDB = new GoalDB(chargeAPPDB.getReadableDatabase());

        //Detail
        gson = new Gson();
        Calendar date = Calendar.getInstance();
        year = date.get(Calendar.YEAR);
        month = date.get(Calendar.MONTH);
        day = date.get(Calendar.DAY_OF_MONTH);
        dweek = date.get(Calendar.DAY_OF_WEEK);
        //notify message
        String message;
        Intent activeI;

        if (setNotify) {
            activeI = new Intent(context, Welcome.class);
            activeI.setAction("showFix");

            List<ConsumeVO> consumeVOS = consumeDB.getNotify();
            title = " " + sf.format(new Date(System.currentTimeMillis())) + "今天繳費提醒";

            for (ConsumeVO consumeVO : consumeVOS) {

                detail = consumeVO.getFixDateDetail();
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
                } else if ("每周".equals(action)) {
                    String fixdetail = jsonObject.get("choicedate").getAsString().trim();
                    HashMap<String, Integer> change = getStringtoInt();
                    if (date.get(Calendar.DAY_OF_WEEK) != change.get(fixdetail)) {
                        continue;
                    }
                } else if ("每月".equals(action)) {
                    int Maxday = date.getActualMaximum(Calendar.DAY_OF_MONTH);
                    String fixdate = jsonObject.get("choicedate").getAsString().trim();
                    fixdate = fixdate.substring(0, fixdate.indexOf("日"));
                    boolean needNotify = false;//是否需要通知
                    if (fixdate.equals(String.valueOf(day))) {
                        needNotify = true;
                    }
                    if (Maxday < Integer.valueOf(fixdate) && day == Maxday) {
                        needNotify = true;
                    }

                    if (!needNotify) {
                        continue;
                    }
                } else {
                    //每年
                    String fixdate = jsonObject.get("choicedate").getAsString().trim();
                    fixdate = fixdate.substring(0, fixdate.indexOf("月"));
                    int d = Integer.valueOf(fixdate) - 1;
                    if (!(month == d && day == 1)) {
                        continue;
                    }
                }

                message = " 繳納" + consumeVO.getSecondType() + "費用:" + consumeVO.getMoney() + " 元";
                showNotification(title, message, context, id, activeI);
                id++;
            }

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year=calendar.get(Calendar.YEAR)-1911;
            if (month == 1 || month == 3 || month == 5 || month == 7 || month == 9 || month == 11) {
                if (day == 25) {
                    //統一發票 通知
                    activeI=new Intent(context,Welcome.class);
                    activeI.setAction("nulPriceNotify");
                    title=" 統一發票";
                    if(month==1)
                    {
                        message=" 民國"+(year-1)+"年11-12月開獎";
                    }
                    else if(month==3)
                    {
                        message=" 民國"+year+"年1-2月開獎";
                    } else if(month==5)
                    {
                        message=" 民國"+year+"年3-4月開獎";
                    } else if(month==7)
                    {
                        message=" 民國"+year+"年5-6月開獎";
                    } else if(month==9)
                    {
                        message=" 民國"+year+"年7-8月開獎";
                    } else
                    {
                        message=" 民國"+year+"年9-10月開獎";
                    }
                    showNotification(title,message,context,id,activeI);
                    id++;
                }
            }

            List<GoalVO> goalVOS = goalDB.getNotify();
            for (GoalVO goalVO : goalVOS) {

                String statue = goalVO.getNotifyStatue().trim();
                if (statue.equals("每天")) {
                    if (goalVO.isNoWeekend()) {
                        if (dweek == 1 || dweek == 7) {
                            continue;
                        }
                    }
                    setGoalNotification(goalVO, context);
                    id++;
                } else if (statue.equals("每周")) {
                    HashMap<String, Integer> change = getStringtoInt();
                    String dateStatue = goalVO.getNotifyDate().trim();
                    if (dweek == change.get(dateStatue)) {
                        setGoalNotification(goalVO, context);
                        id++;
                    }
                } else if (statue.equals("每月")) {
                    int max = date.getActualMaximum(Calendar.DAY_OF_MONTH);
                    String dateStatue = goalVO.getNotifyDate().trim();
                    dateStatue = dateStatue.substring(0, dateStatue.indexOf("日"));
                    Log.d("service", dateStatue + " : " + day);
                    if (dateStatue.equals(String.valueOf(day))) {
                        setGoalNotification(goalVO, context);
                        id++;
                    }
                    if (day == max && Integer.valueOf(dateStatue) > day) {
                        setGoalNotification(goalVO, context);
                        id++;
                    }
                } else {
                    String fixdate = goalVO.getNotifyDate().trim();
                    fixdate = fixdate.substring(0, fixdate.indexOf("月"));
                    int d = Integer.valueOf(fixdate) - 1;
                    if (month == d && day == 1) {
                        setGoalNotification(goalVO, context);
                        id++;
                    }
                }
            }
        }
    }


    private void setGoalNotification(GoalVO goalVO, Context context) {

        String timeStatue = goalVO.getTimeStatue().trim();
        int consumeCount = 0;
        String title = "", message = "";
        Calendar now = Calendar.getInstance();
        Calendar start, end;
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        int dweek = now.get(Calendar.DAY_OF_WEEK);


        if (goalVO.getType().trim().equals("支出")) {
            if (timeStatue.equals("每天")) {
                start = new GregorianCalendar(year, month, day, 0, 0, 0);
                consumeCount = consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()), new Timestamp(System.currentTimeMillis())) +
                        invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(System.currentTimeMillis()));
                message = " 花費 : 本日支出" + consumeCount + "元";
            } else if (timeStatue.equals("每周")) {
                start = new GregorianCalendar(year, month, day - dweek + 1, 0, 0, 0);
                end = new GregorianCalendar(year, month, day, 23, 59, 59);
                consumeCount = consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis())) +
                        invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
                message = " 花費 : 本周支出" + consumeCount + "元";
            } else if (timeStatue.equals("每月")) {
                start = new GregorianCalendar(year, month, 1, 0, 0, 0);
                end = new GregorianCalendar(year, month, day, 23, 59, 59);
                consumeCount = consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis())) +
                        invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
                message = " 花費 : 本月支出" + consumeCount + "元";
            } else if (timeStatue.equals("每年")) {
                int max = now.getActualMaximum(Calendar.DAY_OF_MONTH);
                start = new GregorianCalendar(year, month, 1, 0, 0, 0);
                end = new GregorianCalendar(year, month, max, 23, 59, 59);
                consumeCount = consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis())) +
                        invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
                message = " 花費 : 本年支出" + consumeCount + "元";
            }
            title = " 目標 : " + goalVO.getName() + " " + goalVO.getTimeStatue() + "支出" + goalVO.getMoney() + "元";
        } else {
            if (timeStatue.equals("今日")) {

                consumeCount = consumeDB.getTimeTotal(new Timestamp(goalVO.getStartTime().getTime()), new Timestamp(goalVO.getEndTime().getTime())) +
                        invoiceDB.getTotalBytime(new Timestamp(goalVO.getStartTime().getTime()), new Timestamp(goalVO.getEndTime().getTime()));
                int saveMoney = bankDB.getTimeTotal(new Timestamp(goalVO.getStartTime().getTime()), new Timestamp(goalVO.getEndTime().getTime())) - consumeCount;

                if (goalVO.getEndTime().getTime() < System.currentTimeMillis()) {
                    title = " 目標 :" + goalVO.getName() + " " + Common.sTwo.format(goalVO.getEndTime()) + "前儲蓄" + goalVO.getMoney() + "元";
                    if (Integer.valueOf(goalVO.getMoney()) < saveMoney) {
                        goalVO.setStatue(1);
                        goalVO.setNotify(false);
                        message = Common.sTwo.format(goalVO.getEndTime()) + "前已儲蓄" + saveMoney + "元 達成";
                    } else {
                        goalVO.setStatue(2);
                        goalVO.setNotify(false);
                        message = Common.sTwo.format(goalVO.getEndTime()) + "前已儲蓄" + saveMoney + "元 失敗";
                    }
                    goalDB.update(goalVO);
                } else {
                    title = " 目標 :" + goalVO.getName() + " " + Common.sTwo.format(goalVO.getEndTime()) + "前儲蓄" + goalVO.getMoney() + "元";
                    double remainday = ((goalVO.getEndTime().getTime() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24));

                    if(remainday<1)
                    {
                        double remainhour=remainday*24;
                        if(remainhour<1)
                        {
                            double remainMin=remainhour*60;
                            message=" 倒數"+(int)remainMin+"分鐘";

                        }else{
                            message=" 倒數"+(int)remainhour+"小時";
                        }

                    }else {
                        message=" 倒數"+(int)remainday+"天";
                    }


                    if (Integer.valueOf(goalVO.getMoney()) < saveMoney) {
                        goalVO.setStatue(1);
                        message = message+" 目前已儲蓄" + saveMoney + "元 達成";
                        goalDB.update(goalVO);
                    } else {
                        message = message +" 目前已儲蓄" + saveMoney + "元";
                    }
                }
            } else if (timeStatue.equals("每月")) {
                int max = now.getActualMaximum(Calendar.DAY_OF_MONTH);
                start = new GregorianCalendar(year, month, 1, 0, 0, 0);
                end = new GregorianCalendar(year, month, max, 23, 59, 59);
                consumeCount = consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis())) +
                        invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
                int savemoney = bankDB.getTimeTotal(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis())) - consumeCount;
                title = " 目標 :" + goalVO.getName() + " 每月儲蓄" + goalVO.getMoney() + "元";
                message = " 目前 : 本月已存款" + savemoney + "元";
            } else if (timeStatue.equals("每年")) {
                start = new GregorianCalendar(year, 0, 1, 0, 0, 0);
                end = new GregorianCalendar(year, 11, 31, 23, 59, 59);
                consumeCount = consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis())) +
                        invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
                int savemoney = bankDB.getTimeTotal(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis())) - consumeCount;
                title = " 目標 :" + goalVO.getName() + " 每年儲蓄" + goalVO.getMoney() + "元";
                message = " 目前 : 本年已存款" + savemoney + "元";
            }
        }
        if (title.trim().length() > 0) {
            Intent activeI = new Intent(context, Welcome.class);
            activeI.setAction("goal");
            showNotification(title, message, context, this.id, activeI);
        }
    }

    //set Channel
    public final String PRIMARY_CHANNEL = "記帳小助手";
    NotificationManager manager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void channel(Context ctx) {
        NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                PRIMARY_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
        chan1.setLightColor(Color.GREEN);
        chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(chan1);
    }

    private void showNotification(String title, String message, Context context, int NOTIFICATION_ID, Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            if (manager == null) {
                channel(context);
            }
            PendingIntent appIntent = PendingIntent.getActivity(context, 0, intent, 0);
            Notification.Builder nb = new Notification.Builder(context.getApplicationContext(), PRIMARY_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(appIntent);
            manager.notify(NOTIFICATION_ID, nb.build());
        }
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

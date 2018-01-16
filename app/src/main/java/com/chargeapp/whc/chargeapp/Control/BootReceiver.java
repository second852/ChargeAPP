package com.chargeapp.whc.chargeapp.Control;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;


import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumerDB;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;




public class BootReceiver extends BroadcastReceiver {
    private final static String TAG = "BootReceiver";
    private ChargeAPPDB chargeAPPDB;
    private ConsumerDB consumerDB;
    private  NotificationManager notificationManager;
    private int i=0;
    private SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Gson gson=new Gson();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.DATE_CHANGED")||
                intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            notifyLottery(context);
            chargeAPPDB=new ChargeAPPDB(context);
            consumerDB=new ConsumerDB(chargeAPPDB.getReadableDatabase());
            List<ConsumeVO> consumerVOS=consumerDB.getFixdate();
            JsonObject jsonObject;
            long settime=0;
            long setnewtime=0;
            int i=0;
            if(consumerVOS.size()>0&&consumerVOS!=null)
            {
                for(ConsumeVO consumeVO:consumerVOS)
                {

                    String detail=consumeVO.getFixDateDetail();
                    jsonObject=gson.fromJson(detail,JsonObject.class);
                    String action=jsonObject.get("choicestatue").getAsString();
                    boolean notify=Boolean.valueOf(consumeVO.getNotify());
                    Calendar date=Calendar.getInstance();
                    if("每天".equals(action))
                    {
                        boolean nowwek=jsonObject.get("noweek").getAsBoolean();
                        setnewtime = date.getTimeInMillis();
                        settime=date.getTimeInMillis()+1000*60*60*19+i;
                        int today=date.get(Calendar.DAY_OF_WEEK);
                        if(nowwek&&today==7)
                        {
                            return;
                        }
                        if(nowwek&&today==1)
                        {
                            return;
                        }
                        consumeVO.setNotify("false");
                        consumeVO.setFixDate("false");
                        consumeVO.setDate(new Date(setnewtime));
                        consumerDB.insert(consumeVO);
                        if(notify)
                        {
                            NotifyUse(consumeVO,context,settime);
                        }
                    }else if("每周".equals(action)){
                        String fixdetail=jsonObject.get("choicedate").getAsString();
                        HashMap<String,Integer> change=getStringtoInt();
                        if(date.get(Calendar.DAY_OF_WEEK)==change.get(fixdetail))
                        {
                            consumeVO.setNotify("false");
                            consumeVO.setFixDate("false");
                            consumeVO.setDate(new Date((date.getTimeInMillis())));
                            consumerDB.insert(consumeVO);
                            settime=date.getTimeInMillis()+1000*60*60*19+i;
                            if(notify)
                            {
                                NotifyUse(consumeVO,context,settime);
                            }
                        }
                    }else if("每個月".equals(action)){
                        int day=date.get(Calendar.DAY_OF_MONTH);
                        int Maxday=date.getActualMaximum(Calendar.DAY_OF_MONTH);
                        String fixdate=jsonObject.get("choicedate").getAsString().trim();
                        if(fixdate.equals(String.valueOf(day)))
                        {
                            consumeVO.setNotify("false");
                            consumeVO.setFixDate("false");
                            consumeVO.setDate(new Date((date.getTimeInMillis())));
                            consumerDB.insert(consumeVO);
                            settime=date.getTimeInMillis()+1000*60*60*19+i;
                            if(notify)
                            {
                                NotifyUse(consumeVO,context,settime);
                            }
                        }
                        if(Maxday<Integer.valueOf(fixdate)&&day==Maxday)
                        {
                            consumeVO.setNotify("false");
                            consumeVO.setFixDate("false");
                            consumeVO.setDate(new Date((date.getTimeInMillis())));
                            consumerDB.insert(consumeVO);
                            settime=date.getTimeInMillis()+1000*60*60*19+i;
                            if(notify)
                            {
                                NotifyUse(consumeVO,context,settime);
                            }
                        }
                    }else {
                        String fixdate=jsonObject.get("choicedate").getAsString().trim();
                        int month=date.get(Calendar.MONTH)+1;
                        int day=date.get(Calendar.DAY_OF_MONTH);
                        if(Integer.valueOf(fixdate)==month&&day==1)
                        {
                            consumeVO.setNotify("false");
                            consumeVO.setFixDate("false");
                            consumeVO.setDate(new Date((date.getTimeInMillis())));
                            consumerDB.insert(consumeVO);
                            settime=date.getTimeInMillis()+1000*60*60*19+i;
                            if(notify)
                            {
                                NotifyUse(consumeVO,context,settime);
                            }
                        }
                    }
                }
            }

        }
    }

    private void notifyLottery(Context context) {
        Calendar calendar=Calendar.getInstance();
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int month=calendar.get(Calendar.MONTH)+1;
        if(day==25&&month==1||month==3||month==5||month==7||month==9||month==11)
        {
            long settime=calendar.getTimeInMillis()+1000*60*60*20;
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Bundle bundle=new Bundle();
            bundle.putSerializable("action","notifyNul");
            Intent alarmIntent = new Intent(context, secondReceiver.class);
            alarmIntent.putExtras(bundle);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            manager.set(AlarmManager.RTC_WAKEUP,settime,pendingIntent);
        }
    }

    private void NotifyUse(ConsumeVO consumeVO,Context context,long settime)
    {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Bundle bundle=new Bundle();
        bundle.putSerializable("action","notifyC");
        bundle.putSerializable("comsumer",consumeVO);
        Intent alarmIntent = new Intent(context, secondReceiver.class);
        alarmIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        manager.set(AlarmManager.RTC_WAKEUP, settime,pendingIntent);
    }


    private HashMap<String,Integer> getStringtoInt() {
        HashMap<String,Integer> hashMap=new HashMap<>();
        hashMap.put("星期一",2);
        hashMap.put("星期二",3);
        hashMap.put("星期三",4);
        hashMap.put("星期四",5);
        hashMap.put("星期五",6);
        hashMap.put("星期六",7);
        hashMap.put("星期日",1);
        return hashMap;
    }


}



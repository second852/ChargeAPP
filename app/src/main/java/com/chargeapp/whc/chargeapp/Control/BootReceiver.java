package com.chargeapp.whc.chargeapp.Control;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;


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

    private ChargeAPPDB chargeAPPDB;
    private ConsumerDB consumerDB;
    private Gson gson=new Gson();
    private SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
            notifyLottery(context);
            chargeAPPDB=new ChargeAPPDB(context);
            consumerDB=new ConsumerDB(chargeAPPDB.getReadableDatabase());
            List<ConsumeVO> consumerVOS=consumerDB.getFixdate();
            JsonObject jsonObject;
            long settime=0;
            long setnewtime=0;
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
                        int year=date.get(Calendar.YEAR);
                        int month=date.get(Calendar.MONTH);
                        int day=date.get(Calendar.DAY_OF_MONTH);
                        boolean nowwek=jsonObject.get("noweek").getAsBoolean();
                        setnewtime = (new GregorianCalendar(year,month,day,18,0,0)).getTimeInMillis();
                        settime=date.getTimeInMillis()+1000*60*60*19;
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
                            Log.d("XXXXX",sf.format(settime));
                        }
                    }else if("每周".equals(action)){
                        String fixdetail=jsonObject.get("choicedate").getAsString();
                        HashMap<String,Integer> change=getStringtoInt();
                        if(date.get(Calendar.DAY_OF_WEEK)==change.get(fixdetail))
                        {
                            int year=date.get(Calendar.YEAR);
                            int month=date.get(Calendar.MONTH);
                            int day=date.get(Calendar.DAY_OF_MONTH);
                            consumeVO.setNotify("false");
                            consumeVO.setFixDate("false");
                            consumeVO.setDate(new Date((date.getTimeInMillis())));
                            consumerDB.insert(consumeVO);
                            settime=(new GregorianCalendar(year,month,day,18,0,0)).getTimeInMillis();
                            if(notify)
                            {
                                NotifyUse(consumeVO,context,settime);
                            }
                        }
                    }else if("每個月".equals(action)){
                        int day=date.get(Calendar.DAY_OF_MONTH);
                        int Maxday=date.getActualMaximum(Calendar.DAY_OF_MONTH);
                        int year=date.get(Calendar.YEAR);
                        int month=date.get(Calendar.MONTH);
                        String fixdate=jsonObject.get("choicedate").getAsString().trim();
                        if(fixdate.equals(String.valueOf(day)))
                        {

                            consumeVO.setNotify("false");
                            consumeVO.setFixDate("false");
                            consumeVO.setDate(new Date((date.getTimeInMillis())));
                            consumerDB.insert(consumeVO);
                            settime=(new GregorianCalendar(year,month,day,18,0,0)).getTimeInMillis();
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
                            settime=(new GregorianCalendar(year,month,day,18,0,0)).getTimeInMillis();
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
                            month=date.get(Calendar.MONTH);
                            int year=date.get(Calendar.YEAR);
                            consumeVO.setNotify("false");
                            consumeVO.setFixDate("false");
                            consumeVO.setDate(new Date((date.getTimeInMillis())));
                            consumerDB.insert(consumeVO);
                            settime=(new GregorianCalendar(year,month,day,18,0,0)).getTimeInMillis();
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
            int year=calendar.get(Calendar.YEAR);
            month=calendar.get(Calendar.MONTH);
            long settime=(new GregorianCalendar(year,month,day,18,0,0)).getTimeInMillis();;
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
        String  message="繳納"+consumeVO.getSecondType()+"費用:"+consumeVO.getMoney();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Bundle bundle=new Bundle();
        bundle.putSerializable("action","notifyC");
        bundle.putSerializable("comsumer",message);
        bundle.putSerializable("id",consumeVO.getId());
        Intent alarmIntent = new Intent(context, secondReceiver.class);
        alarmIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, consumeVO.getId());
        manager.set(AlarmManager.RTC_WAKEUP, settime,pendingIntent);
        Log.d("xxxx",message);
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



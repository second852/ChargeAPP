package com.chargeapp.whc.chargeapp.Control;



import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.content.Context.NOTIFICATION_SERVICE;


public class ThirdReceiver extends BroadcastReceiver {
    private  NotificationManager notificationManager;
    private SimpleDateFormat sf;

    @Override
    public void onReceive(Context context, Intent intent) {
        sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Bundle bundle=intent.getExtras();
        String action= (String) bundle.get("action");
        String message,title;
        int id;
        if(action.equals("notifyC"))
        {

             message= (String) bundle.getSerializable("comsumer");
             id= (int) bundle.getSerializable("id");
            title="繳費提醒";
            showNotification(title,message,context,id);
        }

        if(action.equals("notifyNul"))
        {
            title="統一發票";
            Calendar calendar=Calendar.getInstance();
            int month=calendar.get(Calendar.MONTH)+1;
            int year=calendar.get(Calendar.YEAR)-1911;
            id= (int) bundle.getSerializable("id");
            if(month==1)
            {
                message="民國"+(year-1)+"年11-12月開獎";
            }
            else if(month==3)
            {
                message="民國"+year+"年1-2月開獎";
            } else if(month==5)
            {
                message="民國"+year+"年3-4月開獎";
            } else if(month==7)
            {
                message="民國"+year+"年5-6月開獎";
            } else if(month==9)
            {
                message="民國"+year+"年7-8月開獎";
            } else
            {
                message="民國"+year+"年9-10月開獎";
            }
            showNotification(title,message,context,id);
        }
        if(action.equals("goalC"))
        {
            setGoalNotification(context,bundle);
        }
    }

    private void setGoalNotification(Context context,Bundle bundle) {
        ChargeAPPDB chargeAPPDB=new ChargeAPPDB(context);
        ConsumeDB consumeDB=new ConsumeDB(chargeAPPDB.getReadableDatabase());
        InvoiceDB invoiceDB=new InvoiceDB(chargeAPPDB.getReadableDatabase());
        BankDB bankDB=new BankDB(chargeAPPDB.getReadableDatabase());
        GoalDB goalDB=new GoalDB(chargeAPPDB.getReadableDatabase());
        int goalId= (int) bundle.getSerializable("goal");
        GoalVO goalVO=goalDB.getFindid(goalId);
        String timeStatue=goalVO.getTimeStatue().trim();
        int consumeCount=0;
        String title="",message="";
        Calendar now=Calendar.getInstance();
        Calendar start,end;
        int year=now.get(Calendar.YEAR);
        int month=now.get(Calendar.MONTH);
        int day=now.get(Calendar.DAY_OF_MONTH);
        int dweek=now.get(Calendar.DAY_OF_WEEK);
        int id= (int) bundle.getSerializable("id");
        if(goalVO.getType().trim().equals("支出"))
        {
            if(timeStatue.equals("每天"))
            {
                start=new GregorianCalendar(year,month,day,0,0,0);
                consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(System.currentTimeMillis()))+
                        invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(System.currentTimeMillis()));
               message="花費 : 本日支出"+consumeCount+"元";
            }else if(timeStatue.equals("每周"))
            {
                start=new GregorianCalendar(year,month,day-dweek+1,0,0,0);
                end=new GregorianCalendar(year,month,day,23,59,59);
                consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                        invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                message="花費 : 本周支出"+consumeCount+"元";
            }else if(timeStatue.equals("每月"))
            {
                start=new GregorianCalendar(year,month,1,0,0,0);
                end=new GregorianCalendar(year,month,day,23,59,59);
                consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                        invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                message="花費 : 本月支出"+consumeCount+"元";
            }else if(timeStatue.equals("每年"))
            {
                int max=now.getActualMaximum(Calendar.DAY_OF_MONTH);
                start=new GregorianCalendar(year,month,1,0,0,0);
                end=new GregorianCalendar(year,month,max,23,59,59);
                consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                        invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                message="花費 : 本年支出"+consumeCount+"元";
            }
            title="目標 : "+goalVO.getName()+" "+goalVO.getTimeStatue()+"支出"+goalVO.getMoney()+"元";
        }else {
            if(timeStatue.equals("今日"))
            {

                consumeCount=consumeDB.getTimeTotal(new Timestamp(goalVO.getStartTime().getTime()),new Timestamp(goalVO.getEndTime().getTime()))+
                        invoiceDB.getTotalBytime(new Timestamp(goalVO.getStartTime().getTime()),new Timestamp(goalVO.getEndTime().getTime()));
                int saveMoney=bankDB.getTimeTotal(new Timestamp(goalVO.getStartTime().getTime()),new Timestamp(goalVO.getEndTime().getTime()))-consumeCount;

                if(goalVO.getEndTime().getTime()<System.currentTimeMillis())
                {
                    title=" 目標 :"+goalVO.getName()+" "+Common.sTwo.format(goalVO.getEndTime())+"前儲蓄"+goalVO.getMoney()+"元";
                    if(Integer.valueOf(goalVO.getMoney())<saveMoney)
                    {
                        goalVO.setStatue(1);
                        goalVO.setNotify(false);
                        message=Common.sTwo.format(goalVO.getEndTime())+"前已儲蓄"+saveMoney+"元 達成";
                    }else{
                        goalVO.setStatue(2);
                        goalVO.setNotify(false);
                        message=Common.sTwo.format(goalVO.getEndTime())+"前已儲蓄"+saveMoney+"元 失敗";
                    }
                    goalDB.update(goalVO);
                }else{
                    title=" 目標 :"+goalVO.getName()+" "+Common.sTwo.format(goalVO.getEndTime())+"前儲蓄"+goalVO.getMoney()+"元";
                    double remainday=((goalVO.getEndTime().getTime()-System.currentTimeMillis())/(1000*60*60*24));
                    if(Integer.valueOf(goalVO.getMoney())<saveMoney)
                    {
                        goalVO.setStatue(1);
                        message="倒數"+(int)remainday+"天 目前已儲蓄"+saveMoney+"元 達成";
                        goalDB.update(goalVO);
                    }else{
                        message="倒數"+(int)remainday+"天 目前已儲蓄"+saveMoney+"元";
                    }
                }
            }else if(timeStatue.equals("每月"))
            {
                int max=now.getActualMaximum(Calendar.DAY_OF_MONTH);
                start=new GregorianCalendar(year,month,1,0,0,0);
                end=new GregorianCalendar(year,month,max,23,59,59);
                consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                        invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                int savemoney=bankDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))-consumeCount;
                title=" 目標 :"+goalVO.getName()+" 每月儲蓄"+goalVO.getMoney()+"元";
                message="目前 : 本月已存款"+savemoney+"元";
            }else if(timeStatue.equals("每年"))
            {
                start=new GregorianCalendar(year,0,1,0,0,0);
                end=new GregorianCalendar(year,11,31,23,59,59);
                consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                        invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                int savemoney=bankDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))-consumeCount;
                title=" 目標 :"+goalVO.getName()+" 每年儲蓄"+goalVO.getMoney()+"元";
                message="目前 : 本年已存款"+savemoney+"元";
            }
            if(title.trim().length()>0)
            {
                showNotification(title,message,context,id);
            }
        }
    }

    private void showNotification(String title,String message,Context context,int NOTIFICATION_ID) {
        NotificationChannel channelLove = null;
        String idLove="記帳小助手";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channelLove = new NotificationChannel(idLove,
                    "記帳小助手", NotificationManager.IMPORTANCE_HIGH);
            channelLove.setDescription("重要提醒");
            channelLove.enableLights(true);
            channelLove.enableVibration(true);
            Notification.Builder builder =  new Notification.Builder(context,idLove)
                            .setSmallIcon(R.mipmap.ele_book)
                            .setContentTitle(title)
                            .setContentText(message);

            Intent intent = new Intent(context, Download.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            builder.setContentIntent(resultPendingIntent);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}

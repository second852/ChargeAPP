package com.chargeapp.whc.chargeapp.Control;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumerDB;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;


public class BootReceiver extends BroadcastReceiver {
    private final static String TAG = "BootReceiver";
    private ChargeAPPDB chargeAPPDB;
    private ConsumerDB consumerDB;
    private  NotificationManager notificationManager;
    private int i=0;
    private SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.DATE_CHANGED")||
                intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            chargeAPPDB=new ChargeAPPDB(context);
            consumerDB=new ConsumerDB(chargeAPPDB.getReadableDatabase());
            List<ConsumeVO> consumerVOS=consumerDB.getNotify();
            Intent alarmIntent = new Intent(context, secondReceiver.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Bundle bundle;
            for(ConsumeVO consumeVO:consumerVOS)
            {
                String detail=consumeVO.getFixDateDetail();
                bundle=new Bundle();
                manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),pendingIntent);
            }
        }
    }
}



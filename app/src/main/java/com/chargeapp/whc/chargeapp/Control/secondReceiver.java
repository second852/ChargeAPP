package com.chargeapp.whc.chargeapp.Control;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumerDB;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;


public class secondReceiver extends BroadcastReceiver {
    private final static String TAG = "BootReceiver";
    private ChargeAPPDB chargeAPPDB;
    private ConsumerDB consumerDB;
    private  NotificationManager notificationManager;
    private int i=0;
    private SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle=intent.getExtras();
        int a= (int) bundle.get("a");
        notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        showNotification(context,a);
    }



    private void showNotification(Context context,int NOTIFICATION_ID) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        Notification notification = new Notification.Builder(context)
                .setTicker("Service Stopped")
                .setContentTitle(sf.format(new Date(System.currentTimeMillis())))
                .setContentText("Service stopped!!")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
        i++;
    }
}

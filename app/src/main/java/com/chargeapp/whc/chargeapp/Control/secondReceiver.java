package com.chargeapp.whc.chargeapp.Control;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;


public class secondReceiver extends BroadcastReceiver {
    private final static String TAG = "BootReceiver";
    private  NotificationManager notificationManager;
    private SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onReceive(Context context, Intent intent) {
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
            showNotification(title,message,context,99999);
        }
    }

    private void showNotification(String title,String message,Context context,int NOTIFICATION_ID) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        Notification notification = new Notification.Builder(context)
                .setTicker(sf.format(new Date(System.currentTimeMillis())))
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
        Log.d("XXXXXXXXxx", String.valueOf(NOTIFICATION_ID));
    }
}

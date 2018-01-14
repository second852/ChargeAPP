package com.chargeapp.whc.chargeapp.Control;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;


public class MainService extends JobService {
    private final static int NOTIFICATION_ID = 0;
    public final static String ACTION_SERVICE_START = "idv.ron.servicedemo.service.start";
    private PowerManager.WakeLock wakeLock;
    NotificationManager notificationManager;



    private void showNotification(String s) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, 0);
        Notification notification = new Notification.Builder(this)
                .setTicker("s")
                .setContentTitle(s)
                .setContentText("Service stopped!!"+s)
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }


    private Handler mJobHandler = new Handler(new Handler.Callback() {
        // 在Handler中，需要实现handleMessage(Message msg)方法来处理任务逻辑。
        @Override
        public boolean handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "JobService task running", Toast.LENGTH_SHORT).show();
            // 调用jobFinished
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            showNotification( sf.format(new Date(System.currentTimeMillis())));
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }
    });



    @Override
    public boolean onStartJob(JobParameters params) {
        mJobHandler.sendMessage(Message.obtain(mJobHandler, 1, params));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Toast.makeText(getApplicationContext(), "JobService task running", Toast.LENGTH_SHORT).show();
        // 调用jobFinished
        return false;
    }
}
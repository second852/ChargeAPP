package com.chargeapp.whc.chargeapp.Control;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.beardedhen.androidbootstrap.BootstrapThumbnail;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Welcome extends AppCompatActivity {

    private BootstrapThumbnail imageWelcome;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        new Thread(runnable).start();
    }

    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            TypefaceProvider.registerDefaultIconSets();
            imageWelcome=findViewById(R.id.imageWelcome);
            setJob();
            mHandler.sendEmptyMessageDelayed(0, 500);
        }
    };



    private void setJob() {
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        //判斷是否建立過
//     tm.cancelAll(); no need,becauseCompiler will remove all job
        if(tm.getAllPendingJobs().size()==2)
        {
            return;
        }

        ComponentName mServiceComponent = new ComponentName(this, JobSchedulerService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, mServiceComponent);
//       tm.cancelAll();
        builder.setPeriodic(1000*30);
        builder.setPersisted(true);
//        builder.setMinimumLatency(1);
//        builder.setOverrideDeadline(2);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        tm.schedule(builder.build());

        ComponentName DowloadComponet = new ComponentName(this, DowloadNewDataJob.class);
        JobInfo.Builder DownloadBuilder = new JobInfo.Builder(1, DowloadComponet);
        DownloadBuilder.setPersisted(true);
        DownloadBuilder.setPeriodic(1000*60*60);
//        DownloadBuilder.setMinimumLatency(1);
//        DownloadBuilder.setOverrideDeadline(2);
        DownloadBuilder.setRequiresCharging(false);
        DownloadBuilder.setRequiresDeviceIdle(false);
        tm.schedule(DownloadBuilder.build());
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {


            //初始化
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            if (dpWidth > 650) {
                Common.screenSize = Common.Screen.xLarge;
            } else if (dpWidth > 470) {
                Common.screenSize = Common.Screen.large;
            } else {
                Common.screenSize = Common.Screen.normal;
            }
            Common.lostCarrier = new ArrayList<>();
            askPermissions();


        }

    };


    public void askPermissions() {
        //因為是群組授權，所以請求ACCESS_COARSE_LOCATION就等同於請求ACCESS_FINE_LOCATION，因為同屬於LOCATION群組
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(Welcome.this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    87);
        } else {
            permissionsOk();
        }
    }

    private void permissionsOk() {
        MainActivity.chargeAPPDB = new ChargeAPPDB(Welcome.this);
        TypeDB typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        List<TypeVO> typeVOS = typeDB.getAll();
        if (typeVOS.size() <= 0) {
            Fragment fragment=new Download();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.welcomeFrame, fragment);
            fragmentTransaction.commit();
            imageWelcome.setVisibility(View.GONE);
        }else {
            Intent intent = new Intent();
            //將原本Activity的換成MainActivity
            intent.setClass(Welcome.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 87: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionsOk();
                } else {

                    PermissionFragment permissionFragment = new PermissionFragment();
                    permissionFragment.setObject(Welcome.this);
                    permissionFragment.show(getSupportFragmentManager(), "show");

                }
                return;
            }
        }
    }
}

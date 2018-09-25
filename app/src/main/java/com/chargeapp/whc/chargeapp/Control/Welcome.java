package com.chargeapp.whc.chargeapp.Control;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.WindowManager;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        mHandler.sendEmptyMessageDelayed(0, 500);
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            //初始化
            adjustFontScale(getResources().getConfiguration());
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

            MainActivity.chargeAPPDB=new ChargeAPPDB(Welcome.this);
            TypeDB typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
            List<TypeVO> typeVOS = typeDB.getAll();
            if (typeVOS.size() <= 0) {
                Intent intent = new Intent();
                //將原本Activity的換成MainActivity
                intent.setClass(Welcome.this, Download.class);
                startActivity(intent);
                finish();
                return;
            }

            //沒有存檔權限到Download
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            Set<String> permissionsRequest = new HashSet<>();
            for (String permission : permissions) {
                int result = ContextCompat.checkSelfPermission(Welcome.this, permission);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionsRequest.add(permission);
                }
            }
            if (!permissionsRequest.isEmpty()) {
                Intent intent = new Intent();
                //將原本Activity的換成MainActivity
                intent.setClass(Welcome.this, Download.class);
                startActivity(intent);
                finish();
                return;
            }

            Intent intent = new Intent();
            //將原本Activity的換成MainActivity
            intent.setClass(Welcome.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    };

    public void adjustFontScale(Configuration configuration) {
        configuration.fontScale = (float) 1;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);

    }
}

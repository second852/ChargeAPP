package com.chargeapp.whc.chargeapp.Control;

import android.Manifest;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;


import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 1709008NB01 on 2018/1/29.
 */

public class Download extends AppCompatActivity {

    public static int[] imageAll = {
            R.drawable.food, R.drawable.phone, R.drawable.clothes, R.drawable.traffic, R.drawable.teach, R.drawable.happy,
            R.drawable.home, R.drawable.medical, R.drawable.invent,
            R.drawable.worker, R.drawable.bus, R.drawable.breakfast, R.drawable.cellphone, R.drawable.clothe, R.drawable.dinner,
            R.drawable.drink, R.drawable.easycard, R.drawable.easygo, R.drawable.electricty, R.drawable.fruit,
            R.drawable.gasstation, R.drawable.highspeedtrain, R.drawable.hospital, R.drawable.image, R.drawable.internet,
            R.drawable.jacket, R.drawable.water, R.drawable.university, R.drawable.trousers, R.drawable.treatment, R.drawable.training,
            R.drawable.ticket, R.drawable.supplement, R.drawable.subway, R.drawable.rent, R.drawable.rentfilled, R.drawable.phonet,
            R.drawable.origin, R.drawable.movie, R.drawable.microphone, R.drawable.lunch, R.drawable.losemoney, R.drawable.lipgloss, R.drawable.train
            , R.drawable.salary, R.drawable.lotto, R.drawable.bouns, R.drawable.interest, R.drawable.fund, R.drawable.bank, R.drawable.health,
            R.drawable.shose, R.drawable.book, R.drawable.setting, R.drawable.search, R.drawable.export,R.drawable.add
    };

    private String food = "堡 三明治 優酪乳 肉 飯 雙手卷 腿 麵 麵包 熱狗 雞 手卷 肉 粉 蔬菜 牛 豬 起司 花生 豆 蛋 魚 菜 瓜 黑胡椒 土司 泡芙 排";
    private String drink = "咖啡 茶 豆漿 拿鐵 乳 飲 ml 罐 酒 杯 水 奶 冰 珍珠";
    private Handler firstH;
    private GetSQLDate getSQLDate;
    private TextView percentage, progressT;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_main);
        progressT = findViewById(R.id.progressT);
        percentage = findViewById(R.id.percentage);
        adView =findViewById(R.id.adView);
        Common.setAdView(adView,this);
        Common.lostCarrier=new ArrayList<>();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        if(dpWidth>650)
        {
            Common.screenSize= Common.Screen.xLarge;
        }else if(dpWidth>470)
        {
            Common.screenSize= Common.Screen.large;
        }else{
            Common.screenSize= Common.Screen.normal;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        askPermissions();
    }

    private void setJob()
    {
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        //判斷是否建立過
        boolean hasBeenScheduled=false;
        for (JobInfo jobInfo : tm.getAllPendingJobs()) {
            if (jobInfo.getId() == 0) {
                hasBeenScheduled = true;
                break;
            }
        }
        if(hasBeenScheduled)
        {
            return;
        }
        ComponentName mServiceComponent = new ComponentName(this, JobSchedulerService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, mServiceComponent);
        builder.setPeriodic(1000*30);
        builder.setPersisted(true);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        tm.schedule(builder.build());
    }

    public  void askPermissions() {
        //因為是群組授權，所以請求ACCESS_COARSE_LOCATION就等同於請求ACCESS_FINE_LOCATION，因為同屬於LOCATION群組
        String[] permissions={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(Download.this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    87);
        }else{
            permisionOk();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 87: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    permisionOk();
                } else {

                    PermissionFragment permissionFragment = new PermissionFragment();
                    permissionFragment.setObject(Download.this);
                    permissionFragment.show(getSupportFragmentManager(), "show");

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }



    private void permisionOk()
    {
        TypefaceProvider.registerDefaultIconSets();
        Common.setChargeDB(Download.this);
        (getSupportActionBar()).hide();
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            getSQLDate = new GetSQLDate(this);
            getSQLDate.setPercentage(percentage);
            getSQLDate.setProgressT(progressT);
            getSQLDate.execute("download");
        } else {
            tonNewActivity();
            Common.showToast(this, "網路沒有開啟，無法下載!");
        }
    }



    private Runnable runToNeW = new Runnable() {
        @Override
        public void run() {
            (new Common()).AutoSetPrice();
            String a = getIntent().getStringExtra("action");
            Intent intent = new Intent();
            if (a != null) {
                intent.putExtra("action", a);
            }
            startActivity(intent.setClass(Download.this, MainActivity.class));
            finish();
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firstH != null) {
            firstH.removeCallbacks(runToNeW);
        }
        if (getSQLDate != null) {
            getSQLDate.cancel(true);
        }
    }

    public void tonNewActivity() {
        //setJob
        setJob();
        //set origin
        setdate();
        firstH = new Handler();
        firstH.postDelayed(runToNeW, 500);
    }


    public void setdate() {
        TypeDB typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        TypeDetailDB typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        List<TypeVO> typeVOS = typeDB.getAll();
        if (typeVOS.size() > 0) {
            return;
        }
        typeDB.insert(new TypeVO("food", "食物", 0));
        typeDB.insert(new TypeVO("phone", "通訊", 1));
        typeDB.insert(new TypeVO("clothes", "衣服", 2));
        typeDB.insert(new TypeVO("traffic", "交通", 3));
        typeDB.insert(new TypeVO("teach", "教育", 4));
        typeDB.insert(new TypeVO("happy", "娛樂", 5));
        typeDB.insert(new TypeVO("home", "住宿", 6));
        typeDB.insert(new TypeVO("medical", "醫療", 7));
        typeDB.insert(new TypeVO("invent", "投資", 8));
        typeDetailDB.insert(new TypeDetailVO("食物", "早餐", indexOfIntArray(imageAll, R.drawable.breakfast), food));
        typeDetailDB.insert(new TypeDetailVO("食物", "午餐", indexOfIntArray(imageAll, R.drawable.lunch), "0"));
        typeDetailDB.insert(new TypeDetailVO("食物", "晚餐", indexOfIntArray(imageAll, R.drawable.dinner), "0"));
        typeDetailDB.insert(new TypeDetailVO("食物", "飲料", indexOfIntArray(imageAll, R.drawable.drink), drink));
        typeDetailDB.insert(new TypeDetailVO("食物", "水果", indexOfIntArray(imageAll, R.drawable.fruit), "蘋果 鳳梨 瓜 蕉 葡萄 蓮霧 番石榴 李 果 檬 橙 莓 椰子 桃 橘 柚 姆 柑 棗 蓮"));
        typeDetailDB.insert(new TypeDetailVO("通訊", "電話費", indexOfIntArray(imageAll, R.drawable.phonet), "中華 市話"));
        typeDetailDB.insert(new TypeDetailVO("通訊", "月租費", indexOfIntArray(imageAll, R.drawable.cellphone), "月租費"));
        typeDetailDB.insert(new TypeDetailVO("通訊", "易付卡", indexOfIntArray(imageAll, R.drawable.easycard), "易付卡 卡"));
        typeDetailDB.insert(new TypeDetailVO("通訊", "網路費", indexOfIntArray(imageAll, R.drawable.internet), "M 上網 寬頻 光纖"));
        typeDetailDB.insert(new TypeDetailVO("交通", "火車", indexOfIntArray(imageAll, R.drawable.train), "0"));
        typeDetailDB.insert(new TypeDetailVO("交通", "高鐵", indexOfIntArray(imageAll, R.drawable.highspeedtrain), "0"));
        typeDetailDB.insert(new TypeDetailVO("交通", "捷運", indexOfIntArray(imageAll, R.drawable.subway), "0"));
        typeDetailDB.insert(new TypeDetailVO("交通", "客運", indexOfIntArray(imageAll, R.drawable.bus), "0"));
        typeDetailDB.insert(new TypeDetailVO("交通", "加油", indexOfIntArray(imageAll, R.drawable.gasstation), "汽油 92 95 98"));
        typeDetailDB.insert(new TypeDetailVO("教育", "補習費", indexOfIntArray(imageAll, R.drawable.training), "0"));
        typeDetailDB.insert(new TypeDetailVO("教育", "學雜費", indexOfIntArray(imageAll, R.drawable.university), "0"));
        typeDetailDB.insert(new TypeDetailVO("娛樂", "電影", indexOfIntArray(imageAll, R.drawable.movie), "0"));
        typeDetailDB.insert(new TypeDetailVO("娛樂", "KTV", indexOfIntArray(imageAll, R.drawable.microphone), "0"));
        typeDetailDB.insert(new TypeDetailVO("娛樂", "門票", indexOfIntArray(imageAll, R.drawable.ticket), "0"));
        typeDetailDB.insert(new TypeDetailVO("住宿", "水費", indexOfIntArray(imageAll, R.drawable.price_button), "0"));
        typeDetailDB.insert(new TypeDetailVO("住宿", "電費", indexOfIntArray(imageAll, R.drawable.electricty), "0"));
        typeDetailDB.insert(new TypeDetailVO("住宿", "瓦斯", indexOfIntArray(imageAll, R.drawable.gasstation), "0"));
        typeDetailDB.insert(new TypeDetailVO("住宿", "房租", indexOfIntArray(imageAll, R.drawable.rent), "0"));
        typeDetailDB.insert(new TypeDetailVO("住宿", "房貸", indexOfIntArray(imageAll, R.drawable.rentfilled), "0"));
        typeDetailDB.insert(new TypeDetailVO("醫療", "健保", indexOfIntArray(imageAll, R.drawable.health), "0"));
        typeDetailDB.insert(new TypeDetailVO("醫療", "勞保", indexOfIntArray(imageAll, R.drawable.worker), "0"));
        typeDetailDB.insert(new TypeDetailVO("醫療", "醫療", indexOfIntArray(imageAll, R.drawable.medical), "0"));
        typeDetailDB.insert(new TypeDetailVO("醫療", "保健食品", indexOfIntArray(imageAll, R.drawable.supplement), "0"));
        typeDetailDB.insert(new TypeDetailVO("投資", "保險", indexOfIntArray(imageAll, R.drawable.treatment), "0"));
        typeDetailDB.insert(new TypeDetailVO("投資", "投資損失", indexOfIntArray(imageAll, R.drawable.losemoney), "0"));
        typeDetailDB.insert(new TypeDetailVO("衣服", "上衣", indexOfIntArray(imageAll, R.drawable.jacket), "衣 外套 套 袖 shirt 棉"));
        typeDetailDB.insert(new TypeDetailVO("衣服", "褲子", indexOfIntArray(imageAll, R.drawable.trousers), "褲"));
        typeDetailDB.insert(new TypeDetailVO("衣服", "內衣褲", indexOfIntArray(imageAll, R.drawable.clothe), "罩 內褲"));
        typeDetailDB.insert(new TypeDetailVO("衣服", "鞋子", indexOfIntArray(imageAll, R.drawable.shose), "鞋"));
        typeDetailDB.insert(new TypeDetailVO("衣服", "化妝品", indexOfIntArray(imageAll, R.drawable.lipgloss), "指甲 臉 面 膜"));
        BankTybeDB bankTybeDB = new BankTybeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankTybeDB.insert(new BankTypeVO("薪水", "薪水", indexOfIntArray(imageAll, R.drawable.salary)));
        bankTybeDB.insert(new BankTypeVO("薪水", "中獎", indexOfIntArray(imageAll, R.drawable.lotto)));
        bankTybeDB.insert(new BankTypeVO("薪水", "獎金", indexOfIntArray(imageAll, R.drawable.bouns)));
        bankTybeDB.insert(new BankTypeVO("薪水", "基金", indexOfIntArray(imageAll, R.drawable.fund)));
        bankTybeDB.insert(new BankTypeVO("薪水", "股票", indexOfIntArray(imageAll, R.drawable.origin)));
        bankTybeDB.insert(new BankTypeVO("薪水", "利息", indexOfIntArray(imageAll, R.drawable.bank)));
        bankTybeDB.insert(new BankTypeVO("薪水", "股利", indexOfIntArray(imageAll, R.drawable.interest)));
    }

    public int indexOfIntArray(int[] array, int key) {
        int returnvalue = 0;
        for (int i = 0; i < array.length; ++i) {
            if (key == array[i]) {
                returnvalue = i;
                break;
            }
        }
        return returnvalue;
    }

}

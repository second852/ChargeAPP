package com.chargeapp.whc.chargeapp.Control;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.BootstrapThumbnail;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.Adapter.PermissionFragment;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.SetupDateBase64;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.Control.Insert.InsertActivity;
import com.chargeapp.whc.chargeapp.Job.DownloadNewDataJob;
import com.chargeapp.whc.chargeapp.Job.JobSchedulerService;
import com.chargeapp.whc.chargeapp.Job.ThirdReceiver;

import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;

import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.ui.ScanFragment;
import com.github.mikephil.charting.utils.Utils;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class Welcome extends AppCompatActivity {

    private BootstrapThumbnail imageWelcome;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utils.init(this);
        setContentView(R.layout.welcome);
        super.onCreate(savedInstanceState);
        Common.setChargeDB(this);
        Common.insertNewTableCol();
        new GetSQLDate(this).execute("checkId");
        new Thread(runnable).start();
        new Thread(downloadCurrency).start();
//        MainActivity.chargeAPPDB.getReadableDatabase().execSQL("DROP TABLE Property;");
//        MainActivity.chargeAPPDB.getReadableDatabase().execSQL("DROP TABLE PropertyFrom;");


//        new GetSQLDate(Welcome.this).execute("getWinInvoice");

//        InvoiceDB invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB);
//        InvoiceVO InvoiceVO=invoiceDB.findOldByNulAmount("QK40811010","178");
//
//        InvoiceVO.setIswin("other");
//        InvoiceVO.setIsWinNul(InvoiceVO.getInvNum());
//        invoiceDB.update(InvoiceVO);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }

    private void testNotify()
    {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, ThirdReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10, pendingIntent);
    }



   private void testConsume()
   {
        Common.setChargeDB(this);
        ConsumeDB consumeDB=new ConsumeDB(MainActivity.chargeAPPDB);
        for(ConsumeVO consumeVO:consumeDB.getAll())
        {
            ConsumeVO c=consumeDB.findOldCon(consumeVO);
            if(c==null)
            {
                Log.d("XXXXX","Id :"+consumeVO.getId());
            }
        }

   }



    private void testInvoiceDownload()
    {

//        MainActivity.chargeAPPDB.getReadableDatabase().execSQL("DROP TABLE Currency;");
          InvoiceDB invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB);
          invoiceDB.deleteBytime(Timestamp.valueOf("2019-02-01 00:00:00"));

//        ElePeriodDB ele=new ElePeriodDB(MainActivity.chargeAPPDB.getReadableDatabase());
//        ele.deleteByCARNUL("/2RDO8+P");
//        new GetSQLDate(this).execute("download");
    }




    //下載現在匯率資料
    private Runnable downloadCurrency=new Runnable() {
        @Override
        public void run() {
            try {


                Common.setChargeDB(Welcome.this);
                Common.insertNewTableCol();
                CurrencyDB currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);

//                if it  has downloaded today,not run;
                Calendar nowTime=Calendar.getInstance();
                Calendar startTime=new GregorianCalendar(nowTime.get(Calendar.YEAR),nowTime.get(Calendar.MONTH),nowTime.get(Calendar.DAY_OF_MONTH),0,0,0);
                Calendar endTime=new GregorianCalendar(nowTime.get(Calendar.YEAR),nowTime.get(Calendar.MONTH),nowTime.get(Calendar.DAY_OF_MONTH),23,59,59);
                if(currencyDB.getAllBytime(startTime.getTimeInMillis(),endTime.getTimeInMillis()).size()>0)
                {
                    return;
                }


                //Download now currency
                Document doc = Jsoup.connect("https://rate.bot.com.tw/xrt?Lang=zh-TW").get();
                Elements newsHeadlines=doc.getElementsByAttributeValue("data-table","本行現金賣出");
                int i=0;
                CurrencyVO currencyVO=null;
                String typeName;
                for(Element e:newsHeadlines)
                {
                    switch(i)
                    {
                        case 0:
                            typeName="USD";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 2:
                            typeName="HKD";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 4:
                            typeName="GBP";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 6:
                            typeName="AUD";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 8:
                            typeName="CAD";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 10:
                            typeName="SGD";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 12:
                            typeName="CHF";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 14:
                            typeName="JPY";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 18:
                            typeName="SEK";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 20:
                            typeName="NZD";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 22:
                            typeName="THB";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 24:
                            typeName="PHP";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 26:
                            typeName="IDR";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 28:
                            typeName="EUR";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 30:
                            typeName="KRW";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 32:
                            typeName="VND";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 34:
                            typeName="MYR";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                        case 36:
                            typeName="CNY";
                            currencyVO=new CurrencyVO();
                            currencyVO.setType(typeName);
                            currencyVO.setName(Common.showCurrency().get(typeName));
                            currencyVO.setSymbol(Common.Currency().get(typeName));
                            currencyVO.setMoney(e.text());
                            currencyVO.setTime(new Date(System.currentTimeMillis()));
                            break;
                    }
                    if(currencyVO!=null)
                    {
                        currencyDB.insert(currencyVO);
                        currencyVO=null;
                    }
                    i++;
                }
                newsHeadlines=doc.getElementsByAttributeValue("data-table","本行即期賣出");


                typeName="ZAR";
                currencyVO=new CurrencyVO();
                currencyVO.setType(typeName);
                currencyVO.setName(Common.showCurrency().get(typeName));
                currencyVO.setSymbol(Common.Currency().get(typeName));
                currencyVO.setMoney(newsHeadlines.get(16).text());
                currencyVO.setTime(new Date(System.currentTimeMillis()));
                if(currencyVO!=null)
                {
                    currencyDB.insert(currencyVO);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            TypefaceProvider.registerDefaultIconSets();
            imageWelcome=findViewById(R.id.imageWelcome);
            setJob();
            mHandler.sendEmptyMessageDelayed(1, 300);
        }
    };


    private void setJob() {
        Common.setChargeDB(Welcome.this);
        Common.insertNewTableCol();
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        //判斷是否建立過
//     tm.cancelAll(); no need,becauseCompiler will remove all job

        tm.cancelAll();
        new GetSQLDate(this).execute("download");
        new GetSQLDate(this).execute("getWinInvoice");
        new SetupDateBase64(this).execute("consumeVO");

        ComponentName mServiceComponent = new ComponentName(this, JobSchedulerService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, mServiceComponent);
//        tm.cancelAll();
        builder.setPeriodic(1000*60*60);
        builder.setPersisted(true);
//        builder.setMinimumLatency(1);
//        builder.setOverrideDeadline(2);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        tm.schedule(builder.build());

        ComponentName DownloadComponent = new ComponentName(this, DownloadNewDataJob.class);
        JobInfo.Builder DownloadBuilder = new JobInfo.Builder(1, DownloadComponent);
        DownloadBuilder.setPersisted(true);
        DownloadBuilder.setPeriodic(1000*60*60);
//        DownloadBuilder.setMinimumLatency(1);
//        DownloadBuilder.setOverrideDeadline(2);
        DownloadBuilder.setRequiresCharging(false);
        DownloadBuilder.setRequiresDeviceIdle(false);
        tm.schedule(DownloadBuilder.build());
    }


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what)
            {
                case 1:
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
                    askPermissions();
                    break;
            }
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
        TypeDB typeDB = new TypeDB(MainActivity.chargeAPPDB);
        List<TypeVO> typeVOS = typeDB.getAll();
        PriceDB priceDB=new PriceDB(MainActivity.chargeAPPDB);
        if (typeVOS.size() <= 0||priceDB.getAll().size()<=0) {
            View welcomeFrame=findViewById(R.id.welcomeFrame);
            welcomeFrame.setVisibility(View.VISIBLE);
            Fragment fragment=new Download();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.welcomeFrame, fragment);
            fragmentTransaction.commitAllowingStateLoss();
            imageWelcome.setVisibility(View.GONE);
        }else {
            Intent intent = new Intent();
            //將原本Activity的換成MainActivity
            MainActivity mainActivity=new MainActivity();
            mainActivity.fragment=new InsertActivity();
            intent.setClass(Welcome.this, MainActivity.class);
            intent.setAction(getIntent().getAction());
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
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

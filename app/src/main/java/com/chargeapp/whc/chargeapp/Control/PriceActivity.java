package com.chargeapp.whc.chargeapp.Control;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuWrapperFactory;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.R;

import java.util.List;

public class PriceActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapterViewPager;
    private Button importMoney,showN,howtogetprice;
    public static  Button goneMoney;
    private HorizontalScrollView choiceitem;
    private LinearLayout text;
    private int nowpoint=0;
    private float movefirst;
    private Button exportMoney;
    private CarrierDB carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private PriceDB priceDB= new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private ProgressDialog progressDialog;
    private List<CarrierVO> carrierVOS;
    public static AsyncTask<Object, Integer, String> getGetSQLDate1;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.price_main);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mAdapterViewPager = new MainPagerAdapter(getSupportFragmentManager());
        exportMoney=findViewById(R.id.exportD);
        importMoney=findViewById(R.id.showD);
        choiceitem=findViewById(R.id.choiceitem);
        goneMoney=findViewById(R.id.goneD);
        showN=findViewById(R.id.showN);
        howtogetprice=findViewById(R.id.howtogetprice);
        text=findViewById(R.id.text);
        progressDialog = new ProgressDialog(this);
        carrierVOS=carrierDB.getAll();
        download();
    }


    public void newInvoice()
    {
        if (carrierVOS != null&&carrierVOS.size()>0) {
            getGetSQLDate1.cancel(true);
            getGetSQLDate1 = new GetSQLDate(this).execute("GetToday");
        }else {
            setloyout();
        }
    }



    private void download() {
        boolean showcircle=false;
        List<PriceVO> priceVOS = priceDB.getAll();
        if (priceVOS == null || priceVOS.size() <= 0) {
            getGetSQLDate1 =new GetSQLDate(this).execute("getAllPriceNul");
            showcircle=true;
        } else {
            if (getGetSQLDate1 == null) {
                getGetSQLDate1 = new GetSQLDate(this).execute("getNeWPrice");
                showcircle=true;
            }
        }
        if(showcircle)
        {
            progressDialog.setMessage("正在更新資料,請稍候...");
            progressDialog.show();
        }else{
            setloyout();
        }
    }


    public void setloyout()
    {
        progressDialog.cancel();
        mViewPager.setAdapter(mAdapterViewPager);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(6);
        setcurrentpage();
        howtogetprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PriceActivity.this, HowGetPrice.class);
                startActivity(intent);
            }
        });
    }

    public void setcurrentpage()
    {
        int page=mViewPager.getCurrentItem();
        exportMoney.setOnClickListener(new ChangePage(page));
        importMoney.setOnClickListener(new ChangePage(page+1));
        showN.setOnClickListener(new ChangePage(page-1));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(getGetSQLDate1!=null)
        {
           getGetSQLDate1.cancel(true);
           getGetSQLDate1=null;
        }
    }

    public static class MainPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 12;

        MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            int currentpoition=position%3;
            if (currentpoition == 0) {
                return new PriceInvoice();
            } else  if(currentpoition == 1)
            {
                return new PriceHand();
            }
            else{
                return new PriceNumber();
            }
        }
    }


    @Override
    public void onPageSelected(int position) {
        int currentpoition=position%3;
        nowpoint=position;
        if(currentpoition==0)
        {
            setcurrentpage();
            goneMoney.setText("兌獎號碼");
            exportMoney.setText("中獎發票");
            importMoney.setText("兌獎");
            showN.setText("兌獎號碼");
        }else if(currentpoition==1)
        {
            setcurrentpage();
            goneMoney.setText("中獎發票");
            exportMoney.setText("兌獎");
            importMoney.setText("兌獎號碼");
            showN.setText("中獎發票");
        } else
        {
            setcurrentpage();
            goneMoney.setText("兌獎");
            exportMoney.setText("兌獎號碼");
            importMoney.setText("中獎發票");
            showN.setText("兌獎");
        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(nowpoint>position)
        {
            text.setX(movefirst+(1-positionOffset)*goneMoney.getWidth()*2);
        }else{
            text.setX(movefirst-(positionOffset*goneMoney.getWidth()*2));
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {
    }
    //畫面呈現完抓取距離
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        goneMoney.setVisibility(View.VISIBLE);
        movefirst=-importMoney.getWidth();
        text.setX(movefirst);
    }
    private class ChangePage implements View.OnClickListener{
        private int page;
        public ChangePage(int page)
        {
            this.page=page;
        }
        @Override
        public void onClick(View view) {
            mViewPager.setCurrentItem(page);
        }
    }

}

package com.chargeapp.whc.chargeapp.Control;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.chargeapp.whc.chargeapp.R;

public class SelectActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapterViewPager;
    private Button exportMoney,importMoney,goneMoney,getMoney;
    private HorizontalScrollView choiceitem;
    private LinearLayout text;
    private int nowpoint=0;
    private float movefirst;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_main);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        exportMoney=findViewById(R.id.exportMoney);
        importMoney=findViewById(R.id.importMoney);
        choiceitem=findViewById(R.id.choiceitem);
        goneMoney=findViewById(R.id.goneMoney);
        getMoney=findViewById(R.id.getMoney);
        mAdapterViewPager = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapterViewPager);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(15);
        setcurrentpage();
        text=findViewById(R.id.text);
    }

    public void setcurrentpage()
    {
        int page=mViewPager.getCurrentItem();
        exportMoney.setOnClickListener(new ChangePage(page));
        importMoney.setOnClickListener(new ChangePage(page+1));
        getMoney.setOnClickListener(new ChangePage(page-1));
    }


    public static class MainPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 30;

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
                return new SelectChartAll();
            } else  {
                return new InsertIncome();
            }
        }
    }


    @Override
    public void onPageSelected(int position) {
        int currentpoition=position%3;
        nowpoint=position;
        setcurrentpage();
        if(currentpoition==0)
        {
            goneMoney.setText("收入");
            exportMoney.setText("總覽");
            importMoney.setText("支出");
            getMoney.setText("收入");
        }else if(currentpoition==1)
        {
            goneMoney.setText("總覽");
            exportMoney.setText("支出");
            importMoney.setText("收入");
            getMoney.setText("總覽");
        }else{
            goneMoney.setText("支出");
            exportMoney.setText("收入");
            importMoney.setText("總覽");
            getMoney.setText("支出");
        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(nowpoint>position)
        {
            text.setX(movefirst+(1-positionOffset)*320);
        }else{
            text.setX(movefirst-(positionOffset*320));
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {
    }
    //畫面呈現完抓取距離
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        movefirst=-exportMoney.getX();
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

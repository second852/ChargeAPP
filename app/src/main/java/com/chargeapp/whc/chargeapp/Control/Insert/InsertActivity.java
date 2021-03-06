package com.chargeapp.whc.chargeapp.Control.Insert;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.R;

public class InsertActivity extends Fragment implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private Button exportMoney,importMoney,goneMoney;
    private LinearLayout text;
    private int nowpoint=0;
    private static int position;
    private Activity activity;
    private View view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            activity= (Activity) context;
        }else {
            activity=getActivity();
        }
        activity.setTitle(R.string.text_Com);
        MainActivity.firstShowInsertActivity=false;
    }

    private  Handler handlerP=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 2:
                    mViewPager.setAdapter(new MainPagerAdapter(getFragmentManager()));
                    mViewPager.addOnPageChangeListener(InsertActivity.this);
                    mViewPager.setCurrentItem(position);
                    setCurrentPage();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.insert_main, container, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mViewPager = view.findViewById(R.id.insert_viewPager);
                exportMoney=view.findViewById(R.id.exportD);
                importMoney=view.findViewById(R.id.showD);
                goneMoney=view.findViewById(R.id.goneD);
                handlerP.sendEmptyMessage(2);
            }
        }).start();
        text=view.findViewById(R.id.text);
        return  view;
    }


    public  void setCurrentPage()
    {
        int page=mViewPager.getCurrentItem();
        if(page==0)
        {
            exportMoney.setOnClickListener(new ChangePage(page));
            importMoney.setOnClickListener(new ChangePage(page+1));
        }else{
            exportMoney.setOnClickListener(new ChangePage(page));
            importMoney.setOnClickListener(new ChangePage(page-1));
        }
    }


    public static class MainPagerAdapter extends FragmentPagerAdapter {
        private  int NUM_ITEMS = 2;

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return InsertSpend.instance();
            } else  {
                return InsertIncome.instance();
            }
        }
    }


    @Override
    public void onPageSelected(int position) {
        nowpoint=position;
        InsertActivity.this.position=position;
        if(position==0)
        {
            setCurrentPage();
            goneMoney.setText("收入");
            exportMoney.setText("支出");
            importMoney.setText("收入");
        }else
        {
            setCurrentPage();
            goneMoney.setText("支出");
            exportMoney.setText("收入");
            importMoney.setText("支出");
        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(nowpoint>position)
        {
            text.setX((1-positionOffset)*320);
        }else{
            text.setX(-(positionOffset*320));
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {
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

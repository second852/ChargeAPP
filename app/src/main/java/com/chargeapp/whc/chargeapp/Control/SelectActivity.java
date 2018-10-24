package com.chargeapp.whc.chargeapp.Control;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.R;

import java.util.Calendar;

public class SelectActivity extends Fragment implements ViewPager.OnPageChangeListener {
    private ViewPager SViewPager;
    private FragmentPagerAdapter mAdapterViewPager;
    private Button exportMoney, importMoney, goneMoney, getMoney;
    private LinearLayout text;
    private int nowpoint = 0;
    private float movefirst;
    public TextView mainTitle;
    public static int position;
    private Activity context;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.context=(Activity) context;
        }else{
            this.context=getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.select_char_main, container, false);
        SViewPager = (ViewPager) view.findViewById(R.id.seleViewPager);
        exportMoney = view.findViewById(R.id.exportD);
        importMoney = view.findViewById(R.id.showD);
        goneMoney = view.findViewById(R.id.goneD);
        getMoney = view.findViewById(R.id.getMoney);
        mAdapterViewPager = new MainPagerAdapter(getFragmentManager());
        SViewPager.setAdapter(mAdapterViewPager);
        SViewPager.addOnPageChangeListener(this);
        SViewPager.setCurrentItem(position);
        setcurrentpage();
        text = view.findViewById(R.id.text);
        movefirst = -importMoney.getWidth();
        context.setTitle(R.string.text_DataPicture);
        return view;
    }

    public void setcurrentpage() {
        int page = SViewPager.getCurrentItem();
        if(page==0)
        {
            exportMoney.setOnClickListener(new ChangePage(page));
            importMoney.setOnClickListener(new ChangePage(page + 1));
            getMoney.setOnClickListener(new ChangePage(page +2));
        }else if(page==1)
        {
            exportMoney.setOnClickListener(new ChangePage(page));
            importMoney.setOnClickListener(new ChangePage(page + 1));
            getMoney.setOnClickListener(new ChangePage(page - 1));
        }else{
            exportMoney.setOnClickListener(new ChangePage(page));
            importMoney.setOnClickListener(new ChangePage(page -2));
            getMoney.setOnClickListener(new ChangePage(page - 1));
        }

    }



    public class MainPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 3;

        MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }


        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new SelectConsume();
            } else if (position == 1) {
                return new SelectIncome();
            } else {
                return new SelectDeposit();
            }
        }
    }


    @Override
    public void onPageSelected(int position) {
        nowpoint = position;
        this.position=position;
        setcurrentpage();
        if (position == 0) {
            goneMoney.setText("存款");
            exportMoney.setText("支出");
            importMoney.setText("收入");
            getMoney.setText("存款");
        } else if (position == 1) {
            goneMoney.setText("支出");
            exportMoney.setText("收入");
            importMoney.setText("存款");
            getMoney.setText("支出");
        } else {
            goneMoney.setText("收入");
            exportMoney.setText("存款");
            importMoney.setText("支出");
            getMoney.setText("收入");
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (nowpoint > position) {
            text.setX(movefirst + (1 - positionOffset) * 320);
        } else {
            text.setX(movefirst - (positionOffset * 320));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private class ChangePage implements View.OnClickListener {
        private int page;

        public ChangePage(int page) {
            this.page = page;
        }

        @Override
        public void onClick(View view) {
            SViewPager.setCurrentItem(page);
        }
    }

}

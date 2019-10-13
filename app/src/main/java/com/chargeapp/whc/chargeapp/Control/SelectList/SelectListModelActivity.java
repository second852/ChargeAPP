package com.chargeapp.whc.chargeapp.Control.SelectList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdView;


public class SelectListModelActivity extends Fragment implements ViewPager.OnPageChangeListener {
    private ViewPager SelAllviewPager;
    private FragmentPagerAdapter mAdapterViewPager;
    private Button exportMoney, importMoney, goneMoney;
    private LinearLayout text;
    private int nowpoint = 0;
    private float movefirst;
    public TextView mainTitle;
    public static int page;
    private Activity context;
    private AdView adview;


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
        final View view = inflater.inflate(R.layout.select_list_main, container, false);
        SelAllviewPager = (ViewPager) view.findViewById(R.id.SelAllviewPager);
        goneMoney = view.findViewById(R.id.goneD);
        exportMoney = view.findViewById(R.id.exportD);
        importMoney = view.findViewById(R.id.showD);
        adview=view.findViewById(R.id.adView);
        Common.setAdView(adview,context);

        mAdapterViewPager = new MainPagerAdapter(getFragmentManager());
        SelAllviewPager.setAdapter(mAdapterViewPager);
        SelAllviewPager.addOnPageChangeListener(this);
        SelAllviewPager.setCurrentItem(page);
        setcurrentpage();
        text = view.findViewById(R.id.text);
        movefirst = -importMoney.getWidth();
        return view;
    }


    public void setcurrentpage() {
        int page = SelAllviewPager.getCurrentItem();
        if(page==0)
        {
            exportMoney.setOnClickListener(new ChangePage(page));
            importMoney.setOnClickListener(new ChangePage(page + 1));
        }else{
            exportMoney.setOnClickListener(new ChangePage(page));
            importMoney.setOnClickListener(new ChangePage(page - 1));
        }

    }




    public  class MainPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 2;

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
                return new SelectListModelCom();
            }  else {
                return new SelectListModelIM();
            }
        }
    }


    @Override
    public void onPageSelected(int position) {
        page=position;
        nowpoint = position;
        setcurrentpage();
        if (position == 0) {
            goneMoney.setText("收入");
            exportMoney.setText("支出");
            importMoney.setText("收入");
        } else {
            goneMoney.setText("支出");
            exportMoney.setText("收入");
            importMoney.setText("支出");
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
            SelAllviewPager.setCurrentItem(page);
        }
    }

}

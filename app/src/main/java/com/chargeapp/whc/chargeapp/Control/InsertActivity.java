package com.chargeapp.whc.chargeapp.Control;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.chargeapp.whc.chargeapp.R;

public class InsertActivity extends Fragment implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private Button exportMoney,importMoney,goneMoney;
    private HorizontalScrollView choiceitem;
    private LinearLayout text;
    private int nowpoint=0;
    private float movefirst;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.insert_main, container, false);
        mViewPager =  view.findViewById(R.id.insert_viewPager);
        exportMoney=view.findViewById(R.id.exportD);
        importMoney=view.findViewById(R.id.showD);
        choiceitem=view.findViewById(R.id.choiceitem);
        goneMoney=view.findViewById(R.id.goneD);
        mViewPager.setAdapter(new MainPagerAdapter(getFragmentManager()));
        mViewPager.addOnPageChangeListener(this);
        setcurrentpage();
        text=view.findViewById(R.id.text);
        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();
        movefirst=-exportMoney.getX();
    }

    public void setcurrentpage()
    {
        int page=mViewPager.getCurrentItem();
        exportMoney.setOnClickListener(new ChangePage(page));
        importMoney.setOnClickListener(new ChangePage(page+1));
    }


    @Override
    public void onStop() {
        super.onStop();
        mViewPager.removeAllViews();
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
            Bundle bundle=new Bundle();
            bundle.putSerializable("needSet",false);
            if (position == 0) {
                Fragment fragment=new InsertSpend();
                fragment.setArguments(bundle);
                return fragment;
            } else  {
                Fragment fragment=new InsertIncome();
                fragment.setArguments(bundle);
                return fragment;
            }
        }
    }


    @Override
    public void onPageSelected(int position) {
        nowpoint=position;
        if(position==0)
        {
            setcurrentpage();
            goneMoney.setText("收入");
            exportMoney.setText("支出");
            importMoney.setText("收入");
        }else
        {
            setcurrentpage();
            goneMoney.setText("支出");
            exportMoney.setText("收入");
            importMoney.setText("支出");
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

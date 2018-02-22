package com.chargeapp.whc.chargeapp.Control;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.R;

import java.util.Calendar;

public class SelectActivity extends Fragment implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapterViewPager;
    private Button exportMoney,importMoney,goneMoney,getMoney;
    private HorizontalScrollView choiceitem;
    private LinearLayout text;
    private int nowpoint=0;
    private float movefirst;
    public TextView mainTitle;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.select_char_main, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        exportMoney=view.findViewById(R.id.exportD);
        importMoney=view.findViewById(R.id.showD);
        choiceitem=view.findViewById(R.id.choiceitem);
        goneMoney=view.findViewById(R.id.goneD);
        getMoney=view.findViewById(R.id.getMoney);
        mAdapterViewPager = new MainPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mAdapterViewPager);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(6);
        setcurrentpage();
        text=view.findViewById(R.id.text);
        movefirst=-importMoney.getWidth();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        View actionbarLayout = LayoutInflater.from(getActivity()).inflate(R.layout.actionbar_layout, null);
        actionBar.setCustomView(actionbarLayout);
        Button list=actionbarLayout.findViewById(R.id.howtogetprice);
        mainTitle=actionbarLayout.findViewById(R.id.mainTitle);
        mainTitle.setText("數據統計");
        list.setText("列表模式");
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new SelectListModelActivity();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                for (Fragment fragment1 : getFragmentManager().getFragments()) {
                    fragmentTransaction.remove(fragment1);
                }
                fragmentTransaction.replace(R.id.body, fragment);
                fragmentTransaction.commit();
            }
        });
    }

    public void setcurrentpage()
    {
        int page=mViewPager.getCurrentItem();
        if(page==8||page==0)
        {
            page=6;
        }
        exportMoney.setOnClickListener(new ChangePage(page));
        importMoney.setOnClickListener(new ChangePage(page+1));
        getMoney.setOnClickListener(new ChangePage(page-1));
    }


    public static class MainPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 9;

        MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            int currentpoition = position % 3;
            if (currentpoition == 0) {
                return new SelectConsume();
            }  else if(currentpoition==1){
                return new SelectIncome();
            }else{
                return new SelectDeposit();
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
            goneMoney.setText("存款");
            exportMoney.setText("支出");
            importMoney.setText("收入");
            getMoney.setText("存款");
        } else if(currentpoition==1)
        {
            goneMoney.setText("支出");
            exportMoney.setText("收入");
            importMoney.setText("存款");
            getMoney.setText("支出");
        }else{
            goneMoney.setText("收入");
            exportMoney.setText("存款");
            importMoney.setText("支出");
            getMoney.setText("收入");
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

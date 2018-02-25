package com.chargeapp.whc.chargeapp.Control;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.chargeapp.whc.chargeapp.R;

public class GoalActivity extends Fragment implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapterViewPager;
    private Button exportMoney,importMoney,goneMoney;
    private HorizontalScrollView choiceitem;
    private LinearLayout text;
    private int nowpoint=0;
    private float movefirst;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.goal_main, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.insert_viewPager);
        exportMoney=view.findViewById(R.id.exportD);
        importMoney=view.findViewById(R.id.showD);
        choiceitem=view.findViewById(R.id.choiceitem);
        goneMoney=view.findViewById(R.id.goneD);
        mAdapterViewPager = new MainPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mAdapterViewPager);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(4);
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
        if(page==5||page==1)
        {
            page=3;
        }
        exportMoney.setOnClickListener(new ChangePage(page));
        importMoney.setOnClickListener(new ChangePage(page+1));
    }


    public static class MainPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 6;

        MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            int currentpoition=position%2;
            if (currentpoition == 0) {
                Fragment fragment=new GoalSet();
                return fragment;
            } else  {
                Fragment fragment=new GoalListAll();
                return fragment;
            }
        }
    }


    @Override
    public void onPageSelected(int position) {
        int currentpoition=position%2;
        nowpoint=position;
        if(currentpoition==0)
        {
            setcurrentpage();
            goneMoney.setText("新增");
            exportMoney.setText("紀錄");
            importMoney.setText("新增");
        }else
        {
            setcurrentpage();
            goneMoney.setText("紀錄");
            exportMoney.setText("新增");
            importMoney.setText("紀錄");
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

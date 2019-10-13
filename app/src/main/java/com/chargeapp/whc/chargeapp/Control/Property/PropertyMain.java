package com.chargeapp.whc.chargeapp.Control.Property;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

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

import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdView;


/**
 * Created by Wang on 2019/1/26.
 */

public class PropertyMain extends Fragment implements ViewPager.OnPageChangeListener{


    private Activity activity;
    private ViewPager propertyViewPager;
    private FragmentPagerAdapter mAdapterViewPager;
    private LinearLayout choiceItem;
    private Button first,second,third;
    private float moveD;
    private int nowPage;
    private AdView adView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.activity= (Activity) context;
        }else{
            this.activity=getActivity();
        }
        activity.setTitle(R.string.text_Property);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.property_main, container, false);
        findViewById(view);
        setPageListener();
        moveD=-first.getWidth();
        mAdapterViewPager = new MainPagerAdapter(getFragmentManager());
        propertyViewPager.setAdapter(mAdapterViewPager);
        propertyViewPager.addOnPageChangeListener(this);
        Common.setAdView(adView,activity);
        return view;
    }




    private void findViewById(View view)
    {
        adView=view.findViewById(R.id.adView);
        choiceItem=view.findViewById(R.id.choiceItem);
        propertyViewPager=view.findViewById(R.id.propertyViewPager);
        first=view.findViewById(R.id.first);
        second=view.findViewById(R.id.second);
        third=view.findViewById(R.id.third);
    }


    public void setPageListener() {
        int page = propertyViewPager.getCurrentItem();
        switch (page) {
            case 0:
                second.setOnClickListener(new ChangePage(page));
                third.setOnClickListener(new ChangePage(page + 1));
                break;
           default:
                second.setOnClickListener(new ChangePage(page));
                third.setOnClickListener(new ChangePage(page - 1));
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (nowPage > position) {
            choiceItem.setX(moveD + (1 - positionOffset) * 320);
        } else {
            choiceItem.setX(moveD - (positionOffset * 320));
        }
    }

    @Override
    public void onPageSelected(int position) {
        nowPage=position;
        switch (nowPage) {
            case 0:
                first.setText(R.string.property_new);
                second.setText(R.string.property_list);
                third.setText(R.string.property_new);
                break;
            case 1:
                first.setText(R.string.property_list);
                second.setText(R.string.property_new);
                third.setText(R.string.property_list);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static class MainPagerAdapter extends FragmentPagerAdapter {
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
            switch (position)
            {
                case 0:
                    return new PropertyList();
                default:
                    return new PropertyInsert();
            }
        }
    }

    private class ChangePage implements View.OnClickListener {
        private int page;

        public ChangePage(int page) {
            this.page = page;
        }

        @Override
        public void onClick(View view) {
            propertyViewPager.setCurrentItem(page);
        }
    }
}

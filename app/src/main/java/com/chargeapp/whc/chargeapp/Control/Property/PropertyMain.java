package com.chargeapp.whc.chargeapp.Control.Property;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chargeapp.whc.chargeapp.Control.Price.PriceHand;
import com.chargeapp.whc.chargeapp.Control.Price.PriceInvoice;
import com.chargeapp.whc.chargeapp.Control.Price.PriceNumber;
import com.chargeapp.whc.chargeapp.R;


/**
 * Created by Wang on 2019/1/26.
 */

public class PropertyMain extends Fragment implements ViewPager.OnPageChangeListener{


    private Activity activity;
    private ViewPager propertyViewPager;
    private FragmentPagerAdapter mAdapterViewPager;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.activity= (Activity) context;
        }else{
            this.activity=getActivity();
        }
        activity.setTitle(R.string.text_DonateMain);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.ele_setdenote_main, container, false);
        propertyViewPager=view.findViewById(R.id.propertyViewPager);
        mAdapterViewPager = new MainPagerAdapter(getFragmentManager());

        return view;
    }




    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

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
            if (position == 0) {
                return new PriceInvoice();
            } else if (position == 1) {
                return new PriceHand();
            } else {
                return new PriceNumber();
            }
        }
    }
}

package com.chargeapp.whc.chargeapp.Control.EleInvoice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;

import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdView;


public class EleDonateMain extends Fragment implements ViewPager.OnPageChangeListener {
    private ViewPager DonateViewPager;
    private FragmentPagerAdapter mAdapterViewPager;
    private Button importMoney,exportMoney;
    public  Button goneMoney;
    private LinearLayout text;
    private int nowpoint=0;
    private float movefirst;
    private Activity context;
    private DrawerLayout drawerLayout;
    private AdView adView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.context= (Activity) context;
        }else{
            this.context=getActivity();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context.setTitle(R.string.text_DonateMain);
        final View view = inflater.inflate(R.layout.ele_setdenote_main, container, false);
        DonateViewPager = (ViewPager) view.findViewById(R.id.DonateViewPager);
        exportMoney=view.findViewById(R.id.exportD);
        importMoney=view.findViewById(R.id.showD);
        goneMoney=view.findViewById(R.id.goneD);
        adView=view.findViewById(R.id.adView);
        Common.setAdView(adView,context);
        mAdapterViewPager = new MainPagerAdapter(getFragmentManager());
        DonateViewPager.setAdapter(mAdapterViewPager);
        DonateViewPager.addOnPageChangeListener(this);
        setcurrentpage();
        text=view.findViewById(R.id.text);
        movefirst=exportMoney.getWidth();
        drawerLayout = this.context.findViewById(R.id.drawer_layout);
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                drawerLayout.closeDrawer(GravityCompat.START);
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        return view;
    }

    public void setcurrentpage()
    {
        int page=DonateViewPager.getCurrentItem();
        if(page==0)
        {
            exportMoney.setOnClickListener(new ChangePage(page));
            importMoney.setOnClickListener(new ChangePage(page+1));
        }else{
            exportMoney.setOnClickListener(new ChangePage(page));
            importMoney.setOnClickListener(new ChangePage(page-1));
        }
    }


    public  class MainPagerAdapter extends FragmentPagerAdapter {
        private  int NUM_ITEMS = 2;

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
                return new EleDonate();
            } else  {
                return new EleDonateRecord();
            }
        }

    }


    @Override
    public void onPageSelected(int position) {
        nowpoint=position;
        if(position==0)
        {
            setcurrentpage();
            goneMoney.setText("紀錄");
            exportMoney.setText("捐獻");
            importMoney.setText("紀錄");
        }else
        {
            setcurrentpage();
            goneMoney.setText("捐獻");
            exportMoney.setText("紀錄");
            importMoney.setText("捐獻");
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
            DonateViewPager.setCurrentItem(page);
        }
    }

}

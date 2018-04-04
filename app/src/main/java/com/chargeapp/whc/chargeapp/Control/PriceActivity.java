package com.chargeapp.whc.chargeapp.Control;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


import com.chargeapp.whc.chargeapp.R;

import java.util.List;

public class PriceActivity extends Fragment implements ViewPager.OnPageChangeListener {
    private ViewPager priceViewPager;
    private FragmentPagerAdapter mAdapterViewPager;
    private Button importMoney, showN, howtogetprice;
    public Button goneMoney;
    private HorizontalScrollView choiceitem;
    private LinearLayout text;
    private int nowpoint = 0;
    private float movefirst;
    private Button exportMoney;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.price_main, container, false);
        priceViewPager = (ViewPager) view.findViewById(R.id.priceViewPager);
        mAdapterViewPager = new MainPagerAdapter(getFragmentManager());
        priceViewPager.setAdapter(mAdapterViewPager);
        priceViewPager.addOnPageChangeListener(this);
        exportMoney = view.findViewById(R.id.exportD);
        importMoney = view.findViewById(R.id.showD);
        choiceitem = view.findViewById(R.id.choiceitem);
        goneMoney = view.findViewById(R.id.goneD);
        showN = view.findViewById(R.id.showN);
        text = view.findViewById(R.id.text);
        setloyout();
        return view;
    }


    public void setloyout() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        View actionbarLayout = LayoutInflater.from(getActivity()).inflate(R.layout.actionbar_layout, null);
        actionBar.setCustomView(actionbarLayout);
        setcurrentpage();
        howtogetprice = actionbarLayout.findViewById(R.id.howtogetprice);
        howtogetprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                actionBar.setDisplayShowCustomEnabled(false);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                List<Fragment> fragments = getFragmentManager().getFragments();
                for (Fragment f : fragments) {
                    fragmentTransaction.remove(f);
                }
                getActivity().setTitle(R.string.text_HowGet);
                Fragment fragment = new HowGetPrice();
                fragmentTransaction.replace(R.id.body, fragment);
                fragmentTransaction.commit();
            }
        });
    }

    public void setcurrentpage() {
        int page = priceViewPager.getCurrentItem();
        if(page==0)
        {
            exportMoney.setOnClickListener(new ChangePage(page));
            importMoney.setOnClickListener(new ChangePage(page + 1));
            showN.setOnClickListener(new ChangePage(page +2));
        }else if(page==1)
        {
            exportMoney.setOnClickListener(new ChangePage(page));
            importMoney.setOnClickListener(new ChangePage(page + 1));
            showN.setOnClickListener(new ChangePage(page - 1));
        }else{
            exportMoney.setOnClickListener(new ChangePage(page));
            importMoney.setOnClickListener(new ChangePage(page - 2));
            showN.setOnClickListener(new ChangePage(page - 1));
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
                return new PriceInvoice();
            } else if (position == 1) {
                return new PriceHand();
            } else {
                return new PriceNumber();
            }
        }
    }



    @Override
    public void onPageSelected(int position) {
        nowpoint = position;
        if (position == 0) {
            setcurrentpage();
            goneMoney.setText("兌獎號碼");
            exportMoney.setText("中獎發票");
            importMoney.setText("兌獎");
            showN.setText("兌獎號碼");
        } else if (position == 1) {
            setcurrentpage();
            goneMoney.setText("中獎發票");
            exportMoney.setText("兌獎");
            importMoney.setText("兌獎號碼");
            showN.setText("中獎發票");
        } else {
            setcurrentpage();
            goneMoney.setText("兌獎");
            exportMoney.setText("兌獎號碼");
            importMoney.setText("中獎發票");
            showN.setText("兌獎");
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (nowpoint > position) {
            text.setX(movefirst + (1 - positionOffset) * goneMoney.getWidth() * 2);
        } else {
            text.setX(movefirst - (positionOffset * goneMoney.getWidth() * 2));
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
            priceViewPager.setCurrentItem(page);
        }
    }

}

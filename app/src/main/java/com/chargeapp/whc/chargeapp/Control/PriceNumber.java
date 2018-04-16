package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class PriceNumber extends Fragment {
    private ImageView PIdateAdd, PIdateCut;
    private PriceDB priceDB;
    private Calendar now;
    private int month, year;
    private TextView PIdateTittle,superN,spcN,firstN,addsixN,showRemain;
    private PriceVO priceVO;
    private RelativeLayout showNul,PIdateL;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.price_number, container, false);
        now = Calendar.getInstance();
        Common.setChargeDB(getActivity());
        priceDB = new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        findViewById(view);
        String period=priceDB.findMaxPeriod();
        if(period==null)
        {
            PIdateL.setVisibility(View.GONE);
            showNul.setVisibility(View.GONE);
            showRemain.setVisibility(View.VISIBLE);
            showRemain.setText("財政部網路忙線中!\n請稍後使用!");
            return view;
        }
        this.month=Integer.valueOf(period.substring(period.length() - 2));
        this.year= Integer.valueOf(period.substring(0, period.length() - 2));
        setMonText("in");
        PIdateAdd.setOnClickListener(new addMonth());
        PIdateCut.setOnClickListener(new cutMonth());
        return view;
    }


    private void setMonText(String action) {
        String showtime,searchtime;
        if (month==2) {
            showtime =year+"年1-2月";
            searchtime =year+"02";
        } else if (month==4) {
            showtime = year+"年3-4月";
            searchtime = year+"04";
        } else if (month==6) {
            showtime = year+"年5-6月";
            searchtime = year+"06";
        } else if (month==8) {
            showtime = year+"年7-8月";
            searchtime = year+"08";
        } else if (month==10) {
            showtime = year+"年9-10月";
            searchtime = year+"10";
        } else {
            showtime = year+"年11-12月";
            searchtime = year+"12";
        }
        Log.d("XXXXXXsearchtime",searchtime);
        priceVO = priceDB.getPeriodAll(searchtime);

        if (priceVO == null && action.equals("add")) {
            month = month - 2;
            if(month==0)
            {
                month=12;
                year=year-1;
            }
            setMonText("add");
            Common.showToast(getActivity(), showtime+"尚未開獎");
            return;
        }
        if (priceVO == null && action.equals("cut")) {
            month = month + 2;
            if(month>12)
            {
                month=2;
                year=year+1;
            }
            Common.showToast(getActivity(), "沒有資料");
            return;
        }
        PIdateTittle.setText(showtime);
        setNul();
    }

    private void setNul() {
        if(priceVO!=null)
        {
            superN.setText(priceVO.getSuperPrizeNo());
            spcN.setText(priceVO.getSpcPrizeNo());
            firstN.setText(priceVO.getFirstPrizeNo1()+"\n"+priceVO.getFirstPrizeNo2()+"\n"+priceVO.getFirstPrizeNo3());
            StringBuffer sb=new StringBuffer();
            if(!priceVO.getSixthPrizeNo1().equals("0"))
            {
                sb.append(priceVO.getSixthPrizeNo1());
            }
            if(!priceVO.getSixthPrizeNo2().equals("0"))
            {
                sb.append(","+priceVO.getSixthPrizeNo2());
            }
            if(!priceVO.getSixthPrizeNo3().equals("0"))
            {
                sb.append(","+priceVO.getSixthPrizeNo3());
            }
            if(!priceVO.getSixthPrizeNo3().equals("0"))
            {
                sb.append(","+priceVO.getSixthPrizeNo1());
            }
            if(!priceVO.getSixthPrizeNo4().equals("0"))
            {
                sb.append(","+priceVO.getSixthPrizeNo4());
            }
            if(!priceVO.getSixthPrizeNo5().equals("0"))
            {
                sb.append(","+priceVO.getSixthPrizeNo5());
            }
            if(!priceVO.getSixthPrizeNo6().equals("0"))
            {
                sb.append(","+priceVO.getSixthPrizeNo6());
            }
            if(sb.toString().trim().length()<=0)
            {
                sb.append("本期無加開號碼");
            }
            addsixN.setText(sb.toString());
        }
    }


    private void findViewById(View view) {
        month = now.get(Calendar.MONTH);
        year = now.get(Calendar.YEAR);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
        PIdateL=view.findViewById(R.id.PIdateL);
        superN=view.findViewById(R.id.superN);
        spcN=view.findViewById(R.id.spcN);
        firstN=view.findViewById(R.id.firstN);
        addsixN=view.findViewById(R.id.addsixN);
        showRemain=view.findViewById(R.id.showRemain);
        showNul=view.findViewById(R.id.showNul);
    }


    private class addMonth implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            month=month+2;
            if(month>12)
            {
                month=2;
                year=year+1;
            }
            setMonText("add");
        }
    }

    private class cutMonth implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            month = month - 2;
            if(month<=0)
            {
                month=12;
                year=year-1;
            }
            setMonText("cut");
        }
    }
}

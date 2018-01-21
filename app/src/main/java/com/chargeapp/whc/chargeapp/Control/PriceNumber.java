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
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class PriceNumber extends Fragment {
    private ImageView PIdateAdd, PIdateCut;
    private PriceDB priceDB = new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private Calendar now = Calendar.getInstance();
    private int month, year;
    private TextView PIdateTittle,superN,spcN,firstN,addsixN;
    private SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
    private PriceVO priceVO;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.price_number, container, false);
        findViewById(view);
        setMonText(now, "in");
        PIdateAdd.setOnClickListener(new addMonth());
        PIdateCut.setOnClickListener(new cutMonth());
        return view;
    }


    private void setMonText(Calendar time, String action) {
        Log.d("XXXX", sd.format(new Date(time.getTimeInMillis())));
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTimeInMillis()));
        int year = cal.get(Calendar.YEAR);
        cal.set(year, 0, 25);
        long one25 = cal.getTimeInMillis();
        cal.set(year, 2, 25);
        long three25 = cal.getTimeInMillis();
        cal.set(year, 4, 25);
        long five25 = cal.getTimeInMillis();
        cal.set(year, 6, 25);
        long seven25 = cal.getTimeInMillis();
        cal.set(year, 8, 25);
        long night25 = cal.getTimeInMillis();
        cal.set(year, 10, 25);
        long ele25 = cal.getTimeInMillis();
        String showtime, searchtime;
        long now = this.now.getTimeInMillis();
        Log.d("XXXX", sd.format(new Date(now)));
        Log.d("XXXX", sd.format(new Date(one25)));
        Log.d("XXXX", sd.format(new Date(three25)));
        Log.d("XXXX", sd.format(new Date(five25)));
        Log.d("XXXX", sd.format(new Date(seven25)));
        Log.d("XXXX", sd.format(new Date(night25)));
        Log.d("XXXX", sd.format(new Date(ele25)));
        if (now > one25 && now < three25) {
            showtime = String.valueOf(time.get(Calendar.YEAR) - 1911 - 1) + "年11-12月";
            searchtime = String.valueOf(time.get(Calendar.YEAR) - 1911 - 1) + "12";
        } else if (now > three25 && now < five25) {
            showtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "年1-2月";
            searchtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "02";
        } else if (now > five25 && now < seven25) {
            showtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "年3-4月";
            searchtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "04";
        } else if (now > seven25 && now < night25) {
            showtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "年5-6月";
            searchtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "06";
        } else if (now > night25 && now < ele25) {
            showtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "年7-8月";
            searchtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "08";
        } else {
            if (this.now.get(Calendar.MONTH) == 0) {
                showtime = String.valueOf(time.get(Calendar.YEAR) - 1911 - 1) + "年9-10月";
                searchtime = String.valueOf(time.get(Calendar.YEAR) - 1911 - 1) + "10";
            } else {
                showtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "年9-10月";
                searchtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "10";
            }
        }
        Log.d("XXXXXXsearchtime",searchtime);

        priceVO = priceDB.getPeriodAll(searchtime);

        if (priceVO == null && action.equals("add")) {
            month = month - 2;
            this.now.set(this.year,month,1);
            setMonText(this.now,"add");
            Common.showToast(getActivity(), showtime+"尚未開獎");
            return;
        }
        if (priceVO == null && action.equals("cut")) {
            month = month + 2;
            this.now.set(this.year,month,1);
            setMonText(this.now,"cut");
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
        superN=view.findViewById(R.id.superN);
        spcN=view.findViewById(R.id.spcN);
        firstN=view.findViewById(R.id.firstN);
        addsixN=view.findViewById(R.id.addsixN);
    }


    private class addMonth implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            month += 2;
            if (month > 11) {
                month = month - 11;
                year++;
            }
            now.set(year, month, 1);
            setMonText(now, "add");
        }
    }

    private class cutMonth implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            month -= 2;
            if (month < 0) {
                month = 11 + month;
                year--;
            }
            now.set(year, month, 1);
            setMonText(now, "cut");
        }
    }
}

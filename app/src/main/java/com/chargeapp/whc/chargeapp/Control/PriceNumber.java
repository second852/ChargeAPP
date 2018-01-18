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
    private TextView PIdateTittle;
    private SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
    private PriceVO priceVO;
    private String message = "";
    private List<PriceVO> priceVOS;
    private HashMap<String,String> levelPrice;
    private String[] level={"first","second","third","fourth","fifth","sixth"};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.price_number, container, false);
        findViewById(view);
        setMonText(now, "in");
        PIdateAdd.setOnClickListener(new addMonth());
        PIdateCut.setOnClickListener(new cutMonth());
        List<String> number = getInputN();
        return view;
    }

    private List<String> getInputN() {
        levelPrice=new HashMap<>();
        List<String> number = new ArrayList<>();
        number.add("7");
        number.add("8");
        number.add("9");
        number.add("4");
        number.add("5");
        number.add("6");
        number.add("1");
        number.add("2");
        number.add("3");
        number.add("C");
        number.add("0");
        number.add("Del");
        levelPrice.put("first","20萬");
        levelPrice.put("second","4萬");
        levelPrice.put("third","1萬");
        levelPrice.put("fourth","4000");
        levelPrice.put("fifth","1000");
        levelPrice.put("sixth","200");
        levelPrice.put("02","01-02月");
        levelPrice.put("04","03-04月");
        levelPrice.put("06","05-06月");
        levelPrice.put("08","07-08月");
        levelPrice.put("10","09-10月");
        levelPrice.put("12","11-12月");
        return number;
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
        String showtime, searchtime, searcholdtime;
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
            searcholdtime = String.valueOf(time.get(Calendar.YEAR) - 1911 - 1) + "10";
        } else if (now > three25 && now < five25) {
            showtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "年1-2月";
            searchtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "02";
            searcholdtime = String.valueOf(time.get(Calendar.YEAR) - 1911 - 1) + "12";
        } else if (now > five25 && now < seven25) {
            showtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "年3-4月";
            searchtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "04";
            searcholdtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "02";
        } else if (now > seven25 && now < night25) {
            showtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "年5-6月";
            searchtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "06";
            searcholdtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "04";
        } else if (now > night25 && now < ele25) {
            showtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "年7-8月";
            searchtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "08";
            searcholdtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "06";
        } else {
            if (this.now.get(Calendar.MONTH) == 0) {
                showtime = String.valueOf(time.get(Calendar.YEAR) - 1911 - 1) + "年9-10月";
                searchtime = String.valueOf(time.get(Calendar.YEAR) - 1911 - 1) + "10";
                searcholdtime = String.valueOf(time.get(Calendar.YEAR) - 1911 - 1) + "08";
            } else {
                showtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "年9-10月";
                searchtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "10";
                searcholdtime = String.valueOf(time.get(Calendar.YEAR) - 1911) + "08";
            }
        }
        Log.d("XXXXXXsearchtime",searchtime);
        Log.d("XXXXXsearcholdtime",searcholdtime);

        priceVO = priceDB.getPeriodAll(searchtime);
        priceVOS = new ArrayList<>();
        priceVOS.add(priceVO);

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
    }


    private void findViewById(View view) {
        month = now.get(Calendar.MONTH);
        year = now.get(Calendar.YEAR);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
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

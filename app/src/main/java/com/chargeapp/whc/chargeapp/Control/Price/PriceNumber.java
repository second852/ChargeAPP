package com.chargeapp.whc.chargeapp.Control.Price;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;


import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.R;


import java.util.Calendar;


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
    private Activity context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.context=(Activity) context;
        }else{
            this.context=getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.price_number, container, false);
        now = Calendar.getInstance();
        Common.setChargeDB(context);
        priceDB = new PriceDB(MainActivity.chargeAPPDB);
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
            Common.showToast(context, showtime+"尚未開獎");
            return;
        }
        if (priceVO == null && action.equals("cut")) {
            month = month + 2;
            if(month>12)
            {
                month=2;
                year=year+1;
            }
            Common.showToast(context, "沒有資料");
            return;
        }
        PIdateTittle.setText(showtime);
        setNul();
    }

    private void setNul() {
        if(priceVO!=null)
        {
            superN.setText(priceVO.getSuperPrizeNo()+"\n");
            spcN.setText(priceVO.getSpcPrizeNo()+"\n");
            String firstNumber=priceVO.getFirstPrizeNo1()+"\n"+priceVO.getFirstPrizeNo2()+"\n"+priceVO.getFirstPrizeNo3();
            SpannableString detailC = new SpannableString(firstNumber);
            detailC.setSpan(new ForegroundColorSpan(Color.RED),5,
                    8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            detailC.setSpan(new ForegroundColorSpan(Color.RED),14,
                    17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            detailC.setSpan(new ForegroundColorSpan(Color.RED),23,
                    26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            firstN.setText(detailC);
            StringBuffer sb=new StringBuffer();
            int r=0;
            if(!priceVO.getSixthPrizeNo1().equals("0"))
            {
                sb.append(" "+priceVO.getSixthPrizeNo1()+" ");
                r=r+1;
            }
            if(!priceVO.getSixthPrizeNo2().equals("0"))
            {
                sb.append(", "+priceVO.getSixthPrizeNo2()+" ");
                r=r+1;
            }
            if(!priceVO.getSixthPrizeNo3().equals("0"))
            {
                sb.append(", "+priceVO.getSixthPrizeNo3()+" ");
                r=r+1;
            }
            if(!priceVO.getSixthPrizeNo4().equals("0"))
            {
                sb.append(", "+priceVO.getSixthPrizeNo4()+" ");
                r=r+1;
            }
            if(!priceVO.getSixthPrizeNo5().equals("0"))
            {
                sb.append(", "+priceVO.getSixthPrizeNo5()+" ");
                r=r+1;
            }
            if(!priceVO.getSixthPrizeNo6().equals("0"))
            {
                sb.append(", "+priceVO.getSixthPrizeNo6()+" ");
                r=r+1;
            }
            if(sb.toString().trim().length()<=0)
            {
                sb.append("本期無加開號碼");
                addsixN.setText(sb.toString());
                return;
            }
            SpannableString detailS = new SpannableString(sb.toString());
            for(int i=0;i<r;i++)
            {
                detailS.setSpan(new ForegroundColorSpan(Color.RED),6*i,
                        6*i+4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            addsixN.setText(detailS);
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

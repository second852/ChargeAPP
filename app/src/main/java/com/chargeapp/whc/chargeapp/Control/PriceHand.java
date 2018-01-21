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

public class PriceHand extends Fragment {
    private ImageView PIdateAdd, PIdateCut;
    private TextView priceTitle, PIdateTittle, inputNul;
    private RecyclerView donateRL;
    private PriceDB priceDB = new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private Calendar now = Calendar.getInstance();
    private int month, year;
    private SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
    private PriceVO priceVO, oldPriceVO;
    private String message = "";
    private List<PriceVO> priceVOS;
    private HashMap<String,String> levelPrice;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.price_hand, container, false);
        findViewById(view);
        setMonText(now, "in");
        PIdateAdd.setOnClickListener(new addMonth());
        PIdateCut.setOnClickListener(new cutMonth());
        donateRL.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        List<String> number = getInputN();
        donateRL.setAdapter(new InputAdapter(getActivity(), number));
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

    private void autoSetInWin(String gnul) {
        String message=null;
        HashMap<Integer,String> allMessage=new HashMap<>();
        int i=0;
        for (PriceVO priceVO : priceVOS) {
            String nul=gnul;
            message=null;
            if (priceVO != null) {
                if (nul.equals(priceVO.getSuperPrizeNo().substring(5))) {
                   message="特別獎?"+priceVO.getSuperPrizeNo()+"\n獎金一千萬";
                }
                if (nul.equals(priceVO.getSpcPrizeNo().substring(5))) {
                    message="特獎?"+priceVO.getSpcPrizeNo()+"\n獎金兩百萬";
                }
                if ( nul.equals(priceVO.getFirstPrizeNo1().substring(5))) {
                    message="頭獎?"+priceVO.getFirstPrizeNo1()+"\n獎金20萬";
                }
                if (nul.equals(priceVO.getFirstPrizeNo2().substring(5))) {
                    message="頭獎?"+priceVO.getFirstPrizeNo2()+"\n獎金20萬";;
                }
                if (nul.equals(priceVO.getFirstPrizeNo3().substring(5))) {
                   message="頭獎?"+priceVO.getFirstPrizeNo3()+"\n獎金20萬";
                }
                if (nul.equals(priceVO.getSixthPrizeNo1())) {
                   message="六獎"+priceVO.getSixthPrizeNo1()+"\n獎金200";
                }
                if (nul.equals(priceVO.getSixthPrizeNo2())) {
                    message="六獎"+priceVO.getSixthPrizeNo2()+"\n獎金200";
                }
                if (nul.equals(priceVO.getSixthPrizeNo3())) {
                    message="六獎"+priceVO.getSixthPrizeNo3()+"\n獎金200";
                }
                if (nul.equals(priceVO.getSixthPrizeNo4())) {
                    message="六獎"+priceVO.getSixthPrizeNo4()+"\n獎金200";
                }
                if (nul.equals(priceVO.getSixthPrizeNo5())) {
                    message="六獎"+priceVO.getSixthPrizeNo5()+"\n獎金200";
                }
                if (nul.equals(priceVO.getSixthPrizeNo6())) {
                    message="六獎"+priceVO.getSixthPrizeNo6()+"\n獎金200";
                }
            }
            allMessage.put(i,message);
            i++;
        }

        String totalmessage=null;
        int redF=0,redE=0,printF=0,printE=0;
        String year,month;
        if(allMessage.get(0)!=null)
        {
            totalmessage=allMessage.get(0);
            redF=totalmessage.lastIndexOf("獎");
            redE=redF-4;
        }
        if(allMessage.get(1)!=null)
        {
            int length=(totalmessage==null?0:totalmessage.length());
            totalmessage=(totalmessage==null?"":totalmessage);
            String old;
            year=oldPriceVO.getInvoYm().substring(0,oldPriceVO.getInvoYm().length()-2);
            month=oldPriceVO.getInvoYm().substring(oldPriceVO.getInvoYm().length()-2);
            old="上一期"+year+"年"+levelPrice.get(month)+allMessage.get(1);
            printE=old.lastIndexOf("獎")+length;
            printF=printE-4;
            totalmessage=totalmessage+old;
        }
        if(totalmessage!=null)
        {
            Spannable content = new SpannableString(totalmessage);
            content.setSpan(new ForegroundColorSpan(Color.RED), redE, redF, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(new ForegroundColorSpan(Color.MAGENTA), printF, printE, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceTitle.setText(content);
            return;
        }else {
            priceTitle.setText("沒有中獎!再接再厲!");
        }

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
        oldPriceVO = priceDB.getPeriodAll(searcholdtime);
        priceVOS = new ArrayList<>();
        priceVOS.add(priceVO);
        priceVOS.add(oldPriceVO);

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
        donateRL = view.findViewById(R.id.donateRL);
        priceTitle = view.findViewById(R.id.priceTitle);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
        donateRL = view.findViewById(R.id.donateRL);
        inputNul = view.findViewById(R.id.inputNul);
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
            inputNul.setText("");

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
            inputNul.setText("");
        }
    }

    private class InputAdapter extends
            RecyclerView.Adapter<PriceHand.InputAdapter.MyViewHolder> {
        private Context context;
        private List<String> numberList;
        InputAdapter(Context context, List<String> memberList) {
            this.context = context;
            this.numberList = memberList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView day;
            CardView cardview;


            MyViewHolder(View itemView) {
                super(itemView);
                day = itemView.findViewById(R.id.DRnul);
                cardview = itemView.findViewById(R.id.cardview);
            }
        }

        @Override
        public int getItemCount() {
            return numberList.size();
        }

        @Override
        public PriceHand.InputAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.ele_setdenote_record_item, viewGroup, false);
            return new PriceHand.InputAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PriceHand.InputAdapter.MyViewHolder viewHolder, int position) {
            final String number = numberList.get(position);
            viewHolder.day.setText(number);
            viewHolder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SpannableString content;
                    if (number.equals("Del")) {

                        if (message.length() > 2) {
                            message = message.substring(0, message.length() - 1);
                            content = new SpannableString(message);
                            content.setSpan(new UnderlineSpan(), message.length() - 1, content.length(), 0);
                            inputNul.setText(content);
                            return;
                        } else if (message.length() > 1) {
                            message = message.substring(0, 1);
                            content = new SpannableString(message);
                            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                            inputNul.setText(content);
                            return;
                        } else {
                            message = "";
                        }
                        inputNul.setText(message);
                        return;
                    }
                    if (number.equals("C")) {
                        message = "";
                        inputNul.setText(message);
                        return;
                    }
                    message = message + number;
                    if (message.length() == 3) {
                        autoSetInWin(message);
                    }
                    if (message.length() > 3) {
                        message = number;
                        priceTitle.setText("請輸入末三碼");
                    }
                    content = new SpannableString(message);
                    content.setSpan(new UnderlineSpan(), message.length() - 1, content.length(), 0);
                    inputNul.setText(content);
                }
            });
        }
    }


}

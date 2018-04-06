package com.chargeapp.whc.chargeapp.Control;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class PriceInvoice extends Fragment {
    private ImageView PIdateAdd, PIdateCut;
    private TextView  DRmessage, PIdateTittle;
    private RelativeLayout PIdateL;
    private InvoiceDB invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private PriceDB priceDB = new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private ConsumeDB consumeDB;
    private HashMap<String,String> levelprice;
    private HashMap<String,Integer> levellength;
    private HashMap<String,String> levelMoney;
    private long start,end;
    private TextView showRemain;
    private int month,year;
    private ListView donateRL;
    private Handler handler;
    private ProgressDialog progressDialog;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.price_invoice, container, false);
        findViewById(view);
        progressDialog=new ProgressDialog(getActivity());
        handler=new Handler();
        levelprice=Common.getPriceName();
        levellength=Common.getlevellength();
        levelMoney=Common.getPrice();
        String period=priceDB.findMaxPeriod();
        if(period==null)
        {
            DRmessage.setVisibility(View.VISIBLE);
            DRmessage.setText("財政部網路忙線中!\n請稍後使用!");
            PIdateL.setVisibility(View.GONE);
            return view;
        }
        PIdateAdd.setOnClickListener(new addMonth());
        PIdateCut.setOnClickListener(new cutMonth());
        this.month=Integer.valueOf(period.substring(period.length() - 2));
        this.year= Integer.valueOf(period.substring(0, period.length() - 2));
        progressDialog.setTitle("自動兌獎中…");
        progressDialog.show();
        handler.post(runnable);
        return view;
    }

   private Runnable runnable=new Runnable() {
       @Override
       public void run() {
           new Common().AutoSetPrice();
           setMonText("in");
       }
   };




    private void setMonText(String action) {
        String showtime,period;
        if (month==2) {
            showtime =year+"年1-2月";
            period=year+"02";
            start=(new GregorianCalendar(year+1911,0,1,0,0,0)).getTimeInMillis();
            end=(new GregorianCalendar(year+1911,2,1,0,0,0)).getTimeInMillis()-1000;
        } else if (month==4) {
            showtime =year+"年3-4月";
            period=year+"04";
            start=(new GregorianCalendar(year+1911,2,1,0,0,0)).getTimeInMillis();
            end=(new GregorianCalendar(year+1911,4,1,0,0,0)).getTimeInMillis()-1000;
        } else if (month==6) {
            showtime = year+"年5-6月";
            period=year+"06";
            start=(new GregorianCalendar(year+1911,4,1,0,0,0)).getTimeInMillis();
            end=(new GregorianCalendar(year+1911,6,1,0,0,0)).getTimeInMillis()-1000;
        } else if (month==8) {
            showtime = year +"年7-8月";
            period=year+"08";
            start=(new GregorianCalendar(year+1911,6,1,0,0,0)).getTimeInMillis();
            end=(new GregorianCalendar(year+1911,8,1,0,0,0)).getTimeInMillis()-1000;
        } else if (month==10) {
            showtime =year + "年9-10月";
            period=year+"10";
            start=(new GregorianCalendar(year+1911,8,1,0,0,0)).getTimeInMillis();
            end=(new GregorianCalendar(year+1911,10,1,0,0,0)).getTimeInMillis()-1000;
        } else {
            showtime =year +"年11-12月";
            period=year+"12";
            start=(new GregorianCalendar(year+1911,10,1,0,0,0)).getTimeInMillis();
            end=(new GregorianCalendar(year+1912,0,1,0,0,0)).getTimeInMillis()-1000;
        }
        PriceVO priceVO = priceDB.getPeriodAll(period);
        if (priceVO == null && action.equals("add")) {
            month=month- 2;
            if(month<=0)
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
            setMonText("cut");
            Common.showToast(getActivity(), "沒有資料");
            return;
        }
        PIdateTittle.setText(showtime);
        setlayout();
    }



    private void setlayout() {
        progressDialog.cancel();
        List<Object> objectList=new ArrayList<>();
        List<InvoiceVO> invoiceVOS=invoiceDB.getWinIn(start,end);
        List<ConsumeVO> consumeVOS= consumeDB.getWinAll(start,end);
        objectList.addAll(invoiceVOS);
        objectList.addAll(consumeVOS);
        if(objectList.size()>0)
        {
            donateRL.setVisibility(View.VISIBLE);
            DRmessage.setVisibility(View.GONE);
            ListAdapter listAdapter= (ListAdapter) donateRL.getAdapter();
            if(listAdapter==null)
            {
                listAdapter=new ListAdapter(getActivity(),objectList);
                donateRL.setAdapter(listAdapter);
            }else{
                listAdapter.setObjects(objectList);
                listAdapter.notifyDataSetChanged();
                donateRL.invalidate();
            }

        }else {
            DRmessage.setVisibility(View.VISIBLE);
            DRmessage.setText("本期發票沒有中獎!");
            donateRL.setVisibility(View.GONE);
        }
    }

    private void findViewById(View view) {
        PIdateL = view.findViewById(R.id.PIdateL);
        DRmessage = view.findViewById(R.id.DRmessage);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
        showRemain=view.findViewById(R.id.showRemain);
        donateRL=view.findViewById(R.id.donateRL);
        showRemain.setText("(無實體電子發票專屬獎中獎清單\n請到財政部網站確認)");
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        AdView adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
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


    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<Object> objects;

        ListAdapter(Context context, List<Object> objects) {
            this.context = context;
            this.objects = objects;
        }


        public void setObjects(List<Object> objects) {
            this.objects = objects;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.ele_setdenote_record_item, parent, false);
            }
            TextView Title=itemView.findViewById(R.id.listTitle);
            TextView describe=itemView.findViewById(R.id.listDetail);
            LinearLayout remindL=itemView.findViewById(R.id.remindL);
            TextView remainT=itemView.findViewById(R.id.remainT);
            LinearLayout fixL=itemView.findViewById(R.id.fixL);
            TextView fixT=itemView.findViewById(R.id.fixT);
            LinearLayout donateL=itemView.findViewById(R.id.donateL);
            String title,day;
            Object o=objects.get(position);
            //區別電子發票
            if(o instanceof InvoiceVO)
            {
                //電子發票
                InvoiceVO invoiceVO= (InvoiceVO) o;
                remainT.setText("電子發票");
                remainT.setTextColor(Color.parseColor("#008844"));
                remindL.setBackgroundColor(Color.parseColor("#008844"));
                remindL.setVisibility(View.VISIBLE);
                if(invoiceVO.getDonateMark().equals("true"))
                {
                    donateL.setVisibility(View.VISIBLE);
                }else{
                    donateL.setVisibility(View.GONE);
                }
                //標題
                day=Common.sDay.format(new Date(invoiceVO.getTime().getTime()))+" ";
                title=day+invoiceVO.getMaintype();
                Title.setText(title);
                //中獎顯示
                fixL.setVisibility(View.VISIBLE);
                fixL.setBackgroundColor(Color.parseColor("#AA0000"));
                fixT.setTextColor(Color.parseColor("#AA0000"));
                fixT.setText(levelprice.get(invoiceVO.getIswin()));

                //detail
                String firstH="發票號碼 : ";
                String firstL="\n中獎號碼 : ";
                String firstAll=firstH+invoiceVO.getInvNum();
                String secondAll=firstL+invoiceVO.getIsWinNul();
                String pIF="\n獎金 : ";
                String detail=firstAll+secondAll+pIF+levelMoney.get(invoiceVO.getIswin());
                int correctLength=-2;
                if(invoiceVO.getIsWinNul().trim().length()<5)
                {
                    correctLength=-7;
                }
                SpannableString detailC = new SpannableString(detail);
                detailC.setSpan(new ForegroundColorSpan(Color.parseColor("#CC0000")),firstH.length()+levellength.get(invoiceVO.getIswin().trim()),
                        firstAll.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                detailC.setSpan(new ForegroundColorSpan(Color.parseColor("#CC0000")),firstAll.length()+firstL.length()+levellength.get(invoiceVO.getIswin().trim())+correctLength,
                        firstAll.length()+secondAll.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                detailC.setSpan(new ForegroundColorSpan(Color.parseColor("#CC0000")),firstAll.length()+secondAll.length()+pIF.length(),
                        detail.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                describe.setText(detailC);
            }else{
                //紙本發票
                ConsumeVO consumeVO= (ConsumeVO) o;
                remainT.setText("紙本發票");
                remainT.setTextColor(Color.parseColor("#0044BB"));
                remindL.setBackgroundColor(Color.parseColor("#0044BB"));
                remindL.setVisibility(View.VISIBLE);
                //標題
                day=Common.sDay.format(consumeVO.getDate())+" ";
                title=day+consumeVO.getMaintype();
                Title.setText(title);
                //中獎顯示
                fixL.setVisibility(View.VISIBLE);
                fixL.setBackgroundColor(Color.parseColor("#AA0000"));
                fixT.setTextColor(Color.parseColor("#AA0000"));
                fixT.setText(levelprice.get(consumeVO.getIsWin()));

                //detail
                String firstH="發票號碼 : ";
                String firstL="\n中獎號碼 : ";
                String firstAll=firstH+consumeVO.getNumber();
                String secondAll=firstL+consumeVO.getIsWinNul();
                String pIF="\n獎金 : ";
                String detail=firstAll+secondAll+pIF+levelMoney.get(consumeVO.getIsWin());
                if(!consumeVO.getIsWinNul().equals("0"))
                {
                    int correctLength=-2;
                    if(consumeVO.getIsWinNul().trim().length()<5)
                    {
                        correctLength=-7;
                    }
                    SpannableString detailC = new SpannableString(detail);
                    detailC.setSpan(new ForegroundColorSpan(Color.parseColor("#CC0000")),firstH.length()+levellength.get(consumeVO.getIsWin().trim()),
                            firstAll.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    detailC.setSpan(new ForegroundColorSpan(Color.parseColor("#CC0000")),firstAll.length()+firstL.length()+levellength.get(consumeVO.getIsWin().trim())+correctLength,
                            firstAll.length()+secondAll.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    detailC.setSpan(new ForegroundColorSpan(Color.parseColor("#CC0000")),firstAll.length()+secondAll.length()+pIF.length(),
                            detail.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    describe.setText(detailC);
                }
            }
            return itemView;
        }
    }
}

package com.chargeapp.whc.chargeapp.Control.Price;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;


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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;

import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.R;


import java.sql.Date;

import java.util.ArrayList;

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
    private InvoiceDB invoiceDB;
    private PriceDB priceDB;
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
    public static  boolean first;
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
        View view = inflater.inflate(R.layout.price_invoice, container, false);
        findViewById(view);
        Common.setChargeDB(context);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB);
        priceDB = new PriceDB(MainActivity.chargeAPPDB);
        progressDialog=new ProgressDialog(context);
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
        if(first)
        {
            progressDialog.setTitle("自動對獎中…");
            progressDialog.show();
            handler.post(runnable);
            first=false;
        }else{
            setMonText("in");
        }
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
            setMonText("cut");
            Common.showToast(context, "沒有資料");
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
                listAdapter=new ListAdapter(context,objectList);
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
        showRemain.setText("(雲端發票專屬獎中獎清單\n請到財政部網站確認)");
        Common.setChargeDB(context);
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB);
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
            BootstrapButton remainT=itemView.findViewById(R.id.remainT);
            LinearLayout fixL=itemView.findViewById(R.id.fixL);
            BootstrapButton fixT=itemView.findViewById(R.id.fixT);
            String title,day;
            Object o=objects.get(position);
            fixT.setOnClickListener(null);
            //區別電子發票
            if(o instanceof InvoiceVO)
            {
                //電子發票
                InvoiceVO invoiceVO= (InvoiceVO) o;
                remainT.setText("雲端發票");
                remainT.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                remindL.setVisibility(View.VISIBLE);
                //標題
                day=Common.sDay.format(new Date(invoiceVO.getTime().getTime()))+" ";
                title=day+invoiceVO.getMaintype();
                Title.setText(title);
                //中獎顯示
                fixL.setVisibility(View.VISIBLE);
                fixT.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                //detail
                String firstH="發票號碼 : ";
                String firstL="\n中獎號碼 : ";
                String firstAll=firstH+invoiceVO.getInvNum();
                String secondAll=firstL+invoiceVO.getIsWinNul();
                String pIF="\n獎金 : ";
                String detail=firstAll+secondAll+pIF+levelMoney.get(invoiceVO.getIswin());
                int correctLength=-2;


                if(invoiceVO.getIswin().equals("other"))
                {
                    fixT.setText("中獎清單");
                    fixT.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("http://invoice.etax.nat.gov.tw/nowNumber.html"));
                            startActivity(intent);
                        }
                    });
                }else{
                    fixT.setText(levelprice.get(invoiceVO.getIswin()));
                }

                try {
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
                }catch (Exception e)
                {
                    Log.d("XXXXX",e.toString());
                        describe.setText("");
                }

            }else{
                //紙本發票
                ConsumeVO consumeVO= (ConsumeVO) o;
                remainT.setText("紙本發票");
                remainT.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                remindL.setVisibility(View.VISIBLE);
                //標題
                day=Common.sDay.format(consumeVO.getDate())+" ";
                title=day+consumeVO.getMaintype();
                Title.setText(title);
                //中獎顯示
                fixL.setVisibility(View.VISIBLE);
                fixT.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
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

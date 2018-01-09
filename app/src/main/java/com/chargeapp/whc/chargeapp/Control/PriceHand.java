package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class PriceHand extends Fragment {
    private ImageView  PIdateAdd, PIdateCut;
    private TextView DRmessage, PIdateTittle;
    private RecyclerView donateRL;
    private InvoiceDB invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private CarrierDB carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private PriceDB priceDB=new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private List<CarrierVO> carrierVOS;
    private List<InvoiceVO> invoiceVOS;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
    public int choiceca = 0;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Calendar now = Calendar.getInstance();
    private int month, year;
    private SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd");
    private String[] level={"first","second","third","fourth","fifth","sixth"};
    private PriceVO priceVO;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.price_hand, container, false);
        findViewById(view);
        setMonText(now,"in");
        PIdateAdd.setOnClickListener(new addMonth());
        PIdateCut.setOnClickListener(new cutMonth());
        return view;
    }

    private void AutoSetPrice()
    {
        List<PriceVO> priceVOS=priceDB.getAll();
        int month;
        int year;
        for(PriceVO priceVO:priceVOS)
        {
            SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd");
            long startTime,endTime;
           String invoYM = priceVO.getInvoYm();
           month=Integer.valueOf(invoYM.substring(invoYM.length()-2));
           year=Integer.valueOf(invoYM.substring(0,invoYM.length()-2))+1911;
           startTime=(new GregorianCalendar(year,month-2,1)).getTimeInMillis();
           endTime=(new GregorianCalendar(year,month,1)).getTimeInMillis();
           autoSetInWin(startTime,endTime,priceVO);
        }
    }



    private void autoSetInWin(long startTime, long endTime, PriceVO priceVO) {
        List<CarrierVO> carrierVOS=carrierDB.getAll();
        for(CarrierVO c:carrierVOS)
        {
            List<InvoiceVO> invoiceVOS=invoiceDB.getNotSetWin(c.getCarNul(),startTime,endTime);
            for(InvoiceVO i:invoiceVOS)
            {
                String nul=i.getInvNum().substring(2);
                if(nul.equals(priceVO.getSuperPrizeNo()))
                {
                    i.setIswin("super");
                    invoiceDB.update(i);
                    return;
                }
                if(nul.equals(priceVO.getSpcPrizeNo())){
                    i.setIswin("spc");
                    invoiceDB.update(i);
                    return;
                }
                if(firsttofourprice(i,priceVO.getFirstPrizeNo1()))
                {
                    return;
                }
                if(firsttofourprice(i,priceVO.getFirstPrizeNo2()))
                {
                    return;
                }
                if(firsttofourprice(i,priceVO.getFirstPrizeNo3()))
                {
                    return;
                }
                nul=nul.substring(5);
                if(nul.equals(priceVO.getSixthPrizeNo1()))
                {
                    i.setIswin("sixth");
                    invoiceDB.update(i);
                    return;
                }
                if(nul.equals(priceVO.getSixthPrizeNo2()))
                {
                    i.setIswin("sixth");
                    invoiceDB.update(i);
                    return;
                }
                if(nul.equals(priceVO.getSixthPrizeNo3()))
                {
                    i.setIswin("sixth");
                    invoiceDB.update(i);
                    return;
                }
                if(nul.equals(priceVO.getSixthPrizeNo4()))
                {
                    i.setIswin("sixth");
                    invoiceDB.update(i);
                    return;
                }
                if(nul.equals(priceVO.getSixthPrizeNo5()))
                {
                    i.setIswin("sixth");
                    invoiceDB.update(i);
                    return;
                }
                if(nul.equals(priceVO.getSixthPrizeNo6()))
                {
                    i.setIswin("sixth");
                    invoiceDB.update(i);
                    return;
                }
                i.setIswin("N");
                invoiceDB.update(i);
            }
        }
    }

    private boolean firsttofourprice(InvoiceVO iv,String pricenul)
    {
        String nul=iv.getInvNum().substring(2);
        for(int i=0;i<6;i++)
        {
            if(nul.substring(i).equals(pricenul.substring(i)))
            {
                iv.setIswin(level[i]);
                invoiceDB.update(iv);
                return true;
            }
//            Log.d("XXXXXnul",nul.substring(i));
//            Log.d("XXXXXpnul",pnul.substring(i));
        }
        return false;
    }


    private void setMonText(Calendar time,String action) {
        Log.d("XXXX",  sd.format(new Date(time.getTimeInMillis())));
        Calendar cal=Calendar.getInstance();
        cal.setTime(new Date(time.getTimeInMillis()));
        int year=cal.get(Calendar.YEAR);
        cal.set(year,0,25);
        long one25=cal.getTimeInMillis();
        cal.set(year,2,25);
        long three25=cal.getTimeInMillis();
        cal.set(year,4,25);
        long five25=cal.getTimeInMillis();
        cal.set(year,6,25);
        long seven25=cal.getTimeInMillis();
        cal.set(year,8,25);
        long night25=cal.getTimeInMillis();
        cal.set(year,10,25);
        long ele25=cal.getTimeInMillis();
        String showtime,searchtime;
        long now=this.now.getTimeInMillis();
        Log.d("XXXX",  sd.format(new Date(now)));
        Log.d("XXXX",  sd.format(new Date(one25)));
        Log.d("XXXX",  sd.format(new Date(three25)));
        Log.d("XXXX",  sd.format(new Date(five25)));
        Log.d("XXXX",  sd.format(new Date(seven25)));
        Log.d("XXXX",  sd.format(new Date(night25)));
        Log.d("XXXX",  sd.format(new Date(ele25)));
        if(now>one25&&now<three25)
        {
            showtime=String.valueOf(time.get(Calendar.YEAR)-1911-1)+"年11-12月";
            searchtime=String.valueOf(time.get(Calendar.YEAR)-1911-1)+"12";
        }
        else if(now>three25&&now<five25)
        {
            showtime=String.valueOf(time.get(Calendar.YEAR)-1911)+"年1-2月";
            searchtime=String.valueOf(time.get(Calendar.YEAR)-1911)+"02";
        }
        else if(now>five25&&now<seven25)
        {
            showtime=String.valueOf(time.get(Calendar.YEAR)-1911)+"年3-4月";
            searchtime=String.valueOf(time.get(Calendar.YEAR)-1911)+"04";
        }
        else if(now>seven25&&now<night25)
        {
            showtime=String.valueOf(time.get(Calendar.YEAR)-1911)+"年5-6月";
            searchtime=String.valueOf(time.get(Calendar.YEAR)-1911)+"06";
        }
        else if(now>night25&&now<ele25)
        {
            showtime=String.valueOf(time.get(Calendar.YEAR)-1911)+"年7-8月";
            searchtime=String.valueOf(time.get(Calendar.YEAR)-1911)+"08";
        }
        else
        {
            if(this.now.get(Calendar.MONTH)==0)
            {
                showtime=String.valueOf(time.get(Calendar.YEAR)-1911-1)+"年9-10月";
                searchtime=String.valueOf(time.get(Calendar.YEAR)-1911-1)+"10";
            }else {
                showtime=String.valueOf(time.get(Calendar.YEAR)-1911)+"年9-10月";
                searchtime=String.valueOf(time.get(Calendar.YEAR)-1911)+"10";
            }
        }
        priceVO=priceDB.getPeriodAll(searchtime);
        Log.d("XXXXXXX",searchtime);
        if(priceVO==null&&action.equals("add"))
        {
            month=month-2;
            Common.showLongToast(getActivity(),"尚未開獎");
            return;
        }
        if(priceVO==null&&action.equals("cut"))
        {
            month=month+2;
            Common.showLongToast(getActivity(),"沒有資料");
            return;
        }

        PIdateTittle.setText(showtime);
    }


    private void findViewById(View view) {
        month = now.get(Calendar.MONTH);
        year = now.get(Calendar.YEAR);
        donateRL = view.findViewById(R.id.donateRL);
        DRmessage = view.findViewById(R.id.DRmessage);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
    }


    private class addMonth implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            month+=2;
            Calendar calendar=Calendar.getInstance();
            if (month >11) {
                month = month-11;
                year++;
            }
            now.set(year, month, 1);
            setMonText(now,"add");
        }
    }

    private class cutMonth implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            month-=2;
            if (month < 0) {
                month = 11+month;
                year--;
            }
            now.set(year, month, 1);
            setMonText(now,"cut");
        }
    }

}

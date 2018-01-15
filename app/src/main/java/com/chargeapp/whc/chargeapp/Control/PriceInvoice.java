package com.chargeapp.whc.chargeapp.Control;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class PriceInvoice extends Fragment {
    private ImageView DRadd, DRcut, PIdateAdd, PIdateCut;
    private TextView DRcarrier, DRmessage, PIdateTittle;
    private RecyclerView donateRL;
    private InvoiceDB invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private CarrierDB carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private PriceDB priceDB=new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private List<InvoiceVO> invoiceVOS;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
    public int choiceca = 0;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Calendar now = Calendar.getInstance();
    private int month, year;
    private SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd");
    private String[] level={"first","second","third","fourth","fifth","sixth"};
    private static AsyncTask<Object, Integer, String> getGetSQLDate1;
    private static AsyncTask<Object, Integer, String> getGetSQLDate2;
    private ProgressDialog progressDialog;
    private List<CarrierVO> carrierVOS;

    /***
     *  自動對自己發票還沒寫好
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.price_invoice, container, false);
        findViewById(view);
        download();
        setMonText(now);
        DRadd.setOnClickListener(new addOnClick());
        DRcut.setOnClickListener(new cutOnClick());
        PIdateAdd.setOnClickListener(new addMonth());
        PIdateCut.setOnClickListener(new cutMonth());
        swipeRefreshLayout =
                (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                setlayout();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    private void download() {
        List<PriceVO> priceVOS=priceDB.getAll();
        carrierVOS=carrierDB.getAll();
        if(priceVOS==null||priceVOS.size()<=0)
        {
            new GetSQLDate(this).execute("getAllPriceNul");
            return;
        }else
        {
            if(getGetSQLDate1==null)
            {
                getGetSQLDate1=new GetSQLDate(this).execute("getNeWPrice");
            }
        }
        progressDialog.setMessage("正在更新資料,請稍候...");
        progressDialog.show();
        if(getGetSQLDate2==null&&carrierVOS!=null)
        {
            getGetSQLDate2=new GetSQLDate(this).execute("GetToday");
        }
    }



    public void AutoSetPrice()
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
        }
        return false;
    }

    private void setMonText(Calendar time) {
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
        String showtime;
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
        }
        else if(now>three25&&now<five25)
        {
            showtime=String.valueOf(time.get(Calendar.YEAR)-1911)+"年1-2月";
        }
        else if(now>five25&&now<seven25)
        {
            showtime=String.valueOf(time.get(Calendar.YEAR)-1911)+"年3-4月";
        }
        else if(now>seven25&&now<night25)
        {
            showtime=String.valueOf(time.get(Calendar.YEAR)-1911)+"年5-6月";
        }
        else if(now>night25&&now<ele25)
        {
            showtime=String.valueOf(time.get(Calendar.YEAR)-1911)+"年7-8月";
        }
        else
        {
            if(this.now.get(Calendar.MONTH)==0)
            {
                showtime=String.valueOf(time.get(Calendar.YEAR)-1911-1)+"年9-10月";
            }else {
                showtime=String.valueOf(time.get(Calendar.YEAR)-1911)+"年9-10月";
            }
        }

        PIdateTittle.setText(showtime);
    }

    private class cutOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            choiceca--;
            if (choiceca < 0) {
                choiceca = carrierVOS.size() - 1;
            }
            setlayout();
        }
    }

    private class addOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choiceca++;
            if (choiceca > (carrierVOS.size() - 1)) {
                choiceca = 0;
            }
            setlayout();
        }
    }


    private void setlayout() {
        DRcarrier.setText(carrierVOS.get(choiceca).getCarNul());
        invoiceVOS = invoiceDB.getisDonated(carrierVOS.get(choiceca).getCarNul());
        if (invoiceVOS == null || invoiceVOS.size() <= 0) {
            DRmessage.setText("沒有捐贈發票~");
            DRmessage.setVisibility(View.VISIBLE);
            return;
        }
        donateRL.setLayoutManager(new LinearLayoutManager(getActivity()));
        donateRL.setAdapter(new PriceInvoice.InvoiceAdapter(getActivity(), invoiceVOS));
    }

    private void findViewById(View view) {
        month = now.get(Calendar.MONTH);
        year = now.get(Calendar.YEAR);
        DRadd = view.findViewById(R.id.DRadd);
        DRcut = view.findViewById(R.id.DRcut);
        DRcarrier = view.findViewById(R.id.DRcarrier);
        donateRL = view.findViewById(R.id.donateRL);
        DRmessage = view.findViewById(R.id.DRmessage);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
        progressDialog=new ProgressDialog(getActivity());
    }

    private class InvoiceAdapter extends
            RecyclerView.Adapter<PriceInvoice.InvoiceAdapter.MyViewHolder> {
        private Context context;
        private List<InvoiceVO> invoiceVOList;


        InvoiceAdapter(Context context, List<InvoiceVO> memberList) {
            this.context = context;
            this.invoiceVOList = memberList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView day, nul, checkdonate;


            MyViewHolder(View itemView) {
                super(itemView);
                checkdonate = itemView.findViewById(R.id.DRdate);
                day = itemView.findViewById(R.id.DRnul);
                nul = itemView.findViewById(R.id.DRamout);
            }
        }

        @Override
        public int getItemCount() {
            return invoiceVOList.size();
        }

        @Override
        public PriceInvoice.InvoiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.ele_setdenote_record_item, viewGroup, false);
            return new PriceInvoice.InvoiceAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PriceInvoice.InvoiceAdapter.MyViewHolder viewHolder, int position) {
            final InvoiceVO invoiceVO = invoiceVOList.get(position);
            viewHolder.checkdonate.setText(sf.format(new Date(invoiceVO.getTime().getTime())));
            viewHolder.day.setText(invoiceVO.getInvNum());
            viewHolder.nul.setText(invoiceVO.getHeartyteam());
        }
    }

    private class addMonth implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            month+=2;
            Calendar calendar=Calendar.getInstance();
            if(month>calendar.get(Calendar.MONTH)&&year==calendar.get(Calendar.YEAR))
            {
                month=month-2;
                Common.showLongToast(getActivity(),"尚未開獎");
                return;
            }
            if (month >11) {
                month = month-11;
                year++;
            }
            now.set(year, month, 1);
            setMonText(now);
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
            setMonText(now);
        }
    }

}
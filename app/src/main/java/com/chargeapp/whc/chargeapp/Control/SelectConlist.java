package com.chargeapp.whc.chargeapp.Control;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class SelectConlist extends Fragment {
    private ImageView DRadd,DRcut;
    private TextView DRcarrier,DRmessage;
    private RecyclerView donateRL;
    private InvoiceDB invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private CarrierDB carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private ConsumeDB consumeDB=new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private List<CarrierVO> carrierVOS;
    private List<InvoiceVO> invoiceVOS;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
    public  int choiceca = 0;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int month,year,day;
    Calendar start,end;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_con_list, container, false);
        findViewById(view);
        DRadd.setOnClickListener(new addOnClick());
        DRcut.setOnClickListener(new cutOnClick());
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
        end=Calendar.getInstance();
        year=end.get(Calendar.YEAR);
        month=end.get(Calendar.MONTH);
        day=end.get(Calendar.DAY_OF_MONTH);
        start =new GregorianCalendar(year,month,1,0,0,0);
        setlayout();
        return view;
    }

    private class cutOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            month=month-1;
            if (month < 0) {
                month = 11;
                year=year-1;
            }
            start=new GregorianCalendar(year,month,1,0,0,0);
            end=new GregorianCalendar(year,month,start.getActualMaximum(Calendar.DAY_OF_MONTH),23,59,59);
            setlayout();
        }
    }

    private class addOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            month=month+1;
            if (month > 11) {
                month = 0;
                year=year+1;
            }
            start=new GregorianCalendar(year,month,1,0,0,0);
            end=new GregorianCalendar(year,month,start.getActualMaximum(Calendar.DAY_OF_MONTH),23,59,59);
            setlayout();
        }
    }


    private void setlayout() {
        carrierVOS=carrierDB.getAll();
        if(carrierVOS==null||carrierVOS.size()<=0)
        {
            DRmessage.setText("請新增載具!");
            DRmessage.setVisibility(View.VISIBLE);
            return ;
        }
        String text=String.valueOf(year-1911)+"年"+String.valueOf(month+1)+"月";
        DRcarrier.setText(text);
        invoiceVOS=invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()),carrierVOS.get(choiceca).getCarNul());
        if(invoiceVOS==null||invoiceVOS.size()<=0)
        {

            DRmessage.setText("沒有資料~");
            DRmessage.setVisibility(View.VISIBLE);
            donateRL.setVisibility(View.GONE);
            return ;
        }
        List<ConsumeVO> consumeVOS=consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
        List<Object> objectList=new ArrayList<>();
        objectList.addAll(consumeVOS);
        objectList.addAll(invoiceVOS);
        Collections.sort(objectList, new Comparator<Object>() {
            @Override
            public int compare(Object o, Object t1) {
                long time1=(o instanceof ConsumeVO)?((ConsumeVO)o).getDate().getTime():((InvoiceVO)o).getTime().getTime();
                long time2=(t1 instanceof ConsumeVO)?((ConsumeVO)t1).getDate().getTime():((InvoiceVO)t1).getTime().getTime();
                if((time1-time2)>0)
                {
                    return 1 ;
                }else if((time1-time2)==0)
                {
                    return 0 ;
                }else{
                    return -1 ;
                }
            }
        });
        DRmessage.setVisibility(View.GONE);
        donateRL.setVisibility(View.VISIBLE);
        donateRL.setLayoutManager(new LinearLayoutManager(getActivity()));
        donateRL.setAdapter(new SelectConlist.InvoiceAdapter(getActivity(), objectList));
    }

    private void findViewById(View view) {
        DRadd=view.findViewById(R.id.DRadd);
        DRcut=view.findViewById(R.id.DRcut);
        DRcarrier=view.findViewById(R.id.DRcarrier);
        donateRL=view.findViewById(R.id.donateRL);
        DRmessage=view.findViewById(R.id.DRmessage);

    }

    private class InvoiceAdapter extends
            RecyclerView.Adapter<SelectConlist.InvoiceAdapter.MyViewHolder> {
        private Context context;
        private List<Object> invoiceVOList;


        InvoiceAdapter(Context context, List<Object> memberList) {
            this.context = context;
            this.invoiceVOList = memberList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView day, nul,checkdonate;


            MyViewHolder(View itemView) {
                super(itemView);
                checkdonate = itemView.findViewById(R.id.DRdate);
                day = itemView.findViewById(R.id.QrCodeA);
                nul = itemView.findViewById(R.id.DRamout);
            }
        }

        @Override
        public int getItemCount() {
            return invoiceVOList.size();
        }

        @Override
        public SelectConlist.InvoiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.ele_setdenote_record_item, viewGroup, false);
            return new SelectConlist.InvoiceAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(SelectConlist.InvoiceAdapter.MyViewHolder viewHolder, int position) {
            final Object o = invoiceVOList.get(position);
            if(o instanceof InvoiceVO)
            {   InvoiceVO invoiceVO= (InvoiceVO) o;
                viewHolder.checkdonate.setText(sf.format(new Date(invoiceVO.getTime().getTime())));
                viewHolder.day.setText("電子"+invoiceVO.getMaintype());
                viewHolder.nul.setText(invoiceVO.getAmount());
            }else{
                ConsumeVO consumeVO= (ConsumeVO) o;
                viewHolder.checkdonate.setText(sf.format(new Date(consumeVO.getDate().getTime())));
                viewHolder.day.setText("本地"+consumeVO.getMaintype());
                viewHolder.nul.setText(consumeVO.getMoney());
            }
        }
    }
}

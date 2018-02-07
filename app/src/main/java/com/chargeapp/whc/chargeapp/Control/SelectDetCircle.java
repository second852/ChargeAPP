package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectDetCircle extends Fragment {

    //    private LineChart lineChart;
    private InvoiceDB invoiceDB;
    private CarrierDB carrierDB;
    private ConsumeDB consumeDB;
    private RecyclerView listView;
    private boolean ShowConsume ;
    private boolean ShowAllCarrier;
    private boolean noShowCarrier;
    private int year,month,day,index;
    private String carrier;
    private  List<InvoiceVO> invoiceVOS;
    private List<ConsumeVO> consumeVOS;
    private HashMap<String,HashMap<String,Integer>> hashMap;






    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_con_detail, container, false);
        setDB();
        findViewById(view);
        hashMap=new HashMap<>();
        ShowConsume= (boolean) getArguments().getSerializable("ShowConsume");
        ShowAllCarrier= (boolean) getArguments().getSerializable("ShowAllCarrier");
        noShowCarrier= (boolean) getArguments().getSerializable("noShowCarrier");
        year= (int) getArguments().getSerializable("year");
        month= (int) getArguments().getSerializable("month");
        day= (int) getArguments().getSerializable("day");
        index= (int) getArguments().getSerializable("index");
        carrier= (String) getArguments().getSerializable("carrier");
        Calendar start=new GregorianCalendar(year,month,day+index,0,0,0);
        Calendar end=new GregorianCalendar(year,month,day+index,23,59,59);
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        Log.d("XXXXXX",sf.format(new Date(start.getTimeInMillis()))+" / "+sf.format(new Date(end.getTimeInMillis())));
        if(ShowConsume)
        {
               consumeVOS=consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
               for(ConsumeVO c:consumeVOS)
               {
                   if(hashMap.get(c.getMaintype())==null)
                   {
                       HashMap<String,Integer> second=new HashMap<>();
                       second.put(c.getSecondType(),Integer.valueOf(c.getMoney()));
                       hashMap.put(c.getMaintype(),second);
                   }else{
                       HashMap<String,Integer> second=hashMap.get(c.getMaintype());
                       if(second.get(c.getSecondType())==null)
                       {
                           second.put(c.getSecondType(),Integer.valueOf(c.getMoney()));
                       }else{
                           second.put(c.getSecondType(),second.get(c.getSecondType())+Integer.valueOf(c.getMoney()));
                       }
                   }
               }
        }
        if(!noShowCarrier)
        {
            if(ShowAllCarrier)
            {
                invoiceVOS=invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
            }else{
                invoiceVOS=invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()),carrier);
            }
            for(InvoiceVO I:invoiceVOS)
            {
                if(hashMap.get(I.getMaintype())==null)
                {
                    HashMap<String,Integer> second=new HashMap<>();
                    second.put(I.getSecondtype(),Integer.valueOf(I.getAmount()));
                    hashMap.put(I.getMaintype(),second);
                }else{
                    HashMap<String,Integer> second=hashMap.get(I.getMaintype());
                    if(second.get(I.getSecondtype())==null)
                    {
                        second.put(I.getSecondtype(),Integer.valueOf(I.getAmount()));
                    }else{
                        second.put(I.getSecondtype(),second.get(I.getSecondtype())+Integer.valueOf(I.getAmount()));
                    }
                }
            }
        }
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<String> stringList=new ArrayList<>(hashMap.keySet());
        for (String s:stringList)
        {
            Log.d("XXXXXXX",s);
        }
        listView.setAdapter(new ChartAdapter(getActivity(),stringList));
        return view;
    }

    private void findViewById(View view) {
        listView=view.findViewById(R.id.listCircle);
    }

    private void setDB() {
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
    }

    private PieData addData(String key,TextView detail) {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        HashMap<String,Integer> second=hashMap.get(key);
        int i=0;
        String details="key";
            for(String s:second.keySet())
            {
                yVals1.add(new Entry(second.get(s),i));
                xVals.add(s);
                Log.d("XXXXXXX",s);
                details=details+" : "+second.get(s)+"元\n";
                i++;
            }
        detail.setText(details);
        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "種類");
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(5);
        // instantiate pie data object now
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        return data;
    }

    private class ChartAdapter extends
            RecyclerView.Adapter<ChartAdapter.MyViewHolder> {
        private Context context;
        private List<String> KeyList;


        ChartAdapter (Context context, List<String> KeyList) {
            this.context = context;
            this.KeyList = KeyList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
           PieChart pieChart;
           TextView detail;
            MyViewHolder(View itemView) {
                super(itemView);
                pieChart = itemView.findViewById(R.id.pieChart);
                detail=itemView.findViewById(R.id.detail);
            }
        }

        @Override
        public int getItemCount() {
            return KeyList.size();
        }

        @Override
        public ChartAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.select_con_detail_item, viewGroup, false);
            return new ChartAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ChartAdapter.MyViewHolder viewHolder, int position) {
            String key=KeyList.get(position);
            viewHolder.pieChart.setData(addData(key,viewHolder.detail));
            viewHolder.pieChart.highlightValues(null);
            viewHolder.pieChart.invalidate();
            viewHolder.pieChart.setBackgroundColor(Color.parseColor("#f5f5f5"));
        }
    }
}

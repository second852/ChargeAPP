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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.gson.JsonObject;

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
    private ListView listView;
    private boolean ShowConsume ;
    private boolean ShowAllCarrier;
    private boolean noShowCarrier;
    private int year,month,day,index;
    private String carrier;
    private  List<InvoiceVO> invoiceVOS;
    private List<ConsumeVO> consumeVOS;
    private HashMap<String,Integer> main;
    private String mainTitle;
    private HashMap<String,HashMap<String,Integer>> hashMap;
    private int[] colorlist = {Color.parseColor("#FF8888"), Color.parseColor("#FFDD55"), Color.parseColor("#66FF66"), Color.parseColor("#77DDFF"), Color.parseColor("#D28EFF"),Color.parseColor("#aaaaaa")};
    private int size;





    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_con_detail, container, false);
        setDB();
        findViewById(view);
        hashMap=new HashMap<>();
        main=new HashMap<>();
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
        SimpleDateFormat sf=new SimpleDateFormat("yyyy 年 MM 月 dd 日");
        Log.d("XXXXXX",sf.format(new Date(start.getTimeInMillis()))+" / "+sf.format(new Date(end.getTimeInMillis())));
        int total=0;
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
                   if(main.get(c.getMaintype())==null)
                   {
                       main.put(c.getMaintype(),Integer.valueOf(c.getMoney()));
                   }else{
                       main.put(c.getMaintype(),main.get(c.getMaintype())+Integer.valueOf(c.getMoney()));
                   }
                   total=total+Integer.parseInt(c.getMoney());
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


                if(main.get(I.getMaintype())==null)
                {
                    main.put(I.getMaintype(),Integer.valueOf(I.getAmount()));
                }else{
                    main.put(I.getMaintype(),main.get(I.getMaintype())+Integer.valueOf(I.getAmount()));
                }

                total= total+Integer.parseInt(I.getAmount());
            }
        }
        mainTitle=sf.format(new Date(start.getTimeInMillis()))+"\n 總共:"+total+"元";
        List<String> stringList=new ArrayList<>(hashMap.keySet());
        size=stringList.size();
        listView.setAdapter(new ListAdapter(getActivity(),stringList));
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
        int total=0;
        if(key.equals("total"))
        {
            for(String s:main.keySet())
            {
                if(s.equals("O"))
                {
                    xVals.add("其他");
                }else{
                    xVals.add(s);
                }
                yVals1.add(new Entry(main.get(s),i));
                i++;
            }
            detail.setText(mainTitle);
        }else{
            for(String s:second.keySet())
            {
                if(s.equals("O"))
                {
                    xVals.add("其他");
                }else{
                    xVals.add(s);
                }
                yVals1.add(new Entry(second.get(s),i));
                total=total+second.get(s);
                i++;
            }
            if(key.equals("O"))
            {
                detail.setText((size==1)?mainTitle:"其他 : "+total+"元");
            }else{
                detail.setText((size==1)?mainTitle:key+" : "+total+"元");
            }
        }

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "種類");
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(5);
        dataSet.setColors(colorlist);
        // instantiate pie data object now
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new SelectCharFormat());
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.BLACK);
        return data;
    }
    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<String> KeyList;

        ListAdapter(Context context, List<String> KeyList) {
            this.context = context;
            this.KeyList = KeyList;
        }

        @Override
        public int getCount() {
            if(size==1)
            {
                return size;
            }else{
                return size+1;
            }
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.select_con_detail_item, parent, false);
            }
            PieChart pieChart=itemView.findViewById(R.id.pieChart);
            TextView detail=itemView.findViewById(R.id.detail);
            if(position==0&&size>1)
            {
                pieChart.setData(addData("total",detail));
                pieChart.setOnChartValueSelectedListener(new choiceTotal());
            }else{
                String key=KeyList.get((size==1)?position:position-1);
                pieChart.setData(addData(key,detail));
            }
            pieChart.highlightValues(null);
            pieChart.setUsePercentValues(true);
            pieChart.setDescription(" ");
            // enable hole and configure
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleRadius(7);
            pieChart.setTransparentCircleRadius(10);
            // enable rotation of the chart by touch
            pieChart.setRotationAngle(0);
            pieChart.setRotationEnabled(true);
            Legend l =  pieChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setXEntrySpace(7);
            l.setYEntrySpace(5);
            pieChart.invalidate();
            pieChart.setBackgroundColor(Color.parseColor("#f5f5f5"));
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return KeyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    private class choiceTotal implements com.github.mikephil.charting.listener.OnChartValueSelectedListener {
        @Override
        public void onValueSelected(Entry entry, int i, Highlight highlight) {
           listView.smoothScrollToPosition(entry.getXIndex()+1);
        }

        @Override
        public void onNothingSelected() {
        }
    }
}

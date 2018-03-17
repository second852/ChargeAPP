package com.chargeapp.whc.chargeapp.Control;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Wang on 2018/3/17.
 */

public class HomePage extends Fragment {
    private PieChart pieChart;
    private TextView pieChartT;
    private GoalDB goalDB;
    private InvoiceDB invoiceDB;
    private ConsumeDB consumeDB;
    private BankDB bankDB;
    private Calendar start,end;
    private HashMap<String,Integer> hashMap;
    private List<Map.Entry<String, Integer>> list_Data;
    private ArrayList<String> OKey;
    private ListView listView;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_page, container, false);
        pieChart=view.findViewById(R.id.pieChart);
        pieChartT=view.findViewById(R.id.pieChartT);
        listView=view.findViewById(R.id.list);
        goalDB=new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB=new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        end=Calendar.getInstance();
        start=new GregorianCalendar(end.get(Calendar.YEAR),end.get(Calendar.MONTH),end.get(Calendar.DAY_OF_MONTH),0,0,0);
        setListLayout();
        return view;
    }

    private void setListLayout() {
        List<GoalVO> goalVOS=goalDB.getNoCompleteAll();
        List<Object> objects=new ArrayList<>();
        if(goalVOS.size()>0)
        {
            objects.addAll(goalVOS);
        }else{
            objects.add("本周花費");
        }
        listView.setAdapter(new ListAdapter(getActivity(),objects));
    }

    @Override
    public void onStart() {
        super.onStart();
        setPieChart();
    }

    private void setPieChart() {


        hashMap=new HashMap<>();
        HashMap<String,Integer> consumeVOS=consumeDB.getTimePeriodHashMap(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
        HashMap<String,Integer> invoiceVOS=invoiceDB.getInvoiceBytimeHashMap(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
        hashMap.putAll(consumeVOS);
        for(String s:invoiceVOS.keySet())
        {
          if(hashMap.get(s)==null)
          {
              hashMap.put(s,invoiceVOS.get(s));
          }else{
              hashMap.put(s,invoiceVOS.get(s)+hashMap.get(s));
          }
        }

        pieChartT.setText(Common.sDay.format(new Date(end.getTimeInMillis()))+"今日花費 :"+hashMap.get("total")+"元");
        list_Data = new ArrayList<Map.Entry<String, Integer>>(hashMap.entrySet());
        //
        if(list_Data.size()>5)
        {
            OKey=new ArrayList<>();
            ChartEntry other = new ChartEntry("O", 0);
            Collections.sort(list_Data, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> entry1,
                                   Map.Entry<String, Integer> entry2) {

                    return (entry2.getValue() - entry1.getValue());
                }
            });
            for (int i = 0; i < list_Data.size(); i++) {
                if (i >= 4) {
                    other.setValue(other.getValue() + list_Data.get(4).getValue());
                    OKey.add(list_Data.get(4).getKey());
                    list_Data.remove(4);
                    i--;
                }
                list_Data.add(other);
           }
        }


        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);
        pieChart.setRotationAngle(30);
        pieChart.setRotationEnabled(true);
        pieChart.setDescription(Common.getDeescription());
        addData();
        // customize legends
        pieChart.getLegend().setEnabled(false);
    }

    private void addData() {
        ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();
        boolean ShowZero = true;

        for (int i = 0; i < list_Data.size(); i++) {
            if (list_Data.get(i).getValue() > 0) {
                ShowZero = false;
                if(list_Data.get(i).getKey().equals("total"))
                {
                    continue;
                }
                if(list_Data.get(i).getKey().equals("O"))
                {
                    yVals1.add(new PieEntry(list_Data.get(i).getValue(),"其他"));
                }else{
                    yVals1.add(new PieEntry(list_Data.get(i).getValue(), list_Data.get(i).getKey()));
                }
            }
        }

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "種類");
        if (ShowZero) {
            dataSet.setDrawValues(false);
            yVals1.add(new PieEntry(1, "無花費"));
            int[] c = {Color.parseColor("#CCEEFF")};
            dataSet.setColors(c);
        } else {
            dataSet.setColors(Common.getColor(yVals1.size()));
            dataSet.setDrawValues(true);

        }
        dataSet.setValueLinePart1OffsetPercentage(90.f);
        dataSet.setValueLinePart1Length(1f);
        dataSet.setValueLinePart2Length(.2f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(20f);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.setBackgroundColor(Color.parseColor("#f5f5f5"));
    }


    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<Object> objects;

        ListAdapter(Context context, List<Object> objects) {
            this.context = context;
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
                itemView = layoutInflater.inflate(R.layout.home_page_item, parent, false);
            }
            Object o=objects.get(position);
            ImageView imageView=itemView.findViewById(R.id.ivImage);
            TextView title=itemView.findViewById(R.id.goal);
            TextView describe=itemView.findViewById(R.id.describe);
            if(o instanceof GoalVO)
            {
                GoalVO goalVO= (GoalVO) o;
                if(goalVO.getType().equals("支出"))
                {
                      String timeStatue=goalVO.getTimeStatue();
                      int consumeCount;
                      if(timeStatue.equals("每天"))
                      {


                      }else if(timeStatue.equals("每周"))
                      {

                      }else if(timeStatue.equals("每月"))
                      {

                      }else if(timeStatue.equals("每年"))
                      {

                      }else {

                      }


                }else {

                }


            }else{

            }
            return itemView;
        }
    }
}





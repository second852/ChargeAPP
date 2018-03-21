package com.chargeapp.whc.chargeapp.Control;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuWrapperFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.apache.poi.ss.formula.functions.T;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private int year,month,day;


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
        year=end.get(Calendar.YEAR);
        month=end.get(Calendar.MONTH);
        day=end.get(Calendar.DAY_OF_MONTH);
        start=new GregorianCalendar(year,month,day,0,0,0);
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
            objects.add("本月花費");
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

        pieChartT.setText(Common.sDay.format(new Date(end.getTimeInMillis()))+"本日花費 : "+hashMap.get("total")+"元");
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
            TextView resultT=itemView.findViewById(R.id.resultT);
            LinearLayout resultL=itemView.findViewById(R.id.resultL);
            if(o instanceof GoalVO)
            {
                int consumeCount=0;
                Calendar start,end;
                GoalVO goalVO= (GoalVO) o;
                imageView.setImageResource(R.drawable.goal);
                String timeStatue=goalVO.getTimeStatue().trim();
                StringBuffer describeContent=new StringBuffer();
                if(goalVO.getType().trim().equals("支出"))
                {

                      if(timeStatue.equals("每天"))
                      {
                          consumeCount=consumeDB.getTimeTotal(new Timestamp(HomePage.this.start.getTimeInMillis()),new Timestamp(System.currentTimeMillis()))+
                          invoiceDB.getTotalBytime(new Timestamp(HomePage.this.start.getTimeInMillis()),new Timestamp(System.currentTimeMillis()));
                          describeContent.append("花費 : 本日支出"+consumeCount+"元");
                      }else if(timeStatue.equals("每周"))
                      {
                          int dweek=HomePage.this.start.get(Calendar.DAY_OF_WEEK);
                          start=new GregorianCalendar(year,month,day-dweek+1,0,0,0);
                          end=new GregorianCalendar(year,month,day,23,59,59);
                          consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                                  invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                          describeContent.append("花費 : 本周支出"+consumeCount+"元");
                      }else if(timeStatue.equals("每月"))
                      {
                          start=new GregorianCalendar(year,month,1,0,0,0);
                          end=new GregorianCalendar(year,month,day,23,59,59);
                          consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                                  invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                          describeContent.append("花費 : 本月支出"+consumeCount+"元");
                      }else if(timeStatue.equals("每年"))
                      {
                          int max=HomePage.this.start.getActualMaximum(Calendar.DAY_OF_MONTH);
                          start=new GregorianCalendar(year,month,1,0,0,0);
                          end=new GregorianCalendar(year,month,max,23,59,59);
                          consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                                  invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                          describeContent.append("花費 : 本年支出"+consumeCount+"元");
                      }
                      title.setText("目標 : "+goalVO.getName()+" "+goalVO.getTimeStatue()+"支出"+goalVO.getMoney()+"元");
                      if(Integer.valueOf(goalVO.getMoney())>consumeCount)
                      {
                          resultT.setText("達成");
                          resultT.setTextColor(Color.parseColor("#2E8B57"));
                          resultL.setBackgroundColor(Color.parseColor("#2E8B57"));

                      }else{
                          resultT.setText("失敗");
                          resultT.setTextColor(Color.parseColor("#DC143C"));
                          resultL.setBackgroundColor(Color.parseColor("#DC143C"));
                      }
                      describe.setText(describeContent.toString());
                }else {

                    if(timeStatue.equals("今日"))
                    {

                        consumeCount=consumeDB.getTimeTotal(new Timestamp(goalVO.getStartTime().getTime()),new Timestamp(goalVO.getEndTime().getTime()))+
                                invoiceDB.getTotalBytime(new Timestamp(goalVO.getStartTime().getTime()),new Timestamp(goalVO.getEndTime().getTime()));
                        int saveMoney=bankDB.getTimeTotal(new Timestamp(goalVO.getStartTime().getTime()),new Timestamp(goalVO.getEndTime().getTime()))-consumeCount;

                        if(goalVO.getEndTime().getTime()>System.currentTimeMillis())
                        {
                            title.setText(" 目標 :"+goalVO.getName()+" "+Common.sTwo.format(goalVO.getEndTime())+"前儲蓄"+goalVO.getMoney()+"元");
                            if(Integer.valueOf(goalVO.getMoney())<saveMoney)
                            {
                                goalVO.setStatue(1);
                                describeContent.append(Common.sTwo.format(goalVO.getEndTime())+"前已儲蓄"+saveMoney+"元");
                                resultT.setText("達成");
                                resultT.setTextColor(Color.parseColor("#2E8B57"));
                                resultL.setBackgroundColor(Color.parseColor("#2E8B57"));
                            }else{
                                goalVO.setStatue(2);
                                describeContent.append(Common.sTwo.format(goalVO.getEndTime())+"前已儲蓄"+saveMoney+"元");
                                resultT.setText("失敗");
                                resultT.setTextColor(Color.parseColor("#DC143C"));
                                resultL.setBackgroundColor(Color.parseColor("#DC143C"));
                            }
                            goalDB.update(goalVO);
                        }else{
                            title.setText(" 目標 :"+goalVO.getName()+" "+Common.sTwo.format(goalVO.getEndTime())+"前儲蓄"+goalVO.getMoney()+"元");
                            double day=((goalVO.getEndTime().getTime()-System.currentTimeMillis())/(1000*60*60*24));
                            if(Integer.valueOf(goalVO.getMoney())<saveMoney)
                            {
                                goalVO.setStatue(1);
                                describeContent.append("倒數"+(int)day+"天 目前已儲蓄"+saveMoney+"元");
                                resultT.setText("達成");
                                resultT.setTextColor(Color.parseColor("#2E8B57"));
                                resultL.setBackgroundColor(Color.parseColor("#2E8B57"));
                            }else{
                                goalVO.setStatue(2);
                                describeContent.append("倒數"+(int)day+"天 目前已儲蓄"+saveMoney+"元");
                                resultT.setText("持續中");
                                resultT.setTextColor(Color.parseColor("#DC143C"));
                                resultL.setBackgroundColor(Color.parseColor("#DC143C"));
                            }
                        }
                        describe.setText(describeContent.toString());
                    }else if(timeStatue.equals("每月"))
                    {
                        int max=HomePage.this.start.getActualMaximum(Calendar.DAY_OF_MONTH);
                        start=new GregorianCalendar(year,month,1,0,0,0);
                        end=new GregorianCalendar(year,month,max,23,59,59);
                        consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                                invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                        int savemoney=bankDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))-consumeCount;
                        title.setText(" 目標 :"+goalVO.getName()+" 每月儲蓄"+goalVO.getMoney()+"元");
                        describe.setText("目前 : 本月已存款"+savemoney+"元");
                        if(Integer.valueOf(goalVO.getMoney())<savemoney)
                        {
                            resultT.setText("達成");
                            resultT.setTextColor(Color.parseColor("#2E8B57"));
                            resultL.setBackgroundColor(Color.parseColor("#2E8B57"));
                        }else{
                            resultT.setText("失敗");
                            resultT.setTextColor(Color.parseColor("#DC143C"));
                            resultL.setBackgroundColor(Color.parseColor("#DC143C"));
                        }
                    }else if(timeStatue.equals("每年"))
                    {
                        start=new GregorianCalendar(year,0,1,0,0,0);
                        end=new GregorianCalendar(year,11,31,23,59,59);
                        consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                                invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                        int savemoney=bankDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))-consumeCount;
                        title.setText(" 目標 :"+goalVO.getName()+" 每年儲蓄"+goalVO.getMoney()+"元");
                        describe.setText("目前 : 本年已存款"+savemoney+"元");
                        if(Integer.valueOf(goalVO.getMoney())<savemoney)
                        {
                            resultT.setText("達成");
                            resultT.setTextColor(Color.parseColor("#2E8B57"));
                            resultL.setBackgroundColor(Color.parseColor("#2E8B57"));
                        }else{
                            resultT.setText("失敗");
                            resultT.setTextColor(Color.parseColor("#DC143C"));
                            resultL.setBackgroundColor(Color.parseColor("#DC143C"));
                        }
                    }

                }

            }else{

                //沒有目標顯示
                //顯示本周花費
                int consumeCount;
                resultL.setVisibility(View.GONE);
                HashMap<String,Integer> hashMap=new HashMap<>();
                List<ChartEntry> chartEntries= new ArrayList<>();
                List<String> strings=new ArrayList<>();

                if(position==0)
                {
                    imageView.setImageResource(R.drawable.bouns);
                    int dweek=HomePage.this.start.get(Calendar.DAY_OF_WEEK);
                    start=new GregorianCalendar(year,month,day-dweek+1,0,0,0);
                    end=new GregorianCalendar(year,month,day,23,59,59);
                    consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                            invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                    title.setText("本周支出 : "+consumeCount+"元");
                    List<ChartEntry> consumeVOS=consumeDB.getTimeMaxType(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                    List<ChartEntry> invoiceVOS=invoiceDB.getInvoiceBytimeMaxType(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                    chartEntries.addAll(consumeVOS);
                    chartEntries.addAll(invoiceVOS);
                }else {
                    //顯示本月花費
                    imageView.setImageResource(R.drawable.lotto);
                    int max=HomePage.this.start.getActualMaximum(Calendar.DAY_OF_MONTH);
                    start=new GregorianCalendar(year,month,1,0,0,0);
                    end=new GregorianCalendar(year,month,max,23,59,59);
                    consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                            invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                    title.setText("本年支出 : "+consumeCount+"元");
                    List<ChartEntry> consumeVOS=consumeDB.getTimeMaxType(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                    List<ChartEntry> invoiceVOS=invoiceDB.getInvoiceBytimeMaxType(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                    chartEntries.addAll(consumeVOS);
                    chartEntries.addAll(invoiceVOS);
                }

                if(chartEntries.size()>0)
                {
                    describe.setVisibility(View.VISIBLE);
                    Collections.sort(chartEntries, new Comparator<ChartEntry>() {
                        @Override
                        public int compare(ChartEntry o1, ChartEntry o2) {
                            return o2.getValue()-o1.getValue();
                        }
                    });

                    for(ChartEntry chartEntry:chartEntries)
                    {
                        if(hashMap.get(chartEntry.getKey())==null)
                        {
                            hashMap.put(chartEntry.getKey(),chartEntry.getValue());
                            strings.add(chartEntry.getKey());
                        }else{
                            hashMap.put(chartEntry.getKey(),hashMap.get(chartEntry.getKey())+chartEntry.getValue());
                        }
                    }
                    describe.setText("最多花費 : "+(strings.get(0).equals("O")?"其他":strings.get(0))+" "+ hashMap.get(strings.get(0))+"元");
                    describe.setVisibility(View.VISIBLE);
                }else{
                    describe.setVisibility(View.GONE);
                }
            }
            return itemView;
        }
    }
}





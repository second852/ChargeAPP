package com.chargeapp.whc.chargeapp.Control;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.apache.poi.ss.formula.functions.T;

import java.sql.Timestamp;
import java.text.NumberFormat;
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
    private ListView listView;
    private int year,month,day;
    private boolean ShowZero;
    private  ArrayList<PieEntry> yVals1;
    private ArrayList<String> Okey;
    private Activity context;
    private DrawerLayout drawerLayout;


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


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context.setTitle(R.string.text_Home);
        final View view = inflater.inflate(R.layout.home_page, container, false);
        pieChart=view.findViewById(R.id.pieChart);
        pieChartT=view.findViewById(R.id.pieChartT);
        listView=view.findViewById(R.id.list);
        Common.setChargeDB(context);
        goalDB=new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB=new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        end=Calendar.getInstance();
        year=end.get(Calendar.YEAR);
        month=end.get(Calendar.MONTH);
        day=end.get(Calendar.DAY_OF_MONTH);
        start=new GregorianCalendar(year,month,day,0,0,0);
        end=new GregorianCalendar(year,month,day,23,59,59);
        drawerLayout = context.findViewById(R.id.drawer_layout);
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                drawerLayout.closeDrawer(GravityCompat.START);
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        Common.setScreen(Common.screenSize,getResources().getDisplayMetrics());
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
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
        ListAdapter listAdapter= (ListAdapter) listView.getAdapter();
        if(listAdapter==null)
        {
            listView.setAdapter(new ListAdapter(context,objects));
        }else{
            listAdapter.setObjects(objects);
            listAdapter.notifyDataSetChanged();
            listView.invalidate();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setListLayout();
        setPieChart();
    }



    private void setPieChart() {
        HashMap<String,Integer> consumeVOS=consumeDB.getTimePeriodHashMap(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
        HashMap<String,Integer> invoiceVOS=invoiceDB.getInvoiceBytimeHashMap(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));

        if(invoiceVOS.size()>consumeVOS.size())
        {
            for(String s:consumeVOS.keySet())
            {
                if(invoiceVOS.get(s)==null)
                {
                    invoiceVOS.put(s,consumeVOS.get(s));
                }else{
                    invoiceVOS.put(s,invoiceVOS.get(s)+consumeVOS.get(s));
                }
            }
            addData(invoiceVOS);
        }else{
            for(String s:invoiceVOS.keySet())
            {
                if(consumeVOS.get(s)==null)
                {
                    consumeVOS.put(s,invoiceVOS.get(s));
                }else{
                    consumeVOS.put(s,invoiceVOS.get(s)+consumeVOS.get(s));
                }
            }
            addData(consumeVOS);
        }
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);
        pieChart.setRotationAngle(30);
        pieChart.setRotationEnabled(true);
        pieChart.setDescription(Common.getDeescription());
        // customize legends
        pieChart.getLegend().setEnabled(false);
    }

    private void addData(HashMap<String, Integer> consumeVOS) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        pieChartT.setText(Common.sDay.format(new Date(end.getTimeInMillis()))+"本日花費 : "+nf.format(consumeVOS.get("total"))+"元");
        yVals1 = new ArrayList<PieEntry>();
        Okey=new ArrayList<>();
        ShowZero = true;
        int total=consumeVOS.get("total");
        consumeVOS.remove("total");
        ChartEntry chartEntry=new ChartEntry("其他",0);
        int i=0;
        for (String key:consumeVOS.keySet()) {
            double part=0.0;
            if(total>0)
            {
                ShowZero = false;
                part=(consumeVOS.get(key)*100/total);
            }
            if(i<4&&part>2&&(!key.equals("O"))&&(!key.equals("0")))
            {
                yVals1.add(new PieEntry(consumeVOS.get(key),key));
                i++;
            }else{
                chartEntry.setValue(chartEntry.getValue()+consumeVOS.get(key));
                Okey.add(key);
            }
        }
        if(chartEntry.getValue()>0)
        {
            yVals1.add(new PieEntry(chartEntry.getValue(),chartEntry.getKey()));
        }
        // create pie data set
        final PieDataSet dataSet = new PieDataSet(yVals1, "種類");
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
        switch (Common.screenSize){
            case xLarge:
                dataSet.setValueTextSize(25f);
                pieChart.setEntryLabelTextSize(25f);
                break;
            case large:
                dataSet.setValueTextSize(18f);
                pieChart.setEntryLabelTextSize(18f);
                break;
            case normal:
                dataSet.setValueTextSize(12f);
                pieChart.setEntryLabelTextSize(12f);
                break;
        }

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.setBackgroundColor(Color.parseColor("#f5f5f5"));
        pieChart.setOnChartValueSelectedListener(new pieValue());
        pieChart.invalidate();
        pieChart.notifyDataSetChanged();
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
                itemView = layoutInflater.inflate(R.layout.home_page_item, parent, false);
            }
            NumberFormat nf = NumberFormat.getNumberInstance();
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
                          consumeCount=consumeDB.getTimeTotal(new Timestamp(HomePage.this.start.getTimeInMillis()),new Timestamp(HomePage.this.end.getTimeInMillis()))+
                          invoiceDB.getTotalBytime(new Timestamp(HomePage.this.start.getTimeInMillis()),new Timestamp(HomePage.this.end.getTimeInMillis()));
                          describeContent.append("花費 : 本日支出"+nf.format(consumeCount)+"元");
                      }else if(timeStatue.equals("每周"))
                      {
                          int dweek=HomePage.this.start.get(Calendar.DAY_OF_WEEK);
                          start=new GregorianCalendar(year,month,day-dweek+1,0,0,0);
                          end=new GregorianCalendar(year,month,day,23,59,59);
                          consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                                  invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                          describeContent.append("花費 : 本周支出"+nf.format(consumeCount)+"元");
                      }else if(timeStatue.equals("每月"))
                      {
                          start=new GregorianCalendar(year,month,1,0,0,0);
                          end=new GregorianCalendar(year,month,day,23,59,59);
                          consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                                  invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                          describeContent.append("花費 : 本月支出"+nf.format(consumeCount)+"元");
                      }else if(timeStatue.equals("每年"))
                      {
                          int max=HomePage.this.start.getActualMaximum(Calendar.DAY_OF_MONTH);
                          start=new GregorianCalendar(year,month,1,0,0,0);
                          end=new GregorianCalendar(year,month,max,23,59,59);
                          consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                                  invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                          describeContent.append("花費 : 本年支出"+nf.format(consumeCount)+"元");
                      }

                      //設定Title
                      title.setText("目標 : "+goalVO.getName().trim()+goalVO.getTimeStatue().trim()+"支出"+nf.format(goalVO.getMoney())+"元");


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

                        if(goalVO.getEndTime().getTime()<System.currentTimeMillis())
                        {
                            title.setText("目標 : "+goalVO.getName()+"\n"+Common.sTwo.format(goalVO.getEndTime())+"前 儲蓄"+nf.format(goalVO.getMoney())+"元");
                            if(Integer.valueOf(goalVO.getMoney())<saveMoney)
                            {
                                goalVO.setStatue(1);
                                goalVO.setNotify(false);
                                describeContent.append(Common.sTwo.format(goalVO.getEndTime())+"前\n已儲蓄"+nf.format(saveMoney)+"元");
                                resultT.setText("達成");
                                resultT.setTextColor(Color.parseColor("#2E8B57"));
                                resultL.setBackgroundColor(Color.parseColor("#2E8B57"));
                            }else{
                                goalVO.setStatue(2);
                                goalVO.setNotify(false);
                                describeContent.append(Common.sTwo.format(goalVO.getEndTime())+"前\n已儲蓄"+nf.format(saveMoney)+"元");
                                resultT.setText("失敗");
                                resultT.setTextColor(Color.parseColor("#DC143C"));
                                resultL.setBackgroundColor(Color.parseColor("#DC143C"));
                            }
                            goalDB.update(goalVO);
                        }else{
                            title.setText("目標 : "+goalVO.getName()+"\n"+Common.sTwo.format(goalVO.getEndTime())+"前 儲蓄"+nf.format(goalVO.getMoney())+"元");
                            double day=((goalVO.getEndTime().getTime()-System.currentTimeMillis())/(1000*60*60*24));
                            if(Integer.valueOf(goalVO.getMoney())<saveMoney)
                            {
                                goalVO.setStatue(1);
                                describeContent.append("倒數"+(int)day+"天\n目前已儲蓄"+nf.format(saveMoney)+"元");
                                resultT.setText("達成");
                                resultT.setTextColor(Color.parseColor("#FF8800"));
                                resultL.setBackgroundColor(Color.parseColor("#FF8800"));
                            }else{
                                goalVO.setStatue(2);
                                describeContent.append("倒數"+(int)day+"天\n目前已儲蓄"+nf.format(saveMoney)+"元");
                                resultT.setText("持續中");
                                resultT.setTextColor(Color.parseColor("#0000FF"));
                                resultL.setBackgroundColor(Color.parseColor("#0000FF"));
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
                        title.setText(" 目標 :"+goalVO.getName()+" 每月儲蓄"+nf.format(goalVO.getMoney())+"元");
                        describe.setText(" 目前 : 本月\n 已存款"+nf.format(savemoney)+"元");
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
                        title.setText(" 目標 :"+goalVO.getName()+" 每年儲蓄"+nf.format(goalVO.getMoney())+"元");
                        describe.setText(" 目前 : 本年\n 已存款"+nf.format(savemoney)+"元");
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
                    //顯示本周花費
                    imageView.setImageResource(R.drawable.bouns);
                    Calendar calendar=Calendar.getInstance();
                    int dweek=calendar.get(Calendar.DAY_OF_WEEK);
                    start=new GregorianCalendar(year,month,day-dweek+1,0,0,0);
                    end=new GregorianCalendar(year,month,day,23,59,59);
                    consumeCount=consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()))+
                            invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
                    title.setText("本周支出 : "+nf.format(consumeCount)+"元");
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
                    title.setText("本月支出 : "+nf.format(consumeCount)+"元");
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
                    describe.setText("最多花費 : "+(strings.get(0).equals("O")?"其他":strings.get(0))+" "+ nf.format(hashMap.get(strings.get(0))) +"元");
                    describe.setVisibility(View.VISIBLE);
                }else{
                    describe.setVisibility(View.GONE);
                }
            }
            return itemView;
        }
    }

    private class pieValue implements OnChartValueSelectedListener {
        @Override
        public void onValueSelected(Entry e, Highlight h) {

            if (ShowZero) {
                Fragment fragment=new InsertActivity();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                for (Fragment fragment1 :  getFragmentManager().getFragments()) {
                    fragmentTransaction.remove(fragment1);
                }
                fragmentTransaction.replace(R.id.body, fragment);
                fragmentTransaction.commit();
                return;
            }else{
                String key = yVals1.get((int) h.getX()).getLabel();
                Bundle bundle = new Bundle();
                Fragment fragment=new HomePagetList();
                bundle.putStringArrayList("OKey", Okey);
                bundle.putSerializable("key",key);
                bundle.putSerializable("total",(int)h.getY());
                bundle.putSerializable("position",0);
                fragment.setArguments(bundle);
                switchFragment(fragment);
            }
        }

        @Override
        public void onNothingSelected() {

        }
    }

    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("HomePage");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 :  getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }
}





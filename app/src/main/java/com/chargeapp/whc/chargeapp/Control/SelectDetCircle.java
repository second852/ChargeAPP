package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;

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


    private InvoiceDB invoiceDB;
    private ConsumeDB consumeDB;
    private ListView listView;
    private boolean ShowConsume;
    private boolean ShowAllCarrier;
    private boolean noShowCarrier;
    private int year, month, day, index;
    private int carrier;
    private List<InvoiceVO> invoiceVOS;
    private List<ConsumeVO> consumeVOS;
    private HashMap<String, HashMap<String, Integer>> hashMap;
    private int size;
    private CarrierDB carrierDB;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_con_detail, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(false);
        setDB();
        findViewById(view);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(false);
        hashMap = new HashMap<>();
        ShowConsume = (boolean) getArguments().getSerializable("ShowConsume");
        ShowAllCarrier = (boolean) getArguments().getSerializable("ShowAllCarrier");
        noShowCarrier = (boolean) getArguments().getSerializable("noShowCarrier");
        year = (int) getArguments().getSerializable("year");
        month = (int) getArguments().getSerializable("month");
        day = (int) getArguments().getSerializable("day");
        index = (int) getArguments().getSerializable("index");
        carrier = (int) getArguments().getSerializable("carrier");
        List<CarrierVO> carrierVOS=carrierDB.getAll();
        if(carrierVOS.size()<=0)
        {
            noShowCarrier=true;
        }
        Calendar start = new GregorianCalendar(year, month, day + index, 0, 0, 0);
        Calendar end = new GregorianCalendar(year, month, day + index, 23, 59, 59);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
        Log.d("XXXXXX", sf.format(new Date(start.getTimeInMillis())) + " / " + sf.format(new Date(end.getTimeInMillis())));
        int total = 0;
        if (ShowConsume) {
            consumeVOS = consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
            for (ConsumeVO c : consumeVOS) {
                if (hashMap.get(c.getMaintype()) == null) {
                    HashMap<String, Integer> second = new HashMap<>();
                    second.put(c.getSecondType(), Integer.valueOf(c.getMoney()));
                    hashMap.put(c.getMaintype(), second);
                } else {
                    HashMap<String, Integer> second = hashMap.get(c.getMaintype());
                    if (second.get(c.getSecondType()) == null) {
                        second.put(c.getSecondType(), Integer.valueOf(c.getMoney()));
                    } else {
                        second.put(c.getSecondType(), second.get(c.getSecondType()) + Integer.valueOf(c.getMoney()));
                    }
                }
                total = total + c.getMoney();
            }
        }
        if (!noShowCarrier) {
            String carNul=carrierVOS.get(carrier).getCarNul();
            if (ShowAllCarrier) {
                invoiceVOS = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
            } else {
                invoiceVOS = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), carNul);
            }
            for (InvoiceVO I : invoiceVOS) {
                if (hashMap.get(I.getMaintype()) == null) {
                    HashMap<String, Integer> second = new HashMap<>();
                    second.put(I.getSecondtype(), Integer.valueOf(I.getAmount()));
                    hashMap.put(I.getMaintype(), second);
                } else {
                    HashMap<String, Integer> second = hashMap.get(I.getMaintype());
                    if (second.get(I.getSecondtype()) == null) {
                        second.put(I.getSecondtype(), Integer.valueOf(I.getAmount()));
                    } else {
                        second.put(I.getSecondtype(), second.get(I.getSecondtype()) + Integer.valueOf(I.getAmount()));
                    }
                }

                total = total + I.getAmount();
            }
        }
        getActivity().setTitle(sf.format(new Date(start.getTimeInMillis())));
        List<String> stringList = new ArrayList<>(hashMap.keySet());
        size = stringList.size();
        listView.setAdapter(new ListAdapter(getActivity(), stringList));
        return view;
    }

    private void findViewById(View view) {
        listView = view.findViewById(R.id.listCircle);
    }

    private void setDB() {
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
    }

    private PieData addData(String key, TextView detail) {
        ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();
        HashMap<String, Integer> second = hashMap.get(key);
        int total = 0;
        for (String s : second.keySet()) {
            if (s.equals("O")) {
                yVals1.add(new PieEntry(second.get(s), "其他"));
            } else if(s.equals("0")){
                yVals1.add(new PieEntry(second.get(s), "未知"));
            }else{
                yVals1.add(new PieEntry(second.get(s), s));
            }
            total = total + second.get(s);
        }
        if (key.equals("O")) {
            detail.setText("其他 : " + total + "元");
        } else if(key.equals("0")){
            detail.setText("未知 : " + total + "元");
        }else{
            detail.setText(key + " : " + total + "元");
        }
        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "種類");
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(30);
        dataSet.setColors(Common.getColor(yVals1.size()));
        dataSet.setValueLinePart1OffsetPercentage(90.f);
        dataSet.setValueLinePart1Length(1f);
        dataSet.setValueLinePart2Length(.2f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        // instantiate pie data object now
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
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
           return size;
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.select_con_detail_item, parent, false);
            }
            PieChart pieChart = itemView.findViewById(R.id.pieChart);
            TextView detail = itemView.findViewById(R.id.detail);
            String key = KeyList.get(position);
            pieChart.setData(addData(key, detail));
            pieChart.setOnChartValueSelectedListener(new changeToNewF(key));
            pieChart.setDescription(Common.getDeescription());
            pieChart.highlightValues(null);
            pieChart.setUsePercentValues(true);
            // enable hole and configure
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleRadius(7);
            pieChart.setTransparentCircleRadius(10);
            // enable rotation of the chart by touch
            pieChart.setRotationAngle(30);
            pieChart.setRotationEnabled(true);
            Legend l = pieChart.getLegend();
            l.setEnabled(false);
            pieChart.invalidate();
            pieChart.setEntryLabelColor(Color.BLACK);
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


    private class changeToNewF implements com.github.mikephil.charting.listener.OnChartValueSelectedListener {
        private String key;

        changeToNewF(String key) {
            this.key = key;
        }

        @Override
        public void onValueSelected(Entry e, Highlight h) {
            Fragment fragment = new SelectDetList();
            Bundle bundle = new Bundle();
            bundle.putSerializable("ShowConsume", ShowConsume);
            bundle.putSerializable("ShowAllCarrier", ShowAllCarrier);
            bundle.putSerializable("noShowCarrier", noShowCarrier);
            bundle.putSerializable("year", year);
            bundle.putSerializable("month", month);
            bundle.putSerializable("day", day + index);
            bundle.putSerializable("key", key);
            bundle.putSerializable("carrier", carrier);
            bundle.putSerializable("index",index);
            bundle.putSerializable("statue", 0);
            bundle.putSerializable("action", "SelectDetCircle");
            bundle.putSerializable("position",0);
            bundle.putSerializable("period", getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
            fragment.setArguments(bundle);
            switchFragment(fragment);
        }

        @Override
        public void onNothingSelected() {

        }
    }

    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("SelectDetCircle");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }
}

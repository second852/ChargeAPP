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
import com.github.mikephil.charting.components.Description;
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

public class SelectOtherCircle extends Fragment {


    private InvoiceDB invoiceDB;
    private ConsumeDB consumeDB;
    private ListView listView;
    private boolean ShowConsume;
    private boolean ShowAllCarrier;
    private boolean noShowCarrier;
    private int year, month, day;
    private int carrier;
    private List<InvoiceVO> invoiceVOS;
    private List<ConsumeVO> consumeVOS;
    private ArrayList<String> Okey;
    private Calendar start, end;
    private int Statue;
    private int total,period,dweek;
    private List<Integer> totalList;
    private HashMap<String, HashMap<String, Integer>> mapHashMap;
    private int countOther;
    private TextView message;
    private CarrierDB carrierDB;
    private List<CarrierVO> carrierVOS;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_con_detail, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(false);
        setDB();
        carrierVOS=carrierDB.getAll();
        listView = view.findViewById(R.id.listCircle);
        message=view.findViewById(R.id.message);
        ShowConsume = (boolean) getArguments().getSerializable("ShowConsume");
        ShowAllCarrier = (boolean) getArguments().getSerializable("ShowAllCarrier");
        noShowCarrier = (boolean) getArguments().getSerializable("noShowCarrier");
        year = (int) getArguments().getSerializable("year");
        month = (int) getArguments().getSerializable("month");
        day = (int) getArguments().getSerializable("day");
        carrier = (int) getArguments().getSerializable("carrier");
        Okey = getArguments().getStringArrayList("OKey");
        Statue = (int) getArguments().getSerializable("statue");
        period= (int) getArguments().getSerializable("period");
        dweek= (int) getArguments().getSerializable("dweek");
        countOther = (int) getArguments().getSerializable("total");
        String title;
        if (Statue == 0) {
            start = new GregorianCalendar(year, month, day, 0, 0, 0);
            end = new GregorianCalendar(year, month, day, 23, 59, 59);
            title = Common.sOne.format(new Date(start.getTimeInMillis()));
        } else if (Statue == 1) {
            start=new GregorianCalendar(year,month,day - dweek + 1,0,0,0);
            end=new GregorianCalendar(year,month,day - dweek + 1 + period-1,23,59,59);
            title = Common.sTwo.format(new Date(start.getTimeInMillis())) + "~" + Common.sTwo.format(new Date(end.getTimeInMillis()));
        } else if (Statue == 2) {
            start = new GregorianCalendar(year, month, 1, 0, 0, 0);
            end = new GregorianCalendar(year, month, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            title = Common.sThree.format(new Date(start.getTimeInMillis()));
        } else {
            start = new GregorianCalendar(year, 0, 1, 0, 0, 0);
            end = new GregorianCalendar(year, 11, 31, 23, 59, 59);
            title = Common.sFour.format(new Date(start.getTimeInMillis()));
        }
        getActivity().setTitle(title);
        setLayout();
        return view;
    }

    private void setLayout() {
        totalList = new ArrayList<>();
        mapHashMap = new HashMap<>();
        HashMap<String, Integer> second;
        HashMap<String, Integer> totalOther = new HashMap<>();
        for (int i=0;i<Okey.size();i++) {
            String key=Okey.get(i);
            Log.d("test",key);
            total = 0;
            if (ShowConsume) {
                consumeVOS = consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), key);
                for (ConsumeVO c : consumeVOS) {
                    if(mapHashMap.get(c.getMaintype())==null)
                    {
                        second = new HashMap<>();
                        second.put(c.getSecondType(),Integer.valueOf(c.getMoney()));
                    }else{
                        second=mapHashMap.get(c.getMaintype());
                        if(second.get(c.getSecondType())==null)
                        {
                            second.put(c.getSecondType(),Integer.valueOf(c.getMoney()));
                        }else{
                            second.put(c.getSecondType(), second.get(c.getSecondType()) + Integer.valueOf(c.getMoney()));
                        }
                    }
                    mapHashMap.put(c.getMaintype(),second);
                    total = total + c.getMoney();
                }
            }
            if (!noShowCarrier&&carrierVOS.size()>0) {
                if (ShowAllCarrier) {
                    invoiceVOS = invoiceDB.getInvoiceBytimeMainType(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), key);
                } else {
                    invoiceVOS = invoiceDB.getInvoiceBytimeMainType(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), key, carrierVOS.get(carrier).getCarNul());
                }
                for (InvoiceVO I : invoiceVOS) {
                    if(mapHashMap.get(I.getMaintype())==null)
                    {
                        second = new HashMap<>();
                        second.put(I.getSecondtype(),Integer.valueOf(I.getAmount()));
                    }else{
                        second=mapHashMap.get(I.getMaintype());
                        if(second.get(I.getSecondtype())==null)
                        {
                            second.put(I.getSecondtype(), Integer.valueOf(I.getAmount()));
                        }else{
                            second.put(I.getSecondtype(), second.get(I.getSecondtype()) + Integer.valueOf(I.getAmount()));
                        }
                    }
                    mapHashMap.put(I.getMaintype(),second);
                    total = total + I.getAmount();
                }
            }
            if (total <= 0) {
                Okey.remove(key);
                continue;
            }
            totalOther.put(key, total);
        }
        if(Okey.size()>1)
        {
            Okey.add(0, "total");
            mapHashMap.put("total", totalOther);
        }
        listView.setAdapter(new ListAdapter(getActivity(), Okey));
    }


    private void setDB() {
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
    }

    private PieData addData(String key, TextView detail, HashMap<String, Integer> hashMap) {
        ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();
        int total=0;
        if(key.equals("total"))
        {
            for(int j=1;j<Okey.size();j++)
            {
                if (Okey.get(j).equals("O")) {
                    yVals1.add(new PieEntry(hashMap.get(Okey.get(j)), "其他"));
                } else if(Okey.get(j).equals("0")){
                    if(hashMap.get(Okey.get(j))!=null)
                    {
                        yVals1.add(new PieEntry(hashMap.get(Okey.get(j)), "未知"));
                    }
                }else{
                    yVals1.add(new PieEntry(hashMap.get(Okey.get(j)), Okey.get(j)));
                }
                total=total+hashMap.get(Okey.get(j));
            }
        }else{
            for (String s : hashMap.keySet()) {
                if (s.equals("O")) {
                    yVals1.add(new PieEntry(hashMap.get(s), "其他"));
                } else if(s.equals("0")){
                    if(hashMap.get(s)!=null)
                    {
                        yVals1.add(new PieEntry(hashMap.get(s), "未知"));
                    }
                }else{
                    yVals1.add(new PieEntry(hashMap.get(s), s));
                }
                total=total+hashMap.get(s);
            }
        }
        if (key.equals("O")) {
            detail.setText("其他" + " : 總共" + total + "元");
        } else if (key.equals("total")) {
            detail.setText("其他細項 : 總共" + countOther + "元");
        } else if(key.equals("0")){
            detail.setText("未知" + " : 總共" + total + "元");
        }else{
            detail.setText(key + " : 總共" + total + "元");
        }
        PieDataSet dataSet = new PieDataSet(yVals1, "種類");
        dataSet.setDrawValues(true);
        dataSet.setValueLinePart1OffsetPercentage(90.f);
        dataSet.setValueLinePart1Length(1f);
        dataSet.setValueLinePart2Length(.2f);
        dataSet.setColors(Common.getColor(yVals1.size()));
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(30);
        dataSet.setValueTextColor(Color.BLACK);
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
            return KeyList.size();
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
            HashMap<String, Integer> hashMap = mapHashMap.get(key);
            if(hashMap!=null)
            {
                pieChart.setData(addData(key, detail,hashMap));
                if(key.equals("total"))
                {
                    pieChart.setOnChartValueSelectedListener(new choiceTotal());
                }else{
                    pieChart.setOnChartValueSelectedListener(new changeToNewF(key));
                }
                pieChart.highlightValues(null);
                pieChart.setUsePercentValues(true);
                pieChart.setDrawHoleEnabled(true);
                pieChart.setHoleRadius(7);
                pieChart.setTransparentCircleRadius(10);
                pieChart.setRotationAngle(60);
                pieChart.setRotationEnabled(true);
                pieChart.setEntryLabelColor(Color.BLACK);
                pieChart.getLegend().setEnabled(false);
                pieChart.setDescription(Common.getDeescription());
                pieChart.invalidate();
                pieChart.setBackgroundColor(Color.parseColor("#f5f5f5"));
            }
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
        public void onValueSelected(Entry e, Highlight h) {
            int index= (int) h.getX();
            index=index+1;
            listView.smoothScrollToPosition(index);
        }
        @Override
        public void onNothingSelected() {
        }
    }

    private class changeToNewF implements com.github.mikephil.charting.listener.OnChartValueSelectedListener {
        private String key;

        public changeToNewF(String key) {
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
            bundle.putSerializable("key", key);
            bundle.putSerializable("day", day);
            bundle.putSerializable("carrier", carrier);
            bundle.putSerializable("statue",Statue);
            bundle.putSerializable("position",0);
            bundle.putSerializable("period", period);
            bundle.putSerializable("dweek",dweek);
            bundle.putSerializable("total",getArguments().getSerializable("total"));
            bundle.putStringArrayList("OKey",getArguments().getStringArrayList("OKey"));
            fragment.setArguments(bundle);
            switchFragment(fragment);
        }

        @Override
        public void onNothingSelected() {

        }
    }

    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("SelectOtherCircle");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }
}

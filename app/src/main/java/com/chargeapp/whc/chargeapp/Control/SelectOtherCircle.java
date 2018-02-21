package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
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
    private String carrier;
    private List<InvoiceVO> invoiceVOS;
    private List<ConsumeVO> consumeVOS;
    private ArrayList<String> Okey;
    private Calendar start, end;
    private int Statue;
    private int total;
    private List<Integer> totalList;
    private HashMap<String, HashMap<String, Integer>> mapHashMap;
    private int countOther;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_con_detail, container, false);
        setDB();
        listView = view.findViewById(R.id.listCircle);
        ShowConsume = (boolean) getArguments().getSerializable("ShowConsume");
        ShowAllCarrier = (boolean) getArguments().getSerializable("ShowAllCarrier");
        noShowCarrier = (boolean) getArguments().getSerializable("noShowCarrier");
        year = (int) getArguments().getSerializable("year");
        month = (int) getArguments().getSerializable("month");
        day = (int) getArguments().getSerializable("day");
        carrier = (String) getArguments().getSerializable("carrier");
        Okey = getArguments().getStringArrayList("OKey");
        Statue = (int) getArguments().getSerializable("statue");
        countOther = (int) getArguments().getSerializable("total");
        String title;
        if (Statue == 0) {
            start = new GregorianCalendar(year, month, day, 0, 0, 0);
            end = new GregorianCalendar(year, month, day, 23, 59, 59);
            title = Common.sOne.format(new Date(start.getTimeInMillis()));

        } else if (Statue == 1) {
            start = new GregorianCalendar(year, month, day, 0, 0, 0);
            end = new GregorianCalendar(year, month, day + 6, 23, 59, 59);
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
        Okey.add("O");
        SelectActivity.mainTitle.setText(title);
        setLayout();

        return view;
    }

    private void setLayout() {
        totalList = new ArrayList<>();
        mapHashMap = new HashMap<>();
        HashMap<String, Integer> second;
        HashMap<String, Integer> totalOther = new HashMap<>();
        for (String key : Okey) {
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
                    total = total + Integer.parseInt(c.getMoney());
                }
            }
            if (!noShowCarrier) {
                if (ShowAllCarrier) {
                    invoiceVOS = invoiceDB.getInvoiceBytimeMainType(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), key);
                } else {
                    invoiceVOS = invoiceDB.getInvoiceBytimeMainType(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), key, carrier);
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
                    total = total + Integer.parseInt(I.getAmount());
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
    }

    private PieData addData(String key, TextView detail) {
        ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();
        HashMap<String, Integer> hashMap = mapHashMap.get(key);
        int i = 0;
        int total=0;
        for (String s : hashMap.keySet()) {
            if (s.equals("O")) {
                yVals1.add(new PieEntry(hashMap.get(s), "其他"));
            } else {
                yVals1.add(new PieEntry(hashMap.get(s), s));
            }
            total=total+hashMap.get(s);
            i++;
        }
        if (key.equals("O")) {
            detail.setText("其他" + " : 總共" + total + "元");
        } else if (key.equals("total")) {
            detail.setText("其他細項 : 總共" + countOther + "元");
        } else {
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
            pieChart.setData(addData(key, detail));
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
            pieChart.setRotationAngle(30);
            pieChart.setRotationEnabled(true);
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.getLegend().setEnabled(false);
            pieChart.setDescription(Common.getDeescription());
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
        public void onValueSelected(Entry e, Highlight h) {
            listView.smoothScrollToPosition((int) (h.getX()+1));
        }
        @Override
        public void onNothingSelected() {
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
            bundle.putSerializable("key", key);
            bundle.putSerializable("day", day);
            bundle.putSerializable("carrier", carrier);
            bundle.putSerializable("Statue",Statue);
            fragment.setArguments(bundle);
            switchFragment(fragment);
        }

        @Override
        public void onNothingSelected() {

        }
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }
}

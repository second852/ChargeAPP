package com.chargeapp.whc.chargeapp.Control;


import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

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
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectIncome extends Fragment {


    private TextView PIdateTittle, describe;
    private ImageView PIdateCut, PIdateAdd;
    private String TAG = "SelectIncome";
    private BarChart chart_bar;
    private int month, year,day;
    private Spinner choicePeriod;
    private PieChart chart_pie;
    private int total, period;
    private String DesTittle;
    private List<String> chartLabels;
    private BankDB bankDB;
    private List<Map.Entry<String, Integer>> list_Data;
    private List<BankVO> bankVOS;
    private ArrayList<String> Okey;
    private List<String> type;
    public static Calendar end;
    public static int Statue;
    private Activity context;

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
        View view = inflater.inflate(R.layout.select_income, container, false);
        if(end==null)
        {
            end=Calendar.getInstance();
        }
        month = end.get(Calendar.MONTH);
        year = end.get(Calendar.YEAR);
        day=end.get(Calendar.DAY_OF_MONTH);
        Common.setChargeDB(context);
        bankDB = new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        findViewById(view);
        PIdateAdd.setOnClickListener(new AddOnClick());
        PIdateCut.setOnClickListener(new CutOnClick());
        choicePeriod.setOnItemSelectedListener(new ChoicePeriodStatue());
        chart_pie.setOnChartValueSelectedListener(new pievalue());
        chart_bar.setOnChartValueSelectedListener(new charValue());
        choicePeriod.setSelection(Statue);
        return view;
    }


    private void findViewById(View view) {
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        chart_bar = view.findViewById(R.id.chart_bar);
        choicePeriod = view.findViewById(R.id.choicePeriod);
        chart_pie = view.findViewById(R.id.chart_pie);
        describe = view.findViewById(R.id.describe);
        ArrayList<String> SpinnerItem1 = new ArrayList<>();
        SpinnerItem1.add(" 月 ");
        SpinnerItem1.add(" 年 ");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinneritem, SpinnerItem1);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        choicePeriod.setAdapter(arrayAdapter);
    }


    private BarData getBarData() {
        BarDataSet dataSetA = new BarDataSet(getChartData(), " ");
        dataSetA.setColors(Common.getColor(list_Data.size()));
        dataSetA.setStackLabels(getStackLabels());
        dataSetA.setDrawValues(false);
        dataSetA.setHighLightAlpha(20);
        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetA);
        return new BarData(dataSets);
    }


    private String[] getStackLabels() {
        String[] s = new String[list_Data.size()];
        for (int i = 0; i < s.length; i++) {
            s[i] = list_Data.get(i).getKey();
        }
        return s;
    }

    private void findMaxFive() {
        total = 0;
        Okey = new ArrayList<>();
        ChartEntry other = new ChartEntry("其他", 0);
        Map<String, Integer> hashMap = new HashMap<>();
        Calendar start, end;
        if (Statue == 0) {
            DesTittle = "本月收入";
            start = new GregorianCalendar(year, month, 1, 0, 0, 0);
            end = new GregorianCalendar(year, month, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            PIdateTittle.setText(Common.sThree.format(new Date(start.getTimeInMillis())));
        } else {
            DesTittle = "本年收入";
            start = new GregorianCalendar(year, 0, 1, 0, 0, 0);
            end = new GregorianCalendar(year, 11, 31, 23, 59, 59);
            PIdateTittle.setText(Common.sFour.format(new Date(start.getTimeInMillis())));
        }
        bankVOS = bankDB.getTimeAll(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
        for (BankVO b : bankVOS) {
            if (hashMap.get(b.getMaintype()) == null) {
                hashMap.put(b.getMaintype(), Integer.valueOf(b.getMoney()));
            } else {
                hashMap.put(b.getMaintype(), Integer.valueOf(b.getMoney()) + hashMap.get(b.getMaintype()));
            }
            total = total + Integer.valueOf(b.getMoney());
        }
        list_Data = new ArrayList<Map.Entry<String, Integer>>(hashMap.entrySet());
        Collections.sort(list_Data, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> entry1,
                               Map.Entry<String, Integer> entry2) {
                return (entry2.getValue() - entry1.getValue());
            }
        });


        for (int i = 0; i < list_Data.size(); i++) {
            Double percent = ((double) list_Data.get(i).getValue() / total) * 100;
            if (percent < 4) {
                Okey.add(list_Data.get(i).getKey());
                other.setValue(other.getValue() + list_Data.get(i).getValue());
                list_Data.remove(i);
                i--;
                Log.d(TAG,i+" : "+list_Data.size());
                continue;
            }
            if (i >= 4) {
                other.setValue(other.getValue() + list_Data.get(4).getValue());
                Okey.add(list_Data.get(4).getKey());
                Log.d(TAG, list_Data.get(4).getKey());
                list_Data.remove(4);
                i--;
            }
        }
        list_Data.add(other);
    }

    private List<String> getLabels() {
        chartLabels = new ArrayList<>();
        Calendar time;
        if (Statue == 0) {
            for (int i = 0; i < period; i++) {
                time = new GregorianCalendar(year, month, 1 + i);
                chartLabels.add(Common.sDay.format(new Date(time.getTimeInMillis())));
            }
        } else {
            for (int i = 0; i < period; i++) {
                chartLabels.add(i + 1 + "月");
            }
        }
        return chartLabels;
    }


    private List<BarEntry> getChartData() {
        List<BarEntry> chartData = new ArrayList<>();
        Calendar start, end;
        BarEntry barEntry;
        if (Statue == 0) {
            for (int i = 0; i < period; i++) {
                start = new GregorianCalendar(year, month, i + 1, 0, 0, 0);
                end = new GregorianCalendar(year, month, i + 1, 23, 59, 59);
                barEntry = new BarEntry(i, Periodfloat(start, end));
                chartData.add(barEntry);
            }
        } else {
            for (int i = 0; i < period; i++) {
                start = new GregorianCalendar(year, month + i, 1, 0, 0, 0);
                end = new GregorianCalendar(year, month + i, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
                barEntry = new BarEntry(i, Periodfloat(start, end));
                chartData.add(barEntry);
            }
        }
        return chartData;
    }

    private float[] Periodfloat(Calendar start, Calendar end) {
        Map<String, Integer> barMap = new HashMap<>();
        float[] f = new float[list_Data.size()];
        List<BankVO> bankVOS = bankDB.getTimeAll(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
        boolean isOtherExist;
        ChartEntry other = new ChartEntry("其他", 0);
        for (BankVO b : bankVOS) {
            isOtherExist = true;
            for (int i = 0; i < list_Data.size(); i++) {
                if (list_Data.get(i).getKey().equals(b.getMaintype())) {
                    if (barMap.get(b.getMaintype()) == null) {
                        barMap.put(b.getMaintype(), Integer.valueOf(b.getMoney()));
                    } else {
                        barMap.put(b.getMaintype(), Integer.valueOf(b.getMoney()) + barMap.get(b.getMaintype()));
                    }
                    isOtherExist = false;
                    break;
                }
            }
            if (isOtherExist) {
                other.setValue(other.getValue() + Integer.valueOf(b.getMoney()));
            }
        }
        for (int i = 0; i < list_Data.size(); i++) {
            if (list_Data.get(i).getKey().equals("其他")) {
                f[i] = other.getValue();
                continue;
            }
            if (barMap.get(list_Data.get(i).getKey()) == null) {
                f[i] = 0;
                continue;
            }
            f[i] = barMap.get(list_Data.get(i).getKey());
        }
        return f;
    }


    public void dataAnalyze() {
        chart_bar.clear();
        findMaxFive();
        //ChartBar setting
        chart_bar.setDrawGridBackground(false);
        chart_bar.setDragEnabled(true);
        chart_bar.setScaleEnabled(true);
        chart_bar.setEnabled(true);
        chart_bar.invalidate();
        chart_bar.setDrawBarShadow(false);
        chart_bar.setDoubleTapToZoomEnabled(false);
        chart_bar.setDescription(Common.getDeescription());
        XAxis xAxis = chart_bar.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        if (period > 12) {
            xAxis.setGranularity(4f);
        } else {
            xAxis.setGranularity(1f);
        }
        YAxis yAxis = chart_bar.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yAxis1 = chart_bar.getAxis(YAxis.AxisDependency.RIGHT);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                try {
                    List<String> ss = getLabels();
                    int idex = (int) value;
                    return ss.get(idex);
                } catch (Exception e) {
                    return String.valueOf(value);
                }
            }
        });
        xAxis.setLabelCount(period);
        chart_bar.setData(getBarData());
        yAxis.setMinWidth(0);
        yAxis.setAxisMinimum(0);
        yAxis1.setAxisMinimum(0);


        chart_pie.setUsePercentValues(true);
        chart_pie.setDrawHoleEnabled(true);
        chart_pie.setHoleRadius(7);
        chart_pie.setTransparentCircleRadius(10);
        chart_pie.setRotationAngle(30);
        chart_pie.setRotationEnabled(true);
        chart_pie.setDescription(Common.getDeescription());
        addData();
        chart_pie.getLegend().setEnabled(false);
    }

    private void addData() {
        type = new ArrayList<>();
        describe.setText(DesTittle + total + "元");
        ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();
        boolean ShowZero = true;
        ChartEntry other = new ChartEntry("其他", 0);
        for (int i = 0; i < list_Data.size(); i++) {
            if (list_Data.get(i).getValue() <= 0) {
                continue;
            }
            ShowZero = false;
            if (list_Data.get(i).getKey().equals("其他")) {
                other.setValue(other.getValue() + list_Data.get(i).getValue());
                continue;
            }
            type.add(list_Data.get(i).getKey());
            pieEntries.add(new PieEntry(list_Data.get(i).getValue(), list_Data.get(i).getKey()));
        }
        if (other.getValue() > 0) {
            type.add("其他");
            pieEntries.add(new PieEntry(other.getValue(), "其他"));
        }

        // create pie data set
        PieDataSet dataSet = new PieDataSet(pieEntries, "種類");
        if (ShowZero) {
            dataSet.setDrawValues(false);
            pieEntries.add(new PieEntry(1, "無收入"));
            int[] c = {Color.parseColor("#CCEEFF")};
            dataSet.setColors(c);
        } else {
            dataSet.setColors(Common.getColor(pieEntries.size()));
            dataSet.setDrawValues(true);
        }

        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(20);
        dataSet.setValueLinePart1OffsetPercentage(90.f);
        dataSet.setValueLinePart1Length(1f);
        dataSet.setValueLinePart2Length(.2f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        // instantiate pie data object now
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        chart_pie.setEntryLabelColor(Color.BLACK);
        chart_pie.setData(data);
        chart_pie.highlightValues(null);
        chart_pie.invalidate();
        chart_pie.setBackgroundColor(Color.parseColor("#f5f5f5"));
    }


    private class AddOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (Statue == 0) {
                month = month + 1;
                end = new GregorianCalendar(year, month, 1);
                period = end.getActualMaximum(Calendar.DAY_OF_MONTH);
            } else {
                year = year + 1;
                period = 12;
            }
            dataAnalyze();
        }
    }

    private class CutOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (Statue == 0) {
                month = month - 1;
                end = new GregorianCalendar(year, month, 1);
                period = end.getActualMaximum(Calendar.DAY_OF_MONTH);
            } else {
                year = year - 1;
                period = 12;
            }
            dataAnalyze();
        }
    }


    private class ChoicePeriodStatue implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Statue = position;
            month = end.get(Calendar.MONTH);
            year = end.get(Calendar.YEAR);
            if (position == 0) {
                period = end.getActualMaximum(Calendar.DAY_OF_MONTH);
                dataAnalyze();
            } else {
                period = 12;
                month = 0;
                Log.d(TAG, "month" + month);
                dataAnalyze();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("SelectActivity");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

    private class pievalue implements OnChartValueSelectedListener {

        @Override
        public void onValueSelected(Entry e, Highlight h) {
            if (list_Data.size()<=0) {
                return;
            }
            if (type.size()<=0) {
                return;
            }
            Fragment fragment = new SelectListPieIncome();
            Bundle bundle = new Bundle();
            bundle.putSerializable("year", year);
            bundle.putSerializable("month", month);
            bundle.putSerializable("day", day);
            bundle.putSerializable("statue", Statue);
            bundle.putSerializable("type", type.get((int) h.getX()));
            bundle.putStringArrayList("OKey", Okey);
            fragment.setArguments(bundle);
            switchFragment(fragment);
        }

        @Override
        public void onNothingSelected() {

        }
    }

    private class charValue implements OnChartValueSelectedListener {
        @Override
        public void onValueSelected(Entry e, Highlight h) {
            if (e.getY()<=0) {
                return;
            }
            Fragment fragment = new SelectListBarIncome();
            Bundle bundle = new Bundle();
            bundle.putSerializable("year", year);
            bundle.putSerializable("month", month);
            bundle.putSerializable("day", day);
            bundle.putSerializable("statue", Statue);
            bundle.putSerializable("index", (int) h.getX());
            fragment.setArguments(bundle);
            switchFragment(fragment);
        }

        @Override
        public void onNothingSelected() {

        }
    }
}

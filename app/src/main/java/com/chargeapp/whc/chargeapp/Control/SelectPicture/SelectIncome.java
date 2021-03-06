package com.chargeapp.whc.chargeapp.Control.SelectPicture;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
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
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;


import org.jsoup.internal.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chargeapp.whc.chargeapp.Control.Common.choiceCurrency;
import static com.chargeapp.whc.chargeapp.Control.Common.getCurrency;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectIncome extends Fragment {


    private TextView PIdateTittle;
    private ImageView PIdateCut, PIdateAdd;
    private String TAG = "SelectIncome";
    private BarChart chart_bar;
    private int month, year, day;
    private BootstrapDropDown choicePeriod;
    private PieChart chart_pie;
    private int period;
    private String DesTittle;
    private List<String> chartLabels;
    private BankDB bankDB;
    private List<Map.Entry<String, Double>> list_Data;
    private List<BankVO> bankVOS;
    private ArrayList<String> Okey;
    private List<String> type;
    public static Calendar end;
    public static int Statue;
    private Activity context;
    private List<BootstrapText> periodIncome;

    private SharedPreferences sharedPreferences;
    private String nowCurrency;
    private CurrencyDB currencyDB;
    private CurrencyVO currencyVO;
    private double total;
    private BootstrapButton setCurrency;
    private AwesomeTextView otherMessage;
    private PopupMenu popupMenu;
    private Calendar startPopup, endPopup;
    private boolean oneShow;
    private float lastX, lastY;
    private HorizontalBarChart chartHor;
    private boolean ShowZero;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = (Activity) context;
        } else {
            this.context = getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Common.setScreen(Common.screenSize, context);
        final View view = inflater.inflate(R.layout.select_income, container, false);
        if (end == null) {
            end = Calendar.getInstance();
        }

        month = end.get(Calendar.MONTH);
        year = end.get(Calendar.YEAR);
        day = end.get(Calendar.DAY_OF_MONTH);
        Common.setChargeDB(context);
        bankDB = new BankDB(MainActivity.chargeAPPDB);
        currencyDB = new CurrencyDB(MainActivity.chargeAPPDB);
        findViewById(view);
        PIdateAdd.setOnClickListener(new AddOnClick());
        PIdateCut.setOnClickListener(new CutOnClick());
        TouchView touchView=new TouchView();
        chart_pie.setOnTouchListener(touchView);
        chart_bar.setOnTouchListener(touchView);
        chartHor.setOnTouchListener(touchView);

        chartHor.setOnChartValueSelectedListener(new PieValue(chartHor));
        chart_pie.setOnChartValueSelectedListener(new PieValue(chart_pie));


        chart_bar.setOnChartValueSelectedListener(new charValue());
        choicePeriod.setOnDropDownItemClickListener(new choicePeriodI());



        //current
        sharedPreferences = context.getSharedPreferences("Charge_User", Context.MODE_PRIVATE);
        nowCurrency = sharedPreferences.getString(Common.choiceCurrency, "TWD");
        setCurrency.setText(getCurrency(nowCurrency));
        popupMenu = new PopupMenu(context, setCurrency);
        Common.createCurrencyPopMenu(popupMenu, context);
        setCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
        popupMenu.setOnMenuItemClickListener(new choiceCurrency());


        //        choicePeriod.setOnItemSelectedListener(new ChoicePeriodStatue());
//        choicePeriod.setSelection(Statue);

        switch (Statue) {
            case 0:
                period = end.getActualMaximum(Calendar.DAY_OF_MONTH);
                break;
            case 1:
                month = 0;
                period = 12;
                break;
        }
        dataAnalyze();
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                choicePeriod.setBootstrapText(periodIncome.get(Statue));
                choicePeriod.setShowOutline(false);
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        return view;
    }


    private void findViewById(View view) {
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        chart_bar = view.findViewById(R.id.chart_bar);
        choicePeriod = view.findViewById(R.id.choicePeriod);
        chart_pie = view.findViewById(R.id.chart_pie);
        otherMessage = view.findViewById(R.id.otherMessage);
        chartHor = view.findViewById(R.id.chart_hor);
        otherMessage.setBootstrapBrand(null);
        otherMessage.setTextColor(Color.BLACK);
        setCurrency = view.findViewById(R.id.setCurrency);
        ArrayList<String> SpinnerItem1 = new ArrayList<>();
        SpinnerItem1.add(" 月 ");
        SpinnerItem1.add(" 年 ");
        periodIncome = new ArrayList<>();
        String[] periodArray = new String[SpinnerItem1.size()];
        for (int i = 0; i < SpinnerItem1.size(); i++) {
            periodIncome.add(Common.setPeriodSelectCBsTest(context, SpinnerItem1.get(i)));
            periodArray[i] = SpinnerItem1.get(i);
        }
        choicePeriod.setDropdownData(periodArray);
        choicePeriod.setBootstrapText(periodIncome.get(Statue));
        choicePeriod.setShowOutline(false);


        dataAnalyze();
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinneritem, SpinnerItem1);
//        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
//        choicePeriod.setAdapter(arrayAdapter);
    }


    private BarData getBarData() {
        BarDataSet dataSetA = new BarDataSet(getChartData(), " ");
        dataSetA.setColors(Common.getColor(list_Data.size()));
        dataSetA.setStackLabels(getStackLabels());
        dataSetA.setDrawValues(false);
        dataSetA.setHighLightAlpha(20);
        BarData barDataSet = new BarData(dataSetA);
        return barDataSet;
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
        ChartEntry other = new ChartEntry("其他", 0.0);
        Map<String, Double> hashMap = new HashMap<>();
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


        startPopup = new GregorianCalendar();
        startPopup.setTime(start.getTime());
        endPopup = new GregorianCalendar();
        endPopup.setTime(end.getTime());
        //SetCurrency choice
        currencyVO = currencyDB.getBytimeAndType(start.getTimeInMillis(), end.getTimeInMillis(), nowCurrency);

        bankVOS = bankDB.getTimeAll(start.getTimeInMillis(), end.getTimeInMillis());
        for (BankVO b : bankVOS) {
            CurrencyVO currencyVO = currencyDB.getBytimeAndType(start.getTimeInMillis(), end.getTimeInMillis(), b.getCurrency());
            String money = (b.getRealMoney() == null) ? String.valueOf(b.getMoney()) : b.getRealMoney();
            Double bankAmount = Double.valueOf(money) * Double.valueOf(currencyVO.getMoney())/Double.valueOf(this.currencyVO.getMoney());
            if (hashMap.get(b.getMaintype()) == null) {
                hashMap.put(b.getMaintype(), bankAmount);
            } else {
                hashMap.put(b.getMaintype(), bankAmount + hashMap.get(b.getMaintype()));
            }
            total = total + bankAmount;
        }
        list_Data = new ArrayList<Map.Entry<String, Double>>(hashMap.entrySet());
        Collections.sort(list_Data, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> entry1,
                               Map.Entry<String, Double> entry2) {
                return (int) (entry2.getValue() - entry1.getValue());
            }
        });


        for (int i = 0; i < list_Data.size(); i++) {
            Double percent = ((double) list_Data.get(i).getValue() / total) * 100;
            if (percent < 4) {
                Okey.add(list_Data.get(i).getKey());
                other.setValue(other.getValue() + list_Data.get(i).getValue());
                list_Data.remove(i);
                i--;
                Log.d(TAG, i + " : " + list_Data.size());
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
        Map<String, Double> barMap = new HashMap<>();
        float[] f = new float[list_Data.size()];
        List<BankVO> bankVOS = bankDB.getTimeAll(start.getTimeInMillis(), end.getTimeInMillis());
        boolean isOtherExist;
        ChartEntry other = new ChartEntry("其他", 0.0);
        for (BankVO b : bankVOS) {
            isOtherExist = true;
            CurrencyVO currencyVO = currencyDB.getBytimeAndType(start.getTimeInMillis(), end.getTimeInMillis(), b.getCurrency());

            if (StringUtil.isBlank(b.getRealMoney())) {
                b.setRealMoney(String.valueOf(b.getMoney()));
                bankDB.update(b);
            }

            Double bankAmount = Double.valueOf(b.getRealMoney()) * Double.valueOf(currencyVO.getMoney());
            bankAmount = bankAmount / Double.valueOf(this.currencyVO.getMoney());
            for (int i = 0; i < list_Data.size(); i++) {
                if (list_Data.get(i).getKey().equals(b.getMaintype())) {
                    if (barMap.get(b.getMaintype()) == null) {
                        barMap.put(b.getMaintype(), bankAmount);
                    } else {
                        barMap.put(b.getMaintype(), bankAmount + barMap.get(b.getMaintype()));
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
                f[i] = other.getValue().floatValue();
                continue;
            }
            if (barMap.get(list_Data.get(i).getKey()) == null) {
                f[i] = 0;
                continue;
            }
            f[i] = barMap.get(list_Data.get(i).getKey()).floatValue();
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
        yAxis.setMinWidth(0);
        yAxis.setAxisMinimum(0);
        yAxis1.setAxisMinimum(0);
        yAxis1.setDrawLabels(false);
        chart_bar.setData(getBarData());


        //chart_pie set
        chart_pie.setUsePercentValues(true);
        chart_pie.setDrawHoleEnabled(true);
        chart_pie.setHoleRadius(7);
        chart_pie.setTransparentCircleRadius(10);
        chart_pie.setRotationAngle(30);
        chart_pie.setRotationEnabled(true);
        chart_pie.setDescription(Common.getDeescription());
        chart_pie.setRotationEnabled(false);


        addChartPieData();
        chart_pie.getLegend().setEnabled(false);
        Legend l = chart_bar.getLegend();
        l.setEnabled(!ShowZero);

        PieDataSet dataSet = (PieDataSet) chart_pie.getData().getDataSet();
        switch (Common.screenSize) {
            case xLarge:
                dataSet.setValueTextSize(25f);
                chart_pie.setEntryLabelTextSize(25f);
                xAxis.setTextSize(20f);
                yAxis.setTextSize(20f);
                yAxis1.setTextSize(20f);
                l.setTextSize(20f);
                l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
                l.setYEntrySpace(5f);
                l.setFormSize(20f);
                break;
            case large:
                dataSet.setValueTextSize(20f);
                chart_pie.setEntryLabelTextSize(20f);
                xAxis.setTextSize(20f);
                yAxis.setTextSize(15f);
                yAxis1.setTextSize(15f);
                l.setTextSize(15f);
                l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
                l.setYEntrySpace(5f);
                l.setFormSize(15f);
                break;
            case normal:
                dataSet.setValueTextSize(12f);
                xAxis.setTextSize(11f);
                yAxis.setTextSize(12f);
                yAxis1.setTextSize(12f);
                l.setTextSize(12f);
                l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
                l.setYEntrySpace(5f);
                l.setFormSize(12f);
                break;
        }

        chart_bar.notifyDataSetChanged();
        chart_bar.invalidate();

        chart_pie.notifyDataSetChanged();
        chart_pie.invalidate();

        chartHor.notifyDataSetChanged();
        chartHor.invalidate();
    }

    private void addChartPieData() {
        type = new ArrayList<>();
        otherMessage.setText(DesTittle);
        setCurrency.setText(Common.goalCurrencyResult(total, currencyVO.getType()));

        ArrayList<BarEntry> yHor = new ArrayList<BarEntry>();
        ArrayList<String> xHr = new ArrayList<String>();
        ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();
        ShowZero = true;
        int index = 1;
        ChartEntry other = new ChartEntry("其他", 0.0);
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
            pieEntries.add(new PieEntry(list_Data.get(i).getValue().floatValue(), list_Data.get(i).getKey()));
            yHor.add(new BarEntry(index++, list_Data.get(i).getValue().floatValue()));
            xHr.add(list_Data.get(i).getKey());
        }
        if (other.getValue() > 0) {
            type.add("其他");
            pieEntries.add(new PieEntry(other.getValue().floatValue(), "其他"));
            yHor.add(new BarEntry(index++, other.getValue().floatValue()));
            xHr.add("其他");
        }

        if (ShowZero) {
            yHor.add(new BarEntry(1, 0));
            xHr.add("   ");
            yHor.add(new BarEntry(2, 0));
            xHr.add("   ");
        }

        BarDataSet barDataSet1 = new BarDataSet(yHor, "");
        barDataSet1.setColors(Common.getColor(yHor.size()));
        barDataSet1.setStackLabels(getStackLabels());
        BarData barData = new BarData(barDataSet1);
        barData.setBarWidth(0.9f);
        barData.setDrawValues(!ShowZero);
        barData.setValueTextSize(12f);

        XAxis xHAxis = chartHor.getXAxis();
        xHAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xHAxis.setGranularity(1f);
        xHAxis.setGranularityEnabled(true);
        YAxis yHAxis = chartHor.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yHAxis1 = chartHor.getAxis(YAxis.AxisDependency.RIGHT);
        yHAxis.setAxisMinimum(0);
        yHAxis1.setAxisMinimum(0);
        yHAxis.setDrawLabels(false);
        xHAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                try {
                    return xHr.get((int) value - 1);
                } catch (Exception e) {
                    return String.valueOf(value);
                }
            }
        });


        chartHor.setFitBars(true);
        chartHor.setDrawBarShadow(false);
        chartHor.setDoubleTapToZoomEnabled(false);
        chartHor.setHighlightFullBarEnabled(false);
        chartHor.setDrawBarShadow(false);
        chartHor.setDoubleTapToZoomEnabled(false);
        chartHor.setDescription(Common.description);
        chartHor.setHighlightFullBarEnabled(false);
        chartHor.setData(barData);
        chartHor.setNoDataText("沒有資料!");
        chartHor.setNoDataTextColor(Color.BLACK);

        chartHor.getLegend().setEnabled(!ShowZero);


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
        dataSet.setSelectionShift(30);
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

    private class PieValue implements OnChartValueSelectedListener {

        View view;

        public PieValue(View view) {
            this.view = view;
        }

        @Override
        public void onValueSelected(Entry e, Highlight h) {
            if (list_Data.size() <= 0||(!oneShow)) {
                return;
            }
            if (type.size() <= 0) {
                return;
            }


            int id=view.getId();
            int index= (int) h.getX();
            if (id == R.id.chart_hor) {
                index = index - 1;
            }


            Fragment fragment = new SelectListPieIncome();
            Bundle bundle = new Bundle();
            bundle.putSerializable("year", year);
            bundle.putSerializable("month", month);
            bundle.putSerializable("day", day);
            bundle.putSerializable("statue", Statue);
            bundle.putSerializable("type", type.get(index));
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
            if (e.getY() <= 0 ||( !oneShow)) {
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

    private class choicePeriodI implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int position) {
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
    }

    private class choiceCurrency implements PopupMenu.OnMenuItemClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String title = (String) menuItem.getTitle();
            switch (title) {
                case "新台幣":
                    nowCurrency = "TWD";
                    sharedPreferences.edit().putString(choiceCurrency, nowCurrency).apply();
                    currencyVO = currencyDB.getBytimeAndType(startPopup.getTimeInMillis(), endPopup.getTimeInMillis(), nowCurrency);
                case "離開":
                    popupMenu.dismiss();
                    break;
                default:
                    nowCurrency = Common.code.get(menuItem.getItemId() - 2);
                    sharedPreferences.edit().putString(choiceCurrency, nowCurrency).apply();
                    currencyVO = currencyDB.getBytimeAndType(startPopup.getTimeInMillis(), endPopup.getTimeInMillis(), nowCurrency);
                    break;
            }
            dataAnalyze();
            return true;
        }
    }



    private class TouchView implements  View.OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            float x=motionEvent.getX();
            float y=motionEvent.getY();
            oneShow=false;
            switch (motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    lastX=x;
                    lastY=y;
                    break;
                case MotionEvent.ACTION_UP:
                    double dis = Math.sqrt(Math.abs((x-lastX)* (x-lastX)+(y-lastY)* (y-lastY)));
                    oneShow=(dis<10);
                    break;
            }
            return false;
        }
    }





}

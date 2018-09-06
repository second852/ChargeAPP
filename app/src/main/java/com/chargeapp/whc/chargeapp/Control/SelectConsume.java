package com.chargeapp.whc.chargeapp.Control;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.solver.Goal;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;

import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;


import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_BULLSEYE;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_FLAG;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_STAR_O;
import static com.chargeapp.whc.chargeapp.Control.Common.nf;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectConsume extends Fragment {


    private InvoiceDB invoiceDB;
    private CarrierDB carrierDB;
    private ConsumeDB consumeDB;
    private TextView PIdateTittle, describe;
    private ImageView PIdateCut, PIdateAdd;
    public int choiceD;
    private List<CarrierVO> carrierVOS;
    private String TAG = "SelectConsume";
    private BarChart chart_bar;
    private List<Map.Entry<String, Integer>> list_Data;
    private int month, year, day, dweek, extra;
    private BootstrapDropDown choiceCarrier, choicePeriod;
    private PieChart chart_pie;
    private int total, period;
    private String DesTittle;
    private boolean ShowConsume = true;
    private boolean ShowAllCarrier = true;
    private boolean noShowCarrier = false;
    private List<String> chartLabels;
    private boolean ShowZero;
    private Set<String> OKey;
    private XAxis xAxis;
    private int week;
    private GoalDB goalDB;
    private GoalVO goalVO;
    private int Max;
    public static int Statue;
    public static Calendar end;
    public static int CStatue;
    private Activity context;
    private AwesomeTextView goalConsume;
    private List<BootstrapText> carrierTexts;
    private List<BootstrapText> periodTexts;



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
        Common.setScreen(Common.screenSize, getResources().getDisplayMetrics());
        final View view = inflater.inflate(R.layout.select_consume, container, false);
        if (end == null ) {
            end = Calendar.getInstance();
            SelectConsume.Statue = 1;
        }


        month = end.get(Calendar.MONTH);
        year = end.get(Calendar.YEAR);
        day = end.get(Calendar.DAY_OF_MONTH);
        dweek = end.get(Calendar.DAY_OF_WEEK);
        //設定dweek period
        switch (Statue)
        {
            case 0:
                period = 1;
                break;
            case 1:
                if (week == 1) {
                    dweek = 1;
                }
                period = 7 + extra;
                extra = 0;
                week = 0;
                break;
            case 2:
                period = end.getActualMaximum(Calendar.WEEK_OF_MONTH);
                break;
            case 3:
                period=12;
                month=0;
                break;
        }



        //載具
        switch (CStatue)
        {
            case 0:
                ShowConsume = true;
                ShowAllCarrier = true;
                noShowCarrier = false;
                break;
            case 1:
                ShowConsume = true;
                ShowAllCarrier = false;
                noShowCarrier = true;
                break;
            case 2:
                choiceD = CStatue - 2;
                ShowConsume = false;
                ShowAllCarrier = false;
                noShowCarrier = false;
                break;
        }

        setDB();
        findViewById(view);
        PIdateAdd.setOnClickListener(new AddOnClick());
        PIdateCut.setOnClickListener(new CutOnClick());

        chart_bar.setOnChartValueSelectedListener(new charvalue());
        chart_pie.setOnChartValueSelectedListener(new pievalue());
        goalVO = goalDB.getFindType("支出");

        dataAnalyze();
        //        choicePeriod.setOnItemSelectedListener(new ChoicePeriodStatue());
//        choiceCarrier.setOnItemSelectedListener(new ChoiceCarrier());
        //        choiceCarrier.setSelection(CStatue);
        //        choicePeriod.setSelection(Statue);
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                choiceCarrier.setBootstrapText(carrierTexts.get(CStatue));
                choiceCarrier.setShowOutline(false);
                choicePeriod.setBootstrapText(periodTexts.get(Statue));
                choicePeriod.setShowOutline(false);
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        return view;
    }


    private void setGoalVO() {
        Max = 0;
        choicePeriod.setBootstrapText(periodTexts.get(Statue));
        choicePeriod.setShowOutline(false);
        if (goalVO != null) {
            String goalTimeStatue = goalVO.getTimeStatue().trim();
            if (goalTimeStatue.equals("每天") && Statue == 0) {
                Max = goalVO.getMoney();
            } else if (goalTimeStatue.equals("每天") && Statue == 1) {
                Max = goalVO.getMoney();
            } else if (goalTimeStatue.equals("每周") && Statue == 2) {
                Max = goalVO.getMoney();
            } else if (goalTimeStatue.equals("每月") && Statue == 3) {
                Max = goalVO.getMoney();
            }
        }
    }


    private void setDB() {
        Common.setChargeDB(context);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        goalDB = new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
    }


    private void findViewById(View view) {
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        chart_bar = view.findViewById(R.id.chart_bar);
        choicePeriod = view.findViewById(R.id.choicePeriod);
        choiceCarrier = view.findViewById(R.id.choiceCarrier);
        chart_pie = view.findViewById(R.id.chart_pie);
        describe = view.findViewById(R.id.describe);
        goalConsume = view.findViewById(R.id.goalConsume);
        ArrayList<String> SpinnerItem1 = new ArrayList<>();
        SpinnerItem1.add("  日  ");
        SpinnerItem1.add("  周  ");
        SpinnerItem1.add("  月  ");
        SpinnerItem1.add("  年  ");

        periodTexts = new ArrayList<>();
        for (String s : SpinnerItem1) {
            periodTexts.add(Common.setPeriodSelectCBsTest(context, s));
        }
        choicePeriod.setDropdownData(SpinnerItem1.toArray(new String[0]));
        choicePeriod.setOnDropDownItemClickListener(new choicePeriodD());

//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinneritem, SpinnerItem1);
//        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
//        choicePeriod.setAdapter(arrayAdapter);
        carrierVOS = carrierDB.getAll();
        if (carrierVOS == null || carrierVOS.size() <= 0) {
            ShowAllCarrier = false;
        }

        //設定下拉選單

        List<String> carrierS = new ArrayList<>();
        carrierS.add("全部");
        carrierS.add("本地");
        carrierTexts = new ArrayList<>();
        carrierTexts.add(Common.setCarrierSetBsTest(context, "全部"));
        carrierTexts.add(Common.setCarrierSetBsTest(context, "本地"));
        for (CarrierVO c : carrierVOS) {
            carrierTexts.add(Common.setCarrierSetBsTest(context, c.getCarNul()));
            carrierS.add(c.getCarNul());
        }
        choiceCarrier.setDropdownData(carrierS.toArray(new String[0]));
        choiceCarrier.setOnDropDownItemClickListener(new choiceCarrierOnClick());
//        choiceCarrier.setAdapter(arrayAdapter);
    }


    private BarData getBarData() {
        String[] stack = getStackLabels();
        BarDataSet dataSetA = new BarDataSet(getChartData(), " ");
        dataSetA.setColors(Common.getColor(stack.length));
        dataSetA.setStackLabels(stack);
        dataSetA.setDrawValues(false);
        BarData barData = new BarData(dataSetA);
        barData.setBarWidth(0.9f);
        barData.notifyDataChanged();
        return barData;
    }


    private String[] getStackLabels() {
        String[] s = new String[list_Data.size()];
        for (int i = 0; i < list_Data.size(); i++) {
            s[i] = list_Data.get(i).getKey();
        }
        return s;
    }

    private void findMaxFive() {
        total = 0;
        OKey = new HashSet<>();
        HashMap<String, Integer> hashMap = new HashMap<>();
        Calendar start, end;
        ChartEntry other = new ChartEntry("其他", 0);
        if (Statue == 0) {
            DesTittle = "當天花費";
            start = new GregorianCalendar(year, month, day, 0, 0, 0);
            end = new GregorianCalendar(year, month, day, 23, 59, 59);
            PIdateTittle.setText(Common.sOne.format(new Date(start.getTimeInMillis())));
        } else if (Statue == 1) {
            DesTittle = "這周花費";
            start = new GregorianCalendar(year, month, day - dweek + 1, 0, 0, 0);
            end = new GregorianCalendar(year, month, day - dweek + 1 + period - 1, 23, 59, 59);
            PIdateTittle.setText(Common.sTwo.format(new Date(start.getTimeInMillis())) + " ~ " + Common.sTwo.format(new Date(end.getTimeInMillis())));
        } else if (Statue == 2) {
            DesTittle = "本月花費";
            start = new GregorianCalendar(year, month, 1, 0, 0, 0);
            end = new GregorianCalendar(year, month, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            PIdateTittle.setText(Common.sThree.format(new Date(start.getTimeInMillis())));
        } else {
            DesTittle = "本年花費";
            start = new GregorianCalendar(year, 0, 1, 0, 0, 0);
            end = new GregorianCalendar(year, 11, 31, 23, 59, 59);
            PIdateTittle.setText(Common.sFour.format(new Date(start.getTimeInMillis())));
        }

        if (!noShowCarrier && carrierVOS.size() > 0) {
            List<InvoiceVO> invoiceVOS;
            if (ShowAllCarrier) {
                invoiceVOS = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
            } else {
                if(CStatue>=carrierVOS.size())
                {
                    CStatue=carrierVOS.size()-1;
                }
                if(CStatue<0)
                {
                    CStatue=0;
                }
                invoiceVOS = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), carrierVOS.get(choiceD).getCarNul());
            }
            for (InvoiceVO I : invoiceVOS) {
                if (I.getMaintype().equals("0") || I.getMaintype().equals("O")) {
                    other.setValue(other.getValue() + Integer.valueOf(I.getAmount()));
                    OKey.add(I.getMaintype());
                    continue;
                }
                if (hashMap.get(I.getMaintype()) == null) {
                    hashMap.put(I.getMaintype(), Integer.valueOf(I.getAmount()));
                } else {
                    hashMap.put(I.getMaintype(), Integer.valueOf(I.getAmount()) + hashMap.get(I.getMaintype()));
                }
                total = total + Integer.valueOf(I.getAmount());
            }
        }
        total = total + other.getValue();


        if (ShowConsume) {
            List<ConsumeVO> consumeVOS = consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
            for (ConsumeVO c : consumeVOS) {
                if (hashMap.get(c.getMaintype()) == null) {
                    hashMap.put(c.getMaintype(), Integer.valueOf(c.getMoney()));
                } else {
                    hashMap.put(c.getMaintype(), Integer.valueOf(c.getMoney()) + hashMap.get(c.getMaintype()));
                }
                total = total + Integer.valueOf(c.getMoney());
            }
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
                OKey.add(list_Data.get(i).getKey());
                other.setValue(other.getValue() + list_Data.get(i).getValue());
                list_Data.remove(i);
                i--;
                Log.d(TAG, i + " : " + list_Data.size());
                continue;
            }
            if (i >= 4) {
                other.setValue(other.getValue() + list_Data.get(4).getValue());
                OKey.add(list_Data.get(4).getKey());
                Log.d(TAG, list_Data.get(4).getKey());
                list_Data.remove(4);
                Log.d(TAG, "over" + i + " : " + list_Data.size());
                i--;
            }
        }
        list_Data.add(other);
    }

    private List<String> getLabels() {
        chartLabels = new ArrayList<>();
        Calendar time;
        if (Statue == 0) {
            time = new GregorianCalendar(year, month, day, 0, 0, 0);
            chartLabels.add(Common.sDay.format(new Date(time.getTimeInMillis())));
        } else if (Statue == 1) {
            for (int i = 0; i < period; i++) {
                time = new GregorianCalendar(year, month, day - dweek + 1 + i);
                chartLabels.add(Common.sDay.format(new Date(time.getTimeInMillis())));
            }
        } else if (Statue == 2) {
            for (int i = 0; i < period; i++) {
                chartLabels.add("第" + (i + 1) + "周");
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
        if (Statue == 0) {
            start = new GregorianCalendar(year, month, day, 0, 0, 0);
            end = new GregorianCalendar(year, month, day, 23, 59, 59);
            BarEntry barEntry = new BarEntry(0, Periodfloat(start, end));
            chartData.add(barEntry);
            setGoalVO();
        } else if (Statue == 1) {
            setGoalVO();
            for (int i = 0; i < period; i++) {
                start = new GregorianCalendar(year, month, day - dweek + 1 + i, 0, 0, 0);
                end = new GregorianCalendar(year, month, day - dweek + 1 + i, 23, 59, 59);
                Log.d(TAG, "start" + Common.sDay.format(new Date(start.getTimeInMillis())));
                BarEntry barEntry = new BarEntry(i, Periodfloat(start, end));
                chartData.add(barEntry);
            }
        } else if (Statue == 2) {
            Calendar calendar = new GregorianCalendar(year, month, 1, 0, 0, 0);
            start = new GregorianCalendar(year, month, 1, 0, 0, 0);
            period = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
            setGoalVO();
            calendar.set(Calendar.WEEK_OF_MONTH, 1);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            end = new GregorianCalendar(year, month, calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
            BarEntry barEntry;
            for (int i = 2; i < period; i++) {
                barEntry = new BarEntry(i - 2, Periodfloat(start, end));
                chartData.add(barEntry);
                Log.d(TAG, "week " + String.valueOf(i - 2) + ":" + Common.sDay.format(new Date(start.getTimeInMillis())) + "~" + Common.sDay.format(new Date(end.getTimeInMillis())));
                calendar.set(Calendar.WEEK_OF_MONTH, i);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                start = new GregorianCalendar(year, month, calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                calendar.set(Calendar.WEEK_OF_MONTH, i);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                end = new GregorianCalendar(year, month, calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
            }
            barEntry = new BarEntry(period - 2, Periodfloat(start, end));
            chartData.add(barEntry);
            Log.d(TAG, "week " + String.valueOf(period - 2) + ":" + Common.sDay.format(new Date(start.getTimeInMillis())) + "~" + Common.sDay.format(new Date(end.getTimeInMillis())));
            calendar.set(Calendar.WEEK_OF_MONTH, period);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            start = new GregorianCalendar(year, month, calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            end = new GregorianCalendar(year, month, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            barEntry = new BarEntry(period - 1, Periodfloat(start, end));
            chartData.add(barEntry);
            Log.d(TAG, "week " + String.valueOf(period - 1) + ":" + Common.sDay.format(new Date(start.getTimeInMillis())) + "~" + Common.sDay.format(new Date(end.getTimeInMillis())));
        } else {
            setGoalVO();
            for (int i = 0; i < period; i++) {
                start = new GregorianCalendar(year, month + i, 1, 0, 0, 0);
                end = new GregorianCalendar(year, month + i, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
                BarEntry barEntry = new BarEntry(i, Periodfloat(start, end));
                chartData.add(barEntry);
            }
        }
        return chartData;
    }

    private float[] Periodfloat(Calendar start, Calendar end) {
        Map<String, Integer> hashMap = new LinkedHashMap<>();
        boolean isOther;
        float[] f = new float[list_Data.size()];
        ChartEntry other = new ChartEntry("其他", 0);
        if (!noShowCarrier && carrierVOS.size() > 0) {
            if(CStatue>=carrierVOS.size())
            {
                CStatue=carrierVOS.size()-1;
            }
            if(CStatue<0)
            {
                CStatue=0;
            }
            String carrier = carrierVOS.get(choiceD).getCarNul();
            List<InvoiceVO> periodInvoice;
            if (ShowAllCarrier) {
                periodInvoice = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
            } else {
                periodInvoice = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), carrier);
            }
            for (InvoiceVO I : periodInvoice) {
                isOther = true;
                for (Map.Entry e : list_Data) {
                    if (I.getMaintype().equals(e.getKey())) {
                        if (hashMap.get(I.getMaintype()) == null) {
                            hashMap.put(I.getMaintype(), Integer.valueOf(I.getAmount()));
                        } else {
                            hashMap.put(I.getMaintype(), Integer.valueOf(I.getAmount()) + hashMap.get(I.getMaintype()));
                        }
                        isOther = false;
                        break;
                    }
                }
                if (isOther) {
                    other.setValue(other.getValue() + Integer.valueOf(I.getAmount()));
                }
            }
        }
        if (ShowConsume) {
            List<ConsumeVO> periodConsume = consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
            for (ConsumeVO c : periodConsume) {
                isOther = true;
                for (Map.Entry e : list_Data) {
                    if (c.getMaintype().equals(e.getKey())) {
                        if (hashMap.get(c.getMaintype()) == null) {
                            hashMap.put(c.getMaintype(), Integer.valueOf(c.getMoney()));
                        } else {
                            hashMap.put(c.getMaintype(), Integer.valueOf(c.getMoney()) + hashMap.get(c.getMaintype()));
                        }
                        isOther = false;
                        break;
                    }
                }
                if (isOther) {
                    other.setValue(other.getValue() + Integer.valueOf(c.getMoney()));
                }
            }
        }

        for (int i = 0; i < list_Data.size(); i++) {
            if (list_Data.get(i).getKey().equals("其他")) {
                f[i] = other.getValue();
                continue;
            }
            if (hashMap.get(list_Data.get(i).getKey()) == null) {
                f[i] = 0;
                continue;
            }
            f[i] = hashMap.get(list_Data.get(i).getKey());
        }
        return f;
    }


    public void dataAnalyze() {
        chart_bar.clear();
        findMaxFive();
        if (list_Data.size() <= 0) {
            return;
        }
        chart_bar.setDrawGridBackground(false);
        chart_bar.setDragEnabled(true);
        chart_bar.setScaleEnabled(false);
        chart_bar.setEnabled(true);
        xAxis = chart_bar.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        YAxis yAxis = chart_bar.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yAxis1 = chart_bar.getAxis(YAxis.AxisDependency.RIGHT);
        yAxis1.setAxisMinimum(0);
        yAxis.setAxisMinimum(0);
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
        chart_bar.setFitBars(true);
        chart_bar.setDrawBarShadow(false);
        chart_bar.setDoubleTapToZoomEnabled(false);
        chart_bar.setDescription(Common.description);
        chart_bar.setHighlightFullBarEnabled(false);
        if (goalVO != null && Max != 0) {
            BootstrapText bootstrapText = new BootstrapText.Builder(context)
                    .addFontAwesomeIcon(FA_FLAG)
                    .addText(" 目標 : " + goalVO.getName() + goalVO.getTimeStatue() + goalVO.getType() + goalVO.getMoney() + "元")
                    .build();
            goalConsume.setBootstrapText(bootstrapText);
            yAxis.removeAllLimitLines();
            LimitLine yLimitLine = new LimitLine(Max, "支出目標");
            yLimitLine.setLineColor(Color.parseColor("#007bff"));
            yLimitLine.setTextColor(Color.parseColor("#007bff"));
            yAxis.addLimitLine(yLimitLine);
        } else {
            goalConsume.setText("");
            yAxis.removeAllLimitLines();
        }


        //chart_pie
        chart_pie.setEntryLabelColor(Color.BLACK);
        chart_pie.setUsePercentValues(true);
        chart_pie.setDrawHoleEnabled(true);
        chart_pie.setHoleRadius(7);
        chart_pie.setTransparentCircleRadius(10);
        chart_pie.setRotationAngle(30);
        chart_pie.setRotationEnabled(false);
        chart_pie.setDescription(Common.description);
        addChartPieData();
        // customize legends
        Legend l = chart_pie.getLegend();
        l.setEnabled(false);
    }

    private void addChartPieData() {
        describe.setText(DesTittle + nf.format(total) + "元");
        final ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();
        ShowZero = true;
        for (int i = 0; i < list_Data.size(); i++) {
            if (list_Data.get(i).getValue() > 0) {
                ShowZero = false;
                yVals1.add(new PieEntry(list_Data.get(i).getValue(), list_Data.get(i).getKey()));
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
        dataSet.setSelectionShift(30f);
        dataSet.notifyDataSetChanged();
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextColor(Color.BLACK);
        chart_pie.setData(data);
        chart_pie.setBackgroundColor(Color.parseColor("#f5f5f5"));
        YAxis yAxis = chart_bar.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yAxis1 = chart_bar.getAxis(YAxis.AxisDependency.RIGHT);
        Legend l = chart_bar.getLegend();


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
    }


    private class AddOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (Statue == 0) {
                day = day + 1;
                end = new GregorianCalendar(year, month, day);
            } else if (Statue == 1) {
                day = day + 7;
                period = 7;
                end = new GregorianCalendar(year, month, day);
                dweek = end.get(Calendar.DAY_OF_WEEK);
            } else if (Statue == 2) {
                month = month + 1;
                end = new GregorianCalendar(year, month, day);
                period = end.getActualMaximum(Calendar.WEEK_OF_MONTH);
            } else {
                year = year + 1;
                end = new GregorianCalendar(year, month, day);
                period = 12;
            }
            dataAnalyze();
        }
    }

    private class CutOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (Statue == 0) {
                day = day - 1;
                end = new GregorianCalendar(year, month, day);
            } else if (Statue == 1) {
                day = day - 7;
                period = 7;
                end = new GregorianCalendar(year, month, day);
                dweek = end.get(Calendar.DAY_OF_WEEK);
            } else if (Statue == 2) {
                month = month - 1;
                end = new GregorianCalendar(year, month, day);
                period = end.getActualMaximum(Calendar.WEEK_OF_MONTH);
            } else {
                year = year - 1;
                end = new GregorianCalendar(year, month, day);
                period = 12;
            }
            dataAnalyze();
        }
    }


    private class ChoicePeriodStatue implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Statue = position;
            end = new GregorianCalendar(year, month, day);
            month = end.get(Calendar.MONTH);
            year = end.get(Calendar.YEAR);
            dweek = end.get(Calendar.DAY_OF_WEEK);
            day = end.get(Calendar.DAY_OF_MONTH);
            Log.d(TAG, "day" + Common.sDay.format(new Date(end.getTimeInMillis())) + " : " + dweek);
            if (position == 0) {
                period = 1;
            } else if (position == 1) {
                if (week == 1) {
                    dweek = 1;
                }
                period = 7 + extra;
                extra = 0;
                week = 0;
            } else if (position == 2) {
                period = end.getActualMaximum(Calendar.WEEK_OF_MONTH);
            } else {
                month = 0;
                period = 12;
            }
            dataAnalyze();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class ChoiceCarrier implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            CStatue = i;
            if (i == 0) {
                ShowConsume = true;
                ShowAllCarrier = true;
                noShowCarrier = false;
                dataAnalyze();
            } else if (i == 1) {
                ShowConsume = true;
                ShowAllCarrier = false;
                noShowCarrier = true;
                dataAnalyze();
            } else {
                choiceD = i - 2;
                ShowConsume = false;
                ShowAllCarrier = false;
                noShowCarrier = false;
                dataAnalyze();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class charvalue implements OnChartValueSelectedListener {
        @Override
        public void onValueSelected(Entry e, Highlight h) {
            if (e.getY() <= 0) {
                return;
            }
            if (Statue == 2) {
                week = (int) (e.getX() + 1);
                Statue = 1;
                if (week == 1) {
                    Calendar calendar = new GregorianCalendar(year, month, 1, 0, 0, 0);
                    dweek=1;
                    day = 1;
                    period=7-calendar.get(Calendar.DAY_OF_WEEK)+1;
                } else if (week == period) {
                    Calendar calendar = new GregorianCalendar(year, month, 1);
                    calendar.set(Calendar.WEEK_OF_MONTH, week);
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    period = calendar.getMaximum(Calendar.DAY_OF_MONTH) - day+1;
                    dweek=1;
                } else {
                    period=7;
                    Calendar calendar = new GregorianCalendar(year, month, 1);
                    calendar.set(Calendar.WEEK_OF_MONTH, week);
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    dweek=1;
                    extra = 0;
                }
                dataAnalyze();
            } else if (Statue == 3) {
                Statue = 2;
                month = (int) e.getX();
                dataAnalyze();
            } else {
                Fragment fragment = new SelectDetCircle();
                Bundle bundle = new Bundle();
                bundle.putSerializable("ShowConsume", ShowConsume);
                bundle.putSerializable("ShowAllCarrier", ShowAllCarrier);
                bundle.putSerializable("noShowCarrier", noShowCarrier);
                bundle.putSerializable("year", year);
                bundle.putSerializable("month", month);
                bundle.putSerializable("day", day);
                bundle.putSerializable("index", (int) e.getX());
                bundle.putSerializable("carrier", choiceD);
                bundle.putSerializable("total", (int) e.getY());
                bundle.putSerializable("period", period);
                bundle.putSerializable("dweek", dweek);
                bundle.putSerializable("statue", Statue);
                fragment.setArguments(bundle);
                switchFragment(fragment);
            }
        }

        @Override
        public void onNothingSelected() {

        }

    }

    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("SelectConsume");
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
            if (ShowZero) {
                return;
            }
            String key = list_Data.get((int) h.getX()).getKey();
            Bundle bundle = new Bundle();
            Fragment fragment;
            if (key.equals("其他")) {
                fragment = new SelectOtherCircle();
                ArrayList<String> s = new ArrayList<>();
                s.addAll(OKey);
                bundle.putStringArrayList("OKey", s);
                bundle.putSerializable("total", (int) h.getY());
            } else {
                fragment = new SelectShowCircleDe();
            }
            bundle.putSerializable("ShowConsume", ShowConsume);
            bundle.putSerializable("ShowAllCarrier", ShowAllCarrier);
            bundle.putSerializable("noShowCarrier", noShowCarrier);
            bundle.putSerializable("year", year);
            bundle.putSerializable("month", month);
            bundle.putSerializable("day", day);
            bundle.putSerializable("index", list_Data.get((int) h.getX()).getKey());
            bundle.putSerializable("carrier", choiceD);
            bundle.putSerializable("statue", Statue);
            bundle.putSerializable("period", period);
            bundle.putSerializable("dweek", dweek);
            bundle.putSerializable("position", 0);
            fragment.setArguments(bundle);
            switchFragment(fragment);
        }

        @Override
        public void onNothingSelected() {

        }
    }

    private class choiceCarrierOnClick implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int i) {
            choiceCarrier.setBootstrapText(carrierTexts.get(i));
            choiceCarrier.setShowOutline(false);
            CStatue = i;
            if (i == 0) {
                ShowConsume = true;
                ShowAllCarrier = true;
                noShowCarrier = false;
                dataAnalyze();
            } else if (i == 1) {
                ShowConsume = true;
                ShowAllCarrier = false;
                noShowCarrier = true;
                dataAnalyze();
            } else {
                choiceD = i - 2;
                ShowConsume = false;
                ShowAllCarrier = false;
                noShowCarrier = false;
                dataAnalyze();
            }
        }
    }

    private class choicePeriodD implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int position) {
            Statue = position;
            end = new GregorianCalendar(year, month, day);
            month = end.get(Calendar.MONTH);
            year = end.get(Calendar.YEAR);
            dweek = end.get(Calendar.DAY_OF_WEEK);
            day = end.get(Calendar.DAY_OF_MONTH);
            Log.d(TAG, "day" + Common.sDay.format(new Date(end.getTimeInMillis())) + " : " + dweek);
            if (position == 0) {
                period = 1;
            } else if (position == 1) {
                if (week == 1) {
                    dweek = 1;
                }
                period = 7 + extra;
                extra = 0;
                week = 0;
            } else if (position == 2) {
                period = end.getActualMaximum(Calendar.WEEK_OF_MONTH);
            } else {
                month = 0;
                period = 12;
            }
            dataAnalyze();
        }
    }
}

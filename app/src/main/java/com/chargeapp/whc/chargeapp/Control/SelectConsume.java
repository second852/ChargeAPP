package com.chargeapp.whc.chargeapp.Control;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.solver.Goal;
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
import java.text.SimpleDateFormat;
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


import com.github.mikephil.charting.listener.OnChartValueSelectedListener;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectConsume extends Fragment {


    private InvoiceDB invoiceDB;
    private CarrierDB carrierDB;
    private ConsumeDB consumeDB;
    private ProgressDialog progressDialog;
    private static AsyncTask first = null;
    private TextView PIdateTittle, describe;
    private ImageView PIdateCut, PIdateAdd;
    private int choiceD = 0;
    private List<CarrierVO> carrierVOS;
    private String TAG = "SelectConsume";
    private BarChart chart_bar;
    private TypeDB typeDB;
    private List<TypeVO> typeList;
    private List<Map.Entry<String, Integer>> list_Data;
    private int month, year,day,dweek,extra;
    private Calendar end;
    private Spinner choicePeriod, choiceCarrier;
    private PieChart chart_pie;
    private TypeDetailDB typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private int total, period;
    private int Statue = 1;
    private String DesTittle;
    private boolean ShowConsume = true;
    private boolean ShowAllCarrier = true;
    private boolean noShowCarrier = false;
    private List<String> chartLabels;
    private boolean ShowZero;
    private ArrayList<String> OKey;
    private XAxis xAxis;
    private int week;
    private GoalDB goalDB;
    private GoalVO goalVO;
    private int Max;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_consume, container, false);
        end = Calendar.getInstance();
        month = end.get(Calendar.MONTH);
        year = end.get(Calendar.YEAR);
        dweek = end.get(Calendar.DAY_OF_WEEK);
        day = end.get(Calendar.DAY_OF_MONTH);
        setDB();
        findViewById(view);
        typeList = typeDB.getAll();
        progressDialog = new ProgressDialog(getActivity());
        PIdateAdd.setOnClickListener(new AddOnClick());
        PIdateCut.setOnClickListener(new CutOnClick());
        choicePeriod.setOnItemSelectedListener(new ChoicePeriodStatue());
        choiceCarrier.setOnItemSelectedListener(new ChoiceCarrier());
        chart_bar.setOnChartValueSelectedListener(new charvalue());
        chart_pie.setOnChartValueSelectedListener(new pievalue());
        goalVO=goalDB.getFindType("支出");
        return view;
    }

    private void setGoalVO(Calendar month) {
        if(goalVO!=null)
        {
            String goalTimeStatue=goalVO.getTimeStatue().trim();
            if(goalTimeStatue.equals("每天"))
            {
                Max= goalVO.getMoney();
            }else if(goalTimeStatue.equals("每周"))
            {
                Max= goalVO.getMoney()/7;
            }else if(goalTimeStatue.equals("每月"))
            {
                int day=month.getActualMaximum(Calendar.DAY_OF_MONTH);
                Max= goalVO.getMoney()/day;
            }else if(goalTimeStatue.equals("每年"))
            {
                Max= goalVO.getMoney()/365;
            }
        }
    }


    private void setDB() {
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        goalDB=new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
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
        ArrayList<String> SpinnerItem1 = new ArrayList<>();
        SpinnerItem1.add("  日  ");
        SpinnerItem1.add("  周  ");
        SpinnerItem1.add("  月  ");
        SpinnerItem1.add("  年  ");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinneritem, SpinnerItem1);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        ArrayList<String> SpinnerItem2 = new ArrayList<>();
        choicePeriod.setAdapter(arrayAdapter);
        choicePeriod.setSelection(1);
        carrierVOS = carrierDB.getAll();
        if (carrierVOS == null || carrierVOS.size() <= 0) {
            ShowAllCarrier = false;
        }
        SpinnerItem2.add(" 全部 ");
        SpinnerItem2.add(" 本地 ");
        for (CarrierVO c : carrierVOS) {
            SpinnerItem2.add(" " + c.getCarNul() + " ");
        }
        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinneritem, SpinnerItem2);
        choiceCarrier.setAdapter(arrayAdapter);
    }


    private BarData getBarData() {
        String [] stack=getStackLabels();
        BarDataSet dataSetA = new BarDataSet(getChartData(), " ");
        dataSetA.setColors(Common.getColor(stack.length));
        dataSetA.setStackLabels(stack);
        dataSetA.setDrawValues(false);
        BarData barData = new BarData(dataSetA);
        barData.setBarWidth(0.9f);
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
        OKey = new ArrayList<>();
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
            start = new GregorianCalendar(year, month, day - dweek + 1 , 0, 0, 0);
            end = new GregorianCalendar(year, month, day - dweek + 1 + period-1, 23, 59, 59);
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

        if (!noShowCarrier&&carrierVOS.size()>0) {
            List<InvoiceVO> invoiceVOS;
            if (ShowAllCarrier) {
                invoiceVOS = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
            } else {
                invoiceVOS = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), carrierVOS.get(choiceD).getCarNul());
            }
            for (InvoiceVO I : invoiceVOS) {
                if (I.getMaintype().equals("O")) {
                    other.setValue(other.getValue() + Integer.valueOf(I.getAmount()));
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
                Log.d(TAG,i+" : "+list_Data.size());
                continue;
            }
            if (i >= 4) {
                other.setValue(other.getValue() + list_Data.get(4).getValue());
                OKey.add(list_Data.get(4).getKey());
                Log.d(TAG, list_Data.get(4).getKey());
                list_Data.remove(4);
                Log.d(TAG,"over"+i+" : "+list_Data.size());
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
            BarEntry barEntry = new BarEntry(0, Periodfloat(start,end));
            chartData.add(barEntry);
            setGoalVO(start);
        } else if (Statue == 1) {
            start = new GregorianCalendar(year, month, day - dweek + 1, 0, 0, 0);
            setGoalVO(start);
            for (int i = 0; i < period; i++) {
                start = new GregorianCalendar(year, month, day - dweek + 1 + i, 0, 0, 0);
                end = new GregorianCalendar(year, month, day - dweek + 1 + i, 23, 59, 59);
                Log.d(TAG,"start"+Common.sDay.format(new Date(start.getTimeInMillis())));
                BarEntry barEntry = new BarEntry(i, Periodfloat(start,end));
                chartData.add(barEntry);
            }
        } else if (Statue == 2) {
            Calendar calendar = new GregorianCalendar(year, month, 1, 0, 0, 0);
            start = new GregorianCalendar(year, month, 1, 0, 0, 0);
            setGoalVO(start);
            Max=Max*7;
            calendar.set(Calendar.WEEK_OF_MONTH, 1);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            end = new GregorianCalendar(year, month, calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
            BarEntry barEntry;
            for (int i = 2; i < period; i++) {
                barEntry = new BarEntry(i - 2, Periodfloat(start,end));
                chartData.add(barEntry);
                Log.d(TAG,"week "+String.valueOf(i - 2)+":"+Common.sDay.format(new Date(start.getTimeInMillis()))+"~"+Common.sDay.format(new Date(end.getTimeInMillis())));
                calendar.set(Calendar.WEEK_OF_MONTH, i);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                start = new GregorianCalendar(year, month, calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                calendar.set(Calendar.WEEK_OF_MONTH, i);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                end = new GregorianCalendar(year, month, calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
            }
            barEntry = new BarEntry(period - 2, Periodfloat(start, end));
            chartData.add(barEntry);
            Log.d(TAG,"week "+String.valueOf(period - 2)+":"+Common.sDay.format(new Date(start.getTimeInMillis()))+"~"+Common.sDay.format(new Date(end.getTimeInMillis())));
            calendar.set(Calendar.WEEK_OF_MONTH, period);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            start = new GregorianCalendar(year, month, calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            end = new GregorianCalendar(year, month, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            barEntry = new BarEntry(period - 1, Periodfloat(start, end));
            chartData.add(barEntry);
            Log.d(TAG,"week "+String.valueOf(period - 1)+":"+Common.sDay.format(new Date(start.getTimeInMillis()))+"~"+Common.sDay.format(new Date(end.getTimeInMillis())));
        } else {
            start = new GregorianCalendar(year, month , 1, 0, 0, 0);
            setGoalVO(start);
            Max=Max*365/12;
            for (int i = 0; i < period; i++) {
                start = new GregorianCalendar(year, month + i, 1, 0, 0, 0);
                end = new GregorianCalendar(year, month + i, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
                BarEntry barEntry = new BarEntry(i, Periodfloat(start,end));
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
        if (!noShowCarrier&&carrierVOS.size()>0) {
            String carrier=carrierVOS.get(choiceD).getCarNul();
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


    private void download() {
        List<CarrierVO> carrierVOList = carrierDB.getAll();
        if (carrierVOList == null || carrierVOList.size() <= 0) {
            return;
        }
        if (first == null) {
            first = new GetSQLDate(this).execute("GetToday");
            progressDialog.setMessage("正在更新資料,請稍候...");
            progressDialog.show();
        } else {
            dataAnalyze();
        }
    }


    private InvoiceVO getType(InvoiceVO invoiceVO) {
        List<TypeDetailVO> typeDetailVOS = typeDetailDB.getTypdAll();
        String main = "O", second = "O";
        int x = 0, total = 0;
        for (TypeDetailVO t : typeDetailVOS) {
            x = 0;
            String[] key = t.getKeyword().split(" ");
            for (int i = 0; i < key.length; i++) {
                if (invoiceVO.getDetail().indexOf(key[i].trim()) != -1) {
                    x = x + key[i].length();
                }
            }
            if (x > total) {
                total = x;
                main = t.getGroupNumber();
                second = t.getName();
            }
        }
        if (second.indexOf("餐") != -1) {
            int hour = Integer.valueOf(Common.sHour.format(new Date(invoiceVO.getTime().getTime())));
            if (hour > 0 && hour < 11) {
                second = "早餐";
            } else if (hour >= 11 && hour < 18) {
                second = "午餐";
            } else {
                second = "晚餐";
            }
        }
        invoiceVO.setMaintype(main);
        invoiceVO.setSecondtype(second);
        invoiceDB.update(invoiceVO);
        Log.d(TAG, invoiceVO.getInvNum() + " : " + main + " : " + second);
        return invoiceVO;
    }

    public void getAllInvoiceDetail() {
        new GetSQLDate(this).execute("GetAllInvoice");
    }

    public void dataAnalyze() {
        chart_bar.clear();
        findMaxFive();
        Description description = new Description();
        description.setText(" ");
        if(list_Data.size()<=0)
        {
            return;
        }
        chart_bar.setDrawGridBackground(false);
        chart_bar.setDragEnabled(true);
        chart_bar.setScaleEnabled(true);
        chart_bar.setEnabled(true);
        xAxis = chart_bar.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
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
        chart_bar.setDescription(description);
        chart_bar.setHighlightFullBarEnabled(false);
        if(goalVO!=null)
        {
            yAxis.removeAllLimitLines();
            LimitLine yLimitLine = new LimitLine(Max,"支出目標");
            yLimitLine.setLineColor(Color.parseColor("#191970"));
            yLimitLine.setTextColor(Color.parseColor("#191970"));
            yAxis.addLimitLine(yLimitLine);
        }
        chart_bar.invalidate();
        chart_pie.setEntryLabelColor(Color.BLACK);
        chart_pie.setUsePercentValues(true);
        chart_pie.setDrawHoleEnabled(true);
        chart_pie.setHoleRadius(7);
        chart_pie.setTransparentCircleRadius(10);
        chart_pie.setRotationAngle(30);
        chart_pie.setRotationEnabled(false);
        chart_pie.setDescription(description);
        addData();
        // customize legends
        Legend l = chart_pie.getLegend();
        l.setEnabled(false);
    }

    private void addData() {
        describe.setText(DesTittle + total + "元");
        ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();
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
        dataSet.setSelectionShift(20f);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        chart_pie.setData(data);
        chart_pie.invalidate();
        chart_pie.setBackgroundColor(Color.parseColor("#f5f5f5"));
    }


    private class AddOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (Statue == 0) {
                day = day + 1;
            } else if (Statue == 1) {
                day = day + 7;
                period=7;
                end = new GregorianCalendar(year,month,day);
                dweek=end.get(Calendar.DAY_OF_WEEK);
            } else if (Statue == 2) {
                month = month + 1;
                end = new GregorianCalendar(year,month,day);
                period=end.getActualMaximum(Calendar.WEEK_OF_MONTH);
            } else {
                year = year + 1;
                period=12;
            }
            dataAnalyze();
        }
    }

    private class CutOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (Statue == 0) {
                day = day - 1;
            } else if (Statue == 1) {
                day = day - 7;
                period=7;
                end = new GregorianCalendar(year,month,day);
                dweek=end.get(Calendar.DAY_OF_WEEK);
            } else if (Statue == 2) {
                month = month - 1;
                end = new GregorianCalendar(year,month,day);
                period=end.getActualMaximum(Calendar.WEEK_OF_MONTH);
            } else {
                year = year - 1;
                period=12;
            }
            dataAnalyze();
        }
    }


    private class ChoicePeriodStatue implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Statue = position;
                end = new GregorianCalendar(year,month,day);
                month = end.get(Calendar.MONTH);
                year = end.get(Calendar.YEAR);
                dweek = end.get(Calendar.DAY_OF_WEEK);
                day = end.get(Calendar.DAY_OF_MONTH);
                Log.d(TAG,"day"+Common.sDay.format(new Date(end.getTimeInMillis()))+" : "+dweek);
                if (position == 0) {
                    period = 1;
                } else if (position == 1) {
                    if(week==1)
                    {
                        dweek=1;
                    }
                    period =7+extra;
                    extra=0;
                    week=0;
                } else if (position == 2) {
                    period = end.getActualMaximum(Calendar.WEEK_OF_MONTH);
                } else {
                    month=0;
                    period =12;
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
            if (e.getY() <=0) {
                return;
            }
            if (Statue == 2) {
                week = (int) (e.getX() + 1);
                Statue = 1;
                if (week == 1) {
                    Calendar calendar = new GregorianCalendar(year, month, 1, 0, 0, 0);
                    dweek = calendar.get(Calendar.DAY_OF_WEEK);
                    day = 1;
                    extra=-dweek+1;
                } else if (week == period) {
                    Calendar calendar = new GregorianCalendar(year, month, 1);
                    calendar.set(Calendar.WEEK_OF_MONTH, week);
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    extra=calendar.getMaximum(Calendar.DAY_OF_MONTH)-day-7+1;
                } else {
                    Calendar calendar = new GregorianCalendar(year, month, 1);
                    calendar.set(Calendar.WEEK_OF_MONTH, week);
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    extra=0;
                }
                choicePeriod.setSelection(1);
            } else if (Statue == 3) {
                Statue = 2;
                month = (int) e.getX();
                choicePeriod.setSelection(2);
            } else {
                Fragment fragment = new SelectDetCircle();
                Bundle bundle = new Bundle();
                bundle.putSerializable("ShowConsume", ShowConsume);
                bundle.putSerializable("ShowAllCarrier", ShowAllCarrier);
                bundle.putSerializable("noShowCarrier", noShowCarrier);
                bundle.putSerializable("year", year);
                bundle.putSerializable("month", month);
                bundle.putSerializable("day", day-dweek+1);
                bundle.putSerializable("index", (int) e.getX());
                bundle.putSerializable("carrier",choiceD);
                bundle.putSerializable("total",(int)e.getY());
                bundle.putSerializable("period", period);
                bundle.putSerializable("dweek",dweek);
                fragment.setArguments(bundle);
                switchFragment(fragment);
            }
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
                bundle.putStringArrayList("OKey", OKey);
                bundle.putSerializable("total",(int)h.getY());
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
            bundle.putSerializable("carrier",choiceD);
            bundle.putSerializable("statue", Statue);
            bundle.putSerializable("period", period);
            bundle.putSerializable("dweek",dweek);
            bundle.putSerializable("position",0);
            fragment.setArguments(bundle);
            switchFragment(fragment);
        }

        @Override
        public void onNothingSelected() {

        }
    }
}

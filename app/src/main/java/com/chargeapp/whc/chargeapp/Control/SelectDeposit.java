package com.chargeapp.whc.chargeapp.Control;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectDeposit extends Fragment {


    private TextView PIdateTittle, describeT, describeD;
    private ImageView PIdateCut, PIdateAdd;
    private String TAG = "SelectDeposit";
    private int month, year;
    private Calendar end;
    private Spinner choicePeriod;
    private LineChart chart_line;
    private int Statue = 0, period, Alltotal, Allincome, Allconsume;
    private BankDB bankDB;
    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private String Title, desTittleTop, desTittleDown;
    private RelativeLayout PIdateL;
    private GoalDB goalDB;
    private GoalVO goalVO;
    private int Max;
    private  String goalTimeStatue;
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
        View view = inflater.inflate(R.layout.select_deposit, container, false);
        goalDB = new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        goalVO = goalDB.getFindType("儲蓄");
        end = Calendar.getInstance();
        year = end.get(Calendar.YEAR);
        Common.setChargeDB(context);
        bankDB = new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        findViewById(view);
        PIdateAdd.setOnClickListener(new AddOnClick());
        PIdateCut.setOnClickListener(new CutOnClick());
        choicePeriod.setOnItemSelectedListener(new ChoicePeriodStatue());
        return view;
    }

    private void setGoalVO() {
        if (goalVO != null) {
            goalTimeStatue = goalVO.getTimeStatue().trim();
            if (goalTimeStatue.equals("每月")) {
                Max = goalVO.getMoney();
            } else if (goalTimeStatue.equals("每年")) {
                Max = goalVO.getMoney() / 12;
            } else if (goalTimeStatue.equals("今日")) {
                if (System.currentTimeMillis() > goalVO.getEndTime().getTime()) {
                    Max = 0;
                    int Itotal = invoiceDB.getTotalBytime(new Timestamp(goalVO.getStartTime().getTime()), new Timestamp(goalVO.getEndTime().getTime()));
                    int Btotal = bankDB.getTimeTotal(new Timestamp(goalVO.getStartTime().getTime()), new Timestamp(goalVO.getEndTime().getTime()));
                    int Ctotal = consumeDB.getTimeTotal(new Timestamp(goalVO.getStartTime().getTime()), new Timestamp(goalVO.getEndTime().getTime()));
                    int Alltotal = Btotal - Ctotal - Itotal;
                    if (Alltotal > goalVO.getMoney()) {
                        goalVO.setStatue(2);
                    } else {
                        goalVO.setStatue(1);
                    }
                    goalDB.update(goalVO);
                } else {
                    Calendar start = new GregorianCalendar();
                    start.setTime(goalVO.getStartTime());
                    Calendar end = new GregorianCalendar();
                    end.setTime(goalVO.getEndTime());
                    int divid = (end.get(Calendar.MONTH) - start.get(Calendar.MONTH));
                    if (divid == 0) {
                        divid = 1;
                    }
                    Max = goalVO.getMoney() / divid;
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        dataAnalyze();
    }


    private void dataAnalyze() {
        Calendar start, end;
        List<Entry> entries = new ArrayList<Entry>();
        Allconsume = 0;
        Alltotal = 0;
        Allincome = 0;
        if (Statue == 0) {
            setGoalVO();
            for (int i = 0; i <= period; i++) {
                start = new GregorianCalendar(year, month + i, 01, 0, 0, 0);
                end = new GregorianCalendar(year, month + i, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
                entries.add(new Entry(i, PerStatue(start, end)));
                Title = Common.sFour.format(new Date(start.getTimeInMillis()));
                PIdateL.setVisibility(View.VISIBLE);
            }
        } else {
            setGoalVO();
            Max = Max * 12;
            for (int i = 0; i <= period; i++) {
                start = new GregorianCalendar(year + i, 0, 01, 0, 0, 0);
                end = new GregorianCalendar(year + i, 11, 31, 23, 59, 59);
                entries.add(new Entry(i, PerStatue(start, end)));
                Log.d(TAG, i + " : " + Common.sFour.format(new Date(start.getTimeInMillis())));
                PIdateL.setVisibility(View.GONE);
            }
        }
        desTittleTop = "累計收入 : " + Allincome + "元  累計花費 : " + Allconsume + "元";
        desTittleDown = "累計存款 : " + Alltotal + "元";
        SpannableString span = new SpannableString(desTittleTop);
        span.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, desTittleTop.indexOf("元") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(Color.BLUE), desTittleTop.indexOf("元") + 1, desTittleTop.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        describeT.setText(span);
        describeD.setText(desTittleDown);
        describeD.setTextColor(Color.RED);
        PIdateTittle.setText(Title);
        LineDataSet dataSet = new LineDataSet(entries, "存款");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawFilled(true);
        dataSet.setColor(Color.BLACK);
        dataSet.setFillColor(Color.BLUE);
        dataSet.setHighlightEnabled(false);
        dataSet.setValueTextSize(12f);
        dataSet.setDrawValues(true);
        LineData data = new LineData(dataSet);
        XAxis xAxis = chart_line.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                try {
                    int index = (int) value;
                    return getLabels().get(index);
                } catch (Exception e) {
                    return " ";
                }
            }
        });
        chart_line.setData(data);
        chart_line.setDescription(Common.getDeescription());
        if (goalVO != null) {
            LimitLine yLimitLine = new LimitLine(Max, "儲蓄目標");
            yLimitLine.setLineColor(Color.RED);
            yLimitLine.setTextColor(Color.RED);
            YAxis yAxis = chart_line.getAxisLeft();
            yAxis.removeAllLimitLines();
            yAxis.addLimitLine(yLimitLine);
            if(goalTimeStatue.trim().equals("今日"))
            {
                LimitLine xLimitLine;
                Calendar endGoal =new GregorianCalendar();
                endGoal.setTime(goalVO.getEndTime());
                XAxis xAxis1=chart_line.getXAxis();
                xAxis.removeAllLimitLines();
                if(Statue==0)
                {
                    int month=endGoal.get(Calendar.MONTH);
                    xLimitLine = new LimitLine(month, "儲蓄目標");
                    xLimitLine.setTextColor(Color.BLUE);
                    xAxis1.addLimitLine(xLimitLine);
                }else{
                    int year=endGoal.get(Calendar.YEAR);
                    xLimitLine = new LimitLine(year, "儲蓄目標");
                    xLimitLine.setTextColor(Color.BLUE);
                    xAxis1.addLimitLine(xLimitLine);
                }


            }
        }
        chart_line.invalidate();
    }

    private List<String> getLabels() {
        List<String> chartLabels = new ArrayList<>();
        if (Statue == 0) {
            for (int i = 0; i <= period; i++) {
                chartLabels.add(i + 1 + "月");
            }
        } else {
            for (int i = 0; i <= period; i++) {
                chartLabels.add(i + year + "年");
            }
        }
        return chartLabels;
    }

    private float PerStatue(Calendar start, Calendar end) {
        int income, comsume, total, invoice;
        income = bankDB.getTimeTotal(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
        comsume = consumeDB.getTimeTotal(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
        invoice = invoiceDB.getTotalBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
        total = income - comsume - invoice;
        Alltotal = Alltotal + total;
        Allincome = income + Allincome;
        Allconsume = Allconsume + invoice + comsume;
        return total;
    }

    private void findViewById(View view) {
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        choicePeriod = view.findViewById(R.id.choicePeriod);
        chart_line = view.findViewById(R.id.chart_line);
        describeT = view.findViewById(R.id.describeT);
        describeD = view.findViewById(R.id.describeD);
        PIdateL = view.findViewById(R.id.PIdateL);
        ArrayList<String> SpinnerItem1 = new ArrayList<>();
        SpinnerItem1.add(" 月 ");
        SpinnerItem1.add(" 年 ");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinneritem, SpinnerItem1);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        choicePeriod.setAdapter(arrayAdapter);
    }


    private class AddOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (Statue == 0) {
                year = year + 1;
                period = 11;
                month = 0;
                dataAnalyze();
            }
        }
    }

    private class CutOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (Statue == 0) {
                year = year - 1;
                period = 11;
                month = 0;
                dataAnalyze();
            }
        }
    }


    private class ChoicePeriodStatue implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Statue = position;
            if (position == 0) {
                period = 11;
                month = 0;
            } else {
                ArrayList<Long> minTime = new ArrayList<>();
                minTime.add(bankDB.getMinTime());
                minTime.add(consumeDB.getMinTime());
                minTime.add(invoiceDB.getMinTime());
                Collections.sort(minTime, new Comparator<Long>() {
                    @Override
                    public int compare(Long aLong, Long t1) {
                        if (aLong > t1) {
                            return 1;
                        } else if (aLong == t1) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                start.setTime(new Date(minTime.get(0)));
                year = start.get(Calendar.YEAR);
                period = end.get(Calendar.YEAR) - year;
                if(period>10)
                {
                    period=10;
                    year=end.get(Calendar.YEAR)-10;
                }
            }
            dataAnalyze();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}

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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.Utils;


import org.jsoup.internal.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_FLAG;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_THUMBS_O_UP;
import static com.chargeapp.whc.chargeapp.Control.Common.choiceCurrency;
import static com.chargeapp.whc.chargeapp.Control.Common.getCurrency;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectDeposit extends Fragment {


    private TextView PIdateTittle, describeC, describeI;
    private ImageView PIdateCut, PIdateAdd;
    private String TAG = "SelectDeposit";
    private int month, year;
    private Calendar end;
    private BootstrapDropDown choicePeriod;
    private LineChart chart_line, chart_consume, chart_income;
    private int Statue = 0, period;
    private BankDB bankDB;
    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private String Title;
    private RelativeLayout PIdateL;
    private GoalDB goalDB;
    private GoalVO goalVO;
    private Activity context;
    private String pictureT, pictureL, pictureCT, pictureCL, pictureIT, pictureIL;
    private List<Entry> entries, entriesC, entriesI;
    private List<BootstrapText> periodDepoist;
    private AwesomeTextView goalDeposit;
    private double Alltotal, Allincome, Allconsume;


    private SharedPreferences sharedPreferences;
    private String nowCurrency;
    private CurrencyDB currencyDB;
    private CurrencyVO currencyVO;
    private BootstrapButton setCurrency;
    private AwesomeTextView otherMessage;
    private PopupMenu popupMenu;
    private Calendar startPopup,endPopup;


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
        final View view = inflater.inflate(R.layout.select_deposit, container, false);
        Common.setScreen(Common.screenSize,context);
        //DB
        Common.setChargeDB(context);

        goalDB = new GoalDB(MainActivity.chargeAPPDB);
        goalVO = goalDB.getFindType("儲蓄");
        //setTime
        end = Calendar.getInstance();
        year = end.get(Calendar.YEAR);
        period = 11;
        month = 0;

        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);
        bankDB = new BankDB(MainActivity.chargeAPPDB);
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB);
        findViewById(view);


        //current
        sharedPreferences=context.getSharedPreferences("Charge_User",Context.MODE_PRIVATE);
        nowCurrency=sharedPreferences.getString(Common.choiceCurrency,"TWD");
        setCurrency.setText(getCurrency(nowCurrency));
        popupMenu=new PopupMenu(context,setCurrency);
        Common.createCurrencyPopMenu(popupMenu, context);
        setCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
        popupMenu.setOnMenuItemClickListener(new choiceCurrency());



        PIdateAdd.setOnClickListener(new AddOnClick());
        PIdateCut.setOnClickListener(new CutOnClick());
//        choicePeriod.setOnItemSelectedListener(new ChoicePeriodStatue());
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                choicePeriod.setBootstrapText(periodDepoist.get(Statue));
                choicePeriod.setShowOutline(false);
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        dataAnalyze();
    }


    private void dataAnalyze() {
        Calendar start, end;
        entries = new ArrayList<Entry>();
        entriesC = new ArrayList<Entry>();
        entriesI = new ArrayList<Entry>();
        Allconsume = 0;
        Alltotal = 0;
        Allincome = 0;
        List<Double> allTotal;
        if (Statue == 0) {
            for (int i = 0; i <= period; i++) {
                start = new GregorianCalendar(year, month + i, 01, 0, 0, 0);
                end = new GregorianCalendar(year, month + i, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
                currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
                startPopup=start;
                endPopup=end;


                allTotal = PerStatue(start, end);
                entries.add(new Entry(i, allTotal.get(2).floatValue()));
                entriesC.add(new Entry(i, allTotal.get(0).floatValue()));
                entriesI.add(new Entry(i, allTotal.get(1).floatValue()));
                Title = Common.sFour.format(new Date(start.getTimeInMillis()));
                PIdateL.setVisibility(View.VISIBLE);
            }
            pictureL = year + "年每月存款";
            pictureT = year + "年累計存款";
            pictureCL = year + "年每月花費";
            pictureCT = year + "年累計花費 : " + Common.CurrencyResult(Allconsume,currencyVO);
            pictureIL = year + "年每月收入";
            pictureIT = year + "年累計收入 : " + Common.CurrencyResult(Allincome,currencyVO);
        } else {
            for (int i = 0; i <= period; i++) {
                start = new GregorianCalendar(year + i, 0, 01, 0, 0, 0);
                end = new GregorianCalendar(year + i, 11, 31, 23, 59, 59);

                currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
                startPopup=start;
                endPopup=end;

                allTotal = PerStatue(start, end);
                entries.add(new Entry(i, allTotal.get(2).floatValue()));
                entriesC.add(new Entry(i, allTotal.get(0).floatValue()));
                entriesI.add(new Entry(i, allTotal.get(1).floatValue()));
                Log.d(TAG, i + " : " + Common.sFour.format(new Date(start.getTimeInMillis())));
                PIdateL.setVisibility(View.GONE);
            }
            pictureL = "每年存款";
            pictureT = "歷年累計存款";
            pictureCL = "每年花費";
            pictureCT = "歷年累計花費 : " +Common.CurrencyResult(Allconsume,currencyVO);
            pictureIL = "每年收入";
            pictureIT = "歷年累計收入 : " +Common.CurrencyResult(Allincome,currencyVO);
        }
        //累計存款
        setDeposit();
        setConsume();
        setIncome();
    }


    private void setIncome() {

        describeI.setText(pictureIT);

        LineDataSet dataSet = new LineDataSet(entriesI, pictureIL);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawFilled(true);
        dataSet.setColor(Color.parseColor("#007bff"));
        dataSet.setFillColor(Color.parseColor("#007bff"));
        dataSet.setHighlightEnabled(false);
        dataSet.setDrawValues(false);
        LineData data = new LineData(dataSet);
        XAxis xAxis = chart_income.getXAxis();
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
        chart_income.setData(data);
        chart_income.setDescription(Common.getDeescription());
        chart_income.setTouchEnabled(false);
        chart_income.setScaleEnabled(false);
        YAxis yAxis = chart_income.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yAxis1 = chart_income.getAxis(YAxis.AxisDependency.RIGHT);
        yAxis1.setDrawAxisLine(false);
        yAxis1.setDrawLabels(false);
        Legend l = chart_income.getLegend();
        l.setFormSize(18f);
        l.setTextColor(Color.parseColor("#007bff"));

            switch (Common.screenSize) {
                case xLarge:
                    xAxis.setTextSize(20f);
                    yAxis.setTextSize(20f);
                    yAxis1.setTextSize(20f);
                    l.setTextSize(20f);
                    l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
                    l.setYEntrySpace(5f);
                    l.setFormSize(20f);
                    break;
                case large:
                    xAxis.setTextSize(20f);
                    yAxis.setTextSize(20f);
                    yAxis1.setTextSize(20f);
                    l.setTextSize(20f);
                    l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
                    l.setYEntrySpace(5f);
                    l.setFormSize(20f);
                    break;
                case normal:
                    xAxis.setTextSize(11f);
                    yAxis.setTextSize(12f);
                    yAxis1.setTextSize(12f);
                    l.setTextSize(12f);
                    l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
                    l.setYEntrySpace(5f);
                    l.setFormSize(12f);
                    break;
            }
        chart_income.notifyDataSetChanged();
        chart_income.invalidate();
    }

    private void setConsume() {
        describeC.setText(pictureCT);
        LineDataSet dataSet = new LineDataSet(entriesC, pictureCL);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawFilled(true);
        dataSet.setColor(Color.parseColor("#dc3545"));
        dataSet.setFillColor(Color.parseColor("#dc3545"));
        dataSet.setHighlightEnabled(false);
        dataSet.setDrawValues(false);
        LineData data = new LineData(dataSet);
        XAxis xAxis = chart_consume.getXAxis();
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
        chart_consume.setData(data);
        chart_consume.setDescription(Common.getDeescription());
        chart_consume.setTouchEnabled(false);
        chart_consume.setScaleEnabled(false);
        YAxis yAxis = chart_consume.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yAxis1 = chart_consume.getAxis(YAxis.AxisDependency.RIGHT);
        yAxis1.setDrawAxisLine(false);
        yAxis1.setDrawLabels(false);
        Legend l = chart_consume.getLegend();
        l.setFormSize(18f);
        l.setTextColor(Color.parseColor("#dc3545"));


            switch (Common.screenSize) {
                case xLarge:
                    xAxis.setTextSize(20f);
                    yAxis.setTextSize(20f);
                    yAxis1.setTextSize(20f);
                    l.setTextSize(20f);
                    l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
                    l.setYEntrySpace(5f);
                    l.setFormSize(20f);
                    break;
                case large:
                    xAxis.setTextSize(20f);
                    yAxis.setTextSize(20f);
                    yAxis1.setTextSize(20f);
                    l.setTextSize(20f);
                    l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
                    l.setYEntrySpace(5f);
                    l.setFormSize(20f);
                    break;
                case normal:
                    xAxis.setTextSize(11f);
                    yAxis.setTextSize(12f);
                    yAxis1.setTextSize(12f);
                    l.setTextSize(12f);
                    l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
                    l.setYEntrySpace(5f);
                    l.setFormSize(12f);
                    break;
            }

        chart_consume.notifyDataSetChanged();
        chart_consume.invalidate();
    }


    private void setDeposit() {
        setCurrency.setText(Common.CurrencyResult(Alltotal,currencyVO));
        otherMessage.setText(pictureT);
        otherMessage.setTextColor(Color.BLACK);
        PIdateTittle.setText(Title);
        LineDataSet dataSet = new LineDataSet(entries, pictureL);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawFilled(true);
        dataSet.setColor(Color.parseColor("#28a745"));
        dataSet.setFillColor(Color.parseColor("#28a745"));
        dataSet.setHighlightEnabled(false);
        dataSet.setDrawValues(false);
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
        chart_line.setTouchEnabled(false);
        chart_line.setScaleEnabled(false);


        if (goalVO != null) {
            YAxis yAxis = chart_line.getAxisLeft();
            yAxis.removeAllLimitLines();
            CurrencyVO currencyVO=currencyDB.getBytimeAndType(goalVO.getStartTime().getTime(), goalVO.getEndTime().getTime(),goalVO.getCurrency());

            if(StringUtil.isBlank(goalVO.getRealMoney()))
            {
                goalVO.setRealMoney(String.valueOf(goalVO.getMoney()));
                goalDB.update(goalVO);
            }

            Double goalMoney=Double.valueOf(goalVO.getRealMoney())*Double.valueOf(currencyVO.getMoney());

            if (Statue == 0 && goalVO.getTimeStatue().trim().equals("每月")) {
                LimitLine yLimitLine = new LimitLine(goalMoney.floatValue(), "儲蓄目標");
                yLimitLine.setLineColor(Color.parseColor("#007bff"));
                yLimitLine.setTextColor(Color.parseColor("#007bff"));
                yAxis.addLimitLine(yLimitLine);
                BootstrapText bootstrapText = new BootstrapText.Builder(context)
                        .addFontAwesomeIcon(FA_FLAG)
                        .addText(" 目標 : " + goalVO.getName() + goalVO.getTimeStatue() + goalVO.getType() + Common.CurrencyResult(goalMoney,this.currencyVO))
                        .build();

                goalDeposit.setBootstrapText(bootstrapText);
            } else if (Statue == 1 && goalVO.getTimeStatue().trim().equals("每年")) {
                LimitLine yLimitLine = new LimitLine(goalMoney.floatValue(), "儲蓄目標");
                yLimitLine.setLineColor(Color.parseColor("#007bff"));
                yLimitLine.setTextColor(Color.parseColor("#007bff"));
                yAxis.addLimitLine(yLimitLine);
                BootstrapText bootstrapText = new BootstrapText.Builder(context)
                        .addFontAwesomeIcon(FA_FLAG)
                        .addText(" 目標 : " + goalVO.getName() + goalVO.getTimeStatue() + goalVO.getType() + Common.CurrencyResult(goalMoney,this.currencyVO))
                        .build();
                goalDeposit.setBootstrapText(bootstrapText);
            } else {
                goalDeposit.setText(null);
            }

            if (goalVO.getTimeStatue().trim().equals("今日") && goalVO.getEndTime().getTime() > System.currentTimeMillis()) {

                //描素目標狀態
                StringBuilder sb = new StringBuilder();

                double Itotal = invoiceDB.getInvoiceByTimeHashMap(goalVO.getStartTime().getTime(), goalVO.getEndTime().getTime()).get("total");
                double Btotal = bankDB.getTimeTotal(goalVO.getStartTime(), goalVO.getEndTime());
                double Ctotal = consumeDB.getTimePeriodHashMap(goalVO.getStartTime().getTime(), goalVO.getEndTime().getTime()).get("total");
                double Alltotal = Btotal - Ctotal - Itotal;
                double differentMoney = Double.valueOf(goalVO.getRealMoney())*Double.valueOf(currencyVO.getMoney()) - Alltotal;

                LimitLine xLimitLine;
                Calendar endGoal = new GregorianCalendar();
                endGoal.setTime(goalVO.getEndTime());
                Calendar startGoal = new GregorianCalendar();
                startGoal.setTime(goalVO.getStartTime());
                Calendar nowCalendar =new GregorianCalendar(year,month,1);
                int divideEndY = endGoal.get(Calendar.YEAR) - nowCalendar.get(Calendar.YEAR);
                int divideStartY = startGoal.get(Calendar.YEAR) - nowCalendar.get(Calendar.YEAR);

                XAxis xAxis1 = chart_line.getXAxis();
                xAxis.removeAllLimitLines();
                //倒數日期
                long differentT = endGoal.getTimeInMillis() - nowCalendar.getTimeInMillis();
                int differentDay = (int) (differentT / (1000 * 60 * 60 * 24));
                sb.append("倒數" + differentDay + "天  ");


                BootstrapText bootstrapText;
                if (differentMoney <= 0) {
                    sb.append("完成目標 ");
                    bootstrapText = new BootstrapText.Builder(context)
                            .addFontAwesomeIcon(FA_FLAG)
                            .addText(" 目標 : " + goalVO.getName() + goalVO.getType() + Common.CurrencyResult(goalMoney,this.currencyVO)+"\n" + sb.toString()).addFontAwesomeIcon(FA_THUMBS_O_UP)
                            .build();
                } else {

                    sb.append("還差" + Common.CurrencyResult(differentMoney,this.currencyVO) + "元 加油!");
                    bootstrapText = new BootstrapText.Builder(context)
                            .addFontAwesomeIcon(FA_FLAG)
                            .addText(" 目標 : " + goalVO.getName() + goalVO.getType() +Common.CurrencyResult(goalMoney,this.currencyVO) + "\n" + sb.toString())
                            .build();
                }
                goalDeposit.setBootstrapText(bootstrapText);


                xAxis1.removeAllLimitLines();
                //月-終止時間
                if (Statue == 0 && divideEndY == 0) {
                    float month = endGoal.get(Calendar.MONTH);
                    float day =  (float)endGoal.get(Calendar.DAY_OF_MONTH) / endGoal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if(month==11)
                    {
                        xLimitLine = new LimitLine(month,"目標訖日");
                    }else {
                        xLimitLine = new LimitLine(month + day, "目標訖日");
                    }
                    xLimitLine.setTextColor(Color.RED);
                    if(month>9)
                    {
                        xLimitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
                    }else{
                        xLimitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
                    }
                    xAxis1.addLimitLine(xLimitLine);
                }

                //月-起始時間
                if (Statue == 0 && divideStartY == 0) {
                    //起始時間
                    float month = startGoal.get(Calendar.MONTH);
                    float day = (float) startGoal.get(Calendar.DAY_OF_MONTH) / startGoal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if(month==11)
                    {
                        xLimitLine = new LimitLine(11,"目標起日");
                    }else {
                        xLimitLine = new LimitLine(month + day, "目標起日");
                    }
                    if(month>9)
                    {
                        xLimitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
                    }
                    xLimitLine.setTextColor(Color.BLUE);
                    xLimitLine.setLineColor(Color.BLUE);
                    xAxis1.addLimitLine(xLimitLine);
                }
                //年-起始時間
                if (Statue == 1) {
                    //起始時間
                    float month = (float) startGoal.get(Calendar.MONTH) / 12;
                    float year=startGoal.get(Calendar.YEAR)-this.year;
                    float total=month+year;
                    xLimitLine = new LimitLine( total, "目標起日");
                    xLimitLine.setTextColor(Color.BLUE);
                    xLimitLine.setLineColor(Color.BLUE);
                    xAxis1.addLimitLine(xLimitLine);

                    //終止時間
                    month = Float.valueOf(endGoal.get(Calendar.MONTH) / 12);
                    year=endGoal.get(Calendar.YEAR)-this.year;
                    total=month+year;
                    xLimitLine = new LimitLine(total, "目標訖日");
                    xLimitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
                    xLimitLine.setTextColor(Color.RED);
                    xAxis1.addLimitLine(xLimitLine);
                }
            }
        }


        YAxis yAxis = chart_line.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yAxis1 = chart_line.getAxis(YAxis.AxisDependency.RIGHT);
        yAxis1.setDrawAxisLine(false);
        yAxis1.setDrawLabels(false);
        Legend l = chart_line.getLegend();
        l.setFormSize(18f);
        l.setTextColor(Color.parseColor("#28a745"));


            switch (Common.screenSize) {
                case xLarge:
                    xAxis.setTextSize(20f);
                    yAxis.setTextSize(20f);
                    yAxis1.setTextSize(20f);
                    l.setTextSize(20f);
                    l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
                    l.setYEntrySpace(5f);
                    l.setFormSize(20f);
                    break;
                case large:
                    xAxis.setTextSize(20f);
                    yAxis.setTextSize(20f);
                    yAxis1.setTextSize(20f);
                    l.setTextSize(20f);
                    l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
                    l.setYEntrySpace(5f);
                    l.setFormSize(20f);
                    break;
                case normal:
                    xAxis.setTextSize(11f);
                    yAxis.setTextSize(12f);
                    yAxis1.setTextSize(12f);
                    l.setTextSize(12f);
                    l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
                    l.setYEntrySpace(5f);
                    l.setFormSize(12f);
                    break;
            }

        chart_line.notifyDataSetChanged();
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

    private List<Double> PerStatue(Calendar start, Calendar end) {
        double income, comsume, total, invoice;
        List<Double> addList = new ArrayList<>();
        income = bankDB.getTimeTotal(new java.sql.Date(start.getTimeInMillis()), new java.sql.Date(end.getTimeInMillis()));
        comsume = consumeDB.getTimePeriodHashMap(start.getTimeInMillis(), end.getTimeInMillis()).get("total");
        invoice = invoiceDB.getInvoiceByTimeHashMap(start.getTimeInMillis(), end.getTimeInMillis()).get("total");
        total = income - comsume - invoice;
        addList.add(invoice + comsume);
        addList.add(income);
        addList.add(total);
        Alltotal = Alltotal + total;
        Allincome = income + Allincome;
        Allconsume = Allconsume + invoice + comsume;
        return addList;
    }

    private void findViewById(View view) {
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        choicePeriod = view.findViewById(R.id.choicePeriod);
        chart_line = view.findViewById(R.id.chart_line);
        PIdateL = view.findViewById(R.id.PIdateL);
        describeC = view.findViewById(R.id.describeC);
        describeI = view.findViewById(R.id.describeI);
        chart_consume = view.findViewById(R.id.chart_consume);
        chart_income = view.findViewById(R.id.chart_income);
        goalDeposit = view.findViewById(R.id.goalDeposit);

        setCurrency=view.findViewById(R.id.setCurrency);
        otherMessage=view.findViewById(R.id.otherMessage);

        String[] SpinnerItem1 = new String[2];
        SpinnerItem1[0] = " 月 ";
        SpinnerItem1[1] = " 年 ";
        periodDepoist = new ArrayList<>();
        periodDepoist.add(Common.setPeriodSelectCBsTest(context, SpinnerItem1[0]));
        periodDepoist.add(Common.setPeriodSelectCBsTest(context, SpinnerItem1[1]));
        choicePeriod.setDropdownData(SpinnerItem1);
        choicePeriod.setOnDropDownItemClickListener(new PeriodChoice());
        dataAnalyze();


//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinneritem, SpinnerItem1);
//        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
//        choicePeriod.setAdapter(arrayAdapter);
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
                if (period > 10) {
                    period = 10;
                    year = end.get(Calendar.YEAR) - 10;
                }
            }
            dataAnalyze();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class PeriodChoice implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int position) {
            Statue = position;
            choicePeriod.setBootstrapText(periodDepoist.get(position));
            choicePeriod.setShowOutline(false);
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
                if (period > 10) {
                    period = 10;
                    year = end.get(Calendar.YEAR) - 10;
                }
            }
            dataAnalyze();
        }
    }

    private class choiceCurrency implements PopupMenu.OnMenuItemClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String title= (String) menuItem.getTitle();
            switch (title) {
                case "新台幣":
                    nowCurrency = "TWD";
                    sharedPreferences.edit().putString(choiceCurrency, nowCurrency).apply();
                    currencyVO=currencyDB.getBytimeAndType(startPopup.getTimeInMillis(),endPopup.getTimeInMillis(),nowCurrency);
                case "離開":
                    popupMenu.dismiss();
                    break;
                default:
                    nowCurrency = Common.code.get(menuItem.getItemId() - 2);
                    sharedPreferences.edit().putString(choiceCurrency, nowCurrency).apply();
                    currencyVO=currencyDB.getBytimeAndType(startPopup.getTimeInMillis(),endPopup.getTimeInMillis(),nowCurrency);
                    break;
            }
            dataAnalyze();
            return true;
        }
    }


}

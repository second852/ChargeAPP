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
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;

import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
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
import com.github.mikephil.charting.utils.Utils;


import org.jsoup.internal.StringUtil;

import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_FLAG;
import static com.chargeapp.whc.chargeapp.Control.Common.choiceCurrency;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectConsume extends Fragment {


    private InvoiceDB invoiceDB;
    private CarrierDB carrierDB;
    private ConsumeDB consumeDB;
    private TextView PIdateTittle;
    private ImageView PIdateCut, PIdateAdd;
    public int choiceD;
    private List<CarrierVO> carrierVOS;
    private String TAG = "SelectConsume";
    private BarChart chart_bar;
    private List<Map.Entry<String, Double>> list_Data;
    private int month, year, day, dayWeek, extra;
    private BootstrapDropDown choiceCarrier, choicePeriod;
    private PieChart chart_pie;
    private int period;
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
    private Double Max;
    public static int Statue;
    public static Calendar end;
    public static int CStatue;
    private Activity context;
    private AwesomeTextView goalConsume;
    private List<BootstrapText> carrierTexts;
    private List<BootstrapText> periodTexts;


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
    private float lastX,lastY;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = (Activity) context;
        } else {
            this.context = getActivity();
        }
        Utils.init(this.context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Common.setScreen(Common.screenSize, context);
        final View view = inflater.inflate(R.layout.select_consume, container, false);
        //init DataBase
        setDB();
        findViewById(view);

        //current
        sharedPreferences = context.getSharedPreferences("Charge_User", Context.MODE_PRIVATE);
        nowCurrency = sharedPreferences.getString(Common.choiceCurrency, "TWD");
        popupMenu = new PopupMenu(context, setCurrency);
        Common.createCurrencyPopMenu(popupMenu, context);
        setCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
        popupMenu.setOnMenuItemClickListener(new choiceCurrency());


        if (end == null) {
            end = Calendar.getInstance();
            SelectConsume.Statue = 1;
        }

        month = end.get(Calendar.MONTH);
        year = end.get(Calendar.YEAR);
        day = end.get(Calendar.DAY_OF_MONTH);
        dayWeek = end.get(Calendar.DAY_OF_WEEK);
        //設定dayWeek period
        switch (Statue) {
            case 0:
                period = 1;
                break;
            case 1:
                if (week == 1) {
                    dayWeek = 1;
                }
                period = 7 + extra;
                extra = 0;
                week = 0;
                break;
            case 2:
                period = end.getActualMaximum(Calendar.WEEK_OF_MONTH);
                break;
            case 3:
                period = 12;
                month = 0;
                break;
        }


        //載具
        switch (CStatue) {
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


        PIdateAdd.setOnClickListener(new AddOnClick());
        PIdateCut.setOnClickListener(new CutOnClick());

        chart_bar.setOnTouchListener(new View.OnTouchListener() {
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
        });
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


    private void setGoalVO(Calendar start, Calendar end) {
        Max = 0.0;
        choicePeriod.setBootstrapText(periodTexts.get(Statue));
        choicePeriod.setShowOutline(false);
        if (goalVO != null) {
            String goalTimeStatue = goalVO.getTimeStatue().trim();

            if (StringUtil.isBlank(goalVO.getRealMoney())) {
                goalVO.setRealMoney(String.valueOf(goalVO.getMoney()));
                goalDB.update(goalVO);
            }


            if (goalTimeStatue.equals("每天") && Statue == 0) {
                CurrencyVO currencyVO = currencyDB.getBytimeAndType(start.getTimeInMillis(), end.getTimeInMillis(), goalVO.getCurrency());
                Max = (Double.valueOf(goalVO.getRealMoney()) * Double.valueOf(currencyVO.getMoney())) / Double.valueOf(this.currencyVO.getMoney());
            } else if (goalTimeStatue.equals("每天") && Statue == 1) {
                CurrencyVO currencyVO = currencyDB.getBytimeAndType(start.getTimeInMillis(), end.getTimeInMillis(), goalVO.getCurrency());
                Max = (Double.valueOf(goalVO.getRealMoney()) * Double.valueOf(currencyVO.getMoney())) / Double.valueOf(this.currencyVO.getMoney());
            } else if (goalTimeStatue.equals("每周") && Statue == 2) {
                int dayWeek = start.get(Calendar.WEEK_OF_MONTH);
                start.set(Calendar.DAY_OF_MONTH, -dayWeek + 1);
                end.set(Calendar.DAY_OF_MONTH, -dayWeek + 7);
                CurrencyVO currencyVO = currencyDB.getBytimeAndType(start.getTimeInMillis(), end.getTimeInMillis(), goalVO.getCurrency());
                Max = (Double.valueOf(goalVO.getRealMoney()) * Double.valueOf(currencyVO.getMoney())) / Double.valueOf(this.currencyVO.getMoney());
            } else if (goalTimeStatue.equals("每月") && Statue == 3) {
                start.set(Calendar.DAY_OF_MONTH, 1);
                end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
                CurrencyVO currencyVO = currencyDB.getBytimeAndType(start.getTimeInMillis(), end.getTimeInMillis(), goalVO.getCurrency());
                Max = (Double.valueOf(goalVO.getRealMoney()) * Double.valueOf(currencyVO.getMoney())) / Double.valueOf(this.currencyVO.getMoney());
            }
        }
    }


    private void setDB() {
        Common.setChargeDB(context);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB);
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB);
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB);
        goalDB = new GoalDB(MainActivity.chargeAPPDB);
        currencyDB = new CurrencyDB(MainActivity.chargeAPPDB);
    }


    private void findViewById(View view) {
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        chart_bar = view.findViewById(R.id.chart_bar);
        choicePeriod = view.findViewById(R.id.choicePeriod);
        choiceCarrier = view.findViewById(R.id.choiceCarrier);
        chart_pie = view.findViewById(R.id.chart_pie);
        goalConsume = view.findViewById(R.id.goalConsume);
        setCurrency = view.findViewById(R.id.setCurrency);
        otherMessage = view.findViewById(R.id.otherMessage);
        otherMessage.setBootstrapBrand(null);
        otherMessage.setTextColor(Color.BLACK);

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
        HashMap<String, Double> hashMap = new HashMap<>();
        Calendar start, end;
        ChartEntry other = new ChartEntry("其他", 0.0);
        if (Statue == 0) {
            DesTittle = "當天花費";
            start = new GregorianCalendar(year, month, day, 0, 0, 0);
            end = new GregorianCalendar(year, month, day, 23, 59, 59);
            PIdateTittle.setText(Common.sOne.format(new Date(start.getTimeInMillis())));
        } else if (Statue == 1) {
            DesTittle = "這周花費";
            start = new GregorianCalendar(year, month, day - dayWeek + 1, 0, 0, 0);
            end = new GregorianCalendar(year, month, day - dayWeek + 1 + period - 1, 23, 59, 59);
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

        startPopup = new GregorianCalendar();
        startPopup.setTime(start.getTime());
        endPopup = new GregorianCalendar();
        endPopup.setTime(end.getTime());

        //SetCurrency choice
        currencyVO = currencyDB.getBytimeAndType(start.getTimeInMillis(), end.getTimeInMillis(), nowCurrency);

        if (!noShowCarrier && carrierVOS.size() > 0) {
            List<InvoiceVO> invoiceVOS;
            if (ShowAllCarrier) {
                invoiceVOS = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
            } else {
                if (CStatue >= carrierVOS.size()) {
                    CStatue = carrierVOS.size() - 1;
                }
                if (CStatue < 0) {
                    CStatue = 0;
                }
                invoiceVOS = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), carrierVOS.get(choiceD).getCarNul());
            }
            for (InvoiceVO I : invoiceVOS) {
                CurrencyVO currencyVO = currencyDB.getBytimeAndType(start.getTimeInMillis(), end.getTimeInMillis(), I.getCurrency());
                Double invoiceVOMoney = Double.valueOf(I.getRealAmount()) * Double.valueOf(currencyVO.getMoney());
                if (I.getMaintype().equals("0") || I.getMaintype().equals("O") || I.getMaintype().equals("其他")) {
                    other.setValue(other.getValue() + invoiceVOMoney);
                    OKey.add(I.getMaintype());
                    continue;
                }
                if (hashMap.get(I.getMaintype()) == null) {
                    hashMap.put(I.getMaintype(), invoiceVOMoney);
                } else {
                    hashMap.put(I.getMaintype(), invoiceVOMoney + hashMap.get(I.getMaintype()));
                }
                total = total + invoiceVOMoney;
            }
        }
        total = total + other.getValue();


        if (ShowConsume) {
            List<ConsumeVO> consumeVOS = consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
            for (ConsumeVO c : consumeVOS) {
                CurrencyVO currencyVO = currencyDB.getBytimeAndType(start.getTimeInMillis(), end.getTimeInMillis(), c.getCurrency());

                if (StringUtil.isBlank(c.getRealMoney())) {
                    c.setRealMoney(String.valueOf(c.getCurrency()));
                    consumeDB.update(c);
                }


                Double consumeVOMoney = Double.valueOf(c.getRealMoney()) * Double.valueOf(currencyVO.getMoney());
                if (hashMap.get(c.getMaintype()) == null) {
                    hashMap.put(c.getMaintype(), consumeVOMoney);
                } else {
                    hashMap.put(c.getMaintype(), consumeVOMoney + hashMap.get(c.getMaintype()));
                }
                total = total + consumeVOMoney;
            }
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
                time = new GregorianCalendar(year, month, day - dayWeek + 1 + i);
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
        Calendar start = null, end = null;
        if (Statue == 0) {
            start = new GregorianCalendar(year, month, day, 0, 0, 0);
            end = new GregorianCalendar(year, month, day, 23, 59, 59);
            BarEntry barEntry = new BarEntry(0, Periodfloat(start, end));
            chartData.add(barEntry);
            setGoalVO(start, end);
        } else if (Statue == 1) {
            for (int i = 0; i < period; i++) {
                start = new GregorianCalendar(year, month, day - dayWeek + 1 + i, 0, 0, 0);
                end = new GregorianCalendar(year, month, day - dayWeek + 1 + i, 23, 59, 59);
                Log.d(TAG, "start" + Common.sDay.format(new Date(start.getTimeInMillis())));
                BarEntry barEntry = new BarEntry(i, Periodfloat(start, end));
                chartData.add(barEntry);
            }
            setGoalVO(start, end);
        } else if (Statue == 2) {
            Calendar calendar = new GregorianCalendar(year, month, 1, 0, 0, 0);
            start = new GregorianCalendar(year, month, 1, 0, 0, 0);
            period = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
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
            setGoalVO(start, end);
            Log.d(TAG, "week " + String.valueOf(period - 1) + ":" + Common.sDay.format(new Date(start.getTimeInMillis())) + "~" + Common.sDay.format(new Date(end.getTimeInMillis())));
        } else {
            for (int i = 0; i < period; i++) {
                start = new GregorianCalendar(year, month + i, 1, 0, 0, 0);
                end = new GregorianCalendar(year, month + i, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
                BarEntry barEntry = new BarEntry(i, Periodfloat(start, end));
                chartData.add(barEntry);
            }
            setGoalVO(start, end);
        }
        return chartData;
    }

    private float[] Periodfloat(Calendar start, Calendar end) {
        Map<String, Double> hashMap = new LinkedHashMap<>();
        boolean isOther;
        float[] f = new float[list_Data.size()];
        ChartEntry other = new ChartEntry("其他", 0.0);
        if (!noShowCarrier && carrierVOS.size() > 0) {
            if (CStatue >= carrierVOS.size()) {
                CStatue = carrierVOS.size() - 1;
            }
            if (CStatue < 0) {
                CStatue = 0;
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
                CurrencyVO currencyVO = currencyDB.getBytimeAndType(start.getTimeInMillis(), end.getTimeInMillis(), I.getCurrency());
                double invoiceVOMoney = Double.valueOf(I.getRealAmount()) * Double.valueOf(currencyVO.getMoney());
                invoiceVOMoney = invoiceVOMoney / Double.valueOf(this.currencyVO.getMoney());
                for (Map.Entry e : list_Data) {
                    if (I.getMaintype().equals(e.getKey())) {
                        if (hashMap.get(I.getMaintype()) == null) {
                            hashMap.put(I.getMaintype(), invoiceVOMoney);
                        } else {
                            hashMap.put(I.getMaintype(), invoiceVOMoney + hashMap.get(I.getMaintype()));
                        }
                        isOther = false;
                        break;
                    }
                }
                if (isOther) {
                    other.setValue(other.getValue() + invoiceVOMoney);
                }
            }
        }
        if (ShowConsume) {
            List<ConsumeVO> periodConsume = consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
            for (ConsumeVO c : periodConsume) {
                isOther = true;
                CurrencyVO currencyVO = currencyDB.getBytimeAndType(start.getTimeInMillis(), end.getTimeInMillis(), c.getCurrency());

                if (StringUtil.isBlank(c.getRealMoney())) {
                    c.setRealMoney(String.valueOf(c.getRealMoney()));
                    consumeDB.update(c);
                }

                double consumeVOMoney = Double.valueOf(c.getRealMoney()) * Double.valueOf(currencyVO.getMoney());
                consumeVOMoney = consumeVOMoney / Double.valueOf(this.currencyVO.getMoney());
                for (Map.Entry e : list_Data) {
                    if (c.getMaintype().equals(e.getKey())) {
                        if (hashMap.get(c.getMaintype()) == null) {
                            hashMap.put(c.getMaintype(), consumeVOMoney);
                        } else {
                            hashMap.put(c.getMaintype(), consumeVOMoney + hashMap.get(c.getMaintype()));
                        }
                        isOther = false;
                        break;
                    }
                }
                if (isOther) {
                    other.setValue(other.getValue() + consumeVOMoney);
                }
            }
        }

        for (int i = 0; i < list_Data.size(); i++) {
            if (list_Data.get(i).getKey().equals("其他")) {
                f[i] = other.getValue().floatValue();
                continue;
            }
            if (hashMap.get(list_Data.get(i).getKey()) == null) {
                f[i] = 0;
                continue;
            }
            f[i] = hashMap.get(list_Data.get(i).getKey()).floatValue();
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
                    .addText(" 目標 : " + goalVO.getName() + goalVO.getTimeStatue() + goalVO.getType() + Common.Currency().get(goalVO.getCurrency()) + goalVO.getRealMoney())
                    .build();
            goalConsume.setBootstrapText(bootstrapText);
            yAxis.removeAllLimitLines();
            LimitLine yLimitLine = new LimitLine(Max.floatValue(), "支出目標");
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

        otherMessage.setText(DesTittle);
        setCurrency.setText(Common.CurrencyResult(total, currencyVO));


        ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();
        ShowZero = true;
        for (int i = 0; i < list_Data.size(); i++) {
            if (list_Data.get(i).getValue() > 0) {
                ShowZero = false;
                yVals1.add(new PieEntry(list_Data.get(i).getValue().floatValue(), list_Data.get(i).getKey()));
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
                dayWeek = end.get(Calendar.DAY_OF_WEEK);
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
                dayWeek = end.get(Calendar.DAY_OF_WEEK);
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
            dayWeek = end.get(Calendar.DAY_OF_WEEK);
            day = end.get(Calendar.DAY_OF_MONTH);
            Log.d(TAG, "day" + Common.sDay.format(new Date(end.getTimeInMillis())) + " : " + dayWeek);
            if (position == 0) {
                period = 1;
            } else if (position == 1) {
                if (week == 1) {
                    dayWeek = 1;
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
            if (e.getY() <= 0||!oneShow) {
                return;
            }

            if (Statue == 2) {
                week = (int) (e.getX() + 1);
                Statue = 1;
                if (week == 1) {
                    Calendar calendar = new GregorianCalendar(year, month, 1, 0, 0, 0);
                    dayWeek=1;
                    day = 1;
                    period=7-calendar.get(Calendar.DAY_OF_WEEK)+1;
                } else if (week == period) {
                    Calendar calendar = new GregorianCalendar(year, month, 1);
                    calendar.set(Calendar.WEEK_OF_MONTH, week);
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    period = calendar.getMaximum(Calendar.DAY_OF_MONTH) - day+1;
                    dayWeek=1;
                } else {
                    period=7;
                    Calendar calendar = new GregorianCalendar(year, month, 1);
                    calendar.set(Calendar.WEEK_OF_MONTH, week);
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    dayWeek=1;
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
                bundle.putSerializable("dweek", dayWeek);
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
            bundle.putSerializable("dweek", dayWeek);
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
            dayWeek = end.get(Calendar.DAY_OF_WEEK);
            day = end.get(Calendar.DAY_OF_MONTH);
            Log.d(TAG, "day" + Common.sDay.format(new Date(end.getTimeInMillis())) + " : " + dayWeek);
            if (position == 0) {
                period = 1;
            } else if (position == 1) {
                if (week == 1) {
                    dayWeek = 1;
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

    private class choiceCurrency implements PopupMenu.OnMenuItemClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case 1:
                    nowCurrency = "TWD";
                    sharedPreferences.edit().putString(choiceCurrency, nowCurrency).apply();
                    currencyVO = currencyDB.getBytimeAndType(startPopup.getTimeInMillis(), endPopup.getTimeInMillis(), nowCurrency);
                case 8:
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

}

package com.chargeapp.whc.chargeapp.Control;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.R;
import static com.chargeapp.whc.chargeapp.Control.Common.*;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;



import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static com.chargeapp.whc.chargeapp.Control.Common.Currency;
import static com.chargeapp.whc.chargeapp.Control.Common.doubleRemoveZero;

/**
 * Created by Wang on 2018/3/17.
 */

public class HomePage extends Fragment {
    private PieChart pieChart;
    private TextView pieChartT;
    private GoalDB goalDB;
    private InvoiceDB invoiceDB;
    private ConsumeDB consumeDB;
    private BankDB bankDB;
    private  Calendar start, end;
    private ListView listView;
    private int year, month, day;
    private boolean ShowZero;
    private ArrayList<PieEntry> yVals1;
    private ArrayList<String> Okey;
    private Activity context;
    private BootstrapButton currency;
    private SharedPreferences sharedPreferences;
    private String nowCurrency;
    private PopupMenu popupMenu;
    private CurrencyDB currencyDB;
    private CurrencyVO currencyVO;
    private double total;


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
        context.setTitle(R.string.text_Home);

        //set View
        View view = inflater.inflate(R.layout.home_page, container, false);
        pieChart = view.findViewById(R.id.pieChart);
        pieChartT = view.findViewById(R.id.pieChartT);
        listView = view.findViewById(R.id.list);
        currency=view.findViewById(R.id.currency);

        //DB
        Common.setChargeDB(context);
        Common.setScreen(Common.screenSize, context);
        goalDB = new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankDB = new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());


        //找出現在選擇Currency
        sharedPreferences = context.getSharedPreferences("Charge_User", Context.MODE_PRIVATE);
        nowCurrency = sharedPreferences.getString("choiceCurrency", "TWD");
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        popupMenu=new PopupMenu(context,currency);
        Common.createCurrencyPopMenu(popupMenu, context);
        currency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
        popupMenu.setOnMenuItemClickListener(new choiceCurrency());


        return view;
    }



    private void setListLayout() {
        List<GoalVO> goalVOS = goalDB.getNoCompleteAll();
        List<Object> objects = new ArrayList<>();
        if (goalVOS.size() > 0) {
            objects.addAll(goalVOS);
        } else {
            objects.add("本周花費");
            objects.add("本月花費");
        }
        ListAdapter listAdapter = (ListAdapter) listView.getAdapter();
        if (listAdapter == null) {
            listView.setAdapter(new ListAdapter(context, objects));
        } else {
            listAdapter.setObjects(objects);
            listAdapter.notifyDataSetChanged();
            listView.invalidate();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //init day
        end = Calendar.getInstance();
        year = end.get(Calendar.YEAR);
        month = end.get(Calendar.MONTH);
        day = end.get(Calendar.DAY_OF_MONTH);
        start = new GregorianCalendar(year, month, day, 0, 0, 0);
        end = new GregorianCalendar(year, month, day, 23, 59, 59);
        currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
        setListLayout();
        setPieChart();
    }


    private void setPieChart() {
        HashMap<String, Double> consumeVOS = consumeDB.getTimePeriodHashMap(start.getTimeInMillis(), end.getTimeInMillis());
        HashMap<String, Double> invoiceVOS = invoiceDB.getInvoiceByTimeHashMap(start.getTimeInMillis(), end.getTimeInMillis());

        if (invoiceVOS.size() > consumeVOS.size()) {
            for (String s : consumeVOS.keySet()) {
                if (invoiceVOS.get(s) == null) {
                    invoiceVOS.put(s, consumeVOS.get(s));
                } else {
                    invoiceVOS.put(s, invoiceVOS.get(s) + consumeVOS.get(s));
                }
            }
            addData(invoiceVOS);
        } else {
            for (String s : invoiceVOS.keySet()) {
                if (consumeVOS.get(s) == null) {
                    consumeVOS.put(s, invoiceVOS.get(s));
                } else {
                    consumeVOS.put(s, invoiceVOS.get(s) + consumeVOS.get(s));
                }
            }
            addData(consumeVOS);
        }

        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);
        pieChart.setRotationAngle(30);
        pieChart.setRotationEnabled(true);
        pieChart.setDescription(Common.getDeescription());
        // customize legends
        pieChart.getLegend().setEnabled(false);


        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }

    @SuppressLint("SetTextI18n")
    private void addData(HashMap<String, Double> consumeVOS) {
        total=consumeVOS.get("total");
        pieChartT.setText(Common.sDay.format(new Date(end.getTimeInMillis())) + "本日花費 ");
        currency.setText(Common.getCurrency(nowCurrency)+" "+doubleRemoveZero(total/Double.valueOf(currencyVO.getMoney())));
        yVals1 = new ArrayList<>();
        Okey = new ArrayList<>();
        ShowZero = true;
        Double total = consumeVOS.get("total");
        consumeVOS.remove("total");
        ChartEntry chartEntry = new ChartEntry("其他", 0.0);
        int i = 0;
        for (String key : consumeVOS.keySet()) {
            double part = 0.0;
            if (total > 0) {
                ShowZero = false;
                part = (consumeVOS.get(key) * 100 / total);
            }
            if (i < 4 && part > 2 && (!key.equals("O")) && (!key.equals("0"))) {
                yVals1.add(new PieEntry(consumeVOS.get(key).floatValue(), key));
                i++;
            } else {
                chartEntry.setValue(chartEntry.getValue() + consumeVOS.get(key));
                Okey.add(key);
            }
        }
        if (chartEntry.getValue() > 0) {
            yVals1.add(new PieEntry(chartEntry.getValue().floatValue(), chartEntry.getKey()));
        }
        // create pie data set
        final PieDataSet dataSet = new PieDataSet(yVals1, "種類");
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
        switch (Common.screenSize) {
            case xLarge:
                dataSet.setValueTextSize(30f);
                pieChart.setEntryLabelTextSize(30f);
                break;
            case large:
                dataSet.setValueTextSize(25f);
                pieChart.setEntryLabelTextSize(25f);
                break;
            case normal:
                dataSet.setValueTextSize(15f);
                pieChart.setEntryLabelTextSize(15f);
                break;
        }

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.setBackgroundColor(Color.parseColor("#f5f5f5"));
        pieChart.setOnChartValueSelectedListener(new pieValue());
    }


    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<Object> objects;

        ListAdapter(Context context, List<Object> objects) {
            this.context = context;
            this.objects = objects;
        }

        public void setObjects(List<Object> objects) {
            this.objects = objects;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.home_page_item, parent, false);
            }
            Object o = objects.get(position);
            ImageView imageView = itemView.findViewById(R.id.ivImage);
            TextView title = itemView.findViewById(R.id.goal);
            TextView describe = itemView.findViewById(R.id.describe);
            BootstrapButton resultT = itemView.findViewById(R.id.resultT);
            LinearLayout resultL = itemView.findViewById(R.id.resultL);
            if (o instanceof GoalVO) {
                double consumeCount = 0;
                Calendar start, end;
                GoalVO goalVO = (GoalVO) o;
                CurrencyVO goalCurrencyVO = null;
                imageView.setImageResource(R.drawable.goal);
                String timeStatue = goalVO.getTimeStatue().trim();
                StringBuffer describeContent = new StringBuffer();
                Double transFormCurrency;

                if (goalVO.getType().trim().equals("支出")) {

                    if (timeStatue.equals("每天")) {
                        consumeCount = consumeDB.getTimeMaxType(HomePage.this.start.getTimeInMillis(),HomePage.this.end.getTimeInMillis()).get("total") +
                                invoiceDB.getInvoiceByTimeMaxType(HomePage.this.start.getTimeInMillis(), HomePage.this.end.getTimeInMillis()).get("total");
                        describeContent.append("花費 : 本日支出" +Common.getCurrency(nowCurrency)+" ");
                        transFormCurrency=consumeCount/Double.valueOf(currencyVO.getMoney());
                        describeContent.append(doubleRemoveZero(transFormCurrency));
                        goalCurrencyVO=currencyDB.getBytimeAndType(HomePage.this.start.getTimeInMillis(),HomePage.this.end.getTimeInMillis(),goalVO.getCurrency());
                    } else if (timeStatue.equals("每周")) {
                        int dayWeek = HomePage.this.start.get(Calendar.DAY_OF_WEEK);
                        start = new GregorianCalendar(year, month, day - dayWeek + 1, 0, 0, 0);
                        end = new GregorianCalendar(year, month, day - dayWeek + 7, 23, 59, 59);
                        consumeCount = consumeDB.getTimeMaxType(start.getTimeInMillis(),end.getTimeInMillis()).get("total") +
                                invoiceDB.getInvoiceByTimeMaxType(start.getTimeInMillis(), end.getTimeInMillis()).get("total");
                        describeContent.append("花費 : 本周支出" +Common.getCurrency(nowCurrency)+" ");
                        transFormCurrency=consumeCount/Double.valueOf(currencyVO.getMoney());
                        describeContent.append(doubleRemoveZero(transFormCurrency));
                        goalCurrencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),goalVO.getCurrency());
                    } else if (timeStatue.equals("每月")) {
                        int max = HomePage.this.start.getActualMaximum(Calendar.DAY_OF_MONTH);
                        start = new GregorianCalendar(year, month, 1, 0, 0, 0);
                        end = new GregorianCalendar(year, month, max, 23, 59, 59);
                        consumeCount = consumeDB.getTimeMaxType(start.getTimeInMillis(),end.getTimeInMillis()).get("total") +
                                invoiceDB.getInvoiceByTimeMaxType(start.getTimeInMillis(), end.getTimeInMillis()).get("total");
                        describeContent.append("花費 : 本月支出"  +Common.getCurrency(nowCurrency)+" ");
                        transFormCurrency=consumeCount/Double.valueOf(currencyVO.getMoney());
                        describeContent.append(doubleRemoveZero(transFormCurrency));
                        goalCurrencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),goalVO.getCurrency());
                    } else if (timeStatue.equals("每年")) {
                        start = new GregorianCalendar(year, 0, 1, 0, 0, 0);
                        end = new GregorianCalendar(year, 11, 31, 23, 59, 59);
                        consumeCount = consumeDB.getTimeMaxType(start.getTimeInMillis(), end.getTimeInMillis()).get("total") +
                                invoiceDB.getInvoiceByTimeMaxType(start.getTimeInMillis(),end.getTimeInMillis()).get("total");
                        describeContent.append("花費 : 本年支出"  +Common.getCurrency(nowCurrency)+" ");
                        transFormCurrency=consumeCount/Double.valueOf(currencyVO.getMoney());
                        describeContent.append(doubleRemoveZero(transFormCurrency));
                        goalCurrencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),goalVO.getCurrency());
                    }

                    //設定Title
                    double goalVOMoney=(Double.valueOf(goalVO.getRealMoney())*Double.valueOf(goalCurrencyVO.getMoney()))/Double.valueOf(currencyVO.getMoney());
                    consumeCount=consumeCount/Double.valueOf(currencyVO.getMoney());
                    title.setText("目標 : " + goalVO.getName().trim() + goalVO.getTimeStatue().trim() + "支出" +getCurrency(nowCurrency)+" "+doubleRemoveZero(goalVOMoney));


                    if (goalVOMoney > consumeCount) {
                        resultT.setText("達成");
                        resultT.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);

                    } else {
                        resultT.setText("失敗");
                        resultT.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                    }
                    describe.setText(describeContent.toString());
                } else {

                    if (timeStatue.equals("今日")) {
                        consumeCount = consumeDB.getTimeMaxType(goalVO.getStartTime().getTime(), goalVO.getEndTime().getTime()).get("total") +
                                invoiceDB.getInvoiceByTimeMaxType(goalVO.getStartTime().getTime(), goalVO.getEndTime().getTime()).get("total");
                        Double saveMoney = bankDB.getTimeTotal(goalVO.getStartTime().getTime(), goalVO.getEndTime().getTime()) - consumeCount;
                        saveMoney=saveMoney/Double.valueOf(currencyVO.getMoney());
                        goalCurrencyVO=currencyDB.getBytimeAndType(goalVO.getStartTime().getTime(),goalVO.getEndTime().getTime(),goalVO.getCurrency());
                        double goalVOMoney=(Double.valueOf(goalVO.getRealMoney())*Double.valueOf(goalCurrencyVO.getMoney()))/Double.valueOf(currencyVO.getMoney());
                        String saveMoneyResult=Common.CurrencyResult(saveMoney,currencyVO);
                        String goalVOMoneyResult=Common.CurrencyResult(goalVOMoney,currencyVO);

                        if (goalVO.getEndTime().getTime() < System.currentTimeMillis()) {
                            title.setText("目標 : " + goalVO.getName() + "\n" + Common.sTwo.format(goalVO.getEndTime()) + "前 儲蓄"+goalVOMoneyResult);
                            if (goalVOMoney < saveMoney) {
                                goalVO.setStatue(1);
                                goalVO.setNotify(false);
                                describeContent.append(Common.sTwo.format(goalVO.getEndTime()) + "前\n已儲蓄" +saveMoneyResult);
                                resultT.setText("達成");
                                resultT.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                            } else {
                                goalVO.setStatue(2);
                                goalVO.setNotify(false);
                                describeContent.append(Common.sTwo.format(goalVO.getEndTime()) + "前\n已儲蓄" +saveMoneyResult);
                                resultT.setText("失敗");
                                resultT.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                            }
                            Log.d("goal", "homepage" + goalVO.getStatue());
                            goalDB.update(goalVO);
                        } else {

                            title.setText("目標 : " + goalVO.getName() + "\n" + Common.sTwo.format(goalVO.getEndTime()) + "前 儲蓄"+goalVOMoneyResult);
                            double remainDay = Double.valueOf(goalVO.getEndTime().getTime() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
                            if (remainDay < 1) {
                                double remainHour = remainDay * 24;
                                if (remainHour < 1) {
                                    double remainMin = remainHour * 60;
                                    describeContent.append(" 倒數" + (int) remainMin + "分鐘");

                                } else {
                                    describeContent.append(" 倒數" + (int) remainHour + "小時");
                                }

                            } else {
                                describeContent.append(" 倒數" + (int) remainDay + "天");
                            }
                            if (goalVOMoney < saveMoney) {
                                goalVO.setStatue(1);
                                describeContent.append("\n 目前已儲蓄" + saveMoneyResult);
                                goalDB.update(goalVO);
                                resultT.setText("達成");
                                resultT.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                            } else {
                                describeContent.append("\n 目前已儲蓄" + saveMoneyResult);
                                resultT.setText("進行中");
                                resultT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                            }
                        }
                        describe.setText(describeContent.toString());

                    } else if (timeStatue.equals("每月")) {
                        int max = HomePage.this.start.getActualMaximum(Calendar.DAY_OF_MONTH);
                        start = new GregorianCalendar(year, month, 1, 0, 0, 0);
                        end = new GregorianCalendar(year, month, max, 23, 59, 59);
                        consumeCount = consumeDB.getTimeMaxType(start.getTimeInMillis(),end.getTimeInMillis()).get("total") +
                                invoiceDB.getInvoiceByTimeMaxType(start.getTimeInMillis(), end.getTimeInMillis()).get("total");
                        goalCurrencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),goalVO.getCurrency());

                        Double saveMoney = bankDB.getTimeTotal(start.getTimeInMillis(),end.getTimeInMillis()) - consumeCount;
                        saveMoney=saveMoney/Double.valueOf(currencyVO.getMoney());
                        double goalVOMoney=(Double.valueOf(goalVO.getRealMoney())*Double.valueOf(goalCurrencyVO.getMoney()))/Double.valueOf(currencyVO.getMoney());
                        String saveMoneyResult=Common.CurrencyResult(saveMoney,currencyVO);
                        String goalVOMoneyResult=Common.CurrencyResult(goalVOMoney,currencyVO);

                        title.setText(" 目標 :" + goalVO.getName() + " 每月儲蓄" +goalVOMoneyResult);
                        describe.setText(" 目前 : 本月\n 已存款" +saveMoneyResult);
                        if (goalVOMoney < saveMoney) {
                            resultT.setText("達成");
                            resultT.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                        } else {
                            resultT.setText("失敗");
                            resultT.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                        }
                    } else if (timeStatue.equals("每年")) {
                        start = new GregorianCalendar(year, 0, 1, 0, 0, 0);
                        end = new GregorianCalendar(year, 11, 31, 23, 59, 59);
                        consumeCount =consumeDB.getTimeMaxType(start.getTimeInMillis(), end.getTimeInMillis()).get("total") +
                                invoiceDB.getInvoiceByTimeMaxType(start.getTimeInMillis(),end.getTimeInMillis()).get("total");
                        goalCurrencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),goalVO.getCurrency());

                        Double saveMoney = bankDB.getTimeTotal(start.getTimeInMillis(), end.getTimeInMillis()) - consumeCount;
                        saveMoney=saveMoney/Double.valueOf(currencyVO.getMoney());
                        double goalVOMoney=(Double.valueOf(goalVO.getRealMoney())*Double.valueOf(goalCurrencyVO.getMoney()))/Double.valueOf(currencyVO.getMoney());
                        String saveMoneyResult=Common.CurrencyResult(saveMoney,currencyVO);
                        String goalVOMoneyResult=Common.CurrencyResult(goalVOMoney,currencyVO);

                        title.setText(" 目標 :" + goalVO.getName() + " 每年儲蓄" +goalVOMoneyResult);
                        describe.setText(" 目前 : 本年\n 已存款" + saveMoneyResult);
                        if (goalVOMoney < saveMoney) {
                            resultT.setText("達成");
                            resultT.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                        } else {
                            resultT.setText("失敗");
                            resultT.setTextColor(Color.parseColor("#DC143C"));
                            resultT.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                        }
                    }

                }

            } else {

                //沒有目標顯示
                //顯示本周花費
                double consumeCount;
                resultL.setVisibility(View.GONE);
                HashMap<String,Double> invoiceHashMap,consumeHashMap;
                Set<String> totalKey=new HashSet<>();
                if (position == 0) {
                    //顯示本周花費
                    imageView.setImageResource(R.drawable.bouns);
                    Calendar calendar = Calendar.getInstance();
                    int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    start = new GregorianCalendar(year, month, day - dayWeek + 1, 0, 0, 0);
                    end = new GregorianCalendar(year, month, day - dayWeek + 7, 23, 59, 59);
                    invoiceHashMap= invoiceDB.getInvoiceByTimeMaxType(start.getTimeInMillis(), end.getTimeInMillis());
                    consumeHashMap= consumeDB.getTimeMaxType(start.getTimeInMillis(),end.getTimeInMillis());
                    consumeCount = (invoiceHashMap.get("total")+consumeHashMap.get("total"))/Double.valueOf(currencyVO.getMoney());
                    title.setText("本周支出 : " + Common.CurrencyResult(consumeCount,currencyVO));
                } else {
                    //顯示本月花費
                    imageView.setImageResource(R.drawable.lotto);
                    int max = HomePage.this.start.getActualMaximum(Calendar.DAY_OF_MONTH);
                    start = new GregorianCalendar(year, month, 1, 0, 0, 0);
                    end = new GregorianCalendar(year, month, max, 23, 59, 59);
                    invoiceHashMap= invoiceDB.getInvoiceByTimeMaxType(start.getTimeInMillis(), end.getTimeInMillis());
                    consumeHashMap= consumeDB.getTimeMaxType(start.getTimeInMillis(), end.getTimeInMillis());
                    consumeCount = (invoiceHashMap.get("total")+consumeHashMap.get("total"))/Double.valueOf(currencyVO.getMoney());
                    title.setText("本月支出 : " + Common.CurrencyResult(consumeCount,currencyVO));
                }
                totalKey.addAll(invoiceHashMap.keySet());
                totalKey.addAll(consumeHashMap.keySet());
                totalKey.remove("total");
                ChartEntry chartEntry=new ChartEntry("Max",0.0);
                Double invoiceMoney,consumeMoney,total;
                if (totalKey.size() > 0) {
                    describe.setVisibility(View.VISIBLE);
                    for(String key:totalKey)
                    {
                        invoiceMoney=(invoiceHashMap.get(key)==null?0.0:invoiceHashMap.get(key));
                        consumeMoney=(consumeHashMap.get(key)==null?0.0:consumeHashMap.get(key));
                        total=invoiceMoney+consumeMoney;
                        if(total>chartEntry.getValue())
                        {
                            chartEntry.setKey(key);
                            chartEntry.setValue(total);
                        }
                    }
                    String maxMoney=doubleRemoveZero(chartEntry.getValue()/Double.valueOf(currencyVO.getMoney()));
                    describe.setText("最多花費 : " + (chartEntry.getKey().equals("O") ? "其他" : chartEntry.getKey()) + " "+Common.getCurrency(nowCurrency)+" "+maxMoney);
                    describe.setVisibility(View.VISIBLE);
                } else {
                    describe.setVisibility(View.GONE);
                }
            }
            return itemView;
        }
    }

    private class pieValue implements OnChartValueSelectedListener {
        @Override
        public void onValueSelected(Entry e, Highlight h) {

            if (ShowZero) {
                Fragment fragment = new InsertActivity();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                for (Fragment fragment1 : getFragmentManager().getFragments()) {
                    fragmentTransaction.remove(fragment1);
                }
                fragmentTransaction.replace(R.id.body, fragment);
                fragmentTransaction.commit();
                return;
            } else {
                MainActivity.firstShowInsertActivity=false;
                String key = yVals1.get((int) h.getX()).getLabel();
                Bundle bundle = new Bundle();
                Fragment fragment = new HomePagetList();
                bundle.putStringArrayList("OKey", Okey);
                bundle.putSerializable("key", key);
                bundle.putSerializable("total", (int) h.getY());
                bundle.putSerializable("position", 0);
                fragment.setArguments(bundle);
                switchFragment(fragment);
            }
        }

        @Override
        public void onNothingSelected() {

        }
    }

    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("HomePage");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

    private class choiceCurrency implements PopupMenu.OnMenuItemClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case 1:
                    nowCurrency = "TWD";
                    sharedPreferences.edit().putString(choiceCurrency, nowCurrency).apply();
                    currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
                    currency.setText(Common.getCurrency(nowCurrency)+" "+doubleRemoveZero(total*Double.valueOf(currencyVO.getMoney())));
                case 8:
                    popupMenu.dismiss();
                    break;
                default:
                    nowCurrency = Common.code.get(menuItem.getItemId() - 2);
                    sharedPreferences.edit().putString(choiceCurrency, nowCurrency).apply();
                    currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
                    currency.setText(Common.getCurrency(nowCurrency)+" "+doubleRemoveZero(total*Double.valueOf(currencyVO.getMoney())));
                    break;
            }
            setListLayout();
            return true;
        }
    }
}





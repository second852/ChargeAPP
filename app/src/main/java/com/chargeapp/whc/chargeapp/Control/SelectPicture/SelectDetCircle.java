package com.chargeapp.whc.chargeapp.Control.SelectPicture;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import static com.chargeapp.whc.chargeapp.Control.Common.choiceCurrency;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectDetCircle extends Fragment {


    private InvoiceDB invoiceDB;
    private ConsumeDB consumeDB;
    private ListView listView;
    private boolean ShowConsume;
    private boolean ShowAllCarrier;
    private boolean noShowCarrier;
    private int year, month, day, index,statue,dweek;
    private int carrier;
    private List<InvoiceVO> invoiceVOS;
    private List<ConsumeVO> consumeVOS;
    private HashMap<String, HashMap<String, Double>> hashMap;
    private int size;
    private CarrierDB carrierDB;
    private Calendar start,end;
    private Activity context;

    //新增幣別
    private AwesomeTextView otherMessage;
    private BootstrapButton setCurrency;
    private SharedPreferences sharedPreferences;
    private String nowCurrency;
    private CurrencyDB currencyDB;
    private CurrencyVO currencyVO;
    private double total;
    private PopupMenu popupMenu;
    private List<String> stringList;
    private LinearLayout resultL;




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.context=(Activity) context;
        }else{
            this.context=getActivity();
        }
        Utils.init(this.context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.select_con_detail, container, false);
        sharedPreferences=context.getSharedPreferences("Charge_User",Context.MODE_PRIVATE);
        nowCurrency=sharedPreferences.getString(Common.choiceCurrency,"TWD");
        setDB();
        findViewById(view);

        popupMenu=new PopupMenu(context,setCurrency);
        Common.createCurrencyPopMenu(popupMenu, context);
        setCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
        popupMenu.setOnMenuItemClickListener(new choiceCurrency());

        hashMap = new HashMap<>();
        ShowConsume = (boolean) getArguments().getSerializable("ShowConsume");
        ShowAllCarrier = (boolean) getArguments().getSerializable("ShowAllCarrier");
        noShowCarrier = (boolean) getArguments().getSerializable("noShowCarrier");
        year = (int) getArguments().getSerializable("year");
        month = (int) getArguments().getSerializable("month");
        day = (int) getArguments().getSerializable("day");
        index = (int) getArguments().getSerializable("index");
        carrier = (int) getArguments().getSerializable("carrier");
        statue= (int) getArguments().getSerializable("statue");
        dweek= (int) getArguments().getSerializable("dweek");
        List<CarrierVO> carrierVOS=carrierDB.getAll();
        if(carrierVOS.size()<=0)
        {
            noShowCarrier=true;
        }

        if(statue==1) {
            day = day - dweek + 1 + index;
        }
        start = new GregorianCalendar(year, month, day , 0, 0, 0);
        end = new GregorianCalendar(year, month, day, 23, 59, 59);
        currencyVO =currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);

        total = 0;
        if (ShowConsume) {
            consumeVOS = consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
            for (ConsumeVO c : consumeVOS) {
                CurrencyVO currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),c.getCurrency());
                if (hashMap.get(c.getMaintype()) == null) {
                    HashMap<String, Double> second = new HashMap<>();
                    second.put(c.getSecondType(), Double.valueOf(c.getRealMoney())*Double.valueOf(currencyVO.getMoney()));
                    hashMap.put(c.getMaintype(), second);
                } else {
                    HashMap<String, Double> second = hashMap.get(c.getMaintype());
                    if (second.get(c.getSecondType()) == null) {
                        second.put(c.getSecondType(), Double.valueOf(c.getRealMoney())*Double.valueOf(currencyVO.getMoney()));
                    } else {
                        second.put(c.getSecondType(), second.get(c.getSecondType()) + Double.valueOf(c.getRealMoney())*Double.valueOf(currencyVO.getMoney()));
                    }
                }
                total = total + Double.valueOf(c.getRealMoney())*Double.valueOf(currencyVO.getMoney());
            }
        }
        if (!noShowCarrier) {
            String carNul=carrierVOS.get(carrier).getCarNul();
            if (ShowAllCarrier) {
                invoiceVOS = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
            } else {
                invoiceVOS = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), carNul);
            }
            for (InvoiceVO I : invoiceVOS) {
                CurrencyVO currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),I.getCurrency());
                if (hashMap.get(I.getMaintype()) == null) {
                    HashMap<String, Double> second = new HashMap<>();
                    second.put(I.getSecondtype(), Double.valueOf(I.getRealAmount())*Double.valueOf(currencyVO.getMoney()));
                    hashMap.put(I.getMaintype(), second);
                } else {
                    HashMap<String, Double> second = hashMap.get(I.getMaintype());
                    if (second.get(I.getSecondtype()) == null) {
                        second.put(I.getSecondtype(),  Double.valueOf(I.getRealAmount())*Double.valueOf(currencyVO.getMoney()));
                    } else {
                        second.put(I.getSecondtype(), second.get(I.getSecondtype()) + Double.valueOf(I.getRealAmount())*Double.valueOf(currencyVO.getMoney()));
                    }
                }

                total = total +  Double.valueOf(I.getRealAmount())*Double.valueOf(currencyVO.getMoney());
            }
        }
        context.setTitle(Common.sOne.format(new Date(start.getTimeInMillis())));
        stringList = new ArrayList<>(hashMap.keySet());
        size = stringList.size();
        changeCurrency();
        Common.setScreen(Common.screenSize,context);

        otherMessage.setBootstrapBrand(null);
        otherMessage.setTextColor(Color.BLACK);
        otherMessage.setText("總共:");
        return view;
    }


    private void changeCurrency()
    {
        setCurrency.setText(Common.CurrencyResult(total,currencyVO));
        listView.setAdapter(new ListAdapter(context, stringList));
    }


    private void findViewById(View view) {
        listView = view.findViewById(R.id.listCircle);
        otherMessage=view.findViewById(R.id.otherMessage);
        setCurrency=view.findViewById(R.id.setCurrency);
        resultL=view.findViewById(R.id.resultL);
        resultL.setY(10f);
        listView.setY(20f);
    }

    private void setDB() {
        Common.setChargeDB(context);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        currencyDB = new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());
    }

    private PieData addData(String key, TextView detail) {
        ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();
        HashMap<String, Double> second = hashMap.get(key);
        double total = 0;
        for (String s : second.keySet()) {
            if (s.equals("O")) {
                yVals1.add(new PieEntry(second.get(s).floatValue(), "其他"));
            } else if(s.equals("0")){
                yVals1.add(new PieEntry(second.get(s).floatValue(), "未知"));
            }else{
                yVals1.add(new PieEntry(second.get(s).floatValue(), s));
            }
            total = total + second.get(s);
        }
        if (key.equals("O")) {
            detail.setText("其他 : " +Common.CurrencyResult(total,currencyVO));
        } else if(key.equals("0")){
            detail.setText("未知 : " + Common.CurrencyResult(total,currencyVO));
        }else{
            detail.setText(key + " : " + Common.CurrencyResult(total,currencyVO));
        }
        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "種類");
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(50);
        dataSet.setColors(Common.getColor(yVals1.size()));
        dataSet.setValueLinePart1OffsetPercentage(90.f);
        dataSet.setValueLinePart1Length(1f);
        dataSet.setValueLinePart2Length(.2f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        // instantiate pie data object now
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
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
           return size;
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.select_con_detail_item, parent, false);
            }
            final PieChart pieChart = itemView.findViewById(R.id.pieChart);
            TextView detail = itemView.findViewById(R.id.detail);
            String key = KeyList.get(position);
            pieChart.setData(addData(key, detail));
            pieChart.setOnChartValueSelectedListener(new changeToNewF(key));
            pieChart.setDescription(Common.getDeescription());
            pieChart.highlightValues(null);
            pieChart.setUsePercentValues(true);
            // enable hole and configure
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleRadius(7);
            pieChart.setTransparentCircleRadius(10);

            // enable rotation of the chart by touch
            Legend l = pieChart.getLegend();
            l.setEnabled(false);

            pieChart.setRotationAngle(60);
            pieChart.setRotationEnabled(true);
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.setBackgroundColor(Color.parseColor("#f5f5f5"));
            PieDataSet dataSet= (PieDataSet) pieChart.getData().getDataSet();
            switch (Common.screenSize){
                case xLarge:
                    dataSet.setValueTextSize(25f);
                    pieChart.setEntryLabelTextSize(25f);
                    break;
                case large:
                    dataSet.setValueTextSize(20f);
                    pieChart.setEntryLabelTextSize(20f);
                    break;
                case normal:
                    dataSet.setValueTextSize(12f);
                    pieChart.setEntryLabelTextSize(12f);
                    break;

            }

            pieChart.notifyDataSetChanged();
            pieChart.invalidate();
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
            bundle.putSerializable("day", day );
            bundle.putSerializable("key", key);
            bundle.putSerializable("carrier", carrier);
            bundle.putSerializable("index",index);
            bundle.putSerializable("statue", 0);
            bundle.putSerializable("action", "SelectDetCircle");
            bundle.putSerializable("position",0);
            bundle.putSerializable("period", getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
            fragment.setArguments(bundle);
            switchFragment(fragment);
        }

        @Override
        public void onNothingSelected() {

        }
    }

    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("SelectDetCircle");
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
                case 8:
                    popupMenu.dismiss();
                    break;
                default:
                    nowCurrency = Common.code.get(menuItem.getItemId() - 2);
                    sharedPreferences.edit().putString(choiceCurrency, nowCurrency).apply();
                    currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
                    break;
            }
            changeCurrency();
            return true;
        }
    }

}

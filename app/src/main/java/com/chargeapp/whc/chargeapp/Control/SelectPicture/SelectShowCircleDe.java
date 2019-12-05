package com.chargeapp.whc.chargeapp.Control.SelectPicture;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Adapter.DeleteDialogFragment;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateInvoice;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateSpend;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
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
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import org.jsoup.internal.StringUtil;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import static com.chargeapp.whc.chargeapp.Control.Common.choiceCurrency;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectShowCircleDe extends Fragment {


    private InvoiceDB invoiceDB;
    private ConsumeDB consumeDB;
    private boolean ShowConsume ;
    private boolean ShowAllCarrier;
    private boolean noShowCarrier;
    private int year,month,day,period,dweek;
    private int  carrier;
    private  List<InvoiceVO> invoiceVOS;
    private List<ConsumeVO> consumeVOS;
    private String mainTitle;
    private HashMap<String,Double> hashMap;

    private int Statue;
    private Calendar start,end;
    private PieChart pieChart;
    private ProgressDialog progressDialog;
    private String title;
    private CarrierDB carrierDB;
    private List<CarrierVO> carrierVOS;
    private ArrayList<String> Okey;
    private boolean ShowZero;
    private List<PieEntry> yVals1;
    private Activity activity;

    //新增幣別
    private AwesomeTextView otherMessage;
    private BootstrapButton setCurrency;
    private SharedPreferences sharedPreferences;
    private String nowCurrency;
    private CurrencyDB currencyDB;
    private CurrencyVO currencyVO;
    private double total;
    private PopupMenu popupMenu;
    private HorizontalBarChart chart_hor;
    private boolean oneShow;
    private float lastX,lastY;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            activity= (Activity) context;
        }else {
            activity=getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_circle_detail, container, false);
        sharedPreferences=activity.getSharedPreferences("Charge_User",Context.MODE_PRIVATE);
        nowCurrency=sharedPreferences.getString(Common.choiceCurrency,"TWD");
        setDB();
        carrierVOS=carrierDB.getAll();
        findViewById(view);
        popupMenu=new PopupMenu(activity,setCurrency);
        Common.createCurrencyPopMenu(popupMenu, activity);
        setCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
        popupMenu.setOnMenuItemClickListener(new choiceCurrency());


        progressDialog=new ProgressDialog(activity);
        ShowConsume= (boolean) getArguments().getSerializable("ShowConsume");
        ShowAllCarrier= (boolean) getArguments().getSerializable("ShowAllCarrier");
        noShowCarrier= (boolean) getArguments().getSerializable("noShowCarrier");
        year= (int) getArguments().getSerializable("year");
        month= (int) getArguments().getSerializable("month");
        day= (int) getArguments().getSerializable("day");
        carrier= (int) getArguments().getSerializable("carrier");
        Statue= (int) getArguments().getSerializable("statue");
        mainTitle= (String) getArguments().getSerializable("index");
        period= (int) getArguments().getSerializable("period");
        dweek= (int) getArguments().getSerializable("dweek");
        if(Statue==0)
        {
           start=new GregorianCalendar(year,month,day,0,0,0);
           end=new GregorianCalendar(year,month,day,23,59,59);
           title=Common.sTwo.format(new Date(start.getTimeInMillis()));
        }else if(Statue==1)
        {
            start=new GregorianCalendar(year,month,day - dweek + 1,0,0,0);
            end=new GregorianCalendar(year,month,day - dweek + 1 + period-1,23,59,59);
            title=Common.sTwo.format(new Date(start.getTimeInMillis()))+" ~ "+Common.sTwo.format(new Date(end.getTimeInMillis()));
        }else if(Statue==2)
        {
            start=new GregorianCalendar(year,month,1,0,0,0);
            end=new GregorianCalendar(year,month,start.getActualMaximum(Calendar.DAY_OF_MONTH),23,59,59);
            title=Common.sThree.format(new Date(start.getTimeInMillis()));
            title=title.substring(0,title.indexOf("月")+1);
        }else
        {
            start=new GregorianCalendar(year,0,1,0,0,0);
            end=new GregorianCalendar(year,11,31,23,59,59);
            title=Common.sThree.format(new Date(start.getTimeInMillis()));
            title=title.substring(0,title.indexOf("年")+1);
        }

        currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);

        setLayout();

        return view;
    }

    public void cancelshow(){
        progressDialog.cancel();
        Common.showToast(activity,"財政部網路忙線~");
    }



    private void findViewById(View view) {
        pieChart=view.findViewById(R.id.chart_pie);
        otherMessage=view.findViewById(R.id.otherMessage);
        setCurrency=view.findViewById(R.id.setCurrency);
        chart_hor=view.findViewById(R.id.chart_hor);
    }

    private void setDB() {
        Common.setChargeDB(activity);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB);
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB);
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB);
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);
    }

    private PieData addData() {
        int index=1;
        ArrayList<BarEntry> yHor = new ArrayList<BarEntry>();
        ArrayList<String> xHr=new ArrayList<String>();

        Okey=new ArrayList<>();
        yVals1 = new ArrayList<PieEntry>();

        ChartEntry chartEntry=new ChartEntry("其他",0.00);
        double part=0.0;
        for(String s:hashMap.keySet())
        {
            if(total>0)
            {
                part=(hashMap.get(s)*100/total);
            }
            if(s.equals("O"))
            {
                chartEntry.setValue(chartEntry.getValue()+hashMap.get(s));
                Okey.add(s);
            }else if(s.equals("0")){
                chartEntry.setValue(chartEntry.getValue()+hashMap.get(s));
                Okey.add(s);
            }else {
                if(part>6)
                {
                    yVals1.add(new PieEntry(hashMap.get(s).floatValue(),s));
                }else{
                    chartEntry.setValue(chartEntry.getValue()+hashMap.get(s));
                    Okey.add(s);
                }
            }
        }

        Collections.sort(yVals1, new Comparator<PieEntry>() {
            @Override
            public int compare(PieEntry pieEntry, PieEntry t1) {
                return (int) -(pieEntry.getValue()-t1.getValue());
            }
        });


        if(yVals1.size()>5)
        {
            while (yVals1.size()>5)
            {
                Okey.add(yVals1.get(4).getLabel());
                chartEntry.setValue(chartEntry.getValue()+yVals1.get(4).getValue());
                yVals1.remove(4);
            }
        }

        for(PieEntry pieEntry:yVals1)
        {
            yHor.add(new BarEntry(index++,pieEntry.getValue()));
            xHr.add(pieEntry.getLabel());
        }



        if(chartEntry.getValue()>0)
        {
            yVals1.add(new PieEntry(chartEntry.getValue().floatValue(),chartEntry.getKey()));
            yHor.add(new BarEntry(index++,chartEntry.getValue().floatValue()));
            xHr.add(chartEntry.getKey());
        }


        BarDataSet barDataSet1 = new BarDataSet(yHor, "");
        barDataSet1.setColors(Common.getColor(yHor.size()));
        BarData barData = new BarData(barDataSet1);
        barData.setBarWidth(0.9f);
        barData.setDrawValues(true);
        barData.setValueTextSize(12);


        XAxis xHAxis = chart_hor.getXAxis();
        xHAxis.setPosition(XAxis.XAxisPosition.BOTTOM );
        xHAxis.setGranularity(1f);
        xHAxis.setGranularityEnabled(true);
        YAxis yHAxis = chart_hor.getAxis(YAxis.AxisDependency.LEFT);
        yHAxis.setDrawLabels(false);
        YAxis yHAxis1 = chart_hor.getAxis(YAxis.AxisDependency.RIGHT);
        yHAxis.setAxisMinimum(0);
        yHAxis1.setAxisMinimum(0);
        xHAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                try {
                    return xHr.get((int) value-1);
                } catch (Exception e) {
                    return String.valueOf(value);
                }
            }
        });


        chart_hor.setFitBars(true);
        chart_hor.setDrawBarShadow(false);
        chart_hor.setDoubleTapToZoomEnabled(false);
        chart_hor.setHighlightFullBarEnabled(false);
        chart_hor.setDrawBarShadow(false);
        chart_hor.setDoubleTapToZoomEnabled(false);
        chart_hor.setDescription(Common.description);
        chart_hor.setHighlightFullBarEnabled(false);
        chart_hor.setData(barData);
        chart_hor.setNoDataText("沒有資料!");
        chart_hor.setNoDataTextColor(Color.BLACK);
        chart_hor.notifyDataSetChanged();
        chart_hor.invalidate();


        PieDataSet dataSet = new PieDataSet(yVals1, "種類");
        int size=yVals1.size();
        if(size<=0)
        {
            ShowZero=true;
            dataSet.setDrawValues(false);
            yVals1.add(new PieEntry(1, "無花費"));
            int[] c={Color.parseColor("#CCEEFF")};
            dataSet.setColors(c);
        }else{
            ShowZero=false;
            dataSet.setColors(Common.getColor(size));
            dataSet.setDrawValues(true);
            dataSet.setValueLinePart1OffsetPercentage(90.f);
            dataSet.setValueLinePart1Length(1f);
            dataSet.setValueLinePart2Length(.2f);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        }


        // create pie data set
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(30);
        // instantiate pie data object now
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextColor(Color.BLACK);
        return data;
    }

    private class PieValue implements OnChartValueSelectedListener {

        View view;

        public PieValue(View view) {
            this.view = view;
        }

        @Override
        public void onValueSelected(Entry e, Highlight h) {
            if (ShowZero||(!oneShow)) {
                return;
            }

            int id=view.getId();
            int index= (int) h.getX();
            if (id == R.id.chart_hor) {
                index = index - 1;
            }

            String key = yVals1.get(index).getLabel();
            Bundle bundle = new Bundle();
            Fragment fragment=new SelectShowCircleDeList();
            bundle.putStringArrayList("OKey", Okey);
            bundle.putSerializable("key",key);
            bundle.putSerializable("total",(int)h.getY());
            bundle.putSerializable("ShowConsume", ShowConsume);
            bundle.putSerializable("ShowAllCarrier", ShowAllCarrier);
            bundle.putSerializable("noShowCarrier", noShowCarrier);
            bundle.putSerializable("year", year);
            bundle.putSerializable("month", month);
            bundle.putSerializable("day", day);
            bundle.putSerializable("index", mainTitle);
            bundle.putSerializable("carrier",carrier);
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

    public void setLayout() {
        hashMap=new HashMap<>();
        total=0;


        if(ShowConsume)
        {
            consumeVOS=consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()),mainTitle);
            for(ConsumeVO c:consumeVOS)
            {
                CurrencyVO currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),c.getCurrency());
                if(StringUtil.isBlank(c.getRealMoney()))
                {
                    c.setRealMoney(String.valueOf(c.getMoney()));
                    consumeDB.update(c);
                }

                Double cMoney=Double.valueOf(c.getRealMoney())*Double.valueOf(currencyVO.getMoney())/Double.valueOf(c.getMoney());
                if(hashMap.get(c.getSecondType())==null)
                {
                    hashMap.put(c.getSecondType(),cMoney);
                }else{
                    hashMap.put(c.getSecondType(),hashMap.get(c.getSecondType())+cMoney);
                }
                total=total+cMoney;
            }
        }
        if(!noShowCarrier&&carrierVOS.size()>0)
        {
            if(ShowAllCarrier)
            {
                invoiceVOS=invoiceDB.getInvoiceBytimeMainType(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()),mainTitle);
            }else{
                invoiceVOS=invoiceDB.getInvoiceBytimeMainType(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()),mainTitle,carrierVOS.get(carrier).getCarNul());
            }
            for(InvoiceVO I:invoiceVOS)
            {
               CurrencyVO currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),I.getCurrency());
               Double iMoney= Double.valueOf(I.getRealAmount())*Double.valueOf(currencyVO.getMoney())/Double.valueOf(this.currencyVO.getMoney());
               if(hashMap.get(I.getSecondtype())==null)
               {
                    hashMap.put(I.getSecondtype(),iMoney);
               }else{
                    hashMap.put(I.getSecondtype(),hashMap.get(I.getSecondtype())+iMoney);
                }
                total= total+iMoney;
            }
        }

        TouchView touchView=new TouchView();
        pieChart.setOnChartValueSelectedListener(new PieValue(pieChart));
        pieChart.setOnTouchListener(touchView);
        chart_hor.setOnChartValueSelectedListener(new PieValue(chart_hor));
        chart_hor.setOnTouchListener(touchView);


        pieChart.setData(addData());
        pieChart.highlightValues(null);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);
        pieChart.setRotationAngle(30);
        pieChart.setRotationEnabled(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setDescription(Common.getDeescription());
        pieChart.getLegend().setEnabled(false);
        pieChart.setBackgroundColor(Color.parseColor("#f5f5f5"));
        PieDataSet dataSet = (PieDataSet) pieChart.getData().getDataSet();
        Common.setScreen(Common.screenSize,activity);
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

        activity.setTitle(title);

        //設定顯示幣別
        currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
        otherMessage.setBootstrapBrand(null);
        otherMessage.setTextColor(Color.BLACK);
        otherMessage.setText(mainTitle);
        setCurrency.setText(Common.goalCurrencyResult(total,currencyVO.getType()));
        progressDialog.cancel();
    }




    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("SelectShowCircleDe");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 :  getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
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
                    currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
                case "離開":
                    popupMenu.dismiss();
                    break;
                default:
                    nowCurrency = Common.code.get(menuItem.getItemId() - 2);
                    sharedPreferences.edit().putString(choiceCurrency, nowCurrency).apply();
                    currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
                    break;
            }
            setLayout();
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

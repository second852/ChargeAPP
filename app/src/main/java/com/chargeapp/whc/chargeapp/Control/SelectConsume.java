package com.chargeapp.whc.chargeapp.Control;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;


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


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectConsume extends Fragment {

    //    private LineChart lineChart;
    private InvoiceDB invoiceDB;
    private CarrierDB carrierDB;
    private ConsumeDB consumeDB;
    private ProgressDialog progressDialog;
    private static AsyncTask first = null;
    private TextView PIdateTittle,describe;
    private ImageView PIdateCut, PIdateAdd;
    private int choiceD = 0;
    private List<CarrierVO> carrierVOS;
    private List<InvoiceVO> invoiceVOS;
    private SimpleDateFormat sd = new SimpleDateFormat("MM/dd");
    private SimpleDateFormat syear = new SimpleDateFormat("yyyy/MM/dd");
    private String TAG = "SelectConsume";
    private BarChart chart_bar;
    private TypeDB typeDB;
    private List<TypeVO> typeList;
    private int[] colorlist = {Color.parseColor("#FF8888"), Color.parseColor("#FFDD55"), Color.parseColor("#66FF66"), Color.parseColor("#77DDFF"), Color.parseColor("#D28EFF")};
    private List<Map.Entry<String, Integer>> list_Data;
    private int month,year,day, dweek;
    private Calendar end;
    private Spinner choicePeriod,choiceCarrier;
    private PieChart chart_pie;
    private TypeDetailDB typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private SimpleDateFormat sc = new SimpleDateFormat("HH");
    private SimpleDateFormat sY = new SimpleDateFormat("yyyy");
    private int total,period;
    private int Statue=1;
    private String DesTittle;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_consume, container, false);
        setDB();
        end=Calendar.getInstance();
        month=end.get(Calendar.MONTH);
        year=end.get(Calendar.YEAR);
        dweek =end.get(Calendar.DAY_OF_WEEK);
        day=end.get(Calendar.DAY_OF_MONTH);
        day=day-dweek+1;
        period=dweek;
        findViewById(view);
        typeList = typeDB.getAll();
        progressDialog = new ProgressDialog(getActivity());
        PIdateAdd.setOnClickListener(new AddOnClick());
        PIdateCut.setOnClickListener(new CutOnClick());
        choicePeriod.setOnItemSelectedListener(new ChoicePeriodStatue());
        return view;
    }

    private void setDB() {
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
    }

    @Override
    public void onResume() {
        super.onResume();
         dataAnalyze();
    }

    private void findViewById(View view) {
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        chart_bar = view.findViewById(R.id.chart_bar);
        choicePeriod=view.findViewById(R.id.choicePeriod);
        choiceCarrier=view.findViewById(R.id.choiceCarrier);
        chart_pie=view.findViewById(R.id.chart_pie);
        describe=view.findViewById(R.id.describe);
        ArrayList<String> SpinnerItem1=new ArrayList<>();
        SpinnerItem1.add(" 日 ");
        SpinnerItem1.add(" 周 ");
        SpinnerItem1.add(" 月 ");
        SpinnerItem1.add(" 年 ");
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity(),R.layout.spinneritem,SpinnerItem1);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        ArrayList<String> SpinnerItem2=new ArrayList<>();
        choicePeriod.setAdapter(arrayAdapter);
        choicePeriod.setSelection(1);
        carrierVOS = carrierDB.getAll();
        for(CarrierVO c:carrierVOS)
        {
            SpinnerItem2.add(" "+c.getCarNul()+" ");
        }
        arrayAdapter=new ArrayAdapter<String>(getActivity(),R.layout.spinneritem,SpinnerItem2);
        choiceCarrier.setAdapter(arrayAdapter);
    }


    private BarData getBarData() {
        BarDataSet dataSetA = new BarDataSet(getChartData(), " ");
        dataSetA.setColors(getColor());
        dataSetA.setStackLabels(getStackLabels());
        dataSetA.setValueFormatter(new SelectCharFormat());
        dataSetA.setDrawValues(false);
        dataSetA.setHighLightAlpha(20);
        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetA);
        return new BarData(getLabels(), dataSets);
    }

    private int[] getColor() {
        int[] c=new int[list_Data.size()+1];
        for(int i=0;i<=list_Data.size();i++)
        {
            c[i]=colorlist[i];
        }
        return c;
    }
    private String[] getStackLabels() {
        boolean OtherExist=false;
        String[] s = new String[list_Data.size()+1];
        for (int i = 0; i <list_Data.size(); i++) {
            if(list_Data.get(i).getKey().equals("other"))
            {
                s[i]="其他";
                OtherExist=true;
                continue;
            }
            s[i] = list_Data.get(i).getKey();
        }
        if(!OtherExist)
        {
            s[list_Data.size()]="其他";
        }
        return s;
    }

    private void findMaxFive() {
        total=0;
        HashMap<String, Integer> hashMap = new HashMap<>();
        Calendar start,end;
        if(Statue==0)
        {
            DesTittle="當天花費";
            start = new GregorianCalendar(year, month, day,0,0,0);
            end = new GregorianCalendar(year, month, day,23,59,59);
            PIdateTittle.setText(syear.format(new Date(start.getTimeInMillis())));
        }else if(Statue==1)
        {
            DesTittle="這周花費";
            start = new GregorianCalendar(year, month, day,0,0,0);
            end = new GregorianCalendar(year, month, day +6,23,59,59);
            PIdateTittle.setText(syear.format(new Date(start.getTimeInMillis()))+" ~ "+syear.format(new Date(end.getTimeInMillis())));
        }else if(Statue==2)
        {
            DesTittle="本年花費";
            start = new GregorianCalendar(year, 0,  1,0,0,0);
            end = new GregorianCalendar(year, 11, 31,23,59,59);
            PIdateTittle.setText(sY.format(new Date(start.getTimeInMillis())));
        }else{
            start = new GregorianCalendar(year, 0, 1,0,0,0);
            end = new GregorianCalendar(year, 11, 31,23,59,59);
        }

        Log.d(TAG,"start"+syear.format(new Date(start.getTimeInMillis()))+"end"+syear.format(new Date(end.getTimeInMillis())));
        List<InvoiceVO> invoiceVOS = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), carrierVOS.get(choiceD).getCarNul());
        List<ConsumeVO> consumeVOS=consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
        for (InvoiceVO I : invoiceVOS) {
            if(I.getMaintype().equals("O"))
            {
                if (hashMap.get("other") == null) {
                    hashMap.put("other", Integer.valueOf(I.getAmount()));
                } else {
                    hashMap.put("other", Integer.valueOf(I.getAmount()) + hashMap.get("other"));
                }
                continue;
            }
            if (hashMap.get(I.getMaintype()) == null) {
                hashMap.put(I.getMaintype(), Integer.valueOf(I.getAmount()));
            } else {
                hashMap.put(I.getMaintype(), Integer.valueOf(I.getAmount()) + hashMap.get(I.getMaintype()));
            }
            total=total+Integer.valueOf(I.getAmount());
        }
        for (ConsumeVO c : consumeVOS) {
            if (hashMap.get(c.getMaintype()) == null) {
                hashMap.put(c.getMaintype(), Integer.valueOf(c.getMoney()));
            } else {
                hashMap.put(c.getMaintype(), Integer.valueOf(c.getMoney()) + hashMap.get(c.getMaintype()));
            }
            total=total+Integer.valueOf(c.getMoney());
        }
        list_Data =new ArrayList<Map.Entry<String, Integer>>(hashMap.entrySet());
        Collections.sort(list_Data, new Comparator<Map.Entry<String, Integer>>(){
            public int compare(Map.Entry<String, Integer> entry1,
                               Map.Entry<String, Integer> entry2){
                return (entry2.getValue() - entry1.getValue());
            }
        });

        if(list_Data.size()>4)
        {
            for(int i=4;i<list_Data.size();i++)
            {
                if(hashMap.get("other")==null)
                {
                    hashMap.put("other",list_Data.get(i).getValue());
                }else{
                    hashMap.put("other",hashMap.get("other")+list_Data.get(i).getValue());
                }
                list_Data.remove(i);
            }
            list_Data.add(new ChartEntry("other",hashMap.get("other")));
        }
    }

    private List<String> getLabels() {
        List<String> chartLabels = new ArrayList<>();
        Calendar time;
        if(Statue==0)
        {
            time = new GregorianCalendar(year, month, day,0,0,0);
            chartLabels.add(sd.format(new Date(time.getTimeInMillis())));
        }else if(Statue==1)
        {
            for (int i = 0; i < period; i++) {
                time = new GregorianCalendar(year, month, day+i);
                chartLabels.add(sd.format(new Date(time.getTimeInMillis())));
            }
        }else if(Statue==2)
        {
            for (int i = 0; i < period; i++) {
                chartLabels.add(i+1+"月");
            }
        }else{
            for (int i = 0; i < year; i++) {
                chartLabels.add(i+1+"年");
            }
        }
        for(String s:chartLabels)
        {
            Log.d(TAG,s);
        }
        return chartLabels;
    }


    private List<BarEntry> getChartData() {
        List<BarEntry> chartData = new ArrayList<>();
        Calendar start, end;
        if(Statue==0)
        {
                start = new GregorianCalendar(year, month, day, 0, 0, 0);
                end = new GregorianCalendar(year, month, day, 23, 59, 59);
                BarEntry barEntry = new BarEntry(Periodfloat(start, end, carrierVOS.get(choiceD).getCarNul()), 0);
                chartData.add(barEntry);

        }else if(Statue==1)
        {
            for (int i = 0; i < period; i++) {
                start = new GregorianCalendar(year, month, day+i, 0, 0, 0);
                end = new GregorianCalendar(year, month, day+i, 23, 59, 59);
                BarEntry barEntry = new BarEntry(Periodfloat(start, end, carrierVOS.get(choiceD).getCarNul()), i);
                chartData.add(barEntry);
                Log.d(TAG, "chartData"+sd.format(new Date(start.getTimeInMillis()))+" : "+i+" : "+barEntry.getVal());
            }
        }else if(Statue==2)
        {
            for (int i = 0; i < period; i++) {
                start = new GregorianCalendar(year, month+i, 1, 0, 0, 0);
                end = new GregorianCalendar(year, month+i, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
                BarEntry barEntry = new BarEntry(Periodfloat(start, end, carrierVOS.get(choiceD).getCarNul()), i);
                chartData.add(barEntry);
                Log.d(TAG, "chartData"+sd.format(new Date(start.getTimeInMillis()))+" : "+i+" : "+barEntry.getVal());
            }
        }else{
            for (int i = 0; i < 10; i++) {
                start = new GregorianCalendar(year, month, day- dweek +i+1, 0, 0, 0);
                end = new GregorianCalendar(year, month, day- dweek +i+1, 23, 59, 59);
                BarEntry barEntry = new BarEntry(Periodfloat(start, end, carrierVOS.get(choiceD).getCarNul()), i);
                chartData.add(barEntry);
                Log.d(TAG, "chartData"+sd.format(new Date(start.getTimeInMillis()))+" : "+i+" : "+barEntry.getVal());
            }
        }
        return chartData;
    }

    private float[] Periodfloat(Calendar start, Calendar end, String carrier) {
        Map<String, Integer> hashMap = new LinkedHashMap<>();
        boolean isOther=true;
        float[] f = new float[list_Data.size()+1];
        List<InvoiceVO> periodInvoice = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), carrier);
        List<ConsumeVO> periodConsume = consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
           for (InvoiceVO I : periodInvoice) {
               isOther=true;
               for(Map.Entry e:list_Data )
               {
                   if(I.getMaintype().equals(e.getKey()))
                   {
                       if(hashMap.get(I.getMaintype())==null)
                       {
                          hashMap.put(I.getMaintype(),Integer.valueOf(I.getAmount()));
                       }else{
                           hashMap.put(I.getMaintype(),Integer.valueOf(I.getAmount())+hashMap.get(I.getMaintype()));
                       }
                       isOther=false;
                      break;
                   }
               }
               if(isOther||I.getMaintype().equals("O"))
               {
                   if(hashMap.get("other")==null)
                   {
                       hashMap.put("other",Integer.valueOf(I.getAmount()));
                   }else{
                       hashMap.put("other",hashMap.get("other")+Integer.valueOf(I.getAmount()));
                   }
               }
           }
           for (ConsumeVO c : periodConsume) {
               for(Map.Entry e:list_Data )
               {
                   if(c.getMaintype().equals(e.getKey()))
                   {
                       if(hashMap.get(c.getMaintype())==null)
                       {
                           hashMap.put(c.getMaintype(),Integer.valueOf(c.getMoney()));
                       }else{
                           hashMap.put(c.getMaintype(),Integer.valueOf(c.getMoney())+hashMap.get(c.getMaintype()));
                       }
                       break;
                   }
               }
           }

        boolean OtherExist=false;
       for(int i=0;i<list_Data.size();i++)
       {
           if(hashMap.get(list_Data.get(i).getKey())==null)
           {
               f[i]=0;
               continue;
           }
           if(list_Data.get(i).getKey().equals("other"))
           {
               OtherExist=true;
           }
          f[i]=hashMap.get(list_Data.get(i).getKey());
        }
        if(hashMap.get("other")!=null&&!OtherExist)
        {
            f[list_Data.size()]=hashMap.get("other");
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
        }else{
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
                    x=x+key[i].length();
                }
            }
            if (x > total) {
                total = x;
                main = t.getGroupNumber();
                second = t.getName();
            }
        }
        if (second.indexOf("餐") != -1) {
            int hour = Integer.valueOf(sc.format(new Date(invoiceVO.getTime().getTime())));
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

    public void dataAnalyze(){
        findMaxFive();
        chart_bar.setDrawGridBackground(false);
        chart_bar.setDragEnabled(true);
        chart_bar.setScaleEnabled(true);
        chart_bar.setEnabled(true);
        chart_bar.setDescription(" ");
        XAxis xAxis=chart_bar.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setSpaceBetweenLabels(1);
        YAxis yAxis=chart_bar.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yAxis1=chart_bar.getAxis(YAxis.AxisDependency.RIGHT);
        yAxis1.setAxisMinValue(0);
        yAxis.setAxisMinValue(0);
        chart_bar.setData(getBarData());
        chart_bar.invalidate();
        chart_pie.setUsePercentValues(true);
        chart_pie.setDescription(" ");
        // enable hole and configure
        chart_pie.setDrawHoleEnabled(true);
        chart_pie.setHoleRadius(7);
        chart_pie.setTransparentCircleRadius(10);
        // enable rotation of the chart by touch
        chart_pie.setRotationAngle(0);
        chart_pie.setRotationEnabled(true);
        // add data
        addData();
        // customize legends
        Legend l = chart_pie.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
    }

    private void addData() {
        describe.setText(DesTittle+total+"元");
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        boolean ShowZero=true;
        for (int i = 0; i < list_Data.size(); i++)
        {
            if(list_Data.get(i).getValue()>0)
            {
                ShowZero=false;
                if(list_Data.get(i).getKey().equals("other"))
                {
                    yVals1.add(new Entry(list_Data.get(i).getValue(), i));
                    xVals.add("其他");
                }else{
                    yVals1.add(new Entry(list_Data.get(i).getValue(), i));
                    xVals.add(list_Data.get(i).getKey());
                }
            }
        }

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "種類");
        if(ShowZero)
        {
            dataSet.setDrawValues(false);
            yVals1.add(new Entry(1, 0));
            xVals.add("無花費");
            int[] c={Color.parseColor("#CCEEFF")};
            dataSet.setColors(c);
        }else{
            dataSet.setColors(getColor());
            dataSet.setDrawValues(true);
        }

        dataSet.setSliceSpace(1);
        dataSet.setSelectionShift(5);
        // instantiate pie data object now
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        chart_pie.setData(data);
        // undo all highlights
        chart_pie.highlightValues(null);
        // update pie chart
        chart_pie.invalidate();
        chart_pie.setBackgroundColor(Color.parseColor("#f5f5f5"));
    }


    private class AddOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(Statue==0)
            {
                day=day+1;
            }else if(Statue==1)
            {
                day=day+7;
                period=7;
            }else if(Statue==2)
            {
                year=year+1;
                period=12;
            }
            dataAnalyze();
        }
    }

    private class CutOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(Statue==0)
            {
                day=day-1;
            }else if(Statue==1)
            {
                day=day-7;
                period=7;
            }else if(Statue==2)
            {
               year=year-1;
               period=12;
            }
            dataAnalyze();
        }
    }


    private class ChoicePeriodStatue implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Statue=position;
            end=Calendar.getInstance();
            month=end.get(Calendar.MONTH);
            year=end.get(Calendar.YEAR);
            dweek =end.get(Calendar.DAY_OF_WEEK);
            day=end.get(Calendar.DAY_OF_MONTH);
            if(position==0)
            {
                period=1;
                dataAnalyze();
            }
            else if(position==1)
             {
                 day=day-dweek+1;
                 period=dweek;
                 dataAnalyze();
             }else if(position==2)
            {
                period=month+1;
                month=month-1;
                Log.d(TAG,"month"+month);
                dataAnalyze();
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}

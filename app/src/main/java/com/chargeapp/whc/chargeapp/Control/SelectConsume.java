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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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
import java.util.concurrent.Callable;

/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectConsume extends Fragment {

    //    private LineChart lineChart;
    private InvoiceDB invoiceDB;
    private CarrierDB carrierDB;
    private ConsumeDB consumeDB;
    private BankDB bankDB;
    private TextView message;
    private ProgressDialog progressDialog;
    private AsyncTask first = null;
    private TextView PIdateTittle;
    private ImageView PIdateCut, PIdateAdd;
    private Button Byear, Bmonth, Bday, Bweek;
    private int choiceD = 0;
    private List<CarrierVO> carrierVOS;
    private List<InvoiceVO> invoiceVOS;
    private List<ConsumeVO> consumeVOS;
    private SimpleDateFormat sd = new SimpleDateFormat("MM/dd");
    private List<Object> allComsume;
    private List<BankVO> bankVOS;
    private String TAG = "SelectConsume";
    private BarChart chart_bar;
    private TypeDB typeDB;
    private List<TypeVO> typeList;
    private int[] colorlist = {Color.parseColor("#FF8888"), Color.parseColor("#FFDD55"), Color.parseColor("#66FF66"), Color.parseColor("#77DDFF"), Color.parseColor("#D28EFF")};
    private List<Map.Entry<String, Integer>> list_Data;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_consume, container, false);
        findViewById(view);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankDB = new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeList = typeDB.getAll();
        carrierVOS = carrierDB.getAll();
        progressDialog = new ProgressDialog(getActivity());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        download();
//        cancel();
//        chart_bar.setData(getBarData());
        Calendar start=new GregorianCalendar(2018,0,1);
        Calendar end=new GregorianCalendar(2018,0,31);
        List<InvoiceVO> invoiceVOS=invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()),carrierVOS.get(0).getCarNul());
        invoiceVOS=invoiceDB.getAll();
        Log.d(TAG, String.valueOf(invoiceVOS.size()));
        for (InvoiceVO i:invoiceVOS)
       {
          getType(i);
       }
        invoiceVOS=invoiceDB.getAll();
        for (InvoiceVO i:invoiceVOS)
        {
            Log.d(TAG,i.getMaintype());
        }
    }

    private void findViewById(View view) {
        message = view.findViewById(R.id.message);
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        Byear = view.findViewById(R.id.year);
        Bmonth = view.findViewById(R.id.month);
        Bday = view.findViewById(R.id.day);
        Bweek = view.findViewById(R.id.week);
        chart_bar = (BarChart) view.findViewById(R.id.chart_bar);
    }


    private BarData getBarData() {
        findMaxFive();
        BarDataSet dataSetA = new BarDataSet(getChartData(), getString(R.string.chart_title));
        //設定顏色
        dataSetA.setColors(colorlist);
        //設定顯示字串
        dataSetA.setStackLabels(getStackLabels());

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetA); // add the datasets
        return new BarData(getLabels(), dataSets);
    }

    private void findMaxFive() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        Calendar start = new GregorianCalendar(2018, 0, 1);
        Calendar end = new GregorianCalendar(2018, 0, 7);
        List<InvoiceVO> invoiceVOS = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), carrierVOS.get(choiceD).getCarNul());
        for (InvoiceVO I : invoiceVOS) {
            if (hashMap.get(I.getMaintype()) == null) {
                hashMap.put(I.getMaintype(), Integer.valueOf(I.getAmount()));
            } else {
                hashMap.put(I.getMaintype(), Integer.valueOf(I.getAmount()) + hashMap.get(I.getMaintype()));
            }
        }
       list_Data =new ArrayList<Map.Entry<String, Integer>>(hashMap.entrySet());
        Collections.sort(list_Data, new Comparator<Map.Entry<String, Integer>>(){
            public int compare(Map.Entry<String, Integer> entry1,
                               Map.Entry<String, Integer> entry2){
                return (entry2.getValue() - entry1.getValue());
            }
        });
    }

    private List<String> getLabels() {
        List<String> chartLabels = new ArrayList<>();
        Calendar time;
        for (int i = 0; i <= 6; i++) {
            time = new GregorianCalendar(2018, 0, i + 1);
            chartLabels.add(sd.format(new Date(time.getTimeInMillis())));
        }
        return chartLabels;
    }

    private String[] getStackLabels() {
        String[] s = new String[typeList.size()];
        for (int i = 0; i < s.length; i++) {
            s[i] = typeList.get(i).getName();
        }
        return s;
    }


    private List<BarEntry> getChartData() {
        List<BarEntry> chartData = new ArrayList<>();
        Calendar start, end;
        for (int i = 0; i <= 6; i++) {
            start = new GregorianCalendar(2018, 0, i + 1, 0, 0, 0);
            end = new GregorianCalendar(2018, 0, i + 1, 23, 59, 59);
            chartData.add(new BarEntry(Periodfloat(start, end, carrierVOS.get(choiceD).getCarNul()), i));
        }
        return chartData;
    }

    private float[] Periodfloat(Calendar start, Calendar end, String carrier) {
        Map<String, Integer> hashMap = new HashMap();
        float[] f = new float[5];
        List<InvoiceVO> periodInvoice = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), carrier);
        List<ConsumeVO> periodConsume = consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
       for (int i=0;i<4;i++)
       {
           for (InvoiceVO I : periodInvoice) {
               if(list_Data.get(i).getKey().equals(I.getMaintype()))
               {
                   if(hashMap.get(I.getMaintype())==null)
                   {
                       hashMap.put(I.getMaintype(),Integer.valueOf(I.getAmount()));
                   }else{
                       hashMap.put(I.getMaintype(),Integer.valueOf(I.getAmount())+hashMap.get(I.getMaintype()));
                   }
               }else{
                   if(hashMap.get("other")==null)
                   {
                       hashMap.put("other",Integer.valueOf(I.getAmount()));
                   }else{
                       hashMap.put("other",Integer.valueOf(I.getAmount())+hashMap.get("other"));
                   }
               }
           }
           for (ConsumeVO c : periodConsume) {
               if(list_Data.get(i).getKey().equals(c.getMaintype()))
               {
                   if(hashMap.get(c.getMaintype())==null)
                   {
                       hashMap.put(c.getMaintype(),Integer.valueOf(c.getMaintype()));
                   }else{
                       hashMap.put(c.getMaintype(),Integer.valueOf(c.getMoney())+hashMap.get(c.getMaintype()));
                   }
               }else{
                   if(hashMap.get("other")==null)
                   {
                       hashMap.put("other",Integer.valueOf(c.getMoney()));
                   }else{
                       hashMap.put("other",Integer.valueOf(c.getMoney())+hashMap.get("other"));
                   }
               }
           }
       }
        for(int i=0;i<f.length;i++)
        {
          if(hashMap.get(list_Data.get(i).getKey())==null)
          {
              f[i]=0;
          }else{
              f[i]=hashMap.get(list_Data.get(i).getKey());
          }
        }
        return f;
    }


//    private LineData getLinedate() {
//        ILineDataSet dataSetA = new LineDataSet(getChartData(), "消費");
//        dataSetA.setDrawFilled(true);
//        dataSetA.setHighlightEnabled(true);
//        dataSetA.setValueTextColor(12);
//        dataSetA.setValueTextColor(Color.BLACK);
//        List<ILineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(dataSetA); // add the datasets
//        LineData l=new LineData(getLabels(),  dataSets);
//        l.setDrawValues(true);
//        l.setHighlightEnabled(true);
//
//        return l;
//    }


    private Integer PeriodAmout(Calendar start, Calendar end, String carrier) {
        int amount = 0;
        List<InvoiceVO> periodInvoice = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), carrier);
        List<ConsumeVO> periodConsume = consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
        for (InvoiceVO i : periodInvoice) {
            amount = Integer.parseInt(i.getAmount()) + amount;
        }
        for (ConsumeVO c : periodConsume) {
            amount = Integer.valueOf(c.getMoney()) + amount;
        }
        return amount;
    }


    //    private List<Entry> getChartData() {
//        List<Entry> chartData = new ArrayList<>();
//        Calendar start,end;
//        for(int i=0;i<=30;i++)
//        {
//            start=new GregorianCalendar(2018,0,i+1,0,0,0);
//            end=new GregorianCalendar(2018,0,i+1,23,59,59);
//            chartData.add(new Entry(PeriodAmout(start,end,carrierVOS.get(choiceD).getCarNul()),i));
//        }
//        Log.d(TAG,"total :"+chartData.size());
//        return chartData;
//    }
//    private List<String> getLabels(){
//        List<String> chartLabels = new ArrayList<>();
//        for(int i=0;i<=30;i++){
//            Calendar time=new GregorianCalendar(2017,0,i+1);
//            chartLabels.add(sd.format(new Date(time.getTimeInMillis())));
//        }
//        Log.d(TAG,"label :"+chartLabels.size());
//        return chartLabels;
//    }
    private void download() {
        List<CarrierVO> carrierVOList = carrierDB.getAll();
        if (carrierVOList == null || carrierVOList.size() <= 0) {
            return;
        }
        if (first == null) {
            first = new GetSQLDate(this).execute("GetToday");
            progressDialog.setMessage("正在更新資料,請稍候...");
            progressDialog.show();
        }
    }

    private TypeDetailDB typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private SimpleDateFormat sc = new SimpleDateFormat("HH");

    private InvoiceVO getType(InvoiceVO invoiceVO) {
        List<TypeDetailVO> typeDetailVOS = typeDetailDB.getTypdAll();
        String main = "O", second = "O";
        int x = 0, total = 0;
        for (TypeDetailVO t : typeDetailVOS) {
            x = 0;
            String[] key = t.getKeyword().split(" ");
            for (int i = 0; i < key.length; i++) {
//                Log.d(TAG,key[i]+" : "+invoiceVO.getDetail().indexOf(key[i].trim())+" : "+invoiceVO.getDetail());
                if (invoiceVO.getDetail().indexOf(key[i].trim()) != -1) {
                    x++;
                }
//                Log.d(TAG, String.valueOf(x));
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

//    public void cancel(){
//        progressDialog.cancel();
//        lineChart.setData(getLinedate());
//        lineChart.setBorderColor(0x000000);
//        lineChart.setDrawBorders(true);
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setEnabled(true);
//        xAxis.setDrawGridLines(true);
//        xAxis.setDrawLabels(true);
//        YAxis yAxis=lineChart.getAxisLeft();
//        yAxis.setAxisMinValue(0);
//        lineChart.setDescription("月");
//        lineChart.invalidate();
//    }


}

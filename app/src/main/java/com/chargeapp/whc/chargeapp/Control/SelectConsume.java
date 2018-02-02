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
import com.chargeapp.whc.chargeapp.Model.XYaxis;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
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
    private int month,year,day,dweek;

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
        Calendar today=Calendar.getInstance();
        month=today.get(Calendar.MONTH);
        year=today.get(Calendar.YEAR);
        dweek=today.get(Calendar.DAY_OF_WEEK);
        day=today.get(Calendar.DAY_OF_MONTH);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

//        download();
         dataAnalyze();
//         checked();
    }

    private void checked() {
        Calendar start=new GregorianCalendar(2018,0,1);
        Calendar end=new GregorianCalendar(2018,0,7);
        invoiceVOS=invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()),carrierVOS.get(0).getCarNul());
        Log.d(TAG, String.valueOf(invoiceVOS.size()));
//        for (InvoiceVO i:invoiceVOS)
//       {
//          getType(i);
//       }
//        invoiceVOS=invoiceDB.getAll();
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
        chart_bar = view.findViewById(R.id.chart_bar);
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
        for(int i=0;i<list_Data.size();i++)
        {
            c[i]=colorlist[i];
        }
        c[list_Data.size()]=colorlist[list_Data.size()];
        return c;
    }
    private String[] getStackLabels() {
        String[] s = new String[list_Data.size()+1];

        for (int i = 0; i <list_Data.size(); i++) {
            s[i] = list_Data.get(i).getKey();
        }
        s[list_Data.size()]="其他";
        return s;
    }

    private void findMaxFive() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        Calendar end = Calendar.getInstance();
        Calendar start = new GregorianCalendar(2018, 0, 1);
        List<InvoiceVO> invoiceVOS = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), carrierVOS.get(choiceD).getCarNul());
        List<ConsumeVO> consumeVOS=consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
        for (InvoiceVO I : invoiceVOS) {
            if (hashMap.get(I.getMaintype()) == null) {
                hashMap.put(I.getMaintype(), Integer.valueOf(I.getAmount()));
            } else {
                hashMap.put(I.getMaintype(), Integer.valueOf(I.getAmount()) + hashMap.get(I.getMaintype()));
            }
        }
        for (ConsumeVO c : consumeVOS) {
            if (hashMap.get(c.getMaintype()) == null) {
                hashMap.put(c.getMaintype(), Integer.valueOf(c.getMoney()));
            } else {
                hashMap.put(c.getMaintype(), Integer.valueOf(c.getMoney()) + hashMap.get(c.getMaintype()));
            }
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
               list_Data.remove(i);
           }
        }
        for(Map.Entry<String, Integer> m:list_Data)
        {
          Log.d(TAG,m.getKey()+" : "+m.getValue());
        }
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
               if(isOther)
               {
                   hashMap.put("other",hashMap.get("other")+Integer.valueOf(I.getAmount()));
               }
           }
           for (ConsumeVO c : periodConsume) {
               isOther=true;
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
                       isOther=false;
                       break;
                   }
               }
               if(isOther)
               {
                   hashMap.put("other",hashMap.get("other")+Integer.valueOf(c.getMoney()));
               }
           }


       for(int i=0;i<list_Data.size();i++)
       {
           if(hashMap.get(list_Data.get(i).getKey())==null)
           {
               f[i]=0;
               continue;
           }
          f[i]=hashMap.get(list_Data.get(i).getKey());
        }
        if(hashMap.get("other")!=null)
        {
            f[list_Data.size()]=hashMap.get("other");
        }
       return f;
    }


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
                    x=x+key[i].length();
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

    public void dataAnalyze(){
        progressDialog.cancel();
        findMaxFive();
        chart_bar.setDrawGridBackground(false);
        chart_bar.setDragEnabled(true);
        chart_bar.setScaleEnabled(true);
        chart_bar.setEnabled(true);
        XAxis xAxis=chart_bar.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setSpaceBetweenLabels(1);
        YAxis yAxis=chart_bar.getAxis(YAxis.AxisDependency.LEFT);
        yAxis.setAxisMinValue(0);
        chart_bar.setData(getBarData());
    }


}

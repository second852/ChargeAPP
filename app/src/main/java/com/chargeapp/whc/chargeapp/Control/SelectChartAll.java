package com.chargeapp.whc.chargeapp.Control;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumerDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetail;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectChartAll extends Fragment {

    private LineChart lineChart;
    private InvoiceDB invoiceDB;
    private CarrierDB carrierDB;
    private ConsumerDB consumerDB;
    private BankDB bankDB;
    private TextView message;
    private ProgressDialog progressDialog;
    private AsyncTask first=null;
    private TextView PIdateTittle;
    private ImageView PIdateCut,PIdateAdd;
    private Button Byear,Bmonth,Bday,Bweek;
    private int choiceD=0;
    private List<CarrierVO> carrierVOS;
    private List<InvoiceVO> invoiceVOS;
    private List<ConsumeVO> consumeVOS;
    private SimpleDateFormat sd=new SimpleDateFormat("MM/dd");
    private List<Object> allComsume;
    private List<BankVO> bankVOS;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.select_chart_all, container, false);
        findViewById(view);
        invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumerDB=new ConsumerDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierVOS=carrierDB.getAll();
        progressDialog=new ProgressDialog(getActivity());
        final ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
            @Override
            public void onWindowFocusChanged(final boolean hasFocus) {
                // do your stuff here
                 download();
            }
        });
        return view;
    }





    private void findViewById(View view) {
        lineChart=view.findViewById(R.id.chart_line);
        message=view.findViewById(R.id.message);
        PIdateTittle=view.findViewById(R.id.PIdateTittle);
        PIdateCut=view.findViewById(R.id.PIdateCut);
        PIdateAdd=view.findViewById(R.id.PIdateAdd);
        Byear=view.findViewById(R.id.year);
        Bmonth=view.findViewById(R.id.month);
        Bday=view.findViewById(R.id.day);
        Bweek=view.findViewById(R.id.week);
    }

    private LineData getLinedate() {
        ILineDataSet dataSetA = new LineDataSet(getChartData(), "消費");
        ILineDataSet dataSetB = new LineDataSet(getIncomeData(), "結餘");
        dataSetA.setDrawFilled(true);
        dataSetB.setDrawFilled(true);
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetA); // add the datasets
        dataSets.add(dataSetB);
        LineData l=new LineData(getLabels(),  dataSets);
        l.setDrawValues(true);
        return l;
    }
    private List<Entry> getIncomeData() {
        List<Entry> chartData = new ArrayList<>();
        Timestamp start=Timestamp.valueOf("2018-01-01 00:00:00");
        Timestamp end=new Timestamp(System.currentTimeMillis());
        bankVOS=bankDB.getTimeAll(start,end);
        int total=0;
        for(int i=0;i<allComsume.size();i++){
            Object o=allComsume.get(i);
            String amout=(o instanceof ConsumeVO)?((ConsumeVO) o).getMoney():((InvoiceVO)o).getAmount();
            chartData.add(new Entry(Integer.valueOf(amout), i));
        }
        return chartData;
    }

    private List<Entry> getChartData() {
        allComsume=new ArrayList<>();
        List<Entry> chartData = new ArrayList<>();
        Timestamp start=Timestamp.valueOf("2018-01-01 00:00:00");
        Timestamp end=new Timestamp(System.currentTimeMillis());
        invoiceVOS=invoiceDB.getInvoiceBytime(start,end,carrierVOS.get(choiceD).getCarNul());
        consumeVOS=consumerDB.getTimePeriod(start,end);
        allComsume.addAll(invoiceVOS);
        allComsume.addAll(consumeVOS);
        Collections.sort(allComsume, new Comparator<Object>() {
            @Override
            public int compare(Object o, Object t1) {
                long oldTime=(o instanceof ConsumeVO)?((ConsumeVO) o).getDate().getTime():((InvoiceVO)o).getTime().getTime();
                long newTime=(t1 instanceof ConsumeVO)?((ConsumeVO) t1).getDate().getTime():((InvoiceVO)t1).getTime().getTime();
                Log.d("XXXXXXXX",oldTime+":"+newTime+":"+(oldTime>newTime));
                return (int)(oldTime-newTime);
            }
        });

        for(int i=0;i<allComsume.size();i++){
            Object o=allComsume.get(i);
            String amout=(o instanceof ConsumeVO)?((ConsumeVO) o).getMoney():((InvoiceVO)o).getAmount();
            chartData.add(new Entry(Integer.valueOf(amout), i));
        }
        return chartData;
    }
    private List<String> getLabels(){
        List<String> chartLabels = new ArrayList<>();
        for(int i=0;i<allComsume.size();i++){
            long time=(allComsume.get(i) instanceof ConsumeVO)?((ConsumeVO) allComsume.get(i)).getDate().getTime():((InvoiceVO)allComsume.get(i)).getTime().getTime();
            Log.d("XXXXXX",i+" : "+sd.format(new Date(time)));
            chartLabels.add(sd.format(new Date(time)));
        }
        return chartLabels;
    }
    private void download()
    {
        List<CarrierVO> carrierVOList=carrierDB.getAll();
        if(carrierVOList==null||carrierVOList.size()<=0)
        {
          return;
        }
        if(first==null)
        {
            first=new GetSQLDate(this).execute("GetToday");
            progressDialog.setMessage("正在更新資料,請稍候...");
            progressDialog.show();
        }
    }

    public void getAllInvoiceDetail()
    {
        new GetSQLDate(this).execute("GetAllInvoice");
    }

    public void cancel(){
        progressDialog.cancel();
        lineChart.setData(getLinedate());
        lineChart.setBorderColor(0x000000);
        lineChart.setDrawBorders(true);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(true);
        lineChart.setDescription("月");
        lineChart.invalidate();
    }



}

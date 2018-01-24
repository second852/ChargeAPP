package com.chargeapp.whc.chargeapp.Control;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetail;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectChartAll extends Fragment{

    private LineChart lineChart;
    private InvoiceDB invoiceDB;
    private CarrierDB carrierDB;
    private TextView message;
    private ProgressDialog progressDialog;
    private AsyncTask first=null;
    private TextView PIdateTittle;
    private ImageView PIdateCut,PIdateAdd;
    private Button Byear,Bmonth,Bday,Bweek;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.select_chart_all, container, false);
        findViewById(view);
        invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
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
        lineChart.setData(getLinedate());
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
        ILineDataSet dataSetA = new LineDataSet(getChartData(), "LabelA");
        dataSetA.setDrawFilled(true);
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetA); // add the datasets
        LineData l=new LineData(getLabels(),  dataSets);
        l.setDrawValues(true);
        return l;
    }


    private List<Entry> getChartData() {
        final int DATA_COUNT = 5;
        List<Entry> chartData = new ArrayList<>();
        for(int i=0;i<DATA_COUNT;i++){
            chartData.add(new Entry(i*2, i));
        }
        return chartData;
    }
    private List<String> getLabels(){
        List<String> chartLabels = new ArrayList<>();
        for(int i=0;i<5;i++){
            chartLabels.add("X"+i);
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

    }



}

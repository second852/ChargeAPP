package com.chargeapp.whc.chargeapp.Control;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
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


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.select_chart_all, container, false);
        findViewById(view);
        invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        setTypeIn();
//        download();
        return view;
    }

    private void setTypeIn()
    {
        String detail;
        List<InvoiceVO> invoiceVOS=invoiceDB.findIVTypenull();
        SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd");
        for(InvoiceVO invoiceVO:invoiceVOS)
        {
            Log.d("XXXXXXXXXX",sd.format(invoiceVO.getTime())+" : "+invoiceVO.getDetail());
        }
    }

    private void findViewById(View view) {
        lineChart=view.findViewById(R.id.chart_line);
        lineChart.setData(getLinedate());
        message=view.findViewById(R.id.message);
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
            message.setText("請新增載具!");
            message.setVisibility(View.VISIBLE);
            return;
        }
        new GetSQLDate(this).execute("GetToday");
        progressDialog.setMessage("正在更新資料,請稍候...");
        progressDialog.show();
    }

}

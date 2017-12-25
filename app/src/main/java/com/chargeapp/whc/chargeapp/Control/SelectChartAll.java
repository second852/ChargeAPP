package com.chargeapp.whc.chargeapp.Control;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectChartAll extends Fragment{

    private LineChart lineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.select_chart_all, container, false);
        findViewById(view);
        return view;
    }

    private void findViewById(View view) {
        lineChart=view.findViewById(R.id.chart_line);
        lineChart.setData(getLinedate());

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
}

package com.chargeapp.whc.chargeapp.Control;


import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;


/**
 * Created by 1709008NB01 on 2018/2/2.
 */

public class SelectCharFormat extends PercentFormatter implements YAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return this.mFormat.format((double)value) + " %  ("+(int)entry.getVal()+"元)";
    }


}

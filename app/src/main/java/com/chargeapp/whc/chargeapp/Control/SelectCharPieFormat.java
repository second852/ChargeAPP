package com.chargeapp.whc.chargeapp.Control;


import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;


/**
 * Created by 1709008NB01 on 2018/2/2.
 */

public class SelectCharPieFormat extends PercentFormatter {

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return this.mFormat.format((double)value) + " % \n ("+(int)entry.getY()+"元)";
    }


}

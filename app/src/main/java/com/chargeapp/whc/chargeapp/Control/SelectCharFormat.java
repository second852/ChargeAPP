package com.chargeapp.whc.chargeapp.Control;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by 1709008NB01 on 2018/2/2.
 */

public class SelectCharFormat  implements ValueFormatter, YAxisValueFormatter {
    @Override
    public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {


        return String.valueOf((int)v);
    }

    @Override
    public String getFormattedValue(float v, YAxis yAxis) {
        return String.valueOf((int)v);
    }
}

package com.chargeapp.whc.chargeapp.Model;

import android.app.DialogFragment;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by Wang on 2018/1/27.
 */

public class ChartEntry implements Map.Entry<String,Integer>{
    String key;
    Integer value;

    public ChartEntry(String key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public ChartEntry() {
        this.key = key;
        this.value = value;
    }

    public void setKey(String key)
    {
        this.key=key;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public Integer setValue(Integer value) {
        this.value=value;
        return this.value;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChartEntry that = (ChartEntry) o;

        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}

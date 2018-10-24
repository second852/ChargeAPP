package com.chargeapp.whc.chargeapp.Model;


import java.util.Map;

/**
 * Created by Wang on 2018/1/27.
 */

public class ChartEntry implements Map.Entry<String,Double>{
    String key;
    Double value;

    public ChartEntry(String key, Double value) {
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
    public Double getValue() {
        return this.value;
    }

    @Override
    public Double setValue(Double value) {
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

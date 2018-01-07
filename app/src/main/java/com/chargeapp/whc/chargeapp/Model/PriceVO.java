package com.chargeapp.whc.chargeapp.Model;

/**
 * Created by Wang on 2018/1/7.
 */

public class PriceVO {
    private int id;
    private String number;
    private String level;
    private String period;

    /**
     * 10678
     * @return
     */
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}

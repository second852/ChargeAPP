package com.chargeapp.whc.chargeapp.Model;

/**
 * Created by Wang on 2018/3/25.
 */

public class ElePeriod {

    int id;   //序號
    int year; //yyyy
    int month; //MM
    String carNul; //載具號碼
    boolean download; //是否下載

    public ElePeriod(int year, int month, String carNul, boolean download) {
        this.year = year;
        this.month = month;
        this.carNul = carNul;
        this.download = download;
    }

    public ElePeriod() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getCarNul() {
        return carNul;
    }

    public void setCarNul(String carNul) {
        this.carNul = carNul;
    }

    public boolean isDownload() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download = download;
    }
}

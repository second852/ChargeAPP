package com.chargeapp.whc.chargeapp.Model;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by 1709008NB01 on 2017/11/9.
 */

public class BankVO implements Serializable{

    private int id;//0 收入序號
    private String maintype;//1種類
    private int money;//2薪水金額
    private Date date;//3記帳日期
    private String fixDate;//4 固定收入時時間狀態
    private String fixDateDetail; //5 固定收入時間細節Json
    private String detailname;// 6 收入細節內容
    private boolean auto; //7 是否自動產生
    private int autoId;   //8 自動產生的母ID (-1:無)
    private String currency; //9 貨幣種類
    private String realMoney;// 10 double
    private String fkKey;//11 FK

    public String getFkKey() {
        return fkKey;
    }

    public void setFkKey(String fkKey) {
        this.fkKey = fkKey;
    }

    public String getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(String realMoney) {
        this.realMoney = realMoney;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public int getAutoId() {
        return autoId;
    }

    public void setAutoId(int autoId) {
        this.autoId = autoId;
    }

    public String getFixDateDetail() {
        return fixDateDetail;
    }

    public void setFixDateDetail(String fixDateDetail) {
        this.fixDateDetail = fixDateDetail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDetailname() {
        return detailname;
    }

    public void setDetailname(String detailname) {
        this.detailname = detailname;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMaintype() {
        return maintype;
    }

    public void setMaintype(String maintype) {
        this.maintype = maintype;
    }

    public String getFixDate() {
        return fixDate;
    }

    public void setFixDate(String fixDate) {
        this.fixDate = fixDate;
    }
}

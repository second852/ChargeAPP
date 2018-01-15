package com.chargeapp.whc.chargeapp.Model;

import java.sql.Date;

/**
 * Created by 1709008NB01 on 2017/11/9.
 */

public class BankVO {

    /*
       * 種類
       * */
    private String maintype;

    /*
   *  薪水名稱
   * */
    private String detailname;
    /*
    *   薪水金額
    * */
    private String money;
    /*
    * 入帳日期
    * */
    private Date date;

    /*
    * 入帳時間
    * */
    private String fixDate;

    private int id;

    private String fixDateDetail;

    public BankVO(String maintype, String detailname, String money, Date date, String fixDate, int id, String fixDateDetail) {
        this.maintype = maintype;
        this.detailname = detailname;
        this.money = money;
        this.date = date;
        this.fixDate = fixDate;
        this.id = id;
        this.fixDateDetail = fixDateDetail;
    }

    public BankVO(String maintype, String detailname, String money, Date date, String fixDate, String fixDateDetail) {
        this.maintype = maintype;
        this.detailname = detailname;
        this.money = money;
        this.date = date;
        this.fixDate = fixDate;
        this.fixDateDetail = fixDateDetail;
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

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
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
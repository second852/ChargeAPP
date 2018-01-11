package com.chargeapp.whc.chargeapp.Model;

import java.sql.Date;

/**
 * Created by 1709008NB01 on 2017/11/9.
 */

public class ConsumeVO {

    private int id;
    /*
    * 花費名稱
    * */
    private String detailname;
    /*
    * 花費金額
    * */
    private String money;
    /*
    * 花費日期
    * */
    private Date date;
    /*
    * 發票編碼
    * */
    private String number;
    /*
    * 主要種類
    * */
    private String maintype;
    /*
    * 次要種類
    * */
    private String secondType;
    /*
    * 固定扣款日期
    * */
    private String fixDate;
    /*
     *
     **/
    private String fixDateDetail;
    /*
    *  繳費通知確認
    * */
    private Boolean  notify;

    private String isWin;

    public String getIsWin() {
        return isWin;
    }

    public void setIsWin(String isWin) {
        this.isWin = isWin;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMaintype() {
        return maintype;
    }

    public void setMaintype(String maintype) {
        this.maintype = maintype;
    }

    public String getSecondType() {
        return secondType;
    }

    public void setSecondType(String secondType) {
        this.secondType = secondType;
    }

    public Boolean getNotify() {
        return notify;
    }

    public void setNotify(Boolean notify) {
        this.notify = notify;
    }

    public String getFixDate() {
        return fixDate;
    }

    public void setFixDate(String fixDate) {
        this.fixDate = fixDate;
    }

    public String getFixDateDetail() {
        return fixDateDetail;
    }

    public void setFixDateDetail(String fixDateDetail) {
        this.fixDateDetail = fixDateDetail;
    }
}

package com.chargeapp.whc.chargeapp.Model;

import android.net.wifi.hotspot2.omadm.PpsMoParser;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by 1709008NB01 on 2017/11/9.
 */

public class ConsumeVO implements Serializable{

    private int id;
    /*
    * 花費名稱
    * */
    private String detailname;
    /*
    * 花費金額
    * */
    private int money;
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
    private String  notify;

    private String isWin;

    private boolean auto;

    private int autoId;

    //中獎號碼
    private String isWinNul;

    public String getIsWinNul() {
        return isWinNul;
    }

    public void setIsWinNul(String isWinNul) {
        this.isWinNul = isWinNul;
    }

    public int getAutoId() {
        return autoId;
    }

    public void setAutoId(int autoId) {
        this.autoId = autoId;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

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

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
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

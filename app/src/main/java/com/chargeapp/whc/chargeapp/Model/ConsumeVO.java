package com.chargeapp.whc.chargeapp.Model;

import android.net.wifi.hotspot2.omadm.PpsMoParser;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by 1709008NB01 on 2017/11/9.
 */

public class ConsumeVO implements Serializable{

    private int id;//0花費名稱
    private String maintype;//1次要種類
    private String secondType;//2固定扣款日期
    private int money;//3花費日期
    private Date date;//4發票編碼
    private String number;//5主要種類
    private String fixDate;//6固定日期時狀態
    private String fixDateDetail;//7固定日期時間細節
    private String  notify;//8繳費通知確認
    private String detailname;//9花費金額
    private boolean auto;//10是否自動產生
    private int autoId;//11自動產生母ID(-1:無)
    private String isWin;//12是否中獎
    private String isWinNul;//13中獎號碼

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

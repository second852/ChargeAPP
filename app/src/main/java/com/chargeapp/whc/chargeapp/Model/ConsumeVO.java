package com.chargeapp.whc.chargeapp.Model;



import java.io.Serializable;
import java.sql.Date;

/**
 * Created by 1709008NB01 on 2017/11/9.
 */

public class ConsumeVO implements Serializable{

    private int id;//0花費名稱
    private String maintype;//1主要種類
    private String secondType;//2次要種類
    private int money;//3 金額
    private Date date;//4花費日期
    private String number;//5發票編碼
    private String fixDate;//6固定日期時狀態
    private String fixDateDetail;//7固定日期時間細節
    private String  notify;//8繳費通知確認
    private String detailname;//9花費金額
    private boolean auto;//10是否自動產生
    private int autoId;//11自動產生母ID(-1:無)
    private String isWin;//12是否中獎 (0 沒對獎 N 沒中獎 其他 中獎)
    private String isWinNul;//13中獎號碼
    private String rdNumber;//14 QrCode 隨機碼
    private String currency;//15 幣別
    private String realMoney;//16 小數點金額
    private String fkKey;//對應的FK
    private String buyerBan;//掃描的統篇
    private String sellerName;//賣方名稱
    private String sellerAddress;//賣方的地址


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

    public String getRdNumber() {
        return rdNumber;
    }

    public void setRdNumber(String rdNumber) {
        this.rdNumber = rdNumber;
    }

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

    public String getBuyerBan() {
        return buyerBan;
    }

    public void setBuyerBan(String buyerBan) {
        this.buyerBan = buyerBan;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerAddress() {
        return sellerAddress;
    }

    public void setSellerAddress(String sellerAddress) {
        this.sellerAddress = sellerAddress;
    }
}

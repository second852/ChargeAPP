package com.chargeapp.whc.chargeapp.Model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by 1709008NB01 on 2017/12/20.
 */

public class InvoiceVO implements Serializable{
    private int id;  //0電子發票ID
    private String invNum;//1電子發票號碼
    private String cardType;//2電子發票載具種類
    private String cardNo;//3電子發票載具號碼
    private String cardEncrypt;//4電子發票密碼
    private Timestamp time;//5電子發票時間
    private int amount;//6電子發票金額
    private String detail;//7電子發票細節
    private String invDonatable;//8電子發票是否捐贈
    private String donateMark;//9 電子發票是否可以捐贈(0:未 1:有 99:由於前一版 error 前6個月為99)
    private String carrier;//10電子發票手機載具
    private String maintype;//11電子發票消費主種類
    private String secondtype;//12電子發票消費次種類
    private String heartyteam;//13電子發票捐贈單位
    private Timestamp donateTime;//14電子發票捐贈時間
    private String sellerBan;//15電子發票
    private String sellerName;//16電子發票
    private String sellerAddress;//17電子發票
    private String iswin; //18電子發票中獎資訊
    private String isWinNul; //19電子發票中獎號碼

    public String getIsWinNul() {
        return isWinNul;
    }

    public void setIsWinNul(String isWinNul) {
        this.isWinNul = isWinNul;
    }

    public String getSellerBan() {
        return sellerBan;
    }

    public void setSellerBan(String sellerBan) {
        this.sellerBan = sellerBan;
    }

    public String getSellerAddress() {
        return sellerAddress;
    }

    public void setSellerAddress(String sellerAddress) {
        this.sellerAddress = sellerAddress;
    }

    public String getIswin() {
        return iswin;
    }

    public void setIswin(String iswin) {
        this.iswin = iswin;
    }

    public Timestamp getDonateTime() {
        return donateTime;
    }

    public void setDonateTime(Timestamp donateTime) {
        this.donateTime = donateTime;
    }

    public String getHeartyteam() {
        return heartyteam;
    }

    public void setHeartyteam(String heartyteam) {
        this.heartyteam = heartyteam;
    }

    public String getMaintype() {
        return maintype;
    }

    public void setMaintype(String maintype) {
        this.maintype = maintype;
    }

    public String getSecondtype() {
        return secondtype;
    }

    public void setSecondtype(String secondtype) {
        this.secondtype = secondtype;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvNum() {
        return invNum;
    }

    public void setInvNum(String invNum) {
        this.invNum = invNum;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardEncrypt() {
        return cardEncrypt;
    }

    public void setCardEncrypt(String cardEncrypt) {
        this.cardEncrypt = cardEncrypt;
    }


    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getInvDonatable() {
        return invDonatable;
    }

    public void setInvDonatable(String invDonatable) {
        this.invDonatable = invDonatable;
    }

    public String getDonateMark() {
        return donateMark;
    }

    public void setDonateMark(String donateMark) {
        this.donateMark = donateMark;
    }
}

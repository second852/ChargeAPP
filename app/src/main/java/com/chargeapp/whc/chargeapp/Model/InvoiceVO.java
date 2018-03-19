package com.chargeapp.whc.chargeapp.Model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by 1709008NB01 on 2017/12/20.
 */

public class InvoiceVO implements Serializable{
    private int id;
    private String invNum;
    private String cardType;
    private String cardNo;
    private String cardEncrypt;
    private Timestamp time;
    private int amount;
    private String detail;
    private String invDonatable;
    private String donateMark;
    private String carrier;
    private String maintype;
    private String secondtype;
    private String heartyteam;
    private Timestamp donateTime;
    private String iswin;
    private String sellerBan;
    private String sellerName;
    private String sellerAddress;

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

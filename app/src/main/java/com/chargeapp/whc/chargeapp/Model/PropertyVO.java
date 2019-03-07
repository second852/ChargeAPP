package com.chargeapp.whc.chargeapp.Model;

/**
 * Created by Wang on 2019/1/25.
 */

public class PropertyVO {

    private int id; //Id
    private String name; //名子
    private String currency; //幣別
    private String initMoney; //起始金額
    private String nowMoney; //目前加總
    private String detail;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getInitMoney() {
        return initMoney;
    }

    public void setInitMoney(String initMoney) {
        this.initMoney = initMoney;
    }

    public String getNowMoney() {
        return nowMoney;
    }

    public void setNowMoney(String nowMoney) {
        this.nowMoney = nowMoney;
    }
}

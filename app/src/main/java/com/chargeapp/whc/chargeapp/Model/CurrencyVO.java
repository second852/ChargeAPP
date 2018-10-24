package com.chargeapp.whc.chargeapp.Model;

import java.sql.Date;

public class CurrencyVO {

    private int id;
    private String type;
    private String money;
    private Date time;

    public CurrencyVO() {

    }

    public CurrencyVO(int id, String type, String money, Date time) {
        this.id = id;
        this.type = type;
        this.money = money;
        this.time = time;
    }

    public CurrencyVO(String type, String money, Date time) {
        this.type = type;
        this.money = money;
        this.time = time;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

}

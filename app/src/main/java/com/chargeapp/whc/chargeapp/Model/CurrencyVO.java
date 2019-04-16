package com.chargeapp.whc.chargeapp.Model;

import java.sql.Date;

public class CurrencyVO {

    private int id;
    private String type;
    private String name;
    private String symbol;
    private String money;
    private Date time;

    public CurrencyVO() {

    }

    public CurrencyVO(String type, String money) {
        this.type = type;
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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

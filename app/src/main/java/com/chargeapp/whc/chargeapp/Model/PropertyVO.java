package com.chargeapp.whc.chargeapp.Model;

import com.chargeapp.whc.chargeapp.TypeCode.PropertyType;

/**
 * Created by Wang on 2019/1/25.
 */

public class PropertyVO {

    private int id; //Id
    private String name; //名子
    private PropertyType propertyType;//種類
    private String currency; //幣別
    private String nowMoney; //目前加總





    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
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



    public String getNowMoney() {
        return nowMoney;
    }

    public void setNowMoney(String nowMoney) {
        this.nowMoney = nowMoney;
    }
}

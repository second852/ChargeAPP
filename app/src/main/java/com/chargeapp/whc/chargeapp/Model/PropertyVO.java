package com.chargeapp.whc.chargeapp.Model;

import com.chargeapp.whc.chargeapp.TypeCode.PropertyType;

import java.io.Serializable;

/**
 * Created by Wang on 2019/1/25.
 */

public class PropertyVO implements Serializable {

    private String id; //Id
    private String name; //名子
    private String currency;//幣別


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

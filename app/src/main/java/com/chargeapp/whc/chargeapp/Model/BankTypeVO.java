package com.chargeapp.whc.chargeapp.Model;

import java.io.Serializable;

/**
 * Created by Wang on 2017/11/12.
 */

public class BankTypeVO implements Serializable {

    private int id;//種類細節編號
    private String name;//種類細節名子
    private String groupNumber;//種類細節代號
    private int image;


    public BankTypeVO() {
    }

    public BankTypeVO(int id, String groupNumber, String name, int image) {
        this.id = id;
        this.name = name;
        this.groupNumber = groupNumber;
        this.image=image;
    }

    public BankTypeVO(String groupNumber, String name, int image) {
        this.name = name;
        this.groupNumber = groupNumber;
        this.image = image;
    }



    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
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

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }
}

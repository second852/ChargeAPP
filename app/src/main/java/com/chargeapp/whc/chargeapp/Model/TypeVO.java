package com.chargeapp.whc.chargeapp.Model;

import java.io.Serializable;

/**
 * Created by Wang on 2017/11/12.
 */

public class TypeVO implements Serializable{

    private int id; //種類編號
    private String name;//種類名子
    private String groupNumber;//種類代號
    private int image;//種類圖片

    public TypeVO() {
    }


    public TypeVO(int id, String groupNumber, String name, int image) {
        this.id = id;
        this.name = name;
        this.groupNumber = groupNumber;
        this.image = image;
    }

    public TypeVO(int id, String groupNumber, String name) {
        this.id = id;
        this.name = name;
        this.groupNumber = groupNumber;
    }

    public TypeVO(String groupNumber, String name, int image) {
        this.groupNumber = groupNumber;
        this.name = name;
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

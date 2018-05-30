package com.chargeapp.whc.chargeapp.Model;

import java.io.Serializable;

/**
 * Created by Wang on 2017/11/12.
 */

public class TypeDetailVO implements Serializable{

    private int id;//種類細節編號
    private String name;//種類細節名子
    private String groupNumber;//種類細節代號
    private int image;//種類細節
    private String keyword;//種類細節關鍵字


    public TypeDetailVO() { }

    public TypeDetailVO(int id, String groupNumber, String name, int image) {
        this.id = id;
        this.name = name;
        this.groupNumber = groupNumber;
        this.image = image;
    }

    public TypeDetailVO(String groupNumber, String name, int image) {
        this.name = name;
        this.groupNumber = groupNumber;
        this.image = image;
    }

    public TypeDetailVO(String groupNumber, String name, int image, String keyword) {
        this.name = name;
        this.groupNumber = groupNumber;
        this.image = image;
        this.keyword = keyword;
    }

    public TypeDetailVO(int id, String groupNumber, String name, int image, String keyword) {
        this.id = id;
        this.name = name;
        this.groupNumber = groupNumber;
        this.image = image;
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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

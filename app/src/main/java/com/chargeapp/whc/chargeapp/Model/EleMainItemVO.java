package com.chargeapp.whc.chargeapp.Model;

/**
 * Created by 1709008NB01 on 2017/12/20.
 */

public class EleMainItemVO {
    private String name;
    private int idstring;
    private int image;


    public EleMainItemVO(int idstring, int image) {
        this.idstring = idstring;
        this.image = image;
    }

    public int getIdstring() {
        return idstring;
    }

    public void setIdstring(int idstring) {
        this.idstring = idstring;
    }

    public EleMainItemVO(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}

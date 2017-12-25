package com.chargeapp.whc.chargeapp.Model;

/**
 * Created by 1709008NB01 on 2017/12/21.
 */

public class CarrierVO {

    private String CarNul;
    private String password;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarNul() {
        return CarNul;
    }

    public void setCarNul(String carNul) {
        CarNul = carNul;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

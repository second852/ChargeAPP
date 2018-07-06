package com.chargeapp.whc.chargeapp.Model;

import java.io.Serializable;

/**
 * Created by 1709008NB01 on 2017/12/21.
 */

public class CarrierVO implements Serializable {

    private String CarNul;//手機載具號碼
    private String password;//手機載具密碼
    private int id;//手機載具ID

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

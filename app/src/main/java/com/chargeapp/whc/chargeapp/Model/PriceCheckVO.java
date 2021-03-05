package com.chargeapp.whc.chargeapp.Model;

import com.chargeapp.whc.chargeapp.TypeCode.PriceCheck;
import com.chargeapp.whc.chargeapp.TypeCode.PriceNotify;

import java.util.Date;

public class PriceCheckVO {

    private String inVoYm;
    private String CarNul;
    private Date checkLimit;
    private PriceCheck isCheck;
    private PriceNotify needNotify;

    public String getInVoYm() {
        return inVoYm;
    }

    public void setInVoYm(String inVoYm) {
        this.inVoYm = inVoYm;
    }

    public String getCarNul() {
        return CarNul;
    }

    public void setCarNul(String carNul) {
        CarNul = carNul;
    }

    public Date getCheckLimit() {
        return checkLimit;
    }

    public void setCheckLimit(Date checkLimit) {
        this.checkLimit = checkLimit;
    }

    public PriceCheck getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(PriceCheck isCheck) {
        this.isCheck = isCheck;
    }

    public PriceNotify getNeedNotify() {
        return needNotify;
    }

    public void setNeedNotify(PriceNotify needNotify) {
        this.needNotify = needNotify;
    }
}

package com.chargeapp.whc.chargeapp.Model;

import java.sql.Date;

/**
 * Created by 1709008NB01 on 2017/11/9.
 */

public class GoalVO {
    /*
    * 目標名稱
    * */
    private String name;
    /*
    *  目標金額
     */
    private String saveMoney;
    /*
    *目標開始日期
     */
    private Date begainDate;
    /*
    * 目標結束日期
    * */
    private Date endbegain;
    /*
    * 需要提醒
    * */
    private boolean notify;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSaveMoney() {
        return saveMoney;
    }

    public void setSaveMoney(String saveMoney) {
        this.saveMoney = saveMoney;
    }

    public Date getBegainDate() {
        return begainDate;
    }

    public void setBegainDate(Date begainDate) {
        this.begainDate = begainDate;
    }

    public Date getEndbegain() {
        return endbegain;
    }

    public void setEndbegain(Date endbegain) {
        this.endbegain = endbegain;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }
}

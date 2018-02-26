package com.chargeapp.whc.chargeapp.Model;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by 1709008NB01 on 2017/11/9.
 */

public class GoalVO implements Serializable{

    private int id;
    private String type;

    private String name;

    private String money;

    private String timeStatue;

    private boolean havePeriod;

    private Date periodTime;

    private boolean notify;

    private String notifyStatue;

    private String notifyDate;

    private boolean noWeekend;

    public boolean isNoWeekend() {
        return noWeekend;
    }

    public void setNoWeekend(boolean noWeekend) {
        this.noWeekend = noWeekend;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTimeStatue() {
        return timeStatue;
    }

    public void setTimeStatue(String timeStatue) {
        this.timeStatue = timeStatue;
    }

    public boolean isHavePeriod() {
        return havePeriod;
    }

    public void setHavePeriod(boolean havePeriod) {
        this.havePeriod = havePeriod;
    }

    public Date getPeriodTime() {
        return periodTime;
    }

    public void setPeriodTime(Date periodTime) {
        this.periodTime = periodTime;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public String getNotifyStatue() {
        return notifyStatue;
    }

    public void setNotifyStatue(String notifyStatue) {
        this.notifyStatue = notifyStatue;
    }

    public String getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(String notifyDate) {
        this.notifyDate = notifyDate;
    }
}

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

    private Date startTime;

    private Date endTime;

    private boolean notify;

    private String notifyStatue;

    private String notifyDate;

    private boolean noWeekend;

    /* 0:init 1:fail 2:complete*/
    private int statue;

    public int getStatue() {
        return statue;
    }

    public void setStatue(int statue) {
        this.statue = statue;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

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

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

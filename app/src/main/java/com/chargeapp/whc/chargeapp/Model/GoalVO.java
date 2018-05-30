package com.chargeapp.whc.chargeapp.Model;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by 1709008NB01 on 2017/11/9.
 */

public class GoalVO implements Serializable{

    private int id;        //0目標ID
    private String type;   //1目標種類
    private String name;   //2目標名子
    private int money;     //3目標金額
    private String timeStatue;  //4目標時間狀態
    private Date startTime;     //5目標開始時間
    private Date endTime;       //6目標結束時間
    private boolean notify;     //7目標通知時間
    private String notifyStatue; //8目標通知時間狀態
    private String notifyDate;   //9目標通知日期
    private boolean noWeekend;   //10目標假日是否通知
    private int statue; //11目標狀態 0:init 1:fail 2:complete

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

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
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

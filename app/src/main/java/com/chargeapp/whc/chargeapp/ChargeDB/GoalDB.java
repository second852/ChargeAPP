package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;

import com.chargeapp.whc.chargeapp.Model.GoalVO;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


public class GoalDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="Goal";
    private String COL_id="id";
    public GoalDB(SQLiteDatabase db)
    {
        this.db=db;
    }

    public List<GoalVO> getAll() {
        String sql = "SELECT * FROM goal order by statue,endTime desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<GoalVO> goalVOS = new ArrayList<>();
        GoalVO goalVO;
        while (cursor.moveToNext()) {
           goalVO=new GoalVO();
           goalVO.setId(cursor.getInt(0));
           goalVO.setType(cursor.getString(1));
           goalVO.setName(cursor.getString(2));
           goalVO.setMoney(cursor.getInt(3));
           goalVO.setTimeStatue(cursor.getString(4));
           goalVO.setStartTime(new Date(cursor.getLong(5)));
           goalVO.setEndTime(new Date(cursor.getLong(6)));
           goalVO.setNotify(Boolean.valueOf(cursor.getString(7)));
           goalVO.setNotifyStatue(cursor.getString(8));
           goalVO.setNotifyDate(cursor.getString(9));
           goalVO.setNoWeekend(Boolean.valueOf(cursor.getString(10)));
           goalVO.setStatue(cursor.getInt(11));
           goalVO.setCurrency(cursor.getString(12));
           goalVO.setRealMoney(cursor.getString(13));
           goalVOS.add(goalVO);
        }
        cursor.close();
        return goalVOS;
    }

    public List<GoalVO> getNotify() {
        String sql = "SELECT * FROM goal where notify ='true'and statue = '0' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<GoalVO> goalVOS = new ArrayList<>();
        GoalVO goalVO;
        while (cursor.moveToNext()) {
            goalVO=new GoalVO();
            goalVO.setId(cursor.getInt(0));
            goalVO.setType(cursor.getString(1));
            goalVO.setName(cursor.getString(2));
            goalVO.setMoney(cursor.getInt(3));
            goalVO.setTimeStatue(cursor.getString(4));
            goalVO.setStartTime(new Date(cursor.getLong(5)));
            goalVO.setEndTime(new Date(cursor.getLong(6)));
            goalVO.setNotify(Boolean.valueOf(cursor.getString(7)));
            goalVO.setNotifyStatue(cursor.getString(8));
            goalVO.setNotifyDate(cursor.getString(9));
            goalVO.setNoWeekend(Boolean.valueOf(cursor.getString(10)));
            goalVO.setStatue(cursor.getInt(11));
            goalVO.setCurrency(cursor.getString(12));
            goalVO.setRealMoney(cursor.getString(13));
            goalVOS.add(goalVO);
        }
        cursor.close();
        return goalVOS;
    }

    public List<GoalVO> getNoCompleteAll() {
        String sql = "SELECT * FROM goal where statue = '0' order by statue desc,endTime desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<GoalVO> goalVOS = new ArrayList<>();
        GoalVO goalVO;
        while (cursor.moveToNext()) {
            goalVO=new GoalVO();
            goalVO.setId(cursor.getInt(0));
            goalVO.setType(cursor.getString(1));
            goalVO.setName(cursor.getString(2));
            goalVO.setMoney(cursor.getInt(3));
            goalVO.setTimeStatue(cursor.getString(4));
            goalVO.setStartTime(new Date(cursor.getLong(5)));
            goalVO.setEndTime(new Date(cursor.getLong(6)));
            goalVO.setNotify(Boolean.valueOf(cursor.getString(7)));
            goalVO.setNotifyStatue(cursor.getString(8));
            goalVO.setNotifyDate(cursor.getString(9));
            goalVO.setNoWeekend(Boolean.valueOf(cursor.getString(10)));
            goalVO.setStatue(cursor.getInt(11));
            goalVO.setCurrency(cursor.getString(12));
            goalVO.setRealMoney(cursor.getString(13));
            goalVOS.add(goalVO);
        }
        cursor.close();
        return goalVOS;
    }

    public List<GoalVO> getRealMoneyIsNull() {
        String sql = "SELECT * FROM goal where realMoney isnull or trim(realMoney) = '' ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<GoalVO> goalVOS = new ArrayList<>();
        GoalVO goalVO;
        while (cursor.moveToNext()) {
            goalVO=new GoalVO();
            goalVO.setId(cursor.getInt(0));
            goalVO.setType(cursor.getString(1));
            goalVO.setName(cursor.getString(2));
            goalVO.setMoney(cursor.getInt(3));
            goalVO.setTimeStatue(cursor.getString(4));
            goalVO.setStartTime(new Date(cursor.getLong(5)));
            goalVO.setEndTime(new Date(cursor.getLong(6)));
            goalVO.setNotify(Boolean.valueOf(cursor.getString(7)));
            goalVO.setNotifyStatue(cursor.getString(8));
            goalVO.setNotifyDate(cursor.getString(9));
            goalVO.setNoWeekend(Boolean.valueOf(cursor.getString(10)));
            goalVO.setStatue(cursor.getInt(11));
            goalVO.setCurrency(cursor.getString(12));
            goalVO.setRealMoney(cursor.getString(13));
            goalVOS.add(goalVO);
        }
        cursor.close();
        return goalVOS;
    }

    public GoalVO getFindType(String type) {
        String sql = "SELECT * FROM goal where trim(type) = '"+type+"' and statue = 0 ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        GoalVO goalVO=null;
        if (cursor.moveToNext()) {
            goalVO=new GoalVO();
            goalVO.setId(cursor.getInt(0));
            goalVO.setType(cursor.getString(1));
            goalVO.setName(cursor.getString(2));
            goalVO.setMoney(cursor.getInt(3));
            goalVO.setTimeStatue(cursor.getString(4));
            goalVO.setStartTime(new Date(cursor.getLong(5)));
            goalVO.setEndTime(new Date(cursor.getLong(6)));
            goalVO.setNotify(Boolean.valueOf(cursor.getString(7)));
            goalVO.setNotifyStatue(cursor.getString(8));
            goalVO.setNotifyDate(cursor.getString(9));
            goalVO.setNoWeekend(Boolean.valueOf(cursor.getString(10)));
            goalVO.setStatue(cursor.getInt(11));
            goalVO.setCurrency(cursor.getString(12));
            goalVO.setRealMoney(cursor.getString(13));
        }
        cursor.close();
        return goalVO;
    }

    public GoalVO getFindType(String type,String name) {
        String sql = "SELECT * FROM goal where trim(type) = '"+type+"' and trim(name) = '"+name +"' ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        GoalVO goalVO=null;
        if (cursor.moveToNext()) {
            goalVO=new GoalVO();
            goalVO.setId(cursor.getInt(0));
            goalVO.setType(cursor.getString(1));
            goalVO.setName(cursor.getString(2));
            goalVO.setMoney(cursor.getInt(3));
            goalVO.setTimeStatue(cursor.getString(4));
            goalVO.setStartTime(new Date(cursor.getLong(5)));
            goalVO.setEndTime(new Date(cursor.getLong(6)));
            goalVO.setNotify(Boolean.valueOf(cursor.getString(7)));
            goalVO.setNotifyStatue(cursor.getString(8));
            goalVO.setNotifyDate(cursor.getString(9));
            goalVO.setNoWeekend(Boolean.valueOf(cursor.getString(10)));
            goalVO.setStatue(cursor.getInt(11));
            goalVO.setCurrency(cursor.getString(12));
            goalVO.setRealMoney(cursor.getString(13));
        }
        cursor.close();
        return goalVO;
    }

    public GoalVO getFindid(int id) {
        String sql = "SELECT * FROM goal where id = '"+id+"' and statue = 0 ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        GoalVO goalVO=null;
        if (cursor.moveToNext()) {
            goalVO=new GoalVO();
            goalVO.setId(cursor.getInt(0));
            goalVO.setType(cursor.getString(1));
            goalVO.setName(cursor.getString(2));
            goalVO.setMoney(cursor.getInt(3));
            goalVO.setTimeStatue(cursor.getString(4));
            goalVO.setStartTime(new Date(cursor.getLong(5)));
            goalVO.setEndTime(new Date(cursor.getLong(6)));
            goalVO.setNotify(Boolean.valueOf(cursor.getString(7)));
            goalVO.setNotifyStatue(cursor.getString(8));
            goalVO.setNotifyDate(cursor.getString(9));
            goalVO.setNoWeekend(Boolean.valueOf(cursor.getString(10)));
            goalVO.setStatue(cursor.getInt(11));
            goalVO.setCurrency(cursor.getString(12));
            goalVO.setRealMoney(cursor.getString(13));
        }
        cursor.close();
        return goalVO;
    }


    public long insert(GoalVO goalVO) {
        ContentValues values = new ContentValues();
        values.put("type",goalVO.getType());
        values.put("name",goalVO.getName());
        values.put("money",goalVO.getMoney());
        values.put("timeStatue",goalVO.getTimeStatue());
        values.put("startTime",goalVO.getStartTime().getTime());
        values.put("endTime",goalVO.getEndTime().getTime());
        values.put("notify",String.valueOf(goalVO.isNotify()));
        values.put("notifyStatue",goalVO.getNotifyStatue());
        values.put("notifyDate",goalVO.getNotifyDate());
        values.put("noWeekend",String.valueOf(goalVO.isNoWeekend()));
        values.put("statue",0);
        values.put("currency",goalVO.getCurrency());
        values.put("realMoney",goalVO.getRealMoney());
        return db.insert(TABLE_NAME, null, values);
    }

    public long insertHid(GoalVO goalVO) {
        ContentValues values = new ContentValues();
        values.put("id",goalVO.getId());
        values.put("type",goalVO.getType());
        values.put("name",goalVO.getName());
        values.put("money",goalVO.getMoney());
        values.put("timeStatue",goalVO.getTimeStatue());
        values.put("startTime",goalVO.getStartTime().getTime());
        values.put("endTime",goalVO.getEndTime().getTime());
        values.put("notify",String.valueOf(goalVO.isNotify()));
        values.put("notifyStatue",goalVO.getNotifyStatue());
        values.put("notifyDate",goalVO.getNotifyDate());
        values.put("noWeekend",String.valueOf(goalVO.isNoWeekend()));
        values.put("statue",0);
        values.put("currency",goalVO.getCurrency());
        values.put("realMoney",goalVO.getRealMoney());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(GoalVO goalVO) {
        ContentValues values = new ContentValues();
        values.put("type",goalVO.getType());
        values.put("name",goalVO.getName());
        values.put("money",goalVO.getMoney());
        values.put("timeStatue",goalVO.getTimeStatue());
        values.put("startTime",goalVO.getStartTime().getTime());
        values.put("endTime",goalVO.getEndTime().getTime());
        values.put("notify",String.valueOf(goalVO.isNotify()));
        values.put("notifyStatue",goalVO.getNotifyStatue());
        values.put("notifyDate",goalVO.getNotifyDate());
        values.put("noWeekend",String.valueOf(goalVO.isNoWeekend()));
        values.put("statue",goalVO.getStatue());
        values.put("currency",goalVO.getCurrency());
        values.put("realMoney",goalVO.getRealMoney());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(goalVO.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }
    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

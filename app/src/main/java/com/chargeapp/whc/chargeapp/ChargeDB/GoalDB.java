package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;

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
        String sql = "SELECT * FROM goal order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<GoalVO> goalVOS = new ArrayList<>();
        GoalVO goalVO;
        while (cursor.moveToNext()) {
           goalVO=new GoalVO();
           goalVO.setId(cursor.getInt(0));
           goalVO.setType(cursor.getString(1));
           goalVO.setName(cursor.getString(2));
           goalVO.setMoney(cursor.getString(3));
           goalVO.setTimeStatue(cursor.getString(4));
           goalVO.setHavePeriod(Boolean.valueOf(cursor.getString(5)));
           goalVO.setPeriodTime(new Date(cursor.getLong(6)));
           goalVO.setNotify(Boolean.valueOf(cursor.getString(7)));
           goalVO.setNotifyStatue(cursor.getString(8));
           goalVO.setNotifyDate(cursor.getString(9));
           goalVO.setNoWeekend(Boolean.valueOf(cursor.getString(10)));
           goalVOS.add(goalVO);
        }
        cursor.close();
        return goalVOS;
    }


    public long insert(GoalVO goalVO) {
        ContentValues values = new ContentValues();
        values.put("type",goalVO.getType());
        values.put("name",goalVO.getName());
        values.put("money",goalVO.getMoney());
        values.put("timeStatue",goalVO.getTimeStatue());
        values.put("havePeriod",goalVO.isHavePeriod());
        values.put("periodTime",goalVO.getPeriodTime().getTime());
        values.put("notify",goalVO.isNotify());
        values.put("notifyStatue",goalVO.getNotifyStatue());
        values.put("notifyDate",goalVO.getNotifyDate());
        values.put("noWeekend",goalVO.isNoWeekend());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(GoalVO goalVO) {
        ContentValues values = new ContentValues();
        values.put("type",goalVO.getType());
        values.put("name",goalVO.getName());
        values.put("money",goalVO.getMoney());
        values.put("timeStatue",goalVO.getTimeStatue());
        values.put("havePeriod",goalVO.isHavePeriod());
        values.put("periodTime",goalVO.getPeriodTime().getTime());
        values.put("notify",goalVO.isNotify());
        values.put("notifyStatue",goalVO.getNotifyStatue());
        values.put("notifyDate",goalVO.getNotifyDate());
        values.put("noWeekend",goalVO.isNoWeekend());
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

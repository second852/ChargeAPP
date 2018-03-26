package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.ElePeriod;

import java.util.ArrayList;
import java.util.List;


public class ElePeriodDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="ElePeriod";
    private String COL_id="id";
    public ElePeriodDB(SQLiteDatabase db)
    {
        this.db=db;
    }



    public List<ElePeriod> getAll() {
        String sql = "SELECT * FROM ElePeriod order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ElePeriod> elePeriods = new ArrayList<>();
        ElePeriod elePeriod;
        while (cursor.moveToNext()) {
            elePeriod=new ElePeriod();
            elePeriod.setId(cursor.getInt(0));
            elePeriod.setCarNul(cursor.getString(1));
            elePeriod.setYear(cursor.getInt(2));
            elePeriod.setMonth(cursor.getInt(3));
            elePeriod.setDownload(Boolean.valueOf(cursor.getString(4)));
            elePeriods.add(elePeriod);
        }
        cursor.close();
        return elePeriods;
    }

    public List<ElePeriod> getCarrierAll(String CarNul) {
        String sql = "SELECT * FROM ElePeriod where CARNUL = '"+CarNul+"' and download = 'false' order by year desc,month desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ElePeriod> elePeriods = new ArrayList<>();
        ElePeriod elePeriod;
        while (cursor.moveToNext()) {
            elePeriod=new ElePeriod();
            elePeriod.setId(cursor.getInt(0));
            elePeriod.setCarNul(cursor.getString(1));
            elePeriod.setYear(cursor.getInt(2));
            elePeriod.setMonth(cursor.getInt(3));
            elePeriod.setDownload(Boolean.valueOf(cursor.getString(4)));
            elePeriods.add(elePeriod);
        }
        cursor.close();
        return elePeriods;
    }

    public long insert(ElePeriod elePeriod) {
        ContentValues values = new ContentValues();
        values.put("CARNUL",elePeriod.getCarNul());
        values.put("year", elePeriod.getYear());
        values.put("month",elePeriod.getMonth());
        values.put("download",String.valueOf(elePeriod.isDownload()));
        return db.insert(TABLE_NAME, null, values);
    }

    public long insertHid(ElePeriod elePeriod) {
        ContentValues values = new ContentValues();
        values.put("id",elePeriod.getId());
        values.put("CARNUL",elePeriod.getCarNul());
        values.put("year", elePeriod.getYear());
        values.put("month",elePeriod.getMonth());
        values.put("download",String.valueOf(elePeriod.isDownload()));
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(ElePeriod elePeriod) {
        ContentValues values = new ContentValues();
        values.put("CARNUL",elePeriod.getCarNul());
        values.put("year", elePeriod.getYear());
        values.put("month",elePeriod.getMonth());
        values.put("download",String.valueOf(elePeriod.isDownload()));
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(elePeriod.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }
    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    public int deleteByCARNUL(String CARNUL) {
        String whereClause = "CARNUL = ?;";
        String[] whereArgs = {CARNUL};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

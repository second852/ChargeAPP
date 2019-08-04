package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.TypeCode.PropertyType;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class PropertyDB {
    private SQLiteDatabase db;
    private String TABLE_NAME = "Property";
    private String COL_id = "id";

    public PropertyDB(SQLiteDatabase db) {
        this.db = db;
    }


    private PropertyVO configPropertyVO(Cursor cursor)
    {
        PropertyVO propertyVO=new PropertyVO();
        propertyVO.setId(cursor.getLong(0));
        propertyVO.setName(cursor.getString(1));
        propertyVO.setCurrency(cursor.getString(2));
        return propertyVO;
    }



    public List<PropertyVO> getAll() {
        String sql = "SELECT * FROM Property order by id desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<PropertyVO> propertyVOS = new ArrayList<>();
        PropertyVO propertyVO;
        while (cursor.moveToNext()) {
            propertyVO=configPropertyVO(cursor);
            propertyVOS.add(propertyVO);
        }
        cursor.close();
        return propertyVOS;
    }

    public PropertyVO findById(Long Id) {
        String sql = "SELECT * FROM Property where id ='"+Id +"' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        PropertyVO propertyVO=null;
        if (cursor.moveToNext()) {
            propertyVO=configPropertyVO(cursor);
        }
        cursor.close();
        return propertyVO;
    }



    public PropertyVO findByName(String name) {
        String sql = "SELECT * FROM Property where name ='"+name +"' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        PropertyVO propertyVO=null;
        if (cursor.moveToNext()) {
            propertyVO=configPropertyVO(cursor);
        }
        cursor.close();
        return propertyVO;
    }


    public long insert(PropertyVO propertyVO) {
        ContentValues values = new ContentValues();
        values.put("name", propertyVO.getName());
        values.put("currency", propertyVO.getCurrency());
        return db.insert(TABLE_NAME, null, values);
    }


    public int update(PropertyVO propertyVO) {
        ContentValues values = new ContentValues();
        values.put("name", propertyVO.getName());
        values.put("currency", propertyVO.getCurrency());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Long.toString(propertyVO.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

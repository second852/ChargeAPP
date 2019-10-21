package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chargeapp.whc.chargeapp.Model.PropertyVO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class PropertyDB {
    private SQLiteOpenHelper db;
    private String TABLE_NAME = "Property";
    private String COL_id = "id";

    public PropertyDB(SQLiteOpenHelper db) {
        this.db = db;
    }


    private PropertyVO configPropertyVO(Cursor cursor)
    {
        PropertyVO propertyVO=new PropertyVO();
        propertyVO.setId(cursor.getString(0));
        propertyVO.setName(cursor.getString(1));
        propertyVO.setCurrency(cursor.getString(2));
        return propertyVO;
    }



    public List<PropertyVO> getAll() {
        String sql = "SELECT * FROM Property order by id desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<PropertyVO> propertyVOS = new ArrayList<>();
        PropertyVO propertyVO;
        while (cursor.moveToNext()) {
            propertyVO=configPropertyVO(cursor);
            propertyVOS.add(propertyVO);
        }
        cursor.close();
        return propertyVOS;
    }

    public PropertyVO findById(String Id) {
        String sql = "SELECT * FROM Property where id ='"+Id +"' order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        PropertyVO propertyVO=null;
        if (cursor.moveToNext()) {
            propertyVO=configPropertyVO(cursor);
        }
        cursor.close();
        return propertyVO;
    }

    public List<PropertyVO> findBySearchKey(String searchKey) {
        String sql = "SELECT * FROM Property where name like ? order by id desc;";
        String[] args = {"%"+searchKey+"%"};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<PropertyVO> propertyVOS=new ArrayList<>();
        PropertyVO propertyVO=null;
        while (cursor.moveToNext()) {
            propertyVO=configPropertyVO(cursor);
            propertyVOS.add(propertyVO);
        }
        cursor.close();
        return propertyVOS;
    }



    public PropertyVO findByName(String name) {
        String sql = "SELECT * FROM Property where name ='"+name +"' order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        PropertyVO propertyVO=null;
        if (cursor.moveToNext()) {
            propertyVO=configPropertyVO(cursor);
        }
        cursor.close();
        return propertyVO;
    }


    public String insert(PropertyVO propertyVO) {
        ContentValues values = new ContentValues();
        values.put("id",propertyVO.getId());
        values.put("name", propertyVO.getName());
        values.put("currency", propertyVO.getCurrency());
        db.getWritableDatabase().insert(TABLE_NAME, null, values);
        return propertyVO.getId();
    }


    public int update(PropertyVO propertyVO) {
        ContentValues values = new ContentValues();
        values.put("name", propertyVO.getName());
        values.put("currency", propertyVO.getCurrency());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {propertyVO.getId()};
        return db.getWritableDatabase().update(TABLE_NAME, values, whereClause, whereArgs);
    }

    public int deleteById(String id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {id};
        return db.getWritableDatabase().delete(TABLE_NAME, whereClause, whereArgs);
    }

}

package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chargeapp.whc.chargeapp.Model.PropertyVO;

import java.util.ArrayList;
import java.util.List;


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
        propertyVO.setId(cursor.getLong(0));
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

    public PropertyVO findById(Long Id) {
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


    public long insert(PropertyVO propertyVO) {
        ContentValues values = new ContentValues();
        values.put("name", propertyVO.getName());
        values.put("currency", propertyVO.getCurrency());
        return db.getWritableDatabase().insert(TABLE_NAME, null, values);
    }


    public int update(PropertyVO propertyVO) {
        ContentValues values = new ContentValues();
        values.put("name", propertyVO.getName());
        values.put("currency", propertyVO.getCurrency());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Long.toString(propertyVO.getId())};
        return db.getWritableDatabase().update(TABLE_NAME, values, whereClause, whereArgs);
    }

    public int deleteById(Long id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.getWritableDatabase().delete(TABLE_NAME, whereClause, whereArgs);
    }

}

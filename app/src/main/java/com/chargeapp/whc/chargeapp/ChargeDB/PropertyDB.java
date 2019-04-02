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

    public List<PropertyVO> getAll() {
        String sql = "SELECT * FROM Property order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<PropertyVO> propertyVOS = new ArrayList<>();
        PropertyVO propertyVO;
        while (cursor.moveToNext()) {
            propertyVO=new PropertyVO();
            propertyVO.setId(cursor.getInt(0));
            propertyVO.setName(cursor.getString(1));
            propertyVO.setPropertyType(PropertyType.valueOf(cursor.getString(2)));
            propertyVO.setCurrency(cursor.getString(3));
            propertyVO.setInitMoney(cursor.getString(4));
            propertyVO.setNowMoney(cursor.getString(5));
            propertyVO.setDetail(cursor.getString(6));
            propertyVOS.add(propertyVO);
        }
        cursor.close();
        return propertyVOS;
    }



    public long insert(PropertyVO propertyVO) {
        ContentValues values = new ContentValues();
        values.put("name", propertyVO.getName());
        values.put("property", propertyVO.getPropertyType().getName());
        values.put("currency", propertyVO.getCurrency());
        values.put("initMoney", propertyVO.getInitMoney());
        values.put("nowMoney", propertyVO.getNowMoney());
        values.put("detail", propertyVO.getDetail());
        return db.insert(TABLE_NAME, null, values);
    }


    public int update(PropertyVO propertyVO) {
        ContentValues values = new ContentValues();
        values.put("name", propertyVO.getName());
        values.put("property", propertyVO.getPropertyType().getName());
        values.put("currency", propertyVO.getCurrency());
        values.put("initMoney", propertyVO.getInitMoney());
        values.put("nowMoney", propertyVO.getNowMoney());
        values.put("detail", propertyVO.getDetail());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(propertyVO.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.PropertyFromVO;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.TypeCode.FixDateCode;
import com.chargeapp.whc.chargeapp.TypeCode.PropertyType;

import java.util.ArrayList;
import java.util.List;


public class PropertyFromDB {
    private SQLiteDatabase db;
    private String TABLE_NAME = "PropertyFrom";
    private String COL_id = "id";

    public PropertyFromDB(SQLiteDatabase db) {
        this.db = db;
    }

    public List<PropertyFromVO> getAll() {
        String sql = "SELECT * FROM PropertyFrom order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<PropertyFromVO> propertyFromVOS = new ArrayList<>();
        PropertyFromVO propertyFromVO;
        while (cursor.moveToNext()) {
            propertyFromVO=new PropertyFromVO();
            propertyFromVO.setId(cursor.getString(0));
            propertyFromVO.setSourceId(cursor.getString(1));
            propertyFromVO.setSourceMoney(cursor.getString(2));
            propertyFromVO.setSourceCurrency(cursor.getString(3));
            propertyFromVO.setImportFee(cursor.getString(4));
            propertyFromVO.setFixImport(Boolean.valueOf(cursor.getString(5)));
            propertyFromVO.setFixDateCode(FixDateCode.valueOf(cursor.getString(6)));
            propertyFromVO.setFixDateDetail(cursor.getString(7));
            propertyFromVO.setPropertyId(cursor.getString(8));
        }
        cursor.close();
        return propertyFromVOS;
    }


    public List<PropertyFromVO> findByPropertyId(String propertyId) {
        String sql = "SELECT * FROM PropertyFrom where propertyId ='"+propertyId +"' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<PropertyFromVO> propertyFromVOS = new ArrayList<>();
        PropertyFromVO propertyFromVO;
        while (cursor.moveToNext()) {
            propertyFromVO=new PropertyFromVO();
            propertyFromVO.setId(cursor.getString(0));
            propertyFromVO.setSourceId(cursor.getString(1));
            propertyFromVO.setSourceMoney(cursor.getString(2));
            propertyFromVO.setSourceCurrency(cursor.getString(3));
            propertyFromVO.setImportFee(cursor.getString(4));
            propertyFromVO.setFixImport(Boolean.valueOf(cursor.getString(5)));
            propertyFromVO.setFixDateCode(FixDateCode.valueOf(cursor.getString(6)));
            propertyFromVO.setFixDateDetail(cursor.getString(7));
            propertyFromVO.setPropertyId(cursor.getString(8));
        }
        cursor.close();
        return propertyFromVOS;
    }


    public Double findBySourceId(String sourceId) {
        String sql = "SELECT sourceMoney,sourceCurrency FROM PropertyFrom where sourceId ='"+sourceId +"' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        CurrencyDB currencyDB=new CurrencyDB(db);
        Double total=0.0;
        CurrencyVO currencyVO;
        String money,currencyMoney;
        if (cursor.moveToNext()) {
           money=cursor.getString(0);
           currencyVO=currencyDB.getOneByType(cursor.getString(1));
           currencyMoney=(currencyVO==null)?"1":currencyVO.getMoney();
           total=total+Double.valueOf(currencyMoney)*Double.valueOf(money);
        }
        cursor.close();
        return total;
    }





    public long insert(PropertyFromVO propertyFromVO) {
        ContentValues values = new ContentValues();
        values.put("sourceId", propertyFromVO.getSourceId());
        values.put("sourceMoney",propertyFromVO.getSourceMoney());
        values.put("sourceCurrency",propertyFromVO.getSourceCurrency());
        values.put("importFee",propertyFromVO.getImportFee());
        values.put("fixImport", propertyFromVO.getFixImport().toString());
        values.put("fixDateCode", propertyFromVO.getFixDateDetail());
        values.put("fixDateDetail", propertyFromVO.getFixDateDetail());
        values.put("propertyId", propertyFromVO.getPropertyId());
        return db.insert(TABLE_NAME, null, values);
    }


    public int update(PropertyFromVO propertyFromVO) {
        ContentValues values = new ContentValues();
        values.put("sourceId", propertyFromVO.getSourceId());
        values.put("sourceMoney",propertyFromVO.getSourceMoney());
        values.put("sourceCurrency",propertyFromVO.getSourceCurrency());
        values.put("importFee",propertyFromVO.getImportFee());
        values.put("fixImport", propertyFromVO.getFixImport().toString());
        values.put("fixDateCode", propertyFromVO.getFixDateDetail());
        values.put("fixDateDetail", propertyFromVO.getFixDateDetail());
        values.put("propertyId", propertyFromVO.getPropertyId());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {propertyFromVO.getId()};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;

import java.util.ArrayList;
import java.util.List;


public class CarrierDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="CARRIER";
    private String COL_id="id";
    public CarrierDB(SQLiteDatabase db)
    {
        this.db=db;
    }



    public List<CarrierVO> getAll() {
        String sql = "SELECT * FROM CARRIER order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<CarrierVO> carrierVOS = new ArrayList<>();
        CarrierVO carrierVO;
        while (cursor.moveToNext()) {
            carrierVO=new CarrierVO();
            carrierVO.setId(cursor.getInt(0));
            carrierVO.setCarNul(cursor.getString(1));
            carrierVO.setPassword(cursor.getString(2));
            carrierVOS.add(carrierVO);
        }
        cursor.close();
        return carrierVOS;
    }

    public List<String> getAllNul() {
        String sql = "SELECT * FROM CARRIER order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<String> carrierVOS = new ArrayList<>();
        CarrierVO carrierVO;
        while (cursor.moveToNext()) {
            carrierVO=new CarrierVO();
            carrierVO.setId(cursor.getInt(0));
            carrierVO.setCarNul(cursor.getString(1));
            carrierVO.setPassword(cursor.getString(2));
            carrierVOS.add(carrierVO.getCarNul());
        }
        cursor.close();
        return carrierVOS;
    }

    public CarrierVO findById(int id) {
        String[] columns = {
          "id,CARNUL,PASSWORD"
        };
        String selection = "id = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs,
                null, null, null);
        CarrierVO carrierVO=null;
        if (cursor.moveToNext()) {
            carrierVO=new CarrierVO();
            carrierVO.setId(cursor.getInt(0));
            carrierVO.setCarNul(cursor.getString(1));
            carrierVO.setPassword(cursor.getString(2));
        }
        cursor.close();
        return carrierVO;
    }

    public long insert(CarrierVO carrierVO) {
        ContentValues values = new ContentValues();
        values.put("CARNUL",carrierVO.getCarNul());
        values.put("PASSWORD", carrierVO.getPassword());
        return db.insert(TABLE_NAME, null, values);
    }

    public long insertHid(CarrierVO carrierVO) {
        ContentValues values = new ContentValues();
        values.put("id",carrierVO.getId());
        values.put("CARNUL",carrierVO.getCarNul());
        values.put("PASSWORD", carrierVO.getPassword());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(CarrierVO carrierVO) {
        ContentValues values = new ContentValues();
        values.put("CARNUL",carrierVO.getCarNul());
        values.put("PASSWORD", carrierVO.getPassword());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(carrierVO.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }
    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    public int deleteByCarNul(String CARNUL) {
        String whereClause = "CARNUL = ?;";
        String[] whereArgs = {CARNUL};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;

import java.util.ArrayList;
import java.util.List;


public class PriceDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="PRICE";
    private String COL_id="id";
    public PriceDB(SQLiteDatabase db)
    {
        this.db=db;
    }



    public List<PriceVO> getAll() {
        String sql = "SELECT * FROM PRICE order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<PriceVO> priceVOS=new ArrayList<>();
        PriceVO priceVO;
        while (cursor.moveToNext()) {
            priceVO=new PriceVO();
            priceVO.setId(cursor.getInt(0));
            priceVO.setNumber(cursor.getString(1));
            priceVO.setLevel(cursor.getString(2));
            priceVO.setPeriod(cursor.getString(3));
        }
        cursor.close();
        return priceVOS;
    }



    public long insert(PriceVO priceVO) {
        ContentValues values = new ContentValues();
        values.put("number",priceVO.getNumber());
        values.put("level", priceVO.getLevel());
        values.put("period",priceVO.getPeriod());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(PriceVO priceVO) {
        ContentValues values = new ContentValues();
        values.put("number",priceVO.getNumber());
        values.put("level", priceVO.getLevel());
        values.put("period",priceVO.getPeriod());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(priceVO.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }
    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

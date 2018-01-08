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
    private String TABLE_NAME = "PRICE";
    private String COL_id = "invoYm";

    public PriceDB(SQLiteDatabase db) {
        this.db = db;
    }


    public List<PriceVO> getAll() {
        String sql = "SELECT * FROM PRICE order by invoYm;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<PriceVO> priceVOS = new ArrayList<>();
        PriceVO priceVO;
        while (cursor.moveToNext()) {
            priceVO = new PriceVO();
            priceVO.setInvoYm(cursor.getString(0));
            priceVO.setSuperPrizeNo(cursor.getString(1));
            priceVO.setSpcPrizeNo(cursor.getString(2));
            priceVO.setFirstPrizeNo1(cursor.getString(3));
            priceVO.setFirstPrizeNo2(cursor.getString(4));
            priceVO.setFirstPrizeNo3(cursor.getString(5));
            priceVO.setSixthPrizeNo1(cursor.getString(6));
            priceVO.setFirstPrizeNo2(cursor.getString(7));
            priceVO.setFirstPrizeNo3(cursor.getString(8));
            priceVO.setSuperPrizeAmt(cursor.getString(9));
            priceVO.setSpcPrizeAmt(cursor.getString(10));
            priceVO.setFirstPrizeAmt(cursor.getString(11));
            priceVO.setSecondPrizeAmt(cursor.getString(12));
            priceVO.setThirdPrizeAmt(cursor.getString(13));
            priceVO.setFourthPrizeAmt(cursor.getString(14));
            priceVO.setFifthPrizeAmt(cursor.getString(15));
            priceVO.setSixthPrizeAmt(cursor.getString(16));
            priceVO.setSixthPrizeNo4(cursor.getString(17));
            priceVO.setSixthPrizeNo5(cursor.getString(18));
            priceVO.setSixthPrizeNo6(cursor.getString(19));
            priceVOS.add(priceVO);
        }
        cursor.close();
        return priceVOS;
    }

    public String findMaxPeriod() {
        String sql = "SELECT MAX(invoYm) FROM PRICE ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        String max = null;
        if (cursor.moveToNext()) {
            max = cursor.getString(0);
        }
        cursor.close();
        return max;
    }


    public long insert(PriceVO priceVO) {
        ContentValues values = new ContentValues();
        values.put("invoYm", priceVO.getInvoYm());
        values.put("superPrizeNo", priceVO.getSuperPrizeNo());
        values.put("spcPrizeNo", priceVO.getSpcPrizeNo());
        values.put("firstPrizeNo1", priceVO.getFirstPrizeNo1());
        values.put("firstPrizeNo2", priceVO.getFirstPrizeNo2());
        values.put("firstPrizeNo3", priceVO.getFirstPrizeNo3());
        values.put("sixthPrizeNo1", priceVO.getSixthPrizeNo1());
        values.put("sixthPrizeNo2", priceVO.getSixthPrizeNo2());
        values.put("sixthPrizeNo3", priceVO.getSixthPrizeNo3());
        values.put("superPrizeAmt", priceVO.getSuperPrizeAmt());
        values.put("spcPrizeAmt", priceVO.getSpcPrizeAmt());
        values.put("firstPrizeAmt", priceVO.getFirstPrizeAmt());
        values.put("secondPrizeAmt", priceVO.getSecondPrizeAmt());
        values.put("thirdPrizeAmt", priceVO.getThirdPrizeAmt());
        values.put("fourthPrizeAmt", priceVO.getFourthPrizeAmt());
        values.put("fifthPrizeAmt", priceVO.getFifthPrizeAmt());
        values.put("sixthPrizeAmt", priceVO.getSixthPrizeAmt());
        values.put("sixthPrizeNo4", priceVO.getSixthPrizeNo4());
        values.put("sixthPrizeNo5", priceVO.getSixthPrizeNo5());
        values.put("sixthPrizeNo6", priceVO.getSixthPrizeNo6());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(PriceVO priceVO) {
        ContentValues values = new ContentValues();
        values.put("invoYm", priceVO.getInvoYm());
        values.put("superPrizeNo", priceVO.getSuperPrizeNo());
        values.put("spcPrizeNo", priceVO.getSpcPrizeNo());
        values.put("firstPrizeNo1", priceVO.getFirstPrizeNo1());
        values.put("firstPrizeNo2", priceVO.getFirstPrizeNo2());
        values.put("firstPrizeNo3", priceVO.getFirstPrizeNo3());
        values.put("sixthPrizeNo1", priceVO.getSixthPrizeNo1());
        values.put("sixthPrizeNo2", priceVO.getSixthPrizeNo2());
        values.put("sixthPrizeNo3", priceVO.getSixthPrizeNo3());
        values.put("superPrizeAmt", priceVO.getSuperPrizeAmt());
        values.put("spcPrizeAmt", priceVO.getSpcPrizeAmt());
        values.put("firstPrizeAmt", priceVO.getFirstPrizeAmt());
        values.put("secondPrizeAmt", priceVO.getSecondPrizeAmt());
        values.put("thirdPrizeAmt", priceVO.getThirdPrizeAmt());
        values.put("fourthPrizeAmt", priceVO.getFourthPrizeAmt());
        values.put("fifthPrizeAmt", priceVO.getFifthPrizeAmt());
        values.put("sixthPrizeAmt", priceVO.getSixthPrizeAmt());
        values.put("sixthPrizeNo4", priceVO.getSixthPrizeNo4());
        values.put("sixthPrizeNo5", priceVO.getSixthPrizeNo5());
        values.put("sixthPrizeNo6", priceVO.getSixthPrizeNo6());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {priceVO.getInvoYm()};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

    public int deleteById(String id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {id};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

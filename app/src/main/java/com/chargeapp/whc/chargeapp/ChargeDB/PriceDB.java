package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.TypeCode.PriceNotify;

import java.util.ArrayList;
import java.util.List;


public class PriceDB {
    private SQLiteOpenHelper db;
    private String TABLE_NAME = "PRICE";
    private String COL_id = "invoYm";

    public PriceDB(SQLiteOpenHelper db) {
        this.db = db;
    }


    private PriceVO configPriceVO(Cursor cursor)
    {
        PriceVO priceVO = new PriceVO();
        priceVO.setInVoYm(cursor.getString(0));
        priceVO.setSuperPrizeNo(cursor.getString(1));
        priceVO.setSpcPrizeNo(cursor.getString(2));
        priceVO.setFirstPrizeNo1(cursor.getString(3));
        priceVO.setFirstPrizeNo2(cursor.getString(4));
        priceVO.setFirstPrizeNo3(cursor.getString(5));
        priceVO.setSixthPrizeNo1(cursor.getString(6));
        priceVO.setSixthPrizeNo2(cursor.getString(7));
        priceVO.setSixthPrizeNo3(cursor.getString(8));
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
        priceVO.setCheck(Boolean.valueOf(cursor.getString(20)));
        priceVO.setNeedNotify(PriceNotify.codeToEnum(cursor.getInt(21)));
        return priceVO;
    }


    private ContentValues configContentValues(PriceVO priceVO)
    {

        ContentValues values = new ContentValues();
        values.put("invoYm", priceVO.getInVoYm());
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
        values.put("isCheck", priceVO.getCheck()==null?null:priceVO.getCheck().toString());
        values.put("priceNotify", priceVO.getNeedNotify()==null?null:priceVO.getNeedNotify().getCode());
        return values;
    }





    public List<PriceVO> getAll() {
        String sql = "SELECT * FROM PRICE order by invoYm;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<PriceVO> priceVOS = new ArrayList<>();
        PriceVO priceVO;
        while (cursor.moveToNext()) {
            priceVO = configPriceVO(cursor);
            priceVOS.add(priceVO);
        }
        cursor.close();
        return priceVOS;
    }


    public List<PriceVO> getNotCheckAll() {
        String sql = "SELECT * FROM PRICE where isCheck is null order by invoYm;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<PriceVO> priceVOS = new ArrayList<>();
        PriceVO priceVO;
        while (cursor.moveToNext()) {
            priceVO = configPriceVO(cursor);
            priceVOS.add(priceVO);
        }
        cursor.close();
        return priceVOS;
    }


    public PriceVO getPeriodAll(String period) {
        String sql = "SELECT * FROM PRICE where invoYm ='"+period+"'order by invoYm;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        PriceVO priceVO=null;
        if (cursor.moveToNext()) {
            priceVO = configPriceVO(cursor);
        }
        cursor.close();
        return priceVO;
    }

    public String findMaxPeriod() {
        String sql = "SELECT MAX(invoYm) FROM PRICE ;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        String max = null;
        if (cursor.moveToNext()) {
            max = cursor.getString(0);
        }
        cursor.close();
        return max;
    }


    public long insert(PriceVO priceVO) {
        ContentValues values = configContentValues(priceVO);
        return db.getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public int update(PriceVO priceVO) {
        ContentValues values = configContentValues(priceVO);
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {priceVO.getInVoYm()};
        return db.getWritableDatabase().update(TABLE_NAME, values, whereClause, whereArgs);
    }

    public int deleteById(String id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {id};
        return db.getWritableDatabase().delete(TABLE_NAME, whereClause, whereArgs);
    }

}

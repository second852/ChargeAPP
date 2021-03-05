package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.PriceCheckVO;
import com.chargeapp.whc.chargeapp.TypeCode.PriceCheck;
import com.chargeapp.whc.chargeapp.TypeCode.PriceNotify;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class PriceCheckDB {

    private String TABLE_NAME="PriceCheck";
    private SQLiteOpenHelper db;
    public PriceCheckDB(SQLiteOpenHelper db)
    {
        this.db=db;
    }


    private PriceCheckVO configPriceCheckDB(Cursor cursor)
    {
        PriceCheckVO priceCheckVO=new PriceCheckVO();
        priceCheckVO.setInVoYm(cursor.getString(0));
        priceCheckVO.setCarNul(cursor.getString(1));
        priceCheckVO.setCheckLimit(new Date(cursor.getLong(2)));
        priceCheckVO.setIsCheck(PriceCheck.codeToEnum(cursor.getInt(3)));
        priceCheckVO.setNeedNotify(PriceNotify.codeToEnum(cursor.getInt(4)));
        return priceCheckVO;
    }

    private ContentValues configContentValues(PriceCheckVO priceCheckVO)
    {
        ContentValues values = new ContentValues();
        values.put("inVoYm", priceCheckVO.getInVoYm());
        values.put("CarNul",priceCheckVO.getCarNul());
        values.put("checkLimit",priceCheckVO.getCheckLimit().getTime());
        values.put("isCheck",priceCheckVO.getIsCheck().getCode());
        values.put("needNotify",priceCheckVO.getNeedNotify().getCode());
        return values;
    }

    public int getAllCount() {
        String sql = "SELECT count(*) FROM PriceCheck order by inVoYm;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        int result=0;
        while (cursor.moveToNext()) {
            result=cursor.getInt(0);
        }
        cursor.close();
        return result;
    }

    public List<PriceCheckVO> getCheck() {
        String sql = "SELECT * FROM PriceCheck where isCheck=0 or isCheck is null order by inVoYm;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<PriceCheckVO> priceCheckVOs = new ArrayList<>();
        while (cursor.moveToNext()) {
            priceCheckVOs.add(configPriceCheckDB(cursor));
        }
        cursor.close();
        return priceCheckVOs;
    }

    public long insert(String inVoYm,String CarNul) {
        PriceCheckVO priceCheckVO=new PriceCheckVO();
        priceCheckVO.setInVoYm(inVoYm);
        priceCheckVO.setCarNul(CarNul);
        priceCheckVO.setIsCheck(PriceCheck.notCheck);
        priceCheckVO.setNeedNotify(PriceNotify.NotCheck);
        int length=inVoYm.length();
        Integer year=Integer.valueOf(inVoYm.substring(0,length-2))+1911;
        Integer month=Integer.valueOf(inVoYm.substring(length-2));
        Calendar limitDay=new GregorianCalendar(year,month,25);
        limitDay.add(Calendar.DATE,7);
        priceCheckVO.setCheckLimit(limitDay.getTime());
        ContentValues values = configContentValues(priceCheckVO);
        return db.getWritableDatabase().insert(TABLE_NAME, null, values);
    }


    public long insert(PriceCheckVO priceCheckVO) {
        ContentValues values = configContentValues(priceCheckVO);
        return db.getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public int update(PriceCheckVO priceCheckVO) {
        ContentValues values = configContentValues(priceCheckVO);
        String whereClause = "inVoYm = ? and CarNul = ? ;";
        String[] whereArgs = {priceCheckVO.getInVoYm(),priceCheckVO.getCarNul()};
        return db.getWritableDatabase().update(TABLE_NAME, values, whereClause, whereArgs);
    }
}

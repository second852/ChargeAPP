package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.util.Log;

import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CurrencyDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="Currency";
    private String COL_id="id";
    public CurrencyDB(SQLiteDatabase db)
    {
        this.db=db;
    }



    public List<CurrencyVO> getAll() {
        String sql = "SELECT * FROM Currency order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<CurrencyVO> currencyVOS = new ArrayList<>();
        CurrencyVO currencyVO;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String type=cursor.getString(1);
            String money =cursor.getString(2);
            Date time=new Date(cursor.getLong(3));
            currencyVO=new CurrencyVO(id,type,money,time);
            currencyVOS.add(currencyVO);
        }
        cursor.close();
        return currencyVOS;
    }

    public HashMap<String,List<String>> getAllType() {
        String sql = "SELECT type FROM Currency GROUP BY type order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        HashMap<String,List<String>> types = new HashMap<>();
        List<String> code=new ArrayList<>();
        List<String> show=new ArrayList<>();
        while (cursor.moveToNext()) {
            String type=cursor.getString(0);
            code.add(type);
            show.add(Common.showCurrency().get(type));
        }
        cursor.close();
        types.put("code",code);
        types.put("show",show);
        return types;
    }

    public List<CurrencyVO> getAllBytime(Long start,Long end) {
        String sql = "SELECT * FROM Currency where time between '"+start+"' and '"+end+"';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<CurrencyVO> currencyVOS = new ArrayList<>();
        CurrencyVO currencyVO;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String type=cursor.getString(1);
            String money =cursor.getString(2);
            Date time=new Date(cursor.getLong(3));
            currencyVO=new CurrencyVO(id,type,money,time);
            currencyVOS.add(currencyVO);
        }
        cursor.close();
        return currencyVOS;
    }


    public CurrencyVO getBytimeAndType(Long start,Long end,String objectType) {

        //以下情況 設為TWD
        if(objectType==null||objectType.trim().length()<=0||objectType.trim().equals("TWD"))
        {
            return new CurrencyVO("TWD","1",new Date(System.currentTimeMillis()));
        }

        //查詢
        String sql = "SELECT * FROM Currency where time between '"+start+"' and '"+end+"' and type = '"+objectType+"';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        CurrencyVO currencyVO;
        if (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String type=cursor.getString(1);
            String money =cursor.getString(2);
            Date time=new Date(cursor.getLong(3));
            currencyVO=new CurrencyVO(id,type,money,time);
        }else {
            currencyVO=getOneByType(objectType);
        }
        cursor.close();
        //找不到用內建
        if(currencyVO==null)
        {
            currencyVO=new CurrencyVO(objectType,Common.basicCurrency().get(objectType),new Date(System.currentTimeMillis()));
        }

        return currencyVO;
    }


    public CurrencyVO getOneByType(String s) {
        String sql = "SELECT * FROM Currency where type = '"+s+"' order by time desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        CurrencyVO currencyVO=null;
        if (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String type=cursor.getString(1);
            String money =cursor.getString(2);
            Date time=new Date(cursor.getLong(3));
            currencyVO=new CurrencyVO(id,type,money,time);
        }
        cursor.close();
        return currencyVO;
    }





    public long insert(CurrencyVO currencyVO) {
        ContentValues values = new ContentValues();
        values.put("type",currencyVO.getType());
        values.put("money",currencyVO.getMoney());
        values.put("time",currencyVO.getTime().getTime());
        return db.insert(TABLE_NAME, null, values);
    }


    public int update(CurrencyVO currencyVO) {
        ContentValues values = new ContentValues();
        values.put("type",currencyVO.getType());
        values.put("money",currencyVO.getMoney());
        values.put("time",currencyVO.getTime().getTime());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(currencyVO.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }


    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

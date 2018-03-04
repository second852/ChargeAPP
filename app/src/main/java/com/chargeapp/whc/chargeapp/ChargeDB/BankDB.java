package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class BankDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="BANK";
    private String COL_id="id";
    public BankDB(SQLiteDatabase db)
    {
        this.db=db;
    }

    public List<BankVO> getAll() {
        String sql = "SELECT * FROM BANK order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<BankVO> BankVOList = new ArrayList<>();
        BankVO bankVO;
        while (cursor.moveToNext()) {
            bankVO=new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getString(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
            BankVOList.add(bankVO);
        }
        cursor.close();
        return BankVOList;
    }

    public List<BankVO> getFixDate() {
        String sql = "SELECT * FROM BANK where  fixdate = 'true' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<BankVO> BankVOList = new ArrayList<>();
        BankVO bankVO;
        while (cursor.moveToNext()) {
            bankVO=new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getString(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
            BankVOList.add(bankVO);
        }
        cursor.close();
        return BankVOList;
    }

    public List<BankVO> getAutoSetting(int id) {
        String sql = "SELECT * FROM BANK where  autoId = '"+id+"' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<BankVO> BankVOList = new ArrayList<>();
        BankVO bankVO;
        while (cursor.moveToNext()) {
            bankVO=new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getString(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
            BankVOList.add(bankVO);
        }
        cursor.close();
        return BankVOList;
    }

    public List<BankVO> getTimeAll(Timestamp start,Timestamp end) {
        String sql = "SELECT * FROM BANK where date between '"+start.getTime()+"' and '"+end.getTime()+"' order by date;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<BankVO> BankVOList = new ArrayList<>();
        BankVO bankVO;
        while (cursor.moveToNext()) {
            bankVO=new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getString(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
            BankVOList.add(bankVO);
        }
        cursor.close();
        return BankVOList;
    }

    public BankVO getAutoBank(Timestamp start,Timestamp end,int id) {
        String sql = "SELECT * FROM BANK where autoId = '"+id+"' and date between '"+start.getTime()+"' and '"+end.getTime()+"' order by date;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        BankVO bankVO =null;
        if (cursor.moveToNext()) {
            bankVO=new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getString(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
        }
        cursor.close();
        return bankVO;
    }


    public Integer getTimeTotal(Timestamp start,Timestamp end) {
        String sql = "SELECT money  FROM BANK where date between '"+start.getTime()+"' and '"+end.getTime()+"' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        int total=0;
        while (cursor.moveToNext()) {
          total=total+Integer.valueOf(cursor.getString(0));
        }
        cursor.close();
        return total;
    }

    public long getMinTime() {
        String sql = "SELECT min(date) FROM BANK ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        long minTime=0;
        if (cursor.moveToNext()) {
            minTime=cursor.getLong(0);
        }
        cursor.close();
        return minTime;
    }

    public List<BankVO> getTimeAll(Timestamp start,Timestamp end,String type) {
        String sql = "SELECT * FROM BANK where date between '"+start.getTime()+"' and '"+end.getTime()+"' and maintype = '"+type+"'order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<BankVO> BankVOList = new ArrayList<>();
        BankVO bankVO;
        while (cursor.moveToNext()) {
            bankVO=new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getString(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
            BankVOList.add(bankVO);
        }
        cursor.close();
        return BankVOList;
    }


    public BankVO findById(int id) {
        String sql = "SELECT * FROM BANK where  id = '"+id+"' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        BankVO bankVO = null;
        if (cursor.moveToNext()) {
            bankVO=new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getString(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
        }
        cursor.close();
        return bankVO;
    }

    public long insert(BankVO bankVO) {
        ContentValues values = new ContentValues();
        values.put("maintype",bankVO.getMaintype());
        values.put("money",bankVO.getMoney());
        values.put("date",bankVO.getDate().getTime());
        values.put("fixdate",bankVO.getFixDate());
        values.put("fixdatedetail",bankVO.getFixDateDetail());
        values.put("detailname",bankVO.getDetailname());
        values.put("auto",String.valueOf(bankVO.getAuto()));
        values.put("autoId",bankVO.getAutoId());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(BankVO bankVO) {
        ContentValues values = new ContentValues();
        values.put("maintype",bankVO.getMaintype());
        values.put("money",bankVO.getMoney());
        values.put("date",bankVO.getDate().getTime());
        values.put("fixdate",bankVO.getFixDate());
        values.put("fixdatedetail",bankVO.getFixDateDetail());
        values.put("detailname",bankVO.getDetailname());
        values.put("auto",String.valueOf(bankVO.getAuto()));
        values.put("autoId",bankVO.getAutoId());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(bankVO.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }
    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

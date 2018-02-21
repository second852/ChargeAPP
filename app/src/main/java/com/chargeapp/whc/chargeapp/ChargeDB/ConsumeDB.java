package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class ConsumeDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="Consumer";
    private String COL_id="id";
    public ConsumeDB(SQLiteDatabase db)
    {
        this.db=db;
    }

    public List<ConsumeVO> getAll() {
        String sql = "SELECT * FROM Consumer order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO=new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getString(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            Log.d("XXXXXXXXXXX",cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getWinAll(long startTime,long endTime) {
        String sql = "SELECT * FROM Consumer where iswin != 'N' and date between '"+startTime+"' and '"+endTime+"' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO=new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getString(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }
    public List<ConsumeVO> getNoWinAll(long startTime,long endTime) {
        String sql = "SELECT * FROM Consumer where iswin = '0' and date between '"+startTime+"' and '"+endTime+"' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO=new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getString(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getTimePeriod(Timestamp startTime, Timestamp endTime,String maintyppe) {
        String sql = "SELECT * FROM Consumer where  date between '"+startTime.getTime()+"' and '"+endTime.getTime()+"' and maintype ='"+maintyppe+"' order by date ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO=new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getString(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public Integer getTimeTotal(Timestamp startTime, Timestamp endTime) {
        String sql = "SELECT money FROM Consumer where  date between '"+startTime.getTime()+"' and '"+endTime.getTime()+"' order by date ;";
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
        String sql = "SELECT min(date) FROM Consumer ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        long minTime=0;
        if (cursor.moveToNext()) {
            minTime=cursor.getLong(0);
        }
        cursor.close();
        return minTime;
    }

    public List<ConsumeVO> getTimePeriod(Timestamp startTime, Timestamp endTime) {
        String sql = "SELECT * FROM Consumer where  date between '"+startTime.getTime()+"' and '"+endTime.getTime()+"' order by date ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO=new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getString(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getFixdate() {
        String sql = "SELECT * FROM Consumer where fixdate = 'true' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO=new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getString(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public ConsumeVO findById(int id) {
        String[] columns = {
          "id,maintype,secondtype,money,date,number,fixdate,fixdatedetail,notify,detailname"
        };
        String selection = "id = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query("Consumer", columns, selection, selectionArgs,
                null, null, null);
        ConsumeVO consumeVO = null;
        if (cursor.moveToNext()) {
            consumeVO=new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getString(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
        }
        cursor.close();
        return consumeVO;
    }

    public long insert(ConsumeVO consumeVO) {
        ContentValues values = new ContentValues();
        values.put("maintype",consumeVO.getMaintype());
        values.put("secondtype", consumeVO.getSecondType());
        values.put("money",consumeVO.getMoney());
        values.put("date",consumeVO.getDate().getTime());
        values.put("number",consumeVO.getNumber());
        values.put("fixdate",consumeVO.getFixDate());
        values.put("fixdatedetail",consumeVO.getFixDateDetail());
        values.put("notify",consumeVO.getNotify());
        values.put("detailname",consumeVO.getDetailname());
        values.put("iswin",consumeVO.getIsWin());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(ConsumeVO consumeVO) {
        ContentValues values = new ContentValues();
        values.put("maintype",consumeVO.getMaintype());
        values.put("secondtype", consumeVO.getSecondType());
        values.put("money",consumeVO.getMoney());
        values.put("date",consumeVO.getDate().getTime());
        values.put("number",consumeVO.getNumber());
        values.put("fixdate",consumeVO.getFixDate());
        values.put("fixdatedetail",consumeVO.getFixDateDetail());
        values.put("notify",consumeVO.getNotify());
        values.put("detailname",consumeVO.getDetailname());
        values.put("iswin",consumeVO.getIsWin());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(consumeVO.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }
    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

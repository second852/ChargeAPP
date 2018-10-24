package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ConsumeDB {
    private SQLiteDatabase db;
    private String TABLE_NAME = "Consumer";
    private String COL_id = "id";

    public ConsumeDB(SQLiteDatabase db) {
        this.db = db;
    }

    public List<ConsumeVO> getAll() {
        String sql = "SELECT * FROM Consumer order by id desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getInt(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeVO.setAuto(Boolean.valueOf(cursor.getString(11)));
            consumeVO.setAutoId(cursor.getInt(12));
            consumeVO.setIsWinNul(cursor.getString(13));
            consumeVO.setRdNumber(cursor.getString(14));
            consumeVO.setCurrency(cursor.getString(15));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getWinAll(long startTime, long endTime) {
        String sql = "SELECT * FROM Consumer where iswin != 'N' and date between '" + startTime + "' and '" + endTime + "' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getInt(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeVO.setAuto(Boolean.valueOf(cursor.getString(11)));
            consumeVO.setAutoId(cursor.getInt(12));
            consumeVO.setIsWinNul(cursor.getString(13));
            consumeVO.setRdNumber(cursor.getString(14));
            consumeVO.setCurrency(cursor.getString(15));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getNoWinAll(long startTime, long endTime) {
        String sql = "SELECT * FROM Consumer where iswin = '0' and date between '" + startTime + "' and '" + endTime + "' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getInt(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeVO.setAuto(Boolean.valueOf(cursor.getString(11)));
            consumeVO.setAutoId(cursor.getInt(12));
            consumeVO.setIsWinNul(cursor.getString(13));
            consumeVO.setRdNumber(cursor.getString(14));
            consumeVO.setCurrency(cursor.getString(15));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getTimePeriod(Timestamp startTime, Timestamp endTime, String maintyppe) {
        String sql = "SELECT * FROM Consumer where  date between '" + startTime.getTime() + "' and '" + endTime.getTime() + "' and maintype ='" + maintyppe + "' order by date ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getInt(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeVO.setAuto(Boolean.valueOf(cursor.getString(11)));
            consumeVO.setAutoId(cursor.getInt(12));
            consumeVO.setIsWinNul(cursor.getString(13));
            consumeVO.setRdNumber(cursor.getString(14));
            consumeVO.setCurrency(cursor.getString(15));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getMainTypePeriod(String maintyppe) {
        String sql = "SELECT * FROM Consumer where maintype ='" + maintyppe + "';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getInt(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeVO.setAuto(Boolean.valueOf(cursor.getString(11)));
            consumeVO.setAutoId(cursor.getInt(12));
            consumeVO.setIsWinNul(cursor.getString(13));
            consumeVO.setRdNumber(cursor.getString(14));
            consumeVO.setCurrency(cursor.getString(15));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getSecondTypePeriod(String maintyppe, String second) {
        String sql = "SELECT * FROM Consumer where maintype ='" + maintyppe + "' and secondtype = '" + second + "';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getInt(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeVO.setAuto(Boolean.valueOf(cursor.getString(11)));
            consumeVO.setAutoId(cursor.getInt(12));
            consumeVO.setIsWinNul(cursor.getString(13));
            consumeVO.setRdNumber(cursor.getString(14));
            consumeVO.setCurrency(cursor.getString(15));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }


    public List<ConsumeVO> getSecondTimePeriod(Timestamp startTime, Timestamp endTime, String secondtype) {
        String sql = "SELECT * FROM Consumer where  date between '" + startTime.getTime() + "' and '" + endTime.getTime() + "' and secondtype ='" + secondtype + "' order by date ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getInt(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeVO.setAuto(Boolean.valueOf(cursor.getString(11)));
            consumeVO.setAutoId(cursor.getInt(12));
            consumeVO.setIsWinNul(cursor.getString(13));
            consumeVO.setRdNumber(cursor.getString(14));
            consumeVO.setCurrency(cursor.getString(15));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public Integer getTimeTotal(Timestamp startTime, Timestamp endTime) {
        String sql = "SELECT money FROM Consumer where  date between '" + startTime.getTime() + "' and '" + endTime.getTime() + "' order by date ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        int total = 0;
        while (cursor.moveToNext()) {
            total = total + Integer.valueOf(cursor.getString(0));
        }
        cursor.close();
        return total;
    }


    public List<ChartEntry> getTimeMaxType(Timestamp startTime, Timestamp endTime) {
        String sql = "SELECT SUM(money),maintype FROM Consumer where date between '" + startTime.getTime() + "' and '" + endTime.getTime() + "' group by maintype ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ChartEntry> chartEntries = new ArrayList<>();
        ChartEntry chartEntry;
        while (cursor.moveToNext()) {
            chartEntry = new ChartEntry();
            chartEntry.setValue(cursor.getDouble(0));
            chartEntry.setKey(cursor.getString(1));
            chartEntries.add(chartEntry);
        }
        cursor.close();
        return chartEntries;
    }

    public long getMinTime() {
        String sql = "SELECT min(date) FROM Consumer ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        long minTime = 0;
        if (cursor.moveToNext()) {
            minTime = cursor.getLong(0);
        }
        cursor.close();
        return minTime;
    }

    public List<ConsumeVO> getTimePeriod(Timestamp startTime, Timestamp endTime) {
        String sql = "SELECT * FROM Consumer where  date between '" + startTime.getTime() + "' and '" + endTime.getTime() + "' order by date ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getInt(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeVO.setAuto(Boolean.valueOf(cursor.getString(11)));
            consumeVO.setAutoId(cursor.getInt(12));
            consumeVO.setIsWinNul(cursor.getString(13));
            consumeVO.setRdNumber(cursor.getString(14));
            consumeVO.setCurrency(cursor.getString(15));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public HashMap<String, Integer> getTimePeriodHashMap(Timestamp startTime, Timestamp endTime) {
        String sql = "SELECT maintype,money FROM Consumer where  date between '" + startTime.getTime() + "' and '" + endTime.getTime() + "' order by money desc ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        HashMap<String, Integer> hashMap = new HashMap<>();
        String main;
        int money, total = 0;
        while (cursor.moveToNext()) {
            main = cursor.getString(0);
            money = cursor.getInt(1);
            if (hashMap.get(main) == null) {
                hashMap.put(main, money);
            } else {
                hashMap.put(main, hashMap.get(main) + money);
            }
            total = money + total;
        }
        hashMap.put("total", total);
        cursor.close();
        return hashMap;
    }


    public ConsumeVO getAutoTimePeriod(Timestamp startTime, Timestamp endTime, int id) {
        String sql = "SELECT * FROM Consumer where autoId = '" + id + "' and date between '" + startTime.getTime() + "' and '" + endTime.getTime() + "' order by date ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        ConsumeVO consumeVO = null;
        if (cursor.moveToNext()) {
            consumeVO = new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getInt(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeVO.setAuto(Boolean.valueOf(cursor.getString(11)));
            consumeVO.setAutoId(cursor.getInt(12));
            consumeVO.setIsWinNul(cursor.getString(13));
            consumeVO.setRdNumber(cursor.getString(14));
            consumeVO.setCurrency(cursor.getString(15));
        }
        cursor.close();
        return consumeVO;
    }


    public List<ConsumeVO> getFixdate() {
        String sql = "SELECT * FROM Consumer where fixdate = 'true' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getInt(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeVO.setAuto(Boolean.valueOf(cursor.getString(11)));
            consumeVO.setAutoId(cursor.getInt(12));
            consumeVO.setIsWinNul(cursor.getString(13));
            consumeVO.setRdNumber(cursor.getString(14));
            consumeVO.setCurrency(cursor.getString(15));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getNotify() {
        String sql = "SELECT * FROM Consumer where notify = 'true' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getInt(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeVO.setAuto(Boolean.valueOf(cursor.getString(11)));
            consumeVO.setAutoId(cursor.getInt(12));
            consumeVO.setIsWinNul(cursor.getString(13));
            consumeVO.setRdNumber(cursor.getString(14));
            consumeVO.setCurrency(cursor.getString(15));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }


    public List<ConsumeVO> getAutoCreate(int id) {
        String sql = "SELECT * FROM Consumer where autoId = '" + id + "'order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getInt(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeVO.setAuto(Boolean.valueOf(cursor.getString(11)));
            consumeVO.setAutoId(cursor.getInt(12));
            consumeVO.setIsWinNul(cursor.getString(13));
            consumeVO.setRdNumber(cursor.getString(14));
            consumeVO.setCurrency(cursor.getString(15));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public ConsumeVO findConById(int id) {
        String sql = "SELECT * FROM Consumer where id = '" + id + "'order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        ConsumeVO consumeVO = null;
        if (cursor.moveToNext()) {
            consumeVO = new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getInt(3));
            consumeVO.setDate(new Date(cursor.getLong(4)));
            consumeVO.setNumber(cursor.getString(5));
            consumeVO.setFixDate(cursor.getString(6));
            consumeVO.setFixDateDetail(cursor.getString(7));
            consumeVO.setNotify(cursor.getString(8));
            consumeVO.setDetailname(cursor.getString(9));
            consumeVO.setIsWin(cursor.getString(10));
            consumeVO.setAuto(Boolean.valueOf(cursor.getString(11)));
            consumeVO.setAutoId(cursor.getInt(12));
            consumeVO.setIsWinNul(cursor.getString(13));
            consumeVO.setRdNumber(cursor.getString(14));
            consumeVO.setCurrency(cursor.getString(15));
        }
        cursor.close();
        return consumeVO;
    }


    public ConsumeVO findOldCon(ConsumeVO consumeVO) {
        String sql = "SELECT * FROM Consumer where maintype = '" + consumeVO.getMaintype() + "' " +
                "and secondtype = '" + consumeVO.getSecondType() + "' and date = '" + consumeVO.getDate().getTime() + "'" +
                "and money = '" + consumeVO.getMoney() + "';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        ConsumeVO c = null;
        if (cursor.moveToNext()) {
            c = new ConsumeVO();
            c.setId(cursor.getInt(0));
            c.setMaintype(cursor.getString(1));
            c.setSecondType(cursor.getString(2));
            c.setMoney(cursor.getInt(3));
            c.setDate(new Date(cursor.getLong(4)));
            c.setNumber(cursor.getString(5));
            c.setFixDate(cursor.getString(6));
            c.setFixDateDetail(cursor.getString(7));
            c.setNotify(cursor.getString(8));
            c.setDetailname(cursor.getString(9));
            c.setIsWin(cursor.getString(10));
            c.setAuto(Boolean.valueOf(cursor.getString(11)));
            c.setAutoId(cursor.getInt(12));
            c.setIsWinNul(cursor.getString(13));
            c.setRdNumber(cursor.getString(14));
            consumeVO.setCurrency(cursor.getString(15));
        }
        cursor.close();
        return c;
    }

    public long insert(ConsumeVO consumeVO) {
        ContentValues values = new ContentValues();
        values.put("maintype", consumeVO.getMaintype());
        values.put("secondtype", consumeVO.getSecondType());
        values.put("money", consumeVO.getMoney());
        values.put("date", consumeVO.getDate().getTime());
        values.put("number", consumeVO.getNumber());
        values.put("fixdate", consumeVO.getFixDate());
        values.put("fixdatedetail", consumeVO.getFixDateDetail());
        values.put("notify", consumeVO.getNotify());
        values.put("detailname", (consumeVO.getDetailname() == null ? "" : consumeVO.getDetailname()));
        values.put("iswin", consumeVO.getIsWin());
        values.put("auto", String.valueOf(consumeVO.isAuto()));
        values.put("autoId", consumeVO.getAutoId());
        values.put("isWinNul", consumeVO.getIsWinNul());
        values.put("rdNumber", consumeVO.getRdNumber());
        values.put("currency", consumeVO.getCurrency());
        return db.insert(TABLE_NAME, null, values);
    }

    public long insertHid(ConsumeVO consumeVO) {
        ContentValues values = new ContentValues();
        values.put("id", consumeVO.getId());
        values.put("maintype", consumeVO.getMaintype());
        values.put("secondtype", consumeVO.getSecondType());
        values.put("money", consumeVO.getMoney());
        values.put("date", consumeVO.getDate().getTime());
        values.put("number", consumeVO.getNumber());
        values.put("fixdate", consumeVO.getFixDate());
        values.put("fixdatedetail", consumeVO.getFixDateDetail());
        values.put("notify", consumeVO.getNotify());
        values.put("detailname", (consumeVO.getDetailname() == null ? "" : consumeVO.getDetailname()));
        values.put("iswin", consumeVO.getIsWin());
        values.put("auto", String.valueOf(consumeVO.isAuto()));
        values.put("autoId", consumeVO.getAutoId());
        values.put("isWinNul", consumeVO.getIsWinNul());
        values.put("rdNumber", consumeVO.getRdNumber());
        values.put("currency", consumeVO.getCurrency());
        return db.insert(TABLE_NAME, null, values);
    }


    public int update(ConsumeVO consumeVO) {
        ContentValues values = new ContentValues();
        values.put("maintype", consumeVO.getMaintype());
        values.put("secondtype", consumeVO.getSecondType());
        values.put("money", consumeVO.getMoney());
        values.put("date", consumeVO.getDate().getTime());
        values.put("number", consumeVO.getNumber());
        values.put("fixdate", consumeVO.getFixDate());
        values.put("fixdatedetail", consumeVO.getFixDateDetail());
        values.put("notify", consumeVO.getNotify());
        values.put("detailname", (consumeVO.getDetailname() == null ? "" : consumeVO.getDetailname()));
        values.put("iswin", consumeVO.getIsWin());
        values.put("auto", String.valueOf(consumeVO.isAuto()));
        values.put("autoId", consumeVO.getAutoId());
        values.put("isWinNul", consumeVO.getIsWinNul());
        values.put("rdNumber", consumeVO.getRdNumber());
        values.put("currency", consumeVO.getCurrency());
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

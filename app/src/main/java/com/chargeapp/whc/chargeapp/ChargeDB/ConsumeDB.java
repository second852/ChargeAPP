package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
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


    public List<ConsumeVO> getRealMoneyIsNull() {
        String sql = "SELECT * FROM Consumer where realMoney isnull or trim(realMoney) = '';";
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
            consumeVO.setRealMoney(cursor.getString(16));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
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
            consumeVO.setRealMoney(cursor.getString(16));
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
            consumeVO.setRealMoney(cursor.getString(16));
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
            consumeVO.setRealMoney(cursor.getString(16));
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
            consumeVO.setRealMoney(cursor.getString(16));
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
            consumeVO.setRealMoney(cursor.getString(16));
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
            consumeVO.setRealMoney(cursor.getString(16));
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
            consumeVO.setRealMoney(cursor.getString(16));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }



    public HashMap<String,Double> getTimeMaxType(long startTime, long endTime) {
        String sql = "SELECT maintype,realMoney,currency FROM Consumer where date between '" + startTime + "' and '" + endTime + "';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        HashMap<String,Double> hashMap=new HashMap<>();
        CurrencyDB currencyDB=new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        CurrencyVO currencyVO;
        String mainType;
        Double realAmount,total=0.0;
        while (cursor.moveToNext()) {
            currencyVO=currencyDB.getBytimeAndType(startTime,endTime,cursor.getString(2));
            mainType=cursor.getString(0);
            realAmount=Double.valueOf(cursor.getString(1))*Double.valueOf(currencyVO.getMoney());
            if(hashMap.get(mainType)==null)
            {
                hashMap.put(mainType,realAmount);
            }else {
                hashMap.put(mainType, hashMap.get(mainType)+realAmount);
            }
            total=total+realAmount;
        }
        hashMap.put("total",total);
        cursor.close();
        return hashMap;
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
            consumeVO.setRealMoney(cursor.getString(16));
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public HashMap<String, Double> getTimePeriodHashMap(long startTime, long endTime) {
        String sql = "SELECT maintype,realMoney,currency FROM Consumer where  date between '" + startTime + "' and '" + endTime + "' order by money desc ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        HashMap<String, Double> hashMap = new HashMap<>();
        String main,money,currency;
        CurrencyDB currencyDB=new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        CurrencyVO currencyVO;
        Double total = 0.0,twd=0.0;
        while (cursor.moveToNext()) {
            main = cursor.getString(0);
            money = cursor.getString(1);
            currency= cursor.getString(2);
            currencyVO=currencyDB.getBytimeAndType(startTime,endTime,currency);
            twd= Double.valueOf(money)*Double.valueOf(currencyVO.getMoney());
            if (hashMap.get(main) == null) {
                hashMap.put(main, twd);
            } else {
                hashMap.put(main,twd);
            }
            total = twd + total;
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
            consumeVO.setRealMoney(cursor.getString(16));
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
            consumeVO.setRealMoney(cursor.getString(16));
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
            consumeVO.setRealMoney(cursor.getString(16));
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
            consumeVO.setRealMoney(cursor.getString(16));
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
            consumeVO.setRealMoney(cursor.getString(16));
        }
        cursor.close();
        return consumeVO;
    }


    public ConsumeVO findOldCon(ConsumeVO consumeVO) {
        String sql = "SELECT * FROM Consumer where maintype = '" + consumeVO.getMaintype() + "' " +
                "and secondtype = '" + consumeVO.getSecondType() + "' and date = '" + consumeVO.getDate().getTime() + "'" +
                "and money = '" + consumeVO.getRealMoney() + "';";
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
            consumeVO.setRealMoney(cursor.getString(16));
        }
        cursor.close();
        return c;
    }

    public long insert(ConsumeVO consumeVO) {
        ContentValues values = new ContentValues();
        values.put("maintype", consumeVO.getMaintype());
        values.put("secondtype", consumeVO.getSecondType());
        values.put("realMoney", consumeVO.getRealMoney());
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
        values.put("realMoney", consumeVO.getRealMoney());
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
        values.put("realMoney", consumeVO.getRealMoney());
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

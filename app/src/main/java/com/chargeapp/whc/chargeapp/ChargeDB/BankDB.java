package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BankDB {
    private SQLiteDatabase db;
    private String TABLE_NAME = "BANK";
    private String COL_id = "id";

    public BankDB(SQLiteDatabase db) {
        this.db = db;
    }

    public List<BankVO> getAll() {
        String sql = "SELECT * FROM BANK order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<BankVO> BankVOList = new ArrayList<>();
        BankVO bankVO;
        while (cursor.moveToNext()) {
            bankVO = new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getInt(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
            bankVO.setCurrency(cursor.getString(9));
            bankVO.setRealMoney(cursor.getString(10));
            BankVOList.add(bankVO);
        }
        cursor.close();
        return BankVOList;
    }


    public List<String> getAllName() {
        String sql = "SELECT distinct maintype FROM BANK order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<String> allName = new ArrayList<>();
        while (cursor.moveToNext()) {
            allName.add(cursor.getString(0));
        }
        cursor.close();
        return allName;
    }

    public Double getTotalMoneyByName(String mainType) {
        String sql = "SELECT realMoney,currency FROM BANK  where mainType = '"+mainType+"' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        CurrencyDB currencyDB=new CurrencyDB(db);
        Double total=0.0;
        CurrencyVO currencyVO;
        String money,currencyMoney;
        while (cursor.moveToNext()) {
            money=cursor.getString(0);
            currencyVO=currencyDB.getOneByType(cursor.getString(1));
            currencyMoney=(currencyVO==null)?"1":currencyVO.getMoney();
            total=total+Double.valueOf(money)*Double.valueOf(currencyMoney);
        }
        cursor.close();
        return total;
    }

    public List<BankVO> getrealMoneyIsNullAll() {
        String sql = "SELECT * FROM BANK where realMoney isnull or trim(realMoney) = '';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<BankVO> BankVOList = new ArrayList<>();
        BankVO bankVO;
        while (cursor.moveToNext()) {
            bankVO = new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getInt(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
            bankVO.setCurrency(cursor.getString(9));
            bankVO.setRealMoney(cursor.getString(10));
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
            bankVO = new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getInt(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
            bankVO.setCurrency(cursor.getString(9));
            bankVO.setRealMoney(cursor.getString(10));
            BankVOList.add(bankVO);
        }
        cursor.close();
        return BankVOList;
    }

    public List<BankVO> getAutoSetting(int id) {
        String sql = "SELECT * FROM BANK where autoId = " + id + ";";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<BankVO> BankVOList = new ArrayList<>();
        BankVO bankVO;
        while (cursor.moveToNext()) {
            bankVO = new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getInt(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
            bankVO.setCurrency(cursor.getString(9));
            bankVO.setRealMoney(cursor.getString(10));
            BankVOList.add(bankVO);
        }
        cursor.close();
        return BankVOList;
    }

    public List<BankVO> getTimeAll(Long start, Long end) {
        String sql = "SELECT * FROM BANK where date between '" + start + "' and '" + end + "' order by date desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<BankVO> BankVOList = new ArrayList<>();
        BankVO bankVO;
        while (cursor.moveToNext()) {
            bankVO = new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getInt(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
            bankVO.setCurrency(cursor.getString(9));
            bankVO.setRealMoney(cursor.getString(10));
            bankVO.setPropertyId(cursor.getInt(11));
            BankVOList.add(bankVO);
        }
        cursor.close();
        return BankVOList;
    }

    public BankVO getAutoBank(Timestamp start, Timestamp end, int id) {
        String sql = "SELECT * FROM BANK where autoId = '" + id + "' and date between '" + start.getTime() + "' and '" + end.getTime() + "' order by date;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        BankVO bankVO = null;
        if (cursor.moveToNext()) {
            bankVO = new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getInt(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
            bankVO.setCurrency(cursor.getString(9));
            bankVO.setRealMoney(cursor.getString(10));
            bankVO.setPropertyId(cursor.getInt(11));
        }
        cursor.close();
        return bankVO;
    }

    public BankVO getFindOldBank(BankVO bankVO) {
        String sql = "SELECT * FROM BANK where maintype = '" + bankVO.getMaintype() + "' and realMoney = '" + bankVO.getRealMoney() + "' and date = '" + bankVO.getDate().getTime() + "';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        BankVO b = null;
        if (cursor.moveToNext()) {
            b = new BankVO();
            b.setId(cursor.getInt(0));
            b.setMaintype(cursor.getString(1));
            b.setMoney(cursor.getInt(2));
            b.setDate(new Date(cursor.getLong(3)));
            b.setFixDate(cursor.getString(4));
            b.setFixDateDetail(cursor.getString(5));
            b.setDetailname(cursor.getString(6));
            b.setAuto(Boolean.valueOf(cursor.getString(7)));
            b.setAutoId(cursor.getInt(8));
            bankVO.setCurrency(cursor.getString(9));
            bankVO.setRealMoney(cursor.getString(10));
            bankVO.setPropertyId(cursor.getInt(11));
        }
        cursor.close();
        return b;
    }


    public Double getTimeTotal(long start, long end) {
        String sql = "SELECT realMoney,currency  FROM BANK where date between '" + start + "' and '" + end + "' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        Double total = 0.0;
        CurrencyDB currencyDB = new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        CurrencyVO currencyVO;
        while (cursor.moveToNext()) {
            currencyVO = currencyDB.getBytimeAndType(start, end, cursor.getString(1));
            total = total + Double.valueOf(cursor.getString(0)) * Double.valueOf(currencyVO.getMoney());
        }
        cursor.close();
        return total;
    }


    public Double getAllTotal() {
        String sql = "SELECT realMoney,currency  FROM BANK where date;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        Double total = 0.0;
        CurrencyDB currencyDB = new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        CurrencyVO currencyVO;
        while (cursor.moveToNext()) {
            currencyVO = currencyDB.getOneByType(cursor.getString(1));
            total = total + Double.valueOf(cursor.getString(0)) * Double.valueOf(currencyVO.getMoney());
        }
        cursor.close();
        return total;
    }


    public long getMinTime() {
        String sql = "SELECT min(date) FROM BANK ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        long minTime = 0;
        if (cursor.moveToNext()) {
            minTime = cursor.getLong(0);
        }
        cursor.close();
        return minTime;
    }

    public List<BankVO> getTimeAll(Timestamp start, Timestamp end, String type) {
        String sql = "SELECT * FROM BANK where date between '" + start.getTime() + "' and '" + end.getTime() + "' and maintype = '" + type + "'order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<BankVO> BankVOList = new ArrayList<>();
        BankVO bankVO;
        while (cursor.moveToNext()) {
            bankVO = new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getInt(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
            bankVO.setCurrency(cursor.getString(9));
            bankVO.setRealMoney(cursor.getString(10));
            bankVO.setPropertyId(cursor.getInt(11));
            BankVOList.add(bankVO);
        }
        cursor.close();
        return BankVOList;
    }

    public List<BankVO> getMainType(String mainType) {
        String sql = "SELECT * FROM BANK where maintype = '" + mainType + "';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<BankVO> BankVOList = new ArrayList<>();
        BankVO bankVO;
        while (cursor.moveToNext()) {
            bankVO = new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getInt(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
            bankVO.setCurrency(cursor.getString(9));
            bankVO.setRealMoney(cursor.getString(10));
            bankVO.setPropertyId(cursor.getInt(11));
            BankVOList.add(bankVO);
        }
        cursor.close();
        return BankVOList;
    }


    public BankVO findById(int id) {
        String sql = "SELECT * FROM BANK where  id = '" + id + "' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        BankVO bankVO = null;
        if (cursor.moveToNext()) {
            bankVO = new BankVO();
            bankVO.setId(cursor.getInt(0));
            bankVO.setMaintype(cursor.getString(1));
            bankVO.setMoney(cursor.getInt(2));
            bankVO.setDate(new Date(cursor.getLong(3)));
            bankVO.setFixDate(cursor.getString(4));
            bankVO.setFixDateDetail(cursor.getString(5));
            bankVO.setDetailname(cursor.getString(6));
            bankVO.setAuto(Boolean.valueOf(cursor.getString(7)));
            bankVO.setAutoId(cursor.getInt(8));
            bankVO.setCurrency(cursor.getString(9));
            bankVO.setRealMoney(cursor.getString(10));
            bankVO.setPropertyId(cursor.getInt(11));
        }
        cursor.close();
        return bankVO;
    }

    public long insert(BankVO bankVO) {
        ContentValues values = new ContentValues();
        values.put("maintype", bankVO.getMaintype());
        values.put("money", bankVO.getMoney());
        values.put("date", bankVO.getDate().getTime());
        values.put("fixdate", bankVO.getFixDate());
        values.put("fixdatedetail", bankVO.getFixDateDetail());
        values.put("detailname", bankVO.getDetailname());
        values.put("auto", String.valueOf(bankVO.isAuto()));
        values.put("autoId", bankVO.getAutoId());
        values.put("currency", bankVO.getCurrency());
        values.put("realMoney", bankVO.getRealMoney());
        values.put("propertyId", bankVO.getPropertyId());
        return db.insert(TABLE_NAME, null, values);
    }

    public long insertHid(BankVO bankVO) {
        ContentValues values = new ContentValues();
        values.put("id", bankVO.getId());
        values.put("maintype", bankVO.getMaintype());
        values.put("money", bankVO.getMoney());
        values.put("date", bankVO.getDate().getTime());
        values.put("fixdate", bankVO.getFixDate());
        values.put("fixdatedetail", bankVO.getFixDateDetail());
        values.put("detailname", bankVO.getDetailname());
        values.put("auto", String.valueOf(bankVO.isAuto()));
        values.put("autoId", bankVO.getAutoId());
        values.put("currency", bankVO.getCurrency());
        values.put("realMoney", bankVO.getRealMoney());
        values.put("propertyId", bankVO.getPropertyId());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(BankVO bankVO) {
        ContentValues values = new ContentValues();
        values.put("maintype", bankVO.getMaintype());
        values.put("money", bankVO.getMoney());
        values.put("date", bankVO.getDate().getTime());
        values.put("fixdate", bankVO.getFixDate());
        values.put("fixdatedetail", bankVO.getFixDateDetail());
        values.put("detailname", bankVO.getDetailname());
        values.put("auto", String.valueOf(bankVO.isAuto()));
        values.put("autoId", bankVO.getAutoId());
        values.put("currency", bankVO.getCurrency());
        values.put("realMoney", bankVO.getRealMoney());
        values.put("propertyId", bankVO.getPropertyId());
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

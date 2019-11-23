package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;


public class ConsumeDB {
    private SQLiteOpenHelper db;
    private String TABLE_NAME = "Consumer";
    private String COL_id = "id";

    public ConsumeDB(SQLiteOpenHelper db) {
        this.db = db;
    }



    private ConsumeVO configConsumeVO(Cursor cursor)
    {
        ConsumeVO consumeVO = new ConsumeVO();
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
        consumeVO.setFkKey(cursor.getString(17));
        consumeVO.setBuyerBan(cursor.getString(18));
        consumeVO.setSellerName(cursor.getString(19));
        consumeVO.setSellerAddress(cursor.getString(20));
        return consumeVO;
    }


    private ContentValues configContentValues(ConsumeVO consumeVO)
    {
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
        values.put("fkKey", consumeVO.getFkKey());
        values.put("buyerBan",consumeVO.getBuyerBan());
        values.put("sellerName",consumeVO.getSellerName());
        values.put("sellerAddress",consumeVO.getSellerAddress());
        return values;
    }





    public Double getAllMoney()
    {
        String sql = "SELECT realMoney FROM Consumer ;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        Double total=0.0;
        while (cursor.moveToNext())
        {
            total=total+Double.valueOf(cursor.getString(0));
        }
        return total;
    }




    public List<String> getAllMainType()
    {
        String sql = "SELECT maintype FROM Consumer group by maintype;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<String> type = new ArrayList<>();
        while (cursor.moveToNext())
        {
            type.add(cursor.getString(0));
        }
        return type;
    }

    public List<String> getAllSecondType(String mainType)
    {
        String sql = "SELECT secondtype FROM Consumer where maintype = '"+mainType+"' group by secondtype;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<String> type = new ArrayList<>();
        while (cursor.moveToNext())
        {
            type.add(cursor.getString(0));
        }
        return type;
    }


    public Double getAllSecondTypeMoney(String secondType)
    {
        String sql = "SELECT currency,realMoney,date FROM Consumer where secondType = '"+secondType+"';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        Double total=0.0;
        CurrencyDB currencyDB=new CurrencyDB(db);
        CurrencyVO currencyVO;
        while (cursor.moveToNext())
        {
            currencyVO=currencyDB.getAllByDate(new Date(cursor.getLong(2)),cursor.getString(0));
            total=total+Double.valueOf(currencyVO.getMoney())*Double.valueOf(cursor.getString(1));
        }
        return total;
    }


    public List<ConsumeVO> getMainTypeAllMoney(String mainType) {
        String sql = "SELECT maintype,realMoney,currency,date FROM Consumer where maintype = '"+mainType+"';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }



    public List<ConsumeVO> getRealMoneyIsNull() {
        String sql = "SELECT * FROM Consumer where realMoney isnull or trim(realMoney) = '';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }


    public List<ConsumeVO> getAll() {
        String sql = "SELECT * FROM Consumer order by id desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getQRcode() {
        String sql = "SELECT * FROM Consumer where (detailname is null or length(trim(detailname)) =0 ) and rdNumber is not null and date is not null and number is not null order by id desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getWinAll(long startTime, long endTime) {
        String sql = "SELECT * FROM Consumer where iswin != 'N' and date between '" + startTime + "' and '" + endTime + "' order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getNoWinAll(long startTime, long endTime) {
        String sql = "SELECT * FROM Consumer where iswin = '0' and date between '" + startTime + "' and '" + endTime + "' order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO =configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getTimePeriod(Timestamp startTime, Timestamp endTime, String maintyppe) {
        String sql = "SELECT * FROM Consumer where  date between '" + startTime.getTime() + "' and '" + endTime.getTime() + "' and maintype ='" + maintyppe + "' order by date ;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getMainTypePeriod(String mainType) {
        String sql = "SELECT * FROM Consumer where maintype ='" + mainType + "';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> findByKeyWord(String searchKey) {
        String sql = "SELECT * FROM Consumer where maintype like ? or secondtype like ? or detailname like ? order by date desc;";
        String[] args = {"%"+searchKey+"%","%"+searchKey+"%","%"+searchKey+"%"};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> findByKeyWordAndTime(String searchKey,long start,long end) {
        String sql = "SELECT * FROM Consumer where maintype like ? or secondtype like ? or detailname like ? and date between ? and ? order by date desc;";
        String[] args = {"%"+searchKey+"%","%"+searchKey+"%","%"+searchKey+"%",String.valueOf(start),String.valueOf(end)};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getSecondTypePeriod(String maintyppe, String second) {
        String sql = "SELECT * FROM Consumer where maintype ='" + maintyppe + "' and secondtype = '" + second + "';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }



    public ConsumeVO findByNulAndAmountAndRd(String Nul, String rdNumber,Date cTime) {
        String sql = "SELECT * FROM Consumer where number = ? and rdNumber = ?";
        String[] args = {Nul,rdNumber};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        ConsumeVO consumeVO=null ;
        if (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
            if(!Common.checkEqualOnDay(consumeVO.getDate(),cTime))
            {
                return null;
            }
        }
        cursor.close();
        return consumeVO;
    }


    public List<ConsumeVO> getSecondTimePeriod(Timestamp startTime, Timestamp endTime, String secondtype) {
        String sql = "SELECT * FROM Consumer where  date between '" + startTime.getTime() + "' and '" + endTime.getTime() + "' and secondtype ='" + secondtype + "' order by date ;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO =configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }



    public HashMap<String,Double> getTimeMaxType(long startTime, long endTime) {
        String sql = "SELECT maintype,realMoney,currency FROM Consumer where date between '" + startTime + "' and '" + endTime + "';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        HashMap<String,Double> hashMap=new HashMap<>();
        CurrencyDB currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);
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
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
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
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public HashMap<String, Double> getTimePeriodHashMap(long startTime, long endTime) {
        String sql = "SELECT maintype,realMoney,currency FROM Consumer where  date between '" + startTime + "' and '" + endTime + "' order by realMoney desc ;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        HashMap<String, Double> hashMap = new HashMap<>();
        String main,money,currency;
        CurrencyDB currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);
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


    public ConsumeVO getAutoTimePeriod(Timestamp startTime, Timestamp endTime, String fkKey) {
        String sql = "SELECT * FROM Consumer where fkKey = '" + fkKey + "' and date between '" + startTime.getTime() + "' and '" + endTime.getTime() + "' order by date ;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        ConsumeVO consumeVO = null;
        if (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
        }
        cursor.close();
        return consumeVO;
    }

    public List<ConsumeVO> getFixdateAndfkKeyIsNull() {
        String sql = "SELECT * FROM Consumer where fixdate = 'true' and fkKey is null order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO =configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }


    public List<ConsumeVO> getFixdate() {
        String sql = "SELECT * FROM Consumer where fixdate = 'true' order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO =configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getNotify() {
        String sql = "SELECT * FROM Consumer where notify = 'true' order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }


    public List<ConsumeVO> getAutoCreate(int id) {
        String sql = "SELECT * FROM Consumer where autoId = '" + id + "'order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
            consumeList.add(consumeVO);
        }
        cursor.close();
        return consumeList;
    }

    public List<ConsumeVO> getAutoCreateByFK(String fkKey) {
        String sql = "SELECT * FROM Consumer where fkKey = '" + fkKey + "'order by date desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<ConsumeVO> consumeList = new ArrayList<>();
        ConsumeVO consumeVO;
        while (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
            if(consumeVO.isAuto())
            {
                consumeList.add(consumeVO);
            }else{
                consumeList.add(0,consumeVO);
            }

        }
        cursor.close();
        return consumeList;
    }

    public ConsumeVO findConById(int id) {
        String sql = "SELECT * FROM Consumer where id = '" + id + "'order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        ConsumeVO consumeVO = null;
        if (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
        }
        cursor.close();
        return consumeVO;
    }

    public ConsumeVO findConByFk(String fK) {
        String sql = "SELECT * FROM Consumer where fkKey = ? order by id;";
        String[] args = {fK};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        ConsumeVO consumeVO = null;
        if (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
        }
        cursor.close();
        return consumeVO;
    }

    public ConsumeVO findConByNul(String number) {
        String sql = "SELECT * FROM Consumer where number = ? order by id desc;";
        String[] args = {number};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        ConsumeVO consumeVO = null;
        if (cursor.moveToNext()) {
            consumeVO = configConsumeVO(cursor);
        }
        cursor.close();
        return consumeVO;
    }


    public ConsumeVO findOldCon(ConsumeVO consumeVO) {
        String sql = "SELECT * FROM Consumer where maintype = '" + consumeVO.getMaintype() + "' " +
                "and secondtype = '" + consumeVO.getSecondType() + "' and date = '" + consumeVO.getDate().getTime() + "'" +
                "and realMoney = '" + consumeVO.getRealMoney() + "';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        ConsumeVO c = null;
        if (cursor.moveToNext()) {
            c = configConsumeVO(cursor);
        }
        cursor.close();
        return c;
    }

    public long insert(ConsumeVO consumeVO) {
        ContentValues values = configContentValues(consumeVO);
        return db.getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public long insertHid(ConsumeVO consumeVO) {
        ContentValues values = configContentValues(consumeVO);
        return db.getWritableDatabase().insert(TABLE_NAME, null, values);
    }


    public int update(ConsumeVO consumeVO) {
        ContentValues values = configContentValues(consumeVO);
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(consumeVO.getId())};
        return db.getWritableDatabase().update(TABLE_NAME, values, whereClause, whereArgs);
    }


    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.getWritableDatabase().delete(TABLE_NAME, whereClause, whereArgs);
    }

    public int deleteByFk(String fkKey) {
        String whereClause = "fkKey = ?;";
        String[] whereArgs = {fkKey};
        return db.getWritableDatabase().delete(TABLE_NAME, whereClause, whereArgs);
    }

}

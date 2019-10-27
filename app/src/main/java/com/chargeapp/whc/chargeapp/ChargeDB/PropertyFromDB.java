package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.PropertyFromVO;
import com.chargeapp.whc.chargeapp.TypeCode.FixDateCode;
import com.chargeapp.whc.chargeapp.TypeCode.PropertyType;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class PropertyFromDB {
    private SQLiteOpenHelper db;
    private String TABLE_NAME = "PropertyFrom";
    private String COL_id = "id";

    public PropertyFromDB(SQLiteOpenHelper db) {
        this.db = db;
    }



    //select object
    public PropertyFromVO getPropertyFromVO(Cursor cursor)
    {
        PropertyFromVO propertyFromVO=new PropertyFromVO();
        propertyFromVO.setId(cursor.getString(0));
        propertyFromVO.setType(PropertyType.codeToEnum(cursor.getInt(1)));
        propertyFromVO.setSourceMoney(cursor.getString(2));
        propertyFromVO.setSourceCurrency(cursor.getString(3));
        propertyFromVO.setSourceMainType(cursor.getString(4));
        propertyFromVO.setSourceSecondType(cursor.getString(5));
        propertyFromVO.setSourceTime(new Date(cursor.getLong(6)));
        propertyFromVO.setImportFee(cursor.getString(7));
        propertyFromVO.setImportFeeId(cursor.getString(8));
        propertyFromVO.setFixImport(Boolean.valueOf(cursor.getString(9)));
        propertyFromVO.setFixDateCode(FixDateCode.detailToEnum(cursor.getString(10)));
        propertyFromVO.setFixDateDetail(cursor.getString(11));
        propertyFromVO.setPropertyId(cursor.getString(12));
        propertyFromVO.setFixFromId(cursor.getString(13));
        return propertyFromVO;
    }


    //insert/update
    public ContentValues getContentValues(PropertyFromVO propertyFromVO)
    {
        ContentValues values = new ContentValues();
        values.put("id",propertyFromVO.getId());
        values.put("type",propertyFromVO.getType().getCode());
        values.put("sourceMoney",propertyFromVO.getSourceMoney());
        values.put("sourceCurrency",propertyFromVO.getSourceCurrency());
        values.put("sourceMainType",propertyFromVO.getSourceMainType());
        values.put("sourceSecondType",propertyFromVO.getSourceSecondType());
        values.put("sourceDate",propertyFromVO.getSourceTime().getTime());
        values.put("importFee",propertyFromVO.getImportFee());
        values.put("importFeeId",propertyFromVO.getImportFeeId());
        values.put("fixImport", propertyFromVO.getFixImport().toString());
        values.put("fixDateCode", propertyFromVO.getFixDateCode()==null?null:propertyFromVO.getFixDateCode().getDetail());
        values.put("fixDateDetail", propertyFromVO.getFixDateDetail());
        values.put("propertyId", propertyFromVO.getPropertyId());
        values.put("fixFromId", propertyFromVO.getFixFromId());
        return values;
    }




    public List<PropertyFromVO> getAll() {
        String sql = "SELECT * FROM PropertyFrom order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<PropertyFromVO> propertyFromVOS = new ArrayList<>();
        PropertyFromVO propertyFromVO;
        while (cursor.moveToNext()) {
            propertyFromVO=getPropertyFromVO(cursor);
            propertyFromVOS.add(propertyFromVO);
        }
        cursor.close();
        return propertyFromVOS;
    }

    public List<PropertyFromVO> getFixAll(PropertyType propertyType) {
        String sql = "SELECT * FROM PropertyFrom where type = "+propertyType.getCode()+" and fixImport = 'true'  order by sourceDate desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<PropertyFromVO> propertyFromVOS = new ArrayList<>();
        PropertyFromVO propertyFromVO;
        while (cursor.moveToNext()) {
            propertyFromVO=getPropertyFromVO(cursor);
            propertyFromVOS.add(propertyFromVO);
        }
        cursor.close();
        return propertyFromVOS;
    }


    public List<PropertyFromVO> findByPropertyId(String propertyId) {
        String sql = "SELECT * FROM PropertyFrom where propertyId = ? order by id;";
        String[] args = {propertyId};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<PropertyFromVO> propertyFromVOS = new ArrayList<>();
        PropertyFromVO propertyFromVO;
        while (cursor.moveToNext()) {
            propertyFromVO=getPropertyFromVO(cursor);
            propertyFromVOS.add(propertyFromVO);
        }
        cursor.close();
        return propertyFromVOS;
    }


    public PropertyFromVO findByPropertyFromId(String id) {
        String sql = "SELECT * FROM PropertyFrom where id ='"+id +"' order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        PropertyFromVO propertyFromVO=null;
        if (cursor.moveToNext()) {
            propertyFromVO=getPropertyFromVO(cursor);
        }
        cursor.close();
        return propertyFromVO;
    }


    public PropertyFromVO findByImportFeeId(String id) {
        String sql = "SELECT * FROM PropertyFrom where importFeeId = ? order by id;";
        String[] args = {String.valueOf(id)};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        PropertyFromVO propertyFromVO=null;
        if (cursor.moveToNext()) {
            propertyFromVO=getPropertyFromVO(cursor);
        }
        cursor.close();
        return propertyFromVO;
    }


    public Double totalType(String propertyId, PropertyType type) {
        String sql = "SELECT sourceMoney,sourceCurrency FROM PropertyFrom where propertyId ='"+propertyId +"' and type = '"+type.getCode()+"' order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        Double total=0.0;
        CurrencyVO currencyVO;
        CurrencyDB currencyDB=new CurrencyDB(db);
        String money,currencyMoney;
        while (cursor.moveToNext()) {
            money=cursor.getString(0);
            currencyVO=currencyDB.getOneByType(cursor.getString(1));
            currencyMoney=(currencyVO==null)?"1":currencyVO.getMoney();
            total=total+Double.valueOf(currencyMoney)*Double.valueOf(money);
        }
        cursor.close();
        return total;
    }


    public Double findBySourceMainType(String sourceMainType) {
        String sql = "SELECT sourceMoney,sourceCurrency FROM PropertyFrom where sourceMainType ='"+sourceMainType +"' order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        CurrencyDB currencyDB=new CurrencyDB(db);
        Double total=0.0;
        CurrencyVO currencyVO;
        String money,currencyMoney;
        if (cursor.moveToNext()) {
           money=cursor.getString(0);
           currencyVO=currencyDB.getOneByType(cursor.getString(1));
           currencyMoney=(currencyVO==null)?"1":currencyVO.getMoney();
           total=total+Double.valueOf(currencyMoney)*Double.valueOf(money);
        }
        cursor.close();
        return total;
    }


    public List<PropertyFromVO> findBySearchKey(String searchKey) {
        String sql = "SELECT * FROM PropertyFrom where type like ? or sourceMainType like ? or sourceSecondType like ? order by sourceDate desc;";
        String[] args = {"%"+searchKey+"%","%"+searchKey+"%","%"+searchKey+"%"};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<PropertyFromVO> propertyFromVOS = new ArrayList<>();
        PropertyFromVO propertyFromVO;
        while (cursor.moveToNext()) {
            propertyFromVO=getPropertyFromVO(cursor);
            propertyFromVOS.add(propertyFromVO);
        }
        cursor.close();
        return propertyFromVOS;
    }

    public List<PropertyFromVO> findBySearchKey(String searchKey,Long start,Long end) {
        String sql = "SELECT * FROM PropertyFrom where type like ? or sourceMainType like ? or sourceSecondType like ? and sourceDate between ? and ? order by sourceDate desc;";
        String[] args = {"%"+searchKey+"%","%"+searchKey+"%","%"+searchKey+"%",start.toString(),end.toString()};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<PropertyFromVO> propertyFromVOS = new ArrayList<>();
        PropertyFromVO propertyFromVO;
        while (cursor.moveToNext()) {
            propertyFromVO=getPropertyFromVO(cursor);
            propertyFromVOS.add(propertyFromVO);
        }
        cursor.close();
        return propertyFromVOS;
    }

    public List<PropertyFromVO> findByPropertyMainType(String sourceMainType, String propertyId) {
        String sql = "SELECT * FROM PropertyFrom where sourceMainType ='"+sourceMainType +"' and  propertyId = '"+propertyId+"' order by sourceDate desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<PropertyFromVO> propertyFromVOList=new ArrayList<>();
        while (cursor.moveToNext()) {
            PropertyFromVO propertyFromVO=getPropertyFromVO(cursor);
            propertyFromVOList.add(propertyFromVO);
        }
        cursor.close();
        return propertyFromVOList;
    }


    public List<PropertyFromVO> findFixProperty(String propertyFromVoId) {
        String sql = "SELECT * FROM PropertyFrom where fixFromId = ?  order by sourceDate desc;";
        String[] args = {propertyFromVoId};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<PropertyFromVO> propertyFromVOList=new ArrayList<>();
        while (cursor.moveToNext()) {
            PropertyFromVO propertyFromVO=getPropertyFromVO(cursor);
            propertyFromVOList.add(propertyFromVO);
        }
        cursor.close();
        return propertyFromVOList;
    }


    public List<PropertyFromVO> findFixAllProperty() {
        String sql = "SELECT * FROM PropertyFrom where fixImport = 'true' order by sourceDate desc ;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<PropertyFromVO> propertyFromVOList=new ArrayList<>();
        while (cursor.moveToNext()) {
            PropertyFromVO propertyFromVO=getPropertyFromVO(cursor);
            propertyFromVOList.add(propertyFromVO);
        }
        cursor.close();
        return propertyFromVOList;
    }


    public List<PropertyFromVO> findByPropertySecondType(String sourceSecondType, String propertyId) {
        String sql = "SELECT * FROM PropertyFrom where sourceSecondType ='"+sourceSecondType +"' and  propertyId = '"+propertyId+"' order by sourceDate desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<PropertyFromVO> propertyFromVOList=new ArrayList<>();
        while (cursor.moveToNext()) {
            PropertyFromVO propertyFromVO=getPropertyFromVO(cursor);
            propertyFromVOList.add(propertyFromVO);
        }
        cursor.close();
        return propertyFromVOList;
    }

    public Double findBySourceSecondType(String sourceSecondType) {
        String sql = "SELECT sourceMoney,sourceCurrency FROM PropertyFrom where sourceSecondType ='"+sourceSecondType +"' order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        CurrencyDB currencyDB=new CurrencyDB(db);
        Double total=0.0;
        CurrencyVO currencyVO;
        String money,currencyMoney;
        if (cursor.moveToNext()) {
            money=cursor.getString(0);
            currencyVO=currencyDB.getOneByType(cursor.getString(1));
            currencyMoney=(currencyVO==null)?"1":currencyVO.getMoney();
            total=total+Double.valueOf(currencyMoney)*Double.valueOf(money);
        }
        cursor.close();
        return total;
    }



    public PropertyFromVO findAutoBySourceTimeAndAutoId(Long start,Long end,String id) {
        String sql = "SELECT * FROM PropertyFrom where sourceDate between ? and ? and  fixFromId = ? order by id;";
        String[] args = {String.valueOf(start),String.valueOf(end),String.valueOf(id)};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        PropertyFromVO propertyFromVO=null;
        if (cursor.moveToNext()) {
           propertyFromVO=getPropertyFromVO(cursor);
        }
        cursor.close();
        return propertyFromVO;
    }


    public Double getTotalAll() {
        String sql = "SELECT sourceMoney,sourceCurrency FROM PropertyFrom ;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        CurrencyDB currencyDB=new CurrencyDB(db);
        Double total=0.0;
        CurrencyVO currencyVO;
        String money;
        if (cursor.moveToNext()) {
            money=cursor.getString(0);
            currencyVO=currencyDB.getOneByType(cursor.getString(1));
            total=total+Double.valueOf(currencyVO.getMoney())*Double.valueOf(money);
        }
        cursor.close();
        return total;
    }


    public Map<String,Double> getPieDataMainType(PropertyType propertyType)
    {
        String sql = "SELECT * FROM PropertyFrom where type ='"+propertyType.getCode() +"' order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        CurrencyDB currencyDB=new CurrencyDB(db);
        PropertyFromVO propertyFromVO;
        Map<String,Double> map=new HashMap<>();
        while (cursor.moveToNext()) {
            propertyFromVO=getPropertyFromVO(cursor);
            Double moneyMap=map.get(propertyFromVO.getSourceMainType());
            Long start=propertyFromVO.getSourceTime().getTime()-86400000L;
            Long end=propertyFromVO.getSourceTime().getTime()+86400000L;
            CurrencyVO currencyVO=currencyDB.getBytimeAndType(start,end,propertyFromVO.getSourceCurrency());
            Double money=Double.valueOf(propertyFromVO.getSourceMoney())*Double.valueOf(currencyVO.getMoney());
            if(moneyMap==null)
            {
                moneyMap=money;
            }else{
                moneyMap=moneyMap+money;
            }
            map.put(propertyFromVO.getSourceMainType(),moneyMap);
        }
        return map;
    }


    public Map<String,Double> getPieDataByPropertyId(PropertyType propertyType, String propertyId)
    {
        String sql = "SELECT * FROM PropertyFrom where type = ? and propertyId = ? order by id;";
        String[] args = {String.valueOf(propertyType.getCode()),propertyId};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        CurrencyDB currencyDB=new CurrencyDB(db);
        PropertyFromVO propertyFromVO;
        Map<String,Double> map=new HashMap<>();
        while (cursor.moveToNext()) {
            propertyFromVO=getPropertyFromVO(cursor);
            Double moneyMap=map.get(propertyFromVO.getSourceMainType());
            Long start=propertyFromVO.getSourceTime().getTime()-86400000L;
            Long end=propertyFromVO.getSourceTime().getTime()+86400000L;
            CurrencyVO currencyVO=currencyDB.getBytimeAndType(start,end,propertyFromVO.getSourceCurrency());
            Double money=Double.valueOf(propertyFromVO.getSourceMoney())*Double.valueOf(currencyVO.getMoney());
            if(moneyMap==null)
            {
                moneyMap=money;
            }else{
                moneyMap=moneyMap+money;
            }
            map.put(propertyFromVO.getSourceMainType(),moneyMap);
        }
        return map;
    }

    public Map<String,Double> getPieDataSecondType(PropertyType propertyType, String sourceMainType, String propertyId)
    {
        String sql = "SELECT * FROM PropertyFrom where type ='"+propertyType.getCode() +"' and sourceMainType =  '"+sourceMainType+"' and propertyId = '"+propertyId+"' order by id;";
        String[] args = {};
        Cursor cursor =db.getReadableDatabase().rawQuery(sql, args);
        CurrencyDB currencyDB=new CurrencyDB(db);
        PropertyFromVO propertyFromVO;
        Map<String,Double> map=new HashMap<>();
        while (cursor.moveToNext()) {
            propertyFromVO=getPropertyFromVO(cursor);
            Double moneyMap=map.get(propertyFromVO.getSourceSecondType());
            Long start=propertyFromVO.getSourceTime().getTime()-86400000L;
            Long end=propertyFromVO.getSourceTime().getTime()+86400000L;
            CurrencyVO currencyVO=currencyDB.getBytimeAndType(start,end,propertyFromVO.getSourceCurrency());
            Double money=Double.valueOf(propertyFromVO.getSourceMoney())*Double.valueOf(currencyVO.getMoney());
            if(moneyMap==null)
            {
                moneyMap=money;
            }else{
                moneyMap=moneyMap+money;
            }
            map.put(propertyFromVO.getSourceSecondType(),moneyMap);
        }
        return map;
    }

    public long insert(PropertyFromVO propertyFromVO) {

        ContentValues values = getContentValues(propertyFromVO);
        return db.getWritableDatabase().insert(TABLE_NAME, null, values);
    }


    public long update(PropertyFromVO propertyFromVO) {
        ContentValues values = getContentValues(propertyFromVO);
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {propertyFromVO.getId()};
        return db.getWritableDatabase().update(TABLE_NAME, values, whereClause, whereArgs);
    }

    public int deleteById(String id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {id};
        return db.getWritableDatabase().delete(TABLE_NAME, whereClause, whereArgs);
    }

    public int deleteByPropertyId(String propertyId) {
        String whereClause ="propertyId = ?;";
        String[] whereArgs = {propertyId};
        String sql = "SELECT * FROM PropertyFrom where  propertyId = ? order by id;";
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, whereArgs);
        ConsumeDB consumeDB=new ConsumeDB(db);
        while (cursor.moveToNext())
        {
            PropertyFromVO propertyFromVO=getPropertyFromVO(cursor);
            if(propertyFromVO.getImportFeeId()!=null)
            {
                consumeDB.deleteByFk(propertyFromVO.getImportFeeId());
            }
        }
        return db.getWritableDatabase().delete(TABLE_NAME, whereClause, whereArgs);
    }

}

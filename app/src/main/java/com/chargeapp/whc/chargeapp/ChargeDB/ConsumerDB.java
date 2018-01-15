package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


public class ConsumerDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="Consumer";
    private String COL_id="id";
    public ConsumerDB(SQLiteDatabase db)
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
            consumeVO.setMoney(cursor.getString(2));
            consumeVO.setDate(new Date(cursor.getLong(3)));
            consumeVO.setNumber(cursor.getString(4));
            consumeVO.setFixDate(cursor.getString(5));
            consumeVO.setFixDateDetail(cursor.getString(6));
            consumeVO.setNotify(Boolean.valueOf(cursor.getString(7)));
            consumeVO.setDetailname(cursor.getString(8));
            consumeVO.setIsWin(cursor.getString(9));
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
            consumeVO=new ConsumeVO();
            consumeVO.setId(cursor.getInt(0));
            consumeVO.setMaintype(cursor.getString(1));
            consumeVO.setSecondType(cursor.getString(2));
            consumeVO.setMoney(cursor.getString(2));
            consumeVO.setDate(new Date(cursor.getLong(3)));
            consumeVO.setNumber(cursor.getString(4));
            consumeVO.setFixDate(cursor.getString(5));
            consumeVO.setFixDateDetail(cursor.getString(6));
            consumeVO.setNotify(Boolean.valueOf(cursor.getString(7)));
            consumeVO.setDetailname(cursor.getString(8));
            consumeVO.setIsWin(cursor.getString(9));
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
            consumeVO.setMoney(cursor.getString(2));
            consumeVO.setDate(new Date(cursor.getLong(3)));
            consumeVO.setNumber(cursor.getString(4));
            consumeVO.setFixDate(cursor.getString(5));
            consumeVO.setFixDateDetail(cursor.getString(6));
            consumeVO.setNotify(Boolean.valueOf(cursor.getString(7)));
            consumeVO.setDetailname(cursor.getString(8));
            consumeVO.setIsWin(cursor.getString(9));
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

    public int deletecarNul(String carNul) {
        String whereClause = "carNul" + " = ?;";
        String[] whereArgs = {carNul};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

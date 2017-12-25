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
            int id = cursor.getInt(0);
            String maintype =cursor.getString(1);
            String secondtype=cursor.getString(2);
            String money=cursor.getString(2);
            Date date=new Date(cursor.getLong(3));
            String number=cursor.getString(4);
            String fixdate=cursor.getString(5);
            String fixdatedetail=cursor.getString(6);
            String notify=cursor.getString(7);
            String detailname=cursor.getString(8);
            consumeVO=new ConsumeVO(id,detailname,money,date,number,maintype,secondtype,fixdate,fixdatedetail,Boolean.valueOf(notify));
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
            String maintype =cursor.getString(1);
            String secondtype=cursor.getString(2);
            String money=cursor.getString(2);
            Date date=new Date(cursor.getLong(3));
            String number=cursor.getString(4);
            String fixdate=cursor.getString(5);
            String fixdatedetail=cursor.getString(6);
            String notify=cursor.getString(7);
            String detailname=cursor.getString(8);
            consumeVO=new ConsumeVO(id,detailname,money,date,number,maintype,secondtype,fixdate,fixdatedetail,Boolean.valueOf(notify));
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
        values.put("carNul",consumeVO.getCarNul());
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
        values.put("carNul",consumeVO.getCarNul());
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

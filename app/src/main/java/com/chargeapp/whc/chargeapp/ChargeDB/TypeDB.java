package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chargeapp.whc.chargeapp.Model.BankVO;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


public class TypeDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="Type";
    private String COL_id="id";
    public TypeDB(SQLiteDatabase db)
    {
        this.db=db;
    }

    public List<BankVO> getAll() {
        String sql = "SELECT * FROM Consumer order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<BankVO> BankVOList = new ArrayList<>();
        BankVO bankVO;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String maintype =cursor.getString(1);
            String money=cursor.getString(2);
            Date date=new Date(cursor.getLong(3));
            String fixdate=cursor.getString(4);
            String fixdatedetail=cursor.getString(5);
            String detailname=cursor.getString(6);
            bankVO=new BankVO(maintype,detailname,money,date,fixdate,id,detailname);
            BankVOList.add(bankVO);
        }
        cursor.close();
        return BankVOList;
    }

    public BankVO findById(int id) {
        String[] columns = {
          "id,maintype,money,date,fixdate,fixdatedetail,detailname"
        };
        String selection = "id = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query("Consumer", columns, selection, selectionArgs,
                null, null, null);
        BankVO bankVO = null;
        if (cursor.moveToNext()) {
            String maintype =cursor.getString(1);
            String money=cursor.getString(2);
            Date date=new Date(cursor.getLong(3));
            String fixdate=cursor.getString(4);
            String fixdatedetail=cursor.getString(5);
            String detailname=cursor.getString(6);
            bankVO=new BankVO(maintype,detailname,money,date,fixdate,id,detailname);
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

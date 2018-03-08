package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


public class BankTybeDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="BANKTYPE";
    private String COL_id="id";
    public BankTybeDB(SQLiteDatabase db)
    {
        this.db=db;
    }



    public List<BankTypeVO> getAll() {
        String sql = "SELECT * FROM BANKTYPE order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<BankTypeVO> BankTypeVOlist = new ArrayList<>();
        BankTypeVO bankTypeVO;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String groupnumber =cursor.getString(1);
            String name=cursor.getString(2);
            int image=cursor.getInt(3);
            bankTypeVO=new BankTypeVO(id,groupnumber,name,image);
            BankTypeVOlist.add(bankTypeVO);
        }
        cursor.close();
        return BankTypeVOlist;
    }

    public List<BankTypeVO> getExport() {
        String sql = "SELECT * FROM BANKTYPE where id > 7 order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<BankTypeVO> BankTypeVOlist = new ArrayList<>();
        BankTypeVO bankTypeVO;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String groupnumber =cursor.getString(1);
            String name=cursor.getString(2);
            int image=cursor.getInt(3);
            bankTypeVO=new BankTypeVO(id,groupnumber,name,image);
            BankTypeVOlist.add(bankTypeVO);
        }
        cursor.close();
        return BankTypeVOlist;
    }

    public BankTypeVO findByName(String n) {
        String[] columns = {
                "id,groupNumber,name,image"
        };
        String selection = "name = ?;";
        String[] selectionArgs = {n};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs,
                null, null, null);
        BankTypeVO bankTypeVO = null;
        if (cursor.moveToNext()) {
            int id=cursor.getInt(0);
            String groupnumber =cursor.getString(1);
            String name=cursor.getString(2);
            int image=cursor.getInt(3);
            bankTypeVO=new BankTypeVO(id,groupnumber,name,image);
        }
        cursor.close();
        return bankTypeVO;
    }


    public BankTypeVO findById(int id) {
        String[] columns = {
          "id,groupNumber,name,image"
        };
        String selection = "id = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs,
                null, null, null);
        BankTypeVO bankTypeVO = null;
        if (cursor.moveToNext()) {
            String groupnumber =cursor.getString(1);
            String name=cursor.getString(2);
            int image=cursor.getInt(3);
            bankTypeVO=new BankTypeVO(id,groupnumber,name,image);
        }
        cursor.close();
        return bankTypeVO;
    }

    public long insert(BankTypeVO bankTypeVO) {
        ContentValues values = new ContentValues();
        values.put("groupNumber",bankTypeVO.getGroupNumber());
        values.put("name", bankTypeVO.getName());
        values.put("image",bankTypeVO.getImage());
        return db.insert(TABLE_NAME, null, values);
    }

    public long insertHid(BankTypeVO bankTypeVO) {
        ContentValues values = new ContentValues();
        values.put("id",bankTypeVO.getId());
        values.put("groupNumber",bankTypeVO.getGroupNumber());
        values.put("name", bankTypeVO.getName());
        values.put("image",bankTypeVO.getImage());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(BankTypeVO bankTypeVO) {
        ContentValues values = new ContentValues();
        values.put("groupNumber",bankTypeVO.getGroupNumber());
        values.put("name", bankTypeVO.getName());
        values.put("image",bankTypeVO.getImage());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(bankTypeVO.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }
    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

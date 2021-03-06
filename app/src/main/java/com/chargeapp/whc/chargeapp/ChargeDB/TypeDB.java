package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chargeapp.whc.chargeapp.Control.Download;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TypeDB {
    private SQLiteOpenHelper db;
    private String TABLE_NAME="Type";
    private String COL_id="id";
    public TypeDB(SQLiteOpenHelper db)
    {
        this.db=db;
    }

    public List<TypeVO> getAll() {
        String sql = "SELECT * FROM Type order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<TypeVO> typeList = new ArrayList<>();
        TypeVO typeVO;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String groupNumber = cursor.getString(1);
            String name = cursor.getString(2);
            int image=cursor.getInt(3);
            typeVO=new TypeVO(id,groupNumber,name,image);
            typeList.add(typeVO);
        }
        cursor.close();
        return typeList;
    }


    public List<TypeVO> getExport() {
        String sql = "SELECT * FROM Type where id > 9 order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<TypeVO> typeList = new ArrayList<>();
        TypeVO typeVO;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String groupNumber = cursor.getString(1);
            String name = cursor.getString(2);
            int image=cursor.getInt(3);
            typeVO=new TypeVO(id,groupNumber,name,image);
            typeList.add(typeVO);
        }
        cursor.close();
        return typeList;
    }

    public TypeVO findTypeName(String groupName,String name) {
        String sql = "SELECT * FROM Type where groupNumber = '"+groupName+"' and name = '"+name+"' order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        TypeVO typeVO=null;
        if (cursor.moveToNext()) {
            typeVO=new TypeVO();
            typeVO.setId(cursor.getInt(0));
            typeVO.setGroupNumber(cursor.getString(1));
            typeVO.setName(cursor.getString(2));
            typeVO.setImage(cursor.getInt(3));
        }
        cursor.close();
        return typeVO;
    }

    public TypeVO findTypeName(String n) {
        String sql = "SELECT * FROM Type where TRIM (name) = '"+n+"' order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        TypeVO typeVO=null;
        if (cursor.moveToNext()) {
            typeVO=new TypeVO();
            typeVO.setId(cursor.getInt(0));
            typeVO.setGroupNumber(cursor.getString(1));
            typeVO.setName(cursor.getString(2));
            typeVO.setImage(cursor.getInt(3));
        }
        cursor.close();
        return typeVO;
    }

    public List<TypeVO> findLikeTypeName(String n) {
        String sql = "SELECT * FROM Type where name like '%"+n+"%' order by id;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        TypeVO typeVO=null;
        List<TypeVO> typeVOS=new ArrayList<>();
        while (cursor.moveToNext()) {
            typeVO=new TypeVO();
            typeVO.setId(cursor.getInt(0));
            typeVO.setGroupNumber(cursor.getString(1));
            typeVO.setName(cursor.getString(2));
            typeVO.setImage(cursor.getInt(3));
            typeVOS.add(typeVO);
        }
        cursor.close();
        return typeVOS;
    }

    public TypeVO findById(int id) {
        String[] columns = {
                COL_id,"groupNumber","name", "image"
        };
        String selection = COL_id + " = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.getReadableDatabase().query(TABLE_NAME, columns, selection, selectionArgs,
                null, null, null);
        TypeVO typeVO = null;
        if (cursor.moveToNext()) {
            id = cursor.getInt(0);
            String groupNumber = cursor.getString(1);
            String name = cursor.getString(2);
            int image=cursor.getInt(3);
            typeVO=new TypeVO(id,groupNumber,name,image);
        }
        cursor.close();
        return typeVO;
    }

    public long insert(TypeVO typeVO) {
        ContentValues values = new ContentValues();
        values.put("groupNumber",typeVO.getGroupNumber());
        values.put("name", typeVO.getName());
        values.put("image",typeVO.getImage());
        return db.getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public long insertHId(TypeVO typeVO) {
        ContentValues values = new ContentValues();
        values.put("id",typeVO.getId());
        values.put("groupNumber",typeVO.getGroupNumber());
        values.put("name", typeVO.getName());
        values.put("image",typeVO.getImage());
        return db.getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public int update(TypeVO typeVO) {
        ContentValues values = new ContentValues();
        values.put("groupNumber",typeVO.getGroupNumber());
        values.put("name", typeVO.getName());
        values.put("image",typeVO.getImage());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(typeVO.getId())};
        return db.getWritableDatabase().update(TABLE_NAME, values, whereClause, whereArgs);
    }
    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.getWritableDatabase().delete(TABLE_NAME, whereClause, whereArgs);
    }

}

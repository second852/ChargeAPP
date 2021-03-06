package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1709008NB01 on 2018/1/12.
 */

public class TypeDetailDB {
    private SQLiteOpenHelper db;
    private String TABLE_NAME="TypeDetail";
    private String COL_id="id";
    public TypeDetailDB(SQLiteOpenHelper db)
    {
        this.db=db;
    }

    public List<TypeDetailVO> getTypdAll() {
        String sql = "SELECT * FROM TypeDetail ;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<TypeDetailVO> typeDetailList = new ArrayList<>();
        TypeDetailVO typeDetailVO;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String groupNumber = cursor.getString(1);
            String name = cursor.getString(2);
            int image=cursor.getInt(3);
            String keyword=cursor.getString(4);
            typeDetailVO=new TypeDetailVO(id,groupNumber,name,image,keyword);
            typeDetailList.add(typeDetailVO);
        }
        cursor.close();
        return typeDetailList;
    }

    public List<TypeDetailVO> getExport() {
        String sql = "SELECT * FROM TypeDetail where id > 35 order by id ;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<TypeDetailVO> typeDetailList = new ArrayList<>();
        TypeDetailVO typeDetailVO;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String groupNumber = cursor.getString(1);
            String name = cursor.getString(2);
            int image=cursor.getInt(3);
            String keyword=cursor.getString(4);
            typeDetailVO=new TypeDetailVO(id,groupNumber,name,image,keyword);
            typeDetailList.add(typeDetailVO);
        }
        cursor.close();
        return typeDetailList;
    }

    public List<TypeDetailVO> getHaveDetailTypdAll() {
        String sql = "SELECT * FROM TypeDetail where keyword != '0';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<TypeDetailVO> typeDetailList = new ArrayList<>();
        TypeDetailVO typeDetailVO;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String groupNumber = cursor.getString(1);
            String name = cursor.getString(2);
            int image=cursor.getInt(3);
            String keyword=cursor.getString(4);
            typeDetailVO=new TypeDetailVO(id,groupNumber,name,image,keyword);
            typeDetailList.add(typeDetailVO);
        }
        cursor.close();
        return typeDetailList;
    }

    public TypeDetailVO findTypeDetailById(int id) {
        String[] columns = {
                COL_id,"groupNumber","name", "image","keyword"
        };
        String selection = COL_id + " = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.getReadableDatabase().query("TypeDetail", columns, selection, selectionArgs,
                null, null, null);
        TypeDetailVO typeDetailVO = null;
        if (cursor.moveToNext()) {
            id = cursor.getInt(0);
            String groupNumber = cursor.getString(1);
            String name = cursor.getString(2);
            int image=cursor.getInt(3);
            String keyword=cursor.getString(4);
            typeDetailVO=new TypeDetailVO(id,groupNumber,name,image,keyword);
        }
        cursor.close();
        return typeDetailVO;
    }

    public ArrayList<TypeDetailVO> findByGroupname(String groupname) {
        String[] columns = {
                COL_id,"groupNumber","name", "image","keyword","keyword"
        };
        String selection = "groupNumber = ?;";
        String[] selectionArgs = {String.valueOf(groupname)};
        Cursor cursor = db.getReadableDatabase().query("TypeDetail", columns, selection, selectionArgs,
                null, null, null);
        TypeDetailVO typeDetailVO = null;
        ArrayList<TypeDetailVO> typeDetailVOlist=new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String groupNumber = cursor.getString(1);
            String name = cursor.getString(2);
            int image=cursor.getInt(3);
            String keyword=cursor.getString(4);
            typeDetailVO=new TypeDetailVO(id,groupNumber,name,image,keyword);
            typeDetailVOlist.add(typeDetailVO);
        }
        cursor.close();
        return typeDetailVOlist;
    }

    public TypeDetailVO findByname(String name,String groupNumber) {
        String sql = "SELECT * FROM TypeDetail where name = '"+name+"' and groupNumber = '"+groupNumber+"';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        TypeDetailVO typeDetailVO = null;
        if (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String GetGroupNumber = cursor.getString(1);
            String GetName = cursor.getString(2);
            int image=cursor.getInt(3);
            String keyword=cursor.getString(4);
            typeDetailVO=new TypeDetailVO(id,GetGroupNumber,GetName,image,keyword);
        }
        cursor.close();
        return typeDetailVO;
    }

    public long insert(TypeDetailVO typeDetailVO) {
        ContentValues values = new ContentValues();
        values.put("name", typeDetailVO.getName());
        values.put("groupNumber",typeDetailVO.getGroupNumber());
        values.put("image",typeDetailVO.getImage());
        values.put("keyword",typeDetailVO.getKeyword());
        return db.getWritableDatabase().insert("TypeDetail", null, values);
    }

    public long insertHid(TypeDetailVO typeDetailVO) {
        ContentValues values = new ContentValues();
        values.put("id", typeDetailVO.getId());
        values.put("name", typeDetailVO.getName());
        values.put("groupNumber",typeDetailVO.getGroupNumber());
        values.put("image",typeDetailVO.getImage());
        values.put("keyword",typeDetailVO.getKeyword());
        return db.getWritableDatabase().insert("TypeDetail", null, values);
    }

    public int update(TypeDetailVO typeDetailVO) {
        ContentValues values = new ContentValues();
        values.put("name", typeDetailVO.getName());
        values.put("groupNumber",typeDetailVO.getGroupNumber());
        values.put("image",typeDetailVO.getImage());
        values.put("keyword",typeDetailVO.getKeyword());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(typeDetailVO.getId())};
        return db.getWritableDatabase().update("TypeDetail", values, whereClause, whereArgs);
    }
    public int deleteTypeDetailById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.getWritableDatabase().delete("TypeDetail", whereClause, whereArgs);
    }

    public int deleteTypeDetailByName(String groupNumber) {
        String whereClause ="groupNumber = ?;";
        String[] whereArgs = {groupNumber};
        return db.getWritableDatabase().delete("TypeDetail", whereClause, whereArgs);
    }
}

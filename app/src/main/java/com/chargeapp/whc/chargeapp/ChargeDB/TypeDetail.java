package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1709008NB01 on 2018/1/12.
 */

public class TypeDetail {
    private SQLiteDatabase db;
    private String TABLE_NAME="TypeDetail";
    private String COL_id="id";
    public TypeDetail(SQLiteDatabase db)
    {
        this.db=db;
    }

    public List<TypeDetailVO> getTypdAll() {
        String sql = "SELECT * FROM TypeDetail ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
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
        Cursor cursor = db.rawQuery(sql, args);
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
        Cursor cursor = db.query("TypeDetail", columns, selection, selectionArgs,
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
        Cursor cursor = db.query("TypeDetail", columns, selection, selectionArgs,
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

    public long insert(TypeDetailVO typeDetailVO) {
        ContentValues values = new ContentValues();
        values.put("name", typeDetailVO.getName());
        values.put("groupNumber",typeDetailVO.getGroupNumber());
        values.put("image",typeDetailVO.getImage());
        values.put("keyword",typeDetailVO.getKeyword());
        return db.insert("TypeDetail", null, values);
    }

    public int update(TypeDetailVO typeDetailVO) {
        ContentValues values = new ContentValues();
        values.put("name", typeDetailVO.getName());
        values.put("groupNumber",typeDetailVO.getGroupNumber());
        values.put("image",typeDetailVO.getImage());
        values.put("keyword",typeDetailVO.getKeyword());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(typeDetailVO.getId())};
        return db.update("TypeDetail", values, whereClause, whereArgs);
    }
    public int deleteTypeDetailById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete("TypeDetail", whereClause, whereArgs);
    }
}

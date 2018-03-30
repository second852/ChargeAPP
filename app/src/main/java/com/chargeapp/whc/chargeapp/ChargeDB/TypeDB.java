package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
    private SQLiteDatabase db;
    private String TABLE_NAME="Type";
    private String COL_id="id";
    public TypeDB(SQLiteDatabase db)
    {
        this.db=db;
    }

    public List<TypeVO> getAll() {
        String sql = "SELECT * FROM Type order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
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


    public static ArrayList<Map<String, Object>> getAllName() {
        String sql = "SELECT * FROM Type order by id;";
        String[] args = {};
        Cursor cursor = MainActivity.chargeAPPDB.getReadableDatabase().rawQuery(sql, args);
        ArrayList items = new ArrayList<Map<String, Object>>();
        HashMap item;
        while (cursor.moveToNext()) {
            item = new HashMap<String, Object>();
            item.put("image", Download.imageAll[cursor.getInt(3)]);
            item.put("text", cursor.getString(2));
            items.add(item);
        }
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.add);
        item.put("text", "新增");
        items.add(item);
        cursor.close();
        return items;
    }

    public List<TypeVO> getExport() {
        String sql = "SELECT * FROM Type where id > 9 order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
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

    public TypeVO findTypeName(String n) {
        String sql = "SELECT * FROM Type where name = '"+n+"' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
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

    public TypeVO findById(int id) {
        String[] columns = {
                COL_id,"groupNumber","name", "image"
        };
        String selection = COL_id + " = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs,
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
        return db.insert(TABLE_NAME, null, values);
    }

    public long insertHId(TypeVO typeVO) {
        ContentValues values = new ContentValues();
        values.put("id",typeVO.getId());
        values.put("groupNumber",typeVO.getGroupNumber());
        values.put("name", typeVO.getName());
        values.put("image",typeVO.getImage());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(TypeVO typeVO) {
        ContentValues values = new ContentValues();
        values.put("groupNumber",typeVO.getGroupNumber());
        values.put("name", typeVO.getName());
        values.put("image",typeVO.getImage());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(typeVO.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }
    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

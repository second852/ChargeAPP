package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ChargeAPPDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "ChargeAPP";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "Type";
    private static final String COL_id = "id";
    private static final String COL_name = "name";
    private static final String COL_groupNumber= "groupNumber";
    private static final String COL_image = "image";


    private static final String TABLE_CREATE =
            "CREATE TABLE Type ( id INTEGER PRIMARY KEY AUTOINCREMENT, groupNumber TEXT NOT NULL," +
                    "name TEXT, image INTEGER ) ;";
    private static final String TABLE_CARRIER =
            "CREATE TABLE CARRIER ( id INTEGER PRIMARY KEY AUTOINCREMENT, CARNUL TEXT NOT NULL," +
                    "PASSWORD TEXT) ;";

    private static final String TYPE_DETAIL=" CREATE TABLE TypeDetail ( id INTEGER PRIMARY KEY AUTOINCREMENT, groupNumber TEXT NOT NULL," +
            "name TEXT, image INTEGER ); ";

    private static final String TYPE_COMSUMER=" CREATE TABLE Consumer ( id INTEGER PRIMARY KEY AUTOINCREMENT, maintype TEXT NOT NULL," +
            "secondtype TEXT, money TEXT, date Date , number TEXT , fixdate TEXT , fixdatedetail TEXT ,notify TEXT, detailname TEXT, carNul TEXT" +
            "); ";

    private static final String TYPE_BANK=" CREATE TABLE BANK ( id INTEGER PRIMARY KEY AUTOINCREMENT, maintype TEXT NOT NULL," +
            "money TEXT, date Date , fixdate TEXT , fixdatedetail TEXT , detailname TEXT" + "); ";

    private static final String TABLE_BANK_TYPE =
            "CREATE TABLE BANKTYPE ( id INTEGER PRIMARY KEY AUTOINCREMENT, groupNumber TEXT NOT NULL," +
                    "name TEXT, image INTEGER ) ;";

    private static final String TABLE_INVOICE =
            "CREATE TABLE INVOICE ( id INTEGER PRIMARY KEY AUTOINCREMENT, invNum TEXT NOT NULL," +
                    "cardType TEXT, cardNo TEXT, cardEncrypt TEXT, time DATETIME, amount TEXT, detail TEXT, sellerName TEXT, invDonatable TEXT , donateMark TEXT, carrier TEXT, maintype TEXT, secondtype TEXT);";

    public ChargeAPPDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        db.execSQL(TYPE_DETAIL);
        db.execSQL(TYPE_COMSUMER);
        db.execSQL(TYPE_BANK);
        db.execSQL(TABLE_BANK_TYPE);
        db.execSQL(TABLE_INVOICE);
        db.execSQL(TABLE_CARRIER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + "TypeDetail");
        db.execSQL("DROP TABLE IF EXISTS " + "Consumer");
        db.execSQL("DROP TABLE IF EXISTS " + "Bank");
        db.execSQL("DROP TABLE IF EXISTS " + "BNKTYPE");
        db.execSQL("DROP TABLE IF EXISTS " + "INVOICE");
        db.execSQL("DROP TABLE IF EXISTS " + "CARRIER");
        onCreate(db);
    }

    public List<TypeVO> getAll() {
        SQLiteDatabase db = getReadableDatabase();
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

    public TypeVO findById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {
                COL_id,COL_groupNumber,COL_name, COL_image
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
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_groupNumber,typeVO.getGroupNumber());
        values.put(COL_name, typeVO.getName());
        values.put(COL_image,typeVO.getImage());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(TypeVO typeVO) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_name, typeVO.getName());
        values.put(COL_groupNumber,typeVO.getGroupNumber());
        values.put(COL_image,typeVO.getImage());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(typeVO.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }
    public int deleteById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    public List<TypeDetailVO> getTypdAll() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COL_id, COL_groupNumber, COL_name,COL_image
        };
        Cursor cursor = db.query("TypeDetail", columns, null, null, null, null,
                COL_id);
        List<TypeDetailVO> typeDetailList = new ArrayList<>();
        TypeDetailVO typeDetailVO;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String groupNumber = cursor.getString(1);
            String name = cursor.getString(2);
            int image=cursor.getInt(3);
            typeDetailVO=new TypeDetailVO(id,groupNumber,name,image);
            typeDetailList.add(typeDetailVO);
        }
        cursor.close();
        return typeDetailList;
    }

    public TypeDetailVO findTypeDetailById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {
                COL_id, COL_groupNumber, COL_name,COL_image
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
            typeDetailVO=new TypeDetailVO(id,groupNumber,name,image);
        }
        cursor.close();
        return typeDetailVO;
    }

    public ArrayList<TypeDetailVO> findByGroupname(String groupname) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {
                COL_id, COL_groupNumber, COL_name,COL_image
        };
        String selection = COL_groupNumber + " = ?;";
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
            typeDetailVO=new TypeDetailVO(id,groupNumber,name,image);
            typeDetailVOlist.add(typeDetailVO);
        }
        cursor.close();
        return typeDetailVOlist;
    }

    public long insert(TypeDetailVO typeDetailVO) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_name, typeDetailVO.getName());
        values.put(COL_groupNumber,typeDetailVO.getGroupNumber());
        values.put(COL_image,typeDetailVO.getImage());
        return db.insert("TypeDetail", null, values);
    }

    public int update(TypeDetailVO typeDetailVO) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_name, typeDetailVO.getName());
        values.put(COL_groupNumber,typeDetailVO.getGroupNumber());
        values.put(COL_image,typeDetailVO.getImage());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(typeDetailVO.getId())};
        return db.update("TypeDetail", values, whereClause, whereArgs);
    }
    public int deleteTypeDetailById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete("TypeDetail", whereClause, whereArgs);
    }

}

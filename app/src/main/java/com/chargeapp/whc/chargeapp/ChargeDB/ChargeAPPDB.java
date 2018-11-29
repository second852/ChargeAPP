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

    private static final String TABLE_CREATE =
            "CREATE TABLE Type ( id INTEGER PRIMARY KEY AUTOINCREMENT, groupNumber TEXT NOT NULL," +
                    "name TEXT, image INTEGER ) ;";
    private static final String TABLE_CARRIER =
            "CREATE TABLE CARRIER ( id INTEGER PRIMARY KEY AUTOINCREMENT, CARNUL TEXT NOT NULL," +
                    "PASSWORD TEXT) ;";

    private static final String TABLE_PRICE =
            "CREATE TABLE PRICE ( invoYm TEXT PRIMARY KEY,superPrizeNo TEXT,spcPrizeNo TEXT,firstPrizeNo1 TEXT," +
                    "firstPrizeNo2 TEXT, firstPrizeNo3 TEXT,sixthPrizeNo1 TEXT, sixthPrizeNo2 TEXT, sixthPrizeNo3 TEXT, superPrizeAmt TEXT, spcPrizeAmt TEXT, firstPrizeAmt TEXT ,secondPrizeAmt TEXT, thirdPrizeAmt TEXT, fourthPrizeAmt TEXT, " +
                    "fifthPrizeAmt TEXT, sixthPrizeAmt TEXT, sixthPrizeNo4 TEXT, sixthPrizeNo5 TEXT, sixthPrizeNo6 TEXT);";

    private static final String TABLE_HERATYTEAM =
            "CREATE TABLE HEARTYTEAM ( id INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT NOT NULL," +
                    "NUMBER TEXT) ;";

    private static final String TYPE_DETAIL=" CREATE TABLE TypeDetail ( id INTEGER PRIMARY KEY AUTOINCREMENT, groupNumber TEXT NOT NULL," +
            "name TEXT, image INTEGER , keyword text); ";

    private static final String TYPE_COMSUMER=" CREATE TABLE Consumer ( id INTEGER PRIMARY KEY AUTOINCREMENT, maintype TEXT NOT NULL," +
            "secondtype TEXT, money INTEGER, date Date , number TEXT , fixdate TEXT , fixdatedetail TEXT ,notify TEXT, detailname TEXT, iswin TEXT" +
            ",auto text,autoId INTEGER, isWinNul TEXT);";

    private static final String TYPE_BANK=" CREATE TABLE BANK ( id INTEGER PRIMARY KEY AUTOINCREMENT, maintype TEXT NOT NULL," +
            "money INTEGER, date Date , fixdate TEXT , fixdatedetail TEXT , detailname TEXT ,auto text,autoId INTEGER);";

    private static final String TABLE_BANK_TYPE =
            "CREATE TABLE BANKTYPE ( id INTEGER PRIMARY KEY AUTOINCREMENT, groupNumber TEXT NOT NULL," +
                    "name TEXT, image INTEGER ) ;";

    private static final String TABLE_INVOICE =
            "CREATE TABLE INVOICE ( id INTEGER PRIMARY KEY AUTOINCREMENT, invNum TEXT NOT NULL," +
                    "cardType TEXT, cardNo TEXT, cardEncrypt TEXT, time DATETIME, amount INTEGER, detail TEXT, sellerName TEXT, invDonatable TEXT , donateMark TEXT, carrier TEXT, maintype TEXT, secondtype TEXT ,heartyteam TEXT,donateTime DATETIME,isWin TEXT,sellerBan TEXT, sellerAddress TEXT, isWinNul TEXT);";

    private static final String TABLE_GOAL =
            "CREATE TABLE Goal ( id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT NOT NULL," +
                    "name TEXT, money INTEGER , timeStatue text, startTime DATETIME ,endTime DATETIME, notify TEXT , notifyStatue TEXT , notifyDate TEXT , noWeekend TEXT ,statue INTEGER) ;";


    private static final String TABLE_ElePeriod =
            "CREATE TABLE ElePeriod ( id INTEGER PRIMARY KEY AUTOINCREMENT, CARNUL TEXT," +
                    "year INTEGER,month INTEGER, download TEXT) ;";


    public static final String TABLE_Currency =
            "CREATE TABLE Currency ( id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT NOT NULL," +
                    "name TEXT,symbol TEXT,money TEXT,time DATETIME) ;";

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
        db.execSQL(TABLE_HERATYTEAM);
        db.execSQL(TABLE_PRICE);
        db.execSQL(TABLE_GOAL);
        db.execSQL(TABLE_ElePeriod);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + "TypeDetail");
        db.execSQL("DROP TABLE IF EXISTS " + "Consumer");
        db.execSQL("DROP TABLE IF EXISTS " + "Bank");
        db.execSQL("DROP TABLE IF EXISTS " + "BANKTYPE");
        db.execSQL("DROP TABLE IF EXISTS " + "INVOICE");
        db.execSQL("DROP TABLE IF EXISTS " + "CARRIER");
        db.execSQL("DROP TABLE IF EXISTS " + "HEARTYTEAM");
        db.execSQL("DROP TABLE IF EXISTS " + "PRICE");
        db.execSQL("DROP TABLE IF EXISTS " + "GOAL");
        db.execSQL("DROP TABLE IF EXISTS "+"ElePeriod");
        onCreate(db);
    }

}

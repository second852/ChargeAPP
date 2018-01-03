package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.HeartyTeamVO;

import java.util.ArrayList;
import java.util.List;


public class HeartyTeamDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="heartyteam";
    private String COL_id="id";
    public HeartyTeamDB(SQLiteDatabase db)
    {
        this.db=db;
    }



    public List<HeartyTeamVO> getAll() {
        String sql = "SELECT * FROM heartyteam order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<HeartyTeamVO> heartyTeamVOS = new ArrayList<>();
        HeartyTeamVO heartyTeamVO;
        while (cursor.moveToNext()) {
            heartyTeamVO=new HeartyTeamVO();
            heartyTeamVO.setId(cursor.getInt(0));
            heartyTeamVO.setName(cursor.getString(1));
            heartyTeamVO.setNumber(cursor.getString(2));
            heartyTeamVOS.add(heartyTeamVO);
        }
        cursor.close();
        return heartyTeamVOS;
    }

    public HeartyTeamVO findById(int id) {
        String[] columns = {
          "id,name,number"
        };
        String selection = "id = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs,
                null, null, null);
        HeartyTeamVO heartyTeamVO=null;
        if (cursor.moveToNext()) {
            heartyTeamVO=new HeartyTeamVO();
            heartyTeamVO.setId(cursor.getInt(0));
            heartyTeamVO.setName(cursor.getString(1));
            heartyTeamVO.setNumber(cursor.getString(2));
        }
        cursor.close();
        return heartyTeamVO;
    }

    public long insert(HeartyTeamVO heartyTeamVO) {
        ContentValues values = new ContentValues();
        values.put("name",heartyTeamVO.getName());
        values.put("number", heartyTeamVO.getName());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(HeartyTeamVO heartyTeamVO) {
        ContentValues values = new ContentValues();
        values.put("name",heartyTeamVO.getName());
        values.put("number", heartyTeamVO.getName());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(heartyTeamVO.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }
    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

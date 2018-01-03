package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class InvoiceDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="INVOICE";
    private String COL_id="id";
    public InvoiceDB(SQLiteDatabase db)
    {
        this.db=db;
    }

    public List<InvoiceVO> getAll() {
        String sql = "SELECT * FROM INVOICE order by time desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=new InvoiceVO();
            invoiceVO.setId(cursor.getInt(0));
            invoiceVO.setInvNum(cursor.getString(1));
            invoiceVO.setCardType(cursor.getString(2));
            invoiceVO.setCardNo(cursor.getString(3));
            invoiceVO.setCardEncrypt(cursor.getString(4));
            invoiceVO.setTime(new Timestamp(cursor.getLong(5)));
            invoiceVO.setAmount(cursor.getString(6));
            invoiceVO.setDetail(cursor.getString(7));
            invoiceVO.setSellerName(cursor.getString(8));
            invoiceVO.setInvDonatable(cursor.getString(9));
            invoiceVO.setDonateMark(cursor.getString(10));
            invoiceVO.setCarrier(cursor.getString(11));
            invoiceVO.setMaintype(cursor.getString(12));
            invoiceVO.setSecondtype(cursor.getString(13));
            invoiceVO.setHeartyteam(cursor.getString(14));
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getCarrierDoAll(String carrrier) {
        String sql = "SELECT * FROM INVOICE  where carrier = '"+carrrier+"' and invDonatable = 'true' and donateMark = 'true' order by time desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=new InvoiceVO();
            invoiceVO.setId(cursor.getInt(0));
            invoiceVO.setInvNum(cursor.getString(1));
            invoiceVO.setCardType(cursor.getString(2));
            invoiceVO.setCardNo(cursor.getString(3));
            invoiceVO.setCardEncrypt(cursor.getString(4));
            invoiceVO.setTime(new Timestamp(cursor.getLong(5)));
            invoiceVO.setAmount(cursor.getString(6));
            invoiceVO.setDetail(cursor.getString(7));
            invoiceVO.setSellerName(cursor.getString(8));
            invoiceVO.setInvDonatable(cursor.getString(9));
            invoiceVO.setDonateMark(cursor.getString(10));
            invoiceVO.setCarrier(cursor.getString(11));
            invoiceVO.setMaintype(cursor.getString(12));
            invoiceVO.setSecondtype(cursor.getString(13));
            invoiceVO.setHeartyteam(cursor.getString(14));
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getCarrierAll(String carrrier) {
        String sql = "SELECT * FROM INVOICE  where carrier = '"+carrrier+"' order by time desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=new InvoiceVO();
            invoiceVO.setId(cursor.getInt(0));
            invoiceVO.setInvNum(cursor.getString(1));
            invoiceVO.setCardType(cursor.getString(2));
            invoiceVO.setCardNo(cursor.getString(3));
            invoiceVO.setCardEncrypt(cursor.getString(4));
            invoiceVO.setTime(new Timestamp(cursor.getLong(5)));
            invoiceVO.setAmount(cursor.getString(6));
            invoiceVO.setDetail(cursor.getString(7));
            invoiceVO.setSellerName(cursor.getString(8));
            invoiceVO.setInvDonatable(cursor.getString(9));
            invoiceVO.setDonateMark(cursor.getString(10));
            invoiceVO.setCarrier(cursor.getString(11));
            invoiceVO.setMaintype(cursor.getString(12));
            invoiceVO.setSecondtype(cursor.getString(13));
            invoiceVO.setHeartyteam(cursor.getString(14));
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getInvoiceBytime(Timestamp start,Timestamp end,String user) {
        String sql = "SELECT * FROM INVOICE  where carrier ='"+user+"' and time between '"+start.getTime()+"' and '"+end.getTime()+"' order by time desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=new InvoiceVO();
            invoiceVO.setId(cursor.getInt(0));
            invoiceVO.setInvNum(cursor.getString(1));
            invoiceVO.setCardType(cursor.getString(2));
            invoiceVO.setCardNo(cursor.getString(3));
            invoiceVO.setCardEncrypt(cursor.getString(4));
            invoiceVO.setTime(new Timestamp(cursor.getLong(5)));
            invoiceVO.setAmount(cursor.getString(6));
            invoiceVO.setDetail(cursor.getString(7));
            invoiceVO.setSellerName(cursor.getString(8));
            invoiceVO.setInvDonatable(cursor.getString(9));
            invoiceVO.setDonateMark(cursor.getString(10));
            invoiceVO.setCarrier(cursor.getString(11));
            invoiceVO.setMaintype(cursor.getString(12));
            invoiceVO.setSecondtype(cursor.getString(13));
            invoiceVO.setHeartyteam(cursor.getString(14));
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public long findIVByMaxDate(String carrier) {
        String sql = "SELECT MAX(time) FROM INVOICE where carrier ='"+carrier+"';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        long time=0;
        if (cursor.moveToNext()) {
            time=cursor.getLong(0);
        }
        cursor.close();
        return time;
    }

    public List<InvoiceVO> findIVTypenull() {
        String sql = "SELECT * FROM INVOICE where maintype is null;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=new InvoiceVO();
            invoiceVO.setId(cursor.getInt(0));
            invoiceVO.setInvNum(cursor.getString(1));
            invoiceVO.setCardType(cursor.getString(2));
            invoiceVO.setCardNo(cursor.getString(3));
            invoiceVO.setCardEncrypt(cursor.getString(4));
            invoiceVO.setTime(new Timestamp(cursor.getLong(5)));
            invoiceVO.setAmount(cursor.getString(6));
            invoiceVO.setDetail(cursor.getString(7));
            invoiceVO.setSellerName(cursor.getString(8));
            invoiceVO.setInvDonatable(cursor.getString(9));
            invoiceVO.setDonateMark(cursor.getString(10));
            invoiceVO.setCarrier(cursor.getString(11));
            invoiceVO.setMaintype(cursor.getString(12));
            invoiceVO.setSecondtype(cursor.getString(13));
            invoiceVO.setHeartyteam(cursor.getString(14));
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }





    public InvoiceVO findById(int id) {
        String[] columns = {
          "id,invNum,cardType,cardNo,cardEncrypt,time,amount,detail,sellerName,invDonatable,donateMark,carrier,maintype,secondtype"
        };
        String selection = "id = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query("Consumer", columns, selection, selectionArgs,
                null, null, null);
        InvoiceVO invoiceVO=new InvoiceVO();
        if (cursor.moveToNext()) {
            invoiceVO.setId(cursor.getInt(0));
            invoiceVO.setInvNum(cursor.getString(1));
            invoiceVO.setCardType(cursor.getString(2));
            invoiceVO.setCardNo(cursor.getString(3));
            invoiceVO.setCardEncrypt(cursor.getString(4));
            invoiceVO.setTime(new Timestamp(cursor.getLong(5)));
            invoiceVO.setAmount(cursor.getString(6));
            invoiceVO.setDetail(cursor.getString(7));
            invoiceVO.setSellerName(cursor.getString(8));
            invoiceVO.setInvDonatable(cursor.getString(9));
            invoiceVO.setDonateMark(cursor.getString(10));
            invoiceVO.setCarrier(cursor.getString(11));
            invoiceVO.setMaintype(cursor.getString(12));
            invoiceVO.setSecondtype(cursor.getString(13));
            invoiceVO.setHeartyteam(cursor.getString(14));
        }
        cursor.close();
        return invoiceVO;
    }

    public long insert(InvoiceVO invoiceVO) {
        ContentValues values = new ContentValues();
        values.put("invNum",invoiceVO.getInvNum());
        values.put("cardType", invoiceVO.getCardType());
        values.put("cardNo",invoiceVO.getCardNo());
        values.put("cardEncrypt",invoiceVO.getCardEncrypt());
        values.put("time",invoiceVO.getTime().getTime());
        values.put("amount",invoiceVO.getAmount());
        values.put("detail",invoiceVO.getDetail());
        values.put("sellerName",invoiceVO.getSellerName());
        values.put("invDonatable",invoiceVO.getInvDonatable());
        values.put("donateMark",invoiceVO.getInvDonatable());
        values.put("carrier",invoiceVO.getCarrier());
        values.put("maintype",invoiceVO.getMaintype());
        values.put("secondtype",invoiceVO.getSecondtype());
        values.put("heartyteam",invoiceVO.getHeartyteam());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(InvoiceVO invoiceVO) {
        ContentValues values = new ContentValues();
        values.put("invNum",invoiceVO.getInvNum());
        values.put("cardType", invoiceVO.getCardType());
        values.put("cardNo",invoiceVO.getCardNo());
        values.put("cardEncrypt",invoiceVO.getCardEncrypt());
        values.put("time",invoiceVO.getTime().getTime());
        values.put("amount",invoiceVO.getAmount());
        values.put("detail",invoiceVO.getDetail());
        values.put("sellerName",invoiceVO.getSellerName());
        values.put("invDonatable",invoiceVO.getInvDonatable());
        values.put("donateMark",invoiceVO.getInvDonatable());
        values.put("carrier",invoiceVO.getCarrier());
        values.put("maintype",invoiceVO.getMaintype());
        values.put("secondtype",invoiceVO.getSecondtype());
        values.put("heartyteam",invoiceVO.getHeartyteam());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(invoiceVO.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }
    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }
    public int deleteById(String  carrier) {
        String whereClause = "carrier" + " = ?;";
        String[] whereArgs = {carrier};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }
    public int deleteBytime(Timestamp  timestamp) {
        String whereClause = "time" + " > ?;";
        String[] whereArgs = {String.valueOf(timestamp.getTime())};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}

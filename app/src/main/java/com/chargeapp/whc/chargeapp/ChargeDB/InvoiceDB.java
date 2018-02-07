package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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
            invoiceVO.setDonateTime(new Timestamp(cursor.getLong(15)));
            invoiceVO.setIswin(cursor.getString(16));
            invoiceVO.setSellerBan(cursor.getString(17));
            invoiceVO.setSellerAddress(cursor.getString(18));
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
            invoiceVO.setDonateTime(new Timestamp(cursor.getLong(15)));
            invoiceVO.setIswin(cursor.getString(16));
            invoiceVO.setSellerBan(cursor.getString(17));
            invoiceVO.setSellerAddress(cursor.getString(18));
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getNoDetailAll() {
        String sql = "SELECT * FROM INVOICE  where detail = '0' order by time desc;";
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
            invoiceVO.setDonateTime(new Timestamp(cursor.getLong(15)));
            invoiceVO.setIswin(cursor.getString(16));
            invoiceVO.setSellerBan(cursor.getString(17));
            invoiceVO.setSellerAddress(cursor.getString(18));
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getCarrierAll(String carrrier) {
        long showtime=getStartTime();
        String sql = "SELECT * FROM INVOICE  where carrier = '"+carrrier+"' and time >='"+showtime+"'order by time desc;";
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
            invoiceVO.setDonateTime(new Timestamp(cursor.getLong(15)));
            invoiceVO.setIswin(cursor.getString(16));
            invoiceVO.setSellerBan(cursor.getString(17));
            invoiceVO.setSellerAddress(cursor.getString(18));
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getWinIn(String carrrier,long startTime,long endTime) {
        String sql = "SELECT * FROM INVOICE  where carrier = '"+carrrier+"' and iswin != 'N' and time >= '"+startTime+"' and time <'"+endTime+"' order by time desc;";
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
            invoiceVO.setDonateTime(new Timestamp(cursor.getLong(15)));
            invoiceVO.setIswin(cursor.getString(16));
            invoiceVO.setSellerBan(cursor.getString(17));
            invoiceVO.setSellerAddress(cursor.getString(18));
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }


    public List<InvoiceVO> getNotSetWin(String carrrier,long startTime,long endTime) {
        String sql = "SELECT * FROM INVOICE  where carrier = '"+carrrier+"' and iswin = '0' and time >= '"+startTime+"' and time <'"+endTime+"' order by time desc;";
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
            invoiceVO.setDonateTime(new Timestamp(cursor.getLong(15)));
            invoiceVO.setIswin(cursor.getString(16));
            invoiceVO.setSellerBan(cursor.getString(17));
            invoiceVO.setSellerAddress(cursor.getString(18));
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public long getStartTime()
    {
        Calendar cal=Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        cal.set(year,0,25);
        long one25=cal.getTimeInMillis();
        cal.set(year,2,25);
        long three25=cal.getTimeInMillis();
        cal.set(year,4,25);
        long five25=cal.getTimeInMillis();
        cal.set(year,6,25);
        long seven25=cal.getTimeInMillis();
        cal.set(year,8,25);
        long night25=cal.getTimeInMillis();
        cal.set(year,10,25);
        long ele25=cal.getTimeInMillis();
        long showtime;
        long now=System.currentTimeMillis();
        if(now>one25&&now<three25)
        {
            showtime=Timestamp.valueOf(String.valueOf(year)+"-01-01 00:00:00").getTime();
        }
        else if(now>three25&&now<five25)
        {
            showtime=Timestamp.valueOf(String.valueOf(year)+"-03-01 00:00:00").getTime();
        }
        else if(now>five25&&now<seven25)
        {
            showtime=Timestamp.valueOf(String.valueOf(year)+"-05-01 00:00:00").getTime();
        }
        else if(now>seven25&&now<night25)
        {
            showtime=Timestamp.valueOf(String.valueOf(year)+"-07-01 00:00:00").getTime();
        }
        else if(now>night25&&now<ele25)
        {
            showtime=Timestamp.valueOf(String.valueOf(year)+"-09-01 00:00:00").getTime();
        }
        else
        {
            Calendar nc=Calendar.getInstance();
            if(nc.get(Calendar.MONTH)==0)
            {
                showtime=Timestamp.valueOf(String.valueOf(year-1)+"-11-01 00:00:00").getTime();
            }else{
                showtime=Timestamp.valueOf(String.valueOf(year)+"-11-01 00:00:00").getTime();
            }
        }
        return showtime;
    }


    public List<InvoiceVO> getisDonated(String carrrier) {
        long showtime=getStartTime();
        String sql = "SELECT * FROM INVOICE  where carrier = '"+carrrier+"' and donateMark='false' and invDonatable = 'false' and time >'"+showtime+"'order by donateTime desc;";
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
            invoiceVO.setDonateTime(new Timestamp(cursor.getLong(15)));
            invoiceVO.setIswin(cursor.getString(16));
            invoiceVO.setSellerBan(cursor.getString(17));
            invoiceVO.setSellerAddress(cursor.getString(18));
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getInvoiceBytime(Timestamp start,Timestamp end) {
        String sql = "SELECT * FROM INVOICE  where time between '"+start.getTime()+"' and '"+end.getTime()+"' order by time desc;";
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
            invoiceVO.setDonateTime(new Timestamp(cursor.getLong(15)));
            invoiceVO.setIswin(cursor.getString(16));
            invoiceVO.setSellerBan(cursor.getString(17));
            invoiceVO.setSellerAddress(cursor.getString(18));
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
            invoiceVO.setDonateTime(new Timestamp(cursor.getLong(15)));
            invoiceVO.setIswin(cursor.getString(16));
            invoiceVO.setSellerBan(cursor.getString(17));
            invoiceVO.setSellerAddress(cursor.getString(18));
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
        String sql = "SELECT * FROM INVOICE where maintype = '0';";
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
            invoiceVO.setDonateTime(new Timestamp(cursor.getLong(15)));
            invoiceVO.setIswin(cursor.getString(16));
            invoiceVO.setSellerBan(cursor.getString(17));
            invoiceVO.setSellerAddress(cursor.getString(18));
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
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
        values.put("donateTime",invoiceVO.getDonateTime().getTime());
        values.put("iswin",invoiceVO.getIswin());
        values.put("sellerBan",invoiceVO.getSellerBan());
        values.put("sellerAddress",invoiceVO.getSellerAddress());
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
        values.put("donateTime",invoiceVO.getDonateTime().getTime());
        values.put("iswin",invoiceVO.getIswin());
        values.put("sellerBan",invoiceVO.getSellerBan());
        values.put("sellerAddress",invoiceVO.getSellerAddress());
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
    public void DeleteError() {
        String sql = "Delete from INVOICE where maintype = '0' and secondtype is null;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
    }

}

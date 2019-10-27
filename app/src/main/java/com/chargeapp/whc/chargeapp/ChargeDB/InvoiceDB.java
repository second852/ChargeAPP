package com.chargeapp.whc.chargeapp.ChargeDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class InvoiceDB {
    private SQLiteOpenHelper db;
    private String TABLE_NAME="INVOICE";
    private String COL_id="id";
    public InvoiceDB(SQLiteOpenHelper db)
    {
        this.db=db;
    }


    private InvoiceVO configInvoiceVO(Cursor cursor)
    {
        InvoiceVO invoiceVO=new InvoiceVO();
        invoiceVO.setId(cursor.getInt(0));
        invoiceVO.setInvNum(cursor.getString(1));
        invoiceVO.setCardType(cursor.getString(2));
        invoiceVO.setCardNo(cursor.getString(3));
        invoiceVO.setCardEncrypt(cursor.getString(4));
        invoiceVO.setTime(new Timestamp(cursor.getLong(5)));
        invoiceVO.setAmount(cursor.getInt(6));
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
        invoiceVO.setIsWinNul(cursor.getString(19));
        invoiceVO.setCurrency(cursor.getString(20));
        invoiceVO.setRealAmount(cursor.getString(21));
        return invoiceVO;
    }

    private ContentValues configContentValues(InvoiceVO invoiceVO)
    {
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
        values.put("donateMark",invoiceVO.getDonateMark());
        values.put("carrier",invoiceVO.getCarrier());
        values.put("maintype",invoiceVO.getMaintype());
        values.put("secondtype",invoiceVO.getSecondtype());
        values.put("heartyteam",invoiceVO.getHeartyteam());
        values.put("donateTime",invoiceVO.getDonateTime().getTime());
        values.put("iswin",invoiceVO.getIswin());
        values.put("sellerBan",invoiceVO.getSellerBan());
        values.put("sellerAddress",invoiceVO.getSellerAddress());
        values.put("isWinNul",invoiceVO.getIsWinNul());
        values.put("currency",invoiceVO.getCurrency());
        values.put("realAmount",invoiceVO.getRealAmount());
        return values;
    }




    public List<InvoiceVO> getAll() {
        String sql = "SELECT * FROM INVOICE order by time desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getRealAmountIsNull() {
        String sql = "SELECT * FROM INVOICE where realAmount isnull or trim(realAmount) = '';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getCarrierDoAll(String carrrier) {
        long showtime=getStartTime();
        String sql = "SELECT * FROM INVOICE  where carrier = '"+carrrier+"' and invDonatable = 'true' and donateMark = '0'  and time >='"+showtime+"' order by time desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getErrorDonateMark(String carrrier) {
        String sql = "SELECT * FROM INVOICE  where carrier = '"+carrrier+"'and donateMark = 'true' or donateMark = 'false' order by time ;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }


    public List<InvoiceVO> getNoDetailAll() {
        String sql = "SELECT * FROM INVOICE  where detail = '0' order by time desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getCarrierAll(String carrrier) {
        long showtime=getStartTime();
        String sql = "SELECT * FROM INVOICE  where carrier = '"+carrrier+"' and time >='"+showtime+"'order by time desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }





    public List<InvoiceVO> getWinIn(long startTime,long endTime) {
        String sql = "SELECT * FROM INVOICE  where iswin != 'N' and donateMark = '0' and time >= '"+startTime+"' and time <'"+endTime+"' order by time desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }


    public List<InvoiceVO> getNotSetWin(String carrrier,long startTime,long endTime) {
        String sql = "SELECT * FROM INVOICE  where carrier = '"+carrrier+"' and iswin = '0' and heartyteam is null and time between '"+startTime+"' and '"+endTime+"' order by time desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }
    public long getMinTimeDonate(String carrrier)
    {
        long minTime=0;
        String sql = "SELECT min(time) FROM INVOICE  where carrier = '"+carrrier+"' and donateMark = '1' ;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        if(cursor.moveToNext())
        {
            minTime=cursor.getLong(0);
        }
        cursor.close();
        return minTime;
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

        String sql = "SELECT * FROM INVOICE  where carrier = '"+carrrier+"' and donateMark='1' and invDonatable = 'false' order by donateTime desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public long getMinTime() {
        String sql = "SELECT min(time) FROM INVOICE ;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        long minTime=0;
        if (cursor.moveToNext()) {
            minTime=cursor.getLong(0);
        }
        cursor.close();
        return minTime;
    }


    public List<InvoiceVO> findBySearchKey(String searchKey) {
        String sql = "SELECT * FROM INVOICE  where maintype like ? or secondtype like ? or detail like ? order by time desc;";
        String[] args = {"%"+searchKey+"%","%"+searchKey+"%","%"+searchKey+"%"};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> findBySearchKeyAndTime(String searchKey,long start,long end) {
        String sql = "SELECT * FROM INVOICE  where maintype like ? or secondtype like ? or detail like ? and time between ? and ? order by time desc;";
        String[] args = {"%"+searchKey+"%","%"+searchKey+"%","%"+searchKey+"%",String.valueOf(start),String.valueOf(end)};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getInvoiceBytime(Timestamp start,Timestamp end) {
        String sql = "SELECT * FROM INVOICE  where time between '"+start.getTime()+"' and '"+end.getTime()+"' order by time desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public HashMap<String,Double> getInvoiceByTimeHashMap(long start, long end) {
        String sql = "SELECT maintype,realAmount,currency FROM INVOICE  where time between '"+start+"' and '"+end+"' order by realAmount desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        HashMap<String,Double> hashMap=new HashMap<>();
        String main;
        Double money,total=0.0;
        CurrencyDB currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);
        CurrencyVO currencyVO;
        while (cursor.moveToNext()) {
            main=cursor.getString(0);
            currencyVO=currencyDB.getBytimeAndType(start,end,cursor.getString(2));
            money=Double.valueOf(cursor.getString(1))*Double.valueOf(currencyVO.getMoney());
            if(hashMap.get(main)==null)
            {
                hashMap.put(main,money);
            }else{
                hashMap.put(main,hashMap.get(main)+money);
            }
            total=total+money;
        }
        hashMap.put("total",total);
        cursor.close();
        return hashMap;
    }

    public List<InvoiceVO> getInvoiceBytimeMainType(Timestamp start,Timestamp end,String mainType) {
        String sql = "SELECT * FROM INVOICE  where time between '"+start.getTime()+"' and '"+end.getTime()+"' and maintype = '"+mainType+"' order by time desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getInvoiceMainType(String mainType) {
        String sql = "SELECT * FROM INVOICE  where maintype = '"+mainType+"';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getInvoiceSecondType(String mainType,String second) {
        String sql = "SELECT * FROM INVOICE  where maintype = '"+mainType+"' and secondtype = '"+second+"';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getInvoiceBytimeSecondType(Timestamp start,Timestamp end,String secondtype) {
        String sql = "SELECT * FROM INVOICE  where time between '"+start.getTime()+"' and '"+end.getTime()+"' and secondtype = '"+secondtype+"' order by time desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getInvoiceBytimeSecondType(Timestamp start,Timestamp end,String secondtype,String carrier) {
        String sql = "SELECT * FROM INVOICE  where time between '"+start.getTime()+"' and '"+end.getTime()+"' and secondtype = '"+secondtype+"' and carrier = '"+carrier+"' order by time desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }


    public InvoiceVO findOldInvoiceVO(InvoiceVO oldInvoiceVO) {
        String sql = "SELECT * FROM INVOICE  where maintype ='"+oldInvoiceVO.getMaintype()+"' " +
                "and  secondtype = '"+oldInvoiceVO.getSecondtype()+"' and " +
                "time = '"+oldInvoiceVO.getTime().getTime()+"' and realAmount = '"+oldInvoiceVO.getRealAmount()+"';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        InvoiceVO invoiceVO=null;
        if (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
        }
        cursor.close();
        return invoiceVO;
    }

    public InvoiceVO findOldByNulAmount(String nul,String amount) {
        String sql = "SELECT * FROM INVOICE  where "+
                "invNum = '"+nul+"' and realAmount = '"+amount+"';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        InvoiceVO invoiceVO=null;
        if (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
        }
        cursor.close();
        return invoiceVO;
    }


    public HashMap<String,Double> getInvoiceByTimeMaxType(long start,long end) {
        String sql = "SELECT maintype,realAmount,currency FROM INVOICE  where time between '"+start+"' and '"+end+"';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        HashMap<String,Double> hashMap=new HashMap<>();
        CurrencyDB currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);
        CurrencyVO currencyVO;
        String mainType;
        Double realAmount,total=0.0;
        while (cursor.moveToNext()) {
          currencyVO=currencyDB.getBytimeAndType(start,end,cursor.getString(2));
          mainType=cursor.getString(0);
          realAmount=Double.valueOf(cursor.getString(1))*Double.valueOf(currencyVO.getMoney());
          if(hashMap.get(mainType)==null)
          {
              hashMap.put(mainType,realAmount);
          }else {
              hashMap.put(mainType, hashMap.get(mainType)+realAmount);
          }
          total=total+realAmount;
        }
        hashMap.put("total",total);
        cursor.close();
        return hashMap;
    }

    public List<InvoiceVO> getInvoiceBytimeMainType(Timestamp start,Timestamp end,String mainType,String user) {
        String sql = "SELECT * FROM INVOICE  where time between '"+start.getTime()+"' and '"+end.getTime()+"' and maintype = '"+mainType+"' and carrier = '"+user+"' order by time desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }


    public List<InvoiceVO> checkInvoiceRepeat(Calendar start,Calendar end,String nul) {
        String sql = "SELECT * FROM INVOICE  where time between '"+start.getTimeInMillis()+"' and '"+end.getTimeInMillis()+"' and invNum = '"+nul+"' order by time desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public List<InvoiceVO> getInvoiceBytime(Timestamp start,Timestamp end,String user) {
        String sql = "SELECT * FROM INVOICE  where carrier ='"+user+"' and time between '"+start.getTime()+"' and '"+end.getTime()+"' order by time desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public long findIVByMaxDate(String carrier) {
        String sql = "SELECT MAX(time) FROM INVOICE where carrier ='"+carrier+"';";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
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
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<InvoiceVO> invoiceVOSList = new ArrayList<>();
        InvoiceVO invoiceVO;
        while (cursor.moveToNext()) {
            invoiceVO=configInvoiceVO(cursor);
            invoiceVOSList.add(invoiceVO);
        }
        cursor.close();
        return invoiceVOSList;
    }

    public long insertHid(InvoiceVO invoiceVO) {
        ContentValues values = configContentValues(invoiceVO);
        return db.getWritableDatabase().insert(TABLE_NAME, null, values);
    }





    public long insert(InvoiceVO invoiceVO) {
        ContentValues values = configContentValues(invoiceVO);
        return db.getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public int update(InvoiceVO invoiceVO) {
        ContentValues values = configContentValues(invoiceVO);
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(invoiceVO.getId())};
        return db.getWritableDatabase().update(TABLE_NAME, values, whereClause, whereArgs);
    }



    public int updateCarrier(CarrierVO oldCarrier,CarrierVO newCarrier) {
        ContentValues values = new ContentValues();
        values.put("cardEncrypt",newCarrier.getPassword());
        values.put("carrier",newCarrier.getCarNul());
        String whereClause = "carrier = ?;";
        String[] whereArgs = {oldCarrier.getCarNul()};
        return db.getWritableDatabase().update(TABLE_NAME, values, whereClause, whereArgs);
    }


    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.getWritableDatabase().delete(TABLE_NAME, whereClause, whereArgs);
    }
    public int deleteById(String  carrier) {
        String whereClause = "carrier" + " = ?;";
        String[] whereArgs = {carrier};
        return db.getWritableDatabase().delete(TABLE_NAME, whereClause, whereArgs);
    }
    public int deleteBytime(Timestamp  timestamp) {
        String whereClause = "time" + " > ?;";
        String[] whereArgs = {String.valueOf(timestamp.getTime())};
        return db.getWritableDatabase().delete(TABLE_NAME, whereClause, whereArgs);
    }
    public void DeleteError() {
        String sql = "Delete from INVOICE where secondtype = '0' ;";
        String[] args = {};
        Cursor cursor = db.getWritableDatabase().rawQuery(sql, args);
        db.getWritableDatabase().execSQL(sql);
    }

}

package com.chargeapp.whc.chargeapp.Control;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapText;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyFromDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Control.EleInvoice.EleSetCarrier;
import com.chargeapp.whc.chargeapp.Control.Goal.GoalListAll;
import com.chargeapp.whc.chargeapp.Control.HomePage.HomePage;
import com.chargeapp.whc.chargeapp.Control.HomePage.HomePagetList;
import com.chargeapp.whc.chargeapp.Control.Insert.InsertActivity;
import com.chargeapp.whc.chargeapp.Control.Insert.InsertSpend;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyConsumeShow;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyMain;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyMoneyList;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyTotal;
import com.chargeapp.whc.chargeapp.Control.Search.SearchMain;
import com.chargeapp.whc.chargeapp.Control.SelectList.SelectListModelActivity;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectActivity;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectDetCircle;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectDetList;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectListBarIncome;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectListPieIncome;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectOtherCircle;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectShowCircleDe;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectShowCircleDeList;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingListFix;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingListFixCon;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingListFixIon;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingListFixProperty;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingListType;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingMain;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateIncome;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateInvoice;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateSpend;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.Model.PropertyFromVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.ui.ScanUpdateSpend;
import com.github.mikephil.charting.components.Description;




import org.jsoup.internal.StringUtil;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.WINDOW_SERVICE;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_CALCULATOR;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_CALENDAR_CHECK_O;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_ID_CARD_O;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_MONEY;

/**
 * Created by Wang on 2017/11/19.
 */

public class Common {

    public static int length = 0;
    public static Description description = new Description();
    public static boolean showfirstgrid = false;
    public static boolean showsecondgrid = false;


    public static SimpleDateFormat sOne = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
    public static SimpleDateFormat sTwo = new SimpleDateFormat("yyyy/MM/dd");
    public static SimpleDateFormat sThree = new SimpleDateFormat("yyyy 年 MM 月");
    public static SimpleDateFormat sFour = new SimpleDateFormat("yyyy 年");
    public static SimpleDateFormat sFive = new SimpleDateFormat("yyyyMMdd-HHmm");
    public static SimpleDateFormat sSix = new SimpleDateFormat("yyyyMMdd");

    public static SimpleDateFormat sDay = new SimpleDateFormat("MM/dd");
    public static SimpleDateFormat sHour = new SimpleDateFormat("hh");
    public static SimpleDateFormat sYear = new SimpleDateFormat("yyy 年 MM 月");

    public static String keyboardArray[]={"倒退","7","8","9","+","歸零","4","5","6","-","確定","1","2","3","x","返回",".","0","=","÷"};

    public final static String propertyCurrency="propertyCurrency";
    public final static String choiceCurrency="choiceCurrency";
    public final static String insertCurrency="insertCurrency";

    public final static String fragment="fragment";

    public final static String settingListFixPropertyString="settingListFixProperty";


    public final static String propertyID="propertyID";
    public final static String propertyMain="propertyMain";
    public final static String PropertyMoneyListString="PropertyMoneyList";
    public final static String PropertyTotalString="PropertyTotal";
    public final static String propertyMainType="propertyMainType";
    public final static String propertySecondType="propertySecondType";
    public final static String propertyFromVoId="propertyFromVoId";
    public final static String propertyFragment="propertyFragment";
    public final static String propertyInsertMoneyString="propertyInsertMoneyString";
    public final static String propertyInsertString="propertyInsertString";
    public final static String propertyConsumeShowString="propertyConsumeShowString";
    public final static String searchMainString="searchMainString";


    public final static String multiTrackerActivityWork="multiTrackerActivityWork";
    public final static String scanFragment="scanFragment";
    public final static String scanUpdateSpend="scanUpdateSpend";
    public final static String scanListFragment="scanListFragment";
    public final static String scanByOnline="scanByOnline";



    public static NumberFormat nf = NumberFormat.getNumberInstance();
    public static Vector<CarrierVO> lostCarrier;
    public static Screen screenSize;


    public enum Screen {
        xLarge,
        large,
        normal
    }

    public static String doubleRemoveZero(double d) {
        int a = (int) d;
        if (a == d) {
            return Common.nf.format(d);
        } else {
            return Common.nf.format(a);
        }
    }


    public static String setMainInvoiceTittle(InvoiceVO invoiceVO)
    {
        StringBuilder sbTitle=new StringBuilder();
        sbTitle.append(Common.sDay.format(new Date(invoiceVO.getTime().getTime())));
        //無法分類顯示其他
        if(invoiceVO.getMaintype().trim().equals("0"))
        {
            sbTitle.append("未知");
        }else if(invoiceVO.getMaintype().trim().equals("O"))
        {
            sbTitle.append("其他");
        }else{
            sbTitle.append(" " +invoiceVO.getMaintype()+" ");
        }
        //設定幣別 null 新台幣
        sbTitle.append(Common.getCurrency(invoiceVO.getCurrency()));
        sbTitle.append(" "+invoiceVO.getRealAmount());
        return  sbTitle.toString();
    }


    public static String setSecInvoiceTittle(InvoiceVO invoiceVO)
    {
        StringBuilder sbTitle=new StringBuilder();
        sbTitle.append(Common.sDay.format(new Date(invoiceVO.getTime().getTime())));
        //無法分類顯示其他
        if(invoiceVO.getSecondtype().trim().equals("0"))
        {
            sbTitle.append("未知");
        }else if(invoiceVO.getSecondtype().trim().equals("O"))
        {
            sbTitle.append("其他");
        }else{
            sbTitle.append(" " +invoiceVO.getSecondtype()+" ");
        }
        //設定幣別 null 新台幣
        sbTitle.append(Common.getCurrency(invoiceVO.getSecondtype()));
        sbTitle.append(" "+invoiceVO.getRealAmount());
        return  sbTitle.toString();
    }



    public static String setSecInvoiceTittleYear(InvoiceVO invoiceVO)
    {
        StringBuilder sbTitle=new StringBuilder();
        sbTitle.append(Common.sTwo.format(new Date(invoiceVO.getTime().getTime())));
        //無法分類顯示其他
        if(invoiceVO.getSecondtype().trim().equals("0"))
        {
            sbTitle.append("未知");
        }else if(invoiceVO.getSecondtype().trim().equals("O"))
        {
            sbTitle.append("其他");
        }else{
            sbTitle.append(" " +invoiceVO.getSecondtype()+" ");
        }
        //設定幣別 null 新台幣
        sbTitle.append(Common.getCurrency(invoiceVO.getSecondtype()));
        sbTitle.append(" "+invoiceVO.getRealAmount());
        return  sbTitle.toString();
    }




    public static String setMainConsumerTittle(ConsumeVO consumeVO)
    {
        StringBuilder sbTitle=new StringBuilder();
        sbTitle.append(Common.sDay.format(consumeVO.getDate()));
        sbTitle.append(" " +consumeVO.getMaintype()+" ");
        //設定幣別 null 新台幣
        sbTitle.append(Common.getCurrency(consumeVO.getCurrency()));
        if(StringUtil.isBlank(consumeVO.getRealMoney()))
        {
            sbTitle.append(" "+consumeVO.getMoney());
        }else {
            sbTitle.append(" "+consumeVO.getRealMoney());
        }
        return  sbTitle.toString();
    }

    public static String setSecConsumerTittlesTwo(ConsumeVO consumeVO)
    {
        StringBuilder sbTitle=new StringBuilder();
        sbTitle.append(Common.sTwo.format(consumeVO.getDate()));
        if(consumeVO.getSecondType().trim().equals("0"))
        {
            sbTitle.append("未知");
        }else if(consumeVO.getSecondType().trim().equals("O"))
        {
            sbTitle.append("其他");
        }else{
            sbTitle.append(" " +consumeVO.getSecondType()+" ");
        }
        //設定幣別 null 新台幣
        sbTitle.append(Common.getCurrency(consumeVO.getCurrency()));
        if(StringUtil.isBlank(consumeVO.getRealMoney()))
        {
            consumeVO.setRealMoney(String.valueOf(consumeVO.getMoney()));
        }
        sbTitle.append(" "+consumeVO.getRealMoney());
        return  sbTitle.toString();
    }

    public static String setSecConsumerTittlesDay(ConsumeVO consumeVO)
    {
        StringBuilder sbTitle=new StringBuilder();
        sbTitle.append(Common.sDay.format(consumeVO.getDate()));
        if(consumeVO.getSecondType().trim().equals("0"))
        {
            sbTitle.append("未知");
        }else if(consumeVO.getSecondType().trim().equals("O"))
        {
            sbTitle.append("其他");
        }else{
            sbTitle.append(" " +consumeVO.getSecondType()+" ");
        }


        //設定幣別 null 新台幣
        sbTitle.append(Common.getCurrency(consumeVO.getCurrency()));
        if(StringUtil.isBlank(consumeVO.getRealMoney()))
        {
            consumeVO.setRealMoney(String.valueOf(consumeVO.getMoney()));
        }
        sbTitle.append(" "+consumeVO.getRealMoney());
        return  sbTitle.toString();
    }




    public static String setSecConsumerTittlesDayYear(ConsumeVO consumeVO)
    {
        StringBuilder sbTitle=new StringBuilder();
        sbTitle.append(Common.sTwo.format(consumeVO.getDate()));
        if(consumeVO.getSecondType().trim().equals("0"))
        {
            sbTitle.append("未知");
        }else if(consumeVO.getSecondType().trim().equals("O"))
        {
            sbTitle.append("其他");
        }else{
            sbTitle.append(" " +consumeVO.getSecondType()+" ");
        }


        //設定幣別 null 新台幣
        sbTitle.append(Common.getCurrency(consumeVO.getCurrency()));
        if(StringUtil.isBlank(consumeVO.getRealMoney()))
        {
            consumeVO.setRealMoney(String.valueOf(consumeVO.getMoney()));
        }
        sbTitle.append(" "+consumeVO.getRealMoney());
        return  sbTitle.toString();
    }






    public static String setBankTittlesDay(BankVO bankVO)
    {
        StringBuilder sbTitle=new StringBuilder();
        sbTitle.append(Common.sDay.format(bankVO.getDate()));
        sbTitle.append(" " +bankVO.getMaintype()+" ");
        //設定幣別 null 新台幣
        sbTitle.append(Common.getCurrency(bankVO.getCurrency()));
        if(StringUtil.isBlank(bankVO.getRealMoney()))
        {
            sbTitle.append(" "+bankVO.getMoney());
        }else{
            sbTitle.append(" "+bankVO.getRealMoney());
        }

        return  sbTitle.toString();
    }





    public static String setBankTittlesDayYear(BankVO bankVO)
    {
        StringBuilder sbTitle=new StringBuilder();
        sbTitle.append(Common.sTwo.format(bankVO.getDate()));
        sbTitle.append(" " +bankVO.getMaintype()+" ");
        //設定幣別 null 新台幣
        sbTitle.append(Common.getCurrency(bankVO.getCurrency()));
        if(StringUtil.isBlank(bankVO.getRealMoney()))
        {
            sbTitle.append(" "+bankVO.getMoney());
        }else{
            sbTitle.append(" "+bankVO.getRealMoney());
        }

        return  sbTitle.toString();
    }


    public static String setBankTittlesTwo(BankVO bankVO)
    {
        StringBuilder sbTitle=new StringBuilder();
        sbTitle.append(Common.sTwo.format(bankVO.getDate()));
        sbTitle.append(" " +bankVO.getMaintype()+" ");
        //設定幣別 null 新台幣
        sbTitle.append(Common.getCurrency(bankVO.getCurrency()));
        if(StringUtil.isBlank(bankVO.getRealMoney()))
        {
            sbTitle.append(" "+bankVO.getMoney());
        }else{
            sbTitle.append(" "+bankVO.getRealMoney());
        }

        return  sbTitle.toString();
    }




    public static int identify(byte[] bytes) {
        String[] charsetsToBeTested = {"UTF-8", "big5"};
        boolean isRight;
        int i = 0;
        for (String c : charsetsToBeTested) {
            try {
                Charset charset = Charset.forName(c);
                CharsetDecoder decoder = charset.newDecoder();
                decoder.reset();
                decoder.decode(ByteBuffer.wrap(bytes));
                isRight = true;
            } catch (CharacterCodingException e) {
                isRight = false;
            }
            if (isRight) {
                i++;
            }
        }
        return i;
    }

    public static void setScreen(Screen screen, Activity activity) {
        if (screen == null) {
            DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            if (dpWidth > 650) {
                Common.screenSize = Common.Screen.xLarge;
            } else if (dpWidth > 470) {
                Common.screenSize = Common.Screen.large;
            } else {
                Common.screenSize = Common.Screen.normal;
            }
        }
    }

    public static List<String> code;
    public static void createCurrencyPopMenu(PopupMenu popupMenu,Activity context) {
        android.view.Menu menu_more = popupMenu.getMenu();
        ChargeAPPDB chargeAPPDB=new ChargeAPPDB(context);
        CurrencyDB currencyDB=new CurrencyDB(chargeAPPDB);
        HashMap<String,List<String>> types=currencyDB.getAllType();
        code=types.get("code");
        List<String> show=types.get("show");
        show.add(0,"新台幣");
        show.add("離開");
        int size = show.size();
        for (int i = 0; i < size; i++) {
            menu_more.add(android.view.Menu.NONE, android.view.Menu.FIRST + i, i, show.get(i));
        }

    }

    public static String CurrencyResult(Double total, CurrencyVO currencyVO)
    {
        total=Double.valueOf(total/Double.valueOf(currencyVO.getMoney()));
        return  getCurrency(currencyVO.getType())+" "+doubleRemoveZero(total);
    }

    public static String goalCurrencyResult(Double total,String currency)
    {
        return  getCurrency(currency)+" "+doubleRemoveZero(total);
    }


    public static HashMap<String,String> Currency()
    {
        HashMap<String,String> Currency=new HashMap<String,String>();
        Currency.put("TWD","NT$");//新台幣
        Currency.put("USD","US$");//美元
        Currency.put("HKD","HK$");//港幣
        Currency.put("GBP","£");//英鎊
        Currency.put("AUD","AUD$");//澳幣
        Currency.put("CAD","C$");//加拿大幣
        Currency.put("SGD","S$");//新加坡幣
        Currency.put("CHF","CHF");//瑞士法郎
        Currency.put("JPY","¥");//日圓
        Currency.put("ZAR","R");//南非幣
        Currency.put("SEK","kr");//瑞典幣
        Currency.put("NZD","NZD$");//紐元
        Currency.put("THB","฿");//泰幣
        Currency.put("PHP","₱");//菲國比索
        Currency.put("IDR","Rp");//印尼幣
        Currency.put("EUR","€");//歐元
        Currency.put("KRW","₩");//韓元
        Currency.put("VND","₫");//越南盾
        Currency.put("MYR","RM");//馬來幣
        Currency.put("CNY","CNY¥");//人民幣
        return  Currency;
    }


    public static HashMap<String,String> basicCurrency()
    {
        HashMap<String,String> Currency=new HashMap<String,String>();
        Currency.put("TWD","1.00");//新台幣
        Currency.put("USD","31.22");//美元
        Currency.put("HKD","3.991");//港幣
        Currency.put("GBP","41.06");//英鎊
        Currency.put("AUD","22.28");//澳幣
        Currency.put("CAD","23.67");//加拿大幣
        Currency.put("SGD","22.75");//新加坡幣
        Currency.put("CHF","31.44");//瑞士法郎
        Currency.put("JPY","0.2785");//日圓
        Currency.put("ZAR","2.29");//南非幣
        Currency.put("SEK","3.55");//瑞典幣
        Currency.put("NZD","21.59");//紐元
        Currency.put("THB","1.0052");//泰幣
        Currency.put("PHP","0.6476");//菲國比索
        Currency.put("IDR","0.00248");//印尼幣
        Currency.put("EUR","35.99");//歐元
        Currency.put("KRW","0.02944");//韓元
        Currency.put("VND","0.00145");//越南盾
        Currency.put("MYR","7.899");//馬來幣
        Currency.put("CNY","4.522");//人民幣
        return  Currency;
    }


    public static HashMap<String,String> showCurrency()
    {
        HashMap<String,String> Currency=new HashMap<String,String>();
        Currency.put("TWD","新台幣");//新台幣
        Currency.put("USD","美元");//美元
        Currency.put("HKD","港幣");//港幣
        Currency.put("GBP","英鎊");//英鎊
        Currency.put("AUD","澳幣");//澳幣
        Currency.put("CAD","加拿大幣");//加拿大幣
        Currency.put("SGD","新加坡幣");//新加坡幣
        Currency.put("CHF","瑞士法郎 ");//瑞士法郎
        Currency.put("JPY","日圓");//日圓
        Currency.put("ZAR","南非幣");//南非幣
        Currency.put("SEK","瑞典幣");//瑞典幣
        Currency.put("NZD","紐元");//紐元
        Currency.put("THB","泰幣");//泰幣
        Currency.put("PHP","菲國比索");//菲國比索
        Currency.put("IDR","印尼幣");//印尼幣
        Currency.put("EUR","歐元");//歐元
        Currency.put("KRW","韓元");//韓元
        Currency.put("VND","越南盾");//越南盾
        Currency.put("MYR","馬來幣");//馬來幣
        Currency.put("CNY","人民幣");//人民幣
        return  Currency;
    }


    public static List<String> getALLCurrencyKey()
    {
        List<String> name=new ArrayList<>();
        for(String s:showCurrency().keySet())
        {
            name.add(s);
        }
        name.remove("TWD");
        name.set(0,"TWD");
        return  name;
    }


    public static List<String> getALLCurrencyValue()
    {
        List<String> name=new ArrayList<>();
        for(String s:showCurrency().values())
        {
            name.add(s);
        }
        name.remove("新台幣");
        name.set(0,"新台幣");
        return  name;
    }

    public static String getCurrency(String dollor)
    {
        String currency= Currency().get(dollor);
        if(StringUtil.isBlank(currency))
        {
            return "NT$";
        }
        return currency;
    }




    public static void setChargeDB(Context activity) {
        if (MainActivity.chargeAPPDB == null) {
            MainActivity.chargeAPPDB = new ChargeAPPDB(activity);
        }
    }

    public static void insertNewTableCol() {
        tableExist("Currency", ChargeAPPDB.TABLE_Currency);
        tableExist("Property", ChargeAPPDB.TABLE_Property);
        tableExist("PropertyFrom", ChargeAPPDB.TABLE_PropertyFrom);
        colExist("Consumer","rdNumber","text");
        colExist("Consumer","currency","text");
        colExist("Consumer","realMoney","text");
        colExist("Consumer","fkKey","text");
        colExist("Consumer","buyerBan","text");
        colExist("Consumer","sellerName","text");
        colExist("Consumer","sellerAddress","text");
        colExist("INVOICE","currency","text");
        colExist("INVOICE","realAmount","text");
        colExist("BANK","currency","text");
        colExist("BANK","realMoney","text");
        colExist("BANK","fkKey","text");
        colExist("Goal","currency","text");
        colExist("Goal","realMoney","text");
        colExist("PRICE","isCheck","text");
        colExist("PRICE","priceNotify","Integer");
        ConsumeDB consumeDB=new ConsumeDB(MainActivity.chargeAPPDB);

        //改用UUID FK consume
        List<ConsumeVO> cFatherList = consumeDB.getFixdateAndfkKeyIsNull();
        for(ConsumeVO father:cFatherList)
        {
            Log.d("XXXXXX", father.getFixDate());
            Log.d("XXXXXX",father.getFkKey()+" ");
            father.setFkKey(UUID.randomUUID().toString());
            consumeDB.update(father);
            List<ConsumeVO> sonList=consumeDB.getAutoCreate(father.getId());
            for (ConsumeVO son:sonList)
            {
                son.setFkKey(father.getFkKey());
                consumeDB.update(son);
            }
        }

        //改用double
        List<ConsumeVO> consumeVOS=consumeDB.getRealMoneyIsNull();
        for(ConsumeVO consumeVO:consumeVOS)
        {
            consumeVO.setRealMoney(String.valueOf(consumeVO.getMoney()));
            consumeDB.update(consumeVO);
        }

        BankDB bankDB=new BankDB(MainActivity.chargeAPPDB);
        //改用UUID FK bank
        List<BankVO> bFatherList=bankDB.getFixDateAndfkKeyIsNull();
        for(BankVO father:bFatherList)
        {
            father.setFkKey(UUID.randomUUID().toString());
            bankDB.update(father);
            List<BankVO> sonList =bankDB.getAutoSetting(father.getId());
            for(BankVO son:sonList)
            {
                son.setFkKey(father.getFkKey());
                bankDB.update(son);
            }
        }


        //改用double
        List<BankVO> bankVOS=bankDB.getrealMoneyIsNullAll();
        for(BankVO bankVO:bankVOS)
        {
            bankVO.setRealMoney(String.valueOf(bankVO.getMoney()));
            bankDB.update(bankVO);
        }

        InvoiceDB invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB);
        List<InvoiceVO> invoiceVOS=invoiceDB.getRealAmountIsNull();
        for(InvoiceVO invoiceVO:invoiceVOS)
        {
            invoiceVO.setRealAmount(String.valueOf(invoiceVO.getAmount()));
            invoiceDB.update(invoiceVO);
        }

        GoalDB goalDB=new GoalDB(MainActivity.chargeAPPDB);
        List<GoalVO> goalVOS=goalDB.getRealMoneyIsNull();
        for(GoalVO goalVO:goalVOS)
        {
            goalVO.setRealMoney(String.valueOf(goalVO.getMoney()));
            goalDB.update(goalVO);
        }
    }


    //新增table
    public static void tableExist(String table,String sql) {
        Cursor cursor=null;
        SQLiteDatabase db= MainActivity.chargeAPPDB.getReadableDatabase();
        //如果有就return
        try {
            String searchSql = "SELECT sql FROM sqlite_master where name = '"+table+"' ;";
            cursor = db.rawQuery(searchSql, null);
            if (cursor.moveToNext()) {
                cursor.close();
                return;
            }
        }catch (Exception e)
        {

        }

        //新增table
        try {
            db.execSQL(sql);
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    //新增欄位
    public static void colExist(String table,String col,String property) {
        Cursor cursor=null;
        SQLiteDatabase db= MainActivity.chargeAPPDB.getReadableDatabase();
        //如果有就return
        try {
            String sql = "SELECT sql FROM sqlite_master where name = '"+table+"' ;";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                String result = cursor.getString(0);
                if (result.indexOf(col) != -1) {
                    cursor.close();
                    return;
                }
            }
        }catch (Exception e)
        {

        }

        //新增欄位
        try {
            String add = "ALTER TABLE '"+table+"' ADD '" + col + "' "+property+";";
            db.execSQL(add);
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


//    public static void setAdView(AdView adView, Context activity) {
//        try {
//            if(MainActivity.adRequest==null) {
//                MainActivity.adRequest = new AdRequest.Builder().build();
//            }
//            adView.loadAd(MainActivity.adRequest);
//            adView.setAdListener(new AdListener() {
//                @Override
//                public void onAdFailedToLoad(int i) {
////                    AdRequest adRequest = new AdRequest.Builder().build();
////                    adView.loadAd(adRequest);
//                }
//            });
//
//        } catch (Exception e) {
//            Log.d("adError", e.toString());
//        }
//    }


    public static int[] colorlist = {
            Color.parseColor("#FF8888"),
            Color.parseColor("#FFDD55"),
            Color.parseColor("#77DDFF"),
            Color.parseColor("#9999FF"),
            Color.parseColor("#D28EFF"),
            Color.parseColor("#00DDDD")};

    public static int[] getColor(int size) {
        int[] cc = new int[size];
        length = colorlist.length;
        for (int i = 0; i < size; i++) {
            if (i >= length) {
                String c = "#";
                for (int j = 0; j < 6; j++) {
                    int idex = (int) (Math.random() * 16);
                    c = c + colorRadom().get(idex);
                }
                cc[i] = Color.parseColor(c);
            } else {
                cc[i] = colorlist[i];
            }
        }
        return cc;
    }

    public static List<String> colorRadom() {
        List<String> color = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            color.add(String.valueOf(i));
        }
        for (int i = 65; i <= 70; i++) {
            color.add(String.valueOf((char) i));
        }
        return color;
    }


    public static Description getDeescription() {
        description.setText(" ");
        return description;
    }

    //四捨五入
    public static int DoubleToInt(double a) {
        double b = new BigDecimal(a)
                .setScale(0, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
        return (int) b;
    }


    //Base64
    @NonNull
    public static String[] Base64Convert(String base64) throws UnsupportedEncodingException {
        byte[] bytes = Base64.decode(base64.trim(), Base64.DEFAULT);
        String debase64 = new String(bytes, "UTF-8");
        return debase64.trim().split(":");
    }

    //Big5
    @NonNull
    public static String Big5Convert(String result) throws UnsupportedEncodingException {
        String answer;
        try {
            int codeNumber = Common.identify(result.getBytes("ISO-8859-1"));
            switch (codeNumber) {
                case 1:
                    answer = new String(result.replaceAll("\\s+", "").getBytes("ISO-8859-1"), "Big5");
                    break;
                default:
                    answer = result;
                    break;
            }
        } catch (Exception e1) {
            answer = result;
        }
        return answer;
    }

    //QRCode-Convert
    public static StringBuilder QRCodeToString(List<String> resultString, StringBuilder sb) {
        ArrayList<String> result = new ArrayList<>();
        Double total, price, amount;
        for (String s : resultString) {
            String answer = s.replaceAll("\\s+", "");
            if (answer.length() <= 0) {
                continue;
            }
            result.add(answer);
            if (result.size() == 3) {
                price = Double.valueOf(Common.onlyNumber(result.get(2)));
                amount = Double.valueOf(Common.onlyNumber(result.get(1)));
                total = price * amount;
                sb.append(result.get(0) + " :\n").append(result.get(2) + " X ").append(result.get(1) + " = ").append(Common.DoubleToInt(total) + "\n");
                result.clear();
            }
        }
        return sb;
    }

    //QRCode Error
    public static StringBuilder QRCodeError(String resultString, StringBuilder sb) {
        sb = new StringBuilder();
        String[] resultS = resultString.trim().split(":");
        try {
            int i = 0;
            for (String s : resultS) {
                sb.append(s.replaceAll("\\s+", ""));
                int j = i % 3;
                if (j == 2) {
                    sb.append("\n");
                } else {
                    sb.append(":");
                }
                i++;
            }
        } catch (Exception e) {
            sb = new StringBuilder();
            sb.append("QRCode轉換失敗，請用\"QRCode下載功能\"");
        }
        return sb;
    }


    public static String[] WeekSetSpinnerBS =
            {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};


    public static String[] DaySetSpinnerBS() {
        String[] strings = new String[31];
        for (int i = 0; i < 31; i++) {
            strings[i] = " " + String.valueOf(i + 1) + "日";
        }
        return strings;
    }


    public static String[] MonthSetSpinnerBS() {
        String[] strings = new String[12];
        for (int i = 0; i < 12; i++) {
            strings[i] = " " + String.valueOf(i + 1) + "月";
        }
        return strings;
    }


    public static String[] DateStatueSetSpinner = {"每天", "每周", "每月", "每年"};


    public static Fragment returnFragment(View view)
    {
        Fragment fragment=null;
        String action = MainActivity.oldFramgent.getLast();
        Bundle bundle = MainActivity.bundles.getLast();
        Log.d("MainActivity", action);
        switch (action)
        {
            case "SelectActivity":
            case "SelectConsume":
                fragment = new SelectActivity();
                break;
            case "SelectListPieIncome":
                fragment = new SelectListPieIncome();
                break;
            case "SelectListBarIncome":
                fragment = new SelectListBarIncome();
                break;
            case "SelectListModelIM":
            case "SelectListModelCom":
                fragment = new SelectListModelActivity();
                break;
            case "SelectOtherCircle":
                fragment = new SelectOtherCircle();
                break;
            case "SelectDetList":
                fragment = new SelectDetList();
                break;
            case "SelectShowCircleDe":
                fragment = new SelectShowCircleDe();
                break;
            case "SelectDetCircle":
                fragment = new SelectDetCircle();
                break;
            case "SettingListFix":
                fragment = new SettingListFix();
                break;
            case "SettingListFixIon":
                fragment = new SettingListFixIon();
                break;
            case "SettingListFixCon":
                fragment = new SettingListFixCon();
                break;
            case "SelectShowCircleDeList":
                fragment = new SelectShowCircleDeList();
                break;
            case "UpdateInvoice":
                fragment = new UpdateInvoice();
                break;
            case "UpdateSpend":
                EditText detail=view.findViewById(R.id.detail);
                if(detail!=null)
                {
                    ConsumeVO consumeVO= (ConsumeVO) bundle.getSerializable("consumeVO");
                    consumeVO.setDetailname(detail.getText().toString());
                    bundle.putSerializable("consumeVO",consumeVO);
                    bundle.putSerializable("object", consumeVO);
                }
                fragment = new UpdateSpend();
                break;
            case "UpdateIncome":
                fragment = new UpdateIncome();
                break;
            case "HomePage":
                fragment = new HomePage();
                break;
            case "HomePagetList":
                fragment = new HomePagetList();
                break;
            case "InsertSpend":
                EditText detailI=view.findViewById(R.id.detail);
                if(detailI!=null)
                {
                    InsertSpend.consumeVO.setDetailname(detailI.getText().toString());
                }
                fragment = new InsertActivity();
                break;
            case "InsertIncome":
                fragment = new InsertActivity();
                break;
            case "SettingListType":
                fragment = new SettingListType();
                break;
            case "SettingMain":
                fragment = new SettingMain();
                break;
            case "GoalListAll":
                fragment = new GoalListAll();
                break;
            case "EleSetCarrier":
                fragment = new EleSetCarrier();
                break;
            case propertyMain:
                fragment=new PropertyMain();
                break;
            case Common.PropertyMoneyListString:
                fragment =new PropertyMoneyList();
                break;
            case Common.PropertyTotalString:
                fragment =new PropertyTotal();
                break;
            case Common.propertyConsumeShowString:
                fragment=new PropertyConsumeShow();
                break;
            case Common.settingListFixPropertyString:
                fragment =new SettingListFixProperty();
                break;
            case Common.searchMainString:
                fragment=new SearchMain();
                break;
            case Common.scanUpdateSpend:
                fragment=new ScanUpdateSpend();
                break;
        }
        fragment.setArguments(bundle);
        return fragment;
    }



    public static List<BootstrapText> DateChoiceSetBsTest(Activity activity, String[] data) {
        List<BootstrapText> bootstrapTexts = new ArrayList<>();
        for (String s : data) {
            BootstrapText text = new BootstrapText.Builder(activity)
                    .addText(s + " ")
                    .addFontAwesomeIcon(FA_CALCULATOR)
                    .build();
            bootstrapTexts.add(text);
        }
        return bootstrapTexts;
    }


    public static List<BootstrapText> propertyInsertMoneyData(Activity activity, String[] data) {
        List<BootstrapText> bootstrapTexts = new ArrayList<>();
        for (String s : data) {
            BootstrapText text = new BootstrapText.Builder(activity)
                    .addText(s + " ")
                    .addFontAwesomeIcon(FA_MONEY)
                    .build();
            bootstrapTexts.add(text);
        }
        return bootstrapTexts;
    }


    public static List<BootstrapText> searchScopeSetBsTest(Activity activity, String[] data,String style) {
        List<BootstrapText> bootstrapTexts = new ArrayList<>();
        for (String s : data) {
            BootstrapText text = new BootstrapText.Builder(activity)
                    .addText(s + " ")
                    .addFontAwesomeIcon(style)
                    .build();
            bootstrapTexts.add(text);
        }
        return bootstrapTexts;
    }


    public static List<BootstrapText> currecyData(Activity activity, String[] data) {
        List<BootstrapText> bootstrapTexts = new ArrayList<>();
        for (String s : data)
        {
            s= Common.showCurrency().get(s);
            BootstrapText text = new BootstrapText.Builder(activity)
                    .addText(s + " ")
                    .addFontAwesomeIcon(FA_MONEY)
                    .build();
            bootstrapTexts.add(text);
        }
        return bootstrapTexts;
    }


    public static void adjustFontScale(Configuration configuration, Activity activity) {
        if (configuration.fontScale > 1) {
            configuration.fontScale = (float) 1;
            DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) activity.getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            activity.getBaseContext().getResources().updateConfiguration(configuration, metrics);
        }
    }

    public static BootstrapText setCarrierSetBsTest(Activity activity, String data) {
        BootstrapText text = new BootstrapText.Builder(activity)
                .addText(data + "  ")
                .addFontAwesomeIcon(FA_ID_CARD_O)
                .build();
        return text;
    }

    public static BootstrapText setPriceHandSetBsTest(Activity activity, String data) {
        BootstrapText text = new BootstrapText.Builder(activity)
                .addText(data + "  ")
                .addFontAwesomeIcon(FA_CALCULATOR)
                .build();
        return text;
    }

    public static BootstrapText setPeriodSelectCBsTest(Activity activity, String data) {
        BootstrapText text = new BootstrapText.Builder(activity)
                .addText(data + "  ")
                .addFontAwesomeIcon(FA_CALENDAR_CHECK_O)
                .build();
        return text;
    }


    public static ArrayList<String> DateStatueSetSpinner() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("每天");
        strings.add("每周");
        strings.add("每月");
        strings.add("每年");
        return strings;
    }

    //純數字
    public static String onlyNumber(String s) {
        StringBuilder sb = new StringBuilder();
        Pattern ptn = Pattern.compile("[0-9]|[.]|[-]");
        Matcher mch = ptn.matcher(s);
        while (mch.find()) {
            sb.append(mch.group());
        }
        if(sb.length()>0)
        {
            double dValue = Double.valueOf(sb.toString());
            int value = (int) dValue;
            if (value == dValue) {
                return String.valueOf(value);
            } else {
                return sb.toString();
            }
        }else{
            return "0";
        }
    }

    //純數字
    public static Double onlyNumberToDouble(String s) {
        StringBuilder sb = new StringBuilder();
        Pattern ptn = Pattern.compile("[0-9]|[.]|[-]");
        Matcher mch = ptn.matcher(s);
        while (mch.find()) {
            sb.append(mch.group());
        }
        return Double.valueOf(sb.toString());
    }


    public static void showToast(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }

    //收鍵盤
    public static void clossKeyword(Activity context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(INPUT_METHOD_SERVICE);
        if(imm==null)
        {
            return;
        }
        View view=context.getWindow().getCurrentFocus();
        if(view==null)
        {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static String Utf8forURL(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }


    public static void askPermissions(String s, Activity context, int requestCode) {
        //因為是群組授權，所以請求ACCESS_COARSE_LOCATION就等同於請求ACCESS_FINE_LOCATION，因為同屬於LOCATION群組

        String[] permissions = {s};
        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(context,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    requestCode);
        }
    }

    //電子發票 Card類別
    public static HashMap<String, String> CardType() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("3J0002", "手機條碼");
        hashMap.put("1K0001", "悠遊卡");
        hashMap.put("1H0001", "一卡通");
        hashMap.put("2G0001", "愛金卡");
        hashMap.put("EG0002", "家樂福");
        return hashMap;
    }


    //price month
    public static HashMap<Integer, String> getPriceMonth() {
        HashMap<Integer, String> hashMap = new HashMap<>();
        hashMap.put(2, "01-02月\n");
        hashMap.put(4, "03-04月\n");
        hashMap.put(6, "05-06月\n");
        hashMap.put(8, "07-08月\n");
        hashMap.put(10, "09-10月\n");
        hashMap.put(12, "11-12月\n");
        return hashMap;
    }

    //price set
    public static HashMap<String, Integer> getlevellength() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("super", 2);
        hashMap.put("spc", 2);
        hashMap.put("first", 2);
        hashMap.put("second", 3);
        hashMap.put("third", 4);
        hashMap.put("fourth", 5);
        hashMap.put("fifth", 6);
        hashMap.put("sixth", 7);
        hashMap.put("other", 0);
        return hashMap;
    }

    public static HashMap<String, String> getPriceName() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("super", "特別獎");
        hashMap.put("spc", "特獎");
        hashMap.put("first", "頭獎");
        hashMap.put("second", "二獎");
        hashMap.put("third", "三獎");
        hashMap.put("fourth", "四獎");
        hashMap.put("fifth", "五獎");
        hashMap.put("sixth", "六獎");
        hashMap.put("other", "雲端發票專屬獎");
        return hashMap;
    }

    public static HashMap<String, String> getOtherType() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("O", "其他");
        hashMap.put("0", "未知");
        return hashMap;
    }

    public static String getType(String s) {
        String a = getOtherType().get(s);
        if (a == null) {
            return s;
        } else {
            return a;
        }
    }


    public static HashMap<String, String> getPrice() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("super", "1000萬元");
        hashMap.put("spc", "200萬元");
        hashMap.put("first", "20萬元");
        hashMap.put("second", "4萬元");
        hashMap.put("third", "1萬元");
        hashMap.put("fourth", "4千元");
        hashMap.put("fifth", "1千元");
        hashMap.put("sixth", "200元");
        hashMap.put("other", "雲端發票專屬獎");
        return hashMap;
    }

    public static HashMap<String, Integer> getIntPrice() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("super", 10000000);
        hashMap.put("spc", 2000000);
        hashMap.put("first", 200000);
        hashMap.put("second", 40000);
        hashMap.put("third", 10000);
        hashMap.put("fourth", 4000);
        hashMap.put("fifth", 1000);
        hashMap.put("sixth", 200);
        return hashMap;
    }

    //自動對獎
    private static String[] level = {"first", "second", "third", "fourth", "fifth", "sixth"};

    public void AutoSetPrice() {
        PriceDB priceDB = new PriceDB(MainActivity.chargeAPPDB);
        List<PriceVO> priceVOS = priceDB.getAll();
        int month;
        int year;
        for (PriceVO priceVO : priceVOS) {
            long startTime, endTime;
            String invoYM = priceVO.getInVoYm();
            month = Integer.valueOf(invoYM.substring(invoYM.length() - 2));
            year = Integer.valueOf(invoYM.substring(0, invoYM.length() - 2)) + 1911;
            startTime = (new GregorianCalendar(year, month - 2, 1, 0, 0, 0)).getTimeInMillis();
            Calendar endC = new GregorianCalendar(year, month - 1, 1);
            endTime = (new GregorianCalendar(year, month - 1, endC.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)).getTimeInMillis();
            autoSetCRWin(startTime, endTime, priceVO);
            autoSetInWin(startTime, endTime, priceVO);
        }
    }

    private void autoSetCRWin(long startTime, long endTime, PriceVO priceVO) {
        ConsumeDB consumeDB = new ConsumeDB(MainActivity.chargeAPPDB);
        BankDB bankDB = new BankDB(MainActivity.chargeAPPDB);
        List<ConsumeVO> consumeVOS = consumeDB.getNoWinAll(startTime, endTime);
        for (ConsumeVO consumeVO : consumeVOS) {
            String nul = consumeVO.getNumber();
            consumeVO.setIsWin("N");
            consumeVO.setIsWinNul("N");
            if (nul != null && nul.trim().length() == 10) {
                nul = nul.substring(2);
                List<String> result = answer(nul, priceVO);
                consumeVO.setIsWin(result.get(0));
                consumeVO.setIsWinNul(result.get(1));
                if (!consumeVO.getIsWin().trim().equals("N")) {
                    BankVO bankVO = new BankVO();
                    bankVO.setMoney(getIntPrice().get(consumeVO.getIsWin()));
                    bankVO.setDate(new Date(System.currentTimeMillis()));
                    bankVO.setMaintype("中獎");
                    bankVO.setFixDate("false");
                    int month = Integer.parseInt(priceVO.getInVoYm().substring(3));
                    String detail = priceVO.getInVoYm().substring(0, 3) + "年" + getPriceMonth().get(month)
                            + getPriceName().get(consumeVO.getIsWin()) + " : " + getPrice().get(consumeVO.getIsWin())+"\n中獎號碼 : "+consumeVO.getNumber();
                    bankVO.setDetailname(detail);
                    bankDB.insert(bankVO);
                }

            }
            consumeDB.update(consumeVO);
        }
    }

    private void autoSetInWin(long startTime, long endTime, PriceVO priceVO) {
        CarrierDB carrierDB = new CarrierDB(MainActivity.chargeAPPDB);
        InvoiceDB invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB);
        BankDB bankDB = new BankDB(MainActivity.chargeAPPDB);
        List<CarrierVO> carrierVOS = carrierDB.getAll();
        for (CarrierVO c : carrierVOS) {
            List<InvoiceVO> invoiceVOS = invoiceDB.getNotSetWin(c.getCarNul(), startTime, endTime);
            for (InvoiceVO i : invoiceVOS) {
                String nul = i.getInvNum().trim();
                i.setIswin("N");
                i.setIsWinNul("N");
                if (nul != null && nul.trim().length() == 10) {
                    nul = nul.substring(2);
                    List<String> inWin = answer(nul, priceVO);
                    i.setIswin(inWin.get(0));
                    i.setIsWinNul(inWin.get(1));
                    invoiceDB.update(i);
                    if (!i.getIswin().trim().equals("N")) {
                        BankVO bankVO = new BankVO();
                        bankVO.setFixDate("false");
                        bankVO.setMoney(getIntPrice().get(i.getIswin()));
                        bankVO.setDate(new Date(System.currentTimeMillis()));
                        bankVO.setMaintype("中獎");
                        int month = Integer.parseInt(priceVO.getInVoYm().substring(3));
                        String detail = priceVO.getInVoYm().substring(0, 3) + "年" + getPriceMonth().get(month)
                                + getPriceName().get(i.getIswin()) + " : " + getPrice().get(i.getIswin())+"\n中獎號碼 : "+i.getInvNum();
                        bankVO.setDetailname(detail);
                        bankDB.insert(bankVO);
                    }
                }
            }
        }
    }


    private static String firsttofourprice(String nul, String pricenul) {
        for (int i = 0; i < 6; i++) {
            if (nul.substring(i).equals(pricenul.substring(i))) {
                return level[i];
            }
        }
        return "N";
    }

    public static List<String> answer(String nul, PriceVO priceVO) {
        String threenul = nul.substring(5);
        String s;
        List<String> stringList = new ArrayList<>();
        if (nul.equals(priceVO.getSuperPrizeNo())) {
            stringList.add("super");
            stringList.add(priceVO.getSuperPrizeNo());
            return stringList;
        }
        if (nul.equals(priceVO.getSpcPrizeNo())) {
            stringList.add("spc");
            stringList.add(priceVO.getSpcPrizeNo());
            return stringList;
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo1());
        if (!s.equals("N")) {
            stringList.add(s);
            stringList.add(priceVO.getFirstPrizeNo1());
            return stringList;
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo2());
        if (!s.equals("N")) {
            stringList.add(s);
            stringList.add(priceVO.getFirstPrizeNo2());
            return stringList;
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo3());
        if (!s.equals("N")) {
            stringList.add(s);
            stringList.add(priceVO.getFirstPrizeNo3());
            return stringList;
        }
        if (threenul.equals(priceVO.getSixthPrizeNo1())) {
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo1());
            return stringList;
        }
        if (threenul.equals(priceVO.getSixthPrizeNo2())) {
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo2());
            return stringList;
        }
        if (threenul.equals(priceVO.getSixthPrizeNo3())) {
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo3());
            return stringList;
        }
        if (threenul.equals(priceVO.getSixthPrizeNo4())) {
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo4());
            return stringList;
        }
        if (threenul.equals(priceVO.getSixthPrizeNo5())) {
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo5());
            return stringList;
        }
        if (threenul.equals(priceVO.getSixthPrizeNo6())) {
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo6());
            return stringList;
        }
        stringList.add("N");
        stringList.add("N");
        return stringList;
    }


    public static void switchFragment(Fragment fragment, String fragmentTag, FragmentManager fragmentManager,Integer layout) {
        MainActivity.oldFramgent.add(fragmentTag);
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment fragment1 : fragmentManager.getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(layout, fragment);
        fragmentTransaction.commit();
    }


    public static void switchFragment(Fragment fragment, String fragmentTag, FragmentManager fragmentManager) {
        MainActivity.oldFramgent.add(fragmentTag);
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment fragment1 : fragmentManager.getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

    public static void switchConfirmFragment(Fragment fragment, FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment fragment1 : fragmentManager.getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
        MainActivity.oldFramgent.removeLast();
        MainActivity.bundles.removeLast();
    }


    public static void homePageFragment(FragmentManager fragmentManager, Activity activity) {
        MainActivity.oldFramgent.clear();
        MainActivity.bundles.clear();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment fragment1 : fragmentManager.getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, new HomePage());
        fragmentTransaction.commit();
        showToast(activity,activity.getString(R.string.error_lostData));
    }


    public static void insertAutoPropertyFromVo(PropertyFromDB propertyFromDB, PropertyFromVO propertyFromVO)
    {
          Double pTotal,sTotal;
          ConsumeDB consumeDB=new ConsumeDB(MainActivity.chargeAPPDB);
          BankDB bankDB=new BankDB(MainActivity.chargeAPPDB);
          if(StringUtil.isBlank(propertyFromVO.getSourceSecondType()))
          {
              pTotal=propertyFromDB.findBySourceMainType(propertyFromVO.getSourceMainType());
              sTotal=bankDB.getTotalMoneyByName(propertyFromVO.getSourceMainType());

          }else{
              pTotal=propertyFromDB.findBySourceSecondType(propertyFromVO.getSourceSecondType());
              sTotal=consumeDB.getAllSecondTypeMoney(propertyFromVO.getSourceSecondType());
          }
          sTotal=sTotal-pTotal;
          CurrencyDB currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);
          Calendar findTime=Calendar.getInstance();
          findTime.setTime(propertyFromVO.getSourceTime());
          int year=findTime.get(Calendar.YEAR);
          int month=findTime.get(Calendar.MONTH);
          int day=findTime.get(Calendar.DAY_OF_MONTH);
          Calendar start=new GregorianCalendar(year,month,day,0,0,0);
          Calendar end=new GregorianCalendar(year,month,day,23,59,59);
          CurrencyVO currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),propertyFromVO.getSourceCurrency());
          sTotal=sTotal/Double.valueOf(currencyVO.getMoney());
          //金額小於0部新增
          if(sTotal<0)
          {
              return;
          }

         String mainId= propertyFromVO.getId();
         propertyFromVO.setFixImport(false);
         propertyFromVO.setFixFromId(mainId);
         propertyFromVO.setSourceTime(new Date(System.currentTimeMillis()));



         if(propertyFromVO.getFixFromId()!=null&&!StringUtil.isBlank(propertyFromVO.getImportFee()))
         {
             Double fee=Double.valueOf(propertyFromVO.getImportFee());
             ConsumeVO consumeVO=consumeDB.findConByFk(propertyFromVO.getImportFeeId());
             if(fee>0&&consumeVO!=null)
             {
                consumeVO.setDate(new Date(System.currentTimeMillis()));
                consumeVO.setFkKey(UUID.randomUUID().toString());
                consumeVO.setIsWin("0");
                consumeVO.setNumber("");
                consumeVO.setIsWinNul("0");
                consumeDB.insert(consumeVO);
                propertyFromVO.setImportFeeId(consumeVO.getFkKey());
             }
         }

         //產生新Id
         propertyFromVO.setId(UUID.randomUUID().toString());
         propertyFromDB.insert(propertyFromVO);

    }



    public static void showNotification(String title, String message, Context context, int NOTIFICATION_ID, Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            String PRIMARY_CHANNEL = "記帳小助手";
            NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                    PRIMARY_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
            chan1.setLightColor(Color.GREEN);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(chan1);


            PendingIntent appIntent = PendingIntent.getActivity(context, 0, intent, 0);
            Notification.Builder nb = new Notification.Builder(context.getApplicationContext(), PRIMARY_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(appIntent);
            manager.notify(NOTIFICATION_ID, nb.build());
        }else{
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, 0);
            Notification notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    public static HashMap<String,String> priceMonth()
    {
        HashMap<String,String> price=new HashMap<>();
        price.put("02","1-2月");
        price.put("04","3-4月");
        price.put("06","5-6月");
        price.put("08","7-8月");
        price.put("10","9-10月");
        price.put("12","11-12月");
        return price;
    }

   public static String KeyToRed(String s,String key)
   {
       //可能大寫
       if(!s.contains(key))
       {
           key=key.toUpperCase();
       }
       //可能小寫
       if(!s.contains(key))
       {
           key=key.toLowerCase();
       }
       String fontS="<font color=\"red\">"+key+"</font>";
       s=s.replace(key,fontS);
       return s.replace("\n","<br>");
   }


    /**
     * View渐隐动画效果
     */
    public static void setHideAnimation(View view, int duration)
    {
        if (null == view || duration < 0)
        {
            return;
        }
        AlphaAnimation mHideAnimation= (AlphaAnimation) view.getAnimation();
        if (null != mHideAnimation)
        {
            mHideAnimation.cancel();
        }
        // 监听动画结束的操作
        mHideAnimation = new AlphaAnimation(1.0f, 0.0f);
        mHideAnimation.setDuration(duration);
        mHideAnimation.setFillAfter(true);
        mHideAnimation.setAnimationListener(new Animation.AnimationListener()
        {

            @Override
            public void onAnimationStart(Animation arg0)
            {

            }

            @Override
            public void onAnimationRepeat(Animation arg0)
            {

            }

            @Override
            public void onAnimationEnd(Animation arg0)
            {
                view.setVisibility(View.GONE);
            }
        });
        view.startAnimation(mHideAnimation);
    }


    /**
     * View渐现动画效果
     */
    public static void setShowAnimation( final View view, int duration)
    {
        if (null == view || duration < 0)
        {
            return;
        }
        AlphaAnimation mShowAnimation= (AlphaAnimation) view.getAnimation();
        if (null != mShowAnimation)
        {
            mShowAnimation.cancel();
        }
        mShowAnimation = new AlphaAnimation(0.0f, 1.0f);
        mShowAnimation.setDuration(duration);
        mShowAnimation.setFillAfter(true);
        mShowAnimation.setAnimationListener(new Animation.AnimationListener()
        {

            @Override
            public void onAnimationStart(Animation arg0)
            {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation arg0)
            {

            }

            @Override
            public void onAnimationEnd(Animation arg0)
            {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setHideAnimation(view,1);
            }
        });
        view.startAnimation(mShowAnimation);
    }


    // true 相等 false 不相等
    public static boolean checkEqualOnDay(Date dateOne, Date dateTwo)
    {
        Calendar a =Calendar.getInstance();
        Calendar b=Calendar.getInstance();
        a.setTime(dateOne);
        b.setTime(dateTwo);

        if(a.get(Calendar.YEAR)==b.get(Calendar.YEAR))
        {
            if(a.get(Calendar.MONTH)==b.get(Calendar.MONTH))
            {
                if(a.get(Calendar.DAY_OF_MONTH)==b.get(Calendar.DAY_OF_MONTH))
                {
                       return true;
                }
            }
        }


        return false;
    }


    public static ConsumeVO getType (ConsumeVO consumeVO){
        TypeDetailDB typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB);
        List<TypeDetailVO> typeDetailVOS = typeDetailDB.getTypdAll();
        String main = "其他", second = "其他";
        int x = 0, total = 0;
        for (TypeDetailVO t : typeDetailVOS) {
            x = 0;
            String[] key = t.getKeyword().split(" ");
            for (int i = 0; i < key.length; i++) {
                if (consumeVO.getDetailname().indexOf(key[i].trim()) != -1) {
                    x = x + key[i].length();
                }
            }
            if (x > total) {
                total = x;
                main = t.getGroupNumber();
                second = t.getName();
            }
        }
        if (second.indexOf("餐") != -1) {
            int hour = Integer.valueOf(Common.sHour.format(consumeVO.getDate()));
            if (hour > 0 && hour < 11) {
                second = "早餐";
            } else if (hour >= 11 && hour < 18) {
                second = "午餐";
            } else {
                second = "晚餐";
            }
        }
        consumeVO.setMaintype(main);
        consumeVO.setSecondType(second);
        return consumeVO;
    }


     public static Date stringToDate(String s,boolean end)
     {
         String[] strings=s.split("/");
         int year= Integer.parseInt(strings[0]);
         int month= Integer.parseInt(strings[1])-1;
         int day= Integer.parseInt(strings[2]);
         Calendar time;
         if(end)
         {
             time=new GregorianCalendar(year,month,day,23,59,59);
         }else {
             time=new GregorianCalendar(year,month,day,0,0,0);
         }

         return new Date(time.getTimeInMillis());
     }


}

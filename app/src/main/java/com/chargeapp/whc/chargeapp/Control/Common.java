package com.chargeapp.whc.chargeapp.Control;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapText;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.github.mikephil.charting.components.Description;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_CALCULATOR;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_ID_CARD_O;

/**
 * Created by Wang on 2017/11/19.
 */

public class Common {

    public static int length=0;
    public static Description description=new Description();
    public static boolean showfirstgrid = false;
    public static boolean showsecondgrid = false;


    public static SimpleDateFormat sOne = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
    public static SimpleDateFormat sTwo = new SimpleDateFormat("yyyy/MM/dd");
    public static SimpleDateFormat sThree = new SimpleDateFormat("yyyy 年 MM 月");
    public static SimpleDateFormat sFour = new SimpleDateFormat("yyyy 年");
    public static SimpleDateFormat sDay = new SimpleDateFormat("MM/dd");
    public static SimpleDateFormat sHour = new SimpleDateFormat("hh");
    public static SimpleDateFormat sYear = new SimpleDateFormat("yyy 年 MM 月");
    public static NumberFormat nf = NumberFormat.getNumberInstance();
    public static List<CarrierVO> lostCarrier;
    public static Screen screenSize;
    public enum Screen {
        xLarge,
        large,
        normal
    }

    public static void setScreen(Screen screen,DisplayMetrics displayMetrics)
    {
        if(screen==null)
        {
            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            if(dpWidth>650)
            {
                Common.screenSize= Common.Screen.xLarge;
            }else if(dpWidth>470)
            {
                Common.screenSize= Common.Screen.large;
            }else{
                Common.screenSize= Common.Screen.normal;
            }
        }
    }


    public static void setChargeDB(Context activity)
    {
        if(MainActivity.chargeAPPDB==null)
        {
            MainActivity.chargeAPPDB=new ChargeAPPDB(activity);
        }
         ConsumeDB consumeDB=new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
         consumeDB.colExist("rdNumber");
    }

    public static void setAdView(final AdView adView, Context activity)
    {
        try {
            MobileAds.initialize(activity, "ca-app-pub-5169620543343332~2865524734");
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);
                }
            });
        }catch (Exception e)
        {
            Log.d("adError",e.toString());
        }
    }



    public static int[] colorlist = {
            Color.parseColor("#FF8888"),
            Color.parseColor("#FFDD55"),
            Color.parseColor("#77DDFF"),
            Color.parseColor("#9999FF"),
            Color.parseColor("#D28EFF"),
            Color.parseColor("#00DDDD")};

    public static int[] getColor(int size)
    {
        int[] cc=new int[size];
        length=colorlist.length;
        for(int i=0;i<size;i++)
        {
            if(i>=length)
            {
                String c="#";
               for(int j=0;j<6;j++)
               {
                   int idex= (int) (Math.random()*16);
                   c=c+colorRadom().get(idex);
               }
               cc[i]=Color.parseColor(c);
            }else{
                cc[i]=colorlist[i];
            }
        }
        return cc;
    }

    public static List<String> colorRadom()
    {
        List<String> color=new ArrayList<>();
        for(int i=0;i<=9;i++)
        {
            color.add(String.valueOf(i));
        }
        for(int i=65;i<=70;i++)
        {
            color.add(String.valueOf((char)i));
        }
        return color;
    }


    public static Description getDeescription()
    {
        description.setText(" ");
        return description;
    }

    //四捨五入
    public static int DoubleToInt(double a)
    {
        double b = new BigDecimal(a)
                .setScale(0, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
        return (int)b;
    }


    public static String[] WeekSetSpinnerBS=
            {"星期一","星期二","星期三","星期四","星期五","星期六","星期日"};



    public static String[] DaySetSpinnerBS()
    {
        String[] strings=new String[31];
        for (int i = 0; i < 31; i++) {
            strings[i]=" "+ String.valueOf(i+1) +"日";
        }
        return strings;
    }


    public static String[] MonthSetSpinnerBS()
    {
        String[] strings=new String[12];
        for (int i = 0; i < 12; i++) {
            strings[i]=" " + String.valueOf(i+1) + "月";
        }
        return strings;
    }



    public static String[] DateStatueSetSpinner={"每天","每周","每月","每年"};


    public static List<BootstrapText> DateChoiceSetBsTest(Activity activity,String[] data)
    {
        List<BootstrapText> bootstrapTexts=new ArrayList<>();
        for (String s:data)
        {
            BootstrapText text = new BootstrapText.Builder(activity)
                    .addText(s+" ")
                    .addFontAwesomeIcon(FA_CALCULATOR)
                    .build();
            bootstrapTexts.add(text);
        }
        return bootstrapTexts;
    }

    public static List<BootstrapText> ChoiceCarrierSetBsTest(Activity activity,String[] data)
    {
        List<BootstrapText> bootstrapTexts=new ArrayList<>();
        for (String s:data)
        {
            BootstrapText text = new BootstrapText.Builder(activity)
                    .addText(s+" ")
                    .addFontAwesomeIcon(FA_ID_CARD_O)
                    .build();
            bootstrapTexts.add(text);
        }
        return bootstrapTexts;
    }

    public static ArrayList<String> DateStatueSetSpinner()
    {
        ArrayList<String> strings=new ArrayList<>();
        strings.add("每天");
        strings.add("每周");
        strings.add("每月");
        strings.add("每年");
        return strings;
    }
    //純數字
    public static String onlyNumber(String s){
        StringBuilder sb=new StringBuilder();
        Pattern ptn=Pattern.compile("[0-9]|[.]");
        Matcher mch=ptn.matcher(s);
        while(mch.find()) {
            sb.append(mch.group());
        }
        return sb.toString();
    }




    public static void showToast(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {

        }
    }
    //收鍵盤
    public static void  clossKeyword(Activity context)
    {
        InputMethodManager imm = (InputMethodManager)context
        .getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static String Utf8forURL(HashMap<String, String> params) throws UnsupportedEncodingException
    {
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


    public static void askPermissions(String s,Activity context) {
        //因為是群組授權，所以請求ACCESS_COARSE_LOCATION就等同於請求ACCESS_FINE_LOCATION，因為同屬於LOCATION群組

        String[] permissions={s};
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
                    0);
        }
    }

    //電子發票 Card類別
    public static HashMap<String,String> CardType() {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("3J0002","手機條碼");
        hashMap.put("1K0001","悠遊卡");
        hashMap.put("1H0001","一卡通");
        hashMap.put("2G0001","愛金卡");
        hashMap.put("EG0002","家樂福");
        return hashMap;
    }



    //price month
    public static HashMap<Integer,String> getPriceMonth() {
        HashMap<Integer,String> hashMap=new HashMap<>();
        hashMap.put(2,"01-02月\n");
        hashMap.put(4,"03-04月\n");
        hashMap.put(6,"05-06月\n");
        hashMap.put(8,"07-08月\n");
        hashMap.put(10,"09-10月\n");
        hashMap.put(12,"11-12月\n");
        return hashMap;
    }

    //price set
    public static HashMap<String,Integer> getlevellength() {
        HashMap<String,Integer> hashMap=new HashMap<>();
        hashMap.put("super",2);
        hashMap.put("spc",2);
        hashMap.put("first",2);
        hashMap.put("second",3);
        hashMap.put("third",4);
        hashMap.put("fourth",5);
        hashMap.put("fifth",6);
        hashMap.put("sixth",7);
        return hashMap;
    }

    public static HashMap<String,String> getPriceName() {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("super","特別獎");
        hashMap.put("spc","特獎");
        hashMap.put("first","頭獎");
        hashMap.put("second","二獎");
        hashMap.put("third","三獎");
        hashMap.put("fourth","四獎");
        hashMap.put("fifth","五獎");
        hashMap.put("sixth","六獎");
        return hashMap;
    }

    public static HashMap<String,String> getOtherType() {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("O","其他");
        hashMap.put("0","未知");
        return hashMap;
    }

    public static String getType(String s)
    {
        String a=getOtherType().get(s);
        if(a==null)
        {
          return s;
        }else{
            return a;
        }
    }


    public static HashMap<String,String> getPrice() {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("super","1000萬元");
        hashMap.put("spc","200萬元");
        hashMap.put("first","20萬元");
        hashMap.put("second","4萬元");
        hashMap.put("third","1萬元");
        hashMap.put("fourth","4千元");
        hashMap.put("fifth","1千元");
        hashMap.put("sixth","200元");
        return hashMap;
    }

    public static HashMap<String,Integer> getIntPrice() {
        HashMap<String,Integer> hashMap=new HashMap<>();
        hashMap.put("super",10000000);
        hashMap.put("spc",2000000);
        hashMap.put("first",200000);
        hashMap.put("second",40000);
        hashMap.put("third",10000);
        hashMap.put("fourth",4000);
        hashMap.put("fifth",1000);
        hashMap.put("sixth",200);
        return hashMap;
    }

    //自動兌獎
    private String[] level = {"first", "second", "third", "fourth", "fifth", "sixth"};
    public void AutoSetPrice() {
        PriceDB priceDB=new  PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        List<PriceVO> priceVOS = priceDB.getAll();
        int month;
        int year;
        for (PriceVO priceVO : priceVOS) {
            long startTime, endTime;
            String invoYM = priceVO.getInvoYm();
            month = Integer.valueOf(invoYM.substring(invoYM.length() - 2));
            year = Integer.valueOf(invoYM.substring(0, invoYM.length() - 2)) + 1911;
            startTime = (new GregorianCalendar(year, month - 2, 1,0,0,0)).getTimeInMillis();
            Calendar endC=new GregorianCalendar(year, month-1, 1);
            endTime = (new GregorianCalendar(year, month-1, endC.getActualMaximum(Calendar.DAY_OF_MONTH),23,59,59)).getTimeInMillis();
            autoSetCRWin(startTime, endTime, priceVO);
            autoSetInWin(startTime, endTime, priceVO);
        }
    }

    private void autoSetCRWin(long startTime, long endTime, PriceVO priceVO) {
        ConsumeDB consumeDB =new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        BankDB bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        List<ConsumeVO> consumeVOS = consumeDB.getNoWinAll(startTime, endTime);
        for (ConsumeVO consumeVO : consumeVOS) {
            String nul = consumeVO.getNumber().trim();
            consumeVO.setIsWin("N");
            consumeVO.setIsWinNul("N");
            if (nul != null && nul.trim().length() == 10) {
                nul = nul.substring(2);
                List<String> result = anwswer(nul, priceVO);
                consumeVO.setIsWin(result.get(0));
                consumeVO.setIsWinNul(result.get(1));
                if(!consumeVO.getIsWin().trim().equals("N"))
                {
                    BankVO bankVO=new BankVO();
                    bankVO.setMoney(getIntPrice().get(consumeVO.getIsWin()));
                    bankVO.setDate(new Date(System.currentTimeMillis()));
                    bankVO.setMaintype("中獎");
                    bankVO.setFixDate("false");
                    int month= Integer.parseInt(priceVO.getInvoYm().substring(3));
                    String detail=priceVO.getInvoYm().substring(0,3)+"年"+getPriceMonth().get(month)
                            +getPriceName().get(consumeVO.getIsWin())+" : "+getPrice().get(consumeVO.getIsWin());
                    bankVO.setDetailname(detail);
                    bankDB.insert(bankVO);
                }

            }
            consumeDB.update(consumeVO);
        }
    }

    private void autoSetInWin(long startTime, long endTime, PriceVO priceVO) {
        CarrierDB carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        InvoiceDB invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        BankDB bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        List<CarrierVO> carrierVOS = carrierDB.getAll();
        for (CarrierVO c : carrierVOS) {
            List<InvoiceVO> invoiceVOS = invoiceDB.getNotSetWin(c.getCarNul(), startTime, endTime);
            for (InvoiceVO i : invoiceVOS) {
                String nul = i.getInvNum().trim();
                i.setIswin("N");
                i.setIsWinNul("N");
                if (nul != null && nul.trim().length() == 10)
                {
                    nul=nul.substring(2);
                    List<String> inWin = anwswer(nul, priceVO);
                    i.setIswin(inWin.get(0));
                    i.setIsWinNul(inWin.get(1));
                    invoiceDB.update(i);
                    if(!i.getIswin().trim().equals("N"))
                    {
                        BankVO bankVO=new BankVO();
                        bankVO.setFixDate("false");
                        bankVO.setMoney(getIntPrice().get(i.getIswin()));
                        bankVO.setDate(new Date(System.currentTimeMillis()));
                        bankVO.setMaintype("中獎");
                        int month= Integer.parseInt(priceVO.getInvoYm().substring(3));
                        String detail=priceVO.getInvoYm().substring(0,3)+"年"+getPriceMonth().get(month)
                                +getPriceName().get(i.getIswin())+" : "+getPrice().get(i.getIswin());
                        bankVO.setDetailname(detail);
                        bankDB.insert(bankVO);
                    }
                }
            }
        }
    }


    private String firsttofourprice(String nul, String pricenul) {
        for (int i = 0; i < 6; i++) {
            if (nul.substring(i).equals(pricenul.substring(i))) {
                return level[i];
            }
        }
        return "N";
    }

    private List<String> anwswer(String nul, PriceVO priceVO) {
        String threenul = nul.substring(5);
        String s;
        List<String> stringList=new ArrayList<>();
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


}

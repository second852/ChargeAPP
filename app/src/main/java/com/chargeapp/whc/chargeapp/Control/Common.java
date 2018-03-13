package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.graphics.Color;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.components.Description;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Wang on 2017/11/19.
 */

public class Common {

    public static int length=0;
    public static Description description=new Description();
    public static boolean showfirstgrid = false;
    public static boolean showsecondgrid = false;


    public static SimpleDateFormat sOne = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
    public static SimpleDateFormat sTwo = new SimpleDateFormat("yyyy/M/dd");
    public static SimpleDateFormat sThree = new SimpleDateFormat("yyyy 年 MM 月");
    public static SimpleDateFormat sFour = new SimpleDateFormat("yyyy 年");
    public static SimpleDateFormat sDay = new SimpleDateFormat("MM/dd");
    public static SimpleDateFormat sHour = new SimpleDateFormat("hh");

    public static int[] colorlist = {Color.parseColor("#FF8888"),
            Color.parseColor("#FFDD55"),
            Color.parseColor("#66FF66"),
            Color.parseColor("#77DDFF"),
            Color.parseColor("#D28EFF"),
            Color.parseColor("#aaaaaa")};

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


    public static void showToast(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
            if(SettingDownloadFile.mGoogleApiClient!=null)
            {
                SettingDownloadFile.mGoogleApiClient.disconnect();
                SettingDownloadFile.mGoogleApiClient=null;
            }
        }
    }








}

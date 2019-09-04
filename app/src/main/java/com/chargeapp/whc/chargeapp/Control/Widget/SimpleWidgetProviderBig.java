package com.chargeapp.whc.chargeapp.Control.Widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.Control.Welcome;
import com.chargeapp.whc.chargeapp.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;


import java.util.HashMap;
import java.util.List;


/**
 * Created by 1709008NB01 on 2018/3/13.
 */

public class SimpleWidgetProviderBig extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        int count = appWidgetIds.length;
        ChargeAPPDB chargeAPPDB=new ChargeAPPDB(context);
        CarrierDB carrierDB=new CarrierDB(chargeAPPDB);
        List<String> springItem=carrierDB.getAllNul();
        SharedPreferences sharedPreferences=context.getSharedPreferences("Charge_User",Context.MODE_PRIVATE);
        int b=sharedPreferences.getInt("carrier",0);
        try{
            for (int i = 0; i < count; i++) {
                int widgetId = appWidgetIds[i];
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.simple_widget);
                Intent intent = new Intent(context, Welcome.class);
                if(springItem.size()>0)
                {
                    remoteViews.setBitmap(R.id.imageView, "setImageBitmap",encodeAsBitmap(springItem.get(b), BarcodeFormat.CODE_39, 600, 100));
                    remoteViews.setTextViewText(R.id.text,springItem.get(b));
                    remoteViews.setViewVisibility(R.id.imageView, View.VISIBLE);
                }else{
                    remoteViews.setTextViewText(R.id.text,"無載具，請點擊新增載具");
                    remoteViews.setViewVisibility(R.id.imageView, View.GONE);
                }
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent, 0);
                remoteViews.setOnClickPendingIntent(R.id.linearLayout, pendingIntent);
                appWidgetManager.updateAppWidget(widgetId, remoteViews);
            }
        }catch (Exception e)
        {
           Log.d("XXX",e.getMessage());
        }
    }



    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight) throws WriterException {
        if (contents.length() == 0) return null;
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;
        HashMap<EncodeHintType, String> hints = null;
        String encoding = null;
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                encoding = "UTF-8";
                break;
            }
        }
        if (encoding != null) {
            hints = new HashMap<EncodeHintType, String>(2);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = writer.encode(contents, format, desiredWidth, desiredHeight, hints);
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

}

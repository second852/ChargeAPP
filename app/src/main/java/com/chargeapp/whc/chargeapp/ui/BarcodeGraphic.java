/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chargeapp.whc.chargeapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.HashMap;

/**
 * Graphic instance for rendering barcode position, size, and ID within an associated graphic
 * overlay view.
 */
public class BarcodeGraphic extends TrackedGraphic<Barcode> {
    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN
    };
    private static int mCurrentColorIndex = 0;

    private Paint mRectPaint;
    private Paint mTextPaint;
    private volatile Barcode mBarcode;
    private Activity context;
    public static HashMap<Integer, String> hashMap;
    private PriceDB priceDB;
    private String[] level = {"first", "second", "third", "fourth", "fifth", "sixth"};
    private PriceVO priceVO;
    private HashMap<String, Integer> levellength;
    private HashMap<String, String> levelprice;
    private String EleNul;
    private int max;




    BarcodeGraphic(GraphicOverlay overlay, Activity context) {
        super(overlay);
        hashMap = new HashMap<>();
        this.context = context;
        priceDB = new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];
        mRectPaint = new Paint();
        mRectPaint.setColor(selectedColor);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(4.0f);
        //paint
        mTextPaint = new Paint();
        mTextPaint.setColor(selectedColor);
        mTextPaint.setTextSize(36.0f);
        levelprice = getHashLP();
        levellength = getlevellength();
        max= Integer.parseInt(priceDB.findMaxPeriod());
    }


    private static HashMap<String, Integer> getlevellength() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("super", 8);
        hashMap.put("spc", 8);
        hashMap.put("first", 8);
        hashMap.put("second", 7);
        hashMap.put("third", 6);
        hashMap.put("fourth", 5);
        hashMap.put("fifth", 4);
        hashMap.put("sixth", 3);
        return hashMap;
    }


    private static HashMap<String, String> getHashLP() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("super", "\n特別獎1000萬元");
        hashMap.put("spc", "\n特獎200萬元");
        hashMap.put("first", "\n頭獎20萬元");
        hashMap.put("second", "\n二獎4萬元");
        hashMap.put("third", "\n三獎1萬元");
        hashMap.put("fourth", "\n四獎4千元");
        hashMap.put("fifth", "\n五獎1千元");
        hashMap.put("sixth", "\n六獎200元");
        return hashMap;
    }

    /**
     * Updates the barcode instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateItem(Barcode barcode) {
        mBarcode = barcode;
        if(barcode==null)
        {
            postInvalidate();
            return;
        }
        if (MultiTrackerActivity.refresh) {
            if (mBarcode.rawValue.indexOf(":") != -1 && (!(mBarcode.rawValue.indexOf("**") == 0))) {
                hashMap.put(1, barcode.rawValue);
                Log.d("XXXXXX1", barcode.rawValue);
            }
            if (mBarcode.rawValue.indexOf("**") == 0) {
                hashMap.put(2, barcode.rawValue.substring(2));
                Log.d("XXXXXX2", barcode.rawValue.substring(2));
            }
            if (hashMap.size() == 2) {
                Intent intent = new Intent(context,MainActivity.class);
                if(MultiTrackerActivity.action.equals("UpdateSpend"))
                {
                    Bundle bundle=context.getIntent().getBundleExtra("bundle");
                    intent.putExtra("bundle",context.getIntent().getBundleExtra("bundle"));
                }
                intent.putExtra("action",MultiTrackerActivity.action);
                context.startActivity(intent);
                context.finish();
            }
        } else {
            if (mBarcode.rawValue.indexOf(":") != -1 && (!(mBarcode.rawValue.indexOf("**") == 0))) {
                EleNul = mBarcode.rawValue.substring(0, 10);
                if(MultiTrackerActivity.oldElu==null||(!MultiTrackerActivity.oldElu.equals(EleNul)))
                {
                    MultiTrackerActivity.oldElu=EleNul;
                    MultiTrackerActivity.isold=false;
                    MultiTrackerActivity.colorChange++;
                }else{
                    MultiTrackerActivity.isold=true;
                    postInvalidate();
                    return;
                }
                String day = mBarcode.rawValue.substring(10, 17);
                int mon = Integer.parseInt(day.substring(3, 5));
                if (mon % 2 == 1) {
                    if (mon == 11) {
                        day = day.substring(0, 4) + "2";
                    } else if (mon == 10) {
                        day = day.substring(0, 4) + "1";
                    }  else if (mon == 9) {
                        day = day.substring(0, 3) + "10";
                    }else {
                        mon = mon + 1;
                        day = day.substring(0, 4) + String.valueOf(mon);
                    }
                } else {
                    day = day.substring(0, 5);
                }
                MultiTrackerActivity.p=getPeriod(day);
                if(Integer.valueOf(day)>max)
                {

                    MultiTrackerActivity.result="over";
                    postInvalidate();
                    return;
                }
                priceVO=priceDB.getPeriodAll(day);
                if(priceVO==null)
                {
                    MultiTrackerActivity.result="no";
                    postInvalidate();
                    return;
                }
                MultiTrackerActivity.result=anwswer(EleNul.substring(2),priceVO);
            }
        }
        postInvalidate();
    }


    private String firsttofourprice(String nul, String pricenul) {
        for (int i = 0; i < 6; i++) {
            if (nul.substring(i).equals(pricenul.substring(i))) {
                return level[i];
            }
        }
        return "N";
    }

    private String anwswer(String nul, PriceVO priceVO) {
        String threenul = nul.substring(5);
        String s;
        if (nul.equals(priceVO.getSuperPrizeNo())) {
            levelprice.put("win", priceVO.getSuperPrizeNo());
            return "super";
        }
        if (nul.equals(priceVO.getSpcPrizeNo())) {
            levelprice.put("win", priceVO.getSpcPrizeNo());
            return "spc";
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo1());
        if (!s.equals("N")) {
            levelprice.put("win", priceVO.getFirstPrizeNo1());
            return s;
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo2());
        if (!s.equals("N")) {
            levelprice.put("win", priceVO.getFirstPrizeNo2());
            return s;
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo3());
        if (!s.equals("N")) {
            levelprice.put("win", priceVO.getFirstPrizeNo3());
            return s;
        }
        if (threenul.equals(priceVO.getSixthPrizeNo1())) {
            levelprice.put("win", priceVO.getSixthPrizeNo1());
            return "sixth";
        }
        if (threenul.equals(priceVO.getSixthPrizeNo2())) {
            levelprice.put("win", priceVO.getSixthPrizeNo2());
            return "sixth";
        }
        if (threenul.equals(priceVO.getSixthPrizeNo3())) {
            levelprice.put("win", priceVO.getSixthPrizeNo3());
            return "sixth";
        }
        if (threenul.equals(priceVO.getSixthPrizeNo4())) {
            levelprice.put("win", priceVO.getSixthPrizeNo4());
            return "sixth";
        }
        if (threenul.equals(priceVO.getSixthPrizeNo5())) {
            levelprice.put("win", priceVO.getSixthPrizeNo5());
            return "sixth";
        }
        if (threenul.equals(priceVO.getSixthPrizeNo6())) {
            levelprice.put("win", priceVO.getSixthPrizeNo6());
            return "sixth";
        }
        return "N";
    }


    /**
     * Draws the barcode annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Barcode barcode = mBarcode;
        if (barcode != null) {
            // Draws the bounding box around the barcode.
            RectF rect = new RectF(barcode.getBoundingBox());
            rect.left = translateX(rect.left);
            rect.top = translateY(rect.top);
            rect.right = translateX(rect.right);
            rect.bottom = translateY(rect.bottom);
            canvas.drawRect(rect, mRectPaint);
        }
        if(MultiTrackerActivity.refresh)
        {
            return;
        }
        if(MultiTrackerActivity.result==null)
        {
            MultiTrackerActivity.answer.setText("請對準左邊QRCode~");
            return;
        }
        if(MultiTrackerActivity.result.equals("over"))
        {
            MultiTrackerActivity.answer.setText(MultiTrackerActivity.p+"尚未開獎");
            return;
        }
        if(MultiTrackerActivity.result.equals("no"))
        {
            MultiTrackerActivity.answer.setText(MultiTrackerActivity.p+"已過兌獎期限");
            return;
        }
        if (!MultiTrackerActivity.isold) {
            if (MultiTrackerActivity.result.equals("N")) {
                String total=MultiTrackerActivity.p+"\n發票號碼:"+MultiTrackerActivity.oldElu+"\n"+"沒有中獎!再接再厲!";
                if((MultiTrackerActivity.colorChange%2)==0)
                {
                    Spannable content = new SpannableString(total);
                    content.setSpan(new ForegroundColorSpan(Color.BLUE), 0,total.indexOf("發") , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    content.setSpan(new ForegroundColorSpan(Color.BLUE), total.indexOf(":")+1, total.indexOf("沒"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    MultiTrackerActivity.answer.setText(content);
                }else{
                    Spannable content = new SpannableString(total);
                    content.setSpan(new ForegroundColorSpan(Color.parseColor("#00AA55")), 0,total.indexOf("發") , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    content.setSpan(new ForegroundColorSpan(Color.parseColor("#00AA55")), total.indexOf(":")+1, total.indexOf("沒"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    MultiTrackerActivity.answer.setText(content);
                }
            } else {
                String peroid = getPeriod(priceVO.getInvoYm());
                peroid = peroid + levelprice.get("win") + levelprice.get(MultiTrackerActivity.result) + "\n中獎號碼" + MultiTrackerActivity.oldElu;
                Spannable content = new SpannableString(peroid);
                content.setSpan(new ForegroundColorSpan(Color.RED), 10, 10+levellength.get(MultiTrackerActivity.result), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                content.setSpan(new ForegroundColorSpan(Color.MAGENTA), peroid.length()-(levellength.get(MultiTrackerActivity.result)), peroid.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                MultiTrackerActivity.answer.setText(content);
            }
        }
    }



    private String getPeriod(String inYm) {
        String day = inYm.substring(3, 5);
        String period;
        if (day.equals("02")) {
            period = inYm.substring(0, 3) + "年01-02月";
        } else if (day.equals("04")) {
            period = inYm.substring(0, 3) + "年03-04月";
        } else if (day.equals("06")) {
            period = inYm.substring(0, 3) + "年05-06月";
        } else if (day.equals("08")) {
            period = inYm.substring(0, 3) + "年07-08月";
        } else if (day.equals("10")) {
            period = inYm.substring(0, 3) + "年09-10月";
        } else {
            period = inYm.substring(0, 3) + "年11-12月";
        }
        return period;
    }
}

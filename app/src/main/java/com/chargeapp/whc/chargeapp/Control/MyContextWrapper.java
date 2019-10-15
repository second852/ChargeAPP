package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

public class MyContextWrapper extends ContextWrapper {
    public MyContextWrapper(Context base) {
        super(base);
    }
    @NonNull
    public static ContextWrapper wrap(Context context) {
        Resources resources = context.getResources();
        Configuration newConfig = new Configuration();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        newConfig.setToDefaults();
        //如果没有设置densityDpi, createConfigurationContext对字体大小设置限制无效
        newConfig.densityDpi = metrics.densityDpi;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context = context.createConfigurationContext(newConfig);
        } else {
            resources.updateConfiguration(newConfig, resources.getDisplayMetrics());
        }
        return new MyContextWrapper(context);
    }
}


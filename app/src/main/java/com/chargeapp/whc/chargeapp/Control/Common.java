package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Wang on 2017/11/19.
 */

public class Common {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

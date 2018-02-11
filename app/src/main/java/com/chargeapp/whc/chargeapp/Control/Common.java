package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.chargeapp.whc.chargeapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wang on 2017/11/19.
 */

public class Common {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    public static SimpleAdapter setGridViewAdapt(Context context) {
        HashMap item;
        ArrayList items = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < MainActivity.imageAll.length; i++) {
            item = new HashMap<String, Object>();
            item.put("image", MainActivity.imageAll[i]);
            item.put("text", " ");
            items.add(item);
        }
        SimpleAdapter adapter = new SimpleAdapter(context,
                items, R.layout.main_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        return adapter;
    }

}

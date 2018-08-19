package com.chargeapp.whc.chargeapp.Control;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;

import com.chargeapp.whc.chargeapp.ChargeDB.SetupDateBase64;
import com.chargeapp.whc.chargeapp.R;

/**
 * Created by Wang on 2018/1/3.
 */

public class PermissionFragment extends DialogFragment implements  DialogInterface.OnClickListener{


    private Object object;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }


    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.d("ABSDIALOGFRAG", "Exception", e);
        }
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        try {
            FragmentTransaction ft = transaction;
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.d("ABSDIALOGFRAG", "Exception", e);
        }
        return super.show(transaction, tag);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message;
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)||ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            message="沒有儲存和讀取權限!\n如果要使用此軟體請按\"YES\"，並允許權限!\n不使用本軟體請按\"NO\"。";
        } else {
            message="沒有儲存和讀取權限!\n如果要使用此軟體請按\"YES\"。\n請到權限，打開儲存允許!\n不使用本軟體請按\"NO\"。";
        }
        String title="<font color=\"red\">無法使用</font>";
        return new AlertDialog.Builder(getActivity())
                .setTitle(Html.fromHtml(title))
                .setIcon(R.drawable.warning)
                .setMessage(message)
                .setPositiveButton("YES", this)
                .setNegativeButton("NO", this)
                .create();
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                Download download= (Download) object;
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)||ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    download.askPermissions();
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    this.startActivity(intent);
                    download.finish();
                }
                break;
            default:
                Download d= (Download) object;
                d.finish();
                break;
        }
    }
}

package com.chargeapp.whc.chargeapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chargeapp.whc.chargeapp.ChargeDB.SetupDateBase64;
import com.chargeapp.whc.chargeapp.Control.EleInvoice.EleDonate;

/**
 * Created by Wang on 2018/1/3.
 */

public class AlertDialogFragment extends DialogFragment implements  DialogInterface.OnClickListener{


    private Object object;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Activity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            activity= (Activity) context;
        }else {
            activity=getActivity();
        }
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
        String message="載具 : "+ EleDonate.carrierVO.getCarNul()+"\n";
        String title="<font color=\"white\">確定要捐獻給</font><br><font color=\"red\">"+EleDonate.teamTitle+"?</font>";
        for(String s: EleDonate.donateMap.keySet())
        {
            message=message+s+" X 1\n";
        }
        message=message+"總共 : "+EleDonate.donateMap.size()+" 張";
        return new AlertDialog.Builder(getActivity())
                .setTitle(Html.fromHtml(title))
                .setIcon(null)
                .setMessage(message)
                .setPositiveButton("YES", this)
                .setNegativeButton("NO", this)
                .create();
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                EleDonate eleDonate= (EleDonate) object;
                eleDonate.showDialog();
                new SetupDateBase64(eleDonate,activity).execute("DonateTeam");
                break;
            default:
                dialog.cancel();
                break;
        }
    }
}

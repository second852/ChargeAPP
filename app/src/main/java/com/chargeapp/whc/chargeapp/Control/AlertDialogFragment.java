package com.chargeapp.whc.chargeapp.Control;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Html;


import com.chargeapp.whc.chargeapp.ChargeDB.SetupDateBase64;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message="載具 : "+EleDonate.carrierVO.getCarNul()+"\n";
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
                new SetupDateBase64(eleDonate).execute("DonateTeam");
                break;
            default:
                dialog.cancel();
                break;
        }
    }
}

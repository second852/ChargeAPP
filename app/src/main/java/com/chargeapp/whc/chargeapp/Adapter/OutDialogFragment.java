package com.chargeapp.whc.chargeapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;


import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.chargeapp.whc.chargeapp.R;

/**
 * Created by Wang on 2018/1/3.
 */

public class OutDialogFragment extends DialogFragment implements  DialogInterface.OnClickListener{

    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            activity=(Activity) context;
        }else{
            activity=getActivity();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message="";
        String title="確定要離開?";

        return new AlertDialog.Builder(getActivity())
                .setTitle(Html.fromHtml(title))
                .setIcon(R.mipmap.ele_book)
                .setMessage(message)
                .setPositiveButton("YES", this)
                .setNegativeButton("NO", this)
                .create();
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                getActivity().finish();
                System.exit(0);
                break;
            default:
                dialog.cancel();
                break;
        }
    }
}

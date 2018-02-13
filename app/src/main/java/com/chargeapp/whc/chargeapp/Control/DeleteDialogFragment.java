package com.chargeapp.whc.chargeapp.Control;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;

import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.SetupDateBase64;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;

/**
 * Created by Wang on 2018/1/3.
 */

public class DeleteDialogFragment extends DialogFragment implements  DialogInterface.OnClickListener{

    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private Object object;
    private Fragment fragement;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Fragment getFragement() {
        return fragement;
    }

    public void setFragement(Fragment fragement) {
        this.fragement = fragement;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message;
        consumeDB=new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        if(object instanceof InvoiceVO)
        {
            InvoiceVO I= (InvoiceVO) object;
            message=I.getSecondtype()+" "+I.getAmount()+"元";
        }else {
            ConsumeVO c= (ConsumeVO) object;
            message=c.getSecondType()+" "+c.getMoney()+"元";
        }
        String title="確定要刪除這筆資料?";
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

                if(object instanceof InvoiceVO)
                {
                    InvoiceVO I= (InvoiceVO) object;
                    invoiceDB.deleteById(I.getId());
                }else {
                    ConsumeVO c= (ConsumeVO) object;
                    consumeDB.deleteById(c.getId());
                }
                if(fragement instanceof SelectShowCircleDe)
                {
                    SelectShowCircleDe selectShowCircleDe= (SelectShowCircleDe) fragement;
                    selectShowCircleDe.setLayout();
                }else{
                    SelectDetList selectDetList= (SelectDetList) fragement;
                    selectDetList.setLayout();
                }
                break;
            default:
                dialog.cancel();
                break;
        }
    }
}

package com.chargeapp.whc.chargeapp.Control;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.SetupDateBase64;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;

import java.util.Map;

/**
 * Created by Wang on 2018/1/3.
 */

public class DeleteDialogFragment extends DialogFragment implements  DialogInterface.OnClickListener{

    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private BankDB bankDB;
    private GoalDB goalDB;
    private Object object;
    private Fragment fragement;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private BankTybeDB bankTybeDB;

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
        String message=null;
        consumeDB=new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        goalDB=new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDB=new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB=new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankTybeDB=new BankTybeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        String title="確定要刪除這筆資料?";
        if(object instanceof InvoiceVO)
        {
            InvoiceVO I= (InvoiceVO) object;
            message=I.getSecondtype()+" "+I.getAmount()+"元";
        }else if(object instanceof ConsumeVO){
            ConsumeVO c= (ConsumeVO) object;
            message=c.getSecondType()+" "+c.getMoney()+"元";
        }else if(object instanceof BankVO){
            BankVO b= (BankVO) object;
            message=b.getMaintype()+" "+b.getMoney()+"元";
        }else if(object instanceof GoalVO){
            GoalVO b= (GoalVO) object;
            message=b.getName();
        }else if(fragement instanceof SettingMain)
        {
            title="確定重設資料庫?";
        }else if(object instanceof TypeVO)
        {
            TypeVO typeVO= (TypeVO) object;
            message=typeVO.getName();
        }else if(object instanceof TypeDetailVO)
        {
            TypeDetailVO typeDetailVO= (TypeDetailVO) object;
            message=typeDetailVO.getName();
        }else if(object instanceof BankTypeVO)
        {
            BankTypeVO bankTypeVO= (BankTypeVO) object;
            message=bankTypeVO.getName();
        }

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
                }else if(object instanceof ConsumeVO){
                    ConsumeVO c= (ConsumeVO) object;
                    consumeDB.deleteById(c.getId());
                }else if(object instanceof BankVO)
                {
                    BankVO bankVO = (BankVO) object;
                    bankDB.deleteById(bankVO.getId());
                }else if(object instanceof GoalVO)
                {
                    GoalVO goalVO = (GoalVO) object;
                    goalDB.deleteById(goalVO.getId());
                }else if(object instanceof TypeVO)
                {
                    TypeVO typeVO= (TypeVO) object;
                    typeDetailDB.deleteTypeDetailByName(typeVO.getName());
                    typeDB.deleteById(typeVO.getId());
                }else if(object instanceof TypeDetailVO)
                {
                    TypeDetailVO typeDetailVO= (TypeDetailVO) object;
                    typeDetailDB.deleteTypeDetailById(typeDetailVO.getId());
                }else if(object instanceof BankTypeVO)
                {
                    BankTypeVO bankTypeVO= (BankTypeVO) object;
                    bankDB.deleteById(bankTypeVO.getId());
                }


                if(fragement instanceof SelectShowCircleDe)
                {
                    SelectShowCircleDe selectShowCircleDe= (SelectShowCircleDe) fragement;
                    selectShowCircleDe.setLayout();
                }else if(fragement instanceof SelectDetList){
                    SelectDetList selectDetList= (SelectDetList) fragement;
                    selectDetList.setLayout();
                }else if(fragement instanceof SelectListModelCom){
                    SelectListModelCom selectListModelCom= (SelectListModelCom) fragement;
                    selectListModelCom.setLayout();
                }else if(fragement instanceof SelectListModelIM){
                    SelectListModelIM selectListModelIM= (SelectListModelIM) fragement;
                    selectListModelIM.setLayout();
                }else if(fragement instanceof GoalListAll){
                    GoalListAll goalListAll= (GoalListAll) fragement;
                    goalListAll.setLayout();
                }else if(fragement instanceof SettingMain)
                {
                    getActivity().deleteDatabase("ChargeAPP");
                    new MainActivity().setdate();
                    Common.showToast(getActivity(),"重置成功!");
                }else if(fragement instanceof SettingListType)
                {
                   SettingListType settingListType= (SettingListType) fragement;
                   settingListType.setLayout();
                }

                break;
            default:
                dialog.cancel();
                break;
        }
    }
}

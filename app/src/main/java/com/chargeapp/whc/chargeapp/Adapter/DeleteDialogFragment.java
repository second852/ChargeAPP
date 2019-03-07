package com.chargeapp.whc.chargeapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ElePeriodDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.EleInvoice.EleSetCarrier;
import com.chargeapp.whc.chargeapp.Control.Goal.GoalListAll;
import com.chargeapp.whc.chargeapp.Control.HomePage.HomePagetList;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.SelectList.SelectListModelCom;
import com.chargeapp.whc.chargeapp.Control.SelectList.SelectListModelIM;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectDetList;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectListBarIncome;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectListPieIncome;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectShowCircleDe;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectShowCircleDeList;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingListFixCon;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingListFixIon;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingListType;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingMain;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;

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
    private CarrierDB carrierDB;
    private ElePeriodDB elePeriodDB;
    private Activity activity;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }


    public void setFragement(Fragment fragement) {
        this.fragement = fragement;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            activity= (Activity) context;
        }else {
            activity=getActivity();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message=null;
        Common.setChargeDB(activity);
        consumeDB=new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        goalDB=new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDB=new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB=new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankTybeDB=new BankTybeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        elePeriodDB=new ElePeriodDB(MainActivity.chargeAPPDB.getReadableDatabase());
        String title="確定要刪除這筆資料?";
        if(object instanceof InvoiceVO)
        {
            InvoiceVO I= (InvoiceVO) object;
            message=I.getSecondtype()+" "+Common.getCurrency(I.getCarrier()) + I.getRealAmount();
        }else if(object instanceof ConsumeVO){
            ConsumeVO c= (ConsumeVO) object;
            message=c.getSecondType()+" "+Common.getCurrency(c.getCurrency())+c.getRealMoney();
        }else if(object instanceof BankVO){
            BankVO b= (BankVO) object;
            message=b.getMaintype()+" "+Common.getCurrency(b.getCurrency())+b.getRealMoney();
        }else if(object instanceof GoalVO){
            GoalVO b= (GoalVO) object;
            message=b.getName();
        }else if(fragement instanceof SettingMain)
        {
            title="確定重設資料庫?";
            message="重設後，紀錄資料會消失!";
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
        }else if(object instanceof CarrierVO)
        {
            CarrierVO carrierVO= (CarrierVO) object;
            message=carrierVO.getCarNul()+"相關資料會一併刪除!";
            title="確定要刪除載具?";
        }

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
                    bankTybeDB.deleteById(bankTypeVO.getId());
                }else if(object instanceof CarrierVO)
                {
                    CarrierVO carrierVO= (CarrierVO) object;
                    carrierDB.deleteByCarNul(carrierVO.getCarNul());
                    invoiceDB.deleteById(carrierVO.getCarNul());
                    elePeriodDB.deleteByCARNUL(carrierVO.getCarNul());
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
                    SettingMain settingMain= (SettingMain) fragement;
                    settingMain.deleteAll();
                    Common.showToast(getActivity(),"重置成功!");
                }else if(fragement instanceof SettingListType)
                {
                   SettingListType settingListType= (SettingListType) fragement;
                   settingListType.setLayout();
                }else if(fragement instanceof SettingListFixCon)
                {
                    SettingListFixCon settingListFixCon= (SettingListFixCon) fragement;
                    settingListFixCon.consumeVOS.remove(settingListFixCon.p);
                    settingListFixCon.setLayout();
                }else if(fragement instanceof SettingListFixIon)
                {
                    SettingListFixIon settingListFixIon= (SettingListFixIon) fragement;
                    settingListFixIon.bankVOS.remove(settingListFixIon.p);
                    settingListFixIon.setLayout();
                }else if(fragement instanceof EleSetCarrier)
                {
                    EleSetCarrier eleSetCarrier= (EleSetCarrier) fragement;
                    eleSetCarrier.setListAdapt();
                }else if(fragement instanceof SelectListBarIncome)
                {
                    SelectListBarIncome selectListBarIncome= (SelectListBarIncome) fragement;
                    selectListBarIncome.setLayout();
                }else if(fragement instanceof SelectListPieIncome)
                {
                    SelectListPieIncome selectListPieIncome= (SelectListPieIncome) fragement;
                    selectListPieIncome.setLayout();
                }else if(fragement instanceof HomePagetList)
                {
                    HomePagetList homePagetList= (HomePagetList) fragement;
                    homePagetList.setChoiceLayout();
                }else if(fragement instanceof SelectShowCircleDeList)
                {
                    SelectShowCircleDeList selectShowCircleDeList= (SelectShowCircleDeList) fragement;
                    selectShowCircleDeList.choiceLayout();
                }
                break;
            default:
                dialog.cancel();
                break;
        }
    }
}

package com.chargeapp.whc.chargeapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ElePeriodDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyFromDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.EleInvoice.EleSetCarrier;
import com.chargeapp.whc.chargeapp.Control.Goal.GoalListAll;
import com.chargeapp.whc.chargeapp.Control.HomePage.HomePagetList;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyList;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyMoneyList;
import com.chargeapp.whc.chargeapp.Control.SelectList.SelectListModelCom;
import com.chargeapp.whc.chargeapp.Control.SelectList.SelectListModelIM;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectDetList;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectListBarIncome;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectListPieIncome;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectShowCircleDe;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectShowCircleDeList;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingListFixCon;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingListFixIon;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingListFixProperty;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingListType;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingMain;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PropertyFromVO;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;

import org.jsoup.helper.StringUtil;

/**
 * Created by Wang on 2018/1/3.
 */

public class DeleteDialogFragment extends DialogFragment implements  DialogInterface.OnClickListener{

    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private BankDB bankDB;
    private GoalDB goalDB;
    private Object object;
    private Fragment fragment;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private BankTypeDB bankTypeDB;
    private CarrierDB carrierDB;
    private ElePeriodDB elePeriodDB;
    private Activity activity;
    private PropertyDB propertyDB;
    private PropertyFromDB propertyFromDB;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }


    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
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
        SQLiteOpenHelper database= MainActivity.chargeAPPDB;
        consumeDB=new ConsumeDB(database);
        invoiceDB=new InvoiceDB(database);
        bankDB=new BankDB(database);
        goalDB=new GoalDB(database);
        typeDB=new TypeDB(database);
        typeDetailDB=new TypeDetailDB(database);
        bankTypeDB =new BankTypeDB(database);
        carrierDB=new CarrierDB(database);
        elePeriodDB=new ElePeriodDB(database);
        propertyDB=new PropertyDB(database);
        propertyFromDB=new PropertyFromDB(database);
        String title="確定要刪除這筆資料?";
        if(object instanceof InvoiceVO)
        {
            InvoiceVO I= (InvoiceVO) object;
            message=I.getSecondtype()+" "+ Common.getCurrency(I.getCarrier()) + I.getRealAmount();
        }else if(object instanceof ConsumeVO){
            ConsumeVO c= (ConsumeVO) object;
            message=c.getSecondType()+" "+ Common.getCurrency(c.getCurrency())+c.getRealMoney();
        }else if(object instanceof BankVO){
            BankVO b= (BankVO) object;
            message=b.getMaintype()+" "+ Common.getCurrency(b.getCurrency())+b.getRealMoney();
        }else if(object instanceof GoalVO){
            GoalVO b= (GoalVO) object;
            message=b.getName();
        }else if(fragment instanceof SettingMain)
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
        }else if(object instanceof PropertyVO)
        {
            PropertyVO propertyVO= (PropertyVO) object;
            message=propertyVO.getName()+"相關資料會一併刪除!";
            title=propertyVO.getName();
        }else if(object instanceof PropertyFromVO)
        {
            PropertyFromVO propertyFromVO= (PropertyFromVO) object;
            if(StringUtil.isBlank(propertyFromVO.getSourceMainType()))
            {
                message=propertyFromVO.getSourceMainType();
            }else {
                message=propertyFromVO.getSourceSecondType();
            }
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
                    PropertyFromVO propertyFromVO=propertyFromDB.findByImportFeeId(c.getFkKey());
                    if(propertyFromVO!=null)
                    {
                       propertyFromVO.setImportFee("0");
                       propertyFromVO.setImportFeeId(null);
                       propertyFromDB.update(propertyFromVO);
                    }
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
                    bankTypeDB.deleteById(bankTypeVO.getId());
                }else if(object instanceof CarrierVO)
                {
                    CarrierVO carrierVO= (CarrierVO) object;
                    carrierDB.deleteByCarNul(carrierVO.getCarNul());
                    invoiceDB.deleteById(carrierVO.getCarNul());
                    elePeriodDB.deleteByCARNUL(carrierVO.getCarNul());
                }else if(object instanceof PropertyVO)
                {
                    PropertyVO propertyVO= (PropertyVO) object;
                    propertyFromDB.deleteByPropertyId(propertyVO.getId());
                    propertyDB.deleteById(propertyVO.getId());
                }else  if(object instanceof PropertyFromVO)
                {
                    PropertyFromVO propertyFromVO= (PropertyFromVO) object;
                    propertyFromDB.deleteById(propertyFromVO.getId());
                    if(propertyFromVO.getImportFeeId()!=null)
                    {
                        consumeDB.deleteByFk(propertyFromVO.getImportFeeId());
                    }
                }


                if(fragment instanceof SelectShowCircleDe)
                {
                    SelectShowCircleDe selectShowCircleDe= (SelectShowCircleDe) fragment;
                    selectShowCircleDe.setLayout();
                }else if(fragment instanceof SelectDetList){
                    SelectDetList selectDetList= (SelectDetList) fragment;
                    selectDetList.setLayout();
                }else if(fragment instanceof SelectListModelCom){
                    SelectListModelCom selectListModelCom= (SelectListModelCom) fragment;
                    selectListModelCom.setLayout();
                }else if(fragment instanceof SelectListModelIM){
                    SelectListModelIM selectListModelIM= (SelectListModelIM) fragment;
                    selectListModelIM.setLayout();
                }else if(fragment instanceof GoalListAll){
                    GoalListAll goalListAll= (GoalListAll) fragment;
                    goalListAll.setLayout();
                }else if(fragment instanceof SettingMain)
                {
                    SettingMain settingMain= (SettingMain) fragment;
                    settingMain.deleteAll();
                    Common.showToast(getActivity(),"重置成功!");
                }else if(fragment instanceof SettingListType)
                {
                   SettingListType settingListType= (SettingListType) fragment;
                   settingListType.setLayout();
                }else if(fragment instanceof SettingListFixCon)
                {
                    SettingListFixCon settingListFixCon= (SettingListFixCon) fragment;
                    settingListFixCon.consumeVOS.remove(settingListFixCon.p);
                    settingListFixCon.setLayout();
                }else if(fragment instanceof SettingListFixIon)
                {
                    SettingListFixIon settingListFixIon= (SettingListFixIon) fragment;
                    settingListFixIon.bankVOS.remove(settingListFixIon.p);
                    settingListFixIon.setLayout();
                }else if(fragment instanceof EleSetCarrier)
                {
                    EleSetCarrier eleSetCarrier= (EleSetCarrier) fragment;
                    eleSetCarrier.setListAdapt();
                }else if(fragment instanceof SelectListBarIncome)
                {
                    SelectListBarIncome selectListBarIncome= (SelectListBarIncome) fragment;
                    selectListBarIncome.setLayout();
                }else if(fragment instanceof SelectListPieIncome)
                {
                    SelectListPieIncome selectListPieIncome= (SelectListPieIncome) fragment;
                    selectListPieIncome.setLayout();
                }else if(fragment instanceof HomePagetList)
                {
                    HomePagetList homePagetList= (HomePagetList) fragment;
                    homePagetList.setChoiceLayout();
                }else if(fragment instanceof SelectShowCircleDeList)
                {
                    SelectShowCircleDeList selectShowCircleDeList= (SelectShowCircleDeList) fragment;
                    selectShowCircleDeList.choiceLayout();
                }else if(fragment instanceof PropertyList)
                {
                    PropertyList propertyList= (PropertyList) fragment;
                    propertyList.setAdapt();
                }else if(fragment instanceof PropertyMoneyList)
                {
                    PropertyMoneyList propertyMoneyList= (PropertyMoneyList) fragment;
                    propertyMoneyList.setListView();
                }else if(fragment instanceof SettingListFixProperty)
                {
                    SettingListFixProperty settingListFixProperty= (SettingListFixProperty) fragment;
                    settingListFixProperty.setLayout();
                }
                break;
            default:
                dialog.cancel();
                break;
        }
    }
}

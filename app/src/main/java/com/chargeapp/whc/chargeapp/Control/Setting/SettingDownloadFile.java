package com.chargeapp.whc.chargeapp.Control.Setting;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ElePeriodDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyFromDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Drobox.DbxRequestConfigFactory;
import com.chargeapp.whc.chargeapp.Drobox.DropboxClientFactory;
import com.chargeapp.whc.chargeapp.Drobox.FileActivity;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.EleMainItemVO;
import com.chargeapp.whc.chargeapp.Model.ElePeriod;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.Model.PropertyFromVO;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.TypeCode.FixDateCode;
import com.chargeapp.whc.chargeapp.TypeCode.PropertyType;
import com.chargeapp.whc.chargeapp.TypeCode.RequestCode;
import com.dropbox.core.DbxException;
import com.dropbox.core.android.Auth;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SettingDownloadFile extends Fragment {


    private String TAG="SettingDownloadFile";
    private ListView listView;
    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private BankDB bankDB;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private BankTypeDB bankTypeDB;
    private GoalDB goalDB;
    private CarrierDB carrierDB;
    private PriceDB priceDB;
    private ElePeriodDB elePeriodDB;
    private RelativeLayout progressL;
    private Activity context;
    private PropertyFromDB propertyFromDB;
    private PropertyDB propertyDB;
    private TextView percent;
    private BigDecimal hundred=new BigDecimal(100);
    private BigDecimal t,c;
    public static boolean dropboxOpen;

    //------生命週期

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.context=(Activity) context;
        }else{
            this.context=getActivity();
        }
        dropboxOpen=false;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context.setTitle("匯入檔案");
        View view = inflater.inflate(R.layout.setting_main, container, false);
        Common.setChargeDB(context);
        SQLiteOpenHelper sqLiteOpenHelper=MainActivity.chargeAPPDB;
        consumeDB = new ConsumeDB(sqLiteOpenHelper);
        invoiceDB = new InvoiceDB(sqLiteOpenHelper);
        bankDB = new BankDB(sqLiteOpenHelper);
        typeDB = new TypeDB(sqLiteOpenHelper);
        bankTypeDB = new BankTypeDB(sqLiteOpenHelper);
        typeDetailDB = new TypeDetailDB(sqLiteOpenHelper);
        goalDB = new GoalDB(sqLiteOpenHelper);
        carrierDB = new CarrierDB(sqLiteOpenHelper);
        priceDB=new PriceDB(sqLiteOpenHelper);
        elePeriodDB=new ElePeriodDB(sqLiteOpenHelper);
        propertyDB=new PropertyDB(sqLiteOpenHelper);
        propertyFromDB=new PropertyFromDB((sqLiteOpenHelper));
        List<EleMainItemVO> itemSon = getNewItem();
        listView = view.findViewById(R.id.list);
        percent=view.findViewById(R.id.percent);
        progressL = view.findViewById(R.id.progressL);
        listView.setAdapter(new ListAdapter(context, itemSon));
        progressL.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(dropboxOpen) {
            dropBoxAction();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RequestCode resultCodeEnum= RequestCode.getEnum(requestCode);
        switch (resultCodeEnum) {
            case Dropbox:
                progressL.setVisibility(View.GONE);
                if(FileActivity.result!=null){
                    FileInput fileInput=new FileInput(new ByteArrayInputStream(FileActivity.result));
                    new Thread(fileInput).start();
                }else{
                  Common.showToast(context,"無匯入檔案!");
                }
                break;
            case UpLoadLocalOpen:
                Uri uri =data.getData();
                try {
                    FileInput fileInput=new FileInput(context.getContentResolver().openInputStream(uri));
                    new Thread(fileInput).start();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    //--------view

    private List<EleMainItemVO> getNewItem() {
        List<EleMainItemVO> eleMainItemVOList = new ArrayList<>();
        eleMainItemVOList.add(new EleMainItemVO("從本機匯入資料", R.drawable.export,0));
//        eleMainItemVOList.add(new EleMainItemVO("從Google雲端匯入資料", R.drawable.export,1));
        eleMainItemVOList.add(new EleMainItemVO("從Dropbox匯入資料", R.drawable.export,2));
        return eleMainItemVOList;
    }

    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<EleMainItemVO> eleMainItemVOS;

        ListAdapter(Context context, List<EleMainItemVO> eleMainItemVOS) {
            this.context = context;
            this.eleMainItemVOS = eleMainItemVOS;
        }


        @Override
        public int getCount() {
            return eleMainItemVOS.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.setting_main_item, parent, false);
            }
            final EleMainItemVO eleMainItemVO = eleMainItemVOS.get(position);
            ImageView imageView = itemView.findViewById(R.id.image);
            TextView textView = itemView.findViewById(R.id.listTitle);
            imageView.setImageResource(eleMainItemVO.getImage());
            textView.setText(eleMainItemVO.getName());
            Switch notify = itemView.findViewById(R.id.notify);
            TextView setTime = itemView.findViewById(R.id.setTime);
            setTime.setVisibility(View.GONE);
            notify.setVisibility(View.GONE);
            if (eleMainItemVO.getCode() == 0) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("application/vnd.ms-excel");
                        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, MediaStore.VOLUME_EXTERNAL);
                        startActivityForResult(intent, RequestCode.UpLoadLocalOpen.getCode());
                    }
                });
            } else if (eleMainItemVO.getCode() == 2) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Auth.startOAuth2PKCE(context, getString(R.string.DropboxKey), DbxRequestConfigFactory.getRequestConfig(), DbxRequestConfigFactory.scope);
                        dropboxOpen=true;
                    }});
            }
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return eleMainItemVOS.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    //----dropbox 上傳
    private void dropBoxAction(){
        try {
            DropboxClientFactory.per(SettingDownloadFile.this.context);
            DropboxClientFactory.getClient();
        }catch (Exception e){
            dropboxOpen=false;
            Log.e(TAG, "dropBoxAction: ",e);
            Common.showToast(context,"讀取DropBox失敗!");
            return;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        progressL.setVisibility(View.VISIBLE);
        percent.setText("");
        Future<Boolean> future=executor.submit(dropboxCallBack);
        while (!future.isDone()){}
        executor.shutdown();
        Intent filesIntent = new Intent(context, FileActivity.class);
        startActivityForResult(filesIntent,RequestCode.Dropbox.getCode());
    }

    private Callable<Boolean> dropboxCallBack=new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            try {
                DropboxClientFactory.getClient().files().getPreview(FileActivity.mPath);
            } catch (DbxException e) {
                Log.d(TAG, e.getMessage());
                try {
                    DropboxClientFactory.getClient().files().createFolderV2(FileActivity.mPath);
                } catch (DbxException ex) {
                    Log.d(TAG, ex.getMessage());
                }
            }
            return true;
        }
    };


    //-----file
    public class FileInput implements Runnable{
        private InputStream inp;
        public FileInput(InputStream inp) {
            this.inp = inp;
        }

        @Override
        public void run() {
            try {
                Message message=handler.obtainMessage(5);
                message.sendToTarget();
                inputExcel(inp);
            } catch (Exception e) {
                Message message=handler.obtainMessage();
                message.what=3;
                message.sendToTarget();
            }
        }
    }


    private void inputExcel(InputStream inp) {
        Common.insertNewTableCol();
        int i = 0;
        Message msg = new Message();
        try {
            Workbook workbook = new HSSFWorkbook(inp);

            //count
            int total=0,count=0;
            setMessage(1,1);
            for (Sheet sheet : workbook) {
                total=sheet.getLastRowNum()+total;
            }

            for (Sheet sheet : workbook) {
                String sheetTitle = sheet.getSheetName();
                if ((!sheetTitle.equals("Type") && i == 0)) {
                    msg.what=2;
                    workbook.close();
                    inp.close();
                    return;
                }

                for (Row row : sheet) {
                    if (sheetTitle.equals("Type")) {
                        TypeVO typeVO = new TypeVO();
                        typeVO.setId((int) row.getCell(0).getNumericCellValue());
                        typeVO.setGroupNumber(row.getCell(1).getStringCellValue());
                        typeVO.setName(row.getCell(2).getStringCellValue());
                        typeVO.setImage((int) row.getCell(3).getNumericCellValue());
                        TypeVO oldTypeVO=typeDB.findTypeName(typeVO.getGroupNumber(),typeVO.getName());
                        if(oldTypeVO==null)
                        {
                            typeDB.insert(typeVO);
                        }else{
                            typeDB.update(typeVO);
                        }
                        setMessage(count++,total);
                    } else if (sheetTitle.equals("TypeDetail")) {

                        TypeDetailVO typeDetailVO = new TypeDetailVO();
                        typeDetailVO.setId((int) row.getCell(0).getNumericCellValue());
                        typeDetailVO.setGroupNumber(row.getCell(1).getStringCellValue());
                        typeDetailVO.setName(row.getCell(2).getStringCellValue());
                        typeDetailVO.setImage((int) row.getCell(3).getNumericCellValue());
                        typeDetailVO.setKeyword(row.getCell(4).getStringCellValue());
                        TypeDetailVO oldTypeDetail=typeDetailDB.findByname(typeDetailVO.getName(),typeDetailVO.getGroupNumber());
                        if(oldTypeDetail==null)
                        {
                            typeDetailDB.insert(typeDetailVO);

                        }else{
                            typeDetailDB.update(typeDetailVO);
                        }
                        setMessage(count++,total);
                    } else if (sheetTitle.equals("BankType")) {
                        BankTypeVO bankTypeVO = new BankTypeVO();
                        bankTypeVO.setId((int) row.getCell(0).getNumericCellValue());
                        bankTypeVO.setGroupNumber(row.getCell(1).getStringCellValue());
                        bankTypeVO.setName(row.getCell(2).getStringCellValue());
                        bankTypeVO.setImage((int) row.getCell(3).getNumericCellValue());
                        BankTypeVO oldBankTypeVO= bankTypeDB.findExist(bankTypeVO.getGroupNumber(),bankTypeVO.getName());
                        if(oldBankTypeVO==null)
                        {
                            bankTypeDB.insert(bankTypeVO);
                        }else{
                            bankTypeDB.update(bankTypeVO);
                        }
                        setMessage(count++,total);
                    } else if (sheetTitle.equals("Goal")) {
                        GoalVO goalVO = new GoalVO();
                        goalVO.setId((int) row.getCell(0).getNumericCellValue());
                        goalVO.setType(row.getCell(1).getStringCellValue());
                        goalVO.setName(row.getCell(2).getStringCellValue());
                        goalVO.setRealMoney(row.getCell(3).getStringCellValue());
                        goalVO.setTimeStatue(row.getCell(4).getStringCellValue());
                        goalVO.setStartTime(new java.sql.Date((long) row.getCell(5).getNumericCellValue()));
                        goalVO.setEndTime(new java.sql.Date((long) row.getCell(6).getNumericCellValue()));
                        goalVO.setNotify(row.getCell(7).getBooleanCellValue());
                        goalVO.setNotifyStatue(row.getCell(8).getStringCellValue());
                        goalVO.setNotifyDate(row.getCell(9).getStringCellValue());
                        goalVO.setNoWeekend(row.getCell(10).getBooleanCellValue());
                        goalVO.setStatue((int) row.getCell(11).getNumericCellValue());

                        try {
                            goalVO.setCurrency(row.getCell(12).getStringCellValue());
                        }catch (Exception e)
                        {
                            goalVO.setCurrency("TWD");
                        }

                        GoalVO oldGoalVO=goalDB.getFindType(goalVO.getType(),goalVO.getName());
                        if(oldGoalVO==null)
                        {
                            goalDB.insert(goalVO);
                        }
                        setMessage(count++,total);
                    } else if (sheetTitle.equals("Bank")) {
                        BankVO bankVO = new BankVO();
                        bankVO.setId((int) row.getCell(0).getNumericCellValue());
                        bankVO.setMaintype(row.getCell(1).getStringCellValue());
                        bankVO.setRealMoney(row.getCell(2).getStringCellValue());
                        bankVO.setDate(new java.sql.Date((long) row.getCell(3).getNumericCellValue()));
                        bankVO.setFixDate(row.getCell(4).getStringCellValue());
                        bankVO.setFixDateDetail(row.getCell(5).getStringCellValue());
                        bankVO.setDetailname(row.getCell(6).getStringCellValue());
                        bankVO.setAuto(row.getCell(7).getBooleanCellValue());
                        bankVO.setAutoId((int) row.getCell(8).getNumericCellValue());
                        try {
                            bankVO.setCurrency(row.getCell(9).getStringCellValue());
                        }catch (Exception e)
                        {
                            bankVO.setCurrency("TWD");
                        }
                        bankVO.setFkKey(row.getCell(10).getStringCellValue());
                        BankVO oldBankVO=bankDB.getFindOldBank(bankVO);
                        if(oldBankVO==null)
                        {
                            bankDB.insert(bankVO);
                        }
                        setMessage(count++,total);
                    } else if (sheetTitle.equals("Consume")) {
                        ConsumeVO consumeVO = new ConsumeVO();
                        consumeVO.setId((int) row.getCell(0).getNumericCellValue());
                        consumeVO.setMaintype(row.getCell(1).getStringCellValue());
                        consumeVO.setSecondType(row.getCell(2).getStringCellValue());
                        consumeVO.setRealMoney(row.getCell(3).getStringCellValue());
                        consumeVO.setDate(new java.sql.Date((long) row.getCell(4).getNumericCellValue()));
                        consumeVO.setNumber(row.getCell(5).getStringCellValue());
                        consumeVO.setFixDate(row.getCell(6).getStringCellValue());
                        consumeVO.setFixDateDetail(row.getCell(7).getStringCellValue());
                        consumeVO.setNotify(row.getCell(8).getStringCellValue());
                        consumeVO.setDetailname(row.getCell(9).getStringCellValue());
                        consumeVO.setAuto(row.getCell(10).getBooleanCellValue());
                        consumeVO.setAutoId((int) row.getCell(11).getNumericCellValue());
                        consumeVO.setIsWin(row.getCell(12).getStringCellValue());
                        consumeVO.setIsWinNul(row.getCell(13).getStringCellValue());
                        try {
                            consumeVO.setRdNumber(row.getCell(14).getStringCellValue());
                        }catch (Exception e)
                        {
                            consumeVO.setRdNumber(null);
                        }

                        try {
                            consumeVO.setCurrency(row.getCell(15).getStringCellValue());
                        }catch (Exception e)
                        {
                            consumeVO.setCurrency("TWD");
                        }
                        consumeVO.setFkKey(row.getCell(16).getStringCellValue());

                        ConsumeVO oldConsume=consumeDB.findOldCon(consumeVO);
                        if(oldConsume==null)
                        {
                            consumeDB.insert(consumeVO);
                        }
                        setMessage(count++,total);
                    } else if (sheetTitle.equals("Invoice")) {
                        InvoiceVO invoiceVO = new InvoiceVO();
                        invoiceVO.setId((int) row.getCell(0).getNumericCellValue());
                        invoiceVO.setInvNum(row.getCell(1).getStringCellValue());
                        invoiceVO.setCardType(row.getCell(2).getStringCellValue());
                        invoiceVO.setCardNo(row.getCell(3).getStringCellValue());
                        invoiceVO.setCardEncrypt(row.getCell(4).getStringCellValue());
                        invoiceVO.setTime(new Timestamp((long) row.getCell(5).getNumericCellValue()));
                        invoiceVO.setRealAmount(row.getCell(6).getStringCellValue());
                        invoiceVO.setDetail(row.getCell(7).getStringCellValue());
                        invoiceVO.setInvDonatable(row.getCell(8).getStringCellValue());
                        invoiceVO.setDonateMark(row.getCell(9).getStringCellValue());
                        invoiceVO.setCarrier(row.getCell(10).getStringCellValue());
                        invoiceVO.setMaintype(row.getCell(11).getStringCellValue());
                        invoiceVO.setSecondtype(row.getCell(12).getStringCellValue());
                        try {
                            String value=row.getCell(13).getStringCellValue();
                            invoiceVO.setHeartyteam(value.trim().length()>0?value:null);
                        }catch (Exception e)
                        {
                            invoiceVO.setHeartyteam(null);
                        }
                        invoiceVO.setDonateTime(new Timestamp((long) row.getCell(14).getNumericCellValue()));
                        invoiceVO.setSellerBan(row.getCell(15).getStringCellValue());
                        invoiceVO.setSellerName(row.getCell(16).getStringCellValue());
                        invoiceVO.setSellerAddress(row.getCell(17).getStringCellValue());
                        invoiceVO.setIswin(row.getCell(18).getStringCellValue());
                        invoiceVO.setIsWinNul(row.getCell(19).getStringCellValue());

                        try {
                            invoiceVO.setCurrency(row.getCell(20).getStringCellValue());
                        }catch (Exception e)
                        {
                            invoiceVO.setCurrency("TWD");

                        }

                        InvoiceVO oldInvoiceVO=invoiceDB.findOldInvoiceVO(invoiceVO);
                        if(oldInvoiceVO==null)
                        {
                            invoiceDB.insert(invoiceVO);
                        }
                        setMessage(count++,total);
                    } else if (sheetTitle.equals("Carrier")) {
                        CarrierVO carrierVO = new CarrierVO();
                        carrierVO.setId((int) row.getCell(0).getNumericCellValue());
                        carrierVO.setCarNul(row.getCell(1).getStringCellValue());
                        carrierVO.setPassword(row.getCell(2).getStringCellValue());
                        CarrierVO oldCarrierVO=carrierDB.findOldCarrier(carrierVO);
                        if(oldCarrierVO==null)
                        {
                            carrierDB.insert(carrierVO);
                        }
                        setMessage(count++,total);
                    }else if (sheetTitle.equals("Price")) {
                        PriceVO priceVO = new PriceVO();
                        priceVO.setInVoYm(row.getCell(0).getStringCellValue());
                        priceVO.setSuperPrizeNo(row.getCell(1).getStringCellValue());
                        priceVO.setSpcPrizeNo(row.getCell(2).getStringCellValue());
                        priceVO.setFirstPrizeNo1(row.getCell(3).getStringCellValue());
                        priceVO.setFirstPrizeNo2(row.getCell(4).getStringCellValue());
                        priceVO.setFirstPrizeNo3(row.getCell(5).getStringCellValue());
                        priceVO.setSixthPrizeNo1(row.getCell(6).getStringCellValue());
                        priceVO.setSixthPrizeNo2(row.getCell(7).getStringCellValue());
                        priceVO.setSixthPrizeNo3(row.getCell(8).getStringCellValue());
                        priceVO.setSuperPrizeAmt(row.getCell(9).getStringCellValue());
                        priceVO.setSpcPrizeAmt(row.getCell(10).getStringCellValue());
                        priceVO.setFirstPrizeAmt(row.getCell(11).getStringCellValue());
                        priceVO.setSecondPrizeAmt(row.getCell(12).getStringCellValue());
                        priceVO.setThirdPrizeAmt(row.getCell(13).getStringCellValue());
                        priceVO.setFourthPrizeAmt(row.getCell(14).getStringCellValue());
                        priceVO.setFifthPrizeAmt(row.getCell(15).getStringCellValue());
                        priceVO.setSixthPrizeAmt(row.getCell(16).getStringCellValue());
                        priceVO.setSixthPrizeNo4(row.getCell(17).getStringCellValue());
                        priceVO.setSixthPrizeNo5(row.getCell(18).getStringCellValue());
                        priceVO.setSixthPrizeNo6(row.getCell(19).getStringCellValue());
                        PriceVO oldPriceVO=priceDB.getPeriodAll(priceVO.getInVoYm());
                        if(oldPriceVO==null)
                        {
                            priceDB.insert(priceVO);
                        }
                        setMessage(count++,total);
                    }else if (sheetTitle.equals("ElePeriod")) {
                        ElePeriod elePeriod = new ElePeriod();
                        elePeriod.setId((int) row.getCell(0).getNumericCellValue());
                        elePeriod.setCarNul(row.getCell(1).getStringCellValue());
                        elePeriod.setYear((int) row.getCell(2).getNumericCellValue());
                        elePeriod.setMonth((int) row.getCell(3).getNumericCellValue());
                        elePeriod.setDownload(row.getCell(4).getBooleanCellValue());
                        ElePeriod oldElePeriod=elePeriodDB.OldElePeriod(elePeriod);
                        if(oldElePeriod==null)
                        {
                            elePeriodDB.insert(elePeriod);
                        }
                        setMessage(count++,total);
                    }else if (sheetTitle.equals("Property")) {
                        PropertyVO propertyVO=new PropertyVO();
                        propertyVO.setId(row.getCell(0).getStringCellValue());
                        propertyVO.setCurrency(row.getCell(1).getStringCellValue());
                        propertyVO.setName(row.getCell(2).getStringCellValue());
                        PropertyVO old=propertyDB.findById(propertyVO.getId());
                        if(old==null)
                        {
                            propertyDB.insert(propertyVO);
                        }

                        setMessage(count++,total);
                    }else if (sheetTitle.equals("PropertyFrom")) {
                        PropertyFromVO propertyFromVO=new PropertyFromVO();
                        propertyFromVO.setId(row.getCell(0).getStringCellValue());
                        propertyFromVO.setType(PropertyType.codeToEnum((int) row.getCell(1).getNumericCellValue()));
                        propertyFromVO.setSourceMoney(row.getCell(2).getStringCellValue());
                        propertyFromVO.setSourceCurrency(row.getCell(3).getStringCellValue());
                        propertyFromVO.setSourceMainType(row.getCell(4).getStringCellValue());
                        propertyFromVO.setSourceSecondType(row.getCell(5).getStringCellValue());
                        propertyFromVO.setSourceTime(new Date((long) row.getCell(6).getNumericCellValue()));
                        propertyFromVO.setImportFee(row.getCell(7).getStringCellValue());
                        propertyFromVO.setImportFeeId(row.getCell(8).getStringCellValue());
                        propertyFromVO.setFixImport(row.getCell(9).getBooleanCellValue());
                        propertyFromVO.setFixDateCode(FixDateCode.detailToEnum(row.getCell(10).getStringCellValue()));
                        propertyFromVO.setFixDateDetail(row.getCell(11).getStringCellValue());
                        propertyFromVO.setPropertyId( row.getCell(12).getStringCellValue());
                        propertyFromVO.setFixFromId(row.getCell(13).getStringCellValue());
                        PropertyFromVO old=propertyFromDB.findByPropertyFromId(propertyFromVO.getId());
                        if(old==null)
                        {
                            propertyFromDB.insert(propertyFromVO);
                        }

                        setMessage(count++,total);
                    }else if (sheetTitle.equals("Setting")) {
                        boolean notify=  Boolean.valueOf(row.getCell(0).getStringCellValue());
                        boolean autoCategory=Boolean.valueOf(row.getCell(1).getStringCellValue());
                        SharedPreferences sharedPreferences=context.getSharedPreferences("Charge_User",Context.MODE_PRIVATE);
                        sharedPreferences.edit().putBoolean("notify", notify).apply();
                        sharedPreferences.edit().putBoolean("autoCategory", autoCategory).apply();
                    }
                }
                i++;
            }
            msg.what=0;
            setMessage(total,total);
            workbook.close();
            Common.correctErrorData();
        } catch (Exception e) {
            msg.what=1;
            Log.d("xxxxxxxx", e.toString());
            e.printStackTrace();
        }finally {
            try {
                if(inp!=null){
                    inp.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
        }
    }

    Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Common.showToast(context,"匯入成功");
                    break;
                case 1:
                    Common.showToast(context,"檔案格式不對");
                    break;
                case 2:
                    Common.showToast(context, "不是備份檔");
                    break;
                case 3:
                    Common.showToast(context, "請將檔案放置在/Download，檔名為記帳小助手.xls");
                    break;
                case 4:
                    percent.setText((String) msg.obj);
                    break;
                case 5:
                    progressL.setVisibility(View.VISIBLE);
                    percent.setText("0%");
                    break;
            }
            if(msg.what!=4&&msg.what!=5)
            {
                progressL.setVisibility(View.GONE);
            }
        }
    };


    private void setMessage(int count,int total)
    {
        if(count>=total)
        {
            count=total;
        }
        Message message=new Message();
        message.what=4;
        c=new BigDecimal(count);
        t=new BigDecimal(total);
        message.obj= c.divide(t,4,RoundingMode.HALF_UP).multiply(hundred).setScale(1,BigDecimal.ROUND_HALF_UP)+"%";
        handler.sendMessage(message);
    }


}
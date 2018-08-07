package com.chargeapp.whc.chargeapp.Control;



import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ElePeriodDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.EleMainItemVO;
import com.chargeapp.whc.chargeapp.Model.ElePeriod;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SettingUploadFile extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private ListView listView;
    private LinearLayout fileChoice;
    private ImageView excel, txtFile, cancelF;
    private Spinner choiceT;
    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private BankDB bankDB;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private BankTybeDB bankTybeDB;
    private GoalDB goalDB;
    private CarrierDB carrierDB;
    private PriceDB priceDB;
    public static int position;
    private boolean local, consume, income, all, show = true, txt;
    private GoogleApiClient mGoogleApiClient;
    private RelativeLayout progressL;
    private ElePeriodDB elePeriodDB;
    private Activity context;
    private Type cdType;
    private Gson gson;
    private StringBuffer sb;
    private AdView adView;
    private boolean firstEnter;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0:
                    progressL.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    progressL.setVisibility(View.GONE);
                    break;
                case 2:
                    progressL.setVisibility(View.GONE);
                    Common.showToast(context, "匯出成功，檔名為記帳小助手.txt，路徑為" + "/Download/記帳小助手.txt");
                    break;
                case 3:
                    progressL.setVisibility(View.GONE);
                    Common.showToast(context,"輸出失敗");
                    break;
                case 4:
                    progressL.setVisibility(View.GONE);
                    Common.showToast(context,"匯出成功，檔名為記帳小助手.xls，路徑為" + "/Download/記帳小助手.xls");
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.context=(Activity) context;
        }else{
            this.context=getActivity();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context.setTitle("匯出檔案");
        View view = inflater.inflate(R.layout.setting_upload, container, false);
        Common.setChargeDB(context);
        cdType = new TypeToken<List<JsonObject>>() {}.getType();
        gson=new Gson();
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankDB = new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankTybeDB = new BankTybeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        goalDB = new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        elePeriodDB=new ElePeriodDB(MainActivity.chargeAPPDB.getReadableDatabase());
        priceDB=new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        List<EleMainItemVO> itemSon = getNewItem();
        listView = view.findViewById(R.id.list);
        fileChoice = view.findViewById(R.id.fileChoice);
        excel = view.findViewById(R.id.excel);
        txtFile = view.findViewById(R.id.txtFile);
        cancelF = view.findViewById(R.id.cancelF);
        choiceT = view.findViewById(R.id.choiceT);
        progressL=view.findViewById(R.id.progressL);
        //廣告
        adView = view.findViewById(R.id.adView);
        Common.setAdView(adView,context);

        excel.setOnClickListener(new excelOnClick());
        txtFile.setOnClickListener(new txtOnClick());
        cancelF.setOnClickListener(new cancelOnClick());
        listView.setAdapter(new ListAdapter(context, itemSon));
        setSpinner();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        int rc = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (rc != PackageManager.PERMISSION_GRANTED) {
            Common.askPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,context);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0)
        {
            txt=false;
            openCloud();
        }else if(requestCode==1){
            txt=true;
            openCloud();
        }else if(requestCode==2){
            txt=false;
            openCloud();
        }else if(requestCode==3){
            progressL.setVisibility(View.GONE);
            if (resultCode == -1) {
                Common.showToast(context, "上傳成功");
            } else {
                Common.showToast(context, "上傳失敗");
            }
        }
    }


    private void setSpinner() {
        ArrayList<String> spinnerItem = new ArrayList();
        spinnerItem.add("備分資料庫");
        spinnerItem.add("全部");
        spinnerItem.add("支出");
        spinnerItem.add("收入");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinneritem, spinnerItem);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        choiceT.setAdapter(arrayAdapter);
        choiceT.setOnItemSelectedListener(new choiceAction());
        choiceT.setSelection(position);
    }

    private List<EleMainItemVO> getNewItem() {
        List<EleMainItemVO> eleMainItemVOList = new ArrayList<>();
        eleMainItemVOList.add(new EleMainItemVO("匯出資料到本機", R.drawable.importf));
        eleMainItemVOList.add(new EleMainItemVO("匯出資料到Google雲端", R.drawable.importf));
        return eleMainItemVOList;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        saveFileToDrive();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(context, result.getErrorCode(), 0).show();
            return;
        }
        // Called typically when the app is not yet authorized, and authorization dialog is displayed to the user.
       if(!firstEnter)
       {
           Common.showToast(context, "登入失敗");
           progressL.setVisibility(View.GONE);
           return;
       }
        firstEnter=false;
        try {
            if(position==0)
            {
                result.startResolutionForResult(context, 0);
            }
            else
            {
                if(txt)
                {
                    result.startResolutionForResult(context, 1);
                }else{
                    result.startResolutionForResult(context, 2);
                }
            }
        } catch (IntentSender.SendIntentException e) {

        }
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
            if (position == 0) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        local = true;
                        if (SettingUploadFile.this.position == 0) {
                            FileTOLocal();
                        } else {
                            if (show) {
                                fileChoice.setVisibility(View.VISIBLE);
                                show = false;
                            }
                        }
                    }
                });

            } else if (position == 1) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        local = false;
                        firstEnter=true;
                        if (SettingUploadFile.this.position == 0) {
                            txt = false;
                            openCloud();
                        } else {
                            if (show) {
                                fileChoice.setVisibility(View.VISIBLE);
                                show = false;
                            }
                        }
                    }
                });
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

    private void TxtToLocal() {
        progressL.setVisibility(View.VISIBLE);
        Runnable runnable=new Runnable() {
            @Override
            public void run() {

                try {
                    File dir = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, "記帳小助手.txt");
                    FileOutputStream fs = new FileOutputStream(file);
                    OutPutTxt(fs);
                } catch (Exception e) {
                    Message message=handler.obtainMessage();
                    message.what=3;
                    message.sendToTarget();
                }
            }
        };
        new Thread(runnable).start();
    }

    private void OutPutTxt(OutputStream outputStream) {
        try {
            OutputStreamWriter ow = new OutputStreamWriter(outputStream);
            BufferedWriter bw = new BufferedWriter(ow);
            if (consume) {
                bw.write("消費資料");
                bw.write("\r\n");
                bw.append("日期 ");
                bw.append("主項目 ");
                bw.append("次項目 ");
                bw.append("金額 ");
                bw.append("發票號碼 ");
                bw.append("細節 ");
                bw.append("中獎 ");
                bw.append("類別 ");
                bw.newLine();
                bw.write("\r\n");
                List<ConsumeVO> consumeVOS = consumeDB.getAll();
                List<InvoiceVO> invoiceVOS = invoiceDB.getAll();
                List<Object> objects = new ArrayList<>();
                objects.addAll(consumeVOS);
                objects.addAll(invoiceVOS);
                //照時間排列
                Collections.sort(objects, new Comparator<Object>() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        long t1 = (o1 instanceof InvoiceVO) ? ((InvoiceVO) o1).getTime().getTime() : ((ConsumeVO) o1).getDate().getTime();
                        long t2 = (o2 instanceof InvoiceVO) ? ((InvoiceVO) o2).getTime().getTime() : ((ConsumeVO) o2).getDate().getTime();
                        if (t1 > t2) {
                            return -1;
                        } else if (t1 == t2) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                });

                //塞資料
                for (int i = 0; i < objects.size(); i++) {
                    Object o = objects.get(i);
                    if (o instanceof InvoiceVO) {
                        InvoiceVO invoiceVO = (InvoiceVO) o;
                        bw.append(Common.sTwo.format(new Date(invoiceVO.getTime().getTime())) + " ");
                        bw.append(Common.getType(invoiceVO.getMaintype()) + " ");
                        bw.append(Common.getType(invoiceVO.getSecondtype()) + " ");
                        bw.append(invoiceVO.getAmount() + " ");
                        bw.append(invoiceVO.getInvNum() + " ");
                        //電子發票細節
                        List<JsonObject> js = gson.fromJson(invoiceVO.getDetail(), cdType);
                        sb=new StringBuffer();
                        float price,amout,n;
                        for (JsonObject j : js) {
                            try {
                                amout=j.get("amount").getAsFloat();
                                n = j.get("quantity").getAsFloat();
                                price = j.get("unitPrice").getAsFloat();
                                if(price==0)
                                {
                                    sb.append(j.get("description").getAsString() + " : \n" + (int)(amout/n) + "X" + (int)n + "=" + (int)amout + "元\n");
                                }else{
                                    sb.append(j.get("description").getAsString() + " : \n" + (int)price + "X" + (int)n + "=" + (int)amout + "元\n");
                                }
                            } catch (Exception e) {
                                sb.append(j.get("description").getAsString() + " : \n" + 0 + "X" + 0 + "=" + 0 + "元\n");
                            }
                        }
                        bw.append(sb.toString());
                        //中獎訊息
                        try {
                            if(invoiceVO.getIswin().equals("0"))
                            {
                                bw.append("尚未兌獎");
                            }else if(invoiceVO.getIswin().equals("N")){
                                bw.append("無中獎");
                            }else {
                                bw.append(Common.getPriceName().get(invoiceVO.getIswin()));
                            }
                        }catch (Exception e)
                        {
                            bw.append("尚未兌獎");
                        }
                        bw.append("雲端發票" + " ");
                        bw.newLine();
                        bw.write("\r\n");
                    } else {
                        ConsumeVO consumeVO = (ConsumeVO) o;
                        bw.append(Common.sTwo.format(new Date(consumeVO.getDate().getTime())) + " ");
                        bw.append(consumeVO.getMaintype() + " ");
                        bw.append(consumeVO.getSecondType() + " ");
                        bw.append(consumeVO.getMoney() + " ");
                        bw.append(consumeVO.getNumber() + " ");
                        bw.append((consumeVO.getDetailname()==null?"":consumeVO.getDetailname()) + " ");
                        //中獎訊息
                        try {
                            if(consumeVO.getIsWin().equals("0"))
                            {
                                bw.append("尚未兌獎");
                            }else if(consumeVO.getIsWin().equals("N")){
                                bw.append("無中獎");
                            }else {
                                bw.append(Common.getPriceName().get(consumeVO.getIsWin()));
                            }
                        }catch (Exception e)
                        {
                            bw.append("尚未兌獎");
                        }
                        if(consumeVO.getNumber()==null||consumeVO.getNumber().trim().length()>0)
                        {
                            bw.append("無發票" + " ");
                        }else{
                            bw.append("紙本發票" + " ");
                        }
                        bw.newLine();
                        bw.write("\r\n");
                    }
                }
            }
            if (income) {
                bw.write("\r\n");
                bw.write("收入資料");
                bw.write("\r\n");
                bw.append("日期 ");
                bw.append("主項目 ");
                bw.append("金額 ");
                bw.append("細節 ");
                bw.newLine();
                List<BankVO> bankVOS = bankDB.getAll();
                for (int i = 0; i < bankVOS.size(); i++) {
                    BankVO bankVO = bankVOS.get(i);
                    bw.append(Common.sTwo.format(new Date(bankVO.getDate().getTime())) + " ");
                    bw.append(bankVO.getMaintype() + " ");
                    bw.append(bankVO.getMoney() + " ");
                    bw.append(bankVO.getDetailname() + " ");
                    bw.newLine();
                    bw.write("\r\n");
                }
            }
            bw.close();
            if(local)
            {
                Message message=handler.obtainMessage();
                message.what=2;
                message.sendToTarget();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void FileTOLocal() {
        progressL.setVisibility(View.VISIBLE);
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                try {
                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, "記帳小助手.xls");
                    OutputStream outputStream = new FileOutputStream(file);
                    outputExcel(outputStream);
                } catch (Exception e) {
                   Message message=handler.obtainMessage();
                   message.what=3;
                   message.sendToTarget();
                }
            }
        };
        new Thread(runnable).start();
    }


    private void outputExcel(OutputStream outputStream) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            if (consume) {
                Sheet sheetCon = workbook.createSheet("消費");
                sheetCon.setColumnWidth(0, 11 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(1, 13 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(7, 100 * 256);// 調整欄位寬度
                Row rowTitle = sheetCon.createRow(0);
                rowTitle.createCell(0).setCellValue("日期");
                rowTitle.createCell(1).setCellValue("發票號碼");
                rowTitle.createCell(2).setCellValue("金額");
                rowTitle.createCell(3).setCellValue("主項目");
                rowTitle.createCell(4).setCellValue("次項目");
                rowTitle.createCell(5).setCellValue("中獎");
                rowTitle.createCell(6).setCellValue("類別");
                rowTitle.createCell(7).setCellValue("細節");
                List<ConsumeVO> consumeVOS = consumeDB.getAll();
                List<InvoiceVO> invoiceVOS = invoiceDB.getAll();
                List<Object> objects = new ArrayList<>();
                objects.addAll(consumeVOS);
                objects.addAll(invoiceVOS);
                //照時間排列
                Collections.sort(objects, new Comparator<Object>() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        long t1 = (o1 instanceof InvoiceVO) ? ((InvoiceVO) o1).getTime().getTime() : ((ConsumeVO) o1).getDate().getTime();
                        long t2 = (o2 instanceof InvoiceVO) ? ((InvoiceVO) o2).getTime().getTime() : ((ConsumeVO) o2).getDate().getTime();
                        if (t1 > t2) {
                            return -1;
                        } else if (t1 == t2) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                });


                //塞資料
                for (int i = 0; i < objects.size(); i++) {
                    Row rowContent = sheetCon.createRow(i + 1); // 建立儲存格
                    Object o = objects.get(i);
                    if (o instanceof InvoiceVO) {
                        InvoiceVO invoiceVO = (InvoiceVO) o;
                        rowContent.createCell(0).setCellValue(Common.sTwo.format(new Date(invoiceVO.getTime().getTime())));
                        rowContent.createCell(1).setCellValue(invoiceVO.getInvNum());
                        rowContent.createCell(2).setCellValue(invoiceVO.getAmount());
                        rowContent.createCell(3).setCellValue(Common.getType(invoiceVO.getMaintype()));
                        rowContent.createCell(4).setCellValue(Common.getType(invoiceVO.getSecondtype()));
                        //中獎訊息
                        try {
                            if(invoiceVO.getIswin().equals("0"))
                            {
                                rowContent.createCell(5).setCellValue("尚未兌獎");
                            }else if(invoiceVO.getIswin().equals("N")){
                                rowContent.createCell(5).setCellValue("無中獎");
                            }else {
                                rowContent.createCell(5).setCellValue(Common.getPriceName().get(invoiceVO.getIswin()));
                            }
                        }catch (Exception e)
                        {
                            rowContent.createCell(5).setCellValue("尚未兌獎");
                        }
                        rowContent.createCell(6).setCellValue("雲端發票");
                        //電子發票細節
                        List<JsonObject> js = gson.fromJson(invoiceVO.getDetail(), cdType);
                        sb=new StringBuffer();
                        float price,amout,n;
                        for (JsonObject j : js) {
                            try {
                                amout=j.get("amount").getAsFloat();
                                n = j.get("quantity").getAsFloat();
                                price = j.get("unitPrice").getAsFloat();
                                if(price==0)
                                {
                                    sb.append(j.get("description").getAsString() + " : \n" + (int)(amout/n) + "X" + (int)n + "=" + (int)amout + "元\n");
                                }else{
                                    sb.append(j.get("description").getAsString() + " : \n" + (int)price + "X" + (int)n + "=" + (int)amout + "元\n");
                                }
                            } catch (Exception e) {
                                sb.append(j.get("description").getAsString() + " : \n" + 0 + "X" + 0 + "=" + 0 + "元\n");
                            }
                        }
                        rowContent.createCell(7).setCellValue(sb.toString());
                    } else {
                        ConsumeVO consumeVO = (ConsumeVO) o;
                        rowContent.createCell(0).setCellValue(Common.sTwo.format(new Date(consumeVO.getDate().getTime())));
                        rowContent.createCell(1).setCellValue(consumeVO.getNumber());
                        rowContent.createCell(2).setCellValue(consumeVO.getMoney());
                        rowContent.createCell(3).setCellValue(consumeVO.getMaintype());
                        rowContent.createCell(4).setCellValue(consumeVO.getSecondType());
                        //中獎訊息
                        try {
                            if(consumeVO.getIsWin().equals("0"))
                            {
                                rowContent.createCell(5).setCellValue("尚未兌獎");
                            }else if(consumeVO.getIsWin().equals("N")){
                                rowContent.createCell(5).setCellValue("無中獎");
                            }else {
                                rowContent.createCell(5).setCellValue(Common.getPriceName().get(consumeVO.getIsWin()));
                            }
                        }catch (Exception e)
                        {
                            rowContent.createCell(5).setCellValue("尚未兌獎");
                        }
                        if(consumeVO.getNumber()==null||consumeVO.getNumber().length()>0)
                        {
                            rowContent.createCell(6).setCellValue("無發票");
                        }else{
                            rowContent.createCell(6).setCellValue("紙本發票");
                        }
                        rowContent.createCell(7).setCellValue((consumeVO.getDetailname()==null?"":consumeVO.getDetailname()));
                    }
                }
            }
            if (income) {
                Sheet sheetCon = workbook.createSheet("收入");
                sheetCon.setColumnWidth(0, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(2, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(3, 100*256);// 調整欄位寬度
                Row rowTitle = sheetCon.createRow(0);
                rowTitle.createCell(0).setCellValue("日期");
                rowTitle.createCell(1).setCellValue("主項目");
                rowTitle.createCell(2).setCellValue("金額");
                rowTitle.createCell(3).setCellValue("細節");
                List<BankVO> bankVOS = bankDB.getAll();
                for (int i = 0; i < bankVOS.size(); i++) {
                    Row rowContent = sheetCon.createRow(i + 1); // 建立儲存格
                    BankVO bankVO = bankVOS.get(i);
                    rowContent.createCell(0).setCellValue(Common.sTwo.format(new Date(bankVO.getDate().getTime())));
                    rowContent.createCell(1).setCellValue(bankVO.getMaintype());
                    rowContent.createCell(2).setCellValue(bankVO.getMoney());
                    rowContent.createCell(3).setCellValue(bankVO.getDetailname());
                }
            }
            if (all) {
                //Type
                Sheet sheetCon = workbook.createSheet("Type");
                List<TypeVO> typeVOS = typeDB.getExport();
                for (int i = 0; i < typeVOS.size(); i++) {
                    Row rowContent = sheetCon.createRow(i); // 建立儲存格
                    TypeVO typeVO = typeVOS.get(i);
                    rowContent.createCell(0).setCellValue(typeVO.getId());
                    rowContent.createCell(1).setCellValue(typeVO.getGroupNumber());
                    rowContent.createCell(2).setCellValue(typeVO.getName());
                    rowContent.createCell(3).setCellValue(typeVO.getImage());
                }
                //TypeDetail
                Sheet sheetCon1 = workbook.createSheet("TypeDetail");
                List<TypeDetailVO> typeDetailVOS = typeDetailDB.getExport();
                for (int i = 0; i < typeDetailVOS.size(); i++) {
                    Row rowContent = sheetCon1.createRow(i); // 建立儲存格
                    TypeDetailVO typeDetailVO = typeDetailVOS.get(i);
                    rowContent.createCell(0).setCellValue(typeDetailVO.getId());
                    rowContent.createCell(1).setCellValue(typeDetailVO.getGroupNumber());
                    rowContent.createCell(2).setCellValue(typeDetailVO.getName());
                    rowContent.createCell(3).setCellValue(typeDetailVO.getImage());
                    rowContent.createCell(4).setCellValue(typeDetailVO.getKeyword());
                }

                //BankDetail
                Sheet sheetCon2 = workbook.createSheet("BankType");
                List<BankTypeVO> bankTypeVOS = bankTybeDB.getExport();
                for (int i = 0; i < bankTypeVOS.size(); i++) {
                    Row rowContent = sheetCon2.createRow(i);
                    BankTypeVO bankTypeVO = bankTypeVOS.get(i);
                    rowContent.createCell(0).setCellValue(bankTypeVO.getId());
                    rowContent.createCell(1).setCellValue(bankTypeVO.getGroupNumber());
                    rowContent.createCell(2).setCellValue(bankTypeVO.getName());
                    rowContent.createCell(3).setCellValue(bankTypeVO.getImage());
                }
                //goal
                Sheet sheetCon3 = workbook.createSheet("Goal");
                List<GoalVO> goalVOS = goalDB.getAll();
                for (int i = 0; i < goalVOS.size(); i++) {
                    Row rowContent = sheetCon3.createRow(i);
                    GoalVO goalVO = goalVOS.get(i);
                    rowContent.createCell(0).setCellValue(goalVO.getId());
                    rowContent.createCell(1).setCellValue(goalVO.getType());
                    rowContent.createCell(2).setCellValue(goalVO.getName());
                    rowContent.createCell(3).setCellValue(goalVO.getMoney());
                    rowContent.createCell(4).setCellValue(goalVO.getTimeStatue());
                    rowContent.createCell(5).setCellValue(goalVO.getStartTime());
                    rowContent.createCell(6).setCellValue(goalVO.getEndTime());
                    rowContent.createCell(7).setCellValue(goalVO.isNotify());
                    rowContent.createCell(8).setCellValue(goalVO.getNotifyStatue());
                    rowContent.createCell(9).setCellValue(goalVO.getNotifyDate());
                    rowContent.createCell(10).setCellValue(goalVO.isNoWeekend());
                    rowContent.createCell(11).setCellValue(goalVO.getStatue());
                }

                //bank
                Sheet sheetCon4 = workbook.createSheet("Bank");
                List<BankVO> bankVOS = bankDB.getAll();
                for (int i = 0; i < bankVOS.size(); i++) {
                    Row rowContent = sheetCon4.createRow(i);
                    BankVO bankVO = bankVOS.get(i);
                    rowContent.createCell(0).setCellValue(bankVO.getId());
                    rowContent.createCell(1).setCellValue(bankVO.getMaintype());
                    rowContent.createCell(2).setCellValue(bankVO.getMoney());
                    rowContent.createCell(3).setCellValue(bankVO.getDate().getTime());
                    rowContent.createCell(4).setCellValue(bankVO.getFixDate());
                    rowContent.createCell(5).setCellValue(bankVO.getFixDateDetail());
                    rowContent.createCell(6).setCellValue(bankVO.getDetailname());
                    rowContent.createCell(7).setCellValue(bankVO.isAuto());
                    rowContent.createCell(8).setCellValue(bankVO.getAutoId());
                }

                //Consume
                Sheet sheetCon5 = workbook.createSheet("Consume");
                List<ConsumeVO> consumeVOS = consumeDB.getAll();
                for (int i = 0; i < consumeVOS.size(); i++) {
                    Row rowContent = sheetCon5.createRow(i);
                    ConsumeVO consumeVO = consumeVOS.get(i);
                    rowContent.createCell(0).setCellValue(consumeVO.getId());
                    rowContent.createCell(1).setCellValue(consumeVO.getMaintype());
                    rowContent.createCell(2).setCellValue(consumeVO.getSecondType());
                    rowContent.createCell(3).setCellValue(consumeVO.getMoney());
                    rowContent.createCell(4).setCellValue(consumeVO.getDate().getTime());
                    rowContent.createCell(5).setCellValue(consumeVO.getNumber());
                    rowContent.createCell(6).setCellValue(consumeVO.getFixDate());
                    rowContent.createCell(7).setCellValue(consumeVO.getFixDateDetail());
                    rowContent.createCell(8).setCellValue(consumeVO.getNotify());
                    rowContent.createCell(9).setCellValue((consumeVO.getDetailname()==null?"":consumeVO.getDetailname()));
                    rowContent.createCell(10).setCellValue(consumeVO.isAuto());
                    rowContent.createCell(11).setCellValue(consumeVO.getAutoId());
                    rowContent.createCell(12).setCellValue(consumeVO.getIsWin());
                    rowContent.createCell(13).setCellValue(consumeVO.getIsWinNul());
                }

                //Invoice
                Sheet sheetCon6 = workbook.createSheet("Invoice");
                List<InvoiceVO> invoiceVOS = invoiceDB.getAll();
                for (int i = 0; i < invoiceVOS.size(); i++) {
                    Row rowContent = sheetCon6.createRow(i);
                    InvoiceVO invoiceVO = invoiceVOS.get(i);
                    rowContent.createCell(0).setCellValue(invoiceVO.getId());
                    rowContent.createCell(1).setCellValue(invoiceVO.getInvNum());
                    rowContent.createCell(2).setCellValue(invoiceVO.getCardType());
                    rowContent.createCell(3).setCellValue(invoiceVO.getCardNo());
                    rowContent.createCell(4).setCellValue(invoiceVO.getCardEncrypt());
                    rowContent.createCell(5).setCellValue(invoiceVO.getTime().getTime());
                    rowContent.createCell(6).setCellValue(invoiceVO.getAmount());
                    rowContent.createCell(7).setCellValue(invoiceVO.getDetail());
                    rowContent.createCell(8).setCellValue(invoiceVO.getInvDonatable());
                    rowContent.createCell(9).setCellValue(invoiceVO.getDonateMark());
                    rowContent.createCell(10).setCellValue(invoiceVO.getCarrier());
                    rowContent.createCell(11).setCellValue(invoiceVO.getMaintype());
                    rowContent.createCell(12).setCellValue(invoiceVO.getSecondtype());
                    rowContent.createCell(13).setCellValue(invoiceVO.getHeartyteam());
                    rowContent.createCell(14).setCellValue(invoiceVO.getDonateTime().getTime());
                    rowContent.createCell(15).setCellValue(invoiceVO.getSellerBan());
                    rowContent.createCell(16).setCellValue(invoiceVO.getSellerName());
                    rowContent.createCell(17).setCellValue(invoiceVO.getSellerAddress());
                    rowContent.createCell(18).setCellValue(invoiceVO.getIswin());
                    rowContent.createCell(19).setCellValue(invoiceVO.getIsWinNul());
                }
                //Carrier
                Sheet sheetCon7 = workbook.createSheet("Carrier");
                List<CarrierVO> carrierVOS = carrierDB.getAll();
                for (int i = 0; i < carrierVOS.size(); i++) {
                    Row rowContent = sheetCon7.createRow(i);
                    CarrierVO carrierVO = carrierVOS.get(i);
                    rowContent.createCell(0).setCellValue(carrierVO.getId());
                    rowContent.createCell(1).setCellValue(carrierVO.getCarNul());
                    rowContent.createCell(2).setCellValue(carrierVO.getPassword());
                }
                //Price
                Sheet sheetCon8 = workbook.createSheet("Price");
                List<PriceVO> priceVOS =priceDB.getAll();
                for (int i = 0; i < priceVOS.size(); i++) {
                    Row rowContent = sheetCon8.createRow(i);
                    PriceVO priceVO=priceVOS.get(i);
                    rowContent.createCell(0).setCellValue(priceVO.getInvoYm());
                    rowContent.createCell(1).setCellValue(priceVO.getSuperPrizeNo());
                    rowContent.createCell(2).setCellValue(priceVO.getSpcPrizeNo());
                    rowContent.createCell(3).setCellValue(priceVO.getFirstPrizeNo1());
                    rowContent.createCell(4).setCellValue( priceVO.getFirstPrizeNo2());
                    rowContent.createCell(5).setCellValue( priceVO.getFirstPrizeNo3());
                    rowContent.createCell(6).setCellValue( priceVO.getSixthPrizeNo1());
                    rowContent.createCell(7).setCellValue( priceVO.getSixthPrizeNo2());
                    rowContent.createCell(8).setCellValue( priceVO.getSixthPrizeNo3());
                    rowContent.createCell(9).setCellValue( priceVO.getSuperPrizeAmt());
                    rowContent.createCell(10).setCellValue( priceVO.getSpcPrizeAmt());
                    rowContent.createCell(11).setCellValue( priceVO.getFirstPrizeAmt());
                    rowContent.createCell(12).setCellValue( priceVO.getSecondPrizeAmt());
                    rowContent.createCell(13).setCellValue( priceVO.getThirdPrizeAmt());
                    rowContent.createCell(14).setCellValue( priceVO.getFourthPrizeAmt());
                    rowContent.createCell(15).setCellValue( priceVO.getFifthPrizeAmt());
                    rowContent.createCell(16).setCellValue( priceVO.getSixthPrizeAmt());
                    rowContent.createCell(17).setCellValue( priceVO.getSixthPrizeNo4());
                    rowContent.createCell(18).setCellValue( priceVO.getSixthPrizeNo5());
                    rowContent.createCell(19).setCellValue( priceVO.getSixthPrizeNo6());
                }

                //ElePeriod
                Sheet sheetCon9 = workbook.createSheet("ElePeriod");
                List<ElePeriod> elePeriods = elePeriodDB.getAll();
                for (int i = 0; i < elePeriods.size(); i++) {
                    Row rowContent = sheetCon9.createRow(i);
                    ElePeriod elePeriod = elePeriods.get(i);
                    rowContent.createCell(0).setCellValue(elePeriod.getId());
                    rowContent.createCell(1).setCellValue(elePeriod.getCarNul());
                    rowContent.createCell(2).setCellValue(elePeriod.getYear());
                    rowContent.createCell(3).setCellValue(elePeriod.getMonth());
                    rowContent.createCell(4).setCellValue(elePeriod.isDownload());
                }
            }
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            if(local)
            {
                Message message=handler.obtainMessage();
                message.what=4;
                message.sendToTarget();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private class excelOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            fileChoice.setVisibility(View.GONE);
            show = true;
            txt = false;
            if (local) {
                FileTOLocal();
            } else {
                openCloud();
            }
        }
    }

    private class txtOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            txt = true;
            fileChoice.setVisibility(View.GONE);
            show = true;
            if (local) {
                TxtToLocal();
            } else {
               openCloud();
            }
        }
    }

    private class cancelOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            fileChoice.setVisibility(View.GONE);
            show = true;
        }
    }

    private class choiceAction implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            position = i;
            if (position == 0) {
                all = true;
                income = false;
                consume = false;
            } else if (position == 1) {
                all = false;
                income = true;
                consume = true;
            } else if (position == 2) {
                all = false;
                income = false;
                consume = true;
            } else {
                all = false;
                income = true;
                consume = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public void openCloud() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) SettingUploadFile.this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if(mNetworkInfo==null)
        {
            Common.showToast(SettingUploadFile.this.context,"網路沒有開啟，無法下載!");
            return;
        }
        Message message=handler.obtainMessage();
        message.what=0;
        message.sendToTarget();
        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient!=null)
        {
            mGoogleApiClient.disconnect();
        }
    }



    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.

        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(final DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            Common.showToast(context,"連線失敗!");
                            return;
                        }
                        Runnable runnable=new Runnable() {
                            @Override
                            public void run() {
                                // Otherwise, we can write our data to the new contents.
                                // Get an output stream for the contents.
                                OutputStream outputStream = result.getDriveContents().getOutputStream();
                                // Write the bitmap data from it.
                                ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                                String fileName, fileType;
                                if (txt) {
                                    fileName = "記帳小助手.txt";
                                    fileType = "File/txt";
                                    OutPutTxt(bitmapStream);
                                } else {
                                    fileName = "記帳小助手.xls";
                                    fileType = "File/xls";
                                    outputExcel(bitmapStream);
                                }


                                try {
                                    outputStream.write(bitmapStream.toByteArray());
                                } catch (IOException e1) {

                                }
                                // Create the initial metadata - MIME type and title.
                                // Note that the user will be able to change the title later.
                                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                        .setMimeType(fileType).setTitle(fileName).build();
                                // Create an intent for the file chooser, and start it.
                                IntentSender intentSender = Drive.DriveApi
                                        .newCreateFileActivityBuilder()
                                        .setInitialMetadata(metadataChangeSet)
                                        .setInitialDriveContents(result.getDriveContents())
                                        .build(mGoogleApiClient);
                                try {
                                    context.startIntentSenderForResult(
                                            intentSender, 3, null, 0, 0, 0);
                                } catch (IntentSender.SendIntentException e) {

                                }
                            }
                        };
                        new Thread(runnable).start();
                    }
                });
    }




}
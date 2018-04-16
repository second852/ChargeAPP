package com.chargeapp.whc.chargeapp.Control;



import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
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


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
    public static int position;
    private boolean local, consume, income, all, show = true, txt;
    private GoogleApiClient mGoogleApiClient;
    private RelativeLayout progressL;
    private ElePeriodDB elePeriodDB;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_upload, container, false);
        Common.setChargeDB(getActivity());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankDB = new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankTybeDB = new BankTybeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        goalDB = new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        elePeriodDB=new ElePeriodDB(MainActivity.chargeAPPDB.getReadableDatabase());
        List<EleMainItemVO> itemSon = getNewItem();
        listView = view.findViewById(R.id.list);
        fileChoice = view.findViewById(R.id.fileChoice);
        excel = view.findViewById(R.id.excel);
        txtFile = view.findViewById(R.id.txtFile);
        cancelF = view.findViewById(R.id.cancelF);
        choiceT = view.findViewById(R.id.choiceT);
        progressL=view.findViewById(R.id.progressL);
        AdView adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        excel.setOnClickListener(new excelOnClick());
        txtFile.setOnClickListener(new txtOnClick());
        cancelF.setOnClickListener(new cancelOnClick());
        listView.setAdapter(new ListAdapter(getActivity(), itemSon));
        setSpinner();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (rc != PackageManager.PERMISSION_GRANTED) {
            Common.askPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,getActivity());
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
                Common.showToast(getActivity(), "上傳成功");
            } else {
                Common.showToast(getActivity(), "上傳失敗");
            }
        }
    }


    private void setSpinner() {
        ArrayList<String> spinnerItem = new ArrayList();
        spinnerItem.add("備分資料庫");
        spinnerItem.add("全部");
        spinnerItem.add("支出");
        spinnerItem.add("收入");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinneritem, spinnerItem);
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
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), result.getErrorCode(), 0).show();
            return;
        }
        // Called typically when the app is not yet authorized, and authorization dialog is displayed to the user.
        try {
            if(position==0)
            {
                result.startResolutionForResult(getActivity(), 0);
            }
            else
            {
                if(txt)
                {
                    result.startResolutionForResult(getActivity(), 1);
                }else{
                    result.startResolutionForResult(getActivity(), 2);
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
                        if (SettingUploadFile.this.position == 0) {
                            openCloud();
                            txt = false;
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
            Common.showToast(getActivity(), "File Error!");
        }
    }

    private void OutPutTxt(OutputStream outputStream) {
        progressL.setVisibility(View.VISIBLE);
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
                bw.append("定期支出 ");
                bw.append("定期支出設定 ");
                bw.append("自動產生 ");
                bw.append("自動產生母ID ");
                bw.append("資料庫ID");
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
                        bw.append(invoiceVO.getMaintype() + " ");
                        bw.append(invoiceVO.getSecondtype() + " ");
                        bw.append(invoiceVO.getAmount() + " ");
                        bw.append(invoiceVO.getInvNum() + " ");
                        bw.append(invoiceVO.getDetail() + " ");
                        bw.append(invoiceVO.getIswin() + " ");
                        bw.append("電子發票" + " ");
                        bw.append("false" + " ");
                        bw.append("無" + " ");
                        bw.append("false" + " ");
                        bw.append("-1" + " ");
                        bw.append(String.valueOf(invoiceVO.getId()));
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
                        bw.append(consumeVO.getIsWin() + " ");
                        bw.append("本機" + " ");
                        bw.append(consumeVO.getFixDate() + " ");
                        bw.append(consumeVO.getFixDateDetail() + " ");
                        bw.append(String.valueOf(consumeVO.isAuto()) + " ");
                        bw.append(String.valueOf(consumeVO.getAutoId()) + " ");
                        bw.append(String.valueOf(consumeVO.getId()));
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
                bw.append("定期收入 ");
                bw.append("定期收入設定 ");
                bw.append("自動產生 ");
                bw.append("自動產生母ID ");
                bw.append("資料庫ID");
                bw.newLine();
                List<BankVO> bankVOS = bankDB.getAll();
                for (int i = 0; i < bankVOS.size(); i++) {
                    BankVO bankVO = bankVOS.get(i);
                    bw.append(Common.sTwo.format(new Date(bankVO.getDate().getTime())) + " ");
                    bw.append(bankVO.getMaintype() + " ");
                    bw.append(bankVO.getMoney() + " ");
                    bw.append(bankVO.getDetailname() + " ");
                    bw.append(bankVO.getFixDate() + " ");
                    bw.append(bankVO.getFixDateDetail() + " ");
                    bw.append(String.valueOf(bankVO.isAuto()) + " ");
                    bw.append(String.valueOf(bankVO.getAutoId()) + " ");
                    bw.append(String.valueOf(bankVO.getId()));
                    bw.newLine();
                    bw.write("\r\n");
                }
            }
            bw.close();
            if(local)
            {
                progressL.setVisibility(View.GONE);
                Common.showToast(getActivity(), "匯出成功，檔名為記帳小助手.txt，路徑為" + "/Download/記帳小助手.txt");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void FileTOLocal() {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, "記帳小助手.xls");
            OutputStream outputStream = new FileOutputStream(file);
            outputExcel(outputStream);
        } catch (Exception e) {
            Common.showToast(getActivity(), "File Error");
        }
    }


    private void outputExcel(OutputStream outputStream) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            if (consume) {
                Sheet sheetCon = workbook.createSheet("消費");
                sheetCon.setColumnWidth(2, 2);// 自動調整欄位寬度
                Row rowTitle = sheetCon.createRow(0);
                rowTitle.createCell(0).setCellValue("日期");
                rowTitle.createCell(1).setCellValue("主項目");
                rowTitle.createCell(2).setCellValue("次項目");
                rowTitle.createCell(3).setCellValue("金額");
                rowTitle.createCell(4).setCellValue("發票號碼");
                rowTitle.createCell(5).setCellValue("細節");
                rowTitle.createCell(6).setCellValue("中獎");
                rowTitle.createCell(7).setCellValue("類別");
                rowTitle.createCell(8).setCellValue("定期支出");
                rowTitle.createCell(9).setCellValue("定期支出設定");
                rowTitle.createCell(10).setCellValue("自動產生");
                rowTitle.createCell(11).setCellValue("自動產生母ID");
                rowTitle.createCell(12).setCellValue("資料庫ID");
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
                        rowContent.createCell(1).setCellValue(invoiceVO.getMaintype());
                        rowContent.createCell(2).setCellValue(invoiceVO.getSecondtype());
                        rowContent.createCell(3).setCellValue(invoiceVO.getAmount());
                        rowContent.createCell(4).setCellValue(invoiceVO.getInvNum());
                        rowContent.createCell(5).setCellValue(invoiceVO.getDetail());
                        rowContent.createCell(6).setCellValue(invoiceVO.getIswin());
                        rowContent.createCell(7).setCellValue("電子發票");
                        rowContent.createCell(8).setCellValue(false);
                        rowContent.createCell(9).setCellValue("無");
                        rowContent.createCell(10).setCellValue(false);
                        rowContent.createCell(11).setCellValue(-1);
                        rowContent.createCell(12).setCellValue(invoiceVO.getId());
                    } else {
                        ConsumeVO consumeVO = (ConsumeVO) o;
                        rowContent.createCell(0).setCellValue(Common.sTwo.format(new Date(consumeVO.getDate().getTime())));
                        rowContent.createCell(1).setCellValue(consumeVO.getMaintype());
                        rowContent.createCell(2).setCellValue(consumeVO.getSecondType());
                        rowContent.createCell(3).setCellValue(consumeVO.getMoney());
                        rowContent.createCell(4).setCellValue(consumeVO.getNumber());
                        rowContent.createCell(5).setCellValue((consumeVO.getDetailname()==null?"":consumeVO.getDetailname()));
                        rowContent.createCell(6).setCellValue(consumeVO.getIsWin());
                        rowContent.createCell(7).setCellValue("本機");
                        rowContent.createCell(8).setCellValue(consumeVO.getFixDate());
                        rowContent.createCell(9).setCellValue(consumeVO.getFixDateDetail());
                        rowContent.createCell(10).setCellValue(consumeVO.isAuto());
                        rowContent.createCell(11).setCellValue(consumeVO.getAutoId());
                        rowContent.createCell(12).setCellValue(consumeVO.getId());
                    }
                }
            }
            if (income) {
                Sheet sheetCon = workbook.createSheet("收入");
                sheetCon.setColumnWidth(2, 2);// 自動調整欄位寬度
                Row rowTitle = sheetCon.createRow(0);
                rowTitle.createCell(0).setCellValue("日期");
                rowTitle.createCell(1).setCellValue("主項目");
                rowTitle.createCell(2).setCellValue("金額");
                rowTitle.createCell(3).setCellValue("細節");
                rowTitle.createCell(4).setCellValue("定期收入");
                rowTitle.createCell(5).setCellValue("定期收入設定");
                rowTitle.createCell(6).setCellValue("自動產生");
                rowTitle.createCell(7).setCellValue("自動產生母ID");
                rowTitle.createCell(8).setCellValue("資料庫ID");
                List<BankVO> bankVOS = bankDB.getAll();
                for (int i = 0; i < bankVOS.size(); i++) {
                    Row rowContent = sheetCon.createRow(i + 1); // 建立儲存格
                    BankVO bankVO = bankVOS.get(i);
                    rowContent.createCell(0).setCellValue(Common.sTwo.format(new Date(bankVO.getDate().getTime())));
                    rowContent.createCell(1).setCellValue(bankVO.getMaintype());
                    rowContent.createCell(3).setCellValue(bankVO.getMoney());
                    rowContent.createCell(4).setCellValue(bankVO.getDetailname());
                    rowContent.createCell(5).setCellValue(bankVO.getFixDate());
                    rowContent.createCell(6).setCellValue(bankVO.getFixDateDetail());
                    rowContent.createCell(7).setCellValue(bankVO.isAuto());
                    rowContent.createCell(8).setCellValue(bankVO.getAutoId());
                    rowContent.createCell(9).setCellValue(bankVO.getId());
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
                    rowContent.createCell(4).setCellValue(typeVO.getKeyword());
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
                    rowContent.createCell(10).setCellValue(consumeVO.getIsWin());
                    rowContent.createCell(11).setCellValue(consumeVO.isAuto());
                    rowContent.createCell(12).setCellValue(consumeVO.getAutoId());
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
                    rowContent.createCell(8).setCellValue(invoiceVO.getSellerName());
                    rowContent.createCell(9).setCellValue(invoiceVO.getInvDonatable());
                    rowContent.createCell(10).setCellValue(invoiceVO.getInvDonatable());
                    rowContent.createCell(11).setCellValue(invoiceVO.getCarrier());
                    rowContent.createCell(12).setCellValue(invoiceVO.getMaintype());
                    rowContent.createCell(13).setCellValue(invoiceVO.getSecondtype());
                    rowContent.createCell(14).setCellValue(invoiceVO.getHeartyteam());
                    rowContent.createCell(15).setCellValue(invoiceVO.getTime().getTime());
                    rowContent.createCell(16).setCellValue(invoiceVO.getIswin());
                    rowContent.createCell(17).setCellValue(invoiceVO.getSellerBan());
                    rowContent.createCell(18).setCellValue(invoiceVO.getSellerAddress());
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
                //ElePeriod
                Sheet sheetCon8 = workbook.createSheet("ElePeriod");
                List<ElePeriod> elePeriods = elePeriodDB.getAll();
                for (int i = 0; i < elePeriods.size(); i++) {
                    Row rowContent = sheetCon8.createRow(i);
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
                Common.showToast(getActivity(), "匯出成功，檔名為記帳小助手.xls，路徑為" + "/Download/記帳小助手.xls");
                progressL.setVisibility(View.GONE);
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
        ConnectivityManager mConnectivityManager = (ConnectivityManager) SettingUploadFile.this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if(mNetworkInfo==null)
        {
            Common.showToast(SettingUploadFile.this.getActivity(),"網路沒有開啟，無法下載!");
            return;
        }
        progressL.setVisibility(View.VISIBLE);
        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
                    public void onResult(DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            Common.showToast(getActivity(),"連線失敗!");
                            return;
                        }
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
                            getActivity().startIntentSenderForResult(
                                    intentSender, 3, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {

                        }
                    }
                });
    }




}
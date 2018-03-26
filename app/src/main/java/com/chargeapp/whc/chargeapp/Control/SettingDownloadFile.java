package com.chargeapp.whc.chargeapp.Control;


import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.journeyapps.barcodescanner.ViewfinderView;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SettingDownloadFile extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private ListView listView;
    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private BankDB bankDB;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private BankTybeDB bankTybeDB;
    private GoalDB goalDB;
    private CarrierDB carrierDB;
    private ElePeriodDB elePeriodDB;
    public static GoogleApiClient mGoogleApiClient;
    public static DriveId mSelectedFileDriveId;
    private RelativeLayout progressL;
    private ProgressBar mProgressBar;
    private String action;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_main, container, false);
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankDB = new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankTybeDB = new BankTybeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        goalDB = new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        elePeriodDB=new ElePeriodDB(MainActivity.chargeAPPDB.getReadableDatabase());
        List<EleMainItemVO> itemSon = getNewItem();
        listView = view.findViewById(R.id.list);
        progressL = view.findViewById(R.id.progressL);
        mProgressBar = view.findViewById(R.id.progress);
        listView.setAdapter(new ListAdapter(getActivity(), itemSon));
        action = (String) getArguments().getSerializable("action");
        progressL.setVisibility(View.GONE);
        mProgressBar.setMax(100);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (action.equals("download")) {
            open();
        } else if (action.equals("open")) {
            openCloud();
        } else {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
                mGoogleApiClient = null;
            }
        }
    }

    private List<EleMainItemVO> getNewItem() {
        List<EleMainItemVO> eleMainItemVOList = new ArrayList<>();
        eleMainItemVOList.add(new EleMainItemVO("從本機匯入資料", R.drawable.export));
        eleMainItemVOList.add(new EleMainItemVO("從Google雲端匯入資料", R.drawable.export));
        return eleMainItemVOList;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (mSelectedFileDriveId != null) {
            open();
            return;
        }

        // Let the user pick a file...
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{"file/xls"})
                .build(mGoogleApiClient);
        try {
           getActivity().startIntentSenderForResult(intentSender, 5, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Called whenever the API client fails to connect.
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), result.getErrorCode(), 0).show();
            return;
        }
        // Called typically when the app is not yet authorized, and authorization dialog is displayed to the user.
        try {
            result.startResolutionForResult(getActivity(), 4);
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
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            File file = new File(dir, "記帳小助手.xls");
                            InputStream inp = new FileInputStream(file);
                            inputExcel(inp);
                        } catch (Exception e) {
                            Common.showToast(getActivity(), "請將檔案放置在/Download，檔名為記帳小助手.xls");
                        }
                    }
                });
            } else if (position == 1) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConnectivityManager mConnectivityManager = (ConnectivityManager) SettingDownloadFile.this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                        if(mNetworkInfo!=null)
                        {
                            openCloud();
                        }else{
                            Common.showToast(SettingDownloadFile.this.getActivity(),"網路沒有開啟，無法下載!");
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


    private void inputExcel(InputStream inp) {
        int i = 0;
        try {
            Workbook workbook = new HSSFWorkbook(inp);
            for (Sheet sheet : workbook) {
                String sheetTitle = sheet.getSheetName();
                if ((!sheetTitle.equals("Type") && i == 0)) {
                    Common.showToast(getActivity(), "不是備份檔");
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
                        typeDB.insertHId(typeVO);
                        Log.d("xxxxxxxxx","xxxx");
                    } else if (sheetTitle.equals("TypeDetail")) {
                        TypeDetailVO typeDetailVO = new TypeDetailVO();
                        typeDetailVO.setId((int) row.getCell(0).getNumericCellValue());
                        typeDetailVO.setGroupNumber(row.getCell(1).getStringCellValue());
                        typeDetailVO.setName(row.getCell(2).getStringCellValue());
                        typeDetailVO.setImage((int) row.getCell(3).getNumericCellValue());
                        typeDetailVO.setKeyword(row.getCell(4).getStringCellValue());
                        typeDetailDB.insertHid(typeDetailVO);
                    } else if (sheetTitle.equals("BankType")) {
                        BankTypeVO bankTypeVO = new BankTypeVO();
                        bankTypeVO.setId((int) row.getCell(0).getNumericCellValue());
                        bankTypeVO.setGroupNumber(row.getCell(1).getStringCellValue());
                        bankTypeVO.setName(row.getCell(2).getStringCellValue());
                        bankTypeVO.setImage((int) row.getCell(3).getNumericCellValue());
                        bankTybeDB.insert(bankTypeVO);
                    } else if (sheetTitle.equals("Goal")) {
                        GoalVO goalVO = new GoalVO();
                        goalVO.setId((int) row.getCell(0).getNumericCellValue());
                        goalVO.setType(row.getCell(1).getStringCellValue());
                        goalVO.setName(row.getCell(2).getStringCellValue());
                        goalVO.setMoney((int)row.getCell(3).getNumericCellValue());
                        goalVO.setTimeStatue(row.getCell(4).getStringCellValue());
                        goalVO.setStartTime(new java.sql.Date((long) row.getCell(5).getNumericCellValue()));
                        goalVO.setEndTime(new java.sql.Date((long) row.getCell(6).getNumericCellValue()));
                        goalVO.setNotify(row.getCell(7).getBooleanCellValue());
                        goalVO.setNotifyStatue(row.getCell(8).getStringCellValue());
                        goalVO.setNotifyDate(row.getCell(9).getStringCellValue());
                        goalVO.setNoWeekend(row.getCell(10).getBooleanCellValue());
                        goalVO.setStatue((int) row.getCell(11).getNumericCellValue());
                        goalDB.insertHid(goalVO);
                    } else if (sheetTitle.equals("Bank")) {
                        BankVO bankVO = new BankVO();
                        bankVO.setId((int) row.getCell(0).getNumericCellValue());
                        bankVO.setMaintype(row.getCell(1).getStringCellValue());
                        bankVO.setMoney((int)row.getCell(2).getNumericCellValue());
                        bankVO.setDate(new java.sql.Date((long) row.getCell(3).getNumericCellValue()));
                        bankVO.setFixDate(row.getCell(4).getStringCellValue());
                        bankVO.setFixDateDetail(row.getCell(5).getStringCellValue());
                        bankVO.setDetailname(row.getCell(6).getStringCellValue());
                        bankVO.setAuto(row.getCell(7).getBooleanCellValue());
                        bankVO.setAutoId((int) row.getCell(8).getNumericCellValue());
                        bankDB.insertHid(bankVO);
                    } else if (sheetTitle.equals("Consume")) {
                        ConsumeVO consumeVO = new ConsumeVO();
                        consumeVO.setId((int) row.getCell(0).getNumericCellValue());
                        consumeVO.setMaintype(row.getCell(1).getStringCellValue());
                        consumeVO.setSecondType(row.getCell(2).getStringCellValue());
                        consumeVO.setMoney((int)row.getCell(3).getNumericCellValue());
                        consumeVO.setDate(new java.sql.Date((long) row.getCell(4).getNumericCellValue()));
                        consumeVO.setNumber(row.getCell(5).getStringCellValue());
                        consumeVO.setFixDate(row.getCell(6).getStringCellValue());
                        consumeVO.setFixDateDetail(row.getCell(7).getStringCellValue());
                        consumeVO.setNotify(row.getCell(8).getStringCellValue());
                        consumeVO.setDetailname(row.getCell(9).getStringCellValue());
                        consumeVO.setIsWin(row.getCell(10).getStringCellValue());
                        consumeVO.setAuto(row.getCell(11).getBooleanCellValue());
                        consumeVO.setAutoId((int) row.getCell(12).getNumericCellValue());
                        consumeDB.insertHid(consumeVO);
                    } else if (sheetTitle.equals("Invoice")) {
                        InvoiceVO invoiceVO = new InvoiceVO();
                        invoiceVO.setId((int) row.getCell(0).getNumericCellValue());
                        invoiceVO.setInvNum(row.getCell(1).getStringCellValue());
                        invoiceVO.setCardType(row.getCell(2).getStringCellValue());
                        invoiceVO.setCardNo(row.getCell(3).getStringCellValue());
                        invoiceVO.setCardEncrypt(row.getCell(4).getStringCellValue());
                        invoiceVO.setTime(new Timestamp((long) row.getCell(5).getNumericCellValue()));
                        invoiceVO.setAmount((int)row.getCell(6).getNumericCellValue());
                        invoiceVO.setDetail(row.getCell(7).getStringCellValue());
                        invoiceVO.setSellerName(row.getCell(8).getStringCellValue());
                        invoiceVO.setInvDonatable(row.getCell(9).getStringCellValue());
                        invoiceVO.setDonateMark(row.getCell(10).getStringCellValue());
                        invoiceVO.setCarrier(row.getCell(11).getStringCellValue());
                        invoiceVO.setMaintype(row.getCell(12).getStringCellValue());
                        invoiceVO.setSecondtype(row.getCell(13).getStringCellValue());
                        invoiceVO.setHeartyteam(row.getCell(14).getStringCellValue());
                        invoiceVO.setDonateTime(new Timestamp((long) row.getCell(15).getNumericCellValue()));
                        invoiceVO.setIswin(row.getCell(16).getStringCellValue());
                        invoiceVO.setSellerBan(row.getCell(17).getStringCellValue());
                        invoiceVO.setSellerAddress(row.getCell(18).getStringCellValue());
                        invoiceDB.insertHid(invoiceVO);
                    } else if (sheetTitle.equals("Invoice")) {
                        CarrierVO carrierVO = new CarrierVO();
                        carrierVO.setId((int) row.getCell(0).getNumericCellValue());
                        carrierVO.setCarNul(row.getCell(1).getStringCellValue());
                        carrierVO.setPassword(row.getCell(2).getStringCellValue());
                        carrierDB.insertHid(carrierVO);
                    }else if (sheetTitle.equals("ElePeriod")) {
                        ElePeriod elePeriod = new ElePeriod();
                        elePeriod.setId((int) row.getCell(0).getNumericCellValue());
                        elePeriod.setCarNul(row.getCell(1).getStringCellValue());
                        elePeriod.setYear((int) row.getCell(2).getNumericCellValue());
                        elePeriod.setMonth((int) row.getCell(3).getNumericCellValue());
                        elePeriod.setDownload(row.getCell(4).getBooleanCellValue());
                        elePeriodDB.insertHid(elePeriod);
                    }
                }
                i++;
            }
            workbook.close();
            inp.close();

            Common.showToast(getActivity(),"匯入成功");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void openCloud() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    private void open() {
        DriveFile.DownloadProgressListener listener = new DriveFile.DownloadProgressListener() {
            @Override
            public void onProgress(long bytesDownloaded, long bytesExpected) {
                // Update progress dialog with the latest progress.
                int progress = (int) (bytesDownloaded * 100 / bytesExpected);
                mProgressBar.setProgress(progress);
                if (progress < 100 && progress > 0) {
                    progressL.setVisibility(View.VISIBLE);
                } else {
                    progressL.setVisibility(View.GONE);
                }
            }
        };

        DriveFile driveFile = mSelectedFileDriveId.asDriveFile();
        driveFile.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, listener)
                .setResultCallback(driveContentsCallback);
        mSelectedFileDriveId = null;
    }

    private final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Common.showToast(getActivity(), "連線失敗!");
                        return;
                    }
                    // Read from the input stream an print to LOGCAT
                    DriveContents driveContents = result.getDriveContents();
                    inputExcel(driveContents.getInputStream());
                    // Close file contents
                    driveContents.discard(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient = null;
                }
            };
}
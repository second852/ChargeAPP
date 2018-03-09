package com.chargeapp.whc.chargeapp.Control;


import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
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
import android.widget.Switch;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.EleMainItemVO;
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

public class SettingDownloadFile extends Fragment {


    private ListView listView;
    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private BankDB bankDB;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private BankTybeDB bankTybeDB;
    private GoalDB goalDB;
    private CarrierDB carrierDB;
    private static final String TAG = "drive-quickstart";
    private static final int RC_OPENER = 0;
    private GoogleApiClient mGoogleApiClient;
    private ProgressBar mProgressBar;
    private DriveId mSelectedFileDriveId;



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
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        List<EleMainItemVO> itemSon = getNewItem();
        listView = view.findViewById(R.id.list);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setMax(100);
        listView.setAdapter(new ListAdapter(getActivity(), itemSon));
        return view;
    }


    private List<EleMainItemVO> getNewItem() {
        List<EleMainItemVO> eleMainItemVOList = new ArrayList<>();
        eleMainItemVOList.add(new EleMainItemVO("從本機匯入資料", R.drawable.export));
        eleMainItemVOList.add(new EleMainItemVO("從Google雲端匯入資料", R.drawable.importf));
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
                        }catch (Exception e)
                        {
                            Common.showToast(getActivity(),"請將檔案放置在/Download，檔名為記帳小助手.xls");
                        }
                    }
                });
            } else if (position == 1) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCloud();
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
        int i=0;
        try {
            Workbook workbook = new HSSFWorkbook(inp);
            for (Sheet sheet:workbook)
            {
                String sheetTitle=sheet.getSheetName();
                if((!sheetTitle.equals("Type")&&i==0))
                {
                    Common.showToast(getActivity(),"不是備份檔");
                    return;
                }
                for (Row row:sheet)
                {
                    if(sheetTitle.equals("Type"))
                    {
                        TypeVO typeVO=new TypeVO();
                        typeVO.setId((int)row.getCell(0).getNumericCellValue());
                        typeVO.setGroupNumber(row.getCell(1).getStringCellValue());
                        typeVO.setName(row.getCell(2).getStringCellValue());
                        typeVO.setImage((int) row.getCell(3).getNumericCellValue());
                        typeDB.insertHId(typeVO);
                    }else  if(sheetTitle.equals("TypeDetail"))
                    {
                        TypeDetailVO typeDetailVO=new TypeDetailVO();
                        typeDetailVO.setId((int)row.getCell(0).getNumericCellValue());
                        typeDetailVO.setGroupNumber(row.getCell(1).getStringCellValue());
                        typeDetailVO.setName(row.getCell(2).getStringCellValue());
                        typeDetailVO.setImage((int) row.getCell(3).getNumericCellValue());
                        typeDetailVO.setKeyword(row.getCell(4).getStringCellValue());
                        typeDetailDB.insertHid(typeDetailVO);
                    }else  if(sheetTitle.equals("BankType"))
                    {
                        BankTypeVO bankTypeVO=new BankTypeVO();
                        bankTypeVO.setId((int)row.getCell(0).getNumericCellValue());
                        bankTypeVO.setGroupNumber(row.getCell(1).getStringCellValue());
                        bankTypeVO.setName(row.getCell(2).getStringCellValue());
                        bankTypeVO.setImage((int) row.getCell(3).getNumericCellValue());
                        bankTybeDB.insert(bankTypeVO);
                    }else  if(sheetTitle.equals("Goal"))
                    {
                        GoalVO goalVO=new GoalVO();
                        goalVO.setId((int)row.getCell(0).getNumericCellValue());
                        goalVO.setType(row.getCell(1).getStringCellValue());
                        goalVO.setName(row.getCell(2).getStringCellValue());
                        goalVO.setMoney(row.getCell(3).getStringCellValue());
                        goalVO.setTimeStatue(row.getCell(4).getStringCellValue());
                        goalVO.setStartTime(new java.sql.Date(Long.valueOf(row.getCell(5).getStringCellValue())));
                        goalVO.setEndTime(new java.sql.Date(Long.valueOf(row.getCell(6).getStringCellValue())));
                        goalVO.setNotify(row.getCell(7).getBooleanCellValue());
                        goalVO.setNotifyStatue(row.getCell(8).getStringCellValue());
                        goalVO.setNotifyDate(row.getCell(9).getStringCellValue());
                        goalVO.setNoWeekend(row.getCell(10).getBooleanCellValue());
                        goalVO.setStatue((int)row.getCell(11).getNumericCellValue());
                        goalDB.insertHid(goalVO);
                    }else if(sheetTitle.equals("Bank")){
                        BankVO bankVO = new BankVO();
                        bankVO.setId((int)row.getCell(0).getNumericCellValue());
                        bankVO.setMaintype(row.getCell(1).getStringCellValue());
                        bankVO.setMoney(row.getCell(2).getStringCellValue());
                        bankVO.setDate(new java.sql.Date(Long.valueOf(row.getCell(3).getStringCellValue())));
                        bankVO.setFixDateDetail(row.getCell(4).getStringCellValue());
                        bankVO.setFixDateDetail(row.getCell(5).getStringCellValue());
                        bankVO.setDetailname(row.getCell(6).getStringCellValue());
                        bankVO.setAuto(Boolean.valueOf(row.getCell(7).getStringCellValue()));
                        bankVO.setAutoId((int)row.getCell(8).getNumericCellValue());
                        bankDB.insertHid(bankVO);
                    }else if(sheetTitle.equals("Consume")){
                        ConsumeVO consumeVO=new ConsumeVO();
                        consumeVO.setId((int)row.getCell(0).getNumericCellValue());
                        consumeVO.setMaintype(row.getCell(1).getStringCellValue());
                        consumeVO.setSecondType(row.getCell(2).getStringCellValue());
                        consumeVO.setMoney(row.getCell(3).getStringCellValue());
                        consumeVO.setDate(new java.sql.Date(Long.valueOf(row.getCell(4).getStringCellValue())));
                        consumeVO.setNumber(row.getCell(5).getStringCellValue());
                        consumeVO.setFixDate(row.getCell(6).getStringCellValue());
                        consumeVO.setFixDateDetail(row.getCell(7).getStringCellValue());
                        consumeVO.setNotify(row.getCell(8).getStringCellValue());
                        consumeVO.setDetailname(row.getCell(9).getStringCellValue());
                        consumeVO.setIsWin(row.getCell(10).getStringCellValue());
                        consumeVO.setAuto(Boolean.valueOf(row.getCell(11).getStringCellValue()));
                        consumeVO.setAutoId((int)row.getCell(12).getNumericCellValue());
                        consumeDB.insertHid(consumeVO);
                    }else if(sheetTitle.equals("Invoice")){
                        InvoiceVO invoiceVO=new InvoiceVO();
                        invoiceVO.setId((int)row.getCell(0).getNumericCellValue());
                        invoiceVO.setInvNum(row.getCell(1).getStringCellValue());
                        invoiceVO.setCardType(row.getCell(2).getStringCellValue());
                        invoiceVO.setCardNo(row.getCell(3).getStringCellValue());
                        invoiceVO.setCardEncrypt(row.getCell(4).getStringCellValue());
                        invoiceVO.setTime(new Timestamp(Long.valueOf(row.getCell(5).getStringCellValue())));
                        invoiceVO.setAmount(row.getCell(6).getStringCellValue());
                        invoiceVO.setDetail(row.getCell(7).getStringCellValue());
                        invoiceVO.setSellerName(row.getCell(8).getStringCellValue());
                        invoiceVO.setInvDonatable(row.getCell(9).getStringCellValue());
                        invoiceVO.setDonateMark(row.getCell(10).getStringCellValue());
                        invoiceVO.setCarrier(row.getCell(11).getStringCellValue());
                        invoiceVO.setMaintype(row.getCell(12).getStringCellValue());
                        invoiceVO.setSecondtype(row.getCell(13).getStringCellValue());
                        invoiceVO.setHeartyteam(row.getCell(14).getStringCellValue());
                        invoiceVO.setDonateTime(new Timestamp(Long.valueOf(row.getCell(15).getStringCellValue())));
                        invoiceVO.setIswin(row.getCell(16).getStringCellValue());
                        invoiceVO.setSellerBan(row.getCell(17).getStringCellValue());
                        invoiceVO.setSellerAddress(row.getCell(18).getStringCellValue());
                        invoiceDB.insertHid(invoiceVO);
                    }else if(sheetTitle.equals("Invoice")){
                        CarrierVO carrierVO=new CarrierVO();
                        carrierVO.setId((int)row.getCell(0).getNumericCellValue());
                        carrierVO.setCarNul(row.getCell(1).getStringCellValue());
                        carrierVO.setPassword(row.getCell(2).getStringCellValue());
                        carrierDB.insertHid(carrierVO);
                    }
                }
                i++;
            }
            workbook.close();
            inp.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void openCloud() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(new googleCallback())
                    .addOnConnectionFailedListener(new failCallback())
                    .build();
        }
        mGoogleApiClient.connect();
    }

    private class googleCallback implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.i(TAG, "API client connected.");
            if (mSelectedFileDriveId != null) {
                open();
                return;
            }

            // Let the user pick a file...
            IntentSender intentSender = Drive.DriveApi
                    .newOpenFileActivityBuilder()
                    .setMimeType(new String[]{"file/xls", "image/jpeg", "text/plain"})
                    .build(mGoogleApiClient);
            try {
                getActivity().startIntentSenderForResult(intentSender, RC_OPENER, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Unable to send intent", e);
            }

        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i(TAG, "GoogleApiClient connection suspended");
        }
    }

    private class failCallback implements GoogleApiClient.OnConnectionFailedListener {
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
                result.startResolutionForResult(getActivity(), 2);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Exception while starting resolution activity. " + e.getMessage());
            }
        }
    }

    private void open() {
        mProgressBar.setProgress(0);
        mProgressBar.setVisibility(View.VISIBLE);
        DriveFile.DownloadProgressListener listener = new DriveFile.DownloadProgressListener() {
            @Override
            public void onProgress(long bytesDownloaded, long bytesExpected) {
                // Update progress dialog with the latest progress.
                int progress = (int) (bytesDownloaded * 100 / bytesExpected);
                Log.d(TAG, String.format("Loading progress: %d percent", progress));
                mProgressBar.setProgress(progress);
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
                        Log.w(TAG, "Error while opening the file contents");
                        return;
                    }
                    Log.i(TAG, "File contents opened");
                    // Read from the input stream an print to LOGCAT
                    DriveContents driveContents = result.getDriveContents();
                    inputExcel(driveContents.getInputStream());
                    // Close file contents
                    driveContents.discard(mGoogleApiClient);
                }
            };
}
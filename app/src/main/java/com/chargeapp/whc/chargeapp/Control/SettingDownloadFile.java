package com.chargeapp.whc.chargeapp.Control;


import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Switch;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.BankVO;
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
import com.google.android.gms.drive.MetadataChangeSet;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static int position;
    private boolean txt;
    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_CREATOR = 2;
    private GoogleApiClient mGoogleApiClient;


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
        List<EleMainItemVO> itemSon = getNewItem();
        listView = view.findViewById(R.id.list);
        listView.setAdapter(new ListAdapter(getActivity(), itemSon));
        return view;
    }


    private List<EleMainItemVO> getNewItem() {
        List<EleMainItemVO> eleMainItemVOList = new ArrayList<>();
        eleMainItemVOList.add(new EleMainItemVO("匯出資料到本機", R.drawable.importf));
        eleMainItemVOList.add(new EleMainItemVO("匯出資料到Google雲端", R.drawable.importf));
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

                    }
                });

            } else if (position == 1) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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
        } catch (Exception e) {
            Common.showToast(getActivity(), "File Error!");
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
        } catch (Exception e) {
            Common.showToast(getActivity(), "File Error");
        }
    }

    private void inputExcel(OutputStream outputStream) {
        int i=0;
        try {
            InputStream inp = new FileInputStream("workbook.xls");
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
                    }


                }
                i++;
            }
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void openCloud() {
        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(new googleCallback())
                    .addOnConnectionFailedListener(new failCallback())
                    .build();
        }
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();
    }

    private class googleCallback implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.i(TAG, "API client connected.");
            saveFileToDrive();
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
            Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
            if (!result.hasResolution()) {
                // show the localized error dialog.
                GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), result.getErrorCode(), 0).show();
                return;
            }

        }
    }


    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Failed to create new contents.");
                            return;
                        }
                        // Otherwise, we can write our data to the new contents.
                        Log.i(TAG, "New contents created.");
                        // Get an output stream for the contents.
                        OutputStream outputStream = result.getDriveContents().getOutputStream();
                        // Write the bitmap data from it.
                        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                        String fileName, fileType;
                        if (txt) {
                            fileName = "記帳小助手.txt";
                            fileType = "File/txt";
                        } else {
                            fileName = "記帳小助手.xls";
                            fileType = "File/xls";
                        }


                        try {
                            outputStream.write(bitmapStream.toByteArray());
                        } catch (IOException e1) {
                            Log.i(TAG, "Unable to write file contents.");
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
                                    intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "Failed to launch file chooser.");
                        }
                    }
                });
    }


}
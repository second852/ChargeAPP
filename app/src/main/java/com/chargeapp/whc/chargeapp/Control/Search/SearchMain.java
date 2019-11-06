package com.chargeapp.whc.chargeapp.Control.Search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.Adapter.DeleteDialogFragment;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyFromDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.Goal.GoalUpdate;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyUpdate;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyUpdateConsume;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyUpdateMoney;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateIncome;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateInvoice;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateSpend;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PropertyFromVO;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.TypeCode.PropertyType;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jsoup.internal.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchMain extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private Activity context;
    private View view;
    private BootstrapEditText keyName,beginD,endD;
    private ImageView search;
    private ListView listView;
    private BootstrapButton searchSettingShow;
    private List<Object> searchObject;
    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private BankDB bankDB;
    private PropertyDB propertyDB;
    private PropertyFromDB propertyFromDB;
    private GoalDB goalDB;
    private int p;
    private ProgressDialog progressDialog;
    private Gson gson;
    private CurrencyDB currencyDB;
    private RelativeLayout settingR;
    private BootstrapButton searchSetting;
    private CheckBox timeCheck,consumeCheck,incomeCheck,goalCheck,propertyCheck;
    private LinearLayout beginL,endL,showDate;
    private boolean needTime,needConsume,needIncome,needGoal,needProperty;
    private View dateView;
    private DatePicker datePicker;
    private TextView dateSave,message, percent;
    private Date start,end;
    private View fabBGLayout;
    private StringBuilder showStringTime=new StringBuilder();
    private String keyNameString;
    private String searchMainAction;
    private boolean firstEnter;
    private RelativeLayout progressL;
    private GoogleApiClient mGoogleApiClient;
    private StringBuffer fileName;
    private BigDecimal c,t,hundred=new BigDecimal(100);



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.context=(Activity) context;
        }else{
            this.context=getActivity();
        }
        gson=new Gson();
        ((AppCompatActivity)context).getSupportActionBar().show();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_main, container, false);
        searchMainAction=getArguments().getString("searchMainAction");
        findViewById();
        setDB();
        if(searchMainAction.equals("new"))
        {
            setNew();
        }else {
            setOld();
        }
        return view;
    }

    private void setOld() {
        needTime=getArguments().getBoolean("needTime");
        needConsume=getArguments().getBoolean("needConsume");
        needIncome=getArguments().getBoolean("needIncome");
        needProperty=getArguments().getBoolean("needProperty");
        needGoal=getArguments().getBoolean("needGoal");
        timeCheck.setChecked(needTime);
        consumeCheck.setChecked(needConsume);
        propertyCheck.setChecked(needProperty);
        goalCheck.setChecked(needIncome);
        incomeCheck.setChecked(needGoal);

        beginD.setText(getArguments().getString("beginD"));
        endD.setText(getArguments().getString("endD"));
        keyName.setText(getArguments().getString("keyName"));
        p=getArguments().getInt("p");
        setListView();
    }


    private Bundle getOldBundle()
    {
        Bundle bundle=new Bundle();
        bundle.putSerializable("action",Common.searchMainString);
        bundle.putSerializable("searchMainAction","old");
        bundle.putSerializable("needTime",needTime);
        bundle.putSerializable("needConsume",needConsume);
        bundle.putSerializable("needIncome",needIncome);
        bundle.putSerializable("needProperty",needProperty);
        bundle.putSerializable("needGoal",needGoal);
        bundle.putSerializable("beginD",beginD.getText().toString().trim());
        bundle.putSerializable("endD",endD.getText().toString().trim());
        bundle.putSerializable("keyName",keyNameString);
        bundle.putSerializable("p",p);
        return bundle;
    }


    private void setNew() {
        timeCheck.setChecked(false);
        consumeCheck.setChecked(true);
        propertyCheck.setChecked(true);
        goalCheck.setChecked(true);
        incomeCheck.setChecked(true);
        p=0;
    }

    private void setDB() {
        Common.setChargeDB(context);
        consumeDB=new ConsumeDB(MainActivity.chargeAPPDB);
        bankDB=new BankDB(MainActivity.chargeAPPDB);
        invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB);
        goalDB=new GoalDB(MainActivity.chargeAPPDB);
        propertyDB=new PropertyDB(MainActivity.chargeAPPDB);
        propertyFromDB=new PropertyFromDB(MainActivity.chargeAPPDB);
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);
    }

    private void findViewById() {
        timeCheck=view.findViewById(R.id.timeCheck);
        keyName=view.findViewById(R.id.keyName);
        search=view.findViewById(R.id.search);
        listView=view.findViewById(R.id.list);
        searchSettingShow=view.findViewById(R.id.searchSettingShow);
        searchSettingShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingR.setVisibility(View.VISIBLE);
                fabBGLayout.setVisibility(View.VISIBLE);
            }
        });
        settingR=view.findViewById(R.id.settingR);
        searchSetting=view.findViewById(R.id.searchSetting);
        searchSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingR.setVisibility(View.GONE);
                fabBGLayout.setVisibility(View.GONE);
                searchSettingShow.setVisibility(View.VISIBLE);
                searchSettingShow.setText(showScope());
            }
        });
        search.setOnClickListener(new showSearch());
        timeCheck.setOnCheckedChangeListener(new checkChoice());
        beginL=view.findViewById(R.id.beginL);
        endL=view.findViewById(R.id.endL);
        Calendar calendar=Calendar.getInstance();
        endD=view.findViewById(R.id.endD);
        endD.setShowSoftInputOnFocus(false);
        endD.setText(Common.sTwo.format(calendar.getTime()));
        endD.setOnClickListener(new choiceDay());
        calendar.add(Calendar.MONTH,-1);
        beginD=view.findViewById(R.id.beginD);
        beginD.setShowSoftInputOnFocus(false);
        beginD.setText(Common.sTwo.format(calendar.getTime()));
        beginD.setOnClickListener(new choiceDay());
        showDate=view.findViewById(R.id.showDate);
        datePicker=view.findViewById(R.id.datePicker);
        dateSave=view.findViewById(R.id.dateSave);
        dateSave.setOnClickListener(new choiceDate());
        fabBGLayout=view.findViewById(R.id.fabBGLayout);
        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingR.setVisibility(View.GONE);
                fabBGLayout.setVisibility(View.GONE);
                searchSettingShow.setVisibility(View.VISIBLE);
                searchSettingShow.setText(showScope());
            }
        });
        consumeCheck=view.findViewById(R.id.consumeCheck);
        consumeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                needConsume=b;
            }
        });
        incomeCheck=view.findViewById(R.id.incomeCheck);
        incomeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                needIncome=b;
            }
        });
        goalCheck=view.findViewById(R.id.goalCheck);
        goalCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                needGoal=b;
            }
        });
        propertyCheck=view.findViewById(R.id.propertyCheck);
        propertyCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                needProperty=b;
            }
        });
        message=view.findViewById(R.id.message);
        progressL=view.findViewById(R.id.progressL);
        percent =view.findViewById(R.id.percent);
    }


    private void setListView()
    {
        keyNameString=keyName.getText().toString();

        if(StringUtil.isBlank(keyNameString))
        {
            keyName.setError("不能空白!");
            return;
        }

        searchObject=new ArrayList<>();


        if(needTime)
        {
            start=stringToDate(beginD.getText().toString());
            end=stringToDate(endD.getText().toString());
        }

        //consume main/second/detail
        //invoice main/second/detail
        if(needConsume)
        {
            if(needTime)
            {
                searchObject.addAll(consumeDB.findByKeyWordAndTime(keyNameString,start.getTime(),end.getTime()));
                searchObject.addAll(invoiceDB.findBySearchKeyAndTime(keyNameString,start.getTime(),end.getTime()));

            }else {
                searchObject.addAll(consumeDB.findByKeyWord(keyNameString));
                searchObject.addAll(invoiceDB.findBySearchKey(keyNameString));

            }
        }


        //bank
        if(needIncome)
        {
            if(needTime)
            {
                searchObject.addAll(bankDB.findBySearchKeyAndTime(keyNameString,start.getTime(),end.getTime()));
            }else {
                searchObject.addAll(bankDB.findBySearchKey(keyNameString));
            }
        }

        //goal
        if(needGoal)
        {
            if(needTime)
            {
                searchObject.addAll(goalDB.findSearchKey(keyNameString,start.getTime(),end.getTime()));
            }else {
                searchObject.addAll(goalDB.findSearchKey(keyNameString));
            }
        }



        //property   //propertyFromDB
        if(needProperty)
        {
            searchObject.addAll(propertyDB.findBySearchKey(keyNameString));
            if(needTime)
            {
                searchObject.addAll(propertyFromDB.findBySearchKey(keyNameString,start.getTime(),end.getTime()));
            }else{
                searchObject.addAll(propertyFromDB.findBySearchKey(keyNameString));
            }
        }


        if(searchObject.isEmpty())
        {
            Common.showToast(context,"查無資料!");
            message.setVisibility(View.VISIBLE);
            message.setText("查無資料! ");
        }else{

            Common.showToast(context,"搜尋成功!");
            message.setVisibility(View.GONE);
        }


        listView.setAdapter(new ListAdapter(context,searchObject));
        listView.setSelection(p);
        searchSettingShow.setVisibility(View.VISIBLE);
        searchSettingShow.setText(showScope());
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
            result.startResolutionForResult(context, 0);
        } catch (IntentSender.SendIntentException e) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0)
        {
            openCloud();
        } else{
            progressL.setVisibility(View.GONE);
            if (resultCode == -1) {
                Common.showToast(context, "上傳成功");
            } else {
                Common.showToast(context, "上傳失敗");
            }
        }
    }

    public void openCloud() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) SearchMain.this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if(mNetworkInfo==null)
        {
            Common.showToast(SearchMain.this.context,"網路沒有開啟，無法下載!");
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
                                String fileType;


                                if(fileName ==null)
                                {
                                    Common.showToast(context,"資料遺失! 請重新搜尋!");
                                    return;
                                }
                                fileName.append(".xls");
                                fileType = "File/xls";
                                outPutExcel(bitmapStream);



                                try {
                                    outputStream.write(bitmapStream.toByteArray());
                                } catch (IOException e1) {

                                }
                                // Create the initial metadata - MIME type and title.
                                // Note that the user will be able to change the title later.
                                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                        .setMimeType(fileType).setTitle(fileName.toString()).build();
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


    Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0:
                    percent.setText("0%");
                    progressL.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    progressL.setVisibility(View.GONE);
                    break;
                case 2:
                    percent.setText("100%");
                    progressL.setVisibility(View.GONE);
                    Common.showToast(context, "匯出成功，檔名為"+fileName.toString()+".xls，路徑為" + "/Download/"+fileName.toString()+".txt");
                    break;
                case 3:
                    progressL.setVisibility(View.GONE);
                    Common.showToast(context,"輸出失敗");
                    break;
                case 4:
                    percent.setText("100%");
                    progressL.setVisibility(View.GONE);
                    Common.showToast(context,"匯出成功，檔名為"+fileName.toString()+".xls，路徑為" + "/Download/"+fileName.toString()+".xls");
                    break;
                case 5:
                    percent.setText(String.valueOf(msg.obj));
                    break;
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient!=null)
        {
            mGoogleApiClient.disconnect();
        }
        mGoogleApiClient=null;
    }

    private class showSearch implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            setListView();
            fileName=new StringBuffer();
            if(needTime)
            {
                fileName.append(Common.sSix.format(start)+"-"+Common.sSix.format(end));
            }else{
                fileName.append(Common.sFive.format(new Date(System.currentTimeMillis())));
            }
            fileName.append(" "+keyNameString);
            if(needConsume&&needProperty&&needGoal&&needProperty)
            {
                fileName.append(" 全部");
            }else if(needConsume){

                fileName.append(" 支出");
            }else if(needIncome){

                fileName.append(" 收入");
            }else if(needGoal){

                fileName.append(" 目標");
            }else if(needProperty){
                fileName.append("資產的資料");
            }


        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
         inflater = context.getMenuInflater();
         inflater.inflate(R.menu.search_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        View v = context.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }



        switch (item.getItemId())
        {
            case R.id.setting:
                settingR.setVisibility(View.VISIBLE);
                fabBGLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.excelToGoogle:
                if(fileName==null||fileName.length()<=0)
                {
                    fileName=new StringBuffer();
                    Common.showToast(context,"資料已遺失! 請重新搜尋!");
                    return true;
                }
                if(searchObject==null||searchObject.isEmpty())
                {
                    Common.showToast(context,"沒有資料!");
                    break;
                }
                firstEnter=true;
                openCloud();
                break;
            case R.id.excelToLocal:
                if(fileName==null||fileName.length()<=0)
                {
                    fileName=new StringBuffer();
                    Common.showToast(context,"資料已遺失! 請重新搜尋!");
                    return true;
                }
                if(searchObject==null||searchObject.isEmpty())
                {
                    Common.showToast(context,"沒有資料!");
                    break;
                }
                TxtToLocal();
                break;
            default:
        }
        return true;
    }



    private void outPutExcel(OutputStream outputStream) {

        int count=0;
        t=new BigDecimal(searchObject.size());
        HSSFWorkbook workbook=null;

        try {

            //分類搜尋

            List<Object> consumeList=new ArrayList<>();
            List<BankVO> incomeList=new ArrayList<>();
            List<GoalVO> goalList=new ArrayList<>();
            List<PropertyVO> propertyVOS=new ArrayList<>();
            List<PropertyFromVO> propertyFromVOS=new ArrayList<>();

            for(Object object:searchObject)
            {
                if(object instanceof ConsumeVO || object instanceof InvoiceVO)
                {
                    consumeList.add(object);
                } else if(object instanceof BankVO)
                {
                    incomeList.add((BankVO) object);
                }else if(object instanceof GoalVO)
                {
                    goalList.add((GoalVO) object);
                }else if(object instanceof PropertyVO)
                {
                    propertyVOS.add((PropertyVO) object);
                }else if(object instanceof PropertyFromVO)
                {
                    propertyFromVOS.add((PropertyFromVO) object);
                }
            }



            workbook = new HSSFWorkbook();

            if (!consumeList.isEmpty()) {

                Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
                Sheet sheetCon = workbook.createSheet("消費");
                sheetCon.setColumnWidth(0, 11 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(1, 13 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(8, 12 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(9, 12 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(10, 100 * 256);// 調整欄位寬度
                Row rowTitle = sheetCon.createRow(0);
                rowTitle.createCell(0).setCellValue("日期");
                rowTitle.createCell(1).setCellValue("發票號碼");
                rowTitle.createCell(2).setCellValue("幣別");
                rowTitle.createCell(3).setCellValue("金額");
                rowTitle.createCell(4).setCellValue("主項目");
                rowTitle.createCell(5).setCellValue("次項目");
                rowTitle.createCell(6).setCellValue("中獎");
                rowTitle.createCell(7).setCellValue("類別");
                rowTitle.createCell(8).setCellValue("是否定期");
                rowTitle.createCell(9).setCellValue("定期頻率");
                rowTitle.createCell(10).setCellValue("細節");

                //照時間排列
                Collections.sort(consumeList, new Comparator<Object>() {
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
                for (int i = 0; i < consumeList.size(); i++) {
                    Row rowContent = sheetCon.createRow(i + 1); // 建立儲存格
                    Object o = consumeList.get(i);
                    if (o instanceof InvoiceVO) {
                        InvoiceVO invoiceVO = (InvoiceVO) o;
                        rowContent.createCell(0).setCellValue(Common.sTwo.format(new java.util.Date(invoiceVO.getTime().getTime())));
                        rowContent.createCell(1).setCellValue(invoiceVO.getInvNum());
                        rowContent.createCell(2).setCellValue(Common.getCurrency(invoiceVO.getCurrency()));
                        rowContent.createCell(3).setCellValue(invoiceVO.getRealAmount());
                        rowContent.createCell(4).setCellValue(Common.getType(invoiceVO.getMaintype()));
                        rowContent.createCell(5).setCellValue(Common.getType(invoiceVO.getSecondtype()));
                        //中獎訊息
                        try {
                            if(invoiceVO.getIswin().equals("0"))
                            {
                                rowContent.createCell(6).setCellValue("尚未對獎");
                            }else if(invoiceVO.getIswin().equals("N")){
                                rowContent.createCell(6).setCellValue("無中獎");
                            }else {
                                rowContent.createCell(6).setCellValue(Common.getPriceName().get(invoiceVO.getIswin()));
                            }
                        }catch (Exception e)
                        {
                            rowContent.createCell(6).setCellValue("尚未對獎");
                        }
                        rowContent.createCell(7).setCellValue("雲端發票");

                        rowContent.createCell(8).setCellValue("否");
                        rowContent.createCell(9).setCellValue(" ");
                        //電子發票細節
                        List<JsonObject> js=new ArrayList<>();
                        if(invoiceVO.getDetail().equals("0"))
                        {
                            ConnectivityManager mConnectivityManager = (ConnectivityManager) SearchMain.this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                            if (mNetworkInfo != null) {
                                try {
                                    new GetSQLDate(SearchMain.this, invoiceVO).execute("reDownload").get();
                                    js = gson.fromJson(invoiceVO.getDetail(), cdType);
                                } catch (Exception e) {
                                    js = new ArrayList<>();
                                }
                            }
                        }else{
                            js = gson.fromJson(invoiceVO.getDetail(), cdType);
                        }


                        StringBuilder sb=new StringBuilder();
                        float price,amout,n;
                        for (JsonObject j : js) {
                            try {
                                amout=j.get("amount").getAsFloat();
                                n = j.get("quantity").getAsFloat();
                                price = j.get("unitPrice").getAsFloat();
                                if(price==0)
                                {
                                    sb.append(j.get("description").getAsString() + " : " + (int)(amout/n) + "X" + (int)n + "=" + (int)amout + "元  ");
                                }else{
                                    sb.append(j.get("description").getAsString() + " : " + (int)price + "X" + (int)n + "=" + (int)amout + "元  ");
                                }
                            } catch (Exception e) {
                                sb.append(j.get("description").getAsString() + " : " + 0 + "X" + 0 + "=" + 0 + "元 ");
                            }
                        }

                        rowContent.createCell(10).setCellValue(sb.toString());
                         setMessage(count++);
                    } else {
                        ConsumeVO consumeVO = (ConsumeVO) o;
                        if(StringUtil.isBlank(consumeVO.getRealMoney()))
                        {
                            consumeVO.setRealMoney(String.valueOf(consumeVO.getMoney()));
                            consumeDB.update(consumeVO);
                        }
                        rowContent.createCell(0).setCellValue(Common.sTwo.format(new java.util.Date(consumeVO.getDate().getTime())));
                        rowContent.createCell(1).setCellValue(consumeVO.getNumber());
                        rowContent.createCell(2).setCellValue(Common.getCurrency(consumeVO.getCurrency()));
                        rowContent.createCell(3).setCellValue(consumeVO.getRealMoney());
                        rowContent.createCell(4).setCellValue(consumeVO.getMaintype());
                        rowContent.createCell(5).setCellValue(consumeVO.getSecondType());

                        if(StringUtil.isBlank(consumeVO.getNumber()))
                        {
                            rowContent.createCell(7).setCellValue("無發票");
                            rowContent.createCell(6).setCellValue(" ");
                        }else{
                            rowContent.createCell(7).setCellValue("紙本發票");
                            //中獎訊息
                            try {
                                if(consumeVO.getIsWin().equals("0"))
                                {
                                    rowContent.createCell(6).setCellValue("尚未對獎");
                                }else if(consumeVO.getIsWin().equals("N")){
                                    rowContent.createCell(6).setCellValue("無中獎");
                                }else {
                                    rowContent.createCell(6).setCellValue(Common.getPriceName().get(consumeVO.getIsWin()));
                                }
                            }catch (Exception e)
                            {
                                rowContent.createCell(6).setCellValue("尚未對獎");
                            }
                        }
                        boolean fixData=Boolean.valueOf(consumeVO.getFixDate());
                        boolean auto=Boolean.valueOf(consumeVO.isAuto());

                        if(auto||fixData)
                        {
                            rowContent.createCell(8).setCellValue("是");

                            JsonObject js = gson.fromJson(consumeVO.getFixDateDetail(), JsonObject.class);
                            String choicestatue = js.get("choicestatue").getAsString().trim();
                            String choicedate = js.get("choicedate").getAsString().trim();
                            String s=choicestatue+" "+choicedate;
                            rowContent.createCell(9).setCellValue(s);
                        }else {
                            rowContent.createCell(8).setCellValue("否");
                            rowContent.createCell(9).setCellValue(" ");
                        }


                        rowContent.createCell(10).setCellValue((consumeVO.getDetailname()==null?"":consumeVO.getDetailname()));
                        setMessage(count++);
                    }
                }
            }
            if (!incomeList.isEmpty()) {
                Sheet sheetCon = workbook.createSheet("收入");
                sheetCon.setColumnWidth(0, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(2, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(4, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(5, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(6, 100*256);// 調整欄位寬度
                Row rowTitle = sheetCon.createRow(0);
                rowTitle.createCell(0).setCellValue("日期");
                rowTitle.createCell(1).setCellValue("主項目");
                rowTitle.createCell(2).setCellValue("幣別");
                rowTitle.createCell(3).setCellValue("金額");
                rowTitle.createCell(4).setCellValue("是否定期");
                rowTitle.createCell(5).setCellValue("定期頻率");
                rowTitle.createCell(6).setCellValue("細節");
                for (int i = 0; i < incomeList.size(); i++) {
                    Row rowContent = sheetCon.createRow(i + 1); // 建立儲存格
                    BankVO bankVO = incomeList.get(i);
                    rowContent.createCell(0).setCellValue(Common.sTwo.format(new java.util.Date(bankVO.getDate().getTime())));
                    rowContent.createCell(1).setCellValue(bankVO.getMaintype());
                    rowContent.createCell(2).setCellValue(Common.getCurrency(bankVO.getCurrency()));
                    if(StringUtil.isBlank(bankVO.getRealMoney()))
                    {
                        bankVO.setRealMoney(String.valueOf(bankVO.getMoney()));
                        bankDB.update(bankVO);
                    }
                    rowContent.createCell(3).setCellValue(bankVO.getRealMoney());

                    boolean fixdate=Boolean.valueOf(bankVO.getFixDate());
                    boolean isAuto=Boolean.valueOf(bankVO.isAuto());
                    if(fixdate||isAuto)
                    {
                        rowContent.createCell(4).setCellValue("是");
                        JsonObject js = gson.fromJson(bankVO.getFixDateDetail(),JsonObject.class);
                        String choicestatue= js.get("choicestatue").getAsString().trim();
                        String choicedate=js.get("choicedate").getAsString().trim();
                        String s=choicestatue+" "+choicedate;
                        rowContent.createCell(5).setCellValue(s);

                    }else{
                        rowContent.createCell(4).setCellValue("否");
                        rowContent.createCell(5).setCellValue(" ");

                    }
                    rowContent.createCell(6).setCellValue(bankVO.getDetailname());
                    setMessage(count++);
                }
            }

            if(!goalList.isEmpty())
            {
                //依種類排序
                Collections.sort(goalList, new Comparator<GoalVO>() {
                    @Override
                    public int compare(GoalVO goalVO, GoalVO t1) {
                        if(goalVO.getType().equals("支出"))
                        {
                            return 1;
                        }else {
                            return -1;
                        }
                    }
                });


                Sheet sheetCon = workbook.createSheet("目標");
                sheetCon.setColumnWidth(0, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(2, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(4, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(5, 36*256);// 調整欄位寬度
                Row rowTitle = sheetCon.createRow(0);
                rowTitle.createCell(0).setCellValue("種類");
                rowTitle.createCell(1).setCellValue("名稱");
                rowTitle.createCell(2).setCellValue("幣別");
                rowTitle.createCell(3).setCellValue("金額");
                rowTitle.createCell(4).setCellValue("狀態");
                rowTitle.createCell(5).setCellValue("目標期限");
                for(int i=0;i<goalList.size();i++) {
                    Row rowContent = sheetCon.createRow(i + 1); // 建立儲存格3
                    GoalVO goalVO = goalList.get(i);
                    rowContent.createCell(0).setCellValue(goalVO.getType());
                    rowContent.createCell(1).setCellValue(goalVO.getName());
                    rowContent.createCell(2).setCellValue(Common.getCurrency(goalVO.getCurrency()));
                    rowContent.createCell(3).setCellValue(goalVO.getRealMoney());
                    String status;
                    switch (goalVO.getStatue()) {

                        case 1:
                            status = "完成";
                            break;
                        case 2:
                            status = "失敗";
                            break;
                        default:
                            status = "進行中";
                            break;
                    }
                    rowContent.createCell(4).setCellValue(status);

                    switch (goalVO.getTimeStatue()) {
                        case "每天":
                        case "每周":
                        case "每月":
                        case "每年":
                            rowContent.createCell(5).setCellValue(goalVO.getTimeStatue());
                            break;
                        default:
                            rowContent.createCell(5).setCellValue(Common.sTwo.format(goalVO.getStartTime()) + " ~ " + Common.sTwo.format(goalVO.getEndTime()));
                            break;
                    }
                    setMessage(count++);
                }
            }

            if(!propertyVOS.isEmpty())
            {
                Sheet sheetCon = workbook.createSheet("財產");
                sheetCon.setColumnWidth(0, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(2, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(3, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(4, 12*256);// 調整欄位寬度
                Row rowTitle = sheetCon.createRow(0);
                rowTitle.createCell(0).setCellValue("名稱");
                rowTitle.createCell(1).setCellValue("幣別");
                rowTitle.createCell(2).setCellValue("總金額");
                rowTitle.createCell(3).setCellValue("總支出");
                rowTitle.createCell(4).setCellValue("總收入");
                for(int i=0;i<propertyVOS.size();i++)
                {
                    Row rowContent = sheetCon.createRow(i + 1); // 建立儲存格3
                    PropertyVO propertyVO=propertyVOS.get(i);
                    rowContent.createCell(0).setCellValue(propertyVO.getName());
                    rowContent.createCell(1).setCellValue(Common.getCurrency(propertyVO.getCurrency()));
                    rowContent.createCell(2).setCellValue(propertyVO.getConsumeAll());
                    rowContent.createCell(3).setCellValue(propertyVO.getIncomeAll());
                    setMessage(count++);
                }

            }


            if(!propertyFromVOS.isEmpty())
            {
                Collections.sort(propertyFromVOS, new Comparator<PropertyFromVO>() {
                    @Override
                    public int compare(PropertyFromVO propertyFromVO, PropertyFromVO t1) {

                        int answer=propertyFromVO.getPropertyId().compareTo(t1.getPropertyId());
                        if(answer==0)
                        {
                            answer=propertyFromVO.getType().compareTo(propertyFromVO.getType());
                        }
                        return answer;
                    }
                });
                Sheet sheetCon = workbook.createSheet("財產來源");
                sheetCon.setColumnWidth(0, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(1, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(5, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(6, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(8, 12*256);// 調整欄位寬度
                sheetCon.setColumnWidth(9, 12*256);// 調整欄位寬度
                Row rowTitle = sheetCon.createRow(0);
                rowTitle.createCell(0).setCellValue("隸屬資產");
                rowTitle.createCell(1).setCellValue("來源時間");
                rowTitle.createCell(2).setCellValue("來源類別");
                rowTitle.createCell(3).setCellValue("來源幣別");
                rowTitle.createCell(4).setCellValue("來源金額");
                rowTitle.createCell(5).setCellValue("來源主類別");
                rowTitle.createCell(6).setCellValue("來源次類別");
                rowTitle.createCell(7).setCellValue("手續費");
                rowTitle.createCell(8).setCellValue("是否定期");
                rowTitle.createCell(9).setCellValue("定期頻率");
                for (int i=0;i<propertyFromVOS.size();i++)
                {
                    Row rowContent = sheetCon.createRow(i + 1); // 建立儲存格3
                    PropertyFromVO propertyFromVO=propertyFromVOS.get(i);
                    PropertyVO propertyVO=propertyDB.findById(propertyFromVO.getPropertyId());
                    rowContent.createCell(0).setCellValue(propertyVO.getName());
                    rowContent.createCell(1).setCellValue(Common.sTwo.format(propertyFromVO.getSourceTime()));
                    rowContent.createCell(2).setCellValue(propertyFromVO.getType().getNarrative());
                    rowContent.createCell(3).setCellValue(Common.getCurrency(propertyFromVO.getSourceCurrency()));
                    rowContent.createCell(4).setCellValue(propertyFromVO.getSourceMoney());
                    rowContent.createCell(5).setCellValue(propertyFromVO.getSourceMainType());
                    rowContent.createCell(6).setCellValue(propertyFromVO.getSourceSecondType()==null?" ":propertyFromVO.getSourceSecondType());
                    rowContent.createCell(7).setCellValue(propertyFromVO.getImportFee()==null?"0":propertyFromVO.getImportFee());

                    if(propertyFromVO.getFixImport())
                    {
                        rowContent.createCell(8).setCellValue("是");
                        StringBuilder stringBuilder=new StringBuilder();
                        stringBuilder.append(propertyFromVO.getFixDateCode().getDetail());
                        if(!StringUtil.isBlank(propertyFromVO.getFixDateDetail()))
                        {
                            stringBuilder.append(" "+propertyFromVO.getFixDateDetail());
                        }
                        rowContent.createCell(9).setCellValue(stringBuilder.toString());
                    }else {
                        rowContent.createCell(8).setCellValue("否");
                        rowContent.createCell(9).setCellValue(" ");
                    }
                    setMessage(count++);
                }


            }
            workbook.write(outputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(workbook!=null)
            {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outputStream!=null)
            {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private void TxtToLocal() {
        progressL.setVisibility(View.VISIBLE);
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                FileOutputStream fs=null;
                File file=null;
                try {
                    File dir = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    file = new File(dir, fileName.toString()+".xls");
                    fs = new FileOutputStream(file);
                    outPutExcel(fs);
                    Message message=handler.obtainMessage();
                    message.what=4;
                    message.sendToTarget();

                } catch (Exception e) {
                    Message message=handler.obtainMessage();
                    message.what=3;
                    message.sendToTarget();
                }finally {
                    if(fs!=null)
                    {
                        try {
                            fs.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        new Thread(runnable).start();
    }





    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<Object> objects;

        ListAdapter(Context context, List<Object> objects) {
            this.context = context;
            this.objects = objects;
        }

        public void setObjects(List<Object> objects) {
            this.objects = objects;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.search_main_list_item, parent, false);
            }
            TextView title = itemView.findViewById(R.id.listTitle);
            TextView describe = itemView.findViewById(R.id.listDetail);
            BootstrapButton update = itemView.findViewById(R.id.updateD);
            BootstrapButton deleteI = itemView.findViewById(R.id.deleteI);
            LinearLayout fixL = itemView.findViewById(R.id.fixL);
            BootstrapButton fixT = itemView.findViewById(R.id.fixT);
            LinearLayout remindL = itemView.findViewById(R.id.remindL);
            LinearLayout typeL = itemView.findViewById(R.id.typeL);
            BootstrapButton typeT = itemView.findViewById(R.id.typeT);

            //新增ele Type
            LinearLayout eleTypeL=itemView.findViewById(R.id.eleTypeL);
            BootstrapButton eleTypeT=itemView.findViewById(R.id.eleTypeT);


            BootstrapButton scopeT=itemView.findViewById(R.id.scopeT);


            final Object o = objects.get(position);

            if (o instanceof InvoiceVO) {
                final InvoiceVO I = (InvoiceVO) o;
                StringBuffer sbDescribe = new StringBuffer();
                scopeT.setText("支出");
                //設定標籤
                remindL.setVisibility(View.GONE);
                fixL.setVisibility(View.GONE);

                typeL.setVisibility(View.VISIBLE);
                typeT.setText("雲端發票");
                typeT.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                //設定雲端發票種類
                try {
                    eleTypeL.setVisibility(View.VISIBLE);
                    eleTypeT.setText(Common.CardType().get(I.getCardType().trim()));
                    eleTypeT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                }catch (Exception e)
                {
                    eleTypeL.setVisibility(View.GONE);
                }

                //set detail
                if (I.getDetail().equals("0")) {
                    update.setText("下載");
                    sbDescribe.append("無資料，請按下載\n  \n ");
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ConnectivityManager mConnectivityManager = (ConnectivityManager) SearchMain.this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                            if (mNetworkInfo != null) {
                                p = position;
                                new GetSQLDate(SearchMain.this, I).execute("reDownload");
                                progressDialog=new ProgressDialog(context);
                                progressDialog.setMessage("正在下傳資料,請稍候...");
                                progressDialog.show();
                            } else {
                                Common.showToast(SearchMain.this.context, "網路沒有開啟，無法下載!");
                            }
                        }
                    });
                } else {
                    update.setText("修改");
                    Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
                    List<JsonObject> js = gson.fromJson(I.getDetail(), cdType);
                    float amout,n;
                    for (JsonObject j : js) {
                        try {
                            amout=j.get("amount").getAsFloat();
                        } catch (Exception e) {
                            amout=0;
                        }
                        try {
                            n = j.get("quantity").getAsFloat();
                        } catch (Exception e) {
                            n=0;
                        }
                        if(n!=0)
                        {
                            sbDescribe.append(j.get("description").getAsString() + " : \n" + (int)(amout/n) + "X" + (int)n + "=" + (int)amout + "元\n");
                        }else{
                            sbDescribe.append(j.get("description").getAsString() + " : \n" + (int)amout + "X" + 1 + "=" + (int)amout + "元\n");
                        }
                    }

                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            p = position;
                            Fragment fragment = new UpdateInvoice();
                            Bundle bundle = getOldBundle();
                            bundle.putSerializable("invoiceVO", I);

                            fragment.setArguments(bundle);
                            switchFragment(fragment);
                        }
                    });
                }

                title.setText(Html.fromHtml(Common.KeyToRed(Common.setSecInvoiceTittle(I),keyNameString)), TextView.BufferType.SPANNABLE);
                describe.setText(Html.fromHtml(Common.KeyToRed(sbDescribe.toString(),keyNameString)), TextView.BufferType.SPANNABLE);
            } else if (o instanceof ConsumeVO) {
                update.setText("修改");
                final ConsumeVO c = (ConsumeVO) o;

                scopeT.setText("支出");
                //紙本發票種類
                eleTypeL.setVisibility(View.GONE);


                typeL.setVisibility(View.VISIBLE);
                if(c.getNumber()==null||c.getNumber().trim().length()<=0)
                {
                    typeT.setText("無發票");
                    typeT.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                }else{
                    typeT.setText("紙本發票");
                    typeT.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                }


                //set Notify
                if (Boolean.valueOf(c.getNotify())) {
                    remindL.setVisibility(View.VISIBLE);
                } else {
                    remindL.setVisibility(View.GONE);
                }

                //設定 title
                title.setText(Html.fromHtml(Common.KeyToRed(Common.setSecConsumerTittlesDay(c),keyNameString)), TextView.BufferType.SPANNABLE);



                //設定 describe
                StringBuffer stringBuffer = new StringBuffer();
                fixL.setVisibility(View.GONE);
                if (c.isAuto()) {
                    fixT.setText("自動");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                    fixL.setVisibility(View.VISIBLE);
                    try {
                        JsonObject js = gson.fromJson(c.getFixDateDetail(), JsonObject.class);
                        stringBuffer.append(js.get("choicestatue").getAsString().trim());
                        stringBuffer.append(" " + js.get("choicedate").getAsString().trim());
                        boolean noweek = Boolean.parseBoolean(js.get("noweek").getAsString());
                        if (js.get("choicestatue").getAsString().trim().equals("每天") && noweek) {
                            stringBuffer.append(" 假日除外");
                        }
                    }catch (Exception e)
                    {
                        stringBuffer.append(" ");
                    }
                    stringBuffer.append("\n");
                }


                if (c.getFixDate()!=null&&c.getFixDate().equals("true")) {

                    fixT.setText("固定");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                    fixL.setVisibility(View.VISIBLE);
                    try {
                        JsonObject js = gson.fromJson(c.getFixDateDetail(), JsonObject.class);
                        stringBuffer.append(js.get("choicestatue").getAsString().trim());
                        stringBuffer.append(" " + js.get("choicedate").getAsString().trim());
                        boolean noweek = Boolean.parseBoolean(js.get("noweek").getAsString());
                        if (js.get("choicestatue").getAsString().trim().equals("每天") && noweek) {
                            stringBuffer.append(" 假日除外");
                        }
                    }catch (Exception e)
                    {
                        stringBuffer.append(" ");
                    }
                    stringBuffer.append("\n");
                }


                stringBuffer.append((c.getDetailname()==null?"":c.getDetailname()));
                if(stringBuffer.indexOf("\n")==-1)
                {
                    stringBuffer.append("\n");
                }

                describe.setText(Html.fromHtml(Common.KeyToRed(stringBuffer.toString(),keyNameString)), TextView.BufferType.SPANNABLE);

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        p = position;
                        Fragment fragment = new UpdateSpend();
                        Bundle bundle = getOldBundle();
                        bundle.putSerializable("consumeVO", c);
                        bundle.putSerializable("position", position);
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });
            }else if(o instanceof BankVO)
            {
                BankVO bankVO= (BankVO) o;
                scopeT.setText("收入");

                //設定 describe
                StringBuffer stringBuffer = new StringBuffer();
                if (bankVO.isAuto()) {
                    fixT.setText("自動");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                    fixL.setVisibility(View.VISIBLE);
                    try {
                        JsonObject js = gson.fromJson(bankVO.getFixDateDetail(), JsonObject.class);
                        String daystatue = js.get("choicestatue").getAsString().trim();
                        stringBuffer.append(daystatue);
                        if (!daystatue.equals("每天")) {
                            stringBuffer.append(" " + js.get("choicedate").getAsString().trim());
                        }
                    }catch (Exception e)
                    {
                        stringBuffer.append(" ");
                    }
                    stringBuffer.append("\n");
                }

                if (bankVO.getFixDate()!=null&&bankVO.getFixDate().equals("true")) {
                    fixT.setText("固定");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                    fixL.setVisibility(View.VISIBLE);
                    try {
                        JsonObject js = gson.fromJson(bankVO.getFixDateDetail(), JsonObject.class);
                        String daystatue = js.get("choicestatue").getAsString().trim();
                        stringBuffer.append(daystatue);
                        if (!daystatue.equals("每天")) {
                            stringBuffer.append(" " + js.get("choicedate").getAsString().trim());
                        }
                    }catch (Exception e)
                    {
                        stringBuffer.append(" ");
                    }
                    stringBuffer.append("\n");
                }
                stringBuffer.append(bankVO.getDetailname());
                if(stringBuffer.indexOf("\n")==-1)
                {
                    stringBuffer.append("\n");
                }

                //設定 title
                title.setText(Html.fromHtml(Common.KeyToRed(Common.setBankTittlesDay(bankVO),keyNameString)), TextView.BufferType.SPANNABLE);
                describe.setText(Html.fromHtml(Common.KeyToRed(stringBuffer.toString(),keyNameString)), TextView.BufferType.SPANNABLE);



                update.setText("修改");
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        p = position;
                        Bundle bundle =getOldBundle();
                        bundle.putSerializable("bankVO", bankVO);
                        Fragment fragment = new UpdateIncome();
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });

            }else if(o instanceof GoalVO)
            {
                scopeT.setText("目標");
                GoalVO goalVO= (GoalVO) o;
                String timeDec = goalVO.getTimeStatue().trim();
                int serial = 1 ;
                StringBuffer sb = new StringBuffer();
                if (timeDec.equals("今日")) {
                    //描述目標
                    sb.append(" " + serial + ". 起日 : " + Common.sTwo.format(goalVO.getStartTime()).trim());
                    serial++;
                    sb.append("\n " + serial + ". 訖日 : " + Common.sTwo.format(goalVO.getEndTime()).trim());
                    serial++;
                    sb.append("\n " + serial + ". " + goalVO.getType().trim() + " : " + Common.getCurrency(goalVO.getCurrency()) + " " + goalVO.getRealMoney());
                    serial++;
                }else{
                    sb.append(" "+serial + ". ");
                    sb.append(timeDec + goalVO.getType().trim() + " "+Common.getCurrency(goalVO.getCurrency())+" "+goalVO.getRealMoney());
                    serial++;
                }

                title.setText(Html.fromHtml(Common.KeyToRed(goalVO.getName(),keyNameString)), TextView.BufferType.SPANNABLE);

                sb.append("\n");
                describe.setText(sb.toString());

                boolean updateGoal;
                if (goalVO.getStatue() == 1) {
                    fixT.setText("達成");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                    updateGoal = false;
                } else if (goalVO.getStatue() == 2) {
                    fixT.setText("失敗");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                    updateGoal = false;
                } else {
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                    fixT.setText("進行中");
                    updateGoal = true;
                }
                //notify
                StringBuilder nbNotify = new StringBuilder();
                if (goalVO.isNotify()) {
                    serial++;
                    nbNotify.append(" " + serial + ". 提醒 : " + goalVO.getNotifyStatue().trim()).append(" " + goalVO.getNotifyDate());
                    if (goalVO.isNoWeekend() && goalVO.getNotifyStatue().trim().equals("每天")) {
                        nbNotify.append("假日除外");
                    }
                    remindL.setVisibility(View.VISIBLE);
                } else {
                    remindL.setVisibility(View.GONE);
                }
                if (updateGoal) {
                    update.setVisibility(View.VISIBLE);
                    update.setBackgroundColor(Color.parseColor("#33CCFF"));
                    update.setText("修改");
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            p=position;
                            Bundle bundle =getOldBundle();
                            Fragment fragment = new GoalUpdate();
                            bundle.putSerializable("goalVO", goalVO);
                            bundle.putSerializable("position", position);
                            fragment.setArguments(bundle);
                            switchFragment(fragment);
                        }
                    });
                } else {
                    update.setVisibility(View.INVISIBLE);
                }
            }else if(o instanceof PropertyVO)
            {
                scopeT.setText("資產");
                PropertyVO propertyVO= (PropertyVO) o;
                CurrencyVO currencyVO=currencyDB.getOneByType(propertyVO.getCurrency());
                Double consume=propertyFromDB.totalType(propertyVO.getId(), PropertyType.Negative);
                Double income=propertyFromDB.totalType(propertyVO.getId(), PropertyType.Positive);
                Double total=income-consume;
                String titleP=propertyVO.getName()+" "+ Common.CurrencyResult(total,currencyVO);
                String detailE="收入 "+ Common.CurrencyResult(income,currencyVO)+"\n" +
                        "支出 "+ Common.CurrencyResult(consume,currencyVO);

                propertyVO.setConsumeAll(Common.CurrencyResult(consume,currencyVO));
                propertyVO.setIncomeAll(Common.CurrencyResult(income,currencyVO));
                title.setText(Html.fromHtml(Common.KeyToRed(titleP,keyNameString)), TextView.BufferType.SPANNABLE);
                describe.setText(detailE);
                update.setText("修改");
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        p=position;
                        Bundle bundle =getOldBundle();
                        Fragment fragment = new PropertyUpdate();
                        bundle.putSerializable(Common.propertyID, propertyVO.getId());
                        bundle.putSerializable("position", position);
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });
            }else if(o instanceof PropertyFromVO)
            {

                PropertyFromVO propertyFromVO= (PropertyFromVO) o;
                PropertyVO propertyVO=propertyDB.findById(propertyFromVO.getPropertyId());
                scopeT.setText("資產來源");

                typeL.setVisibility(View.VISIBLE);
                typeT.setText(propertyVO.getName());

                fixL.setVisibility(View.GONE);
                if (!StringUtil.isBlank(propertyFromVO.getFixFromId())) {
                    fixT.setText("自動");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                    fixL.setVisibility(View.VISIBLE);
                }

                if(propertyFromVO.getFixImport())
                {
                    fixT.setText("固定");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                    fixL.setVisibility(View.VISIBLE);
                }





                StringBuilder titleProperty=new StringBuilder();


                if(StringUtil.isBlank(propertyFromVO.getSourceSecondType()))
                {
                    titleProperty.append(propertyFromVO.getSourceMainType());
                }else{
                    titleProperty.append(propertyFromVO.getSourceSecondType());
                }


                titleProperty.append(" "+ Common.getCurrency(propertyFromVO.getSourceCurrency()));
                titleProperty.append(" "+ Common.doubleRemoveZero(Double.valueOf(propertyFromVO.getSourceMoney())));


                title.setText(Html.fromHtml(Common.KeyToRed(titleProperty.toString(),keyNameString)), TextView.BufferType.SPANNABLE);


                StringBuilder detail=new StringBuilder();
                detail.append("1. 日期 : "+ Common.sTwo.format(propertyFromVO.getSourceTime())+" \n");
                detail.append("2. 手續費 : ");
                detail.append(Common.getCurrency(propertyFromVO.getSourceCurrency())).append(propertyFromVO.getImportFee()+"\n");
                if(propertyFromVO.getFixImport())
                {
                    if(StringUtil.isBlank(propertyFromVO.getSourceSecondType()))
                    {
                        detail.append("3. 定期匯入 : ").append(propertyFromVO.getFixDateCode().getDetail());
                    }else{
                        detail.append("3. 定期支出 : ").append(propertyFromVO.getFixDateCode().getDetail());
                    }
                    if(propertyFromVO.getFixDateDetail()!=null)
                    {
                        detail.append(" "+propertyFromVO.getFixDateDetail());
                    }
                }


                describe.setText(Html.fromHtml(Common.KeyToRed(detail.toString(),keyNameString)), TextView.BufferType.SPANNABLE);

                update.setText("修改");
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment=null;
                        switch (propertyFromVO.getType())
                        {
                            case Positive:
                                fragment=new PropertyUpdateMoney();
                                break;
                            case Negative:
                                fragment=new PropertyUpdateConsume();
                                break;

                        }
                        p=position;
                        Bundle bundle=getOldBundle();
                        bundle.putSerializable(Common.propertyFromVoId,propertyFromVO.getId());
                        bundle.putSerializable(Common.fragment,Common.searchMainString);

                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });
            }





            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteDialogFragment aa = new DeleteDialogFragment();
                    aa.setObject(o);
                    aa.setFragment(SearchMain.this);
                    aa.show(getFragmentManager(), "show");
                }
            });
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }



    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add(Common.searchMainString);
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }


    private class checkChoice implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            needTime=b;
            showStringTime=new StringBuilder();
            showStringTime.append("時間 :");
            if(b)
            {
                beginL.setVisibility(View.VISIBLE);
                endL.setVisibility(View.VISIBLE);
                showStringTime.append(beginD.getText().toString()+" ~ "+endD.getText().toString());

            }else{
                beginL.setVisibility(View.GONE);
                endL.setVisibility(View.GONE);
                showStringTime.append("全部");
            }
        }
    }



    private class choiceDay implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            showDate.setVisibility(View.VISIBLE);
            dateView=view;
        }
    }

    private class choiceDate implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String choiceDate=datePicker.getYear()+"/"+String.valueOf(datePicker.getMonth()+1)+"/"+datePicker.getDayOfMonth();
            EditText showView= (EditText) dateView;
            showView.setText(choiceDate);
            showView.setSelection(choiceDate.length());
            showDate.setVisibility(View.GONE);
        }
    }

    private Date stringToDate(String s)
    {
        String[] dates = s.split("/");
        Calendar c = Calendar.getInstance();
        c.set(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 12, 0, 0);
        Date d = new Date(c.getTimeInMillis());
        return d;
    }

    private String showScope()
    {
        StringBuilder sb=new StringBuilder();


        int searchCount=0;
        if(needConsume)
        {
            sb.append("支出 ");
            searchCount++;
        }
        if(needIncome)
        {
            sb.append("收入 ");
            searchCount++;
        }
        if(needGoal)
        {
            sb.append("目標 ");
            searchCount++;
        }
        if(needProperty)
        {
            sb.append("資產 ");
            searchCount++;
        }

        if(searchCount>=4)
        {
            sb=new StringBuilder();
            sb.append("範圍 : 全部");
        }else{
            sb.insert(0,"範圍 : ");
        }


        sb.append("\n時間 : ");

        if(needTime)
        {
            sb.append(beginD.getText().toString()+"~"+endD.getText().toString());
        }else {
            sb.append("全部");
        }
        return sb.toString();
    }


    private void setMessage(int count)
    {

        Message message=new Message();
        message.what=5;
        c=new BigDecimal(count);
        if(c.compareTo(t)>=1)
        {
            c=t;
        }
        message.obj= c.divide(t,4, RoundingMode.HALF_UP).multiply(hundred).setScale(1,BigDecimal.ROUND_HALF_UP)+"%";
        handler.sendMessage(message);
    }

}

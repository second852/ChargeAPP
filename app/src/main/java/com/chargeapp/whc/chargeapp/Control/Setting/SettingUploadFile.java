package com.chargeapp.whc.chargeapp.Control.Setting;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ElePeriodDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
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
import com.chargeapp.whc.chargeapp.Drobox.UploadFileTask;
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
import com.chargeapp.whc.chargeapp.TypeCode.RequestCode;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.files.FileMetadata;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jsoup.internal.StringUtil;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SettingUploadFile extends Fragment {


    private static final String TAG =SettingUploadFile.class.getName();
    private ListView listView;
    private LinearLayout fileChoice;
    private ImageView excel, txtFile, cancelF;
    private Spinner choiceT;
    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private BankDB bankDB;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private BankTypeDB bankTypeDB;
    private GoalDB goalDB;
    private CarrierDB carrierDB;
    private PriceDB priceDB;
    public static int position;
    private boolean local, consume, income, all, show = true, txt,needGoal,needProperty;
    private RelativeLayout progressL;
    private ElePeriodDB elePeriodDB;
    private PropertyFromDB propertyFromDB;
    private PropertyDB propertyDB;
    private Activity context;
    private Type cdType;
    private Gson gson;
    private StringBuffer sb;
    private String fileNameTemp;
    private List<ConsumeVO> consumeVOList;
    private List<InvoiceVO> invoiceVOS;
    private List<BankVO> bankVOS;
    private List<GoalVO> goalVOS;
    private List<PropertyVO> propertyVOS;
    private List<PropertyFromVO> propertyFromVOS;
    private TextView percent;
    private int totalDataCount;
    private BigDecimal hundred=new BigDecimal(100),c,t;
    public static boolean dropboxOpen;
    private Uri uri;

    //----訊息處理

    Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            String fileName=getFileName();
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
                    Common.showToast(context, "匯出成功，檔名為"+fileName+".txt，路徑為" + "/Download/"+fileName+".txt");
                    break;
                case 3:
                    progressL.setVisibility(View.GONE);
                    Common.showToast(context,"輸出失敗");
                    break;
                case 4:
                    percent.setText("100%");
                    progressL.setVisibility(View.GONE);
                    Common.showToast(context,"匯出成功，檔名為"+fileName+"xls，路徑為" + "/Download/"+fileName+".xls");
                    break;
                case 5:
                    percent.setText(String.valueOf(msg.obj));
                    break;
            }
            super.handleMessage(msg);
        }
    };

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
        context.setTitle("匯出檔案");
        View view = inflater.inflate(R.layout.setting_upload, container, false);
        Common.setChargeDB(context);
        cdType = new TypeToken<List<JsonObject>>() {}.getType();
        gson=new Gson();
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB);
        bankDB = new BankDB(MainActivity.chargeAPPDB);
        typeDB = new TypeDB(MainActivity.chargeAPPDB);
        bankTypeDB = new BankTypeDB(MainActivity.chargeAPPDB);
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB);
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB);
        goalDB = new GoalDB(MainActivity.chargeAPPDB);
        elePeriodDB=new ElePeriodDB(MainActivity.chargeAPPDB);
        priceDB=new PriceDB(MainActivity.chargeAPPDB);
        propertyDB=new PropertyDB(MainActivity.chargeAPPDB);
        propertyFromDB=new PropertyFromDB(MainActivity.chargeAPPDB);
        List<EleMainItemVO> itemSon = getNewItem();
        listView = view.findViewById(R.id.list);
        fileChoice = view.findViewById(R.id.fileChoice);
        excel = view.findViewById(R.id.excel);
        txtFile = view.findViewById(R.id.txtFile);
        cancelF = view.findViewById(R.id.cancelF);
        choiceT = view.findViewById(R.id.choiceT);
        progressL=view.findViewById(R.id.progressL);
        percent =view.findViewById(R.id.percent);

        excel.setOnClickListener(new excelOnClick());
        txtFile.setOnClickListener(new txtOnClick());
        cancelF.setOnClickListener(new cancelOnClick());
        listView.setAdapter(new ListAdapter(context, itemSon));

        setSpinner();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(dropboxOpen) {
            try {
                dropBoxAction();
            }catch (Exception e){
                Log.e(TAG, "onResume: ",e);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        RequestCode resultCodeEnum= RequestCode.getEnum(requestCode);
        switch (resultCodeEnum) {
            case UpLoadGoogleUpload:
                progressL.setVisibility(View.GONE);
                if (resultCode == -1) {
                    Common.showToast(context, "上傳成功");
                } else {
                    Common.showToast(context, "上傳失敗");
                }
                break;
        }
    }

   //---dropBox 上傳
   private void dropBoxAction() throws Exception{
       dropboxOpen=false;
       try {
           DropboxClientFactory.per(SettingUploadFile.this.context);
           DropboxClientFactory.getClient();
       }catch (Exception e){
           Log.e(TAG, "dropBoxAction: ",e);
           Common.showToast(context,"讀取DropBox失敗!");
           return;
       }
       progressL.setVisibility(View.VISIBLE);
       percent.setText("0%");
       String fileName=getFileName();
       ExecutorService executor = Executors.newSingleThreadExecutor();
       DropBoxFileOut dropBoxFileOut=new DropBoxFileOut();
       Future<byte[]> dataFuture=executor.submit(dropBoxFileOut);
       while (!dataFuture.isDone()){}
       byte[] dataResult=dataFuture.get();
       if(dataResult==null){
           Common.showToast(context,"上傳檔案失敗!");
           return;
       }
       new UploadFileTask(context, DropboxClientFactory.getClient(), new UploadFileTask.Callback() {
           @Override
           public void onUploadComplete(FileMetadata result) {
               progressL.setVisibility(View.GONE);
               Common.showToast(context,"上傳成功，檔案位置:"+FileActivity.mPath + "/" + fileName);
           }
           @Override
           public void onError(Exception e) {
               Log.e(TAG, "Failed to upload file.", e);
               Toast.makeText(context,
                       "An error has occurred",
                       Toast.LENGTH_SHORT)
                       .show();
           }
       },dataResult).execute(FileActivity.mPath, fileName);
   }

   public class DropBoxFileOut implements Callable<byte[]>{
       @Override
       public byte[] call() throws Exception {
           Thread.sleep(5);
           byte[] result;
           if(txt){
               result=outPutTxt();
           }else{
               result=outputExcel();
           }
           return result;
       }
   }


    //-----view
    private void setSpinner() {
        ArrayList<String> spinnerItem = new ArrayList();
        spinnerItem.add("備分資料庫");
        spinnerItem.add("全部");
        spinnerItem.add("支出");
        spinnerItem.add("收入");
        spinnerItem.add("目標");
        spinnerItem.add("資產");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinneritem, spinnerItem);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        choiceT.setAdapter(arrayAdapter);
        choiceT.setOnItemSelectedListener(new choiceAction());
        choiceT.setSelection(position);
    }

    private List<EleMainItemVO> getNewItem() {
        List<EleMainItemVO> eleMainItemVOList = new ArrayList<>();
        eleMainItemVOList.add(new EleMainItemVO("匯出資料到本機", R.drawable.importf,0));
//        eleMainItemVOList.add(new EleMainItemVO("匯出資料到Google雲端", R.drawable.importf,1));
        eleMainItemVOList.add(new EleMainItemVO("匯出資料到Dropbox", R.drawable.importf,2));
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

            } else if(eleMainItemVO.getCode() == 2){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        local = false;
                        dropboxOpen=true;
                        if (SettingUploadFile.this.position == 0) {
                            txt = false;
                            Auth.startOAuth2PKCE(context, getString(R.string.DropboxKey), DbxRequestConfigFactory.getRequestConfig(), DbxRequestConfigFactory.scope);
                        } else {
                            if (show) {
                                fileChoice.setVisibility(View.VISIBLE);
                                show = false;
                            }
                        }
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




    //------File

    private String getFileName(){
        String fileName=fileNameTemp+Common.sSeven.format(new Date());
        if(txt){
            fileName+=".txt";
        }else{
            fileName+=".xls";
        }
        return fileName;
    }

    private void FileTOLocal() {
        progressL.setVisibility(View.VISIBLE);
        LocalFileOut fileOut=new LocalFileOut();
        new Thread(fileOut).start();
    }

    public class LocalFileOut implements Runnable{
        @Override
        public void run() {
            uri=Common.insertFileIntoMediaStore(context,new File(SettingUploadFile.this.getFileName()));
            ContentResolver contentResolver = context.getContentResolver();
            FileOutputStream outputStream=null;
            try{
                ParcelFileDescriptor parcelFileDescriptor= contentResolver.openFileDescriptor(uri, "w");
                outputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
                byte[] data;
                if(txt){
                    data=outPutTxt();
                }else{
                    data=outputExcel();
                }
                outputStream.write(data);
            }catch (Exception e){
                Log.e(TAG, "run: ",e);
                Message message=handler.obtainMessage(3);
                message.sendToTarget();
            }finally {
                if(outputStream!=null){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private byte[] outPutTxt() throws IOException {
        ByteArrayOutputStream result=new ByteArrayOutputStream();
        try {
            int count=0;
            OutputStreamWriter ow = new OutputStreamWriter(result);
            BufferedWriter bw = new BufferedWriter(ow);
            if (consume) {
                bw.write("消費資料");
                bw.write("\r\n");
                bw.append("日期 "); bw.append(File.separator);
                bw.append("主項目 ");   bw.append(File.separator);
                bw.append("次項目 "); bw.append(File.separator);
                bw.append("類別 "); bw.append(File.separator);
                bw.append("幣別 "); bw.append(File.separator);
                bw.append("金額 "); bw.append(File.separator);
                bw.append("發票號碼 "); bw.append(File.separator);
                bw.append("中獎 ");  bw.append(File.separator);
                bw.append("是否定期 "); bw.append(File.separator);
                bw.append("定期頻率 ");bw.append(File.separator);
                bw.append("賣家 "); bw.append(File.separator);
                bw.append("消費地點 "); bw.append(File.separator);
                bw.append("細節 ");bw.append(File.separator);
                bw.newLine();
                bw.write("\r\n");

                List<Object> objects = new ArrayList<>();
                objects.addAll(consumeVOList);
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
                        bw.append(Common.sSix.format(new Date(invoiceVO.getTime().getTime())) + " ");bw.append(File.separator);
                        bw.append(Common.getType(invoiceVO.getMaintype()) + " ");bw.append(File.separator);
                        bw.append(Common.getType(invoiceVO.getSecondtype()) + " "); bw.append(File.separator);
                        bw.append("雲端發票" + " "); bw.append(File.separator);
                        bw.append(Common.getCurrency(invoiceVO.getCurrency())+" "); bw.append(File.separator);
                        bw.append(invoiceVO.getRealAmount() + " "); bw.append(File.separator);
                        bw.append(invoiceVO.getInvNum() + " "); bw.append(File.separator);

                        //中獎訊息
                        try {
                            if(invoiceVO.getIswin().equals("0"))
                            {
                                bw.append("尚未對獎 "); bw.append(File.separator);
                            }else if(invoiceVO.getIswin().equals("N")){
                                bw.append("無中獎 "); bw.append(File.separator);
                            }else {
                                bw.append(Common.getPriceName().get(invoiceVO.getIswin()));
                            }
                        }catch (Exception e)
                        {
                            bw.append("尚未對獎 :"); bw.append(File.separator);
                        }
                        bw.append("否 "); bw.append(File.separator);
                        bw.append(" "); bw.append(File.separator);

                        bw.append((invoiceVO.getSellerName()==null?"":invoiceVO.getSellerName()) + " "); bw.append(File.separator);
                        bw.append((invoiceVO.getSellerAddress()==null?"":invoiceVO.getSellerAddress()) + " "); bw.append(File.separator);


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
                                    sb.append(j.get("description").getAsString() + " : " + (int)(amout/n) + "X" + (int)n + "= "+Common.getCurrency(invoiceVO.getCurrency()) + (int)amout);
                                }else{
                                    sb.append(j.get("description").getAsString() + " : " + (int)price + "X" + (int)n + "= "+Common.getCurrency(invoiceVO.getCurrency()) + (int)amout);
                                }
                            } catch (Exception e) {
                                sb.append(j.get("description").getAsString() + " : " + 0 + "X" + 0 + "= "+Common.getCurrency(invoiceVO.getCurrency()) + 0);
                            }
                        }
                        bw.append(sb.toString()); bw.append(File.separator);
                        bw.newLine();
                        bw.write("\r\n");
                        setMessage(count++);
                    } else {
                        ConsumeVO consumeVO = (ConsumeVO) o;

                        if(StringUtil.isBlank(consumeVO.getRealMoney()))
                        {
                            consumeVO.setRealMoney(String.valueOf(consumeVO.getMoney()));
                            consumeDB.update(consumeVO);
                        }

                        bw.append(Common.sSix.format(new Date(consumeVO.getDate().getTime())) + " "); bw.append(File.separator);
                        bw.append(consumeVO.getMaintype() + " "); bw.append(File.separator);
                        bw.append(consumeVO.getSecondType() + " "); bw.append(File.separator);

                        if(StringUtil.isBlank(consumeVO.getNumber()))
                        {
                            bw.append("無發票 "); bw.append(File.separator);
                            bw.append(" "); bw.append(File.separator);
                        }else{
                            bw.append("紙本發票 "); bw.append(File.separator);
                            try {
                                if(consumeVO.getIsWin().equals("0"))
                                {
                                    bw.append("尚未對獎 "); bw.append(File.separator);
                                }else if(consumeVO.getIsWin().equals("N")){
                                    bw.append("無中獎 "); bw.append(File.separator);
                                }else {
                                    bw.append(Common.getPriceName().get(consumeVO.getIsWin())); bw.append(File.separator);
                                }
                            }catch (Exception e)
                            {
                                bw.append("尚未對獎 "); bw.append(File.separator);
                            }
                        }



                        bw.append(Common.getCurrency(consumeVO.getCurrency())+" "); bw.append(File.separator);
                        bw.append(consumeVO.getRealMoney() + " "); bw.append(File.separator);
                        bw.append(consumeVO.getNumber() + " "); bw.append(File.separator);
                        //中獎訊息

                        boolean fixData=Boolean.valueOf(consumeVO.getFixDate());
                        boolean auto=Boolean.valueOf(consumeVO.isAuto());

                        if(auto||fixData)
                        {
                            bw.append("是 "); bw.append(File.separator);

                            JsonObject js = gson.fromJson(consumeVO.getFixDateDetail(), JsonObject.class);
                            String choicestatue = js.get("choicestatue").getAsString().trim();
                            String choicedate = js.get("choicedate").getAsString().trim();
                            String s=choicestatue+" "+choicedate;
                            bw.append(s); bw.append(File.separator);
                        }else {
                            bw.append("否 "); bw.append(File.separator);
                            bw.append("  "); bw.append(File.separator);
                        }

                        bw.append((consumeVO.getSellerName()==null?"":consumeVO.getSellerName()) + " "); bw.append(File.separator);
                        bw.append((consumeVO.getSellerAddress()==null?"":consumeVO.getSellerAddress()) + " "); bw.append(File.separator);
                        bw.append((consumeVO.getDetailname()==null?"":consumeVO.getDetailname().replace("\n","")) + " "); bw.append(File.separator);


                        bw.newLine();
                        bw.write("\r\n");
                    }
                }
            }
            if (income) {
                bw.write("\r\n");
                bw.write("收入資料");
                bw.write("\r\n");
                bw.append("日期 "); bw.append(File.separator);
                bw.append("主項目 "); bw.append(File.separator);
                bw.append("幣別" ); bw.append(File.separator);
                bw.append("金額 "); bw.append(File.separator);
                bw.append("是否定期 "); bw.append(File.separator);
                bw.append("定期頻率 "); bw.append(File.separator);
                bw.append("細節 "); bw.append(File.separator);
                bw.newLine();

                for (int i = 0; i < bankVOS.size(); i++) {
                    BankVO bankVO = bankVOS.get(i);
                    bw.append(Common.sSix.format(new Date(bankVO.getDate().getTime())) + " "); bw.append(File.separator);
                    bw.append(bankVO.getMaintype() + " "); bw.append(File.separator);
                    bw.append(Common.getCurrency(bankVO.getCurrency())+" "); bw.append(File.separator);

                    if(StringUtil.isBlank(bankVO.getRealMoney()))
                    {
                        bankDB.update(bankVO);
                        bw.append(bankVO.getRealMoney() + " "); bw.append(File.separator);
                    }
                    bw.append(bankVO.getRealMoney() + " "); bw.append(File.separator);
                    bw.append(bankVO.getDetailname() + " "); bw.append(File.separator);

                    boolean fixdate=Boolean.valueOf(bankVO.getFixDate());
                    boolean isAuto=Boolean.valueOf(bankVO.isAuto());
                    if(fixdate||isAuto)
                    {
                        bw.append("是 ");
                        JsonObject js = gson.fromJson(bankVO.getFixDateDetail(),JsonObject.class);
                        String choicestatue= js.get("choicestatue").getAsString().trim();
                        String choicedate=js.get("choicedate").getAsString().trim();
                        String s=choicestatue+" "+choicedate;
                        bw.append(s); bw.append(File.separator);

                    }else{
                        bw.append("否 "); bw.append(File.separator);
                        bw.append(" "); bw.append(File.separator);
                    }


                    bw.newLine();
                    bw.write("\r\n");
                    setMessage(count++);
                }
            }

            if(needGoal)
            {

                //依種類排序
                Collections.sort(goalVOS, new Comparator<GoalVO>() {
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


                bw.write("\r\n");
                bw.write("目標資料");
                bw.write("\r\n");


                bw.append("種類 "); bw.append(File.separator);
                bw.append("名稱 "); bw.append(File.separator);
                bw.append("幣別 "); bw.append(File.separator);
                bw.append("金額 "); bw.append(File.separator);
                bw.append("狀態 "); bw.append(File.separator);
                bw.append("目標期限 "); bw.append(File.separator);
                bw.newLine();

                for(int i=0;i<goalVOS.size();i++) {

                    GoalVO goalVO = goalVOS.get(i);
                    bw.append(goalVO.getType()+" "); bw.append(File.separator);
                    bw.append(goalVO.getName()+" "); bw.append(File.separator);
                    bw.append(Common.getCurrency(goalVO.getCurrency())+" "); bw.append(File.separator);
                    bw.append(goalVO.getRealMoney()+" "); bw.append(File.separator);
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
                    bw.append(status+" "); bw.append(File.separator);

                    switch (goalVO.getTimeStatue()) {
                        case "每天":
                        case "每周":
                        case "每月":
                        case "每年":
                            bw.append(goalVO.getTimeStatue()+" "); bw.append(File.separator);
                            break;
                        default:
                            bw.append(Common.sSix.format(goalVO.getStartTime()) + " ~ " + Common.sSix.format(goalVO.getEndTime())+" "); bw.append(File.separator);
                            break;
                    }
                    bw.newLine();
                    setMessage(count++);
                }
            }



            if(needProperty) {

                bw.write("\r\n");
                bw.write("資產資料");
                bw.write("\r\n");


                bw.append("名稱 "); bw.append(File.separator);
                bw.append("幣別 "); bw.append(File.separator);
                bw.append("總金額 "); bw.append(File.separator);
                bw.append("總支出 "); bw.append(File.separator);
                bw.append("總收入 "); bw.append(File.separator);
                bw.newLine();


                for (int i = 0; i < propertyVOS.size(); i++) {
                    PropertyVO propertyVO = propertyVOS.get(i);
                    bw.append(propertyVO.getName()+" "); bw.append(File.separator);
                    bw.append(Common.getCurrency(propertyVO.getCurrency())+" "); bw.append(File.separator);
                    bw.append(propertyVO.getConsumeAll()+" "); bw.append(File.separator);
                    bw.append(propertyVO.getIncomeAll()+" "); bw.append(File.separator);
                    bw.newLine();
                    setMessage(count++);
                }



                Collections.sort(propertyFromVOS, new Comparator<PropertyFromVO>() {
                    @Override
                    public int compare(PropertyFromVO propertyFromVO, PropertyFromVO t1) {

                        int answer = propertyFromVO.getPropertyId().compareTo(t1.getPropertyId());
                        if (answer == 0) {
                            answer = propertyFromVO.getType().compareTo(propertyFromVO.getType());
                        }
                        return answer;
                    }
                });

                bw.write("\r\n");
                bw.write("資產來源");
                bw.write("\r\n");


                bw.append("隸屬資產 "); bw.append(File.separator);
                bw.append("來源時間 "); bw.append(File.separator);
                bw.append("來源類別 "); bw.append(File.separator);
                bw.append("來源幣別 "); bw.append(File.separator);
                bw.append("來源金額 "); bw.append(File.separator);
                bw.append("來源主類別 "); bw.append(File.separator);
                bw.append("來源次類別 "); bw.append(File.separator);
                bw.append("手續費 "); bw.append(File.separator);
                bw.append("是否定期 "); bw.append(File.separator);
                bw.append("定期頻率 "); bw.append(File.separator);
                bw.newLine();
                for (int i = 0; i < propertyFromVOS.size(); i++) {

                    PropertyFromVO propertyFromVO = propertyFromVOS.get(i);
                    PropertyVO propertyVO = propertyDB.findById(propertyFromVO.getPropertyId());
                    bw.append(propertyVO.getName()+" "); bw.append(File.separator);
                    bw.append(Common.sSix.format(propertyFromVO.getSourceTime())+" "); bw.append(File.separator);
                    bw.append(propertyFromVO.getType().getNarrative()+" "); bw.append(File.separator);
                    bw.append(Common.getCurrency(propertyFromVO.getSourceCurrency())+" "); bw.append(File.separator);
                    bw.append(propertyFromVO.getSourceMoney()+" "); bw.append(File.separator);
                    bw.append(propertyFromVO.getSourceMainType()+" "); bw.append(File.separator);
                    bw.append(propertyFromVO.getSourceSecondType() == null ? " " : propertyFromVO.getSourceSecondType()+" "); bw.append(File.separator);
                    bw.append(propertyFromVO.getImportFee() == null ? "0" : propertyFromVO.getImportFee()+" "); bw.append(File.separator);

                    if (propertyFromVO.getFixImport()) {
                        bw.append("是 ");
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(propertyFromVO.getFixDateCode().getDetail());
                        if (!StringUtil.isBlank(propertyFromVO.getFixDateDetail())) {
                            stringBuilder.append(" " + propertyFromVO.getFixDateDetail());
                        }
                        bw.append(stringBuilder.toString()+" "); bw.append(File.separator);
                    } else {
                        bw.append("否 "); bw.append(File.separator);
                        bw.append("  "); bw.append(File.separator);
                    }
                    setMessage(count++);
                }
            }
            bw.close();
            if(local)
            {
                Message message=handler.obtainMessage();
                message.what=2;
                message.sendToTarget();
            }
            result.flush();
        } catch (IOException e) {
            Log.e(TAG,e.toString());
        }finally {
            result.close();
        }
        return result.toByteArray();
    }

    private byte[] outputExcel() throws IOException {
        ByteArrayOutputStream result=new ByteArrayOutputStream();
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            int count=0;
            if (consume) {
                Sheet sheetCon = workbook.createSheet("消費");
                sheetCon.setColumnWidth(0, 11 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(1, 13 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(8, 12 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(9, 12 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(10, 50 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(11, 50 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(12, 100 * 256);// 調整欄位寬度
                Row rowTitle = sheetCon.createRow(0);
                rowTitle.createCell(0).setCellValue("消費日期");
                rowTitle.createCell(1).setCellValue("發票號碼");
                rowTitle.createCell(2).setCellValue("幣別");
                rowTitle.createCell(3).setCellValue("金額");
                rowTitle.createCell(4).setCellValue("主項目");
                rowTitle.createCell(5).setCellValue("次項目");
                rowTitle.createCell(6).setCellValue("中獎");
                rowTitle.createCell(7).setCellValue("類別");
                rowTitle.createCell(8).setCellValue("是否定期");
                rowTitle.createCell(9).setCellValue("定期頻率");
                rowTitle.createCell(10).setCellValue("賣家");
                rowTitle.createCell(11).setCellValue("消費地點");
                rowTitle.createCell(12).setCellValue("細節");

                List<Object> objects = new ArrayList<>();
                objects.addAll(consumeVOList);
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
                        rowContent.createCell(2).setCellValue(Common.getCurrency(invoiceVO.getCurrency()));
                        rowContent.createCell(3).setCellValue(Double.valueOf(invoiceVO.getRealAmount()));
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
                        //電子發票細節
                        List<JsonObject> js=new ArrayList<>();
                        if(invoiceVO.getDetail().equals("0"))
                        {
                            ConnectivityManager mConnectivityManager = (ConnectivityManager) SettingUploadFile.this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                            if (mNetworkInfo != null) {
                                try {
                                    new GetSQLDate(SettingUploadFile.this, invoiceVO,context).execute("reDownload").get();
                                    js = gson.fromJson(invoiceVO.getDetail(), cdType);
                                } catch (Exception e) {
                                    js = new ArrayList<>();
                                }
                            }
                        }else{
                            js = gson.fromJson(invoiceVO.getDetail(), cdType);
                        }


                        sb=new StringBuffer();
                        float price,amout,n;
                        for (JsonObject j : js) {
                            try {
                                amout=j.get("amount").getAsFloat();
                                n = j.get("quantity").getAsFloat();
                                price = j.get("unitPrice").getAsFloat();
                                if(price==0)
                                {
                                    sb.append(j.get("description").getAsString() + " : " + (int)(amout/n) + "X" + (int)n + "= "+Common.getCurrency(invoiceVO.getCurrency()) + (int)amout);
                                }else{
                                    sb.append(j.get("description").getAsString() + " : " + (int)price + "X" + (int)n + "= "+ Common.getCurrency(invoiceVO.getCurrency())+ (int)amout);
                                }
                            } catch (Exception e) {
                                sb.append(j.get("description").getAsString() + " : " + 0 + "X" + 0 + "= "+Common.getCurrency(invoiceVO.getCurrency()) + 0 );
                            }
                        }


                        rowContent.createCell(8).setCellValue("否");
                        rowContent.createCell(9).setCellValue(" ");
                        rowContent.createCell(10).setCellValue(invoiceVO.getSellerName());
                        rowContent.createCell(11).setCellValue(invoiceVO.getSellerAddress());
                        rowContent.createCell(12).setCellValue(sb.toString());

                        setMessage(count++);

                    } else {
                        ConsumeVO consumeVO = (ConsumeVO) o;
                        if(StringUtil.isBlank(consumeVO.getRealMoney()))
                        {
                            consumeVO.setRealMoney(String.valueOf(consumeVO.getMoney()));
                            consumeDB.update(consumeVO);
                        }
                        rowContent.createCell(0).setCellValue(Common.sTwo.format(new Date(consumeVO.getDate().getTime())));
                        rowContent.createCell(1).setCellValue(consumeVO.getNumber());
                        rowContent.createCell(2).setCellValue(Common.getCurrency(consumeVO.getCurrency()));
                        rowContent.createCell(3).setCellValue(Double.valueOf(consumeVO.getRealMoney()));
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
                        rowContent.createCell(10).setCellValue((consumeVO.getSellerName()==null?"":consumeVO.getSellerName()));
                        rowContent.createCell(11).setCellValue((consumeVO.getSellerAddress()==null?"":consumeVO.getSellerAddress()));
                        rowContent.createCell(12).setCellValue((consumeVO.getDetailname()==null?"":consumeVO.getDetailname()));
                        setMessage(count++);
                    }
                }
            }
            if (income) {
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

                for (int i = 0; i < bankVOS.size(); i++) {
                    Row rowContent = sheetCon.createRow(i + 1); // 建立儲存格
                    BankVO bankVO = bankVOS.get(i);
                    rowContent.createCell(0).setCellValue(Common.sTwo.format(new Date(bankVO.getDate().getTime())));
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

            if(needGoal)
            {

                //依種類排序
                Collections.sort(goalVOS, new Comparator<GoalVO>() {
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
                for(int i=0;i<goalVOS.size();i++) {
                    Row rowContent = sheetCon.createRow(i + 1); // 建立儲存格3
                    GoalVO goalVO = goalVOS.get(i);
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



            if(needProperty) {
                Sheet sheetCon = workbook.createSheet("財產");
                sheetCon.setColumnWidth(0, 12 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(2, 12 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(3, 12 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(4, 12 * 256);// 調整欄位寬度
                Row rowTitle = sheetCon.createRow(0);
                rowTitle.createCell(0).setCellValue("名稱");
                rowTitle.createCell(1).setCellValue("幣別");
                rowTitle.createCell(2).setCellValue("總金額");
                rowTitle.createCell(3).setCellValue("總支出");
                rowTitle.createCell(4).setCellValue("總收入");


                for (int i = 0; i < propertyVOS.size(); i++) {
                    Row rowContent = sheetCon.createRow(i + 1); // 建立儲存格3
                    PropertyVO propertyVO = propertyVOS.get(i);
                    rowContent.createCell(0).setCellValue(propertyVO.getName());
                    rowContent.createCell(1).setCellValue(Common.getCurrency(propertyVO.getCurrency()));
                    rowContent.createCell(2).setCellValue(propertyVO.getConsumeAll());
                    rowContent.createCell(3).setCellValue(propertyVO.getIncomeAll());
                    setMessage(i);
                }

                List<PropertyFromVO> propertyFromVOS=propertyFromDB.getAll();

                Collections.sort(propertyFromVOS, new Comparator<PropertyFromVO>() {
                    @Override
                    public int compare(PropertyFromVO propertyFromVO, PropertyFromVO t1) {

                        int answer = propertyFromVO.getPropertyId().compareTo(t1.getPropertyId());
                        if (answer == 0) {
                            answer = propertyFromVO.getType().compareTo(propertyFromVO.getType());
                        }
                        return answer;
                    }
                });
                sheetCon = workbook.createSheet("財產來源");
                sheetCon.setColumnWidth(0, 12 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(1, 12 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(5, 12 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(6, 12 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(8, 12 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(9, 12 * 256);// 調整欄位寬度
                rowTitle = sheetCon.createRow(0);
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
                for (int i = 0; i < propertyFromVOS.size(); i++) {
                    Row rowContent = sheetCon.createRow(i + 1); // 建立儲存格3
                    PropertyFromVO propertyFromVO = propertyFromVOS.get(i);
                    PropertyVO propertyVO = propertyDB.findById(propertyFromVO.getPropertyId());
                    rowContent.createCell(0).setCellValue(propertyVO.getName());
                    rowContent.createCell(1).setCellValue(Common.sTwo.format(propertyFromVO.getSourceTime()));
                    rowContent.createCell(2).setCellValue(propertyFromVO.getType().getNarrative());
                    rowContent.createCell(3).setCellValue(Common.getCurrency(propertyFromVO.getSourceCurrency()));
                    rowContent.createCell(4).setCellValue(propertyFromVO.getSourceMoney());
                    rowContent.createCell(5).setCellValue(propertyFromVO.getSourceMainType());
                    rowContent.createCell(6).setCellValue(propertyFromVO.getSourceSecondType() == null ? " " : propertyFromVO.getSourceSecondType());
                    rowContent.createCell(7).setCellValue(propertyFromVO.getImportFee() == null ? "0" : propertyFromVO.getImportFee());

                    if (propertyFromVO.getFixImport()) {
                        rowContent.createCell(8).setCellValue("是");
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(propertyFromVO.getFixDateCode().getDetail());
                        if (!StringUtil.isBlank(propertyFromVO.getFixDateDetail())) {
                            stringBuilder.append(" " + propertyFromVO.getFixDateDetail());
                        }
                        rowContent.createCell(9).setCellValue(stringBuilder.toString());
                    } else {
                        rowContent.createCell(8).setCellValue("否");
                        rowContent.createCell(9).setCellValue(" ");
                    }
                    setMessage(count++);
                }
            }






            if (all) {
                //Type
                Sheet sheetCon = workbook.createSheet("Type");
                List<TypeVO> typeVOS = typeDB.getAll();
                List<TypeDetailVO> typeDetailVOS = typeDetailDB.getTypdAll();
                List<BankTypeVO> bankTypeVOS = bankTypeDB.getAll();
                List<GoalVO> goalVOS = goalDB.getAll();
                List<BankVO> bankVOS = bankDB.getAll();
                List<ConsumeVO> consumeVOS = consumeDB.getAll();
                List<InvoiceVO> invoiceVOS = invoiceDB.getAll();
                List<CarrierVO> carrierVOS = carrierDB.getAll();
                List<PriceVO> priceVOS =priceDB.getAll();
                List<ElePeriod> elePeriods = elePeriodDB.getAll();
                List<PropertyVO> propertyVOS=propertyDB.getAll();
                List<PropertyFromVO> propertyFromVOS=propertyFromDB.getAll();
                totalDataCount=typeVOS.size()+bankTypeVOS.size()+goalVOS.size()+typeDetailVOS.size()+
                        consumeVOS.size()+invoiceVOS.size()+carrierVOS.size()+
                        priceVOS.size()+elePeriods.size()+priceVOS.size()+propertyFromVOS.size();



                for (int i = 0; i < typeVOS.size(); i++) {
                    Row rowContent = sheetCon.createRow(i); // 建立儲存格
                    TypeVO typeVO = typeVOS.get(i);
                    rowContent.createCell(0).setCellValue(typeVO.getId());
                    rowContent.createCell(1).setCellValue(typeVO.getGroupNumber());
                    rowContent.createCell(2).setCellValue(typeVO.getName());
                    rowContent.createCell(3).setCellValue(typeVO.getImage());
                    setMessage(count++);
                }
                //TypeDetail
                Sheet sheetCon1 = workbook.createSheet("TypeDetail");

                for (int i = 0; i < typeDetailVOS.size(); i++) {
                    Row rowContent = sheetCon1.createRow(i); // 建立儲存格
                    TypeDetailVO typeDetailVO = typeDetailVOS.get(i);
                    rowContent.createCell(0).setCellValue(typeDetailVO.getId());
                    rowContent.createCell(1).setCellValue(typeDetailVO.getGroupNumber());
                    rowContent.createCell(2).setCellValue(typeDetailVO.getName());
                    rowContent.createCell(3).setCellValue(typeDetailVO.getImage());
                    rowContent.createCell(4).setCellValue(typeDetailVO.getKeyword());
                    setMessage(count++);
                }

                //BankDetail
                Sheet sheetCon2 = workbook.createSheet("BankType");

                for (int i = 0; i < bankTypeVOS.size(); i++) {
                    Row rowContent = sheetCon2.createRow(i);
                    BankTypeVO bankTypeVO = bankTypeVOS.get(i);
                    rowContent.createCell(0).setCellValue(bankTypeVO.getId());
                    rowContent.createCell(1).setCellValue(bankTypeVO.getGroupNumber());
                    rowContent.createCell(2).setCellValue(bankTypeVO.getName());
                    rowContent.createCell(3).setCellValue(bankTypeVO.getImage());
                    setMessage(count++);
                }
                //goal
                Sheet sheetCon3 = workbook.createSheet("Goal");

                for (int i = 0; i < goalVOS.size(); i++) {
                    Row rowContent = sheetCon3.createRow(i);
                    GoalVO goalVO = goalVOS.get(i);
                    rowContent.createCell(0).setCellValue(goalVO.getId());
                    rowContent.createCell(1).setCellValue(goalVO.getType());
                    rowContent.createCell(2).setCellValue(goalVO.getName());
                    rowContent.createCell(3).setCellValue(goalVO.getRealMoney());
                    rowContent.createCell(4).setCellValue(goalVO.getTimeStatue());
                    rowContent.createCell(5).setCellValue(goalVO.getStartTime());
                    rowContent.createCell(6).setCellValue(goalVO.getEndTime());
                    rowContent.createCell(7).setCellValue(goalVO.isNotify());
                    rowContent.createCell(8).setCellValue(goalVO.getNotifyStatue());
                    rowContent.createCell(9).setCellValue(goalVO.getNotifyDate());
                    rowContent.createCell(10).setCellValue(goalVO.isNoWeekend());
                    rowContent.createCell(11).setCellValue(goalVO.getStatue());
                    rowContent.createCell(12).setCellValue(goalVO.getCurrency());
                    setMessage(count++);
                }

                //bank
                Sheet sheetCon4 = workbook.createSheet("Bank");

                for (int i = 0; i < bankVOS.size(); i++) {
                    Row rowContent = sheetCon4.createRow(i);
                    BankVO bankVO = bankVOS.get(i);
                    rowContent.createCell(0).setCellValue(bankVO.getId());
                    rowContent.createCell(1).setCellValue(bankVO.getMaintype());
                    if(StringUtil.isBlank(bankVO.getRealMoney()))
                    {
                        bankVO.setRealMoney(String.valueOf(bankVO.getMoney()));
                        bankDB.update(bankVO);
                    }
                    rowContent.createCell(2).setCellValue(bankVO.getRealMoney());
                    rowContent.createCell(3).setCellValue(bankVO.getDate().getTime());
                    rowContent.createCell(4).setCellValue(bankVO.getFixDate());
                    rowContent.createCell(5).setCellValue(bankVO.getFixDateDetail());
                    rowContent.createCell(6).setCellValue(bankVO.getDetailname());
                    rowContent.createCell(7).setCellValue(bankVO.isAuto());
                    rowContent.createCell(8).setCellValue(bankVO.getAutoId());
                    rowContent.createCell(9).setCellValue(bankVO.getCurrency());
                    rowContent.createCell(10).setCellValue(bankVO.getFkKey());
                    setMessage(count++);
                }

                //Consume
                Sheet sheetCon5 = workbook.createSheet("Consume");

                for (int i = 0; i < consumeVOS.size(); i++) {
                    Row rowContent = sheetCon5.createRow(i);
                    ConsumeVO consumeVO = consumeVOS.get(i);
                    if(StringUtil.isBlank(consumeVO.getRealMoney()))
                    {
                        consumeVO.setRealMoney(String.valueOf(consumeVO.getMoney()));
                        consumeDB.update(consumeVO);
                    }


                    rowContent.createCell(0).setCellValue(consumeVO.getId());
                    rowContent.createCell(1).setCellValue(consumeVO.getMaintype());
                    rowContent.createCell(2).setCellValue(consumeVO.getSecondType());
                    rowContent.createCell(3).setCellValue(consumeVO.getRealMoney());
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
                    rowContent.createCell(14).setCellValue(consumeVO.getRdNumber());
                    rowContent.createCell(15).setCellValue(consumeVO.getCurrency());
                    rowContent.createCell(16).setCellValue(consumeVO.getFkKey());
                    setMessage(count++);
                }

                //Invoice
                Sheet sheetCon6 = workbook.createSheet("Invoice");

                for (int i = 0; i < invoiceVOS.size(); i++) {
                    Row rowContent = sheetCon6.createRow(i);
                    InvoiceVO invoiceVO = invoiceVOS.get(i);
                    rowContent.createCell(0).setCellValue(invoiceVO.getId());
                    rowContent.createCell(1).setCellValue(invoiceVO.getInvNum());
                    rowContent.createCell(2).setCellValue(invoiceVO.getCardType());
                    rowContent.createCell(3).setCellValue(invoiceVO.getCardNo());
                    rowContent.createCell(4).setCellValue(invoiceVO.getCardEncrypt());
                    rowContent.createCell(5).setCellValue(invoiceVO.getTime().getTime());
                    rowContent.createCell(6).setCellValue(invoiceVO.getRealAmount());
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
                    rowContent.createCell(20).setCellValue(invoiceVO.getCurrency());
                    setMessage(count++);
                }
                //Carrier
                Sheet sheetCon7 = workbook.createSheet("Carrier");

                for (int i = 0; i < carrierVOS.size(); i++) {
                    Row rowContent = sheetCon7.createRow(i);
                    CarrierVO carrierVO = carrierVOS.get(i);
                    rowContent.createCell(0).setCellValue(carrierVO.getId());
                    rowContent.createCell(1).setCellValue(carrierVO.getCarNul());
                    rowContent.createCell(2).setCellValue(carrierVO.getPassword());
                    setMessage(count++);
                }
                //Price
                Sheet sheetCon8 = workbook.createSheet("Price");

                for (int i = 0; i < priceVOS.size(); i++) {
                    Row rowContent = sheetCon8.createRow(i);
                    PriceVO priceVO=priceVOS.get(i);
                    rowContent.createCell(0).setCellValue(priceVO.getInVoYm());
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
                    setMessage(count++);
                }

                //ElePeriod
                Sheet sheetCon9 = workbook.createSheet("ElePeriod");

                for (int i = 0; i < elePeriods.size(); i++) {
                    Row rowContent = sheetCon9.createRow(i);
                    ElePeriod elePeriod = elePeriods.get(i);
                    rowContent.createCell(0).setCellValue(elePeriod.getId());
                    rowContent.createCell(1).setCellValue(elePeriod.getCarNul());
                    rowContent.createCell(2).setCellValue(elePeriod.getYear());
                    rowContent.createCell(3).setCellValue(elePeriod.getMonth());
                    rowContent.createCell(4).setCellValue(elePeriod.isDownload());
                    setMessage(count++);
                }

                //Property
                Sheet sheetCon10 = workbook.createSheet("Property");

                for(int i=0;i<propertyVOS.size();i++)
                {
                    Row rowContent=sheetCon10.createRow(i);
                    PropertyVO propertyVO=propertyVOS.get(i);
                    rowContent.createCell(0).setCellValue(propertyVO.getId());
                    rowContent.createCell(1).setCellValue(propertyVO.getCurrency());
                    rowContent.createCell(2).setCellValue(propertyVO.getName());
                    setMessage(count++);
                }


                //PropertyFromVo
                Sheet sheetCon11 = workbook.createSheet("PropertyFrom");

                for(int i=0;i<propertyFromVOS.size();i++)
                {
                    Row rowContent=sheetCon11.createRow(i);
                    PropertyFromVO propertyFromVO=propertyFromVOS.get(i);
                    rowContent.createCell(0).setCellValue(propertyFromVO.getId());
                    rowContent.createCell(1).setCellValue(propertyFromVO.getType().getCode());
                    rowContent.createCell(2).setCellValue(propertyFromVO.getSourceMoney());
                    rowContent.createCell(3).setCellValue(propertyFromVO.getSourceCurrency());
                    rowContent.createCell(4).setCellValue(propertyFromVO.getSourceMainType());
                    rowContent.createCell(5).setCellValue(propertyFromVO.getSourceSecondType());
                    rowContent.createCell(6).setCellValue(propertyFromVO.getSourceTime().getTime());
                    rowContent.createCell(7).setCellValue(propertyFromVO.getImportFee());
                    rowContent.createCell(8).setCellValue(propertyFromVO.getImportFeeId());
                    rowContent.createCell(9).setCellValue(propertyFromVO.getFixImport());
                    rowContent.createCell(10).setCellValue(propertyFromVO.getFixDateCode().getDetail());
                    rowContent.createCell(11).setCellValue(propertyFromVO.getFixDateDetail());
                    rowContent.createCell(12).setCellValue(propertyFromVO.getPropertyId());
                    rowContent.createCell(13).setCellValue(propertyFromVO.getFixFromId());
                    setMessage(count++);
                }

                Sheet sheetCon12 = workbook.createSheet("Setting");
                Row rowContent=sheetCon12.createRow(0);
                SharedPreferences sharedPreferences=context.getSharedPreferences("Charge_User",Context.MODE_PRIVATE);
                boolean notify=sharedPreferences.getBoolean("notify",true);
                rowContent.createCell(0).setCellValue(String.valueOf(notify));
                boolean autoCategory=sharedPreferences.getBoolean("autoCategory",true);
                rowContent.createCell(1).setCellValue(String.valueOf(autoCategory));
            }
            workbook.write(result);
            workbook.close();
            result.flush();
            if(local)
            {
                Message message=handler.obtainMessage();
                message.what=4;
                message.sendToTarget();
            }
        } catch (IOException e) {
            Log.e(TAG,e.toString());
        }finally {
            result.close();
        }
        return result.toByteArray();
    }



    private class excelOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            fileChoice.setVisibility(View.GONE);
            show = true;
            txt = false;
            if (local) {
                FileTOLocal();
            } else if(dropboxOpen){
                Auth.startOAuth2PKCE(context, getString(R.string.DropboxKey), DbxRequestConfigFactory.getRequestConfig(), DbxRequestConfigFactory.scope);
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
                FileTOLocal();
            } else if(dropboxOpen){
                Auth.startOAuth2PKCE(context, getString(R.string.DropboxKey), DbxRequestConfigFactory.getRequestConfig(), DbxRequestConfigFactory.scope);
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
                needGoal=false;
                needProperty=false;
                fileNameTemp="記帳小助手(備份資料)";
            } else if (position == 1) {
                all = false;
                income = true;
                consume = true;
                needGoal=true;
                needProperty=true;
                fileNameTemp="記帳小助手(全部)";
                consumeVOList=consumeDB.getAll();
                invoiceVOS=invoiceDB.getAll();
                bankVOS=bankDB.getAll();
                goalVOS=goalDB.getAll();
                propertyVOS=propertyDB.getAll();
                propertyFromVOS=propertyFromDB.getAll();

                totalDataCount=consumeVOList.size()+invoiceVOS.size()+bankVOS.size()+goalVOS.size()+propertyVOS.size()+propertyFromVOS.size();


            } else if (position == 2) {
                all = false;
                income = false;
                consume = true;
                needGoal=false;
                needProperty=false;
                fileNameTemp="記帳小助手(支出)";


                consumeVOList=consumeDB.getAll();
                invoiceVOS=invoiceDB.getAll();
                totalDataCount=consumeVOList.size()+invoiceVOS.size();


            } else if(position==3){
                all = false;
                income = true;
                consume = false;
                needGoal=false;
                needProperty=false;
                fileNameTemp="記帳小助手(收入)";



                bankVOS=bankDB.getAll();
                totalDataCount=bankVOS.size();


            }else if(position==4){
                all = false;
                income = false;
                consume = false;
                needGoal=true;
                needProperty=false;
                fileNameTemp="記帳小助手(目標)";
                goalVOS=goalDB.getAll();
                totalDataCount=goalVOS.size();

            }else if(position==5){
                all = false;
                income = false;
                consume = false;
                needGoal=false;
                needProperty=true;
                fileNameTemp="記帳小助手(資產)";

                propertyVOS=propertyDB.getAll();
                propertyFromVOS=propertyFromDB.getAll();

                totalDataCount=propertyVOS.size()+propertyFromVOS.size();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }



    private void setMessage(int count)
    {
        if(count>=totalDataCount)
        {
            count=totalDataCount;
        }
        Message message=new Message();
        message.what=5;
        c=new BigDecimal(count);
        t=new BigDecimal(totalDataCount);
        message.obj= c.divide(t,4, RoundingMode.HALF_UP).multiply(hundred).setScale(1,BigDecimal.ROUND_HALF_UP)+"%";
        handler.sendMessage(message);
    }


}
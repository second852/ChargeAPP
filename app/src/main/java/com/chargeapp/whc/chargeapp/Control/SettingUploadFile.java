package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.EleMainItemVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.zxing.oned.OneDimensionalCodeWriter;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SettingUploadFile extends Fragment {


    private ListView listView;
    private LinearLayout fileChoice;
    private ImageView excel, txtFile, cancelF;
    private Boolean isShow = true;
    private Spinner choiceT;
    private boolean local, consume, income,all;
    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private BankDB bankDB;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private BankTybeDB bankTybeDB;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_upload, container, false);
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDB=new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankTybeDB=new BankTybeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB=new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        List<EleMainItemVO> itemSon = getNewItem();
        listView = view.findViewById(R.id.list);
        fileChoice = view.findViewById(R.id.fileChoice);
        excel = view.findViewById(R.id.excel);
        txtFile = view.findViewById(R.id.txtFile);
        cancelF = view.findViewById(R.id.cancelF);
        choiceT = view.findViewById(R.id.choiceT);
        excel.setOnClickListener(new excelOnClick());
        txtFile.setOnClickListener(new txtOnClick());
        cancelF.setOnClickListener(new cancelOnClick());
        listView.setAdapter(new ListAdapter(getActivity(), itemSon));
        setSpinner();
        return view;
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
                        if (isShow) {
                            fileChoice.setVisibility(View.VISIBLE);
                            isShow = false;
                            local = true;
                        } else {


                        }
                    }
                });

            } else if (position == 1) {

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isShow) {
                            fileChoice.setVisibility(View.VISIBLE);
                            isShow = false;
                            local = false;
                        } else {

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

    private void outputExcel() {
        File dir = null;
        dir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {

            File file = new File(dir, "記帳小助手.xls");
            HSSFWorkbook workbook = new HSSFWorkbook();
            OutputStream outputStream = new FileOutputStream(file);

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
                rowTitle.createCell(11).setCellValue("資料庫ID");
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
                        rowContent.createCell(5).setCellValue(consumeVO.getDetailname());
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
                    BankVO bankVO=bankVOS.get(i);
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
            if(all)
            {
                //Type
                Sheet sheetCon = workbook.createSheet("Type");
                sheetCon.setColumnWidth(2, 2);// 自動調整欄位寬度
                List<TypeVO> typeVOS = typeDB.getExport();
                for (int i = 0; i < typeVOS.size(); i++) {
                    Row rowContent = sheetCon.createRow(i); // 建立儲存格
                    TypeVO typeVO=typeVOS.get(i);
                    rowContent.createCell(0).setCellValue(typeVO.getId());
                    rowContent.createCell(1).setCellValue(typeVO.getGroupNumber());
                    rowContent.createCell(2).setCellValue(typeVO.getName());
                    rowContent.createCell(3).setCellValue(typeVO.getImage());
                    rowContent.createCell(4).setCellValue(typeVO.getKeyword());
                }
                //TypeDetail
                Sheet sheetCon1 = workbook.createSheet("TypeDetail");
                sheetCon1.setColumnWidth(2, 2);// 自動調整欄位寬度
                List<TypeDetailVO> typeDetailVOS = typeDetailDB.getExport();
                for (int i = 0; i < typeDetailVOS.size(); i++) {
                    Row rowContent = sheetCon1.createRow(i); // 建立儲存格
                    TypeDetailVO typeDetailVO=typeDetailVOS.get(i);
                    rowContent.createCell(0).setCellValue(typeDetailVO.getId());
                    rowContent.createCell(1).setCellValue(typeDetailVO.getGroupNumber());
                    rowContent.createCell(2).setCellValue(typeDetailVO.getName());
                    rowContent.createCell(3).setCellValue(typeDetailVO.getImage());
                    rowContent.createCell(4).setCellValue(typeDetailVO.getKeyword());
                }

                //BankDetail
                Sheet sheetCon2 = workbook.createSheet("TypeDetail");
                sheetCon2.setColumnWidth(2, 2);
                List<BankTypeVO> bankTypeVOS = bankTybeDB.getExport();
                for (int i = 0; i < bankTypeVOS.size(); i++) {
                    Row rowContent = sheetCon2.createRow(i);
                    BankTypeVO bankTypeVO=bankTypeVOS.get(i);
                    rowContent.createCell(0).setCellValue(bankTypeVO.getId());
                    rowContent.createCell(1).setCellValue(bankTypeVO.getGroupNumber());
                    rowContent.createCell(2).setCellValue(bankTypeVO.getName());
                    rowContent.createCell(3).setCellValue(bankTypeVO.getImage());
                }

            }else{

            }
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private class excelOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            fileChoice.setVisibility(View.GONE);
            isShow = true;
        }
    }

    private class txtOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            fileChoice.setVisibility(View.GONE);
            isShow = true;
        }
    }

    private class cancelOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            fileChoice.setVisibility(View.GONE);
            isShow = true;
        }
    }

    private class choiceAction implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (i == 0) {
                consume = true;
                income = true;
                isShow = false;
            } else if (i == 1) {
                consume = true;
                income = true;
                isShow = true;

            } else if (i == 2) {
                consume = true;
                income = false;
                isShow = true;
            } else if (i == 3) {
                consume = false;
                income = true;
                isShow = true;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}

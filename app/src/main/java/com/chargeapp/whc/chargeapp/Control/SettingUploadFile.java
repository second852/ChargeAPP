package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.provider.SyncStateContract;
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

import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.EleMainItemVO;
import com.chargeapp.whc.chargeapp.R;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SettingUploadFile extends Fragment {


    private ListView listView;
    private LinearLayout fileChoice;
    private ImageView excel, txtFile,cancelF;
    private Boolean  isShow=true;
    private Spinner choiceT;
    private int position=0;
    private boolean local;
    private ConsumeDB consumeDB;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_upload, container, false);
        consumeDB=new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        List<EleMainItemVO> itemSon = getNewItem();
        listView = view.findViewById(R.id.list);
        fileChoice = view.findViewById(R.id.fileChoice);
        excel = view.findViewById(R.id.excel);
        txtFile = view.findViewById(R.id.txtFile);
        cancelF=view.findViewById(R.id.cancelF);
        choiceT=view.findViewById(R.id.choiceT);
        excel.setOnClickListener(new excelOnClick());
        txtFile.setOnClickListener(new txtOnClick());
        cancelF.setOnClickListener(new cancelOnClick());
        listView.setAdapter(new ListAdapter(getActivity(), itemSon));
        setSpinner();
        return view;
    }

    private void setSpinner() {
        ArrayList<String> spinnerItem=new ArrayList();
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
                            isShow=false;
                            local=true;
                        }
                    }
                });

            } else if (position == 1) {

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isShow) {
                            fileChoice.setVisibility(View.VISIBLE);
                            isShow=false;
                            local=false;
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

    private class excelOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            fileChoice.setVisibility(View.GONE);
            isShow=true;
            if(local)
            {
                File dir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS);
            }else{


            }
            HSSFWorkbook workbook = new HSSFWorkbook();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // 設定儲存格格式
            HSSFCellStyle styleRow1 = workbook.createCellStyle();
            // styleRow1.setFillForegroundColor(HSSFColor.GREEN.index);//填滿顏色
            // styleRow1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            styleRow1.setAlignment(HorizontalAlignment.CENTER);
            styleRow1.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直置中
            // 設定框線
            styleRow1.setBorderBottom(BorderStyle.DASH_DOT);
            styleRow1.setBorderTop(BorderStyle.DASH_DOT);
            styleRow1.setBorderLeft(BorderStyle.DASH_DOT);
            styleRow1.setBorderRight(BorderStyle.DASH_DOT);
            styleRow1.setWrapText(true); // 自動換行
	        /* Title */

            Sheet sheetCon = workbook.createSheet("消費");

            sheetCon.autoSizeColumn(0); // 自動調整欄位寬度

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
            List<ConsumeVO> consumeVOS=consumeDB.getAll();
            for (int i = 0; i < consumeVOS.size(); i++) {
                Row rowContent = sheetCon.createRow(i + 1); // 建立儲存格
                Cell cellContent = rowContent.createCell(0);
                cellContent.setCellValue(Common.sTwo.format(consumeVOS.get(i).getDate()));
                cellContent = rowContent.createCell(1);
                cellContent.setCellValue(consumeVOS.get(i).getMaintype());
            }

            try {
                workbook.write(bos);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private class txtOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            fileChoice.setVisibility(View.GONE);
            isShow=true;
        }
    }

    private class cancelOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            fileChoice.setVisibility(View.GONE);
            isShow=true;
        }
    }

    private class choiceAction implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            position=i;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}

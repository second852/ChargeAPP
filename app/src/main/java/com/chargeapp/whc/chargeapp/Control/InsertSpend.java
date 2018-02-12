package com.chargeapp.whc.chargeapp.Control;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;


import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.SetupDateBase64;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.ui.BarcodeGraphic;
import com.chargeapp.whc.chargeapp.ui.MultiTrackerActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class InsertSpend extends Fragment {
    private EditText money, newtype, inserttypeDetail, number, detailname;
    private CheckBox fixdate, notify, noWek;
    private TextView secondname, name;
    private TextView save, clear, date, saveType, clearType, showTitle, datesave;
    private GridView gridView, showAllpicture;
    private RelativeLayout insertType;
    private List<TypeVO> typeVOList;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> items;
    private List<Map<String, Object>> Detailitems;
    private Map<String, Object> item;
    private LinearLayout showPicture, showdate, showAllPL, showfixdate;
    private ImageView imageTitle, imageDetatil;
    private int imageTitleId = 999, imageDetatilId = 999;
    private boolean isType;
    private DatePicker datePicker;
    private String choicedate;
    private Spinner choiceStatue, choiceday;
    private Gson gson;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private boolean noweek = false;
    private ConsumeDB consumeDB;
    private RelativeLayout qrcode;
    private ConsumeVO consumeVO;
    private int updateChoice;
    private String action;
    private boolean first=true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.insert_spend, container, false);
        findviewByid(view);
        if (MainActivity.chargeAPPDB == null) {
            MainActivity.chargeAPPDB = new ChargeAPPDB(getActivity());
        }
        gson = new Gson();
        typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeVOList = typeDB.getAll();
        Detailitems = new ArrayList<Map<String, Object>>();
        items = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < typeVOList.size(); i++) {
            item = new HashMap<String, Object>();
            item.put("image", MainActivity.imageAll[typeVOList.get(i).getImage()]);
            item.put("text", typeVOList.get(i).getName());
            items.add(item);
        }
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.add);
        item.put("text", "新增種類");
        items.add(item);
        date.setText(sf.format(new Date(System.currentTimeMillis())));
        name.setOnClickListener(new choiceType());
        saveType.setOnClickListener(new insertType());
        clearType.setOnClickListener(new cancelinsert());
        imageTitle.setOnClickListener(new choicePicture());
        imageDetatil.setOnClickListener(new choicePicture());
        date.setOnClickListener(new dateClickListener());
        showdate.setOnClickListener(new choicedateClick());
        fixdate.setOnCheckedChangeListener(new showfixdateClick());
        choiceStatue.setOnItemSelectedListener(new choiceStateItem());
        clear.setOnClickListener(new clearAllInput());
        save.setOnClickListener(new savecomsumer());
        noWek.setOnCheckedChangeListener(new nowWekchange());
        qrcode.setOnClickListener(new QrCodeClick());
        action= (String) getArguments().getSerializable("action");
        if(action.equals("update"))
        {
            setUpdate();
        }
        return view;
    }

    private void setUpdate() {
        consumeVO= (ConsumeVO) getArguments().getSerializable("consumeVO");
        name.setText(consumeVO.getMaintype());
        number.setText(consumeVO.getNumber());
        secondname.setText(consumeVO.getSecondType());
        money.setText(consumeVO.getMoney());
        date.setText(sf.format(consumeVO.getDate()));
        detailname.setText(consumeVO.getDetailname());
        Log.d("XXXXXX",consumeVO.getFixDateDetail());
        if(consumeVO.getFixDate().equals("true"))
        {
                fixdate.setChecked(Boolean.valueOf(consumeVO.getFixDate()));
                notify.setChecked(Boolean.valueOf(consumeVO.getNotify()));
                JsonObject js = gson.fromJson(consumeVO.getFixDateDetail(),JsonObject.class);
                String choicestatue= js.get("choicestatue").getAsString().trim();
                String choicedate=js.get("choicedate").getAsString().trim();
                String noweek=js.get("noweek").getAsString().trim();
                noWek.setChecked(Boolean.valueOf(noweek));
                if(choicestatue.trim().equals("每天"))
                {
                    choiceStatue.setSelection(0);
                }else if(choicestatue.trim().equals("每周")){
                    choiceStatue.setSelection(1);
                    if(choicedate.equals("星期一"))
                    {
                        updateChoice=0;
                    }else if(choicedate.equals("星期二"))
                    {
                        updateChoice=1;
                    }else if(choicedate.equals("星期三"))
                    {
                        updateChoice=2;
                    }else if(choicedate.equals("星期四"))
                    {
                        updateChoice=3;
                    }else if(choicedate.equals("星期五"))
                    {
                        updateChoice=4;
                    }else if(choicedate.equals("星期六"))
                    {
                        updateChoice=5;
                    }else{
                        updateChoice=6;
                    }
                }else if(choicestatue.trim().equals("每個月")){
                    choiceStatue.setSelection(2);
                    updateChoice= Integer.valueOf(choicedate)-1;
                }else{
                    choiceStatue.setSelection(3);
                    updateChoice=Integer.valueOf(choicedate.substring(0,choicedate.indexOf("月")))-1;
                }
            }

    }


    public void findviewByid(View view) {
        name = view.findViewById(R.id.name);
        secondname = view.findViewById(R.id.secondname);
        money = view.findViewById(R.id.money);
        date = view.findViewById(R.id.date);
        fixdate = view.findViewById(R.id.fixdate);
        save = view.findViewById(R.id.save);
        clear = view.findViewById(R.id.clear);
        gridView = view.findViewById(R.id.choiceType);
        insertType = view.findViewById(R.id.insertType);
        saveType = view.findViewById(R.id.saveType);
        clearType = view.findViewById(R.id.clearType);
        newtype = view.findViewById(R.id.newtype);
        inserttypeDetail = view.findViewById(R.id.inserttypeDetail);
        showPicture = view.findViewById(R.id.showPicture);
        imageDetatil = view.findViewById(R.id.imageDetail);
        imageTitle = view.findViewById(R.id.imageTitle);
        showTitle = view.findViewById(R.id.showTitle);
        showAllpicture = view.findViewById(R.id.showAllpicture);
        datesave = view.findViewById(R.id.datesave);
        showdate = view.findViewById(R.id.showdate);
        datePicker = view.findViewById(R.id.datePicker);
        showfixdate = view.findViewById(R.id.showfixdate);
        choiceStatue = view.findViewById(R.id.choiceStatue);
        choiceday = view.findViewById(R.id.choiceday);
        showAllPL = view.findViewById(R.id.showAllPL);
        number = view.findViewById(R.id.number);
        detailname = view.findViewById(R.id.detailname);
        notify = view.findViewById(R.id.notify);
        noWek = view.findViewById(R.id.noWek);
        qrcode = view.findViewById(R.id.qrcode);
        ArrayList<String> spinneritem = new ArrayList<>();
        spinneritem.add("每天");
        spinneritem.add("每周");
        spinneritem.add("每個月");
        spinneritem.add("每年");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinneritem, spinneritem);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        choiceStatue.setAdapter(arrayAdapter);
    }

    private class cancelinsert implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            inserttypeDetail.setText(" ");
            newtype.setText(" ");
            insertType.setVisibility(View.GONE);
            showPicture.setVisibility(View.VISIBLE);
        }
    }

    private class insertType implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (newtype.getText().toString() == null || newtype.getText().toString().isEmpty()) {
                Common.showToast(getActivity(), "主項目不能空白");
                return;
            }
            if (inserttypeDetail.getText().toString().trim() == null || inserttypeDetail.getText().toString().trim().isEmpty()) {
                Common.showToast(getActivity(), "次項目不能空白");
                return;
            }
            Common.showToast(getActivity(), "新增成功");
            if (imageTitleId == 999) {
                imageTitleId = MainActivity.imageAll.length - 1;
            }
            if (imageDetatilId == 999) {
                imageDetatilId = MainActivity.imageAll.length - 1;
            }
            typeDB.insert(new TypeVO("other", newtype.getText().toString(), imageTitleId));
            typeDetailDB.insert(new TypeDetailVO(newtype.getText().toString().trim(), inserttypeDetail.getText().toString().toString(), imageDetatilId));
            if (isType) {
                items.remove(items.size() - 1);
                Map<String, Object> newitem = new HashMap<String, Object>();
                newitem.put("image", MainActivity.imageAll[imageTitleId]);
                newitem.put("text", newtype.getText().toString());
                items.add(newitem);
                items.add(item);
                adapter = new SimpleAdapter(getActivity(),
                        items, R.layout.main_item, new String[]{"image", "text"},
                        new int[]{R.id.image, R.id.text});
                gridView.setAdapter(adapter);

            } else {
                Detailitems.remove(Detailitems.size() - 1);
                Detailitems.remove(Detailitems.size() - 1);
                Map<String, Object> newitem = new HashMap<String, Object>();
                newitem.put("image", MainActivity.imageAll[imageDetatilId]);
                newitem.put("text", inserttypeDetail.getText().toString());
                Detailitems.add(newitem);
                newitem = new HashMap<String, Object>();
                newitem.put("image", R.drawable.returnt);
                newitem.put("text", "返回");
                Detailitems.add(newitem);
                Detailitems.add(item);
                SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                        Detailitems, R.layout.main_item, new String[]{"image", "text"},
                        new int[]{R.id.image, R.id.text});
                gridView.setAdapter(adapter);
            }
            insertType.setVisibility(View.GONE);
            showPicture.setVisibility(View.VISIBLE);
            return;
        }
    }

    private class choiceType implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            showTitle.setText("選擇主項目種類");
            adapter = new SimpleAdapter(getActivity(),
                    items, R.layout.main_item, new String[]{"image", "text"},
                    new int[]{R.id.image, R.id.text});
            gridView.setNumColumns(4);
            gridView.setAdapter(adapter);
            showPicture.setVisibility(View.VISIBLE);
            gridView.setOnItemClickListener(new choiceTypeitem());
        }
    }

    private class dateClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showdate.setVisibility(View.VISIBLE);
        }

    }

    private class choicePicture implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final ImageView setimg = v.findViewById(v.getId());
            ArrayList items = new ArrayList<Map<String, Object>>();
            HashMap item;
            for (int i = 0; i < MainActivity.imageAll.length; i++) {
                item = new HashMap<String, Object>();
                item.put("image", MainActivity.imageAll[i]);
                item.put("text", " ");
                items.add(item);
            }
            SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                    items, R.layout.main_item, new String[]{"image", "text"},
                    new int[]{R.id.image, R.id.text});
            showAllpicture.setNumColumns(4);
            showAllpicture.setAdapter(adapter);
            insertType.setVisibility(View.GONE);
            showAllPL.setVisibility(View.VISIBLE);
            showAllpicture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ImageView image = view.findViewById(R.id.image);
                    setimg.setImageDrawable(image.getDrawable());
                    showAllPL.setVisibility(View.GONE);
                    insertType.setVisibility(View.VISIBLE);
                    if (setimg.getId() == R.id.imageTitle) {
                        imageTitleId = position;
                    } else {
                        imageDetatilId = position;
                    }
                }
            });
        }
    }

    private class choicedateClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choicedate = datePicker.getYear() + "-" + String.valueOf(datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
            date.setText(choicedate);
            showdate.setVisibility(View.GONE);
        }
    }


    private class showfixdateClick implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            Log.d("XXXXXXXXXXX", String.valueOf(notify.isChecked()));
            if (b) {
                notify.setVisibility(View.VISIBLE);
                noWek.setVisibility(View.VISIBLE);
                choiceStatue.setVisibility(View.VISIBLE);
            } else {
                notify.setVisibility(View.GONE);
                choiceStatue.setVisibility(View.GONE);
                choiceday.setVisibility(View.GONE);
                noWek.setVisibility(View.GONE);

            }
        }
    }

    private class choiceStateItem implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            String choiceitem = adapterView.getItemAtPosition(position).toString();
            ArrayList<String> spinneritem = new ArrayList<>();
            if (choiceitem.equals("每天")) {
                choiceday.setVisibility(View.GONE);
                noWek.setVisibility(View.VISIBLE);
                choiceStatue.setVisibility(View.VISIBLE);
                return;
            }
            if (choiceitem.equals("每周")) {
                spinneritem.add("星期一");
                spinneritem.add("星期二");
                spinneritem.add("星期三");
                spinneritem.add("星期四");
                spinneritem.add("星期五");
                spinneritem.add("星期六");
                spinneritem.add("星期日");
            }
            if (choiceitem.equals("每個月")) {
                for (int i = 1; i <= 31; i++) {
                    spinneritem.add("    " + String.valueOf(i) + "   ");
                }
            }
            if (choiceitem.equals("每年")) {
                for (int i = 1; i <= 12; i++) {
                    spinneritem.add(" " + String.valueOf(i) + "月");
                }
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinneritem, spinneritem);
            arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
            choiceday.setAdapter(arrayAdapter);
            choiceday.setVisibility(View.VISIBLE);
            if(first)
            {
                choiceday.setSelection(updateChoice);
                first=false;
            }
            noWek.setVisibility(View.GONE);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class choiceTypeitem implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            TextView t = view.findViewById(R.id.text);
            Log.d("one", t.getText().toString());
            if (t.getText().toString().equals("新增種類")) {
                showPicture.setVisibility(View.GONE);
                insertType.setVisibility(View.VISIBLE);
                newtype.setText(" ");
                inserttypeDetail.setText(" ");
                isType = true;
                return;
            }
            name.setText(t.getText());
            showTitle.setText("選擇次項目種類");
            Detailitems = new ArrayList<>();
            HashMap detailitem;
            ArrayList<TypeDetailVO> typeDetailVOS = typeDetailDB.findByGroupname(t.getText().toString().trim());
            for (int i = 0; i < typeDetailVOS.size(); i++) {
                detailitem = new HashMap<String, Object>();
                detailitem.put("image", MainActivity.imageAll[typeDetailVOS.get(i).getImage()]);
                detailitem.put("text", typeDetailVOS.get(i).getName());
                Detailitems.add(detailitem);
            }
            secondname.setOnClickListener(new secondnameonclick());
            detailitem = new HashMap<String, Object>();
            detailitem.put("image", R.drawable.returnt);
            detailitem.put("text", "返回");
            Detailitems.add(detailitem);
            Detailitems.add(item);
            SimpleAdapter detailAdapter = new SimpleAdapter(getActivity(),
                    Detailitems, R.layout.main_item, new String[]{"image", "text"},
                    new int[]{R.id.image, R.id.text});
            gridView.setAdapter(detailAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView t = view.findViewById(R.id.text);
                    Log.d("two", t.getText().toString());
                    if (t.getText().toString().equals("新增種類")) {
                        showPicture.setVisibility(View.GONE);
                        insertType.setVisibility(View.VISIBLE);
                        newtype.setText(name.getText());
                        inserttypeDetail.setText(" ");
                        isType = false;
                        return;
                    }
                    if (t.getText().toString().equals("返回")) {
                        gridView.setNumColumns(4);
                        gridView.setAdapter(adapter);
                        gridView.setOnItemClickListener(new choiceTypeitem());
                        return;
                    }
                    secondname.setText(t.getText());
                    showPicture.setVisibility(View.GONE);
                    insertType.setVisibility(View.GONE);
                }
            });
        }
    }

    private class clearAllInput implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            name.setText(" ");
            secondname.setText(" ");
            money.setText(" ");
            fixdate.setChecked(false);
            number.setText(" ");
        }
    }

    private String isnull(Object text) {
        if (text == null || text.toString().length() <= 0) {
            return " ";
        }
        return text.toString();
    }


    private class savecomsumer implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            name.setBackgroundColor(Color.parseColor("#FFEE99"));
            secondname.setBackgroundColor(Color.parseColor("#FFEE99"));
            if (name.getText().toString().trim() == null || name.getText().toString().trim().length() == 0) {
                name.setBackgroundColor(Color.parseColor("#ff471a"));
                Common.showToast(getActivity(), "主項目不能空白");
                return;
            }
            if (secondname.getText().toString().trim() == null || secondname.getText().toString().trim().length() == 0) {
                secondname.setBackgroundColor(Color.parseColor("#ff471a"));
                Common.showToast(getActivity(), "次項目不能空白");
                return;
            }
            if (money.getText().toString().trim() == null || money.getText().toString().trim().length() == 0) {
                money.setError("金額不能空白");
                return;
            }
            if (date.getText().toString().trim() == null || date.getText().toString().trim().length() == 0) {
                name.setError(" ");
                Common.showToast(getActivity(), "日期不能空白");
                return;
            }
            if (showdate.getVisibility() == View.VISIBLE || showPicture.getVisibility() == View.VISIBLE || showAllPL.getVisibility() == View.VISIBLE) {
                return;
            }
            String CheckNul = number.getText().toString();
            if (CheckNul.trim().length() > 0) {
                if (CheckNul.length() != 10) {
                    number.setError("統一發票中英文10個號碼");
                    return;
                }
                try {
                    new Integer(CheckNul.substring(2));
                } catch (NumberFormatException e) {
                    number.setError("統一發票後8碼為數字");
                    return;
                }
                int sN = (int) CheckNul.charAt(0);
                int eN = (int) CheckNul.charAt(1);
                if (sN < 65 || sN > 90 || eN < 65 || eN > 90) {
                    number.setError("統一發票號前2碼為大寫英文字母");
                    return;
                }
            }
            Map<String, String> g = new HashMap<>();
            g.put("choicestatue", isnull(choiceStatue.getSelectedItem().toString()));
            g.put("choicedate", isnull(choiceday.getSelectedItem()));
            g.put("noweek", String.valueOf(noweek));
            String fixdatedetail = gson.toJson(g);
            String[] dates = date.getText().toString().split("-");
            Calendar c = Calendar.getInstance();
            c.set(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 12, 0, 0);
            Date d = new Date(c.getTimeInMillis());
            if(action.equals("update"))
            {
                consumeVO.setMaintype(name.getText().toString());
                consumeVO.setSecondType(secondname.getText().toString());
                consumeVO.setMoney(money.getText().toString());
                consumeVO.setDate(d);
                consumeVO.setNumber(number.getText().toString());
                consumeVO.setFixDate(String.valueOf(fixdate.isChecked()));
                consumeVO.setFixDateDetail(fixdatedetail);
                consumeVO.setNotify(String.valueOf(notify.isChecked()));
                consumeVO.setDetailname(detailname.getText().toString());
                consumeDB.update(consumeVO);
                Common.showToast(getActivity(), "修改成功");
            }else{
                ConsumeVO consumeVO= new ConsumeVO();
                consumeVO.setMaintype(name.getText().toString());
                consumeVO.setSecondType(secondname.getText().toString());
                consumeVO.setMoney(money.getText().toString());
                consumeVO.setDate(d);
                consumeVO.setNumber(number.getText().toString());
                consumeVO.setFixDate(String.valueOf(fixdate.isChecked()));
                consumeVO.setFixDateDetail(fixdatedetail);
                consumeVO.setNotify(String.valueOf(notify.isChecked()));
                consumeVO.setDetailname(detailname.getText().toString());
                consumeVO.setIsWin("0");
                consumeDB.insert(consumeVO);
                Common.showToast(getActivity(), "新增成功");
            }

            Log.d("XXXXXXXXXXX", String.valueOf(consumeVO.getNotify()));
        }
    }

    private class secondnameonclick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            showPicture.setVisibility(View.VISIBLE);
            SimpleAdapter detailAdapter = new SimpleAdapter(getActivity(),
                    Detailitems, R.layout.main_item, new String[]{"image", "text"},
                    new int[]{R.id.image, R.id.text});
            gridView.setAdapter(detailAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView t = view.findViewById(R.id.text);
                    Log.d("two", t.getText().toString());
                    if (t.getText().toString().equals("新增種類")) {
                        showPicture.setVisibility(View.GONE);
                        insertType.setVisibility(View.VISIBLE);
                        newtype.setText(name.getText());
                        inserttypeDetail.setText(" ");
                        isType = false;
                        return;
                    }
                    if (t.getText().toString().equals("返回")) {
                        gridView.setNumColumns(4);
                        gridView.setAdapter(adapter);
                        gridView.setOnItemClickListener(new choiceTypeitem());
                        return;
                    }
                    secondname.setText(t.getText());
                    showPicture.setVisibility(View.GONE);
                    insertType.setVisibility(View.GONE);
                }
            });
        }
    }

    private class nowWekchange implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (noWek.isChecked()) {
                noweek = true;

            } else {
                noweek = false;
            }
        }
    }

    private class QrCodeClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            MultiTrackerActivity.refresh=true;
            BarcodeGraphic.hashMap = new HashMap<>();
            Intent intent = new Intent(InsertSpend.this.getActivity(), MultiTrackerActivity.class);
            startActivityForResult(intent, 0);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                HashMap<Integer, String> contents = BarcodeGraphic.hashMap;
                String all = BarcodeGraphic.hashMap.get(1).trim() + BarcodeGraphic.hashMap.get(2).trim();
                String[] EleNulAll = all.split(":");
                String EleNul = EleNulAll[0].substring(0, 10);
                String day = EleNulAll[0].substring(10, 17);
                String m = EleNulAll[0].substring(29, 37);
                String westday = (Integer.valueOf(day.substring(0, 3)) + 1911) + "-" + day.substring(3, 5) + "-" + day.substring(5);
                money.setText(String.valueOf(Integer.parseInt(m, 16)));
                number.setText(EleNul);
                date.setText(westday);
                StringBuffer sb = new StringBuffer();
                if (EleNulAll[4].equals("2")) {
                    try {
                        String base64 = EleNulAll[5];
                        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                        if (EleNulAll[3].equals("1")) {
                            sb.append(new String(bytes, "UTF-8") + "/1/" + money.getText().toString());
                        } else {
                            String debase64 = new String(bytes, "UTF-8");
                            String[] ddd = debase64.trim().split(":");
                            for (int j = 0; j < ddd.length; j = j + 2) {
                                sb.append(ddd[j] + "/" + ddd[j + 1] + "/" + ddd[j + 2] + " ");
                            }
                        }
                    } catch (Exception e) {
                        Common.showToast(getActivity(), e.getMessage());
                    }
                } else if(EleNulAll[4].equals("0")) {
                    try {
                        String a=new SetupDateBase64(this).execute("getThisDetail").get();
                        if(a.equals("InternetError"))
                        {
                            Common.showToast(getActivity(),"連線逾時,請從新掃QRCODE");
                            return;
                        }
                        Gson gson=new Gson();
                        JsonObject jFT=gson.fromJson(a,JsonObject.class);
                        String s = jFT.get("details").toString();
                        Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
                        List<JsonObject> b = gson.fromJson(s, cdType);
                        for (JsonObject j : b) {
                           sb.append(j.get("description").getAsString()+"/"+j.get("quantity").getAsString()+"/"+j.get("unitPrice").getAsString()+" ");
                        }
                    }catch (Exception e)
                    {
                        Common.showToast(getActivity(), e.getMessage());
                    }
                }else {
                    if (EleNulAll[3].equals("1")) {
                        sb.append(EleNulAll[5] + "/1/" + money.getText().toString());
                    } else {
                        for (int i = 5; i < EleNulAll.length; i = i + 3) {
                            sb.append(EleNulAll[i]+"/"+EleNulAll[i+1]+"/"+EleNulAll[i+2]+" ");
                        }
                    }
                }
                detailname.setText(sb.toString());
            }
        } else if (resultCode == RESULT_CANCELED) {
            // To Handle cancel
            Log.i("App", "Scan unsuccessful");
        }
    }
}





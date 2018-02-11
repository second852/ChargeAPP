package com.chargeapp.whc.chargeapp.Control;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.SetupDateBase64;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.ui.BarcodeGraphic;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class UpdateInvoice extends Fragment {
    private EditText money, newtype, inserttypeDetail, number;
    private TextView secondname, name;
    private TextView save, clear, date, saveType, clearType, showTitle, datesave, detailname;
    private GridView gridView, showAllpicture;
    private RelativeLayout insertType;
    private List<TypeVO> typeVOList;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> items;
    private List<Map<String, Object>> Detailitems;
    private Map<String, Object> item;
    private LinearLayout showPicture, showdate, showAllPL;
    private ImageView imageTitle, imageDetatil;
    private int imageTitleId = 999, imageDetatilId = 999;
    private boolean isType;
    private DatePicker datePicker;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private InvoiceDB invoiceDB;
    private String choicedate;
    private InvoiceVO invoiceVO;
    private ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_invoice, container, false);
        findviewByid(view);
        getActivity().setTitle("修改發票");
        setInvoice();
        if (MainActivity.chargeAPPDB == null) {
            MainActivity.chargeAPPDB = new ChargeAPPDB(getActivity());
        }
        typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
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
        name.setOnClickListener(new choiceType());
        saveType.setOnClickListener(new insertType());
        clearType.setOnClickListener(new cancelinsert());
        imageTitle.setOnClickListener(new choicePicture());
        imageDetatil.setOnClickListener(new choicePicture());
        date.setOnClickListener(new dateClickListener());
        clear.setOnClickListener(new clearAllInput());
        save.setOnClickListener(new saveInvoice());
        datesave.setOnClickListener(new choicedate());
        return view;
    }

    private void setInvoice() {
        invoiceVO = (InvoiceVO) getArguments().getSerializable("invoiceVO");
        name.setText(invoiceVO.getMaintype().equals("O")?"其他":invoiceVO.getMaintype());
        number.setText(invoiceVO.getInvNum());
        secondname.setText(invoiceVO.getSecondtype().equals("O")?"其他":invoiceVO.getSecondtype());
        money.setText(invoiceVO.getAmount());
        date.setText(sf.format(new Date(invoiceVO.getTime().getTime())));
        setLayout();
    }

    public void cancelshow() {
        progressDialog.cancel();
        Common.showToast(getActivity(), "財政部網路忙線~");
    }

    public void findviewByid(View view) {
        name = view.findViewById(R.id.name);
        secondname = view.findViewById(R.id.secondname);
        money = view.findViewById(R.id.money);
        date = view.findViewById(R.id.date);
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
        showAllPL = view.findViewById(R.id.showAllPL);
        number = view.findViewById(R.id.number);
        detailname = view.findViewById(R.id.detailname);
        adapter = Common.setGridViewAdapt(getActivity());
        showAllpicture.setNumColumns(4);
        showAllpicture.setAdapter(adapter);
        progressDialog = new ProgressDialog(getActivity());
    }

    public void setLayout() {
        if (!invoiceVO.getDetail().equals("0")) {
            detailname.setText("--點擊顯示--");
            detailname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] dates = date.getText().toString().split("-");
                    Calendar c = Calendar.getInstance();
                    c.set(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 12, 0, 0);
                    invoiceVO.setMaintype(name.getText().toString());
                    invoiceVO.setSecondtype(secondname.getText().toString());
                    invoiceVO.setAmount(money.getText().toString());
                    invoiceVO.setTime(new Timestamp(c.getTimeInMillis()));
                    invoiceVO.setInvNum(number.getText().toString());
                    Fragment fragment = new UpdateDetail();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("invoiceVO", invoiceVO);
                    fragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    for (Fragment fragment1 : getFragmentManager().getFragments()) {
                        fragmentTransaction.remove(fragment1);
                    }
                    fragmentTransaction.replace(R.id.body, fragment);
                    fragmentTransaction.commit();
                }
            });
        } else {
            detailname.setText("--點擊下載--");
            detailname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GetSQLDate(UpdateInvoice.this, invoiceVO).execute("reDownload");
                    progressDialog.setMessage("正在下傳資料,請稍候...");
                    progressDialog.show();
                }
            });
        }
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
                        gridView.setOnItemClickListener(new UpdateInvoice.choiceTypeitem());
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
            number.setText(" ");
        }
    }


    private class saveInvoice implements View.OnClickListener {
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
            String[] dates = date.getText().toString().split("-");
            Calendar c = Calendar.getInstance();
            c.set(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 12, 0, 0);
            invoiceVO.setMaintype(name.getText().toString());
            invoiceVO.setSecondtype(secondname.getText().toString());
            invoiceVO.setAmount(money.getText().toString());
            invoiceVO.setTime(new Timestamp(c.getTimeInMillis()));
            invoiceVO.setInvNum(number.getText().toString());
            invoiceDB.update(invoiceVO);
            Common.showToast(getActivity(), "修改成功");
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

    private class choicedate implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            choicedate = datePicker.getYear() + "-" + String.valueOf(datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
            date.setText(choicedate);
            showdate.setVisibility(View.GONE);
        }
    }
}





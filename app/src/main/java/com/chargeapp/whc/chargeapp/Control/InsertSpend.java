package com.chargeapp.whc.chargeapp.Control;


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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class InsertSpend extends Fragment {
    private EditText money, number, detailname;
    private CheckBox fixdate, notify, noWek;
    private TextView secondname, name;
    private TextView save, clear, date, datesave;
    private LinearLayout showdate, showfixdate;
    private DatePicker datePicker;
    private String choicedate;
    private Spinner choiceStatue, choiceday;
    private Gson gson;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private boolean noweek = false;
    private ConsumeDB consumeDB;
    private RelativeLayout qrcode;
    private LinearLayout firstL, secondL;
    private GridView firstG, secondG;
    private ConsumeVO consumeVO;
    private boolean needSet;
    private int updateChoice;
    private boolean first;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.insert_spend, container, false);
        needSet= (boolean) getArguments().getSerializable("needSet");
        consumeVO = new ConsumeVO();
        findviewByid(view);
        gson = new Gson();
        setSpinner();
        typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        date.setText(Common.sTwo.format(new Date(System.currentTimeMillis())));
        date.setOnClickListener(new dateClickListener());
        showdate.setOnClickListener(new choicedateClick());
        fixdate.setOnCheckedChangeListener(new showfixdateClick());
        choiceStatue.setOnItemSelectedListener(new choiceStateItem());
        clear.setOnClickListener(new clearAllInput());
        save.setOnClickListener(new savecomsumer());
        noWek.setOnCheckedChangeListener(new nowWekchange());
        qrcode.setOnClickListener(new QrCodeClick());
        name.setOnClickListener(new showFirstG());
        firstG.setOnItemClickListener(new firstGridOnClick());
        secondG.setOnItemClickListener(new secondGridOnClick());
        if(needSet)
        {
            setUpdate();
            secondname.setOnClickListener(new showSecondG());
        }
        return view;
    }

    private void setSpinner() {
        ArrayList<String> strings=new ArrayList<>();
        strings.add("每天");
        strings.add("每周");
        strings.add("每月");
        strings.add("每年");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinneritem, strings);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        choiceStatue.setAdapter(arrayAdapter);
    }

    private void setUpdate() {
        first=true;
        consumeVO = (ConsumeVO) getArguments().getSerializable("consumeVO");
        name.setText(consumeVO.getMaintype());
        number.setText(consumeVO.getNumber());
        secondname.setText(consumeVO.getSecondType());
        money.setText(String.valueOf(consumeVO.getMoney()));
        date.setText(Common.sTwo.format(consumeVO.getDate()));
        detailname.setText(consumeVO.getDetailname());
        if (consumeVO.getFixDate().equals("true")) {
            fixdate.setChecked(Boolean.valueOf(consumeVO.getFixDate()));
            notify.setChecked(Boolean.valueOf(consumeVO.getNotify()));
            JsonObject js = gson.fromJson(consumeVO.getFixDateDetail(), JsonObject.class);
            String choicestatue = js.get("choicestatue").getAsString().trim();
            String choicedate = js.get("choicedate").getAsString().trim();
            String noweek = js.get("noweek").getAsString().trim();
            noWek.setChecked(Boolean.valueOf(noweek));
            if (choicestatue.trim().equals("每天")) {
                choiceStatue.setSelection(0);
            } else if (choicestatue.trim().equals("每周")) {
                choiceStatue.setSelection(1);
                if (choicedate.equals("星期一")) {
                    updateChoice = 0;
                } else if (choicedate.equals("星期二")) {
                    updateChoice = 1;
                } else if (choicedate.equals("星期三")) {
                    updateChoice = 2;
                } else if (choicedate.equals("星期四")) {
                    updateChoice = 3;
                } else if (choicedate.equals("星期五")) {
                    updateChoice = 4;
                } else if (choicedate.equals("星期六")) {
                    updateChoice = 5;
                } else {
                    updateChoice = 6;
                }
            } else if (choicestatue.trim().equals("每月")) {
                Log.d("XXX", String.valueOf(choicedate));
                choiceStatue.setSelection(2);
                choicedate=choicedate.substring(0,choicedate.indexOf("日"));
                updateChoice = Integer.valueOf(choicedate) - 1;
            } else {
                choiceStatue.setSelection(3);
                updateChoice = Integer.valueOf(choicedate.substring(0, choicedate.indexOf("月"))) - 1;
            }
        }
        Log.d("XXX", String.valueOf(updateChoice));
    }

    @Override
    public void onStart() {
        super.onStart();
        setFirstGrid();
        setSecondGrid();
        if (Common.showfirstgrid) {
            firstL.setVisibility(View.VISIBLE);
            Common.showfirstgrid = false;
        }
        if (Common.showsecondgrid) {
            secondL.setVisibility(View.VISIBLE);
            Common.showsecondgrid = false;
        }
    }

    private void setSecondGrid() {
        HashMap item;
        ArrayList items = new ArrayList<Map<String, Object>>();
        List<TypeDetailVO> typeDetailVOS = typeDetailDB.findByGroupname(name.getText().toString().trim());
        for (TypeDetailVO t : typeDetailVOS) {
            item = new HashMap<String, Object>();
            item.put("image", Download.imageAll[t.getImage()]);
            item.put("text", t.getName());
            items.add(item);
        }
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.returnt);
        item.put("text", "返回");
        items.add(item);
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.add);
        item.put("text", "新增");
        items.add(item);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                items, R.layout.main_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        secondG.setAdapter(adapter);
        secondG.setNumColumns(4);
    }

    private void setFirstGrid() {
        HashMap item;
        ArrayList items = new ArrayList<Map<String, Object>>();
        List<TypeVO> typeVOS = typeDB.getAll();
        for (TypeVO t : typeVOS) {
            item = new HashMap<String, Object>();
            item.put("image", Download.imageAll[t.getImage()]);
            item.put("text", t.getName());
            items.add(item);
        }
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.add);
        item.put("text", "新增");
        items.add(item);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                items, R.layout.main_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        firstG.setAdapter(adapter);
        firstG.setNumColumns(4);
    }

    public void findviewByid(View view) {
        firstG = view.findViewById(R.id.firstG);
        firstL = view.findViewById(R.id.firstL);
        secondG = view.findViewById(R.id.secondG);
        secondL = view.findViewById(R.id.secondL);
        name = view.findViewById(R.id.name);
        secondname = view.findViewById(R.id.secondname);
        money = view.findViewById(R.id.money);
        date = view.findViewById(R.id.date);
        fixdate = view.findViewById(R.id.fixdate);
        save = view.findViewById(R.id.save);
        clear = view.findViewById(R.id.clear);
        datesave = view.findViewById(R.id.datesave);
        showdate = view.findViewById(R.id.showdate);
        datePicker = view.findViewById(R.id.datePicker);
        showfixdate = view.findViewById(R.id.showfixdate);
        choiceStatue = view.findViewById(R.id.choiceStatue);
        choiceday = view.findViewById(R.id.choiceday);
        number = view.findViewById(R.id.number);
        detailname = view.findViewById(R.id.detailname);
        notify = view.findViewById(R.id.notify);
        noWek = view.findViewById(R.id.noWek);
        qrcode = view.findViewById(R.id.qrcode);

    }


    private class dateClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showdate.setVisibility(View.VISIBLE);
        }

    }


    private class choicedateClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choicedate = datePicker.getYear() + "/" + String.valueOf(datePicker.getMonth() + 1) + "/" + datePicker.getDayOfMonth();
            date.setText(choicedate);
            showdate.setVisibility(View.GONE);
        }
    }


    private class showfixdateClick implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                notify.setVisibility(View.VISIBLE);
                noWek.setVisibility(View.VISIBLE);
                choiceStatue.setVisibility(View.VISIBLE);
            } else {
                notify.setVisibility(View.GONE);
                choiceStatue.setVisibility(View.GONE);
                choiceday.setVisibility(View.GONE);
                noWek.setVisibility(View.GONE);
                notify.setChecked(false);
            }
        }
    }

    private class choiceStateItem implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            ArrayList<String> spinneritem=new ArrayList<>();
            if (position==0) {
                choiceday.setVisibility(View.GONE);
                noWek.setVisibility(View.VISIBLE);
                choiceStatue.setVisibility(View.VISIBLE);
                return;
            }
            if (position==1) {
                spinneritem=Common.WeekSetSpinner();
            }
            if (position==2) {
                spinneritem=Common.DaySetSpinner();
            }
            if (position==3) {
                spinneritem=Common.MonthSetSpinner();
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinneritem, spinneritem);
            arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
            choiceday.setAdapter(arrayAdapter);
            choiceday.setVisibility(View.VISIBLE);
            noWek.setVisibility(View.GONE);
            noWek.setChecked(false);
            if (first) {
                choiceday.setSelection(updateChoice);
                first = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

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
            setConsume();
            consumeDB.insert(consumeVO);
            Common.showToast(getActivity(), "新增成功");
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
            MultiTrackerActivity.refresh = true;
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
                } else if (EleNulAll[4].equals("0")) {
                    try {
                        String a = new SetupDateBase64(this).execute("getThisDetail").get();
                        if (a.equals("InternetError")) {
                            Common.showToast(getActivity(), "連線逾時,請從新掃QRCODE");
                            return;
                        }
                        JsonObject jFT = gson.fromJson(a, JsonObject.class);
                        String s = jFT.get("details").toString();
                        Type cdType = new TypeToken<List<JsonObject>>() {
                        }.getType();
                        List<JsonObject> b = gson.fromJson(s, cdType);
                        for (JsonObject j : b) {
                            sb.append(j.get("description").getAsString() + "/" + j.get("quantity").getAsString() + "/" + j.get("unitPrice").getAsString() + " ");
                        }
                    } catch (Exception e) {
                        Common.showToast(getActivity(), e.getMessage());
                    }
                } else {
                    if (EleNulAll[3].equals("1")) {
                        sb.append(EleNulAll[5] + "/1/" + money.getText().toString());
                    } else {
                        for (int i = 5; i < EleNulAll.length; i = i + 3) {
                            sb.append(EleNulAll[i] + "/" + EleNulAll[i + 1] + "/" + EleNulAll[i + 2] + " ");
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

    private class showFirstG implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            firstL.setVisibility(View.VISIBLE);
        }
    }


    private class firstGridOnClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView textView = view.findViewById(R.id.text);
            String type = textView.getText().toString().trim();
            if (type.equals("新增")) {
                Common.showfirstgrid = true;
                returnThisFramgent(new InsertConsumeType());
                return;
            }
            name.setText(type);
            setSecondGrid();
            firstL.setVisibility(View.GONE);
            secondL.setVisibility(View.VISIBLE);
            secondname.setOnClickListener(new showSecondG());
            Common.showfirstgrid = false;
        }
    }

    private class secondGridOnClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView textView = view.findViewById(R.id.text);
            String type = textView.getText().toString().trim();
            if (type.equals("返回")) {
                firstL.setVisibility(View.VISIBLE);
                secondL.setVisibility(View.GONE);
                return;
            }
            if (type.equals("新增")) {
                Common.showsecondgrid = true;
                returnThisFramgent(new InsertConsumeType());
                return;
            }
            secondname.setText(type);
            secondL.setVisibility(View.GONE);
            Common.showsecondgrid = false;
        }
    }

    private class showSecondG implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            secondL.setVisibility(View.VISIBLE);
        }
    }


    private void returnThisFramgent(Fragment fragment) {
        setConsume();
        Bundle bundle = new Bundle();
        bundle.putSerializable("object",consumeVO);
        bundle.putSerializable("action","InsertSpend");
        bundle.putSerializable("needSet",true);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }


    private void setConsume() {
        Map<String, String> g = new HashMap<>();
        g.put("choicestatue", isnull(choiceStatue.getSelectedItem().toString()));
        g.put("choicedate", isnull(choiceday.getSelectedItem()));
        g.put("noweek", String.valueOf(noweek));
        String fixdatedetail = gson.toJson(g);
        String[] dates = date.getText().toString().split("/");
        Calendar c = Calendar.getInstance();
        c.set(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 12, 0, 0);
        Date d = new Date(c.getTimeInMillis());
        consumeVO.setMaintype(name.getText().toString());
        consumeVO.setSecondType(secondname.getText().toString());
        consumeVO.setMoney(Integer.valueOf(money.getText().toString().trim()));
        consumeVO.setDate(d);
        consumeVO.setNumber(number.getText().toString());
        consumeVO.setFixDate(String.valueOf(fixdate.isChecked()));
        consumeVO.setFixDateDetail(fixdatedetail);
        consumeVO.setNotify(String.valueOf(notify.isChecked()));
        consumeVO.setDetailname(detailname.getText().toString());
        consumeVO.setAuto(false);
        consumeVO.setAutoId(-1);
        consumeVO.setIsWin("0");
    }
}





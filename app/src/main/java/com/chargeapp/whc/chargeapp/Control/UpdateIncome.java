package com.chargeapp.whc.chargeapp.Control;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UpdateIncome extends Fragment {
    private EditText money, detailname;
    private CheckBox fixdate;
    private TextView name;
    private TextView save, clear, date, datesave;
    private LinearLayout showdate, showfixdate;
    private DatePicker datePicker;
    private String choicedate;
    private Spinner choiceStatue, choiceday;
    private Gson gson;
    private BankDB bankDB;
    private BankVO bankVO;
    private int updateChoice;
    private boolean first = true;
    private String action;
    private GridView firstG;
    private LinearLayout firstL;
    private BankTybeDB bankTybeDB;
    private TextView standard;
    private int year, month, day;
    private Map<String, String> g;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.insert_income, container, false);
        findviewByid(view);
        setSpinner();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(false);
        getActivity().setTitle("修改資料");
        action = (String) getArguments().getSerializable("action");
        bankVO = (BankVO) getArguments().getSerializable("bankVO");
        gson = new Gson();
        bankDB = new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankTybeDB = new BankTybeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        name.setOnClickListener(new showFirstG());
        firstG.setOnItemClickListener(new firstGridOnClick());
        date.setText(Common.sTwo.format(new Date(System.currentTimeMillis())));
        date.setOnClickListener(new dateClickListener());
        showdate.setOnClickListener(new choicedateClick());
        choiceStatue.setOnItemSelectedListener(new choiceStateItem());
        clear.setOnClickListener(new clearAllInput());
        save.setOnClickListener(new savecomsumer());
        fixdate.setOnCheckedChangeListener(new showfixdateClick());
        if (action.equals("SettingListFixIon") && (!bankVO.isAuto())) {
            standard.setText("全部");
            standard.setVisibility(View.VISIBLE);
            standard.setOnClickListener(new saveAllConsume());
        }
        setUpdate();
        return view;
    }

    private void setSpinner() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("每天");
        strings.add("每周");
        strings.add("每月");
        strings.add("每年");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinneritem, strings);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        choiceStatue.setAdapter(arrayAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        setFirstGrid();
        if (Common.showfirstgrid) {
            firstL.setVisibility(View.VISIBLE);
            Common.showfirstgrid = false;
        }
    }

    private class firstGridOnClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView textView = view.findViewById(R.id.text);
            String type = textView.getText().toString().trim();
            if (type.equals("新增")) {
                Common.showfirstgrid = true;
                Fragment fragment = new InsertIncomeType();
                Bundle bundle = new Bundle();
                setBankVO();
                if (action.equals("SelectListPieIncome")) {
                    bundle.putSerializable("type", getArguments().getSerializable("type"));
                    bundle.putStringArrayList("OKey", getArguments().getStringArrayList("OKey"));
                    gotoFramgent(fragment, bundle);
                } else if (action.equals("SettingListFixIon")) {
                    bundle.putSerializable("bankVO", bankVO);
                    bundle.putSerializable("action", action);
                    bundle.putSerializable("position", getArguments().getSerializable("position"));
                    fragment.setArguments(bundle);
                    switchFragment(fragment);
                } else if (action.equals("SelectListPieIncome")) {
                    gotoFramgent(fragment, bundle);
                }
                return;
            }
            name.setText(type);
            firstL.setVisibility(View.GONE);
            Common.showfirstgrid = false;
        }
    }


    private class showFirstG implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            firstL.setVisibility(View.VISIBLE);
        }
    }

    private void setUpdate() {
        first = true;
        name.setText(bankVO.getMaintype());
        money.setText(bankVO.getMoney());
        date.setText(Common.sTwo.format(bankVO.getDate()));
        detailname.setText(bankVO.getDetailname());
        fixdate.setChecked(Boolean.valueOf(bankVO.getFixDate()));
        if (bankVO.getFixDate().equals("true")) {
            JsonObject js = gson.fromJson(bankVO.getFixDateDetail(), JsonObject.class);
            String choicestatue = js.get("choicestatue").getAsString().trim();
            String choicedate = js.get("choicedate").getAsString().trim();
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
                choiceStatue.setSelection(2);
                choicedate = choicedate.substring(0, choicedate.indexOf("日"));
                updateChoice = Integer.valueOf(choicedate) - 1;
            } else {
                choiceStatue.setSelection(3);
                updateChoice = Integer.valueOf(choicedate.substring(0, choicedate.indexOf("月"))) - 1;
            }
        }
    }


    public void findviewByid(View view) {
        gson = new Gson();
        name = view.findViewById(R.id.name);
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
        detailname = view.findViewById(R.id.detailname);
        firstG = view.findViewById(R.id.firstG);
        firstL = view.findViewById(R.id.firstL);
        standard = view.findViewById(R.id.standard);
        getActivity().setTitle("修改資料");
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
                fixdate.setVisibility(View.VISIBLE);
                fixdate.setX(showfixdate.getWidth() / 10);
                choiceStatue.setX(showfixdate.getWidth() / 2 + showfixdate.getWidth() / 10);
                choiceStatue.setVisibility(View.VISIBLE);
            } else {
                choiceStatue.setVisibility(View.GONE);
                choiceday.setVisibility(View.GONE);
                fixdate.setX(showfixdate.getWidth() / 3);
            }
        }
    }

    private class choiceStateItem implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            String choiceitem = adapterView.getItemAtPosition(position).toString();
            ArrayList<String> spinneritem = new ArrayList<>();
            if (position == 0) {
                choiceday.setVisibility(View.GONE);
                fixdate.setX(showfixdate.getWidth() / 10);
                choiceStatue.setX(showfixdate.getWidth() / 2 + showfixdate.getWidth() / 10);
                choiceStatue.setVisibility(View.VISIBLE);
                return;
            }
            if (position == 1) {
                spinneritem.add("星期一");
                spinneritem.add("星期二");
                spinneritem.add("星期三");
                spinneritem.add("星期四");
                spinneritem.add("星期五");
                spinneritem.add("星期六");
                spinneritem.add("星期日");
            }
            if (position == 2) {
                for (int i = 1; i <= 31; i++) {
                    spinneritem.add(" " + String.valueOf(i) + "日");
                }
            }
            if (position == 3) {
                for (int i = 1; i <= 12; i++) {
                    spinneritem.add(" " + String.valueOf(i) + "月");
                }
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinneritem, spinneritem);
            arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
            choiceday.setAdapter(arrayAdapter);
            choiceday.setVisibility(View.VISIBLE);
            if (first) {
                choiceday.setSelection(updateChoice);
                first = false;
            }
            fixdate.setX(showfixdate.getWidth() / 20);
            choiceStatue.setX(showfixdate.getWidth() / 3 + showfixdate.getWidth() / 20);
            choiceday.setX((showfixdate.getWidth() * 2 / 3) + showfixdate.getWidth() / 20);
        }


        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }


    private class clearAllInput implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            name.setText(" ");
            money.setText(" ");
            fixdate.setChecked(false);
        }
    }

    private String isnull(Object text) {
        if (text == null || text.toString().length() <= 0) {
            return " ";
        }
        return text.toString();
    }

    private void setBankVO() {
        g = new HashMap<>();
        g.put("choicestatue", isnull(choiceStatue.getSelectedItem().toString()));
        g.put("choicedate", isnull(choiceday.getSelectedItem()));
        String fixdatedetail = gson.toJson(g);
        String[] dates = date.getText().toString().split("/");
        Calendar c = Calendar.getInstance();
        year = Integer.valueOf(dates[0]);
        month = (Integer.valueOf(dates[1]) - 1);
        day = Integer.valueOf(dates[2]);
        c.set(year, month, day, 12, 0, 0);
        Date d = new Date(c.getTimeInMillis());
        bankVO.setMaintype(name.getText().toString());
        bankVO.setMoney(money.getText().toString());
        bankVO.setDate(d);
        bankVO.setFixDate(String.valueOf(fixdate.isChecked()));
        bankVO.setFixDateDetail(fixdatedetail);
        bankVO.setDetailname(detailname.getText().toString());
    }

    private void setFirstGrid() {
        HashMap item;
        ArrayList items = new ArrayList<Map<String, Object>>();
        List<BankTypeVO> bankTypeVOS = bankTybeDB.getAll();
        for (BankTypeVO t : bankTypeVOS) {
            item = new HashMap<String, Object>();
            item.put("image", MainActivity.imageAll[t.getImage()]);
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


    private class savecomsumer implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            name.setBackgroundColor(Color.parseColor("#FFEE99"));
            if (name.getText().toString().trim() == null || name.getText().toString().trim().length() == 0) {
                name.setBackgroundColor(Color.parseColor("#ff471a"));
                Common.showToast(getActivity(), "主項目不能空白");
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
            setBankVO();
            bankDB.update(bankVO);
            Fragment fragment = null;
            Bundle bundle = new Bundle();
            if (action.equals("SelectListBarIncome")) {
                fragment = new SelectListBarIncome();
                gotoFramgent(fragment, bundle);
            } else if (action.equals("SelectListPieIncome")) {
                fragment = new SelectListPieIncome();
                bundle.putSerializable("type", getArguments().getSerializable("type"));
                bundle.putStringArrayList("OKey", getArguments().getStringArrayList("OKey"));
                gotoFramgent(fragment, bundle);
            } else if (action.equals("SettingListFixIon")) {
                fragment = new SettingListFixIon();
                bundle.putSerializable("bankVO", bankVO);
                bundle.putSerializable("action", action);
                bundle.putSerializable("position", getArguments().getSerializable("position"));
                fragment.setArguments(bundle);
                switchFragment(fragment);
            }
            Common.showToast(getActivity(), "修改成功");
        }
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

    private void gotoFramgent(Fragment fragment, Bundle bundle) {

        bundle.putSerializable("bankVO", bankVO);
        bundle.putSerializable("action", action);
        bundle.putSerializable("position", getArguments().getSerializable("position"));
        bundle.putSerializable("year", getArguments().getSerializable("year"));
        bundle.putSerializable("month", getArguments().getSerializable("month"));
        bundle.putSerializable("day", getArguments().getSerializable("day"));
        bundle.putSerializable("statue", getArguments().getSerializable("statue"));
        bundle.putSerializable("index", getArguments().getSerializable("index"));
        fragment.setArguments(bundle);
        switchFragment(fragment);
    }

    private class saveAllConsume implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            name.setBackgroundColor(Color.parseColor("#FFEE99"));
            if (name.getText().toString().trim() == null || name.getText().toString().trim().length() == 0) {
                name.setBackgroundColor(Color.parseColor("#ff471a"));
                Common.showToast(getActivity(), "主項目不能空白");
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
            setBankVO();
            List<BankVO> bankVOS = bankDB.getAutoSetting(bankVO.getId());
            String statue = choiceStatue.getSelectedItem().toString().trim();
            String dateSpinner = g.get("choicedate").trim();
            Calendar cd = new GregorianCalendar(year, month, day, 12, 0, 0);
            int dweek = cd.get(Calendar.WEEK_OF_MONTH);
            int i = 0;
            for (BankVO bb : bankVOS) {

                bb.setMaintype(bb.getMaintype());
                bb.setDetailname(bb.getDetailname());
                bb.setMoney(bb.getMoney());
                bb.setFixDateDetail(bb.getFixDateDetail());
                if (statue.equals("每天")) {
                    i++;
                    Calendar calendar = new GregorianCalendar(year, month, day + i, 12, 0, 0);
                    bb.setDate(new Date(calendar.getTimeInMillis()));
                } else if (statue.equals("每周")) {
                    i++;
                    if (dateSpinner.equals("星期一")) {
                        cd.set(Calendar.WEEK_OF_MONTH, dweek + i);
                        cd.set(Calendar.DAY_OF_WEEK, 2);
                    } else if (dateSpinner.equals("星期二")) {
                        cd.set(Calendar.WEEK_OF_MONTH, dweek + i);
                        cd.set(Calendar.DAY_OF_WEEK, 3);
                    } else if (dateSpinner.equals("星期三")) {
                        cd.set(Calendar.WEEK_OF_MONTH, dweek + i);
                        cd.set(Calendar.DAY_OF_WEEK, 4);
                    } else if (dateSpinner.equals("星期四")) {
                        cd.set(Calendar.WEEK_OF_MONTH, dweek + i);
                        cd.set(Calendar.DAY_OF_WEEK, 5);
                    } else if (dateSpinner.equals("星期五")) {
                        cd.set(Calendar.WEEK_OF_MONTH, dweek + i);
                        cd.set(Calendar.DAY_OF_WEEK, 6);
                    } else if (dateSpinner.equals("星期六")) {
                        cd.set(Calendar.WEEK_OF_MONTH, dweek + i);
                        cd.set(Calendar.DAY_OF_WEEK, 7);
                    } else {
                        cd.set(Calendar.WEEK_OF_MONTH, dweek + i);
                        cd.set(Calendar.DAY_OF_WEEK, 1);
                    }
                    bb.setDate(new Date(cd.getTimeInMillis()));
                } else if (statue.equals("每月")) {
                    i++;
                    String string = dateSpinner.substring(0, dateSpinner.indexOf("日"));
                    Common.showToast(getActivity(), dateSpinner + " : " + dateSpinner.length());
                    int choiceD = Integer.valueOf(string.trim());
                    Calendar calendar = new GregorianCalendar(year, month + i, 1, 12, 0, 0);
                    int monMax = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if (choiceD > monMax) {
                        calendar = new GregorianCalendar(year, month + i, monMax, 12, 0, 0);
                    } else {
                        calendar = new GregorianCalendar(year, month + i, choiceD, 12, 0, 0);
                    }
                    bb.setDate(new Date(calendar.getTimeInMillis()));

                } else if (statue.equals("每年")) {
                    i++;
                    int choiceD = Integer.valueOf(dateSpinner.substring(0, dateSpinner.indexOf("月"))) - 1;
                    Calendar calendar = new GregorianCalendar(year + i, choiceD, 1, 12, 0, 0);
                    bb.setDate(new Date(calendar.getTimeInMillis()));
                }
                bankDB.update(bb);
            }
            Bundle bundle = new Bundle();
            Fragment fragment = new SettingListFixIon();
            bundle.putSerializable("bankVO", bankVO);
            bundle.putSerializable("action", action);
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            fragment.setArguments(bundle);
            switchFragment(fragment);
            Common.showToast(getActivity(), "修改成功");
        }
    }
}



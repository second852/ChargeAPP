package com.chargeapp.whc.chargeapp.Control.Insert;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.beardedhen.androidbootstrap.BootstrapText;

import com.beardedhen.androidbootstrap.api.defaults.ExpandDirection;
import com.chargeapp.whc.chargeapp.Adapter.KeyBoardInputNumberOnItemClickListener;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.Download;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateConsumeDetail;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.ui.BarcodeGraphic;
import com.chargeapp.whc.chargeapp.ui.MultiTrackerActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import org.jsoup.internal.StringUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Calendar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import static com.chargeapp.whc.chargeapp.Control.Common.insertCurrency;
import static com.chargeapp.whc.chargeapp.Control.Common.onlyNumber;

public class InsertSpend extends Fragment {

    private BootstrapEditText number, name, money, secondName, date;
    private CheckBox fixDate, notify, noWek;
    private BootstrapLabel detailName;
    private BootstrapButton save, clear;
    private LinearLayout showDate;
    private DatePicker datePicker;
    private String choiceDate;
    private BootstrapDropDown choiceStatue, choiceDay;
    private Gson gson;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private boolean noweek = false;
    private ConsumeDB consumeDB;
    private AwesomeTextView qrcode;
    private LinearLayout firstL, secondL;
    private GridView firstG, secondG;
    public static ConsumeVO consumeVO;
    public static boolean needSet;
    private int updateChoice;
    private TextView noWekT, notifyT;
    private Activity context;
    private String oldMainType;
    private TypeVO typeVO;
    private List<TypeVO> typeVOS;
    private List<BootstrapText> BsTextDay, BsTextWeek, BsTextMonth, BsTextStatue;
    private int statueNumber;
    private String resultStatue, resultDay;
    private View view;
    private RelativeLayout notifyRel;
    private BootstrapButton currency, calculate;
    private SharedPreferences sharedPreferences;
    private String nowCurrency;
    private PopupMenu popupMenu;
    private GridView numberKeyBoard;

    public static InsertSpend instance() {
        return new InsertSpend();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = (Activity) context;
        } else {
            this.context = getActivity();
        }
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.insert_spend, container, false);
        Common.setChargeDB(context);
        if (consumeVO == null) {
            consumeVO = new ConsumeVO();
        }
        findViewById();
        gson = new Gson();
        setSetOnClickView();
        new Thread(runnable).start();
        new Thread(setOnClick).start();
        new Thread(setKeyboard).start();
        return view;
    }


    private void setUpdate() {
        if (consumeVO.getMaintype().equals("O")) {
            name.setText("其他");
            secondName.setText("其他");
        } else if (consumeVO.getSecondType().equals("0")) {
            name.setText("未知");
            secondName.setText("未知");
        } else {
            name.setText(consumeVO.getMaintype());
            secondName.setText(consumeVO.getSecondType());
        }

        number.setText(consumeVO.getNumber());

        if(StringUtil.isBlank(consumeVO.getRealMoney()))
        {
            consumeVO.setRealMoney(String.valueOf(consumeVO.getMoney()));
            consumeDB.update(consumeVO);
        }
        StringBuilder sb= new StringBuilder();
        sb.append(consumeVO.getRealMoney());
        numberKeyBoard.setOnItemClickListener(new KeyBoardInputNumberOnItemClickListener(calculate, money, context, numberKeyBoard, sb, false));

        money.setText(String.valueOf(consumeVO.getRealMoney()));
        nowCurrency=consumeVO.getCurrency();
        currency.setText(Common.getCurrency(nowCurrency));
        date.setText(Common.sTwo.format(consumeVO.getDate()));
        if (consumeVO.getFixDate() == null) {
            setSecondGrid();
            needSet = false;
            return;
        }
        if (consumeVO.getFixDate().equals("true")) {
            fixDate.setChecked(Boolean.valueOf(consumeVO.getFixDate()));
            notify.setChecked(Boolean.valueOf(consumeVO.getNotify()));
            JsonObject js = gson.fromJson(consumeVO.getFixDateDetail(), JsonObject.class);
            String choicestatue = js.get("choicestatue").getAsString().trim();
            String choicedate = js.get("choicedate").getAsString().trim();
            String noweek = js.get("noweek").getAsString().trim();
            noWek.setChecked(Boolean.valueOf(noweek));
            if (choicestatue.trim().equals("每天")) {
                statueNumber = 0;
                noWekT.setVisibility(View.VISIBLE);
                noWek.setVisibility(View.VISIBLE);
                choiceDay.setVisibility(View.GONE);
                resultStatue = BsTextStatue.get(0).toString();
                choiceStatue.setBootstrapText(BsTextStatue.get(0));
                choiceStatue.setVisibility(View.VISIBLE);
                choiceDay.setExpandDirection(ExpandDirection.UP);
                resultDay = "";
            } else if (choicestatue.trim().equals("每周")) {
                statueNumber = 1;
                noWekT.setVisibility(View.GONE);
                noWek.setVisibility(View.GONE);
                choiceDay.setVisibility(View.VISIBLE);
                resultStatue = BsTextStatue.get(1).toString();
                choiceStatue.setBootstrapText(BsTextStatue.get(1));
                choiceDay.setDropdownData(Common.WeekSetSpinnerBS);
                choiceStatue.setVisibility(View.VISIBLE);
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
                choiceDay.setBootstrapText(BsTextWeek.get(updateChoice));
                resultDay = BsTextWeek.get(updateChoice).toString();
                choiceDay.setExpandDirection(ExpandDirection.UP);
            } else if (choicestatue.trim().equals("每月")) {
                statueNumber = 2;
                noWekT.setVisibility(View.GONE);
                noWek.setVisibility(View.GONE);
                choiceDay.setVisibility(View.VISIBLE);
                resultStatue = BsTextStatue.get(2).toString();
                choiceStatue.setBootstrapText(BsTextStatue.get(2));
                choiceStatue.setVisibility(View.VISIBLE);
                choicedate = choicedate.substring(0, choicedate.indexOf("日"));
                updateChoice = Integer.valueOf(choicedate) - 1;
                resultDay = BsTextDay.get(updateChoice).toString();
                choiceDay.setBootstrapText(BsTextDay.get(updateChoice));
                choiceDay.setDropdownData(Common.DaySetSpinnerBS());
                choiceDay.setExpandDirection(ExpandDirection.UP);
            } else {
                statueNumber = 3;
                noWekT.setVisibility(View.GONE);
                noWek.setVisibility(View.GONE);
                choiceDay.setVisibility(View.VISIBLE);
                resultStatue = BsTextStatue.get(3).toString();
                choiceStatue.setBootstrapText(BsTextStatue.get(3));
                choiceStatue.setVisibility(View.VISIBLE);
                updateChoice = Integer.valueOf(choicedate.substring(0, choicedate.indexOf("月"))) - 1;
                resultDay = BsTextMonth.get(updateChoice).toString();
                choiceDay.setBootstrapText(BsTextMonth.get(updateChoice));
                choiceDay.setDropdownData(Common.MonthSetSpinnerBS());
                choiceDay.setExpandDirection(ExpandDirection.UP);
            }
        }
        setSecondGrid();
        needSet = false;
    }

    private Handler handlerPicture = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    setFirstGridAdapter((ArrayList<Map<String, Object>>) msg.obj);
                    if (Common.showfirstgrid) {
                        firstL.setVisibility(View.VISIBLE);
                        Common.showfirstgrid = false;
                    }
                    currency.setText(Common.getCurrency(nowCurrency));
                    break;
                case 1:
                    setSecondGridAdapt((ArrayList<Map<String, Object>>) msg.obj);
                    break;
                case 2:
                    choiceStatue.setVisibility(View.GONE);
                    choiceDay.setVisibility(View.GONE);
                    setUpdate();
                    secondName.setOnFocusChangeListener(new showSecondG());
                    secondName.setOnClickListener(new showSecondG());
                    secondName.setInputType(InputType.TYPE_NULL);

                    if (Common.showsecondgrid) {
                        secondL.setVisibility(View.VISIBLE);
                        Common.showsecondgrid = false;
                    }
                    break;
                case 3:
                    choiceStatue.setVisibility(View.GONE);
                    choiceDay.setVisibility(View.GONE);
                    date.setText(Common.sTwo.format(new Date(System.currentTimeMillis())));
                    break;
                case 4:
                    setKeyBoardGridAdapter(((ArrayList<Map<String, Object>>) msg.obj));
                    money.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {

                            if (b) {
                                numberKeyBoard.setVisibility(View.VISIBLE);
                                firstL.setVisibility(View.GONE);
                                secondL.setVisibility(View.GONE);
                                showDate.setVisibility(View.GONE);

                                Common.clossKeyword(context);
                                number.clearFocus();
                                name.clearFocus();
                                secondName.clearFocus();
                                date.clearFocus();
                            }
                        }
                    });
                    money.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            numberKeyBoard.setVisibility(View.VISIBLE);
                            firstL.setVisibility(View.GONE);
                            secondL.setVisibility(View.GONE);
                            showDate.setVisibility(View.GONE);
                        }
                    });
                    break;
                case 5:
                    name.setShowSoftInputOnFocus(false);
                    secondName.setShowSoftInputOnFocus(false);
                    money.setShowSoftInputOnFocus(false);
                    date.setShowSoftInputOnFocus(false);
                    break;
            }
        }
    };


    private Runnable setKeyboard = new Runnable() {
        @Override
        public void run() {
            numberKeyBoard = view.findViewById(R.id.numberKeyBoard);
            calculate = view.findViewById(R.id.calculate);
            money = view.findViewById(R.id.money);
            numberKeyBoard.setOnItemClickListener(new KeyBoardInputNumberOnItemClickListener(calculate, money, context, numberKeyBoard, new StringBuilder(), true));
            ArrayList items = new ArrayList<Map<String, Object>>();
            Map<String, Object> hashMap;
            for (String s : Common.keyboardArray) {
                hashMap = new HashMap<>();
                hashMap.put("text", s);
                items.add(hashMap);
            }
            Message message = new Message();
            message.obj = items;
            message.what = 4;
            handlerPicture.sendMessage(message);
        }
    };


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //set Currency
            sharedPreferences = context.getSharedPreferences("Charge_User", Context.MODE_PRIVATE);
            nowCurrency = sharedPreferences.getString(insertCurrency, "TWD");

            currency = view.findViewById(R.id.currency);
            popupMenu = new PopupMenu(context, currency);
            Common.createCurrencyPopMenu(popupMenu, context);
            currency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupMenu.show();
                }
            });
            popupMenu.setOnMenuItemClickListener(new choiceCurrency());

            //type
            typeDB = new TypeDB(MainActivity.chargeAPPDB);
            consumeDB = new ConsumeDB(MainActivity.chargeAPPDB);
            firstG = view.findViewById(R.id.firstG);
            firstG.setOnItemClickListener(new firstGridOnClick());
            setFirstGrid();
        }
    };

    private Runnable setOnClick = new Runnable() {
        @Override
        public void run() {
            BsTextDay = Common.DateChoiceSetBsTest(context, Common.DaySetSpinnerBS());
            BsTextWeek = Common.DateChoiceSetBsTest(context, Common.WeekSetSpinnerBS);
            BsTextMonth = Common.DateChoiceSetBsTest(context, Common.MonthSetSpinnerBS());
            BsTextStatue = Common.DateChoiceSetBsTest(context, Common.DateStatueSetSpinner);


            handlerPicture.sendEmptyMessage(5);
            if (needSet) {
                handlerPicture.sendEmptyMessage(2);
            } else {
                handlerPicture.sendEmptyMessage(3);
            }
        }
    };

    private void setSetOnClickView() {

        showDate.setOnClickListener(new choicedateClick());
        fixDate.setOnCheckedChangeListener(new showfixdateClick());
        choiceStatue.setOnDropDownItemClickListener(new choiceStateItemBS());
        choiceDay.setOnDropDownItemClickListener(new choicedayItemBS());
        clear.setOnClickListener(new clearAllInput());
        save.setOnClickListener(new savecomsumer());
        noWek.setOnCheckedChangeListener(new nowWekchange());
        qrcode.setOnClickListener(new QrCodeClick());
        number.setOnClickListener(new closeAllGridView());
        number.setOnFocusChangeListener(new closeAllGridView());
        name.setOnClickListener(new showFirstG());
        name.setOnFocusChangeListener(new showFirstG());
        name.setInputType(InputType.TYPE_NULL);
        date.setOnFocusChangeListener(new dateClickListener());
        date.setOnClickListener(new dateClickListener());
        detailName.setOnClickListener(new DetailEdit());
        secondG.setOnItemClickListener(new secondGridOnClick());
    }

    private void setSecondGrid() {
        HashMap item;
        ArrayList items = new ArrayList<Map<String, Object>>();
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB);
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
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.cancel);
        item.put("text", "取消");
        items.add(item);
        Message message = new Message();
        message.obj = items;
        message.what = 1;
        handlerPicture.sendMessage(message);
    }

    private void setSecondGridAdapt(ArrayList<Map<String, Object>> items) {
        SimpleAdapter adapter = new SimpleAdapter(context,
                items, R.layout.main_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        secondG.setAdapter(adapter);
        secondG.setNumColumns(4);
    }


    private void setFirstGrid() {
        try {
            HashMap item;
            ArrayList items = new ArrayList<Map<String, Object>>();
            typeVOS = typeDB.getAll();
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
            item = new HashMap<String, Object>();
            item.put("image", R.drawable.cancel);
            item.put("text", "取消");
            items.add(item);
            Message message = new Message();
            message.obj = items;
            message.what = 0;
            handlerPicture.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFirstGridAdapter(ArrayList<Map<String, Object>> items) {
        SimpleAdapter adapter = new SimpleAdapter(context, items, R.layout.main_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        firstG.setAdapter(adapter);
        firstG.setNumColumns(4);
    }

    private void setKeyBoardGridAdapter(ArrayList<Map<String, Object>> items) {
        SimpleAdapter adapter = new SimpleAdapter(context, items, R.layout.ele_hand_item, new String[]{"text"},
                new int[]{R.id.cardview});
        numberKeyBoard.setAdapter(adapter);
        numberKeyBoard.setNumColumns(5);
    }


    public void findViewById() {
        secondName = view.findViewById(R.id.secondname);
        money = view.findViewById(R.id.money);
        date = view.findViewById(R.id.date);
        fixDate = view.findViewById(R.id.fixdate);
        save = view.findViewById(R.id.save);
        clear = view.findViewById(R.id.clear);
        showDate = view.findViewById(R.id.showdate);
        datePicker = view.findViewById(R.id.datePicker);
        choiceStatue = view.findViewById(R.id.choiceStatue);
        choiceDay = view.findViewById(R.id.choiceday);
        number = view.findViewById(R.id.number);
        detailName = view.findViewById(R.id.detailname);
        notify = view.findViewById(R.id.notify);
        noWek = view.findViewById(R.id.noWek);
        qrcode = view.findViewById(R.id.qrcode);
        name = view.findViewById(R.id.name);
        secondG = view.findViewById(R.id.secondG);
        secondL = view.findViewById(R.id.secondL);
        noWekT = view.findViewById(R.id.noWekT);
        notifyT = view.findViewById(R.id.notifyT);
        firstL = view.findViewById(R.id.firstL);
        notifyRel = view.findViewById(R.id.notifyRel);

    }


    private class dateClickListener implements View.OnFocusChangeListener, View.OnClickListener {

        @Override
        public void onFocusChange(View view, boolean b) {

            if (b) {
                firstL.setVisibility(View.GONE);
                secondL.setVisibility(View.GONE);
                showDate.setVisibility(View.VISIBLE);
                numberKeyBoard.setVisibility(View.GONE);

                Common.clossKeyword(context);
                name.clearFocus();
                number.clearFocus();
                money.clearFocus();
                secondName.clearFocus();

            }

            date.setSelection(date.getText().toString().length());
        }

        @Override
        public void onClick(View view) {
            firstL.setVisibility(View.GONE);
            secondL.setVisibility(View.GONE);
            showDate.setVisibility(View.VISIBLE);
            numberKeyBoard.setVisibility(View.GONE);
        }
    }


    private class choicedateClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choiceDate = datePicker.getYear() + "/" + String.valueOf(datePicker.getMonth() + 1) + "/" + datePicker.getDayOfMonth();
            date.setText(choiceDate);
            showDate.setVisibility(View.GONE);
            date.setSelection(choiceDate.length());
        }
    }


    private class showfixdateClick implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                notifyRel.setVisibility(View.VISIBLE);
                choiceStatue.setBootstrapText(BsTextStatue.get(0));
                resultStatue = BsTextStatue.get(0).toString();
                notifyT.setVisibility(View.VISIBLE);
                noWekT.setVisibility(View.VISIBLE);
                notify.setVisibility(View.VISIBLE);
                noWek.setVisibility(View.VISIBLE);
                choiceStatue.setVisibility(View.VISIBLE);
            } else {
                resultStatue = "";
                resultDay = "";
                notifyT.setVisibility(View.GONE);
                noWekT.setVisibility(View.GONE);
                notify.setVisibility(View.GONE);
                choiceStatue.setVisibility(View.GONE);
                choiceDay.setVisibility(View.GONE);
                noWek.setVisibility(View.GONE);
                notify.setChecked(false);
            }
        }
    }

//    private class choiceStateItem implements AdapterView.OnItemSelectedListener {
//        @Override
//        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//            ArrayList<String> spinneritem = new ArrayList<>();
//            if (position == 0) {
//                choiceday.setVisibility(View.GONE);
//                noWek.setVisibility(View.VISIBLE);
//                choiceStatue.setVisibility(View.VISIBLE);
//                notifyT.setVisibility(View.VISIBLE);
//                noWekT.setVisibility(View.VISIBLE);
//                return;
//            }
//            if (position == 1) {
//                spinneritem = Common.WeekSetSpinner();
//            }
//            if (position == 2) {
//                spinneritem = Common.DaySetSpinner();
//            }
//            if (position == 3) {
//                spinneritem = Common.MonthSetSpinner();
//            }
//            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinneritem, spinneritem);
//            arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
//            choiceday.setAdapter(arrayAdapter);
//            choiceday.setVisibility(View.VISIBLE);
//            notifyT.setVisibility(View.VISIBLE);
//            noWek.setVisibility(View.GONE);
//            noWekT.setVisibility(View.GONE);
//            noWek.setChecked(false);
//            if (first) {
//                choiceday.setSelection(updateChoice);
//                first = false;
//            }
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> adapterView) {
//
//        }
//    }


    private class clearAllInput implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //設定種類時 不能儲存
            if (firstL.getVisibility() == View.VISIBLE) {
                return;
            }
            if (secondL.getVisibility() == View.VISIBLE) {
                return;
            }

            //date show not save
            if (showDate.getVisibility() == View.VISIBLE) {
                return;
            }
            name.setText("");
            secondName.setText("");
            money.setText("");
            numberKeyBoard.setOnItemClickListener(new KeyBoardInputNumberOnItemClickListener(calculate, money, context, numberKeyBoard, new StringBuilder(), true));
            fixDate.setChecked(false);
            number.setText("");
            consumeVO = new ConsumeVO();
            choiceStatue.setBootstrapText(BsTextStatue.get(0));
            choiceDay.setBootstrapText(BsTextDay.get(0));
            choiceStatue.setVisibility(View.GONE);
            choiceDay.setVisibility(View.GONE);
            resultDay = "";
            resultStatue = "";
        }
    }

    private String isnull(String text) {
        if (text == null || text.toString().length() <= 0) {
            return " ";
        }
        text = text.substring(0, text.lastIndexOf(" "));
        return text;
    }


    private class savecomsumer implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            //設定種類時 不能儲存
            if (firstL.getVisibility() == View.VISIBLE) {
                return;
            }
            if (secondL.getVisibility() == View.VISIBLE) {
                return;
            }

            //date show not save
            if (showDate.getVisibility() == View.VISIBLE) {
                return;
            }

            //必填資料
            if (name.getText() == null || name.getText().toString().trim().length() == 0) {
                name.setError("主項目不能空白");
                return;
            }

            //必填資料
            if (name.getText().toString().trim().equals("0") || name.getText().toString().trim().equals("O")) {
                name.setError("主項目不能為其他");
                return;
            }

            if (secondName.getText() == null || secondName.getText().toString().trim().length() == 0) {
                secondName.setError("次項目不能空白");
                return;
            }

            if (secondName.getText().toString().trim().equals("0") || secondName.getText().toString().trim().equals("O")) {
                secondName.setError("次項目不能為其他");
                return;
            }

            try {
                if (!oldMainType.equals(name.getText().toString().trim())) {
                    secondName.setError("次項目不屬於主項目種類");
                    return;
                }
            } catch (Exception e) {

            }


            if (money.getText() == null || money.getText().toString().trim().length() == 0) {
                money.setError("金額不能空白!");
                return;
            }

            try {
                if (Common.nf.parse(money.getText().toString().trim()).doubleValue() == 0) {
                    money.setError("金額不能為0");
                    return;
                }
            } catch (Exception e) {
                money.setError("不是數字!");
                return;
            }


            if (date.getText() == null || date.getText().toString().trim().length() == 0) {
                date.setError("日期不能空白");
                return;
            }

            String CheckNul = number.getText().toString();
            if (CheckNul.trim().length() > 0) {
                if (CheckNul.length() != 10) {
                    number.setError("統一發票中英文10個號碼");
                    return;
                }
                try {
                    Integer.valueOf(CheckNul.substring(2));
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
            Common.showToast(context, "新增成功");
            consumeVO = new ConsumeVO();
            name.setText("");
            secondName.setText("");
            money.setText("");
            fixDate.setChecked(false);
            number.setText("");
            numberKeyBoard.setOnItemClickListener(new KeyBoardInputNumberOnItemClickListener(calculate, money, context, numberKeyBoard, new StringBuilder(), true));
//            choiceStatue.setBottom(0);
//            choiceday.setSelection(0);
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
            final PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.camera:
                            setConsume();
                            BarcodeGraphic.hashMap = new HashMap<>();
                            Intent intent = new Intent(InsertSpend.this.context, MultiTrackerActivity.class);
                            intent.putExtra("action", "setConsume");
                            startActivityForResult(intent, 6);
                            break;
                        case R.id.searchInternet:
                            Fragment fragment = new SearchByQrCode();
                            returnThisFramgent(fragment);
                            break;
                        default:
                            popupMenu.dismiss();
                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }

    private class showFirstG implements View.OnClickListener, View.OnFocusChangeListener {

        @Override
        public void onClick(View view) {
            showDate.setVisibility(View.GONE);
            secondL.setVisibility(View.GONE);
            firstL.setVisibility(View.VISIBLE);
            numberKeyBoard.setVisibility(View.GONE);
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            if(b)
            {
                Common.clossKeyword(context);
                number.clearFocus();
                money.clearFocus();
                secondName.clearFocus();
                date.clearFocus();
                showDate.setVisibility(View.GONE);
                secondL.setVisibility(View.GONE);
                firstL.setVisibility(View.VISIBLE);
                numberKeyBoard.setVisibility(View.GONE);
            }

        }
    }



    private class firstGridOnClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            name.setError(null);
            TextView textView = view.findViewById(R.id.text);
            String type = textView.getText().toString().trim();
            if (i < typeVOS.size()) {
                typeVO = typeVOS.get(i);
            }
            if (type.equals("新增")) {
                Common.showfirstgrid = true;
                returnThisFramgent(new InsertConsumeType());
                return;
            }
            if (type.equals("取消")) {
                firstL.setVisibility(View.GONE);
                Common.showfirstgrid = false;
                return;
            }
            name.setText(type);
            name.setSelection(type.length());


            setSecondGrid();
            firstL.setVisibility(View.GONE);
            secondL.setVisibility(View.VISIBLE);
            secondName.setOnFocusChangeListener(new showSecondG());
            secondName.setOnClickListener(new showSecondG());
            secondName.setInputType(InputType.TYPE_NULL);
            Common.showfirstgrid = false;
        }
    }

    private class secondGridOnClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            secondName.setError(null);
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
            if (type.equals("取消")) {
                Common.showsecondgrid = false;
                secondL.setVisibility(View.GONE);
                return;
            }
            oldMainType = name.getText().toString().trim();
            secondName.setText(type);
            secondName.setSelection(type.length());
            secondL.setVisibility(View.GONE);
            Common.showsecondgrid = false;

        }
    }

    private class showSecondG implements View.OnFocusChangeListener, View.OnClickListener {

        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                Common.clossKeyword(context);
                number.clearFocus();
                money.clearFocus();
                name.clearFocus();
                date.clearFocus();
                firstL.setVisibility(View.GONE);
                showDate.setVisibility(View.GONE);
                numberKeyBoard.setVisibility(View.GONE);
                secondL.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onClick(View view) {
            firstL.setVisibility(View.GONE);
            showDate.setVisibility(View.GONE);
            numberKeyBoard.setVisibility(View.GONE);
            secondL.setVisibility(View.VISIBLE);
        }
    }


    private void returnThisFramgent(Fragment fragment) {
        setConsume();
        needSet = true;
        Bundle bundle = new Bundle();
        bundle.putSerializable("typeVO", typeVO);
        bundle.putSerializable("object", consumeVO);
        bundle.putSerializable("action", "InsertSpend");
        bundle.putSerializable("needSet", true);
        fragment.setArguments(bundle);
        MainActivity.oldFramgent.add("InsertSpend");
        MainActivity.bundles.add(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }


    private void setConsume() {
        Map<String, String> g = new HashMap<>();
        g.put("choicestatue", isnull(resultStatue));
        g.put("choicedate", isnull(resultDay));
        g.put("noweek", String.valueOf(noweek));
        String fixdatedetail = gson.toJson(g);
        String[] dates = date.getText().toString().split("/");
        Calendar c = Calendar.getInstance();
        c.set(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 12, 0, 0);
        Date d = new Date(c.getTimeInMillis());
        consumeVO.setMaintype(name.getText().toString().trim());
        consumeVO.setSecondType(secondName.getText().toString().trim());

        Double inputMoney;
        try {
            inputMoney=Common.nf.parse(money.getText().toString().trim()).doubleValue();
            consumeVO.setRealMoney(onlyNumber(Common.doubleRemoveZero(inputMoney)));
        } catch (ParseException e) {
            consumeVO.setRealMoney("0");
        }


        consumeVO.setDate(d);
        consumeVO.setNumber(number.getText().toString().trim());
        consumeVO.setFixDate(String.valueOf(fixDate.isChecked()));
        consumeVO.setFixDateDetail(fixdatedetail);
        consumeVO.setNotify(String.valueOf(notify.isChecked()));
        consumeVO.setFkKey(UUID.randomUUID().toString());
        consumeVO.setAuto(false);
        consumeVO.setAutoId(-1);
        consumeVO.setIsWin("0");
        consumeVO.setIsWinNul("0");
        consumeVO.setCurrency(nowCurrency);
    }


    private class DetailEdit implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Fragment fragment = new UpdateConsumeDetail();
            returnThisFramgent(fragment);
        }
    }

    private class choiceStateItemBS implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int id) {
            resultStatue = BsTextStatue.get(id).toString();
            choiceStatue.setBootstrapText(BsTextStatue.get(id));
            statueNumber = id;
            choiceDay.setExpandDirection(ExpandDirection.UP);
            if (id == 0) {
                resultDay = "";
                choiceDay.setVisibility(View.GONE);
                noWek.setVisibility(View.VISIBLE);
                choiceStatue.setVisibility(View.VISIBLE);
                notifyT.setVisibility(View.VISIBLE);
                noWekT.setVisibility(View.VISIBLE);
                return;
            }
            if (id == 1) {
                resultDay = BsTextWeek.get(0).toString();
                choiceDay.setBootstrapText(BsTextWeek.get(0));
                choiceDay.setDropdownData(Common.WeekSetSpinnerBS);
            }
            if (id == 2) {
                resultDay = BsTextDay.get(0).toString();
                choiceDay.setBootstrapText(BsTextDay.get(0));
                choiceDay.setDropdownData(Common.DaySetSpinnerBS());
            }
            if (id == 3) {
                resultDay = BsTextMonth.get(0).toString();
                choiceDay.setBootstrapText(BsTextMonth.get(0));
                choiceDay.setDropdownData(Common.MonthSetSpinnerBS());
            }

            choiceDay.setVisibility(View.VISIBLE);
            notifyT.setVisibility(View.VISIBLE);
            noWek.setVisibility(View.GONE);
            noWekT.setVisibility(View.GONE);
            noWek.setChecked(false);
        }
    }

    private class choicedayItemBS implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int id) {
            switch (statueNumber) {
                case 1:
                    choiceDay.setBootstrapText(BsTextWeek.get(id));
                    resultDay = BsTextWeek.get(id).toString();
                    break;
                case 2:
                    choiceDay.setBootstrapText(BsTextDay.get(id));
                    resultDay = BsTextDay.get(id).toString();
                    break;
                case 3:
                    choiceDay.setBootstrapText(BsTextMonth.get(id));
                    resultDay = BsTextMonth.get(id).toString();
                    break;
            }
        }
    }

    private class choiceCurrency implements PopupMenu.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case 1:
                    nowCurrency = "TWD";
                    sharedPreferences.edit().putString(insertCurrency, nowCurrency).apply();
                    currency.setText(Common.getCurrency(nowCurrency));
                case 8:
                    popupMenu.dismiss();
                    break;
                default:
                    nowCurrency = Common.code.get(menuItem.getItemId() - 2);
                    sharedPreferences.edit().putString(insertCurrency, nowCurrency).apply();
                    currency.setText(Common.getCurrency(nowCurrency));
                    break;
            }
            return true;
        }
    }

    private class closeAllGridView implements View.OnClickListener, View.OnFocusChangeListener {


        @Override
        public void onClick(View view) {
            number.setError(null);
            firstL.setVisibility(View.GONE);
            secondL.setVisibility(View.GONE);
            showDate.setVisibility(View.GONE);
            numberKeyBoard.setVisibility(View.GONE);
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            if(b)
            {
                numberKeyBoard.setVisibility(View.GONE);
            }
        }
    }

}





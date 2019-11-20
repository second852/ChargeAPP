package com.chargeapp.whc.chargeapp.Control.Update;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.beardedhen.androidbootstrap.api.defaults.ExpandDirection;
import com.chargeapp.whc.chargeapp.Adapter.KeyBoardInputNumberOnItemClickListener;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.Download;
import com.chargeapp.whc.chargeapp.Control.HomePage.HomePagetList;
import com.chargeapp.whc.chargeapp.Control.Insert.InsertConsumeType;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Insert.SearchByQrCode;
import com.chargeapp.whc.chargeapp.Control.Search.SearchMain;
import com.chargeapp.whc.chargeapp.Control.SelectList.SelectListModelActivity;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectDetList;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectShowCircleDe;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectShowCircleDeList;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingListFixCon;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.ui.BarcodeGraphic;
import com.chargeapp.whc.chargeapp.ui.MultiTrackerActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import org.jsoup.internal.StringUtil;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chargeapp.whc.chargeapp.Control.Common.*;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_SAVE;


public class UpdateSpend extends Fragment {

    private BootstrapEditText number, name, money, secondname, date;
    private BootstrapButton save, clear,standard;
    private BootstrapLabel detailname;
    private BootstrapDropDown choiceStatue,choiceday;
    private CheckBox fixdate, notify, noWek;
    private TextView fixDateT;
    private LinearLayout showdate;
    private DatePicker datePicker;
    private String choicedate;
    private Gson gson;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private boolean noweek = false;
    private ConsumeDB consumeDB;
    private AwesomeTextView qrcode;
    private ConsumeVO consumeVO;
    private int updateChoice;
    private String action;
    private LinearLayout firstL, secondL;
    private GridView firstG, secondG;
    private int year,month,day;
    private  Map<String, String> g;
    private TextView noWekT,notifyT;
    private Activity context;
    private String oldMainType;
    private List<TypeVO> typeVOS;
    private TypeVO typeVO;
    private List<BootstrapText> BsTextDay,BsTextWeek,BsTextMonth,BsTextStatue;
    private int statueNumber;
    private String resultStatue,resultDay;
    private RelativeLayout notifyRel;
    private BootstrapButton currency, calculate;
    private PopupMenu popupMenu;
    private GridView numberKeyBoard;
    private String nowCurrency;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.context=(Activity) context;
        }else{
            this.context=getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TypefaceProvider.registerDefaultIconSets();

        View view = inflater.inflate(R.layout.insert_spend, container, false);
        findViewByid(view);
        action = (String) getArguments().getSerializable("action");
        consumeVO = (ConsumeVO) getArguments().getSerializable("consumeVO");
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB);
        if(StringUtil.isBlank(consumeVO.getRealMoney()))
        {
            consumeVO.setRealMoney(String.valueOf(consumeVO.getMoney()));
            consumeDB.update(consumeVO);
        }


        ((AppCompatActivity) context).getSupportActionBar().setDisplayShowCustomEnabled(false);
        BsTextDay= Common.DateChoiceSetBsTest(context,Common.DaySetSpinnerBS());
        BsTextWeek=Common.DateChoiceSetBsTest(context,Common.WeekSetSpinnerBS);
        BsTextMonth=Common.DateChoiceSetBsTest(context,Common.MonthSetSpinnerBS());
        BsTextStatue=Common.DateChoiceSetBsTest(context,Common.DateStatueSetSpinner);
        context.setTitle("修改資料");
        gson = new Gson();
        setSpinner();
        Common.setChargeDB(context);
        typeDB = new TypeDB(MainActivity.chargeAPPDB);
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB);




        date.setText(Common.sTwo.format(new Date(System.currentTimeMillis())));
        date.setOnFocusChangeListener(new dateClickListener());
        date.setOnClickListener(new dateClickListener());
        showdate.setOnClickListener(new choicedateClick());
        fixdate.setOnCheckedChangeListener(new showfixdateClick());
        choiceStatue.setOnDropDownItemClickListener(new choiceStateItemBS());
        choiceday.setOnDropDownItemClickListener(new choicedayItemBS());
        clear.setOnClickListener(new clearAllInput());
        save.setOnClickListener(new savecomsumer());
        noWek.setOnCheckedChangeListener(new nowWekchange());
        qrcode.setOnClickListener(new QrCodeClick());
        number.setOnClickListener(new closeAllGridView());
        number.setOnFocusChangeListener(new closeAllGridView());
        name.setOnClickListener(new showFirstG());
        name.setOnFocusChangeListener(new showFirstG());
        secondname.setOnClickListener(new showSecondG());
        secondname.setOnFocusChangeListener(new showSecondG());
        firstG.setOnItemClickListener(new firstGridOnClick());
        secondG.setOnItemClickListener(new secondGridOnClick());
        detailname.setOnClickListener(new showDetail());
        if (action.equals("SettingListFixCon") && (!consumeVO.isAuto())) {
            BootstrapText text = new BootstrapText.Builder(context)
                    .addText("全部"+"  ")
                    .addFontAwesomeIcon(FA_SAVE)
                    .build();
            standard.setText(text);
            standard.setVisibility(View.VISIBLE);
            standard.setOnClickListener(new saveAllConsume());
        }else{
            standard.setVisibility(View.INVISIBLE);
        }
//        showOnlyQRCodeToast();
        setUpdate();
        setPopupMenu();
        return view;
    }

    private void setPopupMenu() {
        popupMenu = new PopupMenu(context, currency);
        Common.createCurrencyPopMenu(popupMenu, context);
        currency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
        popupMenu.setOnMenuItemClickListener(new choiceUpdateCurrency());
        StringBuilder showSb=new StringBuilder();
        numberKeyBoard.setOnItemClickListener(new KeyBoardInputNumberOnItemClickListener(calculate,money,context,numberKeyBoard,showSb.append(consumeVO.getRealMoney()),false));
        ArrayList items = new ArrayList<Map<String, Object>>();
        Map<String, Object> hashMap;
        for (String s : Common.keyboardArray) {
            hashMap = new HashMap<>();
            hashMap.put("text", s);
            items.add(hashMap);
        }
        SimpleAdapter adapter = new SimpleAdapter(context, items, R.layout.ele_hand_item, new String[]{"text"},
                new int[]{R.id.cardview});
        numberKeyBoard.setAdapter(adapter);
        numberKeyBoard.setNumColumns(5);
        money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberKeyBoard.setVisibility(View.VISIBLE);
                firstL.setVisibility(View.GONE);
                secondL.setVisibility(View.GONE);
                showdate.setVisibility(View.GONE);
            }
        });
        money.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    numberKeyBoard.setVisibility(View.VISIBLE);
                    firstL.setVisibility(View.GONE);
                    secondL.setVisibility(View.GONE);
                    showdate.setVisibility(View.GONE);

                    Common.clossKeyword(context);
                    number.clearFocus();
                    name.clearFocus();
                    secondname.clearFocus();
                    date.clearFocus();
                }
            }
        });
    }

//    private void showOnlyQRCodeToast() {
//        try {
//            returnCM = (boolean) getArguments().getSerializable("returnCM");
//        } catch (Exception e) {
//            returnCM = false;
//        }
//        if (returnCM) {
//            if (BarcodeGraphic.hashMap.get(1) != null) {
//                Common.showToast(context,"明細無法辨識，需要自行輸入!");
//            } else if (BarcodeGraphic.hashMap.get(2) != null) {
//                Common.showToast(context,"部分明細可辨識，其他項目需要自行輸入!");
//            }
//        }
//    }

    private void setSpinner() {
        choiceStatue.setDropdownData(Common.DateStatueSetSpinner);
    }


    private void setUpdate() {
        if(consumeVO.getMaintype().trim().equals("O"))
        {
            name.setText("其他");
            secondname.setText("其他");
        }else if(consumeVO.getSecondType().trim().equals("0"))
        {
            name.setText("未知");
            secondname.setText("未知");
        }else{
            name.setText(consumeVO.getMaintype());
            secondname.setText(consumeVO.getSecondType());
        }


        number.setText(consumeVO.getNumber());
        money.setText(consumeVO.getRealMoney());

        if(consumeVO.getCurrency()==null)
        {
           nowCurrency="TWD";
        }else{
            nowCurrency=consumeVO.getCurrency();
        }

        currency.setText(Common.getCurrency(nowCurrency));

        date.setText(Common.sTwo.format(consumeVO.getDate()));

        //自動產生不能設fix和QRcode 掃描
        if (consumeVO.isAuto()) {
            fixdate.setVisibility(View.GONE);
            fixDateT.setVisibility(View.GONE);
            qrcode.setVisibility(View.GONE);
            return;
        }

        if ("true".equals(consumeVO.getFixDate())) {
            fixdate.setChecked(Boolean.valueOf(consumeVO.getFixDate()));
            notify.setChecked(Boolean.valueOf(consumeVO.getNotify()));
            JsonObject js = gson.fromJson(consumeVO.getFixDateDetail(), JsonObject.class);
            String choicestatue = js.get("choicestatue").getAsString().trim();
            String choicedate = js.get("choicedate").getAsString().trim();
            String noweek = js.get("noweek").getAsString().trim();
            noWek.setChecked(Boolean.valueOf(noweek));
            if (choicestatue.trim().equals("每天")) {
                statueNumber=0;
                noWekT.setVisibility(View.VISIBLE);
                noWek.setVisibility(View.VISIBLE);
                choiceday.setVisibility(View.GONE);
                resultStatue=BsTextStatue.get(0).toString();
                resultDay="";
                choiceStatue.setBootstrapText(BsTextStatue.get(0));
                choiceday.setExpandDirection(ExpandDirection.UP);
            } else if (choicestatue.trim().equals("每周")) {
                statueNumber=1;
                noWekT.setVisibility(View.GONE);
                noWek.setVisibility(View.GONE);
                choiceday.setVisibility(View.VISIBLE);
                resultStatue=BsTextStatue.get(1).toString();
                choiceStatue.setBootstrapText(BsTextStatue.get(1));
                choiceday.setDropdownData(Common.WeekSetSpinnerBS);
                if (choicedate.equals("星期一")) {
                    updateChoice =0;
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
                choiceday.setBootstrapText(BsTextWeek.get(updateChoice));
                resultDay=BsTextWeek.get(updateChoice).toString();
                choiceday.setExpandDirection(ExpandDirection.UP);
            } else if (choicestatue.trim().equals("每月")) {
                statueNumber=2;
                noWekT.setVisibility(View.GONE);
                noWek.setVisibility(View.GONE);
                choiceday.setVisibility(View.VISIBLE);
                resultStatue=BsTextStatue.get(2).toString();
                choiceStatue.setBootstrapText(BsTextStatue.get(2));
                choicedate = choicedate.substring(0, choicedate.indexOf("日"));
                updateChoice = Integer.valueOf(choicedate) - 1;
                resultDay=BsTextDay.get(updateChoice).toString();
                choiceday.setBootstrapText(BsTextDay.get(updateChoice));
                choiceday.setDropdownData(Common.DaySetSpinnerBS());
                choiceday.setExpandDirection(ExpandDirection.UP);
            } else {
                statueNumber=3;
                noWekT.setVisibility(View.GONE);
                noWek.setVisibility(View.GONE);
                choiceday.setVisibility(View.VISIBLE);
                resultStatue=BsTextStatue.get(3).toString();
                choiceStatue.setBootstrapText(BsTextStatue.get(3));
                updateChoice = Integer.valueOf(choicedate.substring(0, choicedate.indexOf("月"))) - 1;
                resultDay=BsTextMonth.get(updateChoice).toString();
                choiceday.setBootstrapText(BsTextMonth.get(updateChoice));
                choiceday.setDropdownData(Common.MonthSetSpinnerBS());
                choiceday.setExpandDirection(ExpandDirection.UP);
            }
        }

    }

    private void goBackFramgent() {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        bundle.putSerializable("consumeVO ", consumeVO);
        if (action.equals("SelectShowCircleDe")) {
            fragment = new SelectShowCircleDe();
            bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
            bundle.putSerializable("ShowAllCarrier", getArguments().getSerializable("ShowAllCarrier"));
            bundle.putSerializable("noShowCarrier", getArguments().getSerializable("noShowCarrier"));
            bundle.putSerializable("year", getArguments().getSerializable("year"));
            bundle.putSerializable("month", getArguments().getSerializable("month"));
            bundle.putSerializable("day", getArguments().getSerializable("day"));
            bundle.putSerializable("index", getArguments().getSerializable("index"));
            bundle.putSerializable("carrier", getArguments().getSerializable("carrier"));
            bundle.putSerializable("statue", getArguments().getSerializable("statue"));
            bundle.putSerializable("period", getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
            bundle.putSerializable("position", getArguments().getSerializable("position"));
        } else if (action.equals("SelectDetList")) {
            fragment = new SelectDetList();
            bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
            bundle.putSerializable("ShowAllCarrier", getArguments().getSerializable("ShowAllCarrier"));
            bundle.putSerializable("noShowCarrier", getArguments().getSerializable("noShowCarrier"));
            bundle.putSerializable("year", getArguments().getSerializable("year"));
            bundle.putSerializable("month", getArguments().getSerializable("month"));
            bundle.putSerializable("day", getArguments().getSerializable("day"));
            bundle.putSerializable("key", getArguments().getSerializable("key"));
            bundle.putSerializable("carrier", getArguments().getSerializable("carrier"));
            bundle.putSerializable("statue", getArguments().getSerializable("statue"));
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            bundle.putSerializable("period", getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
        } else if (action.equals("SelectListModelCom")) {
            fragment = new SelectListModelActivity();
        }else if (action.equals("SettingListFixCon")) {
            fragment = new SettingListFixCon();
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            bundle.putSerializable("consumeVO",consumeVO);
        }else if (action.equals("SelectShowCircleDeList")) {
            fragment = new SelectShowCircleDeList();
            bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
            bundle.putSerializable("ShowAllCarrier", getArguments().getSerializable("ShowAllCarrier"));
            bundle.putSerializable("noShowCarrier", getArguments().getSerializable("noShowCarrier"));
            bundle.putSerializable("year", getArguments().getSerializable("year"));
            bundle.putSerializable("month", getArguments().getSerializable("month"));
            bundle.putSerializable("day", getArguments().getSerializable("day"));
            bundle.putSerializable("key", getArguments().getSerializable("key"));
            bundle.putSerializable("carrier", getArguments().getSerializable("carrier"));
            bundle.putSerializable("statue", getArguments().getSerializable("statue"));
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            bundle.putSerializable("period", getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
            bundle.putStringArrayList("OKey",getArguments().getStringArrayList("OKey"));
        }else if(action.equals("HomePagetList"))
        {
            fragment=new HomePagetList();
            bundle.putSerializable("action","HomePagetList");
            bundle.putStringArrayList("OKey",getArguments().getStringArrayList("OKey"));
            bundle.putSerializable("position",0);
            bundle.putSerializable("key", getArguments().getSerializable("key"));
        }else if(action.equals(searchMainString))
        {
            fragment=new SearchMain();
            bundle.putAll(getArguments());
        }
        fragment.setArguments(bundle);
        switchFramgent(fragment);
    }


    private void returnThisFragment(Fragment fragment) {
        setConsume();
        Bundle bundle = new Bundle();
        bundle.putSerializable("object", consumeVO);
        bundle.putSerializable("consumeVO",consumeVO);
        bundle.putSerializable("action", action);
        bundle.putSerializable("typeVO",typeVO);
        if (action.equals("SelectDetList")) {
            bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
            bundle.putSerializable("ShowAllCarrier", getArguments().getSerializable("ShowAllCarrier"));
            bundle.putSerializable("noShowCarrier", getArguments().getSerializable("noShowCarrier"));
            bundle.putSerializable("year", getArguments().getSerializable("year"));
            bundle.putSerializable("month", getArguments().getSerializable("month"));
            bundle.putSerializable("day", getArguments().getSerializable("day"));
            bundle.putSerializable("key", getArguments().getSerializable("key"));
            bundle.putSerializable("carrier", getArguments().getSerializable("carrier"));
            bundle.putSerializable("statue", getArguments().getSerializable("statue"));
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            bundle.putSerializable("period", getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
        } else if (action.equals("SelectShowCircleDe")) {
            bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
            bundle.putSerializable("ShowAllCarrier", getArguments().getSerializable("ShowAllCarrier"));
            bundle.putSerializable("noShowCarrier", getArguments().getSerializable("noShowCarrier"));
            bundle.putSerializable("year", getArguments().getSerializable("year"));
            bundle.putSerializable("month", getArguments().getSerializable("month"));
            bundle.putSerializable("day", getArguments().getSerializable("day"));
            bundle.putSerializable("index", getArguments().getSerializable("index"));
            bundle.putSerializable("carrier", getArguments().getSerializable("carrier"));
            bundle.putSerializable("statue", getArguments().getSerializable("statue"));
            bundle.putSerializable("period", getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
            bundle.putSerializable("position", getArguments().getSerializable("position"));
        }else if (action.equals("SettingListFixCon")) {
            bundle.putSerializable("position", getArguments().getSerializable("position"));
        }else  if (action.equals("SelectShowCircleDeList")) {
            bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
            bundle.putSerializable("ShowAllCarrier", getArguments().getSerializable("ShowAllCarrier"));
            bundle.putSerializable("noShowCarrier", getArguments().getSerializable("noShowCarrier"));
            bundle.putSerializable("year", getArguments().getSerializable("year"));
            bundle.putSerializable("month", getArguments().getSerializable("month"));
            bundle.putSerializable("day", getArguments().getSerializable("day"));
            bundle.putSerializable("key", getArguments().getSerializable("key"));
            bundle.putSerializable("carrier", getArguments().getSerializable("carrier"));
            bundle.putSerializable("statue", getArguments().getSerializable("statue"));
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            bundle.putSerializable("period", getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
            bundle.putStringArrayList("OKey",getArguments().getStringArrayList("OKey"));
        }else if(action.equals("HomePagetList"))
        {
            bundle.putSerializable("action","HomePagetList");
            bundle.putStringArrayList("OKey",getArguments().getStringArrayList("OKey"));
            bundle.putSerializable("position",0);
            bundle.putSerializable("key", getArguments().getSerializable("key"));
        }else if(action.equals(searchMainString))
        {
            bundle.putAll(getArguments());
        }
        fragment.setArguments(bundle);
        MainActivity.bundles.add(bundle);
        MainActivity.oldFramgent.add("UpdateSpend");
        switchFramgent(fragment);
    }

    public void findViewByid(View view) {
        firstG = view.findViewById(R.id.firstG);
        firstL = view.findViewById(R.id.firstL);
        secondG = view.findViewById(R.id.secondG);
        secondL = view.findViewById(R.id.secondL);
        name = view.findViewById(R.id.name);
        name.setShowSoftInputOnFocus(false);
        secondname = view.findViewById(R.id.secondname);
        secondname.setShowSoftInputOnFocus(false);
        money = view.findViewById(R.id.money);
        money.setShowSoftInputOnFocus(false);
        date = view.findViewById(R.id.date);
        date.setShowSoftInputOnFocus(false);
        fixdate = view.findViewById(R.id.fixdate);
        save = view.findViewById(R.id.save);
        clear = view.findViewById(R.id.clear);
        showdate = view.findViewById(R.id.showdate);
        datePicker = view.findViewById(R.id.datePicker);
        choiceStatue = view.findViewById(R.id.choiceStatue);
        choiceStatue.setVisibility(View.GONE);
        choiceday = view.findViewById(R.id.choiceday);
        choiceday.setVisibility(View.GONE);
        number = view.findViewById(R.id.number);
        detailname = view.findViewById(R.id.detailname);
        notify = view.findViewById(R.id.notify);
        noWek = view.findViewById(R.id.noWek);
        qrcode = view.findViewById(R.id.qrcode);
        standard = view.findViewById(R.id.standard);
        noWekT=view.findViewById(R.id.noWekT);
        notifyT=view.findViewById(R.id.notifyT);
        fixDateT=view.findViewById(R.id.fixDateT);
        notifyRel=view.findViewById(R.id.notifyRel);
        currency=view.findViewById(R.id.currency);
        calculate=view.findViewById(R.id.calculate);
        numberKeyBoard=view.findViewById(R.id.numberKeyBoard);
    }

    private void setFirstGrid() {
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
        SimpleAdapter adapter = new SimpleAdapter(context,
                items, R.layout.main_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        firstG.setAdapter(adapter);
        firstG.setNumColumns(4);
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
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.cancel);
        item.put("text", "取消");
        items.add(item);
        SimpleAdapter adapter = new SimpleAdapter(context,
                items, R.layout.main_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        secondG.setAdapter(adapter);
        secondG.setNumColumns(4);
    }

    private class dateClickListener implements  View.OnFocusChangeListener, View.OnClickListener {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                firstL.setVisibility(View.GONE);
                secondL.setVisibility(View.GONE);
                showdate.setVisibility(View.VISIBLE);
                numberKeyBoard.setVisibility(View.GONE);

                Common.clossKeyword(context);
                name.clearFocus();
                number.clearFocus();
                money.clearFocus();
                secondname.clearFocus();

            }

            date.setSelection(date.getText().toString().length());
        }

        @Override
        public void onClick(View view) {
            firstL.setVisibility(View.GONE);
            secondL.setVisibility(View.GONE);
            showdate.setVisibility(View.VISIBLE);
            numberKeyBoard.setVisibility(View.GONE);
            date.setSelection(date.getText().toString().length());
        }
    }


    private class choicedateClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            date.setError(null);
            choicedate = datePicker.getYear() + "/" + String.valueOf(datePicker.getMonth() + 1) + "/" + datePicker.getDayOfMonth();
            date.setText(choicedate);
            showdate.setVisibility(View.GONE);
            date.setSelection(choicedate.length());
        }
    }


    private class showfixdateClick implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                notifyRel.setVisibility(View.VISIBLE);
                choiceStatue.setBootstrapText(BsTextStatue.get(0));
                resultStatue=BsTextStatue.get(0).toString();
                notifyT.setVisibility(View.VISIBLE);
                noWekT.setVisibility(View.VISIBLE);
                notify.setVisibility(View.VISIBLE);
                noWek.setVisibility(View.VISIBLE);
                choiceStatue.setVisibility(View.VISIBLE);
            } else {
                resultStatue="";
                resultDay="";
                notifyT.setVisibility(View.GONE);
                noWekT.setVisibility(View.GONE);
                notify.setVisibility(View.GONE);
                choiceStatue.setVisibility(View.GONE);
                choiceday.setVisibility(View.GONE);
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
//                spinneritem.add("星期一");
//                spinneritem.add("星期二");
//                spinneritem.add("星期三");
//                spinneritem.add("星期四");
//                spinneritem.add("星期五");
//                spinneritem.add("星期六");
//                spinneritem.add("星期日");
//            }
//            if (position == 2) {
//                for (int i = 1; i <= 31; i++) {
//                    spinneritem.add(" " + String.valueOf(i) + "日");
//                }
//            }
//            if (position == 3) {
//                for (int i = 1; i <= 12; i++) {
//                    spinneritem.add(" " + String.valueOf(i) + "月");
//                }
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
            if(firstL.getVisibility()==View.VISIBLE)
            {
                return;
            }
            if(secondL.getVisibility()==View.VISIBLE)
            {
                return;
            }

            //date show not save
            if(showdate.getVisibility()==View.VISIBLE)
            {
                return;
            }
            name.setText("");
            secondname.setText("");
            money.setText("");
            numberKeyBoard.setOnItemClickListener(new KeyBoardInputNumberOnItemClickListener(calculate, money, context, numberKeyBoard, new StringBuilder(), true));
            fixdate.setChecked(false);
            number.setText("");
        }
    }

    private String isnull(String text) {
        if (text == null || text.toString().length() <= 0) {
            return " ";
        }
        text=text.substring(0,text.lastIndexOf(" "));
        return text.toString();
    }


    private class savecomsumer implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            //設定種類時 不能儲存
            if(firstL.getVisibility()==View.VISIBLE)
            {
                return;
            }
            if(secondL.getVisibility()==View.VISIBLE)
            {
                return;
            }

            //date show not save
            if(showdate.getVisibility()==View.VISIBLE)
            {
                return;
            }

            if (name.getText()== null || name.getText().toString().trim().length() == 0) {
                name.setError("主項目不能空白");
                return;
            }

            //無法分類自己設分類
            if (name.getText().toString().trim().equals("O") || name.getText().toString().trim().equals("0")) {
                name.setError("主項目不能為其他");
                return;
            }

            if (secondname.getText() == null || secondname.getText().toString().trim().length() == 0) {
                secondname.setError("次項目不能空白");
                return;
            }
           try {
               if(!oldMainType.equals(name.getText().toString().trim()))
               {
                   secondname.setBackgroundColor(Color.parseColor("#ff471a"));
                   Common.showToast(context, "次項目不屬於主項目種類");
                   return;
               }
           }catch (Exception e)
           {

           }
            //無法分類自己設分類
            if (secondname.getText().toString().trim().equals("O") || secondname.getText().toString().equals("0")) {
                secondname.setError("次項目不能為其他");
                return;
            }

            if (money.getText()== null || money.getText().toString().trim().length() == 0) {
                money.setError("金額不能空白");
                return;
            }

            try {
                if (Common.nf.parse(money.getText().toString().trim()).doubleValue() == 0) {
                    money.setError("金額不能空白");
                    return;
                }
            } catch (Exception e) {
                money.setError("只能輸入數字");
                return;
            }


            if (date.getText()== null || date.getText().toString().trim().length() == 0) {
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
            MainActivity.oldFramgent.remove(MainActivity.oldFramgent.size()-1);
            MainActivity.bundles.remove(MainActivity.bundles.size()-1);
            setConsume();
            consumeDB.update(consumeVO);
            goBackFramgent();
            Common.showToast(context, "修改成功");

        }
    }

    private void setConsume() {
        g = new HashMap<>();
        g.put("choicestatue", isnull(resultStatue));
        g.put("choicedate", isnull(resultDay));
        g.put("noweek", String.valueOf(noweek));
        String fixdatedetail = gson.toJson(g);
        String[] dates = date.getText().toString().split("/");
        Calendar c = Calendar.getInstance();
        year=Integer.valueOf(dates[0]);
        month=(Integer.valueOf(dates[1]) - 1);
        day=Integer.valueOf(dates[2]);
        c.set(year,month,day,12, 0, 0);
        Date d = new Date(c.getTimeInMillis());
        consumeVO.setMaintype(name.getText().toString().trim());
        consumeVO.setSecondType(secondname.getText().toString().trim());
        consumeVO.setDate(d);
        Double inputMoney;
        try {
            inputMoney=Common.nf.parse(money.getText().toString().trim()).doubleValue();
            consumeVO.setRealMoney(onlyNumber(Common.doubleRemoveZero(inputMoney)));
        } catch (ParseException e) {
            consumeVO.setRealMoney("0");
        }

        consumeVO.setCurrency(nowCurrency);
        consumeVO.setNumber(number.getText().toString().trim());

        if(!consumeVO.isAuto())
        {
            consumeVO.setFixDate(String.valueOf(fixdate.isChecked()));
            consumeVO.setFixDateDetail(fixdatedetail);
            consumeVO.setNotify(String.valueOf(notify.isChecked()));
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
            final PopupMenu popupMenu=new PopupMenu(context,v);
            popupMenu.inflate(R.menu.menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.camera:
                            setConsume();
                            Intent intent = new Intent(UpdateSpend.this.context, MultiTrackerActivity.class);
                            intent.putExtra("action","UpdateSpend");
                            intent.putExtra("bundle",UpdateSpend.this.getArguments());
                            startActivityForResult(intent, 6);
                            break;
                        case R.id.searchInternet:
                            Fragment fragment=new SearchByQrCode();
                            returnThisFragment(fragment);
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




    public void switchFramgent(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }


    private class showFirstG implements View.OnClickListener, View.OnFocusChangeListener {
        @Override
        public void onClick(View view) {
            secondL.setVisibility(View.GONE);
            showdate.setVisibility(View.GONE);
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
                secondname.clearFocus();
                date.clearFocus();
                secondL.setVisibility(View.GONE);
                showdate.setVisibility(View.GONE);
                numberKeyBoard.setVisibility(View.GONE);
                firstL.setVisibility(View.VISIBLE);
            }
        }
    }

    private class firstGridOnClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            name.setError(null);
            TextView textView = view.findViewById(R.id.text);
            String type = textView.getText().toString().trim();
            if(i<typeVOS.size())
            {
                typeVO=typeVOS.get(i);
            }
            if (type.equals("新增")) {
                Common.showfirstgrid = true;
                returnThisFragment(new InsertConsumeType());
                return;
            }
            if (type.equals("取消")) {
                firstL.setVisibility(View.GONE);
                Common.showfirstgrid=false;
                return;
            }
            name.setText(type);
            name.setSelection(type.length());
            setSecondGrid();
            firstL.setVisibility(View.GONE);
            secondL.setVisibility(View.VISIBLE);
        }
    }

    private class secondGridOnClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            secondname.setError(null);
            TextView textView = view.findViewById(R.id.text);
            String type = textView.getText().toString().trim();
            if (type.equals("返回")) {
                firstL.setVisibility(View.VISIBLE);
                secondL.setVisibility(View.GONE);
                return;
            }
            if (type.equals("新增")) {
                Common.showsecondgrid = true;
                returnThisFragment(new InsertConsumeType());
                return;
            }
            if (type.equals("取消")) {
                Common.showsecondgrid = false;
                secondL.setVisibility(View.GONE);
                return;
            }
            oldMainType=name.getText().toString().trim();
            secondname.setText(type);
            secondname.setSelection(type.length());
            secondL.setVisibility(View.GONE);
        }
    }


    private class showSecondG implements View.OnClickListener, View.OnFocusChangeListener {
        @Override
        public void onClick(View view) {
            firstL.setVisibility(View.GONE);
            showdate.setVisibility(View.GONE);
            numberKeyBoard.setVisibility(View.GONE);
            secondL.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            if(b)
            {
                Common.clossKeyword(context);
                number.clearFocus();
                money.clearFocus();
                name.clearFocus();
                date.clearFocus();
                firstL.setVisibility(View.GONE);
                showdate.setVisibility(View.GONE);
                numberKeyBoard.setVisibility(View.GONE);
                secondL.setVisibility(View.VISIBLE);
            }

        }
    }


    private class saveAllConsume implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //設定種類時 不能儲存
            if(firstL.getVisibility()==View.VISIBLE)
            {
                return;
            }
            if(secondL.getVisibility()==View.VISIBLE)
            {
                return;
            }

            //date show not save
            if(showdate.getVisibility()==View.VISIBLE)
            {
                return;
            }
            if (name.getText()== null || name.getText().toString().trim().length() == 0) {
                name.setError("主項目不能空白");
                return;
            }

            if (name.getText().toString().trim().equals("O") || name.getText().toString().trim().equals("0")) {
                name.setError("主項目不能為其他");
                return;
            }


            if (secondname.getText() == null || secondname.getText().toString().trim().length() == 0) {
                secondname.setError("次項目不能空白");
                return;
            }

            if (secondname.getText().toString().trim().equals("O") || secondname.getText().toString().trim().equals("0")) {
                secondname.setError("次項目不能為其他");
                return;
            }
           try {
               if(!oldMainType.equals(name.getText().toString().trim()))
               {
                   secondname.setError("次項目不屬於主項目種類");
                   return;
               }
           }catch (Exception e)
           {

           }
            if (money.getText()== null || money.getText().toString().trim().length() == 0) {
                money.setError("金額不能空白");
                return;
            }

            try {
                if (Common.nf.parse(money.getText().toString().trim()).doubleValue() == 0) {
                    money.setError("金額不能為0");
                    return;
                }
            } catch (Exception e) {
                money.setError("只能輸入數字");
                return;
            }

            if (date.getText() == null || date.getText().toString().trim().length() == 0) {
                name.setError("日期不能空白");
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
            consumeDB.update(consumeVO);
            List<ConsumeVO> consumeVOS=consumeDB.getAutoCreate(consumeVO.getId());
            String statue=resultStatue;
            String dateSpinner= g.get("choicedate").trim();
            Calendar cd = new GregorianCalendar(year,month,day,12,0,0);
            int dweek=cd.get(Calendar.WEEK_OF_MONTH);
            int i=0;
            for(ConsumeVO c:consumeVOS)
            {
                c.setMaintype(consumeVO.getMaintype());
                c.setSecondType(consumeVO.getSecondType());
                c.setDetailname((consumeVO.getDetailname()==null?"":consumeVO.getDetailname()));
                c.setMoney(consumeVO.getMoney());
                c.setNumber(consumeVO.getNumber());
                c.setFixDateDetail(consumeVO.getFixDateDetail());
                if(statue.equals("每天"))
                {
                    i++;
                    Calendar calendar = new GregorianCalendar(year,month,day+i,12,0,0);
                    c.setDate(new Date(calendar.getTimeInMillis()));

                }else if(statue.equals("每周"))
                {
                    i++;
                    if (dateSpinner.equals("星期一")) {
                        cd.set(Calendar.WEEK_OF_MONTH,dweek+i);
                        cd.set(Calendar.DAY_OF_WEEK,2);
                    } else if (dateSpinner.equals("星期二")) {
                        cd.set(Calendar.WEEK_OF_MONTH,dweek+i);
                        cd.set(Calendar.DAY_OF_WEEK,3);
                    } else if (dateSpinner.equals("星期三")) {
                        cd.set(Calendar.WEEK_OF_MONTH,dweek+i);
                        cd.set(Calendar.DAY_OF_WEEK,4);
                    } else if (dateSpinner.equals("星期四")) {
                        cd.set(Calendar.WEEK_OF_MONTH,dweek+i);
                        cd.set(Calendar.DAY_OF_WEEK,5);
                    } else if (dateSpinner.equals("星期五")) {
                        cd.set(Calendar.WEEK_OF_MONTH,dweek+i);
                        cd.set(Calendar.DAY_OF_WEEK,6);
                    } else if (dateSpinner.equals("星期六")) {
                        cd.set(Calendar.WEEK_OF_MONTH,dweek+i);
                        cd.set(Calendar.DAY_OF_WEEK,7);
                    } else {
                        cd.set(Calendar.WEEK_OF_MONTH,dweek+i);
                        cd.set(Calendar.DAY_OF_WEEK,1);
                    }
                    c.setDate(new Date(cd.getTimeInMillis()));
                }else if(statue.equals("每月"))
                {
                    i++;
                    String string=dateSpinner.substring(0,dateSpinner.indexOf("日"));
                    int  choiceD=Integer.valueOf(string.trim());
                    Calendar calendar = new GregorianCalendar(year,month+i,1,12,0,0);
                    int monMax=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if(choiceD>monMax)
                    {
                        calendar = new GregorianCalendar(year,month+i,monMax,12,0,0);
                    }else{
                        calendar = new GregorianCalendar(year,month+i,choiceD,12,0,0);
                    }
                    c.setDate(new Date(calendar.getTimeInMillis()));

                }else if(statue.equals("每年"))
                {
                    i++;
                    int choiceD=Integer.valueOf(dateSpinner.substring(0, dateSpinner.indexOf("月"))) - 1;
                    Calendar calendar = new GregorianCalendar(year+i,choiceD,1,12,0,0);
                    c.setDate(new Date(calendar.getTimeInMillis()));
                }
                consumeDB.update(c);
            }
            MainActivity.oldFramgent.remove(MainActivity.oldFramgent.size()-1);
            MainActivity.bundles.remove(MainActivity.bundles.size()-1);
            goBackFramgent();
            Common.showToast(context, "修改成功");
        }
    }

    private class showDetail implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            returnThisFragment(new UpdateConsumeDetail());
        }
    }
    private class choiceStateItemBS implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int id) {
            resultStatue=BsTextStatue.get(id).toString();
            choiceStatue.setBootstrapText(BsTextStatue.get(id));
            statueNumber=id;
            choiceday.setExpandDirection(ExpandDirection.UP);
            if (id == 0) {
                resultDay="";
                choiceday.setVisibility(View.GONE);
                noWek.setVisibility(View.VISIBLE);
                choiceStatue.setVisibility(View.VISIBLE);
                notifyT.setVisibility(View.VISIBLE);
                noWekT.setVisibility(View.VISIBLE);
                return;
            }
            if (id == 1) {
                resultDay=BsTextWeek.get(0).toString();
                choiceday.setBootstrapText(BsTextWeek.get(0));
                choiceday.setDropdownData(Common.WeekSetSpinnerBS);
            }
            if (id == 2) {
                resultDay=BsTextDay.get(0).toString();
                choiceday.setBootstrapText(BsTextDay.get(0));
                choiceday.setDropdownData(Common.DaySetSpinnerBS());
            }
            if (id == 3) {
                resultDay=BsTextMonth.get(0).toString();
                choiceday.setBootstrapText(BsTextMonth.get(0));
                choiceday.setDropdownData(Common.MonthSetSpinnerBS());
            }

            choiceday.setVisibility(View.VISIBLE);
            notifyT.setVisibility(View.VISIBLE);
            noWek.setVisibility(View.GONE);
            noWekT.setVisibility(View.GONE);
            noWek.setChecked(false);
        }
    }

    private class choicedayItemBS implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int id) {
            switch (statueNumber)
            {
                case 1:
                    choiceday.setBootstrapText(BsTextWeek.get(id));
                    resultDay=BsTextWeek.get(id).toString();
                    break;
                case 2:
                    choiceday.setBootstrapText(BsTextDay.get(id));
                    resultDay=BsTextDay.get(id).toString();
                    break;
                case 3:
                    choiceday.setBootstrapText(BsTextMonth.get(id));
                    resultDay=BsTextMonth.get(id).toString();
                    break;
            }
        }
    }

    private class choiceUpdateCurrency implements PopupMenu.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case 1:
                    nowCurrency="TWD";
                    currency.setText(Common.getCurrency(nowCurrency));
                case 8:
                    popupMenu.dismiss();
                    break;
                default:
                    nowCurrency=Common.code.get(menuItem.getItemId() - 2);
                    currency.setText(Common.getCurrency(nowCurrency));
                    break;
            }
            return true;
        }
    }

    private class closeAllGridView implements View.OnClickListener, View.OnFocusChangeListener {
        @Override
        public void onClick(View view) {
            firstL.setVisibility(View.GONE);
            secondL.setVisibility(View.GONE);
            showdate.setVisibility(View.GONE);
            numberKeyBoard.setVisibility(View.GONE);
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            numberKeyBoard.setVisibility(View.GONE);
        }
    }
}






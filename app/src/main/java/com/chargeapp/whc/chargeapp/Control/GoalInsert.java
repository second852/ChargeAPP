package com.chargeapp.whc.chargeapp.Control;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.chargeapp.whc.chargeapp.Adapter.KeyBoardInputNumberOnItemClickListener;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static com.chargeapp.whc.chargeapp.Control.Common.insertCurrency;


/**
 * Created by Wang on 2018/2/25.
 */

public class GoalInsert extends Fragment {
    private EditText name,money,limitP;
    private Spinner spinnerT,choiceStatue,remindS,remindD;
    private CheckBox remind,noWeekend;
    private LinearLayout showDate;
    private TextView shift,noWeekendT,remindT;
    private DatePicker datePicker;
    private RelativeLayout remindL;
    private GoalDB goalDB;
    private String action;
    private ArrayList<String> listDayStatue;
    private Activity context;
    private BootstrapButton save,clear;
    private BootstrapButton currency, calculate;
    private PopupMenu popupMenu;
    private GridView numberKeyBoard;
    private String nowCurrency;
    private SharedPreferences sharedPreferences;




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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goal_setgoal, container, false);
        Common.setChargeDB(context);
        goalDB=new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        action= (String) getArguments().getSerializable("action");
        context.setTitle("新增目標");
        findViewById(view);
        setSpinner();
        limitP.setOnClickListener(new showDate());
        limitP.setOnFocusChangeListener(new showDate());
        limitP.setShowSoftInputOnFocus(false);
        limitP.setInputType(InputType.TYPE_NULL);

        showDate.setOnClickListener(new choicedateClick());
        remind.setOnCheckedChangeListener(new dateStatue());
        remindS.setOnItemSelectedListener(new choiceDateStatue());
        clear.setOnClickListener(new clearOnClick());
        save.setOnClickListener(new saveOnClick());
        spinnerT.setOnItemSelectedListener(new SelectType());
        choiceStatue.setOnItemSelectedListener(new choiceStatueSelected());
        name.setOnClickListener(new closeAllView());
        name.requestFocus();
        setPopupMenu();
        return view;
    }

    private void setPopupMenu() {

        sharedPreferences = context.getSharedPreferences("Charge_User", Context.MODE_PRIVATE);
        nowCurrency = sharedPreferences.getString(insertCurrency, "TWD");
        currency.setText(Common.getCurrency(nowCurrency));


        popupMenu = new PopupMenu(context, currency);
        Common.createCurrencyPopMenu(popupMenu, context);
        currency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
        popupMenu.setOnMenuItemClickListener(new choiceUpdateCurrency());
        numberKeyBoard.setOnItemClickListener(new KeyBoardInputNumberOnItemClickListener(calculate,money,context,numberKeyBoard,new StringBuilder(),false));
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
        money.setShowSoftInputOnFocus(false);
        money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberKeyBoard.setVisibility(View.VISIBLE);
                showDate.setVisibility(View.GONE);
            }
        });
        money.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    Common.clossKeyword(context);
                    numberKeyBoard.setVisibility(View.VISIBLE);
                    showDate.setVisibility(View.GONE);
                    name.clearFocus();
                    limitP.clearFocus();
                }
            }
        });
    }

    private class choiceUpdateCurrency implements PopupMenu.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case 1:
                    nowCurrency="TWD";
                    sharedPreferences.edit().putString(insertCurrency, nowCurrency).apply();
                    currency.setText(Common.getCurrency(nowCurrency));
                case 8:
                    popupMenu.dismiss();
                    break;
                default:
                    nowCurrency=Common.code.get(menuItem.getItemId() - 2);
                    sharedPreferences.edit().putString(insertCurrency, nowCurrency).apply();
                    currency.setText(Common.getCurrency(nowCurrency));
                    break;
            }
            return true;
        }
    }

    private void setSpinner() {
        listDayStatue=new ArrayList<>();
        listDayStatue.add(" 每天 ");
        listDayStatue.add(" 每周 ");
        listDayStatue.add(" 每月 ");
        listDayStatue.add(" 每年 ");
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(context,R.layout.spinneritem,listDayStatue);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        remindS.setAdapter(arrayAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        ArrayList<String> spinneritem=new ArrayList<>();
        if(action.equals("all"))
        {
            spinneritem.add(" 支出 ");
            spinneritem.add(" 儲蓄 ");
        }else if(action.equals("Consume"))
        {
            spinneritem.add(" 支出 ");
        }else{
            spinneritem.add(" 儲蓄 ");
        }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(context,R.layout.spinneritem,spinneritem);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        spinnerT.setAdapter(arrayAdapter);
    }

    private void findViewById(View view) {
        spinnerT=view.findViewById(R.id.spinnerT);
        name=view.findViewById(R.id.name);
        money=view.findViewById(R.id.money);
        limitP=view.findViewById(R.id.limitP);
        remind=view.findViewById(R.id.remind);
        remindS=view.findViewById(R.id.remindS);
        remindD=view.findViewById(R.id.remindD);
        showDate=view.findViewById(R.id.showDate);
        datePicker=view.findViewById(R.id.datePicker);
        noWeekend=view.findViewById(R.id.noWeekend);
        clear=view.findViewById(R.id.clear);
        save=view.findViewById(R.id.save);
        choiceStatue=view.findViewById(R.id.choiceStatue);
        shift=view.findViewById(R.id.shift);
        noWeekendT=view.findViewById(R.id.noWeekendT);
        remindT=view.findViewById(R.id.remindT);
        remindL=view.findViewById(R.id.remindL);
        currency=view.findViewById(R.id.currency);
        calculate=view.findViewById(R.id.calculate);
        numberKeyBoard=view.findViewById(R.id.numberKeyBoard);

    }

    private class showDate implements View.OnClickListener, View.OnFocusChangeListener {
        @Override
        public void onClick(View view) {
            showDate.setVisibility(View.VISIBLE);
            numberKeyBoard.setVisibility(View.GONE);
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            if(b)
            {
                showDate.setVisibility(View.VISIBLE);
                numberKeyBoard.setVisibility(View.GONE);
                Common.clossKeyword(context);
                money.clearFocus();
                name.clearFocus();
            }

        }
    }

    private class choicedateClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            limitP.setError(null);
            String choicedate=datePicker.getYear()+"/"+String.valueOf(datePicker.getMonth()+1)+"/"+datePicker.getDayOfMonth();
            limitP.setText(choicedate);
            showDate.setVisibility(View.GONE);
            limitP.setSelection(choicedate.length());
        }
    }




    private class dateStatue implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b)
            {

                remind.setX(remindL.getWidth()/10-remindL.getWidth()/20);
                remindT.setX(remindL.getWidth()/10+remind.getWidth()-remindL.getWidth()/20);
                remindS.setX(name.getX());
                noWeekend.setX((remindL.getWidth()*2/3)+remindL.getWidth()/20-remindL.getWidth()/10);
                noWeekendT.setX((remindL.getWidth()*2/3)+remindL.getWidth()/20+noWeekend.getWidth()-remindL.getWidth()/10);

                remindS.setVisibility(View.VISIBLE);
                noWeekend.setVisibility(View.VISIBLE);
                noWeekendT.setVisibility(View.VISIBLE);
                remindD.setVisibility(View.GONE);
            }else {
                remind.setX(remindL.getWidth()/3);
                remindT.setX(remindL.getWidth()/3+remind.getWidth());

                remindD.setVisibility(View.GONE);
                remindS.setVisibility(View.GONE);
                noWeekend.setVisibility(View.GONE);
                noWeekendT.setVisibility(View.GONE);
            }
        }
    }


    private class choiceDateStatue implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            ArrayList<String> spinneritem=new ArrayList<>();

            if(position==0)
            {

                remind.setX(remindL.getWidth()/10-remindL.getWidth()/20);
                remindT.setX(remindL.getWidth()/10+remind.getWidth()-remindL.getWidth()/20);
                remindS.setX(spinnerT.getX());
                noWeekend.setX((remindL.getWidth()*2/3)+remindL.getWidth()/20-remindL.getWidth()/20);
                noWeekendT.setX((remindL.getWidth()*2/3)+remindL.getWidth()/20+noWeekend.getWidth()-remindL.getWidth()/20);
                remindD.setX((remindL.getWidth()*2/3)+remindL.getWidth()/20-remindL.getWidth()/20);

                noWeekend.setVisibility(View.VISIBLE);
                noWeekendT.setVisibility(View.VISIBLE);
                remindD.setVisibility(View.GONE);
                return;
            }else if(position==1)
            {
                spinneritem.add("星期一");
                spinneritem.add("星期二");
                spinneritem.add("星期三");
                spinneritem.add("星期四");
                spinneritem.add("星期五");
                spinneritem.add("星期六");
                spinneritem.add("星期日");
            }else if(position==2)
            {
                for(int i=1;i<=31;i++) {
                    spinneritem.add(" "+String.valueOf(i)+"日");
                }

            }else{
                for(int i=1;i<=12;i++) {
                    spinneritem.add(" "+String.valueOf(i)+"月");
                }
            }

            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(context,R.layout.spinneritem,spinneritem);
            arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
            remindD.setAdapter(arrayAdapter);
            remindD.setVisibility(View.VISIBLE);
            noWeekend.setChecked(false);
            remind.setX(remindL.getWidth()/10-remindL.getWidth()/20);
            remindT.setX(remindL.getWidth()/10+remind.getWidth()-remindL.getWidth()/20);
            remindS.setX(spinnerT.getX());
            noWeekend.setX((remindL.getWidth()*2/3)+remindL.getWidth()/20-remindL.getWidth()/20);
            noWeekendT.setX((remindL.getWidth()*2/3)+remindL.getWidth()/20+noWeekend.getWidth()-remindL.getWidth()/20);
            remindD.setX((remindL.getWidth()*2/3)+remindL.getWidth()/20-remindL.getWidth()/20);


            noWeekend.setVisibility(View.GONE);
            noWeekendT.setVisibility(View.GONE);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class clearOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(showDate.getVisibility()==View.VISIBLE)
            {
                return;
            }
            name.setText("");
            money.setText("");
            remind.setChecked(false);
            numberKeyBoard.setOnItemClickListener(new KeyBoardInputNumberOnItemClickListener(calculate,money,context,numberKeyBoard,new StringBuilder(),true));
        }
    }

    private class saveOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            GoalVO goalVO=new GoalVO();
            String goalName = name.getText().toString();
            String goalMoney = money.getText().toString();
            String dayStatue=choiceStatue.getSelectedItem().toString();
            String day=limitP.getText().toString();

            //showDate not save
            if(showDate.getVisibility()==View.VISIBLE)
            {
              return;
            }

            if (goalName == null || goalName.length() <= 0)
            {
                name.setError("不能空白");
                return;
            }

            if(goalMoney==null||goalMoney.length()<=0)
            {
                Common.showToast(context,"金額不能空白");
                money.setError(" ");
                return;
            }

            try {
                if (Double.valueOf(money.getText().toString().trim()) == 0) {
                    money.setError("金額不能為0");
                    return;
                }
            } catch (Exception e) {
                money.setError("只能輸入數字");
                return;
            }


            if(dayStatue.equals("今日"))
            {
                if(day==null||day.length()<=0)
                {
                    limitP.setError("不能空白");
                    Common.showToast(context,"不能空白");
                    return;
                }
                String[] dates = day.split("/");
                Calendar c = new GregorianCalendar(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 0, 0, 0);
                Date d = new Date(c.getTimeInMillis());
                if(c.getTimeInMillis()<System.currentTimeMillis())
                {
                    limitP.setError("不能過去時間");
                    return;
                }

                goalVO.setEndTime(d);
            }else{
                goalVO.setEndTime(new Date(0));
            }
            //hour second set zero
            Calendar calendar=Calendar.getInstance();
            calendar.set(Calendar.HOUR,0);
            calendar.set(Calendar.SECOND,0);
            goalVO.setStartTime(new Date(calendar.getTimeInMillis()));
            String reMa=(remindD.getSelectedItem()==null)?"":remindD.getSelectedItem().toString().trim();
            goalVO.setName(goalName);

            goalVO.setRealMoney(Common.onlyNumber(goalMoney));
            goalVO.setCurrency(nowCurrency);

            goalVO.setNoWeekend(noWeekend.isChecked());
            goalVO.setNotify(remind.isChecked());
            goalVO.setNotifyDate(reMa);
            goalVO.setNotifyStatue(remindS.getSelectedItem().toString().trim());
            goalVO.setType(spinnerT.getSelectedItem().toString().trim());
            goalVO.setTimeStatue(dayStatue);
            goalDB.insert(goalVO);



            Fragment fragment = new GoalListAll();
            Bundle bundle=new Bundle();
            bundle.putSerializable("position",0);
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            for (Fragment fragment1 :  getFragmentManager().getFragments()) {
                fragmentTransaction.remove(fragment1);
            }
            fragmentTransaction.replace(R.id.body, fragment);
            fragmentTransaction.commit();
            MainActivity.oldFramgent.remove(MainActivity.oldFramgent.size()-1);
            MainActivity.bundles.remove(MainActivity.bundles.size()-1);
            Common.showToast(context,"新增成功!");
            Common.clossKeyword(context);
        }
    }

    private class SelectType implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            ArrayList<String> spinneritem=new ArrayList<>();
            TextView textView= (TextView) view;
            String title=textView.getText().toString().trim();
            if(title.equals("支出"))
            {
                spinneritem=listDayStatue;
            }else{
                spinneritem.add(" 今日 ");
                spinneritem.add(" 每月 ");
                spinneritem.add(" 每年 ");
            }
            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(context,R.layout.spinneritem,spinneritem);
            arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
            choiceStatue.setAdapter(arrayAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class choiceStatueSelected implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            TextView textView= (TextView) view;
            if(textView==null)
            {
                return;
            }
            String s=textView.getText().toString().trim();
            if(s.equals("今日"))
            {
                shift.setVisibility(View.VISIBLE);
                limitP.setVisibility(View.VISIBLE);
            }else{
                shift.setVisibility(View.GONE);
                limitP.setVisibility(View.GONE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class closeAllView implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            numberKeyBoard.setVisibility(View.GONE);
            showDate.setVisibility(View.GONE);
        }
    }
}

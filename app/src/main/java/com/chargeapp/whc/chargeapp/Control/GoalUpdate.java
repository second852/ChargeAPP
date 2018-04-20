package com.chargeapp.whc.chargeapp.Control;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.R;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Wang on 2018/2/25.
 */

public class GoalUpdate extends Fragment {
    private EditText name, money;
    private Spinner choiceStatue, remindS, remindD;
    private CheckBox remind, noWeekend;
    private LinearLayout showDate;
    private TextView limitP, save, clear, shift, noWeekendT, remindT, spinnerT;
    private DatePicker datePicker;
    private GoalDB goalDB;
    private GoalVO goalVO;
    private RelativeLayout remindL;
    private Boolean first = true;
    private int updateChoice;
    private String startTitle;
    private ArrayList<String> listDayStatue;
    private int poistion;
    private Activity context;

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
        View view = inflater.inflate(R.layout.goal_update_goal, container, false);
        Common.setChargeDB(context);
        goalDB = new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        goalVO = (GoalVO) getArguments().getSerializable("goalVO");
        findViewById(view);
        setGoalVO();
        setRemindS();
        limitP.setOnClickListener(new showDate());
        showDate.setOnClickListener(new choicedateClick());
        remind.setOnCheckedChangeListener(new dateStatue());
        remindS.setOnItemSelectedListener(new choiceDateStatue());
        clear.setOnClickListener(new clearOnClick());
        save.setOnClickListener(new saveOnClick());
        choiceStatue.setOnItemSelectedListener(new choiceStatueSelected());
        return view;
    }

    private void setRemindS() {
        listDayStatue = new ArrayList<>();
        listDayStatue.add(" 每天 ");
        listDayStatue.add(" 每周 ");
        listDayStatue.add(" 每月 ");
        listDayStatue.add(" 每年 ");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinneritem, listDayStatue);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        remindS.setAdapter(arrayAdapter);
    }


    private void setGoalVO() {
        name.setText(goalVO.getName());
        money.setText(String.valueOf(goalVO.getMoney()));
        spinnerT.setText(goalVO.getType());
        limitP.setText(Common.sTwo.format(goalVO.getEndTime()));
        remind.setChecked(goalVO.isNotify());
        noWeekend.setChecked(goalVO.isNoWeekend());

        setSpinnerT();
        String[] strings = getResources().getStringArray(R.array.fixDay);
        List<String> b = Arrays.asList(strings);
        remindS.setSelection(b.indexOf(goalVO.getNotifyStatue().trim()));
        String choicestatue = goalVO.getNotifyStatue().trim();
        String choicedate = goalVO.getNotifyDate().trim();
        if (goalVO.isNotify()) {
            if (choicestatue.trim().equals("每天")) {
                remindS.setSelection(0);
            } else if (choicestatue.trim().equals("每周")) {
                remindS.setSelection(1);
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
                remindS.setSelection(2);
                updateChoice = Integer.valueOf(choicedate) - 1;
            } else {
                remindS.setSelection(3);
                updateChoice = Integer.valueOf(choicedate.substring(0, choicedate.indexOf("月"))) - 1;
            }
        }
    }

    private void setSpinnerT() {
        ArrayList<String> spinneritem = new ArrayList<>();
        if (goalVO.getType().trim().equals("支出")) {
            spinneritem = listDayStatue;
        } else {
            Date date=new Date(0);
            if (goalVO.getEndTime().getTime()==date.getTime()) {
                spinneritem.add(" 今日 ");
                startTitle = "今日";
                limitP.setText("");
            } else {
                startTitle = Common.sTwo.format(goalVO.getStartTime());
                spinneritem.add(startTitle);
            }
            spinneritem.add(" 每月 ");
            spinneritem.add(" 每年 ");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinneritem, spinneritem);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        choiceStatue.setAdapter(arrayAdapter);
        int choice = spinneritem.indexOf(" "+goalVO.getTimeStatue()+" ");
        if (goalVO.getTimeStatue().trim().equals("今日")) {
            choiceStatue.setSelection(0);
        } else {
            choiceStatue.setSelection(choice);
        }
    }


    private void findViewById(View view) {
        spinnerT = view.findViewById(R.id.spinnerT);
        name = view.findViewById(R.id.name);
        money = view.findViewById(R.id.money);
        limitP = view.findViewById(R.id.limitP);
        remind = view.findViewById(R.id.remind);
        remindS = view.findViewById(R.id.remindS);
        remindD = view.findViewById(R.id.remindD);
        showDate = view.findViewById(R.id.showDate);
        datePicker = view.findViewById(R.id.datePicker);
        noWeekend = view.findViewById(R.id.noWeekend);
        clear = view.findViewById(R.id.clear);
        save = view.findViewById(R.id.save);
        choiceStatue = view.findViewById(R.id.choiceStatue);
        shift = view.findViewById(R.id.shift);
        remindL = view.findViewById(R.id.remindL);
        noWeekendT = view.findViewById(R.id.noWeekendT);
        remindT = view.findViewById(R.id.remindT);
    }

    private class showDate implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            showDate.setVisibility(View.VISIBLE);
        }
    }

    private class choicedateClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String choicedate = datePicker.getYear() + "/" + String.valueOf(datePicker.getMonth() + 1) + "/" + datePicker.getDayOfMonth();
            limitP.setText(choicedate);
            showDate.setVisibility(View.GONE);
        }
    }


    private class dateStatue implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            remind.setChecked(b);
            if (b) {
                remind.setX(remindL.getWidth() / 10 - remindL.getWidth() / 20);
                remindT.setX(remindL.getWidth() / 10 + remind.getWidth() - remindL.getWidth() / 20);
                remindS.setX(remindL.getWidth() / 3 + remindL.getWidth() / 10 - remindL.getWidth() / 20);
                noWeekend.setX((remindL.getWidth() * 2 / 3) + remindL.getWidth() / 20 - remindL.getWidth() / 20);
                noWeekendT.setX((remindL.getWidth() * 2 / 3) + remindL.getWidth() / 20 + noWeekend.getWidth() - remindL.getWidth() / 20);

                remindS.setVisibility(View.VISIBLE);
                noWeekend.setVisibility(View.VISIBLE);
                noWeekendT.setVisibility(View.VISIBLE);
                remindD.setVisibility(View.GONE);
            } else {
                remind.setX(remindL.getWidth() / 3);
                remindT.setX(remindL.getWidth() / 3 + remind.getWidth());

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
            ArrayList<String> spinneritem = new ArrayList<>();
            if (position == 0) {
                remind.setX(remindL.getWidth()/10-remindL.getWidth()/20);
                remindT.setX(remindL.getWidth()/10+remind.getWidth()-remindL.getWidth()/20);
                remindS.setX(remindL.getWidth()/3+remindL.getWidth()/10-remindL.getWidth()/20);
                noWeekend.setX((remindL.getWidth()*2/3)+remindL.getWidth()/20-remindL.getWidth()/20);
                noWeekendT.setX((remindL.getWidth()*2/3)+remindL.getWidth()/20+noWeekend.getWidth()-remindL.getWidth()/20);
                remindD.setX((remindL.getWidth()*2/3)+remindL.getWidth()/20-remindL.getWidth()/20);

                noWeekend.setVisibility(View.VISIBLE);
                noWeekendT.setVisibility(View.VISIBLE);
                remindD.setVisibility(View.GONE);
                return;
            } else if (position == 1) {
                spinneritem.add("星期一");
                spinneritem.add("星期二");
                spinneritem.add("星期三");
                spinneritem.add("星期四");
                spinneritem.add("星期五");
                spinneritem.add("星期六");
                spinneritem.add("星期日");
            } else if (position == 2) {
                for (int i = 1; i <= 31; i++) {
                    spinneritem.add("  " + String.valueOf(i) + "  ");
                }

            } else {
                for (int i = 1; i <= 12; i++) {
                    spinneritem.add(" " + String.valueOf(i) + "月");
                }
            }
            noWeekend.setChecked(false);
            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(context,R.layout.spinneritem,spinneritem);
            arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
            remindD.setAdapter(arrayAdapter);
            remindD.setVisibility(View.VISIBLE);
            noWeekend.setChecked(false);
            remind.setX(remindL.getWidth()/10-remindL.getWidth()/20);
            remindT.setX(remindL.getWidth()/10+remind.getWidth()-remindL.getWidth()/20);
            remindS.setX(remindL.getWidth()/3+remindL.getWidth()/10-remindL.getWidth()/20);
            noWeekend.setX((remindL.getWidth()*2/3)+remindL.getWidth()/20-remindL.getWidth()/20);
            noWeekendT.setX((remindL.getWidth()*2/3)+remindL.getWidth()/20+noWeekend.getWidth()-remindL.getWidth()/20);
            remindD.setX((remindL.getWidth()*2/3)+remindL.getWidth()/20-remindL.getWidth()/20);
            noWeekend.setVisibility(View.GONE);
            noWeekendT.setVisibility(View.GONE);
            if (first) {
                remindD.setSelection(updateChoice);
                first = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class clearOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //showDate not save
            if(showDate.getVisibility()==View.VISIBLE)
            {
                return;
            }
            name.setText("");
            money.setText("");
            remind.setChecked(false);
        }
    }

    private class saveOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String goalName = name.getText().toString().trim();
            String goalMoney = money.getText().toString().trim();
            String dayStatue = choiceStatue.getSelectedItem().toString().trim();
            String day = limitP.getText().toString().trim();


            //showDate not save
            if(showDate.getVisibility()==View.VISIBLE)
            {
                return;
            }


            if (goalName == null || goalName.length() <= 0) {
                name.setError("不能空白");
                return;
            }
            if (goalMoney == null || goalMoney.length() <= 0) {
                money.setError("不能空白");
                return;
            }

            try {
                if (Integer.valueOf(money.getText().toString().trim()) == 0) {
                    money.setError("金額不能為0");
                    return;
                }
            } catch (Exception e) {
                money.setError("只能輸入數字");
                return;
            }

            if (dayStatue.equals(startTitle)) {
                if (day == null || day.length() <= 0) {
                    limitP.setError("不能空白");
                    Common.showToast(context, "不能空白");
                    return;
                }
                String[] dates = day.split("/");
                Calendar c = new GregorianCalendar(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 12, 0, 0);
                Date d = new Date(c.getTimeInMillis());
                if (d.getTime() < System.currentTimeMillis()) {
                    limitP.setError("");
                    Common.showToast(context, "不能過去時間");
                    return;
                }
                goalVO.setEndTime(d);
            } else {
                goalVO.setEndTime(new Date(0));
            }

            if (dayStatue.equals("今日")) {
                goalVO.setStartTime(new Date(System.currentTimeMillis()));
            }
            String reMa = (remindD.getSelectedItem() == null) ? "" : remindD.getSelectedItem().toString();
            goalVO.setName(goalName);
            goalVO.setMoney(Integer.valueOf(goalMoney));
            goalVO.setNoWeekend(noWeekend.isChecked());
            goalVO.setNotify(remind.isChecked());
            goalVO.setNotifyDate(reMa);
            goalVO.setNotifyStatue(remindS.getSelectedItem().toString());
            if(poistion==0&&goalVO.getType().trim().equals("儲蓄"))
            {
                goalVO.setTimeStatue("今日");
            }else{
                goalVO.setTimeStatue(choiceStatue.getSelectedItem().toString().trim());
            }

            Log.d("XXXXXXXupdate",goalVO.getTimeStatue());
            goalDB.update(goalVO);
            Fragment fragment = new GoalListAll();
            Bundle bundle = new Bundle();
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            for (Fragment fragment1 : getFragmentManager().getFragments()) {
                fragmentTransaction.remove(fragment1);
            }
            fragmentTransaction.replace(R.id.body, fragment);
            fragmentTransaction.commit();
            Common.showToast(context, "修改成功!");
        }
    }


    private class choiceStatueSelected implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            TextView textView = (TextView) view;
            poistion=i;
            if (textView == null) {
                return;
            }
            String s = textView.getText().toString().trim();
            if (s.equals(startTitle)) {
                shift.setVisibility(View.VISIBLE);
                limitP.setVisibility(View.VISIBLE);
            } else {
                shift.setVisibility(View.GONE);
                limitP.setVisibility(View.GONE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}

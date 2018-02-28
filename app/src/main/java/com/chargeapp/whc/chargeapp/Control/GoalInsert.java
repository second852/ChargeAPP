package com.chargeapp.whc.chargeapp.Control;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.GregorianCalendar;

/**
 * Created by Wang on 2018/2/25.
 */

public class GoalInsert extends Fragment {
    private EditText name,money;
    private Spinner spinnerT,choiceStatue,remindS,remindD;
    private CheckBox remind,noWeekend;
    private LinearLayout showDate;
    private TextView limitP,save,clear,shift;
    private DatePicker datePicker;
    private View mainView;
    private GoalDB goalDB;
    private String action;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goal_setgoal, container, false);
        goalDB=new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        action= (String) getArguments().getSerializable("action");
        getActivity().setTitle("新增目標");
        mainView=view;
        findViewById(view);
        limitP.setOnClickListener(new showDate());
        showDate.setOnClickListener(new choicedateClick());
        remind.setOnCheckedChangeListener(new dateStatue());
        remindS.setOnItemSelectedListener(new choiceDateStatue());
        clear.setOnClickListener(new clearOnClick());
        save.setOnClickListener(new saveOnClick());
        spinnerT.setOnItemSelectedListener(new SelectType());
        choiceStatue.setOnItemSelectedListener(new choiceStatueSelected());
        return view;
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
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity(),R.layout.spinneritem,spinneritem);
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
            String choicedate=datePicker.getYear()+"/"+String.valueOf(datePicker.getMonth()+1)+"/"+datePicker.getDayOfMonth();
            limitP.setText(choicedate);
            showDate.setVisibility(View.GONE);
        }
    }




    private class dateStatue implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            int width=mainView.getWidth();
            if(b)
            {
                remind.setX(width/20);
                remindS.setX(spinnerT.getX());
                noWeekend.setX(width/2+remind.getWidth());
                remindD.setX(width/2+remind.getWidth());
                remindS.setVisibility(View.VISIBLE);
                noWeekend.setVisibility(View.VISIBLE);
                remindD.setVisibility(View.GONE);
            }else {
                remind.setX(width/2-remind.getWidth()/2);
                remindD.setVisibility(View.GONE);
                remindS.setVisibility(View.GONE);
                noWeekend.setVisibility(View.GONE);
            }
        }
    }


    private class choiceDateStatue implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            ArrayList<String> spinneritem=new ArrayList<>();
            if(position==0)
            {
                noWeekend.setVisibility(View.VISIBLE);
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
                    spinneritem.add("    "+String.valueOf(i)+"   ");
                }

            }else{
                for(int i=1;i<=12;i++) {
                    spinneritem.add(" "+String.valueOf(i)+"月");
                }
            }
            remindD.setVisibility(View.VISIBLE);
            noWeekend.setVisibility(View.GONE);
            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity(),R.layout.spinneritem,spinneritem);
            arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
            remindD.setAdapter(arrayAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class clearOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            name.setText("");
            money.setText("");
            remind.setChecked(false);
        }
    }

    private class saveOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            GoalVO goalVO=new GoalVO();
            String goalName = name.getText().toString().trim();
            String goalMoney = money.getText().toString().trim();
            String dayStatue=choiceStatue.getSelectedItem().toString().trim();
            String day=limitP.getText().toString().trim();
            if (goalName == null || goalName.length() <= 0)
            {
                name.setError("不能空白");
                return;
            }
            if(goalMoney==null||goalMoney.length()<=0)
            {
                money.setError("不能空白");
                return;
            }
            if(dayStatue.equals("今日"))
            {
                if(day==null||day.length()<=0)
                {
                    limitP.setError("不能空白");
                    Common.showToast(getActivity(),"不能空白");
                    return;
                }
                String[] dates = day.split("/");
                Calendar c = new GregorianCalendar(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 12, 0, 0);
                Date d = new Date(c.getTimeInMillis());
                if(d.getTime()<System.currentTimeMillis())
                {
                    limitP.setError(" ");
                    Common.showToast(getActivity(),"不能過去時間");
                    return;
                }
                goalVO.setEndTime(d);
            }else{
                goalVO.setEndTime(new Date(0));
            }
            String reMa=(remindD.getSelectedItem()==null)?"":remindD.getSelectedItem().toString();
            goalVO.setName(goalName);
            goalVO.setMoney(goalMoney);
            goalVO.setNoWeekend(noWeekend.isChecked());
            goalVO.setNotify(remind.isChecked());
            goalVO.setNotifyDate(reMa);
            goalVO.setNotifyStatue(remindS.getSelectedItem().toString());
            goalVO.setType(spinnerT.getSelectedItem().toString());
            goalVO.setTimeStatue(dayStatue);
            goalDB.insert(goalVO);


            Fragment fragment = new GoalListAll();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            for (Fragment fragment1 :  getFragmentManager().getFragments()) {
                fragmentTransaction.remove(fragment1);
            }
            fragmentTransaction.replace(R.id.body, fragment);
            fragmentTransaction.commit();
            Common.showToast(getActivity(),"新增成功!");
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
                spinneritem.add(" 每天 ");
                spinneritem.add(" 每周 ");
                spinneritem.add(" 每月 ");
                spinneritem.add(" 每年 ");
            }else{
                spinneritem.add(" 今日 ");
                spinneritem.add(" 每月 ");
                spinneritem.add(" 每年 ");
            }
            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity(),R.layout.spinneritem,spinneritem);
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
}

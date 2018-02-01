package com.chargeapp.whc.chargeapp.Control;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InsertIncome extends Fragment {
    private EditText money, newtype,number,detailname;
    private CheckBox fixdate;
    private EditText name;
    private TextView save, clear, date, saveType, clearType, showTitle,datesave;
    private GridView gridView,showAllpicture;
    private RelativeLayout insertType;
    private List<BankTypeVO> bankTypeVOSList;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> items;
    private List<Map<String, Object>> Detailitems;
    private Map<String, Object> item;
    private LinearLayout showPicture,showdate,showAllPL,showfixdate;
    private ImageView imageTitle;
    private int imageTitleId =999,imageDetatilId =999;
    private boolean isType;
    private DatePicker datePicker;
    private String choicedate;
    private Spinner choiceStatue,choiceday;
    private Gson gson;
    private SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
    private BankTybeDB bankTybeDB;
    private BankDB bankDB;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.insert_income, container, false);
        findviewByid(view);
        bankTybeDB=new BankTybeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDB=new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB =new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankTypeVOSList = bankTybeDB.getAll();
        Detailitems=new ArrayList<Map<String, Object>>();
        items = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < bankTypeVOSList.size(); i++) {
            item = new HashMap<String, Object>();
            item.put("image", MainActivity.imageAll[bankTypeVOSList.get(i).getImage()]);
            item.put("text", bankTypeVOSList.get(i).getName());
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
        date.setOnClickListener(new dateClickListener());
        showdate.setOnClickListener(new choicedateClick());
        choiceStatue.setOnItemSelectedListener(new choiceStateItem());
        clear.setOnClickListener(new clearAllInput());
        save.setOnClickListener(new savecomsumer());
        fixdate.setOnCheckedChangeListener(new showfixdateClick());
        return view;
    }


    public void findviewByid(View view) {
        name = view.findViewById(R.id.name);
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
        showPicture = view.findViewById(R.id.showPicture);
        imageTitle = view.findViewById(R.id.imageTitle);
        showTitle = view.findViewById(R.id.showTitle);
        showAllpicture=view.findViewById(R.id.showAllpicture);
        datesave=view.findViewById(R.id.datesave);
        showdate=view.findViewById(R.id.showdate);
        datePicker=view.findViewById(R.id.datePicker);
        showfixdate=view.findViewById(R.id.showfixdate);
        choiceStatue=view.findViewById(R.id.choiceStatue);
        choiceday=view.findViewById(R.id.choiceday);
        showAllPL=view.findViewById(R.id.showAllPL);
        number=view.findViewById(R.id.number);
        detailname=view.findViewById(R.id.detailname);
        ArrayList<String> spinneritem=new ArrayList<>();
        spinneritem.add("每天");
        spinneritem.add("每周");
        spinneritem.add("每個月");
        spinneritem.add("每年");
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity(),R.layout.spinneritem,spinneritem);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        choiceStatue.setAdapter(arrayAdapter);
    }

    private class cancelinsert implements View.OnClickListener {
        @Override
        public void onClick(View view) {
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
            Common.showToast(getActivity(), "新增成功");
            if(imageTitleId==999)
            {
                imageTitleId=MainActivity.imageAll.length-1;
            }
            if(imageDetatilId==999)
            {
                imageDetatilId=MainActivity.imageAll.length-1;
            }
            typeDB.insert(new TypeVO("other",newtype.getText().toString(),imageTitleId));
            if(isType)
            {
                items.remove(items.size() - 1);
                Map<String, Object> newitem = new HashMap<String, Object>();
                newitem.put("image",MainActivity.imageAll[imageTitleId]);
                newitem.put("text", newtype.getText().toString());
                items.add(newitem);
                items.add(item);
                adapter = new SimpleAdapter(getActivity(),
                        items, R.layout.main_item, new String[]{"image", "text"},
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
            showTitle.setText("選擇種類");
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
                    if(setimg.getId()==R.id.imageTitle)
                    {
                        imageTitleId=position;
                    }else{
                        imageDetatilId=position;
                    }
                }
            });
        }
    }

    private class choicedateClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choicedate=datePicker.getYear()+"-"+String.valueOf(datePicker.getMonth()+1)+"-"+datePicker.getDayOfMonth();
            date.setText(choicedate);
            showdate.setVisibility(View.GONE);
        }
    }


    private class showfixdateClick implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b)
            {
                fixdate.setVisibility(View.VISIBLE);
                fixdate.setX(showfixdate.getWidth()/10);
                choiceStatue.setX(showfixdate.getWidth()/2+showfixdate.getWidth()/10);
                choiceStatue.setVisibility(View.VISIBLE);
            }else{
                choiceStatue.setVisibility(View.GONE);
                choiceday.setVisibility(View.GONE);
                fixdate.setX(showfixdate.getWidth()/3);
            }
        }
    }

    private class choiceStateItem implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            String choiceitem=adapterView.getItemAtPosition(position).toString();
            ArrayList<String> spinneritem=new ArrayList<>();
            if(choiceitem.equals("每天"))
            {
                choiceday.setVisibility(View.GONE);
                fixdate.setX(showfixdate.getWidth()/10);
                choiceStatue.setX(showfixdate.getWidth()/2+showfixdate.getWidth()/10);
                choiceStatue.setVisibility(View.VISIBLE);
                return;
            }
            if(choiceitem.equals("每周"))
            {
                spinneritem.add("星期一");
                spinneritem.add("星期二");
                spinneritem.add("星期三");
                spinneritem.add("星期四");
                spinneritem.add("星期五");
                spinneritem.add("星期六");
                spinneritem.add("星期日");
            }
            if(choiceitem.equals("每個月"))
            {
                for(int i=1;i<=31;i++) {
                    spinneritem.add("    "+String.valueOf(i)+"   ");
                }
            }
            if(choiceitem.equals("每年"))
            {
                for(int i=1;i<=12;i++) {
                    spinneritem.add(" "+String.valueOf(i)+"月");
                }
            }
            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity(),R.layout.spinneritem,spinneritem);
            arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
            choiceday.setAdapter(arrayAdapter);
            choiceday.setVisibility(View.VISIBLE);
            fixdate.setX(showfixdate.getWidth()/20);
            choiceStatue.setX(showfixdate.getWidth()/3+showfixdate.getWidth()/20);
            choiceday.setX((showfixdate.getWidth()*2/3)+showfixdate.getWidth()/20);
        }



        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class choiceTypeitem implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            TextView t = view.findViewById(R.id.text);
            Log.d("one",t.getText().toString());
            if (t.getText().toString().equals("新增種類")) {
                showPicture.setVisibility(View.GONE);
                insertType.setVisibility(View.VISIBLE);
                newtype.setText(" ");
                isType=true;
                return;
            }
            name.setText(t.getText());
            showPicture.setVisibility(View.GONE);
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

    private String isnull(Object text)
    {
        if(text==null||text.toString().length()<=0)
        {
            return " ";
        }
        return text.toString();
    }


    private class savecomsumer implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            name.setBackgroundColor(Color.parseColor("#FFEE99"));
            if(name.getText().toString().trim()==null||name.getText().toString().trim().length()==0)
            {
                name.setBackgroundColor(Color.parseColor("#ff471a"));
                Common.showToast(getActivity(),"主項目不能空白");
                return;
            }
            if(money.getText().toString().trim()==null||money.getText().toString().trim().length()==0)
            {
                money.setError("金額不能空白");
                return;
            }
            if(date.getText().toString().trim()==null||date.getText().toString().trim().length()==0)
            {
                name.setError(" ");
                Common.showToast(getActivity(),"日期不能空白");
                return;
            }
            if(showdate.getVisibility()==View.VISIBLE||showPicture.getVisibility()==View.VISIBLE||showAllPL.getVisibility()==View.VISIBLE)
            {
                return;
            }
            gson=new Gson();
            Map<String,String> g=new HashMap<>();
            g.put("choicestatue",isnull(choiceStatue.getSelectedItem().toString()));
            g.put("choicedate",isnull(choiceday.getSelectedItem()));
            String fixdatedetail=gson.toJson(g);
            Calendar c=Calendar.getInstance();
            c.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
            Date d= new Date(c.getTimeInMillis());
            bankDB.insert(new BankVO(name.getText().toString(),detailname.getText().toString(),money.getText().toString(),d,String.valueOf(fixdate.isChecked()),fixdatedetail));
            Common.showToast(getActivity(),"新增成功");
        }
    }
}


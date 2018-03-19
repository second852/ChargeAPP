package com.chargeapp.whc.chargeapp.Control;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class UpdateInvoice extends Fragment {
    private TextView secondname, name,money,number;
    private TextView save, clear, date,detailname;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private InvoiceDB invoiceDB;
    private InvoiceVO invoiceVO;
    private ProgressDialog progressDialog;
    private String action;
    private LinearLayout firstL,secondL;
    private GridView firstG,secondG;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_invoice, container, false);
        findviewByid(view);
        setInvoice();
        getActivity().setTitle("修改資料");
        typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        clear.setOnClickListener(new clearAllInput());
        save.setOnClickListener(new saveInvoice());
        name.setOnClickListener(new showFirstG());
        secondname.setOnClickListener(new showSecondG());
        firstG.setOnItemClickListener(new firstGridOnClick());
        secondG.setOnItemClickListener(new secondGridOnClick());
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setFirstGrid();
        setSecondGrid();
        if(Common.showfirstgrid)
        {
            firstL.setVisibility(View.VISIBLE);
            Common.showfirstgrid=false;
        }
        if(Common.showsecondgrid)
        {
            secondL.setVisibility(View.VISIBLE);
            Common.showsecondgrid=false;
        }
    }

    private void setFirstGrid()
    {
        HashMap item;
        ArrayList items = new ArrayList<Map<String, Object>>();
        List<TypeVO> typeVOS=typeDB.getAll();
        for(TypeVO t:typeVOS)
        {
            item = new HashMap<String, Object>();
            item.put("image", MainActivity.imageAll[t.getImage()]);
            item.put("text",t.getName());
            items.add(item);
        }
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.add);
        item.put("text","新增");
        items.add(item);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                items, R.layout.main_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        firstG.setAdapter(adapter);
        firstG.setNumColumns(4);
    }

    private void setSecondGrid()
    {
        HashMap item;
        ArrayList items = new ArrayList<Map<String, Object>>();
        List<TypeDetailVO> typeDetailVOS=typeDetailDB.findByGroupname(name.getText().toString().trim());
        for(TypeDetailVO t:typeDetailVOS)
        {
            item = new HashMap<String, Object>();
            item.put("image", MainActivity.imageAll[t.getImage()]);
            item.put("text",t.getName());
            items.add(item);
        }
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.returnt);
        item.put("text","返回");
        items.add(item);
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.add);
        item.put("text","新增");
        items.add(item);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                items, R.layout.main_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        secondG.setAdapter(adapter);
        secondG.setNumColumns(4);
    }

    private void setInvoice() {
        invoiceVO = (InvoiceVO) getArguments().getSerializable("invoiceVO");
        action= (String) getArguments().getSerializable("action");
        name.setText(invoiceVO.getMaintype().equals("O")?"其他":invoiceVO.getMaintype());
        number.setText(invoiceVO.getInvNum());
        secondname.setText(invoiceVO.getSecondtype().equals("O")?"其他":invoiceVO.getSecondtype());
        money.setText(invoiceVO.getAmount());
        date.setText(Common.sTwo.format(new Date(invoiceVO.getTime().getTime())));
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
        number = view.findViewById(R.id.number);
        detailname = view.findViewById(R.id.detailname);
        firstG=view.findViewById(R.id.firstG);
        firstL=view.findViewById(R.id.firstL);
        secondG=view.findViewById(R.id.secondG);
        secondL=view.findViewById(R.id.secondL);
        progressDialog = new ProgressDialog(getActivity());
    }

    public void setLayout() {
        if (!invoiceVO.getDetail().equals("0")) {
            detailname.setText("--點擊顯示--");
            detailname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    invoiceVO.setMaintype(name.getText().toString());
                    invoiceVO.setSecondtype(secondname.getText().toString());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("invoiceVO",invoiceVO);
                    bundle.putSerializable("action",action);
                    returnThisFramgent(new UpdateDetail(),bundle);
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








    private class clearAllInput implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            name.setText(" ");
            secondname.setText(" ");
        }
    }


    private class saveInvoice implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            name.setBackgroundColor(Color.parseColor("#FFEE99"));
            secondname.setBackgroundColor(Color.parseColor("#FFEE99"));
            if(firstL.getVisibility()==View.VISIBLE||secondL.getVisibility()==View.VISIBLE)
            {
                return;
            }
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
            String[] dates = date.getText().toString().split("/");
            Calendar c = Calendar.getInstance();
            c.set(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 12, 0, 0);
            invoiceVO.setMaintype(name.getText().toString());
            invoiceVO.setSecondtype(secondname.getText().toString());
            invoiceVO.setAmount(Integer.valueOf(money.getText().toString().trim()));
            invoiceVO.setTime(new Timestamp(c.getTimeInMillis()));
            invoiceVO.setInvNum(number.getText().toString());
            invoiceDB.update(invoiceVO);
            Common.showToast(getActivity(), "修改成功");
            goBackFramgent();
        }
    }

    private void returnThisFramgent(Fragment fragment,Bundle bundle)
    {


        if(action.equals("SelectDetList"))
        {
            bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
            bundle.putSerializable("ShowAllCarrier",  getArguments().getSerializable("ShowAllCarrier"));
            bundle.putSerializable("noShowCarrier",  getArguments().getSerializable("noShowCarrier"));
            bundle.putSerializable("year",  getArguments().getSerializable("year"));
            bundle.putSerializable("month",  getArguments().getSerializable("month"));
            bundle.putSerializable("day",  getArguments().getSerializable("day"));
            bundle.putSerializable("key",  getArguments().getSerializable("key"));
            bundle.putSerializable("carrier",  getArguments().getSerializable("carrier"));
            bundle.putSerializable("Statue", getArguments().getSerializable("Statue"));
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            bundle.putSerializable("period",  getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
        }else if(action.equals("SelectShowCircleDe"))
        {
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
            bundle.putSerializable("dweek",getArguments().getSerializable("dweek"));
            bundle.putSerializable("position",getArguments().getSerializable("position"));
        }
        fragment.setArguments(bundle);
        switchFramgent(fragment);
    }



    private void goBackFramgent() {
        Fragment fragment=null;
        Bundle bundle=new Bundle();
        if(action.equals("SelectListModelCom"))
        {
            fragment=new SelectListModelActivity();
        }else if(action.equals("SelectDetList"))
        {
            fragment=new SelectDetList();
            bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
            bundle.putSerializable("ShowAllCarrier",  getArguments().getSerializable("ShowAllCarrier"));
            bundle.putSerializable("noShowCarrier",  getArguments().getSerializable("noShowCarrier"));
            bundle.putSerializable("year",  getArguments().getSerializable("year"));
            bundle.putSerializable("month",  getArguments().getSerializable("month"));
            bundle.putSerializable("day",  getArguments().getSerializable("day"));
            bundle.putSerializable("key",  getArguments().getSerializable("key"));
            bundle.putSerializable("carrier",  getArguments().getSerializable("carrier"));
            bundle.putSerializable("Statue", getArguments().getSerializable("Statue"));
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            bundle.putSerializable("period",  getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
        }else if(action.equals("SelectShowCircleDe"))
        {
            fragment=new SelectShowCircleDe();
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
            bundle.putSerializable("dweek",getArguments().getSerializable("dweek"));
            bundle.putSerializable("position",getArguments().getSerializable("position"));
        }else if(action.equals("SelectListModelCom"))
        {
            fragment=new SelectListModelCom();
        }
        fragment.setArguments(bundle);
        switchFramgent(fragment);
    }

    public void switchFramgent(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 :  getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
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
            TextView textView=view.findViewById(R.id.text);
            String type=textView.getText().toString().trim();
            if(type.equals("新增"))
            {
                invoiceVO.setMaintype(name.getText().toString());
                invoiceVO.setSecondtype(secondname.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putSerializable("object",invoiceVO);
                bundle.putSerializable("action",action);
                Common.showfirstgrid=true;
                returnThisFramgent(new InsertConsumeType(),bundle);
                return;
            }
            name.setText(type);
            setSecondGrid();
            firstL.setVisibility(View.GONE);
            secondL.setVisibility(View.VISIBLE);
        }
    }

    private class secondGridOnClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView textView=view.findViewById(R.id.text);
            String type=textView.getText().toString().trim();
            if(type.equals("返回"))
            {
                firstL.setVisibility(View.VISIBLE);
                secondL.setVisibility(View.GONE);
                return;
            }
            if(type.equals("新增"))
            {
                invoiceVO.setMaintype(name.getText().toString());
                invoiceVO.setSecondtype(secondname.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putSerializable("object",invoiceVO);
                bundle.putSerializable("action",action);
                Common.showsecondgrid=true;
                returnThisFramgent(new InsertConsumeType(),bundle);
                return;
            }
            secondname.setText(type);
            secondL.setVisibility(View.GONE);
        }
    }

    private class showSecondG implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            secondL.setVisibility(View.VISIBLE);
        }
    }
}





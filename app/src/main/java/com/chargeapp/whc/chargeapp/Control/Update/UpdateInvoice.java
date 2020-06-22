package com.chargeapp.whc.chargeapp.Control.Update;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.Download;
import com.chargeapp.whc.chargeapp.Control.HomePage.HomePagetList;
import com.chargeapp.whc.chargeapp.Control.Insert.InsertConsumeType;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Search.SearchMain;
import com.chargeapp.whc.chargeapp.Control.SelectList.SelectListModelActivity;
import com.chargeapp.whc.chargeapp.Control.SelectList.SelectListModelCom;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectDetList;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectShowCircleDe;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectShowCircleDeList;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class UpdateInvoice extends Fragment {

    private BootstrapEditText number, name, money, secondname, date;
    private BootstrapButton save, clear,currency;
    private BootstrapLabel detailname;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private InvoiceDB invoiceDB;
    private InvoiceVO invoiceVO;
    private ProgressDialog progressDialog;
    private String action;
    private LinearLayout firstL,secondL;
    private GridView firstG,secondG;
    private Activity context;
    private String oldMainType;
    private List<TypeVO> typeVOS;
    private TypeVO typeVO;

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
        View view = inflater.inflate(R.layout.update_invoice, container, false);
        findViewById(view);
        setInvoice();
        context.setTitle("修改資料");
        Common.setChargeDB(context);
        typeDB = new TypeDB(MainActivity.chargeAPPDB);
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB);
        clear.setOnClickListener(new clearAllInput());
        save.setOnClickListener(new saveInvoice());
        name.setOnClickListener(new showFirstG());
        secondname.setOnClickListener(new showSecondG());
        firstG.setOnItemClickListener(new firstGridOnClick());
        secondG.setOnItemClickListener(new secondGridOnClick());
        ((AppCompatActivity)context).getSupportActionBar().setDisplayShowCustomEnabled(false);
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
        typeVOS=typeDB.getAll();
        for(TypeVO t:typeVOS)
        {
            item = new HashMap<String, Object>();
            item.put("image", Download.imageAll[t.getImage()]);
            item.put("text",t.getName());
            items.add(item);
        }
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.add);
        item.put("text","新增");
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

    private void setSecondGrid()
    {
        HashMap item;
        ArrayList items = new ArrayList<Map<String, Object>>();
        List<TypeDetailVO> typeDetailVOS=typeDetailDB.findByGroupname(name.getText().toString().trim());
        for(TypeDetailVO t:typeDetailVOS)
        {
            item = new HashMap<String, Object>();
            item.put("image", Download.imageAll[t.getImage()]);
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

    private void setInvoice() {
        invoiceVO = (InvoiceVO) getArguments().getSerializable("invoiceVO");
        action= (String) getArguments().getSerializable("action");


        if(invoiceVO.getMaintype().trim().equals("O"))
        {
            name.setText("其他");
            secondname.setText("其他");
        }else if(invoiceVO.getSecondtype().trim().equals("0"))
        {
            name.setText("未知");
            secondname.setText("未知");
        }else{
            name.setText(invoiceVO.getMaintype());
            secondname.setText(invoiceVO.getSecondtype());
        }

        if(invoiceVO.getCurrency()==null||invoiceVO.getCurrency().trim().isEmpty())
        {
           currency.setText(Common.getCurrency("TWD"));
        }else {
            currency.setText(Common.Currency().get(invoiceVO.getCurrency()));
        }

        number.setText(invoiceVO.getInvNum());
        money.setText(String.valueOf(invoiceVO.getRealAmount()));
        date.setText(Common.sTwo.format(new Date(invoiceVO.getTime().getTime())));
        setLayout();
    }

    public void cancelShow() {
        progressDialog.cancel();
        Common.showToast(context, "財政部網路忙線~");
    }


    public void findViewById(View view) {
        name = view.findViewById(R.id.name);
        name.setShowSoftInputOnFocus(false);
        secondname = view.findViewById(R.id.secondname);
        secondname.setShowSoftInputOnFocus(false);
        money = view.findViewById(R.id.money);
        money.setFocusable(false);
        money.setFocusableInTouchMode(false);
        money.setBackgroundColor(Color.parseColor("#DDDDDD"));
        date = view.findViewById(R.id.date);
        date.setFocusable(false);
        date.setFocusableInTouchMode(false);
        date.setBackgroundColor(Color.parseColor("#DDDDDD"));
        save = view.findViewById(R.id.save);
        clear = view.findViewById(R.id.clear);
        number = view.findViewById(R.id.number);
        number.setFocusable(false);
        number.setFocusableInTouchMode(false);
        number.setBackgroundColor(Color.parseColor("#DDDDDD"));
        detailname = view.findViewById(R.id.detailname);
        firstG=view.findViewById(R.id.firstG);
        firstL=view.findViewById(R.id.firstL);
        secondG=view.findViewById(R.id.secondG);
        secondL=view.findViewById(R.id.secondL);
        progressDialog = new ProgressDialog(context);
        currency=view.findViewById(R.id.currency);
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
                    ConnectivityManager mConnectivityManager = (ConnectivityManager) UpdateInvoice.this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                    if(mNetworkInfo!=null)
                    {
                        new GetSQLDate(UpdateInvoice.this, invoiceVO,context).execute("reDownload");
                        progressDialog.setMessage("正在下傳資料,請稍候...");
                        progressDialog.show();
                    }else{
                        Common.showToast( UpdateInvoice.this.context,"網路沒有開啟，無法下載!");
                    }

                }
            });
        }
    }








    private class clearAllInput implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(firstL.getVisibility()==View.VISIBLE)
            {
                return;
            }
            if(secondL.getVisibility()==View.VISIBLE)
            {
                return;
            }
            name.setText(" ");
            secondname.setText(" ");
        }
    }


    private class saveInvoice implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(firstL.getVisibility()==View.VISIBLE||secondL.getVisibility()==View.VISIBLE)
            {
                return;
            }
            if (name.getText()== null || name.getText().toString().trim().length() == 0) {
                name.setError(" ");
                Common.showToast(context, "主項目不能空白");
                return;
            }
            if (secondname.getText() == null || secondname.getText().toString().trim().length() == 0) {
                secondname.setError(" ");
                Common.showToast(context, "次項目不能空白");
                return;
            }
            try {
                if(!oldMainType.equals(name.getText().toString().trim()))
                {
                    secondname.setError(" ");
                    Common.showToast(context, "次項目不屬於主項目種類");
                    return;
                }
            }catch (Exception e)
            {

            }

            if (money.getText() == null || money.getText().toString().trim().length() == 0) {
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
                name.setError(" ");
                Common.showToast(context, "日期不能空白");
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
            String[] dates = date.getText().toString().split("/");
            Calendar c = Calendar.getInstance();
            c.set(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 12, 0, 0);
            invoiceVO.setMaintype(name.getText().toString().trim());
            invoiceVO.setSecondtype(secondname.getText().toString().trim());

            Double inputMoney;
            try {
                inputMoney=Common.nf.parse(money.getText().toString().trim()).doubleValue();
            } catch (ParseException e) {
                money.setError("不是數值!");
                return;
            }
            invoiceVO.setRealAmount(Common.onlyNumber(Common.doubleRemoveZero(inputMoney)));
            invoiceVO.setAmount(inputMoney.intValue());
            invoiceVO.setTime(new Timestamp(c.getTimeInMillis()));
            invoiceVO.setInvNum(number.getText().toString().trim());
            invoiceDB.update(invoiceVO);
            Common.showToast(context, "修改成功");
            goBackFramgent();
        }
    }

    private void returnThisFramgent(Fragment fragment,Bundle bundle)
    {

        bundle.putSerializable("typeVO",typeVO);
        bundle.putSerializable("invoiceVO",invoiceVO);
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
            bundle.putSerializable("statue", getArguments().getSerializable("statue"));
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
        }else if(action.equals("SelectShowCircleDeList"))
        {
            bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
            bundle.putSerializable("ShowAllCarrier",  getArguments().getSerializable("ShowAllCarrier"));
            bundle.putSerializable("noShowCarrier",  getArguments().getSerializable("noShowCarrier"));
            bundle.putSerializable("year",  getArguments().getSerializable("year"));
            bundle.putSerializable("month",  getArguments().getSerializable("month"));
            bundle.putSerializable("day",  getArguments().getSerializable("day"));
            bundle.putSerializable("key",  getArguments().getSerializable("key"));
            bundle.putSerializable("carrier",  getArguments().getSerializable("carrier"));
            bundle.putSerializable("statue", getArguments().getSerializable("statue"));
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            bundle.putSerializable("period",  getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
            bundle.putStringArrayList("OKey",getArguments().getStringArrayList("OKey"));
        }else if(action.equals("HomePagetList"))
        {
            bundle.putStringArrayList("OKey",getArguments().getStringArrayList("OKey"));
            bundle.putSerializable("position",getArguments().getSerializable("position"));
            bundle.putSerializable("key", getArguments().getSerializable("key"));
        }else if(action.equals(Common.searchMainString))
        {
            bundle.putAll(getArguments());
        }
        fragment.setArguments(bundle);
        MainActivity.bundles.add(bundle);
        MainActivity.oldFramgent.add("UpdateInvoice");
        switchFramgent(fragment);
    }



    private void goBackFramgent() {
        MainActivity.bundles.remove(MainActivity.bundles.size()-1);
        MainActivity.oldFramgent.remove(MainActivity.oldFramgent.size()-1);
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
            bundle.putSerializable("statue", getArguments().getSerializable("statue"));
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
        }else if(action.equals("SelectShowCircleDeList"))
        {
            fragment=new SelectShowCircleDeList();
            bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
            bundle.putSerializable("ShowAllCarrier",  getArguments().getSerializable("ShowAllCarrier"));
            bundle.putSerializable("noShowCarrier",  getArguments().getSerializable("noShowCarrier"));
            bundle.putSerializable("year",  getArguments().getSerializable("year"));
            bundle.putSerializable("month",  getArguments().getSerializable("month"));
            bundle.putSerializable("day",  getArguments().getSerializable("day"));
            bundle.putSerializable("key",  getArguments().getSerializable("key"));
            bundle.putSerializable("carrier",  getArguments().getSerializable("carrier"));
            bundle.putSerializable("statue", getArguments().getSerializable("statue"));
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            bundle.putSerializable("period",  getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
            bundle.putStringArrayList("OKey",getArguments().getStringArrayList("OKey"));
        }else if(action.equals("HomePagetList"))
        {
            fragment=new HomePagetList();
            bundle.putStringArrayList("OKey",getArguments().getStringArrayList("OKey"));
            bundle.putSerializable("position",getArguments().getSerializable("position"));
            bundle.putSerializable("key", getArguments().getSerializable("key"));
        }else if(action.equals(Common.searchMainString))
        {
            fragment=new SearchMain();
            bundle.putAll(getArguments());
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
            secondL.setVisibility(View.GONE);
            firstL.setVisibility(View.VISIBLE);
        }

    }

    private class firstGridOnClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            name.setError(null);
            TextView textView=view.findViewById(R.id.text);
            String type=textView.getText().toString().trim();
            if(i<typeVOS.size())
            {
               typeVO=typeVOS.get(i);
            }

            if(type.equals("新增"))
            {
                invoiceVO.setMaintype(name.getText().toString().trim());
                invoiceVO.setSecondtype(secondname.getText().toString().trim());
                Bundle bundle = new Bundle();
                bundle.putSerializable("object",invoiceVO);
                bundle.putSerializable("action",action);
                Common.showfirstgrid=true;
                returnThisFramgent(new InsertConsumeType(),bundle);
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
            if (type.equals("取消")) {
                Common.showsecondgrid = false;
                secondL.setVisibility(View.GONE);
                return;
            }
            oldMainType=name.getText().toString().trim();
            secondname.setText(type);
            secondname.setSelection(type.length());
            secondL.setVisibility(View.GONE);
            firstL.setVisibility(View.GONE);
        }
    }

    private class showSecondG implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            firstL.setVisibility(View.GONE);
            secondL.setVisibility(View.VISIBLE);
        }
    }
}





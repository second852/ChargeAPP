package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 1709008NB01 on 2018/2/22.
 */

public class InsertIncomeType extends Fragment {
    private ImageView mainImage, resultI;
    private EditText mainName;
    private Button save, clear;
    private LinearLayout choiceL;
    private GridView choiceG;
    private String action;
    private BankTybeDB bankTybeDB;
    private BankTypeVO bankTypeVO;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.updae_inc_type, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(false);
        action = (String) getArguments().getSerializable("action");
        bankTybeDB = new BankTybeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankTypeVO = new BankTypeVO();
        findViewById(view);
        setGridPicture();
        getActivity().setTitle("新增項目類別");
        choiceG.setOnItemClickListener(new choicePicture());
        clear.setOnClickListener(new clearOnClick());
        save.setOnClickListener(new insertType());
        mainImage.setOnClickListener(new showImage());
        return view;
    }


    private void setGridPicture() {
        HashMap item;
        ArrayList items = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < Download.imageAll.length; i++) {
            item = new HashMap<String, Object>();
            item.put("image", Download.imageAll[i]);
            item.put("text", " ");
            items.add(item);
        }
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                items, R.layout.main_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        choiceG.setAdapter(adapter);
        choiceG.setNumColumns(4);
    }

    private void findViewById(View view) {
        mainImage = view.findViewById(R.id.mainImage);
        mainName = view.findViewById(R.id.mainName);
        save = view.findViewById(R.id.save);
        clear = view.findViewById(R.id.clear);
        choiceL = view.findViewById(R.id.choiceL);
        choiceG = view.findViewById(R.id.choiceG);
    }

    private class showImage implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choiceL.setVisibility(View.VISIBLE);
            resultI = (ImageView) view;
        }
    }

    private class choicePicture implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            resultI.setImageResource(Download.imageAll[i]);
            choiceL.setVisibility(View.GONE);
            bankTypeVO.setImage(i);
        }
    }

    private class clearOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mainImage.setImageResource(R.drawable.add);
            mainName.setText(" ");
        }
    }

    private class insertType implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String mainType = mainName.getText().toString();
            if (mainType == null || mainType.isEmpty()) {
                mainName.setError("項目種類不能空白");
                return;
            }
            if (mainType.indexOf(";") != -1) {
                mainName.setError("項目種類不能有特殊符號");
                return;
            }
            BankTypeVO b = bankTybeDB.findByName(mainType);
            if (b != null) {
                mainName.setError("項目種類名稱不能重複");
                return;
            }
            bankTypeVO.setGroupNumber(mainType);
            bankTypeVO.setName(mainType);
            bankTybeDB.insert(bankTypeVO);
            gotoFramgent();

            //關閉鍵盤
            View v =InsertIncomeType.this.getActivity().getCurrentFocus();
            if (v != null) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            Common.showToast(getActivity(), "新增成功");
        }
    }

    private void gotoFramgent() {
        Bundle bundle = new Bundle();
        Fragment fragment = null;
        bundle.putSerializable("action", action);
        bundle.putSerializable("bankVO", getArguments().getSerializable("bankVO"));
        if (action.equals("InsertIncome")) {
            fragment = new InsertIncome();
            bundle.putSerializable("needSet", true);
        } else if (action.equals("SelectListPieIncome")) {
            fragment = new UpdateIncome();
            bundle.putSerializable("type", getArguments().getSerializable("type"));
            bundle.putStringArrayList("OKey", getArguments().getStringArrayList("OKey"));
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            bundle.putSerializable("year", getArguments().getSerializable("year"));
            bundle.putSerializable("month", getArguments().getSerializable("month"));
            bundle.putSerializable("day", getArguments().getSerializable("day"));
            bundle.putSerializable("statue", getArguments().getSerializable("statue"));
            bundle.putSerializable("index", getArguments().getSerializable("index"));
        } else if (action.equals("SelectListPieIncome")) {
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            bundle.putSerializable("year", getArguments().getSerializable("year"));
            bundle.putSerializable("month", getArguments().getSerializable("month"));
            bundle.putSerializable("day", getArguments().getSerializable("day"));
            bundle.putSerializable("statue", getArguments().getSerializable("statue"));
            bundle.putSerializable("index", getArguments().getSerializable("index"));
        }else if(action.equals("SettingListFixIon"))
        {
            bundle.putSerializable("position",getArguments().getSerializable("position"));
        }else if(action.equals("SelectListModelIM"))
        {
            fragment = new UpdateIncome();
        }else if(action.equals("SelectListBarIncome"))
        {
            fragment = new UpdateIncome();
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            bundle.putSerializable("year", getArguments().getSerializable("year"));
            bundle.putSerializable("month", getArguments().getSerializable("month"));
            bundle.putSerializable("day", getArguments().getSerializable("day"));
            bundle.putSerializable("statue", getArguments().getSerializable("statue"));
            bundle.putSerializable("index", getArguments().getSerializable("index"));
        }else if(action.equals("InsertIncome"))
        {
            fragment=new InsertActivity();
            bundle=MainActivity.bundles.getLast();
        }
        fragment.setArguments(bundle);
        switchFramgent(fragment);
    }

    public void switchFramgent(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }
}

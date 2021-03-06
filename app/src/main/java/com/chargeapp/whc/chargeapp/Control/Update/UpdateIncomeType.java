package com.chargeapp.whc.chargeapp.Control.Update;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.Download;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingListType;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 1709008NB01 on 2018/2/22.
 */

public class UpdateIncomeType extends Fragment {
    private ImageView mainImage, resultI;
    private EditText mainName;
    private BootstrapButton save, clear;
    private RelativeLayout choiceL;
    private GridView choiceG;
    private String action;
    private BankTypeDB bankTypeDB;
    private BankTypeVO bankTypeVO;
    private TypeDB typeDB;
    private TypeVO typeVO;
    private TypeDetailDB typeDetailDB;
    private InvoiceDB invoiceDB;
    private ConsumeDB consumeDB;
    private BankDB bankDB;
    private ProgressDialog progressDialog;
    private Handler handler;
    private String oldname;
    private Activity context;
    private TextView button;

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
        View view = inflater.inflate(R.layout.updae_inc_type, container, false);
        ((AppCompatActivity) context).getSupportActionBar().setDisplayShowCustomEnabled(false);
        action = (String) getArguments().getSerializable("action");
        Common.setChargeDB(context);
        bankTypeDB = new BankTypeDB(MainActivity.chargeAPPDB);
        typeDB = new TypeDB(MainActivity.chargeAPPDB);
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB);
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB);
        bankDB = new BankDB(MainActivity.chargeAPPDB);
        progressDialog = new ProgressDialog(context);
        handler = new Handler(Looper.getMainLooper());
        findViewById(view);
        setGridPicture();
        context.setTitle("修改類別");
        choiceG.setOnItemClickListener(new choicePicture());
        clear.setOnClickListener(new clearOnClick());
        save.setOnClickListener(new insertType());
        mainImage.setOnClickListener(new showImage());
        if (action.equals("updateT")) {
            setTypeVO();
        } else {
            setBankVO();
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choiceL.setVisibility(View.GONE);
            }
        });
        return view;
    }

    private void setBankVO() {
        bankTypeVO = (BankTypeVO) getArguments().getSerializable("bankTypeVO");
        mainImage.setImageResource(Download.imageAll[bankTypeVO.getImage()]);
        mainName.setText(bankTypeVO.getName());
        oldname=bankTypeVO.getName();
    }

    private void setTypeVO() {
        typeVO = (TypeVO) getArguments().getSerializable("typeVO");
        mainImage.setImageResource(Download.imageAll[typeVO.getImage()]);
        mainName.setText(typeVO.getName());
        oldname=typeVO.getName();
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
        SimpleAdapter adapter = new SimpleAdapter(context,
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
        button=view.findViewById(R.id.button);
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
            if (action.equals("updateT")) {
                typeVO.setImage(i);
            } else {
                bankTypeVO.setImage(i);
            }
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
            String mainType = mainName.getText().toString().trim();
            if (mainType == null || mainType.isEmpty()) {
                mainName.setError("項目種類不能空白");
                return;
            }
            if (mainType.indexOf(";") != -1) {
                mainName.setError("項目種類不能有特殊符號");
                return;
            }

            if (action.equals("updateT")) {
                TypeVO oldTypeVO = typeDB.findTypeName(mainType);
                if ((oldTypeVO != null)&&(!oldname.trim().equals(mainType))) {
                    mainName.setError("項目不能重複");
                    return;
                }
                progressDialog.setTitle("修改中…");
                progressDialog.show();
                handler.postDelayed(consume,500);

            } else {

                BankTypeVO old = bankTypeDB.findByName(mainType);
                if ((old != null)&&(!oldname.trim().equals(mainType)))
                {
                    mainName.setError("項目不能重複");
                    return;
                }
                progressDialog.setTitle("修改中…");
                progressDialog.show();
                handler.postDelayed(income,500);
            }
        }

        private Runnable consume = new Runnable() {
            @Override
            public void run() {
                String mainType = mainName.getText().toString().trim();
                List<TypeDetailVO> typeDetailVOS = typeDetailDB.findByGroupname(typeVO.getName());
                for (TypeDetailVO t : typeDetailVOS) {
                    t.setGroupNumber(mainType);
                    typeDetailDB.update(t);
                }
                List<ConsumeVO> consumeVOS = consumeDB.getMainTypePeriod(typeVO.getName().trim());
                for (ConsumeVO c : consumeVOS) {
                    c.setMaintype(mainType);
                    consumeDB.update(c);
                }
                List<InvoiceVO> invoiceVOS = invoiceDB.getInvoiceMainType(typeVO.getName().trim());
                for (InvoiceVO i : invoiceVOS) {
                    i.setMaintype(mainType);
                    invoiceDB.update(i);
                }
                typeVO.setGroupNumber(mainType);
                typeVO.setName(mainType);
                typeDB.update(typeVO);
                progressDialog.cancel();
                Bundle bundle = new Bundle();
                Fragment fragment = new SettingListType();
                bundle.putSerializable("position", getArguments().getSerializable("position"));
                bundle.putSerializable("spinnerC", getArguments().getSerializable("spinnerC"));
                fragment.setArguments(bundle);
                switchFramgent(fragment);
                Common.showToast(context, "修改成功!");
            }
        };

        private Runnable income = new Runnable() {
            @Override
            public void run() {
                String mainType = mainName.getText().toString().trim();

                List<BankVO> bankVOS = bankDB.getMainType(bankTypeVO.getName().trim());
                for (BankVO b : bankVOS) {
                    b.setMaintype(mainType);
                    bankDB.update(b);
                }
                bankTypeVO.setGroupNumber(mainType);
                bankTypeVO.setName(mainType);
                bankTypeDB.update(bankTypeVO);
                progressDialog.cancel();
                Bundle bundle = new Bundle();
                Fragment fragment = new SettingListType();
                bundle.putSerializable("position", getArguments().getSerializable("position"));
                bundle.putSerializable("spinnerC", getArguments().getSerializable("spinnerC"));
                fragment.setArguments(bundle);
                switchFramgent(fragment);
                Common.showToast(context, "修改成功!");
            }
        };

        public void switchFramgent(Fragment fragment) {
            MainActivity.bundles.remove(MainActivity.bundles.size() - 1);
            MainActivity.oldFramgent.remove(MainActivity.oldFramgent.size() - 1);
            //關閉鍵盤
            View v = UpdateIncomeType.this.context.getCurrentFocus();
            if (v != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            for (Fragment fragment1 : getFragmentManager().getFragments()) {
                fragmentTransaction.remove(fragment1);
            }
            fragmentTransaction.replace(R.id.body, fragment);
            fragmentTransaction.commit();
        }
    }
}


package com.chargeapp.whc.chargeapp.Control;

import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 1709008NB01 on 2018/2/22.
 */

public class InsertConsumeType extends Fragment {
    private ImageView mainImage, secondImage, resultI;
    private EditText mainName, secondName, secondKey;
    private Button save, clear;
    private RelativeLayout choiceL;
    private GridView choiceG;
    private TypeVO typeVO;
    private TypeDetailVO typeDetailVO;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private Object object;
    private String action;
    private boolean mainClick,secondClick;
    private TextView mainT,button;
    private Activity context;
    private AdView adView;

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
        View view = inflater.inflate(R.layout.updae_con_type, container, false);
        ((AppCompatActivity) context).getSupportActionBar().setDisplayShowCustomEnabled(false);
        Common.setChargeDB(context);
        typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        object = getArguments().getSerializable("object");
        action = (String) getArguments().getSerializable("action");
        mainClick=false;
        secondClick=false;
        findViewById(view);
        setGridPicture();
        if (Common.showsecondgrid) {
            setType();
            mainImage.setOnClickListener(null);
            context.setTitle("新增次項目類別");
        } else {
            typeVO = new TypeVO();
            context.setTitle("新增主/次項目類別");
            mainImage.setOnClickListener(new showImage());
        }
        secondImage.setOnClickListener(new showImage());
        choiceG.setOnItemClickListener(new choicePicture());
        clear.setOnClickListener(new clearOnClick());
        save.setOnClickListener(new insertType());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choiceL.setVisibility(View.GONE);
            }
        });
        return view;
    }

    private void setType() {
        String type = (object instanceof InvoiceVO) ? ((InvoiceVO) object).getMaintype() : ((ConsumeVO) object).getMaintype();
        typeVO = typeDB.findTypeName(type);
        mainImage.setImageResource(Download.imageAll[typeVO.getImage()]);
        mainName.setText(typeVO.getName().trim());
        mainName.setFocusable(false);
        mainName.setFocusableInTouchMode(false);
        mainName.setBackgroundColor(Color.parseColor("#DDDDDD"));
        mainT.setText(typeVO.getName());
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
        typeDetailVO = new TypeDetailVO();
        mainImage = view.findViewById(R.id.mainImage);
        secondImage = view.findViewById(R.id.secondImage);
        mainName = view.findViewById(R.id.mainName);
        secondName = view.findViewById(R.id.secondName);
        secondKey = view.findViewById(R.id.secondKey);
        save = view.findViewById(R.id.save);
        clear = view.findViewById(R.id.clear);
        choiceL = view.findViewById(R.id.choiceL);
        choiceG = view.findViewById(R.id.choiceG);
        mainT=view.findViewById(R.id.mainT);
        adView = view.findViewById(R.id.adView);
        button=view.findViewById(R.id.button);
        Common.setAdView(adView,context);
    }

    private class showImage implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choiceL.setVisibility(View.VISIBLE);
            resultI = (ImageView) view;
        }
    }

    private class choicePicture implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            resultI.setImageResource(Download.imageAll[i]);
            int id = resultI.getId();
            if (id == R.id.mainImage) {
                mainClick=true;
                typeVO.setImage(i);
            } else {
                secondClick=true;
                typeDetailVO.setImage(i);
            }
            choiceL.setVisibility(View.GONE);
        }
    }

    private class clearOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(Common.showsecondgrid)
            {

                secondImage.setImageResource(R.drawable.add);
                secondName.setText("");
                secondKey.setText("");
            }else{
                mainImage.setImageResource(R.drawable.add);
                secondImage.setImageResource(R.drawable.add);
                mainName.setText("");
                secondName.setText("");
                secondKey.setText("");
            }
        }
    }

    private class insertType implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String mainType = mainName.getText().toString();
            String secondTitle = secondName.getText().toString().trim();
            String keyWorld = secondKey.getText().toString().trim();
            if (mainType == null || mainType.isEmpty()) {
                mainName.setError("主項目不能空白");
                return;
            }
            if (secondTitle == null || secondTitle.isEmpty()) {
                secondName.setError("次項目不能空白");
                return;
            }
            if (keyWorld == null || keyWorld.isEmpty()) {
                secondKey.setError("關鍵字不能空白");
                return;
            }
            if(mainType.indexOf(";")!=-1)
            {
                mainName.setError("項目種類不能有特殊符號");
                return;
            }
            if (secondTitle.indexOf(";") !=-1) {
                secondName.setError("次項目不能有特殊符號");
                return;
            }

            TypeDetailVO TDO = typeDetailDB.findByname(secondTitle, mainType);
            if (TDO != null) {
                secondName.setError("新增次項目名稱不可重複");
                return;
            }

            if (!Common.showsecondgrid ) {
                TypeVO old = typeDB.findTypeName(mainType);
                if (old != null) {
                    mainName.setError("新增主項目名稱不可重複");
                    return;
                }
                typeVO.setName(mainType);
                typeVO.setGroupNumber(mainType);

                //沒有選擇圖片情況
                if(!mainClick)
                {
                    typeVO.setImage(0);
                }
                typeDB.insert(typeVO);
            }

            typeDetailVO.setGroupNumber(mainName.getText().toString().trim());
            typeDetailVO.setName(secondName.getText().toString().trim());
            typeDetailVO.setKeyword(secondKey.getText().toString().trim());

            //沒有選擇圖片情況
            if(!secondClick)
            {
                typeDetailVO.setImage(0);
            }

            typeDetailDB.insert(typeDetailVO);
            if (object instanceof InvoiceVO) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("invoiceVO", (InvoiceVO) object);
                returnThisFramgent(new UpdateInvoice(), bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putSerializable("consumeVO", (ConsumeVO) object);
                returnThisFramgent(new UpdateSpend(), bundle);
            }
            MainActivity.bundles.remove(MainActivity.bundles.size()-1);
            MainActivity.oldFramgent.remove(MainActivity.oldFramgent.size()-1);
            Common.showToast(context, "新增成功");
            View v =InsertConsumeType.this.context.getCurrentFocus();
            if (v != null) {
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    private void returnThisFramgent(Fragment fragment, Bundle bundle) {
        bundle.putSerializable("action", action);
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
            bundle.putSerializable("period", getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
            bundle.putSerializable("position", getArguments().getSerializable("position"));
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
        }else if(action.equals("InsertSpend"))
        {
            fragment=new InsertSpend();
            bundle.putSerializable("needSet", getArguments().getSerializable("needSet"));
        }else if (action.equals("SettingListFixCon")) {
            bundle.putSerializable("position", getArguments().getSerializable("position"));
        }else if (action.equals("SelectShowCircleDeList")) {
            bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
            bundle.putSerializable("ShowAllCarrier", getArguments().getSerializable("ShowAllCarrier"));
            bundle.putSerializable("noShowCarrier", getArguments().getSerializable("noShowCarrier"));
            bundle.putSerializable("year", getArguments().getSerializable("year"));
            bundle.putSerializable("month", getArguments().getSerializable("month"));
            bundle.putSerializable("day", getArguments().getSerializable("day"));
            bundle.putSerializable("key", getArguments().getSerializable("key"));
            bundle.putSerializable("carrier", getArguments().getSerializable("carrier"));
            bundle.putSerializable("statue", getArguments().getSerializable("statue"));
            bundle.putSerializable("period", getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            bundle.putStringArrayList("OKey", getArguments().getStringArrayList("OKey"));
        }else if(action.equals("InsertSpend"))
        {
            fragment=new InsertActivity();
            bundle=MainActivity.bundles.getLast();
        }else if(action.equals("HomePagetList"))
        {
            bundle.putSerializable("action","HomePagetList");
            bundle.putStringArrayList("OKey",getArguments().getStringArrayList("OKey"));
            bundle.putSerializable("position",0);
            bundle.putSerializable("key", getArguments().getSerializable("key"));
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

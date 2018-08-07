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

import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
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
import java.util.List;
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
    private boolean mainClick, secondClick;
    private TextView mainT, button,gridT;
    private Activity context;
    private AdView adView;
    private List<TypeVO> typeVOS;
    private boolean insertNewType;
    private boolean isTypeVO;
    private String type;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = (Activity) context;
        } else {
            this.context = getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context.setTitle("新增主/次項目類別");
        View view = inflater.inflate(R.layout.updae_con_type, container, false);
        ((AppCompatActivity) context).getSupportActionBar().setDisplayShowCustomEnabled(false);
        Common.setChargeDB(context);
        typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        object = getArguments().getSerializable("object");
        action = (String) getArguments().getSerializable("action");
        mainClick = false;
        secondClick = false;
        findViewById(view);
        if (Common.showsecondgrid) {
            setType();
        } else {
            typeVO = new TypeVO();
            mainImage.setOnClickListener(new showImage());
            setGridPicture();
        }
        return view;
    }

    private void setVoidOnClick()
    {
        secondImage.setOnClickListener(new showImage());
        choiceG.setOnItemClickListener(new choicePicture());
        clear.setOnClickListener(new clearOnClick());
        save.setOnClickListener(new insertType());
        button.setOnClickListener(new chioceGClose());
    }


    private void setType() {
        type = (object instanceof InvoiceVO) ? ((InvoiceVO) object).getMaintype() : ((ConsumeVO) object).getMaintype();
        typeVO = (TypeVO) getArguments().getSerializable("typeVO");
        if(typeVO==null)
        {
            typeVO = typeDB.findTypeName(type);
        }

        if(typeVO!=null)
        {
            insertNewType=false;
            TypeVO old=typeDB.findTypeName(typeVO.getName());
            if(old==null)
            {
                typeVO.setGroupNumber(typeVO.getGroupNumber().trim());
                typeVO.setName(typeVO.getName().trim());
                typeDB.update(typeVO);
            }
            mainImage.setImageResource(Download.imageAll[typeVO.getImage()]);
            mainName.setText(typeVO.getName().trim());
            mainName.setFocusable(false);
            mainName.setFocusableInTouchMode(false);
            mainName.setBackgroundColor(Color.parseColor("#DDDDDD"));
            mainT.setText(typeVO.getName().trim());
            mainImage.setOnClickListener(null);
            context.setTitle("新增次項目類別");
            setGridPicture();
        }else{
            typeVOS = typeDB.findLikeTypeName(type.trim());
            if(typeVOS.size()>0)
            {
                gridT.setText("查無主項目類別\n以下可能是相關的類別\n請做確認!");
                HashMap item;
                ArrayList items = new ArrayList<Map<String, Object>>();
                for (TypeVO t:typeVOS) {
                    item = new HashMap<String, Object>();
                    item.put("image",Download.imageAll[t.getImage()] );
                    item.put("text",t.getName());
                    items.add(item);
                }
                SimpleAdapter adapter = new SimpleAdapter(context,
                        items, R.layout.main_item, new String[]{"image", "text"},
                        new int[]{R.id.image, R.id.text});
                choiceG.setAdapter(adapter);
                choiceG.setNumColumns(4);
                choiceL.setVisibility(View.VISIBLE);
                choiceG.setOnItemClickListener(new choiceCurrentPicture());
                button.setOnClickListener(new InsertNewType());
            }else{
                context.setTitle("新增主/次項目類別");
                insertNewType=true;
                typeVO=new TypeVO();
                setGridPicture();
                mainImage.setOnClickListener(new showMainImage());
                mainImage.setImageResource(R.drawable.add);
                mainName.setText(type);
                mainName.setFocusable(true);
                mainName.setFocusableInTouchMode(true);
                mainName.setBackgroundColor(Color.parseColor("#FFEE99"));
                Common.showToast(context,"找不到主要類別，會新增此相關主要類別!");
            }
        }
    }

    private void setGridPicture() {
        gridT.setText("選擇圖片");
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
        setVoidOnClick();
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
        mainT = view.findViewById(R.id.mainT);
        adView = view.findViewById(R.id.adView);
        button = view.findViewById(R.id.button);
        gridT=view.findViewById(R.id.gridT);
        Common.setAdView(adView, context);
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
                mainClick = true;
                typeVO.setImage(i);
            } else {
                secondClick = true;
                typeDetailVO.setImage(i);
            }
            choiceL.setVisibility(View.GONE);
        }
    }

    private class clearOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (Common.showsecondgrid) {

                secondImage.setImageResource(R.drawable.add);
                secondName.setText("");
                secondKey.setText("");
            } else {
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
            String mainType = mainName.getText().toString().trim();
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
            if (mainType.indexOf(";") != -1) {
                mainName.setError("項目種類不能有特殊符號");
                return;
            }
            if (secondTitle.indexOf(";") != -1) {
                secondName.setError("次項目不能有特殊符號");
                return;
            }

            TypeDetailVO TDO = typeDetailDB.findByname(secondTitle, mainType);
            if (TDO != null) {
                secondName.setError("新增次項目名稱不可重複");
                return;
            }

            if (!Common.showsecondgrid||insertNewType) {
                TypeVO old = typeDB.findTypeName(mainType);
                if (old != null) {
                    mainName.setError("新增主項目名稱不可重複");
                    return;
                }
                typeVO.setName(mainType);
                typeVO.setGroupNumber(mainType);

                //沒有選擇圖片情況
                if (!mainClick) {
                    typeVO.setImage(0);
                }
                typeDB.insert(typeVO);
            }

            typeDetailVO.setGroupNumber(mainType);
            typeDetailVO.setName(secondTitle);
            typeDetailVO.setKeyword(keyWorld);

            //沒有選擇圖片情況
            if (!secondClick) {
                typeDetailVO.setImage(0);
            }

            typeDetailDB.insert(typeDetailVO);
            if (object instanceof InvoiceVO) {
                Bundle bundle = new Bundle();
                InvoiceVO invoiceVO= (InvoiceVO) object;
                invoiceVO.setMaintype(mainType);
                invoiceVO.setSecondtype(secondTitle);
                bundle.putSerializable("invoiceVO",invoiceVO);
                returnThisFramgent(new UpdateInvoice(), bundle);
            } else {
                Bundle bundle = new Bundle();
                ConsumeVO consumeVO= (ConsumeVO) object;
                consumeVO.setMaintype(mainType);
                consumeVO.setSecondType(secondTitle);
                bundle.putSerializable("consumeVO",consumeVO);
                returnThisFramgent(new UpdateSpend(), bundle);
            }
            MainActivity.bundles.remove(MainActivity.bundles.size() - 1);
            MainActivity.oldFramgent.remove(MainActivity.oldFramgent.size() - 1);
            Common.showToast(context, "新增成功");
            Common.clossKeyword(context);
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
        } else if (action.equals("InsertSpend")) {
            fragment = new InsertActivity();
            bundle.putSerializable("needSet", getArguments().getSerializable("needSet"));
        } else if (action.equals("SettingListFixCon")) {
            bundle.putSerializable("position", getArguments().getSerializable("position"));
        } else if (action.equals("SelectShowCircleDeList")) {
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
        } else if (action.equals("InsertSpend")) {
            fragment = new InsertActivity();
            bundle = MainActivity.bundles.getLast();
        } else if (action.equals("HomePagetList")) {
            bundle.putSerializable("action", "HomePagetList");
            bundle.putStringArrayList("OKey", getArguments().getStringArrayList("OKey"));
            bundle.putSerializable("position", 0);
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

    private class chioceGClose implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choiceL.setVisibility(View.GONE);
        }
    }

    private class choiceCurrentPicture implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            insertNewType=false;
            context.setTitle("新增次項目類別");
            TypeVO typeVO=typeVOS.get(i);
            ConsumeDB consumeDB=new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
            InvoiceDB invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
            List<TypeDetailVO> typeDetailVOS = typeDetailDB.findByGroupname(typeVO.getName());
            for (TypeDetailVO t : typeDetailVOS) {
                t.setGroupNumber(typeVO.getName().trim());
                typeDetailDB.update(t);
            }
            List<ConsumeVO> consumeVOS = consumeDB.getMainTypePeriod(typeVO.getName());
            for (ConsumeVO c : consumeVOS) {
                c.setMaintype(typeVO.getName().trim());
                consumeDB.update(c);
            }
            List<InvoiceVO> invoiceVOS = invoiceDB.getInvoiceMainType(typeVO.getName());
            for (InvoiceVO invoiceVO : invoiceVOS) {
                invoiceVO.setMaintype(typeVO.getName().trim());
                invoiceDB.update(invoiceVO);
            }

            typeVO.setGroupNumber(typeVO.getGroupNumber().trim());
            typeVO.setName(typeVO.getName().trim());
            typeDB.update(typeVO);

            mainName.setText(typeVO.getGroupNumber().trim());
            mainName.setFocusable(false);
            mainName.setFocusableInTouchMode(false);
            mainName.setBackgroundColor(Color.parseColor("#DDDDDD"));
            mainImage.setImageResource(Download.imageAll[typeVO.getImage()]);
            choiceL.setVisibility(View.GONE);
            setGridPicture();
        }
    }

    private class showMainImage implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            isTypeVO=true;
            choiceL.setVisibility(View.VISIBLE);
            resultI = (ImageView) view;
        }
    }

    private class InsertNewType implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            typeVO=new TypeVO();
            insertNewType=true;
            context.setTitle("新增主/次項目類別");
            setGridPicture();
            choiceL.setVisibility(View.GONE);
            mainImage.setOnClickListener(new showMainImage());
            mainImage.setImageResource(R.drawable.add);
            mainName.setText(type.trim());
            mainName.setFocusable(true);
            mainName.setFocusableInTouchMode(true);
            mainName.setBackgroundColor(Color.parseColor("#FFEE99"));
            Common.showToast(context,"找不到主要類別，會新增此相關主要類別!");
        }
    }
}

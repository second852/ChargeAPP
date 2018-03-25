package com.chargeapp.whc.chargeapp.Control;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
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

public class UpdateConsumeType extends Fragment {
    private ImageView mainImage, secondImage, resultI;
    private EditText mainName, secondName, secondKey;
    private Button save, clear;
    private LinearLayout choiceL;
    private GridView choiceG;
    private TypeDetailVO typeDetailVO;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.updae_con_type, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(false);
        typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        findViewById(view);
        setGridPicture();
        setTypeDetail();
        secondImage.setOnClickListener(new showImage());
        choiceG.setOnItemClickListener(new choicePicture());
        clear.setOnClickListener(new clearOnClick());
        save.setOnClickListener(new insertType());
        return view;
    }

    private void setTypeDetail() {
        typeDetailVO= (TypeDetailVO) getArguments().getSerializable("TypeDetailVO");
        TypeVO typeVO = typeDB.findTypeName(typeDetailVO.getGroupNumber());
        mainImage.setImageResource(Download.imageAll[typeVO.getImage()]);
        mainName.setText(typeVO.getName().trim());
        mainName.setFocusable(false);
        mainName.setFocusableInTouchMode(false);
        mainName.setBackgroundColor(Color.parseColor("#DDDDDD"));
        secondImage.setImageResource(Download.imageAll[typeDetailVO.getImage()]);
        secondName.setText(typeDetailVO.getName());
        secondKey.setText(typeDetailVO.getKeyword());
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
            typeDetailVO.setImage(i);
            choiceL.setVisibility(View.GONE);
        }
    }

    private class clearOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            secondImage.setImageResource(R.drawable.add);
            secondName.setText(" ");
            secondKey.setText(" ");
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

            typeDetailVO.setGroupNumber(mainName.getText().toString().trim());
            typeDetailVO.setName(secondName.getText().toString().trim());
            typeDetailVO.setKeyword(secondKey.getText().toString().trim());
            typeDetailDB.update(typeDetailVO);
            Bundle bundle=new Bundle();
            bundle.putSerializable("position",getArguments().getSerializable("position"));
            bundle.putSerializable("spinnerC",getArguments().getSerializable("spinnerC"));
            Fragment fragment=new SettingListType();
            fragment.setArguments(bundle);
            switchFramgent(fragment);
            Common.showToast(getActivity(), "修改成功");
        }
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

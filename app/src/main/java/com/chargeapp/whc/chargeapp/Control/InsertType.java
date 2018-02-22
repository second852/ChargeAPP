package com.chargeapp.whc.chargeapp.Control;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class InsertType extends Fragment {
    private ImageView mainImage,secondImage,resultI;
    private EditText mainName,secondName,secondKey;
    private Button save,clear;
    private LinearLayout choiceL;
    private GridView choiceG;
    private TypeVO typeVO;
    private TypeDetailVO typeDetailVO;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private InvoiceVO invoiceVO;
    private String action;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.updae_type, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(false);
        typeDB=new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB=new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceVO = (InvoiceVO) getArguments().getSerializable("invoiceVO");
        action= (String) getArguments().getSerializable("action");
        findViewById(view);
        setGridPicture();
        if(UpdateInvoice.showsecondgrid)
        {
            setType();
        }
        mainImage.setOnClickListener(new showImage());
        secondImage.setOnClickListener(new showImage());
        choiceG.setOnItemClickListener(new choicePicture());
        clear.setOnClickListener(new clearOnClick());
        save.setOnClickListener(new insertType());
        return view;
    }

    private void setType() {
        Log.d("TAG",invoiceVO.getMaintype());
        TypeVO typeVO=typeDB.findTypeName(invoiceVO.getMaintype());
        mainImage.setImageResource(MainActivity.imageAll[typeVO.getImage()]);
        mainName.setText(typeVO.getName().trim());
    }

    private void setGridPicture() {
        HashMap item;
        ArrayList items = new ArrayList<Map<String, Object>>();
        for(int i=0;i<MainActivity.imageAll.length;i++)
        {
            item = new HashMap<String, Object>();
            item.put("image", MainActivity.imageAll[i]);
            item.put("text"," ");
            items.add(item);
        }
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                items, R.layout.main_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        choiceG.setAdapter(adapter);
        choiceG.setNumColumns(4);
    }

    private void findViewById(View view) {
        typeDetailVO=new TypeDetailVO();
        typeVO=new TypeVO();
        mainImage=view.findViewById(R.id.mainImage);
        secondImage=view.findViewById(R.id.secondImage);
        mainName=view.findViewById(R.id.mainName);
        secondName=view.findViewById(R.id.secondName);
        secondKey=view.findViewById(R.id.secondKey);
        save=view.findViewById(R.id.save);
        clear=view.findViewById(R.id.clear);
        choiceL=view.findViewById(R.id.choiceL);
        choiceG=view.findViewById(R.id.choiceG);
    }

    private class showImage implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choiceL.setVisibility(View.VISIBLE);
            resultI= (ImageView) view;
        }
    }

    private class choicePicture implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            resultI.setImageResource(MainActivity.imageAll[i]);
            int id=resultI.getId();
            if(id==R.id.mainImage)
            {
                typeVO.setImage(i);
            }else {
                typeDetailVO.setImage(i);
            }
            choiceL.setVisibility(View.GONE);
        }
    }

    private class clearOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mainImage.setImageResource(R.drawable.add);
            secondImage.setImageResource(R.drawable.add);
            mainName.setText(" ");
            secondName.setText(" ");
            secondKey.setText(" ");
        }
    }

    private class insertType implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (mainName.getText().toString() == null || mainName.getText().toString().isEmpty()) {
                mainName.setError("主項目不能空白");
                return;
            }
            if (secondName.getText().toString().trim() == null || secondName.getText().toString().trim().isEmpty()) {
                secondName.setError("次項目不能空白");
                return;
            }
            if (secondKey.getText().toString().trim() == null || secondKey.getText().toString().trim().isEmpty()) {
                secondKey.setError("關鍵字不能空白");
                return;
            }
            typeDetailVO.setGroupNumber(mainName.getText().toString().trim());
            typeDetailVO.setName(secondName.getText().toString().trim());
            typeDetailVO.setKeyword(secondKey.getText().toString().trim());
            typeVO.setName(mainName.getText().toString().trim());
            typeVO.setGroupNumber(mainName.getText().toString().trim());
            typeDetailDB.insert(typeDetailVO);
            typeDB.insert(typeVO);
            returnThisFramgent(new UpdateInvoice());
            Common.showToast(getActivity(), "新增成功");
        }
    }
    private void returnThisFramgent(Fragment fragment)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable("invoiceVO",invoiceVO);
        bundle.putSerializable("action",action);
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
    public void switchFramgent(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 :  getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }
}

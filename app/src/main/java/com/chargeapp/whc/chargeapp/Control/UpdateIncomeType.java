package com.chargeapp.whc.chargeapp.Control;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuWrapperFactory;
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

import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
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
    private ImageView mainImage,resultI;
    private EditText mainName;
    private Button save, clear;
    private LinearLayout choiceL;
    private GridView choiceG;
    private String action;
    private BankTybeDB bankTybeDB;
    private BankTypeVO bankTypeVO;
    private TypeDB typeDB;
    private TypeVO typeVO;
    private TypeDetailDB typeDetailDB;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.updae_inc_type, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(false);
        action = (String) getArguments().getSerializable("action");
        bankTybeDB=new BankTybeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDB= new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB=new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        findViewById(view);
        setGridPicture();
        getActivity().setTitle("修改類別");
        choiceG.setOnItemClickListener(new choicePicture());
        clear.setOnClickListener(new clearOnClick());
        save.setOnClickListener(new insertType());
        mainImage.setOnClickListener(new showImage());
        if(action.equals("updateT"))
        {
            setTypeVO();
        }else {
            setBankVO();
        }
        return view;
    }

    private void setBankVO() {
        bankTypeVO= (BankTypeVO) getArguments().getSerializable("bankTypeVO");
        mainImage.setImageResource(MainActivity.imageAll[bankTypeVO.getImage()]);
        mainName.setText(bankTypeVO.getName());
    }

    private void setTypeVO() {
        typeVO= (TypeVO) getArguments().getSerializable("typeVO");
        mainImage.setImageResource(MainActivity.imageAll[typeVO.getImage()]);
        mainName.setText(typeVO.getName());
    }


    private void setGridPicture() {
        HashMap item;
        ArrayList items = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < MainActivity.imageAll.length; i++) {
            item = new HashMap<String, Object>();
            item.put("image", MainActivity.imageAll[i]);
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
            resultI.setImageResource(MainActivity.imageAll[i]);
            choiceL.setVisibility(View.GONE);
            if(action.equals("updateT"))
            {
                typeVO.setImage(i);
            }else {
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
            if(mainType.indexOf(";")!=-1)
            {
                mainName.setError("項目種類不能有特殊符號");
                return;
            }
            Bundle bundle=new Bundle();
            Fragment fragment=new SettingListType();
            if(action.equals("updateT"))
            {
                List<TypeDetailVO> typeDetailVOS=typeDetailDB.findByGroupname(typeVO.getName());
                for(TypeDetailVO t:typeDetailVOS)
                {
                    t.setGroupNumber(mainType);
                    typeDetailDB.update(t);
                }
                typeVO.setGroupNumber(mainType);
                typeVO.setName(mainType);
                typeDB.update(typeVO);
            }else {
                bankTypeVO.setGroupNumber(mainType);
                bankTypeVO.setName(mainType);
                bankTybeDB.update(bankTypeVO);
            }
            bundle.putSerializable("position",getArguments().getSerializable("position"));
            bundle.putSerializable("spinnerC",getArguments().getSerializable("spinnerC"));
            fragment.setArguments(bundle);
            switchFramgent(fragment);
            Common.showToast(getActivity(), "修改成功!");
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

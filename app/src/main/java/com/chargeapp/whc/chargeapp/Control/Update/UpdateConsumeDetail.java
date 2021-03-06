package com.chargeapp.whc.chargeapp.Control.Update;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.HomePage.HomePage;
import com.chargeapp.whc.chargeapp.Control.Insert.InsertActivity;
import com.chargeapp.whc.chargeapp.Control.Insert.InsertSpend;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Search.SearchMain;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.ui.ScanUpdateSpend;

import static com.chargeapp.whc.chargeapp.Control.Common.searchMainString;

/**
 * Created by 1709008NB01 on 2018/1/29.
 */

public class UpdateConsumeDetail extends Fragment {
    private BootstrapEditText detail,store,address;
    private String action;
    private ConsumeVO consumeVO;
    private Activity context;
    private BootstrapButton backP;
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


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.update_consume_detail, container, false);
        detail=view.findViewById(R.id.detail);
        store=view.findViewById(R.id.store);
        address=view.findViewById(R.id.address);
        backP=view.findViewById(R.id.backP);
        action= (String) getArguments().getSerializable("action");
        context.setTitle("細節");
        ConsumeVO consumeVO;
        if(action.equals("InsertSpend"))
        {
            consumeVO=InsertSpend.consumeVO;

        }else{

            if(getArguments().getSerializable("consumeVO")==null)
            {
                MainActivity.oldFramgent.clear();
                MainActivity.bundles.clear();
                switchFragment(new HomePage());
                Common.showToast(context,"資料遺失!請重新操作!");
                return view;
            }
            consumeVO= (ConsumeVO) getArguments().getSerializable("consumeVO");
            this.consumeVO=consumeVO;
        }

        detail.setText(consumeVO.getDetailname()==null?"":consumeVO.getDetailname());
        address.setText(consumeVO.getSellerAddress()==null?"":consumeVO.getSellerAddress());
        store.setText(consumeVO.getSellerName()==null?"":consumeVO.getSellerName());

        backP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.oldFramgent.remove(MainActivity.oldFramgent.size()-1);
                MainActivity.bundles.remove(MainActivity.bundles.size()-1);
                String result=detail.getText().toString().trim();
                String storeR=store.getText().toString().trim();
                String addressR=address.getText().toString().trim();

                switch (action)
                {
                    case "InsertSpend":
                        InsertSpend.consumeVO.setDetailname(result);
                        InsertSpend.consumeVO.setSellerAddress(addressR);
                        InsertSpend.consumeVO.setSellerName(storeR);
                        Fragment fragment=new InsertActivity();
                        switchFragment(fragment);
                        break;
                    case Common.scanUpdateSpend:
                        fragment=new ScanUpdateSpend();
                        fragment.setArguments(getArguments());
                        switchFragment(fragment);
                        MainActivity.bundles.removeLast();
                        MainActivity.oldFramgent.removeLast();
                        break;
                     default:
                         consumeVO.setDetailname(result);
                         consumeVO.setSellerAddress(addressR);
                         consumeVO.setSellerName(storeR);
                         returnThisFramgent(new UpdateSpend());
                         break;
                }
           }
         }
        );
        return view;
    }

    private void returnThisFramgent(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("object", consumeVO);
        bundle.putSerializable("consumeVO",consumeVO);
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
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            bundle.putSerializable("period", getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
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
        }else if (action.equals("SettingListFixCon")) {
            bundle.putSerializable("position", getArguments().getSerializable("position"));
        }else  if (action.equals("SelectShowCircleDeList")) {
            bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
            bundle.putSerializable("ShowAllCarrier", getArguments().getSerializable("ShowAllCarrier"));
            bundle.putSerializable("noShowCarrier", getArguments().getSerializable("noShowCarrier"));
            bundle.putSerializable("year", getArguments().getSerializable("year"));
            bundle.putSerializable("month", getArguments().getSerializable("month"));
            bundle.putSerializable("day", getArguments().getSerializable("day"));
            bundle.putSerializable("key", getArguments().getSerializable("key"));
            bundle.putSerializable("carrier", getArguments().getSerializable("carrier"));
            bundle.putSerializable("statue", getArguments().getSerializable("statue"));
            bundle.putSerializable("position", getArguments().getSerializable("position"));
            bundle.putSerializable("period", getArguments().getSerializable("period"));
            bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
            bundle.putStringArrayList("OKey",getArguments().getStringArrayList("OKey"));
        }else if(action.equals("HomePagetList"))
        {
            bundle.putSerializable("action","HomePagetList");
            bundle.putStringArrayList("OKey",getArguments().getStringArrayList("OKey"));
            bundle.putSerializable("position",0);
            bundle.putSerializable("key", getArguments().getSerializable("key"));
        }else if(action.equals(searchMainString))
        {
            bundle.putAll(getArguments());
        }
        fragment.setArguments(bundle);
        switchFragment(fragment);
    }


    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment f : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(f);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

}

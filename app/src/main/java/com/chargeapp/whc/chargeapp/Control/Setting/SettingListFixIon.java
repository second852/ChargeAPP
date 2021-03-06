package com.chargeapp.whc.chargeapp.Control.Setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.Adapter.DeleteDialogFragment;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateIncome;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SettingListFixIon extends Fragment {


    private ListView listView;
    public int p;
    private BankDB bankDB;
    private Gson gson;
    private TextView message;
    public List<BankVO> bankVOS;
    private BankVO bankVO;
    private Activity context;


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
        gson = new Gson();
        Common.setChargeDB(context);
        bankDB = new BankDB(MainActivity.chargeAPPDB);
        View view = inflater.inflate(R.layout.setting_main, container, false);
        bankVO = (BankVO) getArguments().getSerializable("bankVO");
        p = (int) getArguments().getSerializable("position");
        listView = view.findViewById(R.id.list);
        message = view.findViewById(R.id.message);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        bankVOS = bankDB.getAutoByFk(bankVO.getFkKey());
        setLayout();
    }

    public void setLayout() {


        ListAdapter adapter = (ListAdapter) listView.getAdapter();
        if (adapter == null) {
            listView.setAdapter(new ListAdapter(context, bankVOS));
        } else {
            adapter.setObjects(bankVOS);
            adapter.notifyDataSetChanged();
            listView.invalidate();
        }
        if (bankVOS.size() <= 0) {
            message.setText("無資料!");
            message.setVisibility(View.VISIBLE);
        }
        listView.setSelection(p);
    }


    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<BankVO> bankVOS;

        ListAdapter(Context context, List<BankVO> bankVOS) {
            this.context = context;
            this.bankVOS = bankVOS;
        }

        public void setObjects(List<BankVO> bankVOS) {
            this.bankVOS = bankVOS;
        }

        @Override
        public int getCount() {
            return bankVOS.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.select_con_detail_list_item, parent, false);
            }
            TextView title = itemView.findViewById(R.id.listTitle);
            TextView decribe = itemView.findViewById(R.id.listDetail);
            BootstrapButton fixT = itemView.findViewById(R.id.fixT);
            BootstrapButton remainT = itemView.findViewById(R.id.remainT);
            LinearLayout remindL = itemView.findViewById(R.id.remindL);
            LinearLayout fixL = itemView.findViewById(R.id.fixL);
            BootstrapButton update = itemView.findViewById(R.id.updateD);
            BootstrapButton deleteI = itemView.findViewById(R.id.deleteI);
            update.setText("修改");

            final BankVO bankVO = bankVOS.get(position);
            if (bankVO.isAuto()) {
                fixL.setVisibility(View.VISIBLE);
                fixT.setText("自動");
                fixT.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                remindL.setVisibility(View.GONE);
                remainT.setBootstrapBrand(DefaultBootstrapBrand.INFO);
            } else {
                remindL.setVisibility(View.GONE);
                fixL.setVisibility(View.VISIBLE);
                fixT.setText("設定檔");
                fixT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
            }
            //設定 title
            title.setText(Common.setBankTittlesTwo(bankVO));


            //設定 describe
            StringBuffer stringBuffer = new StringBuffer();
            JsonObject js = gson.fromJson(bankVO.getFixDateDetail(), JsonObject.class);
            String daystatue = js.get("choicestatue").getAsString().trim();
            stringBuffer.append(daystatue);
            if (!daystatue.equals("每天")) {
                stringBuffer.append(" " + js.get("choicedate").getAsString().trim());
            }
            decribe.setText(stringBuffer.toString() + " \n" + bankVO.getDetailname());
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new UpdateIncome();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bankVO", bankVO);
                    bundle.putSerializable("position", position);
                    bundle.putSerializable("action", "SettingListFixIon");
                    fragment.setArguments(bundle);
                    switchFragment(fragment);
                }
            });

            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    p=position;
                    DeleteDialogFragment aa = new DeleteDialogFragment();
                    aa.setObject(bankVO);
                    aa.setFragment(SettingListFixIon.this);
                    aa.show(getFragmentManager(), "show");
                }
            });
            return itemView;
        }

        @Override
        public BankVO getItem(int position) {
            return bankVOS.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }


    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("SettingListFixIon");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

}

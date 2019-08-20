package com.chargeapp.whc.chargeapp.Control.Setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.Adapter.DeleteDialogFragment;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyFromDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateSpend;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.PropertyFromVO;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SettingListFixProperty extends Fragment {


    private ListView listView;
    public int p;
    private PropertyFromDB propertyFromDB;
    private Gson gson;
    private TextView message;
    public List<PropertyFromVO> propertyFromVOS;
    public PropertyFromVO propertyFromVO;
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
        gson=new Gson();
        Common.setChargeDB(context);
        View view = inflater.inflate(R.layout.setting_main, container, false);
        propertyFromDB=new PropertyFromDB(MainActivity.chargeAPPDB.getReadableDatabase());
        Long id= (Long) getArguments().getSerializable(Common.propertyFromVoId);
        propertyFromVO=propertyFromDB.findByPropertyFromId(id);
        p= (int) getArguments().getSerializable("position");
        listView=view.findViewById(R.id.list);
        message=view.findViewById(R.id.message);
        Common.setAdView((AdView) view.findViewById(R.id.adView),context);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(propertyFromVO.getFixImport())
        {
            propertyFromVOS=propertyFromDB.findFixProperty(propertyFromVO.getId());

        }else{
            propertyFromVOS=propertyFromDB.findFixProperty(propertyFromVO.getFixFromId());
            propertyFromVO=propertyFromDB.findByPropertyFromId(propertyFromVO.getFixFromId());
        }
        propertyFromVOS.add(0,propertyFromVO);
        setLayout();
    }

    public void setLayout() {
        ListAdapter adapter = (ListAdapter) listView.getAdapter();
        if (adapter == null) {
            listView.setAdapter(new ListAdapter(context, propertyFromVOS));
        } else {
            adapter.setObjects(propertyFromVOS);
            adapter.notifyDataSetChanged();
            listView.invalidate();
        }
        if(propertyFromVOS.size()<=0)
        {
            message.setText("無資料!");
            message.setVisibility(View.VISIBLE);
        }
        listView.setSelection(p);
    }


    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<PropertyFromVO> propertyFromVOS;

        public ListAdapter(Context context, List<PropertyFromVO> propertyFromVOS) {
            this.context = context;
            this.propertyFromVOS = propertyFromVOS;
        }

        public void setObjects(List<PropertyFromVO> propertyFromVOS) {
            this.propertyFromVOS = propertyFromVOS;
        }

        @Override
        public int getCount() {
            return propertyFromVOS.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.select_con_detail_list_item, parent, false);
            }
            TextView title=itemView.findViewById(R.id.listTitle);
            TextView decribe=itemView.findViewById(R.id.listDetail);
            BootstrapButton fixT=itemView.findViewById(R.id.fixT);
            BootstrapButton remainT=itemView.findViewById(R.id.remainT);
            LinearLayout remindL=itemView.findViewById(R.id.remindL);
            final LinearLayout fixL=itemView.findViewById(R.id.fixL);
            BootstrapButton update=itemView.findViewById(R.id.updateD);
            BootstrapButton deleteI=itemView.findViewById(R.id.deleteI);
            update.setText("修改");
            final PropertyFromVO propertyFromVO=propertyFromVOS.get(position);

            StringBuilder titleString=new StringBuilder();

            if(!propertyFromVO.getFixImport())
            {
                fixL.setVisibility(View.VISIBLE);
                fixT.setText("子體");
                fixT.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                remindL.setVisibility(View.VISIBLE);
                remainT.setText("自動");
                remainT.setBootstrapBrand(DefaultBootstrapBrand.INFO);

                titleString.append(Common.sTwo.format(propertyFromVO.getSourceTime()));

            }else {
                remindL.setVisibility(View.GONE);
                fixT.setText("本體");
                fixT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                fixL.setVisibility(View.VISIBLE);
            }
            //設定 title


            //設定 describe
            StringBuilder stringBuilder=new StringBuilder();
            switch (propertyFromVO.getType())
            {
                case Negative:
                    titleString.append(" "+propertyFromVO.getSourceSecondType());
                    stringBuilder.append("1. 定期支出 : "+Common.Currency().get(propertyFromVO.getSourceCurrency())+" "+propertyFromVO.getSourceMoney()+"\n");
                    break;
                case Positive:
                    titleString.append(" "+propertyFromVO.getSourceMainType());
                    stringBuilder.append("1. 定期收入 : "+Common.Currency().get(propertyFromVO.getSourceCurrency())+" "+propertyFromVO.getSourceMoney()+"\n");
                    break;
            }
            stringBuilder.append("2. 時間 : "+propertyFromVO.getFixDateCode().getDetail()+propertyFromVO.getFixDateDetail());


            title.setText(titleString.toString());
            decribe.setText(stringBuilder.toString());



            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteDialogFragment aa= new DeleteDialogFragment();
                    p=position;
                    aa.setObject(propertyFromVO);
                    aa.setFragement(SettingListFixProperty.this);
                    aa.show(getFragmentManager(),"show");
                }
            });
            return itemView;
        }

        @Override
        public PropertyFromVO getItem(int position) {
            return propertyFromVOS.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }


    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("SettingListFixCon");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

}

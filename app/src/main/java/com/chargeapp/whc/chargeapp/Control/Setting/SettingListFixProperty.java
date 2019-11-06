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

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.Adapter.DeleteDialogFragment;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyFromDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyUpdateConsume;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyUpdateMoney;
import com.chargeapp.whc.chargeapp.Model.PropertyFromVO;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdView;


import org.jsoup.internal.StringUtil;

import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SettingListFixProperty extends Fragment {


    private ListView listView;
    public int p;
    private PropertyFromDB propertyFromDB;
    private TextView message;
    public List<PropertyFromVO> propertyFromVOS;
    public PropertyFromVO propertyFromVO;
    private Activity context;
    private PropertyDB propertyDB;
    private Bundle bundle;


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
        Common.setChargeDB(context);
        View view = inflater.inflate(R.layout.setting_main, container, false);
        propertyFromDB=new PropertyFromDB(MainActivity.chargeAPPDB);
        propertyDB=new PropertyDB(MainActivity.chargeAPPDB);
        bundle=getArguments();
        String id= (String) getArguments().getSerializable(Common.propertyFromVoId);
        propertyFromVO=propertyFromDB.findByPropertyFromId(id);
        p= (int) getArguments().getSerializable("position");
        listView=view.findViewById(R.id.list);
        message=view.findViewById(R.id.message);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        setLayout();
    }

    public void setLayout() {
        if(propertyFromVO.getFixImport())
        {
            propertyFromVOS=propertyFromDB.findFixProperty(propertyFromVO.getId());

        }else{
            propertyFromVOS=propertyFromDB.findFixProperty(propertyFromVO.getFixFromId());
            propertyFromVO=propertyFromDB.findByPropertyFromId(propertyFromVO.getFixFromId());
        }
        propertyFromVOS.add(0,propertyFromVO);
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
            LinearLayout remindL=itemView.findViewById(R.id.remindL);
            final LinearLayout fixL=itemView.findViewById(R.id.fixL);
            BootstrapButton update=itemView.findViewById(R.id.updateD);
            BootstrapButton deleteI=itemView.findViewById(R.id.deleteI);
            update.setText("修改");
            final PropertyFromVO propertyFromVO=propertyFromVOS.get(position);

            StringBuilder titleString=new StringBuilder();
            PropertyVO propertyVO=propertyDB.findById(propertyFromVO.getPropertyId());


            if(!propertyFromVO.getFixImport())
            {
                fixL.setVisibility(View.VISIBLE);
                fixT.setText("自動");
                fixT.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                remindL.setVisibility(View.GONE);
                titleString.append(Common.sTwo.format(propertyFromVO.getSourceTime()));

            }else {
                remindL.setVisibility(View.GONE);
                fixT.setText("設定檔");
                fixT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                fixL.setVisibility(View.VISIBLE);
            }
            //設定 title


            //設定 describe
            int index=1;
            String currency=Common.Currency().get(propertyFromVO.getSourceCurrency());
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append((index++)+". 資產 : "+propertyVO.getName()+"\n");
            switch (propertyFromVO.getType())
            {
                case Negative:
                    titleString.append(" "+propertyFromVO.getSourceSecondType());
                    stringBuilder.append((index++)+". 定期支出 : "+currency +" "+propertyFromVO.getSourceMoney()+"\n");
                    break;
                case Positive:
                    titleString.append(" "+propertyFromVO.getSourceMainType());
                    stringBuilder.append((index++)+". 定期收入 : "+ currency+" "+propertyFromVO.getSourceMoney()+"\n");
                    break;
            }

            if(propertyFromVO.getFixFromId()!=null&&!StringUtil.isBlank(propertyFromVO.getImportFee()))
            {
                Double fee=Double.valueOf(propertyFromVO.getImportFee());
                if(fee>0)
                {
                    stringBuilder.append((index++)+". 手續費 : "+currency+propertyFromVO.getImportFee()+"\n");
                }
            }

            stringBuilder.append((index++)+". 時間 : "+propertyFromVO.getFixDateCode().getDetail());

            if(!StringUtil.isBlank(propertyFromVO.getFixDateDetail()))
            {
                stringBuilder.append(propertyFromVO.getFixDateDetail());
            }



            title.setText(titleString.toString());
            decribe.setText(stringBuilder.toString());


            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment=null;
                    bundle.putSerializable(Common.propertyFromVoId,propertyFromVO.getId());
                    switch (propertyFromVO.getType())
                    {
                        case Negative:
                          fragment=new PropertyUpdateConsume();
                             break;
                        case Positive:
                           fragment=new PropertyUpdateMoney();
                            break;
                    }
                    bundle.putSerializable(Common.fragment, Common.settingListFixPropertyString);
                    fragment.setArguments(bundle);
                    Common.switchFragment(fragment, Common.settingListFixPropertyString,getFragmentManager());
                }
            });


            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteDialogFragment aa= new DeleteDialogFragment();
                    p=position;
                    aa.setObject(propertyFromVO);
                    aa.setFragment(SettingListFixProperty.this);
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



}

package com.chargeapp.whc.chargeapp.Control.Setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;

import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;

import com.chargeapp.whc.chargeapp.ChargeDB.PropertyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyFromDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.PropertyFromVO;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.TypeCode.PropertyType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import org.jsoup.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SettingListFix extends Fragment {


    private ListView listView;
    private Spinner typeH;
    private ArrayList<Object> objects;
    private BankDB bankDB;
    private ConsumeDB consumeDB;
    private PropertyFromDB propertyFromDB;
    private PropertyDB propertyDB;
    private Gson gson;
    private TextView message;
    public static int p;
    public static int spinnerC;
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
        context.setTitle("設定定期項目");
        View view = inflater.inflate(R.layout.setting_list, container, false);
        Common.setChargeDB(context);
        bankDB=new BankDB(MainActivity.chargeAPPDB);
        consumeDB=new ConsumeDB(MainActivity.chargeAPPDB);
        propertyFromDB=new PropertyFromDB(MainActivity.chargeAPPDB);
        propertyDB=new PropertyDB(MainActivity.chargeAPPDB);
        listView = view.findViewById(R.id.list);
        typeH = view.findViewById(R.id.typeH);
        message=view.findViewById(R.id.message);
        typeH.setOnItemSelectedListener(new choiceType());
        setSpinner();
        setLayout();
        return view;
    }

    private void setSpinner() {
        ArrayList<String> spinnerItem = new ArrayList<>();
        spinnerItem.add("  定期支出  ");
        spinnerItem.add("  定期收入  ");
        spinnerItem.add("定期資產收入");
        spinnerItem.add("定期資產支出");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinneritem, spinnerItem);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        typeH.setAdapter(arrayAdapter);
        typeH.setSelection(spinnerC);
    }

    public void setLayout() {
        objects=new ArrayList<>();

        switch(spinnerC)
        {
            case 0:
                objects.addAll(consumeDB.getFixdate());
                break;
            case 1:
                objects.addAll(bankDB.getFixDate());
                break;
            case 2:
                objects.addAll(propertyFromDB.getFixAll(PropertyType.Positive));
                break;
            case 3:
                objects.addAll(propertyFromDB.getFixAll(PropertyType.Negative));
                break;
        }



        ListAdapter adapter = (ListAdapter) listView.getAdapter();
        if (adapter == null) {
            listView.setAdapter(new ListAdapter(context, objects));
        } else {
            adapter.setObjects(objects);
            adapter.notifyDataSetChanged();
            listView.invalidate();
        }
        if(objects.size()<=0)
        {
            message.setText("無資料!");
            message.setVisibility(View.VISIBLE);
        }else{
            message.setVisibility(View.GONE);
            listView.setSelection(p);
        }
    }


    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<Object> objects;

        ListAdapter(Context context, List<Object> objects) {
            this.context = context;
            this.objects = objects;
        }

        public void setObjects(List<Object> objects) {
            this.objects = objects;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.select_con_detail_list_item, parent, false);
            }
            TextView title=itemView.findViewById(R.id.listTitle);
            TextView decribe=itemView.findViewById(R.id.listDetail);
            LinearLayout remindL=itemView.findViewById(R.id.remindL);
            LinearLayout fixL=itemView.findViewById(R.id.fixL);
            BootstrapButton update=itemView.findViewById(R.id.updateD);
            BootstrapButton deleteI=itemView.findViewById(R.id.deleteI);
            fixL.setVisibility(View.VISIBLE);
            deleteI.setVisibility(View.GONE);
            update.setText("檢視");
            final Object o=objects.get(position);
            if(o instanceof ConsumeVO)
            {
                final ConsumeVO consumeVO= (ConsumeVO) o;

                //設定tittle
                title.setText(Common.setSecConsumerTittlesTwo(consumeVO));


                //設定 此筆detail
                StringBuffer stringBuffer=new StringBuffer();
                //設定標籤
                fixL.setVisibility(View.VISIBLE);
                if(consumeVO.getNotify().equals("true"))
                {
                    remindL.setVisibility(View.VISIBLE);
                }else {
                    remindL.setVisibility(View.GONE);
                }
                JsonObject js=gson.fromJson(consumeVO.getFixDateDetail(),JsonObject.class);
                stringBuffer.append(js.get("choicestatue").getAsString().trim());
                stringBuffer.append(" "+js.get("choicedate").getAsString().trim());
                boolean noweek= Boolean.parseBoolean(js.get("noweek").getAsString());
                if(js.get("choicestatue").getAsString().trim().equals("每天")&&noweek)
                {
                    stringBuffer.append(" 假日除外");
                }
                decribe.setText(stringBuffer.toString()+" \n"+(consumeVO.getDetailname()==null?"":consumeVO.getDetailname()));
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       Fragment fragment=new SettingListFixCon();
                       Bundle bundle=new Bundle();
                       bundle.putSerializable("consumeVO",consumeVO);
                       bundle.putSerializable("position",0);
                       fragment.setArguments(bundle);
                       switchFragment(fragment);
                    }
                });

            }else if(o instanceof  BankVO){
                //設定標記
                fixL.setVisibility(View.VISIBLE);
                remindL.setVisibility(View.GONE);

                final BankVO bankVO= (BankVO) o;
                //設定 tittle
                title.setText(Common.setBankTittlesDay(bankVO));
                //descide
                StringBuffer stringBuffer=new StringBuffer();
                JsonObject js=gson.fromJson(bankVO.getFixDateDetail(),JsonObject.class);
                String choiceStatue=js.get("choicestatue").getAsString().trim();
                stringBuffer.append(choiceStatue);
                if(!choiceStatue.equals("每天"))
                {
                    stringBuffer.append(" "+js.get("choicedate").getAsString().trim());
                }
                decribe.setText(stringBuffer.toString()+" \n"+bankVO.getDetailname());
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment fragment=new SettingListFixIon();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("bankVO",bankVO);
                        bundle.putSerializable("position",0);
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });
            }else if(o instanceof PropertyFromVO)
            {
                fixL.setVisibility(View.VISIBLE);
                remindL.setVisibility(View.GONE);
                final PropertyFromVO propertyFromVO= (PropertyFromVO) o;
                PropertyVO propertyVO=propertyDB.findById(propertyFromVO.getPropertyId());
                title.setText(propertyVO.getName());


                StringBuilder stringBuilder=new StringBuilder();
                int index=1;
                String currency=Common.Currency().get(propertyFromVO.getSourceCurrency());
                switch (propertyFromVO.getType())
                {
                    case Negative:
                        stringBuilder.append((index++)+". 項目 : "+propertyFromVO.getSourceSecondType()+"\n");
                        stringBuilder.append((index++)+". 定期支出 : "+currency +" "+propertyFromVO.getSourceMoney()+"\n");
                        break;
                    case Positive:
                        stringBuilder.append((index++)+". 項目 : "+propertyFromVO.getSourceMainType()+"\n");
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



                decribe.setText(stringBuilder.toString());
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment fragment=new SettingListFixProperty();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable(Common.propertyFromVoId,propertyFromVO.getId());
                        bundle.putSerializable("position",0);
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });

            }
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }


    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("SettingListFix");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

    private class choiceType implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            spinnerC = i;
            SettingListFix.this.setLayout();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}

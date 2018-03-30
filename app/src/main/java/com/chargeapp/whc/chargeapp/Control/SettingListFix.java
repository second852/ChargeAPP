package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
    private Gson gson;
    private TextView message;
    public static int p;
    public static int spinnerC;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gson=new Gson();
        View view = inflater.inflate(R.layout.setting_list, container, false);
        bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB=new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        listView = view.findViewById(R.id.list);
        typeH = view.findViewById(R.id.typeH);
        message=view.findViewById(R.id.message);
        AdView adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        typeH.setOnItemSelectedListener(new choiceType());
        setSpinner();
        setLayout();
        return view;
    }

    private void setSpinner() {
        ArrayList<String> spinnerItem = new ArrayList<>();
        spinnerItem.add("定期支出");
        spinnerItem.add("定期收入");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinneritem, spinnerItem);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        typeH.setAdapter(arrayAdapter);
        typeH.setSelection(spinnerC);
    }

    public void setLayout() {
        objects=new ArrayList<>();
        if(spinnerC==0)
        {
            objects.addAll(consumeDB.getFixdate());
        }else{
            objects.addAll(bankDB.getFixDate());
        }
        ListAdapter adapter = (ListAdapter) listView.getAdapter();
        if (adapter == null) {
            listView.setAdapter(new ListAdapter(getActivity(), objects));
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
            TextView remainT=itemView.findViewById(R.id.remainT);
            TextView fixT=itemView.findViewById(R.id.fixT);
            LinearLayout fixL=itemView.findViewById(R.id.fixL);
            Button update=itemView.findViewById(R.id.updateD);
            Button deleteI=itemView.findViewById(R.id.deleteI);
            fixL.setVisibility(View.VISIBLE);
            deleteI.setVisibility(View.GONE);
            update.setText("檢視");
            final Object o=objects.get(position);
            StringBuffer stringBuffer = new StringBuffer();
            if(o instanceof ConsumeVO)
            {
                final ConsumeVO consumeVO= (ConsumeVO) o;
                stringBuffer.append(Common.sTwo.format(consumeVO.getDate()));
                stringBuffer.append(" "+consumeVO.getMaintype());
                stringBuffer.append("\n共"+consumeVO.getMoney()+"元");
                title.setText(stringBuffer.toString());
                stringBuffer=new StringBuffer();
                remindL.setVisibility(View.VISIBLE);
                remainT.setText("固定");
                remainT.setTextColor(Color.parseColor("#0000FF"));
                remindL.setBackgroundColor(Color.parseColor("#0000FF"));
                if(consumeVO.getNotify().equals("true"))
                {
                    fixL.setVisibility(View.VISIBLE);
                    fixT.setText("提醒");
                    fixT.setTextColor(Color.parseColor("#CC0000"));
                    fixL.setBackgroundColor(Color.parseColor("#CC0000"));
                }else {
                    fixL.setVisibility(View.GONE);
                }
                JsonObject js=gson.fromJson(consumeVO.getFixDateDetail(),JsonObject.class);
                stringBuffer.append(js.get("choicestatue").getAsString().trim());
                stringBuffer.append(" "+js.get("choicedate").getAsString().trim());
                boolean noweek= Boolean.parseBoolean(js.get("noweek").getAsString());
                if(js.get("choicestatue").getAsString().trim().equals("每天")&&noweek)
                {
                    stringBuffer.append(" 假日除外");
                }
                decribe.setText(stringBuffer.toString()+" \n"+consumeVO.getDetailname());
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

            }else{
                remindL.setVisibility(View.VISIBLE);
                remainT.setText("固定");
                remainT.setTextColor(Color.parseColor("#0000FF"));
                fixL.setVisibility(View.GONE);
                final BankVO bankVO= (BankVO) o;
                stringBuffer.append(Common.sTwo.format(bankVO.getDate()));
                stringBuffer.append(" "+bankVO.getMaintype());
                stringBuffer.append("\n共"+bankVO.getMoney()+"元");
                title.setText(stringBuffer.toString());
                //descide

                stringBuffer=new StringBuffer();
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
                        bundle.putSerializable("BankVO",bankVO);
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

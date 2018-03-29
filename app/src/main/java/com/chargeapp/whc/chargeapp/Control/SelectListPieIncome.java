package com.chargeapp.whc.chargeapp.Control;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectListPieIncome extends Fragment {

    //    private LineChart lineChart;
    private ListView listView;
    private int year, month,day;
    private HashMap<String, Integer> main;
    private List<Object> objects;
    private ProgressDialog progressDialog;
    private Calendar start, end;
    private int statue;
    private String key;
    private BankDB bankDB;
    private ArrayList<String> Okey;
    private TextView message;
    private String title;
    private Gson gson;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_con_detail, container, false);
        findViewById(view);
        gson =new Gson();
        main = new HashMap<>();
        bankDB = new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        progressDialog = new ProgressDialog(getActivity());
        year = (int) getArguments().getSerializable("year");
        month = (int) getArguments().getSerializable("month");
        key = (String) getArguments().getSerializable("type");
        statue = (int) getArguments().getSerializable("statue");
        day=(int) getArguments().getSerializable("day");
        Okey = getArguments().getStringArrayList("OKey");
        if (statue == 0) {
            start = new GregorianCalendar(year, month, 1, 0, 0, 0);
            end = new GregorianCalendar(year, month, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23,59, 59);
            title = Common.sThree.format(new Date(start.getTimeInMillis()));
        } else {
            Calendar calendar = new GregorianCalendar(year, month, 1, 0, 0, 0);
            start = new GregorianCalendar(calendar.get(Calendar.YEAR), 0, 1, 0, 0, 0);
            end = new GregorianCalendar(calendar.get(Calendar.YEAR), 11, 31, 23, 59, 59);
            title = Common.sFour.format(new Date(start.getTimeInMillis()));
        }
        getActivity().setTitle(title);
        setLayout();
        return view;
    }


    public void setLayout() {
        List<BankVO> bankVOS;
        if (key.equals("其他")) {
            bankVOS = new ArrayList<>();
            for (String s : Okey) {
                bankVOS.addAll(bankDB.getTimeAll(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), s));
            }
        } else {
            bankVOS = bankDB.getTimeAll(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), key);
        }

        ListAdapter baseAdapter = (ListAdapter) listView.getAdapter();
        if (baseAdapter == null) {
            listView.setAdapter(new ListAdapter(getActivity(), bankVOS));
        } else {
            baseAdapter.setBankVOs(bankVOS);
            baseAdapter.notifyDataSetChanged();
            listView.invalidate();
        }
        if (bankVOS.size() <= 0) {
            message.setVisibility(View.VISIBLE);
            message.setText(title + "\n" + key + "總類沒有資料");
        }else{
            message.setVisibility(View.GONE);
        }
    }

    private void findViewById(View view) {
        listView = view.findViewById(R.id.listCircle);
        message = view.findViewById(R.id.message);
        AdView adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<BankVO> bankVOS;

        ListAdapter(Context context, List<BankVO> bankVOS) {
            this.context = context;
            this.bankVOS = bankVOS;
        }


        public void setBankVOs(List<BankVO> bankVOS) {
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
            final BankVO bankVO = bankVOS.get(position);
            TextView title = itemView.findViewById(R.id.listTitle);
            TextView decribe = itemView.findViewById(R.id.listDetail);
            Button update = itemView.findViewById(R.id.updateD);
            Button deleteI = itemView.findViewById(R.id.deleteI);
            TextView remainT = itemView.findViewById(R.id.remainT);
            LinearLayout remindL = itemView.findViewById(R.id.remindL);
            LinearLayout typeL=itemView.findViewById(R.id.typeL);
            TextView typeT=itemView.findViewById(R.id.typeT);

            StringBuffer stringBuffer = new StringBuffer();
            if (bankVO.isAuto()) {
                remindL.setVisibility(View.VISIBLE);
                remainT.setText("自動");
                remainT.setTextColor(Color.parseColor("#7700BB"));
                remindL.setBackgroundColor(Color.parseColor("#7700BB"));
            } else {
                remindL.setVisibility(View.GONE);
            }

            //設定 title
            stringBuffer.append(Common.sTwo.format(bankVO.getDate()));
            stringBuffer.append(" "+bankVO.getMaintype());
            stringBuffer.append("\n共"+bankVO.getMoney()+"元");
            title.setText(stringBuffer.toString());

            //設定 describe
            if (bankVO.getFixDate().equals("true")||bankVO.isAuto()) {

                typeL.setVisibility(View.VISIBLE);
                typeT.setText("固定");
                typeT.setTextColor(Color.parseColor("#008844"));
                typeL.setBackgroundColor(Color.parseColor("#008844"));

                stringBuffer=new StringBuffer();
                JsonObject js=gson.fromJson(bankVO.getFixDateDetail(),JsonObject.class);
                String daystatue=js.get("choicestatue").getAsString().trim();
                stringBuffer.append(daystatue);
                if(!daystatue.equals("每天"))
                {
                    stringBuffer.append(" "+js.get("choicedate").getAsString().trim());
                }
                decribe.setText(stringBuffer.toString()+" \n"+bankVO.getDetailname());
            } else {
                typeL.setVisibility(View.GONE);
                decribe.setText(stringBuffer.toString());
            }

            update.setText("修改");
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bankVO", bankVO);
                    bundle.putSerializable("year", year);
                    bundle.putSerializable("month", month);
                    bundle.putSerializable("day", day);
                    bundle.putSerializable("statue", statue);
                    bundle.putSerializable("type", key);
                    bundle.putStringArrayList("OKey", Okey);
                    bundle.putSerializable("position", position);
                    bundle.putSerializable("action", "SelectListPieIncome");
                    Fragment fragment = new UpdateIncome();
                    fragment.setArguments(bundle);
                    switchFragment(fragment);
                }
            });
            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteDialogFragment aa = new DeleteDialogFragment();
                    aa.setObject(bankVO);
                    aa.setFragement(SelectListPieIncome.this);
                    aa.show(getFragmentManager(), "show");
                }
            });
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
        MainActivity.oldFramgent.add("SelectListPieIncome");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

}

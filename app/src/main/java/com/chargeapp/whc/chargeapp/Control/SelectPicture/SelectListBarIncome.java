package com.chargeapp.whc.chargeapp.Control.SelectPicture;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Adapter.DeleteDialogFragment;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateIncome;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.R;

import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectListBarIncome extends Fragment {

    //    private LineChart lineChart;
    private ListView listView;
    private int year,month,day;
    private Calendar start,end;
    private int statue;
    private BankDB bankDB;
    private int index;
    private TextView message;
    private String title;
    private Gson gson;
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
        Utils.init(this.context);
    }





    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_con_detail, container, false);
        ((AppCompatActivity)context).getSupportActionBar().setDisplayShowCustomEnabled(false);
        gson=new Gson();
        findViewById(view);
        Common.setChargeDB(context);
        bankDB=new BankDB(MainActivity.chargeAPPDB);
        year= (int) getArguments().getSerializable("year");
        month= (int) getArguments().getSerializable("month");
        day= (int) getArguments().getSerializable("day");
        statue= (int) getArguments().getSerializable("statue");
        index= (int) getArguments().getSerializable("index");
        if(statue==0) {
            start = new GregorianCalendar(year, month, 1+index, 0, 0, 0);
            end=new GregorianCalendar(year,month,1+index,23,59,59);
            title =Common.sOne.format(new Date(start.getTimeInMillis()));
        }else{
            start = new GregorianCalendar(year, month+index, 1, 0, 0, 0);
            end=new GregorianCalendar(year,month+index,start.getActualMaximum(Calendar.DAY_OF_MONTH),23,59,59);
            title =Common.sThree.format(new Date(start.getTimeInMillis()));
        }
        context.setTitle(title);
        setLayout();
        return view;
    }




    public void setLayout()
    {
        List<BankVO>  bankVOS=bankDB.getTimeAll(start.getTimeInMillis(),end.getTimeInMillis());
        ListAdapter baseAdapter= (ListAdapter) listView.getAdapter();
        if(baseAdapter==null)
        {
            listView.setAdapter(new ListAdapter(context,bankVOS));
        }else{
            baseAdapter.setBankVOs(bankVOS);
            baseAdapter.notifyDataSetChanged();
            listView.invalidate();
        }
        if(bankVOS.size()<=0)
        {
            message.setVisibility(View.VISIBLE);
            message.setText(title+"\n沒有資料");
        }else{
            message.setVisibility(View.GONE);
        }
    }

    private void findViewById(View view) {
        listView=view.findViewById(R.id.listCircle);
        message=view.findViewById(R.id.message);
    }



    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<BankVO> bankVOS;

        ListAdapter(Context context,List<BankVO> bankVOS) {
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
            final BankVO bankVO=bankVOS.get(position);
            TextView title=itemView.findViewById(R.id.listTitle);
            TextView decribe=itemView.findViewById(R.id.listDetail);
            BootstrapButton update=itemView.findViewById(R.id.updateD);
            BootstrapButton deleteI=itemView.findViewById(R.id.deleteI);
            BootstrapButton fixT = itemView.findViewById(R.id.fixT);
            LinearLayout fixL = itemView.findViewById(R.id.fixL);
            LinearLayout remindL=itemView.findViewById(R.id.remindL);
            LinearLayout typeL=itemView.findViewById(R.id.typeL);

            //設定標籤
            typeL.setVisibility(View.GONE);
            remindL.setVisibility(View.GONE);
            fixL.setVisibility(View.GONE);



            //設定 title
            title.setText(Common.setBankTittlesDay(bankVO));
            //設定 describe
            StringBuilder stringBuffer = new StringBuilder();
            if (bankVO.isAuto()) {
                fixT.setText("自動");
                fixT.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                fixL.setVisibility(View.VISIBLE);
                 try {
                     JsonObject js = gson.fromJson(bankVO.getFixDateDetail(), JsonObject.class);
                     String daystatue = js.get("choicestatue").getAsString().trim();
                     stringBuffer.append(daystatue);
                     if (!daystatue.equals("每天")) {
                         stringBuffer.append(" " + js.get("choicedate").getAsString().trim());
                     }
                 }catch (Exception e)
                 {
                     stringBuffer.append(" ");
                 }

                stringBuffer.append("\n");
            }

            if (bankVO.getFixDate()!=null&&bankVO.getFixDate().equals("true")) {

                fixT.setText("固定");
                fixT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                fixL.setVisibility(View.VISIBLE);
                try {
                    JsonObject js = gson.fromJson(bankVO.getFixDateDetail(), JsonObject.class);
                    String daystatue = js.get("choicestatue").getAsString().trim();
                    stringBuffer.append(daystatue);
                    if (!daystatue.equals("每天")) {
                        stringBuffer.append(" " + js.get("choicedate").getAsString().trim());
                    }
                }catch (Exception e)
                {
                    stringBuffer.append(" ");
                }
                stringBuffer.append("\n");
            }
            stringBuffer.append(bankVO.getDetailname());
            if(stringBuffer.indexOf("\n")==-1)
            {
                stringBuffer.append("\n");
            }
            decribe.setText(stringBuffer.toString());

            update.setText("修改");
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      Bundle bundle=new Bundle();
                      bundle.putSerializable("bankVO",bankVO);
                      bundle.putSerializable("year",year);
                      bundle.putSerializable("month",month);
                      bundle.putSerializable("day",day);
                      bundle.putSerializable("statue",statue);
                      bundle.putSerializable("index",index);
                      bundle.putSerializable("action","SelectListBarIncome");
                      Fragment fragment=new UpdateIncome();
                      fragment.setArguments(bundle);
                      switchFragment(fragment);
                }
            });
            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteDialogFragment aa = new DeleteDialogFragment();
                    aa.setObject(bankVO);
                    aa.setFragment(SelectListBarIncome.this);
                    aa.show(getFragmentManager(), "show");
                }
            });
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return bankVOS.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }



    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("SelectListBarIncome");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 :  getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

}

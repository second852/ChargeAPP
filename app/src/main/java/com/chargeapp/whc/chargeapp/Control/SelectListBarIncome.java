package com.chargeapp.whc.chargeapp.Control;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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






    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_con_detail, container, false);
        findViewById(view);
        bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        year= (int) getArguments().getSerializable("year");
        month= (int) getArguments().getSerializable("month");
        day= (int) getArguments().getSerializable("day");
        statue= (int) getArguments().getSerializable("statue");
        index= (int) getArguments().getSerializable("index");
        String title;
        if(statue==0) {
            start = new GregorianCalendar(year, month, 1+index, 0, 0, 0);
            end=new GregorianCalendar(year,month,1+index,23,59,59);
            title=Common.sOne.format(new Date(start.getTimeInMillis()));
        }else{
            start = new GregorianCalendar(year, month+index, 1, 0, 0, 0);
            end=new GregorianCalendar(year,month+index,start.getActualMaximum(Calendar.DAY_OF_MONTH),23,59,59);
            title=Common.sThree.format(new Date(start.getTimeInMillis()));
        }
        getActivity().setTitle(title);
        setLayout();
        return view;
    }




    public void setLayout()
    {

        List<BankVO>  bankVOS=bankDB.getTimeAll(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
        ListAdapter baseAdapter= (ListAdapter) listView.getAdapter();
        if(baseAdapter==null)
        {
            listView.setAdapter(new ListAdapter(getActivity(),bankVOS));
        }else{
            baseAdapter.setBankVOs(bankVOS);
            baseAdapter.notifyDataSetChanged();
            listView.invalidate();
        }
    }

    private void findViewById(View view) {
        listView=view.findViewById(R.id.listCircle);
    }



    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<BankVO> bankVOS;

        ListAdapter(Context context,List<BankVO> bankVOS) {
            this.context = context;
            this.bankVOS = bankVOS;
        }

        public List<BankVO> getBankVOs() {
            return bankVOS;
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
            StringBuffer buffer=new StringBuffer();
            TextView title=itemView.findViewById(R.id.listTitle);
            TextView decribe=itemView.findViewById(R.id.listDetail);
            Button update=itemView.findViewById(R.id.updateD);
            Button deleteI=itemView.findViewById(R.id.deleteI);
            buffer.append(Common.sDay.format(bankVO.getDate())+" ");
            buffer.append(bankVO.getMaintype()+" "+bankVO.getMoney());
            title.setText(buffer.toString());
            decribe.setText(bankVO.getDetailname());
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
                      bundle.putSerializable("type","bar");
                      Fragment fragment=new UpdateIncome();
                      fragment.setArguments(bundle);
                      switchFragment(fragment);
                }
            });
            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bankDB.deleteById(bankVO.getId());
                    setLayout();
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
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 :  getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

}

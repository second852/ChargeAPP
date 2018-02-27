package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.solver.Goal;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class GoalListAll extends Fragment {


    private ListView listView;
    private GoalDB goalDB;
    private int p;
    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private BankDB bankDB;
    private Button addGoal;
    private Boolean complete=true;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goal_list, container, false);
        goalDB = new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB=new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        findViewById(view);
        setLayout();
        return view;
    }


    public void setLayout() {

        List<GoalVO> goalVOS = goalDB.getAll();
        ListAdapter adapter = (ListAdapter) listView.getAdapter();
        if (adapter == null) {
            adapter = new ListAdapter(getActivity(), goalVOS);
            listView.setAdapter(adapter);
        } else {
            adapter.setGoalVOS(goalVOS);
            adapter.notifyDataSetChanged();
            listView.invalidate();
        }
        if(goalVOS.size()<=1)
        {
            addGoal.setVisibility(View.VISIBLE);
            return;
        }
    }

    private void findViewById(View view) {
        listView = view.findViewById(R.id.list);
        addGoal=view.findViewById(R.id.addGoal);
    }


    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<GoalVO> goalVOS;

        ListAdapter(Context context, List<GoalVO> goalVOS) {
            this.context = context;
            this.goalVOS = goalVOS;
        }

        public void setGoalVOS(List<GoalVO> goalVOS) {
            this.goalVOS = goalVOS;
        }

        @Override
        public int getCount() {
            return goalVOS.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.select_con_detail_list_item, parent, false);
            }
            final GoalVO goalVO = goalVOS.get(position);
            StringBuffer sb = new StringBuffer();
            TextView title = itemView.findViewById(R.id.listTitle);
            TextView decribe = itemView.findViewById(R.id.listDetail);
            LinearLayout remindL = itemView.findViewById(R.id.remindL);
            LinearLayout fixL = itemView.findViewById(R.id.fixL);
            TextView fixT=itemView.findViewById(R.id.fixT);
            Button update = itemView.findViewById(R.id.updateD);
            Button deleteI = itemView.findViewById(R.id.deleteI);
            fixL.setVisibility(View.VISIBLE);
            title.setText(goalVO.getName());
            String timeDec = goalVO.getTimeStatue();
            fixT.setText("未完成");
            if (timeDec.equals("今日")&&goalVO.getStatue()==0) {
                timeDec = Common.sTwo.format(goalVO.getStartTime()).trim() + " ~ " + Common.sTwo.format(goalVO.getEndTime()).trim()+"\n";
                if(goalVO.getEndTime().getTime()> System.currentTimeMillis())
                {
                    int Itotal=invoiceDB.getTotalBytime(new Timestamp(goalVO.getStartTime().getTime()),new Timestamp(goalVO.getEndTime().getTime()));
                    int Ctotal=consumeDB.getTimeTotal(new Timestamp(goalVO.getStartTime().getTime()),new Timestamp(goalVO.getEndTime().getTime()));
                    int Btotal=bankDB.getTimeTotal(new Timestamp(goalVO.getStartTime().getTime()),new Timestamp(goalVO.getEndTime().getTime()));
                    int amout=Btotal-Ctotal-Itotal;
                    if(amout>Integer.valueOf(goalVO.getMoney()))
                    {
                      complete=true;
                      goalVO.setStatue(2);
                    }else{
                       complete=false;
                       goalVO.setStatue(1);
                    }
                    goalDB.update(goalVO);
                 }else{
                   complete=false;
                }
            }



            if(goalVO.getStatue()==1)
            {
                update.setVisibility(View.GONE);
                fixT.setText("失敗");
                fixT.setTextColor(Color.parseColor("#7700FF"));
                fixL.setBackgroundColor(Color.parseColor("#7700FF"));
            }else if(goalVO.getStatue()==2){
                update.setVisibility(View.GONE);
                fixT.setText("完成");
                fixT.setTextColor(Color.parseColor("#FF8800"));
                fixL.setBackgroundColor(Color.parseColor("#FF8800"));
            }else {
                fixT.setText("進行中");
            }


            if (goalVO.isNotify()) {
                remindL.setVisibility(View.VISIBLE);
            }

            sb.append(" " + timeDec);
            sb.append(goalVO.getType());
            sb.append(goalVO.getMoney() + " 元");
            decribe.setText(sb.toString());
            update.setText("修改");
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    p = position;
                    Bundle bundle = new Bundle();
                    Fragment fragment = new UpdateIncome();
                    fragment.setArguments(bundle);
                    switchFragment(fragment);
                }
            });
            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteDialogFragment aa = new DeleteDialogFragment();
                    aa.setObject(goalVO);
                    aa.setFragement(GoalListAll.this);
                    aa.show(getFragmentManager(), "show");
                }
            });
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return goalVOS.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }


    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

}

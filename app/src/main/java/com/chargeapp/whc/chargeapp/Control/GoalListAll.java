package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.solver.Goal;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
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
    private TextView message;





    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goal_list, container, false);
        goalDB=new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        findViewById(view);
        setLayout();
        return view;
    }




    public void setLayout()
    {
        List<GoalVO> goalVOS=goalDB.getAll();
        message.setVisibility(View.GONE);
        ListAdapter adapter= (ListAdapter) listView.getAdapter();
        if(adapter==null)
        {
          adapter=new ListAdapter(getActivity(),goalVOS);
          listView.setAdapter(adapter);
        }else{
            adapter.setGoalVOS(goalVOS);
            adapter.notifyDataSetChanged();
            listView.invalidate();
        }
        if(goalVOS.size()<=0)
        {
            message.setVisibility(View.VISIBLE);
            message.setText("無資料");
            return;
        }
    }

    private void findViewById(View view) {
        listView=view.findViewById(R.id.list);
        message=view.findViewById(R.id.message);
    }






    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<GoalVO> goalVOS;

        ListAdapter(Context context,List<GoalVO> goalVOS) {
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
            final GoalVO goalVO=goalVOS.get(position);
            TextView title=itemView.findViewById(R.id.listTitle);
            TextView decribe=itemView.findViewById(R.id.listDetail);
            title.setText(goalVO.getName());
            decribe.append(goalVO.getTimeStatue());
            Button update=itemView.findViewById(R.id.updateD);
            Button deleteI=itemView.findViewById(R.id.deleteI);
            update.setText("修改");
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      p=position;
                      Bundle bundle=new Bundle();
                      Fragment fragment=new UpdateIncome();
                      fragment.setArguments(bundle);
                      switchFragment(fragment);
                }
            });
            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteDialogFragment aa= new DeleteDialogFragment();
                    aa.setObject(goalVO);
                    aa.setFragement(GoalListAll.this);
                    aa.show(getFragmentManager(),"show");
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
        for (Fragment fragment1 :  getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

}

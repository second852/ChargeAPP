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
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.R;

import java.sql.Timestamp;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class GoalListAll extends Fragment {


    private ListView listView;
    private GoalDB goalDB;
    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private BankDB bankDB;
    private ImageView addGoal;
    private boolean goalSaveComplete;
    private boolean goalConsumeComplete;
    private TextView message;
    private int p;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goal_list, container, false);
        goalDB = new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankDB = new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        p= (int) getArguments().getSerializable("position");
        findViewById(view);
        setLayout();
        return view;
    }




    public void setLayout() {
        goalSaveComplete = true;
        goalConsumeComplete = true;
        List<GoalVO> goalVOS = goalDB.getAll();
        if(goalVOS.size()<=0)
        {
            message.setText("無目標紀錄!\n 請按右下角圖片新增!");
            message.setVisibility(View.VISIBLE);
        }else{
            message.setVisibility(View.GONE);
        }
        for (GoalVO g : goalVOS) {
            if (g.getTimeStatue().trim().equals("今日") && g.getStatue() == 0) {
                if (g.getEndTime().getTime() < System.currentTimeMillis()) {
                    int Itotal = invoiceDB.getTotalBytime(new Timestamp(g.getStartTime().getTime()), new Timestamp(g.getEndTime().getTime()));
                    int Ctotal = consumeDB.getTimeTotal(new Timestamp(g.getStartTime().getTime()), new Timestamp(g.getEndTime().getTime()));
                    int Btotal = bankDB.getTimeTotal(new Timestamp(g.getStartTime().getTime()), new Timestamp(g.getEndTime().getTime()));
                    int amout = Btotal - Ctotal - Itotal;
                    if (amout > Integer.valueOf(g.getMoney())) {
                        g.setStatue(2);
                    } else {
                        g.setStatue(1);
                    }
                    goalDB.update(g);
                }
            }

            if (g.getType().trim().equals("支出")) {
                goalConsumeComplete = false;
            } else {
                if (g.getStatue() == 0) {
                    goalSaveComplete = false;
                } else {
                    goalSaveComplete = true;
                }
            }
        }

        ListAdapter adapter = (ListAdapter) listView.getAdapter();
        if (adapter == null) {
            adapter = new ListAdapter(getActivity(), goalVOS);
            listView.setAdapter(adapter);
        } else {
            adapter.setGoalVOS(goalVOS);
            adapter.notifyDataSetChanged();
            listView.invalidate();
        }
        listView.setSelection(p);
        if (goalConsumeComplete) {
            addGoal.setVisibility(View.VISIBLE);
            addGoal.setOnClickListener(new addNewGoalClick());
        } else if (goalSaveComplete) {
            addGoal.setVisibility(View.VISIBLE);
            addGoal.setOnClickListener(new addNewGoalClick());
        } else {
            addGoal.setVisibility(View.GONE);
            addGoal.setOnClickListener(null);
        }

    }


    private void findViewById(View view) {
        listView = view.findViewById(R.id.list);
        addGoal = view.findViewById(R.id.addGoal);
        message=view.findViewById(R.id.message);
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
            TextView fixT = itemView.findViewById(R.id.fixT);
            Button update = itemView.findViewById(R.id.updateD);
            Button deleteI = itemView.findViewById(R.id.deleteI);
            fixL.setVisibility(View.VISIBLE);
            title.setText(goalVO.getName());
            String timeDec = goalVO.getTimeStatue().trim();

            if (timeDec.equals("今日")) {
                timeDec = Common.sTwo.format(goalVO.getStartTime()).trim() + " ~ " + Common.sTwo.format(goalVO.getEndTime()).trim() + "\n";
            }
            if (goalVO.isNotify()) {
                remindL.setVisibility(View.VISIBLE);
            }else{
                remindL.setVisibility(View.GONE);
            }

            boolean updateGoal;
            if (goalVO.getStatue() == 1) {
                fixT.setText("失敗");
                fixT.setTextColor(Color.parseColor("#7700FF"));
                fixL.setBackgroundColor(Color.parseColor("#7700FF"));
                updateGoal=false;
            } else if (goalVO.getStatue() == 2) {
                fixT.setText("完成");
                fixT.setTextColor(Color.parseColor("#FF8800"));
                fixL.setBackgroundColor(Color.parseColor("#FF8800"));
                updateGoal=false;
            } else {
                fixT.setTextColor(Color.parseColor("#0000FF"));
                fixL.setBackgroundColor(Color.parseColor("#0000FF"));
                fixT.setText("進行中");
                updateGoal=true;
            }




            sb.append(" " + timeDec);
            sb.append(goalVO.getType());
            sb.append(goalVO.getMoney() + " 元");
            decribe.setText(sb.toString());
            if(updateGoal)
            {
                update.setVisibility(View.VISIBLE);
                update.setBackgroundColor(Color.parseColor("#33CCFF"));
                update.setText("修改");
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        Fragment fragment = new GoalUpdate();
                        bundle.putSerializable("goalVO",goalVO);
                        bundle.putSerializable("position",position);
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });
            }else{
               update.setVisibility(View.INVISIBLE);
            }
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

    private class addNewGoalClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Fragment fragment = new GoalInsert();
            Bundle bundle=new Bundle();
            if(goalConsumeComplete&&goalSaveComplete){
                bundle.putSerializable("action","all");
            } else if (goalConsumeComplete) {
                bundle.putSerializable("action","Consume");
            } else if (goalSaveComplete) {
                bundle.putSerializable("action","Save");
            }
            fragment.setArguments(bundle);
            switchFragment(fragment);
        }
    }
}

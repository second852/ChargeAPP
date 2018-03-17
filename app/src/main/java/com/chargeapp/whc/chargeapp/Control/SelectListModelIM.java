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
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectListModelIM extends Fragment {

    private ImageView DRadd, DRcut;
    private ListView listView;
    public static int year, month, p;
    private Calendar start, end;
    private BankDB bankDB;
    private TextView DRcarrier;
    private TextView message;
    private Gson gson;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gson = new Gson();
        View view = inflater.inflate(R.layout.select_con_list, container, false);
        findViewById(view);
        bankDB = new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        setLayout();
        return view;
    }


    public void setLayout() {
        start = new GregorianCalendar(year, month, 1, 0, 0, 0);
        end = new GregorianCalendar(year, month, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        List<BankVO> bankVOS = bankDB.getTimeAll(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
        String title = Common.sThree.format(new Date(start.getTimeInMillis()));
        DRcarrier.setText(title);
        if (bankVOS.size() <= 0) {
            message.setVisibility(View.VISIBLE);
            message.setText(title + "\n無資料");
        }else{
            message.setVisibility(View.GONE);
        }

        ListAdapter baseAdapter = (ListAdapter) listView.getAdapter();
        if (baseAdapter == null) {
            listView.setAdapter(new ListAdapter(getActivity(), bankVOS));
        } else {
            baseAdapter.setBankVOs(bankVOS);
            baseAdapter.notifyDataSetChanged();
            listView.invalidate();
        }
        listView.setSelection(p);
    }

    private void findViewById(View view) {
        listView = view.findViewById(R.id.list);
        DRcarrier = view.findViewById(R.id.DRcarrier);
        DRadd = view.findViewById(R.id.DRadd);
        DRcut = view.findViewById(R.id.DRcut);
        message = view.findViewById(R.id.message);
        DRadd.setOnClickListener(new addOnClick());
        DRcut.setOnClickListener(new cutOnClick());
    }


    private class cutOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            p = 0;
            month = month - 1;
            if (month < 0) {
                month = 11;
                year = year - 1;
            }
            setLayout();
        }
    }

    private class addOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            p = 0;
            month = month + 1;
            if (month > 11) {
                month = 0;
                year = year + 1;
            }
            setLayout();
        }
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
            LinearLayout fixL = itemView.findViewById(R.id.fixL);


            StringBuffer stringBuffer = new StringBuffer();
            if (bankVO.isAuto()) {
                remindL.setVisibility(View.VISIBLE);
                remainT.setText("自動");
                remainT.setTextColor(Color.parseColor("#EE7700"));
                remindL.setBackgroundColor(Color.parseColor("#EE7700"));
            } else {
                remindL.setVisibility(View.GONE);
            }

            //設定 title
            stringBuffer.append(Common.sTwo.format(bankVO.getDate()));
            stringBuffer.append(" " + bankVO.getMaintype());
            stringBuffer.append("\n共" + bankVO.getMoney() + "元");
            title.setText(stringBuffer.toString());

            //設定 describe
            stringBuffer = new StringBuffer();
            if (bankVO.getFixDate().equals("true")&&bankVO.isAuto()) {
                fixL.setVisibility(View.VISIBLE);
                JsonObject js = gson.fromJson(bankVO.getFixDateDetail(), JsonObject.class);
                String daystatue = js.get("choicestatue").getAsString().trim();
                stringBuffer.append(daystatue);
                if (!daystatue.equals("每天")) {
                    stringBuffer.append(" " + js.get("choicedate").getAsString().trim());
                }
                decribe.setText(stringBuffer.toString() + " \n" + bankVO.getDetailname());
            } else {
                fixL.setVisibility(View.GONE);
                decribe.setText(bankVO.getDetailname());
            }


            update.setText("修改");
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    p = position;
                    SelectListModelActivity.page = 3;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bankVO", bankVO);
                    bundle.putSerializable("action", "SelectListModelIM");
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
                    aa.setFragement(SelectListModelIM.this);
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
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

}

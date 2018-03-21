package com.chargeapp.whc.chargeapp.Control;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class EleDonate extends Fragment {

    private TextView  message;
    private ListView listinviuce;
    private InvoiceDB invoiceDB;
    private CarrierDB carrierDB;
    private List<CarrierVO> carrierVOList;
    public static CarrierVO carrierVO;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
    private RelativeLayout showmonth, searchRL,choice;
    public static int choiceca = 0;
    private ProgressDialog progressDialog;
    public static HashMap<String, InvoiceVO> donateMap;
    private Button choiceall, save, cancel;
    private boolean sellall = false;
    private EditText inputH;
    private ImageView searchI;
    private ListView heartyList;
    public static String teamNumber, teamTitle;
    private Button returnSH;
    private ProgressBar progressbar;
    private RelativeLayout modelR;
    private Spinner choiceModel;
    public static AsyncTask get1=null;
    private List<CarrierVO> carrierVOS;
    private List<InvoiceVO> invoiceVOList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ele_setdenote, container, false);
        progressDialog = new ProgressDialog(getActivity());
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        donateMap = new HashMap<>();
        findviewbyid(view);
        choiceall.setOnClickListener(new choiceallchecked());
        cancel.setOnClickListener(new cancelallchecked());
        save.setOnClickListener(new uploadheraty());
        searchI.setOnClickListener(new searchHeartyTeam());
        returnSH.setOnClickListener(new retrinDonateM());
        carrierVOS=carrierDB.getAll();
        if (carrierVOList == null || carrierVOList.size() <= 0) {
            message.setText("請新增載具!");
            message.setVisibility(View.VISIBLE);
            listinviuce.setVisibility(View.GONE);
            showmonth.setVisibility(View.GONE);
            choice.setVisibility(View.GONE);
            return view;
        }
        ArrayList<String> SpinnerItem = new ArrayList<>();
        for(CarrierVO c:carrierVOS)
        {
            SpinnerItem.add(c.getCarNul());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinneritem, SpinnerItem);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        choiceModel.setAdapter(arrayAdapter);
        choiceModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choiceca=position;
                setlayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }



    public void cancelDialog() {

        progressDialog.cancel();
        progressbar.setVisibility(View.GONE);
    }


    public void setlistTeam(String jsonin) {
        cancelDialog();
        try {

            if (jsonin.indexOf("details") != -1) {
                Gson gson = new Gson();
                JsonObject jFS = gson.fromJson(jsonin, JsonObject.class);
                String jFSDT = jFS.get("details").toString();
                Type cdType = new TypeToken<List<JsonObject>>() {
                }.getType();
                List<JsonObject> jSS = gson.fromJson(jFSDT, cdType);
                heartyList.setAdapter(new HeartyAdapter(getActivity(), jSS));
            } else {
                Common.showToast(getActivity(), "查無資料");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setlayout() {
        cancelDialog();
        carrierVO = carrierVOList.get(choiceca);
        invoiceVOList = invoiceDB.getCarrierDoAll(carrierVO.getCarNul());
        if (invoiceVOList == null || invoiceVOList.size() <= 0) {
            message.setText("目前沒有可捐贈發票!");
            message.setVisibility(View.VISIBLE);
            listinviuce.setVisibility(View.GONE);
            showmonth.setVisibility(View.VISIBLE);
        }else{
            message.setVisibility(View.GONE);
            listinviuce.setVisibility(View.VISIBLE);
            showmonth.setVisibility(View.VISIBLE);
        }
        ListAdapter adapter= (ListAdapter) listinviuce.getAdapter();
        if(adapter==null)
        {
            listinviuce.setAdapter(new ListAdapter(getActivity(), invoiceVOList));
        }else{
            adapter.setObjects(invoiceVOList);
            adapter.notifyDataSetChanged();
            listinviuce.invalidate();
        }
    }


    private void findviewbyid(View view) {
        listinviuce = view.findViewById(R.id.recyclenul);
        message = view.findViewById(R.id.message);
        showmonth = view.findViewById(R.id.DRshow);
        choiceall = view.findViewById(R.id.choiceall);
        save = view.findViewById(R.id.save);
        cancel = view.findViewById(R.id.cancel);
        inputH = view.findViewById(R.id.inputH);
        searchI = view.findViewById(R.id.searchI);
        searchRL = view.findViewById(R.id.searchRL);
        heartyList = view.findViewById(R.id.heartyList);
        returnSH = view.findViewById(R.id.returnSH);
        progressbar = view.findViewById(R.id.progressbar);
        choice=view.findViewById(R.id.choice);
        modelR=view.findViewById(R.id.modelR);
        choiceModel=view.findViewById(R.id.choiceModel);
    }





    private class InvoiceAdapter extends
            RecyclerView.Adapter<InvoiceAdapter.MyViewHolder> {
        private Context context;
        private List<InvoiceVO> invoiceVOList;


        InvoiceAdapter(Context context, List<InvoiceVO> memberList) {
            this.context = context;
            this.invoiceVOList = memberList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView day, nul, amount;
            CheckBox checkdonate;

            MyViewHolder(View itemView) {
                super(itemView);
                checkdonate = itemView.findViewById(R.id.DRdate);
                day = itemView.findViewById(R.id.QrCodeA);
                nul = itemView.findViewById(R.id.DRamout);
                amount = itemView.findViewById(R.id.eleamount);
            }
        }

        @Override
        public int getItemCount() {
            return invoiceVOList.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.ele_setdenote_item, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, int position) {
            final InvoiceVO invoiceVO = invoiceVOList.get(position);
            viewHolder.day.setText(sf.format(new Date(invoiceVO.getTime().getTime())));
            viewHolder.nul.setText(invoiceVO.getInvNum());
            String amout = "NT$" + invoiceVO.getAmount();
            viewHolder.amount.setText(String.format(amout));
            viewHolder.checkdonate.setChecked(sellall);
            viewHolder.checkdonate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isChecked()) {
                        donateMap.put(invoiceVO.getInvNum(), invoiceVO);
                    } else {
                        donateMap.remove(invoiceVO.getInvNum());
                    }
                }
            });

        }
    }




    private class choiceallchecked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            sellall = true;
            for (InvoiceVO invoiceVO : invoiceVOList) {
                donateMap.put(invoiceVO.getInvNum(), invoiceVO);
            }
            listinviuce.invalidate();
        }
    }

    private class cancelallchecked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            sellall = false;
            donateMap.clear();
            listinviuce.invalidate();
        }
    }

    private class uploadheraty implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (donateMap.size() == 0) {
                Common.showToast(getActivity(), "請勾選要捐獻發票");
                return;
            }
            searchRL.setVisibility(View.VISIBLE);
        }
    }

    private class searchHeartyTeam implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new GetSQLDate(EleDonate.this).execute("searchHeartyTeam", inputH.getText().toString());
            progressbar.setVisibility(View.VISIBLE);
        }
    }

    private class HeartyAdapter extends BaseAdapter {
        Context context;
        List<JsonObject> teamlist;

        HeartyAdapter(Context context, List<JsonObject> teamlist) {
            this.context = context;
            this.teamlist = teamlist;
        }

        @Override
        public int getCount() {
            return teamlist.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.ele_main_item, parent, false);
            }
            final JsonObject team = teamlist.get(position);
            String teamName = "";
            try {
                teamName = team.get("SocialWelfareName").getAsString();
            } catch (NullPointerException e) {
                teamName = team.get("LoveCode").getAsString();
            }
            TextView tvId = (TextView) itemView.findViewById(R.id.tvId);
            tvId.setTextSize(20);
            tvId.setText(teamName);
            tvId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchRL.setVisibility(View.GONE);
                    teamTitle = team.get("SocialWelfareName").getAsString();
                    teamNumber = team.get("SocialWelfareBAN").getAsString();
                    AlertDialogFragment aa= new AlertDialogFragment();
                    aa.setObject(EleDonate.this);
                    aa.show(getFragmentManager(),"show");
                    progressDialog.setMessage("正在上傳資料,請稍候...");
                    progressDialog.show();
                }
            });
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return teamlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    private class retrinDonateM implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            searchRL.setVisibility(View.GONE);
        }
    }


    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<InvoiceVO> invoiceVOS;

        ListAdapter(Context context, List<InvoiceVO> invoiceVOS) {
            this.context = context;
            this.invoiceVOS = invoiceVOS;
        }


        public void setObjects(List<InvoiceVO> invoiceVOS) {
            this.invoiceVOS = invoiceVOS;
        }

        @Override
        public int getCount() {
            return invoiceVOS.size();
        }

        @Override
        public Object getItem(int position) {
            return invoiceVOS.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.ele_setdenote_record_item, parent, false);
            }
            TextView Title = itemView.findViewById(R.id.listTitle);
            TextView describe = itemView.findViewById(R.id.listDetail);
            LinearLayout remindL = itemView.findViewById(R.id.remindL);
            TextView remainT = itemView.findViewById(R.id.remainT);
            LinearLayout fixL = itemView.findViewById(R.id.fixL);
            TextView fixT = itemView.findViewById(R.id.fixT);
            LinearLayout donateL = itemView.findViewById(R.id.donateL);
            InvoiceVO invoiceVO = invoiceVOS.get(position);
            Title.setText(Common.sTwo.format(new Date(invoiceVO.getTime().getTime())) + " " + invoiceVO.getInvNum());
            describe.setText(invoiceVO.getHeartyteam());
            return itemView;
        }
    }

}


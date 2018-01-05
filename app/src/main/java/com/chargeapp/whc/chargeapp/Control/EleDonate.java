package com.chargeapp.whc.chargeapp.Control;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class EleDonate extends Fragment {

    private TextView carrier, message;
    private ImageView add, cut;
    private RecyclerView listinviuce;
    private InvoiceDB invoiceDB;
    private CarrierDB carrierDB;
    private List<CarrierVO> carrierVOList;
    public static CarrierVO carrierVO;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
    private RelativeLayout showmonth, searchRL;
    public static int choiceca = 0;
    private ProgressDialog progressDialog;
    public static HashMap<String, InvoiceVO> donateMap;
    private Button choiceall, save, cancel;
    private List<InvoiceVO> invoiceVOList;
    private boolean sellall = false;
    private EditText inputH;
    private ImageView searchI;
    private ListView heartyList;
    public static String teamNumber, teamTitle;
    private Button returnSH;
    private ProgressBar progressbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ele_setdenote, container, false);
        progressDialog = new ProgressDialog(getActivity());
        donateMap = new HashMap<>();
        findviewbyid(view);
        download();
        add.setOnClickListener(new addOnClick());
        cut.setOnClickListener(new cutOnClick());
        choiceall.setOnClickListener(new choiceallchecked());
        cancel.setOnClickListener(new cancelallchecked());
        save.setOnClickListener(new uploadheraty());
        searchI.setOnClickListener(new searchHeartyTeam());
        returnSH.setOnClickListener(new retrinDonateM());
        return view;
    }

    private void download() {

        carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());

//        invoiceDB.deleteBytime(Timestamp.valueOf("2017-12-17 00:00:00"));
//        setlayout();
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        invoiceVOList=invoiceDB.getAll();
        for(InvoiceVO i:invoiceVOList)
        {
            Log.d("XXXXXXXXX",sf.format(i.getTime())+" : "+i.getInvDonatable()+" :"+ i.getDonateMark());
        }
        carrierVOList = carrierDB.getAll();
        if (carrierVOList == null || carrierVOList.size() <= 0) {
            message.setText("請新增載具!");
            message.setVisibility(View.VISIBLE);
            listinviuce.setVisibility(View.GONE);
            showmonth.setVisibility(View.GONE);
            return;
        }
        new GetSQLDate(this).execute("GetToday");
        progressDialog.setMessage("正在更新資料,請稍候...");
        progressDialog.show();
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
        EleDonateMain.goneMoney.setVisibility(View.VISIBLE);
        carrierVO = carrierVOList.get(choiceca);
        listinviuce.removeAllViews();
        invoiceVOList = invoiceDB.getCarrierDoAll(carrierVO.getCarNul());
        carrier.setText(carrierVO.getCarNul());
        if (invoiceVOList == null || invoiceVOList.size() <= 0) {
            message.setText("目前沒有可捐贈發票!");
            message.setVisibility(View.VISIBLE);
            listinviuce.setVisibility(View.GONE);
            showmonth.setVisibility(View.VISIBLE);
            return;
        }
        message.setVisibility(View.GONE);
        listinviuce.setVisibility(View.VISIBLE);
        showmonth.setVisibility(View.VISIBLE);
        listinviuce.setLayoutManager(new LinearLayoutManager(getActivity()));
        listinviuce.setAdapter(new InvoiceAdapter(getActivity(), invoiceVOList));
    }


    private void findviewbyid(View view) {
        carrier = view.findViewById(R.id.DRcarrier);
        add = view.findViewById(R.id.DRadd);
        cut = view.findViewById(R.id.DRcut);
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
                day = itemView.findViewById(R.id.DRnul);
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


    private class addOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choiceca++;
            if (choiceca > (carrierVOList.size() - 1)) {
                choiceca = 0;
            }
            setlayout();
        }
    }

    private class cutOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            choiceca--;
            if (choiceca < 0) {
                choiceca = carrierVOList.size() - 1;
            }
            setlayout();
        }
    }


    private class choiceallchecked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            sellall = true;
            for (InvoiceVO invoiceVO : invoiceVOList) {
                donateMap.put(invoiceVO.getInvNum(), invoiceVO);
            }
            listinviuce.setAdapter(new InvoiceAdapter(getActivity(), invoiceVOList));
        }
    }

    private class cancelallchecked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            sellall = false;
            donateMap.clear();
            listinviuce.setAdapter(new InvoiceAdapter(getActivity(), invoiceVOList));
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

}


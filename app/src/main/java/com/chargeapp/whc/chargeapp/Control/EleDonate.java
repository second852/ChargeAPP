package com.chargeapp.whc.chargeapp.Control;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
    private  CarrierDB carrierDB;
    public  static CarrierVO carrierVO;
    private RelativeLayout searchRL,choice;
    public static int choiceca = 0;
    private ProgressDialog progressDialog;
    public static HashMap<String, InvoiceVO> donateMap;
    private Button choiceall, save, cancel;
    private EditText inputH;
    private ImageView searchI;
    private ListView heartyList;
    public static String teamNumber, teamTitle;
    private Button returnSH;
    private ProgressBar progressbar;
    private RelativeLayout modelR;
    private Spinner choiceModel;
    private List<CarrierVO> carrierVOS;
    private List<InvoiceVO> invoiceVOList;
    private Gson gson;
    private int poisition;
    private TextView showM;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ele_setdenote, container, false);
        progressDialog = new ProgressDialog(getActivity());
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        gson=new Gson();
        donateMap = new HashMap<>();
        findviewbyid(view);
        choiceall.setOnClickListener(new choiceallchecked());
        cancel.setOnClickListener(new cancelallchecked());
        save.setOnClickListener(new uploadheraty());
        searchI.setOnClickListener(new searchHeartyTeam());
        returnSH.setOnClickListener(new retrinDonateM());
        carrierVOS=carrierDB.getAll();
        if (carrierVOS == null || carrierVOS.size() <= 0) {
            message.setText("請新增載具!");
            message.setVisibility(View.VISIBLE);
            listinviuce.setVisibility(View.GONE);
            choice.setVisibility(View.GONE);
            modelR.setVisibility(View.GONE);
            showM.setVisibility(View.GONE);
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

    public void showDialog()
    {
        progressDialog.setTitle("上傳資料中!");
        progressDialog.show();
        progressbar.setVisibility(View.GONE);
    }


    public void cancelDialog() {
        progressDialog.cancel();
        progressbar.setVisibility(View.GONE);
    }


    public void setlistTeam(String jsonin) {
        cancelDialog();
        try {
             Log.d("XXXXXx",jsonin);
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
        carrierVO = carrierVOS.get(choiceca);
        invoiceVOList = invoiceDB.getCarrierDoAll(carrierVO.getCarNul());
        if (invoiceVOList == null || invoiceVOList.size() <= 0) {
            message.setText("目前沒有可捐贈發票!");
            message.setVisibility(View.VISIBLE);
            listinviuce.setVisibility(View.GONE);
        }else{
            message.setVisibility(View.GONE);
            listinviuce.setVisibility(View.VISIBLE);
        }
        listinviuce.setAdapter(null);
        listinviuce.setAdapter(new ListAdapter(getActivity(), invoiceVOList));
        listinviuce.setSelection(poisition);
    }

    public void donateOK()
    {
        cancelDialog();
        Fragment fragment=new EleDonateMain();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 :  getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }



    private void findviewbyid(View view) {
        listinviuce = view.findViewById(R.id.recyclenul);
        message = view.findViewById(R.id.message);
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
        AdView adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }





    private class choiceallchecked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            for (InvoiceVO invoiceVO : invoiceVOList) {
                donateMap.put(invoiceVO.getInvNum(), invoiceVO);
            }
            setlayout();
        }
    }

    private class cancelallchecked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            donateMap.clear();
            setlayout();
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
            ConnectivityManager mConnectivityManager = (ConnectivityManager) EleDonate.this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if(mNetworkInfo!=null)
            {
                new GetSQLDate(EleDonate.this).execute("searchHeartyTeam", inputH.getText().toString());
                progressbar.setVisibility(View.VISIBLE);
            }else{
                Common.showToast(EleDonate.this.getActivity(),"網路沒有開啟，無法下載!");
            }

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
            itemView.setBackgroundColor(Color.parseColor("#FFDD55"));
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
                itemView = layoutInflater.inflate(R.layout.ele_setdenote_item, parent, false);
            }
            TextView Title = itemView.findViewById(R.id.listTitle);
            TextView describe = itemView.findViewById(R.id.listDetail);
            Button updateD=itemView.findViewById(R.id.updateD);
            CheckBox donateC=itemView.findViewById(R.id.donateC);
            final InvoiceVO invoiceVO = invoiceVOS.get(position);
            Title.setText(Common.sTwo.format(new Date(invoiceVO.getTime().getTime())) + " " +invoiceVO.getMaintype()+" 共"+invoiceVO.getAmount()+"元");
            //設定describe
            StringBuffer sbDecribe=new StringBuffer();
            sbDecribe.append("統一編號:\n"+invoiceVO.getInvNum()+"\n\n");
            if(invoiceVO.getDetail().equals("0"))
            {
                updateD.setVisibility(View.VISIBLE);
                sbDecribe.append("無資料，請按下載\n  \n ");
                updateD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ConnectivityManager mConnectivityManager = (ConnectivityManager) EleDonate.this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                        if(mNetworkInfo!=null)
                        {
                            new GetSQLDate(EleDonate.this,invoiceVO).execute("reDownload");
                            progressDialog.setMessage("正在下傳資料,請稍候...");
                            progressDialog.show();
                            EleDonate.this.poisition=position;
                        }else{
                            Common.showToast(EleDonate.this.getActivity(),"網路沒有開啟，無法下載!");
                        }

                    }
                });
            }else{
                updateD.setVisibility(View.GONE);
                Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
                List<JsonObject> js=gson.fromJson(invoiceVO.getDetail(), cdType);
                int price,n;
                for(JsonObject j:js)
                {
                    try {
                        n=j.get("amount").getAsInt();
                        price=j.get("unitPrice").getAsInt();
                        sbDecribe.append(j.get("description").getAsString()+" : \n"+price+"X"+n/price+"="+n+"元\n");
                    }catch (Exception e)
                    {
                        sbDecribe.append(j.get("description").getAsString()+" : \n"+0+"X"+0+"="+0+"元\n");
                    }
                }
            }
            describe.setText(sbDecribe.toString());

            if(donateMap.get(invoiceVO.getInvNum())!=null)
            {
                donateC.setChecked(true);
            }else{
                donateC.setChecked(false);
            }
            donateC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox c= (CheckBox) view;

                    if(searchRL.getVisibility()==View.VISIBLE)
                    {
                        if(donateMap.get(invoiceVO.getInvNum())!=null)
                        {
                            c.setChecked(true);
                        }else{
                            c.setChecked(false);
                        }
                        return;
                    }

                    if(c.isChecked())
                    {
                        donateMap.put(invoiceVO.getInvNum(), invoiceVO);
                    }else{
                        donateMap.remove(invoiceVO.getInvNum());
                    }
                }
            });
            return itemView;
        }
    }

}


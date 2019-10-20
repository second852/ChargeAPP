package com.chargeapp.whc.chargeapp.Control.Search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.Adapter.DeleteDialogFragment;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyFromDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.Goal.GoalUpdate;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyUpdateConsume;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyUpdateMoney;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateIncome;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateInvoice;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateSpend;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PropertyFromVO;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.TypeCode.PropertyType;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jsoup.internal.StringUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchMain extends Fragment {

    private Activity context;
    private View view;
    private BootstrapEditText keyName;
    private ImageView search;
    private ListView listView;
    private BootstrapButton searchSettingShow;
    private List<Object> searchObject;
    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private BankDB bankDB;
    private PropertyDB propertyDB;
    private PropertyFromDB propertyFromDB;
    private GoalDB goalDB;
    private int p;
    private ProgressDialog progressDialog;
    private Gson gson;
    private CurrencyDB currencyDB;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.context=(Activity) context;
        }else{
            this.context=getActivity();
        }
        gson=new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_main, container, false);
        AdView adView=view.findViewById(R.id.adView);
        Common.setAdView(adView,context);
        findViewById();
        setDB();
        return view;
    }

    private void setDB() {
        Common.setChargeDB(context);
        consumeDB=new ConsumeDB(MainActivity.chargeAPPDB);
        bankDB=new BankDB(MainActivity.chargeAPPDB);
        invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB);
        goalDB=new GoalDB(MainActivity.chargeAPPDB);
        propertyDB=new PropertyDB(MainActivity.chargeAPPDB);
        propertyFromDB=new PropertyFromDB(MainActivity.chargeAPPDB);
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);
    }

    private void findViewById() {
        keyName=view.findViewById(R.id.keyName);
        search=view.findViewById(R.id.search);
        listView=view.findViewById(R.id.list);
        searchSettingShow=view.findViewById(R.id.searchSettingShow);
        search.setOnClickListener(new showSearch());
    }


    private class showSearch implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String key=keyName.getText().toString();
            if(StringUtil.isBlank(key))
            {
                keyName.setError("不能空白!");
                return;
            }
            searchObject=new ArrayList<>();
            //consume main/second/detail
            searchObject.addAll(consumeDB.findByKeyWord(key));
            //invoice main/second/detail
            searchObject.addAll(invoiceDB.findBySearchKey(key));
            //bank
            searchObject.addAll(bankDB.findBySearchKey(key));
            //goal
            searchObject.addAll(goalDB.findSearchKey(key));
            //property
            searchObject.addAll(propertyDB.findBySearchKey(key));
            //propertyFromDB
            searchObject.addAll(propertyFromDB.findBySearchKey(key));
        }
    }


    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<Object> objects;

        ListAdapter(Context context, List<Object> objects) {
            this.context = context;
            this.objects = objects;
        }

        public void setObjects(List<Object> objects) {
            this.objects = objects;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.select_con_detail_list_item, parent, false);
            }
            TextView title = itemView.findViewById(R.id.listTitle);
            TextView decribe = itemView.findViewById(R.id.listDetail);
            BootstrapButton update = itemView.findViewById(R.id.updateD);
            BootstrapButton deleteI = itemView.findViewById(R.id.deleteI);
            LinearLayout fixL = itemView.findViewById(R.id.fixL);
            BootstrapButton fixT = itemView.findViewById(R.id.fixT);
            LinearLayout remindL = itemView.findViewById(R.id.remindL);
            LinearLayout typeL = itemView.findViewById(R.id.typeL);
            BootstrapButton typeT = itemView.findViewById(R.id.typeT);

            //新增ele Type
            LinearLayout eleTypeL=itemView.findViewById(R.id.eleTypeL);
            BootstrapButton eleTypeT=itemView.findViewById(R.id.eleTypeT);


            final Object o = objects.get(position);
            StringBuffer sbDecribe = new StringBuffer();
            if (o instanceof InvoiceVO) {
                final InvoiceVO I = (InvoiceVO) o;

                //設定標籤
                remindL.setVisibility(View.GONE);
                fixL.setVisibility(View.GONE);

                typeL.setVisibility(View.VISIBLE);
                typeT.setText("雲端發票");
                typeT.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                //設定雲端發票種類
                try {
                    eleTypeL.setVisibility(View.VISIBLE);
                    eleTypeT.setText(Common.CardType().get(I.getCardType().trim()));
                    eleTypeT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                }catch (Exception e)
                {
                    eleTypeL.setVisibility(View.GONE);
                }

                //set detail
                if (I.getDetail().equals("0")) {
                    update.setText("下載");
                    sbDecribe.append("無資料，請按下載\n  \n ");
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ConnectivityManager mConnectivityManager = (ConnectivityManager) SearchMain.this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                            if (mNetworkInfo != null) {
                                p = position;
                                new GetSQLDate(SearchMain.this, I).execute("reDownload");
                                progressDialog.setMessage("正在下傳資料,請稍候...");
                                progressDialog.show();
                            } else {
                                Common.showToast(SearchMain.this.context, "網路沒有開啟，無法下載!");
                            }
                        }
                    });
                } else {
                    update.setText("修改");
                    Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
                    List<JsonObject> js = gson.fromJson(I.getDetail(), cdType);
                    float amout,n;
                    for (JsonObject j : js) {
                        try {
                            amout=j.get("amount").getAsFloat();
                        } catch (Exception e) {
                            amout=0;
                        }
                        try {
                            n = j.get("quantity").getAsFloat();
                        } catch (Exception e) {
                            n=0;
                        }
                        if(n!=0)
                        {
                            sbDecribe.append(j.get("description").getAsString() + " : \n" + (int)(amout/n) + "X" + (int)n + "=" + (int)amout + "元\n");
                        }else{
                            sbDecribe.append(j.get("description").getAsString() + " : \n" + (int)amout + "X" + 1 + "=" + (int)amout + "元\n");
                        }
                    }

                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            p = position;
                            Fragment fragment = new UpdateInvoice();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("invoiceVO", I);
                            bundle.putSerializable("action", "SelectListModelCom");
                            fragment.setArguments(bundle);
                            switchFragment(fragment);
                        }
                    });
                }


                title.setText(Common.setSecInvoiceTittle(I));
                decribe.setText(sbDecribe.toString());
            } else if (o instanceof ConsumeVO) {
                update.setText("修改");
                final ConsumeVO c = (ConsumeVO) o;

                //紙本發票種類
                eleTypeL.setVisibility(View.GONE);


                typeL.setVisibility(View.VISIBLE);
                if(c.getNumber()==null||c.getNumber().trim().length()<=0)
                {
                    typeT.setText("無發票");
                    typeT.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                }else{
                    typeT.setText("紙本發票");
                    typeT.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                }


                //set Notify
                if (Boolean.valueOf(c.getNotify())) {
                    remindL.setVisibility(View.VISIBLE);
                } else {
                    remindL.setVisibility(View.GONE);
                }


                //設定 title
                title.setText(Common.setSecConsumerTittlesDay(c));

                //設定 describe
                StringBuffer stringBuffer = new StringBuffer();
                fixL.setVisibility(View.GONE);
                if (c.isAuto()) {
                    fixT.setText("自動");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                    fixL.setVisibility(View.VISIBLE);
                    try {
                        JsonObject js = gson.fromJson(c.getFixDateDetail(), JsonObject.class);
                        stringBuffer.append(js.get("choicestatue").getAsString().trim());
                        stringBuffer.append(" " + js.get("choicedate").getAsString().trim());
                        boolean noweek = Boolean.parseBoolean(js.get("noweek").getAsString());
                        if (js.get("choicestatue").getAsString().trim().equals("每天") && noweek) {
                            stringBuffer.append(" 假日除外");
                        }
                    }catch (Exception e)
                    {
                        stringBuffer.append(" ");
                    }
                    stringBuffer.append("\n");
                }


                if (c.getFixDate()!=null&&c.getFixDate().equals("true")) {

                    fixT.setText("固定");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                    fixL.setVisibility(View.VISIBLE);
                    try {
                        JsonObject js = gson.fromJson(c.getFixDateDetail(), JsonObject.class);
                        stringBuffer.append(js.get("choicestatue").getAsString().trim());
                        stringBuffer.append(" " + js.get("choicedate").getAsString().trim());
                        boolean noweek = Boolean.parseBoolean(js.get("noweek").getAsString());
                        if (js.get("choicestatue").getAsString().trim().equals("每天") && noweek) {
                            stringBuffer.append(" 假日除外");
                        }
                    }catch (Exception e)
                    {
                        stringBuffer.append(" ");
                    }
                    stringBuffer.append("\n");
                }


                stringBuffer.append((c.getDetailname()==null?"":c.getDetailname()));
                if(stringBuffer.indexOf("\n")==-1)
                {
                    stringBuffer.append("\n");
                }
                decribe.setText(stringBuffer.toString());

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        p = position;
                        Fragment fragment = new UpdateSpend();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("consumeVO", c);
                        bundle.putSerializable("action", "SelectListModelCom");
                        bundle.putSerializable("position", position);
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });
            }else if(o instanceof BankVO)
            {
                BankVO bankVO= (BankVO) o;
                //設定 title
                title.setText(Common.setBankTittlesDay(bankVO));

                //設定 describe
                StringBuffer stringBuffer = new StringBuffer();
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
                        p = position;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("bankVO", bankVO);
                        bundle.putSerializable("action", "SelectListModelIM");
                        Fragment fragment = new UpdateIncome();
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });

            }else if(o instanceof GoalVO)
            {
                GoalVO goalVO= (GoalVO) o;
                String timeDec = goalVO.getTimeStatue().trim();
                int serial = 1 ;
                StringBuffer sb = new StringBuffer();
                if (timeDec.equals("今日")) {
                    //描述目標
                    sb.append(" " + serial + ". 起日 : " + Common.sTwo.format(goalVO.getStartTime()).trim());
                    serial++;
                    sb.append("\n " + serial + ". 訖日 : " + Common.sTwo.format(goalVO.getEndTime()).trim());
                    serial++;
                    sb.append("\n " + serial + ". " + goalVO.getType().trim() + " : " + Common.getCurrency(goalVO.getCurrency()) + " " + goalVO.getRealMoney());
                    serial++;
                }else{
                    sb.append(" "+serial + ". ");
                    sb.append(timeDec + goalVO.getType().trim() + " "+Common.getCurrency(goalVO.getCurrency())+" "+goalVO.getRealMoney());
                    serial++;
                }

                title.setText(goalVO.getName());
                decribe.setText(sb.toString());

                boolean updateGoal;
                if (goalVO.getStatue() == 1) {
                    fixT.setText("達成");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                    updateGoal = false;
                } else if (goalVO.getStatue() == 2) {
                    fixT.setText("失敗");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                    updateGoal = false;
                } else {
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                    fixT.setText("進行中");
                    updateGoal = true;
                }
                //notify
                StringBuilder nbNotify = new StringBuilder();
                if (goalVO.isNotify()) {
                    serial++;
                    nbNotify.append(" " + serial + ". 提醒 : " + goalVO.getNotifyStatue().trim()).append(" " + goalVO.getNotifyDate());
                    if (goalVO.isNoWeekend() && goalVO.getNotifyStatue().trim().equals("每天")) {
                        nbNotify.append("假日除外");
                    }
                    remindL.setVisibility(View.VISIBLE);
                } else {
                    remindL.setVisibility(View.GONE);
                }
                if (updateGoal) {
                    update.setVisibility(View.VISIBLE);
                    update.setBackgroundColor(Color.parseColor("#33CCFF"));
                    update.setText("修改");
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            Fragment fragment = new GoalUpdate();
                            bundle.putSerializable("goalVO", goalVO);
                            bundle.putSerializable("position", position);
                            fragment.setArguments(bundle);
                            switchFragment(fragment);
                        }
                    });
                } else {
                    update.setVisibility(View.INVISIBLE);
                }
            }else if(o instanceof PropertyVO)
            {
                PropertyVO propertyVO= (PropertyVO) o;
                CurrencyVO currencyVO=currencyDB.getOneByType(propertyVO.getCurrency());
                Double consume=propertyFromDB.totalType(propertyVO.getId(), PropertyType.Negative);
                Double income=propertyFromDB.totalType(propertyVO.getId(), PropertyType.Positive);
                Double total=income-consume;
                String titleP=propertyVO.getName()+" "+ Common.CurrencyResult(total,currencyVO);
                String detailE="收入 "+ Common.CurrencyResult(income,currencyVO)+"\n" +
                        "支出 "+ Common.CurrencyResult(consume,currencyVO);
                title.setText(titleP);
                decribe.setText(detailE);
            }else if(o instanceof PropertyFromVO)
            {
                PropertyFromVO propertyFromVO= (PropertyFromVO) o;
                fixL.setVisibility(View.GONE);
                if (!StringUtil.isBlank(propertyFromVO.getFixFromId())) {
                    fixT.setText("自動");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                    fixL.setVisibility(View.VISIBLE);
                }

                if(propertyFromVO.getFixImport())
                {
                    fixT.setText("固定");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                    fixL.setVisibility(View.VISIBLE);
                }





                StringBuilder titleProperty=new StringBuilder();


                if(StringUtil.isBlank(propertyFromVO.getSourceSecondType()))
                {
                    titleProperty.append(propertyFromVO.getSourceMainType());
                }else{
                    titleProperty.append(propertyFromVO.getSourceSecondType());
                }


                titleProperty.append(" "+ Common.getCurrency(propertyFromVO.getSourceCurrency()));
                titleProperty.append(" "+ Common.doubleRemoveZero(Double.valueOf(propertyFromVO.getSourceMoney())));
                title.setText(titleProperty.toString());
                StringBuilder detail=new StringBuilder();
                detail.append("1. 日期 : "+ Common.sTwo.format(propertyFromVO.getSourceTime())+" \n");
                detail.append("2. 手續費 : ");
                detail.append(Common.getCurrency(propertyFromVO.getSourceCurrency())).append(propertyFromVO.getImportFee()+"\n");
                if(propertyFromVO.getFixImport())
                {
                    if(StringUtil.isBlank(propertyFromVO.getSourceSecondType()))
                    {
                        detail.append("3. 定期匯入 : ").append(propertyFromVO.getFixDateCode().getDetail());
                    }else{
                        detail.append("3. 定期支出 : ").append(propertyFromVO.getFixDateCode().getDetail());
                    }
                    if(propertyFromVO.getFixDateDetail()!=null)
                    {
                        detail.append(" "+propertyFromVO.getFixDateDetail());
                    }
                }
                decribe.setText(detail.toString());
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment=null;
                        switch (propertyFromVO.getType())
                        {
                            case Positive:
                                fragment=new PropertyUpdateMoney();
                                break;
                            case Negative:
                                fragment=new PropertyUpdateConsume();
                                break;

                        }
                      switchFragment(fragment);
                    }
                });
            }





            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteDialogFragment aa = new DeleteDialogFragment();
                    aa.setObject(o);
                    aa.setFragment(SearchMain.this);
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
        MainActivity.oldFramgent.add(Common.searchMainString);
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }



}

package com.chargeapp.whc.chargeapp.Control.SelectList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.Adapter.DeleteDialogFragment;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateInvoice;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateSpend;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class SelectListModelCom extends Fragment {
    private ImageView DRadd, DRcut;
    private TextView DRcarrier;
    private ListView listView;
    private InvoiceDB invoiceDB;
    private ConsumeDB consumeDB;
    private List<Object> objects;
    public static int month, year;
    private ProgressDialog progressDialog;
    private Gson gson = new Gson();
    public static int p;
    private TextView message;
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TypefaceProvider.registerDefaultIconSets();
        View view = inflater.inflate(R.layout.select_con_list, container, false);
        if(year==0)
        {
            Calendar calendar=Calendar.getInstance();
            SelectListModelCom.year = calendar.get(Calendar.YEAR);
            SelectListModelCom.month = calendar.get(Calendar.MONTH);
        }

        Common.setChargeDB(context);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB);
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB);
        findViewById(view);
        progressDialog = new ProgressDialog(context);
        DRadd.setOnClickListener(new addOnClick());
        DRcut.setOnClickListener(new cutOnClick());
        setLayout();
        return view;
    }

    public void cancelshow() {
        progressDialog.cancel();
        Common.showToast(context, "財政部網路忙線~");
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


    public void setLayout() {
        objects = new ArrayList<>();
        Calendar start = new GregorianCalendar(year, month, 1, 0, 0, 0);
        Calendar end = new GregorianCalendar(year, month, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        List<ConsumeVO> consumeVOS = consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));
        List<InvoiceVO> invoiceVOS = invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()));

//         for(InvoiceVO i:invoiceVOS)
//         {
//             Log.d("XXXXXXX",i.getMaintype()+" "+i.getSecondtype());
//         }

        objects.addAll(consumeVOS);
        objects.addAll(invoiceVOS);
        Collections.sort(objects, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                long t1 = (o1 instanceof ConsumeVO) ? (((ConsumeVO) o1).getDate().getTime()) : (((InvoiceVO) o1).getTime().getTime());
                long t2 = (o2 instanceof ConsumeVO) ? (((ConsumeVO) o2).getDate().getTime()) : (((InvoiceVO) o2).getTime().getTime());
                if (t1 > t2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        DRcarrier.setText(Common.sThree.format(new Date(start.getTimeInMillis())));
        if (objects.size() <= 0) {
            message.setVisibility(View.VISIBLE);
            message.setText(Common.sThree.format(new Date(start.getTimeInMillis())) + "\n無資料");
        } else {
            message.setVisibility(View.GONE);
        }

        if (listView.getAdapter() != null) {
            ListAdapter adapter = (ListAdapter) listView.getAdapter();
            adapter.setObjects(objects);
            adapter.notifyDataSetChanged();
        } else {
            listView.setAdapter(new ListAdapter(context, objects));
        }
        listView.setSelection(p);
        progressDialog.cancel();
    }

    private void findViewById(View view) {
        DRadd = view.findViewById(R.id.DRadd);
        DRcut = view.findViewById(R.id.DRcut);
        DRcarrier = view.findViewById(R.id.DRcarrier);
        listView = view.findViewById(R.id.list);
        message = view.findViewById(R.id.message);
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
                    eleTypeT.setText(Common.CardType(I.getSellerName().trim()));
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
                            ConnectivityManager mConnectivityManager = (ConnectivityManager) SelectListModelCom.this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                            if (mNetworkInfo != null) {
                                p = position;
                                new GetSQLDate(SelectListModelCom.this, I).execute("reDownload");
                                progressDialog.setMessage("正在下傳資料,請稍候...");
                                progressDialog.show();
                            } else {
                                Common.showToast(SelectListModelCom.this.context, "網路沒有開啟，無法下載!");
                            }
                        }
                    });
                } else {
                    update.setText("修改");
                    Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
                    List<JsonObject> js = gson.fromJson(I.getDetail(), cdType);
                    Float amout,n;
                    for (JsonObject j : js) {
                        try {
                            amout=j.get("amount").getAsFloat();
                        } catch (Exception e) {
                            amout=0f;
                        }
                        try {
                            n = j.get("quantity").getAsFloat();
                        } catch (Exception e) {
                            n=0f;
                        }
                        if(n!=0f)
                        {
                            sbDecribe.append(j.get("description").getAsString() + " : \n" + Common.doubleRemoveZero(amout/n) + "X" + n.intValue() + "= "+Common.goalCurrencyResult(amout.doubleValue(),I.getCurrency()) + "\n");
                        }else{
                            sbDecribe.append(j.get("description").getAsString() +" : \n"+Common.doubleRemoveZero(amout) + "X" + 1 + "= "+Common.goalCurrencyResult(amout.doubleValue(),I.getCurrency()) + "\n");
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

            } else {
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

                //設定紙本發票種類
                try {
                    eleTypeL.setVisibility(View.VISIBLE);
                    eleTypeT.setText(Common.CardType(c.getSellerName().trim()));
                    eleTypeT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                }catch (Exception e)
                {
                    eleTypeL.setVisibility(View.GONE);
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
            }

            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteDialogFragment aa = new DeleteDialogFragment();
                    aa.setObject(o);
                    aa.setFragment(SelectListModelCom.this);
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
        MainActivity.oldFramgent.add("SelectListModelCom");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

}

package com.chargeapp.whc.chargeapp.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.Adapter.DeleteDialogFragment;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyFromDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyTotal;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyUpdate;
import com.chargeapp.whc.chargeapp.Control.SelectList.SelectListModelCom;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateSpend;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.TypeCode.PropertyType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ScanListFragment extends Fragment {

    private View view;
    private ListView list;
    private Activity activity;
    private TextView message;
    private int position;
    private ConsumeDB consumeDB;
    private Gson gson = new Gson();
    private BootstrapButton backP;
    private Set<String> winLevel;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        } else {
            this.activity = getActivity();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.scan_list, container, false);
        Common.setChargeDB(activity);
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB);
        winLevel=Common.getPrice().keySet();
        list = view.findViewById(R.id.list);
        backP = view.findViewById(R.id.backP);
        backP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String action = MainActivity.oldFramgent.getLast();
                switch (action) {
                    case Common.scanFragment:
                        Fragment fragment = new ScanFragment();
                        fragment.setArguments(getArguments());
                        Common.switchConfirmFragment(fragment, getFragmentManager());
                        break;

                    case Common.scanByOnline:
                        fragment = new ScanByOnline();
                        fragment.setArguments(getArguments());
                        Common.switchConfirmFragment(fragment, getFragmentManager());
                        break;
                }

            }
        });
        message = view.findViewById(R.id.message);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setAdapt();
    }

    public void setAdapt() {
        List<ConsumeVO> consumeVOS = new ArrayList<>();
        for (String nul : ScanFragment.nulName.keySet()) {
            ConsumeVO consumeVO = consumeDB.findConByNul(nul,ScanFragment.nulName.get(nul));
            consumeVOS.add(consumeVO);
        }

        if (consumeVOS.isEmpty()) {
            message.setText(R.string.error_noScanData);
            message.setVisibility(View.VISIBLE);
        } else {
            message.setVisibility(View.GONE);
            Collections.sort(consumeVOS, new Comparator<ConsumeVO>() {
                @Override
                public int compare(ConsumeVO consumeVO, ConsumeVO t1) {
                    switch (consumeVO.getIsWin())
                    {
                        case "0":
                        case "N":
                            return  -(consumeVO.getDate().compareTo(t1.getDate())) ;
                        default:
                             return 1;
                    }
                }
            });
        }
        try {
            position = getArguments().getInt("position");
        } catch (Exception e) {
            position = 0;
        }
        list.setAdapter(new ListAdapter(activity, consumeVOS));
        list.setSelection(position);
    }


    //Adapter
    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<ConsumeVO> consumeVOS;


        public ListAdapter(Context context, List<ConsumeVO> consumeVOS) {
            this.context = context;
            this.consumeVOS = consumeVOS;
        }

        @Override
        public int getCount() {
            return consumeVOS.size();
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
            LinearLayout eleTypeL = itemView.findViewById(R.id.eleTypeL);
            BootstrapButton eleTypeT = itemView.findViewById(R.id.eleTypeT);

            update.setText("修改");
            final ConsumeVO c = consumeVOS.get(position);

            //中獎資訊
            StringBuffer stringBuffer = new StringBuffer();
            eleTypeL.setVisibility(View.VISIBLE);
            switch (c.getIsWin()) {
                case "0":
                    eleTypeT.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                    eleTypeT.setText("尚未開獎");
                    break;
                case "N":
                    eleTypeT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                    eleTypeT.setText("未中獎");
                    break;
                default:
                    eleTypeT.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                    eleTypeT.setText(Common.getPriceName().get(c.getIsWin()));


                    //detail
                    stringBuffer.append("發票號碼 : ");
                    stringBuffer.append(c.getNumber());
                    stringBuffer.append("\n中獎號碼 : ");
                    stringBuffer.append(c.getIsWinNul());
                    stringBuffer.append("\n獎金 : ");
                    stringBuffer.append(Common.getPrice().get(c.getIsWin()) + "\n");

                    break;
            }


            typeL.setVisibility(View.VISIBLE);
            if (c.getNumber() == null || c.getNumber().trim().length() <= 0) {
                typeT.setText("無發票");
                typeT.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
            } else {
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
                } catch (Exception e) {
                    stringBuffer.append(" ");
                }
                stringBuffer.append("\n");
            }


            if (c.getFixDate() != null && c.getFixDate().equals("true")) {

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
                } catch (Exception e) {
                    stringBuffer.append(" ");
                }
                stringBuffer.append("\n");
            }


            stringBuffer.append((c.getDetailname() == null ? "" : c.getDetailname()));
            if (stringBuffer.indexOf("\n") == -1) {
                stringBuffer.append("\n");
            }


            if(winLevel.contains(c.getIsWin()))
            {
                SpannableString detailC = new SpannableString(stringBuffer.toString());

                int nulL=Common.getlevellength().get(c.getIsWin());
                int indexF=stringBuffer.indexOf(c.getNumber());
                int indexS=stringBuffer.indexOf("中獎號碼 : ");
                int sL="中獎號碼 : ".length();
                int sBetween=nulL-2;
                if(c.getIsWinNul().length()<=nulL)
                {
                    sBetween=0;
                }


                int indexT=stringBuffer.indexOf(Common.getPrice().get(c.getIsWin()));

                detailC.setSpan(new ForegroundColorSpan(Color.parseColor("#CC0000")),indexF+nulL,
                        indexF+c.getNumber().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                detailC.setSpan(new ForegroundColorSpan(Color.parseColor("#CC0000")),indexS+sL+sBetween,
                        indexS+c.getIsWinNul().length()+sL, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                detailC.setSpan(new ForegroundColorSpan(Color.parseColor("#CC0000")),indexT,
                       indexT+Common.getPrice().get(c.getIsWin()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                decribe.setText(detailC);
            }else {
                decribe.setText(stringBuffer.toString());
            }




            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScanListFragment.this.position = position;
                    Fragment fragment = new ScanUpdateSpend();
                    Bundle bundle = getArguments();
                    bundle.putSerializable("consumeVO", c);
                    bundle.putSerializable("position", position);
                    fragment.setArguments(bundle);
                    switchFragment(fragment);
                }
            });


            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteDialogFragment aa = new DeleteDialogFragment();
                    aa.setObject(c);
                    aa.setFragment(ScanListFragment.this);
                    aa.show(getFragmentManager(), "show");
                }
            });


            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return consumeVOS.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    private void switchFragment(Fragment fragment) {
        MainActivity.bundles.add(fragment.getArguments());
        MainActivity.oldFramgent.add(Common.scanFragment);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }
}

package com.chargeapp.whc.chargeapp.Control.Setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.Adapter.DeleteDialogFragment;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateSpend;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SettingListFixCon extends Fragment {


    private ListView listView;
    public int p;
    private ConsumeDB consumeDB;
    private Gson gson;
    private TextView message;
    public List<ConsumeVO> consumeVOS;
    public ConsumeVO consumeVO;
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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gson=new Gson();
        Common.setChargeDB(context);
        consumeDB=new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        View view = inflater.inflate(R.layout.setting_main, container, false);
        consumeVO= (ConsumeVO) getArguments().getSerializable("consumeVO");
        p= (int) getArguments().getSerializable("position");
        listView=view.findViewById(R.id.list);
        message=view.findViewById(R.id.message);
        Common.setAdView((AdView) view.findViewById(R.id.adView),context);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(consumeVO.isAuto())
        {
            consumeVOS=consumeDB.getAutoCreate(consumeVO.getAutoId());
            consumeVOS.add(0,consumeDB.findConById(consumeVO.getAutoId()));
        }else{
            consumeVOS=consumeDB.getAutoCreate(consumeVO.getId());
            consumeVOS.add(0,consumeVO);
        }
        setLayout();
    }

    public void setLayout() {
        ListAdapter adapter = (ListAdapter) listView.getAdapter();
        if (adapter == null) {
            listView.setAdapter(new ListAdapter(context, consumeVOS));
        } else {
            adapter.setObjects(consumeVOS);
            adapter.notifyDataSetChanged();
            listView.invalidate();
        }
        if(consumeVOS.size()<=0)
        {
            message.setText("無資料!");
            message.setVisibility(View.VISIBLE);
        }
        listView.setSelection(p);
    }


    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<ConsumeVO> consumeVOS;

        ListAdapter(Context context, List<ConsumeVO> consumeVOS) {
            this.context = context;
            this.consumeVOS = consumeVOS;
        }

        public void setObjects(List<ConsumeVO> consumeVOS) {
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
            TextView title=itemView.findViewById(R.id.listTitle);
            TextView decribe=itemView.findViewById(R.id.listDetail);
            BootstrapButton fixT=itemView.findViewById(R.id.fixT);
            BootstrapButton remainT=itemView.findViewById(R.id.remainT);
            LinearLayout remindL=itemView.findViewById(R.id.remindL);
            final LinearLayout fixL=itemView.findViewById(R.id.fixL);
            BootstrapButton update=itemView.findViewById(R.id.updateD);
            BootstrapButton deleteI=itemView.findViewById(R.id.deleteI);
            update.setText("修改");
            final ConsumeVO consumeVO=consumeVOS.get(position);

            if(consumeVO.isAuto())
            {
                fixL.setVisibility(View.VISIBLE);
                fixT.setText("自動");
                fixT.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                remindL.setVisibility(View.GONE);
                remainT.setBootstrapBrand(DefaultBootstrapBrand.INFO);
            }else {
                if(consumeVO.getNotify().equals("true"))
                {
                    remainT.setText("提醒");
                    remindL.setVisibility(View.VISIBLE);
                }else {
                    remindL.setVisibility(View.GONE);
                }
                fixT.setText("設定檔");
                fixT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                fixL.setVisibility(View.VISIBLE);
            }
            //設定 title
            title.setText(Common.setSecConsumerTittlesTwo(consumeVO));

            //設定 describe
            StringBuilder stringBuffer=new StringBuilder();
            JsonObject js=gson.fromJson(consumeVO.getFixDateDetail(),JsonObject.class);
            stringBuffer.append(js.get("choicestatue").getAsString().trim());
            stringBuffer.append(" "+js.get("choicedate").getAsString().trim());
            boolean noweek= Boolean.parseBoolean(js.get("noweek").getAsString());
            if(js.get("choicestatue").getAsString().trim().equals("每天")&&noweek)
            {
                stringBuffer.append(" 假日除外");
            }
            decribe.setText(stringBuffer.toString()+" \n"+(consumeVO.getDetailname()==null?"":consumeVO.getDetailname()));
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment=new UpdateSpend();
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("consumeVO",consumeVO);
                    bundle.putSerializable("position",position);
                    bundle.putSerializable("action","SettingListFixCon");
                    fragment.setArguments(bundle);
                    switchFragment(fragment);
                }
            });

            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteDialogFragment aa= new DeleteDialogFragment();
                    p=position;
                    aa.setObject(consumeVO);
                    aa.setFragement(SettingListFixCon.this);
                    aa.show(getFragmentManager(),"show");
                }
            });
            return itemView;
        }

        @Override
        public ConsumeVO getItem(int position) {
            return consumeVOS.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }


    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("SettingListFixCon");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

}

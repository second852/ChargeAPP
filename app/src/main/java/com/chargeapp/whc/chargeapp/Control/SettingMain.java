package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.EleMainItemVO;
import com.chargeapp.whc.chargeapp.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SettingMain extends Fragment {


    private ListView listView;
    private CarrierDB carrierDB;




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_main, container, false);
        listView=view.findViewById(R.id.list);
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        List<EleMainItemVO> itemSon= getNewItem();
        listView.setAdapter(new ListAdapter(getActivity(),itemSon));
        return view;
    }

    private List<EleMainItemVO> getNewItem() {
        List<EleMainItemVO> eleMainItemVOList = new ArrayList<>();
        eleMainItemVOList.add(new EleMainItemVO("種類修改/刪除", R.drawable.treatment));
        eleMainItemVOList.add(new EleMainItemVO("設定載具", R.drawable.barcode));
        eleMainItemVOList.add(new EleMainItemVO("關閉提醒", R.drawable.notifyt));
        eleMainItemVOList.add(new EleMainItemVO("設定提醒時間", R.drawable.timei));
        eleMainItemVOList.add(new EleMainItemVO("取消提醒項目", R.drawable.cancel));
        eleMainItemVOList.add(new EleMainItemVO("匯出檔案", R.drawable.importf));
        eleMainItemVOList.add(new EleMainItemVO("匯入檔案",R.drawable.export));
        return eleMainItemVOList;
    }



    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<EleMainItemVO> eleMainItemVOS;

        ListAdapter(Context context,List<EleMainItemVO> eleMainItemVOS) {
            this.context = context;
            this.eleMainItemVOS = eleMainItemVOS;
        }


        @Override
        public int getCount() {
            return eleMainItemVOS.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.setting_main_item, parent, false);
            }
            EleMainItemVO eleMainItemVO = eleMainItemVOS.get(position);
            ImageView imageView=itemView.findViewById(R.id.image);
            TextView textView=itemView.findViewById(R.id.listTitle);
            imageView.setImageResource(eleMainItemVO.getImage());
            textView.setText(eleMainItemVO.getName());
            Spinner carrier=itemView.findViewById(R.id.carrier);
            Switch  notify=itemView.findViewById(R.id.notify);
            TextView setTime=itemView.findViewById(R.id.setTime);
            carrier.setVisibility(View.GONE);
            setTime.setVisibility(View.GONE);
            notify.setVisibility(View.GONE);
            if(position==0)
            {

            }else if(position==1)
            {
                ArrayList<String> spinnerItem = new ArrayList<>();
                List<CarrierVO> carrierVOS=carrierDB.getAll();
                if(carrierVOS.size()>0)
                {
                    for(CarrierVO c:carrierVOS)
                    {
                        spinnerItem.add(c.getCarNul());
                    }
                }else{
                    spinnerItem.add("無載具");
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinneritem, spinnerItem);
                arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
                carrier.setAdapter(arrayAdapter);
                carrier.setVisibility(View.VISIBLE);
            }else if(position==2)
            {
                notify.setVisibility(View.VISIBLE);
            }else if(position==3)
            {
                setTime.setVisibility(View.VISIBLE);
            }else if(position==4)
            {

            }else if(position==5)
            {

            }

            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return eleMainItemVOS.get(position);
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

package com.chargeapp.whc.chargeapp.Control;

import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
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

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SettingMain extends Fragment {


    private ListView listView;
    private CarrierDB carrierDB;
    private SharedPreferences sharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_main, container, false);
        sharedPreferences=getActivity().getSharedPreferences("Charge_User",Context.MODE_PRIVATE);
        listView = view.findViewById(R.id.list);
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        List<EleMainItemVO> itemSon = getNewItem();
        listView.setAdapter(new ListAdapter(getActivity(), itemSon));
        return view;
    }

    private List<EleMainItemVO> getNewItem() {
        List<EleMainItemVO> eleMainItemVOList = new ArrayList<>();
        eleMainItemVOList.add(new EleMainItemVO("種類修改/刪除", R.drawable.treatment));
        eleMainItemVOList.add(new EleMainItemVO("關閉提醒", R.drawable.notifyt));
        eleMainItemVOList.add(new EleMainItemVO("設定提醒時間", R.drawable.timei));
        eleMainItemVOList.add(new EleMainItemVO("設定定期項目", R.drawable.cancel));
        eleMainItemVOList.add(new EleMainItemVO("匯出檔案", R.drawable.importf));
        eleMainItemVOList.add(new EleMainItemVO("匯入檔案", R.drawable.export));
        eleMainItemVOList.add(new EleMainItemVO("重設資料庫", R.drawable.origin));
        return eleMainItemVOList;
    }

    public void deleteAll()
    {
        getActivity().deleteDatabase("ChargeAPP");
        MainActivity.chargeAPPDB=new ChargeAPPDB(getActivity());
        new MainActivity().setdate();
    }

    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<EleMainItemVO> eleMainItemVOS;

        ListAdapter(Context context, List<EleMainItemVO> eleMainItemVOS) {
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
            final EleMainItemVO eleMainItemVO = eleMainItemVOS.get(position);
            ImageView imageView = itemView.findViewById(R.id.image);
            final TextView textView = itemView.findViewById(R.id.listTitle);
            imageView.setImageResource(eleMainItemVO.getImage());
            textView.setText(eleMainItemVO.getName());
            Switch notify = itemView.findViewById(R.id.notify);
            final TextView setTime = itemView.findViewById(R.id.setTime);
            setTime.setVisibility(View.GONE);
            notify.setVisibility(View.GONE);
            if (position == 0) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment=new SettingListType();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("position",0);
                        bundle.putSerializable("spinnerC",0);
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });
            } else if (position == 1) {
                boolean b=sharedPreferences.getBoolean("notify",true);
                notify.setChecked(b);
                notify.setVisibility(View.VISIBLE);
                notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        sharedPreferences.edit().putBoolean("notify",isChecked).apply();
                        if(isChecked)
                        {
                            textView.setText("關閉提醒");
                        }else{
                            textView.setText("打開提醒");
                        }
                    }
                });
            } else if (position == 2) {
                String userTime=sharedPreferences.getString("userTime","6:00 p.m.");
                setTime.setVisibility(View.VISIBLE);
                setTime.setText(userTime);
                final GregorianCalendar calendar=new GregorianCalendar();
                setTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TimePickerDialog(SettingMain.this.getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                int hour =(hourOfDay>12)?(hourOfDay-12): hourOfDay ;
                                String period=(hourOfDay>12)?" p.m.":" a.m.";
                                String m=String.valueOf(minute).trim();
                                m=(m.length()==2)?m:"0"+m;
                                setTime.setText(hour+":"+m+period);
                                sharedPreferences.edit().putString("userTime",hour+":"+m+period).apply();
                                Calendar now=Calendar.getInstance();
                                Calendar setNotifyTime=new GregorianCalendar(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH),hourOfDay,minute);
                                if(now.getTimeInMillis()<setNotifyTime.getTimeInMillis())
                                {
                                    NotificationManager notificationManager= (NotificationManager) SettingMain.this.getActivity().getSystemService(NOTIFICATION_SERVICE);
                                    notificationManager.cancelAll();
                                    BootReceiver bootReceiver=new BootReceiver();
                                    Intent intent=new Intent();
                                    intent.setAction(Intent.ACTION_DATE_CHANGED);
                                    bootReceiver.onReceive(SettingMain.this.getActivity(),intent);
                                }
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                    }
                });
            } else if (position == 3) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment=new SettingListFix();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("position",0);
                        bundle.putSerializable("spinnerC",0);
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });
            } else if (position == 4) {

            } else if (position == 5) {


            } else if (position == 6) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DeleteDialogFragment aa = new DeleteDialogFragment();
                        aa.setFragement(SettingMain.this);
                        aa.show(getFragmentManager(), "show");
                    }
                });
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
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }



}

package com.chargeapp.whc.chargeapp.Control.Setting;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.chargeapp.whc.chargeapp.Adapter.DeleteDialogFragment;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.Download;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Job.JobSchedulerService;
import com.chargeapp.whc.chargeapp.Model.EleMainItemVO;
import com.chargeapp.whc.chargeapp.R;

import java.text.SimpleDateFormat;
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
    private SharedPreferences sharedPreferences;
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
        final View view = inflater.inflate(R.layout.setting_main, container, false);
        sharedPreferences=context.getSharedPreferences("Charge_User",Context.MODE_PRIVATE);
        listView = view.findViewById(R.id.list);
        List<EleMainItemVO> itemSon = getNewItem();
        listView.setAdapter(new ListAdapter(context, itemSon));
        return view;
    }

    private List<EleMainItemVO> getNewItem() {
        List<EleMainItemVO> eleMainItemVOList = new ArrayList<>();
        eleMainItemVOList.add(new EleMainItemVO("種類修改/刪除", R.drawable.treatment));
        eleMainItemVOList.add(new EleMainItemVO("關閉提醒", R.drawable.notifyt));
        eleMainItemVOList.add(new EleMainItemVO("設定提醒時間", R.drawable.timei));
        eleMainItemVOList.add(new EleMainItemVO("設定定期項目", R.drawable.cancel));
        eleMainItemVOList.add(new EleMainItemVO("自動分類", R.drawable.book));
        eleMainItemVOList.add(new EleMainItemVO("本日匯率查詢", R.drawable.bouns));
        eleMainItemVOList.add(new EleMainItemVO("匯出檔案", R.drawable.importf));
        eleMainItemVOList.add(new EleMainItemVO("匯入檔案", R.drawable.export));
        eleMainItemVOList.add(new EleMainItemVO("重設資料庫", R.drawable.origin));
        return eleMainItemVOList;
    }

    public void deleteAll()
    {
        context.deleteDatabase("ChargeAPP");
        MainActivity.chargeAPPDB=new ChargeAPPDB(context);
        Common.insertNewTableCol();
        new Download().setData();
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
                if(b)
                {
                    textView.setText("自動提醒(使用中)");
                }else {
                    textView.setText("自動提醒(關閉中)");
                }

                notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        sharedPreferences.edit().putBoolean("notify",isChecked).apply();
                        NotificationManager notificationManager= (NotificationManager) SettingMain.this.context.getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();
                        if(isChecked)
                        {
                            textView.setText("自動提醒(使用中)");
                        }else{
                            textView.setText("自動提醒(關閉中)");
                            //重製job
                            String setTime = sharedPreferences.getString("userTime", "6:00 p.m.").trim();
                            int hour, min;
                            if (setTime.indexOf("p") == -1) {
                                hour = Integer.valueOf(setTime.substring(0, setTime.indexOf(":")));
                                min = Integer.valueOf(setTime.substring(setTime.indexOf(":") + 1, setTime.indexOf("a")).trim());
                            } else {
                                hour = Integer.valueOf(setTime.substring(0, setTime.indexOf(":"))) + 12;
                                min = Integer.valueOf(setTime.substring(setTime.indexOf(":") + 1, setTime.indexOf("p")).trim());
                            }
                            Calendar date = Calendar.getInstance();
                            int year = date.get(Calendar.YEAR);
                            int month = date.get(Calendar.MONTH);
                            int day = date.get(Calendar.DAY_OF_MONTH);
                            Calendar setNewTime = new GregorianCalendar(year, month, day, hour, min, 0);
                            if(setNewTime.getTimeInMillis()>System.currentTimeMillis())
                            {
                                //避免重複執行
                                JobScheduler tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                                boolean hasBeenScheduled=false;
                                for (JobInfo jobInfo : tm.getAllPendingJobs()) {
                                    if (jobInfo.getId() == 1) {
                                        hasBeenScheduled = true;
                                        break;
                                    }
                                }
                                if (hasBeenScheduled) {
                                    tm.cancel(1);
                                }

                                //重製job
                                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                                sharedPreferences.edit().putBoolean(sf.format(new Date(System.currentTimeMillis())), false).apply();
                                ComponentName mServiceComponent = new ComponentName(context, JobSchedulerService.class);
                                JobInfo.Builder builder = new JobInfo.Builder(1, mServiceComponent);
                                builder.setMinimumLatency(1);
                                builder.setPersisted(true);
                                builder.setRequiresCharging(false);
                                builder.setRequiresDeviceIdle(false);
                                tm.schedule(builder.build());
                            }
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
                        new TimePickerDialog(SettingMain.this.context, new TimePickerDialog.OnTimeSetListener() {
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
                                    NotificationManager notificationManager= (NotificationManager) SettingMain.this.context.getSystemService(NOTIFICATION_SERVICE);
                                    notificationManager.cancelAll();

                                    //避免重複執行
                                    JobScheduler tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                                    boolean hasBeenScheduled=false;
                                    for (JobInfo jobInfo : tm.getAllPendingJobs()) {
                                        if (jobInfo.getId() == 1) {
                                            hasBeenScheduled = true;
                                            break;
                                        }
                                    }
                                    if (hasBeenScheduled) {
                                        tm.cancel(1);
                                    }

                                    //重製job
                                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                                    sharedPreferences.edit().putBoolean(sf.format(new Date(System.currentTimeMillis())), false).apply();
                                    ComponentName mServiceComponent = new ComponentName(context, JobSchedulerService.class);
                                    JobInfo.Builder builder = new JobInfo.Builder(1, mServiceComponent);
                                    builder.setMinimumLatency(1);
                                    builder.setPersisted(true);
                                    builder.setRequiresCharging(false);
                                    builder.setRequiresDeviceIdle(false);
                                    tm.schedule(builder.build());
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
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });
            } else if(position == 4){

                boolean autoCategory=sharedPreferences.getBoolean("autoCategory",true);
                notify.setChecked(autoCategory);
                notify.setVisibility(View.VISIBLE);
                if(autoCategory)
                {
                    textView.setText("自動分類(使用中)");
                }else{
                    textView.setText("自動分類(關閉中)");
                }
                notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if(isChecked)
                        {
                            textView.setText("自動分類(使用中)");
                        }else{
                            textView.setText("自動分類(關閉中)");
                        }
                        sharedPreferences.edit().putBoolean("autoCategory", isChecked).apply();
                    }
                });
            } else if (position == 5) {

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment=new SettingCurrency();
                        switchFragment(fragment);
                    }
                });

            }else if (position == 6) {

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment=new SettingUploadFile();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("action","no");
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });

            }else if (position == 7) {

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment=new SettingDownloadFile();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("action","no");
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });

            } else if (position == 8) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DeleteDialogFragment aa = new DeleteDialogFragment();
                        aa.setFragment(SettingMain.this);
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
        MainActivity.oldFramgent.add("SettingMain");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }



}

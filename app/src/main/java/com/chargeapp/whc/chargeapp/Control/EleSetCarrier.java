package com.chargeapp.whc.chargeapp.Control;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.R;




import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/20.
 */

public class EleSetCarrier extends Fragment {
    private BootstrapEditText cellphone,certcode;
    private ListView listcarrier;
    private BootstrapButton confirm;
    private TextView percentage,progressT;
    public GetSQLDate getIvnum;
    private List<CarrierVO> carrierlist;
    public CarrierDB carrierDB;
    public TextView listtiitle;
    private RelativeLayout progressbarL;
    private  SharedPreferences sharedPreferences;
    private  int position;
    private CarrierVO carrierVO;
    private Handler handler;
    private View view;
    private Activity context;
    private DrawerLayout drawerLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.context=(Activity) context;
        }else {
            this.context=getActivity();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context.setTitle(R.string.text_SetCarrier);
        TypefaceProvider.registerDefaultIconSets();
        view = inflater.inflate(R.layout.ele_setcarrier, container, false);
        carrierVO=new CarrierVO();
        cellphone = view.findViewById(R.id.cellphone);
        certcode = view.findViewById(R.id.certcode);
        listcarrier = view.findViewById(R.id.listcarrier);
        confirm = view.findViewById(R.id.confirm);
        listtiitle=view.findViewById(R.id.listtiitle);
        progressbarL=view.findViewById(R.id.progressbarL);
        progressT=view.findViewById(R.id.progressT);
        percentage=view.findViewById(R.id.percentage);
        drawerLayout = this.context.findViewById(R.id.drawer_layout);
        confirm.setOnClickListener(new Confirmlisten());
        Common.setChargeDB(context);
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        sharedPreferences=context.getSharedPreferences("Charge_User",Context.MODE_PRIVATE);
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                drawerLayout.closeDrawer(GravityCompat.START);
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        setListAdapt();
        return view;
    }

    public void closeDialog()
    {
        progressbarL.setVisibility(View.GONE);
    }

    public void SucessDownload()
    {
        handler=new Handler();
        handler.postDelayed(runnable,500);
    }

    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            setListAdapt();
        }
    };




    @Override
    public void onStop() {
        super.onStop();
        if(getIvnum!=null)
        {
            getIvnum.cancel(true);
        }
        if(handler!=null)
        {
            handler.removeCallbacks(runnable);
        }
    }

    public void setListAdapt()
    {
        position=sharedPreferences.getInt("carrier",0);
        carrierlist=carrierDB.getAll();
        Intent intent = new Intent(context, SimpleWidgetProviderBig.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context.getApplication()).getAppWidgetIds(new ComponentName(context.getApplication(), SimpleWidgetProviderBig.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
        if(carrierlist!=null&&carrierlist.size()>0)
        {
            listtiitle.setVisibility(View.VISIBLE);
        }else{
            listtiitle.setVisibility(View.GONE);
        }
        Adapter adapter=listcarrier.getAdapter();
        if(adapter==null)
        {
            listcarrier.setAdapter(new EleSetCarrierAdapter(context,carrierlist));
        }else{
            EleSetCarrierAdapter adapter1 = (EleSetCarrierAdapter) listcarrier.getAdapter();
            adapter1.setCarNulList(carrierlist);
            adapter1.notifyDataSetChanged();
            listcarrier.invalidate();
        }
        closeDialog();
    }

    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("EleSetCarrier");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

    private class EleSetCarrierAdapter extends BaseAdapter {
        Context context;
        List<CarrierVO> CarNulList;

        EleSetCarrierAdapter(Context context, List<CarrierVO> CarNulList) {
            this.context = context;
            this.CarNulList = CarNulList;
        }

        public void setCarNulList(List<CarrierVO> carNulList) {
            CarNulList = carNulList;
        }

        @Override
        public int getCount() {
            return CarNulList.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.ele_set_item, parent, false);
            }
            final CarrierVO carrierVO = CarNulList.get(position);
            TextView tvId = (TextView) itemView.findViewById(R.id.tvId);
            BootstrapButton deletecarrier=itemView.findViewById(R.id.deletecarrier);
            BootstrapButton widgetShow=itemView.findViewById(R.id.widgetShow);
            BootstrapButton updateC=itemView.findViewById(R.id.updateC);
            widgetShow.setVisibility(View.VISIBLE);
            deletecarrier.setVisibility(View.VISIBLE);
            updateC.setVisibility(View.VISIBLE);
            //顯示有問題帳號
            if(Common.lostCarrier!=null)
            {
                for (CarrierVO c:Common.lostCarrier)
                {
                    if(c.getCarNul().equals(carrierVO.getCarNul()))
                    {
                        itemView.setBackgroundColor(Color.RED);
                        break;
                    }
                }

            }


            //小工具綁定工具列
            if(carrierlist.size()>1&&position!=EleSetCarrier.this.position)
            {
                widgetShow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPreferences.edit().putInt("carrier",position).apply();
                        setListAdapt();
                    }
                });
                widgetShow.setShowOutline(false);
                widgetShow.setBadgeText("選取");
                widgetShow.setEnabled(true);
            }else {
                widgetShow.setShowOutline(true);
                widgetShow.setText("已選取");
                widgetShow.setOnClickListener(null);
                widgetShow.setEnabled(false);
            }
            String show=carrierVO.getCarNul();
            tvId.setText(show);
            deletecarrier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DeleteDialogFragment aa= new DeleteDialogFragment();
                    aa.setObject(carrierVO);
                    aa.setFragement(EleSetCarrier.this);
                    aa.show(getFragmentManager(),"show");

                }
            });

            updateC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Fragment fragment=new EleUpdateCarrier();
                  Bundle bundle=new Bundle();
                  bundle.putSerializable("carrier",carrierVO);
                  fragment.setArguments(bundle);
                  switchFragment(fragment);
                }
            });
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return CarNulList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }


    private class Confirmlisten implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String user=cellphone.getText().toString().trim();
            String password=certcode.getText().toString().trim();
            if(user==null||user.isEmpty())
            {
                cellphone.setError("不能空白");
                return;
            }
            if(password==null||password.isEmpty())
            {
                certcode.setError("不能空白");
                return;
            }
            Boolean exist=checkExist(user);
            if(exist)
            {
                Common.showToast(context,"載具已新增過");
                return;
            }
            carrierVO.setCarNul(user);
            carrierVO.setPassword(password);
            ConnectivityManager mConnectivityManager = (ConnectivityManager)EleSetCarrier.this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if(mNetworkInfo!=null)
            {

                //close keyboard
                View v = EleSetCarrier.this.context.getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                getIvnum= (GetSQLDate) new GetSQLDate(EleSetCarrier.this);
                getIvnum.setProgressT(progressT);
                getIvnum.setPercentage(percentage);
                getIvnum.execute("getInvoice",user,password);
                progressbarL.setVisibility(View.VISIBLE);
            }else{
                Common.showToast(EleSetCarrier.this.context,"網路沒有開啟，無法下載!");
            }

        }
    }

    private Boolean checkExist(String carNul) {
        List <CarrierVO> carrierVOS=carrierDB.getAll();
        boolean exist=false;
        for(CarrierVO c:carrierVOS)
        {
            if(c.getCarNul().equals(carNul))
            {
                exist=true;
                break;
            }
        }
        return exist;
    }
}

package com.chargeapp.whc.chargeapp.Control.EleInvoice;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.R;



/**
 * Created by 1709008NB01 on 2017/12/20.
 */

public class EleUpdateCarrier extends Fragment {
    private BootstrapEditText cellphone,certcode;
    private ListView listcarrier;
    private BootstrapButton confirm;
    public GetSQLDate getIvnum;
    public CarrierDB carrierDB;
    public TextView listtiitle;
    private CarrierVO oldCarrierVO;
    private View view;
    private Activity context;
    private DrawerLayout drawerLayout;
    private RelativeLayout progressbarL;
    private TextView percentage,progressT;
    private CarrierVO carrierVO;

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
        view = inflater.inflate(R.layout.ele_setcarrier, container, false);
        context.setTitle("修改載具");
        cellphone = view.findViewById(R.id.cellphone);
        certcode = view.findViewById(R.id.certcode);
        listcarrier = view.findViewById(R.id.listcarrier);
        confirm = view.findViewById(R.id.confirm);
        listtiitle=view.findViewById(R.id.listtiitle);
        progressbarL=view.findViewById(R.id.progressbarL);
        percentage=view.findViewById(R.id.percentage);
        progressT=view.findViewById(R.id.progressT);

        percentage.setVisibility(View.GONE);
        progressT.setText("確認中...");

        drawerLayout = this.context.findViewById(R.id.drawer_layout);

        oldCarrierVO= (CarrierVO) getArguments().getSerializable("carrier");
        cellphone.setText(oldCarrierVO.getCarNul());
        certcode.setText(oldCarrierVO.getPassword());

        confirm.setOnClickListener(new Confirmlisten());
        Common.setChargeDB(context);
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                drawerLayout.closeDrawer(GravityCompat.START);
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        listtiitle.setVisibility(View.GONE);
        listcarrier.setVisibility(View.GONE);
        return view;
    }









    @Override
    public void onStop() {
        super.onStop();
        if(getIvnum!=null)
        {
            getIvnum.cancel(true);
        }
    }




    private class Confirmlisten implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String user=cellphone.getText().toString().trim();
            String password=certcode.getText().toString().trim();
            carrierVO=new CarrierVO();
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
            ConnectivityManager mConnectivityManager = (ConnectivityManager)EleUpdateCarrier.this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if(mNetworkInfo!=null)
            {

                //close keyboard
                View v = EleUpdateCarrier.this.context.getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                getIvnum= (GetSQLDate) new GetSQLDate(EleUpdateCarrier.this);
                getIvnum.execute("checkCarrier",user,password);
                carrierVO.setCarNul(user);
                carrierVO.setPassword(password);
                progressbarL.setVisibility(View.VISIBLE);
            }else{
                Common.showToast(EleUpdateCarrier.this.context,"網路沒有開啟，無法確認!");
                Common.showToast(EleUpdateCarrier.this.context,"修改失敗!");
            }

        }
    }

    public void check(String result)
    {
        if(result.indexOf("200")!=-1)
        {
            carrierVO.setId(oldCarrierVO.getId());
            carrierDB.update(carrierVO);
            InvoiceDB invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
            invoiceDB.updateCarrier(oldCarrierVO,carrierVO);
            int i=0;
            if(Common.lostCarrier!=null)
            {
                for (CarrierVO c:Common.lostCarrier)
                {
                    if(c.getCarNul().equals(oldCarrierVO.getCarNul()))
                    {
                        Common.lostCarrier.remove(i);
                        break;
                    }
                    i++;
                }

            }
            Fragment fragment=new EleSetCarrier();
            switchFragment(fragment);
            Common.showToast(context,"修改成功");
        }else{
            Common.showToast(context,"帳號密碼錯誤，修改失敗");
        }
        progressbarL.setVisibility(View.GONE);

    }




    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.remove(MainActivity.oldFramgent.getLast());
        MainActivity.bundles.remove(MainActivity.bundles.getLast());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }
}

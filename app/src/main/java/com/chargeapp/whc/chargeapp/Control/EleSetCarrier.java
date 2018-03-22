package com.chargeapp.whc.chargeapp.Control;

import android.app.Notification;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.R;

import org.apache.poi.ss.formula.functions.T;

import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/20.
 */

public class EleSetCarrier extends Fragment {
    private EditText cellphone,certcode;
    private ListView listcarrier;
    private TextView confirm;
    private InvoiceDB invoiceDB;
    public GetSQLDate getIvnum;
    private List<CarrierVO> carrierlist;
    public CarrierDB carrierDB;
    public TextView listtiitle;
    private ConsumeDB consumeDB;
    private RelativeLayout progressbarL;
    private ProgressBar progressBar;
    private  SharedPreferences sharedPreferences;
    private  int position;
    private CarrierVO carrierVO;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ele_setcarrier, container, false);
        carrierVO=new CarrierVO();
        cellphone = view.findViewById(R.id.cellphone);
        certcode = view.findViewById(R.id.certcode);
        listcarrier = view.findViewById(R.id.listcarrier);
        confirm = view.findViewById(R.id.confirm);
        listtiitle=view.findViewById(R.id.listtiitle);
        progressbarL=view.findViewById(R.id.progressbarL);
        progressBar=view.findViewById(R.id.progressbar);
        confirm.setOnClickListener(new Confirmlisten());
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB =new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        sharedPreferences=getActivity().getSharedPreferences("Charge_User",Context.MODE_PRIVATE);
        setListAdapt();
        return view;
    }

    public void closeDialog()
    {
        progressbarL.setVisibility(View.GONE);
    }

    public void setListAdapt()
    {
        position=sharedPreferences.getInt("carrier",0);
        carrierlist=carrierDB.getAll();
        Intent intent = new Intent(getActivity(), SimpleWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getActivity().getApplication()).getAppWidgetIds(new ComponentName(getActivity().getApplication(), SimpleWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        getActivity().sendBroadcast(intent);
        if(carrierlist!=null&&carrierlist.size()>0)
        {
            listtiitle.setVisibility(View.VISIBLE);
        }else{
            listtiitle.setVisibility(View.GONE);
        }
        Adapter adapter=listcarrier.getAdapter();
        if(adapter==null)
        {
            listcarrier.setAdapter(new EleSetCarrierAdapter(getActivity(),carrierlist));
        }else{
            EleSetCarrierAdapter adapter1 = (EleSetCarrierAdapter) listcarrier.getAdapter();
            adapter1.setCarNulList(carrierlist);
            adapter1.notifyDataSetChanged();
            listcarrier.invalidate();
        }
        closeDialog();
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
                itemView = layoutInflater.inflate(R.layout.ele_main_item, parent, false);
            }
            final CarrierVO carrierVO = CarNulList.get(position);
            TextView tvId = (TextView) itemView.findViewById(R.id.tvId);
            Button deletecarrier=itemView.findViewById(R.id.deletecarrier);
            Button widgetShow=itemView.findViewById(R.id.widgetShow);
            deletecarrier.setVisibility(View.VISIBLE);
            widgetShow.setVisibility(View.VISIBLE);
            //綁定工具列
            if(position!=EleSetCarrier.this.position)
            {
                widgetShow.setText("設置條碼");
                widgetShow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPreferences.edit().putInt("carrier",position).apply();
                        setListAdapt();
                    }
                });
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
                Common.showToast(getActivity(),"載具已新增過");
                return;
            }
            carrierVO.setCarNul(user);
            carrierVO.setPassword(password);
            carrierDB.insert(carrierVO);
            setListAdapt();
            getIvnum= (GetSQLDate) new GetSQLDate(EleSetCarrier.this).execute("getInvoice",user,password);
            getIvnum.setProgressBar(progressBar);
            progressbarL.setVisibility(View.VISIBLE);
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

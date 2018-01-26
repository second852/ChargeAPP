package com.chargeapp.whc.chargeapp.Control;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumerDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.EleMainItemVO;
import com.chargeapp.whc.chargeapp.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/20.
 */

public class EleSetCarrier extends Fragment {
    private EditText cellphone,certcode;
    private ListView listcarrier;
    private Button confirm;
    private InvoiceDB invoiceDB;
    public AsyncTask getIvnum;
    private List<CarrierVO> carrierlist;
    public CarrierDB carrierDB;
    public TextView listtiitle;
    private ConsumerDB consumerDB;
    private ProgressDialog progressDialog;
    private long time;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ele_setcarrier, container, false);
        cellphone = view.findViewById(R.id.cellphone);
        certcode = view.findViewById(R.id.certcode);
        listcarrier = view.findViewById(R.id.listcarrier);
        confirm = view.findViewById(R.id.confirm);
        listtiitle=view.findViewById(R.id.listtiitle);
        progressDialog=new ProgressDialog(getActivity());
        confirm.setOnClickListener(new Confirmlisten());
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumerDB=new ConsumerDB(MainActivity.chargeAPPDB.getReadableDatabase());
        setListAdapt();
        return view;
    }

    public void closeDialog()
    {
        progressDialog.cancel();
    }

    public void setListAdapt()
    {
        carrierlist=carrierDB.getAll();
        if(carrierlist!=null&&carrierlist.size()>0)
        {
            listtiitle.setVisibility(View.VISIBLE);
            listcarrier.setAdapter(new EleSetCarrierAdapter(getActivity(),carrierlist));
        }else{
            listtiitle.setVisibility(View.GONE);
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
            deletecarrier.setVisibility(View.VISIBLE);
            String show=carrierVO.getCarNul();
            tvId.setText(show);
            deletecarrier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    carrierDB.deleteByCarNul(carrierVO.getCarNul());
                    invoiceDB.deleteById(carrierVO.getCarNul());
                    setListAdapt();
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
            time=System.currentTimeMillis();
            getIvnum=new GetSQLDate(EleSetCarrier.this).execute("getInvoice",user,password);
            progressDialog.setMessage("正在下載資料,請稍候...");
            progressDialog.show();
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

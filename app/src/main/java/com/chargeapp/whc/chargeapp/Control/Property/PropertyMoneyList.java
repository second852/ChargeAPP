package com.chargeapp.whc.chargeapp.Control.Property;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapSize;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyFromDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.PropertyFromVO;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.R;

import java.util.Calendar;
import java.util.List;

import static com.chargeapp.whc.chargeapp.Control.Common.*;


/**
 * Created by Wang on 2019/3/12.
 */

public class PropertyMoneyList extends Fragment {

    private View view;
    private Activity activity;
    private BootstrapButton currency;
    private PropertyDB propertyDB;
    private SharedPreferences sharedPreferences;
    private String nowCurrency;
    private PopupMenu popupMenu;
    private CurrencyDB currencyDB;
    private CurrencyVO currencyVO;
    private Calendar start,end;
    private PropertyFromDB propertyFromDB;
    private String propertyId;
    private double total;
    private List<PropertyFromVO> propertyFromVOS;
    private TextView name;
    private ListView listData;
    private BootstrapButton insertFrom,returnMain;
    private PropertyVO propertyVO;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.activity=(Activity) context;
        }else{
            this.activity=getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.property_list_money, container, false);
        Object object=getArguments().getSerializable(Common.propertyID);
        if(object==null)
        {
            Common.homePageFragment(getFragmentManager(),activity);
            return view;
        }
        propertyId=object.toString();
        Common.setChargeDB(activity);
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        propertyDB=new PropertyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        propertyFromDB=new PropertyFromDB(MainActivity.chargeAPPDB.getReadableDatabase());
        propertyVO=propertyDB.findById(propertyId);
        findViewById();
        setPopupMenu();
        setNowMoney();
        setListView();
        return view;
    }

    private void setListView() {
        List<PropertyFromVO> propertyFromVOS=propertyFromDB.findByPropertyId(propertyId);
        listData.setAdapter(new ListAdapter(activity,propertyFromVOS));
    }

    private void setNowMoney() {
        total=0.0;
        propertyFromVOS=propertyFromDB.findByPropertyId(propertyId);
        for(PropertyFromVO propertyFromVO:propertyFromVOS)
        {
            CurrencyVO currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),propertyFromVO.getSourceCurrency());
            total=total+Double.valueOf(propertyFromVO.getSourceMoney())*Double.valueOf(currencyVO.getMoney());
        }
        currency.setText(Common.CurrencyResult(total,currencyVO));
    }

    private void setPopupMenu() {
        //找出現在選擇Currency
        start=Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY,0);
        start.set(Calendar.SECOND,0);
        start.set(Calendar.MINUTE,0);
        end=Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY,23);
        end.set(Calendar.MINUTE,59);
        end.set(Calendar.SECOND,59);
        sharedPreferences = activity.getSharedPreferences("Charge_User", Context.MODE_PRIVATE);
        nowCurrency = sharedPreferences.getString(propertyCurrency, "TWD");
        currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
        popupMenu=new PopupMenu(activity,currency);
        Common.createCurrencyPopMenu(popupMenu, activity);
        currency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        popupMenu.setOnMenuItemClickListener(new choiceCurrency());
    }

    private void findViewById() {
        name=view.findViewById(R.id.name);
        name.setText(propertyVO.getName());
        currency=view.findViewById(R.id.currency);
        listData=view.findViewById(R.id.listData);
        insertFrom=view.findViewById(R.id.insertFrom);
        insertFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new PropertyInsertMoney();
                Bundle bundle=new Bundle();
                bundle.putSerializable(Common.propertyID,propertyId);
                fragment.setArguments(bundle);
                Common.switchFragment(fragment,PropertyMoneyList,getFragmentManager());
            }
        });

        //返回
        returnMain=returnMain.findViewById(R.id.returnMain);
        returnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.switchConfirmFragment(new PropertyMain(),getFragmentManager());
            }
        });
    }


    private class choiceCurrency implements PopupMenu.OnMenuItemClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case 1:
                    nowCurrency = "TWD";
                    sharedPreferences.edit().putString(propertyCurrency, nowCurrency).apply();
                    currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
                    currency.setText(CurrencyResult(total,currencyVO));
                case 8:
                    popupMenu.dismiss();
                    break;
                default:
                    nowCurrency = Common.code.get(menuItem.getItemId() - 2);
                    sharedPreferences.edit().putString(propertyCurrency, nowCurrency).apply();
                    currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
                    currency.setText(CurrencyResult(total,currencyVO));
                    break;
            }
            return true;
        }
    }


    //Adapter
    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<PropertyFromVO> propertyFromVOS;

        public ListAdapter(Context context, List<PropertyFromVO> propertyFromVOS) {
            this.context = context;
            this.propertyFromVOS = propertyFromVOS;
        }

        @Override
        public int getCount() {
            return propertyFromVOS.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.property_list_from_detail, parent, false);
            }
            PropertyFromVO propertyFromVO=propertyFromVOS.get(position);
            TextView listTitle=itemView.findViewById(R.id.listTitle);
            TextView listDetail=itemView.findViewById(R.id.listDetail);
            StringBuilder title=new StringBuilder();
            title.append(propertyFromVO.getSourceId());
            title.append(" "+Common.getCurrency(propertyFromVO.getSourceCurrency()));
            title.append(" "+Common.doubleRemoveZero(Double.valueOf(propertyFromVO.getSourceMoney())));
            listTitle.setText(title.toString());
            StringBuilder detail=new StringBuilder();
            detail.append("1. 手續費 : ");
            detail.append(Common.getCurrency(propertyFromVO.getSourceCurrency())).append(propertyFromVO.getImportFee()+"\n");
            if(propertyFromVO.getFixImport())
            {
                detail.append("2. 定期匯入 : ").append(propertyFromVO.getFixDateCode().getName()).append(" "+propertyFromVO.getFixDateDetail());
            }
            listDetail.setText(detail.toString());
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return propertyFromVOS.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}

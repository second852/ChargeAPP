package com.chargeapp.whc.chargeapp.Control.Property;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapSize;
import com.chargeapp.whc.chargeapp.Adapter.DeleteDialogFragment;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyFromDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.PropertyFromVO;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.R;

import java.util.Calendar;
import java.util.List;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;

import org.jsoup.helper.StringUtil;

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
    private Long propertyId;
    private double total;
    private List<PropertyFromVO> propertyFromVOS;
    private TextView name;
    private ListView listData;
    private PropertyVO propertyVO;
    private FloatingActionButton fab;
    private LinearLayout insertMoney,insertConsume,returnMain;
    boolean isFABOpen=false;
    private View fabBGLayout;
    private String mainType,fragmentString,secondType;
    private Bundle bundle;
    private TextView message,totalString;


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
        bundle=getArguments();
        if(bundle==null)
        {
            Common.homePageFragment(getFragmentManager(),activity);
            return view;
        }
        propertyId= (Long) bundle.getSerializable(Common.propertyID);
        fragmentString= (String) bundle.getSerializable(Common.propertyFragment);
        Common.setChargeDB(activity);
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        propertyDB=new PropertyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        propertyFromDB=new PropertyFromDB(MainActivity.chargeAPPDB.getReadableDatabase());
        propertyVO=propertyDB.findById(propertyId);
        findViewById();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setPopupMenu();
        setListView();
        setNowMoney();
    }

    public void setListView() {


        switch (fragmentString)
        {
            case Common.PropertyTotalString:
                mainType= (String) bundle.getSerializable(Common.propertyMainType);
                propertyFromVOS=propertyFromDB.findByPropertyMainType(mainType,propertyId);
                totalString.setText(mainType+"總收入");
                break;
             case Common.propertyInsertString:
             case Common.propertyConsumeShowString:
                 secondType= (String) bundle.getSerializable(Common.propertySecondType);
                 propertyFromVOS=propertyFromDB.findByPropertySecondType(secondType,propertyId);
                 totalString.setText(secondType+"總支出");
                break;
        }

        if(propertyFromVOS.isEmpty())
        {
            message.setVisibility(View.VISIBLE);
        }else {
            message.setVisibility(View.GONE);
        }

        listData.setAdapter(new ListAdapter(activity,propertyFromVOS));
    }

    private void setNowMoney() {
        total=0.0;
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
        message=view.findViewById(R.id.message);
        name=view.findViewById(R.id.name);
        name.setText(propertyVO.getName());
        currency=view.findViewById(R.id.currency);
        listData=view.findViewById(R.id.listData);
        fabBGLayout=view.findViewById(R.id.fabBGLayout);

        insertConsume=view.findViewById(R.id.insertConsume);
        insertMoney= view.findViewById(R.id.insertMoney);
        returnMain= view.findViewById(R.id.returnMain);
        totalString=view.findViewById(R.id.totalString);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });
        insertMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BankDB bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
                if(bankDB.getAllTotal()<=0)
                {
                    Common.showToast(activity,"沒有資金，請增加收入!");
                    closeFABMenu();
                    return;
                }
                Fragment fragment=new PropertyInsertMoney();
                Bundle bundle=new Bundle();
                bundle.putSerializable(Common.propertyID,propertyId);
                fragment.setArguments(bundle);
                Common.switchFragment(fragment,Common.PropertyMoneyListString,getFragmentManager());
            }
        });

        insertConsume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConsumeDB consumeDB=new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
                if(consumeDB.getAllMoney()<=0)
                {
                    Common.showToast(activity,"沒有消費，無法歸類!");
                    closeFABMenu();
                    return;
                }
                Fragment fragment=new PropertyInsertConsume();
                Bundle bundle=new Bundle();
                bundle.putSerializable(Common.propertyID,propertyId);
                fragment.setArguments(bundle);
                Common.switchFragment(fragment,Common.PropertyMoneyListString,getFragmentManager());
            }
        });

        //返回
        returnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=Common.returnFragment();
                Common.switchConfirmFragment(fragment,getFragmentManager());
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
            setListView();
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
            final PropertyFromVO propertyFromVO=propertyFromVOS.get(position);
            TextView listTitle=itemView.findViewById(R.id.listTitle);
            TextView listDetail=itemView.findViewById(R.id.listDetail);
            BootstrapButton showD=itemView.findViewById(R.id.showD);
            BootstrapButton deleteI=itemView.findViewById(R.id.deleteI);
            StringBuilder title=new StringBuilder();


            if(StringUtil.isBlank(propertyFromVO.getSourceSecondType()))
            {
                title.append(propertyFromVO.getSourceMainType());
            }else{
                title.append(propertyFromVO.getSourceSecondType());
            }


            title.append(" "+Common.getCurrency(propertyFromVO.getSourceCurrency()));
            title.append(" "+Common.doubleRemoveZero(Double.valueOf(propertyFromVO.getSourceMoney())));
            listTitle.setText(title.toString());
            StringBuilder detail=new StringBuilder();
            detail.append("1. 日期 : "+Common.sTwo.format(propertyFromVO.getSourceTime())+" \n");
            detail.append("2. 手續費 : ");
            detail.append(Common.getCurrency(propertyFromVO.getSourceCurrency())).append(propertyFromVO.getImportFee()+"\n");
            if(propertyFromVO.getFixImport())
            {
                detail.append("3. 定期匯入 : ").append(propertyFromVO.getFixDateCode().getDetail());
                if(propertyFromVO.getFixDateDetail()!=null)
                {
                    detail.append(" "+propertyFromVO.getFixDateDetail());
                }
            }
            listDetail.setText(detail.toString());
            showD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment=null;
                    switch (propertyFromVO.getType())
                    {
                        case Positive:
                            fragment=new PropertyUpdateMoney();
                            break;
                        case Negative:
                            fragment=new PropertyUpdateConsume();
                            break;

                    }
                    bundle.putSerializable(Common.fragment,Common.PropertyMoneyListString);
                    bundle.putSerializable(Common.propertyFromVoId,propertyFromVO.getId());
                    fragment.setArguments(bundle);
                    Common.switchFragment(fragment,Common.PropertyMoneyListString,getFragmentManager());
                }
            });

            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteDialogFragment deleteObject= new DeleteDialogFragment();
                    deleteObject.setObject(propertyFromVO);
                    deleteObject.setFragement(PropertyMoneyList.this);
                    deleteObject.show(getFragmentManager(),"show");
                }
            });


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


    private void showFABMenu(){
        isFABOpen=true;
        insertConsume.setVisibility(View.VISIBLE);
        insertMoney.setVisibility(View.VISIBLE);
        returnMain.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);

        fab.animate().rotationBy(180).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                if (fab.getRotation() != 180) {
                    fab.setRotation(180);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        insertMoney.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        insertConsume.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        returnMain.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fabBGLayout.setVisibility(View.GONE);
        insertMoney.animate().translationY(0);
        fab.animate().rotationBy(-180);
        insertConsume.animate().translationY(0);
        returnMain.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen){
                    insertMoney.setVisibility(View.GONE);
                    returnMain.setVisibility(View.GONE);
                    insertConsume.setVisibility(View.GONE);
                }
                if (fab.getRotation() != -180) {
                    fab.setRotation(-180);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
}

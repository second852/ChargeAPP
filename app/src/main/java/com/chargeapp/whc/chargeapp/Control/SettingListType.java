package com.chargeapp.whc.chargeapp.Control;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SettingListType extends Fragment {


    private ListView listView;
    private TypeDB typeDB;
    private TypeDetailDB typeDetailDB;
    private BankTybeDB bankTybeDB;
    private int p;
    private Spinner typeH;
    private int spinnerC;
    private ArrayList<Object> objects;
    private Activity context;
    private AdView adView;

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
        View view = inflater.inflate(R.layout.setting_list, container, false);
        listView = view.findViewById(R.id.list);
        typeH = view.findViewById(R.id.typeH);


        adView = view.findViewById(R.id.adView);
        Common.setAdView(adView,context);
        Common.setChargeDB(context);
        typeDB = new TypeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
        bankTybeDB = new BankTybeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        p = (int) getArguments().getSerializable("position");
        spinnerC = (int) getArguments().getSerializable("spinnerC");
        typeH.setOnItemSelectedListener(new choiceType());
        setSpinner();
        setLayout();
        return view;
    }

    private void setSpinner() {
        ArrayList<String> spinnerItem = new ArrayList<>();
        spinnerItem.add("主項目種類");
        spinnerItem.add("次項目種類");
        spinnerItem.add("收入種類");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinneritem, spinnerItem);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        typeH.setAdapter(arrayAdapter);
        typeH.setSelection(spinnerC);
    }

    public void setLayout() {
        objects = new ArrayList<>();
        if (spinnerC == 0) {
            objects.addAll(typeDB.getAll());
        } else if (spinnerC == 1) {
            objects.addAll(typeDetailDB.getTypdAll());
        } else {
            objects.addAll(bankTybeDB.getAll());
        }
        ListAdapter adapter = (ListAdapter) listView.getAdapter();
        if (adapter == null) {
            listView.setAdapter(new ListAdapter(context, objects));
        } else {
            adapter.setObjects(objects);
            adapter.notifyDataSetChanged();
            listView.invalidate();
        }
        listView.setSelection(p);
    }


    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<Object> objects;

        ListAdapter(Context context, List<Object> objects) {
            this.context = context;
            this.objects = objects;
        }

        public void setObjects(List<Object> objects) {
            this.objects = objects;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.setting_list_item, parent, false);
            }
            ImageView image = itemView.findViewById(R.id.image);
            TextView listTitle = itemView.findViewById(R.id.listTitle);
            Button saveT = itemView.findViewById(R.id.saveT);
            Button deleteT = itemView.findViewById(R.id.deleteT);
            final Object o = objects.get(position);
            deleteT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteDialogFragment aa = new DeleteDialogFragment();
                    aa.setObject(o);
                    aa.setFragement(SettingListType.this);
                    aa.show(getFragmentManager(), "show");
                }
            });

            if (o instanceof TypeVO) {
                final TypeVO typeVO = (TypeVO) o;
                image.setImageResource(Download.imageAll[typeVO.getImage()]);
                listTitle.setText(typeVO.getName());
                saveT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment = new UpdateIncomeType();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("action","updateT");
                        bundle.putSerializable("typeVO", typeVO);
                        bundle.putSerializable("position", position);
                        bundle.putSerializable("spinnerC", spinnerC);
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });

            } else if (o instanceof TypeDetailVO) {
                final TypeDetailVO typeDetailVO = (TypeDetailVO) o;
                image.setImageResource(Download.imageAll[typeDetailVO.getImage()]);
                listTitle.setText(typeDetailVO.getName());
                saveT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        Fragment fragment = new UpdateConsumeType();
                        bundle.putSerializable("TypeDetailVO", typeDetailVO);
                        bundle.putSerializable("position", position);
                        bundle.putSerializable("spinnerC", spinnerC);
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });
            } else {
                final BankTypeVO bankTypeVO = (BankTypeVO) o;
                image.setImageResource(Download.imageAll[bankTypeVO.getImage()]);
                listTitle.setText(bankTypeVO.getName());
                saveT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        Fragment fragment = new UpdateIncomeType();
                        bundle.putSerializable("action","updateB");
                        bundle.putSerializable("bankTypeVO", bankTypeVO);
                        bundle.putSerializable("position", position);
                        bundle.putSerializable("spinnerC", spinnerC);
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });
            }
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }


    private void switchFragment(Fragment fragment) {
        //紀錄
        MainActivity.oldFramgent.add("SettingListType");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

    private class choiceType implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            spinnerC = i;
            SettingListType.this.setLayout();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}

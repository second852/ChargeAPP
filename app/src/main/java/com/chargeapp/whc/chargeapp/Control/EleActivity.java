package com.chargeapp.whc.chargeapp.Control;



import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.Model.EleMainItemVO;
import com.chargeapp.whc.chargeapp.R;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class EleActivity extends Fragment {
    private ListView elefunction;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ele_main, container, false);
        elefunction=view.findViewById(R.id.elefunction);
        List<EleMainItemVO> eleMainItemVOList=getElemainItemList();
        elefunction.setAdapter(new EleMainItemVOAdapter(getActivity(),eleMainItemVOList));
        elefunction.setOnItemClickListener(new choiceItemFramgent());
        return view;
    }

    private List<EleMainItemVO> getElemainItemList() {
        List<EleMainItemVO> list=new ArrayList<>();
        list.add(new EleMainItemVO("綁訂/取消通用性載具",R.drawable.cellphone));
        list.add(new EleMainItemVO("捐贈發票",R.drawable.health));
        list.add(new EleMainItemVO("如何綁定載具",R.drawable.easygo));
        list.add(new EleMainItemVO("申請通用性載具",R.drawable.barcode));
        list.add(new EleMainItemVO("手機條碼綁定金融帳戶",R.drawable.bank));
        list.add(new EleMainItemVO("電子發票如何兌獎",R.drawable.invent));
        list.add(new EleMainItemVO("電子發票是什麼?",R.drawable.image));
        return list;
    }


    private class EleMainItemVOAdapter extends BaseAdapter {
        Context context;
        List<EleMainItemVO> eleMainItemVOList;

        EleMainItemVOAdapter(Context context, List<EleMainItemVO> eleMainItemVOList) {
            this.context = context;
            this.eleMainItemVOList = eleMainItemVOList;
        }

        @Override
        public int getCount() {
            return eleMainItemVOList.size();
        }

        @Override
        public View getView(int position, View itemView, ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.ele_main_item, parent, false);
            }

            EleMainItemVO member = eleMainItemVOList.get(position);
            ImageView ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            ivImage.setImageResource(member.getImage());

            TextView tvId = (TextView) itemView.findViewById(R.id.tvId);
            tvId.setText(String.valueOf(member.getName()));
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return eleMainItemVOList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }


    private class choiceItemFramgent implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               Fragment fragment=null;
               FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction().addToBackStack("Elemain");
               if(i==0)
               {
                   elefunction.setOnItemClickListener(null);
                  fragment=new EleSetCarrier();
                  fragmentTransaction.add(R.id.elemain,fragment);
                  fragmentTransaction.commit();
               }else if(i==1)
               {
                   fragment=new EleDonateMain();
                   fragmentTransaction.replace(R.id.body, fragment);
                   fragmentTransaction.commit();
               }else if(i==2)
               {
                   Intent intent = new Intent();
                   intent.setAction(Intent.ACTION_VIEW);
                   intent.setData(Uri.parse("http://www.teach.ltu.edu.tw/public/News/11503/201412041535091.pdf"));
                   startActivity(intent);

               }else if(i==3)
               {
                   fragment=new EleNewCarrier();
                   fragmentTransaction.replace(R.id.body, fragment);
                   fragmentTransaction.commit();

               }else if(i==4)
               {
                   fragment=new EleAddBank();
                   fragmentTransaction.replace(R.id.body, fragment);
                   fragmentTransaction.commit();
               }else if(i==5){
                   Intent intent = new Intent(getActivity(), HowGetPrice.class);
                   startActivity(intent);
               }else{
                   Intent intent = new Intent();
                   intent.setAction(Intent.ACTION_VIEW);
                   intent.setData(Uri.parse("http://www.nknu.edu.tw/~psl/new.file/103/08/1030825reciept1.pdf"));
                   startActivity(intent);
               }
        }
    }
}

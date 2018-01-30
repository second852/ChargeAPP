package com.chargeapp.whc.chargeapp.Control;




import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import java.util.List;



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
               List<Fragment> fragments=getFragmentManager().getFragments();
               for(Fragment f:fragments)
               {
                   fragmentTransaction.remove(f);
               }
               fragmentTransaction.addToBackStack(null);
               if(i==0)
               {
                  fragment=new EleSetCarrier();
                  getActivity().setTitle(R.string.text_SetCarrier);
                  fragmentTransaction.add(R.id.body,fragment);
                  fragmentTransaction.commit();
               }else if(i==1)
               {
                   getActivity().setTitle(R.string.text_DonateMain);
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
                   getActivity().setTitle(R.string.text_NewCarrier);
                   fragment=new EleNewCarrier();
                   fragmentTransaction.replace(R.id.body, fragment);
                   fragmentTransaction.commit();
               }else if(i==4)
               {
                   getActivity().setTitle(R.string.text_EleBank);
                   fragment=new EleAddBank();
                   fragmentTransaction.replace(R.id.body, fragment);
                   fragmentTransaction.commit();
               }else if(i==5){
                   getActivity().setTitle(R.string.text_HowGet);
                   fragment=new HowGetPrice();
                   fragmentTransaction.replace(R.id.body, fragment);
                   fragmentTransaction.commit();
               }else{
                   Intent intent = new Intent();
                   intent.setAction(Intent.ACTION_VIEW);
                   intent.setData(Uri.parse("http://www.nknu.edu.tw/~psl/new.file/103/08/1030825reciept1.pdf"));
                   startActivity(intent);
               }
        }
    }
}

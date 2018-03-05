package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.Model.EleMainItemVO;
import com.chargeapp.whc.chargeapp.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SettingUploadFile extends Fragment {


    private ListView listView;
    private LinearLayout fileChoice;
    private ImageView excel, txtFile,cancelF;
    private Boolean isExport,isShow=true;
    private String action;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_main, container, false);
        List<EleMainItemVO> itemSon = getNewItem();
        listView = view.findViewById(R.id.list);
        fileChoice = view.findViewById(R.id.fileChoice);
        excel = view.findViewById(R.id.excel);
        txtFile = view.findViewById(R.id.txtFile);
        cancelF=view.findViewById(R.id.cancelF);
        excel.setOnClickListener(new excelOnClick());
        txtFile.setOnClickListener(new txtOnClick());
        cancelF.setOnClickListener(new cancelOnClick());
        listView.setAdapter(new ListAdapter(getActivity(), itemSon));
        return view;
    }

    private List<EleMainItemVO> getNewItem() {
        List<EleMainItemVO> eleMainItemVOList = new ArrayList<>();
        eleMainItemVOList.add(new EleMainItemVO("匯出資料到本地端", R.drawable.importf));
        eleMainItemVOList.add(new EleMainItemVO("匯出資料到Google雲端", R.drawable.importf));
        eleMainItemVOList.add(new EleMainItemVO("從本地端匯入資料", R.drawable.export));
        eleMainItemVOList.add(new EleMainItemVO("從Google雲端匯入資料", R.drawable.export));
        return eleMainItemVOList;
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
            TextView textView = itemView.findViewById(R.id.listTitle);
            imageView.setImageResource(eleMainItemVO.getImage());
            textView.setText(eleMainItemVO.getName());
            Switch notify = itemView.findViewById(R.id.notify);
            TextView setTime = itemView.findViewById(R.id.setTime);
            setTime.setVisibility(View.GONE);
            notify.setVisibility(View.GONE);
            if (position == 0) {

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isShow) {
                            fileChoice.setVisibility(View.VISIBLE);
                            isExport = true;
                            isShow=false;
                            action="local";
                        }
                    }
                });

            } else if (position == 1) {

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isShow) {
                            fileChoice.setVisibility(View.VISIBLE);
                            isExport = true;
                            isShow=false;
                            action="google";
                        }
                    }
                });

            } else if (position == 2) {

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isShow) {
                            fileChoice.setVisibility(View.VISIBLE);
                            isExport = false;
                            isShow=false;
                            action="local";
                        }
                    }
                });

            } else if (position == 3) {

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isShow) {
                            fileChoice.setVisibility(View.VISIBLE);
                            isExport = false;
                            isShow=false;
                            action="google";
                        }
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

    private class excelOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            fileChoice.setVisibility(View.GONE);
            isShow=true;
            if(isExport)
            {

                if(action.equals("google"))
                {
                    Common.showToast(getActivity(),"google輸出Excel");
                }else{
                    Common.showToast(getActivity(),"local輸出Excel");
                }

            }else{

                if(action.equals("google"))
                {
                    Common.showToast(getActivity(),"google輸入excel");
                }else{
                    Common.showToast(getActivity(),"local輸入excel");
                }

            }

        }
    }

    private class txtOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            fileChoice.setVisibility(View.GONE);
            isShow=true;
            if(isExport)
            {

                if(action.equals("google"))
                {
                    Common.showToast(getActivity(),"google輸出txt");
                }else{
                    Common.showToast(getActivity(),"local輸出txt");
                }

            }else{

                if(action.equals("google"))
                {
                    Common.showToast(getActivity(),"google輸入txt");
                }else{
                    Common.showToast(getActivity(),"local輸入txt");
                }

            }
        }
    }

    private class cancelOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            fileChoice.setVisibility(View.GONE);
            isShow=true;
        }
    }
}

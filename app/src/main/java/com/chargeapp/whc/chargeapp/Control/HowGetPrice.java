package com.chargeapp.whc.chargeapp.Control;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.TextView;


import com.chargeapp.whc.chargeapp.R;




import java.util.ArrayList;
import java.util.List;


public class HowGetPrice extends Fragment {
    private TextView needcarrier;
    private ListView list;
    private Activity context;
    private DrawerLayout drawerLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = (Activity) context;
        } else {
            this.context = getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context.setTitle(R.string.text_HowGet);;
        final View view = inflater.inflate(R.layout.how_get_price, container, false);
        needcarrier = view.findViewById(R.id.needcarrier);
        needcarrier.setText("1.需攜帶中獎發票、中獎人印章、身分證到郵局領獎。\n\n2.無實體發票需列印出來，並攜帶此中獎發票、中獎人印章、身分證到郵局領獎。如果手機載具有綁定帳戶，不需要上述步驟，會自動匯到該戶頭。\n\n3.特別獎、特獎、頭獎及無實體雲端發票專屬百萬獎：中獎金額20萬元以上（含20萬元），請至25處指定郵局儲匯窗口兌領。");
        list = view.findViewById(R.id.list);
        List<String> title = new ArrayList<>();
        title.add("如何印無實體中獎發票");
        title.add("25處指定郵局兌領位置");
        title.add("雲端發票綁定銀行帳戶\n(財政部網站)");
        list.setAdapter(new ListAdapter(context, title));
        drawerLayout = this.context.findViewById(R.id.drawer_layout);
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                drawerLayout.closeDrawer(GravityCompat.START);
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        return view;
    }

    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<String> strings;

        ListAdapter(Context context, List<String> strings) {
            this.context = context;
            this.strings = strings;
        }


        public void setObjects(List<Object> objects) {
            this.strings = strings;
        }

        @Override
        public int getCount() {
            return strings.size();
        }

        @Override
        public Object getItem(int position) {
            return strings.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.ele_hand_item, parent, false);
            }
            final Button cardView = itemView.findViewById(R.id.cardview);
            cardView.setText(strings.get(position));
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == 0) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://event.family.com.tw/2016_invoice/invoice.html"));
                        startActivity(intent);
                    } else if (position == 1) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.post.gov.tw/post/internet/B_saving/index.jsp?ID=30306#localpost"));
                        startActivity(intent);
                    }  else if (position == 2) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.einvoice.nat.gov.tw/APMEMBERVAN/GeneralCarrier/generalCarrier!login"));
                        startActivity(intent);
                    }
                }
            });
            return itemView;
        }
    }
}

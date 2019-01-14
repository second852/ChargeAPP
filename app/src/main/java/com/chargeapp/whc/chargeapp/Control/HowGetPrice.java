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
//        needcarrier.setText("1.需攜帶中獎發票和身分證。\n\n2.無實體發票需列印出來，並攜帶此中獎發票和身分證到郵局領獎。如果手機載具有綁定帳戶，不需要上述步驟，會自動匯到該戶頭。");
        list = view.findViewById(R.id.list);
        List<String> title = new ArrayList<>();
        title.add("據點");
        title.add("第一銀行、彰化銀行、全國農業金庫、金門縣信用合作社、連江縣農會信用部");
        title.add("四大超商、全聯、美聯社");
        title.add("統一發票兌獎APP");
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
                itemView = layoutInflater.inflate(R.layout.how_adapter_item, parent, false);
            }
            TextView port=itemView.findViewById(R.id.port);
            TextView price=itemView.findViewById(R.id.price);
            TextView time=itemView.findViewById(R.id.time);
            switch (position)
            {
                case 0:
                    port.setText(strings.get(0));
                    price.setText("獎別");
                    time.setText("服務時間");
                    break;
                case 1:
                    port.setText(strings.get(1));
                    price.setText("全部獎品");
                    time.setText("於營業時間內兌換");
                    break;
                case 2:
                    port.setText(strings.get(2));
                    price.setText("二獎以下獎品\n雲端發票專屬千元獎");
                    time.setText("於營業時間內兌換");
                    break;
                case 3:
                    port.setText(strings.get(3));
                    price.setText("五獎\n六獎");
                    time.setText("9~23點(兌換現金、等值商品、儲值金)\n其餘時間(等值商品、儲值金)");
                    break;
                case 4:
                    port.setText(strings.get(4));
                    price.setText("全部獎項(此期開獎前以綁定回款帳戶)\n五獎、六獎(此期開獎後，需要電子平鎮)");
                    time.setText("24小時皆可兌換");
                    break;
            }



            return itemView;
        }
    }
}

package com.chargeapp.whc.chargeapp.Control.Price;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;

import android.widget.ListView;
import android.widget.TextView;


import com.beardedhen.androidbootstrap.BootstrapButton;
import com.chargeapp.whc.chargeapp.R;




import java.util.ArrayList;
import java.util.List;


public class HowGetPrice extends Fragment {

    private ListView list;
    private Activity context;
    private DrawerLayout drawerLayout;
    private TextView needCarrier;
    private BootstrapButton button;

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
        needCarrier=view.findViewById(R.id.needCarrier);
        button=view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=tw.gov.invoice"));
                startActivity(intent);
            }
        });
        needCarrier.setText("1.需攜帶中獎發票和身分證，到下列地點兌換。\n2.無實體發票需列印出來，並攜帶此中獎發票和身分證到到下列地點兌換。如果手機載具有綁定帳戶，不需要上述步驟，會自動匯到該戶頭。");
        list = view.findViewById(R.id.list);
        List<String> title = new ArrayList<>();
        title.add("據點");
        title.add("第一銀行、彰化銀行、全國農業金庫、金門縣信用合作社、連江縣農會信用部");
        title.add("指定之信用合作社、農/漁會信用部");
        title.add("四大超商、全聯、美聯社");
        title.add("統一發票兌獎APP");
        list.setDividerHeight(0);
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
            Drawable drawable=null;
            switch (position)
            {
                case 0:
                    drawable=context.getResources().getDrawable(R.drawable.show_date_model_1);
                    port.setText(strings.get(0));
                    price.setText("獎別");
                    time.setText("服務時間");
                    break;
                case 1:
                    drawable=context.getResources().getDrawable(R.drawable.show_date_model_2);
                    port.setText(strings.get(1));
                    price.setText("全部獎品");
                    time.setText("於營業時間內兌換");
                    break;
                case 2:
                    drawable=context.getResources().getDrawable(R.drawable.show_date_model_2);
                    port.setText(strings.get(2));
                    price.setText("二獎以下獎品\n雲端發票專屬千元獎");
                    time.setText("於營業時間內兌換");
                    break;
                case 3:
                    drawable=context.getResources().getDrawable(R.drawable.show_date_model_2);
                    port.setText(strings.get(3));
                    price.setText("五獎\n六獎");
                    time.setText("9~23點(兌換現金、等值商品、儲值金)\n其餘時間(等值商品、儲值金)");
                    break;
                case 4:
                    drawable=context.getResources().getDrawable(R.drawable.show_date_model_2);
                    port.setText(strings.get(4));
                    price.setText("五獎、六獎(需要實體中獎發票，用此APP上傳)");
                    time.setText("24小時皆可兌換");
                    break;
            }
            port.setBackground(drawable);
            price.setBackground(drawable);
            time.setBackground(drawable);
            return itemView;
        }
    }
}

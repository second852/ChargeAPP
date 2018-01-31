package com.chargeapp.whc.chargeapp.Control;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.chargeapp.whc.chargeapp.R;



public class HowGetPrice extends Fragment {
  private TextView needcarrier;
  private TextView local,setbank,goWeb,goprintWeb;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.how_get_price, container, false);
        needcarrier=view.findViewById(R.id.needcarrier);
        needcarrier.setText("1.需攜帶中獎發票、中獎人印章、身分證到郵局領獎。\n\n2.無實體發票需列印出來，如果有綁定帳戶會自動匯到該戶頭。\n\n3.特別獎、特獎、頭獎及無實體電子發票專屬百萬獎：中獎金額20萬元以上（含20萬元），請至25處指定郵局儲匯窗口兌領。");
        local=view.findViewById(R.id.local);
        setbank=view.findViewById(R.id.setbank);
        goWeb=view.findViewById(R.id.goWeb);
        goprintWeb=view.findViewById(R.id.goprintWeb);
        goprintWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://event.family.com.tw/2016_invoice/invoice.html"));
                startActivity(intent);
            }
        });
        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.post.gov.tw/post/internet/B_saving/index.jsp?ID=30306#localpost"));
                startActivity(intent);
            }
        });
        setbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setTitle(R.string.text_EleBank);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                for (Fragment f: getFragmentManager().getFragments())
                {
                    fragmentTransaction.remove(f);
                }
                Fragment fragment=new EleAddBank();
                fragmentTransaction.replace(R.id.body, fragment);
                fragmentTransaction.commit();
            }
        });
        goWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.einvoice.nat.gov.tw/APMEMBERVAN/GeneralCarrier/generalCarrier!login"));
                startActivity(intent);
            }
        });
     return view;
    }

}

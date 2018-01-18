package com.chargeapp.whc.chargeapp.Control;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.chargeapp.whc.chargeapp.R;



public class HowGetPrice extends AppCompatActivity {
  private TextView needcarrier;
  private TextView local,setbank,goWeb,goprintWeb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.how_get_price);
        needcarrier=findViewById(R.id.needcarrier);
        needcarrier.setText("1.需攜帶中獎發票、中獎人印章、身分證到郵局領獎。\n\n2.無實體發票需列印出來，如果有綁定帳戶會自動匯到該戶頭。\n\n3.特別獎、特獎、頭獎及無實體電子發票專屬百萬獎：中獎金額20萬元以上（含20萬元），請至25處指定郵局儲匯窗口兌領。");
        local=findViewById(R.id.local);
        setbank=findViewById(R.id.setbank);
        goWeb=findViewById(R.id.goWeb);
        goprintWeb=findViewById(R.id.goprintWeb);
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
                setbank.setOnClickListener(null);
                goWeb.setOnClickListener(null);
                local.setOnClickListener(null);
                Fragment fragment=null;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragment=new EleAddBank();
                fragmentTransaction.replace(R.id.howgetpage, fragment);
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



    }

}

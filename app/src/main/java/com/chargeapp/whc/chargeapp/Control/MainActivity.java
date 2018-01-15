package com.chargeapp.whc.chargeapp.Control;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetail;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {



    private GridView gridView;
    public static ChargeAPPDB chargeAPPDB;
    private int[] image = {
            R.drawable.book, R.drawable.goal, R.drawable.chart, R.drawable.ele,R.drawable.lotto,R.drawable.setting
    };
    private String[] imgText = {
            "記帳", "目標", "圖表", "電子發票","發票兌獎","設定"
    };

    public static int[] imageAll = {
            R.drawable.food, R.drawable.phone, R.drawable.clothes, R.drawable.traffic, R.drawable.teach, R.drawable.happy,
            R.drawable.home, R.drawable.medical, R.drawable.invent,
            R.drawable.worker, R.drawable.bus, R.drawable.breakfast, R.drawable.cellphone, R.drawable.clothe, R.drawable.dinner,
            R.drawable.drink, R.drawable.easycard, R.drawable.easygo, R.drawable.electricty, R.drawable.fruit,
            R.drawable.gasstation, R.drawable.highspeedtrain, R.drawable.hospital, R.drawable.image, R.drawable.internet,
            R.drawable.jacket, R.drawable.water, R.drawable.university, R.drawable.trousers, R.drawable.treatment, R.drawable.training,
            R.drawable.ticket, R.drawable.supplement, R.drawable.subway, R.drawable.rent, R.drawable.rentfilled, R.drawable.phonet,
            R.drawable.origin, R.drawable.movie, R.drawable.microphone, R.drawable.lunch, R.drawable.losemoney, R.drawable.lipgloss,R.drawable.train
            ,R.drawable.salary,R.drawable.lotto,R.drawable.bouns,R.drawable.interest,R.drawable.fund,R.drawable.bank
    };

    private String food="堡 三明治 優酪乳 肉 飯 雙手卷 腿 麵 麵包 熱狗 雞 手卷 肉 粉 蔬菜 牛 豬 起司 花生 豆 蛋 魚 菜 瓜 黑胡椒 土司 泡芙 排";
    private String drink="咖啡 茶 豆漿 拿鐵 乳 飲 ml 罐 酒 杯 水";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askPermissions();
//        deleteDatabase("ChargeAPP");
        if (chargeAPPDB == null) {
            setdate();
        }
        setContentView(R.layout.activity_main);
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < image.length; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("image", image[i]);
            item.put("text", imgText[i]);
            items.add(item);
        }
        SimpleAdapter adapter = new SimpleAdapter(this,
                items, R.layout.main_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        gridView = findViewById(R.id.mainGrid);
        gridView.setNumColumns(2);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(MainActivity.this, InsertActivity.class);
                    startActivity(intent);
                }
                if (position == 2) {
                    Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                    startActivity(intent);
                }
                if (position == 3) {
                    Intent intent = new Intent(MainActivity.this, EleActivity.class);
                    startActivity(intent);
                }
                if (position == 4) {
                    Intent intent = new Intent(MainActivity.this, PriceActivity.class);
                    startActivity(intent);
                }
            }
        });
    }


    private void setdate() {
        chargeAPPDB =new ChargeAPPDB(this);
        TypeDB typeDB=new TypeDB(chargeAPPDB.getReadableDatabase());
        TypeDetail typeDetail=new TypeDetail(chargeAPPDB.getReadableDatabase());
        List<TypeVO> typeVOS=typeDB.getAll();
        if(typeVOS.size()>0)
        {
          return;
        }
        typeDB.insert(new TypeVO("food", "食物",0));
        typeDB.insert(new TypeVO("phone", "手機", 1));
        typeDB.insert(new TypeVO("clothes", "衣服", 2));
        typeDB.insert(new TypeVO("traffic", "交通", 3));
        typeDB.insert(new TypeVO("teach", "教育", 4));
        typeDB.insert(new TypeVO("happy", "娛樂", 5));
        typeDB.insert(new TypeVO("home", "住宿", 6));
        typeDB.insert(new TypeVO("medical", "醫療", 7));
        typeDB.insert(new TypeVO("invent", "投資", 8));
        typeDetail.insert(new TypeDetailVO("食物","早餐", indexOfIntArray(imageAll,R.drawable.breakfast),food));
        typeDetail.insert(new TypeDetailVO("食物","午餐", indexOfIntArray(imageAll,R.drawable.lunch),food));
        typeDetail.insert(new TypeDetailVO("食物","晚餐", indexOfIntArray(imageAll,R.drawable.dinner),food));
        typeDetail.insert(new TypeDetailVO("食物","飲料", indexOfIntArray(imageAll,R.drawable.drink),drink));
        typeDetail.insert(new TypeDetailVO("食物","水果", indexOfIntArray(imageAll,R.drawable.fruit),"蘋果 鳳梨 瓜 蕉 葡萄 蓮霧 番石榴 李 果 檬 橙 莓 椰子 桃 橘 柚 姆 柑 棗 蓮"));
        typeDetail.insert(new TypeDetailVO("手機","電話費", indexOfIntArray(imageAll,R.drawable.phonet),"中華 市話"));
        typeDetail.insert(new TypeDetailVO("手機","月租費", indexOfIntArray(imageAll,R.drawable.cellphone),"月租費"));
        typeDetail.insert(new TypeDetailVO("手機","易付卡", indexOfIntArray(imageAll,R.drawable.easycard),"易付卡 卡"));
        typeDetail.insert(new TypeDetailVO("手機","網路費",indexOfIntArray(imageAll,R.drawable.internet),"M 上網 寬頻 光纖"));
        typeDetail.insert(new TypeDetailVO("交通","火車", indexOfIntArray(imageAll,R.drawable.train)));
        typeDetail.insert(new TypeDetailVO("交通","高鐵", indexOfIntArray(imageAll,R.drawable.highspeedtrain)));
        typeDetail.insert(new TypeDetailVO("交通","捷運", indexOfIntArray(imageAll,R.drawable.subway)));
        typeDetail.insert(new TypeDetailVO("交通","客運", indexOfIntArray(imageAll,R.drawable.bus)));
        typeDetail.insert(new TypeDetailVO("交通","加油",indexOfIntArray(imageAll,R.drawable.gasstation),"汽油 92 95 98"));
        typeDetail.insert(new TypeDetailVO("教育","補習費", indexOfIntArray(imageAll,R.drawable.training)));
        typeDetail.insert(new TypeDetailVO("教育","學雜費", indexOfIntArray(imageAll,R.drawable.university)));
        typeDetail.insert(new TypeDetailVO("娛樂","電影",indexOfIntArray(imageAll,R.drawable.movie)));
        typeDetail.insert(new TypeDetailVO("娛樂","KTV", indexOfIntArray(imageAll,R.drawable.microphone)));
        typeDetail.insert(new TypeDetailVO("娛樂","門票", indexOfIntArray(imageAll,R.drawable.ticket)));
        typeDetail.insert(new TypeDetailVO("住宿","水費", indexOfIntArray(imageAll,R.drawable.water)));
        typeDetail.insert(new TypeDetailVO("住宿","電費", indexOfIntArray(imageAll,R.drawable.electricty)));
        typeDetail.insert(new TypeDetailVO("住宿","瓦斯", indexOfIntArray(imageAll,R.drawable.gasstation)));
        typeDetail.insert(new TypeDetailVO("住宿","房租", indexOfIntArray(imageAll,R.drawable.rent)));
        typeDetail.insert(new TypeDetailVO("住宿","房貸",indexOfIntArray(imageAll,R.drawable.rentfilled)));
        typeDetail.insert(new TypeDetailVO("醫療","健保",indexOfIntArray(imageAll,R.drawable.health)));
        typeDetail.insert(new TypeDetailVO("醫療","勞保", indexOfIntArray(imageAll,R.drawable.water)));
        typeDetail.insert(new TypeDetailVO("醫療","醫療", indexOfIntArray(imageAll,R.drawable.medical)));
        typeDetail.insert(new TypeDetailVO("醫療","保健食品", indexOfIntArray(imageAll,R.drawable.supplement)));
        typeDetail.insert(new TypeDetailVO("投資","保險", indexOfIntArray(imageAll,R.drawable.treatment)));
        typeDetail.insert(new TypeDetailVO("投資","投資損失", indexOfIntArray(imageAll,R.drawable.losemoney)));
        typeDetail.insert(new TypeDetailVO("衣服","衣服", indexOfIntArray(imageAll,R.drawable.jacket),"衣 外套 套 袖 shirt 棉"));
        typeDetail.insert(new TypeDetailVO("衣服","褲子",indexOfIntArray(imageAll,R.drawable.trousers),"褲"));
        typeDetail.insert(new TypeDetailVO("衣服","內衣褲", indexOfIntArray(imageAll,R.drawable.clothe),"罩 內褲"));
        typeDetail.insert(new TypeDetailVO("衣服","鞋子", indexOfIntArray(imageAll,R.drawable.shose),"鞋"));
        typeDetail.insert(new TypeDetailVO("衣服","化妝品", indexOfIntArray(imageAll,R.drawable.lipgloss)));
        BankTybeDB bankTybeDB=new BankTybeDB(chargeAPPDB.getReadableDatabase());
        bankTybeDB.insert(new BankTypeVO("薪水","薪水",indexOfIntArray(imageAll,R.drawable.salary)));
        bankTybeDB.insert(new BankTypeVO("薪水","中獎",indexOfIntArray(imageAll,R.drawable.lotto)));
        bankTybeDB.insert(new BankTypeVO("薪水","獎金",indexOfIntArray(imageAll,R.drawable.bouns)));
        bankTybeDB.insert(new BankTypeVO("薪水","基金",indexOfIntArray(imageAll,R.drawable.fund)));
        bankTybeDB.insert(new BankTypeVO("薪水","股票",indexOfIntArray(imageAll,R.drawable.origin)));
        bankTybeDB.insert(new BankTypeVO("薪水","利息",indexOfIntArray(imageAll,R.drawable.bank)));
        bankTybeDB.insert(new BankTypeVO("薪水","股利",indexOfIntArray(imageAll,R.drawable.interest)));
    }

    public static int indexOfIntArray(int[] array, int key) {
        int returnvalue = 0;
        for (int i = 0; i < array.length; ++i) {
            if (key == array[i]) {
                returnvalue = i;
                break;
            }
        }
        return returnvalue;
    }

    private static final int REQ_PERMISSIONS = 0;

    public void askPermissions() {
        //因為是群組授權，所以請求ACCESS_COARSE_LOCATION就等同於請求ACCESS_FINE_LOCATION，因為同屬於LOCATION群組
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    REQ_PERMISSIONS);
        }
    }


}
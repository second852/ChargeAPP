package com.chargeapp.whc.chargeapp.Control;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;


import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.EleMainItemVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private  ExpandableListView listView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public static ChargeAPPDB chargeAPPDB;
    private int position=6;
    private View oldview;
    private int[] image = {
            R.drawable.book, R.drawable.goal, R.drawable.chart, R.drawable.ele, R.drawable.lotto, R.drawable.setting
    };
    private String[] imgText = {
            "記帳", "目標", "圖表", "電子發票", "發票兌獎", "設定"
    };

    public static int[] imageAll = {
            R.drawable.food, R.drawable.phone, R.drawable.clothes, R.drawable.traffic, R.drawable.teach, R.drawable.happy,
            R.drawable.home, R.drawable.medical, R.drawable.invent,
            R.drawable.worker, R.drawable.bus, R.drawable.breakfast, R.drawable.cellphone, R.drawable.clothe, R.drawable.dinner,
            R.drawable.drink, R.drawable.easycard, R.drawable.easygo, R.drawable.electricty, R.drawable.fruit,
            R.drawable.gasstation, R.drawable.highspeedtrain, R.drawable.hospital, R.drawable.image, R.drawable.internet,
            R.drawable.jacket, R.drawable.water, R.drawable.university, R.drawable.trousers, R.drawable.treatment, R.drawable.training,
            R.drawable.ticket, R.drawable.supplement, R.drawable.subway, R.drawable.rent, R.drawable.rentfilled, R.drawable.phonet,
            R.drawable.origin, R.drawable.movie, R.drawable.microphone, R.drawable.lunch, R.drawable.losemoney, R.drawable.lipgloss, R.drawable.train
            , R.drawable.salary, R.drawable.lotto, R.drawable.bouns, R.drawable.interest, R.drawable.fund, R.drawable.bank, R.drawable.health, R.drawable.shose
    };

    private String food = "堡 三明治 優酪乳 肉 飯 雙手卷 腿 麵 麵包 熱狗 雞 手卷 肉 粉 蔬菜 牛 豬 起司 花生 豆 蛋 魚 菜 瓜 黑胡椒 土司 泡芙 排";
    private String drink = "咖啡 茶 豆漿 拿鐵 乳 飲 ml 罐 酒 杯 水";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askPermissions();
//        deleteDatabase("ChargeAPP");
        if (chargeAPPDB == null) {
            setdate();
        }
        setUpActionBar();
        initDrawer();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // home icon will keep still without calling syncState()
        actionBarDrawerToggle.syncState();
    }

    private void initDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.text_Open, R.string.text_Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        listView = findViewById(R.id.list_menu);
        List<EleMainItemVO> itemVOS = getNewItem();
        final List<EleMainItemVO> itemSon= getElemainItemList();
        listView.setAdapter(new ExpandableAdapter(this,itemVOS,itemSon));
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Fragment fragment;
                position=groupPosition;
                getSupportActionBar().setDisplayShowCustomEnabled(false);
                if (groupPosition == 0) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    listView.collapseGroup(groupPosition);
                    listView.collapseGroup(1);
                    fragment = new InsertActivity();
                    setTitle(R.string.text_Com);
                    switchFragment(fragment);
                } else if (groupPosition == 1) {
                    View view;
                    for(int i=0;i<listView.getChildCount();i++)
                    {
                        view=listView.getChildAt(i);
                        view.setBackgroundColor(Color.parseColor("#f5f5f5"));
                    }
                    view=listView.getChildAt(position);
                    view.setBackgroundColor(Color.parseColor("#FFEE99"));
                } else if (groupPosition == 2) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    listView.collapseGroup(groupPosition);
                    listView.collapseGroup(1);
                    fragment = new Download();
                    setTitle(R.string.text_Price);
                    switchFragment(fragment);
                } else if (groupPosition == 3) {

                    Calendar calendar=Calendar.getInstance();
                    SelectListModelCom.year=calendar.get(Calendar.YEAR);
                    SelectListModelCom.month=calendar.get(Calendar.MONTH);
                    SelectListModelCom.p=0;
                    SelectListModelIM.year=calendar.get(Calendar.YEAR);
                    SelectListModelIM.month=calendar.get(Calendar.MONTH);
                    SelectListModelIM.p=0;
                    UpdateInvoice.showsecondgrid=false;
                    UpdateInvoice.showfirstgrid=false;
                    UpdateSpend.showsecondgrid=false;
                    UpdateSpend.showfirstgrid=false;

                    setTitle(R.string.text_Picture);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    listView.collapseGroup(groupPosition);
                    listView.collapseGroup(1);
                    fragment = new SelectActivity();
                    switchFragment(fragment);
                } else if (groupPosition == 4) {
                    setTitle(R.string.text_Goal);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    listView.collapseGroup(groupPosition);
                    listView.collapseGroup(1);
                } else if (groupPosition == 5){
                    setTitle(R.string.text_Setting);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    listView.collapseGroup(groupPosition);
                    listView.collapseGroup(1);
                }else {
                    setTitle(R.string.text_Home);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    listView.collapseGroup(groupPosition);
                    listView.collapseGroup(1);
                }
            }
        });
    }

    private List<EleMainItemVO> getElemainItemList() {
        List<EleMainItemVO> list=new ArrayList<>();
        list.add(new EleMainItemVO(R.string.text_SetCarrier,R.drawable.cellphone));
        list.add(new EleMainItemVO(R.string.text_DonateMain,R.drawable.health));
        list.add(new EleMainItemVO(R.string.text_HowSetC,R.drawable.easygo));
        list.add(new EleMainItemVO(R.string.text_NewCarrier,R.drawable.barcode));
        list.add(new EleMainItemVO(R.string.text_EleBank,R.drawable.bank));
        list.add(new EleMainItemVO(R.string.text_HowGet,R.drawable.invent));
        list.add(new EleMainItemVO(R.string.text_EleWhat,R.drawable.image));
        return list;
    }
    private List<EleMainItemVO> getNewItem() {
        List<EleMainItemVO> eleMainItemVOList = new ArrayList<>();
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Com, R.drawable.book));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Ele, R.drawable.barcode));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Price, R.drawable.bouns));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Picture, R.drawable.chart));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Goal, R.drawable.goal));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Setting, R.drawable.settings));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Home,R.drawable.home));
        return eleMainItemVOList;
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment1 :  getSupportFragmentManager().getFragments()) {
                fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(position!=1)
        {
            View view;
            for(int i=0;i<this.listView.getChildCount();i++)
            {
                view=listView.getChildAt(i);
                view.setBackgroundColor(Color.parseColor("#f5f5f5"));
            }
            view=listView.getChildAt(position);
            view.setBackgroundColor(Color.parseColor("#FFEE99"));
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight) throws WriterException {
        if (contents.length() == 0) return null;
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;
        HashMap<EncodeHintType, String> hints = null;
        String encoding = null;
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                encoding = "UTF-8";
                break;
            }
        }
        if (encoding != null) {
            hints = new HashMap<EncodeHintType, String>(2);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = writer.encode(contents, format, desiredWidth, desiredHeight, hints);
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private void setBarCode() throws WriterException {
        try {
//        carrB.setImageBitmap(encodeAsBitmap("/2RDO8+P", BarcodeFormat.CODE_39, 600, 100));
        } catch (Exception e) {
        }
    }


    private void setdate() {
        chargeAPPDB = new ChargeAPPDB(this);
        TypeDB typeDB = new TypeDB(chargeAPPDB.getReadableDatabase());
        TypeDetailDB typeDetailDB = new TypeDetailDB(chargeAPPDB.getReadableDatabase());
        List<TypeVO> typeVOS = typeDB.getAll();
        if (typeVOS.size() > 0) {
            return;
        }
        typeDB.insert(new TypeVO("food", "食物", 0));
        typeDB.insert(new TypeVO("phone", "通訊", 1));
        typeDB.insert(new TypeVO("clothes", "衣服", 2));
        typeDB.insert(new TypeVO("traffic", "交通", 3));
        typeDB.insert(new TypeVO("teach", "教育", 4));
        typeDB.insert(new TypeVO("happy", "娛樂", 5));
        typeDB.insert(new TypeVO("home", "住宿", 6));
        typeDB.insert(new TypeVO("medical", "醫療", 7));
        typeDB.insert(new TypeVO("invent", "投資", 8));
        typeDetailDB.insert(new TypeDetailVO("食物", "早餐", indexOfIntArray(imageAll, R.drawable.breakfast), food));
        typeDetailDB.insert(new TypeDetailVO("食物", "午餐", indexOfIntArray(imageAll, R.drawable.lunch), "0"));
        typeDetailDB.insert(new TypeDetailVO("食物", "晚餐", indexOfIntArray(imageAll, R.drawable.dinner), "0"));
        typeDetailDB.insert(new TypeDetailVO("食物", "飲料", indexOfIntArray(imageAll, R.drawable.drink), drink));
        typeDetailDB.insert(new TypeDetailVO("食物", "水果", indexOfIntArray(imageAll, R.drawable.fruit), "蘋果 鳳梨 瓜 蕉 葡萄 蓮霧 番石榴 李 果 檬 橙 莓 椰子 桃 橘 柚 姆 柑 棗 蓮"));
        typeDetailDB.insert(new TypeDetailVO("通訊", "電話費", indexOfIntArray(imageAll, R.drawable.phonet), "中華 市話"));
        typeDetailDB.insert(new TypeDetailVO("通訊", "月租費", indexOfIntArray(imageAll, R.drawable.cellphone), "月租費"));
        typeDetailDB.insert(new TypeDetailVO("通訊", "易付卡", indexOfIntArray(imageAll, R.drawable.easycard), "易付卡 卡"));
        typeDetailDB.insert(new TypeDetailVO("通訊", "網路費", indexOfIntArray(imageAll, R.drawable.internet), "M 上網 寬頻 光纖"));
        typeDetailDB.insert(new TypeDetailVO("交通", "火車", indexOfIntArray(imageAll, R.drawable.train), "0"));
        typeDetailDB.insert(new TypeDetailVO("交通", "高鐵", indexOfIntArray(imageAll, R.drawable.highspeedtrain), "0"));
        typeDetailDB.insert(new TypeDetailVO("交通", "捷運", indexOfIntArray(imageAll, R.drawable.subway), "0"));
        typeDetailDB.insert(new TypeDetailVO("交通", "客運", indexOfIntArray(imageAll, R.drawable.bus), "0"));
        typeDetailDB.insert(new TypeDetailVO("交通", "加油", indexOfIntArray(imageAll, R.drawable.gasstation), "汽油 92 95 98"));
        typeDetailDB.insert(new TypeDetailVO("教育", "補習費", indexOfIntArray(imageAll, R.drawable.training), "0"));
        typeDetailDB.insert(new TypeDetailVO("教育", "學雜費", indexOfIntArray(imageAll, R.drawable.university), "0"));
        typeDetailDB.insert(new TypeDetailVO("娛樂", "電影", indexOfIntArray(imageAll, R.drawable.movie), "0"));
        typeDetailDB.insert(new TypeDetailVO("娛樂", "KTV", indexOfIntArray(imageAll, R.drawable.microphone), "0"));
        typeDetailDB.insert(new TypeDetailVO("娛樂", "門票", indexOfIntArray(imageAll, R.drawable.ticket), "0"));
        typeDetailDB.insert(new TypeDetailVO("住宿", "水費", indexOfIntArray(imageAll, R.drawable.water), "0"));
        typeDetailDB.insert(new TypeDetailVO("住宿", "電費", indexOfIntArray(imageAll, R.drawable.electricty), "0"));
        typeDetailDB.insert(new TypeDetailVO("住宿", "瓦斯", indexOfIntArray(imageAll, R.drawable.gasstation), "0"));
        typeDetailDB.insert(new TypeDetailVO("住宿", "房租", indexOfIntArray(imageAll, R.drawable.rent), "0"));
        typeDetailDB.insert(new TypeDetailVO("住宿", "房貸", indexOfIntArray(imageAll, R.drawable.rentfilled), "0"));
        typeDetailDB.insert(new TypeDetailVO("醫療", "健保", indexOfIntArray(imageAll, R.drawable.health), "0"));
        typeDetailDB.insert(new TypeDetailVO("醫療", "勞保", indexOfIntArray(imageAll, R.drawable.worker), "0"));
        typeDetailDB.insert(new TypeDetailVO("醫療", "醫療", indexOfIntArray(imageAll, R.drawable.medical), "0"));
        typeDetailDB.insert(new TypeDetailVO("醫療", "保健食品", indexOfIntArray(imageAll, R.drawable.supplement), "0"));
        typeDetailDB.insert(new TypeDetailVO("投資", "保險", indexOfIntArray(imageAll, R.drawable.treatment), "0"));
        typeDetailDB.insert(new TypeDetailVO("投資", "投資損失", indexOfIntArray(imageAll, R.drawable.losemoney), "0"));
        typeDetailDB.insert(new TypeDetailVO("衣服", "上衣", indexOfIntArray(imageAll, R.drawable.jacket), "衣 外套 套 袖 shirt 棉"));
        typeDetailDB.insert(new TypeDetailVO("衣服", "褲子", indexOfIntArray(imageAll, R.drawable.trousers), "褲"));
        typeDetailDB.insert(new TypeDetailVO("衣服", "內衣褲", indexOfIntArray(imageAll, R.drawable.clothe), "罩 內褲"));
        typeDetailDB.insert(new TypeDetailVO("衣服", "鞋子", indexOfIntArray(imageAll, R.drawable.shose), "鞋"));
        typeDetailDB.insert(new TypeDetailVO("衣服", "化妝品", indexOfIntArray(imageAll, R.drawable.lipgloss), "指甲 臉 面 膜"));
        BankTybeDB bankTybeDB = new BankTybeDB(chargeAPPDB.getReadableDatabase());
        bankTybeDB.insert(new BankTypeVO("薪水", "薪水", indexOfIntArray(imageAll, R.drawable.salary)));
        bankTybeDB.insert(new BankTypeVO("薪水", "中獎", indexOfIntArray(imageAll, R.drawable.lotto)));
        bankTybeDB.insert(new BankTypeVO("薪水", "獎金", indexOfIntArray(imageAll, R.drawable.bouns)));
        bankTybeDB.insert(new BankTypeVO("薪水", "基金", indexOfIntArray(imageAll, R.drawable.fund)));
        bankTybeDB.insert(new BankTypeVO("薪水", "股票", indexOfIntArray(imageAll, R.drawable.origin)));
        bankTybeDB.insert(new BankTypeVO("薪水", "利息", indexOfIntArray(imageAll, R.drawable.bank)));
        bankTybeDB.insert(new BankTypeVO("薪水", "股利", indexOfIntArray(imageAll, R.drawable.interest)));
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
    private  class ExpandableAdapter extends BaseExpandableListAdapter {
           Context context;
           List<EleMainItemVO> list;
           List<EleMainItemVO> son;
        ExpandableAdapter(Context context,List<EleMainItemVO> list,List<EleMainItemVO> son)
        {
            this.context=context;
            this.list=list;
            this.son=son;
        }

        @Override
        public int getGroupCount() {
            return list.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return son.size();
        }

        @Override
        public Object getGroup(int i) {
            return list.get(i);
        }

        @Override
        public Object getChild(int i, int i1) {
            return son.get(i);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                view = layoutInflater.inflate(R.layout.ele_main_item, viewGroup, false);
            }
            ImageView indicator =view.findViewById(R.id.ele_indicator);
            indicator.setVisibility(View.GONE);
            if(i==1)
            {
                indicator.setVisibility(View.VISIBLE);
                if(b)
                {
                    indicator.setImageResource(R.drawable.arrow_up);
                }else {
                    indicator.setImageResource(R.drawable.arrow_down);
                }
            }
            EleMainItemVO member = list.get(i);
            ImageView ivImage = (ImageView) view.findViewById(R.id.ivImage);
            ivImage.setImageResource(member.getImage());
            String mystring = getResources().getString(member.getIdstring());
            TextView tvId = (TextView) view.findViewById(R.id.tvId);
            tvId.setText(mystring);
            return view;
        }

        @Override
        public View getChildView(int i, final int i1, boolean b, View view, final ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                view = layoutInflater.inflate(R.layout.ele_child_item, viewGroup, false);
            }
            EleMainItemVO member = son.get(i1);
            ImageView ivImage = (ImageView) view.findViewById(R.id.ivImage);
            ivImage.setImageResource(member.getImage());
            String mystring = getResources().getString(member.getIdstring());
            TextView tvId = (TextView) view.findViewById(R.id.tvId);
            tvId.setText(mystring);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment;
                    drawerLayout.closeDrawer(GravityCompat.START);
                    if(i1==0)
                    {
                        fragment=new EleSetCarrier();
                        setTitle(R.string.text_SetCarrier);
                        switchFragment(fragment);
                    }else if(i1==1)
                    {
                        setTitle(R.string.text_DonateMain);
                        fragment=new EleDonateMain();
                        switchFragment(fragment);
                    }else if(i1==2)
                    {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://www.teach.ltu.edu.tw/public/News/11503/201412041535091.pdf"));
                        startActivity(intent);
                    }else if(i1==3)
                    {
                        setTitle(R.string.text_NewCarrier);
                        fragment=new EleNewCarrier();
                        switchFragment(fragment);
                    }else if(i1==4)
                    {
                        setTitle(R.string.text_EleBank);
                        fragment=new EleAddBank();
                        switchFragment(fragment);
                    }else if(i1==5){
                        setTitle(R.string.text_HowGet);
                        fragment=new HowGetPrice();
                        switchFragment(fragment);
                    }else{
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://www.nknu.edu.tw/~psl/new.file/103/08/1030825reciept1.pdf"));
                        startActivity(intent);
                    }
                    if(i1==2||i1==6)
                    {
                        return;
                    }
                    if(oldview!=null)
                    {
                        oldview.setBackgroundColor(Color.WHITE);
                    }
                    oldview=view;
                    view.setBackgroundColor(Color.parseColor("#EEFFBB"));
                }
            });
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        int index = getSupportFragmentManager().getBackStackEntryCount() - 1;
//        if(index!=-1)
//        {
//            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
//            String tag = backEntry.getName();
//            if(tag!=null)
//            {
//                if (tag.equals("Elemain")) {
//                    Intent intent = new Intent(MainActivity.this, EleActivity.class);
//                    startActivity(intent);
//                    return true;
//                }
//            }
//        }else {
//            Intent intent = new Intent(MainActivity.this, MainActivity.class);
//            startActivity(intent);
//        }
//        return super.onKeyDown(keyCode, event);
//    }
    }


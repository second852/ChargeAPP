package com.chargeapp.whc.chargeapp.Control;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;



import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.Adapter.OutDialogFragment;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Control.EleInvoice.EleAddBank;
import com.chargeapp.whc.chargeapp.Control.EleInvoice.EleAddCarrier;
import com.chargeapp.whc.chargeapp.Control.EleInvoice.EleDonateMain;
import com.chargeapp.whc.chargeapp.Control.EleInvoice.EleNewCarrier;
import com.chargeapp.whc.chargeapp.Control.EleInvoice.EleSetCarrier;
import com.chargeapp.whc.chargeapp.Control.EleInvoice.EleShowCarrier;
import com.chargeapp.whc.chargeapp.Control.Goal.GoalListAll;
import com.chargeapp.whc.chargeapp.Control.HomePage.HomePage;
import com.chargeapp.whc.chargeapp.Control.Insert.InsertActivity;
import com.chargeapp.whc.chargeapp.Control.Insert.InsertIncome;
import com.chargeapp.whc.chargeapp.Control.Insert.InsertSpend;
import com.chargeapp.whc.chargeapp.Control.Insert.SearchByQrCode;
import com.chargeapp.whc.chargeapp.Control.Price.HowGetPrice;
import com.chargeapp.whc.chargeapp.Control.Price.PriceActivity;
import com.chargeapp.whc.chargeapp.Control.Price.PriceHand;
import com.chargeapp.whc.chargeapp.Control.Price.PriceInvoice;
import com.chargeapp.whc.chargeapp.Control.Property.PropertyMain;
import com.chargeapp.whc.chargeapp.Control.Search.SearchMain;
import com.chargeapp.whc.chargeapp.Control.SelectList.SelectListModelActivity;
import com.chargeapp.whc.chargeapp.Control.SelectList.SelectListModelCom;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectActivity;
import com.chargeapp.whc.chargeapp.Control.SelectPicture.SelectConsume;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingDownloadFile;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingListFix;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingMain;
import com.chargeapp.whc.chargeapp.Control.Setting.SettingUploadFile;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateSpend;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.EleMainItemVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.ui.BarcodeGraphic;
import com.chargeapp.whc.chargeapp.ui.MultiTrackerActivity;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ExpandableListView listView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public static ChargeAPPDB chargeAPPDB;
    private View oldSecondView, oldMainView;
    private int position;
    private boolean doubleClick = false;
    public static LinkedList<String> oldFramgent;
    public static LinkedList<Bundle> bundles;
    public Fragment fragment;
    public View nowView;
    public boolean firstShowF;
    public static boolean firstShowInsertActivity;
    public static GridView numberKeyBoard;
    private AdView mAdView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);
        firstShowF = true;
        firstShowInsertActivity = true;
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onPause() {
        super.onPause();
        firstShowF=false;
        firstShowInsertActivity=false;
    }

    //    @Override
//    public Resources getResources() {
//        Resources res = super.getResources();
//        if (res.getConfiguration().fontScale > 1) {
//            Configuration config = new Configuration();
//            config.setToDefaults();
////            res.updateConfiguration(config, res.getDisplayMetrics());
//            getApplicationContext().createConfigurationContext(config);
//        }
//        return res;
//    }


    public Fragment[] fragments = {new HomePage(), new HomePage(), new InsertActivity(), new PriceActivity(), new PropertyMain(),new SelectActivity(), new SelectListModelActivity(), new GoalListAll(),new SearchMain(), new SettingMain()};
    public String[] fragmentTags = {"firstFragment", "HomePage", "InserActivity", "PriceActivity", "PropertyMain","SelectActivity", "SelectListModelActivity", "GoalListAll","SearchMain","SettingMain"};

    private void initFragment() {
        FragmentManager fManager = getSupportFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {

            if(fragments[i] instanceof SearchMain)
            {
                Bundle bundle=new Bundle();
                bundle.putSerializable("searchMainAction","new");
                fragments[i].setArguments(bundle);
            }

            fTransaction.add(R.id.body, fragments[i], fragmentTags[i]);
        }
        fTransaction.commit();
        try {
            fManager.executePendingTransactions();
        } catch (Exception e) {

        }
    }

    private void initHideFragment() {
        FragmentManager fManager = getSupportFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {

            fTransaction.hide(fManager.findFragmentByTag(fragmentTags[i]));
        }
        fTransaction.commit();
        try {
            fManager.executePendingTransactions();
        } catch (Exception e) {

        }
    }


    private void showFragment1(int fragmentIndex) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        transaction.commit();
    }

    private void showFragment2(int fragmentIndex) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.remove(fragments[i]);
            }
        }
        transaction.commit();
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
        List<EleMainItemVO> itemSon = getElemainItemList();
        listView.setAdapter(new ExpandableAdapter(this, itemVOS, itemSon));
    }


    private List<EleMainItemVO> getElemainItemList() {
        List<EleMainItemVO> list = new ArrayList<>();
        list.add(new EleMainItemVO(R.string.text_SetCarrier, R.drawable.cellphone));
        list.add(new EleMainItemVO(R.string.text_ShowCal, R.drawable.barcode));
        list.add(new EleMainItemVO(R.string.text_DonateMain, R.drawable.health));
        list.add(new EleMainItemVO(R.string.text_AddCarrier, R.drawable.treatment));
        list.add(new EleMainItemVO(R.string.text_HowSetC, R.drawable.easygo));
        list.add(new EleMainItemVO(R.string.text_NewCarrier, R.drawable.barcode));
        list.add(new EleMainItemVO(R.string.text_EleBank, R.drawable.bank));
        list.add(new EleMainItemVO(R.string.text_HowGet, R.drawable.invent));
        list.add(new EleMainItemVO(R.string.text_EleWhat, R.drawable.image));
        return list;
    }

    private List<EleMainItemVO> getNewItem() {
        List<EleMainItemVO> eleMainItemVOList = new ArrayList<>();
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Com, R.drawable.book));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_QRcode, R.drawable.qrcode));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Ele, R.drawable.barcode));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Price, R.drawable.bouns));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Property, R.drawable.property));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_DataPicture, R.drawable.chart));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_DataList, R.drawable.invent));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Goal, R.drawable.goal));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Search, R.drawable.search));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Setting, R.drawable.settings));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Home, R.drawable.home));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Teach, R.drawable.teach));
        return eleMainItemVOList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                for (Fragment f : fragments) {
                    if (f instanceof PriceHand) {
                        f.onRequestPermissionsResult(requestCode, permissions, grantResults);
                        break;
                    }
                }
                break;
        }
    }

    private void switchFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (Fragment f : getSupportFragmentManager().getFragments()) {
            fragmentTransaction.remove(f);
        }
        fragmentTransaction.add(R.id.body, fragment);
        fragmentTransaction.commit();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (oldFramgent == null) {
            oldFramgent = new LinkedList<>();
            bundles = new LinkedList<>();
        }
        setUpActionBar();
        initDrawer();
        Common.setChargeDB(this);
        //設定NotifyCation 傳進去參數
        String action;
        try {
            action = getIntent().getAction();
        } catch (Exception e) {
            action = null;
        }
        if (action != null) {
            String title="首頁";
            if (action.equals("showFix")) {
                title = "設定定期項目";
                fragments[0] = new SettingListFix();
                fragmentTags[0] = "SettingListFix";
            } else if (action.equals("nulPriceNotify")) {
                title = getResources().getString(R.string.text_Price);
                fragments[0] = new PriceActivity();
                fragmentTags[0] = "PriceActivity";
            } else if (action.equals("goal")) {
                title = getResources().getString(R.string.text_Goal);
                fragments[0] = new GoalListAll();
            } else if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
                title = getResources().getString(R.string.text_SetCarrier);
                fragments[0] = new EleSetCarrier();
            }
            initFragment();
            initHideFragment();
            showFragment1(0);
            firstShowF = false;
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            setTitle(title);
            getIntent().setAction(null);
            return;
        }
        if (firstShowF) {
            initFragment();
            initHideFragment();
            showFragment1(0);
            firstShowF = false;
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            setTitle(R.string.text_Home);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Common.clossKeyword(MainActivity.this);
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
            actionBar.show();
        }
    }


    //設定目前選擇項目的顏色
    private void setColor(View v) {
        (getSupportActionBar()).setDisplayShowCustomEnabled(false);
        v.setBackgroundColor(Color.parseColor("#FFDD55"));
        if (oldMainView != null && v != oldMainView) {
            oldMainView.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }
        oldMainView = v;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (nowView != null) {
                setColor(nowView);
            }
        }
    };

    private Handler setTittle = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity.this.setTitle((String)msg.obj);
        }
    };


    private class ExpandableAdapter extends BaseExpandableListAdapter {
        Context context;
        List<EleMainItemVO> list;
        List<EleMainItemVO> son;

        ExpandableAdapter(Context context, List<EleMainItemVO> list, List<EleMainItemVO> son) {
            this.context = context;
            this.list = list;
            this.son = son;
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
        public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                view = layoutInflater.inflate(R.layout.ele_main_item, viewGroup, false);
            }
            ImageView indicator = view.findViewById(R.id.ele_indicator);
            RelativeLayout rea = view.findViewById(R.id.rea);

            //not show bottom
            if (oldMainView == view && MainActivity.this.position == i) {
                rea.setBackgroundColor(Color.parseColor("#FFDD55"));
            } else {
                rea.setBackgroundColor(Color.parseColor("#DDDDDD"));
            }


            indicator.setVisibility(View.GONE);
            if (i == 2) {
                indicator.setVisibility(View.VISIBLE);
                if (Common.lostCarrier != null) {
                    if (!Common.lostCarrier.isEmpty()) {
                        view.setBackgroundColor(Color.RED);
                        Common.showToast(MainActivity.this,"電子發票密碼錯誤!");
                    }
                }
                if (b) {
                    indicator.setImageResource(R.drawable.arrow_up);
                } else {
                    indicator.setImageResource(R.drawable.arrow_down);
                }
            }
            EleMainItemVO member = list.get(i);
            ImageView ivImage = view.findViewById(R.id.ivImage);
            ivImage.setImageResource(member.getImage());
            String mystring = getResources().getString(member.getIdstring());
            TextView tvId = view.findViewById(R.id.tvId);
            tvId.setText(mystring);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            oldFramgent.clear();
                            bundles.clear();
                            MainActivity.this.position = i;
                            nowView = v;
                            handler.sendEmptyMessage(0);
                        }
                    }).start();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (i == 0) {

                                Common.showfirstgrid = false;
                                Common.showsecondgrid = false;
                                InsertSpend.consumeVO = new ConsumeVO();
                                InsertSpend.needSet = false;
                                InsertIncome.needSet = false;
                                InsertIncome.bankVO = new BankVO();
                                if (firstShowInsertActivity) {
                                    showFragment2(2);
                                    firstShowInsertActivity = false;
                                } else {
                                    fragment = new InsertActivity();
                                    switchFragment();
                                }

                                Message message=new Message();
                                message.obj= getResources().getString(R.string.text_Com);
                                setTittle.sendMessage(message);

                            } else if (i == 1) {
                                firstShowInsertActivity = false;
                                Intent intent = new Intent(MainActivity.this, MultiTrackerActivity.class);
                                intent.putExtra("action", "moreQRcode");
                                startActivityForResult(intent, 10);
                                return;
                            }else if (i == 2) {
                                firstShowInsertActivity = false;
                                return;
                            } else if (i == 3) {
                                PriceInvoice.first = true;
                                firstShowInsertActivity=false;
                                fragment = new PriceActivity();
                                switchFragment();
                                Message message=new Message();
                                message.obj= getResources().getString(R.string.text_Price);
                                setTittle.sendMessage(message);

                            }else if(i== 4) {


                                if (firstShowInsertActivity) {
                                    showFragment2(4);
                                    firstShowInsertActivity = false;

                                } else {
                                    fragment = new PropertyMain();
                                    switchFragment();
                                }

                                Message message=new Message();
                                message.obj= getResources().getString(R.string.text_Property);
                                setTittle.sendMessage(message);

                            } else if (i == 5) {

                                if (firstShowInsertActivity) {
                                    showFragment2(5);
                                    firstShowInsertActivity = false;

                                } else {
                                    fragment = new SelectActivity();
                                    switchFragment();
                                }

                                Message message=new Message();
                                message.obj= getResources().getString(R.string.text_DataPicture);
                                setTittle.sendMessage(message);

                            } else if (i == 6) {

                                if (firstShowInsertActivity) {
                                    showFragment2(6);
                                    firstShowInsertActivity = false;

                                } else {

                                    fragment = new SelectListModelActivity();
                                    switchFragment();
                                }

                                Message message=new Message();
                                message.obj= getResources().getString(R.string.text_DataList);
                                setTittle.sendMessage(message);
                            } else if (i ==7) {

                                if (firstShowInsertActivity) {
                                    showFragment2(7);
                                    firstShowInsertActivity = false;
                                } else {

                                    fragment = new GoalListAll();
                                    switchFragment();
                                }
                                Message message=new Message();
                                message.obj= getResources().getString(R.string.text_Goal);
                                setTittle.sendMessage(message);

                            } else if (i ==8) {

                                if (firstShowInsertActivity) {
                                    showFragment2(8);
                                    firstShowInsertActivity = false;
                                } else {

                                    fragment = new SearchMain();
                                    Bundle bundle=new Bundle();
                                    bundle.putSerializable("searchMainAction","new");
                                    fragment.setArguments(bundle);
                                    switchFragment();
                                }
                                Message message=new Message();
                                message.obj= getResources().getString(R.string.text_Search);
                                setTittle.sendMessage(message);

                            } else if (i == 9) {

                                if (firstShowInsertActivity) {
                                    showFragment2(9);
                                    firstShowInsertActivity = false;
                                } else {
                                    fragment = new SettingMain();
                                    switchFragment();
                                }
                                Message message=new Message();
                                message.obj= getResources().getString(R.string.text_Setting);
                                setTittle.sendMessage(message);

                            } else if (i == 10) {
                                if (firstShowInsertActivity) {
                                    showFragment2(1);
                                    firstShowInsertActivity = false;
                                } else {
                                    fragment = new HomePage();
                                    switchFragment();
                                }

                                Message message=new Message();
                                message.obj= getResources().getString(R.string.text_Home);
                                setTittle.sendMessage(message);
                            } else {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("https://www.youtube.com/playlist?list=PLrGq9ODiZ15rdXvKV_5FEIrdaP5ix0c55"));
                                startActivity(intent);
                            }


                        }
                    }).start();

                    if (i == 2) {
                        setTitle(R.string.text_Ele);
                        if (doubleClick) {
                            listView.collapseGroup(2);
                            doubleClick = false;
                        } else {
                            listView.expandGroup(2);
                            doubleClick = true;
                        }
                    } else {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
            });
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

            if (Common.lostCarrier != null) {
                if (i1 == 0 && !Common.lostCarrier.isEmpty()) {
                    view.setBackgroundColor(Color.RED);
                }
            }


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (oldSecondView != null && oldSecondView != view) {
                        oldSecondView.setBackgroundColor(Color.WHITE);
                    }
                    oldSecondView = view;
                    view.setBackgroundColor(Color.parseColor("#EEFFBB"));
                    if (i1 == 0) {
                        fragment = new EleSetCarrier();
                        switchFragment();
                    } else if (i1 == 1) {
                        fragment = new EleShowCarrier();
                        switchFragment();
                    } else if (i1 == 2) {
                        fragment = new EleDonateMain();
                        switchFragment();
                    } else if (i1 == 3) {
                        fragment = new EleAddCarrier();
                        switchFragment();
                    } else if (i1 == 4) {
                        //close drawer
                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://www.teach.ltu.edu.tw/public/News/11503/201412041535091.pdf"));
                        startActivity(intent);
                    } else if (i1 == 5) {
                        fragment = new EleNewCarrier();
                        switchFragment();
                        return;
                    } else if (i1 == 6) {
                        fragment = new EleAddBank();
                        switchFragment();
                    } else if (i1 == 7) {
                        fragment = new HowGetPrice();
                        switchFragment();
                    } else {
                        //close drawer
                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.einvoice.nat.gov.tw/index!easyKnow"));
                        startActivity(intent);
                    }
                }
            });
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "requestCode" + requestCode);
        String a;
        try {
            a = data.getStringExtra("action");
        } catch (NullPointerException e) {
            a = null;
        }
        if (a == null) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            switch (requestCode) {
                case 12:
                    for (int i = fragments.size() - 1; i >= 0; i--) {
                        if (fragments.get(i) instanceof PriceHand) {
                            fragments.get(i).onActivityResult(requestCode, resultCode, data);
                            break;
                        }
                    }
                    break;
                case 5:
                case 4:
                    for (int i = fragments.size() - 1; i >= 0; i--) {
                        if (fragments.get(i) instanceof SettingDownloadFile) {
                            fragments.get(i).onActivityResult(requestCode, resultCode, data);
                            break;
                        }
                    }
                    break;
                case 3:
                    for (int i = fragments.size() - 1; i >= 0; i--) {
                        if (fragments.get(i) instanceof SettingUploadFile || fragments.get(i) instanceof SearchMain) {
                            fragments.get(i).onActivityResult(requestCode, resultCode, data);
                            break;
                        }
                    }
                    break;
                case 10:
                    for (int i = fragments.size() - 1; i >= 0; i--) {
                        if (fragments.get(i) instanceof SelectListModelActivity) {
                          fragment=new SelectListModelActivity();
                          switchFragment();
                        }else if(fragments.get(i) instanceof SelectActivity)
                        {
                           fragment=new SelectActivity();
                           switchFragment();
                        }
                    }
                 break;
            }

        } else {
            if (a.equals("setCarrier")) {
                fragment = new EleSetCarrier();
                switchFragment();
            } else if (a.equals("setConsume")) {
                if (resultCode == 9) {
                    data.putExtra("scan", "true");
                    searchQRCode(data);
                }
                else {
//                    setConsume();
                    fragment = new InsertActivity();
                    switchFragment();
                }

            } else if (a.equals("UpdateSpend")) {
                if (resultCode == 9) {
                    data.putExtra("scan", "true");
                    searchQRCode(data);
                } else {
                    fragment = new UpdateSpend();
                    fragment.setArguments(data.getExtras().getBundle("bundle"));
                    switchFragment();
//                  setUpdateConsume(data);
                }
            } else if (a.equals("PriceHand")) {
                fragment = new PriceActivity();
                switchFragment();
            }
        }
    }

    private void searchQRCode(Intent intent) {
        fragment = new SearchByQrCode();
        fragment.setArguments(intent.getExtras());
        switchFragment();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("MainActivity", String.valueOf(oldFramgent.size()) + ":" + String.valueOf(bundles.size()));
        if (keyCode == KeyEvent.KEYCODE_BACK) {



            //close KeyBoard
            List<Fragment> fragments=getSupportFragmentManager().getFragments();
            View v = MainActivity.this.getCurrentFocus();

            if(v==null)
            {
                v=fragments.get(fragments.size()-1).getView();
            }

            //------關閉內建keyboard-----//
            if (v != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            //------關閉自製keyboard-----//
            View numberKeyBoard;

            for(Fragment fragment:fragments)
            {
                numberKeyBoard= fragment.getView().findViewById(R.id.numberKeyBoard);
                if(numberKeyBoard!=null&&numberKeyBoard.getVisibility()==View.VISIBLE)
                {
                    numberKeyBoard.setVisibility(View.GONE);
                    return true;
                }
                numberKeyBoard=findViewById(R.id.numberKeyBoard1);
                if(numberKeyBoard!=null&&numberKeyBoard.getVisibility()==View.VISIBLE)
                {
                    numberKeyBoard.setVisibility(View.GONE);
                    return true;
                }
            }




            if (oldFramgent.size() == 0 || bundles.size() == 0) {
                OutDialogFragment out = new OutDialogFragment();
                out.show(this.getSupportFragmentManager(), "show");
            } else {
                fragment=Common.returnFragment(v);
                //------- 切換-----------//
                switchFragment();
                MainActivity.oldFramgent.remove(MainActivity.oldFramgent.size() - 1);
                MainActivity.bundles.remove(MainActivity.bundles.size() - 1);
                return true;
            }
        }
        return true;
    }
}


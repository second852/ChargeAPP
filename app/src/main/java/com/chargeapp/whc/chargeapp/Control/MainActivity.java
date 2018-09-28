package com.chargeapp.whc.chargeapp.Control;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.EleMainItemVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.ui.BarcodeGraphic;
import com.chargeapp.whc.chargeapp.ui.MultiTrackerActivity;
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

    //維持現在Framgent
    public boolean mFramgent;
    public View nowView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);
    }



    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if(res.getConfiguration().fontScale>1)
        {
            Configuration config = new Configuration();
            config.setToDefaults();
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return res;
    }

//    public void adjustFontScale(Configuration configuration) {
//            configuration.fontScale = (float) 1;
//            DisplayMetrics metrics = getResources().getDisplayMetrics();
//            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
//            wm.getDefaultDisplay().getMetrics(metrics);
//            metrics.scaledDensity = configuration.fontScale * metrics.density;
//            getBaseContext().getResources().updateConfiguration(configuration, metrics);
//    }

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
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Ele, R.drawable.barcode));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Price, R.drawable.bouns));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_DataPicture, R.drawable.chart));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_DataList, R.drawable.invent));
        eleMainItemVOList.add(new EleMainItemVO(R.string.text_Goal, R.drawable.goal));
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
        fragmentTransaction.replace(R.id.body, fragment);
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
            if (action.equals("showFix")) {
                fragment = new SettingListFix();
                switchFragment();
                return;
            } else if (action.equals("nulPriceNotify")) {
                fragment = new PriceActivity();
                switchFragment();
                return;
            } else if (action.equals("goal")) {
                fragment = new GoalListAll();
                switchFragment();
                return;
            }
        }
        if (!mFramgent) {
            fragment = new HomePage();
            switchFragment();
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        mFramgent=true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                if(nowView!=null)
                {
                    setColor(nowView);
                }
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
            BootstrapButton deletecarrier = view.findViewById(R.id.deletecarrier);
            BootstrapButton widgetShow = view.findViewById(R.id.widgetShow);
            BootstrapButton updateC = view.findViewById(R.id.updateC);
            widgetShow.setVisibility(View.GONE);
            deletecarrier.setVisibility(View.GONE);
            updateC.setVisibility(View.GONE);

            if (oldMainView == view && MainActivity.this.position == i) {
                rea.setBackgroundColor(Color.parseColor("#FFDD55"));
            } else {
                rea.setBackgroundColor(Color.parseColor("#DDDDDD"));
            }


            indicator.setVisibility(View.GONE);
            if (i == 1) {
                indicator.setVisibility(View.VISIBLE);
                if (Common.lostCarrier != null) {
                    if (Common.lostCarrier.size() > 0) {
                        view.setBackgroundColor(Color.RED);
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
                            nowView=v;
                            handler.sendEmptyMessage(0);
                        }}).start();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (i == 0) {
                                Common.showfirstgrid = false;
                                Common.showsecondgrid = false;
                                fragment = new InsertActivity();
                                switchFragment();
                            } else if (i == 1) {
                                return;
                            } else if (i == 2) {
                                fragment = new PriceActivity();
                                switchFragment();
                                PriceInvoice.first = true;
                            } else if (i == 3) {
                                fragment = new SelectActivity();
                                switchFragment();
                            } else if (i == 4) {
                                fragment = new SelectListModelActivity();
                                switchFragment();
                            } else if (i == 5) {
                                fragment = new GoalListAll();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("position", 0);
                                fragment.setArguments(bundle);
                                switchFragment();
                            } else if (i == 6) {
                                fragment = new SettingMain();
                                switchFragment();
                            } else if (i == 7) {
                                fragment = new HomePage();
                                switchFragment();
                            } else {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("https://www.youtube.com/playlist?list=PLrGq9ODiZ15rdXvKV_5FEIrdaP5ix0c55"));
                                startActivity(intent);
                            }
                            //重置InsertConsume
                            if (MainActivity.this.position != 0) {
                                InsertSpend.consumeVO = new ConsumeVO();
                                InsertSpend.needSet = false;
                                InsertIncome.needSet = false;
                                InsertIncome.bankVO = new BankVO();
                            }

                        }
                    }).start();

                    if(i==1)
                    {
                        setTitle(R.string.text_Ele);
                        if (doubleClick) {
                            listView.collapseGroup(1);
                            doubleClick = false;
                        } else {
                            listView.expandGroup(1);
                            doubleClick = true;
                        }
                    }else {
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
                if (i1 == 0 && Common.lostCarrier.size() > 0) {
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
                        intent.setData(Uri.parse("http://www.nknu.edu.tw/~psl/new.file/103/08/1030825reciept1.pdf"));
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
        Log.d("onActivityResult", "requestCode"+requestCode);
        String a;
        mFramgent = true;
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
                    for (int i = fragments.size() - 1; i >= 0; i--) {
                        if (fragments.get(i) instanceof SettingDownloadFile) {
                            fragments.get(i).onActivityResult(requestCode, resultCode, data);
                            break;
                        }
                    }
                    break;
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
                        if (fragments.get(i) instanceof SettingUploadFile) {
                            fragments.get(i).onActivityResult(requestCode, resultCode, data);
                            break;
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
                } else {
                    setConsume();
                }

            } else if (a.equals("UpdateSpend")) {
                if (resultCode == 9) {
                    data.putExtra("scan", "true");
                    searchQRCode(data);
                } else {
                    setUpdateConsume(data);
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

    private void setConsume() {
        if (BarcodeGraphic.hashMap == null) {
            return;
        }
        if (BarcodeGraphic.hashMap.size() <= 0) {
            return;
        }
        if (BarcodeGraphic.result != null) {
            InsertSpend.consumeVO = QRCodeNetResult(BarcodeGraphic.result, InsertSpend.consumeVO);
            if (InsertSpend.consumeVO.getDetailname() != null && BarcodeGraphic.hashMap.get(1) != null) {

                InsertSpend.needSet = true;
                String eleOne = BarcodeGraphic.hashMap.get(1).trim();
                String[] eleOneS = eleOne.trim().split(":");
                String EleNul = eleOneS[0].substring(0, 10);
                String day = eleOneS[0].substring(10, 17);
                String m = eleOneS[0].substring(29, 37);
                String rdNumber = eleOneS[0].substring(17, 21);
                Calendar calendar = new GregorianCalendar((Integer.valueOf(day.substring(0, 3)) + 1911), (Integer.valueOf(day.substring(3, 5)) - 1), Integer.valueOf(day.substring(5)), 12, 0, 0);
                InsertSpend.consumeVO.setMoney(Integer.parseInt(m, 16));
                InsertSpend.consumeVO.setNumber(EleNul);
                InsertSpend.consumeVO.setDate(new Date(calendar.getTimeInMillis()));
                InsertSpend.consumeVO.setRdNumber(rdNumber);
                fragment = new InsertActivity();
                switchFragment();
                return;
            }
        }

        if (BarcodeGraphic.hashMap.size() == 2) {

            StringBuilder sb = new StringBuilder();
            InsertSpend.needSet = true;
            String eleOne = BarcodeGraphic.hashMap.get(1).trim();
            String eleTwo = BarcodeGraphic.hashMap.get(2).trim();
            String[] eleOneS = eleOne.trim().split(":");
            String EleNul = eleOneS[0].substring(0, 10);
            String day = eleOneS[0].substring(10, 17);
            String m = eleOneS[0].substring(29, 37);
            String rdNumber = eleOneS[0].substring(17, 21);
            Calendar calendar = new GregorianCalendar((Integer.valueOf(day.substring(0, 3)) + 1911), (Integer.valueOf(day.substring(3, 5)) - 1), Integer.valueOf(day.substring(5)), 12, 0, 0);
            InsertSpend.consumeVO.setMoney(Integer.parseInt(m, 16));
            InsertSpend.consumeVO.setNumber(EleNul);
            InsertSpend.consumeVO.setDate(new Date(calendar.getTimeInMillis()));
            InsertSpend.consumeVO.setRdNumber(rdNumber);
            List<String> eleAll = new ArrayList<>();
            try {

                if (eleOneS[4].equals("2")) {
                    //Base64
                    eleAll.addAll(Arrays.asList(Common.Base64Convert(eleOneS[5])));
                    eleAll.addAll(Arrays.asList(Common.Base64Convert(eleTwo)));
                    Common.QRCodeToString(eleAll, sb);

                } else if (eleOneS[4].equals("0")) {
                    //Big5
                    eleAll.add(Common.Big5Convert(eleOneS[5]));
                    eleAll.add(Common.Big5Convert(eleOneS[6]));
                    eleAll.add(Common.Big5Convert(eleOneS[7]));
                    eleAll.addAll(Arrays.asList(Common.Big5Convert(eleTwo).split(":")));
                    Common.QRCodeToString(eleAll, sb);


                } else {

                    //UTF-8
                    //數量為1
                    if (eleOneS[3].equals("1")) {
                        sb.append(eleOneS[5].replaceAll("\\s+", "") + " :\n" + InsertSpend.consumeVO.getMoney() + " X 1 = " + InsertSpend.consumeVO.getMoney() + "\n");
                    } else {
                        eleAll.add((eleOneS[5]));
                        eleAll.add((eleOneS[6]));
                        eleAll.add((eleOneS[7]));
                        eleAll.addAll(Arrays.asList(eleTwo.split(":")));
                        Common.QRCodeToString(eleAll, sb);
                    }
                }

                if (sb.toString().trim().length() > 0) {
                    InsertSpend.consumeVO.setDetailname(sb.toString());
                    InsertSpend.consumeVO = getType(InsertSpend.consumeVO);
                } else {
                    InsertSpend.consumeVO.setDetailname("");
                    InsertSpend.consumeVO.setMaintype("O");
                    InsertSpend.consumeVO.setSecondType("O");
                }

            } catch (Exception e) {
                sb = new StringBuilder();
                sb.append("QRCode轉換失敗。\n請用\"QRCode下載功能\"。");
                Common.showToast(this, "QRCode轉換失敗。\n請用\"QRCode下載功能\"。");
            }

        } else {
            InsertSpend.consumeVO = setQRcodCon(InsertSpend.consumeVO);
        }
        fragment = new InsertActivity();
        switchFragment();
    }

    private ConsumeVO setQRcodCon(ConsumeVO consumeVO) {
        if (BarcodeGraphic.hashMap.get(1) != null) {
            try {
                InsertSpend.needSet = true;
                String[] EleNulAll = BarcodeGraphic.hashMap.get(1).split(":");
                String EleNul = EleNulAll[0].substring(0, 10);
                String day = EleNulAll[0].substring(10, 17);
                String m = EleNulAll[0].substring(29, 37);
                String rdNumber = EleNulAll[0].substring(17, 21);
                Calendar calendar = new GregorianCalendar((Integer.valueOf(day.substring(0, 3)) + 1911), (Integer.valueOf(day.substring(3, 5)) - 1), Integer.valueOf(day.substring(5)), 12, 0, 0);
                consumeVO.setMoney(Integer.parseInt(m, 16));
                consumeVO.setNumber(EleNul);
                consumeVO.setDate(new Date(calendar.getTimeInMillis()));
                consumeVO.setRdNumber(rdNumber);
                consumeVO.setMaintype("O");
                consumeVO.setSecondType("O");
            } catch (Exception e) {
                InsertSpend.needSet = false;
            }

        }
//        if (BarcodeGraphic.hashMap.get(2) != null) {
//            String s = BarcodeGraphic.hashMap.get(2);
//            String result = "";
//            if (s.indexOf(":") == -1) {
//                try {
//                    byte[] bytes = Base64.decode(s, Base64.DEFAULT);
//                    result = new String(bytes, "UTF-8");
//                } catch (Exception e) {
//                    result = "";
//                }
//            } else {
//                try {
//                    int codeNumber = Common.identify(s.getBytes("ISO-8859-1"));
//                    switch (codeNumber) {
//                        case 1:
//                            result = new String(s.getBytes("ISO-8859-1"), "Big5");
//                            break;
//                        case 2:
//                            result = s;
//                            break;
//                    }
//                } catch (Exception e1) {
//                    result = "";
//                }
//            }
//            StringBuffer sb = new StringBuffer();
//            if (result.trim().length() > 0) {
//                String[] ddd = result.trim().split(":");
//                ArrayList<String> answer = new ArrayList<>();
//                Double total, price, amount;
//                for (String string : ddd) {
//                    answer.add(string.replaceAll("\\s+", ""));
//                    if (answer.size() == 3) {
//                        price = Double.valueOf(Common.onlyNumber(answer.get(2)));
//                        amount = Double.valueOf(Common.onlyNumber(answer.get(1)));
//                        total = price * amount;
//                        sb.append(answer.get(0) + " :\n").append(answer.get(2) + " X ").append(answer.get(1) + " = ").append(Common.DoubleToInt(total) + "\n");
//                        answer.clear();
//                    }
//                }
//                if (sb.length() > 0) {
//                    consumeVO.setDetailname(sb.toString());
//                    consumeVO = getType(consumeVO);
//                } else {
//                    consumeVO.setDetailname(sb.toString());
//                    consumeVO.setMaintype("O");
//                    consumeVO.setSecondType("O");
//                }
//            }
//        }
        return consumeVO;
    }


    private void setUpdateConsume(Intent intent) {
        if (BarcodeGraphic.hashMap == null) {
            return;
        }
        if (BarcodeGraphic.hashMap.size() <= 0) {
            return;
        }
        Bundle bundle = intent.getBundleExtra("bundle");
        ConsumeVO consumeVO = (ConsumeVO) bundle.getSerializable("consumeVO");
        if (BarcodeGraphic.result != null) {
            consumeVO = QRCodeNetResult(BarcodeGraphic.result, consumeVO);
            if (consumeVO.getDetailname() != null) {
                String eleOne = BarcodeGraphic.hashMap.get(1).trim();
                String[] eleOneS = eleOne.trim().split(":");
                String EleNul = eleOneS[0].substring(0, 10);
                String day = eleOneS[0].substring(10, 17);
                String m = eleOneS[0].substring(29, 37);
                String rdNumber = eleOneS[0].substring(17, 21);
                Calendar calendar = new GregorianCalendar((Integer.valueOf(day.substring(0, 3)) + 1911), (Integer.valueOf(day.substring(3, 5)) - 1), Integer.valueOf(day.substring(5)), 12, 0, 0);
                consumeVO.setMoney(Integer.parseInt(m, 16));
                consumeVO.setNumber(EleNul);
                consumeVO.setDate(new Date(calendar.getTimeInMillis()));
                consumeVO.setRdNumber(rdNumber);
                bundle.putSerializable("consumeVO", consumeVO);
                fragment = new UpdateSpend();
                fragment.setArguments(bundle);
                switchFragment();
                return;
            }
        }

        if (BarcodeGraphic.hashMap.size() == 2) {

            StringBuilder sb = new StringBuilder();
            String eleOne = BarcodeGraphic.hashMap.get(1).trim();
            String eleTwo = BarcodeGraphic.hashMap.get(2).trim();
            String[] eleOneS = eleOne.trim().split(":");
            String EleNul = eleOneS[0].substring(0, 10);
            String day = eleOneS[0].substring(10, 17);
            String m = eleOneS[0].substring(29, 37);
            String rdNumber = eleOneS[0].substring(17, 21);
            Calendar calendar = new GregorianCalendar((Integer.valueOf(day.substring(0, 3)) + 1911), (Integer.valueOf(day.substring(3, 5)) - 1), Integer.valueOf(day.substring(5)), 12, 0, 0);
            consumeVO.setMoney(Integer.parseInt(m, 16));
            consumeVO.setNumber(EleNul);
            consumeVO.setDate(new Date(calendar.getTimeInMillis()));
            consumeVO.setRdNumber(rdNumber);
            List<String> eleAll = new ArrayList<>();
            try {
                if (eleOneS[4].equals("2")) {
                    //Base64
                    eleAll.addAll(Arrays.asList(Common.Base64Convert(eleOneS[5])));
                    eleAll.addAll(Arrays.asList(Common.Base64Convert(eleTwo)));
                    Common.QRCodeToString(eleAll, sb);
                } else if (eleOneS[4].equals("0")) {
                    //Big5

                    eleAll.add(Common.Big5Convert(eleOneS[5]));
                    eleAll.add(Common.Big5Convert(eleOneS[6]));
                    eleAll.add(Common.Big5Convert(eleOneS[7]));
                    eleAll.addAll(Arrays.asList(Common.Big5Convert(eleTwo).split(":")));
                    Common.QRCodeToString(eleAll, sb);

                } else {

                    //UTF-8
                    //數量為1
                    if (eleOneS[3].equals("1")) {
                        sb.append(eleOneS[5].replaceAll("\\s+", "") + " :\n" + InsertSpend.consumeVO.getMoney() + " X 1 = " + InsertSpend.consumeVO.getMoney() + "\n");
                    } else {
                        eleAll.add((eleOneS[5]));
                        eleAll.add((eleOneS[6]));
                        eleAll.add((eleOneS[7]));
                        eleAll.addAll(Arrays.asList(eleTwo.split(":")));
                        Common.QRCodeToString(eleAll, sb);
                    }

                }

                if (sb.toString().trim().length() > 0) {
                    consumeVO.setDetailname(sb.toString());
                    consumeVO = getType(consumeVO);
                } else {
                    consumeVO.setDetailname("");
                    consumeVO.setMaintype("O");
                    consumeVO.setSecondType("O");
                }


            } catch (Exception e) {
                sb = new StringBuilder();
                sb.append("QRCode轉換失敗。\n請用\"QRCode下載功能\"。");
                Common.showToast(this, "QRCode轉換失敗。\n請用\"QRCode下載功能\"。");
            }

        } else {
            consumeVO = setQRcodCon(consumeVO);
        }
        bundle.putSerializable("consumeVO", consumeVO);
        fragment = new UpdateSpend();
        fragment.setArguments(bundle);
        switchFragment();
    }

    private ConsumeVO getType(ConsumeVO consumeVO) {
        TypeDetailDB typeDetailDB = new TypeDetailDB(chargeAPPDB.getReadableDatabase());
        List<TypeDetailVO> typeDetailVOS = typeDetailDB.getTypdAll();
        String main = "O", second = "O";
        int x = 0, total = 0;
        for (TypeDetailVO t : typeDetailVOS) {
            x = 0;
            String[] key = t.getKeyword().split(" ");
            for (int i = 0; i < key.length; i++) {
                if (consumeVO.getDetailname().indexOf(key[i].trim()) != -1) {
                    x = x + key[i].length();
                }
            }
            if (x > total) {
                total = x;
                main = t.getGroupNumber();
                second = t.getName();
            }
        }
        if (second.indexOf("餐") != -1) {
            int hour = Integer.valueOf(Common.sHour.format(consumeVO.getDate()));
            if (hour > 0 && hour < 11) {
                second = "早餐";
            } else if (hour >= 11 && hour < 18) {
                second = "午餐";
            } else {
                second = "晚餐";
            }
        }
        consumeVO.setMaintype(main);
        consumeVO.setSecondType(second);
        return consumeVO;
    }


    public ConsumeVO QRCodeNetResult(String s, ConsumeVO consumeVO) {
        Gson gson = new Gson();
        JsonObject js = gson.fromJson(s, JsonObject.class);
        Type cdType = new TypeToken<List<JsonObject>>() {
        }.getType();
        String result = js.get("details").toString();
        List<JsonObject> b = gson.fromJson(result, cdType);
        double price, unit, unitTotal;
        double total = 0;
        StringBuilder sb = new StringBuilder();
        for (JsonObject jsonObject : b) {

            try {
                price = jsonObject.get("unitPrice").getAsDouble();
            } catch (Exception e) {
                price = 0;
            }

            try {
                unit = jsonObject.get("quantity").getAsDouble();
            } catch (Exception e) {
                unit = 0;
            }


            try {
                unitTotal = jsonObject.get("amount").getAsDouble();
            } catch (Exception e) {
                unitTotal = 0;
            }


            try {
                sb.append(jsonObject.get("description").getAsString());
            } catch (Exception e) {
                sb.append(jsonObject.get("錯誤").getAsString());
            }
            sb.append(":\n").append(Common.doubleRemoveZero(price)).append("X").append(Common.doubleRemoveZero(unit)).append("=").append(Common.doubleRemoveZero(unitTotal) + "\n");

            try {
                total = Double.valueOf(unitTotal) + total;
            } catch (Exception e) {

            }

        }
        consumeVO.setMoney(Common.DoubleToInt(total));
        consumeVO.setDetailname(sb.toString());
        consumeVO = getType(consumeVO);
        return consumeVO;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("MainActivity", String.valueOf(oldFramgent.size()) + ":" + String.valueOf(bundles.size()));
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (oldFramgent.size() == 0 || bundles.size() == 0) {
                OutDialogFragment aa = new OutDialogFragment();
                aa.setObject(MainActivity.this);
                aa.show(this.getSupportFragmentManager(), "show");
            } else {
                String action = oldFramgent.getLast();
                Bundle bundle = bundles.getLast();
                Log.d("MainActivity", action);
                if (action.equals("SelectActivity")) {
                    fragment = new SelectActivity();
                    fragment.setArguments(bundle);
                } else if (action.equals("SelectListPieIncome")) {
                    fragment = new SelectListPieIncome();
                    fragment.setArguments(bundle);
                } else if (action.equals("SelectListBarIncome")) {
                    fragment = new SelectListBarIncome();
                    fragment.setArguments(bundle);
                } else if (action.equals("SelectListModelIM")) {
                    fragment = new SelectListModelActivity();
                    fragment.setArguments(bundle);
                } else if (action.equals("SelectListModelCom")) {
                    fragment = new SelectListModelActivity();
                    fragment.setArguments(bundle);
                } else if (action.equals("SelectConsume")) {
                    fragment = new SelectActivity();
                    fragment.setArguments(bundle);
                } else if (action.equals("SelectOtherCircle")) {
                    fragment = new SelectOtherCircle();
                    fragment.setArguments(bundle);
                } else if (action.equals("SelectDetList")) {
                    fragment = new SelectDetList();
                    fragment.setArguments(bundle);
                } else if (action.equals("SelectShowCircleDe")) {
                    fragment = new SelectShowCircleDe();
                    fragment.setArguments(bundle);
                } else if (action.equals("SelectDetCircle")) {
                    fragment = new SelectDetCircle();
                    fragment.setArguments(bundle);
                } else if (action.equals("SettingListFix")) {
                    fragment = new SettingListFix();
                    fragment.setArguments(bundle);
                } else if (action.equals("SettingListFixIon")) {
                    fragment = new SettingListFixIon();
                    fragment.setArguments(bundle);
                } else if (action.equals("SettingListFixCon")) {
                    fragment = new SettingListFixCon();
                    fragment.setArguments(bundle);
                } else if (action.equals("SelectShowCircleDeList")) {
                    fragment = new SelectShowCircleDeList();
                    fragment.setArguments(bundle);
                } else if (action.equals("UpdateInvoice")) {
                    fragment = new UpdateInvoice();
                    fragment.setArguments(bundle);
                } else if (action.equals("UpdateSpend")) {
                    fragment = new UpdateSpend();
                    fragment.setArguments(bundle);
                } else if (action.equals("UpdateIncome")) {
                    fragment = new UpdateIncome();
                    fragment.setArguments(bundle);
                } else if (action.equals("HomePage")) {
                    fragment = new HomePage();
                    fragment.setArguments(bundle);
                } else if (action.equals("HomePagetList")) {
                    fragment = new HomePagetList();
                    fragment.setArguments(bundle);
                } else if (action.equals("InsertSpend") || action.equals("InsertIncome")) {
                    fragment = new InsertActivity();
                    fragment.setArguments(bundle);
                } else if (action.equals("SettingListType")) {
                    fragment = new SettingListType();
                    fragment.setArguments(bundle);
                } else if (action.equals("SettingMain")) {
                    fragment = new SettingMain();
                    fragment.setArguments(bundle);
                } else if (action.equals("GoalListAll")) {
                    fragment = new GoalListAll();
                    fragment.setArguments(bundle);
                } else if (action.equals("EleSetCarrier")) {
                    fragment = new EleSetCarrier();
                    fragment.setArguments(bundle);
                }

                //關閉keyboart
                View v = this.getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                oldFramgent.remove(oldFramgent.size() - 1);
                bundles.remove(bundles.size() - 1);
                switchFragment();
                return true;
            }
        }
        return true;
    }
}


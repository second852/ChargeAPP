package com.chargeapp.whc.chargeapp.Control;


import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.Model.EleMainItemVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.util.ArrayList;
import java.util.Calendar;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oldFramgent = new LinkedList<>();
        bundles = new LinkedList<>();
        SelectIncome.end= Calendar.getInstance();
        SelectIncome.Statue=0;
        Calendar calendar=Calendar.getInstance();
        SelectListModelCom.year=calendar.get(Calendar.YEAR);
        SelectListModelCom.month=calendar.get(Calendar.MONTH);
        SelectListModelCom.p=0;
        SelectListModelIM.year=calendar.get(Calendar.YEAR);
        SelectListModelIM.month=calendar.get(Calendar.MONTH);
        SelectListModelIM.p=0;
        SelectConsume.Statue=1;
        SelectConsume.end=Calendar.getInstance();
        SelectConsume.CStatue=0;
        SettingListFix.spinnerC=0;
        SettingListFix.p=0;
        setContentView(R.layout.activity_main);
        (getSupportActionBar()).show();
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
        final List<EleMainItemVO> itemSon = getElemainItemList();
        listView.setAdapter(new ExpandableAdapter(this, itemVOS, itemSon));
    }

    private List<EleMainItemVO> getElemainItemList() {
        List<EleMainItemVO> list = new ArrayList<>();
        list.add(new EleMainItemVO(R.string.text_SetCarrier, R.drawable.cellphone));
        list.add(new EleMainItemVO(R.string.text_DonateMain, R.drawable.health));
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
        return eleMainItemVOList;
    }


    private void switchFragment(Fragment fragment) {
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
        String a = getIntent().getStringExtra("action");
        if (a != null) {
            Fragment fragment = new EleSetCarrier();
            switchFragment(fragment);
        } else {
            Fragment fragment = new HomePage();
            switchFragment(fragment);
        }

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
            if (oldMainView == view && MainActivity.this.position == i) {
                rea.setBackgroundColor(Color.parseColor("#FFDD55"));
            } else {
                rea.setBackgroundColor(Color.parseColor("#DDDDDD"));
            }


            indicator.setVisibility(View.GONE);
            if (i == 1) {
                indicator.setVisibility(View.VISIBLE);
                if (b) {
                    indicator.setImageResource(R.drawable.arrow_up);
                } else {
                    indicator.setImageResource(R.drawable.arrow_down);
                }
            }
            EleMainItemVO member = list.get(i);
            ImageView ivImage =  view.findViewById(R.id.ivImage);
            ivImage.setImageResource(member.getImage());
            String mystring = getResources().getString(member.getIdstring());
            TextView tvId =  view.findViewById(R.id.tvId);
            tvId.setText(mystring);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment;
                    MainActivity.this.position = i;
                    oldFramgent.clear();
                    bundles.clear();
                    if (i == 0) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        fragment = new InsertActivity();
                        switchFragment(fragment);
                        listView.collapseGroup(i);
                        setTitle(R.string.text_Com);
                    } else if (i == 1) {
                        if (doubleClick) {
                            listView.collapseGroup(1);
                            doubleClick = false;
                        } else {
                            listView.expandGroup(1);
                            doubleClick = true;
                        }
                    } else if (i == 2) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        fragment = new PriceActivity();
                        switchFragment(fragment);
                        setTitle(R.string.text_Price);
                        listView.collapseGroup(i);
                    } else if (i == 3) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        fragment = new SelectActivity();
                        switchFragment(fragment);
                        listView.collapseGroup(i);
                        setTitle(R.string.text_DataPicture);
                    } else if (i == 4) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        fragment = new SelectListModelActivity();
                        switchFragment(fragment);
                        listView.collapseGroup(i);
                        setTitle(R.string.text_DataList);
                    } else if (i == 5) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        fragment = new GoalListAll();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("position", 0);
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                        listView.collapseGroup(i);
                        setTitle(R.string.text_Goal);
                    } else if (i == 6) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        fragment = new SettingMain();
                        switchFragment(fragment);
                        setTitle(R.string.text_Setting);
                        listView.collapseGroup(i);
                    } else {
                        setTitle(R.string.text_Home);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        fragment = new HomePage();
                        switchFragment(fragment);
                        listView.collapseGroup(i);
                    }
                    setColor(v);
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
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment;
                    drawerLayout.closeDrawer(GravityCompat.START);
                    if (i1 == 0) {
                        fragment = new EleSetCarrier();
                        setTitle(R.string.text_SetCarrier);
                        switchFragment(fragment);
                    } else if (i1 == 1) {
                        setTitle(R.string.text_DonateMain);
                        fragment = new EleDonateMain();
                        switchFragment(fragment);
                    } else if (i1 == 2) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://www.teach.ltu.edu.tw/public/News/11503/201412041535091.pdf"));
                        startActivity(intent);
                    } else if (i1 == 3) {
                        setTitle(R.string.text_NewCarrier);
                        fragment = new EleNewCarrier();
                        switchFragment(fragment);
                    } else if (i1 == 4) {
                        setTitle(R.string.text_EleBank);
                        fragment = new EleAddBank();
                        switchFragment(fragment);
                    } else if (i1 == 5) {
                        setTitle(R.string.text_HowGet);
                        fragment = new HowGetPrice();
                        switchFragment(fragment);
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://www.nknu.edu.tw/~psl/new.file/103/08/1030825reciept1.pdf"));
                        startActivity(intent);
                    }
                    if (oldSecondView != null && oldSecondView != view) {
                        oldSecondView.setBackgroundColor(Color.WHITE);
                    }
                    oldSecondView = view;
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            Fragment fragment = new SettingUploadFile();
            Bundle bundle = new Bundle();
            bundle.putSerializable("action", "all");
            fragment.setArguments(bundle);
            switchFragment(fragment);
        } else if (requestCode == 1) {
            Fragment fragment = new SettingUploadFile();
            Bundle bundle = new Bundle();
            bundle.putSerializable("action", "uploadTxt");
            fragment.setArguments(bundle);
            switchFragment(fragment);
        } else if (requestCode == 2) {
            Fragment fragment = new SettingUploadFile();
            Bundle bundle = new Bundle();
            bundle.putSerializable("action", "uploadExcel");
            fragment.setArguments(bundle);
            switchFragment(fragment);
        } else if (requestCode == 3) {
            Fragment fragment = new SettingUploadFile();
            Bundle bundle = new Bundle();
            bundle.putSerializable("action", "no");
            fragment.setArguments(bundle);
            switchFragment(fragment);
            if (resultCode == -1) {
                Common.showToast(this, "上傳成功");
            } else {
                Common.showToast(this, "上傳失敗");
            }
        } else if (requestCode == 4) {
            Fragment fragment = new SettingDownloadFile();
            Bundle bundle = new Bundle();
            bundle.putSerializable("action", "open");
            fragment.setArguments(bundle);
            switchFragment(fragment);
        } else if (requestCode == 5) {
            Fragment fragment = new SettingDownloadFile();
            Bundle bundle = new Bundle();
            if (resultCode == -1) {
                SettingDownloadFile.mSelectedFileDriveId = data.getParcelableExtra(
                        OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                bundle.putSerializable("action", "download");
                Common.showToast(this, "下傳成功");
            } else {
                bundle.putSerializable("action", "no");
                Common.showToast(this, "下傳失敗");
            }
            fragment.setArguments(bundle);
            switchFragment(fragment);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(oldFramgent.size()==0||bundles.size()==0)
        {
            OutDialogFragment aa= new OutDialogFragment();
            aa.setObject(MainActivity.this);
            aa.show(this.getSupportFragmentManager(),"show");
        }else{

          String action=oldFramgent.getLast();
          Bundle bundle=bundles.getLast();
            Log.d("MainActivity",action);
          Fragment fragment=null;
          if(action.equals("SelectActivity"))
          {
              fragment=new SelectActivity();
              fragment.setArguments(bundle);
          }else if(action.equals("SelectListPieIncome"))
          {
              fragment=new SelectListPieIncome();
              fragment.setArguments(bundle);
          }else if(action.equals("SelectListBarIncome"))
          {
              fragment=new SelectListBarIncome();
              fragment.setArguments(bundle);
          }else if(action.equals("SelectListModelIM"))
          {
              fragment=new SelectListModelActivity();
              fragment.setArguments(bundle);
          }else if(action.equals("SelectListModelCom"))
          {
              fragment=new SelectListModelActivity();
              fragment.setArguments(bundle);
          }else if(action.equals("SelectConsume"))
          {
              fragment=new SelectActivity();
              fragment.setArguments(bundle);
          }else if(action.equals("SelectOtherCircle"))
          {
              fragment=new SelectOtherCircle();
              fragment.setArguments(bundle);
          }else if(action.equals("SelectDetList"))
          {
              fragment=new SelectDetList();
              fragment.setArguments(bundle);
          }else if(action.equals("SelectShowCircleDe"))
          {
              fragment=new SelectShowCircleDe();
              fragment.setArguments(bundle);
          }else if(action.equals("SelectDetCircle"))
          {
              fragment=new SelectDetCircle();
              int index= (int) bundle.getSerializable("index");
              int day = (int) bundle.getSerializable("day");
              bundle.putSerializable("day",day-index);
              fragment.setArguments(bundle);
          }else if(action.equals("SettingListFix"))
          {
              fragment=new SettingListFix();
              fragment.setArguments(bundle);
          }else if(action.equals("SettingListFixIon"))
          {
              fragment=new SettingListFixIon();
              fragment.setArguments(bundle);
          }else if(action.equals("SettingListFixCon"))
          {
              fragment=new SettingListFixCon();
              fragment.setArguments(bundle);
          }
          oldFramgent.remove(oldFramgent.size()-1);
          bundles.remove(bundles.size()-1);
          switchFragment(fragment);
          return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}


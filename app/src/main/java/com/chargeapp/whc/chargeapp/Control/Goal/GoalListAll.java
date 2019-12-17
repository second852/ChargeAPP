package com.chargeapp.whc.chargeapp.Control.Goal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Adapter.DeleteDialogFragment;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.jsoup.internal.StringUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;




/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class GoalListAll extends Fragment {


    private ListView listView;
    private GoalDB goalDB;
    private ConsumeDB consumeDB;
    private InvoiceDB invoiceDB;
    private BankDB bankDB;
    private FloatingActionButton addGoal;
    private int goalSaveComplete;
    private boolean goalConsumeComplete;
    private TextView message;
    private int p;
    private Activity context;
    private boolean firstShow;
    private CurrencyDB currencyDB;



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
        firstShow = true;
        final View view = inflater.inflate(R.layout.goal_list, container, false);
        Common.setChargeDB(context);
        goalDB = new GoalDB(MainActivity.chargeAPPDB);
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB);
        bankDB = new BankDB(MainActivity.chargeAPPDB);
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);
        try {
            p = (int) getArguments().getSerializable("position");
        } catch (Exception e) {
            p = 0;
        }
        findViewById(view);


        setLayout();
        return view;
    }



    public void setLayout() {
        goalSaveComplete = 0;
        goalConsumeComplete = true;
        List<GoalVO> goalVOS = goalDB.getAll();
        if (goalVOS.size() <= 0) {
            message.setText("無目標紀錄!\n 請按右下角圖片新增!");
            message.setVisibility(View.VISIBLE);
            addGoal.show();
            addGoal.setOnClickListener(new addNewGoalClick());
        } else {
            message.setVisibility(View.GONE);
        }


        ListAdapter adapter = (ListAdapter) listView.getAdapter();
        if (adapter == null) {
            adapter = new ListAdapter(context, goalVOS);
            listView.setAdapter(adapter);
        } else {
            adapter.setGoalVOS(goalVOS);
            adapter.notifyDataSetChanged();
            listView.invalidate();
        }
        listView.setSelection(p);
    }


    private void findViewById(View view) {
        listView = view.findViewById(R.id.list);
        addGoal = view.findViewById(R.id.addGoal);
        message = view.findViewById(R.id.message);
    }


    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<GoalVO> goalVOS;

        ListAdapter(Context context, List<GoalVO> goalVOS) {
            this.context = context;
            this.goalVOS = goalVOS;
        }

        public void setGoalVOS(List<GoalVO> goalVOS) {
            this.goalVOS = goalVOS;
        }

        @Override
        public int getCount() {
            return goalVOS.size();
        }


        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.goal_list_item, parent, false);
            }
            final GoalVO goalVO = goalVOS.get(position);
            TextView title = itemView.findViewById(R.id.listTitle);
            TextView decribe = itemView.findViewById(R.id.listDetail);
            LinearLayout remindL = itemView.findViewById(R.id.remindL);
            LinearLayout fixL = itemView.findViewById(R.id.fixL);
            BootstrapButton fixT = itemView.findViewById(R.id.fixT);
            BootstrapButton update = itemView.findViewById(R.id.updateD);
            BootstrapButton deleteI = itemView.findViewById(R.id.deleteI);

            AwesomeTextView resultG = itemView.findViewById(R.id.resultG);
            AwesomeTextView notifyG = itemView.findViewById(R.id.notifyG);

            fixL.setVisibility(View.VISIBLE);
            title.setText(goalVO.getName());

            //設定敘述和檢驗目標
            int serial = 1 ;
            double totalI = 0, totalC = 0, totalB = 0, amount = 0;
            StringBuffer sb = new StringBuffer();
            StringBuffer sbResult = new StringBuffer();
            String timeDec = goalVO.getTimeStatue().trim();

            CurrencyVO currencyVO=currencyDB.getBytimeAndType(goalVO.getStartTime().getTime(),goalVO.getEndTime().getTime(),goalVO.getCurrency());
            if(StringUtil.isBlank(goalVO.getRealMoney()))
            {
                goalVO.setRealMoney(String.valueOf(goalVO.getMoney()));
                goalDB.update(goalVO);
            }


            Double goalMoney=Double.valueOf(goalVO.getRealMoney())*Double.valueOf(currencyVO.getMoney());

            if (timeDec.equals("今日")) {
                //描述目標
                sb.append(" " + serial + ". 起日 : " + Common.sTwo.format(goalVO.getStartTime()).trim());
                serial++;
                sb.append("\n "+serial+". 訖日 : "+ Common.sTwo.format(goalVO.getEndTime()).trim());
                serial++;
                sb.append("\n " + serial + ". " + goalVO.getType().trim() + " : " + Common.getCurrency(goalVO.getCurrency())+" "+goalVO.getRealMoney());
                serial++;

                //描述成果

                totalI = invoiceDB.getInvoiceByTimeHashMap(goalVO.getStartTime().getTime(),goalVO.getEndTime().getTime()).get("total");
                totalC = consumeDB.getTimePeriodHashMap(goalVO.getStartTime().getTime(), goalVO.getEndTime().getTime()).get("total");
                totalB = bankDB.getTimeTotal(goalVO.getStartTime(), goalVO.getEndTime());
                amount = totalB - totalC - totalI;

                Calendar endDay = new GregorianCalendar();
                endDay.setTime(goalVO.getEndTime());

                if (amount >goalMoney) {


                    //彈跳視窗 顯示可以
                    if (firstShow&&goalVO.getStatue()==0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("任務完成")
                                .setMessage("恭喜完成" + " 目標 : " + goalVO.getName() + goalVO.getTimeStatue() + goalVO.getType() + Common.getCurrency(goalVO.getCurrency())+goalVO.getRealMoney())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(R.drawable.ic_thumb_up)
                                .show();
                        firstShow = false;
                        //設定為完成
                        goalVO.setStatue(1);
                        goalVO.setNotify(false);
                        goalDB.update(goalVO);
                    }

                    sbResult.append(" " + serial + ". 成功 : 儲蓄"+Common.CurrencyResult(amount,currencyVO));
                    resultG.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                    serial++;
                } else {
                    if (goalVO.getEndTime().getTime()<System.currentTimeMillis()) {

                        //彈跳視窗 提醒時間過 任務未完成
                        if (firstShow&&goalVO.getStatue()==0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("任務未完成!")
                                    .setMessage("未完成" + " 目標 : " + goalVO.getName() + goalVO.getTimeStatue() + goalVO.getType() + Common.getCurrency(goalVO.getCurrency())+goalVO.getRealMoney())
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setIcon(R.drawable.warning)
                                    .show();
                            firstShow = false;
                            //設定狀態未完成
                            goalVO.setStatue(2);
                            goalVO.setNotify(false);
                            goalDB.update(goalVO);
                        }
                        double leaveMoney=goalMoney-amount;
                        sbResult.append(" " + serial + ". 失敗 : 儲蓄" + Common.CurrencyResult(amount,currencyVO));
                        serial++;
                        sbResult.append("\n " + serial +". 還缺 : "+Common.CurrencyResult(leaveMoney,currencyVO));
                        resultG.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                        serial++;
                    }else{

                        double leaveMoney=goalMoney-amount;
                        sbResult.append(" " + serial + ". 目前 : 儲蓄" + Common.CurrencyResult(amount,currencyVO));
                        serial++;
                        sbResult.append( "\n " + serial + ". 還缺 : "+Common.CurrencyResult(leaveMoney,currencyVO));
                        serial++;
                        double remainday=Double.valueOf(goalVO.getEndTime().getTime()-System.currentTimeMillis())/(1000*60*60*24);

                        if(remainday>0)
                        {
                            if(remainday<1)
                            {
                                Log.d("goal", String.valueOf(remainday));
                                double remainhour=remainday*24;
                                if(remainhour<1)
                                {
                                    double remainMin=remainhour*60;
                                    sbResult.append("\n " + serial + ". 倒數 : "+(int)remainMin+"分鐘");

                                }else{
                                    sbResult.append("\n " + serial + ". 倒數 : "+(int)remainhour+"小時");
                                }

                            }else {
                                sbResult.append("\n " + serial + ". 倒數 : "+(int)remainday+"天");
                            }
                        }
                        resultG.setBootstrapBrand(null);
                        resultG.setTextColor(Color.BLACK);
                    }
                }
                if (goalVO.getStatue() == 0) {
                    goalSaveComplete++;
                }
            } else {


                sb.append(" "+serial + ". ");
                sb.append(timeDec + goalVO.getType().trim() + " "+Common.getCurrency(goalVO.getCurrency())+" "+goalVO.getRealMoney());
                serial++;

                Calendar start = null, end = null;
                if (goalVO.getType().trim().equals("支出")) {
                    sbResult.append(" "+serial + ". ");
                    goalConsumeComplete = false;
                    Calendar cal = Calendar.getInstance();
                    switch (timeDec) {
                        case "每天":
                            sbResult.append("今日花費");
                            start = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                            end = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                            break;
                        case "每周":
                            sbResult.append("本周花費");
                            int sunday = cal.get(Calendar.DAY_OF_WEEK);
                            start = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), (cal.get(Calendar.DAY_OF_MONTH) - sunday + 1), 0, 0, 0);
                            end = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), (cal.get(Calendar.DAY_OF_MONTH) - sunday + 7), 23, 59, 59);
                            Log.d("goalXXXXXXx",Common.sTwo.format(new Date(start.getTimeInMillis()))+" : "+Common.sTwo.format(new Date(end.getTimeInMillis())));
                            break;
                        case "每月":
                            sbResult.append("本月花費");
                            start = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
                            end = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
                            break;
                        case "每年":
                            sbResult.append("今年花費");
                            start = new GregorianCalendar(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0);
                            end = new GregorianCalendar(cal.get(Calendar.YEAR), 11, 31, 23, 59, 59);
                            break;
                    }
                    if (start != null && end != null) {
                        totalI = invoiceDB.getInvoiceByTimeHashMap(start.getTimeInMillis(),end.getTimeInMillis()).get("total");
                        totalC = consumeDB.getTimePeriodHashMap(start.getTimeInMillis(), end.getTimeInMillis()).get("total");
                        double totalCon=totalI + totalC;
                        sbResult.append(" " + Common.CurrencyResult(totalCon,currencyVO));
                        if (goalMoney > totalCon) {
                            serial++;
                            sbResult.append("\n "+serial + ". 完成目標");
                            resultG.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                        }else{
                            resultG.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                        }
                    }
                } else {
                    Calendar cal = Calendar.getInstance();
                    sbResult.append(" "+serial + ". ");
                    switch (timeDec) {
                        case "每月":
                            sbResult.append("本月儲蓄");
                            start = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
                            end = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
                            break;
                        case "每年":
                            sbResult.append("今年儲蓄");
                            start = new GregorianCalendar(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0);
                            end = new GregorianCalendar(cal.get(Calendar.YEAR), 11, 31, 23, 59, 59);
                            break;
                    }
                    if (start != null && end != null) {
                        totalI = invoiceDB.getInvoiceByTimeHashMap(start.getTimeInMillis(), end.getTimeInMillis()).get("total");
                        totalC = consumeDB.getTimePeriodHashMap(start.getTimeInMillis(),end.getTimeInMillis()).get("total");
                        totalB = bankDB.getTimeTotal(new java.sql.Date(start.getTimeInMillis()),new java.sql.Date(end.getTimeInMillis()));
                        double saveMoney=totalB - totalI - totalC;
                        sbResult.append(" " +Common.CurrencyResult(saveMoney,currencyVO));
                        if (goalMoney < saveMoney) {
                            serial++;
                            sbResult.append("\n "+serial + ". 完成目標");
                            resultG.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                        } else {
                            resultG.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                        }
                    }
                    if (goalVO.getStatue() == 0) {
                        goalSaveComplete++;
                    }
                }
            }

            //result
            if(sbResult.length()>0)
            {
                resultG.setBootstrapText(new BootstrapText.Builder(context)
                        .addText(sbResult.toString())
                        .build());
            }else{
                resultG.setText(null);
            }


            //notify
            StringBuilder nbNotify = new StringBuilder();
            if (goalVO.isNotify()) {
                serial++;
                nbNotify.append(" " + serial + ". 提醒 : " + goalVO.getNotifyStatue().trim()).append(" " + goalVO.getNotifyDate());
                if (goalVO.isNoWeekend() && goalVO.getNotifyStatue().trim().equals("每天")) {
                    nbNotify.append("假日除外");
                }
                remindL.setVisibility(View.VISIBLE);
            } else {
                remindL.setVisibility(View.GONE);
            }
            if (nbNotify.length() > 0) {
                notifyG.setBootstrapText(new BootstrapText.Builder(context)
                        .addText(nbNotify.toString())
                        .build());
                notifyG.setTextColor(Color.BLACK);
            } else {
                notifyG.setText(null);
            }


            decribe.setText(sb.toString());


            boolean updateGoal;
            if (goalVO.getStatue() == 1) {
                fixT.setText("達成");
                fixT.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                updateGoal = false;
            } else if (goalVO.getStatue() == 2) {
                fixT.setText("失敗");
                fixT.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                updateGoal = false;
            } else {
                fixT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                fixT.setText("進行中");
                updateGoal = true;
            }

            if (updateGoal) {
                update.setVisibility(View.VISIBLE);
                update.setBackgroundColor(Color.parseColor("#33CCFF"));
                update.setText("修改");
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        Fragment fragment = new GoalUpdate();
                        bundle.putSerializable("goalVO", goalVO);
                        bundle.putSerializable("position", position);
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });
            } else {
                update.setVisibility(View.INVISIBLE);
            }
            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteDialogFragment aa = new DeleteDialogFragment();
                    aa.setObject(goalVO);
                    aa.setFragment(GoalListAll.this);
                    aa.show(getFragmentManager(), "show");
                }
            });

            if(position==(goalVOS.size()-1))
            {
                if (goalConsumeComplete) {
                    addGoal.show();
                    addGoal.setOnClickListener(new addNewGoalClick());
                } else if (goalSaveComplete == 0) {
                    addGoal.show();
                    addGoal.setOnClickListener(new addNewGoalClick());
                } else {
                    addGoal.hide();
                    addGoal.setOnClickListener(null);
                }
            }
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return goalVOS.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }


    private void switchFragment(Fragment fragment) {
        MainActivity.bundles.add(fragment.getArguments());
        MainActivity.oldFramgent.add("GoalListAll");
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

    private class addNewGoalClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Fragment fragment = new GoalInsert();
            Bundle bundle = new Bundle();
            if (goalConsumeComplete && (goalSaveComplete == 0)) {
                bundle.putSerializable("action", "all");
            } else if (goalConsumeComplete) {
                bundle.putSerializable("action", "Consume");
            } else if (goalSaveComplete == 0) {
                bundle.putSerializable("action", "Save");
            }
            fragment.setArguments(bundle);
            switchFragment(fragment);
        }
    }
}

package com.chargeapp.whc.chargeapp.Control;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumerDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class PriceInvoice extends Fragment {
    private ImageView DRadd, DRcut, PIdateAdd, PIdateCut;
    private TextView DRcarrier, DRmessage, PIdateTittle;
    private RecyclerView donateRL;
    private InvoiceDB invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private CarrierDB carrierDB = new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private PriceDB priceDB = new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    public int choiceca = 0;
    private Calendar now = Calendar.getInstance();
    private SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
    private SimpleDateFormat sf = new SimpleDateFormat("M/dd");
    private String[] level = {"first", "second", "third", "fourth", "fifth", "sixth"};
    public static AsyncTask<Object, Integer, String> getGetSQLDate1;
    public static AsyncTask<Object, Integer, String> getGetSQLDate2;
    private ProgressDialog progressDialog;
    private List<CarrierVO> carrierVOS;
    private ConsumerDB consumerDB;
    private HashMap<String,String> levelprice;
    private HashMap<String,Integer> levellength;
    private long start,end;
    private RelativeLayout DRshow;
    private TextView showRemain;
    private String period;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.price_invoice, container, false);
        findViewById(view);
        levelprice=getHashLP();
        levellength=getlevellength();
        DRadd.setOnClickListener(new addOnClick());
        DRcut.setOnClickListener(new cutOnClick());
        PIdateAdd.setOnClickListener(new addMonth());
        PIdateCut.setOnClickListener(new cutMonth());
        carrierVOS = carrierDB.getAll();
        if(carrierVOS.size()>0&&carrierVOS!=null)
        {
            String carrier=carrierVOS.get(choiceca).getCarNul();
            DRcarrier.setText(carrier);
            DRshow.setVisibility(View.VISIBLE);
        }else{
            DRshow.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        download();
    }

    private HashMap<String,Integer> getlevellength() {
        HashMap<String,Integer> hashMap=new HashMap<>();
        hashMap.put("super",2);
        hashMap.put("spc",2);
        hashMap.put("first",2);
        hashMap.put("second",3);
        hashMap.put("third",4);
        hashMap.put("fourth",5);
        hashMap.put("fifth",6);
        hashMap.put("sixth",7);
        return hashMap;
    }


    private HashMap<String,String> getHashLP() {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("super","特別獎\n1000萬元");
        hashMap.put("spc","特獎\n200萬元");
        hashMap.put("first","頭獎\n20萬元");
        hashMap.put("second","二獎\n4萬元");
        hashMap.put("third","三獎\n1萬元");
        hashMap.put("fourth","四獎\n4千元");
        hashMap.put("fifth","五獎\n1千元");
        hashMap.put("sixth","六獎\n200元");
        return hashMap;
    }

    public  void noconnect()
    {
        progressDialog.cancel();
        Common.showLongToast(getActivity(),"沒有網路，更新失敗~");
        AutoSetCMPrice();
        AutoSetInPrice();
    }

    private void download() {
        boolean showcircle=false;
        List<PriceVO> priceVOS = priceDB.getAll();
        if (priceVOS == null || priceVOS.size() <= 0) {
            new GetSQLDate(this).execute("getAllPriceNul");
        } else {
            if (PriceInvoice.getGetSQLDate1 == null) {
                PriceInvoice.getGetSQLDate1 = new GetSQLDate(this).execute("getNeWPrice");
                showcircle=true;
            }
        }
        if (PriceInvoice.getGetSQLDate2 == null && carrierVOS != null&&carrierVOS.size()>0) {
            PriceInvoice.getGetSQLDate2 = new GetSQLDate(this).execute("GetToday");
            showcircle=true;
        }
        if(showcircle)
        {
            progressDialog.setMessage("正在更新資料,請稍候...");
            progressDialog.show();
        }else{
            AutoSetCMPrice();
            AutoSetInPrice();
        }
    }

    public void AutoSetCMPrice() {
        List<PriceVO> priceVOS = priceDB.getAll();
        int month;
        int year;
        for (PriceVO priceVO : priceVOS) {
            long startTime, endTime;
            String invoYM = priceVO.getInvoYm();
            month = Integer.valueOf(invoYM.substring(invoYM.length() - 2));
            year = Integer.valueOf(invoYM.substring(0, invoYM.length() - 2)) + 1911;
            startTime = (new GregorianCalendar(year, month - 2, 1,0,0,0)).getTimeInMillis();
            Calendar endC=new GregorianCalendar(year, month-1, 1);
            endTime = (new GregorianCalendar(year, month-1, endC.getActualMaximum(Calendar.DAY_OF_MONTH),59,59)).getTimeInMillis();
            String show=sd.format(new Date(startTime));
            String shod=sd.format(new Date(endTime));
            autoSetCRWin(startTime, endTime, priceVO);
        }
        if(getGetSQLDate2==null)
        {
            progressDialog.cancel();
            period=priceDB.findMaxPeriod();
            setMonText(period,"in");
        }
    }

    public void AutoSetInPrice() {
        List<PriceVO> priceVOS = priceDB.getAll();
        int month;
        int year;
        for (PriceVO priceVO : priceVOS) {
            long startTime, endTime;
            String invoYM = priceVO.getInvoYm();
            month = Integer.valueOf(invoYM.substring(invoYM.length() - 2));
            year = Integer.valueOf(invoYM.substring(0, invoYM.length() - 2)) + 1911;
            startTime = (new GregorianCalendar(year, month - 2, 1,0,0,0)).getTimeInMillis();
            Calendar endC=new GregorianCalendar(year, month-1, 1);
            endTime = (new GregorianCalendar(year, month-1, endC.getActualMaximum(Calendar.DAY_OF_MONTH),23,59,59)).getTimeInMillis();
            autoSetInWin(startTime, endTime, priceVO);
            progressDialog.cancel();
            period=priceDB.findMaxPeriod();
            setMonText(period,"in");
        }
    }


    private void autoSetCRWin(long startTime, long endTime, PriceVO priceVO) {
        List<ConsumeVO> consumeVOS = consumerDB.getNoWinAll(startTime, endTime);
        for (ConsumeVO consumeVO : consumeVOS) {
            String nul = consumeVO.getNumber().trim();
            consumeVO.setIsWin("N");
            if (nul != null && nul.trim().length() == 10) {
                nul = nul.substring(2);
                String aw = anwswer(nul, priceVO);
                consumeVO.setIsWin(aw);
            }
            consumerDB.update(consumeVO);
        }
    }

    private String anwswer(String nul, PriceVO priceVO) {
        String threenul = nul.substring(5);
        String s;
        if (nul.equals(priceVO.getSuperPrizeNo())) {
            return "super";
        }
        if (nul.equals(priceVO.getSpcPrizeNo())) {
            return "spc";
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo1());
        if (!s.equals("N")) {
            return s;
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo2());
        if (!s.equals("N")) {
            return s;
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo3());
        if (!s.equals("N")) {
            return s;
        }
        if (threenul.equals(priceVO.getSixthPrizeNo1())) {
            return "sixth";
        }
        if (threenul.equals(priceVO.getSixthPrizeNo2())) {
            return "sixth";
        }
        if (threenul.equals(priceVO.getSixthPrizeNo3())) {
            return "sixth";
        }
        if (threenul.equals(priceVO.getSixthPrizeNo4())) {
            return "sixth";
        }
        if (threenul.equals(priceVO.getSixthPrizeNo5())) {
            return "sixth";
        }
        if (threenul.equals(priceVO.getSixthPrizeNo6())) {
            return "sixth";
        }
        return "N";
    }


    private void autoSetInWin(long startTime, long endTime, PriceVO priceVO) {
        List<CarrierVO> carrierVOS = carrierDB.getAll();
        for (CarrierVO c : carrierVOS) {
            List<InvoiceVO> invoiceVOS = invoiceDB.getNotSetWin(c.getCarNul(), startTime, endTime);
            for (InvoiceVO i : invoiceVOS) {
                String nul = i.getInvNum().substring(2);
                String inWin = anwswer(nul, priceVO);
                i.setIswin(inWin);
                invoiceDB.update(i);
            }
        }
    }

    private String firsttofourprice(String nul, String pricenul) {
        for (int i = 0; i < 6; i++) {
            if (nul.substring(i).equals(pricenul.substring(i))) {
                return level[i];
            }
        }
        return "N";
    }

    private void setMonText(String time, String action) {
         String showtime;
         String month=time.substring(time.length() - 2);
         int addmonth= Integer.parseInt(time.substring(time.length() - 2));
         int year= Integer.parseInt(time.substring(0, time.length() - 2))+1911;
        if (month.equals("02")) {
            showtime =time.substring(0, time.length() - 2)  + "年1-2月";
            start=(new GregorianCalendar(year,0,1,0,0,0)).getTimeInMillis();
            end=(new GregorianCalendar(year,2,1,0,0,0)).getTimeInMillis()-1000;
        } else if (month.equals("04")) {
            showtime = time.substring(0, time.length() - 2) + "年3-4月";
            start=(new GregorianCalendar(year,2,1,0,0,0)).getTimeInMillis();
            end=(new GregorianCalendar(year,4,1,0,0,0)).getTimeInMillis()-1000;
        } else if (month.equals("06")) {
            showtime = time.substring(0, time.length() - 2) + "年5-6月";
            start=(new GregorianCalendar(year,4,1,0,0,0)).getTimeInMillis();
            end=(new GregorianCalendar(year,6,1,0,0,0)).getTimeInMillis()-1000;
        } else if (month.equals("08")) {
            showtime = time.substring(0, time.length() - 2) + "年7-8月";
            start=(new GregorianCalendar(year,6,1,0,0,0)).getTimeInMillis();
            end=(new GregorianCalendar(year,8,1,0,0,0)).getTimeInMillis()-1000;
        } else if (month.equals("10")) {
            showtime =time.substring(0, time.length() - 2) + "年9-10月";
            start=(new GregorianCalendar(year,8,1,0,0,0)).getTimeInMillis();
            end=(new GregorianCalendar(year,10,1,0,0,0)).getTimeInMillis()-1000;
        } else {
            showtime =time.substring(0, time.length() - 2) + "年11-12月";
            start=(new GregorianCalendar(year,10,1,0,0,0)).getTimeInMillis();
            end=(new GregorianCalendar(year+1,0,1,0,0,0)).getTimeInMillis()-1000;
        }
        PriceVO priceVO = priceDB.getPeriodAll(time);
        String period;
        if (priceVO == null && action.equals("add")) {
            addmonth = addmonth - 2;
            if(addmonth<=0)
            {
                period=String.valueOf(year-1911-1)+"12";
            }else{
                month=String.valueOf(addmonth);
                for(int i=month.length();i<2;i++)
                {
                    month="0"+month;
                }
                period=time.substring(0, time.length() - 2)+month;
            }
            setMonText(period,"add");
            Common.showToast(getActivity(), showtime+"尚未開獎");
            return;
        }
        if (priceVO == null && action.equals("cut")) {
            addmonth = addmonth + 2;
            if(addmonth>12)
            {
                period=String.valueOf(year-1911+1)+"02";
            }else{
                month=String.valueOf(addmonth);
                for(int i=month.length();i<2;i++)
                {
                    month="0"+month;
                }
                period=time.substring(0, time.length() - 2)+month;
            }
            setMonText(period,"cut");
            Common.showToast(getActivity(), "沒有資料");
            return;
        }
        PIdateTittle.setText(showtime);
        setlayout();
    }


    private class cutOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            choiceca--;
            if (choiceca < 0) {
                choiceca = carrierVOS.size() - 1;
            }
            setlayout();
        }
    }

    private class addOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choiceca++;
            if (choiceca > (carrierVOS.size() - 1)) {
                choiceca = 0;
            }
            setlayout();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    private void setlayout() {
        Log.d("ssss",sd.format(new Date(start))+":"+sd.format(new Date(end)));
        List<Object> objectList=new ArrayList<>();
        if(carrierVOS.size()>0&&carrierVOS!=null)
        {
            String carrier=carrierVOS.get(choiceca).getCarNul();
            List<InvoiceVO> invoiceVOS=invoiceDB.getWinIn(carrier,start,end);
            objectList.addAll(invoiceVOS);
            DRcarrier.setText(carrier);
        }
        List<ConsumeVO> consumeVOS=consumerDB.getWinAll(start,end);
        objectList.addAll(consumeVOS);
        if(objectList.size()>0)
        {
            donateRL.setVisibility(View.VISIBLE);
            donateRL.setLayoutManager(new LinearLayoutManager(getActivity()));
            donateRL.setAdapter(new InvoiceAdapter(getActivity(),objectList));
            DRmessage.setVisibility(View.GONE);
        }else {
            DRmessage.setVisibility(View.VISIBLE);
            DRmessage.setText("本期發票沒有中獎!");
            donateRL.setVisibility(View.GONE);
        }
    }

    private void findViewById(View view) {
        DRadd = view.findViewById(R.id.DRadd);
        DRcut = view.findViewById(R.id.DRcut);
        DRcarrier = view.findViewById(R.id.DRcarrier);
        donateRL = view.findViewById(R.id.donateRL);
        DRmessage = view.findViewById(R.id.DRmessage);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
        DRshow=view.findViewById(R.id.DRshow);
        showRemain=view.findViewById(R.id.showRemain);
        showRemain.setText("(無實體電子發票專屬獎中獎清單\n請到財政部網站確認)");
        progressDialog = new ProgressDialog(getActivity());
        consumerDB = new ConsumerDB(MainActivity.chargeAPPDB.getReadableDatabase());
    }

    private class InvoiceAdapter extends
            RecyclerView.Adapter<PriceInvoice.InvoiceAdapter.MyViewHolder> {
        private Context context;
        private List<Object> invoiceVOList;


        InvoiceAdapter(Context context, List<Object> memberList) {
            this.context = context;
            this.invoiceVOList = memberList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView day, nul, checkdonate;


            MyViewHolder(View itemView) {
                super(itemView);
                checkdonate = itemView.findViewById(R.id.DRdate);
                day = itemView.findViewById(R.id.QrCodeA);
                nul = itemView.findViewById(R.id.DRamout);
            }
        }

        @Override
        public int getItemCount() {
            return invoiceVOList.size();
        }

        @Override
        public PriceInvoice.InvoiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.ele_setdenote_record_item, viewGroup, false);
            return new PriceInvoice.InvoiceAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PriceInvoice.InvoiceAdapter.MyViewHolder viewHolder, int position) {
            final Object object = invoiceVOList.get(position);
            String title,nul,message,iswin;
            if (object instanceof InvoiceVO)
            {
                InvoiceVO invoiceVO= (InvoiceVO) object;
                title="電子發票"+(invoiceVO.getDonateMark().equals("true")?"已捐贈":"未捐贈");
                nul=invoiceVO.getInvNum();
                message=levelprice.get(invoiceVO.getIswin());
                iswin=invoiceVO.getIswin();
            }else{
                ConsumeVO consumeVO= (ConsumeVO) object;
                Calendar calendar=Calendar.getInstance();
                calendar.setTime(consumeVO.getDate());
                title=sf.format(consumeVO.getDate())+"\n實體發票";
                nul=consumeVO.getNumber();
                message=levelprice.get(consumeVO.getIsWin());
                iswin=consumeVO.getIsWin();
            }

            SpannableString content = new SpannableString(nul);
            content.setSpan(new ForegroundColorSpan(Color.RED), levellength.get(iswin), nul.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.checkdonate.setText(title);
            viewHolder.day.setText(content);
            viewHolder.nul.setText(message);
        }
    }

    private class addMonth implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int addmonth= Integer.parseInt(period.substring(period.length() - 2));
            int year= Integer.parseInt(period.substring(0, period.length() - 2))+1911;
            String month;
             addmonth = addmonth + 2;
            if(addmonth>12)
            {
                period=String.valueOf(year-1911+1)+"02";
            }else{
                month=String.valueOf(addmonth);
                for(int i=month.length();i<2;i++)
                {
                    month="0"+month;
                }
                period=period.substring(0, period.length() - 2)+month;
            }
            setMonText(period, "add");
        }
    }

    private class cutMonth implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int addmonth= Integer.parseInt(period.substring(period.length() - 2));
            int year= Integer.parseInt(period.substring(0, period.length() - 2))+1911;
            String month;
            addmonth = addmonth - 2;
            if(addmonth<=0)
            {
                period=String.valueOf(year-1911-1)+"12";
            }else{
                month=String.valueOf(addmonth);
                for(int i=month.length();i<2;i++)
                {
                    month="0"+month;
                }
                period=period.substring(0, period.length() - 2)+month;
            }
            setMonText(period, "cut");
        }
    }
}

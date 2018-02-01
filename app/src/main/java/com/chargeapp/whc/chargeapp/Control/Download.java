package com.chargeapp.whc.chargeapp.Control;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.R;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by 1709008NB01 on 2018/1/29.
 */

public class Download extends Fragment {
    private PriceDB priceDB;
    private CarrierDB carrierDB;
    private String TAG="Download";
    private InvoiceDB invoiceDB;
    private ConsumeDB consumeDB;
    private List<CarrierVO> carrierVOS;
    private String[] level = {"first", "second", "third", "fourth", "fifth", "sixth"};



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.download_main, container, false);
        priceDB=new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB =new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierVOS=carrierDB.getAll();
//        tonewActivity();
        download();
        return view;
    }

    private void download() {
        List<PriceVO> priceVOS = priceDB.getAll();
        if (priceVOS == null || priceVOS.size() <= 0) {
            new GetSQLDate(this).execute("getAllPriceNul");
        } else {
            new GetSQLDate(this).execute("getNeWPrice");
        }
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
    public void newInvoice()
    {
        if (carrierVOS != null&&carrierVOS.size()>0) {
           new GetSQLDate(this).execute("GetToday");
        }else {
            AutoSetPrice();
        }
    }

    public void AutoSetPrice() {
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
            autoSetCRWin(startTime, endTime, priceVO);
            autoSetInWin(startTime, endTime, priceVO);
        }
        tonewActivity();
    }

    private void autoSetCRWin(long startTime, long endTime, PriceVO priceVO) {
        List<ConsumeVO> consumeVOS = consumeDB.getNoWinAll(startTime, endTime);
        for (ConsumeVO consumeVO : consumeVOS) {
            String nul = consumeVO.getNumber().trim();
            consumeVO.setIsWin("N");
            if (nul != null && nul.trim().length() == 10) {
                nul = nul.substring(2);
                String aw = anwswer(nul, priceVO);
                consumeVO.setIsWin(aw);
            }
            consumeDB.update(consumeVO);
        }
    }


    public void tonewActivity()
    {
        FragmentTransaction fragmentTransaction =getActivity().getSupportFragmentManager().beginTransaction();
        List<Fragment> fragments=getActivity().getSupportFragmentManager().getFragments();
        for(Fragment f:fragments)
        {
            fragmentTransaction.remove(f);
        }
        getActivity().setTitle(R.string.text_Price);
        Fragment fragment=new PriceActivity();
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

}

package com.chargeapp.whc.chargeapp.Control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chargeapp.whc.chargeapp.ChargeDB.BankTypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;

import java.util.List;


/**
 * Created by 1709008NB01 on 2018/1/29.
 */

public class Download extends Fragment {

    public static int[] imageAll = {
            R.drawable.food, R.drawable.phone, R.drawable.clothes, R.drawable.traffic, R.drawable.teach, R.drawable.happy,
            R.drawable.home, R.drawable.medical, R.drawable.invent,
            R.drawable.worker, R.drawable.bus, R.drawable.breakfast, R.drawable.cellphone, R.drawable.clothe, R.drawable.dinner,
            R.drawable.drink, R.drawable.easycard, R.drawable.easygo, R.drawable.electricty, R.drawable.fruit,
            R.drawable.gasstation, R.drawable.highspeedtrain, R.drawable.hospital, R.drawable.image, R.drawable.internet,
            R.drawable.jacket, R.drawable.water, R.drawable.university, R.drawable.trousers, R.drawable.treatment, R.drawable.training,
            R.drawable.ticket, R.drawable.supplement, R.drawable.subway, R.drawable.rent, R.drawable.rentfilled, R.drawable.phonet,
            R.drawable.origin, R.drawable.movie, R.drawable.microphone, R.drawable.lunch, R.drawable.losemoney, R.drawable.lipgloss, R.drawable.train
            , R.drawable.salary, R.drawable.lotto, R.drawable.bouns, R.drawable.interest, R.drawable.fund, R.drawable.bank, R.drawable.health,
            R.drawable.shose, R.drawable.book, R.drawable.setting, R.drawable.search, R.drawable.export,R.drawable.add,R.drawable.property
    };

    private String food = "堡 三明治 優酪乳 肉 飯 雙手卷 腿 麵 麵包 熱狗 雞 手卷 肉 粉 蔬菜 牛 豬 起司 花生 豆 蛋 魚 菜 瓜 黑胡椒 土司 泡芙 排";
    private String drink = "咖啡 茶 豆漿 拿鐵 乳 飲 ml 罐 酒 杯 水 奶 冰 珍珠";
    private GetSQLDate getSQLDate;
    private TextView percentage, progressT;
    public Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            activity= (Activity) context;
        }else{
            activity=getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.download_main, container, false);
        progressT = view.findViewById(R.id.progressT);
        percentage = view.findViewById(R.id.percentage);
        originDownload();
        return view;
    }


    private void originDownload()
    {
        Common.setChargeDB(activity);
        setData();
        ConnectivityManager mConnectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            getSQLDate = new GetSQLDate(this,activity);
            getSQLDate.setPercentage(percentage);
            getSQLDate.setProgressT(progressT);
            getSQLDate.execute("download","3");
            new GetSQLDate(this,activity).execute("getWinInvoice");
        } else {
            Common.showToast(activity, "網路沒有開啟，無法下載!");
        }
        tonNewActivity();
    }

    public void tonNewActivity() {
        //set origin
        (new Common()).AutoSetPrice();
        Intent intent=new Intent();
        startActivity(intent.setClass(activity, MainActivity.class));
        activity.finish();
    }


    public void setData() {
        TypeDB typeDB = new TypeDB(MainActivity.chargeAPPDB);
        TypeDetailDB typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB);
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
        typeDetailDB.insert(new TypeDetailVO("住宿", "水費", indexOfIntArray(imageAll, R.drawable.price_button), "0"));
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
        BankTypeDB bankTypeDB = new BankTypeDB(MainActivity.chargeAPPDB);
        bankTypeDB.insert(new BankTypeVO("薪水", "薪水", indexOfIntArray(imageAll, R.drawable.salary)));
        bankTypeDB.insert(new BankTypeVO("薪水", "中獎", indexOfIntArray(imageAll, R.drawable.lotto)));
        bankTypeDB.insert(new BankTypeVO("薪水", "獎金", indexOfIntArray(imageAll, R.drawable.bouns)));
        bankTypeDB.insert(new BankTypeVO("薪水", "基金", indexOfIntArray(imageAll, R.drawable.fund)));
        bankTypeDB.insert(new BankTypeVO("薪水", "股票", indexOfIntArray(imageAll, R.drawable.origin)));
        bankTypeDB.insert(new BankTypeVO("薪水", "利息", indexOfIntArray(imageAll, R.drawable.bank)));
        bankTypeDB.insert(new BankTypeVO("薪水", "股利", indexOfIntArray(imageAll, R.drawable.interest)));
    }

    public int indexOfIntArray(int[] array, int key) {
        int returnvalue = 0;
        for (int i = 0; i < array.length; ++i) {
            if (key == array[i]) {
                returnvalue = i;
                break;
            }
        }
        return returnvalue;
    }

}

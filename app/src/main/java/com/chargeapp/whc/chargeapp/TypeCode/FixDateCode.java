package com.chargeapp.whc.chargeapp.TypeCode;

import org.jsoup.helper.StringUtil;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum FixDateCode {

    FixDay(0,"FixDay","每天"),
    FixWeek(1,"FixWeek","每周"),
    FixMonth(2,"FixMonth","每月"),
    FixYear(3,"FixYear","每年");

    private final Integer code;
    private final String name;
    private final String detail;
    public static Map<String,FixDateCode> mapString;

    FixDateCode(Integer code, String name, String detail) {
        this.code = code;
        this.name = name;
        this.detail = detail;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }

    static {
        Map<String,FixDateCode> map = new ConcurrentHashMap<String, FixDateCode>();
        for (FixDateCode instance : FixDateCode.values()) {
            map.put(instance.getDetail(),instance);
        }
        mapString = Collections.unmodifiableMap(map);
    }

    public static FixDateCode detailToEnum(String detail)
    {
        if(StringUtil.isBlank(detail))
        {
            return null;
        }else {
            return mapString.get(detail);
        }

    }

}

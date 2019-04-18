package com.chargeapp.whc.chargeapp.TypeCode;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum FixDateCode {

    FixDay(1,"FixDay","每天"),
    FixWeek(2,"FixWeek","每周"),
    FixMonth(3,"FixMonth","每月"),
    FixQuarterly(4,"FixQuarterly","每季"),
    FixYear(5,"FixYear","每年");

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
        return mapString.get(detail);
    }

}

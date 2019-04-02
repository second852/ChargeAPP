package com.chargeapp.whc.chargeapp.TypeCode;

public enum FixDateCode {

    FixDay(1,"FixDay","每日"),
    FixWeek(2,"FixWeek","每周"),
    FixMonth(3,"FixMonth","每月"),
    FixQuarterly(4,"FixQuarterly","每季"),
    FixYear(4,"FixYear","每年");

    private final Integer code;
    private final String name;
    private final String detail;

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
}

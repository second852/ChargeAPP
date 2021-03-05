package com.chargeapp.whc.chargeapp.TypeCode;

public enum PriceCheck {
    notCheck(0, "無確認"),
    isCheck(1, "已確認");

    private final Integer code;
    private final String name;

    PriceCheck(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static PriceCheck codeToEnum(Integer code) {
        switch (code) {
            case 0:
                return PriceCheck.notCheck;
            case 1:
                return PriceCheck.isCheck;
            default:
                return null;
        }
    }
}

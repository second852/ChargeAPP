package com.chargeapp.whc.chargeapp.TypeCode;

public enum  PriceNotify {

    Normal(0,"無特別獎"),
    Special(1,"專屬獎通知"),
    Notified(2,"已通知")
    ;



    private  final Integer code;
    private  final String name;

    PriceNotify(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


    public static PriceNotify codeToEnum(Integer code)
    {
        switch (code)
        {
            case 0:
                return PriceNotify.Normal;
            case 1:
                return PriceNotify.Special;
            case 2:
                return PriceNotify.Notified;
            default:
                return null;
        }
    }

}

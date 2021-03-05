package com.chargeapp.whc.chargeapp.TypeCode;

public enum  PriceNotify {

    NotCheck(0,"還沒確認"),
    Normal(1,"無特別獎"),
    Special(2,"專屬獎通知"),
    Notified(3,"已通知")
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
                return PriceNotify.NotCheck;
            case 1:
                return PriceNotify.Normal;
            case 2:
                return PriceNotify.Special;
            case 3:
                return PriceNotify.Notified;
            default:
                return null;
        }
    }

}

package com.chargeapp.whc.chargeapp.TypeCode;

public enum ResultCode {

    GoogleDownload(-1,"下載雲端檔案中"),

    ;

    private int code; //代碼
    private String name;//名子

    ResultCode(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

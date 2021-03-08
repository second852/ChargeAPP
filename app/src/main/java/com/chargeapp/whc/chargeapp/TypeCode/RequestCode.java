package com.chargeapp.whc.chargeapp.TypeCode;

public enum RequestCode {
    UpLoadLocalOpen(0,"上傳頁面打開Local"),
    UpLoadGoogleUpload(3,"上傳頁面上傳雲端"),
    DownLoadGoogleOpen(4,"下傳頁面打開雲端"),
    DownLoadGoogleDownload(5,"下傳頁面下載雲端"),
    Dropbox(6,"dropbox 上傳/下載"),
    ;




    private int code; //代碼
    private String name;//名子

    RequestCode(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RequestCode getEnum(int code){
        RequestCode requestCode=null;
        for(RequestCode r:RequestCode.values()){
            if(r.code==code){
                requestCode=r;
                break;
            }
        }
        return requestCode;
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

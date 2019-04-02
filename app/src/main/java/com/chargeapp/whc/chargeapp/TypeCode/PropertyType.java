package com.chargeapp.whc.chargeapp.TypeCode;

public enum PropertyType {

    Positive(1,"Positive","資產"),
    Negative(2,"Negative","負債");


    PropertyType(Integer code,String name,String narrative)
    {
        this.name = name;
        this.code=code;
        this.narrative=narrative;
    }

    private  final String name;
    private  final Integer code;
    private final String narrative;

    public String getNarrative() {
        return narrative;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }
}

package com.chargeapp.whc.chargeapp.Model;

import com.chargeapp.whc.chargeapp.TypeCode.FixDateCode;

public class PropertyFromVO {

    private String id; //id
    private String sourceId; //來源Id
    private String sourceMoney; //來源金額
    private Boolean fixImport;  //是否定期
    private FixDateCode fixDateCode;
    private String fixDateDetail;
    private String propertyId;


    public String getSourceMoney() {
        return sourceMoney;
    }

    public void setSourceMoney(String sourceMoney) {
        this.sourceMoney = sourceMoney;
    }

    public String getFixDateDetail() {
        return fixDateDetail;
    }

    public void setFixDateDetail(String fixDateDetail) {
        this.fixDateDetail = fixDateDetail;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public Boolean isFixImport() {
        return fixImport;
    }

    public void setFixImport(Boolean fixImport) {
        this.fixImport = fixImport;
    }

    public FixDateCode getFixDateCode() {
        return fixDateCode;
    }

    public void setFixDateCode(FixDateCode fixDateCode) {
        this.fixDateCode = fixDateCode;
    }
}

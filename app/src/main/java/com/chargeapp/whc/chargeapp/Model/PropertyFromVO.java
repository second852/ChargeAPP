package com.chargeapp.whc.chargeapp.Model;

import com.chargeapp.whc.chargeapp.TypeCode.FixDateCode;
import com.chargeapp.whc.chargeapp.TypeCode.PropertyType;

import java.util.Date;

public class PropertyFromVO {

    private String id; //id
    private PropertyType type;//來源類別
    private String sourceMoney; //來源金額
    private String sourceCurrency;//來源幣別
    private String sourceMainType;//來原主類別
    private String sourceSecondType;//來源類別
    private Date sourceTime;//來源時間
    private String importFee;//轉入手續費
    private String importFeeId;//轉入手續費對應的ConsumeId
    private Boolean fixImport;  //是否定期
    private FixDateCode fixDateCode;
    private String fixDateDetail;
    private String propertyId;
    private String fixFromId;//定期Id



    public Date getSourceTime() {
        return sourceTime;
    }

    public void setSourceTime(Date sourceTime) {
        this.sourceTime = sourceTime;
    }

    public String getSourceMainType() {
        return sourceMainType;
    }

    public void setSourceMainType(String sourceMainType) {
        this.sourceMainType = sourceMainType;
    }

    public String getSourceSecondType() {
        return sourceSecondType;
    }

    public void setSourceSecondType(String sourceSecondType) {
        this.sourceSecondType = sourceSecondType;
    }



    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public String getImportFee() {
        return importFee;
    }

    public void setImportFee(String importFee) {
        this.importFee = importFee;
    }

    public String getSourceCurrency() {
        return sourceCurrency;
    }

    public void setSourceCurrency(String sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
    }

    public Boolean getFixImport() {
        return fixImport;
    }

    public void setFixImport(Boolean fixImport) {
        this.fixImport = fixImport;
    }

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




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FixDateCode getFixDateCode() {
        return fixDateCode;
    }

    public void setFixDateCode(FixDateCode fixDateCode) {
        this.fixDateCode = fixDateCode;
    }

    public String getImportFeeId() {
        return importFeeId;
    }

    public void setImportFeeId(String importFeeId) {
        this.importFeeId = importFeeId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getFixFromId() {
        return fixFromId;
    }

    public void setFixFromId(String fixFromId) {
        this.fixFromId = fixFromId;
    }
}

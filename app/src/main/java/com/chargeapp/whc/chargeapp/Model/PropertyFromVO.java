package com.chargeapp.whc.chargeapp.Model;

import com.chargeapp.whc.chargeapp.TypeCode.FixDateCode;

public class PropertyFromVO {

    private String id;
    private String sourceId;
    private boolean fixImport;
    private FixDateCode fixDateCode;
    private String propertyId;

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

    public boolean isFixImport() {
        return fixImport;
    }

    public void setFixImport(boolean fixImport) {
        this.fixImport = fixImport;
    }

    public FixDateCode getFixDateCode() {
        return fixDateCode;
    }

    public void setFixDateCode(FixDateCode fixDateCode) {
        this.fixDateCode = fixDateCode;
    }
}

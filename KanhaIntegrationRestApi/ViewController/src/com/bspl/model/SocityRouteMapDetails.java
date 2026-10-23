package com.bspl.model;

import java.util.Date;

public class SocityRouteMapDetails {
    private Long lineId;
    private String societyCentCd;
    private String routeCode;
    private String unitCd;
    private String fromDate;
    private String toDate;
    private String routeType;
    private int Object_version_number;
    
    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setSocietyCentCd(String societyCentCd) {
        this.societyCentCd = societyCentCd;
    }

    public String getSocietyCentCd() {
        return societyCentCd;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setUnitCd(String unitCd) {
        this.unitCd = unitCd;
    }

    public String getUnitCd() {
        return unitCd;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setObject_version_number(int Object_version_number) {
        this.Object_version_number = Object_version_number;
    }

    public int getObject_version_number() {
        return Object_version_number;
    }
    

   
}

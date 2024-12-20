package com.bspl.model;

public class UnitDetails {
    private String unitCode;
    private String cityCode;
    private String sbuCode;
    private String unitName;
    private String unitAddress;
    private String apiRefno;
    private String status;

    public void setApiRefno(String apiRefno) {
        this.apiRefno = apiRefno;
    }

    public String getApiRefno() {
        return apiRefno;
    }
   

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setSbuCode(String sbuCode) {
        this.sbuCode = sbuCode;
    }

    public String getSbuCode() {
        return sbuCode;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitAddress(String unitAddress) {
        this.unitAddress = unitAddress;
    }

    public String getUnitAddress() {
        return unitAddress;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

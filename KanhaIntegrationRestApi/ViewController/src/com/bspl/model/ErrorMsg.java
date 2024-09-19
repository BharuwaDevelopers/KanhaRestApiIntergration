package com.bspl.model;

import java.util.ArrayList;

public class ErrorMsg {
    private int statusCode;
    private boolean success;
    ArrayList<ChillingMstDetails> chillingdetails;
    ArrayList<SocietyMstDetails> societyDetails;
    ArrayList<RouteMasterDetails> routeMasterDetails;
    ArrayList<VendorMasterDetails> vendorMasterDetails;
    private String message;
    private String refDocNo;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
   
   
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
   

   

    public int getStatusCode() {
        return statusCode;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }


    public void setChillingdetails(ArrayList<ChillingMstDetails> chillingdetails) {
        this.chillingdetails = chillingdetails;
    }

    public ArrayList<ChillingMstDetails> getChillingdetails() {
        return chillingdetails;
    }

    public void setSocietyDetails(ArrayList<SocietyMstDetails> societyDetails) {
        this.societyDetails = societyDetails;
    }

    public ArrayList<SocietyMstDetails> getSocietyDetails() {
        return societyDetails;
    }


    public void setRouteMasterDetails(ArrayList<RouteMasterDetails> routeMasterDetails) {
        this.routeMasterDetails = routeMasterDetails;
    }

    public ArrayList<RouteMasterDetails> getRouteMasterDetails() {
        return routeMasterDetails;
    }

    public void setVendorMasterDetails(ArrayList<VendorMasterDetails> vendorMasterDetails) {
        this.vendorMasterDetails = vendorMasterDetails;
    }

    public ArrayList<VendorMasterDetails> getVendorMasterDetails() {
        return vendorMasterDetails;
    }

    public void setRefDocNo(String refDocNo) {
        this.refDocNo = refDocNo;
    }

    public String getRefDocNo() {
        return refDocNo;
    }
}

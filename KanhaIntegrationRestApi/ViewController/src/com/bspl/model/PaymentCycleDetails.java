package com.bspl.model;

public class PaymentCycleDetails {
    private String unit_cd,chilling_cent_cd,pay_cycle,pay_frm_dt,pay_to_dt,farm_migo,farm_inv_po,dsk_comm,drk_rec_po,api_refno;
     private int  object_version_number;

    public void setUnit_cd(String unit_cd) {
        this.unit_cd = unit_cd;
    }

    public String getUnit_cd() {
        return unit_cd;
    }

    public void setChilling_cent_cd(String chilling_cent_cd) {
        this.chilling_cent_cd = chilling_cent_cd;
    }

    public String getChilling_cent_cd() {
        return chilling_cent_cd;
    }

    public void setPay_cycle(String pay_cycle) {
        this.pay_cycle = pay_cycle;
    }

    public String getPay_cycle() {
        return pay_cycle;
    }

    public void setPay_frm_dt(String pay_frm_dt) {
        this.pay_frm_dt = pay_frm_dt;
    }

    public String getPay_frm_dt() {
        return pay_frm_dt;
    }

    public void setPay_to_dt(String pay_to_dt) {
        this.pay_to_dt = pay_to_dt;
    }

    public String getPay_to_dt() {
        return pay_to_dt;
    }

    public void setFarm_migo(String farm_migo) {
        this.farm_migo = farm_migo;
    }

    public String getFarm_migo() {
        return farm_migo;
    }

    public void setFarm_inv_po(String farm_inv_po) {
        this.farm_inv_po = farm_inv_po;
    }

    public String getFarm_inv_po() {
        return farm_inv_po;
    }

    public void setDsk_comm(String dsk_comm) {
        this.dsk_comm = dsk_comm;
    }

    public String getDsk_comm() {
        return dsk_comm;
    }

    public void setDrk_rec_po(String drk_rec_po) {
        this.drk_rec_po = drk_rec_po;
    }

    public String getDrk_rec_po() {
        return drk_rec_po;
    }

    public void setApi_refno(String api_refno) {
        this.api_refno = api_refno;
    }

    public String getApi_refno() {
        return api_refno;
    }

    public void setObject_version_number(int object_version_number) {
        this.object_version_number = object_version_number;
    }

    public int getObject_version_number() {
        return object_version_number;
    }
}

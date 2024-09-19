package com.bspl.model;

public class RouteMasterDetails {
   private String route_code,route_descp,distance,route_duration_frm,route_duration_to,
    chilling_cent_cd,status,mgr_distance,evng_distance;
    private int  object_version_number;

    public void setRoute_code(String route_code) {
        this.route_code = route_code;
    }

    public String getRoute_code() {
        return route_code;
    }

    public void setRoute_descp(String route_descp) {
        this.route_descp = route_descp;
    }

    public String getRoute_descp() {
        return route_descp;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }

    public void setRoute_duration_frm(String route_duration_frm) {
        this.route_duration_frm = route_duration_frm;
    }

    public String getRoute_duration_frm() {
        return route_duration_frm;
    }

    public void setRoute_duration_to(String route_duration_to) {
        this.route_duration_to = route_duration_to;
    }

    public String getRoute_duration_to() {
        return route_duration_to;
    }

    public void setChilling_cent_cd(String chilling_cent_cd) {
        this.chilling_cent_cd = chilling_cent_cd;
    }

    public String getChilling_cent_cd() {
        return chilling_cent_cd;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setMgr_distance(String mgr_distance) {
        this.mgr_distance = mgr_distance;
    }

    public String getMgr_distance() {
        return mgr_distance;
    }

    public void setEvng_distance(String evng_distance) {
        this.evng_distance = evng_distance;
    }

    public String getEvng_distance() {
        return evng_distance;
    }

    public void setObject_version_number(int object_version_number) {
        this.object_version_number = object_version_number;
    }

    public int getObject_version_number() {
        return object_version_number;
    }
}

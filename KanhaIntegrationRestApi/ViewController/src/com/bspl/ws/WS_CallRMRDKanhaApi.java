package com.bspl.ws;

import com.bspl.adapter.RestAdapter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("RMRD")
public class WS_CallRMRDKanhaApi {
    @GET
    public String getRMRDCollection() {
        RestAdapter restadapter=new RestAdapter();
        return restadapter.getkanhaRMRDCollection();
    }
}

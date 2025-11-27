package com.bspl.ws;

import com.bspl.adapter.RestAdapter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("RMRD")
public class WS_CallRMRDKanhaApi {
    @GET
    public String getRMRDCollection(@QueryParam("unitCode") String unitCode,@QueryParam("empCode") String empCode) {
        RestAdapter restadapter=new RestAdapter();
        return restadapter.getkanhaRMRDCollection(unitCode,empCode);
    }
}

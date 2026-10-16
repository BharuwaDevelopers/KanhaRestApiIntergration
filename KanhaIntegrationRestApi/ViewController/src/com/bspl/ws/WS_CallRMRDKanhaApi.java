package com.bspl.ws;

import com.bspl.adapter.RestAdapter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("RMRD")
public class WS_CallRMRDKanhaApi {
    @GET
    public String getRMRDCollection(@QueryParam("unitCode") String unitCode,@QueryParam("empCode") String empCode,@QueryParam("company_name")String company_name) {
        RestAdapter restadapter=new RestAdapter();
        return restadapter.getkanhaRMRDCollection(unitCode,empCode,company_name);
    }
}

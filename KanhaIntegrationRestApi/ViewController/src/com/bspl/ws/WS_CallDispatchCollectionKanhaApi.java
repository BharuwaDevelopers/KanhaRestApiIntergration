package com.bspl.ws;

import com.bspl.adapter.RestAdapter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("Dispatch")
public class WS_CallDispatchCollectionKanhaApi {
    @GET
    public String getFarmerCollection(@QueryParam("unitCode") String unitCode,@QueryParam("empCode") String empCode,@QueryParam("company_name")String company_name) {
        RestAdapter restadapter=new RestAdapter();
        return restadapter.getkanhaDispatchCollection(unitCode,empCode,company_name);
    }
}

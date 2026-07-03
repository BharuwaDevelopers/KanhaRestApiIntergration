package com.bspl.ws;

import com.bspl.adapter.RestAdapter;

import com.bspl.adapter.RestAdapterData;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("FarmerData")
public class WS_CallFarmerCollectionAegitekApi {
    @GET
    public String getFarmerCollectionAegitekApi(@QueryParam("unitCode") String unitCode,
                                                @QueryParam("empCode") String empCode) {
        RestAdapterData restadapter=new RestAdapterData();
        return restadapter.getAegitekFarmerCollection(unitCode,empCode);
    }
}

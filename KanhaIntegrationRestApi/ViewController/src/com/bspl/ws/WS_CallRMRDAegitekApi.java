package com.bspl.ws;
import com.bspl.adapter.RestAdapter;
import com.bspl.adapter.RestAdapterData;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("RMRD_DATA")
public class WS_CallRMRDAegitekApi {
    @GET
    public String getRMRDCollection(@QueryParam("unitCode") String unitCode, @QueryParam("empCode") String empCode) {
        RestAdapterData restadapter=new RestAdapterData();
        return restadapter.getAegiteckRMRDCollectionData(unitCode,empCode);
    }
}

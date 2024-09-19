package com.bspl.ws;

import com.bspl.adapter.RestAdapter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.JSONException;

@Path("TrnRmrd")
public class WS_RmrdTrnsaction {
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public String getRmrdDetails(String requestHeaderBody)  throws JSONException, Exception {
        RestAdapter restadapter=new RestAdapter();
        return restadapter.setRmrdTransactionDetails(requestHeaderBody);
    }
}

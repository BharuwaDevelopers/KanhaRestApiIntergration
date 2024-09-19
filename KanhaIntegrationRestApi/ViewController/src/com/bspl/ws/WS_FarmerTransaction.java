package com.bspl.ws;

import com.bspl.adapter.RestAdapter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.JSONException;

@Path("TrnFarmer")
public class WS_FarmerTransaction {
    @POST
    @Consumes("application/json")
   // @Produces("application/json")
    public String transaction(String requestFromBody) throws JSONException, Exception {
        RestAdapter restadapter=new RestAdapter();
        return restadapter.setFarmerTransactionDetails(requestFromBody);
    }
}

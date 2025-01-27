package com.bspl.ws;

import com.bspl.adapter.RestAdapter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("Dispatch")
public class WS_CallDispatchCollectionKanhaApi {
    @GET
    public String getFarmerCollection() {
        RestAdapter restadapter=new RestAdapter();
        return restadapter.getkanhaDispatchCollection();
    }
}

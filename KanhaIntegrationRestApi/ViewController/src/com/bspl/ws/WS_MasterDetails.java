package com.bspl.ws;
import com.bspl.adapter.RestAdapter;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.json.JSONException;
import javax.ws.rs.core.SecurityContext;
@Path("masterData")
public class WS_MasterDetails {
    @POST
    @Produces("application/json")
    public String getMasterDetails(String requestFromBody) throws JSONException, Exception {
        RestAdapter restadapter=new RestAdapter();
        return restadapter.getMasterDetails(requestFromBody);
    } 
}

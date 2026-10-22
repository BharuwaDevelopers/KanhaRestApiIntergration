    package com.bspl.ws;

import com.bspl.adapter.RestAdapter;
import com.bspl.adapter.RestAdapterData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import javax.ws.rs.QueryParam;

import org.json.JSONArray;
import org.json.JSONObject;

@Path("FarmerCollection")
public class WS_Transaction_FarmerCollection_Api {
    @GET
    public String getFarmerCollection(@QueryParam("unitCode") String unitCode,@QueryParam("empCode") String empCode,@QueryParam("company_name")String company_name) {
        RestAdapter restadapter=new RestAdapter();
        return restadapter.getkanhaFarmerCollection(unitCode,empCode,company_name);
    }


}

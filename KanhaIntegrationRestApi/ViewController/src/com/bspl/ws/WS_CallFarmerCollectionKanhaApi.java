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

import org.json.JSONArray;
import org.json.JSONObject;

@Path("FarmerCollection")
public class WS_CallFarmerCollectionKanhaApi {
    @GET
    public String getFarmerCollection() {
        RestAdapter restadapter=new RestAdapter();
        return restadapter.getkanhaFarmerCollection();
    }


}

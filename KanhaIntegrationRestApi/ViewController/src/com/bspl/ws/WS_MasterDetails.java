package com.bspl.ws;

import com.bspl.adapter.RestAdapter;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("masterData")
public class WS_MasterDetails {

    private static final String INVALID_KEY_RESPONSE = "{\"status\": \"error\", \"message\": \"api-key parameter is required\"}";
    private static final String EMPTY_BODY_RESPONSE = "{\"status\": \"error\", \"message\": \"Request body cannot be empty\"}";
    private static final String ERROR_RESPONSE = "{\"status\": \"error\", \"message\": \"Request parameters not complete\"}";

    private final RestAdapter restAdapter = new RestAdapter();

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Produces(MediaType.APPLICATION_JSON)
    public String getMasterDetails(@HeaderParam("api-key") String apiKey, String requestFromBody) {
        // 1. Validate API key header
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return INVALID_KEY_RESPONSE;
        }

        // 2. Early validation for request payload
        if (requestFromBody == null || requestFromBody.trim().isEmpty()) {
            return EMPTY_BODY_RESPONSE;
        }

        // 3. Process request
        try {
            return restAdapter.getMasterDetails(apiKey.trim(), requestFromBody);
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR_RESPONSE;
        }
    } 
}


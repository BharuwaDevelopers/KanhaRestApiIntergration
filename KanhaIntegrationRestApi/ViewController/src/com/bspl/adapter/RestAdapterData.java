package com.bspl.adapter;
import com.bspl.model.ErrorMsg;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.json.JSONArray;
import org.json.JSONObject;
public class RestAdapterData {
    public String getAegitekFarmerCollection(String unitCodeFromPage,String empCode) {
        Connection conn = null;
        Statement stmt = null;
        Statement stmt2 = null;
        Statement stmt3 = null;
        String userName=null;
        String password=null;
        String json = "";
        String insertDetailsQuery = "";
        ErrorMsg errorMsgObj = new ErrorMsg();
        Gson gson = new Gson();
            if (unitCodeFromPage == null || unitCodeFromPage.trim().isEmpty() ||
                empCode == null || empCode.trim().isEmpty()) {
                return "No parameter found";
            } else {
                try {
                    conn = getStartConnection();
                    stmt3 = conn.createStatement();
                    ResultSet rs2 = stmt3.executeQuery(
                        "SELECT * FROM API_Authorisation WHERE STATUS='Y' AND AUTH_TYPE='TRANSACTION'"
                    );

                    if (rs2.next()) {
                        userName = rs2.getString("API_USER_CODE").trim();
                        password = rs2.getString("API_USER_PASSWORD").trim();
                    }
                    else{
                       return "User and Password not found"; 
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        
    
        try {
            URL url = new URL("https://betaservices.milkmatrix.com/api/v1/Milk/list-FarmerCollection-custom");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            String jsonInputString ="{\"limit\": 0,\"offset\": 0,\"search\": \"\",\"filters\": {\"unitCode\": \""+unitCodeFromPage +"\",\"additionalProp2\": \"string\",\"additionalProp3\": \"string\"},\"sort\": {\"additionalProp1\": \"string\",\"additionalProp2\": \"string\",\"additionalProp3\": \"string\"}}";
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
                String jsonResponse = response.toString();
                JSONObject responseObject = new JSONObject(jsonResponse);
                int count = responseObject.getInt("count");
                // Check response status
              // int status = responseObject.getInt("responseStatus");
               // String message = responseObject.getString("responseMessage");
               String ApiRefno="NA";
               // String ApiRefno = responseObject.getString("batchcode");
               // System.out.println("Response Status: " + status);
              //  System.out.println("Response Message: " + message);
               // System.out.println("Batch Code: " + ApiRefno);
               // if (status == 200) {
                    try {
                        stmt = conn.createStatement();
                        stmt2 = conn.createStatement();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = new Date();
                        String uploadid = dateFormat.format(date).toString();
                        String unitCode = null;
                        String chillingCode = null;           
                        String formattedDate = null;
                        JSONArray responseData = responseObject.getJSONArray("results");
                        for (int i = 0; i < responseData.length(); i++) {
                            JSONObject jsonobjectDtl = responseData.getJSONObject(i);
                            String societyCode = null;
                            if (jsonobjectDtl.getString("societyCode").toString() == null ||
                                jsonobjectDtl.getString("societyCode").toString() == "") {
                            } else {
                                societyCode = jsonobjectDtl.getString("societyCode").toString();
                            }
                            System.out.println("societyCode: " + societyCode);
                            String eDate = null;
                            if (jsonobjectDtl.getString("eDate").toString() == null ||
                                jsonobjectDtl.getString("eDate").toString() == "") {
                            } else {
                                eDate = jsonobjectDtl.getString("eDate").toString();
                                String inputDate = eDate;
                                //                                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                //                                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yy");
                                //                                // Parse the input date
                                //                                LocalDate date1 = LocalDate.parse(inputDate, inputFormatter);
                                //                                // Format to the desired output
                                //                                formattedDate = date1.format(outputFormatter);
                                //                                System.out.println("formattedDate---" + formattedDate);

                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yy");
                                try {
                                    // Parse the input date string to a Date object
                                    Date date2 = inputFormat.parse(inputDate);
                                    // Format the Date object into the desired output format
                                    String outputDate = outputFormat.format(date2);
                                    // Print the formatted date
                                    System.out.println("Formatted date: " + outputDate);
                                    formattedDate = outputDate;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            System.out.println("eDate: " + eDate);
                            //System.out.println("eDate: " + jsonobjectDtl.getString("eDate").toString());
                            String time = null;
                            if (jsonobjectDtl.getString("time").toString() == null ||
                                jsonobjectDtl.getString("time").toString() == "") {
                            } else {
                                time = jsonobjectDtl.getString("time").toString();
                            }
                            System.out.println("time: " + time);
                            //  System.out.println("time: " + jsonobjectDtl.getString("time").toString());

                            String itemCode = null;
                            if (jsonobjectDtl.getString("itemCode").toString() == null ||
                                jsonobjectDtl.getString("itemCode").toString() == "") {
                            } else {
                                itemCode = jsonobjectDtl.getString("itemCode").toString();
                            }
                            System.out.println("itemcode: " + itemCode);
                            //System.out.println("itemCode: " + jsonobjectDtl.getString("itemCode").toString());
                            //System.out.println("milkType: " + jsonobjectDtl.getString("milkType").toString());
                            String milkType = null;
                            if (jsonobjectDtl.getString("milkType").toString() == null ||
                                jsonobjectDtl.getString("milkType").toString() == "") {
                            } else {
                                milkType = jsonobjectDtl.getString("milkType").toString();
                            }
                            System.out.println("milkType: " + milkType);
                            // System.out.println("localCode: " + jsonobjectDtl.getString("localCode".toString()));
                            String localCode = null;
                            if (jsonobjectDtl.getString("localCode").toString() == null ||
                                jsonobjectDtl.getString("localCode").toString() == "") {
                            } else {
                                localCode = jsonobjectDtl.getString("localCode").toString();
                            }
                            System.out.println("localCode: " + localCode);


                            String extendedCode = null;
                            if (jsonobjectDtl.getString("extendedCode").toString() == null ||
                                jsonobjectDtl.getString("extendedCode").toString() == "") {
                            } else {
                                extendedCode = jsonobjectDtl.getString("extendedCode").toString();
                            }
                            System.out.println("extendedCode: " + extendedCode);
                            //System.out.println("quantity: " + jsonobjectDtl.getString("quantity").toString());

                            String quantity = null;
                            if (jsonobjectDtl.getString("quantity").toString() == null ||
                                jsonobjectDtl.getString("quantity").toString() == "") {
                            } else {
                                quantity = jsonobjectDtl.getString("quantity").toString();
                            }
                            System.out.println("quantity: " + quantity);

                            //System.out.println("fat: " + jsonobjectDtl.getString("fat").toString());

                            String fat = null;
                            if (jsonobjectDtl.getString("fat").toString() == null ||
                                jsonobjectDtl.getString("fat").toString() == "") {
                            } else {
                                fat = jsonobjectDtl.getString("fat").toString();
                            }
                            System.out.println("fat: " + fat);
                            //  System.out.println("snf: " + jsonobjectDtl.getString("snf").toString());

                            String snf = null;
                            if (jsonobjectDtl.getString("snf").toString() == null ||
                                jsonobjectDtl.getString("snf").toString() == "") {
                            } else {
                                snf = jsonobjectDtl.getString("snf").toString();
                            }
                            System.out.println("snf: " + snf);
                            // System.out.println("amount: " + jsonobjectDtl.getString("amount").toString());
                            String amount = null;
                            if (jsonobjectDtl.getString("amount").toString() == null ||
                                jsonobjectDtl.getString("amount").toString() == "") {
                            } else {
                                amount = jsonobjectDtl.getString("amount").toString();
                            }
                            System.out.println("amount: " + amount);

                            //System.out.println("quantity_Mode: " + jsonobjectDtl.getString("quantity_Mode").toString());
                            String quantity_Mode = null;
                            if (jsonobjectDtl.getString("quantity_Mode").toString() == null ||
                                jsonobjectDtl.getString("quantity_Mode").toString() == "") {
                            } else {
                                quantity_Mode = jsonobjectDtl.getString("quantity_Mode").toString();
                            }
                            System.out.println("quantity_Mode: " + quantity_Mode);

                            //System.out.println("shift: " + jsonobjectDtl.getString("shift").toString());
                            String shift = null;
                            if (jsonobjectDtl.getString("shift").toString() == null ||
                                jsonobjectDtl.getString("shift").toString() == "") {
                            } else {
                                shift = jsonobjectDtl.getString("shift").toString();
                            }
                            System.out.println("shift: " + shift);

                            //  System.out.println("rate: " + jsonobjectDtl.getString("rate").toString());

                            String rate = null;
                            if (jsonobjectDtl.getString("rate").toString() == null ||
                                jsonobjectDtl.getString("rate").toString() == "") {
                            } else {
                                rate = jsonobjectDtl.getString("rate").toString();
                            }
                            System.out.println("rate: " + rate);

                            // System.out.println("primeryId: " + jsonobjectDtl.getString("primeryId").toString());

                            String primeryId = null;
                            if (jsonobjectDtl.getString("primeryId").toString() == null ||
                                jsonobjectDtl.getString("primeryId").toString() == "") {
                            } else {
                                primeryId = jsonobjectDtl.getString("primeryId").toString();
                            }
                            System.out.println("primeryId: " + primeryId);

                            //System.out.println("objectversionNo: " +jsonobjectDtl.getString("objectversionNo").toString());

                            String objectversionNo = "0";
                            if (jsonobjectDtl.getString("objectversionNo").toString() == null ||
                                jsonobjectDtl.getString("objectversionNo").toString() == "") {
                            } else {
                                objectversionNo = jsonobjectDtl.getString("objectversionNo").toString();
                            }
                            System.out.println("objectversionNo: " + objectversionNo);

                            if (jsonobjectDtl.getString("chillingCode").toString() == null ||
                                jsonobjectDtl.getString("chillingCode").toString() == "") {

                            } else {
                                chillingCode = jsonobjectDtl.getString("chillingCode").toString();
                            }
                            System.out.println("chillingCode: " + chillingCode);
                            //System.out.println("can: " + jsonobjectDtl.getString("can").toString());


                            String can = null;
                            if (jsonobjectDtl.getString("can").toString() == null ||
                                jsonobjectDtl.getString("can").toString() == "") {
                            } else {
                                can = jsonobjectDtl.getString("can").toString();
                            }
                            System.out.println("can: " + can);
                            //System.out.println("unitCode: " + jsonobjectDtl.getString("unitCode").toString());

                            if (jsonobjectDtl.getString("unitCode").toString() == null ||
                                jsonobjectDtl.getString("unitCode").toString() == "") {
                            } else {
                                unitCode = jsonobjectDtl.getString("unitCode").toString();
                            }
                            System.out.println("unitCode: " + unitCode);


                            //jsonobjectDtl.getString("measurement_Mode").toString()
                            String measurement_Mode = null;
                            if (jsonobjectDtl.getString("measurement_Mode").toString() == null ||
                                jsonobjectDtl.getString("measurement_Mode").toString() == "") {
                            } else {
                                measurement_Mode = jsonobjectDtl.getString("measurement_Mode").toString();
                            }
                            System.out.println("measurement_Mode: " + measurement_Mode);
                            
                            String route_Code = null;
                            if (jsonobjectDtl.getString("route_Code").toString() == null ||
                                jsonobjectDtl.getString("route_Code").toString() == "") {
                            } else {
                                route_Code = jsonobjectDtl.getString("route_Code").toString();
                            }
                            System.out.println("measurement_Mode: " + route_Code);



                            //  unitCode = jsonobjectDtl.getString("unitCode").toString();
                            try {
                                insertDetailsQuery =
                                    "insert into mm_farmer_data_upload_api(UNIT_CD,UPLOAD_ID,UPLOAD_LINE_ID,CP_CODE,MCC_CODE,PAY_CYCLE_ID," +
                                    "E_DATE,E_TIME,MILK_TYPE,LOCAL_CODE,EXTENDED_CODE,QUANTITY,FAT,SNF,AMOUNT," +
                                    "QUANTITY_MODE,MEASUREMENT_MODE,SHIFT,RATE,ITEM_CD," +
                                    "SYS_RATE_ID,SYS_RATE,REC_STATUS,CREATED_BY,CREATED_DATE,MODIFY_BY,MODIFY_DATE,API_REFNO,API_FLAG,NR,CAN,ROUTE_CODE)\n" +
                                    "VALUES('" + unitCode + "','" + uploadid + "',GLOBAL_OCI_SEQ.nextval,'" +
                                    societyCode + "','" + chillingCode + "',null,'" + formattedDate.toString() + "','" +
                                    time + "','" + milkType + "','" + localCode + "','" + extendedCode + "','" +
                                    quantity + "','" + fat + "','" + snf + "','" + amount + "','" + quantity_Mode +
                                    "','" + measurement_Mode + "','" + shift + "','" + rate + "','" + itemCode + "'," +
                                    "'0','0','E','"+empCode+"',SYSDATE,'"+empCode+"',SYSDATE,'" + ApiRefno + "','Y','" + primeryId +
                                    "','" + can + "','"+route_Code+"')";
                                System.out.println("insertDetailsQuery--" + insertDetailsQuery);
                                stmt2.addBatch(insertDetailsQuery);
                            } catch (Exception ex) {
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setMessage("Records not update! Please retry");
                                ex.printStackTrace();
                                json = gson.toJson(errorMsgObj);
                                return json;

                            }
                        }
                        
                        try {
                            //    int[] updateCounts = stmt.executeBatch();
                            int[] updateCounts1 = stmt2.executeBatch();
                            conn.commit();
                            if (updateCounts1.length > 0) {

                                CallableStatement cs = null;
                                String resultFlag = null;
                                String errorMessage = null;
                                BigDecimal id = new BigDecimal(uploadid);


                                try {
                                    // con = getConnection();
                                    //CallableStatement cs;
                                    cs = conn.prepareCall("{CALL PROC_INST_FRMER_TO_MAIN_TAB(?,?,?,?)}");
                                    cs.setObject(1, id);
                                    cs.setObject(2, empCode);
                                    cs.registerOutParameter(3, Types.VARCHAR);
                                    cs.registerOutParameter(4, Types.VARCHAR);
                                    // ResultSet rs2 = cs.executeQuery();
                                    cs.executeQuery();
                                    resultFlag = cs.getString(3);
                                    errorMessage = cs.getString(4);
                                    System.out.println("resultFlag===>"+resultFlag);
                                    System.out.println("errorMessage===>"+errorMessage);
                                  
                                    
                                    if(resultFlag.equalsIgnoreCase("S")){
                                        errorMsgObj.setStatusCode(200);
                                        errorMsgObj.setSuccess(true);
                                        errorMsgObj.setRefDocNo(ApiRefno);
                                        //errorMsgObj.setUploadId(uploadid);
                                        errorMsgObj.setMessage(" Records has been updated");
                                    }else{
                                        errorMsgObj.setStatusCode(500);
                                        errorMsgObj.setSuccess(false);
                                        errorMsgObj.setRefDocNo(" ");
                                      //  errorMsgObj.setUploadId("0");
                                        errorMsgObj.setMessage("Records not update! Please retry");
                                    }
                                   
                                    conn.close();

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    //return "Error: " + e.getMessage();
                                    errorMsgObj.setStatusCode(500);
                                    errorMsgObj.setSuccess(false);
                                    errorMsgObj.setRefDocNo(" ");
                                    //  errorMsgObj.setUploadId("0");
                                    errorMsgObj.setMessage("Records not update! Please retry");
                                } finally {
                                    conn.close();
                                    if (cs != null) {
                                        try {
                                            cs.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                

                              
                            } else {
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setRefDocNo(" ");
                                //errorMsgObj.setUploadId("0");
                                errorMsgObj.setMessage("Records not update! Please retry");
                            }
                        } catch (Exception ex) {
                            try {
                                conn.rollback();
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setMessage("Records not update! Please retry");
                                ex.printStackTrace();
                                //return "Failed to save data. Data safely rolled back";
                            } catch (Exception ex1) {
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setMessage("Records not update! Please retry");
                                ex.printStackTrace();
                                json = gson.toJson(errorMsgObj);
                                return json;
                            }
                        } finally {
                            try {
                                stmt.close();
                                stmt2.close();
                                conn.close();
                            } catch (Exception nex) {
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setMessage("Records not update! Please retry");
                                nex.printStackTrace();
                                json = gson.toJson(errorMsgObj);
                                return json;
                            }
                        }

                    } catch (Exception ex) {
                        errorMsgObj.setStatusCode(500);
                        errorMsgObj.setSuccess(false);
                        errorMsgObj.setMessage("Records not update! Please retry");
                        ex.printStackTrace();
                        json = gson.toJson(errorMsgObj);
                        return json;
                    }
//                } 
//                else {
//                    errorMsgObj.setStatusCode(500);
//                    errorMsgObj.setSuccess(false);
//                    errorMsgObj.setMessage("Data not fetch from kanha Api! Please retry");
//                    json = gson.toJson(errorMsgObj);
//                    return json;
//                }


            }
        } catch (Exception ex) {
            errorMsgObj.setStatusCode(500);
            errorMsgObj.setSuccess(false);
            errorMsgObj.setMessage("Records not update! Please retry");
            ex.printStackTrace();
            json = gson.toJson(errorMsgObj);
            return json;
        }
        try {
            json = gson.toJson(errorMsgObj);

        } catch (Exception ex) {
            errorMsgObj.setStatusCode(500);
            errorMsgObj.setSuccess(false);
            errorMsgObj.setMessage("Records not update! Please retry");
            ex.printStackTrace();
            json = gson.toJson(errorMsgObj);
            return json;
        }
        return json;
    }
    
    
    public String getkanhaRMRDCollection(String unitCodeFromPage,String empCode) {
        Connection conn = null;
        Statement stmt = null;
        Statement stmt2 = null;
        Statement stmt3 = null;
        String json = "";
        String insertDetailsQuery = "";
        String userName=null;
        String password =null;
        ErrorMsg errorMsgObj = new ErrorMsg();
        Gson gson = new Gson();
        if (unitCodeFromPage == null || unitCodeFromPage.trim().isEmpty() ||
            empCode == null || empCode.trim().isEmpty()) {
            return "No parameter found";
        } else {
            try {
                conn = getStartConnection();
                stmt3 = conn.createStatement();
                ResultSet rs2 = stmt3.executeQuery(
                    "SELECT * FROM API_Authorisation WHERE STATUS='Y' AND AUTH_TYPE='TRANSACTION'"
                );

                if (rs2.next()) {
                    userName = rs2.getString("API_USER_CODE").trim();
                    password = rs2.getString("API_USER_PASSWORD").trim();
                }
                else{
                   return "User and Password not found"; 
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            // URL url = new URL("http://182.18.144.204:50019/api/v1.0/UCDF/Sycncollectionucdf");
            URL url = new URL("http://140.245.15.174:50003/api/v1.0/UCDF/Sycncollectionucdf");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
    //            String jsonInputString =
    //                "{\"user\":\"Admin\",\"password\":\"Admin@123\",\"collectiontype\":\"RMRD\",\"refdoc_no\":\"0\"}";
                        String jsonInputString =
            "{\"user\":\""+userName+"\",\"password\":\""+password+"\",\"collectiontype\":\"RMRD\",\"refdoc_no\":\"0\",\"unitCode\":\"" + unitCodeFromPage + "\"}";
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
                String jsonResponse = response.toString();
                JSONObject responseObject = new JSONObject(jsonResponse);
                // Check response status
                int status = responseObject.getInt("responseStatus");
                String message = responseObject.getString("responseMessage");
                String ApiRefno = responseObject.getString("batchcode");
                System.out.println("Response Status: " + status);
                System.out.println("Response Message: " + message);
                System.out.println("Batch Code: " + ApiRefno);
                if (status == 200) {

                   // conn = getStartConnection();
                    try {

                        stmt = conn.createStatement();
                        stmt2 = conn.createStatement();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = new Date();
                        String uploadid = dateFormat.format(date).toString();
                        String unitCode = null;
                        String chillingCode = null;
                        String paymentcycleID = null;
                        String formattedDate = null;
                        JSONArray responseData = responseObject.getJSONArray("responseData");
                        for (int i = 0; i < responseData.length(); i++) {
                            JSONObject jsonobjectDtl = responseData.getJSONObject(i);
                            String societyCode = null;
                            if (jsonobjectDtl.getString("societyCode").toString() == null ||
                                jsonobjectDtl.getString("societyCode").toString() == "") {
                            } else {
                                societyCode = jsonobjectDtl.getString("societyCode").toString();
                            }
                            System.out.println("societyCode: " + societyCode);
                            String eDate = null;
                            if (jsonobjectDtl.getString("eDate").toString() == null ||
                                jsonobjectDtl.getString("eDate").toString() == "") {
                            } else {
                                eDate = jsonobjectDtl.getString("eDate").toString();
                                String inputDate = eDate;
                                //                                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                //                                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yy");
                                //                                // Parse the input date
                                //                                LocalDate date1 = LocalDate.parse(inputDate, inputFormatter);
                                //                                // Format to the desired output
                                //                                formattedDate = date1.format(outputFormatter);
                                //                                System.out.println("formattedDate---" + formattedDate);

                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yy");

                                try {
                                    // Parse the input date string to a Date object
                                    Date date2 = inputFormat.parse(inputDate);
                                    // Format the Date object into the desired output format
                                    String outputDate = outputFormat.format(date2);
                                    // Print the formatted date
                                    System.out.println("Formatted date: " + outputDate);
                                    formattedDate = outputDate;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            System.out.println("eDate: " + eDate);
                            //System.out.println("eDate: " + jsonobjectDtl.getString("eDate").toString());
                            String time = null;
                            if (jsonobjectDtl.getString("time").toString() == null ||
                                jsonobjectDtl.getString("time").toString() == "") {
                            } else {
                                time = jsonobjectDtl.getString("time").toString();
                            }
                            System.out.println("time: " + time);
                            //  System.out.println("time: " + jsonobjectDtl.getString("time").toString());

                            String itemCode = null;
                            if (jsonobjectDtl.getString("itemCode").toString() == null ||
                                jsonobjectDtl.getString("itemCode").toString() == "") {
                            } else {
                                itemCode = jsonobjectDtl.getString("itemCode").toString();
                            }
                            System.out.println("itemcode: " + itemCode);
                            //System.out.println("itemCode: " + jsonobjectDtl.getString("itemCode").toString());
                            //System.out.println("milkType: " + jsonobjectDtl.getString("milkType").toString());
                            String milkType = null;
                            if (jsonobjectDtl.getString("milkType").toString() == null ||
                                jsonobjectDtl.getString("milkType").toString() == "") {
                            } else {
                                milkType = jsonobjectDtl.getString("milkType").toString();
                            }
                            System.out.println("milkType: " + milkType);
                            // System.out.println("localCode: " + jsonobjectDtl.getString("localCode".toString()));
                            String localCode = null;
                            if (jsonobjectDtl.getString("localCode").toString() == null ||
                                jsonobjectDtl.getString("localCode").toString() == "") {
                            } else {
                                localCode = jsonobjectDtl.getString("localCode").toString();
                            }
                            System.out.println("localCode: " + localCode);


                            String extendedCode = null;
                            if (jsonobjectDtl.getString("extendedCode").toString() == null ||
                                jsonobjectDtl.getString("extendedCode").toString() == "") {
                            } else {
                                extendedCode = jsonobjectDtl.getString("extendedCode").toString();
                            }
                            System.out.println("extendedCode: " + extendedCode);
                            //System.out.println("quantity: " + jsonobjectDtl.getString("quantity").toString());

                            String quantity = null;
                            if (jsonobjectDtl.getString("quantity").toString() == null ||
                                jsonobjectDtl.getString("quantity").toString() == "") {
                            } else {
                                quantity = jsonobjectDtl.getString("quantity").toString();
                            }
                            System.out.println("quantity: " + quantity);

                            //System.out.println("fat: " + jsonobjectDtl.getString("fat").toString());

                            String fat = null;
                            if (jsonobjectDtl.getString("fat").toString() == null ||
                                jsonobjectDtl.getString("fat").toString() == "") {
                            } else {
                                fat = jsonobjectDtl.getString("fat").toString();
                            }
                            System.out.println("fat: " + fat);
                            //  System.out.println("snf: " + jsonobjectDtl.getString("snf").toString());

                            String snf = null;
                            if (jsonobjectDtl.getString("snf").toString() == null ||
                                jsonobjectDtl.getString("snf").toString() == "") {
                            } else {
                                snf = jsonobjectDtl.getString("snf").toString();
                            }
                            System.out.println("snf: " + snf);
                            // System.out.println("amount: " + jsonobjectDtl.getString("amount").toString());
                            String amount = null;
                            if (jsonobjectDtl.getString("amount").toString() == null ||
                                jsonobjectDtl.getString("amount").toString() == "") {
                            } else {
                                amount = jsonobjectDtl.getString("amount").toString();
                            }
                            System.out.println("amount: " + amount);

                            //System.out.println("quantity_Mode: " + jsonobjectDtl.getString("quantity_Mode").toString());
                            String quantity_Mode = null;
                            if (jsonobjectDtl.getString("quantity_Mode").toString() == null ||
                                jsonobjectDtl.getString("quantity_Mode").toString() == "") {
                            } else {
                                quantity_Mode = jsonobjectDtl.getString("quantity_Mode").toString();
                            }
                            System.out.println("quantity_Mode: " + quantity_Mode);

                            //System.out.println("shift: " + jsonobjectDtl.getString("shift").toString());
                            String shift = null;
                            if (jsonobjectDtl.getString("shift").toString() == null ||
                                jsonobjectDtl.getString("shift").toString() == "") {
                            } else {
                                shift = jsonobjectDtl.getString("shift").toString();
                            }
                            System.out.println("shift: " + shift);

                            //  System.out.println("rate: " + jsonobjectDtl.getString("rate").toString());

                            String rate = null;
                            if (jsonobjectDtl.getString("rate").toString() == null ||
                                jsonobjectDtl.getString("rate").toString() == "") {
                            } else {
                                rate = jsonobjectDtl.getString("rate").toString();
                            }
                            System.out.println("rate: " + rate);

                            // System.out.println("primeryId: " + jsonobjectDtl.getString("primeryId").toString());

                            String primeryId = null;
                            if (jsonobjectDtl.getString("primeryId").toString() == null ||
                                jsonobjectDtl.getString("primeryId").toString() == "") {
                            } else {
                                primeryId = jsonobjectDtl.getString("primeryId").toString();
                            }
                            System.out.println("primeryId: " + primeryId);

                            //System.out.println("objectversionNo: " +jsonobjectDtl.getString("objectversionNo").toString());

                            String objectversionNo = "0";
                            if (jsonobjectDtl.getString("objectversionNo").toString() == null ||
                                jsonobjectDtl.getString("objectversionNo").toString() == "") {
                            } else {
                                objectversionNo = jsonobjectDtl.getString("objectversionNo").toString();
                            }
                            System.out.println("objectversionNo: " + objectversionNo);

                            if (jsonobjectDtl.getString("chillingCode").toString() == null ||
                                jsonobjectDtl.getString("chillingCode").toString() == "") {

                            } else {
                                chillingCode = jsonobjectDtl.getString("chillingCode").toString();
                            }
                            System.out.println("chillingCode: " + chillingCode);
                            //System.out.println("can: " + jsonobjectDtl.getString("can").toString());

    //
    //                            String can = null;
    //                            if (jsonobjectDtl.getString("can").toString() == null ||
    //                                jsonobjectDtl.getString("can").toString() == "") {
    //                            } else {
    //                                can = jsonobjectDtl.getString("can").toString();
    //                            }
    //                            System.out.println("can: " + can);
                            //System.out.println("unitCode: " + jsonobjectDtl.getString("unitCode").toString());

                            if (jsonobjectDtl.getString("unitCode").toString() == null ||
                                jsonobjectDtl.getString("unitCode").toString() == "") {
                            } else {
                                unitCode = jsonobjectDtl.getString("unitCode").toString();
                            }
                            System.out.println("unitCode: " + unitCode);


                            //jsonobjectDtl.getString("measurement_Mode").toString()
                            String measurement_Mode = null;
                            if (jsonobjectDtl.getString("measurement_Mode").toString() == null ||
                                jsonobjectDtl.getString("measurement_Mode").toString() == "") {
                            } else {
                                measurement_Mode = jsonobjectDtl.getString("measurement_Mode").toString();
                            }
                            System.out.println("measurement_Mode: " + measurement_Mode);
                            
                            String route_Code = null;
                            if (jsonobjectDtl.getString("route_Code").toString() == null ||
                                jsonobjectDtl.getString("route_Code").toString() == "") {
                            } else {
                                route_Code = jsonobjectDtl.getString("route_Code").toString();
                            }
                            System.out.println("route_Code: " + route_Code);
                            
                            String rcans = null;
                            if (jsonobjectDtl.getString("rcans").toString() == null ||
                                jsonobjectDtl.getString("rcans").toString() == "") {
                            } else {
                                rcans = jsonobjectDtl.getString("rcans").toString();
                            }
                            System.out.println("rcans: " + rcans);
                            
                            String acans = null;
                            if (jsonobjectDtl.getString("acans").toString() == null ||
                                jsonobjectDtl.getString("acans").toString() == "") {
                            } else {
                                acans = jsonobjectDtl.getString("acans").toString();
                            }
                            System.out.println("acans: " + acans);
                            
                            String sampleid = null;
                            if (jsonobjectDtl.getString("sampleid").toString() == null ||
                                jsonobjectDtl.getString("sampleid").toString() == "") {
                            } else {
                                sampleid = jsonobjectDtl.getString("sampleid").toString();
                            }
                            System.out.println("sampleid: " + sampleid);
                          
                            BigDecimal rejecT_QTY = BigDecimal.ZERO;

                            String rejectQtyStr = jsonobjectDtl.optString("rejecT_QTY", "").trim();

                            if (!rejectQtyStr.isEmpty()) {
                                try {
                                    rejecT_QTY = new BigDecimal(rejectQtyStr);
                                } catch (NumberFormatException e) {
                                    // Handle invalid number format gracefully
                                    rejecT_QTY = BigDecimal.ZERO;
                                    // Optionally log the error or rethrow
                                }
                            }
                            String adulteration_STATUS=null;
                            if (jsonobjectDtl.getString("adulteration_STATUS").toString() == null ||
                                jsonobjectDtl.getString("adulteration_STATUS").toString() == "") {
                            } else {
                                adulteration_STATUS = jsonobjectDtl.getString("adulteration_STATUS").toString();
                            }
                            String remark=null;
                            if (jsonobjectDtl.getString("remark").toString() == null ||
                                jsonobjectDtl.getString("remark").toString() == "") {
                            } else {
                                remark = jsonobjectDtl.getString("remark").toString();
                            }



                            //  unitCode = jsonobjectDtl.getString("unitCode").toString();
                            try {
    //                                insertDetailsQuery =
    //                                    "insert into mm_farmer_data_upload_api(UNIT_CD,UPLOAD_ID,UPLOAD_LINE_ID,CP_CODE,MCC_CODE,PAY_CYCLE_ID," +
    //                                    "E_DATE,E_TIME,MILK_TYPE,LOCAL_CODE,EXTENDED_CODE,QUANTITY,FAT,SNF,AMOUNT," +
    //                                    "QUANTITY_MODE,MEASUREMENT_MODE,SHIFT,RATE,ITEM_CD," +
    //                                    "SYS_RATE_ID,SYS_RATE,REC_STATUS,CREATED_BY,CREATED_DATE,MODIFY_BY,MODIFY_DATE,API_REFNO,API_FLAG,NR,CAN,ROUTE_CODE)\n" +
    //                                    "VALUES('" + unitCode + "','" + uploadid + "',GLOBAL_OCI_SEQ.nextval,'" +
    //                                    societyCode + "','" + chillingCode + "',null,'" + formattedDate.toString() + "','" +
    //                                    time + "','" + milkType + "','" + localCode + "','" + extendedCode + "','" +
    //                                    quantity + "','" + fat + "','" + snf + "','" + amount + "','" + quantity_Mode +
    //                                    "','" + measurement_Mode + "','" + shift + "','" + rate + "','" + itemCode + "'," +
    //                                    "'0','0','E','Admin',SYSDATE,'Admin',SYSDATE,'" + ApiRefno + "','Y','" + primeryId +
    //                                    "','" + can + "','"+route_Code+"')";
    //                                System.out.println("insertDetailsQuery--" + insertDetailsQuery);
                                
                                insertDetailsQuery =
                                "insert into mm_rmrd_data_upload_api (UNIT_CD,UPLOAD_ID,UPLOAD_LINE_ID,CP_CODE,CP_VENDOR,E_DATE,E_TIME,MILK_TYPE,LOCAL_CODE,EXTENDED_CODE,QUANTITY,FAT,SNF,AMOUNT,QUANTITY_MODE,MEASUREMENT_MODE,SHIFT,RATE,ITEM_CD,SYS_RATE_ID,SYS_RATE,REC_STATUS,CREATED_BY,CREATED_DATE,MODIFY_BY,MODIFY_DATE,API_REFNO,API_FLAG,NR,ROUTE_CODE,recive_can,accept_can,sample_id,reject_can,REJECT_QTY,ADULTERATION_STATUS,REMARK)\n" +
                                "VALUES('" + unitCode + "','" + uploadid + "',GLOBAL_OCI_SEQ.nextval,'" + chillingCode + "','" +
                                chillingCode + "','" +formattedDate.toString() + "','" + time + "','" +
                                milkType + "','" + localCode +"','" + extendedCode + "','" + quantity + "','" + fat + "','" +
                                snf + "','" + amount + "','" + quantity_Mode + "','" +measurement_Mode + "','" + shift + "','" + rate + "','" + itemCode + "'," + "'0','0','E','"+empCode+"',SYSDATE,'Admin',SYSDATE,'"+ApiRefno+"','Y','"+primeryId+"','"+route_Code+"','"+rcans+"','"+acans+"','"+sampleid+"',null,"+rejecT_QTY+",'"+adulteration_STATUS+"','"+remark+"')";
                                System.out.println("insertDetailsQuery--" + insertDetailsQuery);
                                
                                stmt2.addBatch(insertDetailsQuery);
                            } catch (Exception ex) {
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setMessage("Records not update! Please retry");
                                ex.printStackTrace();
                                json = gson.toJson(errorMsgObj);
                                return json;

                            }
                        }
                        //                        try {
                       
                        try {
                            //    int[] updateCounts = stmt.executeBatch();
                            int[] updateCounts1 = stmt2.executeBatch();
                            conn.commit();
                            if (updateCounts1.length > 0) {

                                CallableStatement cs = null;
                                String resultFlag = null;
                                String errorMessage = null;
                                BigDecimal id = new BigDecimal(uploadid);


                                try {
                                    // con = getConnection();
                                    //CallableStatement cs;
                                    cs = conn.prepareCall("{CALL PROC_INST_RMRD_TO_MAIN_TAB(?,?,?,?)}");
                                    cs.setObject(1, id);
                                    cs.setObject(2, empCode);
                                    cs.registerOutParameter(3, Types.VARCHAR);
                                    cs.registerOutParameter(4, Types.VARCHAR);
                                    // ResultSet rs2 = cs.executeQuery();
                                    cs.executeQuery();
                                    resultFlag = cs.getString(3);
                                    errorMessage = cs.getString(4);
                                    System.out.println("resultFlag===>"+resultFlag);
                                    System.out.println("errorMessage===>"+errorMessage);
                                  
                                    
                                    if(resultFlag.equalsIgnoreCase("S")){
                                        errorMsgObj.setStatusCode(200);
                                        errorMsgObj.setSuccess(true);
                                        errorMsgObj.setRefDocNo(ApiRefno);
                                        //errorMsgObj.setUploadId(uploadid);
                                        errorMsgObj.setMessage(" Records has been updated");
                                    }else{
                                        errorMsgObj.setStatusCode(500);
                                        errorMsgObj.setSuccess(false);
                                        errorMsgObj.setRefDocNo(" ");
                                      //  errorMsgObj.setUploadId("0");
                                        errorMsgObj.setMessage("Records not update! Please retry");
                                    }
                                   
                                    conn.close();

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    //return "Error: " + e.getMessage();
                                    errorMsgObj.setStatusCode(500);
                                    errorMsgObj.setSuccess(false);
                                    errorMsgObj.setRefDocNo(" ");
                                    //  errorMsgObj.setUploadId("0");
                                    errorMsgObj.setMessage("Records not update! Please retry");
                                } finally {
                                    conn.close();
                                    if (cs != null) {
                                        try {
                                            cs.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                

                              
                            } else {
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setRefDocNo(" ");
                                //errorMsgObj.setUploadId("0");
                                errorMsgObj.setMessage("Records not update! Please retry");
                            }
                        } catch (Exception ex) {
                            try {
                                conn.rollback();
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setMessage("Records not update! Please retry");
                                ex.printStackTrace();
                            } catch (Exception ex1) {
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setMessage("Records not update! Please retry");
                                ex.printStackTrace();
                                json = gson.toJson(errorMsgObj);
                                return json;
                            }
                        } finally {
                            try {
                                stmt.close();
                                stmt2.close();
                                conn.close();
                            } catch (Exception nex) {
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setMessage("Records not update! Please retry");
                                nex.printStackTrace();
                                json = gson.toJson(errorMsgObj);
                                return json;
                            }
                        }

                    } catch (Exception ex) {
                        errorMsgObj.setStatusCode(500);
                        errorMsgObj.setSuccess(false);
                        errorMsgObj.setMessage("Records not update! Please retry");
                        ex.printStackTrace();
                        json = gson.toJson(errorMsgObj);
                        return json;
                    }
                } else {
                    errorMsgObj.setStatusCode(500);
                    errorMsgObj.setSuccess(false);
                    errorMsgObj.setMessage("Data not fetch from kanha Api! Please retry");
                    json = gson.toJson(errorMsgObj);
                    return json;
                }


            }
        } catch (Exception ex) {
            errorMsgObj.setStatusCode(500);
            errorMsgObj.setSuccess(false);
            errorMsgObj.setMessage("Records not update! Please retry");
            ex.printStackTrace();
            json = gson.toJson(errorMsgObj);
            return json;
        }
        try {
            json = gson.toJson(errorMsgObj);

        } catch (Exception ex) {
            errorMsgObj.setStatusCode(500);
            errorMsgObj.setSuccess(false);
            errorMsgObj.setMessage("Records not update! Please retry");
            ex.printStackTrace();
            json = gson.toJson(errorMsgObj);
            return json;
        }
        return json;
    }
    
    public Connection getStartConnection() throws Exception {
        InitialContext initialContext = new InitialContext();
        DataSource ds = (DataSource) initialContext.lookup("java:comp/env/jdbc/APPLICATIONDBDS");
        java.sql.Connection conn = ds.getConnection();
        return conn;
    }
}

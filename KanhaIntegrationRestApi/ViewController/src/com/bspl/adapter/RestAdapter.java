package com.bspl.adapter;
import com.bspl.model.ErrorMsg;
import com.bspl.model.ChillingMstDetails;
import com.bspl.model.RouteMasterDetails;
import com.bspl.model.SocietyMstDetails;
import com.bspl.model.VendorMasterDetails;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class RestAdapter {
    public String getMasterDetails(String mstDetails) throws JSONException, Exception {
        ErrorMsg errorMsgObj = new ErrorMsg();
        Connection conn = null;
        Statement stmt = null;
        Statement stmt2 = null;
        String json = "";
        String updateQuery = "";
        String password = null;
        String user_name = null;
        try {
            if (mstDetails == null || mstDetails == "") {
                mstDetails = "Invalid Json";
            } else {
                JSONObject mJson = new JSONObject(mstDetails);
                conn = getStartConnection();
                try {
                    stmt2 = conn.createStatement();
                    ResultSet rs2 =
                        stmt2.executeQuery("select * from API_Authorisation where API_USER_CODE='" +
                                           mJson.getString("user") + "' and api_user_password= '" +
                                           mJson.getString("password") + "' and STATUS='Y'");
                    if (rs2.next()) {
                        stmt = conn.createStatement();
                        errorMsgObj.setStatusCode(200);
                        errorMsgObj.setSuccess(true);
                        errorMsgObj.setMessage("succes");
                        int i = 0;
                        int charSize = 15;
                        StringBuilder sb = new StringBuilder(charSize);
                        String AlphaNumericString =
                            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz" + "@$#&*";
                        for (int j = 0; j < charSize; j++) {
                            int index = (int) (AlphaNumericString.length() * Math.random());
                            sb.append(AlphaNumericString.charAt(index));
                        }
                        if (mJson.getString("mastertype").equalsIgnoreCase("Society") &&
                            mJson.getString("refdoc_no").equalsIgnoreCase("0")) {
                            SocietyMstDetails result = null;
                            ArrayList<SocietyMstDetails> SocietyMstDetailsList = new ArrayList<SocietyMstDetails>();
                            ResultSet rs =
                                stmt.executeQuery("SELECT A.unit_cd,A.society_cent_cd,b.society_cent_desp,D.chilling_cent_cd,E.chilling_cent_desp,b.address,b.city_cd,b.district_cd,\n" +
                                                  "b.phone_no,b.email,b.gst_no,b.pan_no,b.contact_person,b.fssi_license_no,b.old_society_cent_cd,F.status,\n" +
                                                  "A.route_code,C.route_descp,A.from_date,A.to_date,b.object_version_number\n" +
                                                  "FROM society_rute_map A,society_center_mst b,society_center_dtl F,route_master C,society_chill_vend_map D,chilling_master E\n" +
                                                  "WHERE A.society_cent_cd=b.society_cent_cd\n" +
                                                  "AND A.route_code=C.route_code\n" +
                                                  "AND A.society_cent_cd=D.society_cent_cd\n" +
                                                  "AND D.chilling_cent_cd=E.chilling_cent_cd\n" +
                                                  "AND b.line_id=F.line_id\n" + "AND F.status='Y'\n" +
                                                  "AND b.api_flag='N'");
                            while (rs.next()) {
                                i++;
                                result = new SocietyMstDetails();
                                result.setUnit_cd(rs.getString("unit_cd"));
                                result.setSociety_cent_cd(rs.getString("society_cent_cd"));
                                result.setSociety_cent_desp(rs.getString("society_cent_desp"));
                                result.setAddress(rs.getString("address"));
                                result.setCity_cd(rs.getString("city_cd"));
                                result.setDistrict_cd(rs.getString("district_cd"));
                                result.setPhone_no(rs.getString("phone_no"));
                                result.setEmail(rs.getString("email"));
                                result.setGst_no(rs.getString("gst_no"));
                                result.setPan_no(rs.getString("pan_no"));
                                result.setContact_person(rs.getString("contact_person"));
                                result.setFssi_license_no(rs.getString("fssi_license_no"));
                                result.setOld_society_cent_cd(rs.getString("old_society_cent_cd"));
                                result.setStatus(rs.getString("status"));
                                result.setRoute_code(rs.getString("route_code"));
                                result.setRoute_descp(rs.getString("route_descp"));
                                result.setFrom_date(rs.getString("from_date"));
                                result.setTo_date(rs.getString("to_date"));
                                result.setChilling_cent_cd(rs.getString("chilling_cent_cd"));
                                result.setChilling_cent_desp(rs.getString("chilling_cent_desp"));
                                result.setObject_version_number(rs.getInt("object_version_number"));
                                SocietyMstDetailsList.add(result);
                                updateQuery =
                                    "update society_center_mst set API_REFNo='" + sb.toString() +
                                    "' where SOCIETY_CENT_CD='" + rs.getString("society_cent_cd").toString() + "'";
                                stmt.addBatch(updateQuery);
                            }
                            //  System.out.println("updateQuery--" + updateQuery);
                            try {
                                int[] updateCounts = stmt.executeBatch();
                                //System.out.println("updateCounts--" + updateCounts.length);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                WriteToFile(ex.toString());
                            } finally {
                                try {
                                    stmt.close();
                                } catch (Exception ex) {
                                    WriteToFile(ex.toString());
                                }
                            }
                            errorMsgObj.setMessage("Successfully! All records has been fetched.");
                            errorMsgObj.setRefDocNo(sb.toString());
                            errorMsgObj.setSocietyDetails(SocietyMstDetailsList);
                        }

                        else if (mJson.getString("mastertype").equalsIgnoreCase("chilling") &&
                                 mJson.getString("refdoc_no").equalsIgnoreCase("0")) {
                            ChillingMstDetails result = null;
                            ArrayList<ChillingMstDetails> chillingMstDetailsList = new ArrayList<ChillingMstDetails>();
                            ResultSet rs =
                                stmt.executeQuery("select\n" +
                                                  "a.unit_cd,a.chilling_cent_cd,a.chilling_cent_desp,a.address,a.city_cd,a.district_cd,\n" +
                                                  "a.phone_no,a.email,a.gst_no,a.pan_no,a.contact_person,a.fssi_license_no,\n" +
                                                  "a.old_chilling_cent_cd,a.own_comp_flg,b.status,a.object_version_number\n" +
                                                  "from chilling_master a, chilling_detail b\n" +
                                                  "where a.line_id=b.line_id\n" + "and b.status='Y'");
                            while (rs.next()) {
                                i++;
                                result = new ChillingMstDetails();
                                result.setUnit_cd(rs.getString("unit_cd"));
                                result.setChilling_cent_cd(rs.getString("chilling_cent_cd"));
                                result.setChilling_cent_desp(rs.getString("chilling_cent_desp"));
                                result.setAddress(rs.getString("address"));
                                result.setCity_cd(rs.getString("city_cd"));
                                result.setDistrict_cd(rs.getString("district_cd"));
                                result.setPhone_no(rs.getString("phone_no"));
                                result.setEmail(rs.getString("email"));
                                result.setGst_no(rs.getString("gst_no"));
                                result.setPan_no(rs.getString("pan_no"));
                                result.setContact_person(rs.getString("contact_person"));
                                result.setFssi_license_no(rs.getString("fssi_license_no"));
                                result.setOld_chilling_cent_cd(rs.getString("old_chilling_cent_cd"));
                                result.setOwn_comp_flg(rs.getString("own_comp_flg"));
                                result.setStatus(rs.getString("status"));
                                result.setObject_version_number(rs.getInt("object_version_number"));
                                chillingMstDetailsList.add(result);
                                updateQuery =
                                    "update chilling_master set API_REFNo='" + sb.toString() +
                                    "' where CHILLING_CENT_CD='" + rs.getString("chilling_cent_cd").toString() + "'";
                                stmt.addBatch(updateQuery);
                            }
                            // System.out.println("updateQuery--" + updateQuery);
                            try {
                                int[] updateCounts = stmt.executeBatch();
                                //System.out.println("updateCounts--" + updateCounts.length);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                WriteToFile(ex.toString());
                            } finally {
                                try {
                                    stmt.close();
                                } catch (Exception ex) {
                                    WriteToFile(ex.toString());
                                }
                            }
                            errorMsgObj.setMessage("Successfully! All records has been fetched.");
                            errorMsgObj.setRefDocNo(sb.toString());
                            errorMsgObj.setChillingdetails(chillingMstDetailsList);


                        } else if (mJson.getString("mastertype").equalsIgnoreCase("Route") &&
                                   mJson.getString("refdoc_no").equalsIgnoreCase("0")) {
                            RouteMasterDetails result = null;
                            ArrayList<RouteMasterDetails> routeMasterDetailsList = new ArrayList<RouteMasterDetails>();
                            ResultSet rs =
                                stmt.executeQuery("SELECT route_code,route_descp,distance,route_duration_frm,route_duration_to,\n" +
                                                  "chilling_cent_cd,status,object_version_number,mgr_distance,evng_distance\n" +
                                                  "FROM route_master");
                            while (rs.next()) {
                                i++;
                                result = new RouteMasterDetails();
                                result.setRoute_code(rs.getString("route_code"));
                                result.setRoute_descp(rs.getString("route_descp"));
                                result.setRoute_duration_frm(rs.getString("route_duration_frm"));
                                result.setRoute_duration_to(rs.getString("route_duration_to"));
                                result.setChilling_cent_cd(rs.getString("chilling_cent_cd"));
                                result.setStatus(rs.getString("status"));
                                result.setObject_version_number(rs.getInt("object_version_number"));
                                result.setMgr_distance(rs.getString("mgr_distance"));
                                result.setEvng_distance(rs.getString("evng_distance"));
                                routeMasterDetailsList.add(result);
                                updateQuery =
                                    "update route_master set API_REFNo='" + sb.toString() + "' where ROUTE_CODE='" +
                                    rs.getString("route_code").toString() + "'";
                                stmt.addBatch(updateQuery);
                            }
                            //System.out.println("updateQuery--" + updateQuery);
                            try {
                                int[] updateCounts = stmt.executeBatch();
                                //System.out.println("updateCounts--" + updateCounts.length);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                WriteToFile(ex.toString());
                            } finally {
                                try {
                                    stmt.close();
                                } catch (Exception ex) {
                                    WriteToFile(ex.toString());
                                }
                            }
                            errorMsgObj.setMessage("Successfully! All records has been fetched.");
                            errorMsgObj.setRefDocNo(sb.toString());
                            errorMsgObj.setRouteMasterDetails(routeMasterDetailsList);
                        }

                        else if (mJson.getString("mastertype").equalsIgnoreCase("Vendor") &&
                                  mJson.getString("refdoc_no").equalsIgnoreCase("0")) {
                            VendorMasterDetails result = null;
                            ArrayList<VendorMasterDetails> vendorMasterDetailsList =
                                new ArrayList<VendorMasterDetails>();
                            ResultSet rs =
                                stmt.executeQuery("SELECT A.vendor_id,A.vendor_code,A.NAME as vendor_name,A.registeration_date,A.pan_no,A.gst_reg_no,A.aadhar_card,A.father_husband_name,A.farmer_local_code,A.cast_category,\n" +
                                                  "A.sex_gender,A.ven_type_code,A.ven_type ,C.contact_person,C.address1,C.city,C.STATE,C.city_code,b.bank_name,b.bank_ac_no,b.bank_ifcs_code,A.vendor_status,\n" +
                                                  "D.society_cent_cd,E.route_code,F.chilling_cent_cd,A.object_version_number\n" +
                                                  "FROM vendor_master A,vendor_bank_detail b,vendor_regd_address C ,\n" +
                                                  "society_farmer_ven_map D,society_rute_map E,society_chill_vend_map F\n" +
                                                  "WHERE   ven_type='F' AND A.vendor_code=b.ven_cd AND A.vendor_code=C.vendor_code\n" +
                                                  "AND A.vendor_code=D.farmer_ven_cd AND D.society_cent_cd=E.society_cent_cd\n" +
                                                  "AND D.society_cent_cd=F.society_cent_cd\n" + "AND A.api_flag='N'");
                            while (rs.next()) {
                                i++;
                                result = new VendorMasterDetails();
                                result.setVendor_code(rs.getString("vendor_code"));
                                result.setVendor_name(rs.getString("vendor_name"));
                                result.setRegisteration_date(rs.getString("registeration_date"));
                                result.setPan_no(rs.getString("pan_no"));
                                result.setGst_reg_no(rs.getString("gst_reg_no"));
                                result.setAadhar_card(rs.getString("aadhar_card"));
                                result.setFather_husband_name(rs.getString("father_husband_name"));
                                result.setFarmer_local_code(rs.getString("farmer_local_code"));
                                result.setCast_category(rs.getString("cast_category"));
                                result.setSex_gender(rs.getString("sex_gender"));
                                result.setVen_type_code(rs.getString("ven_type_code"));
                                result.setVen_type(rs.getString("ven_type"));
                                result.setContact_person(rs.getString("contact_person"));
                                result.setAddress1(rs.getString("address1"));
                                result.setCity(rs.getString("city"));
                                result.setSTATE(rs.getString("STATE"));
                                result.setCity_code(rs.getString("city_code"));
                                result.setBank_name(rs.getString("bank_name"));
                                result.setBank_ac_no(rs.getString("bank_ac_no"));
                                result.setBank_ifcs_code(rs.getString("bank_ifcs_code"));
                                result.setVendor_status(rs.getString("vendor_status"));
                                result.setSociety_cent_cd(rs.getString("society_cent_cd"));
                                result.setRoute_code(rs.getString("route_code"));
                                result.setChilling_cent_cd(rs.getString("chilling_cent_cd"));
                                result.setObject_version_number(rs.getInt("object_version_number"));
                                vendorMasterDetailsList.add(result);
                                updateQuery =
                                    "update vendor_master set API_REFNO='" + sb.toString() + "' where vendor_id='" +
                                    rs.getString("vendor_id").toString() + "'";
                                stmt.addBatch(updateQuery);
                            }
                            // System.out.println("updateQuery--" + updateQuery);
                            try {
                                int[] updateCounts = stmt.executeBatch();
                                //System.out.println("updateCounts--" + updateCounts.length);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                WriteToFile(ex.toString());
                            } finally {
                                try {
                                    stmt.close();
                                } catch (Exception nex) {
                                    //return "N";
                                }
                            }
                            errorMsgObj.setMessage("Successfully! All records has been fetched.");
                            errorMsgObj.setRefDocNo(sb.toString());
                            errorMsgObj.setVendorMasterDetails(vendorMasterDetailsList);
                        }


                        if (mJson.getString("mastertype").equalsIgnoreCase("Society") &&
                            !mJson.getString("refdoc_no").equalsIgnoreCase("0")) {

                            int status = updateApiStatus("society_center_mst", mJson.getString("refdoc_no"));
                            if (status>0) {
                                i++;
                                errorMsgObj.setStatusCode(200);
                                errorMsgObj.setSuccess(true);
                                errorMsgObj.setMessage("Thanks for your confirmation records has been updated");
                            }
                        }
                        if (mJson.getString("mastertype").equalsIgnoreCase("chilling") &&
                            !mJson.getString("refdoc_no").equalsIgnoreCase("0")) {
                            int status = updateApiStatus("chilling_master", mJson.getString("refdoc_no"));
                            if (status>0) {
                                i++;
                                errorMsgObj.setStatusCode(200);
                                errorMsgObj.setSuccess(true);
                                errorMsgObj.setMessage("Thanks for your confirmation records has been updated");
                            }
                        }
                        if (mJson.getString("mastertype").equalsIgnoreCase("Route") &&
                            !mJson.getString("refdoc_no").equalsIgnoreCase("0")) {
                            int status = updateApiStatus("route_master", mJson.getString("refdoc_no"));
                            if (status>0) {
                                i++;
                                errorMsgObj.setStatusCode(200);
                                errorMsgObj.setSuccess(true);
                                errorMsgObj.setMessage("Thanks for your confirmation records has been updated");
                            }
                        }
                        if (mJson.getString("mastertype").equalsIgnoreCase("Vendor") &&
                            !mJson.getString("refdoc_no").equalsIgnoreCase("0")) {
                            int status = updateApiStatus("vendor_master", mJson.getString("refdoc_no"));
                            if (status>0) {
                                i++;
                                errorMsgObj.setStatusCode(200);
                                errorMsgObj.setSuccess(true);
                                errorMsgObj.setMessage("Thanks for your confirmation records has been updated");
                            }
                        }
                        updateQuery = "";
                        if (i == 0) {
                            errorMsgObj.setStatusCode(500);
                            errorMsgObj.setSuccess(false);
                            errorMsgObj.setMessage("data not found");
                        }
                        stmt.close();
                    } else {
                        errorMsgObj.setStatusCode(500);
                        errorMsgObj.setSuccess(false);
                        errorMsgObj.setMessage("not authorized");
                    }
                    stmt2.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    errorMsgObj.setStatusCode(500);
                    errorMsgObj.setSuccess(false);
                    errorMsgObj.setMessage(ex.toString());
                    WriteToFile(ex.toString());
                } finally {
                    try {
                        conn.close();
                    } catch (Exception ex) {
                        WriteToFile(ex.toString());
                    }
                }

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            WriteToFile(ex.toString());
            return null;
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.getMessage();
                WriteToFile(ex.toString());
                return null;
            }
        }
        // result.setSucess(true);
        try {
            Gson gson = new Gson();
            json = gson.toJson(errorMsgObj);
        } catch (Exception ex) {
            WriteToFile(ex.toString());
        }

        return json;
    }


    public String setFarmerTransactionDetails(String trnDetails) throws JSONException, Exception {
        ErrorMsg errorMsgObj = new ErrorMsg();
        Connection conn = null;
        Statement stmt = null;
        Statement stmt2 = null;
        String json = "";
        String insertQuery = "";
        String password = null;
        String user_name = null;

        if (trnDetails == null || trnDetails == "") {
            trnDetails = "Invalid Json";
        } else {
            JSONArray jsonarray = new JSONArray(trnDetails);
            JSONObject mJson = jsonarray.getJSONObject(0);
            conn = getStartConnection();
            try {
                conn.setAutoCommit(false);
                stmt = conn.createStatement();
                stmt2 = conn.createStatement();
                ResultSet rs2 =
                    stmt2.executeQuery("select * from API_Authorisation where API_USER_CODE='" +
                                       mJson.getString("user") + "' and api_user_password= '" +
                                       mJson.getString("password") + "' and STATUS='Y'");
                if (rs2.next()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date date = new Date();
                    //System.out.println(dateFormat.format(date));
                    String uploadId = dateFormat.format(date).toString();

                    //   JSONArray jsonarray = new JSONArray(trnDetails);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String unitCode = jsonobject.getString("UnitCode");
                        String ChillingCode = jsonobject.getString("ChillingCode");
                        String PaymentcycleID = jsonobject.getString("PaymentcycleID");
                        //System.out.println("url---" + UnitCode);
                        String insertQuery1 =
                            "insert into rmrd_mcc_upd_mst (UPLOAD_ID,UNIT_CD,MCC_CD,UPD_DATA_TYPE,PAY_CYCLE_ID,CREATED_BY,CREATION_DATE,LAST_UPDATED_BY,LAST_UPDATE_DATE,UPLOAD_FROM)\n" +
                            "values('" + uploadId + "','" + unitCode + "','" + ChillingCode + "','DSK','" +
                            PaymentcycleID + "','API',SYSDATE,'API',SYSDATE,'API')";
                       // System.out.println("updateQuery11--" + insertQuery1);
                        stmt.addBatch(insertQuery1);
                   
                        JSONArray details = jsonobject.getJSONArray("Details");
                        for (int k = 0; k < details.length(); k++) {
                            JSONObject jsonobjectDtl = details.getJSONObject(i);
                            // int version= Integer.parseInt(jsonobjectDtl.getString("object_version_number"));
                            //if (version==1) {
                            //SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
                          //  Date date1 = new Date();
                            //System.out.println(dateFormat.format(date));
                             //String uploadLineId=dateFormat1.format(date1).toString();
                            
                           
                            insertQuery =
                                "insert into mm_farmer_data_upload (UNIT_CD,UPLOAD_ID,UPLOAD_LINE_ID,CP_CODE,MCC_CODE,PAY_CYCLE_ID," +
                                "E_DATE,E_TIME,MILK_TYPE,LOCAL_CODE,EXTENDED_CODE,QUANTITY,FAT,SNF,AMOUNT," +
                                "QUANTITY_MODE,MEASUREMENT_MODE,SHIFT,RATE,ITEM_CD," +
                                "SYS_RATE_ID,SYS_RATE,REC_STATUS,CREATED_BY,CREATED_DATE,MODIFY_BY,MODIFY_DATE)\n" +
                                "VALUES('" + unitCode + "','" + dateFormat.format(date).toString() + "',GLOBAL_OCI_SEQ.nextval,'" + jsonobjectDtl.getString("SocietyCode") +
                                "','" + jsonobjectDtl.getString("ChillingCODE") + "','" + PaymentcycleID + "','"+jsonobjectDtl.getString("EDate")+"','" + jsonobjectDtl.getString("Time") + "','" +
                                jsonobjectDtl.getString("MilkType") + "','" + jsonobjectDtl.getString("LocalCode") +
                                "','" + jsonobjectDtl.getString("ExtendedCode") + "','" +
                                jsonobjectDtl.getString("Quantity") + "','" + jsonobjectDtl.getString("FAT") + "','" +
                                jsonobjectDtl.getString("SNF") + "','" + jsonobjectDtl.getString("Amount") + "','" +
                                jsonobjectDtl.getString("Quantity_Mode") + "','" +
                                jsonobjectDtl.getString("Measurement_Mode") + "','" + jsonobjectDtl.getString("SHIFT") +
                                "','" + jsonobjectDtl.getString("RATE") + "','" + jsonobjectDtl.getString("itemCode") +
                                "'," + "'0','0','E','API',SYSDATE,'Admin',SYSDATE)";
                            System.out.println("insertQuery--" + insertQuery);
                            stmt2.addBatch(insertQuery);
                        }
                    }
                    try {
                        int[] updateCounts = stmt.executeBatch();
                        int[] updateCounts1 = stmt2.executeBatch();
                        conn.commit();
                        errorMsgObj.setStatusCode(200);
                        errorMsgObj.setSuccess(true);
                        errorMsgObj.setMessage(" Records has been updated");
                    } catch (Exception ex) {
                        try {
                            conn.rollback();
                            errorMsgObj.setStatusCode(500);
                            errorMsgObj.setSuccess(false);
                            errorMsgObj.setMessage("Records not update! Please retry");
                            ex.printStackTrace();
                            WriteToFile(ex.toString());
                            //return "Failed to save data. Data safely rolled back";
                        } catch (Exception ex1) {

                        }
                    } finally {
                        try {
                            stmt.close();
                            stmt2.close();
                            conn.close();
                        } catch (Exception nex) {
                            //return "N";
                        }
                    }
                }
                else{
                    errorMsgObj.setStatusCode(500);
                    errorMsgObj.setSuccess(false);
                    errorMsgObj.setMessage("not authorized");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                WriteToFile(ex.toString());
               // return null;
            } finally {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.getMessage();
                    WriteToFile(ex.toString());
                }
            }
        }
        try {
            Gson gson = new Gson();
            json = gson.toJson(errorMsgObj);
        } catch (Exception ex) {
            WriteToFile(ex.toString());
        }
        return json;
    }


    public String setRmrdTransactionDetails(String trnDetails) throws JSONException, Exception {
        ErrorMsg errorMsgObj = new ErrorMsg();
        Connection conn = null;
        Statement stmt = null;
        Statement stmt2 = null;
        String json = "";
        String insertDetailsQuery = "";
        String password = null;
        String user_name = null;

        if (trnDetails == null || trnDetails == "") {
            trnDetails = "Invalid Json";
        } else {
            JSONArray jsonarray = new JSONArray(trnDetails);
            JSONObject mJson = jsonarray.getJSONObject(0);
            conn = getStartConnection();
            try {
                conn.setAutoCommit(false);
                stmt = conn.createStatement();
                stmt2 = conn.createStatement();
                ResultSet rs2 =
                    stmt2.executeQuery("select * from API_Authorisation where API_USER_CODE='" +
                                       mJson.getString("user") + "' and api_user_password= '" +
                                       mJson.getString("password") + "' and STATUS='Y'");
                if (rs2.next()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date date = new Date();
                    //System.out.println(dateFormat.format(date));
                    String uploadId = dateFormat.format(date).toString();

                    //   JSONArray jsonarray = new JSONArray(trnDetails);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String unitCode = jsonobject.getString("UnitCode");
                        String ChillingCode = jsonobject.getString("ChillingCode");
                        String PaymentcycleID = jsonobject.getString("PaymentcycleID");
                        //System.out.println("url---" + UnitCode);
                        String insertHeaderTableQuery =
                            "insert into rmrd_mcc_upd_mst (UPLOAD_ID,UNIT_CD,MCC_CD,UPD_DATA_TYPE,PAY_CYCLE_ID,CREATED_BY,CREATION_DATE,LAST_UPDATED_BY,LAST_UPDATE_DATE,UPLOAD_FROM)\n" +
                            "values('" + uploadId + "','" + unitCode + "','" + ChillingCode + "','MCC','" +
                            PaymentcycleID + "','Admin',SYSDATE,'Admin',SYSDATE,'API')";
                       // System.out.println("updateQuery11--" + insertQuery1);
                        stmt.addBatch(insertHeaderTableQuery);
                   
                        JSONArray jsonArrayObj = jsonobject.getJSONArray("Details");
                        for (int k = 0; k < jsonArrayObj.length(); k++) {
                            JSONObject jsonobjectDtl = jsonArrayObj.getJSONObject(i);
                           
                            insertDetailsQuery =
                                "insert into mm_rmrd_data_upload (UNIT_CD,UPLOAD_ID,UPLOAD_LINE_ID,CP_CODE,CP_VENDOR,PAY_CYCLE_ID,E_DATE,E_TIME,MILK_TYPE,LOCAL_CODE,EXTENDED_CODE,QUANTITY,FAT,SNF,AMOUNT,QUANTITY_MODE,MEASUREMENT_MODE,SHIFT,RATE,ITEM_CD,SYS_RATE_ID,SYS_RATE,REC_STATUS,CREATED_BY,CREATED_DATE,MODIFY_BY,MODIFY_DATE)\n" +
                                "VALUES('" + unitCode + "','" + dateFormat.format(date).toString() + "',GLOBAL_OCI_SEQ.nextval,'" + jsonobjectDtl.getString("SocietyCode") +
                                "','" + jsonobjectDtl.getString("ChillingCODE") + "','" + PaymentcycleID + "','"+jsonobjectDtl.getString("EDate")+"','" + jsonobjectDtl.getString("Time") + "','" +
                                jsonobjectDtl.getString("MilkType") + "','" + jsonobjectDtl.getString("LocalCode") +
                                "','" + jsonobjectDtl.getString("ExtendedCode") + "','" +
                                jsonobjectDtl.getString("Quantity") + "','" + jsonobjectDtl.getString("FAT") + "','" +
                                jsonobjectDtl.getString("SNF") + "','" + jsonobjectDtl.getString("Amount") + "','" +
                                jsonobjectDtl.getString("Quantity_Mode") + "','" +
                                jsonobjectDtl.getString("Measurement_Mode") + "','" + jsonobjectDtl.getString("SHIFT") +
                                "','" + jsonobjectDtl.getString("RATE") + "','" + jsonobjectDtl.getString("itemCode") +
                                "'," + "'0','0','E','Admin',SYSDATE,'Admin',SYSDATE)";
                            System.out.println("insertDetailsQuery--" + insertDetailsQuery);
                            stmt2.addBatch(insertDetailsQuery);
                        }
                    }
                    try {
                        int[] updateCounts = stmt.executeBatch();
                        int[] updateCounts1 = stmt2.executeBatch();
                        conn.commit();
                        errorMsgObj.setStatusCode(200);
                        errorMsgObj.setSuccess(true);
                        errorMsgObj.setMessage(" Records has been updated");
                    } catch (Exception ex) {
                        try {
                            conn.rollback();
                            errorMsgObj.setStatusCode(500);
                            errorMsgObj.setSuccess(false);
                            errorMsgObj.setMessage("Records not update! Please retry");
                            ex.printStackTrace();
                            WriteToFile(ex.toString());
                            //return "Failed to save data. Data safely rolled back";
                        } catch (Exception ex1) {

                        }
                    } finally {
                        try {
                            stmt.close();
                            stmt2.close();
                            conn.close();
                        } catch (Exception nex) {
                            //return "N";
                        }
                    }
                }
                else{
                    errorMsgObj.setStatusCode(500);
                    errorMsgObj.setSuccess(false);
                    errorMsgObj.setMessage("not authorized");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                WriteToFile(ex.toString());
               // return null;
            } finally {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.getMessage();
                    WriteToFile(ex.toString());
                }
            }
        }
        try {
            Gson gson = new Gson();
            json = gson.toJson(errorMsgObj);
        } catch (Exception ex) {
            WriteToFile(ex.toString());
        }
        return json;
    }


    public Connection getStartConnection() throws Exception {
        InitialContext initialContext = new InitialContext();
        DataSource ds = (DataSource) initialContext.lookup("java:comp/env/jdbc/APPLICATIONDBDS");
        java.sql.Connection conn = ds.getConnection();
        return conn;
        //        String url = "jdbc:postgresql://10.0.6.171/PFHPL";
        //        System.out.println("url--->" + url);
        //        String user = "postgres";
        //        String password = "Password@1";
        //        Connection conn = null;
        //        try {
        //            conn = DriverManager.getConnection(url, user, password);
        //            System.out.println("Connected to the PostgreSQL server successfully.");
        //        } catch (SQLException e) {
        //            System.out.println(e.getMessage());
        //        }
        //        return conn;
    }

    public static void closeDataSourceConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public int updateApiStatus(String tableName, String refDocNo) {
        int status = 0;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getStartConnection();
            stmt = conn.createStatement();
            String updateQuery = "update " + tableName + " set API_FLAG='Y' where API_REFNO='" + refDocNo + "'";
            System.out.println("updateQuery--" + updateQuery);
             status=  stmt.executeUpdate(updateQuery);
            conn.commit();
            /// stmt.addBatch(updateQuery);
            //int[] updateCounts = stmt.executeBatch();
            //System.out.println("updateCounts--" + updateCounts.length);
        } catch (Exception ex) {
            ex.printStackTrace();
            status = 0;
            WriteToFile(ex.toString());
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (Exception ex) {
                status =0;
                WriteToFile(ex.toString());
            }
        }
        return status;
    }

    public void WriteToFile(String errorMsg) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            //System.out.println(formatter.format(date));
            String file = "/home/oracle/API_ErrorLogs/ErrorLog.txt";
            //String file = "/home/lenovo/Desktop/UCDF/ErrorLog.txt";
            File myObj = new File(file);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
                try {
                    FileWriter myWriter = new FileWriter(file);
                    myWriter.write(formatter.format(date) + "--" + errorMsg);
                    myWriter.close();
                    System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("File already exists.");
                try {
                    FileWriter myWriter = new FileWriter(file);
                    myWriter.write(formatter.format(date) + "--" + errorMsg);
                    myWriter.close();
                    System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

package com.bspl.adapter;

import com.bspl.model.ErrorMsg;
import com.bspl.model.ChillingMstDetails;
import com.bspl.model.PaymentCycleDetails;
import com.bspl.model.RouteMasterDetails;
import com.bspl.model.SocietyMstDetails;
import com.bspl.model.SocityRouteMapDetails;
import com.bspl.model.UnitDetails;
import com.bspl.model.VendorMasterDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.math.BigDecimal;

import java.net.HttpURLConnection;
import java.net.URL;

import java.sql.CallableStatement;

import java.sql.Types;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Date;

import javax.naming.InitialContext;

import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RestAdapter {
    private static final Gson GSON = new Gson();
    private static final String INVALID_KEY_RESPONSE =
        "{\"status\": \"error\", \"message\": \"api-key parameter is required\"}";

    public String getMasterDetails(String apiKey, String mstDetails) throws JSONException, Exception {
        ErrorMsg errorMsgObj = new ErrorMsg();

        if (apiKey == null || apiKey.trim().isEmpty()) {
            return INVALID_KEY_RESPONSE;
        }

        if (mstDetails == null || mstDetails.trim().isEmpty()) {
            return "{\"status\": \"error\", \"message\": \"Invalid Json\"}";
        }

        JSONObject mJson;
        try {
            mJson = new JSONObject(mstDetails);
        } catch (Exception jsone) {
            jsone.printStackTrace();
            return "{\"status\": \"error\", \"message\": \"Invalid JSON format\"}";
        }

        // Validate each required parameter individually with a specific message
        if (!mJson.has("user") || mJson.isNull("user") || mJson.optString("user", "").trim().isEmpty()) {
            return "{\"status\": \"error\", \"message\": \"user parameter is required\"}";
        }
        if (!mJson.has("password") || mJson.isNull("password") || mJson.optString("password", "").trim().isEmpty()) {
            return "{\"status\": \"error\", \"message\": \"password parameter is required\"}";
        }
        if (!mJson.has("unitCode") || mJson.isNull("unitCode") ||
            mJson.optString("unitCode", "").trim().isEmpty()) {
            return "{\"status\": \"error\", \"message\": \"plant_code parameter is required\"}";
        }
        if (!mJson.has("company_name") || mJson.isNull("company_name") ||
            mJson.optString("company_name", "").trim().isEmpty()) {
            return "{\"status\": \"error\", \"message\": \"company_name parameter is required\"}";
        }
        if (!mJson.has("refdoc_no") || mJson.isNull("refdoc_no") || mJson.optString("refdoc_no", "").trim().isEmpty()) {
            return "{\"status\": \"error\", \"message\": \"refdoc_no parameter is required\"}";
        }
        if (!mJson.has("mastertype") || mJson.isNull("mastertype") ||
            mJson.optString("mastertype", "").trim().isEmpty()) {
            return "{\"status\": \"error\", \"message\": \"mastertype parameter is required\"}";
        }

        String user = mJson.getString("user");
        String password = mJson.getString("password");
        String refdocNo = mJson.getString("refdoc_no");
        String plantCode = mJson.getString("unitCode");
        String masterType = mJson.optString("mastertype", "");
        String company_name = mJson.optString("company_name", "");

        String json = "";
        int i = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String sb = dateFormat.format(new Date());

        String authSql =
            "select * from API_Authorisation where API_USER_CODE=? and api_user_password=? and API_HEAD_KEY=? and API_COMPANY_NAME=? and STATUS='Y' and AUTH_TYPE='MASTER'";

        try (Connection conn = getStartConnection();
             PreparedStatement stmt2 = conn.prepareStatement(authSql)) {

            stmt2.setString(1, user.trim());
            stmt2.setString(2, password.trim());
            stmt2.setString(3, apiKey.trim());
            stmt2.setString(4, company_name.trim());

            try (ResultSet rs2 = stmt2.executeQuery()) {
                if (rs2.next()) {
                    errorMsgObj.setStatusCode(200);
                    errorMsgObj.setSuccess(true);
                    errorMsgObj.setMessage("succes");

                    if ("0".equalsIgnoreCase(refdocNo)) {
                        if ("Society".equalsIgnoreCase(masterType)) {
                            String selectSql =
                                "SELECT A.UNIT_CD,A.SOCIETY_CENT_CD,B.SOCIETY_CENT_DESP,D.CHILLING_CENT_CD,E.CHILLING_CENT_DESP,B.ADDRESS,B.CITY_CD,B.DISTRICT_CD,\n" +
                                "B.PHONE_NO,B.EMAIL,B.GST_NO,B.PAN_NO,B.CONTACT_PERSON,B.FSSI_LICENSE_NO,B.OLD_SOCIETY_CENT_CD,F.STATUS,\n" +
                                "A.ROUTE_CODE,C.ROUTE_DESCP,A.FROM_DATE,A.TO_DATE,B.OBJECT_VERSION_NUMBER\n" +
                                "FROM SOCIETY_RUTE_MAP A\n" +
                                "JOIN SOCIETY_CENTER_MST B ON (A.UNIT_CD = B.UNIT_CD AND A.SOCIETY_CENT_CD = B.SOCIETY_CENT_CD)\n" +
                                "JOIN SOCIETY_CENTER_DTL F ON (A.UNIT_CD = F.UNIT_CD AND B.LINE_ID = F.LINE_ID)\n" +
                                "JOIN ROUTE_MASTER C ON (A.UNIT_CD = C.UNIT_CD AND A.ROUTE_CODE = C.ROUTE_CODE)\n" +
                                "JOIN SOCIETY_CHILL_VEND_MAP D ON (A.UNIT_CD = D.UNIT_CD AND A.SOCIETY_CENT_CD = D.SOCIETY_CENT_CD)\n" +
                                "JOIN CHILLING_MASTER E ON (D.UNIT_CD = E.UNIT_CD AND D.CHILLING_CENT_CD = E.CHILLING_CENT_CD)\n" +
                                "WHERE A.UNIT_CD=? AND TRUNC(SYSDATE) BETWEEN A.FROM_DATE AND A.TO_DATE AND\n" +
                                "NVL(F.STATUS,'N')='Y' AND NVL(B.API_FLAG,'N')='N'";

                            String updateSql = "update society_center_mst set API_REFNo=? where SOCIETY_CENT_CD=?";

                            ArrayList<SocietyMstDetails> societyList = new ArrayList<SocietyMstDetails>();

                            try (PreparedStatement stmt = conn.prepareStatement(selectSql);
                                 PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

                                stmt.setString(1, plantCode);
                                try (ResultSet rs = stmt.executeQuery()) {
                                    while (rs.next()) {
                                        i++;
                                        SocietyMstDetails result = new SocietyMstDetails();
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
                                        societyList.add(result);

                                        updateStmt.setString(1, sb);
                                        updateStmt.setString(2, rs.getString("society_cent_cd"));
                                        updateStmt.addBatch();
                                    }
                                }

                                if (i > 0) {
                                    try {
                                        updateStmt.executeBatch();
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                        WriteToFile(ex.toString());
                                    }
                                }
                            }
                            errorMsgObj.setMessage("Successfully! All records has been fetched.");
                            errorMsgObj.setRefDocNo(sb);
                            errorMsgObj.setSocietyDetails(societyList);

                        } else if ("chilling".equalsIgnoreCase(masterType)) {
                            String selectSql =
                                "select a.unit_cd,a.chilling_cent_cd,a.chilling_cent_desp,a.address,a.city_cd,a.district_cd,\n" +
                                "a.phone_no,a.email,a.gst_no,a.pan_no,a.contact_person,a.fssi_license_no,\n" +
                                "a.old_chilling_cent_cd,a.own_comp_flg,b.status,a.object_version_number\n" +
                                "from chilling_master a\n" +
                                "join chilling_detail b on a.line_id=b.line_id and a.UNIT_CD=b.UNIT_CD and b.status='Y'\n" +
                                "where trunc(sysdate) between nvl(b.from_date,trunc(sysdate)) and nvl(b.to_dt,trunc(sysdate))\n" +
                                "and a.UNIT_CD=?";

                            String updateSql = "update chilling_master set API_REFNo=? where CHILLING_CENT_CD=?";

                            ArrayList<ChillingMstDetails> chillingList = new ArrayList<ChillingMstDetails>();

                            try (PreparedStatement stmt = conn.prepareStatement(selectSql);
                                 PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

                                stmt.setString(1, plantCode);
                                try (ResultSet rs = stmt.executeQuery()) {
                                    while (rs.next()) {
                                        i++;
                                        ChillingMstDetails result = new ChillingMstDetails();
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
                                        chillingList.add(result);

                                        updateStmt.setString(1, sb);
                                        updateStmt.setString(2, rs.getString("chilling_cent_cd"));
                                        updateStmt.addBatch();
                                    }
                                }

                                if (i > 0) {
                                    try {
                                        updateStmt.executeBatch();
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                        WriteToFile(ex.toString());
                                    }
                                }
                            }
                            errorMsgObj.setMessage("Successfully! All records has been fetched.");
                            errorMsgObj.setRefDocNo(sb);
                            errorMsgObj.setChillingdetails(chillingList);

                        } else if ("Route".equalsIgnoreCase(masterType)) {
                            String selectSql =
                                "SELECT route_code,route_descp,distance,route_duration_frm,route_duration_to,chilling_cent_cd,status,object_version_number,mgr_distance,evng_distance,UNIT_CD FROM route_master where UNIT_CD=?";
                            String updateSql = "update route_master set API_REFNo=? where ROUTE_CODE=?";

                            ArrayList<RouteMasterDetails> routeList = new ArrayList<RouteMasterDetails>();

                            try (PreparedStatement stmt = conn.prepareStatement(selectSql);
                                 PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

                                stmt.setString(1, plantCode);
                                try (ResultSet rs = stmt.executeQuery()) {
                                    while (rs.next()) {
                                        i++;
                                        RouteMasterDetails result = new RouteMasterDetails();
                                        result.setRoute_code(rs.getString("route_code"));
                                        result.setRoute_descp(rs.getString("route_descp"));
                                        result.setRoute_duration_frm(rs.getString("route_duration_frm"));
                                        result.setRoute_duration_to(rs.getString("route_duration_to"));
                                        result.setChilling_cent_cd(rs.getString("chilling_cent_cd"));
                                        result.setStatus(rs.getString("status"));
                                        result.setObject_version_number(rs.getInt("object_version_number"));
                                        result.setMgr_distance(rs.getString("mgr_distance"));
                                        result.setEvng_distance(rs.getString("evng_distance"));
                                        result.setUnitCode(rs.getString("UNIT_CD"));
                                        routeList.add(result);

                                        updateStmt.setString(1, sb);
                                        updateStmt.setString(2, rs.getString("route_code"));
                                        updateStmt.addBatch();
                                    }
                                }

                                if (i > 0) {
                                    try {
                                        updateStmt.executeBatch();
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                        WriteToFile(ex.toString());
                                    }
                                }
                            }
                            errorMsgObj.setMessage("Successfully! All records has been fetched.");
                            errorMsgObj.setRefDocNo(sb);
                            errorMsgObj.setRouteMasterDetails(routeList);

                        } else if ("Vendor".equalsIgnoreCase(masterType)) {
                            String selectSql =
                                "SELECT A.vendor_id, A.vendor_code, A.NAME AS vendor_name,A.registeration_date, A.pan_no, A.gst_reg_no, A.aadhar_card, A.father_husband_name, A.farmer_local_code, A.cast_category, A.sex_gender, A.ven_type_code, A.ven_type, C.contact_person, C.address1, C.city, C.STATE, C.city_code, B.bank_name, B.bank_ac_no, B.bank_ifcs_code, A.vendor_status, D.society_cent_cd, E.route_code, F.chilling_cent_cd, A.object_version_number FROM vendor_master A LEFT JOIN vendor_bank_detail B ON A.vendor_code = B.ven_cd  JOIN vendor_regd_address C ON A.vendor_code = C.vendor_code  JOIN society_farmer_ven_map D ON A.vendor_code = D.farmer_ven_cd JOIN (SELECT DISTINCT society_cent_cd, route_code  FROM society_rute_map where UNIT_CD=?) E ON D.society_cent_cd = E.society_cent_cd JOIN (SELECT DISTINCT society_cent_cd, chilling_cent_cd FROM society_chill_vend_map where UNIT_CD=?) F ON D.society_cent_cd = F.society_cent_cd  WHERE A.ven_type = 'F' AND A.vendor_status = 'OK' AND A.QA_STATUS = 'Y'  AND trunc(D.TO_DT) >= trunc(SYSDATE) AND A.api_flag = 'N' and  d.UNIT_CD=?";

                            String updateSql = "update vendor_master set API_REFNO=? where vendor_id=?";

                            ArrayList<VendorMasterDetails> vendorList = new ArrayList<VendorMasterDetails>();

                            try (PreparedStatement stmt = conn.prepareStatement(selectSql);
                                 PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

                                stmt.setString(1, plantCode);
                                stmt.setString(2, plantCode);
                                stmt.setString(3, plantCode);

                                try (ResultSet rs = stmt.executeQuery()) {
                                    while (rs.next()) {
                                        i++;
                                        VendorMasterDetails result = new VendorMasterDetails();
                                        result.setVendor_code(rs.getString("vendor_code"));
                                        result.setVendor_name(rs.getString("vendor_name"));
                                        result.setRegisteration_date(rs.getString("registeration_date"));
                                        result.setPan_no(rs.getString("pan_no"));
                                        result.setGst_reg_no(rs.getString("gst_reg_no"));
                                        result.setAadhar_card(rs.getString("aadhar_card"));
                                        result.setFather_husband_name(rs.getString("father_husband_name"));
                                        if (rs.getString("farmer_local_code") == null ||
                                            rs.getString("farmer_local_code").isEmpty()) {
                                            result.setFarmer_local_code(" ");
                                        } else {
                                            result.setFarmer_local_code(rs.getString("farmer_local_code"));
                                        }

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
                                        result.setVendorId(rs.getString("vendor_id"));
                                        vendorList.add(result);

                                        updateStmt.setString(1, sb);
                                        updateStmt.setString(2, rs.getString("vendor_id"));
                                        updateStmt.addBatch();
                                    }
                                }

                                if (i > 0) {
                                    try {
                                        updateStmt.executeBatch();
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                        WriteToFile(ex.toString());
                                    }
                                }
                            }
                            errorMsgObj.setMessage("Successfully! All records has been fetched.");
                            errorMsgObj.setRefDocNo(sb);
                            errorMsgObj.setVendorMasterDetails(vendorList);

                        } 
                        else if ("PaymentCycle".equalsIgnoreCase(masterType)) {
                            String selectSql =
                                "select unit_cd,chilling_cent_cd,pay_cycle,pay_frm_dt,pay_to_dt,farm_migo,farm_inv_po,dsk_comm,drk_rec_po,pay_line_id,object_version_number,api_refno from paymet_cycle where UNIT_CD=?";
                            String updateSql = "update paymet_cycle set API_REFNo=? where pay_line_id=?";

                            ArrayList<PaymentCycleDetails> paymentList = new ArrayList<PaymentCycleDetails>();

                            try (PreparedStatement stmt = conn.prepareStatement(selectSql);
                                 PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

                                stmt.setString(1, plantCode);
                                try (ResultSet rs = stmt.executeQuery()) {
                                    while (rs.next()) {
                                        i++;
                                        PaymentCycleDetails result = new PaymentCycleDetails();
                                        result.setUnit_cd(rs.getString("unit_cd"));
                                        result.setChilling_cent_cd(rs.getString("chilling_cent_cd"));
                                        result.setPay_cycle(rs.getString("pay_cycle"));
                                        result.setPay_frm_dt(rs.getString("pay_frm_dt"));
                                        result.setPay_to_dt(rs.getString("pay_to_dt"));
                                        result.setFarm_migo(rs.getString("farm_migo"));
                                        result.setFarm_inv_po(rs.getString("farm_inv_po"));
                                        result.setDsk_comm(rs.getString("dsk_comm"));
                                        result.setDrk_rec_po(rs.getString("drk_rec_po"));
                                        result.setObject_version_number(rs.getInt("object_version_number"));
                                        paymentList.add(result);

                                        updateStmt.setString(1, sb);
                                        updateStmt.setString(2, rs.getString("pay_line_id"));
                                        updateStmt.addBatch();
                                    }
                                }

                                if (i > 0) {
                                    try {
                                        updateStmt.executeBatch();
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                        WriteToFile(ex.toString());
                                    }
                                }
                            }
                            errorMsgObj.setMessage("Successfully! All records has been fetched.");
                            errorMsgObj.setRefDocNo(sb);
                            errorMsgObj.setPaymentcycledetails(paymentList);

                        } 
                        else if ("Unit".equalsIgnoreCase(masterType)) {
                            String selectSql =
                                "select CODE,CITY_CODE,SBU_CODE,NAME,ADDRESS,API_REFNO from unit where CODE=?";
                            ArrayList<UnitDetails> unitList = new ArrayList<UnitDetails>();

                            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                                stmt.setString(1, plantCode);
                                try (ResultSet rs = stmt.executeQuery()) {
                                    while (rs.next()) {
                                        i++;
                                        UnitDetails result = new UnitDetails();
                                        result.setUnitCode(rs.getString("CODE"));
                                        result.setCityCode(rs.getString("CITY_CODE"));
                                        result.setSbuCode(rs.getString("SBU_CODE"));
                                        result.setUnitName(rs.getString("NAME"));
                                        result.setUnitAddress(rs.getString("ADDRESS"));
                                        result.setApiRefno(rs.getString("API_REFNO"));
                                        unitList.add(result);
                                    }
                                }
                            }
                            errorMsgObj.setMessage("Successfully! All records has been fetched.");
                            errorMsgObj.setUnitdetails(unitList);
                        }
                        
                        else if ("Society_Route_Map".equalsIgnoreCase(masterType)) {
                            String selectSql =
                                "select LINE_ID,SOCIETY_CENT_CD,ROUTE_CODE,UNIT_CD,FROM_DATE,TO_DATE,ROUTE_TYPE,OBJECT_VERSION_NUMBER from SOCIETY_RUTE_MAP where API_FLAG is null or API_FLAG='N' and UNIT_CD=?";
                            String updateSql = "update SOCIETY_RUTE_MAP set API_REFNo=? where LINE_ID=?";

                            ArrayList<SocityRouteMapDetails> socityRouteMapList = new ArrayList<SocityRouteMapDetails>();

                            try (PreparedStatement stmt = conn.prepareStatement(selectSql);
                                 PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

                                stmt.setString(1, plantCode);
                                try (ResultSet rs = stmt.executeQuery()) {
                                    while (rs.next()) {
                                        i++;
                                        SocityRouteMapDetails result = new SocityRouteMapDetails();
                                        result.setUnitCd(rs.getString("UNIT_CD"));
                                        result.setSocietyCentCd(rs.getString("SOCIETY_CENT_CD"));
                                        result.setRouteCode(rs.getString("ROUTE_CODE"));
                                        result.setFromDate(rs.getString("FROM_DATE"));
                                        result.setToDate(rs.getString("TO_DATE"));
                                        result.setRouteType(rs.getString("ROUTE_TYPE"));
                                        result.setObject_version_number(rs.getInt("object_version_number"));
                                        socityRouteMapList.add(result);
                                        updateStmt.setString(1, sb);
                                        updateStmt.setString(2, rs.getString("LINE_ID"));
                                        updateStmt.addBatch();
                                    }
                                }

                                if (i > 0) {
                                    try {
                                        updateStmt.executeBatch();
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                        WriteToFile(ex.toString());
                                    }
                                }
                            }
                            errorMsgObj.setMessage("Successfully! All records has been fetched.");
                            errorMsgObj.setRefDocNo(sb);
                            errorMsgObj.setSocityRouteMapDetails(socityRouteMapList);

                        } 
                        
                    } 
                    else {
                        String tableName = null;
                        if ("Society".equalsIgnoreCase(masterType)) {
                            tableName = "society_center_mst";
                        } else if ("chilling".equalsIgnoreCase(masterType)) {
                            tableName = "chilling_master";
                        } else if ("Route".equalsIgnoreCase(masterType)) {
                            tableName = "route_master";
                        } else if ("Vendor".equalsIgnoreCase(masterType)) {
                            tableName = "vendor_master";
                        } else if ("PaymentCycle".equalsIgnoreCase(masterType)) {
                            tableName = "paymet_cycle";
                        } else if ("Unit".equalsIgnoreCase(masterType)) {
                            tableName = "unit";
                        }
                        else if ("Society_Route_Map".equalsIgnoreCase(masterType)) {
                                tableName = "SOCIETY_RUTE_MAP";
                                }

                        if (tableName != null) {
                            int status = updateApiStatus(tableName, refdocNo);
                            if (status > 0) {
                                i++;
                                errorMsgObj.setStatusCode(200);
                                errorMsgObj.setSuccess(true);
                                errorMsgObj.setMessage("Thanks for your confirmation records has been updated");
                            }
                        }
                    }

                    if (i == 0) {
                        errorMsgObj.setStatusCode(500);
                        errorMsgObj.setSuccess(false);
                        errorMsgObj.setMessage("data not found");
                    }
                } else {
                    errorMsgObj.setStatusCode(500);
                    errorMsgObj.setSuccess(false);
                    errorMsgObj.setMessage("not authorized");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            errorMsgObj.setStatusCode(500);
            errorMsgObj.setSuccess(false);
            errorMsgObj.setMessage(ex.toString());
            WriteToFile(ex.toString());
        }

        try {
            json = GSON.toJson(errorMsgObj);
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
            JSONObject responseObject = new JSONObject(trnDetails);
            int status = responseObject.getInt("responseStatus");
            String message = responseObject.getString("responseMessage");
            String ApiRefno = responseObject.getString("batchcode");
            System.out.println("Response Status: " + status);
            System.out.println("Response Message: " + message);
            System.out.println("Batch Code: " + ApiRefno);
            if (status == 200) {
                conn = getStartConnection();
                try {
                    stmt = conn.createStatement();
                    stmt2 = conn.createStatement();
                    //                    ResultSet rs =
                    //                        stmt.executeQuery("select * from API_Authorisation where API_USER_CODE='" +
                    //                                           responseObject.getString("user") + "' and api_user_password= '" +
                    //                                           responseObject.getString("password") + "' and STATUS='Y'");
                    //                    if (rs.next()) {
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
                        System.out.println("societyCode: " + jsonobjectDtl.getString("societyCode"));
                        System.out.println("eDate: " + jsonobjectDtl.getString("eDate"));
                        String inputDate = jsonobjectDtl.getString("eDate"); // input date in YYYY-MM-DD format
                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yy");
                        // Parse the input date
                        LocalDate date1 = LocalDate.parse(inputDate, inputFormatter);
                        // Format to the desired output
                        formattedDate = date1.format(outputFormatter);
                        System.out.println("formattedDate---" + formattedDate);

                        System.out.println("time: " + jsonobjectDtl.getString("time"));
                        System.out.println("itemCode: " + jsonobjectDtl.getString("itemCode"));
                        System.out.println("milkType: " + jsonobjectDtl.getString("milkType"));
                        System.out.println("localCode: " + jsonobjectDtl.getString("localCode"));
                        System.out.println("extendedCode: " + jsonobjectDtl.getString("extendedCode"));
                        System.out.println("quantity: " + jsonobjectDtl.getString("quantity"));
                        System.out.println("fat: " + jsonobjectDtl.getString("fat"));
                        System.out.println("snf: " + jsonobjectDtl.getString("snf"));
                        System.out.println("amount: " + jsonobjectDtl.getString("amount"));
                        System.out.println("quantity_Mode: " + jsonobjectDtl.getString("quantity_Mode"));
                        System.out.println("shift: " + jsonobjectDtl.getString("shift"));
                        System.out.println("rate: " + jsonobjectDtl.getString("rate"));
                        System.out.println("primeryId: " + jsonobjectDtl.getString("primeryId"));
                        System.out.println("objectversionNo: " + jsonobjectDtl.getString("objectversionNo"));
                        System.out.println("chillingCode: " + jsonobjectDtl.getString("chillingCode"));
                        chillingCode = jsonobjectDtl.getString("chillingCode");
                        System.out.println("unitCode: " + jsonobjectDtl.getString("unitCode"));
                        unitCode = jsonobjectDtl.getString("unitCode");

                        //                            String paymentcycleIDQuery="SELECT PAY_CYCLE FROM PAYMET_CYCLE WHERE '"+jsonobjectDtl.getString("eDate")+"' BETWEEN PAY_FRM_DT AND PAY_TO_DT";
                        //                            System.out.println("paymentcycleIDQuery---"+paymentcycleIDQuery);
                        //                            stmt2 = conn.createStatement();
                        //                            ResultSet rs2 =stmt2.executeQuery(paymentcycleIDQuery);
                        //                            if (rs2.next()) {
                        //                               PaymentcycleID=  rs2.getString("PAY_CYCLE");
                        //                            }

                        try {
                            insertQuery =
                                "insert into mm_farmer_data_upload (UNIT_CD,UPLOAD_ID,UPLOAD_LINE_ID,CP_CODE,MCC_CODE,PAY_CYCLE_ID," +
                                "E_DATE,E_TIME,MILK_TYPE,LOCAL_CODE,EXTENDED_CODE,QUANTITY,FAT,SNF,AMOUNT," +
                                "QUANTITY_MODE,MEASUREMENT_MODE,SHIFT,RATE,ITEM_CD," +
                                "SYS_RATE_ID,SYS_RATE,REC_STATUS,CREATED_BY,CREATED_DATE,MODIFY_BY,MODIFY_DATE,API_REFNO)\n" +
                                "VALUES('" + unitCode + "','" + uploadid + "',GLOBAL_OCI_SEQ.nextval,'" +
                                jsonobjectDtl.getString("societyCode") + "','" +
                                jsonobjectDtl.getString("chillingCode") + "'," + paymentcycleID + ",'" + formattedDate +
                                "','" + jsonobjectDtl.getString("time") + "','" + jsonobjectDtl.getString("milkType") +
                                "','" + jsonobjectDtl.getString("localCode") + "','" +
                                jsonobjectDtl.getString("extendedCode") + "','" + jsonobjectDtl.getString("quantity") +
                                "','" + jsonobjectDtl.getString("fat") + "','" + jsonobjectDtl.getString("snf") +
                                "','" + jsonobjectDtl.getString("amount") + "','" +
                                jsonobjectDtl.getString("quantity_Mode") + "','" +
                                jsonobjectDtl.getString("measurement_Mode") + "','" + jsonobjectDtl.getString("shift") +
                                "','" + jsonobjectDtl.getString("rate") + "','" + jsonobjectDtl.getString("itemCode") +
                                "'," + "'0','0','E','Admin',SYSDATE,'Admin',SYSDATE,'" + ApiRefno + "')";
                            System.out.println("insertQuery--" + insertQuery);
                            stmt2.addBatch(insertQuery);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    try {
                        String insertHeadQuery =
                            "insert into rmrd_mcc_upd_mst (UPLOAD_ID,UNIT_CD,MCC_CD,UPD_DATA_TYPE,PAY_CYCLE_ID,CREATED_BY,CREATION_DATE,LAST_UPDATED_BY,LAST_UPDATE_DATE,UPLOAD_FROM)\n" +
                            "values('" + uploadid + "','" + unitCode + "','" + chillingCode + "','DSK'," +
                            paymentcycleID + ",'Admin',SYSDATE,'Admin',SYSDATE,'API')";
                        // System.out.println("updateQuery11--" + insertQuery1);
                        stmt.addBatch(insertHeadQuery);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    try {
                        int[] updateCounts = stmt.executeBatch();
                        int[] updateCounts1 = stmt2.executeBatch();
                        conn.commit();
                        errorMsgObj.setStatusCode(200);
                        errorMsgObj.setSuccess(true);
                        errorMsgObj.setRefDocNo(ApiRefno);
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

                    //  }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }


        }


        //            JSONArray jsonarray = new JSONArray(trnDetails);
        //            JSONObject mJson = jsonarray.getJSONObject(0);
        //            conn = getStartConnection();
        //            try {
        //                conn.setAutoCommit(false);
        //                stmt = conn.createStatement();
        //                stmt2 = conn.createStatement();
        //                ResultSet rs2 =
        //                    stmt2.executeQuery("select * from API_Authorisation where API_USER_CODE='" +
        //                                       mJson.getString("user") + "' and api_user_password= '" +
        //                                       mJson.getString("password") + "' and STATUS='Y'");
        //                if (rs2.next()) {
        //                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        //                    Date date = new Date();
        //                    //System.out.println(dateFormat.format(date));
        //                    String uploadId = dateFormat.format(date).toString();
        //
        //                    //   JSONArray jsonarray = new JSONArray(trnDetails);
        //                    for (int i = 0; i < jsonarray.length(); i++) {
        //                        JSONObject jsonobject = jsonarray.getJSONObject(i);
        //                        String unitCode = jsonobject.getString("UnitCode");
        //                        String chillingCode = jsonobject.getString("ChillingCode");
        //                        String PaymentcycleID = jsonobject.getString("PaymentcycleID");
        //                        //System.out.println("url---" + UnitCode);
        //                        String insertQuery1 =
        //                            "insert into rmrd_mcc_upd_mst (UPLOAD_ID,UNIT_CD,MCC_CD,UPD_DATA_TYPE,PAY_CYCLE_ID,CREATED_BY,CREATION_DATE,LAST_UPDATED_BY,LAST_UPDATE_DATE,UPLOAD_FROM)\n" +
        //                            "values('" + uploadId + "','" + unitCode + "','" + chillingCode + "','DSK','" +
        //                            PaymentcycleID + "','API',SYSDATE,'API',SYSDATE,'API')";
        //                        // System.out.println("updateQuery11--" + insertQuery1);
        //                        stmt.addBatch(insertQuery1);
        //
        //                        JSONArray details = jsonobject.getJSONArray("Details");
        //                        for (int k = 0; k < details.length(); k++) {
        //                            JSONObject jsonobjectDtl = details.getJSONObject(i);
        //                            // int version= Integer.parseInt(jsonobjectDtl.getString("object_version_number"));
        //                            //if (version==1) {
        //                            //SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
        //                            //  Date date1 = new Date();
        //                            //System.out.println(dateFormat.format(date));
        //                            //String uploadLineId=dateFormat1.format(date1).toString();
        //
        //
        //                            insertQuery =
        //                                "insert into mm_farmer_data_upload (UNIT_CD,UPLOAD_ID,UPLOAD_LINE_ID,CP_CODE,MCC_CODE,PAY_CYCLE_ID," +
        //                                "E_DATE,E_TIME,MILK_TYPE,LOCAL_CODE,EXTENDED_CODE,QUANTITY,FAT,SNF,AMOUNT," +
        //                                "QUANTITY_MODE,MEASUREMENT_MODE,SHIFT,RATE,ITEM_CD," +
        //                                "SYS_RATE_ID,SYS_RATE,REC_STATUS,CREATED_BY,CREATED_DATE,MODIFY_BY,MODIFY_DATE)\n" +
        //                                "VALUES('" + unitCode + "','" + dateFormat.format(date).toString() +
        //                                "',GLOBAL_OCI_SEQ.nextval,'" + jsonobjectDtl.getString("SocietyCode") + "','" +
        //                                jsonobjectDtl.getString("ChillingCODE") + "','" + PaymentcycleID + "','" +
        //                                jsonobjectDtl.getString("EDate") + "','" + jsonobjectDtl.getString("Time") + "','" +
        //                                jsonobjectDtl.getString("MilkType") + "','" + jsonobjectDtl.getString("LocalCode") +
        //                                "','" + jsonobjectDtl.getString("ExtendedCode") + "','" +
        //                                jsonobjectDtl.getString("Quantity") + "','" + jsonobjectDtl.getString("FAT") + "','" +
        //                                jsonobjectDtl.getString("SNF") + "','" + jsonobjectDtl.getString("Amount") + "','" +
        //                                jsonobjectDtl.getString("Quantity_Mode") + "','" +
        //                                jsonobjectDtl.getString("Measurement_Mode") + "','" + jsonobjectDtl.getString("SHIFT") +
        //                                "','" + jsonobjectDtl.getString("RATE") + "','" + jsonobjectDtl.getString("itemCode") +
        //                                "'," + "'0','0','E','API',SYSDATE,'Admin',SYSDATE)";
        //                            System.out.println("insertQuery--" + insertQuery);
        //                            stmt2.addBatch(insertQuery);
        //                        }
        //                    }
        //                    try {
        //                        int[] updateCounts = stmt.executeBatch();
        //                        int[] updateCounts1 = stmt2.executeBatch();
        //                        conn.commit();
        //                        errorMsgObj.setStatusCode(200);
        //                        errorMsgObj.setSuccess(true);
        //                        errorMsgObj.setMessage(" Records has been updated");
        //                    } catch (Exception ex) {
        //                        try {
        //                            conn.rollback();
        //                            errorMsgObj.setStatusCode(500);
        //                            errorMsgObj.setSuccess(false);
        //                            errorMsgObj.setMessage("Records not update! Please retry");
        //                            ex.printStackTrace();
        //                            WriteToFile(ex.toString());
        //                            //return "Failed to save data. Data safely rolled back";
        //                        } catch (Exception ex1) {
        //
        //                        }
        //                    } finally {
        //                        try {
        //                            stmt.close();
        //                            stmt2.close();
        //                            conn.close();
        //                        } catch (Exception nex) {
        //                            //return "N";
        //                        }
        //                    }
        //                } else {
        //                    errorMsgObj.setStatusCode(500);
        //                    errorMsgObj.setSuccess(false);
        //                    errorMsgObj.setMessage("not authorized");
        //                }
        //            } catch (SQLException ex) {
        //                ex.printStackTrace();
        //                WriteToFile(ex.toString());
        //                // return null;
        //            } finally {
        //                try {
        //                    conn.close();
        //                } catch (SQLException ex) {
        //                    ex.getMessage();
        //                    WriteToFile(ex.toString());
        //                }
        //            }
        //        }
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
                                "VALUES('" + unitCode + "','" + uploadId + "',GLOBAL_OCI_SEQ.nextval,'" +
                                jsonobjectDtl.getString("SocietyCode") + "','" +
                                jsonobjectDtl.getString("ChillingCODE") + "','" + PaymentcycleID + "','" +
                                jsonobjectDtl.getString("EDate") + "','" + jsonobjectDtl.getString("Time") + "','" +
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
                    }

                    finally {
                        try {
                            stmt.close();
                            stmt2.close();
                            conn.close();
                        } catch (Exception nex) {
                            //return "N";
                        }
                    }
                } else {
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
            status = stmt.executeUpdate(updateQuery);
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
                status = 0;
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
            // String file = "/home/oracle/API_ErrorLogs/ErrorLog.txt";
            //String file = "/home/lenovo/Desktop/UCDF/ErrorLog.txt";
            String file = "/u01/data/api_log/ErrorLog.txt";
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


    public String getkanhaFarmerCollection(String unitCodeFromPage, String empCode, String company_name) {
        Connection conn = null;
        Statement stmt = null;
        Statement stmt2 = null;
        Statement stmt3 = null;
        String userName = null;
        String password = null;
        String api_url = null;
        String json = "";
        String insertDetailsQuery = "";
        ErrorMsg errorMsgObj = new ErrorMsg();
        Gson gson = new Gson();
        if (unitCodeFromPage == null || unitCodeFromPage.trim().isEmpty() || empCode == null ||
            empCode.trim().isEmpty() || company_name == null || company_name.trim().isEmpty()) {
            return "No parameter found";
        } else {
            try {
                conn = getStartConnection();
                stmt3 = conn.createStatement();
                ResultSet rs2 =
                    stmt3.executeQuery("SELECT * FROM API_Authorisation WHERE STATUS='Y' AND API_COMPANY_NAME='" +
                                       company_name.trim() + "' AND AUTH_TYPE='TRANSACTION'");

                if (rs2.next()) {
                    userName = rs2.getString("API_USER_CODE").trim();
                    password = rs2.getString("API_USER_PASSWORD").trim();
                    api_url = rs2.getString("API_URL").trim();
                } else {
                    return "User and Password not found";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        try {
            // URL url = new URL("http://182.18.144.204:50019/api/v1.0/UCDF/Sycncollectionucdf");
            if (api_url == null || api_url.trim().isEmpty()) {
                // urlFromTable="http://140.245.15.174:50003/api/v1.0/UCDF/Sycncollectionucdf";
                return "Please Maintain url";
            }
            URL url = new URL(api_url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            //            String jsonInputString =
            //                "{\"user\":\"Admin\",\"password\":\"Admin@123\",\"collectiontype\":\"vendor\",\"refdoc_no\":\"0\"}";
            //  String jsonInputString ="{\"user\":\"Admin\",\"password\":\"Admin@123\",\"collectiontype\":\"vendor\",\"refdoc_no\":\"0\",\"unitCode\":\"" + unitCodeFromPage + "\"}";
//            String jsonInputString =
//                "{\"user\":\"" + userName + "\",\"password\":\"" + password +
//                "\",\"collectiontype\":\"vendor\",\"refdoc_no\":\"0\",\"unitCode\":\"" + unitCodeFromPage + "\"}";
                  String jsonInputString ="{\"user\":\"" + userName +"\",\"password\":\"" + password +"\",\"collectiontype\":\"vendor\"" +",\"refdoc_no\":\"0\"" +",\"unitCode\":\"" + unitCodeFromPage +"\",\"company_name\":\"" + company_name + "\"}";
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
                    try {
                        stmt = conn.createStatement();
                        stmt2 = conn.createStatement();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = new Date();
                        String uploadid = dateFormat.format(date).toString();
                        String unitCode = null;
                        String chillingCode = null;
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
                                    "SYS_RATE_ID,SYS_RATE,REC_STATUS,CREATED_BY,CREATED_DATE,MODIFY_BY,MODIFY_DATE,API_REFNO,API_FLAG,NR,CAN,ROUTE_CODE,API_COMPANY)\n" +
                                    "VALUES('" + unitCode + "','" + uploadid + "',GLOBAL_OCI_SEQ.nextval,'" +
                                    societyCode + "','" + chillingCode + "',null,'" + formattedDate.toString() + "','" +
                                    time + "','" + milkType + "','" + localCode + "','" + extendedCode + "','" +
                                    quantity + "','" + fat + "','" + snf + "','" + amount + "','" + quantity_Mode +
                                    "','" + measurement_Mode + "','" + shift + "','" + rate + "','" + itemCode + "'," +
                                    "'0','0','E','" + empCode + "',SYSDATE,'" + empCode + "',SYSDATE,'" + ApiRefno +
                                    "','Y','" + primeryId + "','" + can + "','" + route_Code + "','" + company_name +
                                    "')";
                                System.out.println("insertDetailsQuery--" + insertDetailsQuery);
                                stmt2.addBatch(insertDetailsQuery);
                            } catch (Exception ex) {
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setMessage("Records not update! Please retry");
                                ex.printStackTrace();
                                WriteToFile(ex.toString());
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
                                    System.out.println("resultFlag===>" + resultFlag);
                                    System.out.println("errorMessage===>" + errorMessage);


                                    if (resultFlag.equalsIgnoreCase("S")) {
                                        errorMsgObj.setStatusCode(200);
                                        errorMsgObj.setSuccess(true);
                                        errorMsgObj.setRefDocNo(ApiRefno);
                                        //errorMsgObj.setUploadId(uploadid);
                                        errorMsgObj.setMessage(" Records has been updated");
                                        WriteToFile("Farmer Collection -->Records has been updated");
                                    } else {
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
                                    WriteToFile(ex.toString());
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
                                WriteToFile(ex.toString());
                                //return "Failed to save data. Data safely rolled back";
                            } catch (Exception ex1) {
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setMessage("Records not update! Please retry");
                                ex.printStackTrace();
                                WriteToFile(ex1.toString());
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
                                WriteToFile(nex.toString());
                                json = gson.toJson(errorMsgObj);
                                return json;
                            }
                        }

                    } catch (Exception ex) {
                        errorMsgObj.setStatusCode(500);
                        errorMsgObj.setSuccess(false);
                        errorMsgObj.setMessage("Records not update! Please retry");
                        ex.printStackTrace();
                        WriteToFile(ex.toString());
                        json = gson.toJson(errorMsgObj);
                        return json;
                    }
                } else {
                    errorMsgObj.setStatusCode(500);
                    errorMsgObj.setSuccess(false);
                    errorMsgObj.setMessage("Data not fetch from kanha Api! Please retry");
                    WriteToFile("Data not featch  from kanha Api");
                    json = gson.toJson(errorMsgObj);
                    return json;
                }


            }
        } catch (Exception ex) {
            errorMsgObj.setStatusCode(500);
            errorMsgObj.setSuccess(false);
            errorMsgObj.setMessage("Records not update! Please retry");
            ex.printStackTrace();
            WriteToFile(ex.toString());
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
            WriteToFile(ex.toString());
            json = gson.toJson(errorMsgObj);
            return json;
        }

        System.out.println("json--->" + json);
        JSONObject responseObject = null;
        try {
            responseObject = new JSONObject(json);
        } catch (JSONException e) {
        }
        boolean success = responseObject.optBoolean("success", false);
        String message = responseObject.optString("message", "");
        String refDocNo = responseObject.optString("refDocNo", "");

        if (success && !refDocNo.isEmpty()) {
            System.out.println("API Message: " + message);
            System.out.println("Reference Document No: " + refDocNo);
            String jsonInputString ="{\"user\":\"" + userName +"\",\"password\":\"" + password +"\",\"collectiontype\":\"vendor\"" +",\"refdoc_no\":\"0\"" +",\"unitCode\":\"" + unitCodeFromPage +"\",\"company_name\":\"" + company_name + "\"}";
            try {
                callPostAcknowledgementApi(api_url, jsonInputString);
            } catch (IOException e) {
            }
        }
        return json;
    }


    public String getkanhaRMRDCollection(String unitCodeFromPage, String empCode, String company_name) {
        Connection conn = null;
        Statement stmt = null;
        Statement stmt2 = null;
        Statement stmt3 = null;
        String json = "";
        String insertDetailsQuery = "";
        String userName = null;
        String password = null;
        String urlFromTable = null;
        ErrorMsg errorMsgObj = new ErrorMsg();
        Gson gson = new Gson();
        if (unitCodeFromPage == null || unitCodeFromPage.trim().isEmpty() || empCode == null ||
            empCode.trim().isEmpty() || company_name == null || company_name.trim().isEmpty()) {
            return "No parameter found";
        } else {
            try {
                conn = getStartConnection();
                stmt3 = conn.createStatement();
                ResultSet rs2 =
                    stmt3.executeQuery("SELECT * FROM API_Authorisation WHERE STATUS='Y' AND API_COMPANY_NAME='" +
                                       company_name.trim() + "' AND AUTH_TYPE='TRANSACTION'");

                if (rs2.next()) {
                    userName = rs2.getString("API_USER_CODE").trim();
                    password = rs2.getString("API_USER_PASSWORD").trim();
                    urlFromTable = rs2.getString("API_URL").trim();
                } else {
                    return "User and Password not found";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            // URL url = new URL("http://182.18.144.204:50019/api/v1.0/UCDF/Sycncollectionucdf");
            if (urlFromTable == null || urlFromTable.trim().isEmpty()) {

                //urlFromTable="http://140.245.15.174:50003/api/v1.0/UCDF/Sycncollectionucdf";
                return "Please Maintain url";
            }
            URL url = new URL(urlFromTable);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            //            String jsonInputString =
            //                "{\"user\":\"Admin\",\"password\":\"Admin@123\",\"collectiontype\":\"RMRD\",\"refdoc_no\":\"0\"}";
//            String jsonInputString =
//                "{\"user\":\"" + userName + "\",\"password\":\"" + password +
//                "\",\"collectiontype\":\"RMRD\",\"refdoc_no\":\"0\",\"unitCode\":\"" + unitCodeFromPage + "\"}";
            String jsonInputString ="{\"user\":\"" + userName +"\",\"password\":\"" + password +"\",\"collectiontype\":\"RMRD\"" +",\"refdoc_no\":\"0\"" +",\"unitCode\":\"" + unitCodeFromPage +"\",\"company_name\":\"" + company_name + "\"}";
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
                            String adulteration_STATUS = null;
                            if (jsonobjectDtl.getString("adulteration_STATUS").toString() == null ||
                                jsonobjectDtl.getString("adulteration_STATUS").toString() == "") {
                            } else {
                                adulteration_STATUS = jsonobjectDtl.getString("adulteration_STATUS").toString();
                            }
                            String remark = null;
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
                                    "insert into mm_rmrd_data_upload_api (UNIT_CD,UPLOAD_ID,UPLOAD_LINE_ID,CP_CODE,CP_VENDOR,E_DATE,E_TIME,MILK_TYPE,LOCAL_CODE,EXTENDED_CODE,QUANTITY,FAT,SNF,AMOUNT,QUANTITY_MODE,MEASUREMENT_MODE,SHIFT,RATE,ITEM_CD,SYS_RATE_ID,SYS_RATE,REC_STATUS,CREATED_BY,CREATED_DATE,MODIFY_BY,MODIFY_DATE,API_REFNO,API_FLAG,NR,ROUTE_CODE,recive_can,accept_can,sample_id,reject_can,REJECT_QTY,ADULTERATION_STATUS,REMARK,API_COMPANY)\n" +
                                    "VALUES('" + unitCode + "','" + uploadid + "',GLOBAL_OCI_SEQ.nextval,'" +
                                    chillingCode + "','" + chillingCode + "','" + formattedDate.toString() + "','" +
                                    time + "','" + milkType + "','" + localCode + "','" + extendedCode + "','" +
                                    quantity + "','" + fat + "','" + snf + "','" + amount + "','" + quantity_Mode +
                                    "','" + measurement_Mode + "','" + shift + "','" + rate + "','" + itemCode + "'," +
                                    "'0','0','E','" + empCode + "',SYSDATE,'Admin',SYSDATE,'" + ApiRefno + "','Y','" +
                                    primeryId + "','" + route_Code + "','" + rcans + "','" + acans + "','" + sampleid +
                                    "',null," + rejecT_QTY + ",'" + adulteration_STATUS + "','" + remark + "','" +
                                    company_name + "')";
                                System.out.println("insertDetailsQuery--" + insertDetailsQuery);

                                stmt2.addBatch(insertDetailsQuery);
                            } catch (Exception ex) {
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setMessage("Records not update! Please retry");
                                ex.printStackTrace();
                                WriteToFile(ex.toString());
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
                                    System.out.println("resultFlag===>" + resultFlag);
                                    System.out.println("errorMessage===>" + errorMessage);


                                    if (resultFlag.equalsIgnoreCase("S")) {
                                        errorMsgObj.setStatusCode(200);
                                        errorMsgObj.setSuccess(true);
                                        errorMsgObj.setRefDocNo(ApiRefno);
                                        //errorMsgObj.setUploadId(uploadid);
                                        errorMsgObj.setMessage(" Records has been updated");
                                        WriteToFile("RMRD--->Records has been updated");
                                    } else {
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
                                    WriteToFile(ex.toString());
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
                                WriteToFile(ex.toString());
                                //return "Failed to save data. Data safely rolled back";
                            } catch (Exception ex1) {
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setMessage("Records not update! Please retry");
                                ex.printStackTrace();
                                WriteToFile(ex1.toString());
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
                                WriteToFile(nex.toString());
                                json = gson.toJson(errorMsgObj);
                                return json;
                            }
                        }

                    } catch (Exception ex) {
                        errorMsgObj.setStatusCode(500);
                        errorMsgObj.setSuccess(false);
                        errorMsgObj.setMessage("Records not update! Please retry");
                        ex.printStackTrace();
                        WriteToFile(ex.toString());
                        json = gson.toJson(errorMsgObj);
                        return json;
                    }
                } else {
                    errorMsgObj.setStatusCode(500);
                    errorMsgObj.setSuccess(false);
                    errorMsgObj.setMessage("Data not fetch from kanha Api! Please retry");
                    WriteToFile("Data not featch  from kanha Api");
                    json = gson.toJson(errorMsgObj);
                    return json;
                }


            }
        } catch (Exception ex) {
            errorMsgObj.setStatusCode(500);
            errorMsgObj.setSuccess(false);
            errorMsgObj.setMessage("Records not update! Please retry");
            ex.printStackTrace();
            WriteToFile(ex.toString());
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
            WriteToFile(ex.toString());
            json = gson.toJson(errorMsgObj);
            return json;
        }
        System.out.println("json--->" + json);
        JSONObject responseObject = null;
        try {
            responseObject = new JSONObject(json);
        } catch (JSONException e) {
        }
        boolean success = responseObject.optBoolean("success", false);
        String message = responseObject.optString("message", "");
        String refDocNo = responseObject.optString("refDocNo", "");

        if (success && !refDocNo.isEmpty()) {
            System.out.println("API Message: " + message);
            System.out.println("Reference Document No: " + refDocNo);
            String jsonInputString ="{\"user\":\"" + userName +"\",\"password\":\"" + password +"\",\"collectiontype\":\"RMRD\"" +",\"refdoc_no\":\"0\"" +",\"unitCode\":\"" + unitCodeFromPage +"\",\"company_name\":\"" + company_name + "\"}";
            try {
                callPostAcknowledgementApi(urlFromTable, jsonInputString);
            } catch (IOException e) {
            }
        }
        return json;
    }


    public String getkanhaDispatchCollection(String unitCodeFromPage, String empCode, String company_name) {
        Connection conn = null;
        Statement stmt = null;
        Statement stmt2 = null;
        Statement stmt3 = null;
        String json = "";
        String insertDetailsQuery = "";
        String userName = null;
        String password = null;
        String urlFromTable = null;
        ErrorMsg errorMsgObj = new ErrorMsg();
        Gson gson = new Gson();
        if (unitCodeFromPage == null || unitCodeFromPage.trim().isEmpty() || empCode == null ||
            empCode.trim().isEmpty() || company_name == null || company_name.trim().isEmpty()) {
            return "No parameter found";
        } else {
            try {
                conn = getStartConnection();
                stmt3 = conn.createStatement();
                ResultSet rs2 =
                    stmt3.executeQuery("SELECT * FROM API_Authorisation WHERE STATUS='Y' AND API_COMPANY='" +
                                       company_name.trim() + "' AND AUTH_TYPE='TRANSACTION'");

                if (rs2.next()) {
                    userName = rs2.getString("API_USER_CODE").trim();
                    password = rs2.getString("API_USER_PASSWORD").trim();
                    urlFromTable = rs2.getString("API_URL").trim();
                } else {
                    return "User and Password not found";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            // URL url = new URL("http://182.18.144.204:50019/api/v1.0/UCDF/Sycncollectionucdf");
            if (urlFromTable == null || urlFromTable.trim().isEmpty()) {
                urlFromTable = "http://140.245.15.174:50003/api/v1.0/UCDF/Sycncollectionucdf";
            }
            URL url = new URL(urlFromTable);
            // URL url = new URL("http://140.245.15.174:50003/api/v1.0/UCDF/Sycncollectionucdf");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            String jsonInputString =
                "{\"user\":\"" + userName + "\",\"password\":\"" + password +
                "\",\"collectiontype\":\"DISPATCH\",\"refdoc_no\":\"0\",\"unitCode\":\"" + unitCodeFromPage + "\"}";
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

                    //  conn = getStartConnection();
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
                                    "insert into mm_rmrd_dispatch_upload_api (UNIT_CD,UPLOAD_ID,UPLOAD_LINE_ID,CP_CODE,CP_VENDOR,E_DATE,E_TIME,MILK_TYPE,LOCAL_CODE,EXTENDED_CODE,QUANTITY,FAT,SNF,AMOUNT,QUANTITY_MODE,MEASUREMENT_MODE,SHIFT,RATE,ITEM_CD,SYS_RATE_ID,SYS_RATE,REC_STATUS,CREATED_BY,CREATED_DATE,MODIFY_BY,MODIFY_DATE,API_REFNO,API_FLAG,NR,ROUTE_CODE,recive_can,accept_can,sample_id,reject_can)\n" +
                                    "VALUES('" + unitCode + "','" + uploadid + "',GLOBAL_OCI_SEQ.nextval,'" +
                                    societyCode + "','" + chillingCode + "','" + formattedDate.toString() + "','" +
                                    time + "','" + milkType + "','" + localCode + "','" + extendedCode + "','" +
                                    quantity + "','" + fat + "','" + snf + "','" + amount + "','" + quantity_Mode +
                                    "','" + measurement_Mode + "','" + shift + "','" + rate + "','" + itemCode + "'," +
                                    "'0','0','E','" + empCode + "',SYSDATE,'Admin',SYSDATE,'" + ApiRefno + "','Y','" +
                                    primeryId + "','" + route_Code + "','" + rcans + "','" + acans + "','" + sampleid +
                                    "',null)";
                                System.out.println("insertDetailsQuery--" + insertDetailsQuery);

                                stmt2.addBatch(insertDetailsQuery);
                            } catch (Exception ex) {
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setMessage("Records not update! Please retry");
                                ex.printStackTrace();
                                WriteToFile(ex.toString());
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
                                    cs = conn.prepareCall("{CALL PROC_INST_DISPATCH_TO_MAIN_TAB(?,?,?,?)}");
                                    cs.setObject(1, id);
                                    cs.setObject(2, empCode);
                                    cs.registerOutParameter(3, Types.VARCHAR);
                                    cs.registerOutParameter(4, Types.VARCHAR);
                                    // ResultSet rs2 = cs.executeQuery();
                                    cs.executeQuery();
                                    resultFlag = cs.getString(3);
                                    errorMessage = cs.getString(4);
                                    System.out.println("resultFlag===>" + resultFlag);
                                    System.out.println("errorMessage===>" + errorMessage);


                                    if (resultFlag.equalsIgnoreCase("S")) {
                                        errorMsgObj.setStatusCode(200);
                                        errorMsgObj.setSuccess(true);
                                        errorMsgObj.setRefDocNo(ApiRefno);
                                        //errorMsgObj.setUploadId(uploadid);
                                        errorMsgObj.setMessage(" Records has been updated");
                                    } else {
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
                                    WriteToFile(ex.toString());
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
                                WriteToFile(ex.toString());
                                //return "Failed to save data. Data safely rolled back";
                            } catch (Exception ex1) {
                                errorMsgObj.setStatusCode(500);
                                errorMsgObj.setSuccess(false);
                                errorMsgObj.setMessage("Records not update! Please retry");
                                ex.printStackTrace();
                                WriteToFile(ex1.toString());
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
                                WriteToFile(nex.toString());
                                json = gson.toJson(errorMsgObj);
                                return json;
                            }
                        }

                    } catch (Exception ex) {
                        errorMsgObj.setStatusCode(500);
                        errorMsgObj.setSuccess(false);
                        errorMsgObj.setMessage("Records not update! Please retry");
                        ex.printStackTrace();
                        WriteToFile(ex.toString());
                        json = gson.toJson(errorMsgObj);
                        return json;
                    }
                } else {
                    errorMsgObj.setStatusCode(500);
                    errorMsgObj.setSuccess(false);
                    errorMsgObj.setMessage("Data not fetch from kanha Api! Please retry");
                    WriteToFile("Data not featch  from kanha Api");
                    json = gson.toJson(errorMsgObj);
                    return json;
                }


            }
        } catch (Exception ex) {
            errorMsgObj.setStatusCode(500);
            errorMsgObj.setSuccess(false);
            errorMsgObj.setMessage("Records not update! Please retry");
            ex.printStackTrace();
            WriteToFile(ex.toString());
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
            WriteToFile(ex.toString());
            json = gson.toJson(errorMsgObj);
            return json;
        }
        return json;
    }


    private String callPostAcknowledgementApi(String apiUrl, String jsonInputString) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setConnectTimeout(15000);
        con.setReadTimeout(30000);
        con.setDoOutput(true);
        try {
            byte[] input = jsonInputString.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            try (OutputStream os = con.getOutputStream()) {
                os.write(input);
            }

            int responseCode = con.getResponseCode();
            InputStream stream =
                responseCode >= 200 && responseCode < 400 ? con.getInputStream() : con.getErrorStream();

            if (stream == null) {
                return "HTTP Status: " + responseCode;
            }

            try (BufferedReader br =
                 new BufferedReader(new InputStreamReader(stream, java.nio.charset.StandardCharsets.UTF_8))) {

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            }
        } finally {
            con.disconnect();
        }
    }


}

package com.bspl.model;
//import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
 import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
 //ObjectMapper om = new ObjectMapper();
//Root[] root = om.readValue(myJsonString, Root[].class); 
public class FarmerDetails {
    @JsonProperty("CP_CODE") 
        public String cP_CODE;
        @JsonProperty("MCC_CODE") 
        public String mCC_CODE;
        @JsonProperty("PAY_CYCLE_ID") 
        public String pAY_CYCLE_ID;
        @JsonProperty("E_DATE") 
        public String e_DATE;
        @JsonProperty("E_TIME") 
        public String e_TIME;
        @JsonProperty("MILK_TYPE") 
        public String mILK_TYPE;
        @JsonProperty("LOCAL_CODE") 
        public String lOCAL_CODE;
        @JsonProperty("EXTENDED_CODE") 
        public String eXTENDED_CODE;
        @JsonProperty("QUANTITY") 
        public String qUANTITY;
        @JsonProperty("FAT") 
        public String fAT;
        @JsonProperty("SNF") 
        public String sNF;
        @JsonProperty("AMOUNT") 
        public String aMOUNT;
        @JsonProperty("QUANTITY_MODE") 
        public String qUANTITY_MODE;
        @JsonProperty("MEASUREMENT_MODE") 
        public String mEASUREMENT_MODE;
        @JsonProperty("SHIFT") 
        public String sHIFT;
        @JsonProperty("RATE") 
        public String rATE;
        @JsonProperty("ITEM_CD") 
        public String iTEM_CD;
        @JsonProperty("STATUS") 
        public String sTATUS;
        @JsonProperty("OBJECT_VERSION_NUMBER") 
        public int oBJECT_VERSION_NUMBER;

    public void setCP_CODE(String cP_CODE) {
        this.cP_CODE = cP_CODE;
    }

    public String getCP_CODE() {
        return cP_CODE;
    }

    public void setMCC_CODE(String mCC_CODE) {
        this.mCC_CODE = mCC_CODE;
    }

    public String getMCC_CODE() {
        return mCC_CODE;
    }

    public void setPAY_CYCLE_ID(String pAY_CYCLE_ID) {
        this.pAY_CYCLE_ID = pAY_CYCLE_ID;
    }

    public String getPAY_CYCLE_ID() {
        return pAY_CYCLE_ID;
    }

    public void setE_DATE(String e_DATE) {
        this.e_DATE = e_DATE;
    }

    public String getE_DATE() {
        return e_DATE;
    }

    public void setE_TIME(String e_TIME) {
        this.e_TIME = e_TIME;
    }

    public String getE_TIME() {
        return e_TIME;
    }

    public void setMILK_TYPE(String mILK_TYPE) {
        this.mILK_TYPE = mILK_TYPE;
    }

    public String getMILK_TYPE() {
        return mILK_TYPE;
    }

    public void setLOCAL_CODE(String lOCAL_CODE) {
        this.lOCAL_CODE = lOCAL_CODE;
    }

    public String getLOCAL_CODE() {
        return lOCAL_CODE;
    }

    public void setEXTENDED_CODE(String eXTENDED_CODE) {
        this.eXTENDED_CODE = eXTENDED_CODE;
    }

    public String getEXTENDED_CODE() {
        return eXTENDED_CODE;
    }

    public void setQUANTITY(String qUANTITY) {
        this.qUANTITY = qUANTITY;
    }

    public String getQUANTITY() {
        return qUANTITY;
    }

    public void setFAT(String fAT) {
        this.fAT = fAT;
    }

    public String getFAT() {
        return fAT;
    }

    public void setSNF(String sNF) {
        this.sNF = sNF;
    }

    public String getSNF() {
        return sNF;
    }

    public void setAMOUNT(String aMOUNT) {
        this.aMOUNT = aMOUNT;
    }

    public String getAMOUNT() {
        return aMOUNT;
    }

    public void setQUANTITY_MODE(String qUANTITY_MODE) {
        this.qUANTITY_MODE = qUANTITY_MODE;
    }

    public String getQUANTITY_MODE() {
        return qUANTITY_MODE;
    }

    public void setMEASUREMENT_MODE(String mEASUREMENT_MODE) {
        this.mEASUREMENT_MODE = mEASUREMENT_MODE;
    }

    public String getMEASUREMENT_MODE() {
        return mEASUREMENT_MODE;
    }

    public void setSHIFT(String sHIFT) {
        this.sHIFT = sHIFT;
    }

    public String getSHIFT() {
        return sHIFT;
    }

    public void setRATE(String rATE) {
        this.rATE = rATE;
    }

    public String getRATE() {
        return rATE;
    }

    public void setITEM_CD(String iTEM_CD) {
        this.iTEM_CD = iTEM_CD;
    }

    public String getITEM_CD() {
        return iTEM_CD;
    }

    public void setSTATUS(String sTATUS) {
        this.sTATUS = sTATUS;
    }

    public String getSTATUS() {
        return sTATUS;
    }

    public void setOBJECT_VERSION_NUMBER(int oBJECT_VERSION_NUMBER) {
        this.oBJECT_VERSION_NUMBER = oBJECT_VERSION_NUMBER;
    }

    public int getOBJECT_VERSION_NUMBER() {
        return oBJECT_VERSION_NUMBER;
    }
}

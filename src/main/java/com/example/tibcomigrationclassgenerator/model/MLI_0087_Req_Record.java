package com.example.tibcomigrationclassgenerator.model;

import lombok.Data;

@Data
//    Mli0087ResRecordWrapper
    public class MLI_0087_Req_Record {
    private String mli_0087_req_mesg_id;  // Corresponds to <MLI-0087-REQ-MESG-ID>
    private String mli_0087_req_ver_no;   // Corresponds to <MLI-0087-REQ-VER-NO>
    private String mli_0087_req_termid;   // Corresponds to <MLI-0087-REQ-TERMID>
    private String mli_0087_re0_user_id;  // Corresponds to <MLI-0087-RE0-USER-ID>
    private String mli_0087_req_dte_time; // Corresponds to <MLI-0087-REQ-DTE-TIME>
    private String mli_0087_req_action_cd;// Corresponds to <MLI-0087-REQ-ACTION-CD>
    private String mli_0087_req_org;      // Corresponds to <MLI-0087-REQ-ORG>
    private String mli_0087_req_bkbr_number; // Corresponds to <MLI-0087-REQ-BKBR-NUMBER>
    private String mli_0087_req_org_inner;

    public String getMli_0087_req_ver_no() {
        return mli_0087_req_ver_no;
    }

    public void setMli_0087_req_ver_no(String mli_0087_req_ver_no) {
        this.mli_0087_req_ver_no = mli_0087_req_ver_no;
    }

    public String getMli_0087_req_mesg_id() {
        return mli_0087_req_mesg_id;
    }

    public void setMli_0087_req_mesg_id(String mli_0087_req_mesg_id) {
        this.mli_0087_req_mesg_id = mli_0087_req_mesg_id;
    }

    public String getMli_0087_req_termid() {
        return mli_0087_req_termid;
    }

    public void setMli_0087_req_termid(String mli_0087_req_termid) {
        this.mli_0087_req_termid = mli_0087_req_termid;
    }

    public String getMli_0087_re0_user_id() {
        return mli_0087_re0_user_id;
    }

    public void setMli_0087_re0_user_id(String mli_0087_re0_user_id) {
        this.mli_0087_re0_user_id = mli_0087_re0_user_id;
    }

    public String getMli_0087_req_dte_time() {
        return mli_0087_req_dte_time;
    }

    public void setMli_0087_req_dte_time(String mli_0087_req_dte_time) {
        this.mli_0087_req_dte_time = mli_0087_req_dte_time;
    }

    public String getMli_0087_req_action_cd() {
        return mli_0087_req_action_cd;
    }

    public void setMli_0087_req_action_cd(String mli_0087_req_action_cd) {
        this.mli_0087_req_action_cd = mli_0087_req_action_cd;
    }

    public String getMli_0087_req_org() {
        return mli_0087_req_org;
    }

    public void setMli_0087_req_org(String mli_0087_req_org) {
        this.mli_0087_req_org = mli_0087_req_org;
    }

    public String getMli_0087_req_bkbr_number() {
        return mli_0087_req_bkbr_number;
    }

    public void setMli_0087_req_bkbr_number(String mli_0087_req_bkbr_number) {
        this.mli_0087_req_bkbr_number = mli_0087_req_bkbr_number;
    }

    public String getMli_0087_req_org_inner() {
        return mli_0087_req_org_inner;
    }

    public void setMli_0087_req_org_inner(String mli_0087_req_org_inner) {
        this.mli_0087_req_org_inner = mli_0087_req_org_inner;
    }
}

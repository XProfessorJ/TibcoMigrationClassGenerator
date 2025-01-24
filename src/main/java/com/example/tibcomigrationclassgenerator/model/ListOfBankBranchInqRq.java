package com.example.tibcomigrationclassgenerator.model;

import java.lang.reflect.Array;

public class ListOfBankBranchInqRq {
    private String branchOrgCode;
    private Country country;
    private String bankBranchNo;
    private Array ListRq;

    public Array getListRq() {
        return ListRq;
    }

    public void setListRq(Array listRq) {
        ListRq = listRq;
    }

    public String getBankBranchNo() {
        return bankBranchNo;
    }

    public void setBankBranchNo(String bankBranchNo) {
        this.bankBranchNo = bankBranchNo;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getBranchOrgCode() {
        return branchOrgCode;
    }

    public void setBranchOrgCode(String branchOrgCode) {
        this.branchOrgCode = branchOrgCode;
    }

    // Inner class for Country
    public class Country {
        private String branchOrgCode;

        public String getBranchOrgCode() {
            return branchOrgCode;
        }

        public void setBranchOrgCode(String branchOrgCode) {
            this.branchOrgCode = branchOrgCode;
        }
    }
}
//class Country {
//    private String branchOrgCode;
//
//    public String getBranchOrgCode() {
//        return branchOrgCode;
//    }
//
//    public void setBranchOrgCode(String branchOrgCode) {
//        this.branchOrgCode = branchOrgCode;
//    }
//}
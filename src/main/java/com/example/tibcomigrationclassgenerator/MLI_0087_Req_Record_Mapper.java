package com.example.tibcomigrationclassgenerator;

import com.example.tibcomigrationclassgenerator.model.APIHeader;
import com.example.tibcomigrationclassgenerator.model.ListOfBankBranchInqRq;
import com.example.tibcomigrationclassgenerator.model.MLI_0087_Req_Record;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {APIHeader.class, ListOfBankBranchInqRq.class})
public abstract class MLI_0087_Req_Record_Mapper {

    @Mapping(target = "mli_0087_req_mesg_id", constant = "0087")
    @Mapping(target = "mli_0087_req_ver_no", constant = "11")
    @Mapping(target = "mli_0087_req_termid", source = "listOfBankBranchInqRq", qualifiedByName = "mapMLI_0087_REQ_TERMID")
    @Mapping(target = "mli_0087_re0_user_id", source = "listOfBankBranchInqRq",  qualifiedByName = "mapMLI_0087_RE0_USER_ID")
    @Mapping(target = "mli_0087_req_dte_time", source = "listOfBankBranchInqRq",  qualifiedByName = "mapMLI_0087_REQ_DTE_TIME")
    @Mapping(target = "mli_0087_req_action_cd", source = "listOfBankBranchInqRq", qualifiedByName = "mapMLI_0087_REQ_ACTION_CD")
    @Mapping(target = "mli_0087_req_org", source = "branchOrgCode")
    @Mapping(target = "mli_0087_req_bkbr_number", ignore = true, qualifiedByName = "mapMLI_0087_REQ_BKBR_NUMBER")
    public abstract MLI_0087_Req_Record map(ListOfBankBranchInqRq listOfBankBranchInqRq, @Context APIHeader apiHeader);

    @Named("mapMLI_0087_REQ_TERMID")
    public String mapMLI_0087_REQ_TERMID(ListOfBankBranchInqRq listOfBankBranchInqRq, @Context APIHeader apiHeader) {
        // "concat(substring($Start/root/pfx4:RqHeader/pfx4:ClientDetails/pfx4:TerminalID,1,8), substring($Start/root/pfx4:RqHeader/pfx4:ClientDetails/pfx4:DestCountryCode,1,2))"
        return null;
    }

    @Named("mapMLI_0087_RE0_USER_ID")
    public String mapMLI_0087_RE0_USER_ID(ListOfBankBranchInqRq listOfBankBranchInqRq, @Context APIHeader apiHeader) {
        // $Start/root/pfx4:RqHeader/pfx4:ClientDetails/pfx4:UserID
        return null;
    }

    @Named("mapMLI_0087_REQ_DTE_TIME")
    public String mapMLI_0087_REQ_DTE_TIME(ListOfBankBranchInqRq listOfBankBranchInqRq, @Context APIHeader apiHeader) {
        //if (esbparam:getDataForTwoKeys('isDateTimeConversionRequired',     $Start/root/pfx4:RqHeader/pfx4:ClientDetails/pfx4:ChannelID,     $Start/root/pfx4:RqHeader/pfx4:ClientDetails/pfx4:DestCountryCode) = 'Y') then     esbcustom:convertECSRequest('DT','DT',esbcustom:getCurrentDatetime($Start/root/pfx4:RqHeader/pfx4:ClientDetails/pfx4:DestCountryCode))     else esbcustom:convertECSRequest('DT','DT',$Start/root/pfx4:RqHeader/pfx4:DateAndTimeStamp)
        //Key: mli-0087-req-action-cd, Value: if (string-length(tib:trim($Start/root/pfx12:ListOfBankBranchInqRq/pfx12:BankBranchNo)) = 0) then                          '02'                          else                          '01'
        return null;
    }

    @Named("mapMLI_0087_REQ_ACTION_CD")
    public String mapMLI_0087_REQ_ACTION_CD(ListOfBankBranchInqRq listOfBankBranchInqRq, @Context APIHeader apiHeader) {
        //if (string-length(tib:trim($Start/root/pfx12:ListOfBankBranchInqRq/pfx12:BankBranchNo)) = 0) then                          '02'                          else                          '01'
        return null;
    }

    @Named("mapMLI_0087_REQ_BKBR_NUMBER")
    public String mapMLI_0087_REQ_BKBR_NUMBER(ListOfBankBranchInqRq listOfBankBranchInqRq, @Context APIHeader apiHeader) {
        //if (string-length(tib:trim($Start/root/pfx12:ListOfBankBranchInqRq/pfx12:ListRq/StartIndex)) != 0) then       $Start/root/pfx12:ListOfBankBranchInqRq/pfx12:ListRq/StartIndex     else if (exists($Start/root/pfx12:ListOfBankBranchInqRq/pfx12:BankBranchNo) and              string-length($Start/root/pfx12:ListOfBankBranchInqRq/pfx12:BankBranchNo) > 0) then       $Start/root/pfx12:ListOfBankBranchInqRq/pfx12:BankBranchNo     else       '0'
        return null;
    }
}
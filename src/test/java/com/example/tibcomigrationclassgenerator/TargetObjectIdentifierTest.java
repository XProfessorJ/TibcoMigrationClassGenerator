package com.example.tibcomigrationclassgenerator;

import com.example.tibcomigrationclassgenerator.controller.TargetObjectIdentifier;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TargetObjectIdentifierTest {

    // 测试用例 1：验证函数返回频率最高的 SourceType
    @Test
    public void testFindMostFrequentSourceType() {
        Map<String, LinkedHashMap<String, String>> keyValueMap = Map.of(
                "MLI-0087-REQ-RECORD", new LinkedHashMap<>() {{
                    put("mli-0087-req-mesg-id", "0087");
                    put("mli-0087-req-ver-no", "11");
                    put("mli-0087-req-termid", "concat(substring($Start/root/pfx4:RqHeader/pfx4:ClientDetails/pfx4:TerminalID,1,8), substring($Start/root/pfx4:RqHeader/pfx4:ClientDetails/pfx4:DestCountryCode,1,2))");
                    put("mli-0087-re0-user-id", "$Start/root/pfx4:RqHeader/pfx4:ClientDetails/pfx4:UserID");
                    put("mli-0087-req-dte-time", "if (esbparam:getDataForTwoKeys('isDateTimeConversionRequired', $Start/root/pfx4:RqHeader/pfx4:ClientDetails/pfx4:ChannelID, $Start/root/pfx4:RqHeader/pfx4:ClientDetails/pfx4:DestCountryCode) = 'Y') then esbcustom:convertECSRequest('DT','DT',esbcustom:getCurrentDatetime($Start/root/pfx4:RqHeader/pfx4:ClientDetails/pfx4:DestCountryCode)) else esbcustom:convertECSRequest('DT','DT',$Start/root/pfx4:RqHeader/pfx4:DateAndTimeStamp)");
                    put("mli-0087-req-action-cd", "if (string-length(tib:trim($Start/root/pfx12:ListOfBankBranchInqRq/pfx12:BankBranchNo)) = 0) then '02' else '01'");
                    put("mli-0087-req-org", "$Start/root/pfx12:ListOfBankBranchInqRq/pfx12:BranchOrgCode");
                    put("mli-0087-req-bkbr-number", "if (string-length(tib:trim($Start/root/pfx12:ListOfBankBranchInqRq/pfx12:ListRq/StartIndex)) != 0) then $Start/root/pfx12:ListOfBankBranchInqRq/pfx12:ListRq/StartIndex else if (exists($Start/root/pfx12:ListOfBankBranchInqRq/pfx12:BankBranchNo) and string-length($Start/root/pfx12:ListOfBankBranchInqRq/pfx12:BankBranchNo) > 0) then $Start/root/pfx12:ListOfBankBranchInqRq/pfx12:BankBranchNo else '0'");
                }}
        );

        // 调用方法得到频率最高的 SourceType
        String sourceType = TargetObjectIdentifier.findMostFrequentSourceType(keyValueMap);

        // 验证返回值是否正确
        assertEquals("ListOfBankBranchInqRq", sourceType, "The most frequent SourceType should be 'ListOfBankBranchInqRq'");
    }
}
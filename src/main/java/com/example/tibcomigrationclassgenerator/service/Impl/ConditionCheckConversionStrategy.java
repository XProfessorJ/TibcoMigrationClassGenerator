package com.example.tibcomigrationclassgenerator.service.Impl;

import com.example.tibcomigrationclassgenerator.service.LogicConversionStrategy;

public class ConditionCheckConversionStrategy implements LogicConversionStrategy {
    @Override
    public String convert(String comment) {
        // 模拟解析 TIBCO 条件判断逻辑
        // TIBCO 注释例子: if (string-length(tib:trim($Start/root/pfx12:ListOfBankBranchInqRq/pfx12:BankBranchNo)) = 0) then '02' else '01'
        return "if (isBankBranchNoEmpty()) {\n" +
                "    return \"02\";\n" +
                "} else {\n" +
                "    return \"01\";\n" +
                "}";
    }
}
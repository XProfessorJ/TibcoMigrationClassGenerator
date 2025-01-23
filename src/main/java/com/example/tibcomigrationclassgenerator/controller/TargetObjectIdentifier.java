package com.example.tibcomigrationclassgenerator.controller;


import java.util.*;
import java.util.regex.*;

public class TargetObjectIdentifier {

    // 主要方法：找到频率最高的 SourceType
    public static String findMostFrequentSourceType(Map<String, LinkedHashMap<String, String>> keyValueMap) {
        // 存储每个复杂字段值中倒数第二个 pfx12 后面出现的对象及其频率
        Map<String, Integer> frequencyMap = new HashMap<>();

        // 遍历所有复杂字段
        for (Map.Entry<String, LinkedHashMap<String, String>> entry : keyValueMap.entrySet()) {
            for (Map.Entry<String, String> subEntry : entry.getValue().entrySet()) {
                String value = subEntry.getValue();

                // 打印所有字段值，方便调试
                System.out.println("Checking field: " + subEntry.getKey() + ", value: " + value);

                // 只处理复杂字段
                if (isComplexValue(value)) {
                    System.out.println("Complex value detected: " + value);

                    // 提取并统计倒数第二个 pfx12 后面的对象
                    String sourceType = extractSecondLastPfx12Object(value);
                    if (sourceType != null) {
                        frequencyMap.put(sourceType, frequencyMap.getOrDefault(sourceType, 0) + 1);
                    }
                }
            }
        }

        // 找到出现频率最高的 SourceType
        return frequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null); // 如果没有找到复杂字段，则返回 null
    }

    // 判断是否为复杂字段的逻辑
    private static boolean isComplexValue(String value) {
        return value != null && (
                value.contains("if") ||
                        value.contains("concat") ||
                        value.contains("esbcustom") ||
                        value.contains("$") ||
                        value.contains("/")
        );
    }

    // 提取复杂字段中的倒数第二个 pfx12 后面的对象
    private static String extractSecondLastPfx12Object(String value) {
        // 使用正则提取所有 pfx12: 后的对象
        Pattern pattern = Pattern.compile("pfx12:([^\\s/]+)");
        Matcher matcher = pattern.matcher(value);

        // 存储所有 pfx12 后的对象
        List<String> matchedParts = new ArrayList<>();
        while (matcher.find()) {
            matchedParts.add(matcher.group(1));
        }

        // 打印匹配到的所有 pfx12 后的对象，方便调试
        System.out.println("Matched pfx12 parts: " + matchedParts);

        // 返回倒数第二个 pfx12 后的对象，如果有的话
        if (matchedParts.size() >= 2) {
            return matchedParts.get(matchedParts.size() - 2);
        }
        return null; // 如果没有找到倒数第二个对象，返回 null
    }

    // 测试主方法
    public static void main(String[] args) {
        // 示例输入数据
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

        // 找到频率最高的 SourceType
        String sourceType = findMostFrequentSourceType(keyValueMap);
        System.out.println("Most frequent SourceType: " + sourceType);
    }
}
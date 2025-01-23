package com.example.tibcomigrationclassgenerator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapperClassGenerator {

    private static final Logger log = LoggerFactory.getLogger(MapperClassGenerator.class);

    // 生成 Mapper 类的通用方法
    public static void generateMapperFromHashMap(Map<String, LinkedHashMap<String, String>> keyValueMap, String className, String packageName) {
        StringBuilder classCode = new StringBuilder();
        String targetObjectIdentifier = getTargetObjectIdentifier(keyValueMap);
        // 添加包名
        classCode.append("package ").append(packageName).append(";\n\n");

        // 添加导入语句
        classCode.append("import org.mapstruct.Context;\n")
                .append("import org.mapstruct.Mapper;\n")
                .append("import org.mapstruct.Mapping;\n")
                .append("import org.mapstruct.Named;\n\n");

        // 类声明
        classCode.append("@Mapper(componentModel = \"spring\")\n")
                .append("public abstract class ").append(className).append(" {\n\n");

        // 遍历 keyValueMap，生成映射方法
        for (Map.Entry<String, LinkedHashMap<String, String>> entry : keyValueMap.entrySet()) {
            LinkedHashMap<String, String> fields = entry.getValue();

            // 遍历每个字段，按顺序生成 @Mapping 注解
            for (Map.Entry<String, String> field : fields.entrySet()) {
                String fieldName = field.getKey(); // 字段名
                String fieldValue = field.getValue(); // 字段值

                // 生成简单字段的 @Mapping 注解
                if (!isComplexValue(fieldValue)) {
                    classCode.append("    @Mapping(target = \"").append(convertToJavaFieldName(fieldName))
                            .append("\", constant = \"").append(fieldValue).append("\")\n");
                } else {
                    // 对复杂字段生成 @Mapping 注解
                    String methodName = convertToMethodName(fieldName);
                    classCode.append("    @Mapping(target = \"").append(convertToJavaFieldName(fieldName))

                            .append("\", source = \"source\", qualifiedByName = \"").append(methodName).append("\")\n");
                }
            }
        }

        // 生成 map 方法
        String mapClassObjectReturn = className.substring(0, className.lastIndexOf("_Mapper"));
        classCode.append("\n    public abstract ").append(mapClassObjectReturn).append(" map(").append(targetObjectIdentifier).append(" source, @Context APIHeader apiHeader);\n");

        // 生成所有的 @Named 方法
        for (Map.Entry<String, LinkedHashMap<String, String>> entry : keyValueMap.entrySet()) {
            LinkedHashMap<String, String> fields = entry.getValue();

            for (Map.Entry<String, String> field : fields.entrySet()) {
                String fieldName = field.getKey(); // 字段名
                String fieldValue = field.getValue(); // 字段值
                //Conver the fieldValue to the corresponding Java condition logic.
                LogicConverter logicConverter = new LogicConverter();
                String javaConditionLogic = logicConverter.convertLogic(fieldValue);
                System.out.println("key:" + fieldName + " - Java Condition Logic: " + javaConditionLogic);

                // 11. 对复杂字段生成 @Named 方法
                if (isComplexValue(fieldValue)) {
                    String methodName = convertToMethodName(fieldName);
                    generateNamedMethod(classCode, methodName, fieldValue, javaConditionLogic, targetObjectIdentifier);
                }
            }
        }

        // 类定义结束
        classCode.append("}\n");

        // 写入文件
        writeToFile(className, classCode.toString());
    }

    // 检查是否为复杂表达式
    private static boolean isComplexValue(String value) {
        return value != null && (
                value.contains("if") ||
                        value.contains("concat") ||
                        value.contains("esbcustom") ||
                        value.contains("$") ||
                        value.contains("/")
        );
    }

    // 生成 @Named 方法
    private static void generateNamedMethod(StringBuilder classCode, String methodName, String value, String javaConditionLogic, String targetObjectIdentifier) {
        classCode.append("\n    @Named(\"").append(methodName).append("\")\n")
                .append("    public String ").append(methodName).append("(").append(targetObjectIdentifier).append(" source, @Context APIHeader apiHeader) {\n")
                .append("        // Tibco Logic: ").append(value).append("\n")
                .append("        // Java Condition Logic: ").append("\n")
                .append(javaConditionLogic).append("\n")
                .append("        return null;\n")
                .append("    }\n");
    }

    // 将字段名转换为 Java 驼峰命名法字段
    private static String convertToJavaFieldName(String fieldName) {
        return fieldName.replace("-", "_");
    }

    // 将字段名转换为方法名（首字母小写）
    private static String convertToMethodName(String fieldName) {
        String camelCaseName = convertToJavaFieldName(fieldName);
        return "map" + camelCaseName.substring(0, 1).toUpperCase() + camelCaseName.substring(1);
    }

    // 写入文件
    private static void writeToFile(String className, String classCode) {
        try {
            File file = new File("./src/main/java/com/example/tibcomigrationclassgenerator/" + className + ".java");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(classCode);
            writer.close();
//            System.out.println("Mapper class generated: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getTargetObjectIdentifier(Map<String, LinkedHashMap<String, String>> keyValueMap) {
        return TargetObjectIdentifier.findMostFrequentSourceType(keyValueMap);
    }

    public static void main(String[] args) {
        // 示例的 keyValueMap
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

        // 调用生成 Mapper 类的工具方法
        String className = "MLI_0087_Req_Record_Mapper_AutoGenerated";
        String packageName = "com.example.tibcomigrationclassgenerator";
        generateMapperFromHashMap(keyValueMap, className, packageName);
    }
}
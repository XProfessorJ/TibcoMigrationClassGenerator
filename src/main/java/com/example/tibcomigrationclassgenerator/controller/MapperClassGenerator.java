package com.example.tibcomigrationclassgenerator.controller;

import com.example.tibcomigrationclassgenerator.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapperClassGenerator {

    private static final Logger log = LoggerFactory.getLogger(MapperClassGenerator.class);

    // 生成 Mapper 类的通用方法
    public static void generateMapperFromHashMap(Map<String, LinkedList<Tag>> keyValueMap, String className, String packageName) {
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
        for (Map.Entry<String, LinkedList<Tag>> entry : keyValueMap.entrySet()) {
            LinkedList<Tag> fields = entry.getValue();

            // 遍历每个字段，按顺序生成 @Mapping 注解
            for (Tag tag: fields) {
                String fieldName = tag.getTagName(); // 字段名
                String fieldValue = tag.getInsideCondition(); // 字段值

                // 生成简单字段的 @Mapping 注解
                if (!isComplexValue(fieldValue)) {
                    if (fieldValue.contains(targetObjectIdentifier)) {
                        String sourceField = extractFieldFromPath(fieldValue, targetObjectIdentifier);
                        classCode.append("    @Mapping(target = \"").append(convertToJavaFieldNameWithTibcoStyle(fieldName))
                                .append("\", source = \"").append(sourceField).append("\")\n");
                    } else {
                        classCode.append("    @Mapping(target = \"").append(convertToJavaFieldNameWithTibcoStyle(fieldName))
                                .append("\", constant = \"").append(fieldValue).append("\")\n");
                    }
                } else {
                    // 对复杂字段生成 @Mapping 注解
                    String methodName = convertToMethodName(fieldName);
                    classCode.append("    @Mapping(target = \"").append(convertToJavaFieldNameWithTibcoStyle(fieldName))

                            .append("\", source = \"source\", qualifiedByName = \"").append(methodName).append("\")\n");
                }
            }
        }

        // 生成 map 方法
        String mapClassObjectReturn = className.substring(0, className.lastIndexOf("_Mapper"));
        classCode.append("\n    public abstract ").append(mapClassObjectReturn).append(" map(").append(targetObjectIdentifier).append(" source, @Context APIHeader apiHeader);\n");

        // 生成所有的 @Named 方法
        for (Map.Entry<String, LinkedList<Tag>> entry : keyValueMap.entrySet()) {
            LinkedList<Tag> tagLinkedList = entry.getValue();

            for (Tag tag : tagLinkedList) {
                String fieldName = tag.getTagName(); // 字段名
                String fieldValue = tag.getCombileLogic()!=null ? tag.getCombileLogic(): tag.getInsideCondition();// 字段值
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
        if (value != null) {
            // 检查常见的复杂表达式标识符
            boolean isComplex = value.contains("if") ||       // 条件语句
                    value.contains("concat") ||   // 字符串拼接
                    value.contains("esbcustom") ||// 特定自定义函数
                    value.contains("RqHeader");
            // 如果是复杂表达式关键字，则返回true
            if (isComplex) {
                return true;
            }
            // 检查路径模式，例如 $Start/root/pfx12:ListOfBankBranchInqRq/pfx12:BranchOrgCode
            // 正则匹配：$Start/root/pfx\d+:<对象名>/(<字段名>|<对象名>/<字段名>)
            if (value.matches(".*\\$Start/root/pfx\\d+:.+/.*")) {
                return false;
            }
        }
        return false;
    }

    // 生成 @Named 方法
    private static void generateNamedMethod(StringBuilder classCode, String methodName, String value, String javaConditionLogic, String targetObjectIdentifier) {
        classCode.append("\n    @Named(\"").append(methodName).append("\")\n")
                .append("    public String ").append(methodName).append("(").append(targetObjectIdentifier).append(" source, @Context APIHeader apiHeader) {\n")
                .append("        // Tibco Logic: ").append(value.replaceAll("\n","\n//")).append("\n")
                .append("        // Java Condition Logic: ").append("\n")
                .append("        // javaConditionLogic has remaked, Will turn on this feature later").append("\n")
//                .append(javaConditionLogic).append("\n")
//                .append("        return null;\n")
                .append("    }\n");
    }

    // 将字段名转换为 Java 驼峰命名法字段
    private static String convertToJavaFieldNameWithTibcoStyle(String fieldName) {
        return fieldName.replace("-", "_");
    }

    private static String extractFieldFromPath(String path, String sourceObject) {
        // 正则表达式匹配路径中的字段
        String regex = ".*/([^/]+)$";  // 匹配最后的字段名部分
        Pattern pattern = Pattern.compile(regex);

        if (path != null && path.matches(regex)) {
            // 提取路径中的最后字段名（如 BranchOrgCode）
            Matcher matcher = pattern.matcher(path);
            if (matcher.find()) {
                // 获取最后部分字段名，如 BranchOrgCode
                String fieldName = matcher.group(1);

                // 处理路径的各个部分：转换为 Java 风格的命名（首字母小写）
                String[] pathParts = path.split("/");

                // 将路径的每个部分转换为 Java 风格的字段（首字母小写）
                List<String> fieldParts = new ArrayList<>();
                boolean sourceFound = false;

                // 从路径中提取并转换字段部分
                for (String part : pathParts) {
                    if (part.contains(":")) {
                        // 去掉前缀部分，例如 "pfx12:"
                        part = part.split(":")[1];
                    }

                    // 如果是源对象，转换成小写驼峰命名
                    if (part.equals(sourceObject) && !sourceFound) {
                        sourceFound = true;  // 找到sourceObject，不做添加
                    } else if (sourceFound) {
                        // 其他部分字段名也转换为驼峰命名
                        fieldParts.add(toCamelCase(part));
                    }
                }

                // 如果找到了 sourceObject 后的部分，返回它
                return String.join(".", fieldParts);
            }
        }
        return "";
    }

    // 将首字母转换为小写的函数，转换成Java风格
    private static String toCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // 将首字母小写，其余保持不变
        StringBuilder result = new StringBuilder(input.length());
        result.append(Character.toLowerCase(input.charAt(0)));

        // 拼接剩余部分
        for (int i = 1; i < input.length(); i++) {
            result.append(input.charAt(i));
        }

        return result.toString();
    }

    // 将字段名转换为方法名（首字母小写）
    private static String convertToMethodName(String fieldName) {
        String camelCaseName = convertToJavaFieldNameWithTibcoStyle(fieldName);
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

    private static String getTargetObjectIdentifier(Map<String, LinkedList<Tag>> keyValueMap) {
        return TargetObjectIdentifier.findMostFrequentSourceType(keyValueMap);
    }

    public static void main(String[] args) throws Exception {
        // 示例的 keyValueMap
        Map<String, LinkedList<Tag>> keyValueMap = XMLParser.getKeyValueMap();

        // 调用生成 Mapper 类的工具方法
        String className = "MLI_0087_Req_Record_Mapper_AutoGenerated";
        String packageName = "com.example.tibcomigrationclassgenerator";
        generateMapperFromHashMap(keyValueMap, className, packageName);
    }
}
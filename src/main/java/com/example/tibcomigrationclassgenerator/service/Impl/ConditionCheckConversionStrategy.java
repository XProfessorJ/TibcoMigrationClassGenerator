package com.example.tibcomigrationclassgenerator.service.Impl;

import com.example.tibcomigrationclassgenerator.service.LogicConversionStrategy;
import com.example.tibcomigrationclassgenerator.utility.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionCheckConversionStrategy implements LogicConversionStrategy {
    private static final String TIBCO_LOGIC_REGEX = "if\\s*\\((.*?)\\)\\s*then\\s*(.*)\\s*else\\s*if\\s*\\((.*)\\)\\s*and\\s*(.*)\\)\\s*then\\s*(.*)\\s*else\\s*(.*)";
    private static final String TIBCO_PATH_REGEX = "\\$Start/root((?:/pfx12:\\w+)+)";

    public static String parseCondition(String condition) {
        if (condition == null) return "false";

        // 替换 Tibco 函数为 Java 方法
        condition = condition.replace("tib:trim", "trim");

        // 替换 TIBCO 变量引用为 Java 变量引用
        condition = convertTibcoPathToJava(condition);
        return condition;
    }

    private static String parseExpression(String expression) {
        if (expression == null) return "null";

        // 替换 TIBCO 变量引用为 Java 变量引用
        expression = convertTibcoPathToJava(expression);

        return expression.trim();
    }

    public static String convertTibcoPathToJava(String tibcoPath) {
        boolean isEmptyCheck = tibcoPath.contains("string-length") || tibcoPath.contains("string-length") || tibcoPath.contains("exists");
        if (tibcoPath == null || tibcoPath.isEmpty()) {
            throw new IllegalArgumentException("Invalid TIBCO path");
        }

        Pattern pattern = Pattern.compile(TIBCO_PATH_REGEX);
        Matcher matcher = pattern.matcher(tibcoPath);
        if (matcher.find()) {
            String allSegments = matcher.group(1);
            Pattern subPattern = Pattern.compile("/pfx12:\\w+");
            Matcher subMatcher = subPattern.matcher(allSegments);
            StringBuilder javaPath = new StringBuilder();
            List<String> segments = new ArrayList<>();
            if (subMatcher.find()) {
//                javaPath.append(subMatcher.group().replace("/pfx12:", ""));
                javaPath.append("source");
            }
            while (subMatcher.find()) {
                javaPath.append(".get"+subMatcher.group().replace("/pfx12:", "")+"()");
            }
            if(isEmptyCheck) {
                return "Utility.isEmpty("+javaPath.toString()+")";
            }
            return javaPath.toString();
        }
        return tibcoPath;
    }

    @Override
    public String convert(String tibcoLogic) {
        StringBuilder javaCode = new StringBuilder();

        Pattern pattern = Pattern.compile(TIBCO_LOGIC_REGEX, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(tibcoLogic);

        if (matcher.find()) {
            javaCode.append("       if (").append(parseCondition(matcher.group(1))).append(") {\n");
            javaCode.append("           return ").append(parseExpression(matcher.group(2))).append(";\n");

            if (matcher.group(3) != null && matcher.group(4) != null) {
                javaCode.append("       } else if (").append(parseCondition(matcher.group(3)));
                javaCode.append(" && ").append(parseExpression(matcher.group(4))).append("){\n");
                javaCode.append("           return ").append(parseExpression(matcher.group(5))).append(";\n");
            }

            if (matcher.group(5) != null) {
                javaCode.append("       } else {\n");
                javaCode.append("           return ").append(parseExpression(matcher.group(6)).replaceAll("'", "\"")).append(";\n");
            }

            javaCode.append("       }");
        } else {
            return "// Unable to parse TIBCO Logic \n       return null;";
        }

        return javaCode.toString();
    }

}
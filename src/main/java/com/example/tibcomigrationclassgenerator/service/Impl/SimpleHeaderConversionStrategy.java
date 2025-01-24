package com.example.tibcomigrationclassgenerator.service.Impl;

import com.example.tibcomigrationclassgenerator.service.LogicConversionStrategy;

public class SimpleHeaderConversionStrategy implements LogicConversionStrategy {
    @Override
    public String convert(String comment) {
        //Scenario 1: Convert for simple header value like
        // $Start/root/pfx4:RqHeader/pfx4:ClientDetails/pfx4:UserID
        // 去掉路径中的固定部分，例如 "$Start/root/"
        String trimmedPath = comment.replace("$Start/root/", "");

        // 将路径按 "/" 分割
        String[] pathParts = trimmedPath.split("/");

        // 需要跳过的部分
        String[] skipPrefixes = {"pfx4:"}; // 可以扩展为其它前缀

        // 初始化结果字符串
        StringBuilder javaCode = new StringBuilder();

        // 对每个路径部分进行处理
        boolean isFirst = true;
        for (String part : pathParts) {
            // 跳过前缀
            for (String prefix : skipPrefixes) {
                if (part.startsWith(prefix)) {
                    part = part.substring(prefix.length());
                    break;
                }
            }

            if (isFirst) {

                javaCode.append("        return apiHeader");  // 使用传入的对象名称
                isFirst = false;
            } else {
                // 对于后续部分，使用get方法调用
                javaCode.append(".get").append(part.substring(0, 1).toUpperCase()).append(part.substring(1)).append("()");
            }
        }
        return javaCode.append(";").toString();
    }
}

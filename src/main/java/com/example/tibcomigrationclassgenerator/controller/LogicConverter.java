package com.example.tibcomigrationclassgenerator.controller;

import com.example.tibcomigrationclassgenerator.service.Impl.ConditionCheckConversionStrategy;
import com.example.tibcomigrationclassgenerator.service.Impl.DateTimeConversionStrategy;
import com.example.tibcomigrationclassgenerator.service.LogicConversionStrategy;

import java.util.HashMap;
import java.util.Map;

public class LogicConverter {

    private final Map<String, LogicConversionStrategy> strategyMap;

    public LogicConverter() {
        strategyMap = new HashMap<>();
        // 注册不同的策略
        strategyMap.put("DateTime", new DateTimeConversionStrategy());
        strategyMap.put("ConditionCheck", new ConditionCheckConversionStrategy());
        // 可以继续扩展新的转换策略
    }

    public String convertLogic(String comment) {
        // 这里根据注释内容选择不同的转换策略
        // 假设通过注释内容的关键词来判断需要使用的策略
        if (comment.contains("DateTime")) {
            return strategyMap.get("DateTime").convert(comment);
        } else if (comment.contains("ConditionCheck")) {
            return strategyMap.get("ConditionCheck").convert(comment);
        } else {
            // 如果没有匹配到任何策略，可以返回默认逻辑或抛出异常
            return comment;
        }
    }
}
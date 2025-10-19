package com.example.ia.mayaAI.tools;

import com.example.ia.mayaAI.models.Tool;

import java.util.Map;
import java.util.function.BiFunction;

public record ToolDefinition(
        Tool tool,
        BiFunction<Tool, Map<String, Object>, Object> function
) {}

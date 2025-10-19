package com.example.ia.mayaAI.tools;

import com.example.ia.mayaAI.models.Tool;
import com.example.ia.mayaAI.tools.cep.CepTools;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Log4j2
@Service
public class ToolsFactory {

    private final Map<String, ToolFunctionPair> tools = new java.util.HashMap<>();

    public ToolsFactory(CepTools cepTools) {
        cepTools.registerTools(tools);

        log.info("Registered tools: " + tools.keySet());
    }

    public List<Tool> getAllTools() {
        return tools.values().stream()
                .map(ToolFunctionPair::tool)
                .toList();
    }

    public Tool getToolByName(String name) {
        ToolFunctionPair pair = tools.get(name);
        if (pair == null) {
            throw new IllegalArgumentException("Tool not found: " + name);
        }
        return pair.tool();
    }

    public Object executeToolFunction(String name, Map<String, Object> params) {
        ToolFunctionPair pair = tools.get(name);
        if (pair == null) {
            throw new IllegalArgumentException("Tool not found: " + name);
        }
        return pair.function().apply(pair.tool(), params);
    }

    public record ToolFunctionPair(Tool tool, BiFunction<Tool, Map<String, Object>, Object> function) {}
}

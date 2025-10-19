package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.models.Tool;
import com.example.ia.mayaAI.responses.ToolResponse;
import com.example.ia.mayaAI.tools.ToolsFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToolService {

    private final ToolsFactory toolsFactory;

    public ToolService(ToolsFactory toolsFactory) {
        this.toolsFactory = toolsFactory;
    }

    public List<ToolResponse> getAvailableTools() {
        return toolsFactory.getAllTools().stream()
                .map(tool -> ToolResponse.builder()
                        .name(tool.getName())
                        .description(tool.getDescription())
                        .parameters(tool.getParameters())
                        .build())
                .collect(Collectors.toList());
    }

    public ToolResponse getToolByName(String name) {
        Tool tool = toolsFactory.getToolByName(name);
        return ToolResponse.builder()
                .name(tool.getName())
                .description(tool.getDescription())
                .parameters(tool.getParameters())
                .build();
    }
}

package com.example.ia.mayaAI.controllers;

import com.example.ia.mayaAI.models.Tool;
import com.example.ia.mayaAI.responses.ToolResponse;
import com.example.ia.mayaAI.services.ToolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tool")
public class ToolController {

    private final ToolService toolService;

    public ToolController(ToolService toolService) {
        this.toolService = toolService;
    }

    @GetMapping
    public ResponseEntity<List<ToolResponse>> getAvailableTools() {
        return ResponseEntity.ok(toolService.getAvailableTools());
    }

    @GetMapping("/{toolName}")
    public ResponseEntity<ToolResponse> getToolByName(@PathVariable String toolName) {
        return ResponseEntity.ok(toolService.getToolByName(toolName));
    }
}

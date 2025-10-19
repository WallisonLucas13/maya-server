package com.example.ia.mayaAI.responses;

import com.example.ia.mayaAI.models.Tool;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ToolResponse {
    private String name;
    private String description;
    private Tool.Parameters parameters;
}

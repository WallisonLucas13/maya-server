package com.example.ia.mayaAI.tools;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tool {
    private static final String type = "function";
    String name;
    String description;
    Object parameters;
}

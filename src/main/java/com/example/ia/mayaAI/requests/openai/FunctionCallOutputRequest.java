package com.example.ia.mayaAI.requests.openai;

import com.example.ia.mayaAI.models.Tool;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FunctionCallOutputRequest {
    private String model;
    private String type;
    private String id;
    private String call_id;
    private String input;
    private String name;
    public List<Tool> tools;
    private String arguments;
    private String tool_choice;
    private String instructions;
}

package com.example.ia.mayaAI.requests.openai;

import com.example.ia.mayaAI.tools.Tool;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageRequest {
    private String model;
    private String input;
    private String conversation;
    private String instructions;
    public List<Tool> tools;
    private String tool_choice;
}

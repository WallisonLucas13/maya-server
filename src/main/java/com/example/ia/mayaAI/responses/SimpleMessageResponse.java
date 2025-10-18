package com.example.ia.mayaAI.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleMessageResponse {
    private String sessionId;
    private String response;
}

package com.example.ia.mayaAI.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleMessageRequest {
    private String message;
}

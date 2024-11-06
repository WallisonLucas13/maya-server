package com.example.ia.mayaAI.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationResponse {

    private UUID id;
    private String title;
    private String username;
    private MessageResponse lastUserMessage;
    private LocalDateTime createdAt;

}

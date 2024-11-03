package com.example.ia.mayaAI.responses;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.MessageType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Lob
    private String message;

    private LocalDateTime createdAt;
}

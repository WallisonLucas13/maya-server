package com.example.ia.mayaAI.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.ai.chat.messages.MessageType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageModel{

    private UUID conversationId;

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Lob
    private String message;

    private LocalDateTime createdAt;
}

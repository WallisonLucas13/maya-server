package com.example.ia.mayaAI.models;

import com.example.ia.mayaAI.utils.UUIDDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.springframework.ai.chat.messages.MessageType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageModel{

    private String conversationId;

    @JsonProperty("_id")
    private String id;

    private MessageType type;

    private String message;

    private LocalDateTime createdAt;
}

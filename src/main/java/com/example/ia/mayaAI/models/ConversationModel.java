package com.example.ia.mayaAI.models;

import com.example.ia.mayaAI.utils.UUIDDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationModel {

    @JsonProperty("_id")
    private String id;

    private String username;
    private String title;

    private List<MessageModel> messages;

    private LocalDateTime createdAt;
}

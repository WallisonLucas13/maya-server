package com.example.ia.mayaAI.responses;

import com.example.ia.mayaAI.models.MessageModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationModelResponse {

    @JsonProperty("_id")
    private String id;
    private String username;
    private List<MessageModelResponse> messages;
    private Instant createdAt;
}

package com.example.ia.mayaAI.responses;

import com.example.ia.mayaAI.models.MessageModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationResponse {

    private String id;

    private String username;
    private String title;
    private List<MessageModel> messages;

    private Instant createdAt;
}

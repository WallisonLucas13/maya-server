package com.example.ia.mayaAI.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationModel {

    private UUID id;

    private String username;
    private String title;

    private List<MessageModel> messages;

    private LocalDateTime createdAt;
}

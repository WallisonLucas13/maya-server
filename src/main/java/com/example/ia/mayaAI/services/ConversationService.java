package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.clients.OpenAIClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class ConversationService {

    private final OpenAIClient openAIClient;

    public ConversationService(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public String createSessionId(String username) {
        Map<String, Object> response = openAIClient.postConversation();
        return (String) Optional.ofNullable(response.getOrDefault("id", null))
                .orElseThrow(() -> new RuntimeException("Failed to create conversation for user: " + username));
    }
}

package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.clients.OpenAIClient;
import com.example.ia.mayaAI.requests.SimpleMessageRequest;
import com.example.ia.mayaAI.requests.openai.MessageRequest;
import com.example.ia.mayaAI.responses.SimpleMessageResponse;
import com.example.ia.mayaAI.responses.openai.MessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MessageService {

    private final OpenAIClient openAIClient;
    private final ConversationService conversationService;
    private final String SYSTEM_PROMPT;

    public MessageService(
            @Value("${prompts.maya-common}") String systemPrompt,
            OpenAIClient openAIClient,
            ConversationService conversationService) {
        SYSTEM_PROMPT = systemPrompt;
        this.openAIClient = openAIClient;
        this.conversationService = conversationService;
    }

    public SimpleMessageResponse postMessage(String username, SimpleMessageRequest request, String sessionId) {
        MessageRequest openAPIRequest = buildMessageRequest(username, request.getMessage(), sessionId);

        MessageResponse response = openAIClient.postMessage(openAPIRequest);

        return SimpleMessageResponse.builder()
                .sessionId(openAPIRequest.getConversation())
                .response(response.getOutput().get(0).getContent().get(0).getText())
                .build();
    }

    private MessageRequest buildMessageRequest(String username, String message, String sessionId) {
        String conversationId = Optional.ofNullable(sessionId)
                .orElseGet(() -> conversationService.createSessionId(username));

        return MessageRequest.builder()
                .model("gpt-4o")
                .input(message)
                .conversation(conversationId)
                .tool_choice("auto")
                .instructions(SYSTEM_PROMPT)
                .build();
    }
}

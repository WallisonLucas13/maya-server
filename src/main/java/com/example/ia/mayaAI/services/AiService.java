package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.models.MessageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AiService {

    @Autowired
    private OpenAiChatModel aiModel;

    @Autowired
    private PromptService promptService;

    public String callSimpleAi(
            String username,
            MessageModel userMessage,
            List<MessageModel> messagesContext,
            String linksContextResume
    ){
        Prompt prompt = this.promptService
                .simplePromptGenerate(
                        username,
                        userMessage,
                        messagesContext,
                        linksContextResume
                );

        ChatResponse aiResponse = aiModel.call(prompt);
        String aiMessage = aiResponse.getResult().getOutput().getContent();
        log.info("Simple AI Response: {}", aiMessage);
        return aiMessage;
    }
    public String callAIWithFilesResume(
            String username,
            MessageModel userMessage,
            String filesContextResume,
            String linksContextResume
    ){
        Prompt prompt = this.promptService
                .promptGenerate(
                        username,
                        userMessage,
                        filesContextResume,
                        linksContextResume
                );

        ChatResponse aiResponse = aiModel.call(prompt);
        String aiMessage = aiResponse.getResult().getOutput().getContent();
        log.info("AI With Files Response: {}", aiMessage);
        return aiMessage;
    }

    public String callAIByTitleGenerate(
            List<MessageModel> messagesContext
    ){
        Prompt prompt = this.promptService
                        .buildTitlePromptFromConversationHistory(messagesContext);

        ChatResponse aiResponse = aiModel.call(prompt);
        String aiMessage = aiResponse.getResult().getOutput().getContent();
        log.info("AI Response From Title Generation: {}", aiMessage);
        return aiMessage;
    }
}

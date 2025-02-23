package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.converters.MessageConverter;
import com.example.ia.mayaAI.inputs.MessageInput;
import com.example.ia.mayaAI.models.MessageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AiService {

    @Autowired
    private OpenAiChatModel aiModel;

    @Autowired
    private PromptService promptService;

    public MessageModel callAI(
            MessageModel userMessage,
            String validConversationId,
            List<MessageModel> messagesContext,
            String filesContextResume,
            String linksContextResume
    ){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Prompt prompt = this.promptService
                .promptGenerate(
                        username,
                        userMessage,
                        messagesContext,
                        filesContextResume,
                        linksContextResume
                );

        ChatResponse aiResponse = aiModel.call(prompt);
        String aiMessage = aiResponse.getResult().getOutput().getContent();
        log.info("AI Response From Conversation: {}", aiMessage);

        MessageModel aiResponseModel = MessageConverter
                .inputToAiMessage(new MessageInput(aiMessage));
        aiResponseModel.setConversationId(validConversationId);

        return aiResponseModel;
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

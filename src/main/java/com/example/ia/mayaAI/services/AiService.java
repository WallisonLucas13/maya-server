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

import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
public class AiService {

    @Autowired
    private OpenAiChatModel aiModel;

    @Autowired
    private PromptService promptService;

    public MessageModel callAICommon(String validConversationId, List<MessageModel> messages){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Prompt prompt = this.promptService
                .promptGenerate(messages, username);

        ChatResponse aiResponse = aiModel.call(prompt);

        log.info("MayaAI response: {}", aiResponse.getResult().getOutput().getContent());
        MessageModel aiMessage = MessageConverter
                .inputToAiMessage(new MessageInput(aiResponse.getResult().getOutput().getContent()));
        aiMessage.setConversationId(validConversationId);

        return aiMessage;
    }

    public MessageModel callAIWithFiles(
            String validConversationId,
            List<MessageModel> messages,
            String filesContextResume
    ){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Prompt prompt = this.promptService
                .promptWithFilesContextGenerate(messages, username, filesContextResume);

        log.info("Prompt: {}", prompt);
        ChatResponse aiResponse = aiModel.call(prompt);

        log.info("MayaAI response: {}", aiResponse.getResult().getOutput().getContent());
        MessageModel aiMessage = MessageConverter
                .inputToAiMessage(new MessageInput(aiResponse.getResult().getOutput().getContent()));
        aiMessage.setConversationId(validConversationId);

        return aiMessage;
    }

    public MessageModel callAIWithLinks(
            String validConversationId,
            List<MessageModel> messages,
            String linksContextResume
    ){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Prompt prompt = this.promptService
                .promptWithLinksGenerate(messages, username, linksContextResume);

        ChatResponse aiResponse = aiModel.call(prompt);

        log.info("MayaAI with links response: {}", aiResponse.getResult().getOutput().getContent());
        MessageModel aiMessage = MessageConverter
                .inputToAiMessage(new MessageInput(aiResponse.getResult().getOutput().getContent()));
        aiMessage.setConversationId(validConversationId);

        return aiMessage;
    }
}

package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.converters.MessageConverter;
import com.example.ia.mayaAI.inputs.MessageInput;
import com.example.ia.mayaAI.models.ConversationModel;
import com.example.ia.mayaAI.models.MessageModel;
import com.example.ia.mayaAI.repositories.ConversationRepository;
import com.example.ia.mayaAI.repositories.MessageRepository;
import com.example.ia.mayaAI.responses.ConversationResponse;
import com.example.ia.mayaAI.responses.MessageResponse;
import com.example.ia.mayaAI.utils.UuidGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
public class ConversationService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private OpenAiChatModel aiModel;

    @Transactional
    public MessageModel sendMessage(UUID conversationId, MessageInput messageInput){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ConversationModel conversation = findOrCreateConversation(conversationId, username);

        MessageModel userMessage = MessageConverter
                .inputToUserMessage(messageInput);
        userMessage.setConversationId(conversation.getId());

        ConversationModel updatedConversation = updateConversation(conversation, userMessage, username);

        Prompt prompt = PromptService.promptGenerateWithInstructions(updatedConversation);
        log.info("Sending prompt: {}", prompt.getContents());

        ChatResponse aiResponse = aiModel.call(prompt);

        log.info("Received message: {}", aiResponse.getResult().getOutput().getContent());
        MessageModel aiMessageModel = MessageConverter
                .inputToAiMessage(new MessageInput(aiResponse.getResult().getOutput().getContent()));

        aiMessageModel.setConversationId(updatedConversation.getId());

        updateConversation(conversation, aiMessageModel, username);

        return aiMessageModel;
    }

    @Transactional
    public ConversationModel updateConversation(ConversationModel conversation,
                                                MessageModel messageModel,
                                                String username){
        if(messageModel.getType().equals(MessageType.USER)){
            messageRepository.save(messageModel);
            List<MessageModel> messages = conversation.getMessages();
            messages.add(messageModel);
            conversation.setMessages(messages);
            conversation.setUsername(username);
            return conversationRepository.save(conversation);
        }

        String extractedTitle = extractTitleBlock(messageModel.getMessage());
        messageModel.setMessage(removeTitleBlock(messageModel.getMessage()));

        messageRepository.save(messageModel);
        List<MessageModel> messages = conversation.getMessages();
        messages.add(messageModel);
        conversation.setMessages(messages);
        if(!extractedTitle.isBlank()) {
            conversation.setTitle(extractedTitle);
        }
        conversation.setUsername(username);
        return conversationRepository.save(conversation);
    }

    @Transactional
    public ConversationModel getConversationById(UUID conversationId){
        Optional<ConversationModel> conversation = conversationRepository
                .findById(conversationId);

        List<MessageModel> messageModels = conversation.get().getMessages();

        messageModels.sort(Comparator.comparing(MessageModel::getCreatedAt));
        conversation.get().setMessages(messageModels);
        return conversation.get();
    }

    @Transactional
    public List<ConversationResponse> getConversations(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ConversationModel> conversations = conversationRepository
                .findAllByUsername(username);

        List<ConversationResponse> sortedConversations = new ArrayList<>(conversations.stream()
                .map(conversation -> {
                    MessageModel lastMessage = findLastUserMessage(conversation);

                    return ConversationResponse.builder()
                            .id(conversation.getId())
                            .username(conversation.getUsername())
                            .title(conversation.getTitle())
                            .lastUserMessage(MessageResponse.builder()
                                    .id(lastMessage.getId())
                                    .type(lastMessage.getType())
                                    .message(lastMessage.getMessage())
                                    .createdAt(lastMessage.getCreatedAt())
                                    .build())
                            .createdAt(conversation.getCreatedAt())
                            .build();
                })
                .toList());

        sortedConversations
                .sort(Comparator.comparing((ConversationResponse c) -> c.getLastUserMessage()
                        .getCreatedAt()).reversed());

        return sortedConversations;
    }

    private MessageModel findLastUserMessage(ConversationModel conversation){
        return conversation.getMessages().stream()
                .filter(message -> message.getType().equals(MessageType.USER))
                .max(Comparator.comparing(MessageModel::getCreatedAt))
                .orElse(null);
    }

    @Transactional
    private ConversationModel findOrCreateConversation(UUID conversationId, String username){
        if(conversationId == null){
            return createConversation(username);
        }

        Optional<ConversationModel> conversation = conversationRepository
                .findById(conversationId);

        return conversation.get();
    }

    @Transactional
    private ConversationModel createConversation(String username){
        ConversationModel newConversation = new ConversationModel();
        newConversation.setUsername(username);
        newConversation.setId(UuidGenerator.generate());
        newConversation.setMessages(List.of());
        newConversation.setCreatedAt(LocalDateTime.now());
        return conversationRepository.save(newConversation);
    }

    private String extractTitleBlock(String message) {
        String regex = "<([\\s\\S]*?)>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    private String removeTitleBlock(String message) {
        String regex = "<([\\s\\S]*?)>|\"\"";
        return message.replaceAll(regex, "").trim().replaceAll("[\"\\n\\r]+$", "");
    }

}

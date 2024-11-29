package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.converters.MessageConverter;
import com.example.ia.mayaAI.inputs.MessageInput;
import com.example.ia.mayaAI.models.ConversationModel;
import com.example.ia.mayaAI.models.MessageModel;
import com.example.ia.mayaAI.repositories.MongoRepository;
import com.example.ia.mayaAI.repositories.impl.MongoRepositoryImpl;
import com.example.ia.mayaAI.responses.ConversationResponse;
import com.example.ia.mayaAI.responses.MessageResponse;
import com.example.ia.mayaAI.utils.UuidGenerator;
import com.mongodb.client.MongoDatabase;
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

    private final Map<String, MongoRepository> mongoRepositoryMap;
    private static final String CONVERSATION_COLLECTION = "conversation";
    private static final String MESSAGE_COLLECTION = "message";
    private static final String FIND_BY_ID = "_id";
    private static final String FIND_BY_USERNAME = "username";

    @Autowired
    private OpenAiChatModel aiModel;

    @Autowired
    public ConversationService(MongoDatabase mongoDatabase) {
        this.mongoRepositoryMap = Map.of(
                CONVERSATION_COLLECTION, new MongoRepositoryImpl(mongoDatabase, CONVERSATION_COLLECTION),
                MESSAGE_COLLECTION, new MongoRepositoryImpl(mongoDatabase, MESSAGE_COLLECTION)
        );
    }

    public MessageModel sendMessage(String conversationId, MessageInput messageInput){
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

    public ConversationModel updateConversation(ConversationModel conversation,
                                                MessageModel messageModel,
                                                String username){
        if(messageModel.getType().equals(MessageType.USER)){
            mongoRepositoryMap.get(MESSAGE_COLLECTION).save(messageModel);
            List<MessageModel> messages = conversation.getMessages();
            messages.add(messageModel);
            conversation.setMessages(messages);
            conversation.setUsername(username);
            return mongoRepositoryMap.get(CONVERSATION_COLLECTION).save(conversation);
        }

        String extractedTitle = extractTitleBlock(messageModel.getMessage());
        messageModel.setMessage(removeTitleBlock(messageModel.getMessage()));

        mongoRepositoryMap.get(MESSAGE_COLLECTION).save(messageModel);
        List<MessageModel> messages = conversation.getMessages();
        messages.add(messageModel);
        conversation.setMessages(messages);
        if(!extractedTitle.isBlank()) {
            conversation.setTitle(extractedTitle);
        }
        conversation.setUsername(username);
        return mongoRepositoryMap.get(CONVERSATION_COLLECTION).save(conversation);
    }

    public ConversationModel getConversationById(String conversationId){
        Optional<ConversationModel> conversation = mongoRepositoryMap.get("conversation")
                .findBy(FIND_BY_ID, conversationId, ConversationModel.class);

        List<MessageModel> messageModels = conversation.get().getMessages();

        messageModels.sort(Comparator.comparing(MessageModel::getCreatedAt));
        conversation.get().setMessages(messageModels);
        return conversation.get();
    }

    public List<ConversationResponse> getConversations(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ConversationModel> conversations = mongoRepositoryMap.get("conversation")
                .findAllBy(FIND_BY_USERNAME, username, ConversationModel.class);

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
        log.info("Finding last user message for conversation: {}", conversation.getId());
        return conversation.getMessages().stream()
                .filter(message -> message.getType().equals(MessageType.USER))
                .max(Comparator.comparing(MessageModel::getCreatedAt))
                .orElse(null);
    }

    private ConversationModel findOrCreateConversation(String conversationId, String username){
        if(conversationId == null){
            return createConversation(username);
        }

        Optional<ConversationModel> conversation = mongoRepositoryMap.get(CONVERSATION_COLLECTION)
                .findBy(FIND_BY_ID, conversationId, ConversationModel.class);

        return conversation.get();
    }

    private ConversationModel createConversation(String username){
        ConversationModel newConversation = new ConversationModel();
        newConversation.setUsername(username);
        newConversation.setId(UuidGenerator.generate().toString());
        newConversation.setMessages(new ArrayList<>());
        newConversation.setCreatedAt(LocalDateTime.now());
        return mongoRepositoryMap.get(CONVERSATION_COLLECTION).save(newConversation);
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

package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.enums.SortDirection;
import com.example.ia.mayaAI.models.ConversationModel;
import com.example.ia.mayaAI.models.MessageModel;
import com.example.ia.mayaAI.repositories.MongoRepository;
import com.example.ia.mayaAI.repositories.impl.MongoRepositoryImpl;
import com.example.ia.mayaAI.responses.ConversationModelResponse;
import com.example.ia.mayaAI.responses.MessageModelResponse;
import com.example.ia.mayaAI.utils.DateGenerateUtil;
import com.mongodb.client.MongoDatabase;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {

    private final MongoRepository conversationRepository;
    private final MongoRepository messageRepository;

    public ConversationService(MongoDatabase database) {
        this.conversationRepository = new MongoRepositoryImpl(database, "conversation");
        this.messageRepository = new MongoRepositoryImpl(database, "message");
    }

    public List<ConversationModel> getAllConversationsByUser(String username) {
        return conversationRepository.findAllBy("username", username, ConversationModel.class);
    }

    public ConversationModelResponse getConversationById(String conversationId){
        ConversationModel conversation = getConversationModelById(conversationId);
        List<MessageModel> messagesModel = messageRepository.findAllBy(
                "conversationId",
                conversationId,
                MessageModel.class,
                "createdAt",
                SortDirection.ASC
        );

        return ConversationModelResponse.builder()
                .id(conversation.getId())
                .username(conversation.getUsername())
                .messages(convertMessagesToResponses(messagesModel))
                .createdAt(conversation.getCreatedAt())
                .build();
    }

    protected ConversationModel getValidConversation(String username, String conversationId) {
        return conversationRepository.findBy("_id", conversationId, ConversationModel.class)
                .orElseGet(() -> {
                    String id = UUID.randomUUID().toString();
                    ConversationModel conversationModel = ConversationModel.builder()
                            .id(id)
                            .username(username)
                            .createdAt(DateGenerateUtil.now())
                            .build();

                    conversationRepository.save(conversationModel);
                    return conversationModel;
                });
    }

    protected ConversationModel getConversationModelById(String conversationId) {
        return conversationRepository.findBy("_id", conversationId, ConversationModel.class)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found with id: " + conversationId));
    }

    private List<MessageModelResponse> convertMessagesToResponses(List<MessageModel> messagesModel) {
        return messagesModel.stream().map(
                messageModel -> MessageModelResponse.builder()
                        .id(messageModel.getId())
                        .type(messageModel.getType())
                        .text(messageModel.getText())
                        .createdAt(messageModel.getCreatedAt())
                        .build()
        ).toList();
    }
}

package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.models.MessageModel;
import com.example.ia.mayaAI.repositories.MongoRepository;
import com.example.ia.mayaAI.repositories.impl.MongoRepositoryImpl;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class MessageService {

    private final MongoRepository mongoRepository;
    private static final String MESSAGE_COLLECTION = "message";

    @Autowired
    public MessageService(MongoDatabase mongoDatabase) {
        this.mongoRepository = new MongoRepositoryImpl(mongoDatabase, MESSAGE_COLLECTION);
    }

    public MessageModel saveMessage(MessageModel messageModel){
        return mongoRepository.save(messageModel);
    }

    public List<MessageModel> getSortedMessages(String conversationId){
        return mongoRepository
                .findAllBy("conversationId", conversationId, MessageModel.class)
                .stream()
                .sorted(Comparator.comparing(MessageModel::getCreatedAt))
                .toList();
    }

    public MessageModel findLastUserMessage(String conversationId){
        log.info("Finding last user message for conversation: {}", conversationId);
        return this.getSortedMessages(conversationId)
                .stream()
                .filter(message -> message.getType().equals(MessageType.USER))
                .max(Comparator.comparing(MessageModel::getCreatedAt))
                .orElse(null);
    }

}

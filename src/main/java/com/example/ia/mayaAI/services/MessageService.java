package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.enums.DocumentSortDirection;
import com.example.ia.mayaAI.models.MessageModel;
import com.example.ia.mayaAI.repositories.common.MongoRepository;
import com.example.ia.mayaAI.repositories.common.impl.MongoRepositoryImpl;
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
    private static final String SORTED_FIELD = "createdAt";
    private static final String FIND_BY_CONVERSATION_ID = "conversationId";

    @Autowired
    public MessageService(MongoDatabase mongoDatabase) {
        this.mongoRepository = new MongoRepositoryImpl(mongoDatabase, MESSAGE_COLLECTION);
    }

    public void saveMessage(MessageModel messageModel){
        mongoRepository.save(messageModel);
    }

    public List<MessageModel> getSortedMessages(String conversationId){
        return mongoRepository
                .findAllBy(
                        FIND_BY_CONVERSATION_ID,
                        conversationId,
                        MessageModel.class,
                        SORTED_FIELD,
                        DocumentSortDirection.ASC
                );
    }

    public MessageModel findLastUserMessage(String conversationId){
        return this.getSortedMessages(conversationId)
                .stream()
                .filter(message -> message.getType().equals(MessageType.USER))
                .max(Comparator.comparing(MessageModel::getCreatedAt))
                .orElse(null);
    }

    public long deleteMessagesByConversationId(String conversationId){
        return mongoRepository
                .deleteAllBy(FIND_BY_CONVERSATION_ID, conversationId);
    }

}

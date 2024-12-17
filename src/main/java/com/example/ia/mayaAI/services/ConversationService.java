package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.enums.DocumentSortDirection;
import com.example.ia.mayaAI.exceptions.NotFoundConversationException;
import com.example.ia.mayaAI.models.ConversationModel;
import com.example.ia.mayaAI.models.MessageModel;
import com.example.ia.mayaAI.repositories.common.MongoRepository;
import com.example.ia.mayaAI.repositories.common.impl.MongoRepositoryImpl;
import com.example.ia.mayaAI.responses.ConversationPreviewResponse;
import com.example.ia.mayaAI.responses.ConversationResponse;
import com.example.ia.mayaAI.responses.MessageResponse;
import com.example.ia.mayaAI.utils.LinkExtractor;
import com.example.ia.mayaAI.utils.TitleFormatOperations;
import com.example.ia.mayaAI.utils.UuidGenerator;
import com.example.ia.mayaAI.utils.ZonedDateGenerate;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class ConversationService {

    private final MongoRepository mongoRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    private AiService aiService;

    @Autowired
    private LinkFetchService linkFetchService;

    @Autowired
    private IndexService indexService;

    private static final String CONVERSATION_COLLECTION = "conversation";
    private static final String FIND_BY_ID = "_id";
    private static final String FIND_BY_USERNAME = "username";
    private static final String TITLE_FIELD = "title";
    private static final String SORTED_FIELD = "createdAt";
    private static final String UPDATED_FIELD = "updatedAt";

    @Autowired
    private OpenAiChatModel aiModel;

    @Autowired
    public ConversationService(MongoDatabase mongoDatabase) {
        this.mongoRepository = new MongoRepositoryImpl(mongoDatabase, CONVERSATION_COLLECTION);
    }

    public MessageModel sendMessage(String conversationId, MessageModel messageModel){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String validConversationId = this.getValidConversationId(conversationId, username);
        messageModel.setConversationId(validConversationId);

        MessageModel aiResponse = sendMessageToAI(messageModel, validConversationId, "");

        MessageModel clearedAiMessage = this.getTitleAndClearMessage(validConversationId, aiResponse);

        this.messageService.saveMessage(messageModel);
        return this.messageService.saveMessage(clearedAiMessage);
    }

    public MessageModel sendMessageWithFile(String conversationId,
                                            MessageModel messageModel,
                                            List<MultipartFile> files){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String searchResults = indexService.indexAndSearchPdfFile(files, messageModel.getMessage());

        String validConversationId = this.getValidConversationId(conversationId, username);
        messageModel.setConversationId(validConversationId);

        MessageModel aiResponse = sendMessageToAI(
                messageModel,
                validConversationId,
                searchResults
        );

        MessageModel clearedAiMessage = this.getTitleAndClearMessage(validConversationId, aiResponse);

        this.messageService.saveMessage(messageModel);
        return this.messageService.saveMessage(clearedAiMessage);
    }

    public ConversationResponse getConversationById(String conversationId){
        ConversationModel conversation = mongoRepository
                .findBy(FIND_BY_ID, conversationId, ConversationModel.class)
                .orElseThrow(() -> new NotFoundConversationException("Conversa não encontrada!"));

        return this.buildConversationResponse(
                conversation,
                this.messageService.getSortedMessages(conversationId)
        );
    }

    public List<ConversationPreviewResponse> getConversationsPreview(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ConversationModel> conversations = mongoRepository
                .findAllBy(
                        FIND_BY_USERNAME,
                        username,
                        ConversationModel.class,
                        SORTED_FIELD,
                        DocumentSortDirection.DESC
                );
        return conversations.stream()
                .map(conversation -> {
                    MessageModel lastMessage = this.messageService
                            .findLastUserMessage(conversation.getId());
                    return this.buildConversationPreviewResponse(conversation, lastMessage);
                }).sorted(Comparator.comparing(ConversationPreviewResponse::getUpdatedAt).reversed())
                .toList();
    }

    private MessageModel getTitleAndClearMessage(String validConversationId,
                                                 MessageModel aiMessageModel){
        String title = TitleFormatOperations
                .extractTitleBlock(aiMessageModel.getMessage());

        if(!title.isBlank()){
            this.updateConversationTitle(validConversationId, title);
        }

        String clearedMessage = TitleFormatOperations
                .clearTitleBlock(aiMessageModel.getMessage());
        aiMessageModel.setMessage(clearedMessage);
        return aiMessageModel;
    }

    private void updateConversationTitle(String conversationId, String title){
        mongoRepository.update(conversationId, TITLE_FIELD, title);
        mongoRepository.update(conversationId, UPDATED_FIELD, ZonedDateGenerate.generate());
    }

    private String getValidConversationId(String conversationId, String username){
        Optional<ConversationModel> conversation = mongoRepository
                .findBy(FIND_BY_ID, conversationId, ConversationModel.class);

        return conversation.map(ConversationModel::getId)
                .orElseGet(() -> createConversation(username).getId());
    }

    private ConversationModel createConversation(String username){
        ConversationModel newConversation = new ConversationModel();
        newConversation.setUsername(username);
        newConversation.setId(UuidGenerator.generate().toString());
        newConversation.setCreatedAt(ZonedDateGenerate.generate());
        newConversation.setUpdatedAt(ZonedDateGenerate.generate());
        return mongoRepository.save(newConversation);
    }

    private ConversationResponse buildConversationResponse(ConversationModel conversation,
                                                           List<MessageModel> messages){
        return ConversationResponse.builder()
                .id(conversation.getId())
                .username(conversation.getUsername())
                .title(conversation.getTitle())
                .messages(messages)
                .createdAt(conversation.getCreatedAt())
                .build();
    }

    private ConversationPreviewResponse buildConversationPreviewResponse(
            ConversationModel conversation,
            MessageModel lastMessage
    ){
        return ConversationPreviewResponse.builder()
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
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    private MessageModel sendMessageToAI(
            MessageModel messageModel,
            String validConversationId,
            String filesContextResume
    ){
        var extractedLinks = LinkExtractor.extractLinks(messageModel.getMessage());

        return this.aiService
                .callAI(
                        messageModel,
                        validConversationId,
                        this.messageService.getSortedMessages(validConversationId),
                        filesContextResume,
                        extractedLinks.isEmpty() ? "" : this.getLinksContextResume(extractedLinks, messageModel.getMessage())
                );
    }

    private String getLinksContextResume(List<String> links, String messageSearch){
        var contentsLinks = links.stream().map(link -> {
            return this.linkFetchService.fetchContent(link);
        }).toList();

        return indexService.indexAndSearchWebPage(contentsLinks, messageSearch);
    }
}

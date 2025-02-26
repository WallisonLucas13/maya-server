package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.enums.DocumentSortDirection;
import com.example.ia.mayaAI.exceptions.NotFoundConversationException;
import com.example.ia.mayaAI.inputs.MessageInput;
import com.example.ia.mayaAI.models.ConversationModel;
import com.example.ia.mayaAI.models.MessageModel;
import com.example.ia.mayaAI.repositories.common.MongoRepository;
import com.example.ia.mayaAI.repositories.common.impl.MongoRepositoryImpl;
import com.example.ia.mayaAI.responses.preview.ConversationPreviewResponse;
import com.example.ia.mayaAI.responses.preview.MessagePreviewResponse;
import com.example.ia.mayaAI.responses.principal.ConversationResponse;
import com.example.ia.mayaAI.responses.principal.MessageResponse;
import com.example.ia.mayaAI.utils.LinkExtractor;
import com.example.ia.mayaAI.utils.UuidGenerator;
import com.example.ia.mayaAI.utils.ZonedDateGenerate;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public ConversationService(MongoDatabase mongoDatabase) {
        this.mongoRepository = new MongoRepositoryImpl(mongoDatabase, CONVERSATION_COLLECTION);
    }

    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    public MessageResponse sendMessage(String conversationId, MessageInput messageInput){
        String username = this.getUsername();
        String validConversationId = this.getValidConversationId(conversationId, username);

        MessageModel userMessageModel = MessageModel
                .buildModel(
                        messageInput.message(),
                        MessageType.USER,
                        validConversationId
                );

        String aiResponse = aiService.callSimpleAi(
                username,
                userMessageModel,
                this.messageService.getSortedMessages(validConversationId),
                this.getLinksContextResumed(userMessageModel)
        );
        MessageModel aiMessageModel = MessageModel.buildModel(
                        aiResponse,
                        MessageType.SYSTEM,
                        validConversationId
        );

        this.messageService.saveMessage(userMessageModel);
        this.messageService.saveMessage(aiMessageModel);

        this.updateConversationTitle(validConversationId);
        return this.buildMessageResponse(aiResponse, aiMessageModel);
    }

    public MessageResponse sendMessageWithFile(String conversationId,
                                            MessageInput messageInput,
                                            List<MultipartFile> files){
        String username = this.getUsername();
        String validConversationId = this.getValidConversationId(conversationId, username);

        MessageModel userMessageModel = MessageModel
                .buildModel(
                        messageInput.message(),
                        MessageType.USER,
                        validConversationId
                );

        String filesResumed = getIndexedFilesResumed(files, userMessageModel.getMessage());
        this.setMessageModelFiles(userMessageModel, files);

        String aiResponse = aiService.callAIWithFilesResume(
                username,
                userMessageModel,
                filesResumed,
                this.getLinksContextResumed(userMessageModel)
        );
        MessageModel aiMessageModel = MessageModel.buildModel(
                        aiResponse,
                        MessageType.SYSTEM,
                        validConversationId
        );

        this.setMessageModelFiles(aiMessageModel, files);
        this.messageService.saveMessage(userMessageModel);
        this.messageService.saveMessage(aiMessageModel);

        this.updateConversationTitle(validConversationId);
        return this.buildMessageResponse(aiResponse, aiMessageModel);
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
        String username = this.getUsername();
        List<ConversationModel> conversations = getConversationsByUsername(username);

        return conversations.parallelStream()
                .map(conversation -> {
                    MessageModel lastMessage = this.messageService
                            .findLastUserMessage(conversation.getId());
                    return this.buildConversationPreviewResponse(conversation, lastMessage);
                })
                .sorted(Comparator.comparing(ConversationPreviewResponse::updatedAt).reversed())
                .toList();
    }

    public long deleteConversationsByUsername(){
        String username = this.getUsername();
        List<ConversationModel> conversations = this.getConversationsByUsername(username);

        long totalMessagesDeleted = conversations.parallelStream()
                .mapToLong(i -> {
                    return this.messageService.deleteMessagesByConversationId(i.getId());
                })
                .sum();

        log.info("Total messages deleted: {}", totalMessagesDeleted);

        long totalConversationsDeleted = mongoRepository
                .deleteAllBy(FIND_BY_USERNAME, username);

        log.info("Total conversations deleted: {}", totalConversationsDeleted);
        return totalConversationsDeleted;
    }

    private List<ConversationModel> getConversationsByUsername(String username){
        return mongoRepository
                .findAllBy(
                        FIND_BY_USERNAME,
                        username,
                        ConversationModel.class,
                        SORTED_FIELD,
                        DocumentSortDirection.DESC
                );
    }

    private void setMessageModelFiles(MessageModel messageModel, List<MultipartFile> files){
        files.forEach(file -> {
            String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            messageModel.addFilename(filename);
        });
    }

    private void updateConversationTitle(String conversationId){
        List<MessageModel> messages = this.messageService.getSortedMessages(conversationId);
        String title = aiService.callAIByTitleGenerate(messages);

        if(!title.isBlank()){
            mongoRepository.update(conversationId, TITLE_FIELD, title);
            mongoRepository.update(conversationId, UPDATED_FIELD, ZonedDateGenerate.generate());
        }
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

    private MessageResponse buildMessageResponse(String message, MessageModel messageModel){
        return new MessageResponse(
                messageModel.getId(),
                messageModel.getConversationId(),
                messageModel.getType(),
                message,
                messageModel.getFiles(),
                messageModel.getCreatedAt()
        );
    }

    private ConversationResponse buildConversationResponse(ConversationModel conversation,
                                                           List<MessageModel> messages){
        return new ConversationResponse(
                conversation.getId(),
                conversation.getUsername(),
                conversation.getTitle(),
                messages,
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }

    private ConversationPreviewResponse buildConversationPreviewResponse(
            ConversationModel conversation,
            MessageModel lastMessage
    ){
        return new ConversationPreviewResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getUsername(),
                new MessagePreviewResponse(
                        lastMessage.getId(),
                        lastMessage.getType(),
                        lastMessage.getMessage(),
                        lastMessage.getCreatedAt()
                ),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }

    private String getLinksContextResumed(MessageModel messageModel){
        var extractedLinks = LinkExtractor.extractLinks(messageModel.getMessage());
        if(extractedLinks.isEmpty()){
            return "";
        }

        var contentsLinks = extractedLinks.stream().map(link -> {
            return this.linkFetchService.fetchContent(link);
        }).toList();

        return indexService.applyWebPageIndex(contentsLinks, messageModel.getMessage());
    }

    private String getIndexedFilesResumed(List<MultipartFile> files, String messageSearch){
        return files.stream()
                .map(file -> {
                    String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                    return filename.toUpperCase() + ":\n" + indexService
                            .applyFileIndex(file, messageSearch);
                })
                .collect(Collectors.joining(System.lineSeparator()));
    }
}

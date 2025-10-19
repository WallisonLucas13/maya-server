package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.clients.OpenAIClient;
import com.example.ia.mayaAI.enums.SortDirection;
import com.example.ia.mayaAI.models.ConversationModel;
import com.example.ia.mayaAI.models.MessageModel;
import com.example.ia.mayaAI.repositories.MongoRepository;
import com.example.ia.mayaAI.repositories.impl.MongoRepositoryImpl;
import com.example.ia.mayaAI.requests.SimpleMessageRequest;
import com.example.ia.mayaAI.requests.openai.FunctionCallOutputRequest;
import com.example.ia.mayaAI.requests.openai.MessageRequest;
import com.example.ia.mayaAI.responses.SimpleMessageResponse;
import com.example.ia.mayaAI.responses.openai.MessageResponse;
import com.example.ia.mayaAI.responses.openai.MessageResponse.OutputResponse;
import com.example.ia.mayaAI.tools.ToolsFactory;
import com.example.ia.mayaAI.utils.DateGenerateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
public class MessageService {

    private final OpenAIClient openAIClient;
    private final ConversationService conversationService;
    private final ToolsFactory toolsFactory;
    private final ObjectMapper objectMapper;
    private final String SYSTEM_PROMPT;
    private final String TOOL_PROMPT;
    private final MongoRepository repository;

    public MessageService(
            @Value("${prompts.maya-common}") String systemPrompt,
            @Value("${prompts.tool-output}") String toolPrompt,
            OpenAIClient openAIClient,
            ConversationService conversationService,
            ToolsFactory toolsFactory,
            ObjectMapper objectMapper,
            MongoDatabase database) {
        SYSTEM_PROMPT = systemPrompt;
        TOOL_PROMPT = toolPrompt;
        this.openAIClient = openAIClient;
        this.conversationService = conversationService;
        this.toolsFactory = toolsFactory;
        this.objectMapper = objectMapper;
        this.repository = new MongoRepositoryImpl(database, "message");
    }

    public SimpleMessageResponse postMessage(String username, SimpleMessageRequest request, String conversationId){
        ConversationModel validConversationModel = conversationService
                .getValidConversation(username, conversationId);
        buildAndSaveMessage(validConversationModel.getId(), "USER", request.getMessage());

        MessageRequest openAPIRequest = buildMessageRequest(validConversationModel.getId());
        List<FunctionCallOutputRequest> functionsCallHistory = new ArrayList<>();

        FunctionCallOutputRequest userRequest = FunctionCallOutputRequest.builder()
                .model("gpt-4.1")
                .input(request.getMessage())
                .build();
        functionsCallHistory.add(userRequest);

        log.info("Enviando mensagem do usuário {} para OpenAI", username);
        MessageResponse response = openAIClient.postMessage(openAPIRequest);

        while(response.getOutput().stream().anyMatch(output -> output.getType().equals("function_call"))){
            response = postFunctionCallOutput(response, functionsCallHistory);
        }

        String finalResponse = response.getOutput().get(0).getContent().get(0).getText();

        buildAndSaveMessage(validConversationModel.getId(), "SYSTEM", finalResponse);
        return SimpleMessageResponse.builder()
                .conversationId(validConversationModel.getId())
                .response(finalResponse)
                .build();
    }

    private MessageRequest buildMessageRequest(String conversationId) {
        String messagesContextStr = getMessagesContextStr(conversationId);

        return MessageRequest.builder()
                .model("gpt-4.1")
                .input(messagesContextStr)
                .tools(toolsFactory.getAllTools())
                .tool_choice("auto")
                .instructions(SYSTEM_PROMPT)
                .build();
    }

    private MessageResponse postFunctionCallOutput(
            MessageResponse response,
            List<FunctionCallOutputRequest> functionsCallHistory
    ){
        processFunctionsCall(response.getOutput(), functionsCallHistory);

        try {
            String callOutputStr = objectMapper.writeValueAsString(functionsCallHistory);
            FunctionCallOutputRequest functionOutputRequest = FunctionCallOutputRequest.builder()
                    .model("gpt-4.1")
                    .input(callOutputStr)
                    .instructions(TOOL_PROMPT)
                    .tools(toolsFactory.getAllTools())
                    .tool_choice("auto")
                    .build();

            log.info("Send function call output to OpenAI: {}", callOutputStr);
            return openAIClient.postMessage(functionOutputRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void processFunctionsCall(
            List<OutputResponse> outputs,
            List<FunctionCallOutputRequest> functionsCallHistory
    ){

        for(OutputResponse output : outputs){
            if(!output.getType().equals("function_call")){
                FunctionCallOutputRequest functionCall = FunctionCallOutputRequest.builder()
                        .type("message")
                        .input(output.getContent().get(0).getText())
                        .build();

                functionsCallHistory.add(functionCall);
                continue;
            }

            log.info("function call: name={}, arguments={}", output.getName(), output.getArguments());
            FunctionCallOutputRequest functionCall = FunctionCallOutputRequest.builder()
                    .type("function_call")
                    .id(output.getId())
                    .call_id(output.getCall_id())
                    .name(output.getName())
                    .arguments(output.getArguments())
                    .build();

            functionsCallHistory.add(functionCall);

            String execResponse = executeFunctionCall(output);

            FunctionCallOutputRequest functionCallOutput = FunctionCallOutputRequest.builder()
                    .type("function_call_output")
                    .model("gpt-4.1")
                    .input(execResponse)
                    .build();

            log.info("function call output: name={}, arguments={}", output.getName(), output.getArguments());
            functionsCallHistory.add(functionCallOutput);
        }
    }

    private String executeFunctionCall(OutputResponse output){
        String toolName = output.getName();
        var parameters = output.getArgumentsAsMap(objectMapper);

        Object res = toolsFactory.executeToolFunction(toolName, parameters);
        try {
            return objectMapper.writeValueAsString(res);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildAndSaveMessage(String conversationId, String type, String text){
        MessageModel messageModel = MessageModel.builder()
                .id(UUID.randomUUID().toString())
                .conversationId(conversationId)
                .type(type)
                .text(text)
                .createdAt(DateGenerateUtil.now())
                .build();

        repository.save(messageModel);
    }

    private String getMessagesContextStr(String conversationId) {
        List<MessageModel> messages = repository.findAllBy(
                "conversationId",
                conversationId,
                MessageModel.class,
                "createdAt",
                SortDirection.DESC
        ).stream()
                .limit(5)
                .sorted((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()))
                .toList();

        try {
            return objectMapper.writeValueAsString(messages);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

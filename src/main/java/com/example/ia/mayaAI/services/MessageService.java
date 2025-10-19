package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.clients.OpenAIClient;
import com.example.ia.mayaAI.requests.SimpleMessageRequest;
import com.example.ia.mayaAI.requests.openai.FunctionCallOutputRequest;
import com.example.ia.mayaAI.requests.openai.MessageRequest;
import com.example.ia.mayaAI.responses.SimpleMessageResponse;
import com.example.ia.mayaAI.responses.openai.MessageResponse;
import com.example.ia.mayaAI.tools.cep.CepToolsFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class MessageService {

    private final OpenAIClient openAIClient;
    private final ConversationService conversationService;
    private final CepToolsFactory cepToolsFactory;
    private final ObjectMapper objectMapper;
    private final String SYSTEM_PROMPT;

    public MessageService(
            @Value("${prompts.maya-common}") String systemPrompt,
            OpenAIClient openAIClient,
            ConversationService conversationService,
            CepToolsFactory cepToolsFactory,
            ObjectMapper objectMapper) {
        SYSTEM_PROMPT = systemPrompt;
        this.openAIClient = openAIClient;
        this.conversationService = conversationService;
        this.cepToolsFactory = cepToolsFactory;
        this.objectMapper = objectMapper;
    }

    public SimpleMessageResponse postMessage(String username, SimpleMessageRequest request, String sessionId){
        MessageRequest openAPIRequest = buildMessageRequest(username, request.getMessage(), sessionId);
        MessageResponse response = openAIClient.postMessage(openAPIRequest);
        String responseType = response.getOutput().get(0).getType();

        while(responseType.equals("function_call")){
            response = postFunctionCallOutput(request, response);
            responseType = response.getOutput().get(0).getType();
        }

        return SimpleMessageResponse.builder()
                .sessionId(openAPIRequest.getConversation())
                .response(response.getOutput().get(0).getContent().get(0).getText())
                .build();
    }

    private MessageRequest buildMessageRequest(String username, String message, String sessionId) {
        String conversationId = Optional.ofNullable(sessionId)
                .orElseGet(() -> conversationService.createSessionId(username));

        return MessageRequest.builder()
                .model("gpt-4.1")
                .input(message)
                .conversation(conversationId)
                .tools(cepToolsFactory.getAllTools())
                .tool_choice("auto")
                .instructions(SYSTEM_PROMPT)
                .build();
    }

    private String processFunctionCall(MessageResponse response){
        String toolName = response.getOutput().get(0).getName();
        var parameters = response.getOutput().get(0).getArgumentsAsMap(objectMapper);

        log.info("Processando chamada de função para a ferramenta: {} com parâmetros: {}", toolName, parameters);
        Object res = cepToolsFactory.executeToolFunction(toolName, parameters);
        try {
            return objectMapper.writeValueAsString(res);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private MessageResponse postFunctionCallOutput(SimpleMessageRequest request, MessageResponse response){
        String output = processFunctionCall(response);

        FunctionCallOutputRequest initialRequest = FunctionCallOutputRequest.builder()
                .model("gpt-4.1")
                .input(request.getMessage())
                .build();

        FunctionCallOutputRequest functionCall = FunctionCallOutputRequest.builder()
                .type("function_call")
                .id(response.getOutput().get(0).getId())
                .call_id(response.getOutput().get(0).getCall_id())
                .name(response.getOutput().get(0).getName())
                .arguments(response.getOutput().get(0).getArguments())
                .build();

        FunctionCallOutputRequest callOutput = FunctionCallOutputRequest.builder()
                .model("gpt-4.1")
                .input(output)
                .build();

        List<FunctionCallOutputRequest> callHistory = List.of(initialRequest, functionCall, callOutput);

        try {
            String callOutputStr = objectMapper.writeValueAsString(callHistory);
            callOutput.setInput(callOutputStr);

            return openAIClient.postMessage(callOutput);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.example.ia.mayaAI.clients;

import com.example.ia.mayaAI.configs.OpenAIClientConfig;
import com.example.ia.mayaAI.requests.openai.MessageRequest;
import com.example.ia.mayaAI.responses.openai.MessageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
        name = "openAIClient",
        url = "${spring.ai.openai.api-base-url}",
        configuration = OpenAIClientConfig.class
)
public interface OpenAIClient {

    @PostMapping("/conversations")
    Map<String, Object> postConversation();

    @PostMapping("/responses")
    MessageResponse postMessage(@RequestBody MessageRequest messageRequest);
}

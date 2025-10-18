package com.example.ia.mayaAI.configs;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor(@Value("${spring.ai.openai.api-key}") String apiKey) {
        return requestTemplate -> requestTemplate.header("Authorization", "Bearer " + apiKey);
    }
}

package com.example.ia.mayaAI.responses.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {
    private String id;
    private List<OutputResponse> output;

    @Data
    @Builder
    @JsonIgnoreProperties
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OutputResponse {
        private String type;
        private String id;
        private String call_id;
        private String name;
        private String arguments;
        private String status;
        private String role;
        private List<Content> content;

        public Map<String, Object> getArgumentsAsMap(ObjectMapper objectMapper) {
            try {
                return objectMapper.readValue(arguments, Map.class);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao desserializar argumentos", e);
            }
        }

        @Data
        @Builder
        @JsonIgnoreProperties
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Content {
            private String type;
            private String text;
            private List<Object> annotations;
        }
    }
}

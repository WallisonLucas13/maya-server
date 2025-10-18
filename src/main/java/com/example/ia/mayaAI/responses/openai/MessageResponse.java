package com.example.ia.mayaAI.responses.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {
    private List<OutputResponse> output;

    @Data
    @JsonIgnoreProperties
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OutputResponse {
        private String type;
        private String id;
        private String status;
        private String role;
        private List<Content> content;

        @Data
        @JsonIgnoreProperties
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Content {
            private String type;
            private String text;
            private List<Object> annotations;
        }
    }
}

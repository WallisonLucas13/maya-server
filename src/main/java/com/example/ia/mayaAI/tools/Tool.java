package com.example.ia.mayaAI.tools;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Tool {
    private final String type = "function";
    private String name;
    private String description;
    private Parameters parameters;

    @Data
    @Builder
    public static class Parameters {
        private final String type = "object";
        private Map<String, Property> properties;
        private String[] required;

        @Data
        @Builder
        public static class Property {
            private String type;
            private String description;
        }
    }
}

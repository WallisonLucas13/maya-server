package com.example.ia.mayaAI.outputs.insights;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TotalMessagesOutput {

    @JsonProperty("_id")
    private TotalMessagesDetails totalMessagesDetails;

    private int totalMessages;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TotalMessagesDetails{
        private int year;
        private int month;
        private int day;
    }
}

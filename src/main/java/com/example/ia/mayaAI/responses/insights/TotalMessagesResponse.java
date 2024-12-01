package com.example.ia.mayaAI.responses.insights;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TotalMessagesResponse {
    private int year;
    private int month;
    private int day;
    private int totalMessages;
}

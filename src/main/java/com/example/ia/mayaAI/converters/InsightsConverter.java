package com.example.ia.mayaAI.converters;

import com.example.ia.mayaAI.outputs.insights.TotalMessagesOutput;
import com.example.ia.mayaAI.responses.insights.TotalMessagesResponse;

public class InsightsConverter {

    public static TotalMessagesResponse convertToTotalMessagesResponse(TotalMessagesOutput totalMessagesOutput){
        return TotalMessagesResponse.builder()
                .year(totalMessagesOutput.getTotalMessagesDetails().getYear())
                .month(totalMessagesOutput.getTotalMessagesDetails().getMonth())
                .day(totalMessagesOutput.getTotalMessagesDetails().getDay())
                .totalMessages(totalMessagesOutput.getTotalMessages())
                .build();
    }
}

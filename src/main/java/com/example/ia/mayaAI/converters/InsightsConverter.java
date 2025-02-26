package com.example.ia.mayaAI.converters;

import com.example.ia.mayaAI.outputs.insights.TotalMessagesOutput;
import com.example.ia.mayaAI.responses.insights.TotalMessagesResponse;

public class InsightsConverter {

    public static TotalMessagesResponse convertToTotalMessagesResponse(TotalMessagesOutput totalMessagesOutput){
        return new TotalMessagesResponse(
                totalMessagesOutput.getTotalMessagesDetails().getYear(),
                totalMessagesOutput.getTotalMessagesDetails().getMonth(),
                totalMessagesOutput.getTotalMessagesDetails().getDay(),
                totalMessagesOutput.getTotalMessages()
        );
    }
}

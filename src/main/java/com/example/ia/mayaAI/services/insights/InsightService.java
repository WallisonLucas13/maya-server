package com.example.ia.mayaAI.services.insights;

import com.example.ia.mayaAI.converters.InsightsConverter;
import com.example.ia.mayaAI.outputs.insights.TotalMessagesOutput;
import com.example.ia.mayaAI.repositories.aggregations.AggregationOperation;
import com.example.ia.mayaAI.repositories.aggregations.impl.AddFieldsOperationStrategy;
import com.example.ia.mayaAI.repositories.aggregations.impl.GroupOperationStrategy;
import com.example.ia.mayaAI.repositories.aggregations.impl.MatchOperationStrategy;
import com.example.ia.mayaAI.repositories.aggregations.impl.SortOperationStrategy;
import com.example.ia.mayaAI.responses.insights.TotalMessagesResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Log4j2
@Service
public class InsightService {

    private final Map<String, MongoCollection<Document>> collectionMap;
    private ObjectMapper objectMapper;
    private static final String CONVERSATION_COLLECTION = "conversation";
    private static final String MESSAGE_COLLECTION = "message";

    @Autowired
    public InsightService(MongoDatabase mongoDatabase) {
        this.collectionMap = Map.of(
                CONVERSATION_COLLECTION, mongoDatabase.getCollection(CONVERSATION_COLLECTION),
                MESSAGE_COLLECTION, mongoDatabase.getCollection(MESSAGE_COLLECTION)
        );

        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
    }

    public List<TotalMessagesResponse> getTotalMessagesByUsers(){
        final String commonOperationsField = "createdAtDate";
        final String commonOperationsValue = "$createdAtDate";

        Map<String, Document> fieldsGroupMap = Map.of(
                "year", new Document("$year", commonOperationsValue),
                "month", new Document("$month", commonOperationsValue),
                "day", new Document("$dayOfMonth", commonOperationsValue)
        );

        LinkedHashMap<String, Long> fieldsSortMap = new LinkedHashMap<>();
        fieldsSortMap.put("_id.year", 1L);
        fieldsSortMap.put("_id.month", 1L);
        fieldsSortMap.put("_id.day", 1L);

        var pipeline = Stream.of(
                        new MatchOperationStrategy("type", MessageType.USER),
                        new AddFieldsOperationStrategy(commonOperationsField),
                        new GroupOperationStrategy(fieldsGroupMap, "totalMessages", "$sum"),
                        new SortOperationStrategy(fieldsSortMap)
                ).map(AggregationOperation::toDocument)
                .toList();

        log.info("Pipeline: {}", pipeline);
        return collectionMap.get(MESSAGE_COLLECTION).aggregate(pipeline)
                .map(document -> {
                    log.info("Document: {}", document);
                    return objectMapper.convertValue(document, TotalMessagesOutput.class);
                })
                .map(InsightsConverter::convertToTotalMessagesResponse)
                .into(new ArrayList<>());
    }
}

package com.example.ia.mayaAI.repositories.aggregations.impl;

import com.example.ia.mayaAI.repositories.aggregations.AggregationOperation;
import org.bson.Document;

import java.util.Map;

public class GroupOperationStrategy implements AggregationOperation {

    private final Map<String, Document> fieldsMap;
    private final String resultName;

    private final String operation;

    public GroupOperationStrategy(
            Map<String, Document> fieldsMap,
            String resultName,
            String operation
    ) {
        this.fieldsMap = fieldsMap;
        this.resultName = resultName;
        this.operation = operation;
    }

    @Override
    public Document toDocument() {
        Document idDocument = new Document();
        for (Map.Entry<String, Document> entry : fieldsMap.entrySet()) {
            idDocument.append(entry.getKey(), entry.getValue());
        }

        return new Document("$group", new Document("_id", idDocument)
                .append(resultName, new Document(operation, 1L)));
    }
}

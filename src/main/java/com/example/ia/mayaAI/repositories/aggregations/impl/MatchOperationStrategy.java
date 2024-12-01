package com.example.ia.mayaAI.repositories.aggregations.impl;

import com.example.ia.mayaAI.repositories.aggregations.AggregationOperation;
import org.bson.Document;

public class MatchOperationStrategy implements AggregationOperation {

    private final String field;
    private final Object value;

    public MatchOperationStrategy(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public Document toDocument() {
        return new Document("$match", new Document(field, value));
    }
}

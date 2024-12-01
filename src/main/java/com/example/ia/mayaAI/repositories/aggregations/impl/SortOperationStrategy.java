package com.example.ia.mayaAI.repositories.aggregations.impl;

import com.example.ia.mayaAI.repositories.aggregations.AggregationOperation;
import org.bson.Document;

public class SortOperationStrategy implements AggregationOperation {
    private final String field;
    private final Long order;

    public SortOperationStrategy(String field, Long order) {
        this.field = field;
        this.order = order;
    }

    @Override
    public Document toDocument() {
        return new Document("$sort", new Document(field, order));
    }
}

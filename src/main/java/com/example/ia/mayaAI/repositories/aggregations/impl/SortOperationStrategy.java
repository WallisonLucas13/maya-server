package com.example.ia.mayaAI.repositories.aggregations.impl;

import com.example.ia.mayaAI.repositories.aggregations.AggregationOperation;
import org.bson.Document;

import java.util.LinkedHashMap;

public class SortOperationStrategy implements AggregationOperation {
    private final LinkedHashMap<String, Long> sortDetailsMap;

    public SortOperationStrategy(LinkedHashMap<String, Long> sortDetailsMap) {
        this.sortDetailsMap = sortDetailsMap;
    }

    @Override
    public Document toDocument() {
        return new Document("$sort", new Document(sortDetailsMap));
    }
}

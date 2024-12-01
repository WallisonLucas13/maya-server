package com.example.ia.mayaAI.repositories.aggregations.impl;

import com.example.ia.mayaAI.repositories.aggregations.AggregationOperation;
import org.bson.Document;

import java.util.Arrays;

public class AddFieldsOperationStrategy implements AggregationOperation {

    private final String field;

    public AddFieldsOperationStrategy(String field) {
        this.field = field;
    }

    @Override
    public Document toDocument() {
        return new Document("$addFields", new Document(field, new Document("$dateAdd", new Document()
                .append("startDate", new Document("$toDate", new Document("$multiply", Arrays.asList("$createdAt", 1000L))))
                .append("unit", "hour")
                .append("amount", -3L))));
    }

}

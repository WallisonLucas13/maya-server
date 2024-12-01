package com.example.ia.mayaAI.repositories.aggregations;

import org.bson.Document;

public interface AggregationOperation {
    Document toDocument();
}

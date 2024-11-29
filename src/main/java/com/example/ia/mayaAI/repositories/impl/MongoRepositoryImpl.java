package com.example.ia.mayaAI.repositories.impl;

import com.example.ia.mayaAI.configs.MongoConfig;
import com.example.ia.mayaAI.repositories.MongoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MongoRepositoryImpl implements MongoRepository {

    private final MongoCollection<Document> collection;
    private final ObjectMapper objectMapper;

    @Autowired
    public MongoRepositoryImpl(MongoDatabase mongoDatabase, String collectionName) {
        this.collection = mongoDatabase.getCollection(collectionName);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public <T> T save(T entity) {
        collection.insertOne(Document.parse(entity.toString()));
        return entity;
    }

    @Override
    public <R, T> Optional<T> findBy(String key, R value, Class<T> responseType) {
        Bson filter = Filters.and(Filters.eq(key, value));
        return Optional.ofNullable(collection.find(filter).first())
                .map(document -> objectMapper.convertValue(document, responseType));
    }

    @Override
    public <R, T> List<T> findAllBy(String key, R value, Class<T> responseType) {
        Bson filter = Filters.and(Filters.eq(key, value));
        return collection.find(filter)
                .map(document -> objectMapper.convertValue(document, responseType))
                .into(Objects.requireNonNullElseGet(List.of(), List::of));
    }
}

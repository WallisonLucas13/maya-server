package com.example.ia.mayaAI.repositories.impl;

import com.example.ia.mayaAI.enums.DocumentSortDirection;
import com.example.ia.mayaAI.repositories.MongoRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class MongoRepositoryImpl implements MongoRepository {

    private final MongoCollection<Document> collection;
    private ObjectMapper objectMapper;

    @Autowired
    public MongoRepositoryImpl(MongoDatabase mongoDatabase, String collectionName) {
        this.collection = mongoDatabase.getCollection(collectionName);
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public <T> T save(T entity) {
        Document document = objectMapper.convertValue(entity, Document.class);
        log.info("Saving entity: {}", document);
        if(!this.update(entity)){
            collection.insertOne(document);
        }
        return entity;
    }

    @Override
    public <T> boolean update(T entity) {
        Document document = objectMapper.convertValue(entity, Document.class);
        log.info("Updating entity: {}", document);
        UpdateResult result = collection
                .replaceOne(Filters.eq("_id", document.get("_id")), document);

        return result.getMatchedCount() > 0;
    }

    @Override
    public <T> void update(String key, String field, T value) {
        Document filter = new Document("_id", key);
        Document update = new Document("$set", new Document(field, value));
        log.info("Updating field: {}", update);
        collection.updateOne(filter, update);
    }

    @Override
    public <R, T> Optional<T> findBy(String key, R value, Class<T> responseType) {
        Bson filter = Filters.and(Filters.eq(key, value));
        log.info("Finding entity by key: {}", filter);
        return Optional.ofNullable(collection.find(filter).first())
                .map(document -> objectMapper.convertValue(document, responseType));
    }

    @Override
    public <R, T> List<T> findAllBy(String key, R value, Class<T> responseType) {
        Bson filter = Filters.and(Filters.eq(key, value));
        Bson sorter = Sorts.ascending("createdAt");
        log.info("Finding all entities by key: {}", filter);
        return collection.find(filter)
                .sort(sorter)
                .map(document -> objectMapper.convertValue(document, responseType))
                .into(new ArrayList<>());
    }

    @Override
    public <R, T> List<T> findAllBy(String key,
                                    R value,
                                    Class<T> responseType,
                                    String sortField,
                                    DocumentSortDirection direction) {
        Bson filter = Filters.and(Filters.eq(key, value));
        Bson sorter = direction.equals(DocumentSortDirection.ASC)
                ? Sorts.ascending(sortField)
                : Sorts.descending(sortField);

        log.info("Finding all entities by key: {} and sort by field: {}", filter, sorter);
        return collection.find(filter)
                .sort(sorter)
                .map(document -> objectMapper.convertValue(document, responseType))
                .into(new ArrayList<>());
    }
}

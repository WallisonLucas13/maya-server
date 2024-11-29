package com.example.ia.mayaAI.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String CONNECTION_URL;

    @Value("${spring.data.mongodb.database}")
    private String DATABASE_NAME;

    @Bean
    public MongoDatabase mongoDatabase() {
        log.info("Connecting to MongoDB... {}", CONNECTION_URL);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new com.mongodb.ConnectionString(CONNECTION_URL))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        return mongoClient.getDatabase(DATABASE_NAME);
    }
}

package com.example.ia.mayaAI.repositories;

import com.example.ia.mayaAI.enums.SortDirection;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MongoRepository {

    <T> T save(T entity);

    <T> boolean update(T entity);

    <T> void update(String key, String field, T value);

    <R, T> Optional<T> findBy(String key, R value, Class<T> responseType);

    <R, T> List<T> findAllBy(String key, R value, Class<T> responseType);

    <R, T> List<T> findAllBy(String key, R value, Class<T> responseType, String sortField, SortDirection direction);

    <T> long deleteAllBy(String key, T value);
}

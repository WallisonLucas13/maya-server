package com.example.ia.mayaAI.repositories;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório genérico para operações com MongoDB.
 */
@Repository
public interface MongoRepository {

    /**
     * Salva uma entidade no MongoDB.
     *
     * @param entity a entidade a ser salva
     * @param <T> o tipo da entidade
     * @return a entidade salva
     */
    <T> T save(T entity);

    /**
     * Atualiza uma entidade no MongoDB.
     *
     * @param entity a entidade a ser atualizada
     * @param <T> o tipo da entidade
     * @return a entidade atualizada
     */
    <T> boolean update(T entity);

    /**
     * Encontra uma entidade pelo valor de uma chave específica.
     *
     * @param key a chave para a busca
     * @param value o valor da chave
     * @param responseType o tipo da resposta esperada
     * @param <R> o tipo do valor da chave
     * @param <T> o tipo da entidade
     * @return um Optional contendo a entidade encontrada, se existir
     */
    <R, T> Optional<T> findBy(String key, R value, Class<T> responseType);

    /**
     * Encontra todas as entidades que correspondem ao valor de uma chave específica.
     *
     * @param key a chave para a busca
     * @param value o valor da chave
     * @param responseType o tipo da resposta esperada
     * @param <R> o tipo do valor da chave
     * @param <T> o tipo da entidade
     * @return uma lista contendo todas as entidades encontradas
     */
    <R, T> List<T> findAllBy(String key, R value, Class<T> responseType);
}
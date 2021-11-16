package com.raunlo.checklist.core.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface CrudRepository<T> {

    T save(final T entity);

    T update(final T entity);

    void delete(final long id);

    Optional<T> findById(final long id);

    Collection<T> getAll();

    default CompletionStage<T> saveAsync(final T entity) {
        return CompletableFuture.supplyAsync(() -> save(entity));
    }

    default CompletionStage<T> updateAsync(final T entity) {
        return CompletableFuture.supplyAsync(() -> update(entity));
    }

    default CompletionStage<Void> deleteAsync(final long id) {
        return CompletableFuture.runAsync(() -> delete(id));
    }

    default CompletionStage<Optional<T>> findByIdAsync(final long id) {
        return CompletableFuture.supplyAsync(() -> findById(id));
    }

    default CompletionStage<Collection<T>> getAllAsync() {
        return CompletableFuture.supplyAsync(this::getAll);
    }

}

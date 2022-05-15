package com.raunlo.checklist.core.repository;

import com.raunlo.checklist.core.entity.Task;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface TaskRepository {

    Task save(final Long checklistId, final Task entity);

    Task update(final Long checklistId, final Task entity);

    void delete(final Long checklistId, final long id);

    Optional<Task> findById(final Long checklistId, final long id);

    Collection<Task> getAll(final Long checklistId);

    default CompletionStage<Task> saveAsync(final Long checklistId, final Task entity) {
        return CompletableFuture.supplyAsync(() -> save(checklistId, entity));
    }

    default CompletionStage<Task> updateAsync(final Long checklistId, final Task entity) {
        return CompletableFuture.supplyAsync(() -> update(checklistId, entity));
    }

    default CompletionStage<Void> deleteAsync(final Long checklistId, final long id) {
        return CompletableFuture.runAsync(() -> delete(checklistId, id));
    }

    default CompletionStage<Optional<Task>> findByIdAsync(final Long checklistId, final long id) {
        return CompletableFuture.supplyAsync(() -> findById(checklistId, id));
    }

    default CompletionStage<Collection<Task>> getAllAsync(final Long checklistId) {
        return CompletableFuture.supplyAsync(() -> getAll(checklistId));
    }
}

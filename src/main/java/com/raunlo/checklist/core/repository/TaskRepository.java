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

    Collection<Task> saveAll(final Long checklistId, final Collection<Task> tasks);
}

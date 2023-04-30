package com.raunlo.checklist.core.repository;

import com.raunlo.checklist.core.entity.Checklist;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ChecklistRepository {
    CompletionStage<Checklist> save(final Checklist entity);

    CompletionStage<Checklist> update(final Checklist entity);

    CompletionStage<Void> delete(final long id);

    CompletionStage<Optional<Checklist>> findById(final long id);

    CompletionStage<Collection<Checklist>> getAll();

    boolean exists(final long id);
}

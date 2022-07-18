package com.raunlo.checklist.core.service;

import com.raunlo.checklist.core.entity.Checklist;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ChecklistService {
    CompletionStage<Checklist> save(final Checklist entity);

    CompletionStage<Checklist> update(final Checklist entity);

    CompletionStage<Void> delete(final long id);

    CompletionStage<Optional<Checklist>> findById(final long id);

    CompletionStage<Collection<Checklist>> getAll();

    CompletionStage<Collection<Checklist>> saveAll(Collection<Checklist> checklists);
}

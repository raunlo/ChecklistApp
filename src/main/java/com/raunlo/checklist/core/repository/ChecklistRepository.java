package com.raunlo.checklist.core.repository;

import com.raunlo.checklist.core.entity.list.ItemList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ChecklistRepository {
    CompletionStage<ItemList> save(final ItemList entity);

    CompletionStage<ItemList> update(final ItemList entity);

    CompletionStage<Void> delete(final long id);

    CompletionStage<Optional<ItemList>> findById(final long id);

    CompletionStage<Collection<ItemList>> getAll();

    CompletionStage<Boolean> exists(final long id);
}

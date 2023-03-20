package com.raunlo.checklist.core.service;

import com.raunlo.checklist.core.entity.list.ItemList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ChecklistService {
    CompletionStage<ItemList> save(final ItemList entity);

    CompletionStage<ItemList> update(final ItemList entity);

    CompletionStage<Void> delete(final long id);

    CompletionStage<Optional<ItemList>> findById(final long id);

    CompletionStage<Collection<ItemList>> getAll();
}

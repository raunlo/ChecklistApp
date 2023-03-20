package com.raunlo.checklist.core.repository;

import com.raunlo.checklist.core.entity.BaseItem;
import com.raunlo.checklist.core.entity.ExistingItem;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ExistingItemRepository {
    CompletionStage<BaseItem> save(final Long checklistId, final BaseItem entity);

    CompletionStage<ExistingItem> update(final Long checklistId, final BaseItem entity);

    CompletionStage<Void> delete(final Long checklistId, final long id);

    CompletionStage<Optional<ExistingItem>> findById(final Long checklistId, final long id);

    CompletionStage<Collection<ExistingItem>> getAll(final Long checklistId);

    CompletionStage<Void> changeOrder(final List<BaseItem> baseItems);

    CompletionStage<List<ExistingItem>> findAllTasksInOrderBounds(final long checklistId, final long taskOrderNumber, final Long newOrderNumber);

    CompletionStage<Collection<ExistingItem>> saveAll(final List<ExistingItem> baseItems, Long checklistId);

}

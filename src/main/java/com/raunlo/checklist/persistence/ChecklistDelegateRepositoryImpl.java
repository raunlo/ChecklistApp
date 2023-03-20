package com.raunlo.checklist.persistence;

import com.raunlo.checklist.core.entity.list.ItemList;
import com.raunlo.checklist.core.repository.ChecklistRepository;
import com.raunlo.checklist.persistence.dao.ChecklistDao;
import com.raunlo.checklist.persistence.mapper.ChecklistMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@ApplicationScoped
class ChecklistDelegateRepositoryImpl implements ChecklistRepository {
    private final ChecklistMapper checklistMapper;
    private final ChecklistDao checklistDao;

    @Inject()
    ChecklistDelegateRepositoryImpl(ChecklistMapper checklistMapper, ChecklistDao checklistDao) {
        this.checklistMapper = checklistMapper;
        this.checklistDao = checklistDao;
    }

    @Override
    public CompletionStage<ItemList> save(ItemList entity) {
        return CompletableFuture.supplyAsync(() ->
                        checklistDao.saveChecklist(entity.getName()))
                .thenApply(id -> new ItemList()
                        .withId(id)
                        .withName(entity.getName()));
    }

    @Override
    public CompletionStage<ItemList> update(ItemList entity) {
        return null;
    }

    @Override
    public CompletionStage<Void> delete(long id) {
        return null;
    }

    @Override
    public CompletionStage<Optional<ItemList>> findById(long id) {
        return CompletableFuture.supplyAsync(Optional::empty);
    }

    @Override
    public CompletionStage<Collection<ItemList>> getAll() {
        return CompletableFuture.supplyAsync(checklistDao::getAllChecklistDbos)
                .thenApply(checklists -> checklists.stream()
                        .map(checklistMapper::map)
                        .collect(Collectors.toList()));

    }
}

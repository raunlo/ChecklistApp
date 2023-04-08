package com.raunlo.checklist.persistence;

import com.raunlo.checklist.core.entity.ChecklistItem;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.core.repository.ChecklistItemRepository;
import com.raunlo.checklist.persistence.dao.PostgresChecklistItemDao;
import com.raunlo.checklist.persistence.mapper.ChecklistItemDboMapper;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
class ChecklistItemDelegateRepository implements ChecklistItemRepository {

  private final PostgresChecklistItemDao postgresChecklistItemDao;
  private final ChecklistItemDboMapper checklistItemDboMapper;

  private final ExecutorService executorService;


  @Inject()
  ChecklistItemDelegateRepository(PostgresChecklistItemDao postgresChecklistItemDao,
      ChecklistItemDboMapper checklistItemDboMapper) {

    this.postgresChecklistItemDao = postgresChecklistItemDao;
    this.checklistItemDboMapper = checklistItemDboMapper;
    this.executorService = Executors.newScheduledThreadPool(1);
  }


  @Override
  public CompletionStage<ChecklistItem> save(final Long checklistId, final ChecklistItem entity) {
    return CompletableFuture.supplyAsync(() -> postgresChecklistItemDao.insert(
            checklistItemDboMapper.map(entity),
            checklistId), executorService)
        .thenApply(checklistItemDboMapper::map);
  }

  @Override
  public CompletionStage<ChecklistItem> update(final Long checklistId, final ChecklistItem entity) {
    return CompletableFuture.supplyAsync(() -> {
      postgresChecklistItemDao.updateTask(checklistId,
          entity.getId(),
          entity.isCompleted(),
          entity.getName());
      return entity;
    }, executorService);
  }

  @Override
  public CompletableFuture<Void> delete(final long checklistId, final long id) {
    return CompletableFuture.runAsync(() ->
        postgresChecklistItemDao.deleteById(checklistId, id), executorService);
  }

  @Override
  public CompletableFuture<Optional<ChecklistItem>> findById(final long checklistId,
      final long id) {
    return CompletableFuture.supplyAsync(() ->
            postgresChecklistItemDao.findById(checklistId, id), executorService)
        .thenApply(checklistItem -> checklistItem.map(checklistItemDboMapper::map));
  }

  @Override
  public CompletionStage<Collection<ChecklistItem>> getAll(final long checklistId,
      final TaskPredefinedFilter predefinedFilter) {
    return CompletableFuture.supplyAsync(() ->
            postgresChecklistItemDao.getAllTasks(checklistId, predefinedFilter))
        .thenApply(checklistItems -> checklistItems.stream()
            .map(checklistItemDboMapper::map)
            .toList());
  }

  @Override
  public CompletionStage<Collection<ChecklistItem>> saveAll(long checklistId,
      List<ChecklistItem> checklistItems) {
    return CompletableFuture.supplyAsync(() -> {
          final var itemsDbos = checklistItems.stream()
              .map(checklistItemDboMapper::map)
              .toList();

          return postgresChecklistItemDao.saveAll(itemsDbos, checklistId);
        })
        .thenApply(
            savedChecklistItems -> savedChecklistItems.stream().map(checklistItemDboMapper::map)
                .toList());
  }

  @Override
  public CompletionStage<Void> removeTaskFromOrderLink(long checklistId, long taskId) {
    return CompletableFuture.runAsync(() ->
        postgresChecklistItemDao.removeTaskFromOrderLink(checklistId, taskId), executorService);
  }

  @Override
  public CompletionStage<Void> updateSavedItemOrderLink(long checklistId, long taskId) {
    return CompletableFuture.runAsync(() ->
            postgresChecklistItemDao.addNewlySavedChecklistItemOrderLink(checklistId, taskId),
        executorService);
  }

  @Override
  public CompletableFuture<Void> updateChecklistItemOrderLink(long checklistId,
      long checklistItemId,
      Long newNextChecklistItemId) {
    return CompletableFuture.runAsync(() ->
        postgresChecklistItemDao.updateChecklistItemOrder(checklistId, checklistItemId,
            newNextChecklistItemId), executorService);
  }


  @Override
  public CompletionStage<Optional<Long>> findNewNextItemIdByOrderAndChecklistItemId(
      long checklistId,
      long orderNumber, long checklistItemId) {
    return CompletableFuture.supplyAsync(
        () -> postgresChecklistItemDao.findChecklistItemByOrder(checklistId,
            orderNumber, checklistItemId), executorService);
  }
}

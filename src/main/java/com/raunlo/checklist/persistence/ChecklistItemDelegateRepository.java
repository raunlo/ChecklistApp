package com.raunlo.checklist.persistence;

import com.raunlo.checklist.core.entity.ChecklistItem;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.core.repository.ChecklistItemRepository;
import com.raunlo.checklist.persistence.dao.PostgresChecklistItemDao;
import com.raunlo.checklist.persistence.mapper.ChecklistItemDboMapper;
import com.raunlo.checklist.persistence.model.ChecklistItemsDbo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
class ChecklistItemDelegateRepository implements ChecklistItemRepository {

  private final PostgresChecklistItemDao postgresChecklistItemDao;
  private final ChecklistItemDboMapper checklistItemDboMapper;

  private ExecutorService executorService;


  @Inject()
  ChecklistItemDelegateRepository(PostgresChecklistItemDao postgresChecklistItemDao,
      ChecklistItemDboMapper checklistItemDboMapper) {

    this.postgresChecklistItemDao = postgresChecklistItemDao;
    this.checklistItemDboMapper = checklistItemDboMapper;
    this.executorService = Executors.newVirtualThreadPerTaskExecutor();
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
  public void changeOrder(List<ChecklistItem> checklistItems) {

    final List<ChecklistItemsDbo> checklistItemsDbos = checklistItems
        .stream()
        .map(checklistItemDboMapper::map)
        .toList();
    postgresChecklistItemDao.updateTasksOrder(checklistItemsDbos);
  }

  @Override
  public List<ChecklistItem> findAllTasksInOrderBounds(long checklistId,
      int taskOrderNumber, int newOrderNumber) {
    final long upperBound = Math.max(newOrderNumber, taskOrderNumber);
    final long lowerBound = Math.min(newOrderNumber, taskOrderNumber);
    return postgresChecklistItemDao.findTasksInOrderBounds(checklistId, lowerBound, upperBound)
        .stream()
        .map(checklistItemDboMapper::map)
        .toList();
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
            savedChecklistItems -> savedChecklistItems.stream().map(checklistItemDboMapper::map).toList());
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
}

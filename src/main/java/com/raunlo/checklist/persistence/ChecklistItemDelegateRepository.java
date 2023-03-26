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
  private final ChecklistItemDboMapper taskMapper;
  private final ExecutorService executorService;


  @Inject()
  ChecklistItemDelegateRepository(PostgresChecklistItemDao postgresChecklistItemDao,
      ChecklistItemDboMapper taskMapper) {

    executorService = Executors.newScheduledThreadPool(1);
    this.postgresChecklistItemDao = postgresChecklistItemDao;
    this.taskMapper = taskMapper;
  }


  @Override
  public CompletionStage<ChecklistItem> save(final Long checklistId, final ChecklistItem entity) {

    return CompletableFuture.supplyAsync(() ->
            postgresChecklistItemDao.insert(entity.getName(), entity.isCompleted(), checklistId),
        executorService).thenApply(taskMapper::map);

  }

  @Override
  public CompletionStage<ChecklistItem> update(final Long checklistId, final ChecklistItem entity) {

    return CompletableFuture.supplyAsync(() -> {
      postgresChecklistItemDao.updateTask(checklistId, entity.getId(), entity.isCompleted(),
          entity.getName());
      return entity;
    }, executorService);
  }

  @Override
  public CompletionStage<Void> delete(final long checklistId, final long id) {

    return CompletableFuture.runAsync(() ->
        postgresChecklistItemDao.deleteById(checklistId, id), executorService);
  }

  @Override
  public CompletionStage<Optional<ChecklistItem>> findById(final long checklistId, final long id) {

    return CompletableFuture.supplyAsync(() ->
        postgresChecklistItemDao.findById(checklistId, id)
            .map(taskMapper::map), executorService);
  }

  @Override
  public CompletionStage<Collection<ChecklistItem>> getAll(final long checklistId,
      final TaskPredefinedFilter predefinedFilter) {

    return CompletableFuture.supplyAsync(() ->
        postgresChecklistItemDao.getAllTasks(checklistId, predefinedFilter)
            .stream()
            .map(taskMapper::map)
            .toList(), executorService);
  }

  @Override
  public CompletionStage<Void> changeOrder(List<ChecklistItem> baseItems) {

    return CompletableFuture.runAsync(() -> {
      final List<ChecklistItemsDbo> checklistItemsDbos = baseItems
          .stream()
          .map(taskMapper::map)
          .toList();
      postgresChecklistItemDao.updateTasksOrder(checklistItemsDbos);
    }, executorService);
  }

  @Override
  public CompletionStage<List<ChecklistItem>> findAllTasksInOrderBounds(long checklistId,
      int taskOrderNumber, int newOrderNumber) {
    final long upperBound = Math.max(newOrderNumber, taskOrderNumber);
    final long lowerBound = Math.min(newOrderNumber, taskOrderNumber);
    return CompletableFuture.supplyAsync(() ->
        postgresChecklistItemDao.findTasksInOrderBounds(checklistId, lowerBound, upperBound)
            .stream()
            .map(taskMapper::map)
            .toList(), executorService);
  }

  @Override
  public CompletionStage<Collection<ChecklistItem>> saveAll(long checklistId,
      List<ChecklistItem> baseItems) {

    return CompletableFuture.supplyAsync(() -> {
          final List<ChecklistItemsDbo> checklistItemsDbos = baseItems.stream().map(taskMapper::map).toList();
          return postgresChecklistItemDao.saveAll(checklistItemsDbos, checklistId)
              .stream().map(taskMapper::map)
              .toList();
        }, executorService
    );
  }
}

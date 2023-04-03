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

@ApplicationScoped
class ChecklistItemDelegateRepository implements ChecklistItemRepository {

  private final PostgresChecklistItemDao postgresChecklistItemDao;
  private final ChecklistItemDboMapper taskMapper;


  @Inject()
  ChecklistItemDelegateRepository(PostgresChecklistItemDao postgresChecklistItemDao,
      ChecklistItemDboMapper taskMapper) {

    this.postgresChecklistItemDao = postgresChecklistItemDao;
    this.taskMapper = taskMapper;
  }


  @Override
  public ChecklistItem save(final Long checklistId, final ChecklistItem entity) {

    final var savedEntity = postgresChecklistItemDao.insert(
        taskMapper.map(entity),
        checklistId);
    return taskMapper.map(savedEntity);
  }

  @Override
  public ChecklistItem update(final Long checklistId, final ChecklistItem entity) {

    postgresChecklistItemDao.updateTask(checklistId,
        entity.getId(),
        entity.isCompleted(),
        entity.getName());
    return entity;
  }

  @Override
  public void delete(final long checklistId, final long id) {

    postgresChecklistItemDao.deleteById(checklistId, id);
  }

  @Override
  public Optional<ChecklistItem> findById(final long checklistId, final long id) {

    return postgresChecklistItemDao.findById(checklistId, id)
        .map(taskMapper::map);
  }

  @Override
  public Collection<ChecklistItem> getAll(final long checklistId,
      final TaskPredefinedFilter predefinedFilter) {

    return postgresChecklistItemDao.getAllTasks(checklistId, predefinedFilter)
        .stream()
        .map(taskMapper::map)
        .toList();
  }

  @Override
  public void changeOrder(List<ChecklistItem> checklistItems) {

    final List<ChecklistItemsDbo> checklistItemsDbos = checklistItems
        .stream()
        .map(taskMapper::map)
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
        .map(taskMapper::map)
        .toList();
  }

  @Override
  public Collection<ChecklistItem> saveAll(long checklistId, List<ChecklistItem> checklistItems) {

    final List<ChecklistItemsDbo> checklistItemsDbos = checklistItems.stream()
        .map(taskMapper::map)
        .toList();
    return postgresChecklistItemDao.saveAll(checklistItemsDbos, checklistId)
        .stream().map(taskMapper::map)
        .toList();
  }

  @Override
  public void removeTaskFromOrderLink(long checklistId, long taskId) {
    postgresChecklistItemDao.removeTaskFromOrderLink(checklistId, taskId);
  }

  @Override
  public void updateSavedItemOrderLink(long checklistId, long taskId) {
    postgresChecklistItemDao.addNewlySavedChecklistItemOrderLink(checklistId, taskId);
  }
}

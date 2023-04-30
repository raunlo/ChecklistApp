package com.raunlo.checklist.persistence;

import com.raunlo.checklist.core.entity.ChecklistItem;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.core.repository.ChecklistItemRepository;
import com.raunlo.checklist.persistence.dao.PostgresChecklistItemDao;
import com.raunlo.checklist.persistence.mapper.ChecklistItemDboMapper;
import com.raunlo.checklist.persistence.model.ChecklistItemsDbo;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
class ChecklistItemDelegateRepository implements ChecklistItemRepository {

  private final PostgresChecklistItemDao postgresChecklistItemDao;
  private final ChecklistItemDboMapper checklistItemDboMapper;



  @Inject()
  ChecklistItemDelegateRepository(PostgresChecklistItemDao postgresChecklistItemDao,
      ChecklistItemDboMapper checklistItemDboMapper) {

    this.postgresChecklistItemDao = postgresChecklistItemDao;
    this.checklistItemDboMapper = checklistItemDboMapper;
  }


  @Override
  public ChecklistItem save(final Long checklistId, final ChecklistItem entity) {

    final ChecklistItemsDbo dbo = checklistItemDboMapper.map(entity);
    final ChecklistItemsDbo insertChecklistDboFuture =
        postgresChecklistItemDao.insert(dbo, checklistId);

    return checklistItemDboMapper.map(insertChecklistDboFuture);
  }

  @Override
  public ChecklistItem update(final Long checklistId, final ChecklistItem entity) {

    postgresChecklistItemDao.updateTask(checklistId, entity.getId(),
        entity.isCompleted(),
        entity.getName());

    return entity;
  }

  @Override
  public void delete(final long checklistId, final long id) {
    postgresChecklistItemDao.deleteById(checklistId, id);
  }

  @Override
  public Optional<ChecklistItem> findById(final long checklistId,
      final long id) {

    final Optional<ChecklistItemsDbo> checklistItemsDbo = postgresChecklistItemDao.findById(
        checklistId, id);
    return checklistItemsDbo.map(checklistItemDboMapper::map);
  }

  @Override
  public Collection<ChecklistItem> getAll(final long checklistId,
      final TaskPredefinedFilter predefinedFilter) {
    final List<ChecklistItemsDbo> allChecklistDbos = postgresChecklistItemDao.getAllTasks(
        checklistId,
        predefinedFilter);
    return allChecklistDbos.stream()
        .map(checklistItemDboMapper::map)
        .toList();
  }

  @Override
  public Collection<ChecklistItem> saveAll(long checklistId,
      List<ChecklistItem> checklistItems) {
    final var itemsDbos = checklistItems.stream()
        .map(checklistItemDboMapper::map)
        .toList();

    final List<ChecklistItemsDbo> savedChecklistDbos = postgresChecklistItemDao.saveAll(itemsDbos,
        checklistId);

    return savedChecklistDbos.stream().map(checklistItemDboMapper::map).toList();
  }

  @Override
  public void removeTaskFromOrderLink(long checklistId, long taskId) {
    postgresChecklistItemDao.removeTaskFromOrderLink(checklistId, taskId);
  }

  @Override
  public void updateSavedItemOrderLink(long checklistId, long taskId) {
    postgresChecklistItemDao.addNewlySavedChecklistItemOrderLink(checklistId, taskId);
  }

  @Override
  public void updateChecklistItemOrderLink(long checklistId,
      long checklistItemId,
      Long newNextChecklistItemId) {
    postgresChecklistItemDao.updateChecklistItemOrder(checklistId, checklistItemId,
        newNextChecklistItemId);
  }


  @Override
  public Optional<Long> findNewNextItemIdByOrderAndChecklistItemId(
      long checklistId,
      long orderNumber, long checklistItemId) {
    return postgresChecklistItemDao.findChecklistItemByOrder(checklistId, orderNumber,
        checklistItemId);
  }
}

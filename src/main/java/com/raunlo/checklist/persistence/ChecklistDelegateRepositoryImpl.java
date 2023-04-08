package com.raunlo.checklist.persistence;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.entity.ChecklistBuilder;
import com.raunlo.checklist.core.repository.ChecklistRepository;
import com.raunlo.checklist.persistence.dao.PostgresChecklistDao;
import com.raunlo.checklist.persistence.mapper.ChecklistDboMapper;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@ApplicationScoped
class ChecklistDelegateRepositoryImpl implements ChecklistRepository {

  private final ChecklistDboMapper checklistDboMapper;
  private final PostgresChecklistDao checklistDao;
  private final ScheduledExecutorService executorService;

  @Inject()
  ChecklistDelegateRepositoryImpl(ChecklistDboMapper checklistDboMapper,
      PostgresChecklistDao checklistDao) {
    this.executorService = Executors.newScheduledThreadPool(1);
    this.checklistDboMapper = checklistDboMapper;
    this.checklistDao = checklistDao;
  }

  @Override
  public CompletionStage<Checklist> save(Checklist entity) {
    return CompletableFuture.supplyAsync(
            () -> checklistDao.save(checklistDboMapper.map(entity)),
            executorService)
        .thenApply(checklistDboMapper::map);
  }

  @Override
  public CompletionStage<Checklist> update(Checklist entity) {
    return CompletableFuture.supplyAsync(() -> {
      checklistDao.updateChecklist(checklistDboMapper.map(entity));
      return entity;
    }, executorService);
  }

  @Override
  public CompletionStage<Void> delete(long id) {
    return CompletableFuture.runAsync(() ->
        checklistDao.delete(id), executorService);
  }

  @Override
  public CompletionStage<Optional<Checklist>> findById(long id) {
    return CompletableFuture.supplyAsync(() ->
            checklistDao.findById(id), executorService)
        .thenApply(dbo -> dbo.map(checklistDboMapper::map));
  }

  @Override
  public CompletionStage<Collection<Checklist>> getAll() {
    return CompletableFuture.supplyAsync(checklistDao::getAllChecklistDbos, executorService)
        .thenApply(checklists -> checklists.stream()
            .map(checklistDboMapper::map)
            .collect(Collectors.toList()));
  }

  @Override
  public CompletionStage<Boolean> exists(long id) {
    return CompletableFuture.supplyAsync(() ->
        checklistDao.checklistExists(id), executorService);
  }
}

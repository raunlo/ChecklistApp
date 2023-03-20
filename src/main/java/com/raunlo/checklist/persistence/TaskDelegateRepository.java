//package com.raunlo.checklist.persistence;
//
//import com.raunlo.baseItemList.persistence.dao.ChecklistDao;
//import com.raunlo.checklist.core.entity.BaseItem;
//import com.raunlo.checklist.core.entity.ChecklistItem;
//import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
//import com.raunlo.checklist.core.repository.ChecklistItemRepository;
//import com.raunlo.checklist.persistence.dao.PostgreChecklistItemDao;
//import com.raunlo.checklist.persistence.mapper.TaskDboMapper;
//import com.raunlo.checklist.persistence.model.ChecklistDbo;
//import com.raunlo.checklist.persistence.model.TaskDbo;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.inject.Inject;
//import java.util.Collection;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CompletionStage;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.function.Function;
//
//@FunctionalInterface
//interface testing<T, D> {
//    T test(Function<T, T> test1);
//}
//
//@ApplicationScoped
//class TaskDelegateRepository implements ChecklistItemRepository {
//
//    private final PostgreChecklistItemDao postgreChecklistItemDao;
//    private final TaskDboMapper taskMapper;
//    private final ExecutorService executorService;
//
//    private final ChecklistDao checklistDao;
//
//    private final Function<Long, CompletionStage<String>> getChecklistType;
//
//
//    @Inject()
//    TaskDelegateRepository(PostgreChecklistItemDao postgreChecklistItemDao, TaskDboMapper taskMapper, ChecklistDao checklistDao) {
//        this.checklistDao = checklistDao;
//        executorService = Executors.newScheduledThreadPool(1);
//        this.postgreChecklistItemDao = postgreChecklistItemDao;
//        this.taskMapper = taskMapper;
//
//        getChecklistType = (Long checklistId) -> {
//            return CompletableFuture.supplyAsync(() -> checklistDao.getChecklistdbo(checklistId)
//                    .map(ChecklistDbo::listType)
//                    .get(), executorService); //error handling
//        };
//    }
//
//
//    @Override
//    public CompletionStage<ChecklistItem> save(final Long checklistId, final BaseItem entity) {
//        return getChecklistType.andThen((checklistTypeFuture) -> {
//            checklistTypeFuture.thenApply((checklistType) -> {
//                taskMapper.map(postgreChecklistItemDao.insert(entity.getName(), entity.isCompleted(), checklistId))
//            });
//            return CompletableFuture.supplyAsync(() ->
//                            t,
//                    executorService);
//        });
//
//
//    }
//
//    @Override
//    public CompletionStage<ChecklistItem> update(final Long checklistId, final BaseItem entity) {
//        return CompletableFuture.supplyAsync(() -> {
//            postgreChecklistItemDao.updateTask(checklistId, entity.getId(), entity.isCompleted(), entity.getName());
//            return entity;
//        }, executorService);
//    }
//
//    @Override
//    public CompletionStage<Void> delete(final Long checklistId, final long id) {
//        return CompletableFuture.runAsync(() ->
//                postgreChecklistItemDao.deleteById(checklistId, id), executorService);
//    }
//
//    @Override
//    public CompletionStage<Optional<BaseItem>> findById(final Long checklistId, final long id) {
//
//        return CompletableFuture.supplyAsync(() ->
//                postgreChecklistItemDao.findById(checklistId, id)
//                        .map(taskMapper::map), executorService);
//    }
//
//    @Override
//    public CompletionStage<Collection<BaseItem>> getAll(final Long checklistId, final TaskPredefinedFilter predefinedFilter) {
//        return CompletableFuture.supplyAsync(() ->
//                postgreChecklistItemDao.getAllTasks(checklistId, predefinedFilter)
//                        .stream()
//                        .map(taskMapper::map)
//                        .toList(), executorService);
//    }
//
//    @Override
//    public CompletionStage<Void> changeOrder(List<BaseItem> baseItems) {
//        return CompletableFuture.runAsync(() -> {
//            final List<TaskDbo> taskDbos = baseItems
//                    .stream()
//                    .map(taskMapper::map)
//                    .toList();
//            postgreChecklistItemDao.updateTasksOrder(taskDbos);
//        }, executorService);
//    }
//
//    @Override
//    public CompletionStage<List<BaseItem>> findAllTasksInOrderBounds(long checklistId, long taskOrderNumber, Long newOrderNumber) {
//        final long upperBound = Math.max(newOrderNumber, taskOrderNumber);
//        final long lowerBound = Math.min(newOrderNumber, taskOrderNumber);
//        return CompletableFuture.supplyAsync(() ->
//                postgreChecklistItemDao.findTasksInOrderBounds(checklistId, lowerBound, upperBound)
//                        .stream()
//                        .map(taskMapper::map)
//                        .toList(), executorService);
//    }
//
//    @Override
//    public CompletionStage<Collection<BaseItem>> saveAll(List<BaseItem> baseItems, Long checklistId) {
//        return CompletableFuture.supplyAsync(() -> {
//                    final List<TaskDbo> taskDbos = baseItems.stream().map(taskMapper::map).toList();
//                    return postgreChecklistItemDao.saveAll(taskDbos, checklistId)
//                            .stream().map(taskMapper::map)
//                            .toList();
//                }, executorService
//        );
//    }
//}

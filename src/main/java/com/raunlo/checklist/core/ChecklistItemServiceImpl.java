package com.raunlo.checklist.core;

import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.ChecklistItem;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.core.entity.error.Errors;
import com.raunlo.checklist.core.entity.internal.RepositoryQuery;
import com.raunlo.checklist.core.entity.internal.RollbackAction;
import com.raunlo.checklist.core.repository.ChecklistItemRepository;
import com.raunlo.checklist.core.service.ChecklistItemService;
import com.raunlo.checklist.core.util.CompletableFutureUtils;
import com.raunlo.checklist.core.util.EitherUtil;
import com.raunlo.checklist.core.validator.BeanValidator;
import com.raunlo.checklist.core.validator.ChecklistItemValidator;
import io.vavr.control.Either;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
class ChecklistItemServiceImpl implements ChecklistItemService {

  private final ChecklistItemRepository checklistItemRepository;
  private final BeanValidator validator;
  private final ChecklistItemValidator checklistItemValidator;

  @Inject
  public ChecklistItemServiceImpl(
      ChecklistItemRepository checklistItemRepository,
      BeanValidator validator,
      ChecklistItemValidator checklistItemValidator) {
    this.checklistItemRepository = checklistItemRepository;
    this.validator = validator;
    this.checklistItemValidator = checklistItemValidator;
  }

  @Override
  public CompletionStage<Either<Errors, ChecklistItem>> save(
      final Long checklistId, final ChecklistItem entity) {

    final RepositoryQuery<ChecklistItem> saveChecklistItem = () ->
        checklistItemRepository.save(checklistId, entity);

    final RollbackAction<ChecklistItem> rollbackFunction = (either) -> {
      if (either.isRight()) {
        final var id = either.get().getId();
        return checklistItemRepository.removeTaskFromOrderLink(checklistId, either.get().getId())
            .thenCompose(__ -> checklistItemRepository.delete(checklistId, id)
                .thenApply(v -> either));
      }
      return CompletableFuture.completedFuture(either);
    };

    final CompletionStage<Either<Errors, ChecklistItem>> saveItem =
        validateEntityAndChecklist(checklistId, entity)
            .thenCompose(validationResult ->
                EitherUtil.mapCompletableStage(validationResult, saveChecklistItem))

            .thenCompose(savedChecklistItemEither -> {
              if (savedChecklistItemEither.isRight()) {
                return checklistItemRepository.updateSavedItemOrderLink(checklistId,
                        savedChecklistItemEither.get().getId())
                    .thenApply(__ -> savedChecklistItemEither);
              }
              return CompletableFuture.completedFuture(savedChecklistItemEither);
            });

    return CompletableFutureUtils.handleResponse(saveItem, rollbackFunction);
  }

  @Override
  public CompletionStage<Either<Errors, ChecklistItem>> update(
      final Long checklistId, final ChecklistItem entity) {

    final RepositoryQuery<ChecklistItem> updateChecklistItem = () ->
        checklistItemRepository.update(checklistId, entity);

    return validateEntityAndChecklist(checklistId, entity)
        .thenCompose(validationResult ->
            EitherUtil.mapCompletableStage(validationResult, updateChecklistItem));
  }


  @Override
  public CompletionStage<Either<Errors, Void>> delete(final Long checklistId, final Long id) {

    final RepositoryQuery<Void> removeChecklistITemOrderLinks = () ->
        checklistItemRepository.removeTaskFromOrderLink(checklistId, id);

    final RepositoryQuery<Void> remoteChecklistItem = () ->
        checklistItemRepository.delete(checklistId, id);

    return checklistItemValidator.validateChecklistExistence(checklistId)
        .thenCompose(validationResult ->
            EitherUtil.mapCompletableStage(validationResult, removeChecklistITemOrderLinks))
        .thenCompose(updateOldTaskLinkResult ->
            EitherUtil.mapCompletableStage(updateOldTaskLinkResult, remoteChecklistItem));
  }

  @Override
  public CompletionStage<Either<Errors, Optional<ChecklistItem>>> findById(
      final Long checklistId, final Long id) {

    final RepositoryQuery<Optional<ChecklistItem>> findChecklistItem = () ->
        checklistItemRepository.findById(checklistId, id);

    return checklistItemValidator.validateChecklistExistence(checklistId)
        .thenCompose(validationEither ->
            EitherUtil.mapCompletableStage(validationEither, findChecklistItem));
  }

  @Override
  public CompletionStage<Either<Errors, Collection<ChecklistItem>>> getAll(
      final Long checklistId, final TaskPredefinedFilter predefineFilter) {

    final RepositoryQuery<Collection<ChecklistItem>> getAllFunction = () ->
        checklistItemRepository.getAll(checklistId, predefineFilter);

    return checklistItemValidator.validateChecklistExistence(checklistId)
        .thenCompose(validationEither ->
            EitherUtil.mapCompletableStage(validationEither, getAllFunction));
  }

  @Override
  public CompletionStage<Either<Errors, Collection<ChecklistItem>>> saveAll(
      List<ChecklistItem> checklistItems, Long checklistId) {

    final var beanValidationStream = checklistItems.stream()
        .map(validator::validate);

    final var validations = Stream.concat(
        Stream.of(checklistItemValidator.validateChecklistExistence(checklistId)),
        beanValidationStream).toList();

    final var validationResult = CompletableFutureUtils.flatMapCompletableFuture(validations,
        (oldEither, newEither) -> {
          if (newEither.isLeft()) {
            return newEither;
          }
          return oldEither;
        });

    final RepositoryQuery<Collection<ChecklistItem>> saveAllChecklistItemsFunction =
        () -> checklistItemRepository.saveAll(checklistId, checklistItems);

    return validationResult.thenCompose(validationResults ->
        EitherUtil.mapCompletableStage(validationResults, saveAllChecklistItemsFunction));

  }

  @Override
  public CompletionStage<Either<Errors, Void>> changeOrder(
      final ChangeOrderRequest changeOrderRequest) {
    final var checklistId = changeOrderRequest.checklistId();
    final var checklistItemId = changeOrderRequest.checklistItemId();

    Function<Long, CompletionStage<Void>> updateItemOrder = newNextItemId ->
        checklistItemRepository.updateChecklistItemOrderLink(checklistId, checklistItemId,
            newNextItemId);

    return checklistItemRepository.findNewNextItemIdByOrderAndChecklistItemId(checklistId,
            changeOrderRequest.newOrderNumber(), checklistItemId)

        .thenApply(newNextItemId -> Either.<Errors, Long>right(newNextItemId.orElse(null)))

        .thenCompose(newNextItemIdEither ->
            EitherUtil.mapCompletableStage(newNextItemIdEither, updateItemOrder));
  }

  private CompletionStage<Either<Errors, Void>> validateEntityAndChecklist(long checklistId,
      ChecklistItem entity) {
    final var checklistExistenceValidation = checklistItemValidator.validateChecklistExistence(
        checklistId);

    return CompletableFutureUtils.flatMapCompletableFuture(
        List.of(validator.validate(entity), checklistExistenceValidation),
        (oldValue, newValue) -> {
          if (newValue.isLeft()) {
            return newValue;
          }
          return oldValue;
        });
  }
}

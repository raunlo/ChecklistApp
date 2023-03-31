package com.raunlo.checklist.core;

import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.ChecklistItem;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.core.entity.error.Errors;
import com.raunlo.checklist.core.processor.ChangeOrderProcessor;
import com.raunlo.checklist.core.repository.ChecklistItemRepository;
import com.raunlo.checklist.core.service.ChecklistItemService;
import com.raunlo.checklist.core.util.CompletableFutureUtils;
import com.raunlo.checklist.core.util.EitherUtil;
import com.raunlo.checklist.core.validator.BeanValidator;
import com.raunlo.checklist.core.validator.ChecklistItemValidator;
import io.vavr.control.Either;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

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

    final Supplier<CompletionStage<ChecklistItem>> saveChecklistItem =
        () -> checklistItemRepository.save(checklistId, entity);

    return validateEntityAndChecklist(checklistId, entity)
        .thenCompose(
            checklistItemEither ->
                EitherUtil.mapCompletableStage(checklistItemEither, saveChecklistItem));
  }

  @Override
  public CompletionStage<Either<Errors, ChecklistItem>> update(
      final Long checklistId, final ChecklistItem entity) {

    final Supplier<CompletionStage<ChecklistItem>> updateChecklistItem =
        () -> checklistItemRepository.update(checklistId, entity);

    return validateEntityAndChecklist(checklistId, entity)
        .thenCompose(checklistItemEither ->
            EitherUtil.mapCompletableStage(checklistItemEither, updateChecklistItem));
  }


  @Override
  public CompletionStage<Either<Errors, Void>> delete(final Long checklistId, final Long id) {

    final Supplier<CompletionStage<Void>> deleteFunction = () ->
        checklistItemRepository.delete(checklistId, id);

    return checklistItemValidator.validateChecklistExistence(checklistId)
        .thenCompose(
            validationEither -> EitherUtil.mapCompletableStage(validationEither, deleteFunction));
  }

  @Override
  public CompletionStage<Either<Errors, Optional<ChecklistItem>>> findById(
      final Long checklistId, final Long id) {

    final Supplier<CompletionStage<Optional<ChecklistItem>>> findChecklistItem = () ->
        checklistItemRepository.findById(checklistId, id);

    return checklistItemValidator.validateChecklistExistence(checklistId)
        .thenCompose(
            validationEither ->
                EitherUtil.mapCompletableStage(validationEither, findChecklistItem));
  }

  @Override
  public CompletionStage<Either<Errors, Collection<ChecklistItem>>> getAll(
      final Long checklistId, final TaskPredefinedFilter predefineFilter) {

    final Supplier<CompletionStage<Collection<ChecklistItem>>> getAllFunction = () ->
        checklistItemRepository.getAll(checklistId, predefineFilter);

    return checklistItemValidator.validateChecklistExistence(checklistId)
        .thenCompose(
            validationEither ->
                EitherUtil.mapCompletableStage(validationEither, getAllFunction));
  }

  @Override
  public CompletionStage<Either<Errors, Collection<ChecklistItem>>> saveAll(
      List<ChecklistItem> checklistItems, Long checklistId) {

    final var beanValidationStream = checklistItems.stream()
        .map(validator::validate);

    final var validations = Stream.concat(
        Stream.of(checklistItemValidator.validateChecklistExistence(checklistId)),
        beanValidationStream
    ).toList();

    final var validationResult = CompletableFutureUtils.flatMapCompletableFuture(validations,
        (oldEither, newEither) -> {
          if (newEither.isLeft()) {
            return newEither;
          }
          return oldEither;
        });

    final Supplier<CompletionStage<Collection<ChecklistItem>>> saveAllChecklistItemsFunction =
        () -> checklistItemRepository.saveAll(checklistId, checklistItems);

    return validationResult.thenCompose(validationResults ->
        EitherUtil.mapCompletableStage(validationResults, saveAllChecklistItemsFunction));
  }

  @Override
  public CompletionStage<Either<Errors, Void>> changeOrder(
      final ChangeOrderRequest changeOrderRequest) {

    final long checklistId = changeOrderRequest.getChecklistId();
    final Function<List<ChecklistItem>, CompletionStage<Void>> updateChecklistItemsFunction =
        checklistItemRepository::changeOrder;

    final Supplier<CompletionStage<Optional<ChecklistItem>>> getItemOrderNumber =
        () -> checklistItemRepository.findById(checklistId, changeOrderRequest.getTaskId());

    final Function<Integer, CompletionStage<List<ChecklistItem>>>
        findAllItemsInCurrentAndOldOrderNumberBounds =
        itemOrderNumber ->
            checklistItemRepository.findAllTasksInOrderBounds(
                checklistId, itemOrderNumber, changeOrderRequest.getNewOrderNumber());

    final var changeOrderProcessor =
        ChangeOrderProcessor.<ChecklistItem>builder()
            .newOrderNumber(changeOrderRequest.getNewOrderNumber())
            .updateOrderFunction(updateChecklistItemsFunction)
            .getItemOrderNumberSupplier(getItemOrderNumber)
            .findAllItemsInCurrentAndOldOrderNumberBounds(
                findAllItemsInCurrentAndOldOrderNumberBounds)
            .build();

    return changeOrderProcessor.changeOrder();
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

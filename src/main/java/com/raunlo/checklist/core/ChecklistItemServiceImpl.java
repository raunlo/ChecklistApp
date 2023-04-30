package com.raunlo.checklist.core;

import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.ChecklistItem;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.core.entity.error.Error;
import com.raunlo.checklist.core.entity.error.ErrorType;
import com.raunlo.checklist.core.entity.error.Errors;
import com.raunlo.checklist.core.entity.error.ErrorsBuilder;
import com.raunlo.checklist.core.repository.ChecklistItemRepository;
import com.raunlo.checklist.core.service.ChecklistItemService;
import com.raunlo.checklist.core.validator.BeanValidator;
import com.raunlo.checklist.core.validator.ChecklistItemValidator;
import io.vavr.control.Either;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import jdk.incubator.concurrent.StructuredTaskScope.ShutdownOnFailure;
import lombok.SneakyThrows;

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
  @SneakyThrows
  public Either<Errors, ChecklistItem> save(
      final Long checklistId, final ChecklistItem entity) {

    final Either<Errors, ChecklistItem> validationResult =
        validateEntityAndChecklist(checklistId, List.of(entity));

    if (validationResult.isEmpty()) {
      return validationResult;
    }

    try (final ShutdownOnFailure scope = new ShutdownOnFailure()) {
      Future<ChecklistItem> saveChecklistItemFuture = scope.fork(() -> {
        final ChecklistItem savedChecklistItem = checklistItemRepository.save(checklistId, entity);
        checklistItemRepository.updateSavedItemOrderLink(checklistId, savedChecklistItem.getId());
        return savedChecklistItem;
      });

      scope.join();

      return Either.right(saveChecklistItemFuture.resultNow());
    }
  }

  @Override
  @SneakyThrows
  public Either<Errors, ChecklistItem> update(
      final Long checklistId, final ChecklistItem entity) {

    final Either<Errors, ChecklistItem> validationResult = validateEntityAndChecklist(checklistId,
        List.of(entity));

    if (validationResult.isEmpty()) {
      return validationResult;
    }

    try (final ShutdownOnFailure scope = new ShutdownOnFailure()) {
      final Future<ChecklistItem> updatedChecklistItem = scope.fork(
          () -> checklistItemRepository.update(checklistId, entity));

      scope.join();
      return Either.right(updatedChecklistItem.resultNow());
    }
  }


  @Override
  @SneakyThrows
  public Either<Errors, Void> delete(final Long checklistId, final Long id) {

    final Either<Errors, Void> validationResult = checklistItemValidator.validateChecklistExistence(
        checklistId);

    if (validationResult.isEmpty()) {
      return validationResult;
    }

    try (final ShutdownOnFailure scope = new ShutdownOnFailure()) {
      scope.fork(() -> {
        checklistItemRepository.removeTaskFromOrderLink(checklistId, id);
        checklistItemRepository.delete(checklistId, id);
        return null;
      });

      scope.join();

      return Either.right(null);
    }
  }

  @Override
  @SneakyThrows
  public Either<Errors, Optional<ChecklistItem>> findById(
      final Long checklistId, final Long id) {

    final Either<Errors, Void> validationResult = checklistItemValidator.validateChecklistExistence(
        checklistId);

    if (validationResult.isEmpty()) {
      return Either.left(validationResult.getLeft());
    }

    try (final ShutdownOnFailure scope = new ShutdownOnFailure()) {
      final Future<Optional<ChecklistItem>> getTask = scope.fork(
          () -> checklistItemRepository.findById(checklistId, id));

      scope.join();

      return Either.right(getTask.resultNow());
    }
  }

  @Override
  public Either<Errors, Collection<ChecklistItem>> getAll(
      final Long checklistId, final TaskPredefinedFilter predefineFilter) {

    final Either<Errors, Void> validationResult = checklistItemValidator.validateChecklistExistence(
        checklistId);

    if (validationResult.isEmpty()) {
      return Either.left(validationResult.getLeft());
    }

    try (final ShutdownOnFailure scope = new ShutdownOnFailure()) {
      final Future<Collection<ChecklistItem>> getItems = scope.fork(
          () -> checklistItemRepository.getAll(checklistId, predefineFilter));
      scope.join();

      return Either.right(getItems.resultNow());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  @SneakyThrows
  public Either<Errors, Collection<ChecklistItem>> saveAll(
      List<ChecklistItem> checklistItems, Long checklistId) {

    final Either<Errors, Collection<ChecklistItem>> validationErrors =
        this.validateEntityAndChecklist(checklistId, checklistItems);

    if (validationErrors.isEmpty()) {
      return validationErrors;
    }

    try (ShutdownOnFailure scope = new ShutdownOnFailure()) {
      final Future<Collection<ChecklistItem>> savedChecklistItemsFuture = scope.fork(
          () -> checklistItemRepository.saveAll(checklistId, checklistItems));

      scope.join();

      return Either.right(savedChecklistItemsFuture.resultNow());
    } catch (java.lang.Error e) {
      System.out.println(e);
    }
    return null;
  }

  @Override
  @SneakyThrows
  public Either<Errors, Void> changeOrder(
      final ChangeOrderRequest changeOrderRequest) {
    final var checklistId = changeOrderRequest.checklistId();
    final var checklistItemId = changeOrderRequest.checklistItemId();

    try (final ShutdownOnFailure scope = new ShutdownOnFailure()) {
      final Future<Either<Errors, Void>> nextItemIdFuture = scope.fork(
          () -> {
            final Long nextItemId = checklistItemRepository.findNewNextItemIdByOrderAndChecklistItemId(
                checklistId,
                changeOrderRequest.newOrderNumber(),
                checklistItemId
            ).orElse(null);
            checklistItemRepository.updateChecklistItemOrderLink(checklistId, checklistItemId,
                nextItemId);
            return Either.right(null);
          });

      scope.join();

      return nextItemIdFuture.get();
    }
  }

  private <T> Either<Errors, T> validateEntityAndChecklist(long checklistId,
      Collection<ChecklistItem> entities) {
    try (final ShutdownOnFailure scope = new ShutdownOnFailure()) {
      Future<List<Either<Errors, Void>>> checklistItemsValidations = scope.fork(
          () -> entities.stream()
              .map(validator::validate)
              .toList());

      Future<Either<Errors, Void>> checklistValidation = scope.fork(() ->
          checklistItemValidator.validateChecklistExistence(checklistId));

      scope.join();

      final Set<Error> errors = Stream.concat(checklistItemsValidations.resultNow().stream(),
              Stream.of(checklistValidation.resultNow()))
          .filter(Either::isLeft)
          .map(Either::getLeft)
          .map(Errors::errors)
          .flatMap(Collection::parallelStream)
          .collect(Collectors.toSet());

      if (errors.isEmpty()) {
        return Either.right(null);
      } else {
        return Either.left(ErrorsBuilder.builder()
            .errorType(ErrorType.VALIDATION_ERROR)
            .errors(errors)
            .build());
      }

    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}

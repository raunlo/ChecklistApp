package com.raunlo.checklist.validator;

import com.raunlo.checklist.core.entity.error.ErrorBuilder;
import com.raunlo.checklist.core.entity.error.ErrorType;
import com.raunlo.checklist.core.entity.error.Errors;
import com.raunlo.checklist.core.entity.error.ErrorsBuilder;
import com.raunlo.checklist.core.repository.ChecklistRepository;
import io.vavr.control.Either;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class ChecklistItemValidator implements
    com.raunlo.checklist.core.validator.ChecklistItemValidator {

  private final ChecklistRepository checklistRepository;

  @Inject
  public ChecklistItemValidator(ChecklistRepository checklistRepository) {
    this.checklistRepository = checklistRepository;
  }

  @Override
  public CompletionStage<Either<Errors, Void>> validateChecklistExistence(Long id) {
    if (id != null) {
      return checklistRepository.exists(id)
          .thenApply(exists -> {
            if (!exists) {
              final var error = ErrorBuilder.builder()
                  .errorMessage("Checklist object doesn't exists with id: " + id)
                  .field(null)
                  .build();

              return Either.left(ErrorsBuilder.builder()
                  .errors(Set.of(error))
                  .errorType(ErrorType.PARENT_ID_MISSING)
                  .build());
            }
            return Either.right(null);
          });
    }
    final var error = ErrorBuilder.builder()
        .errorMessage("Checklist object id is null for this request")
        .field(null)
        .build();

    return CompletableFuture.completedStage(Either.left(ErrorsBuilder.builder()
        .errors(Set.of(error))
        .errorType(ErrorType.PARENT_ID_FIELD_IS_NULL)
        .build()));
  }
}

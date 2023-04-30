package com.raunlo.checklist.validator;

import com.raunlo.checklist.core.entity.error.ErrorBuilder;
import com.raunlo.checklist.core.entity.error.ErrorType;
import com.raunlo.checklist.core.entity.error.Errors;
import com.raunlo.checklist.core.entity.error.ErrorsBuilder;
import com.raunlo.checklist.core.repository.ChecklistRepository;
import io.vavr.control.Either;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.SneakyThrows;

@ApplicationScoped
public class ChecklistItemValidator implements
    com.raunlo.checklist.core.validator.ChecklistItemValidator {

  private final ChecklistRepository checklistRepository;

  @Inject
  public ChecklistItemValidator(ChecklistRepository checklistRepository) {
    this.checklistRepository = checklistRepository;
  }

  @Override
  @SneakyThrows
  public Either<Errors, Void> validateChecklistExistence(Long id) {

    if (id != null) {

        Boolean exists = checklistRepository.exists(id);

        if (!exists) {
          final var error = ErrorBuilder.builder()
              .errorMessage("Checklist object doesn't exists with id: " + id)
              .field(null)
              .build();

          return Either.left(ErrorsBuilder.builder()
              .errors(Set.of(error))
              .errorType(ErrorType.PARENT_ID_MISSING)
              .build());
        } else {
          return Either.right(null);
        }

    } else {
      final var error = ErrorBuilder.builder()
          .errorMessage("Checklist object id is null for this request")
          .field(null)
          .build();

      return Either.left(ErrorsBuilder.builder()
          .errors(Set.of(error))
          .errorType(ErrorType.PARENT_ID_FIELD_IS_NULL)
          .build());
    }
  }
}

package com.raunlo.checklist.validator;

import com.raunlo.checklist.core.entity.error.Error;
import com.raunlo.checklist.core.entity.error.ErrorBuilder;
import com.raunlo.checklist.core.entity.error.ErrorType;
import com.raunlo.checklist.core.entity.error.Errors;
import com.raunlo.checklist.core.entity.error.ErrorsBuilder;
import com.raunlo.checklist.core.validator.BeanValidator;
import io.vavr.control.Either;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class BeanBeanValidatorImpl implements BeanValidator {

    private final Validator validator;

    @Inject
    public BeanBeanValidatorImpl(Validator validatorBean) {
        this.validator = validatorBean;
    }

    @Override
    @ActivateRequestContext
    public <T> Either<Errors, Void> validate(T entity) {
        final Set<ConstraintViolation<T>> validationResult = validator.validate(entity);
        final Set<Error> errors = validationResult.stream()
                .map(violation ->
                        ErrorBuilder.builder()
                                .errorMessage(violation.getMessage())
                                .field(violation.getPropertyPath().toString())
                                .build())
                .collect(Collectors.toSet());

        if (errors.isEmpty()) {
            return Either.right(null);
        } else {
            final var errorsEither = Either.<Errors, Void>left(
                ErrorsBuilder.builder()
                    .errorType(ErrorType.VALIDATION_ERROR)
                    .errors(errors)
                    .build());

            return errorsEither;
        }
    }
}

package com.raunlo.checklist.core.entity.error;

import io.soabase.recordbuilder.core.RecordBuilder;
import java.util.Set;

@RecordBuilder
public record Errors(ErrorType errorType, Set<Error> errors) {

}

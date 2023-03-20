package com.raunlo.checklist.resource.dto.error;


import io.soabase.recordbuilder.core.RecordBuilder;
import java.util.Set;

@RecordBuilder
public record ClientErrorsDto(Set<ClientErrorDto> errors) {
}

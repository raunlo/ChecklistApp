package com.raunlo.checklist.resource.dto.error;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record ClientErrorDto(String reason, String fieldName){
}

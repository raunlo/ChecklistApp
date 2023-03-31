package com.raunlo.checklist.core.entity.error;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record Error(String errorMessage, String field) {
}

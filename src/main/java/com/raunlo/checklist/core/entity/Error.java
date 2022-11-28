package com.raunlo.checklist.core.entity;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record Error(String errorMessage, ErrorType errorType) {

}

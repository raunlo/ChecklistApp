package com.raunlo.checklist.resource.dto;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record ErrorDto(int errorCode, String reason){
}

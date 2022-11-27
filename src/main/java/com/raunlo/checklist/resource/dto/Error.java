package com.raunlo.checklist.resource.dto;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record Error(int errorCode, String reason){
}

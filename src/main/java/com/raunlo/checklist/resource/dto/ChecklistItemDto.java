package com.raunlo.checklist.resource.dto;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record ChecklistItemDto(Long id, String name, boolean completed, String description) implements
    Identifier {

}


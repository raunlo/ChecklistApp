package com.raunlo.checklist.resource.dto;

import com.raunlo.checklist.resource.dto.Identifier;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record ChecklistItemDto(Long id, String name, boolean completed, String description) implements
    Identifier {

}


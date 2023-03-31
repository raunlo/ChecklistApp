package com.raunlo.checklist.resource.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import java.util.Collection;

@RecordBuilder
public record ChecklistDto(String name, Long id,
                           Collection<ChecklistItemDto> checklistItemDtos) implements Identifier {

}

package com.raunlo.checklist.resource.mapper;

import com.raunlo.checklist.core.entity.ChecklistItem;
import com.raunlo.checklist.resource.dto.item.ChecklistItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ChecklistItemMapper {

    @Mapping(target = "order", ignore = true)
    ChecklistItem map(ChecklistItemDto checklistItemDto);

    ChecklistItemDto map(ChecklistItem checklistItem);
}

package com.raunlo.checklist.persistence.mapper;

import com.raunlo.checklist.core.entity.ChecklistItem;
import com.raunlo.checklist.persistence.model.ChecklistItemsDbo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper()
public interface ChecklistItemDboMapper {
    @Mapping(target = "nextItemId", source = "nextItemId")
    @Mapping(source = "taskName", target = "name")
    @Mapping(source = "taskCompleted", target = "completed")
    ChecklistItem map(ChecklistItemsDbo checklistItemsDbos);

    @Mapping(source = "baseItem.name", target = "taskName")
    @Mapping(source = "baseItem.completed", target = "taskCompleted")
    @Mapping(target = "nextItemId", source = "nextItemId")
    ChecklistItemsDbo map(ChecklistItem baseItem);
}

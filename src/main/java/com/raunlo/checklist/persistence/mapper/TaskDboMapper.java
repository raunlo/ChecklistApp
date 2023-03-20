package com.raunlo.checklist.persistence.mapper;

import com.raunlo.checklist.core.entity.BaseItem;
import com.raunlo.checklist.persistence.model.TaskDbo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TaskDboMapper {
    @Mapping(source = "taskName", target = "name")
    @Mapping(source = "taskCompleted", target = "completed")
    BaseItem map(TaskDbo taskDbo);

    @Mapping(source = "baseItem.name", target = "taskName")
    @Mapping(source = "baseItem.completed", target = "taskCompleted")

    //@Mapping(source = "checklistId", target = "checklistDbo.id")
    TaskDbo map(BaseItem baseItem);
}

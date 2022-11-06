package com.raunlo.checklist.persistence.mapper;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.persistence.model.ChecklistDbo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(uses = TaskMapper.class, nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface ChecklistMapper {

    @Mapping(target = "taskDbos", source = "tasks", defaultExpression = "java(new ArrayList<>())")
    ChecklistDbo map(Checklist checklist);

    @Mapping(target = "withTasks", ignore = true)
    @Mapping(target = "withName", ignore = true)
    @Mapping(target = "withId", ignore = true)
    @Mapping(target = "tasks", source = "checklistDbo.taskDbos", defaultExpression = "java(new ArrayList<>())")
    @Mapping(target = "name", source = "checklistDbo.name")
    @Mapping(target = "id", source = "checklistDbo.id")
    Checklist map(ChecklistDbo checklistDbo);


}

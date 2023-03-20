package com.raunlo.checklist.persistence.mapper;

import com.raunlo.checklist.core.entity.list.ItemList;
import com.raunlo.checklist.persistence.model.ChecklistDbo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(uses = TaskDboMapper.class, nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface ChecklistMapper {

    @Mapping(target = "taskDbos", source = "getItems", defaultExpression = "java(new ArrayList<>())")
    ChecklistDbo map(ItemList itemList);

    @Mapping(target = "withBaseItems", ignore = true)
    @Mapping(target = "withName", ignore = true)
    @Mapping(target = "withId", ignore = true)
    @Mapping(target = "tasks", source = "checklistDbo.taskDbos", defaultExpression = "java(new ArrayList<>())")
    @Mapping(target = "name", source = "checklistDbo.name")
    @Mapping(target = "id", source = "checklistDbo.id")
    ItemList map(ChecklistDbo checklistDbo);


}

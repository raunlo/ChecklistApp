package com.raunlo.checklist.persistence.mapper;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.persistence.model.ChecklistDbo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(uses = ChecklistItemDboMapper.class, nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface ChecklistDboMapper {

    @Mapping(target = "checklistItemsDbos", source = "checklistItems", defaultExpression = "java(new ArrayList<>())")
    ChecklistDbo map(Checklist checklist);


    @Mapping(target = "checklistItems", source = "checklistDbo.checklistItemsDbos", defaultExpression = "java(new ArrayList<>())")
    @Mapping(target = "name", source = "checklistDbo.name")
    @Mapping(target = "id", source = "checklistDbo.id")
    Checklist map(ChecklistDbo checklistDbo);
}

package com.raunlo.checklist.resource.mapper;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.resource.dto.ChecklistDto;
import java.util.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(uses = ChecklistItemDtoMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface ChecklistDtoMapper {

  @Mapping(target = "checklistItems", ignore = true, defaultExpression = "java(new ArrayList<>())")
  Checklist map(ChecklistDto checklistDto);

  @Mapping(target = "checklistItemDtos", source = "checklistItems", defaultExpression = "java(new ArrayList<>())")
  ChecklistDto map(Checklist checklistDto);

  default Collection<ChecklistDto> map(Collection<Checklist> checklists) {
    return checklists.stream()
        .map(this::map)
        .toList();
  }
}

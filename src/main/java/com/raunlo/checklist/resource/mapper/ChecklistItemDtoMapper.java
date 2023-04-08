package com.raunlo.checklist.resource.mapper;

import com.raunlo.checklist.core.entity.ChecklistItem;
import com.raunlo.checklist.resource.dto.ChecklistItemDto;
import java.util.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ChecklistItemDtoMapper {

  @Mapping(target = "nextItemId", ignore = true)
  @Mapping(target = "id", source = "id")
  ChecklistItem map(ChecklistItemDto checklistItemDto, Long id);

  default ChecklistItem map(ChecklistItemDto checklistItemDto) {
    return map(checklistItemDto, null);
  }


  @Mapping(target = "description", ignore = true)
  ChecklistItemDto map(ChecklistItem checklistItem);

  default Collection<ChecklistItemDto> map(Collection<ChecklistItem> checklistItems) {
    return checklistItems.stream()
        .map(this::map)
        .toList();
  }
}

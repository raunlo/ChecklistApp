package com.raunlo.checklist.resource.mapper;

import com.raunlo.checklist.core.entity.ExistingItem;
import com.raunlo.checklist.resource.dto.item.ExistingItemDto;
import org.mapstruct.Mapper;

@Mapper
public interface ExistingItemMapper {

  ExistingItemDto map(ExistingItem existingItem);

  ExistingItem map(ExistingItemDto existingItemDto);
}

package com.raunlo.checklist.config;

import com.raunlo.checklist.persistence.mapper.ChecklistDboMapper;
import com.raunlo.checklist.persistence.mapper.ChecklistItemDboMapper;
import com.raunlo.checklist.resource.mapper.ChecklistDtoMapper;
import com.raunlo.checklist.resource.mapper.ChecklistItemDtoMapper;
import com.raunlo.checklist.resource.mapper.ChecklistItemFilterMapper;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.mapstruct.factory.Mappers;

@Dependent
public class MappersApplicationConfig {

  @Produces
  @Singleton
  public ChecklistDtoMapper checklistDtoMapper() {
    return Mappers.getMapper(ChecklistDtoMapper.class);
  }

  @Produces
  @Singleton
  public ChecklistItemDboMapper checklistItemDboMapper() {
    return Mappers.getMapper(ChecklistItemDboMapper.class);
  }

  @Produces
  @Singleton
  public ChecklistDboMapper checklistDboMapper() {
    return Mappers.getMapper(ChecklistDboMapper.class);
  }

  @Produces
  @Singleton
  public ChecklistItemFilterMapper taskFilterMapper() {
    return Mappers.getMapper(ChecklistItemFilterMapper.class);
  }

  @Produces
  @Singleton
  public ChecklistItemDtoMapper taskMapper() {
    return Mappers.getMapper(ChecklistItemDtoMapper.class);
  }
}

package com.raunlo.checklist.config;

import com.raunlo.checklist.persistence.mapper.ChecklistMapper;
import com.raunlo.checklist.persistence.mapper.TaskDboMapper;
import com.raunlo.checklist.resource.mapper.ChecklistItemFilterMapper;
import com.raunlo.checklist.resource.mapper.ChecklistItemMapper;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import org.mapstruct.factory.Mappers;

@Dependent
public class MappersApplicationConfig {

  @Produces
  @Singleton
  public TaskDboMapper taskDboMapper() {
    return Mappers.getMapper(TaskDboMapper.class);
  }

  @Produces
  @Singleton
  public ChecklistMapper checklistDboMapper() {
    return Mappers.getMapper(ChecklistMapper.class);
  }

  @Produces
  @Singleton
  public ChecklistItemFilterMapper taskFilterMapper() { return Mappers.getMapper(
    ChecklistItemFilterMapper.class); }

  @Produces
  @Singleton
  public ChecklistItemMapper taskMapper() { return Mappers.getMapper(ChecklistItemMapper.class); }
}

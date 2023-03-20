package com.raunlo.checklist.resource.dto.item;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public abstract class BaseItemDto implements Identifier{

  private Long id;
  private String name;
  private boolean completed;
  private String description;
}

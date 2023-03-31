package com.raunlo.checklist.core.entity.internal;

import com.raunlo.checklist.core.entity.ChecklistItem;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ItemsInOrderBounds<T extends ChecklistItem> {

  private List<T> items;
  private long oldOrderNumber;
  private long newOrderNumber;


}

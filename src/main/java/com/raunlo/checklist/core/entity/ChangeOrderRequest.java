package com.raunlo.checklist.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class ChangeOrderRequest {
    private long oldOrderNumber;
    private long newOrderNumber;
    private long checklistId;
}

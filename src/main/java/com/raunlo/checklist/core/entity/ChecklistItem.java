package com.raunlo.checklist.core.entity;

import com.raunlo.checklist.core.entity.error.ErrorMessages;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@With
public class ChecklistItem {

    private Long id;
    @NotNull(message = "Name cannot be null")
    @Size(max=900, message = ErrorMessages.ITEM_NAME_IS_TOO_LONG)
    private String name;
    private Long nextItemId;
    private boolean completed = false;
}

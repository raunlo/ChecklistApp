package com.raunlo.checklist.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@With
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {



    private Long id;
    @NotNull(message = "Name cannot be null")
    @Size(max=900, message = Errors.TASK_NAME_IS_TOO_LONG)
    private String name;
    private String description;
    private boolean completed = false;
    @JsonIgnore
    private long order;
}

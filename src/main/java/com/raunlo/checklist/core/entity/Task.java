package com.raunlo.checklist.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String name;
    private String description;
    private boolean completed = false;
    @JsonIgnore
    private long order;
}

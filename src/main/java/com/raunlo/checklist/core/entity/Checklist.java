package com.raunlo.checklist.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@With
@AllArgsConstructor
@NotNull
@NoArgsConstructor
public class Checklist {
    private Collection<Task> tasks;
    private Long id;
    private String name;
}

package com.raunlo.checklist.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@With
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "checklist")
public class ChecklistDbo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "checklist_id")
    private Long id;

    @Column(name = "checklist_name")
    @Size(max = 255)
    @NotNull
    private String name;

    @OneToMany
    private List<TaskDbo> tasks;
}

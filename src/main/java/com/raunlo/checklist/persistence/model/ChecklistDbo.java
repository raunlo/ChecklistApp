package com.raunlo.checklist.persistence.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static com.raunlo.checklist.persistence.model.ChecklistDbo.TABLE_NAME;

@Entity(name = "ChecklistDbo")
@With
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = TABLE_NAME)
public class ChecklistDbo {
    static final String TABLE_NAME = "CHECKLIST";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = "checklist_sequence", allocationSize = 1)
    @Column(name = "checklist_id")
    private Long id;

    @Column(name = "checklist_name", nullable = false)
    @Size(max = 255)
    @NotNull
    private String name;

    @OneToMany(mappedBy = "checklistDbo", fetch = FetchType.EAGER)
    private List<TaskDbo> tasks;
}

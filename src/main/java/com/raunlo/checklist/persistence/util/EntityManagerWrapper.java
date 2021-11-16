package com.raunlo.checklist.persistence.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EntityManager;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityManagerWrapper {
    private EntityManager entityManager;
}

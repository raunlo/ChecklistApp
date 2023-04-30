package com.raunlo.checklist.persistence.dao;

import com.raunlo.checklist.persistence.model.ChecklistDbo;
import java.util.List;
import java.util.Optional;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

@RegisterConstructorMapper(ChecklistDbo.class)
public interface PostgresChecklistDao {

  @SqlQuery("""
              SELECT c.checklist_id as checklist_id, c.checklist_name as checklist_name
              FROM checklist as c
              ORDER BY checklist_name
      """)
  List<ChecklistDbo> getAllChecklistDbos();

  @SqlUpdate("INSERT INTO CHECKLIST(CHECKLIST_ID, CHECKLIST_NAME) VALUES(nextval('checklist_sequence'), :checklist.name)")
  @GetGeneratedKeys
  @Transaction(TransactionIsolationLevel.SERIALIZABLE)
  ChecklistDbo save(@BindMethods("checklist") ChecklistDbo checklistDbo);

  @SqlQuery("""
      SELECT COUNT(1) > 0 FROM CHECKLIST WHERE CHECKLIST_ID = :id
          """)
  Boolean checklistExists(@Bind("id") long checklistId);

  @SqlUpdate("""
      UPDATE CHECKLIST SET CHECKLIST_NAME = :checklist.name where CHECKLIST_ID = :checklist.id
        """)
  @Transaction(TransactionIsolationLevel.SERIALIZABLE)
  void updateChecklist(@BindMethods("checklist") ChecklistDbo checklist);


  @SqlQuery("""
       SELECT c.checklist_id as checklist_id, c.checklist_name as checklist_name
          FROM checklist as c
          WHERE c.checklist_id = :id
          ORDER BY checklist_name
      """)
  Optional<ChecklistDbo> findById(@Bind("id") long id);


  @SqlUpdate("DELETE from CHECKLIST where CHECKLIST_ID = :id")
  void delete(@Bind("id") long id);
}

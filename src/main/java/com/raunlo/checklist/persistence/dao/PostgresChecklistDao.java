package com.raunlo.checklist.persistence.dao;

import com.raunlo.checklist.persistence.model.ChecklistDbo;
import com.raunlo.checklist.persistence.model.ChecklistItemsDbo;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowReducer;
import org.jdbi.v3.sqlobject.transaction.Transaction;

@RegisterConstructorMapper(ChecklistItemsDbo.class)
@RegisterConstructorMapper(ChecklistDbo.class)
public interface PostgresChecklistDao {

  @UseRowReducer(ChecklistRowReducer.class)
  @SqlQuery("""
              SELECT c.checklist_id as checklist_id, c.checklist_name as checklist_name, t.task_id as task_id,
              t.task_completed as task_completed, t.order_number as order_number
              FROM checklist as c
              LEFT JOIN TASK t on c.checklist_id = t.checklist_id
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
       SELECT c.checklist_id as checklist_id, c.checklist_name as checklist_name, t.task_id as task_id,
          t.task_completed as task_completed, t.order_number as order_number
          FROM checklist as c
          LEFT JOIN TASK t on c.checklist_id = t.checklist_id
          WHERE c.checklist_id = :id
          ORDER BY checklist_name
      """)
  Optional<ChecklistDbo> findById(@Bind("id") long id);


  @SqlUpdate("DELETE from CHECKLIST where CHECKLIST_ID = :id")
  void delete(@Bind("id") long id);


  class ChecklistRowReducer implements LinkedHashMapRowReducer<Long, ChecklistDbo> {

    @Override
    public void accumulate(Map<Long, ChecklistDbo> container, RowView rowView) {
      final ChecklistDbo checklist = container.computeIfAbsent(
          rowView.getColumn("checklist_id", Long.class),
          id -> rowView.getRow(ChecklistDbo.class));

      if (rowView.getColumn("task_id", Long.class) != null) {
        checklist.addTask(rowView.getRow(ChecklistItemsDbo.class));
      }
    }
  }
}

package com.raunlo.checklist.persistence.dao;

import com.raunlo.checklist.persistence.model.ChecklistDbo;
import com.raunlo.checklist.persistence.model.TaskDbo;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowReducer;

import java.util.List;
import java.util.Map;

@RegisterConstructorMapper(TaskDbo.class)
@RegisterConstructorMapper(ChecklistDbo.class)
public interface ChecklistDao {

    @UseRowReducer(ChecklistDao.ChecklistRowReducer.class)
    @SqlQuery("""
            SELECT c.checklist_id as checklist_id, c.checklist_name as checklist_name, t.task_id as task_id,
            t.task_completed as task_completed, t.order_number as order_number
            FROM checklist as c
            LEFT JOIN TASK t on c.checklist_id = t.checklist_id
            ORDER BY checklist_name
    """)
    List<ChecklistDbo> getAllChecklistDbos();

    @SqlUpdate("INSERT INTO CHECKLIST(CHECKLIST_ID, CHECKLIST_NAME) VALUES(nextval('checklist_sequence'), :checklistName)")
    @GetGeneratedKeys
    Long saveChecklist(@Bind("checklistName") String checklistName);


    class ChecklistRowReducer implements LinkedHashMapRowReducer<Long, ChecklistDbo> {

        @Override
        public void accumulate(Map<Long, ChecklistDbo> container, RowView rowView) {
            final ChecklistDbo checklist = container.computeIfAbsent(rowView.getColumn("checklist_id", Long.class),
                    id -> rowView.getRow(ChecklistDbo.class));

            if (rowView.getColumn("task_id", Long.class) != null) {
                checklist.addTask(rowView.getRow(TaskDbo.class));
            }
        }
    }

}

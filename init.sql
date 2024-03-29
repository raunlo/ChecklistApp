CREATE SEQUENCE IF NOT EXISTS checklist_sequence START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS CHECKLIST(
                                        checklist_id BIGINT PRIMARY KEY,
                                        checklist_name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS TASK(
                                   task_id BIGINT PRIMARY KEY,
                                   task_name VARCHAR(255) NOT NULL,
    task_completed BOOLEAN NOT NULL DEFAULT FALSE,
    checklist_id bigint not null,
    next_task bigint null default null,
    additional_comments VARCHAR(255),
    CONSTRAINT checklist_id_FK FOREIGN KEY(checklist_id) REFERENCES CHECKLIST(checklist_id) ON DELETE CASCADE
);

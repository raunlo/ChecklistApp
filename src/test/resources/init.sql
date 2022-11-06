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
    order_number bigint not null,
    additional_comments VARCHAR(255),
    CONSTRAINT checklist_id_FK FOREIGN KEY(checklist_id) REFERENCES CHECKLIST(checklist_id) ON DELETE CASCADE
);

INSERT INTO CHECKLIST(checklist_id, checklist_name) VALUES(nextval('checklist_sequence'), 'test')

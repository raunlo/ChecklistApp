/*CREATE SEQUENCE IF NOT EXISTS checklist_sequence START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS CHECKLIST
(
    ID   BIGINT PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS CHECKLIST_ITEM
(
    ID           BIGINT PRIMARY KEY,
    checklist_id bigint  not null,
    NAME         varchar not null,
    next_item_id    bigint  null default null,
    CONSTRAINT checklist_id_FK FOREIGN KEY (checklist_id) REFERENCES CHECKLIST (ID) ON DELETE CASCADE
);

CREATE TABLE TEMPLATE_CHECKLIST_ITEM
(
    ID bigint primary key,
    NAME VARCHAR NOT NULL
);


CREATE TABLE IF NOT EXISTS CHECKLIST_ITEM_ROW
(
    ID                BIGINT PRIMARY KEY,
    COMPLETED         BOOLEAN NOT NULL DEFAULT FALSE,
    CHECKLIST_ITEM_ID BIGINT,
    NAME              varchar,
    CONSTRAINT CHECKLIST_ITEM_ID_FK FOREIGN KEY (CHECKLIST_ITEM_ID) REFERENCES CHECKLIST_ITEM (ID) ON DELETE CASCADE,
    CONSTRAINT TEMPLATE_CHECKLIST_ITEM_ID_FK FOREIGN KEY (CHECKLIST_ITEM_ID) REFERENCES TEMPLATE_CHECKLIST_ITEM (ID) ON DELETE CASCADE
);


*/


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

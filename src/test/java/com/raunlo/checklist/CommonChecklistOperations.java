package com.raunlo.checklist;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.resource.dto.ChecklistDto;
import com.raunlo.checklist.resource.dto.ChecklistItemDto;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collection;

public class CommonChecklistOperations {

    private static final String BASE_PATH = "/api/v1/checklist/";
    @Inject
    WebTarget webTarget;

    public ChecklistDto createChecklist(ChecklistDto itemList) {
        final Response response = webTarget.path(BASE_PATH)
                .request()
                .post(Entity.entity(itemList, MediaType.APPLICATION_JSON_TYPE));
        assertThat(response.getStatus()).isEqualTo(201);
        final ChecklistDto savedItemList = response.readEntity(ChecklistDto.class);
        assertThat(savedItemList.id()).isNotNull();
        assertThat(savedItemList.name()).isEqualTo(itemList.name());
        assertThat(savedItemList.checklistItemDtos()).matches(tasks -> tasks.size() == 0);
        assertThat(webTarget.getUri().toString() + BASE_PATH + savedItemList.id())
                .isEqualTo(response.getHeaderString("Location").replace("127.0.0.1", "localhost"));

        return savedItemList;
    }


    public Response createTask(long checklistId, ChecklistItemDto task) {
        return webTarget.path(BASE_PATH + checklistId + "/task")
                .request()
                .post(Entity.entity(task, MediaType.APPLICATION_JSON_TYPE));
    }


    public ChecklistItemDto successfullyCreatesTask(long checklistId, ChecklistItemDto task) {
            try (final Response createTaskResponse = createTask(checklistId, task)){
                final ChecklistItemDto createdTask = createTaskResponse.readEntity(ChecklistItemDto.class);
                assertThat(createTaskResponse.getStatus()).isEqualTo(201);
                assertThat(createdTask.id()).isNotNull();
                assertThat(createdTask.name()).isEqualTo(task.name());
                assertThat(createdTask.completed()).isEqualTo(task.completed());
                assertThat(webTarget.getUri().toString() + BASE_PATH + checklistId + "/task/" + createdTask.id())
                    .isEqualTo(createTaskResponse.getHeaderString("Location").replace("127.0.0.1", "localhost"));

                return createdTask;
        }
    }

    public Response getTask(long checklistId, long taskId) {
        return webTarget.path(BASE_PATH + checklistId + "/task/" + taskId)
                .request()
                .get();
    }

    public void deleteTask(long checklistId, long taskId) {
        final Response response = webTarget.path(BASE_PATH + checklistId + "/task/" + taskId)
                .request()
                .delete();

        assertThat(response.getStatus()).isEqualTo(204);
    }

    public Collection<ChecklistItemDto> getAllTasks(long checklistId, TaskPredefinedFilter taskPredefinedFilter) {
        WebTarget getTasksWebTarget = webTarget.path(BASE_PATH + checklistId + "/task");
        if (taskPredefinedFilter != null) {
            getTasksWebTarget = getTasksWebTarget.queryParam("filterType",
                    taskPredefinedFilter.name().toLowerCase());
        }

        final Response response = getTasksWebTarget
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);

        return response.readEntity(new GenericType<>() {
        });
    }

    public ChecklistItemDto updateTask(long checklistId, ChecklistItemDto task) {
        try (final Response response = webTarget.path(BASE_PATH + checklistId + "/task/" + task.id())
                .request()
                .put(Entity.entity(task, MediaType.APPLICATION_JSON_TYPE))) {

            assertThat(response.getStatus()).isEqualTo(200);
            return response.readEntity(ChecklistItemDto.class);
        }
    }
}

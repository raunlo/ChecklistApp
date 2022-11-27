package com.raunlo.checklist;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.entity.Task;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CommonChecklistOperations {

    private static final String BASE_PATH = "/api/v1/checklist/";
    @Inject
    WebTarget webTarget;

    public Checklist createChecklist(Checklist checklist) {
        final Response response = webTarget.path(BASE_PATH)
                .request()
                .post(Entity.entity(checklist, MediaType.APPLICATION_JSON_TYPE));
        assertThat(response.getStatus() == 201);
        final Checklist savedChecklist = response.readEntity(Checklist.class);
        assertThat(savedChecklist.getId()).isNotNull();
        assertThat(savedChecklist.getName()).isEqualTo(checklist.getName());
        assertThat(savedChecklist.getTasks()).matches(tasks -> tasks.size() == 0);
        assertThat(webTarget.getUri().toString() + BASE_PATH + savedChecklist.getId())
                .isEqualTo(response.getHeaderString("Location").replace("127.0.0.1", "localhost"));

        return savedChecklist;
    }

    public Task successfullyCreatesTask(long checklistId, Task task) {
        final Response response = webTarget.path(BASE_PATH + checklistId + "/task")
                .request()
                .post(Entity.entity(task, MediaType.APPLICATION_JSON_TYPE));
        final Task createdTask = response.readEntity(Task.class);

        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(createdTask.getId()).isNotNull();
        assertThat(createdTask.getName()).isEqualTo(task.getName());
        assertThat(createdTask.isCompleted()).isEqualTo(task.isCompleted());
        assertThat(webTarget.getUri().toString() + BASE_PATH + checklistId + "/task/" + createdTask.getId());

        return createdTask;
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

    public Collection<Task> getAllTasks(long checklistId, TaskPredefinedFilter taskPredefinedFilter) {
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

    public Task updateTask(long checklistId, Task task) {
        final Response response = webTarget.path(BASE_PATH + checklistId + "/task/" + task.getId())
                .request()
                .put(Entity.entity(task, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(200);
        return response.readEntity(Task.class);
    }
}

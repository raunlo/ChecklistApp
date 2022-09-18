package com.raunlo.checklist.resource.v1;

import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.Task;
import com.raunlo.checklist.core.service.TaskService;
import com.raunlo.checklist.resource.BaseResource;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("api/v1/checklist/{checklist_id}/task")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TaskResource implements BaseResource {

    private final TaskService taskService;

    @PathParam("checklist_id")
    private Long checklistId;

    @Inject()
    public TaskResource(final TaskService taskService) {
        this.taskService = taskService;
    }

    @GET()
    public CompletionStage<Response> getTasks() {
        return taskService.getAll(checklistId)
                .thenApply(tasks -> Response.status(200).entity(tasks).build());
    }

    @GET()
    @Path("/{task_id}")
    public CompletionStage<Response> findTaskById(@PathParam("task_id") int taskId) {
        return taskService.findById(checklistId, taskId)
                .thenApply(this::createResponse);
    }

    @POST()
    public CompletionStage<Response> saveTask(@NotNull @Valid Task task, @Context UriInfo uriInfo) {
        return taskService.save(checklistId, task)
                .thenApply(savedTask -> {
                    final URI getResourceURI = uriInfo.getAbsolutePathBuilder().path(String.valueOf(savedTask.getId())).build();
                    return Response.created(getResourceURI).entity(savedTask)
                            .build();
                });
    }

    @PUT
    @Path("/{id}")
    public CompletionStage<Response> updateTask(@NotNull @Valid Task task, @PathParam("id") long taskId) {
        task.setId(taskId);
        return taskService.update(checklistId, task)
                .thenApply(updatedTask -> Response.ok().entity(updatedTask).build());
    }

    @DELETE
    @Path("/{id}")
    public CompletionStage<Response> deleteTask(@PathParam("id") int id) {
        return taskService.delete(checklistId, id)
                .thenApply((__) -> Response.noContent().build());
    }

    @PATCH
    @Path("/change-order")
    public CompletionStage<Response> changeTaskOrder(ChangeOrderRequest changeOrderRequest) {
        changeOrderRequest.setChecklistId(checklistId);
        return taskService.changeOrder(changeOrderRequest)
                .thenApply((__) -> Response.status(200).build());
    }

    @POST
    @Path("/save-multiple")
    public CompletionStage<Response> saveMultiple(@NotNull @Valid List<Task> taskList) {
        return taskService.saveAll(taskList, checklistId)
                .thenApply((final Collection<Task> tasks) -> Response.status(200).entity(tasks).build());
    }
}

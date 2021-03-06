package com.raunlo.checklist.resource.v1;

import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.Task;
import com.raunlo.checklist.core.service.TaskService;
import com.raunlo.checklist.resource.BaseResource;

import java.net.URI;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("api/v1/checklist/{checklist_id}/task")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
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
    public CompletionStage<Response> updateTask(@NotNull @Valid Task task) {
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
}

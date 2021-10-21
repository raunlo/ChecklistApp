package com.raunlo.checklist.resource.v1;

import com.raunlo.checklist.core.entity.Task;
import com.raunlo.checklist.core.service.TaskService;
import com.raunlo.checklist.resource.BaseResource;
import java.net.URI;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("v1/checklist/tasks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped()
public class TaskResource implements BaseResource {

    private final TaskService taskService;

    @Inject()
    public TaskResource(final TaskService taskService) {
        this.taskService = taskService;
    }

    @GET()
    public Response getTasks() {
        return Response.status(200).entity(taskService.getAll()).build();
    }

    @GET()
    @Path("/{id}")
    @Operation(
        summary = "Returns Task",
        description = "Finds task by id"
    )
    @APIResponse(
        description = "Task json",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,

        sc)
    )
    public Response findTaskById(@PathParam("id") int id) {
        return createResponse(taskService.findById(id));
    }

    @POST()
    public Response saveTask(@NotNull @Valid Task task, @Context UriInfo uriInfo) {
        final Task savedTask = taskService.save(task);
        final URI getResourceURI = uriInfo.getAbsolutePathBuilder().path(String.valueOf(savedTask.getId())).build();
        return Response.created(getResourceURI).entity(savedTask)
            .build();
    }

    @PATCH()
    public Response updateTask(@NotNull @Valid Task task) {
        final Task updatedTask = taskService.update(task);
        return Response.ok().entity(updatedTask)
            .build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTask(@PathParam("id") int id) {
        taskService.delete(id);
        return Response.noContent().build();
    }
}

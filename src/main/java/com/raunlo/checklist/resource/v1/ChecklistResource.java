package com.raunlo.checklist.resource.v1;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.service.ChecklistService;
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
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/v1/checklist/{id}")
@RequestScoped()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChecklistResource implements BaseResource {

    private final ChecklistService checklistService;

    @Inject()
    public ChecklistResource(final ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    @GET()
    @Operation(description = "Finds checklist by id")
    @APIResponse(
        description = "Returns checklist",
        content = @Content(
            schema = @Schema(implementation = Checklist.class)
        ),
        responseCode = "200"
    )
    @APIResponse(
        description = "Checklist not found",
        responseCode = "404"
    )
    @Path("/{id}")
    public Response getChecklistById(@PathParam("id") int id) {
        return createResponse(checklistService.findById(id));
    }

    @GET()
    @Operation(description = "find all checklists")
    @APIResponse(
        description = "Finds all checklists",
        content = @Content(
            schema = @Schema(implementation = Checklist.class)
        ),
        responseCode = "200"
    )
    public Response getAllCheckLists() {
        return Response.status(200).entity(checklistService.getAll()).build();
    }

    @POST()
    @Operation(description = "Saves task")
    @APIResponse(
        description = "Saves task",
        content = @Content(
            schema = @Schema(implementation = Checklist.class)
        ),
        responseCode = "201",
        headers = @Header(
            name = "location",
            description = "URI, what returns saved object"
        )
    )
    public Response saveTask(@NotNull @Valid Checklist checklist, @Context UriInfo uriInfo) {
        final Checklist savedCheckList = checklistService.save(checklist);
        final URI getResourceURI = uriInfo.getAbsolutePathBuilder().path(String.valueOf(savedCheckList.getId())).build();
        return Response.created(getResourceURI).entity(savedCheckList).build();
    }

    @PATCH()
    @Operation(description = "Updates task")
    @APIResponse(
        description = "Updates task",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Checklist.class)
        ),
        responseCode = "200"
    )
    public Response updateTask(@NotNull @Valid Checklist checklist) {
        final Checklist updatedChecklist = checklistService.update(checklist);
        return Response.ok().entity(updatedChecklist).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(description = "Deletes Task")
    @APIResponse(
        description = "Deletes task",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Checklist.class)
        ),
        responseCode = "204"
    )
    public Response deleteTask(@PathParam("id") int id) {
        checklistService.delete(id);
        return Response.noContent().build();
    }
}

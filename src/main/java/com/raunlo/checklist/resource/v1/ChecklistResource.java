package com.raunlo.checklist.resource.v1;

import com.raunlo.checklist.core.entity.list.ItemList;
import com.raunlo.checklist.core.service.ChecklistService;
import com.raunlo.checklist.resource.BaseResource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.concurrent.CompletionStage;

@Path("api/v1/checklist")
@ApplicationScoped()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChecklistResource extends BaseResource {

    private final ChecklistService checklistService;

    @Inject()
    public ChecklistResource(final ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    @GET()
    @Path("/{id}")
    public CompletionStage<Response> getChecklistById(@PathParam("id") int id) {
        return checklistService.findById(id)
                .thenApply(this::createResponse);
    }

    @GET()
    public CompletionStage<Response> getAllCheckLists() {
        return checklistService.getAll()
                .thenApply(checklists -> Response.status(200).entity(checklists).build());
    }

    @POST()
    public CompletionStage<Response> saveChecklist(@NotNull @Valid ItemList itemList, @Context UriInfo uriInfo) {
        return checklistService.save(itemList)
                .thenApply(savedChecklist -> {
                    final URI getResourceURI = uriInfo.getAbsolutePathBuilder().path(String.valueOf(savedChecklist.getId())).build();
                    return Response.created(getResourceURI).entity(savedChecklist).build();
                });
    }

    @PATCH()
    @Path("/{id}")
    public CompletionStage<Response> updateTask(@NotNull @Valid ItemList itemList, @PathParam("id") Long id) {
        itemList.setId(id);
        return checklistService.update(itemList)
                .thenApply(updatedChecklist -> Response.ok().entity(updatedChecklist).build());
    }

    @DELETE
    @Path("/{id}")
    public CompletionStage<Response> deleteChecklist(@PathParam("id") int id) {
        return checklistService.delete(id)
                .thenApply((__) -> Response.noContent().build());
    }
}

package com.raunlo.checklist.resource.v1;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.service.ChecklistService;
import com.raunlo.checklist.resource.BaseResource;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
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

import io.helidon.security.Principal;
import io.helidon.security.annotations.Authenticated;
import io.helidon.security.annotations.Authorized;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/v1/checklist")
@RequestScoped()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated()
public class ChecklistResource implements BaseResource {

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
    public CompletionStage<Response> getAllCheckLists(@Context io.helidon.security.SecurityContext securityContext) {
        Optional<Principal> principal = securityContext.userPrincipal();
        return checklistService.getAll()
                .thenApply(checklists -> Response.status(200).entity(checklists).build());
    }

    @POST()
    public CompletionStage<Response> saveChecklist(@NotNull @Valid Checklist checklist, @Context UriInfo uriInfo) {
        return checklistService.save(checklist)
                .thenApply(savedChecklist -> {
                    final URI getResourceURI = uriInfo.getAbsolutePathBuilder().path(String.valueOf(savedChecklist.getId())).build();
                    return Response.created(getResourceURI).entity(savedChecklist).build();
                });
    }

    @PATCH()
    @Path("/{id}")
    public CompletionStage<Response> updateTask(@NotNull @Valid Checklist checklist, @PathParam("id") Long id) {
        checklist.setId(id);
        return checklistService.update(checklist)
                .thenApply(updatedChecklist -> Response.ok().entity(updatedChecklist).build());
    }

    @DELETE
    @Path("/{id}")
    public CompletionStage<Response> deleteChecklist(@PathParam("id") int id) {
        return checklistService.delete(id)
                .thenApply((__) -> Response.noContent().build());
    }
}

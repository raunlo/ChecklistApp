package com.raunlo.checklist.resource;

import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("api/wake/me/up")
public class WakeMeUpResource {

    @HEAD
    public Response wakeMeUp() {
        return Response.status(200).build();
    }
}

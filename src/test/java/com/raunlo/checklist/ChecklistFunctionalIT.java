package com.raunlo.checklist;


import com.raunlo.checklist.core.entity.Checklist;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;


import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@HelidonTest
@Configuration(configSources = "application.yaml")
public class ChecklistFunctionalIT {

    @Inject
    private WebTarget webTarget;

    @Test
    public void test() {
        Checklist checklist = new Checklist()
                .withName("test checklist");
        final Response response = webTarget.path("/api/v1/checklist")
                .request()
                .post(Entity.entity(checklist, MediaType.APPLICATION_JSON_TYPE));
        assertThat(response.getStatus() == 201);
        final Checklist responseAsEntity = response.readEntity(Checklist.class);
        assertThat(responseAsEntity.getId()).isNotNull();
        assertThat(responseAsEntity.getName().equals(checklist.getName()));
        assertThat(responseAsEntity.getTasks()).matches(tasks -> tasks.size() == 0);

        final Response getResponse = webTarget.path("/api/v1/checklist")
                .request()
                .get();

        final Collection<Checklist> checklists = getResponse.readEntity(new GenericType<Collection<Checklist>>() {
        });

        System.out.println(checklists);
    }
}

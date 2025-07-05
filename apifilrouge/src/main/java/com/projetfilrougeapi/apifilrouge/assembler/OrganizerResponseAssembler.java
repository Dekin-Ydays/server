package com.projetfilrougeapi.apifilrouge.assembler;

import com.projetfilrougeapi.apifilrouge.DTO.OrganizerResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class OrganizerResponseAssembler implements RepresentationModelAssembler<OrganizerResponse, EntityModel<OrganizerResponse>> {
    @Override
    public EntityModel<OrganizerResponse> toModel(OrganizerResponse organizer) {
        return EntityModel.of(
                organizer,
                linkTo(methodOn(UserController.class).getUserById(organizer.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getEventsForUser(organizer.getId(),null)).withRel("events")
        );
    }
}

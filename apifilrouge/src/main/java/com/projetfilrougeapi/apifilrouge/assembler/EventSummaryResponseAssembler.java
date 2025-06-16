package com.projetfilrougeapi.apifilrouge.assembler;

import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Cette classe est un composant Spring dont le seul rôle est de convertir
 * un DTO EventSummaryResponse en un EntityModel avec les liens HATEOAS appropriés.
 * C'est une meilleure pratique qui sépare les responsabilités et nettoie le code du service.
 */
@Component
public class EventSummaryResponseAssembler implements RepresentationModelAssembler<EventSummaryResponse, EntityModel<EventSummaryResponse>> {

    @Override
    public EntityModel<EventSummaryResponse> toModel(EventSummaryResponse dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(EventController.class).getEventById(dto.getId())).withSelfRel());
    }
}

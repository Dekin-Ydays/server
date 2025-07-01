package com.projetfilrougeapi.apifilrouge.assembler;

import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.DTO.PlaceResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PlaceResponseAssembler implements RepresentationModelAssembler<PlaceResponse, EntityModel<PlaceResponse>> {


    public EntityModel<PlaceResponse> toModel(PlaceResponse dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(PlaceController.class).getPlaceById(dto.getId())).withSelfRel());
    }

}

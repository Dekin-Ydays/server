package com.projetfilrougeapi.apifilrouge.assembler;

import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.CityController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This component is responsible for converting a SearchResultResponse DTO
 * into an EntityModel with the appropriate HATEOAS "self" link, based on the resource type.
 */
@Component
public class SearchResultAssembler implements RepresentationModelAssembler<SearchResultResponse, EntityModel<SearchResultResponse>> {

    @Override
    public EntityModel<SearchResultResponse> toModel(SearchResultResponse dto) {
        switch (dto.getType()) {
            case "city":
                return EntityModel.of(dto,
                        linkTo(methodOn(CityController.class).getCityById(dto.getCity().getId())).withSelfRel());
            case "place":
                return EntityModel.of(dto,
                        linkTo(methodOn(PlaceController.class).getPlaceById(dto.getPlace().getId())).withSelfRel());
            case "event":
                return EntityModel.of(dto,
                        linkTo(methodOn(EventController.class).getEventById(dto.getEvent().getId())).withSelfRel());
            case "user":
                return EntityModel.of(dto,
                        linkTo(methodOn(UserController.class).getUserById(dto.getUser().getId())).withSelfRel());
            default:
                return EntityModel.of(dto);
        }
    }
}

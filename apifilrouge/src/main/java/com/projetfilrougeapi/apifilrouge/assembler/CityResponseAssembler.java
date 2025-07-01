package com.projetfilrougeapi.apifilrouge.assembler;

import com.projetfilrougeapi.apifilrouge.DTO.CityResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.CityController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CityResponseAssembler implements RepresentationModelAssembler<CityResponse, EntityModel<CityResponse>> {


    public EntityModel<CityResponse> toModel(CityResponse dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(CityController.class).getCityById(dto.getId())).withSelfRel());
    }

}
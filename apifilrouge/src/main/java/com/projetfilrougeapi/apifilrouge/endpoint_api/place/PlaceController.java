package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class PlaceController {

    private final PlaceRepository placeRepository;

    public PlaceController(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @GetMapping("/places")
    public CollectionModel<EntityModel<Place>> getAllPlaces() {
        List<EntityModel<Place>> places = placeRepository.findAll().stream()
                .map(place -> EntityModel.of(place,
                        linkTo(methodOn(PlaceController.class).getPlaceById(place.getPlaceId())).withSelfRel(),
                        linkTo(methodOn(EventController.class).getPlaceForEvent(place.getPlaceId())).withRel("events")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(places,
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"));
    }

    @GetMapping("/places/{id}")
    public EntityModel<Place> getPlaceById(@PathVariable Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(place,
                linkTo(methodOn(PlaceController.class).getPlaceById(id)).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"),
                linkTo(methodOn(PlaceController.class).getEventsForPlace(place.getPlaceId())).withRel("events"));
    }

    @GetMapping("/places/{id}/events")
    public CollectionModel<EntityModel<Event>> getEventsForPlace(@PathVariable Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EntityModel<Event>> events = place.getEvents().stream()
                .map(event -> EntityModel.of(event,
                        linkTo(methodOn(EventController.class).getEventById(event.getEvent_id())).withSelfRel(),
                        linkTo(methodOn(PlaceController.class).getPlaceById(place.getPlaceId())).withRel("place")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(events,
                linkTo(methodOn(PlaceController.class).getEventsForPlace(id)).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getPlaceById(id)).withRel("place"));
    }

    @PostMapping("/places")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Place> addPlace(@RequestBody Place place) {
        Place savedPlace = placeRepository.save(place);

        return EntityModel.of(savedPlace,
                linkTo(methodOn(PlaceController.class).getPlaceById(savedPlace.getPlaceId())).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"),
                linkTo(methodOn(PlaceController.class).getEventsForPlace(savedPlace.getPlaceId())).withRel("events"));
    }
}

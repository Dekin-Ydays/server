package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.CityController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public CollectionModel<EntityModel<Place>> getAllPlaces() {
        List<EntityModel<Place>> places = placeRepository.findAll().stream()
                .map(place -> EntityModel.of(place,
                        linkTo(methodOn(PlaceController.class).getPlaceById(place.getId())).withSelfRel(),
                        linkTo(methodOn(EventController.class).getPlaceForEvent(place.getId())).withRel("events")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(places,
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withSelfRel(),
                linkTo(methodOn(CityController.class).getAllCities()).withRel("cities"),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"));
    }

    public EntityModel<Place> getPlaceById(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(place,
                linkTo(methodOn(PlaceController.class).getPlaceById(id)).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"),
                linkTo(methodOn(PlaceController.class).getEventsForPlace(place.getId())).withRel("events"));
    }

    public CollectionModel<EntityModel<Event>> getEventsForPlace(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EntityModel<Event>> events = place.getEvents().stream()
                .map(event -> EntityModel.of(event,
                        linkTo(methodOn(EventController.class).getEventById(event.getId())).withSelfRel(),
                        linkTo(methodOn(PlaceController.class).getPlaceById(place.getId())).withRel("place")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(events,
                linkTo(methodOn(PlaceController.class).getEventsForPlace(id)).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getPlaceById(id)).withRel("place"));
    }

    public EntityModel<Place> addPlace(Place place) {
        Place savedPlace = placeRepository.save(place);

        return EntityModel.of(savedPlace,
                linkTo(methodOn(PlaceController.class).getPlaceById(savedPlace.getId())).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"),
                linkTo(methodOn(CityController.class).getAllCities()).withRel("cities"),
                linkTo(methodOn(PlaceController.class).getEventsForPlace(savedPlace.getId())).withRel("events"));
    }

    public EntityModel<Place> updatePlace(Long id, Place place) {
        Place existingPlace = placeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (existingPlace.getName() != null && !existingPlace.getName().equals(place.getName()) ) {
            existingPlace.setName(place.getName());
        }
        if (existingPlace.getDescription() != null && !existingPlace.getDescription().equals(place.getDescription()) ) {
            existingPlace.setDescription(place.getDescription());
        }
        if (existingPlace.getAddress() != null && !existingPlace.getAddress().equals(place.getAddress()) ) {
            existingPlace.setAddress(place.getAddress());
        }
        Place updatedPlace = placeRepository.save(existingPlace);
        return EntityModel.of(updatedPlace,
                linkTo(methodOn(PlaceController.class).getPlaceById(updatedPlace.getId())).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"),
                linkTo(methodOn(CityController.class).getAllCities()).withRel("cities"),
                linkTo(methodOn(PlaceController.class).getEventsForPlace(updatedPlace.getId())).withRel("events"));
    }
    public void deletePlace(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        placeRepository.delete(place);
    }

    public CollectionModel<EntityModel<City>> getPlacesForCity(Long cityId) {
        Place place = placeRepository.findById(cityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EntityModel<City>> cities = place.getCity() != null ? List.of(EntityModel.of(place.getCity(),
                linkTo(methodOn(CityController.class).getCityById(place.getCity().getId())).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getPlaceById(place.getId())).withRel("place")
        )) : List.of();

        return CollectionModel.of(cities,
                linkTo(methodOn(PlaceController.class).getEventsForPlace(cityId)).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getPlaceById(cityId)).withRel("place"));
    }

}

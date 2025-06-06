package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import com.projetfilrougeapi.apifilrouge.DTO.PlaceRequest;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.CityController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.CityRepository;
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
    private final CityRepository cityRepository;

    public PlaceService(PlaceRepository placeRepository, CityRepository cityRepository) {
        this.placeRepository = placeRepository;
        this.cityRepository = cityRepository;
    }

    public CollectionModel<EntityModel<Place>> getAllPlaces() {
        List<EntityModel<Place>> places = placeRepository.findAll().stream()
                .map(place -> EntityModel.of(place,
                        linkTo(methodOn(PlaceController.class).getPlaceById(place.getId())).withSelfRel(),
                        linkTo(methodOn(PlaceController.class).getEventsForPlace(place.getId())).withRel("events")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(places,
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withSelfRel(),
                linkTo(methodOn(CityController.class).getAllCities()).withRel("cities"),
                linkTo(methodOn(EventController.class).getAllEvents(null, null, null, null)).withRel("events"));
    }

    public EntityModel<Place> getPlaceById(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(place,
                linkTo(methodOn(PlaceController.class).getPlaceById(place.getId())).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"),
                linkTo(methodOn(PlaceController.class).getCityForPlace(place.getId())).withRel("city"),
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

    public EntityModel<Place> addPlace(PlaceRequest placeRequest) {
        City city = cityRepository.findById(placeRequest.getCityId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));

        Place place = new Place();
        place.setName(placeRequest.getName());
        place.setDescription(placeRequest.getDescription());
        place.setAddress(placeRequest.getAddress());
        place.setCity(city);

        Place savedPlace = placeRepository.save(place);
        return EntityModel.of(savedPlace,
                linkTo(methodOn(PlaceController.class).getPlaceById(savedPlace.getId())).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"),
                linkTo(methodOn(PlaceController.class).getCityForPlace(savedPlace.getId())).withRel("city"),
                linkTo(methodOn(PlaceController.class).getEventsForPlace(savedPlace.getId())).withRel("events"));
    }

    public EntityModel<Place> updatePlace(Long id, PlaceRequest placeRequest) {
        Place existingPlace = placeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found"));

        if (placeRequest.getName() != null && !placeRequest.getName().equals(existingPlace.getName())) {
            existingPlace.setName(placeRequest.getName());
        }

        if (placeRequest.getDescription() != null && !placeRequest.getDescription().equals(existingPlace.getDescription())) {
            existingPlace.setDescription(placeRequest.getDescription());
        }

        if (placeRequest.getAddress() != null && !placeRequest.getAddress().equals(existingPlace.getAddress())) {
            existingPlace.setAddress(placeRequest.getAddress());
        }

        if (placeRequest.getCityId() != null &&
                (existingPlace.getCity() == null || !existingPlace.getCity().getId().equals(placeRequest.getCityId()))) {

            City newCity = cityRepository.findById(placeRequest.getCityId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "City not found"));
            existingPlace.setCity(newCity);
        }

        Place updatedPlace = placeRepository.save(existingPlace);

        return EntityModel.of(updatedPlace,
                linkTo(methodOn(PlaceController.class).getPlaceById(updatedPlace.getId())).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"),
                linkTo(methodOn(PlaceController.class).getCityForPlace(updatedPlace.getId())).withRel("city"),
                linkTo(methodOn(PlaceController.class).getEventsForPlace(updatedPlace.getId())).withRel("events"));
    }


    public void deletePlace(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        placeRepository.delete(place);
    }

    public EntityModel<City> getCityForPlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        City city = place.getCity();
        if (city == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ville introuvable pour ce lieu");
        }

        return EntityModel.of(city,
                linkTo(methodOn(CityController.class).getCityById(city.getId())).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getPlaceById(place.getId())).withRel("place")
        );
    }


}

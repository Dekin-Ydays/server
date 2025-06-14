package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.DTO.PlaceRequest;
import com.projetfilrougeapi.apifilrouge.Specification.EventSpecification;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.CityController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.CityRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final CityRepository cityRepository;
    private final EventRepository eventRepository;

    public PlaceService(PlaceRepository placeRepository, CityRepository cityRepository, EventRepository eventRepository) {
        this.placeRepository = placeRepository;
        this.cityRepository = cityRepository;
        this.eventRepository = eventRepository;
    }

    public CollectionModel<EntityModel<Place>> getAllPlaces() {
        List<EntityModel<Place>> places = placeRepository.findAll().stream()
                .map(place -> EntityModel.of(place,
                        linkTo(methodOn(PlaceController.class).getPlaceById(place.getId())).withSelfRel(),
                        linkTo(methodOn(PlaceController.class).getEventsForPlace(place.getId(),null,null,null,null,null)).withRel("events")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(places,
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withSelfRel(),
                linkTo(methodOn(CityController.class).getAllCities()).withRel("cities"),
                linkTo(methodOn(EventController.class).getAllEvents(null, null, null, null, null)).withRel("events"));
    }

    public EntityModel<Place> getPlaceById(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(place,
                linkTo(methodOn(PlaceController.class).getPlaceById(place.getId())).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"),
                linkTo(methodOn(PlaceController.class).getCityForPlace(place.getId())).withRel("city"),
                linkTo(methodOn(PlaceController.class).getEventsForPlace(place.getId(), null, null, null, null, null)).withRel("events"));
    }


    public CollectionModel<EntityModel<EventSummaryResponse>> getEventsForPlace(Long id, Double minPrice, Double maxPrice, LocalDate startDate, LocalDate endDate, String[] categories) {
        if (!placeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found");
        }

        Specification<Event> spec = Specification.where(EventSpecification.hasPlace(id));

        if (minPrice != null || maxPrice != null) {
            spec = spec.and(EventSpecification.hasPriceBetween(minPrice, maxPrice));
        }
        if (startDate != null || endDate != null) {
            spec = spec.and(EventSpecification.hasDateBetween(startDate, endDate));
        }
        if (categories != null && categories.length > 0) {
            spec = spec.and(EventSpecification.hasCategories(categories));
        }

        List<Event> events = eventRepository.findAll(spec);

        List<EntityModel<EventSummaryResponse>> eventModels = events.stream()
                .map(event -> {
                    EventSummaryResponse response = EventSummaryResponse.fromEntity(event);
                    return EntityModel.of(response,
                            linkTo(methodOn(EventController.class).getEventById(event.getId())).withSelfRel());
                })
                .collect(Collectors.toList());

        return CollectionModel.of(eventModels,
                linkTo(methodOn(PlaceController.class).getEventsForPlace(id, minPrice, maxPrice, startDate, endDate, categories)).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getPlaceById(id)).withRel("place"));
    }

    public EntityModel<Place> addPlace(PlaceRequest placeRequest) {
        City city = cityRepository.findById(placeRequest.getCityId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));

        Place place = new Place();
        place.setName(placeRequest.getName());
        place.setDescription(placeRequest.getDescription());
        place.setCityName(placeRequest.getCityName());
        place.setAddress(placeRequest.getAddress());
        place.setCity(city);

        Place savedPlace = placeRepository.save(place);
        return EntityModel.of(savedPlace,
                linkTo(methodOn(PlaceController.class).getPlaceById(savedPlace.getId())).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"),
                linkTo(methodOn(PlaceController.class).getCityForPlace(savedPlace.getId())).withRel("city"),
                linkTo(methodOn(PlaceController.class).getEventsForPlace(savedPlace.getId(),null , null, null, null,null)).withRel("events"));
    }

    public EntityModel<Place> updatePlace(Long id, PlaceRequest placeRequest) {
        Place existingPlace = placeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lieu avec l'ID " + id + " introuvable."));

        if (placeRequest.getName() != null && !placeRequest.getName().equals(existingPlace.getName())) {
            if (placeRepository.existsByName(placeRequest.getName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Un lieu avec le nom '" + placeRequest.getName() + "' existe déjà.");
            }
            existingPlace.setName(placeRequest.getName());
        }

        if (placeRequest.getDescription() != null) {
            existingPlace.setDescription(placeRequest.getDescription());
        }
        if (placeRequest.getAddress() != null) {
            existingPlace.setAddress(placeRequest.getAddress());
        }
        if (placeRequest.getCityName() != null) {
            existingPlace.setCityName(placeRequest.getCityName());
        }

        if (placeRequest.getCityId() != null && !placeRequest.getCityId().equals(existingPlace.getCity().getId())) {
            City newCity = cityRepository.findById(placeRequest.getCityId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La ville avec l'ID " + placeRequest.getCityId() + " est introuvable."));
            existingPlace.setCity(newCity);
        }

        Place updatedPlace = placeRepository.save(existingPlace);

        return EntityModel.of(updatedPlace,
                linkTo(methodOn(PlaceController.class).getPlaceById(updatedPlace.getId())).withSelfRel());
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

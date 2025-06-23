package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import com.github.slugify.Slugify;
import com.projetfilrougeapi.apifilrouge.DTO.CityResponse;
import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.DTO.PlaceRequest;
import com.projetfilrougeapi.apifilrouge.DTO.PlaceResponse;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final CityRepository cityRepository;
    private final EventRepository eventRepository;
    private final Slugify slugify = Slugify.builder().build();

    public PlaceService(PlaceRepository placeRepository, CityRepository cityRepository, EventRepository eventRepository) {
        this.placeRepository = placeRepository;
        this.cityRepository = cityRepository;
        this.eventRepository = eventRepository;
    }

    public CollectionModel<EntityModel<PlaceResponse>> findPlaces(String slug) {
        if (slug != null && !slug.isEmpty()) {
            // Search for a specific place by slug
            Place place = placeRepository.findBySlug(slug)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No place found with slug: " + slug));

            EntityModel<PlaceResponse> placeModel = EntityModel.of(PlaceResponse.fromEntity(place),
                    linkTo(methodOn(PlaceController.class).getPlaceById(place.getId())).withSelfRel());

            // Return a collection containing the single result
            return CollectionModel.of(Collections.singletonList(placeModel),
                    linkTo(methodOn(PlaceController.class).findPlaces(slug)).withSelfRel());

        } else {
            // Return the list of all places
            List<EntityModel<PlaceResponse>> places = placeRepository.findAll().stream()
                    .map(place -> {
                        PlaceResponse response = PlaceResponse.fromEntity(place);
                        return EntityModel.of(response,
                                linkTo(methodOn(PlaceController.class).getPlaceById(place.getId())).withSelfRel());
                    })
                    .collect(Collectors.toList());

            return CollectionModel.of(places,
                    linkTo(methodOn(PlaceController.class).findPlaces(null)).withSelfRel());
        }
    }

    public EntityModel<PlaceResponse> getPlaceById(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        PlaceResponse response = PlaceResponse.fromEntity(place);

        return EntityModel.of(response,
                linkTo(methodOn(PlaceController.class).getPlaceById(response.getId())).withSelfRel(),
                linkTo(methodOn(PlaceController.class).findPlaces(null)).withRel("places"),
                linkTo(methodOn(PlaceController.class).getCityForPlace(response.getId())).withRel("city"),
                linkTo(methodOn(PlaceController.class).getEventsForPlace(response.getId(), null, null, null, null, null)).withRel("events"));
    }


    public CollectionModel<EntityModel<EventSummaryResponse>> getEventsForPlace(Long id, Double minPrice, Double maxPrice, LocalDate startDate, LocalDate endDate, String[] categories) {
        if (!placeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lieu non trouvé");
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

    public EntityModel<PlaceResponse> addPlace(PlaceRequest request) {
        String slug = slugify.slugify(request.getName());

        if (placeRepository.existsByNameAndAddressAndLatitudeAndLongitude(
                request.getName(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude()
        )) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A place with this name at this exact address already exists.");
        }
        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ville non trouvée"));

        Place place = Place.builder()
                .name(request.getName())
                .description(request.getDescription())
                .slug(slug)
                .address(request.getAddress())
                .type(request.getType())
                .cityName(request.getCityName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .bannerUrl(request.getBannerUrl())
                .imageUrl(request.getImageUrl())
                .content(request.getContent())
                .city(city)
                .build();

        Place savedPlace = placeRepository.save(place);
        PlaceResponse response = PlaceResponse.fromEntity(savedPlace);

        return EntityModel.of(response,
                linkTo(methodOn(PlaceController.class).getPlaceById(response.getId())).withSelfRel(),
                linkTo(methodOn(PlaceController.class).findPlaces(null)).withRel("places"),
                linkTo(methodOn(PlaceController.class).getCityForPlace(response.getId())).withRel("city"),
                linkTo(methodOn(PlaceController.class).getEventsForPlace(response.getId(), null, null, null, null, null)).withRel("events"));
    }

    public EntityModel<PlaceResponse> updatePlace(Long id, PlaceRequest request) {

        String slug = slugify.slugify(request.getName());

        Place existingPlace = placeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lieu non trouvé"));
        if (request.getName() != null && !request.getName().equals(existingPlace.getName())) {
            // if the name change we verify that it will not create an identic entry
            if (placeRepository.existsByNameAndAddressAndLatitudeAndLongitude(
                    request.getName(),
                    existingPlace.getAddress(),
                    existingPlace.getLatitude(),
                    existingPlace.getLongitude()
            )) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "A place with this name at this exact address already exists.");
            }
            existingPlace.setName(request.getName());
        }
        if (request.getName() != null) existingPlace.setName(request.getName());
        if (request.getCityName() != null) existingPlace.setCityName(request.getCityName());
        if (request.getType() != null) existingPlace.setType(request.getType());
        if (request.getLatitude() != null) existingPlace.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) existingPlace.setLongitude(request.getLongitude());
        if (request.getDescription() != null) existingPlace.setDescription(request.getDescription());
        if (request.getAddress() != null) existingPlace.setAddress(request.getAddress());
        if (request.getImageUrl() != null) existingPlace.setImageUrl(request.getImageUrl());
        if (request.getContent() != null) existingPlace.setContent(request.getContent());
        if (request.getBannerUrl() != null) existingPlace.setBannerUrl(request.getBannerUrl());


        if (request.getCityId() != null && !request.getCityId().equals(existingPlace.getCity().getId())) {
            City newCity = cityRepository.findById(request.getCityId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ville non trouvée"));
            existingPlace.setCity(newCity);
        }

        Place updatedPlace = placeRepository.save(existingPlace);
        PlaceResponse response = PlaceResponse.fromEntity(updatedPlace);

        return EntityModel.of(response,
                linkTo(methodOn(PlaceController.class).getPlaceById(response.getId())).withSelfRel(),
                linkTo(methodOn(PlaceController.class).findPlaces(null)).withRel("places"),
                linkTo(methodOn(PlaceController.class).getCityForPlace(response.getId())).withRel("city"),
                linkTo(methodOn(PlaceController.class).getEventsForPlace(response.getId(), null, null, null, null, null)).withRel("events"));
    }

    public void deletePlace(Long id) {
        if (!placeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lieu non trouvé");
        }
        placeRepository.deleteById(id);
    }

    public EntityModel<CityResponse> getCityForPlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        City city = place.getCity();
        if (city == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ville introuvable pour ce lieu");
        }

        CityResponse response = CityResponse.fromEntity(city);

        return EntityModel.of(response,
                linkTo(methodOn(CityController.class).getCityById(city.getId())).withSelfRel());
    }
}
package com.projetfilrougeapi.apifilrouge.endpoint_api.city;

import com.github.slugify.Slugify;
import com.projetfilrougeapi.apifilrouge.DTO.*;
import com.projetfilrougeapi.apifilrouge.Specification.EventSpecification;
import com.projetfilrougeapi.apifilrouge.assembler.CityResponseAssembler;
import com.projetfilrougeapi.apifilrouge.assembler.EventSummaryResponseAssembler;
import com.projetfilrougeapi.apifilrouge.assembler.PlaceResponseAssembler;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class CityService {

    private final CityRepository cityRepository;
    private final EventRepository eventRepository;

    private final Slugify slugify = Slugify.builder().build();
    private final UserRepository userRepository;
    private final PagedResourcesAssembler pagedResourcesAssembler;
    private final EventSummaryResponseAssembler eventSummaryResponseAssembler;
    private final CityResponseAssembler cityResponseAssembler;
    private final PlaceRepository placeRepository;
    private final PlaceResponseAssembler placeResponseAssembler;

    public CityService(CityRepository cityRepository, EventRepository eventRepository, UserRepository userRepository, PagedResourcesAssembler pagedResourcesAssembler, EventSummaryResponseAssembler eventSummaryResponseAssembler, CityResponseAssembler cityResponseAssembler, PlaceRepository placeRepository, PlaceResponseAssembler placeResponseAssembler) {
        this.cityRepository = cityRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.eventSummaryResponseAssembler = eventSummaryResponseAssembler;
        this.cityResponseAssembler = cityResponseAssembler;
        this.placeRepository = placeRepository;
        this.placeResponseAssembler = placeResponseAssembler;
    }

    public EntityModel<CityResponse> getCityById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        CityResponse response = CityResponse.fromEntity(city);

        return EntityModel.of(response,
                linkTo(methodOn(CityController.class).getCityById(city.getId())).withSelfRel(),
                linkTo(methodOn(CityController.class).getAllCities(null, null)).withRel("cities"),
                linkTo(methodOn(CityController.class).getPlacesForCity(city.getId(),10)).withRel("places"),
                linkTo(methodOn(CityController.class).getOrganizersForCity(city.getId(),10)).withRel("organizers"),
                linkTo(methodOn(CityController.class).getEventsForCity(city.getId(), null, null, null, null, null, null,true)).withRel("events"));
    }

    /**
     * Retrieves a paginated list of cities, optionally filtered by region.
     *
     * @param pageable Pagination and sorting information.
     * @param region   Optional region name to filter cities by (case-insensitive).
     * @return A PagedModel containing CityResponse entities wrapped in EntityModels with HATEOAS links.
     */
    public PagedModel<EntityModel<CityResponse>> getAllCities(Pageable pageable, String region) {
        Page<City> cityPage;

        if (region != null && !region.isEmpty()) {
            // If a region is provided, filter cities by the given region.
            cityPage = cityRepository.findByRegionIgnoreCase(region, pageable);
        } else {
            // retrieve all cities.
            cityPage = cityRepository.findAll(pageable);
        }

        // Map the Page<City> to Page<CityResponse> DTOs.
        Page<CityResponse> cityDtoPage = cityPage.map(CityResponse::fromEntity);

        // Convert to a HATEOAS-compatible paginated model.
        return pagedResourcesAssembler.toModel(cityDtoPage, cityResponseAssembler);
    }

    /**
     * Finds a city by its slug and returns it wrapped in an EntityModel with HATEOAS links.
     *
     * @param slug The unique slug identifier for the city.
     * @return An EntityModel containing the CityResponse and HATEOAS links.
     * @throws ResponseStatusException If no city is found with the given slug.
     */
    public EntityModel<CityResponse> findCityBySlug(String slug) {
        City city = cityRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No city found with the slug: " + slug));

        CityResponse response = CityResponse.fromEntity(city);

        return EntityModel.of(response,
                linkTo(methodOn(CityController.class).getCityById(city.getId())).withSelfRel(),
                linkTo(methodOn(CityController.class).getPlacesForCity(city.getId(),10)).withRel("places"),
                linkTo(methodOn(CityController.class).getOrganizersForCity(city.getId(),10)).withRel("organizers"),
                linkTo(methodOn(CityController.class).getEventsForCity(city.getId(), null, null, null, null, null, null, true)).withRel("events"));
    }

    @Transactional
    public EntityModel<CityResponse> addCity(CityRequest request) {

        String slug = slugify.slugify(request.getName());

        City city = City.builder()
                .name(request.getName())
                .description(request.getDescription())
                .region(request.getRegion())
                .slug(slug)
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .bannerUrl(request.getBannerUrl())
                .imageUrl(request.getImageUrl())
                .content(request.getContent())
                .build();

        // we save to get the id of the city
        City savedCity = cityRepository.save(city);

        // Establishment of the relations
        if (request.getNearestCityIds() != null && !request.getNearestCityIds().isEmpty()) {
            List<City> nearestCitiesToAdd = cityRepository.findAllById(request.getNearestCityIds());
            for (City nearestCity : nearestCitiesToAdd) {
                savedCity.addNearestCity(nearestCity);
            }
        }
        // add of the junctions in the table
        City finalSavedCity = cityRepository.save(savedCity);
        CityResponse response = CityResponse.fromEntity(finalSavedCity);

        return EntityModel.of(response,
                linkTo(methodOn(CityController.class).getCityById(response.getId())).withSelfRel(),
                linkTo(methodOn(CityController.class).getAllCities(null, null)).withRel("cities"),
                linkTo(methodOn(CityController.class).getPlacesForCity(response.getId(),10)).withRel("places"),
                linkTo(methodOn(CityController.class).getOrganizersForCity(response.getId(),10)).withRel("organizers"),
                linkTo(methodOn(CityController.class).getEventsForCity(response.getId(), null, null, null, null, null, null,true)).withRel("events"));
    }

    @Transactional
    public EntityModel<CityResponse> updateCity(Long id, CityRequest request) {

        String slug = slugify.slugify(request.getName());

        City existingCity = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.getName() != null) {
            existingCity.setName(request.getName());
            existingCity.setSlug(slug);
        }
        if (request.getDescription() != null) existingCity.setDescription(request.getDescription());
        if (request.getRegion() != null) existingCity.setRegion(request.getRegion());
        if (request.getPostalCode() != null) existingCity.setPostalCode(request.getPostalCode());
        if (request.getCountry() != null) existingCity.setCountry(request.getCountry());
        if (request.getLatitude() != null) existingCity.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) existingCity.setLongitude(request.getLongitude());
        if (request.getBannerUrl() != null) existingCity.setBannerUrl(request.getBannerUrl());
        if (request.getImageUrl() != null) existingCity.setImageUrl(request.getImageUrl());
        if (request.getContent() != null) existingCity.setContent(request.getContent());

        if (request.getNearestCityIds() != null) {
            // Delete of previous relations
            // Itérate on a copie to dodge ConcurrentModificationException
            new HashSet<>(existingCity.getNearestCities()).forEach(existingCity::removeNearestCity);

            // Adding of the new relations
            List<City> newNearestCities = cityRepository.findAllById(request.getNearestCityIds());
            for (City newNearest : newNearestCities) {
                existingCity.addNearestCity(newNearest);
            }
        }

        City updatedCity = cityRepository.save(existingCity);
        CityResponse response = CityResponse.fromEntity(updatedCity);

        return EntityModel.of(response,
                linkTo(methodOn(CityController.class).getCityById(response.getId())).withSelfRel(),
                linkTo(methodOn(CityController.class).getAllCities(null, null)).withRel("cities"),
                linkTo(methodOn(CityController.class).getPlacesForCity(response.getId(),10)).withRel("places"),
                linkTo(methodOn(CityController.class).getOrganizersForCity(response.getId(),10)).withRel("organizers"),
                linkTo(methodOn(CityController.class).getEventsForCity(response.getId(), null, null, null, null, null, null,true)).withRel("events"));
    }

    /**
     * Retrieves a limited list of places for a given city.
     * @param cityId The ID of the city.
     * @param limit  The maximum number of places to return.
     * @return A simple collection of PlaceResponse DTOs.
     */
    public CollectionModel<EntityModel<PlaceResponse>> getPlacesForCity(Long cityId, int limit) {
        if (!cityRepository.existsById(cityId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found");
        }
        List<Place> allPlacesInCity = placeRepository.findAllByCityId(cityId);

        List<EntityModel<PlaceResponse>> placeModels = allPlacesInCity.stream()
                .limit(limit) // Apply the limit here, in memory.
                .map(place -> {
                    PlaceResponse response = PlaceResponse.fromEntity(place);
                    return EntityModel.of(response,
                            linkTo(methodOn(PlaceController.class).getPlaceById(place.getId())).withSelfRel());
                })
                .collect(Collectors.toList());

        return CollectionModel.of(placeModels,
                linkTo(methodOn(CityController.class).getPlacesForCity(cityId, limit)).withSelfRel());
    }


    /**
     * Retrieves a limited list of organizers for a given city.
     *
     * @param cityId The ID of the city.
     * @param limit The maximum number of organizers to return.
     * @return A simple collection of UserResponse DTOs wrapped in EntityModels.
     */
    public CollectionModel<EntityModel<UserResponse>> getOrganizersForCity(Long cityId, int limit) {
        if (!cityRepository.existsById(cityId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found");
        }

        // Retrieve ALL organizers for the given city.
        List<User> organizers = userRepository.findOrganizersByCity(cityId);

        // Transform the list, apply the limit, and map to DTOs.
        List<EntityModel<UserResponse>> organizerModels = organizers.stream()
                .limit(limit)
                .map(organizer -> {
                    UserResponse response = UserResponse.fromEntity(organizer);
                    return EntityModel.of(response,
                            linkTo(methodOn(UserController.class).getUserById(organizer.getId())).withSelfRel());
                })
                .collect(Collectors.toList());

        return CollectionModel.of(organizerModels,
                linkTo(methodOn(CityController.class).getOrganizersForCity(cityId, limit)).withSelfRel());
    }


    public PagedModel<EntityModel<EventSummaryResponse>> getEventsForCity(Long cityId, Pageable pageable, Double minPrice, Double maxPrice, LocalDate startDate, LocalDate endDate, String[] categories, boolean onlyAvailable) {
        if (!cityRepository.existsById(cityId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found");
        }

        Specification<Event> spec = Specification.where(EventSpecification.hasCity(cityId));

        if (onlyAvailable) {
            spec = spec.and(EventSpecification.isAvailable());
        }

        if (minPrice != null || maxPrice != null) {
            spec = spec.and(EventSpecification.hasPriceBetween(minPrice, maxPrice));
        }
        if (startDate != null || endDate != null) {
            spec = spec.and(EventSpecification.hasDateBetween(startDate, endDate));
        }
        if (categories != null && categories.length > 0) {
            spec = spec.and(EventSpecification.hasCategories(categories));
        }

        Page<Event> eventsPage = eventRepository.findAll(spec, pageable);

        Page<EventSummaryResponse> eventsDtoPage = eventsPage.map(EventSummaryResponse::fromEntity);

        PagedModel<EntityModel<EventSummaryResponse>> pagedModel = pagedResourcesAssembler.toModel(eventsDtoPage, eventSummaryResponseAssembler);

        pagedModel.add(linkTo(methodOn(CityController.class).getCityById(cityId)).withRel("city"));

        return pagedModel;
    }


    public void deleteCity(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        cityRepository.delete(city);
    }
}
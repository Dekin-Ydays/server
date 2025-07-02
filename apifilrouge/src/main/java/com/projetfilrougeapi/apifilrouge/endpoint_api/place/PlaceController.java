package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import com.projetfilrougeapi.apifilrouge.DTO.*;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.CityService;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/places")
public class PlaceController {

    private final PlaceService placeService;
    private final CityService cityService;

    public PlaceController(PlaceService placeService, CityService cityService) {
        this.placeService = placeService;
        this.cityService = cityService;
    }

    @GetMapping("/slug/{slug}")
    public EntityModel<PlaceResponse> findPlaceBySlug(@PathVariable("slug") String slug) {

        return placeService.findPlaceBySlug(slug);
    }

    /**
     * The endpoint accepts optional filters 'types' and 'cities'
     * as arrays of strings.
     *
     * @param pageable Pagination information including page size, number, and sort order.
     * @param types Optional array of place types to filter by.
     * @param cities Optional array of city names to filter by.
     * @return A paged model of EntityModel-wrapped PlaceResponse DTOs matching the filters.
     */
    @GetMapping
    public PagedModel<EntityModel<PlaceResponse>> getAllPlaces(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String[] types,
            @RequestParam(required = false) String[] cities
    ) {
        return placeService.getAllPlaces(pageable, types, cities);
    }


    @GetMapping("/{id}")
    public EntityModel<PlaceResponse> getPlaceById(@PathVariable("id") Long id) {
        return placeService.getPlaceById(id);
    }

    @GetMapping("/{id}/events")
    public PagedModel<EntityModel<EventSummaryResponse>> getEventsForPlace(
            @PathVariable("id") Long id,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String[] categories
    ) {
        return placeService.getEventsForPlace(id, pageable, minPrice, maxPrice, startDate, endDate, categories);
    }

    @GetMapping("/{id}/city")
    public EntityModel<CityResponse> getCityForPlace(@PathVariable("id") Long id) {
        return placeService.getCityForPlace(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<PlaceResponse> addPlace(@Valid @RequestBody PlaceRequest placeRequest) {
        return placeService.addPlace(placeRequest);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<PlaceResponse> updatePlace(@PathVariable("id") Long id, @Valid @RequestBody PlaceRequest placeRequest) {
        return placeService.updatePlace(id, placeRequest);
    }

    /**
     * Retrieves all active organizers at a given location.
     *
     * @param id The ID of the location.
     * @return A collection of organizer profiles.
     */
    @GetMapping("/{id}/organizers")
    public CollectionModel<EntityModel<UserResponse>> getOrganizersForPlace(@PathVariable("id") Long id) {
        return placeService.getOrganizersForPlace(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlace(@PathVariable("id") Long id) {
        placeService.deletePlace(id);
    }
}

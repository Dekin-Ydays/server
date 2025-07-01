package com.projetfilrougeapi.apifilrouge.endpoint_api.city;

import com.projetfilrougeapi.apifilrouge.DTO.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/cities")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    /**
     * GET endpoint to retrieve a city by its numeric ID.
     *
     * @param id The ID of the city.
     * @return An EntityModel containing the CityResponse and related HATEOAS links.
     */
    @GetMapping("/{id}")
    public EntityModel<CityResponse> getCityById(@PathVariable("id") Long id) {
        return cityService.getCityById(id);
    }

    /**
     * GET endpoint to retrieve a city by its slug.
     *
     * @param slug The unique slug of the city.
     * @return An EntityModel containing the CityResponse and related HATEOAS links.
     */
    @GetMapping("/slug/{slug}")
    public EntityModel<CityResponse> findCityBySlug(@PathVariable("slug") String slug) {
        return cityService.findCityBySlug(slug);
    }

    /**
     * GET endpoint to retrieve a paginated list of cities, optionally filtered by region.
     *
     * @param pageable Pagination and sorting information (default: 10 cities per page, sorted by name ascending).
     * @param region   Optional query parameter to filter cities by region.
     * @return A PagedModel containing CityResponse entities wrapped in EntityModels with HATEOAS links.
     */
    @GetMapping
    public PagedModel<EntityModel<CityResponse>> getAllCities(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String region
    ) {
        return cityService.getAllCities(pageable, region);
    }

    @PostMapping
    public EntityModel<CityResponse> addCity(@Valid @RequestBody CityRequest city) {
        return cityService.addCity(city);
    }

    @PatchMapping("/{id}")
    public EntityModel<CityResponse> updateCity(@PathVariable("id") Long id, @Valid @RequestBody CityRequest city) {
        return cityService.updateCity(id, city);
    }

    @GetMapping("/{id}/places")
    public CollectionModel<EntityModel<PlaceResponse>> getPlacesForCity(@PathVariable("id") Long id) {
        return cityService.getPlacesForCity(id);
    }

    @DeleteMapping("/{id}")
    public void deleteCity(@PathVariable("id") Long id) {
        cityService.deleteCity(id);
    }

    @GetMapping("/{id}/events")
    public PagedModel<EntityModel<EventSummaryResponse>> getEventsForCity(
            @PathVariable("id") Long id,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String[] categories
    ) {
        return cityService.getEventsForCity(id, pageable, minPrice, maxPrice, startDate, endDate, categories);
    }

    /**
     * Retrieves all active organizers in a given city.
     *
     * @param id The ID of the city.
     * @return A collection of organizer profiles.
     */
    @GetMapping("/{id}/organizers")
    public CollectionModel<EntityModel<UserResponse>> getOrganizersForCity(@PathVariable("id") Long id) {
        return cityService.getOrganizersForCity(id);
    }

}

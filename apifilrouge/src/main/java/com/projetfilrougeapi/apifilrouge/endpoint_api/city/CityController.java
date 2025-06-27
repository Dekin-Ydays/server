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

    @GetMapping("/{id}")
    public EntityModel<CityResponse> getCityById(@PathVariable("id") Long id) {
        return cityService.getCityById(id);
    }

    @GetMapping
    public CollectionModel<EntityModel<CityResponse>> findCities(@RequestParam(value = "slug",required = false) String slug, @RequestParam(value = "region",required = false) String region) {
        return cityService.findCities(slug, region);
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
    public void deleteCity(@PathVariable("id")  Long id) {
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
     * @param id The ID of the city.
     * @return A collection of organizer profiles.
     */
    @GetMapping("/{id}/organizers")
    public CollectionModel<EntityModel<UserResponse>> getOrganizersForCity(@PathVariable("id") Long id) {
        return cityService.getOrganizersForCity(id);
    }

}

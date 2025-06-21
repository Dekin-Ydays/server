package com.projetfilrougeapi.apifilrouge.endpoint_api.city;

import com.projetfilrougeapi.apifilrouge.DTO.CityRequest;
import com.projetfilrougeapi.apifilrouge.DTO.CityResponse;
import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.DTO.PlaceResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/cities")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/{id}")
    public EntityModel<CityResponse> getCityById(@PathVariable Long id) {
        return cityService.getCityById(id);
    }

    @GetMapping
    public CollectionModel<EntityModel<CityResponse>> findCities(@RequestParam(required = false) String name, @RequestParam(required = false) String region) {
        return cityService.findCities(name, region);
    }
    @PostMapping
    public EntityModel<CityResponse> addCity(@Valid @RequestBody CityRequest city) {
        return cityService.addCity(city);
    }
    @PatchMapping("/{id}")
    public EntityModel<CityResponse> updateCity(@PathVariable Long id, @Valid @RequestBody CityRequest city) {
        return cityService.updateCity(id, city);
    }
    @GetMapping("/{id}/places")
    public CollectionModel<EntityModel<PlaceResponse>> getPlacesForCity(@PathVariable Long id) {
        return cityService.getPlacesForCity(id);
    }
    @DeleteMapping("/{id}")
    public void deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
    }

    @GetMapping("/{id}/events")
    public CollectionModel<EntityModel<EventSummaryResponse>> getEventsForCity(
            @PathVariable Long id,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String[] categories
    ) {
        return cityService.getEventsForCity(id, minPrice, maxPrice, startDate, endDate, categories);
    }

}

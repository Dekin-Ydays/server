package com.projetfilrougeapi.apifilrouge.endpoint_api.city;

import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
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
    public EntityModel<City> getCityById(@PathVariable Long id) {
        return cityService.getCityById(id);
    }
    @GetMapping
    public CollectionModel<EntityModel<City>> getAllCities() {
        return cityService.getAllCities();
    }

    @PostMapping
    public EntityModel<City> addCity(@RequestBody City city) {
        return cityService.addCity(city);
    }
    @PatchMapping("/{id}")
    public EntityModel<City> updateCity(@PathVariable Long id, @RequestBody City city) {
        return cityService.updateCity(id, city);
    }
    @GetMapping("/{id}/places")
    public CollectionModel<EntityModel<Place>> getPlacesForCity(@PathVariable Long id) {
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

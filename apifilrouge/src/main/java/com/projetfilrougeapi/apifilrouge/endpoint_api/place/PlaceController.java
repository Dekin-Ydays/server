package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import com.projetfilrougeapi.apifilrouge.DTO.CityResponse;
import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.DTO.PlaceRequest;
import com.projetfilrougeapi.apifilrouge.DTO.PlaceResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.CityService;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/places")
public class PlaceController {

    private final PlaceService placeService;
    private final CityService cityService;

    public PlaceController(PlaceService placeService, CityService cityService) {
        this.placeService = placeService;
        this.cityService = cityService;
    }

    @GetMapping
    public CollectionModel<EntityModel<PlaceResponse>> findPlaces(@RequestParam(required = false) String name) {
        return placeService.findPlaces(name);
    }

    @GetMapping("/{id}")
    public EntityModel<PlaceResponse> getPlaceById(@PathVariable("id") Long id) {
        return placeService.getPlaceById(id);
    }

    @GetMapping("/{id}/events")
    public CollectionModel<EntityModel<EventSummaryResponse>> getEventsForPlace(
            @PathVariable Long id,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String[] categories
    ) {
        return placeService.getEventsForPlace(id, minPrice, maxPrice, startDate, endDate, categories);
    }
    @GetMapping("/{id}/city")
    public EntityModel<CityResponse> getCityForPlace(@PathVariable Long id) {
        return placeService.getCityForPlace(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<PlaceResponse> addPlace(@Valid @RequestBody PlaceRequest placeRequest) {
        return placeService.addPlace(placeRequest);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<PlaceResponse> updatePlace(@PathVariable Long id, @Valid @RequestBody PlaceRequest placeRequest) {
        return placeService.updatePlace(id, placeRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlace(@PathVariable Long id) {
        placeService.deletePlace(id);
    }
}

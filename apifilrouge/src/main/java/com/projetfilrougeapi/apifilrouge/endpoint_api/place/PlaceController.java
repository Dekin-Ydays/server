package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import com.projetfilrougeapi.apifilrouge.DTO.PlaceRequest;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.CityService;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    public CollectionModel<EntityModel<Place>> getAllPlaces() {
        return placeService.getAllPlaces();
    }

    @GetMapping("/{id}")
    public EntityModel<Place> getPlaceById(@PathVariable("id") Long id) {
        return placeService.getPlaceById(id);
    }

    @GetMapping("/{id}/events")
    public CollectionModel<EntityModel<Event>> getEventsForPlace(@PathVariable Long id) {
        return placeService.getEventsForPlace(id);
    }
    @GetMapping("/{id}/city")
    public EntityModel<City> getCityForPlace(@PathVariable Long id) {
        return placeService.getCityForPlace(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Place> addPlace(@RequestBody PlaceRequest placeRequest) {
        return placeService.addPlace(placeRequest);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<Place> updatePlace(@PathVariable Long id, @RequestBody Place place) {
        return placeService.updatePlace(id, place);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlace(@PathVariable Long id) {
        placeService.deletePlace(id);
    }
}

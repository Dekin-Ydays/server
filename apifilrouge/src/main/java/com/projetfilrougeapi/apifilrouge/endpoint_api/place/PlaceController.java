package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/places")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Place> addPlace(@RequestBody Place place) {
        return placeService.addPlace(place);
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

package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PlaceController {

    private final PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("/places")
    public List<Map<String, Object>> getAllPlaces() {
        return placeService.getPlacesWithEventsLink();
    }

    @PostMapping("/createPlaces")
    public void addPlace(@RequestBody Place place) {
        placeService.addPlace(place);
    }
}

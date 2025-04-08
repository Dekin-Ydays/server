package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    @Autowired
    public PlaceService(PlaceRepository placeRepository) {

        this.placeRepository = placeRepository;
    }

    public List<Place> getPlaces() {

        return placeRepository.findAll();
    }

    // Récupérer un lieu par son ID
    public Optional<Place> getPlaceById(Long id) {
        return placeRepository.findById(id);
    }

    public void addPlace(@RequestBody Place place) {
       /* Optional<Place> existingPlace = placeRepository.
                findById(place.getPlaceId());
        if (existingPlace.isPresent()) {
            // Place already exists, handle accordingly (e.g., update or ignore)
            System.out.println("Place already exists with ID: " + place.getPlaceId());
            throw new IllegalStateException("Place already exists with ID: " + place.getPlaceId());
        } else {
            // Place does not exist, save it*/
        placeRepository.save(place);
            /*System.out.println("Place saved successfully: " + place);
        }*/

    }

    public List<Map<String, Object>> getPlacesWithEventsLink() {
        List<Place> places = placeRepository.findAll();

        return places.stream().map(place -> {
            Map<String, Object> placeMap = new HashMap<>();
            placeMap.put("placeId", place.getPlaceId());
            placeMap.put("city_name", place.getCity_name());
            placeMap.put("events_url", "http://localhost:8090/events/place/" + place.getPlaceId());
            return placeMap;
        }).collect(Collectors.toList());
    }
}

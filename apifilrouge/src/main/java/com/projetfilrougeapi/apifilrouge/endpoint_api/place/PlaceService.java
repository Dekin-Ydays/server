package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

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
}

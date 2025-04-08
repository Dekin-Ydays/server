package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PlaceController {

   private final PlaceService placeService;
    private final PlaceRepository placeRepository;

    @Autowired
    public PlaceController(PlaceService placeService, PlaceRepository placeRepository) {
        this.placeService = placeService;
        this.placeRepository = placeRepository;
    }
    @GetMapping("/places")
    public List<Place> getAllPlaces() {
        return placeService.getPlaces();
    }
    @PostMapping("/createPlaces")
    public Place addPlace(@RequestBody Place place) {
       return placeRepository.save(place);
    }
    //    @GetMapping("/places/{id}")
//    public ResponseEntity<Place> getPlaceById(@PathVariable Long id) {
//         return placeRepository.findById(id)
//                 .map(ResponseEntity::ok)
//                 .orElse(ResponseEntity.notFound().build());
//     }
//    @PostMapping("/createPlace")
//    public ResponseEntity<String> createSinglePlace(@RequestBody Place place) {
//        if (place == null) {
//            return ResponseEntity.badRequest().body("Invalid request: Place is null");
//        }
//        placeRepository.save(place);
//        return ResponseEntity.ok("Place created successfully");
//    }
//
//    @PostMapping("/createPlaces")
//    public ResponseEntity<String> createPlaces(@RequestBody List<Place> places) {
//        if (places == null || places.isEmpty()) {
//            return ResponseEntity.badRequest().body("Invalid request: Place list is empty or null");
//        }
//        placeRepository.saveAll(places);
//        return ResponseEntity.ok("Places created successfully");
//    }
//
//    @GetMapping("/places")
//    public List<Place> getAllPlaces() {
//        return placeRepository.findAll();
//    }
}

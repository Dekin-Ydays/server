package com.projetfilrougeapi.apifilrouge.endpoint_api.city;

import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public EntityModel<City> getCityById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(city,
                linkTo(methodOn(CityController.class).getCityById(city.getId())).withSelfRel(),
                linkTo(methodOn(CityController.class).getAllCities()).withRel("cities"),
                linkTo(methodOn(CityController.class).getPlacesForCity(city.getId())).withRel("places"),
                linkTo(methodOn(CityController.class).getEventsForCity(city.getId())).withRel("events"));
    }

    public CollectionModel<EntityModel<City>> getAllCities() {
        List<EntityModel<City>> cities = cityRepository.findAll().stream()
                .map(city -> EntityModel.of(city,
                        linkTo(methodOn(CityController.class).getCityById(city.getId())).withSelfRel()
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(cities,
                linkTo(methodOn(CityController.class).getAllCities()).withSelfRel(),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("events"));
    }

    public EntityModel<City> addCity(City city) {
        City savedCity = cityRepository.save(city);

        return EntityModel.of(savedCity,
                linkTo(methodOn(CityController.class).getCityById(savedCity.getId())).withSelfRel(),
                linkTo(methodOn(CityController.class).getAllCities()).withRel("cities"),
                linkTo(methodOn(CityController.class).getPlacesForCity(city.getId())).withRel("places"),
                linkTo(methodOn(CityController.class).getEventsForCity(city.getId())).withRel("events"));
    }

    public EntityModel<City> updateCity(Long id, City city) {
        City existingCity = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (city.getName() != null && !city.getName().equals(existingCity.getName())) {
            existingCity.setName(city.getName());
        }
        if (city.getDescription() != null && !city.getDescription().equals(existingCity.getDescription())) {
            existingCity.setDescription(city.getDescription());
        }

        City updatedCity = cityRepository.save(existingCity);

        return EntityModel.of(updatedCity,
                linkTo(methodOn(CityController.class).getCityById(updatedCity.getId())).withSelfRel(),
                linkTo(methodOn(CityController.class).getAllCities()).withRel("cities"),
                linkTo(methodOn(CityController.class).getPlacesForCity(city.getId())).withRel("places"),
                linkTo(methodOn(CityController.class).getEventsForCity(city.getId())).withRel("events"));
    }

    public CollectionModel<EntityModel<Place>> getPlacesForCity(Long cityId) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EntityModel<Place>> places = city.getPlaces().stream()
                .map(place -> EntityModel.of(place,
                        linkTo(methodOn(PlaceController.class).getPlaceById(place.getId())).withSelfRel()))
                .toList();

        return CollectionModel.of(places,
                linkTo(methodOn(CityController.class).getPlacesForCity(cityId)).withSelfRel(),
                linkTo(methodOn(CityController.class).getCityById(cityId)).withRel("city"));
    }

    public void deleteCity(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        cityRepository.delete(city);
    }

    public CollectionModel<EntityModel<Event>> getEventsForCity(Long cityId) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EntityModel<Event>> events = city.getEvents().stream()
                .map(event -> EntityModel.of(event,
                        linkTo(methodOn(EventController.class).getEventById(event.getId())).withSelfRel(),
                        linkTo(methodOn(CityController.class).getCityById(cityId)).withRel("city")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(events,
                linkTo(methodOn(CityController.class).getEventsForCity(cityId)).withSelfRel(),
                linkTo(methodOn(CityController.class).getCityById(cityId)).withRel("city"));
    }
}
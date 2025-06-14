package com.projetfilrougeapi.apifilrouge.endpoint_api.city;

import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.Specification.EventSpecification;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceController;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class CityService {

    private final CityRepository cityRepository;
    private final EventRepository eventRepository;

    public CityService(CityRepository cityRepository, EventRepository eventRepository) {
        this.cityRepository = cityRepository;
        this.eventRepository = eventRepository;
    }

    public EntityModel<City> getCityById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(city,
                linkTo(methodOn(CityController.class).getCityById(city.getId())).withSelfRel(),
                linkTo(methodOn(CityController.class).getAllCities()).withRel("cities"),
                linkTo(methodOn(CityController.class).getPlacesForCity(city.getId())).withRel("places"),
                linkTo(methodOn(CityController.class).getEventsForCity(city.getId(), null, null, null, null, null)).withRel("events"));
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
                linkTo(methodOn(CityController.class).getEventsForCity(city.getId(), null, null, null, null, null)).withRel("events"));
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
                linkTo(methodOn(CityController.class).getPlacesForCity(updatedCity.getId())).withRel("places"),
                linkTo(methodOn(CityController.class).getEventsForCity(updatedCity.getId(), null,null,null,null,null)).withRel("events"));
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

    public CollectionModel<EntityModel<EventSummaryResponse>> getEventsForCity(Long cityId, Double minPrice, Double maxPrice, LocalDate startDate, LocalDate endDate, String[] categories) {
        if (!cityRepository.existsById(cityId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ville non trouvée");
        }

        Specification<Event> spec = Specification.where(EventSpecification.hasCity(cityId));

        if (minPrice != null || maxPrice != null) {
            spec = spec.and(EventSpecification.hasPriceBetween(minPrice, maxPrice));
        }
        if (startDate != null || endDate != null) {
            spec = spec.and(EventSpecification.hasDateBetween(startDate, endDate));
        }
        if (categories != null && categories.length > 0) {
            spec = spec.and(EventSpecification.hasCategories(categories));
        }

        List<Event> events = eventRepository.findAll(spec);

        List<EntityModel<EventSummaryResponse>> eventModels = events.stream()
                .map(event -> {
                    EventSummaryResponse response = EventSummaryResponse.fromEntity(event);
                    return EntityModel.of(response,
                            linkTo(methodOn(EventController.class).getEventById(event.getId())).withSelfRel());
                })
                .collect(Collectors.toList());

        return CollectionModel.of(eventModels,
                linkTo(methodOn(CityController.class).getEventsForCity(cityId, minPrice, maxPrice, startDate, endDate, categories)).withSelfRel(),
                linkTo(methodOn(CityController.class).getCityById(cityId)).withRel("city"));
    }
}
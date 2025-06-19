package com.projetfilrougeapi.apifilrouge.endpoint_api.city;

import com.projetfilrougeapi.apifilrouge.DTO.CityRequest;
import com.projetfilrougeapi.apifilrouge.DTO.CityResponse;
import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.DTO.PlaceResponse;
import com.projetfilrougeapi.apifilrouge.Specification.EventSpecification;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceController;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public EntityModel<CityResponse> getCityById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        CityResponse response = CityResponse.fromEntity(city);

        return EntityModel.of(response,
                linkTo(methodOn(CityController.class).getCityById(city.getId())).withSelfRel(),
                linkTo(methodOn(CityController.class).findCities(null, null)).withRel("cities"),
                linkTo(methodOn(CityController.class).getPlacesForCity(city.getId())).withRel("places"),
                linkTo(methodOn(CityController.class).getEventsForCity(city.getId(), null, null, null, null, null)).withRel("events"));
    }

    public CollectionModel<EntityModel<CityResponse>> findCities(String name, String region) {

        // On cherche une ville par son nom.
        if (name != null && !name.isEmpty()) {
            City city = cityRepository.findByName(name)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucune ville trouvée avec le nom : " + name));

            CityResponse response = CityResponse.fromEntity(city);

            // on construit un EntityModel avec tous les liens utiles, comme pour getById.
            EntityModel<CityResponse> cityModel = EntityModel.of(response,
                    linkTo(methodOn(CityController.class).getCityById(city.getId())).withSelfRel(),
                    linkTo(methodOn(CityController.class).getPlacesForCity(city.getId())).withRel("places"),
                    linkTo(methodOn(CityController.class).getEventsForCity(city.getId(), null, null, null, null, null)).withRel("events"));

            // on retourne une collection contenant ce seul élément enrichi.
            return CollectionModel.of(Collections.singletonList(cityModel),
                    linkTo(methodOn(CityController.class).findCities(name, null)).withSelfRel());
        }

        // On cherche par région ou on liste tout.
        List<City> citiesToReturn;
        if (region != null && !region.isEmpty()) {
            citiesToReturn = cityRepository.findByRegion(region);
        } else {
            citiesToReturn = cityRepository.findAll();
        }

        List<EntityModel<CityResponse>> cityModels = citiesToReturn.stream()
                .map(city -> {
                    CityResponse response = CityResponse.fromEntity(city);
                    return EntityModel.of(response,
                            linkTo(methodOn(CityController.class).getCityById(city.getId())).withSelfRel());
                })
                .collect(Collectors.toList());

        return CollectionModel.of(cityModels,
                linkTo(methodOn(CityController.class).findCities(null, region)).withSelfRel());
    }

    @Transactional
    public EntityModel<CityResponse> addCity(CityRequest request) {
        City city = City.builder()
                .name(request.getName())
                .description(request.getDescription())
                .region(request.getRegion())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .bannerUrl(request.getBannerUrl())
                .imageUrl(request.getImageUrl())
                .content(request.getContent())
                .build();

        // we save to get the id of the city
        City savedCity = cityRepository.save(city);

        // Establishment of the relations
        if (request.getNearestCityIds() != null && !request.getNearestCityIds().isEmpty()) {
            List<City> nearestCitiesToAdd = cityRepository.findAllById(request.getNearestCityIds());
            for (City nearestCity : nearestCitiesToAdd) {
                savedCity.addNearestCity(nearestCity);
            }
        }
        // add of the junctions in the table
        City finalSavedCity = cityRepository.save(savedCity);
        CityResponse response = CityResponse.fromEntity(finalSavedCity);

        return EntityModel.of(response,
                linkTo(methodOn(CityController.class).getCityById(response.getId())).withSelfRel(),
                linkTo(methodOn(CityController.class).findCities(null, null)).withRel("cities"),
                linkTo(methodOn(CityController.class).getPlacesForCity(response.getId())).withRel("places"),
                linkTo(methodOn(CityController.class).getEventsForCity(response.getId(), null, null, null, null, null)).withRel("events"));
    }

    @Transactional
    public EntityModel<CityResponse> updateCity(Long id, CityRequest request) {
        City existingCity = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.getName() != null) existingCity.setName(request.getName());
        if (request.getDescription() != null) existingCity.setDescription(request.getDescription());
        if (request.getRegion() != null) existingCity.setRegion(request.getRegion());
        if (request.getPostalCode() != null) existingCity.setPostalCode(request.getPostalCode());
        if (request.getCountry() != null) existingCity.setCountry(request.getCountry());
        if (request.getLatitude() != null) existingCity.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) existingCity.setLongitude(request.getLongitude());
        if (request.getBannerUrl() != null) existingCity.setBannerUrl(request.getBannerUrl());
        if (request.getImageUrl() != null) existingCity.setImageUrl(request.getImageUrl());
        if (request.getContent() != null) existingCity.setContent(request.getContent());

        if (request.getNearestCityIds() != null) {
            // Delete of previous relations
            // Itérate on a copie to dodge ConcurrentModificationException
            new HashSet<>(existingCity.getNearestCities()).forEach(existingCity::removeNearestCity);

            // Adding of the new relations
            List<City> newNearestCities = cityRepository.findAllById(request.getNearestCityIds());
            for (City newNearest : newNearestCities) {
                existingCity.addNearestCity(newNearest);
            }
        }

        City updatedCity = cityRepository.save(existingCity);
        CityResponse response = CityResponse.fromEntity(updatedCity);

        return EntityModel.of(response,
                linkTo(methodOn(CityController.class).getCityById(response.getId())).withSelfRel(),
                linkTo(methodOn(CityController.class).findCities(null, null)).withRel("cities"),
                linkTo(methodOn(CityController.class).getPlacesForCity(response.getId())).withRel("places"),
                linkTo(methodOn(CityController.class).getEventsForCity(response.getId(), null, null, null, null, null)).withRel("events"));
    }

    public CollectionModel<EntityModel<PlaceResponse>> getPlacesForCity(Long cityId) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EntityModel<PlaceResponse>> places = city.getPlaces().stream()
                .map(place -> {
                    PlaceResponse response = PlaceResponse.fromEntity(place);
                    return EntityModel.of(response,
                            linkTo(methodOn(PlaceController.class).getPlaceById(place.getId())).withSelfRel());
                })
                .collect(Collectors.toList());

        return CollectionModel.of(places,
                linkTo(methodOn(CityController.class).getPlacesForCity(cityId)).withSelfRel());
    }

    public void deleteCity(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        cityRepository.delete(city);
    }

    public CollectionModel<EntityModel<EventSummaryResponse>> getEventsForCity(Long cityId, Double minPrice, Double maxPrice, LocalDate startDate, LocalDate endDate, String[] categories) {
        if (!cityRepository.existsById(cityId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found");
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
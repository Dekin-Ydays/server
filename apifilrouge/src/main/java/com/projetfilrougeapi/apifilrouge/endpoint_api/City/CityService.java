package com.projetfilrougeapi.apifilrouge.endpoint_api.City;

import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryController;
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
                linkTo(methodOn(CityController.class).getCityById(id)).withSelfRel());
    }

    public CollectionModel<EntityModel<City>> getAllCities() {
        List<EntityModel<City>> cities = cityRepository.findAll().stream()
                .map(city -> EntityModel.of(city,
                        linkTo(methodOn(CityController.class).getCityById(city.getCity_id())).withSelfRel()
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(cities,
                linkTo(methodOn(CityController.class).getAllCities()).withSelfRel());
    }


    public EntityModel<City> addCity(City city) {
        City savedCity = cityRepository.save(city);

        return EntityModel.of(savedCity,
                linkTo(methodOn(CityController.class).getCityById(savedCity.getCity_id())).withSelfRel());
    }

    public EntityModel<City> updateCity(Long id, City city) {
        City existingCity = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (existingCity.getCity_name() != null && !existingCity.getCity_name().equals(city.getCity_name()) ) {
            existingCity.setCity_name(city.getCity_name());
        }
        if (existingCity.getDescription() != null && !existingCity.getDescription().equals(city.getDescription()) ) {
            existingCity.setDescription(city.getDescription());
        }

        City updatedCity = cityRepository.save(existingCity);

        return EntityModel.of(updatedCity,
                linkTo(methodOn(CityController.class).getCityById(updatedCity.getCity_id())).withSelfRel());
    }
    public void deleteCity(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        cityRepository.delete(city);
    }


}

package com.projetfilrougeapi.apifilrouge.endpoint_api.city;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Cities")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/{id}")
    public EntityModel<City> getCityById(@PathVariable Long id) {
        return cityService.getCityById(id);
    }
    @GetMapping
    public CollectionModel<EntityModel<City>> getAllCities() {
        return cityService.getAllCities();
    }

    @PostMapping
    public EntityModel<City> addCity(@RequestBody City city) {
        return cityService.addCity(city);
    }
    @PatchMapping("/{id}")
    public EntityModel<City> updateCity(@PathVariable Long id, @RequestBody City city) {
        return cityService.updateCity(id, city);
    }
    @DeleteMapping("/{id}")
    public void deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
    }

}

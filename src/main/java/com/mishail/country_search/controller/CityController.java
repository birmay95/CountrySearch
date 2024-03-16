package com.mishail.country_search.controller;

import com.mishail.country_search.model.City;
import com.mishail.country_search.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class CityController {

    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping(path = "cities")
    public Set<City> getCities() {
        return cityService.getCities();
    }

    @GetMapping(path = "countries/{countryId}/cities")
    public Set<City> getCitiesByCountryId(@PathVariable(value = "countryId") Long countryId) {
        return cityService.getCitiesByCountryId(countryId);
    }

    @PostMapping(path = "countries/{countryId}/cities")
    public void registerNewCityByCountryId(@PathVariable(value = "countryId") Long countryId, @RequestBody City city) {
        cityService.addNewCityByCountryId(countryId, city);
    }

    @DeleteMapping(path = "countries/{countryId}/cities")
    public void deleteCitiesByCountryId(@PathVariable(value = "countryId") Long countryId) {
        cityService.deleteCitiesByCountryId(countryId);
    }

    @PutMapping(path = "cities/{id}")
    public void updateCity(
            @PathVariable("id") Long cityId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double population,
            @RequestParam(required = false) Double areaSquareKm) {
        cityService.updateCity(cityId, name, population, areaSquareKm);
    }
}

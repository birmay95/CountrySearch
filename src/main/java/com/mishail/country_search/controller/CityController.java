package com.mishail.country_search.controller;

import com.mishail.country_search.model.City;
import com.mishail.country_search.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "Cities", description = "You can view, add, update"
        + " and delete information about cities")
@CrossOrigin
public class CityController {

    private final CityService cityService;

    @GetMapping(path = "cities")
    @Operation(method = "GET",
            summary = "Get cities",
            description = "Get information about all cities")
    public ResponseEntity<List<City>> getCities() {
        List<City> cities = cityService.getCities();
        if (cities.isEmpty()) {
            return new ResponseEntity<>(cities, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }

    @GetMapping(path = "countries/{countryId}/cities")
    @Operation(method = "GET",
            summary = "Get all cities by country",
            description = "Get information about all cities"
                    + " from country by its id")
    public ResponseEntity<Set<City>> getCitiesByCountryId(
            @PathVariable(value = "countryId")
            @Parameter(description = "Id of the country,"
                    + " which cities you want to see") final Long countryId) {
        Set<City> cities = cityService.getCitiesByCountryId(countryId);
        if (cities.isEmpty()) {
            return new ResponseEntity<>(cities, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }

    @PostMapping(path = "countries/{countryId}/city")
    @Operation(method = "POST",
            summary = "Add city in country",
            description = "Add new city in country by its id")
    public ResponseEntity<City> addNewCityByCountryId(
            @PathVariable(value = "countryId")
            @Parameter(description = "Id of the country, "
                    + "in which you want to add city") final Long countryId,
            @RequestBody final City city) {
        return new ResponseEntity<>(cityService
                .addNewCityByCountryId(countryId, city),
                HttpStatus.CREATED);
    }

    @PostMapping(path = "countries/{countryId}/cities")
    @Operation(method = "POST",
            summary = "Add cities in country",
            description = "Add new list of cities in country by its id")
    public ResponseEntity<List<City>> addNewCitiesByCountryId(
            @PathVariable(value = "countryId")
            @Parameter(description = "Id of the country, "
                    + "in which you want to add cities") final Long countryId,
            @RequestBody final List<City> cities) {
        return new ResponseEntity<>(cityService
                .addNewCitiesByCountryId(countryId, cities),
                HttpStatus.CREATED);
    }

    @PutMapping(path = "cities/{id}")
    @Operation(method = "PUT",
            summary = "Update city",
            description = "Update information about city by its id")
    public ResponseEntity<City> updateCity(
            @PathVariable("id") final Long cityId,
            @RequestParam(required = false)
            @Parameter(description = "Name of city")
            final String name,
            @RequestParam(required = false)
            @Parameter(description = "Quantity of population of city")
            final Double population,
            @RequestParam(required = false)
            @Parameter(description = "Area of city in square km")
            final Double areaSquareKm) {
        return new ResponseEntity<>(cityService
                .updateCity(cityId, name, population, areaSquareKm),
                HttpStatus.OK);
    }

    @DeleteMapping(path = "countries/{countryId}/cities")
    @Operation(method = "DELETE",
            summary = "Delete cities from country",
            description = "Delete all cities from country by its id")
    public ResponseEntity<HttpStatus> deleteCitiesByCountryId(
            @PathVariable(value = "countryId")
            @Parameter(description = "Id of the country,"
                    + " in which you want to delete all cities")
            final Long countryId) {
        cityService.deleteCitiesByCountryId(countryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "countries/{countryId}/cities/{cityId}")
    @Operation(method = "DELETE",
            summary = "Delete city from country",
            description = "Delete city by its id from country by id")
    public ResponseEntity<HttpStatus> deleteCityByIdFromCountryByCountryId(
            @PathVariable(value = "countryId")
            @Parameter(description = "Id of the country,"
                    + " in which you want to delete city")
            final Long countryId,
            @PathVariable(value = "cityId")
            @Parameter(description = "Id of the city,"
                    + " that's need to delete from country")
            final Long cityId) {
        cityService.deleteCityByIdFromCountryByCountryId(countryId, cityId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

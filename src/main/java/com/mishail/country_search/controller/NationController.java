package com.mishail.country_search.controller;

import com.mishail.country_search.model.Country;
import com.mishail.country_search.model.Nation;
import com.mishail.country_search.service.NationService;
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
@Tag(name = "Nations",
        description = "You can view, add, "
                + "update and delete information about nations")
@CrossOrigin
public class NationController {

    private final NationService nationService;

    @GetMapping("countries/{countryId}/nations")
    @Operation(method = "GET",
            summary = "Get nations from country",
            description = "Get information about all nations "
                    + "from country by its id")
    public ResponseEntity<Set<Nation>> getNationsByCountryId(
            @PathVariable(value = "countryId")
            @Parameter(description = "Id of the country, "
                    + "which nations you want to see") final Long countryId) {
        Set<Nation> nations = nationService.getNationsByCountryId(countryId);
        if (nations.isEmpty()) {
            return new ResponseEntity<>(nations, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(nations, HttpStatus.OK);
    }

    @GetMapping("/nations")
    @Operation(method = "GET",
            summary = "Get nations",
            description = "Get information about all nations")
    public ResponseEntity<List<Nation>> getNations() {
        List<Nation> nations = nationService.getNations();
        if (nations.isEmpty()) {
            return new ResponseEntity<>(nations, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(nations, HttpStatus.OK);
    }

    @GetMapping("nations/{nationId}/countries")
    @Operation(method = "GET",
            summary = "Get countries from nation",
            description = "Get information about all countries "
                    + "from nation by its id")
    public ResponseEntity<Set<Country>> getCountriesByNationId(
            @PathVariable(value = "nationId")
            @Parameter(description = "Id of the nation, "
                    + "which countries you want to see") final Long nationId) {
        Set<Country> countries = nationService.getCountriesByNationId(nationId);
        if (countries.isEmpty()) {
            return new ResponseEntity<>(countries, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(countries, HttpStatus.OK);
    }

    @PostMapping("countries/{countryId}/nation")
    @Operation(method = "POST",
            summary = "Add nation",
            description = "Add new nation at country by its id")
    public ResponseEntity<Nation> addNewNationByCountryId(
            @PathVariable(value = "countryId")
            @Parameter(description = "Id of the country, "
                    + "in which you want to add nation") final Long countryId,
            @RequestBody final Nation nation) {
        return new ResponseEntity<>(nationService
                .addNewNationByCountryId(countryId, nation),
                HttpStatus.CREATED);
    }

    @PostMapping("countries/{countryId}/nations")
    @Operation(method = "POST",
            summary = "Add nations",
            description = "Add new list of nations at country by its id")
    public ResponseEntity<List<Nation>> addNewNationsByCountryId(
            @PathVariable(value = "countryId")
            @Parameter(description = "Id of the country, "
                    + "in which you want to add nations") final Long countryId,
            @RequestBody final List<Nation> nations) {
        return new ResponseEntity<>(nationService
                .addNewNationsByCountryId(countryId, nations),
                HttpStatus.CREATED);
    }

    @PutMapping("/nations/{id}")
    @Operation(method = "PUT",
            summary = "Update nation",
            description = "Update information about nation by its id")
    public ResponseEntity<Nation> updateNation(
            @PathVariable("id") final Long cityId,
            @RequestParam(required = false)
            @Parameter(description = "Name of the nation")
            final String name,
            @RequestParam(required = false)
            @Parameter(description = "Name of language of the nation")
            final String language,
            @RequestParam(required = false)
            @Parameter(description = "Name of religion og the nation")
            final String religion) {
        return new ResponseEntity<>(nationService
                .updateNation(cityId, name, language, religion), HttpStatus.OK);
    }

    @DeleteMapping("/nations/{id}")
    @Operation(method = "DELETE",
            summary = "Delete nation",
            description = "Delete nation from existing nations")
    public ResponseEntity<HttpStatus> deleteNation(
            @PathVariable(value = "id")
            @Parameter(description = "Id of the nation, that's need to delete")
            final Long nationId) {
        nationService.deleteNation(nationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/countries/{countryId}/nations/{nationId}")
    @Operation(method = "DELETE",
            summary = "Delete nation from country",
            description = "Delete nation by its id from country by id")
    public ResponseEntity<HttpStatus> deleteNationFromCountry(
            @PathVariable(value = "countryId")
            @Parameter(description = "Id of the country, "
                    + "in which you want to delete nation")
            final Long countryId,
            @PathVariable(value = "nationId")
            @Parameter(description = "Id of the nation, "
                    + "that's need to delete from country")
            final Long nationId) {
        nationService.deleteNationFromCountry(countryId, nationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

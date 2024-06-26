package com.mishail.country_search.service;

import com.mishail.country_search.cache.CacheService;
import com.mishail.country_search.exception.ObjectExistedException;
import com.mishail.country_search.exception.ObjectNotFoundException;
import com.mishail.country_search.model.Country;
import com.mishail.country_search.model.Nation;
import com.mishail.country_search.repository.CountryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class CountryService {

    private final CountryRepository countryRepository;

    private final CacheService cacheService;

    private static final String ALL_COUNTRIES = "allCountries";
    private static final String COUNTRY_ID = "countryId_";
    private static final String ALL_CITIES_BY_COUNTRY_ID =
            "allCitiesByCountryId_";
    private static final String ALL_CITIES = "allCities";
    private static final String ALL_NATIONS_BY_COUNTRY_ID =
            "allNationsByCountryId_";
    private static final String ALL_COUNTRIES_BY_NATION_ID =
            "allCountriesByNationId_";

    public List<Country> getCountries() {

        if (cacheService.containsKey(ALL_COUNTRIES)) {
            return (List<Country>) cacheService.get(ALL_COUNTRIES);
        } else {
            List<Country> countries = countryRepository
                    .findAllWithCitiesAndNations();
            cacheService.put(ALL_COUNTRIES, countries);
            return countries;
        }
    }

    public Country getCountryById(final Long countryId) {

        if (cacheService.containsKey(COUNTRY_ID + countryId)) {
            return (Country) cacheService.get(COUNTRY_ID + countryId);
        } else {
            Country country = countryRepository
                    .findCountryWithCitiesAndNationsById(countryId)
                    .orElseThrow(() -> new ObjectNotFoundException(
                            "country with id " + countryId
                                    + " does not exist"));
            cacheService.put(COUNTRY_ID + countryId, country);
            return country;
        }
    }

    public Country addNewCountry(final Country country) {

        Optional<Country> countryOptional = countryRepository
                .findCountryByName(country.getName());
        if (countryOptional.isPresent()) {
            throw new ObjectExistedException("country exists");
        }
        if (country.getNations() == null) {
            country.setNations(new HashSet<>());
        }
        if (country.getCities() == null) {
            country.setCities(new HashSet<>());
        }
        countryRepository.save(country);
        if (cacheService.containsKey(ALL_COUNTRIES)) {
            List<Country> countries = (List<Country>) cacheService
                    .get(ALL_COUNTRIES);
            countries.add(country);
            cacheService.put(ALL_COUNTRIES, countries);
        }
        cacheService.put(COUNTRY_ID + country.getId(), country);

        return country;
    }

    public List<Country> addNewCountries(final List<Country> countries) {

        List<Country> addedCountries = new ArrayList<>();

        countries.forEach(country -> addedCountries
                .add(addNewCountry(country)));

        return addedCountries;
    }

    void updateCacheForCountry(Country countryChanged) {
        if (cacheService.containsKey(ALL_COUNTRIES)) {
            cacheService.remove(ALL_COUNTRIES);
        }
        if (cacheService.containsKey(COUNTRY_ID + countryChanged.getId())) {
            cacheService.put(COUNTRY_ID + countryChanged.getId(), countryChanged);
        }
        for(Nation nation : countryChanged.getNations()) {
            if (cacheService.containsKey(
                    ALL_COUNTRIES_BY_NATION_ID + nation.getId())) {
                cacheService.remove(ALL_COUNTRIES_BY_NATION_ID + nation.getId());
            }
        }
    }

    @Transactional
    public Country updateCountry(final Long countryId,
                                 final String name,
                                 final String capital,
                                 final Double population,
                                 final Double areaSquareKm,
                                 final Double gdp) {

        Country countryChanged = countryRepository
                .findCountryWithCitiesAndNationsById(countryId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "country with id " + countryId
                                + "can not be updated, "
                                + "because it does not exist"));

        Country countryBeforeChanges = new Country();
        BeanUtils.copyProperties(countryChanged, countryBeforeChanges);

        if (name != null && !name.isEmpty()
                && !Objects.equals(countryChanged.getName(), name)) {
            Optional<Country> countryOptional = countryRepository
                    .findCountryByName(name);
            if (countryOptional.isPresent()) {
                throw new ObjectExistedException(
                        "country with this name exists");
            }
            countryChanged.setName(name);
        }

        if (capital != null && !capital.isEmpty()
                && !Objects.equals(countryChanged.getCapital(), capital)) {
            countryChanged.setCapital(capital);
        }

        if (population != null && population > 0) {
            countryChanged.setPopulation(population);
        }

        if (areaSquareKm != null && areaSquareKm > 0) {
            countryChanged.setAreaSquareKm(areaSquareKm);
        }

        if (gdp != null && gdp > 0) {
            countryChanged.setGdp(gdp);
        }

        updateCacheForCountry(countryChanged);

        return countryChanged;
    }

    public void deleteCountry(final Long countryId) {

        Country country = countryRepository
                .findCountryWithCitiesAndNationsById(countryId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "country, which id " + countryId + " does not exist"));
        if (cacheService.containsKey(COUNTRY_ID + country.getId())) {
            cacheService.remove(COUNTRY_ID + country.getId());
        }
        if (cacheService.containsKey(ALL_COUNTRIES)) {
            cacheService.remove(ALL_COUNTRIES);
        }
        if (cacheService.containsKey(ALL_CITIES_BY_COUNTRY_ID
                + country.getId())) {
            cacheService.remove(ALL_CITIES_BY_COUNTRY_ID + country.getId());
        }
        if (cacheService.containsKey(ALL_CITIES)) {
            cacheService.remove(ALL_CITIES);
        }
        if (cacheService.containsKey(
                ALL_NATIONS_BY_COUNTRY_ID + country.getId())) {
            cacheService.remove(
                    ALL_NATIONS_BY_COUNTRY_ID + country.getId());
        }
        for(Nation nation : country.getNations()) {
            if (cacheService.containsKey(
                    ALL_COUNTRIES_BY_NATION_ID + nation.getId())) {
                cacheService.remove(ALL_COUNTRIES_BY_NATION_ID + nation.getId());
            }
        }
        country.getCities().clear();
        countryRepository.deleteById(countryId);
    }

    public void deleteCountries() {

        List<Country> countries = countryRepository.findAllWithCities();
        for (Country country : countries) {
            country.getCities().clear();
        }
        countryRepository.deleteAll();
        cacheService.clear();
    }
}

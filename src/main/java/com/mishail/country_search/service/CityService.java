package com.mishail.country_search.service;

import com.mishail.country_search.cache.CacheService;
import com.mishail.country_search.exception.ObjectExistedException;
import com.mishail.country_search.exception.ObjectNotFoundException;
import com.mishail.country_search.repository.CountryRepository;
import com.mishail.country_search.model.City;
import com.mishail.country_search.model.Country;
import com.mishail.country_search.repository.CityRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    private final CountryRepository countryRepository;

    private final CacheService cacheService;

    private static final String ALL_CITIES_BY_COUNTRY_ID =
            "allCitiesByCountryId_";
    private static final String ALL_CITIES = "allCities";

    private void updateCache(final Country country) {

        if (cacheService.containsKey(ALL_CITIES_BY_COUNTRY_ID
                + country.getId())) {
            cacheService.put(ALL_CITIES_BY_COUNTRY_ID + country.getId(),
                    country.getCities());
        }

        if (cacheService.containsKey("allCountries")) {
            cacheService.remove("allCountries");
        }

        if (cacheService.containsKey("countryId_" + country.getId())) {
            cacheService.remove("countryId_" + country.getId());
        }
    }

    public List<City> getCities() {
        if (cacheService.containsKey(ALL_CITIES)) {
            return (List<City>) cacheService.get(ALL_CITIES);
        } else {
            List<City> cities = cityRepository.findAll();
            cacheService.put(ALL_CITIES, cities);
            return cities;
        }
    }

    public Set<City> getCitiesByCountryId(final Long countryId) {
        if (cacheService.containsKey(ALL_CITIES_BY_COUNTRY_ID + countryId)) {
            return (Set<City>) cacheService
                    .get(ALL_CITIES_BY_COUNTRY_ID + countryId);
        } else {
            Country country = countryRepository
                    .findCountryWithCitiesById(countryId)
                    .orElseThrow(() -> new ObjectNotFoundException(
                            "country with id " + countryId
                                    + " does not exist, that's why "
                                    + "you can't view cities from its"));
            Set<City> cities = country.getCities();
            cacheService.put(ALL_CITIES_BY_COUNTRY_ID + countryId, cities);
            return cities;
        }
    }

    @Transactional
    public City addNewCityByCountryId(final Long countryId,
                                      final City cityRequest) {

        Country country = countryRepository
                .findCountryWithCitiesAndNationsById(countryId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "country, which id " + countryId + " does not exist, "
                                + "that's why you can't add new city"));

        if (country.getCities().stream().noneMatch(
                city -> city.getName().equals(cityRequest.getName()))) {
            country.getCities().add(cityRequest);
            cityRepository.save(cityRequest);
            countryRepository.save(country);
        } else {
            throw new ObjectExistedException("city with name "
                    + cityRequest.getName()
                    + " already exists in the country "
                    + country.getName() + ".");
        }

        if (cacheService.containsKey(ALL_CITIES)) {
            List<City> allCities = (List<City>) cacheService.get(ALL_CITIES);
            allCities.add(cityRequest);
            cacheService.put(ALL_CITIES, allCities);
        }

        updateCache(country);

        return cityRequest;
    }

    @Transactional
    public City updateCity(final Long cityId,
                           final String name,
                           final Double population,
                           final Double areaSquareKm) {

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "city with id " + cityId + " does not exist"));

        Country country = countryRepository
                .findCountryWithCitiesByCityId(cityId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "country with city , which id "
                                + cityId
                                + " can not be updated, "
                                + "because it does not exist"));

        City cityBeforeChanges = new City();
        BeanUtils.copyProperties(city, cityBeforeChanges);

        Set<City> cities = country.getCities();

        if (name != null && !name.isEmpty()
                && !Objects.equals(city.getName(), name)) {
            for (City cityTemp : cities) {
                if (Objects.equals(cityTemp.getName(), name)) {
                    throw new ObjectExistedException(
                            "In this country city with this name exists");
                }
            }
            city.setName(name);
        }

        if (population != null && population > 0) {
            city.setPopulation(population);
        }

        if (areaSquareKm != null && areaSquareKm > 0) {
            city.setAreaSquareKm(areaSquareKm);
        }

        if (cacheService.containsKey(ALL_CITIES)) {
            List<City> allCities = (List<City>) cacheService.get(ALL_CITIES);
            allCities.remove(cityBeforeChanges);
            allCities.add(city);
            cacheService.put(ALL_CITIES, allCities);
        }

        updateCache(country);

        return city;
    }

    @Transactional
    public void deleteCitiesByCountryId(final Long countryId) {
        Country country = countryRepository.findCountryWithCitiesById(countryId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "country, which id " + countryId
                                + " does not exist, that's why "
                                + "you can't delete cities from its"));

        Set<City> citiesBeforeChanges = new HashSet<>(country.getCities());
        Set<City> cities = country.getCities();

        for (City city : cities) {
            cityRepository.deleteById(city.getId());
        }

        country.getCities().clear();
        countryRepository.save(country);

        if (cacheService.containsKey(ALL_CITIES)) {
            List<City> allCities = (List<City>) cacheService.get(ALL_CITIES);
            for (City city : citiesBeforeChanges) {
                allCities.remove(city);
            }
            cacheService.put(ALL_CITIES, allCities);
        }

        updateCache(country);
    }

    @Transactional
    public void deleteCityByIdFromCountryByCountryId(final Long countryId,
                                                     final Long cityId) {
        Country country = countryRepository.findCountryWithCitiesById(countryId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "country with id " + countryId
                                + " does not exist, that's why "
                                + "you can't delete city from its"));

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "city with id " + countryId
                                + " does not exist, that's why "
                                + "you can't delete its"));

        cityRepository.deleteById(city.getId());

        country.getCities().remove(city);
        countryRepository.save(country);

        if (cacheService.containsKey(ALL_CITIES)) {
            List<City> allCities = (List<City>) cacheService.get(ALL_CITIES);
            allCities.remove(city);
            cacheService.put(ALL_CITIES, allCities);
        }

        updateCache(country);
    }
}

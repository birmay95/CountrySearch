package com.mishail.country_search.service;

import com.mishail.country_search.cache.CacheService;
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

    private void updateCache(Country country) {

        if (cacheService.containsKey("allCitiesByCountryId_" + country.getId()))
            cacheService.put("allCitiesByCountryId_" + country.getId(), country.getCities());

        if (cacheService.containsKey("allCountries")) {
            cacheService.evict("allCountries");
        }

        if (cacheService.containsKey("countryId_" + country.getId()))
            cacheService.put("countryId_" + country.getId(), country);
    }

    public List<City> getCities() {
        String cacheKey = "allCities";
        if (cacheService.containsKey(cacheKey)) {
            return (List<City>) cacheService.get(cacheKey);
        } else {
            List<City> cities = cityRepository.findAll();
            cacheService.put(cacheKey, cities);
            return cities;
        }
    }

    public Set<City> getCitiesByCountryId(Long countryId) {

        String cacheKey = "allCitiesByCountryId_" + countryId;
        if (cacheService.containsKey(cacheKey)) {
            return (Set<City>) cacheService.get(cacheKey);
        } else {
            Country country = countryRepository.findCountryWithCitiesById(countryId)
                    .orElseThrow(() -> new IllegalStateException(
                            "country with id " + countryId + " does not exist, that's why you can't view cities from its"));
            Set<City> cities = country.getCities();
            cacheService.put(cacheKey, cities);
            return cities;
        }
    }

    @Transactional
    public void addNewCityByCountryId(Long countryId, City cityRequest) {

        Country country = countryRepository.findCountryWithCitiesById(countryId)
                .orElseThrow(() -> new IllegalStateException(
                        "country, which id " + countryId + " does not exist, that's why you can't add new city"));

        if (country.getCities().stream().noneMatch(city -> city.getName().equals(cityRequest.getName()))) {
            country.getCities().add(cityRequest);
            cityRepository.save(cityRequest);
            countryRepository.save(country);
        } else {
            throw new IllegalStateException("city with name " + cityRequest.getName() + " already exists in the country " + country.getName() + ".");
        }

        if (cacheService.containsKey("allCities")) {
            List<City> allCities = (List<City>) cacheService.get("allCities");
            allCities.add(cityRequest);
            cacheService.put("allCities", allCities);
        }

        updateCache(country);
    }

    @Transactional
    public void deleteCitiesByCountryId(Long countryId) {
        Country country = countryRepository.findCountryWithCitiesById(countryId)
                .orElseThrow(() -> new IllegalStateException(
                        "country with id " + countryId + " does not exist, that's why you can't delete cities from its"));

        Set<City> citiesBeforeChanges = new HashSet<>(country.getCities());
        Set<City> cities = country.getCities();

        for (City city : cities) {
            cityRepository.deleteById(city.getId());
        }

        country.getCities().clear();
        countryRepository.save(country);

        if (cacheService.containsKey("allCities")) {
            List<City> allCities = (List<City>) cacheService.get("allCities");
            for (City city : citiesBeforeChanges) {
                allCities.remove(city);
            }
            cacheService.put("allCities", allCities);
        }

        updateCache(country);
    }

    @Transactional
    public void deleteCityByIdFromCountryByCountryId(Long countryId, Long cityId) {
        Country country = countryRepository.findCountryWithCitiesById(countryId)
                .orElseThrow(() -> new IllegalStateException(
                        "country with id " + countryId + " does not exist, that's why you can't delete city from its"));

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new IllegalStateException(
                        "city with id " + countryId + " does not exist, that's why you can't delete its"));

        cityRepository.deleteById(city.getId());

        country.getCities().remove(city);
        countryRepository.save(country);

        if (cacheService.containsKey("allCities")) {
            List<City> allCities = (List<City>) cacheService.get("allCities");
            allCities.remove(city);
            cacheService.put("allCities", allCities);
        }

        updateCache(country);
    }

    @Transactional
    public void updateCity(Long cityId,
                           String name,
                           Double population,
                           Double areaSquareKm) {

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new IllegalStateException(
                        "city with id " + cityId + " does not exist"));

        Country country = countryRepository.findCountryWithCitiesByCityId(cityId)
                .orElseThrow(() -> new IllegalStateException(
                        "country with city , which id " + cityId + " can not be updated, because it does not exist"));

        City cityBeforeChanges = new City();
        BeanUtils.copyProperties(city, cityBeforeChanges);

        Set<City> cities = country.getCities();

        if (name != null && !name.isEmpty() && !Objects.equals(city.getName(), name)) {
            for (City cityTemp : cities) {
                if (Objects.equals(cityTemp.getName(), name)) {
                    throw new IllegalStateException(
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

        if (cacheService.containsKey("allCities")) {
            List<City> allCities = (List<City>) cacheService.get("allCities");
            allCities.remove(cityBeforeChanges);
            allCities.add(city);
            cacheService.put("allCities", allCities);
        }

        updateCache(country);
    }
}

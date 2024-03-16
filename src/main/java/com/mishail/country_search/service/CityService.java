package com.mishail.country_search.service;

import com.mishail.country_search.repository.CountryRepository;
import com.mishail.country_search.model.City;
import com.mishail.country_search.model.Country;
import com.mishail.country_search.repository.CityRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    private final CountryRepository countryRepository;

    public Set<City> getCities() {
        return new HashSet<>(cityRepository.findAll());
    }

    public Set<City> getCitiesByCountryId(Long countryId) {
        Country country = countryRepository.findByIdWithCities(countryId)
                .orElseThrow(() -> new IllegalStateException(
                        "country with id " + countryId + " does not exist, that's why you can't view cities from its"));
        return country.getCities();
    }

    public void addNewCityByCountryId(Long countryId, City cityRequest) {

        Country country = countryRepository.findByIdWithCities(countryId)
                .orElseThrow(() -> new IllegalStateException(
                        "country, which id " + countryId + " does not exist, that's why you can't add new city"));

        if (country.getCities().stream().noneMatch(city -> city.getName().equals(cityRequest.getName()))) {
            country.getCities().add(cityRequest);
            cityRepository.save(cityRequest);
            countryRepository.save(country);
        } else {
            throw new IllegalStateException("city with name " + cityRequest.getName() + " already exists in the country " + country.getName() + ".");
        }
    }

    public void deleteCitiesByCountryId(Long countryId) {
        Country country = countryRepository.findByIdWithCities(countryId)
                .orElseThrow(() -> new IllegalStateException(
                        "country with id " + countryId + " does not exist, that's why you can't delete cities from its"));

        country.getCities().clear();
        countryRepository.save(country);
    }

    @Transactional
    public void updateCity(Long cityId,
                           String name,
                           Double population,
                           Double areaSquareKm) {

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new IllegalStateException(
                        "city with id " + cityId + " does not exist"));

        if (name != null && !name.isEmpty() && !Objects.equals(city.getName(), name)) {
            Optional<City> cityOptional = cityRepository.findCityByName(name);
            if (cityOptional.isPresent()) {
                throw new IllegalStateException("city with this name exists");
            }
            city.setName(name);
        }

        if (population != null && population > 0) {
            city.setPopulation(population);
        }

        if (areaSquareKm != null && areaSquareKm > 0) {
            city.setAreaSquareKm(areaSquareKm);
        }
    }
}

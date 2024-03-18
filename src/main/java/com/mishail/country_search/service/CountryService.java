package com.mishail.country_search.service;

import com.mishail.country_search.cache.CacheService;
import com.mishail.country_search.model.Country;
import com.mishail.country_search.repository.CountryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CountryService {

    private final CountryRepository countryRepository;

    private final CacheService cacheService;

    public List<Country> getCountries() {
        String cacheKey = "allCountries";
        if (cacheService.containsKey(cacheKey)) {
            return (List<Country>) cacheService.get(cacheKey);
        } else {
            List<Country> countries = countryRepository.findAllWithCitiesAndNations();
            cacheService.put(cacheKey, countries);
            return countries;
        }
    }

    public Country getCountryById(Long countryId) {
        String cacheKey = "countryId_" + countryId;
        if (cacheService.containsKey(cacheKey)) {
            return (Country) cacheService.get(cacheKey);
        } else {
            Country country = countryRepository.findCountryWithCitiesAndNationsById(countryId)
                    .orElseThrow(() -> new IllegalStateException(
                            "country with id " + countryId + "does not exist"));
            cacheService.put(cacheKey, country);
            return country;
        }
    }

    public void addNewCountry(Country country) {
        Optional<Country> countryOptional = countryRepository
                .findCountryByName(country.getName());
        if (countryOptional.isPresent()) {
            throw new IllegalStateException("country exists");
        }
        if (country.getNations() == null)
            country.setNations(new HashSet<>());
        if (country.getCities() == null)
            country.setCities(new HashSet<>());
        countryRepository.save(country);
        if (cacheService.containsKey("allCountries")) {
            List<Country> countries = (List<Country>) cacheService.get("allCountries");
            countries.add(country);
            cacheService.put("allCountries", countries);
        }
        String cacheKey = "countryId_" + country.getId();
        cacheService.put(cacheKey, country);
    }

    @Transactional
    public void deleteCountry(Long countryId) {
        Country country = countryRepository.findCountryWithCitiesById(countryId)
                .orElseThrow(() -> new IllegalStateException(
                        "country with id " + countryId + " does not exist"));
        cacheService.evict("countryId_" + country.getId());
        if (cacheService.containsKey("allCountries")) {
            List<Country> countries = (List<Country>) cacheService.get("allCountries");
            countries.remove(country);
            cacheService.put("allCountries", countries);
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

    @Transactional
    public void updateCountry(Long countryId,
                              String name,
                              String capital,
                              Double population,
                              Double areaSquareKm,
                              Double gdp) {
        Country countryChanged = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalStateException(
                        "country with id " + countryId + "can not be updated, because it does not exist"));

        Country countryBeforeChanges = new Country();
        BeanUtils.copyProperties(countryChanged, countryBeforeChanges);

        if (name != null && !name.isEmpty() && !Objects.equals(countryChanged.getName(), name)) {
            Optional<Country> countryOptional = countryRepository.findCountryByName(name);
            if (countryOptional.isPresent()) {
                throw new IllegalStateException("country with this name exists");
            }
            countryChanged.setName(name);
        }

        if (capital != null && !capital.isEmpty() && !Objects.equals(countryChanged.getCapital(), capital)) {
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

        if (cacheService.containsKey("allCountries")) {
            List<Country> countries = (List<Country>) cacheService.get("allCountries");
            countries.remove(countryBeforeChanges);
            countries.add(countryChanged);
            cacheService.put("allCountries", countries);
        }
        String cacheKey = "countryId_" + countryChanged.getId();
        cacheService.put(cacheKey, countryChanged);
    }
}

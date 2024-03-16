package com.mishail.country_search.service;

import com.mishail.country_search.model.Country;
import com.mishail.country_search.repository.CountryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CountryService {

    private final CountryRepository countryRepository;

    public List<Country> getCountries() {
        return countryRepository.findAllWithCitiesAndNations();
    }

    public Country getCountryById(Long countryId) {
        return countryRepository.findByIdWithCitiesAndNations(countryId)
                .orElseThrow(() -> new IllegalStateException(
                        "country with id " + countryId + "does not exist"));
    }

    public void addNewCountry(Country country) {
        Optional<Country> countryOptional = countryRepository
                .findCountryByName(country.getName());
        if (countryOptional.isPresent()) {
            throw new IllegalStateException("country exists");
        }
        countryRepository.save(country);
    }

    public void deleteCountry(Long countryId) {
        Country country = countryRepository.findByIdWithCities(countryId)
                .orElseThrow(() -> new IllegalStateException(
                        "country with id " + countryId + "does not exist"));
        country.getCities().clear();
        countryRepository.deleteById(countryId);
    }

    public void deleteCountries() {
        List<Country> countries = countryRepository.findAllWithCities();
        for (Country country : countries) {
            country.getCities().clear();
        }
        countryRepository.deleteAll();
    }

    @Transactional
    public void updateCountry(Long countryId,
                              String name,
                              String capital,
                              Double population,
                              Double areaSquareKm,
                              Double gdp) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalStateException(
                        "country with id " + countryId + "can not be updated, because it does not exist"));

        if (name != null && !name.isEmpty() && !Objects.equals(country.getName(), name)) {
            Optional<Country> countryOptional = countryRepository.findCountryByName(name);
            if (countryOptional.isPresent()) {
                throw new IllegalStateException("country with this name exists");
            }
            country.setName(name);
        }

        if (capital != null && !capital.isEmpty() && !Objects.equals(country.getCapital(), capital)) {
            country.setCapital(capital);
        }

        if (population != null && population > 0) {
            country.setPopulation(population);
        }

        if (areaSquareKm != null && areaSquareKm > 0) {
            country.setAreaSquareKm(areaSquareKm);
        }

        if (gdp != null && gdp > 0) {
            country.setGdp(gdp);
        }
    }
}

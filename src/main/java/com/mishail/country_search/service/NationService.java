package com.mishail.country_search.service;

import com.mishail.country_search.model.Country;
import com.mishail.country_search.model.Nation;
import com.mishail.country_search.repository.CountryRepository;
import com.mishail.country_search.repository.NationRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class NationService {

    private final NationRepository nationRepository;

    private final CountryRepository countryRepository;

    public Set<Nation> getNationsByCountryId(Long countryId) {
        Country country = countryRepository.findByIdWithNations(countryId)
                .orElseThrow(() -> new IllegalStateException(
                        "country with id " + countryId + " does not exist, that's why you can't view nations from its"));
        return country.getNations();
    }

    public List<Nation> getNations() {
        return nationRepository.findAll();
    }

    public Set<Country> getCountriesByNationId(Long nationId) {
        Nation nation = nationRepository.findByIdWithCountriesWithCities(nationId)
                .orElseThrow(() -> new IllegalStateException(
                        "nation, which id " + nationId + " does not exist, that's why you can't view countries from its"));
        return new HashSet<>(nation.getCountries());
    }

    public void addNewNationByCountryId(Long countryId, Nation nationRequest) {

        Country country = countryRepository.findByIdWithNations(countryId)
                .orElseThrow(() -> new IllegalStateException(
                        "country, which id " + countryId + " does not exist, that's why you can't add nation to its"));

        Nation nation = nationRepository.findNationsByName(nationRequest.getName());

        if (country.getNations().stream().noneMatch(nationFunc -> nationFunc.getName().equals(nationRequest.getName()))) {
            if (nation != null) {
                country.getNations().add(nation);
                countryRepository.save(country);
            } else {
                nationRepository.save(nationRequest);
                country.getNations().add(nationRequest);
                countryRepository.save(country);
            }
        } else {
            throw new IllegalStateException("nation with name " + nationRequest.getName() + " already exists in the country " + country.getName() + ".");
        }

    }

    @Transactional
    public void updateNation(Long nationId,
                             String name,
                             String language,
                             String religion) {
        Nation nation = nationRepository.findById(nationId)
                .orElseThrow(() -> new IllegalStateException(
                        "nation with id " + nationId + " does not exist, that's why you can't update this"));

        if (name != null && !name.isEmpty() && !Objects.equals(nation.getName(), name)) {
            Optional<Nation> nationOptional = Optional.ofNullable(nationRepository.findNationsByName(name));
            if (nationOptional.isPresent()) {
                throw new IllegalStateException("nation with this name exists");
            }
            nation.setName(name);
        }

        if (language != null && !language.isEmpty() && !Objects.equals(nation.getLanguage(), language)) {
            nation.setLanguage(language);
        }

        if (religion != null && !religion.isEmpty() && !Objects.equals(nation.getReligion(), religion)) {
            nation.setReligion(religion);
        }
    }

    @Transactional
    public void deleteNation(Long nationId) {

        Nation nation = nationRepository.findByIdWithCountries(nationId)
                .orElseThrow(() -> new IllegalStateException(
                        "nation, which id " + nationId + " does not exist, that's why you can't delete its"));

        List<Country> countries = countryRepository.findCountriesByNationId(nationId);

        for (Country country : countries) {
            country.getNations().remove(nation);
            countryRepository.save(country);
        }

        nationRepository.delete(nation);
    }

    @Transactional
    public void deleteNationFromCountry(Long countryId, Long nationId) {

        Country country = countryRepository.findByIdWithNations(countryId)
                .orElseThrow(() -> new IllegalStateException(
                        "country with id " + countryId + " doesn't exist, that's why you can't delete its"));

        Nation nation = nationRepository.findById(nationId)
                .orElseThrow(() -> new IllegalStateException(
                        "nation with id " + nationId + " does not exist, that's why you can't delete its"));

        country.getNations().remove(nation);
        countryRepository.save(country);
    }
}

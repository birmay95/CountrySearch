package com.mishail.country_search.service;

import com.mishail.country_search.cache.CacheService;
import com.mishail.country_search.exception.ObjectExistedException;
import com.mishail.country_search.exception.ObjectNotFoundException;
import com.mishail.country_search.model.Country;
import com.mishail.country_search.model.Nation;
import com.mishail.country_search.repository.CountryRepository;
import com.mishail.country_search.repository.NationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class NationServiceTest {

    @Mock
    private NationRepository nationRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private NationService nationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getNationsWhenNotCached() {
        List<Nation> nations = new ArrayList<>();
        when(cacheService.containsKey("allNations")).thenReturn(false);
        when(nationRepository.findAll()).thenReturn(nations);

        List<Nation> result = nationService.getNations();

        assertEquals(nations, result);
        verify(cacheService).put("allNations", nations);
    }

    @Test
    void getNationsWhenCached() {
        List<Nation> nations = new ArrayList<>();
        when(cacheService.containsKey("allNations")).thenReturn(true);
        when(cacheService.get("allNations")).thenReturn(nations);

        List<Nation> result = nationService.getNations();

        assertEquals(nations, result);
        verifyNoInteractions(nationRepository);
    }

    @Test
    void getNationsByCountryByIdWhenNotCached() {
        Long countryId = 1L;
        Set<Nation> nations = new HashSet<>();
        Country country = new Country();
        country.setId(countryId);
        country.setNations(nations);
        when(cacheService.containsKey("allNationsByCountryId_" + countryId)).thenReturn(false);
        when(countryRepository.findCountryWithNationsById(countryId))
                .thenReturn(Optional.of(country));

        Set<Nation> result = nationService.getNationsByCountryId(countryId);

        assertEquals(nations, result);
        verify(cacheService).put("allNationsByCountryId_" + countryId, nations);
    }

    @Test
    void getNationsByCountryByIdWhenNotCachedAndNotExist() {
        Long countryId = 1L;
        when(cacheService.containsKey("allNationsByCountryId_" + countryId)).thenReturn(false);
        when(countryRepository.findCountryWithNationsById(countryId))
                .thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> nationService.getNationsByCountryId(countryId));

        verify(countryRepository, times(1)).findCountryWithNationsById(countryId);
        verifyNoMoreInteractions(countryRepository);
        verify(cacheService, times(1)).containsKey("allNationsByCountryId_" + countryId);
        verifyNoMoreInteractions(cacheService);
    }

    @Test
    void getNationsByCountryByIdWhenCached() {
        Long countryId = 1L;
        Set<Nation> nations = new HashSet<>();
        when(cacheService.containsKey("allNationsByCountryId_" + countryId)).thenReturn(true);
        when(cacheService.get("allNationsByCountryId_" + countryId)).thenReturn(nations);

        Set<Nation> result = nationService.getNationsByCountryId(countryId);

        assertEquals(nations, result);
        verifyNoInteractions(countryRepository);
    }

    @Test
    void getCountriesByNationByIdWhenNotCached() {
        Long nationId = 1L;
        Set<Country> countries = new HashSet<>();
        Nation nation = new Nation();
        nation.setId(nationId);
        nation.setCountries(new ArrayList<>(countries));
        when(cacheService.containsKey("allCountriesByNationId_" + nationId)).thenReturn(false);
        when(nationRepository.findByIdWithCountriesWithCities(nationId))
                .thenReturn(Optional.of(nation));

        Set<Country> result = nationService.getCountriesByNationId(nationId);

        assertEquals(countries, result);
        verify(cacheService).put("allCountriesByNationId_" + nationId, countries);
    }

    @Test
    void getCountriesByNationByIdWhenNotCachedAndNotExist() {
        Long nationId = 1L;
        when(cacheService.containsKey("allCountriesByNationId_" + nationId)).thenReturn(false);
        when(nationRepository.findByIdWithCountriesWithCities(nationId))
                .thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> nationService.getCountriesByNationId(nationId));

        verify(cacheService, times(1)).containsKey("allCountriesByNationId_" + nationId);
        verifyNoMoreInteractions(cacheService);
        verify(nationRepository, times(1)).findByIdWithCountriesWithCities(nationId);
        verifyNoMoreInteractions(nationRepository);
    }

    @Test
    void getCountriesByNationByIdWhenCached() {
        Long nationId = 1L;
        Set<Country> countries = new HashSet<>();
        when(cacheService.containsKey("allCountriesByNationId_" + nationId)).thenReturn(true);
        when(cacheService.get("allCountriesByNationId_" + nationId)).thenReturn(countries);

        Set<Country> result = nationService.getCountriesByNationId(nationId);

        assertEquals(countries, result);
        verifyNoInteractions(nationRepository);
    }

    @Test
    void addNationByCountryById() {
        Long countryId = 1L;
        String nationName = "Russian";

        Country country = new Country();
        country.setId(countryId);
        Set<Nation> nations = new HashSet<>();
        country.setNations(nations);

        Nation existingNation = new Nation();
        existingNation.setName(nationName);

        Nation nationRequest = new Nation();
        nationRequest.setName(nationName);

        when(countryRepository.findCountryWithNationsById(countryId)).thenReturn(Optional.of(country));
        when(nationRepository.findNationByName(nationName)).thenReturn(existingNation);
        when(cacheService.containsKey(anyString())).thenReturn(false);


        Nation result = nationService.addNewNationByCountryId(countryId, nationRequest);

        verify(countryRepository).save(country);
        assertEquals(existingNation, result);
        assertTrue(country.getNations().contains(nationRequest));
    }

    @Test
    void addNewNationByCountryByIdExistingNation() {
        Long countryId = 1L;
        String nationName = "Russian";

        Country country = new Country();
        country.setId(countryId);

        Nation nationRequest = new Nation();
        nationRequest.setName(nationName);
        Nation existedNation = new Nation();
        existedNation.setName("Russian");

        Set<Nation> nations = new HashSet<>();
        nations.add(existedNation);
        country.setNations(nations);

        when(countryRepository.findCountryWithNationsById(countryId)).thenReturn(Optional.of(country));
        when(nationRepository.findNationByName(nationName)).thenReturn(null);

        assertThrows(ObjectExistedException.class, () -> nationService.addNewNationByCountryId(countryId, nationRequest));
        verify(countryRepository, times(1)).findCountryWithNationsById(countryId);
        verifyNoMoreInteractions(countryRepository);
        verify(nationRepository, times(1)).findNationByName(nationName);
        verifyNoMoreInteractions(nationRepository);
    }

    @Test
    void addNewNationByCountryByIdNonexistentCountry() {
        Long countryId = 1L;
        Nation nationRequest = new Nation();
        when(countryRepository.findCountryWithNationsById(countryId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> nationService.addNewNationByCountryId(countryId, nationRequest));
        verifyNoInteractions(nationRepository);
        verifyNoInteractions(cacheService);
        verify(countryRepository, times(1)).findCountryWithNationsById(countryId);
        verifyNoMoreInteractions(countryRepository);
    }

    @Test
    void updateNation() {
        Long nationId = 1L;
        String name = "Russian";
        String language = "Russian";
        String religion = "Christianity";

        Nation nation = new Nation();
        nation.setId(nationId);
        nation.setName("Belarusian");
        nation.setLanguage("Belarusian");
        nation.setReligion("Christianity");

        Nation updatedNation = new Nation();
        BeanUtils.copyProperties(nation, updatedNation);
        updatedNation.setName(name);
        updatedNation.setLanguage(language);
        updatedNation.setReligion(religion);

        when(nationRepository.findById(nationId)).thenReturn(Optional.of(nation));
        when(nationRepository.findNationByName(name)).thenReturn(null);
        when(cacheService.containsKey(anyString())).thenReturn(false);

        Nation result = nationService.updateNation(nationId, name, language, religion);

        assertEquals(updatedNation, result);
    }

    @Test
    void updateNationWithNameAlreadyExists() {
        Long nationId = 1L;
        String name = "Russian";
        String language = "Russian";
        String religion = "Christianity";

        Nation nation = new Nation();
        nation.setId(nationId);
        nation.setName("Belarus");

        when(nationRepository.findById(nationId)).thenReturn(Optional.of(nation));
        when(nationRepository.findNationByName(name)).thenReturn(new Nation());
        when(cacheService.containsKey(anyString())).thenReturn(false);

        assertThrows(ObjectExistedException.class, () -> nationService.updateNation(nationId, name, language, religion));
    }

    @Test
    void updateNationWhenDoesNotExist() {
        Long nationId = 1L;
        String name = "Russian";
        String language = "Russian";
        String religion = "Christianity";

        when(nationRepository.findById(nationId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> nationService.updateNation(nationId, name, language, religion));
        verifyNoInteractions(cacheService);
        verify(nationRepository, times(1)).findById(nationId);
        verifyNoMoreInteractions(nationRepository);
    }

    @Test
    void deleteNation() {
        Long nationId = 1L;
        Nation nation = new Nation();
        nation.setId(nationId);
        Set<Nation> nations = new HashSet<>();
        nations.add(nation);

        List<Country> countries = new ArrayList<>();
        Country countryOne = new Country();
        countryOne.setId(1L);
        countryOne.setNations(nations);
        Country countryTwo = new Country();
        countryTwo.setId(2L);
        countryTwo.setNations(nations);
        countries.add(countryOne);
        countries.add(countryTwo);

        when(nationRepository.findByIdWithCountries(nationId)).thenReturn(Optional.of(nation));
        when(cacheService.containsKey(anyString())).thenReturn(false);
        when(countryRepository.findCountriesWithNationsByNationByNationId(nationId)).thenReturn(countries);

        nationService.deleteNation(nationId);

        verify(nationRepository).delete(nation);
        verify(countryRepository).save(countryOne);
        verify(countryRepository).save(countryTwo);
        assertFalse(countryOne.getNations().contains(nation));
        assertFalse(countryTwo.getNations().contains(nation));
    }

    @Test
    void deleteNationWhenNotExists() {
        Long nationId = 1L;
        when(nationRepository.findByIdWithCountries(nationId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> nationService.deleteNation(nationId));
        verifyNoInteractions(cacheService);
        verifyNoInteractions(countryRepository);
        verify(nationRepository, times(1)).findByIdWithCountries(nationId);
        verifyNoMoreInteractions(nationRepository);
    }

    @Test
    void deleteNationFromCountry() {
        Long countryId = 1L;
        Long nationId = 2L;
        Set<Nation> nations = new HashSet<>();
        Nation nation = new Nation();
        nation.setId(nationId);
        nations.add(nation);

        Country country = new Country();
        country.setId(countryId);
        country.setNations(nations);

        when(countryRepository.findCountryWithNationsById(countryId)).thenReturn(Optional.of(country));
        when(nationRepository.findById(nationId)).thenReturn(Optional.of(nation));
        when(cacheService.containsKey(anyString())).thenReturn(false);

        nationService.deleteNationFromCountry(countryId, nationId);

        verify(countryRepository).save(country);
        assertFalse(country.getNations().contains(nation));
    }

    @Test
    void deleteNationFromCountryWhenCountryNotExists() {
        Long countryId = 1L;
        Long nationId = 2L;

        when(countryRepository.findCountryWithNationsById(countryId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> nationService.deleteNationFromCountry(countryId, nationId));
        verifyNoInteractions(nationRepository);
        verifyNoInteractions(cacheService);
        verify(countryRepository, times(1)).findCountryWithNationsById(countryId);
        verifyNoMoreInteractions(countryRepository);
    }

    @Test
    void deleteNationFromCountryWhenNationNotExists() {
        Long countryId = 1L;
        Long nationId = 2L;

        Country country = new Country();
        country.setId(countryId);

        when(countryRepository.findCountryWithNationsById(countryId)).thenReturn(Optional.of(country));
        when(nationRepository.findById(nationId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> nationService.deleteNationFromCountry(countryId, nationId));
        verifyNoInteractions(cacheService);
        verify(countryRepository, times(1)).findCountryWithNationsById(countryId);
        verifyNoMoreInteractions(countryRepository);
        verify(nationRepository, times(1)).findById(nationId);
        verifyNoMoreInteractions(nationRepository);
    }
}

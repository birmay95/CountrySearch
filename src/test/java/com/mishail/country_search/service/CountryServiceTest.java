package com.mishail.country_search.service;

import com.mishail.country_search.cache.CacheService;
import com.mishail.country_search.exception.ObjectExistedException;
import com.mishail.country_search.exception.ObjectNotFoundException;
import com.mishail.country_search.model.Country;
import com.mishail.country_search.model.Nation;
import com.mishail.country_search.repository.CountryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.BeanUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private CountryService countryService;

    @Test
    void getCountriesWhenNotCached() {
        List<Country> countries = new ArrayList<>();
        when(cacheService.containsKey("allCountries")).thenReturn(false);
        when(countryRepository.findAllWithCitiesAndNations()).thenReturn(countries);

        List<Country> result = countryService.getCountries();

        assertEquals(countries, result);
        verify(cacheService).put("allCountries", countries);
    }

    @Test
    void getCountriesWhenCached() {
        List<Country> countries = new ArrayList<>();
        when(cacheService.containsKey("allCountries")).thenReturn(true);
        when(cacheService.get("allCountries")).thenReturn(countries);

        List<Country> result = countryService.getCountries();

        assertEquals(countries, result);
        verifyNoInteractions(countryRepository);
    }

    @Test
    void getCountryByIdWhenNotCached() {
        Long countryId = 1L;
        Country country = new Country();
        country.setId(countryId);
        when(cacheService.containsKey("countryId_" + countryId)).thenReturn(false);
        when(countryRepository.findCountryWithCitiesAndNationsById(countryId))
                .thenReturn(Optional.of(country));

        Country result = countryService.getCountryById(countryId);

        assertEquals(country, result);
        verify(cacheService).put("countryId_" + countryId, country);
    }
    @Test
    void getCountryByIdWhenNotCachedAndNotExist() {
        Long countryId = 1L;
        when(cacheService.containsKey("countryId_" + countryId)).thenReturn(false);
        when(countryRepository.findCountryWithCitiesAndNationsById(countryId))
                .thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> countryService.getCountryById(countryId));

        verify(countryRepository, times(1)).findCountryWithCitiesAndNationsById(countryId);
        verifyNoMoreInteractions(countryRepository);
        verify(cacheService, times(1)).containsKey("countryId_" + countryId);
        verifyNoMoreInteractions(cacheService);
    }

    @Test
    void getCountryByIdWhenCached() {
        Long countryId = 1L;
        Country country = new Country();
        country.setId(countryId);
        when(cacheService.containsKey("countryId_" + countryId)).thenReturn(true);
        when(cacheService.get("countryId_" + countryId)).thenReturn(country);

        Country result = countryService.getCountryById(countryId);

        assertEquals(country, result);
        verifyNoInteractions(countryRepository);
    }

    @Test
    void addNewCountry() {
        Country country = new Country();
        List<Country> countries = new ArrayList<>();
        when(countryRepository.findCountryByName(anyString())).thenReturn(Optional.empty());
        when(cacheService.containsKey("allCountries")).thenReturn(true);
        when(cacheService.get("allCountries")).thenReturn(countries);

        Country result = countryService.addNewCountry(country);

        assertEquals(country, result);
        verify(cacheService).put("countryId_" + country.getId(), country);
        verify(cacheService).put("allCountries", countries);
        verify(countryRepository).save(country);
        assertTrue(countries.contains(country));
    }

    @Test
    void addNewCountryWithException() {
        Country country = new Country();
        country.setName("Belarus");
        when(countryRepository.findCountryByName("Belarus")).thenReturn(Optional.of(country));

        assertThrows(ObjectExistedException.class, () -> countryService.addNewCountry(country));

        verify(countryRepository, times(1)).findCountryByName("Belarus");
        verifyNoMoreInteractions(countryRepository);
        verifyNoInteractions(cacheService);
    }

    @Test
    void addNewCountries() {
        Country countryOne = new Country();
        countryOne.setId(1L);
        Country countryTwo  = new Country();
        countryTwo.setId(2L);

        List<Country> countries = new ArrayList<>();
        countries.add(countryOne);
        countries.add(countryTwo);

        List<Country> result = countryService.addNewCountries(countries);

        assertEquals(result, countries);
    }

    @Test
    void updateCountry() {
        List<Country> countries = new ArrayList<>();

        Long countryId = 1L;
        String newName = "Belarus";
        String newCapital = "Minsk";
        Double newPopulation = 10000000.0;
        Double newAreaSquareKm = 200000.0;
        Double newGdp = 500000000.0;

        Country existingCountry = new Country();
        existingCountry.setId(countryId);
        existingCountry.setName("Russia");
        existingCountry.setCapital("Moscow");
        existingCountry.setPopulation(5000000.0);
        existingCountry.setAreaSquareKm(100000.0);
        existingCountry.setGdp(200000000.0);
        existingCountry.setNations(new HashSet<>());

        countries.add(existingCountry);

        Country updatedCountry = new Country();
        BeanUtils.copyProperties(existingCountry, updatedCountry);
        updatedCountry.setName(newName);
        updatedCountry.setCapital(newCapital);
        updatedCountry.setPopulation(newPopulation);
        updatedCountry.setAreaSquareKm(newAreaSquareKm);
        updatedCountry.setGdp(newGdp);
        updatedCountry.setNations(new HashSet<>());

        when(countryRepository.findCountryWithCitiesAndNationsById(countryId)).thenReturn(Optional.of(existingCountry));
        when(countryRepository.findCountryByName(newName)).thenReturn(Optional.empty());
        when(cacheService.containsKey("allCountries")).thenReturn(true);
        when(cacheService.containsKey("countryId_" + updatedCountry.getId())).thenReturn(true);

        Country result = countryService.updateCountry(countryId, newName, newCapital, newPopulation, newAreaSquareKm, newGdp);

        assertEquals(updatedCountry, result);
        verify(cacheService).put("countryId_" + updatedCountry.getId(), updatedCountry);
        verify(cacheService).remove("allCountries");
        assertTrue(countries.contains(updatedCountry));
    }

    @Test
    void updateCountryWhenNotFound() {
        Long countryId = 1L;
        when(countryRepository.findCountryWithCitiesAndNationsById(countryId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () ->
                countryService.updateCountry(countryId, "Belarus", "Minsk", 1000000.0, 100000.0, 1000000000.0));

        verify(countryRepository, times(1)).findCountryWithCitiesAndNationsById(countryId);
        verifyNoMoreInteractions(countryRepository);
        verifyNoInteractions(cacheService);
    }
    @Test
    void updateCountryWhenNameExists() {
        Long countryId = 1L;
        String newName = "Belarus";

        Country country = new Country();
        country.setId(countryId);
        country.setName("Russia");
        Country existingCountry = new Country();
        existingCountry.setId(2L);
        existingCountry.setName(newName);
        when(countryRepository.findCountryWithCitiesAndNationsById(countryId)).thenReturn(Optional.of(country));
        when(countryRepository.findCountryByName(newName)).thenReturn(Optional.of(existingCountry));

        assertThrows(ObjectExistedException.class, () ->
                countryService.updateCountry(countryId, newName, "Moscow", 1000000.0, 100000.0, 1000000000.0));

        verifyNoInteractions(cacheService);
    }

    @Test
    void deleteCountry() {
        Long countryId = 1L;
        Country country = new Country();
        country.setId(countryId);
        country.setCities(new HashSet<>());

        Nation nation = new Nation();
        nation.setId(1L);
        Set<Nation> nations = new HashSet<>();
        nations.add(nation);

        country.setNations(nations);
        when(countryRepository.findCountryWithCitiesAndNationsById(countryId)).thenReturn(Optional.of(country));
        when(cacheService.containsKey("allCountries")).thenReturn(true);
        when(cacheService.containsKey("countryId_" + country.getId())).thenReturn(true);
        when(cacheService.containsKey("allCitiesByCountryId_"
                + country.getId())).thenReturn(true);
        when(cacheService.containsKey("allCities")).thenReturn(true);
        when(cacheService.containsKey("allNationsByCountryId_" + country.getId())).thenReturn(true);
        when(cacheService.containsKey("allCountriesByNationId_" + nation.getId())).thenReturn(true);

        countryService.deleteCountry(countryId);

        verify(cacheService).remove("allCountries");
        verify(cacheService).remove("countryId_" + countryId);
        verify(cacheService).remove("allCitiesByCountryId_"
                + countryId);
        verify(cacheService).remove("allCities");
        verify(cacheService).remove("allNationsByCountryId_" + countryId);
        verify(cacheService).remove("allCountriesByNationId_" + nation.getId());
        verify(countryRepository).deleteById(countryId);
    }

    @Test
    void deleteCountryWhenNotFound() {
        Long countryId = 1L;
        when(countryRepository.findCountryWithCitiesById(countryId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> countryService.deleteCountry(countryId));
        verifyNoInteractions(cacheService);
    }

    @Test
    void deleteCountries() {
        Country countryOne = new Country();
        countryOne.setId(1L);
        countryOne.setCities(new HashSet<>());
        Country countryTwo = new Country();
        countryTwo.setId(2L);
        countryTwo.setCities(new HashSet<>());

        List<Country> countries = new ArrayList<>();
        countries.add(countryOne);
        countries.add(countryTwo);

        when(countryRepository.findAllWithCities()).thenReturn(countries);

        countryService.deleteCountries();

        verify(countryRepository).deleteAll();
        verify(cacheService).clear();
    }
}
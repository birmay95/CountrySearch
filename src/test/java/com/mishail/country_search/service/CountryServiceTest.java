package com.mishail.country_search.service;

import com.mishail.country_search.cache.CacheService;
import com.mishail.country_search.exception.ObjectExistedException;
import com.mishail.country_search.exception.ObjectNotFoundException;
import com.mishail.country_search.model.Country;
import com.mishail.country_search.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private CountryService countryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getCountriesWhenNotCached() {
        List<Country> countries = new ArrayList<>();
        when(cacheService.containsKey(eq("allCountries"))).thenReturn(false);
        when(countryRepository.findAllWithCitiesAndNations()).thenReturn(countries);

        List<Country> result = countryService.getCountries();

        assertEquals(countries, result);
        verify(cacheService).put(eq("allCountries"), eq(countries));
    }

    @Test
    public void getCountriesWhenCached() {
        List<Country> countries = new ArrayList<>();
        when(cacheService.containsKey(eq("allCountries"))).thenReturn(true);
        when(cacheService.get(eq("allCountries"))).thenReturn(countries);

        List<Country> result = countryService.getCountries();

        assertEquals(countries, result);
        verifyNoInteractions(countryRepository);
    }

    @Test
    public void getCountryByIdWhenNotCached() {
        Long countryId = 1L;
        Country country = new Country();
        country.setId(countryId);
        when(cacheService.containsKey(eq("countryId_" + countryId))).thenReturn(false);
        when(countryRepository.findCountryWithCitiesAndNationsById(countryId))
                .thenReturn(Optional.of(country));

        Country result = countryService.getCountryById(countryId);

        assertEquals(country, result);
        verify(cacheService).put(eq("countryId_" + countryId), eq(country));
    }
    @Test
    public void getCountryByIdWhenNotCachedAndNotExist() {
        Long countryId = 1L;
        when(cacheService.containsKey(eq("countryId_" + countryId))).thenReturn(false);
        when(countryRepository.findCountryWithCitiesAndNationsById(countryId))
                .thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> countryService.getCountryById(countryId));

        verify(countryRepository, times(1)).findCountryWithCitiesAndNationsById(countryId);
        verifyNoMoreInteractions(countryRepository);
        verify(cacheService, times(1)).containsKey(eq("countryId_" + countryId));
        verifyNoMoreInteractions(cacheService);
    }

    @Test
    public void getCountryByIdWhenCached() {
        Long countryId = 1L;
        Country country = new Country();
        country.setId(countryId);
        when(cacheService.containsKey(eq("countryId_" + countryId))).thenReturn(true);
        when(cacheService.get(eq("countryId_" + countryId))).thenReturn(country);

        Country result = countryService.getCountryById(countryId);

        assertEquals(country, result);
        verifyNoInteractions(countryRepository);
    }

    @Test
    public void addNewCountry() {
        Country country = new Country();
        List<Country> countries = new ArrayList<>();
        when(countryRepository.findCountryByName(anyString())).thenReturn(Optional.empty());
        when(cacheService.containsKey(eq("allCountries"))).thenReturn(true);
        when(cacheService.get(eq("allCountries"))).thenReturn(countries);

        Country result = countryService.addNewCountry(country);

        assertEquals(country, result);
        verify(cacheService).put(eq("countryId_" + country.getId()), eq(country));
        verify(cacheService).put(eq("allCountries"), eq(countries));
        verify(countryRepository).save(eq(country));
        assertTrue(countries.contains(country));
    }

    @Test
    public void addNewCountryWithException() {
        Country country = new Country();
        country.setName("Belarus");
        when(countryRepository.findCountryByName("Belarus")).thenReturn(Optional.of(country));

        assertThrows(ObjectExistedException.class, () -> countryService.addNewCountry(country));

        verify(countryRepository, times(1)).findCountryByName("Belarus");
        verifyNoMoreInteractions(countryRepository);
        verifyNoInteractions(cacheService);
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

        countries.add(existingCountry);

        Country updatedCountry = new Country();
        BeanUtils.copyProperties(existingCountry, updatedCountry);
        updatedCountry.setName(newName);
        updatedCountry.setCapital(newCapital);
        updatedCountry.setPopulation(newPopulation);
        updatedCountry.setAreaSquareKm(newAreaSquareKm);
        updatedCountry.setGdp(newGdp);

        when(countryRepository.findById(countryId)).thenReturn(Optional.of(existingCountry));
        when(countryRepository.findCountryByName(newName)).thenReturn(Optional.empty());
        when(cacheService.containsKey(eq("allCountries"))).thenReturn(true);
        when(cacheService.get(eq("allCountries"))).thenReturn(countries);


        Country result = countryService.updateCountry(countryId, newName, newCapital, newPopulation, newAreaSquareKm, newGdp);

        assertEquals(updatedCountry, result);
        verify(cacheService).put(eq("countryId_" + updatedCountry.getId()), eq(updatedCountry));
        verify(cacheService).put(eq("allCountries"), eq(countries));
        assertTrue(countries.contains(updatedCountry));
    }

    @Test
    public void updateCountryWhenNotFound() {
        Long countryId = 1L;
        when(countryRepository.findById(countryId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () ->
                countryService.updateCountry(countryId, "Belarus", "Minsk", 1000000.0, 100000.0, 1000000000.0));

        verify(countryRepository, times(1)).findById(countryId);
        verifyNoMoreInteractions(countryRepository);
        verifyNoInteractions(cacheService);
    }
    @Test
    public void updateCountryWhenNameExists() {
        Long countryId = 1L;
        String newName = "Belarus";

        Country country = new Country();
        country.setId(countryId);
        country.setName("Russia");
        Country existingCountry = new Country();
        existingCountry.setId(2L);
        existingCountry.setName(newName);
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(country));
        when(countryRepository.findCountryByName(newName)).thenReturn(Optional.of(existingCountry));

        assertThrows(ObjectExistedException.class, () ->
                countryService.updateCountry(countryId, newName, "Moscow", 1000000.0, 100000.0, 1000000000.0));

        verifyNoInteractions(cacheService);
    }

    @Test
    public void deleteCountry() {
        Long countryId = 1L;
        Country country = new Country();
        country.setId(countryId);
        country.setCities(new HashSet<>());
        List<Country> countries = new ArrayList<>();
        countries.add(country);
        when(countryRepository.findCountryWithCitiesById(countryId)).thenReturn(Optional.of(country));
        when(cacheService.containsKey(eq("allCountries"))).thenReturn(true);
        when(cacheService.get(eq("allCountries"))).thenReturn(countries);

        countryService.deleteCountry(countryId);

        verify(cacheService).remove(eq("countryId_" + countryId));
        verify(countryRepository).deleteById(eq(countryId));
        verify(cacheService).put(eq("allCountries"), eq(countries));
        assertFalse(countries.contains(country));
    }

    @Test
    public void deleteCountryWhenNotFound() {
        Long countryId = 1L;
        when(countryRepository.findCountryWithCitiesById(countryId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> countryService.deleteCountry(countryId));
        verifyNoInteractions(cacheService);
    }
}
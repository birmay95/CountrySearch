package com.mishail.country_search.service;

import com.mishail.country_search.cache.CacheService;
import com.mishail.country_search.exception.ObjectExistedException;
import com.mishail.country_search.exception.ObjectNotFoundException;
import com.mishail.country_search.model.City;
import com.mishail.country_search.model.Country;
import com.mishail.country_search.repository.CityRepository;
import com.mishail.country_search.repository.CountryRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private CityService cityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getCitiesWhenNotCached() {
        List<City> cities = new ArrayList<>();
        when(cacheService.containsKey(eq("allCities"))).thenReturn(false);
        when(cityRepository.findAll()).thenReturn(cities);

        List<City> result = cityService.getCities();

        assertEquals(cities, result);
        verify(cacheService).put(eq("allCities"), eq(cities));
    }

    @Test
    public void getCitiesWhenCached() {
        List<City> cities = new ArrayList<>();
        when(cacheService.containsKey(eq("allCities"))).thenReturn(true);
        when(cacheService.get(eq("allCities"))).thenReturn(cities);

        List<City> result = cityService.getCities();

        assertEquals(cities, result);
        verifyNoInteractions(cityRepository);
    }

    @Test
    public void getCitiesByCountryByIdWhenNotCached() {
        Long countryId = 1L;
        Set<City> cities = new HashSet<>();
        Country country = new Country();
        country.setId(countryId);
        country.setCities(cities);
        when(cacheService.containsKey(eq("allCitiesByCountryId_" + countryId))).thenReturn(false);
        when(countryRepository.findCountryWithCitiesById(countryId))
                .thenReturn(Optional.of(country));

        Set<City> result = cityService.getCitiesByCountryId(countryId);

        assertEquals(cities, result);
        verify(cacheService).put(eq("allCitiesByCountryId_" + countryId), eq(cities));
    }

    @Test
    public void getCitiesByCountryByIdWhenNotCachedAndNotExist() {
        Long countryId = 1L;
        when(cacheService.containsKey(eq("allCitiesByCountryId_" + countryId))).thenReturn(false);
        when(countryRepository.findCountryWithCitiesById(countryId))
                .thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> cityService.getCitiesByCountryId(countryId));

        verify(countryRepository, times(1)).findCountryWithCitiesById(countryId);
        verifyNoMoreInteractions(countryRepository);
        verify(cacheService, times(1)).containsKey(eq("allCitiesByCountryId_" + countryId));
        verifyNoMoreInteractions(cacheService);
    }

    @Test
    public void getCitiesByCountryByIdWhenCached() {
        Long countryId = 1L;
        Set<City> cities = new HashSet<>();
        when(cacheService.containsKey(eq("allCitiesByCountryId_" + countryId))).thenReturn(true);
        when(cacheService.get(eq("allCitiesByCountryId_" + countryId))).thenReturn(cities);

        Set<City> result = cityService.getCitiesByCountryId(countryId);

        assertEquals(cities, result);
        verifyNoInteractions(countryRepository);
    }

    @Test
    void addNewCityByCountryByIdWhenCityDoesNotExist() {
        Long countryId = 1L;
        City cityRequest = new City();
        cityRequest.setName("Minsk");

        Country country = new Country();
        country.setId(countryId);
        country.setCities(new HashSet<>());

        when(countryRepository.findCountryWithCitiesById(countryId)).thenReturn(Optional.of(country));
        when(cacheService.containsKey(anyString())).thenReturn(false);


        City result = cityService.addNewCityByCountryId(countryId, cityRequest);

        assertEquals(cityRequest, result);
        assertTrue(country.getCities().contains(cityRequest));
        verify(cityRepository).save(eq(cityRequest));
        verify(countryRepository).save(eq(country));
    }

    @Test
    void addNewCityByCountryByIdWhenCityExists() {
        Long countryId = 1L;
        City cityRequest = new City();
        cityRequest.setName("Minsk");

        Country country = new Country();
        country.setId(countryId);
        Set<City> cities = new HashSet<>();
        cities.add(cityRequest);
        country.setCities(cities);

        when(countryRepository.findCountryWithCitiesById(countryId)).thenReturn(Optional.of(country));

        assertThrows(ObjectExistedException.class, () -> cityService.addNewCityByCountryId(countryId, cityRequest));
        verifyNoInteractions(cityRepository);
        verifyNoInteractions(cacheService);
        verify(countryRepository, times(1)).findCountryWithCitiesById(eq(countryId));
        verifyNoMoreInteractions(countryRepository);
    }

    @Test
    void addNewCityByCountryByIdWhenCountryNotFound() {
        Long countryId = 1L;
        City cityRequest = new City();
        cityRequest.setName("Minsk");

        when(countryRepository.findCountryWithCitiesAndNationsById(countryId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> cityService.addNewCityByCountryId(countryId, cityRequest));
        verifyNoMoreInteractions(cityRepository);
        verifyNoInteractions(cacheService);
        verify(countryRepository, times(1)).findCountryWithCitiesById(eq(countryId));
        verifyNoMoreInteractions(countryRepository);
    }

    @Test
    public void updateCity() {
        Long cityId = 1L;
        String name = "Grodno";
        Double population = 1000000.0;
        Double areaSquareKm = 5000.0;

        City cityBeforeChanges = new City();
        cityBeforeChanges.setId(cityId);
        cityBeforeChanges.setName("Minsk");
        cityBeforeChanges.setPopulation(100000.0);
        cityBeforeChanges.setAreaSquareKm(20000.0);

        Country country = new Country();
        country.setId(1L);
        Set<City> cities = new HashSet<>();
        cities.add(cityBeforeChanges);
        country.setCities(cities);

        City updatedCity = new City();
        BeanUtils.copyProperties(cityBeforeChanges, updatedCity);
        updatedCity.setName(name);
        updatedCity.setPopulation(population);
        updatedCity.setAreaSquareKm(areaSquareKm);

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(cityBeforeChanges));
        when(countryRepository.findCountryWithCitiesByCityId(cityId)).thenReturn(Optional.of(country));
        when(cacheService.containsKey(anyString())).thenReturn(false);


        City result = cityService.updateCity(cityId, name, population, areaSquareKm);

        assertEquals(updatedCity, result);
    }

    @Test
    public void updateCityWhenDoesNotExist() {
        Long cityId = 1L;
        String name = "Minsk";
        Double population = 1000000.0;
        Double areaSquareKm = 500.0;

        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> cityService.updateCity(cityId, name, population, areaSquareKm));
        verifyNoInteractions(countryRepository);
        verifyNoInteractions(cacheService);
        verify(cityRepository, times(1)).findById(eq(cityId));
        verifyNoMoreInteractions(cityRepository);
    }

    @Test
    public void updateCityWhenCityExistsInCountry() {
        Long cityId = 1L;
        String name = "Grodno";
        Double population = 1000000.0;
        Double areaSquareKm = 500.0;

        City city = new City();
        city.setId(cityId);
        city.setName(name);

        City cityTwo = new City();
        cityTwo.setId(2L);
        cityTwo.setName("Minsk");

        Country country = new Country();
        country.setId(1L);
        Set<City> cities = new HashSet<>();
        cities.add(city);
        cities.add(cityTwo);
        country.setCities(cities);

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));
        when(countryRepository.findCountryWithCitiesByCityId(cityId)).thenReturn(Optional.of(country));

        assertThrows(ObjectExistedException.class, () -> cityService.updateCity(cityId, "Minsk", population, areaSquareKm));
        verifyNoInteractions(cacheService);
        verify(cityRepository, times(1)).findById(eq(cityId));
        verifyNoMoreInteractions(cityRepository);
        verify(countryRepository, times(1)).findCountryWithCitiesByCityId(eq(cityId));
        verifyNoMoreInteractions(countryRepository);
    }

    @Test
    public void deleteCityByIdFromCountryByCountryId() {
        Long countryId = 1L;
        Long cityId = 1L;

        City city = new City();
        city.setId(cityId);
        city.setName("Minsk");

        Country country = new Country();
        country.setId(countryId);
        Set<City> cities = new HashSet<>();
        cities.add(city);
        country.setCities(cities);

        when(countryRepository.findCountryWithCitiesById(countryId)).thenReturn(Optional.of(country));
        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));
        when(cacheService.containsKey(anyString())).thenReturn(false);


        cityService.deleteCityByIdFromCountryByCountryId(countryId, cityId);

        verify(cityRepository).deleteById(eq(cityId));
        verify(countryRepository).save(eq(country));
        assertFalse(country.getCities().contains(city));
    }

    @Test
    public void deleteCityByIdFromCountryByCountryIdWhenCountryDoesNotExist() {
        Long countryId = 1L;
        Long cityId = 1L;
        when(countryRepository.findCountryWithCitiesById(countryId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> cityService.deleteCityByIdFromCountryByCountryId(countryId, cityId));
        verifyNoInteractions(cityRepository);
        verifyNoInteractions(cacheService);
        verify(countryRepository, times(1)).findCountryWithCitiesById(countryId);
        verifyNoMoreInteractions(countryRepository);
    }


    @Test
    public void deleteCityByIdFromCountryByCountryIdWhenCityDoesNotExist() {
        Long countryId = 1L;
        Long cityId = 1L;
        Country country = new Country();
        country.setId(countryId);

        when(countryRepository.findCountryWithCitiesById(countryId)).thenReturn(Optional.of(country));
        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());


        assertThrows(ObjectNotFoundException.class, () -> cityService.deleteCityByIdFromCountryByCountryId(countryId, cityId));
        verifyNoInteractions(cacheService);
        verify(countryRepository, times(1)).findCountryWithCitiesById(eq(countryId));
        verifyNoMoreInteractions(countryRepository);
        verify(cityRepository, times(1)).findById(eq(cityId));
        verifyNoMoreInteractions(cityRepository);
    }

    @Test
    public void deleteCitiesByCountryId() {
        Long countryId = 1L;
        Set<City> cities = new HashSet<>();
        City city1 = new City();
        city1.setId(1L);
        City city2 = new City();
        city2.setId(2L);
        cities.add(city1);
        cities.add(city2);

        Country country = new Country();
        country.setId(countryId);
        country.setCities(cities);

        when(countryRepository.findCountryWithCitiesById(countryId)).thenReturn(Optional.of(country));
        when(cacheService.containsKey(anyString())).thenReturn(false);


        cityService.deleteCitiesByCountryId(countryId);

        verify(cityRepository).deleteById(eq(city1.getId()));
        verify(cityRepository).deleteById(eq(city2.getId()));
        verify(countryRepository).save(eq(country));
        assertTrue(country.getCities().isEmpty());
    }

    @Test
    public void testDeleteCitiesByCountryIdWhenCountryDoesNotExist() {
        Long countryId = 1L;
        when(countryRepository.findCountryWithCitiesById(countryId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> cityService.deleteCitiesByCountryId(countryId));
        verifyNoInteractions(cityRepository);
        verifyNoInteractions(cacheService);
        verify(countryRepository, times(1)).findCountryWithCitiesById(eq(countryId));
        verifyNoMoreInteractions(countryRepository);
    }
}
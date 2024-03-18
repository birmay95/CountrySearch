package com.mishail.country_search.repository;

import com.mishail.country_search.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    @Query("SELECT s FROM Country s WHERE s.name = ?1")
    Optional<Country> findCountryByName(String name);

    @Query("SELECT DISTINCT c FROM Country c LEFT JOIN FETCH c.cities city WHERE :cityId IN (SELECT ct.id FROM Country c2 JOIN c2.cities ct WHERE c2 = c)")
    Optional<Country> findCountryWithCitiesByCityId(Long cityId);

    @Query("SELECT DISTINCT c FROM Country c LEFT JOIN FETCH c.nations LEFT JOIN FETCH c.cities")
    List<Country> findAllWithCitiesAndNations();

    @Query("SELECT DISTINCT c FROM Country c LEFT JOIN FETCH c.cities")
    List<Country> findAllWithCities();

    @Query("SELECT DISTINCT c FROM Country c LEFT JOIN FETCH c.nations LEFT JOIN FETCH c.cities WHERE c.id = :id")
    Optional<Country> findCountryWithCitiesAndNationsById(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Country c LEFT JOIN FETCH c.nations WHERE c.id = :id")
    Optional<Country> findCountryWithNationsById(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Country c LEFT JOIN FETCH c.cities WHERE c.id = :id")
    Optional<Country> findCountryWithCitiesById(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Country c LEFT JOIN FETCH c.nations n WHERE n.id = :nationId")
    List<Country> findCountriesWithNationsByNationByNationId(@Param("nationId") Long nationId);
}
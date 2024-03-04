package com.mishail.country_search.repository;

import com.mishail.country_search.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @Query("SELECT s FROM City s WHERE s.name = ?1")
    Optional<City> findCityByName(String name);
}

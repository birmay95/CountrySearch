package com.mishail.country_search.repository;

import com.mishail.country_search.model.Nation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NationRepository extends JpaRepository<Nation, Long> {
    Nation findNationsByName(String name);
}

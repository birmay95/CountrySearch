package com.mishail.country_search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "country")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "capital")
    private String capital;

    @Column(name = "population")
    private Double population;

    @Column(name = "area")
    private Double areaSquareKm;

    @Column(name = "gdp")
    private Double gdp;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "country_id")
    private Set<City> cities;


    @ManyToMany
    @JsonIgnoreProperties("countries")
    @JoinTable(name = "country_nations",
            joinColumns = {@JoinColumn(name = "country_id")},
            inverseJoinColumns = {@JoinColumn(name = "nation_id")})
    private Set<Nation> nations;


}

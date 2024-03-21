package com.mishail.country_search.model;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Column(name = "name")
    @Schema(example = "Belarus")
    private String name;

    @Column(name = "capital")
    @Schema(example = "Minsk")
    private String capital;

    @Column(name = "population")
    @Schema(example = "1.431E8")
    private Double population;

    @Column(name = "area")
    @Schema(example = "1.71E7")
    private Double areaSquareKm;

    @Column(name = "gdp")
    @Schema(example = "1.779E12")
    private Double gdp;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "country_id")
    private Set<City> cities;


    @ManyToMany
    @JoinTable(name = "country_nations",
            joinColumns = {@JoinColumn(name = "country_id")},
            inverseJoinColumns = {@JoinColumn(name = "nation_id")})
    private Set<Nation> nations;
}

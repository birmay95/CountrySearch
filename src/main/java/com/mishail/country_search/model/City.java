package com.mishail.country_search.model;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "city")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Column(name = "name")
    @Schema(example = "Minsk")
    private String name;

    @Column(name = "population")
    @Schema(example = "2000000")
    private Double population;

    @Column(name = "area")
    @Schema(example = "50000")
    private Double areaSquareKm;
}

package com.mishail.country_search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableAspectJAutoProxy
public class CountrySearchApplication {

    public static void main(final String[] args) {
        SpringApplication.run(CountrySearchApplication.class, args);
    }

}

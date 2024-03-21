package com.mishail.country_search.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Country Search",
                description = "You can find information about countries"
                        + " and nations and cities,"
                        + " that are connected with this countries",
                version = "1.0.0",
                contact = @Contact(
                        name = "Borovensky Mishail",
                        url = "https://t.me/mishail_b"
                )
        )
)
public class SwaggerConfig {
}

package com.uatch.catalog.infrastructure.configuration;

import com.uatch.catalog.application.port.BrandRepository;
import com.uatch.catalog.application.usecase.ListBrands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class CatalogConfiguration {

    @Bean
    public ListBrands listBrands(BrandRepository brandRepository) {
        return new ListBrands(brandRepository);
    }
}
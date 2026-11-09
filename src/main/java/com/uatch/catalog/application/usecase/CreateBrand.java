package com.uatch.catalog.application.usecase;

import java.util.Objects;
import java.util.UUID;

import com.uatch.catalog.application.port.BrandRepository;
import com.uatch.catalog.domain.Brand;

public final class CreateBrand {

    private final BrandRepository brandRepository;

    public CreateBrand(BrandRepository brandRepository) {
        this.brandRepository = Objects.requireNonNull(
                brandRepository, "Brand repository must not be null");
    }

    public Brand execute(String name, String slug) {
        Brand brand = new Brand(UUID.randomUUID(), name, slug);

        if (brandRepository.existsBySlug(brand.getSlug())) {
            throw new IllegalArgumentException(
                    "Brand slug already exists");
        }

        brandRepository.save(brand);

        return brand;
    }
}
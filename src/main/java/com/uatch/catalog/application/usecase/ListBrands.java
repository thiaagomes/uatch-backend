package com.uatch.catalog.application.usecase;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.uatch.catalog.application.port.BrandRepository;
import com.uatch.catalog.domain.Brand;

public final class ListBrands {

    private static final Comparator<Brand> BY_NAME =
            Comparator.comparing(
                    Brand::getName,
                    String.CASE_INSENSITIVE_ORDER
            ).thenComparing(Brand::getSlug);

    private final BrandRepository brandRepository;

    public ListBrands(BrandRepository brandRepository) {
        this.brandRepository = Objects.requireNonNull(
                brandRepository, "Brand repository must not be null");
    }

    public List<Brand> execute() {
        return brandRepository.findAll()
                .stream()
                .sorted(BY_NAME)
                .toList();
    }
}
package com.uatch.catalog.presentation.dto;

import java.util.UUID;

import com.uatch.catalog.domain.Brand;

public record BrandResponse(
        UUID id,
        String name,
        String slug
) {

    public static BrandResponse from(Brand brand) {
        return new BrandResponse(
                brand.getId(),
                brand.getName(),
                brand.getSlug()
        );
    }
}
package com.uatch.catalog.application.port;

import java.util.UUID;

import com.uatch.catalog.domain.Brand;

public interface BrandRepository {

    boolean existsById(UUID id);

    boolean existsBySlug(String slug);

    void save(Brand brand);
}
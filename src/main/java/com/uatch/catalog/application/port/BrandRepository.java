package com.uatch.catalog.application.port;

import com.uatch.catalog.domain.Brand;

public interface BrandRepository {

    boolean existsBySlug(String slug);

    void save(Brand brand);
}
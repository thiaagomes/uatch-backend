package com.uatch.catalog.application.port;

import java.util.List;
import java.util.UUID;

import com.uatch.catalog.domain.Brand;

public interface BrandRepository {

    boolean existsById(UUID id);

    boolean existsBySlug(String slug);

    List<Brand> findAll();

    void save(Brand brand);
}
package com.uatch.catalog.infrastructure.persistence.repository;

import java.util.UUID;

import com.uatch.catalog.infrastructure.persistence.entity.BrandJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataBrandRepository
        extends JpaRepository<BrandJpaEntity, UUID> {

    boolean existsBySlug(String slug);
}
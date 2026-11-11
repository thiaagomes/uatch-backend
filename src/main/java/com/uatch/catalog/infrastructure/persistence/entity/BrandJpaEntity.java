package com.uatch.catalog.infrastructure.persistence.entity;

import java.util.UUID;

import com.uatch.catalog.domain.Brand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "brands")
public class BrandJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, columnDefinition = "text")
    private String name;

    @Column(
            name = "slug",
            nullable = false,
            columnDefinition = "text"
    )
    private String slug;

    protected BrandJpaEntity() {
    }

    private BrandJpaEntity(UUID id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }

    public static BrandJpaEntity fromDomain(Brand brand) {
        return new BrandJpaEntity(
                brand.getId(),
                brand.getName(),
                brand.getSlug()
        );
    }

    public Brand toDomain() {
        return new Brand(id, name, slug);
    }
}
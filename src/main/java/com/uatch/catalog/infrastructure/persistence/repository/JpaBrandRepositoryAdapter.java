package com.uatch.catalog.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import com.uatch.catalog.application.port.BrandRepository;
import com.uatch.catalog.domain.Brand;
import com.uatch.catalog.infrastructure.persistence.entity.BrandJpaEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class JpaBrandRepositoryAdapter implements BrandRepository {

    private final SpringDataBrandRepository repository;

    public JpaBrandRepositoryAdapter(SpringDataBrandRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return repository.existsBySlug(slug);
    }

    @Override
    public List<Brand> findAll() {
        return repository.findAll()
                .stream()
                .map(BrandJpaEntity::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void save(Brand brand) {
        repository.save(BrandJpaEntity.fromDomain(brand));
    }
}
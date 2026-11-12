package com.uatch.catalog.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import com.uatch.catalog.application.exception.BrandSlugAlreadyExistsException;
import com.uatch.catalog.application.port.BrandRepository;
import com.uatch.catalog.domain.Brand;
import com.uatch.catalog.infrastructure.persistence.entity.BrandJpaEntity;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class JpaBrandRepositoryAdapter implements BrandRepository {

    private static final String UNIQUE_SLUG_CONSTRAINT = "uk_brands_slug";
    private static final String UNIQUE_VIOLATION_SQL_STATE = "23505";

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
        try {
            repository.saveAndFlush(BrandJpaEntity.fromDomain(brand));
        } catch (DataIntegrityViolationException exception) {
            if (isDuplicateSlug(exception)) {
                throw new BrandSlugAlreadyExistsException(exception);
            }

            throw exception;
        }
    }

    private boolean isDuplicateSlug(Throwable exception) {
        Throwable cause = exception;

        while (cause != null) {
            if (cause instanceof ConstraintViolationException violation
                    && UNIQUE_SLUG_CONSTRAINT.equals(
                            violation.getConstraintName())
                    && UNIQUE_VIOLATION_SQL_STATE.equals(
                            violation.getSQLState())) {
                return true;
            }

            cause = cause.getCause();
        }

        return false;
    }
}
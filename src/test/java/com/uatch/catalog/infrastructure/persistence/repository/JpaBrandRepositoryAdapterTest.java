package com.uatch.catalog.infrastructure.persistence.repository;

import java.util.UUID;

import com.uatch.catalog.application.port.BrandRepository;
import com.uatch.catalog.domain.Brand;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JpaBrandRepositoryAdapterTest {

    @Autowired
    private BrandRepository repository;

    @Autowired
    private SpringDataBrandRepository springDataRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveAndReadBrandFromDatabase() {
        UUID id = UUID.randomUUID();
        String slug = "test-" + id;
        Brand brand = new Brand(id, "Test Brand", slug);

        repository.save(brand);

        entityManager.flush();
        entityManager.clear();

        Brand stored = repository.findAll()
                .stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow();

        assertAll(
                () -> assertEquals(id, stored.getId()),
                () -> assertEquals("Test Brand", stored.getName()),
                () -> assertEquals(slug, stored.getSlug()),
                () -> assertTrue(repository.existsById(id)),
                () -> assertTrue(repository.existsBySlug(slug))
        );
    }

    @Test
    void shouldRejectDuplicateSlugInDatabase() {
        String slug = "test-" + UUID.randomUUID();

        repository.save(
                new Brand(UUID.randomUUID(), "First Brand", slug));
        springDataRepository.flush();

        repository.save(
                new Brand(UUID.randomUUID(), "Second Brand", slug));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> springDataRepository.flush()
        );
    }
}
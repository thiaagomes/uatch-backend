package com.uatch.catalog.infrastructure.persistence.repository;

import java.util.UUID;

import com.uatch.catalog.application.exception.BrandSlugAlreadyExistsException;
import com.uatch.catalog.application.port.BrandRepository;
import com.uatch.catalog.domain.Brand;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    private EntityManager entityManager;

    @Test
    void shouldSaveAndReadBrandFromDatabase() {
        UUID id = UUID.randomUUID();
        String slug = "test-" + id;
        Brand brand = new Brand(id, "Test Brand", slug);

        repository.save(brand);

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
    void shouldTranslateDuplicateSlugFromDatabase() {
        String slug = "test-" + UUID.randomUUID();

        repository.save(
                new Brand(UUID.randomUUID(), "First Brand", slug));

        Brand duplicate = new Brand(
                UUID.randomUUID(), "Second Brand", slug);

        BrandSlugAlreadyExistsException exception = assertThrows(
                BrandSlugAlreadyExistsException.class,
                () -> repository.save(duplicate)
        );

        assertAll(
                () -> assertEquals(
                        "Brand slug already exists",
                        exception.getMessage()),
                () -> assertNotNull(exception.getCause())
        );
    }
}
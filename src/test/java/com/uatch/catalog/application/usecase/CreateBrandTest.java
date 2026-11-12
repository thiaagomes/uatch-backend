package com.uatch.catalog.application.usecase;

import com.uatch.catalog.application.exception.BrandSlugAlreadyExistsException;
import com.uatch.catalog.application.port.BrandRepository;
import com.uatch.catalog.domain.Brand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateBrandTest {

    private BrandRepository repository;
    private CreateBrand createBrand;

    @BeforeEach
    void setUp() {
        repository = mock(BrandRepository.class);
        createBrand = new CreateBrand(repository);
    }

    @Test
    void shouldCreateAndSaveBrand() {
        when(repository.existsBySlug("tag-heuer")).thenReturn(false);

        Brand brand = createBrand.execute("  TAG Heuer  ", "tag-heuer");

        assertAll(
                () -> assertNotNull(brand.getId()),
                () -> assertEquals("TAG Heuer", brand.getName()),
                () -> assertEquals("tag-heuer", brand.getSlug())
        );

        verify(repository).save(brand);
    }

    @Test
    void shouldRejectDuplicateSlugWithoutSaving() {
        when(repository.existsBySlug("rolex")).thenReturn(true);

        assertThrows(
                BrandSlugAlreadyExistsException.class,
                () -> createBrand.execute("Rolex", "rolex")
        );

        verify(repository, never()).save(any(Brand.class));
    }

    @Test
    void shouldRejectInvalidNameBeforeAccessingRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> createBrand.execute(" ", "rolex")
        );

        verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectInvalidSlugBeforeAccessingRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> createBrand.execute("Rolex", "Rolex")
        );

        verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectNullRepository() {
        assertThrows(
                NullPointerException.class,
                () -> new CreateBrand(null)
        );
    }
}
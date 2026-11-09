package com.uatch.catalog.application.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.uatch.catalog.application.port.BrandRepository;
import com.uatch.catalog.domain.Brand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListBrandsTest {

    private BrandRepository repository;
    private ListBrands listBrands;

    @BeforeEach
    void setUp() {
        repository = mock(BrandRepository.class);
        listBrands = new ListBrands(repository);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoBrands() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(listBrands.execute().isEmpty());

        verify(repository).findAll();
    }

    @Test
    void shouldSortByNameIgnoringCase() {
        Brand rolex = new Brand(UUID.randomUUID(), "Rolex", "rolex");
        Brand casio = new Brand(UUID.randomUUID(), "casio", "casio");
        Brand omega = new Brand(UUID.randomUUID(), "Omega", "omega");
        when(repository.findAll()).thenReturn(List.of(rolex, casio, omega));

        List<Brand> result = listBrands.execute();

        assertEquals(List.of(casio, omega, rolex), result);
    }

    @Test
    void shouldBreakNameTiesBySlug() {
        Brand second = new Brand(UUID.randomUUID(), "Brand", "brand-b");
        Brand first = new Brand(UUID.randomUUID(), "brand", "brand-a");
        when(repository.findAll()).thenReturn(List.of(second, first));

        assertEquals(List.of(first, second), listBrands.execute());
    }

    @Test
    void shouldPreserveRepositoryListOrder() {
        Brand rolex = new Brand(UUID.randomUUID(), "Rolex", "rolex");
        Brand casio = new Brand(UUID.randomUUID(), "Casio", "casio");
        List<Brand> source = new ArrayList<>(List.of(rolex, casio));
        when(repository.findAll()).thenReturn(source);

        List<Brand> result = listBrands.execute();

        assertAll(
                () -> assertEquals(List.of(casio, rolex), result),
                () -> assertEquals(List.of(rolex, casio), source)
        );
    }

    @Test
    void shouldReturnUnmodifiableList() {
        Brand rolex = new Brand(UUID.randomUUID(), "Rolex", "rolex");
        when(repository.findAll()).thenReturn(List.of(rolex));

        List<Brand> result = listBrands.execute();

        assertThrows(UnsupportedOperationException.class, result::clear);
    }

    @Test
    void shouldRejectNullRepository() {
        assertThrows(
                NullPointerException.class,
                () -> new ListBrands(null)
        );
    }
}
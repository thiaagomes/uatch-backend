package com.uatch.catalog.domain;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BrandTest {

    @Test
    void shouldCreateBrandAndTrimName() {
        UUID id = UUID.randomUUID();

        Brand brand = new Brand(id, "  TAG Heuer  ", "tag-heuer");

        assertAll(
                () -> assertEquals(id, brand.getId()),
                () -> assertEquals("TAG Heuer", brand.getName()),
                () -> assertEquals("tag-heuer", brand.getSlug())
        );
    }

    @Test
    void shouldRejectNullId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Brand(null, "Rolex", "rolex")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldRejectBlankName(String name) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Brand(UUID.randomUUID(), name, "rolex")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"rolex", "tag-heuer", "brand-123"})
    void shouldAcceptValidSlug(String slug) {
        Brand brand = new Brand(UUID.randomUUID(), "Brand", slug);

        assertEquals(slug, brand.getSlug());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ", "Rolex", "tag heuer", "-rolex",
            "rolex-", "tag--heuer", "relógio", "rolex/watch"
    })
    void shouldRejectInvalidSlug(String slug) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Brand(UUID.randomUUID(), "Brand", slug)
        );
    }
}
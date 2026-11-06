package com.uatch.catalog.domain;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class WatchModelTest {

    @Test
    void shouldCreateModelAndTrimNameAndReference() {
        UUID id = UUID.randomUUID();
        UUID brandId = UUID.randomUUID();

        WatchModel model = new WatchModel(
                id, brandId, "  Submariner  ", "  126610LN  ");

        assertAll(
                () -> assertEquals(id, model.getId()),
                () -> assertEquals(brandId, model.getBrandId()),
                () -> assertEquals("Submariner", model.getModelName()),
                () -> assertEquals("126610LN", model.getReference())
        );
    }

    @Test
    void shouldRejectNullId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new WatchModel(
                        null, UUID.randomUUID(), "Submariner", "126610LN")
        );
    }

    @Test
    void shouldRejectNullBrandId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new WatchModel(
                        UUID.randomUUID(), null, "Submariner", "126610LN")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldRejectBlankModelName(String name) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new WatchModel(
                        UUID.randomUUID(), UUID.randomUUID(), name, "126610LN")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldRejectBlankReference(String reference) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new WatchModel(
                        UUID.randomUUID(), UUID.randomUUID(),
                        "Submariner", reference)
        );
    }
}
package com.uatch.catalog.application.usecase;

import java.util.UUID;

import com.uatch.catalog.application.port.BrandRepository;
import com.uatch.catalog.application.port.WatchModelRepository;
import com.uatch.catalog.domain.WatchModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateWatchModelTest {

    private BrandRepository brandRepository;
    private WatchModelRepository watchModelRepository;
    private CreateWatchModel createWatchModel;

    @BeforeEach
    void setUp() {
        brandRepository = mock(BrandRepository.class);
        watchModelRepository = mock(WatchModelRepository.class);

        createWatchModel = new CreateWatchModel(
                brandRepository, watchModelRepository);
    }

    @Test
    void shouldCreateModelUsingTrimmedReferenceForDuplicateCheck() {
        UUID brandId = UUID.randomUUID();
        when(brandRepository.existsById(brandId)).thenReturn(true);

        WatchModel model = createWatchModel.execute(
                brandId, "  Submariner  ", "  126610LN  ");

        assertAll(
                () -> assertNotNull(model.getId()),
                () -> assertEquals(brandId, model.getBrandId()),
                () -> assertEquals("Submariner", model.getModelName()),
                () -> assertEquals("126610LN", model.getReference())
        );

        verify(watchModelRepository)
                .existsByBrandIdAndReference(brandId, "126610LN");
        verify(watchModelRepository).save(model);
    }

    @Test
    void shouldRejectMissingBrandWithoutAccessingModelRepository() {
        UUID brandId = UUID.randomUUID();
        when(brandRepository.existsById(brandId)).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> createWatchModel.execute(
                        brandId, "Submariner", "126610LN")
        );

        verifyNoInteractions(watchModelRepository);
    }

    @Test
    void shouldRejectDuplicateReferenceWithoutSaving() {
        UUID brandId = UUID.randomUUID();
        when(brandRepository.existsById(brandId)).thenReturn(true);
        when(watchModelRepository.existsByBrandIdAndReference(
                brandId, "126610LN")).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> createWatchModel.execute(
                        brandId, "Submariner", "126610LN")
        );

        verify(watchModelRepository, never()).save(any(WatchModel.class));
    }

    @Test
    void shouldRejectNullBrandIdBeforeAccessingRepositories() {
        assertThrows(
                IllegalArgumentException.class,
                () -> createWatchModel.execute(
                        null, "Submariner", "126610LN")
        );

        verifyNoInteractions(brandRepository, watchModelRepository);
    }

    @Test
    void shouldRejectBlankNameBeforeAccessingRepositories() {
        assertThrows(
                IllegalArgumentException.class,
                () -> createWatchModel.execute(
                        UUID.randomUUID(), " ", "126610LN")
        );

        verifyNoInteractions(brandRepository, watchModelRepository);
    }

    @Test
    void shouldRejectBlankReferenceBeforeAccessingRepositories() {
        assertThrows(
                IllegalArgumentException.class,
                () -> createWatchModel.execute(
                        UUID.randomUUID(), "Submariner", " ")
        );

        verifyNoInteractions(brandRepository, watchModelRepository);
    }

    @Test
    void shouldRejectNullBrandRepository() {
        assertThrows(
                NullPointerException.class,
                () -> new CreateWatchModel(null, watchModelRepository)
        );
    }

    @Test
    void shouldRejectNullWatchModelRepository() {
        assertThrows(
                NullPointerException.class,
                () -> new CreateWatchModel(brandRepository, null)
        );
    }
}
package com.uatch.catalog.application.usecase;

import java.util.Objects;
import java.util.UUID;

import com.uatch.catalog.application.port.BrandRepository;
import com.uatch.catalog.application.port.WatchModelRepository;
import com.uatch.catalog.domain.WatchModel;

public final class CreateWatchModel {

    private final BrandRepository brandRepository;
    private final WatchModelRepository watchModelRepository;

    public CreateWatchModel(
            BrandRepository brandRepository,
            WatchModelRepository watchModelRepository
    ) {
        this.brandRepository = Objects.requireNonNull(
                brandRepository, "Brand repository must not be null");

        this.watchModelRepository = Objects.requireNonNull(
                watchModelRepository,
                "Watch model repository must not be null");
    }

    public WatchModel execute(
            UUID brandId,
            String modelName,
            String reference
    ) {
        WatchModel model = new WatchModel(
                UUID.randomUUID(), brandId, modelName, reference);

        if (!brandRepository.existsById(model.getBrandId())) {
            throw new IllegalArgumentException(
                    "Brand does not exist");
        }

        if (watchModelRepository.existsByBrandIdAndReference(
                model.getBrandId(), model.getReference())) {
            throw new IllegalArgumentException(
                    "Watch model reference already exists for this brand");
        }

        watchModelRepository.save(model);

        return model;
    }
}
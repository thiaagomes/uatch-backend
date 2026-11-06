package com.uatch.catalog.domain;

import java.util.UUID;

public final class WatchModel {

    private final UUID id;
    private final UUID brandId;
    private final String modelName;
    private final String reference;

    public WatchModel(
            UUID id,
            UUID brandId,
            String modelName,
            String reference
    ) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "Watch model id must not be null");
        }

        if (brandId == null) {
            throw new IllegalArgumentException(
                    "Watch model brand id must not be null");
        }

        if (modelName == null || modelName.isBlank()) {
            throw new IllegalArgumentException(
                    "Watch model name must not be blank");
        }

        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException(
                    "Watch model reference must not be blank");
        }

        this.id = id;
        this.brandId = brandId;
        this.modelName = modelName.strip();
        this.reference = reference.strip();
    }

    public UUID getId() {
        return id;
    }

    public UUID getBrandId() {
        return brandId;
    }

    public String getModelName() {
        return modelName;
    }

    public String getReference() {
        return reference;
    }
}
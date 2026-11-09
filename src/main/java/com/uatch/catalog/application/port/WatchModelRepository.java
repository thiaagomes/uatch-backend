package com.uatch.catalog.application.port;

import java.util.UUID;

import com.uatch.catalog.domain.WatchModel;

public interface WatchModelRepository {

    boolean existsByBrandIdAndReference(UUID brandId, String reference);

    void save(WatchModel watchModel);
}
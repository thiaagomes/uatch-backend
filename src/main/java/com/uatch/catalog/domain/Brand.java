package com.uatch.catalog.domain;

import java.util.UUID;
import java.util.regex.Pattern;

public final class Brand {

    private static final Pattern SLUG_PATTERN =
            Pattern.compile("[a-z0-9]+(?:-[a-z0-9]+)*");

    private final UUID id;
    private final String name;
    private final String slug;

    public Brand(UUID id, String name, String slug) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "Brand id must not be null");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Brand name must not be blank");
        }

        if (slug == null || !SLUG_PATTERN.matcher(slug).matches()) {
            throw new IllegalArgumentException(
                    "Brand slug must contain lowercase letters and digits, "
                            + "with single hyphens between words");
        }

        this.id = id;
        this.name = name.strip();
        this.slug = slug;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }
}
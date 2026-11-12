package com.uatch.catalog.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateBrandRequest(

        @NotBlank(message = "Brand name must not be blank")
        String name,

        @NotBlank(message = "Brand slug must not be blank")
        @Pattern(
                regexp = "[a-z0-9]+(?:-[a-z0-9]+)*",
                message = "Brand slug must contain lowercase letters and digits, "
                        + "with single hyphens between words"
        )
        String slug
) {
}
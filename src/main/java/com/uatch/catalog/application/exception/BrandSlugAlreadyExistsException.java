package com.uatch.catalog.application.exception;

public class BrandSlugAlreadyExistsException extends RuntimeException {

    public BrandSlugAlreadyExistsException() {
        super("Brand slug already exists");
    }

    public BrandSlugAlreadyExistsException(Throwable cause) {
        super("Brand slug already exists", cause);
    }
}
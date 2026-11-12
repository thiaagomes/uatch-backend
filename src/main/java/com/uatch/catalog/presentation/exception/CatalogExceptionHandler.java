package com.uatch.catalog.presentation.exception;

import com.uatch.catalog.application.exception.BrandSlugAlreadyExistsException;
import com.uatch.catalog.presentation.controller.BrandController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackageClasses = BrandController.class)
public class CatalogExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BrandSlugAlreadyExistsException.class)
    public ProblemDetail handleBrandSlugAlreadyExists(
            BrandSlugAlreadyExistsException exception
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );

        problem.setTitle("Brand slug conflict");

        return problem;
    }
}
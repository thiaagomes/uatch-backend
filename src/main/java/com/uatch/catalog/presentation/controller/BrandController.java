package com.uatch.catalog.presentation.controller;

import java.util.List;

import com.uatch.catalog.application.usecase.CreateBrand;
import com.uatch.catalog.application.usecase.ListBrands;
import com.uatch.catalog.domain.Brand;
import com.uatch.catalog.presentation.dto.BrandResponse;
import com.uatch.catalog.presentation.dto.CreateBrandRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/brands")
public class BrandController {

    private final ListBrands listBrands;
    private final CreateBrand createBrand;

    public BrandController(
            ListBrands listBrands,
            CreateBrand createBrand
    ) {
        this.listBrands = listBrands;
        this.createBrand = createBrand;
    }

    @GetMapping
    public List<BrandResponse> list() {
        return listBrands.execute()
                .stream()
                .map(BrandResponse::from)
                .toList();
    }

    @PostMapping
    public ResponseEntity<BrandResponse> create(
            @Valid @RequestBody CreateBrandRequest request
    ) {
        Brand brand = createBrand.execute(
                request.name(),
                request.slug()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BrandResponse.from(brand));
    }
}
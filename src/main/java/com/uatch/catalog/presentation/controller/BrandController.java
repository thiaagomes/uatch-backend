package com.uatch.catalog.presentation.controller;

import java.util.List;

import com.uatch.catalog.application.usecase.ListBrands;
import com.uatch.catalog.presentation.dto.BrandResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/brands")
public class BrandController {

    private final ListBrands listBrands;

    public BrandController(ListBrands listBrands) {
        this.listBrands = listBrands;
    }

    @GetMapping
    public List<BrandResponse> list() {
        return listBrands.execute()
                .stream()
                .map(BrandResponse::from)
                .toList();
    }
}
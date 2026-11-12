package com.uatch.catalog.presentation.controller;

import java.util.List;
import java.util.UUID;

import com.uatch.catalog.application.exception.BrandSlugAlreadyExistsException;
import com.uatch.catalog.application.usecase.CreateBrand;
import com.uatch.catalog.application.usecase.ListBrands;
import com.uatch.catalog.domain.Brand;
import com.uatch.catalog.presentation.exception.CatalogExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class BrandControllerTest {

    private ListBrands listBrands;
    private CreateBrand createBrand;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        listBrands = mock(ListBrands.class);
        createBrand = mock(CreateBrand.class);

        mockMvc = standaloneSetup(
                new BrandController(listBrands, createBrand)
        )
                .setControllerAdvice(new CatalogExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnBrandsAsJson() throws Exception {
        UUID id = UUID.randomUUID();
        Brand brand = new Brand(id, "Rolex", "rolex");
        when(listBrands.execute()).thenReturn(List.of(brand));

        mockMvc.perform(get("/api/v1/brands"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].name").value("Rolex"))
                .andExpect(jsonPath("$[0].slug").value("rolex"));

        verify(listBrands).execute();
    }

    @Test
    void shouldReturnEmptyJsonArrayWhenThereAreNoBrands() throws Exception {
        when(listBrands.execute()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/brands"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));

        verify(listBrands).execute();
    }

    @Test
    void shouldCreateBrandAndReturnCreated() throws Exception {
        UUID id = UUID.randomUUID();
        Brand brand = new Brand(id, "Rolex", "rolex");
        when(createBrand.execute("Rolex", "rolex")).thenReturn(brand);

        mockMvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Rolex",
                                    "slug": "rolex"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Rolex"))
                .andExpect(jsonPath("$.slug").value("rolex"));

        verify(createBrand).execute("Rolex", "rolex");
        verifyNoInteractions(listBrands);
    }

    @Test
    void shouldRejectBlankNameBeforeCallingUseCase() throws Exception {
        mockMvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": " ",
                                    "slug": "rolex"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(createBrand, listBrands);
    }

    @Test
    void shouldRejectInvalidSlugBeforeCallingUseCase() throws Exception {
        mockMvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Rolex",
                                    "slug": "Rolex"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(createBrand, listBrands);
    }

    @Test
    void shouldReturnConflictWhenSlugAlreadyExists() throws Exception {
        when(createBrand.execute("Rolex", "rolex"))
                .thenThrow(new BrandSlugAlreadyExistsException());

        mockMvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Rolex",
                                    "slug": "rolex"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Brand slug conflict"))
                .andExpect(jsonPath("$.detail")
                        .value("Brand slug already exists"));

        verify(createBrand).execute("Rolex", "rolex");
        verifyNoInteractions(listBrands);
    }
}
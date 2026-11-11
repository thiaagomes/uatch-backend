package com.uatch.catalog.presentation.controller;

import java.util.List;
import java.util.UUID;

import com.uatch.catalog.application.usecase.ListBrands;
import com.uatch.catalog.domain.Brand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class BrandControllerTest {

    private ListBrands listBrands;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        listBrands = mock(ListBrands.class);
        mockMvc = standaloneSetup(new BrandController(listBrands)).build();
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
}
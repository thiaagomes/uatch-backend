package com.uatch.catalog.presentation.controller;

import java.util.UUID;

import com.uatch.catalog.application.port.BrandRepository;
import com.uatch.catalog.domain.Brand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BrandControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private BrandRepository repository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(context).build();
    }

    @Test
    void shouldCreatePersistAndListBrand() throws Exception {
        String slug = "test-" + UUID.randomUUID();

        mockMvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "  Test Brand  ",
                                    "slug": "%s"
                                }
                                """.formatted(slug)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Test Brand"))
                .andExpect(jsonPath("$.slug").value(slug));

        assertTrue(repository.existsBySlug(slug));

        mockMvc.perform(get("/api/v1/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$[?(@.slug == '%s')].name".formatted(slug)
                ).value(hasItem("Test Brand")));
    }

    @Test
    void shouldReturnConflictForExistingSlug() throws Exception {
        String slug = "test-" + UUID.randomUUID();
        repository.save(new Brand(
                UUID.randomUUID(), "Existing Brand", slug));

        mockMvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Another Brand",
                                    "slug": "%s"
                                }
                                """.formatted(slug)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail")
                        .value("Brand slug already exists"));

        long count = repository.findAll()
                .stream()
                .filter(brand -> brand.getSlug().equals(slug))
                .count();

        assertEquals(1L, count);
    }
}
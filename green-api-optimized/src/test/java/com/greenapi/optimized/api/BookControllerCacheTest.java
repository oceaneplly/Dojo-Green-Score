package com.greenapi.optimized.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("DE02/DE03 HTTP Cache (ETag/304) - Tests unitaires")
@ActiveProfiles("test")
class BookControllerCacheTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test 1: GET /books/{id} retourne un en-tête ETag")
    void testResponseContainsETag() throws Exception {
        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(header().exists("ETag"));
    }

    @Test
    @DisplayName("Test 2: GET /books/{id} avec If-None-Match correspondant retourne 304 Not Modified")
    void testIfNoneMatchReturns304() throws Exception {
        // Première requête pour obtenir l'ETag
        MvcResult firstRequest = mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(header().exists("ETag"))
                .andReturn();

        String etag = firstRequest.getResponse().getHeader("ETag");

        // Deuxième requête avec If-None-Match
        mockMvc.perform(get("/books/1")
                        .header("If-None-Match", etag))
                .andExpect(status().isNotModified())
                .andExpect(header().string("ETag", etag));
    }

    @Test
    @DisplayName("Test 3: Réponse 304 ne contient pas de body (payload vide)")
    void test304ResponseHasNoBody() throws Exception {
        // Première requête pour obtenir l'ETag
        MvcResult firstRequest = mockMvc.perform(get("/books/1"))
                .andReturn();

        String etag = firstRequest.getResponse().getHeader("ETag");
        int firstResponseSize = firstRequest.getResponse().getContentAsByteArray().length;

        // Deuxième requête avec If-None-Match
        MvcResult secondRequest = mockMvc.perform(get("/books/1")
                        .header("If-None-Match", etag))
                .andExpect(status().isNotModified())
                .andReturn();

        int secondResponseSize = secondRequest.getResponse().getContentAsByteArray().length;

        // Vérifier que la réponse 304 ne contient pas de body
        org.assertj.core.api.Assertions.assertThat(secondResponseSize)
                .isEqualTo(0);

        // Vérifier que la première réponse contenait des données
        org.assertj.core.api.Assertions.assertThat(firstResponseSize)
                .isGreaterThan(0);
    }

    @Test
    @DisplayName("Test 4: Deux requêtes identiques retournent le même ETag")
    void testConsistentETag() throws Exception {
        // Première requête
        MvcResult firstRequest = mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andReturn();

        String firstETag = firstRequest.getResponse().getHeader("ETag");

        // Deuxième requête avec le même ID
        MvcResult secondRequest = mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andReturn();

        String secondETag = secondRequest.getResponse().getHeader("ETag");

        // Les ETags doivent être identiques pour des requêtes identiques
        org.assertj.core.api.Assertions.assertThat(firstETag)
                .as("Les ETags doivent être identiques pour des requêtes identiques")
                .isEqualTo(secondETag);
    }

    @Test
    @DisplayName("Test 5: GET /books/{id} avec un ID inexistant retourne 404 Not Found")
    void testNotFoundForInvalidId() throws Exception {
        mockMvc.perform(get("/books/999999999"))
                .andExpect(status().isNotFound());
    }

}


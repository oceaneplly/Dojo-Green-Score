package com.greenapi.optimized.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("DE11 Pagination - Tests unitaires")
@ActiveProfiles("test")
class BookControllerPaginationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test 1: GET /books avec pagination retourne une réponse plus petite (< 100KB)")
    void testPaginationReturnsValidResponse() throws Exception {
        var response = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn();

        // Vérifier la taille de la réponse
        String responseBody = response.getResponse().getContentAsString();
        int responseSize = responseBody.length();

        // Si la pagination fonctionne, la réponse doit être petite (< 100KB)
        // Sans pagination avec 500000 livres, ce serait plusieurs MB
        org.assertj.core.api.Assertions.assertThat(responseSize)
                .as("La réponse paginée doit être < 100KB (pagination active)")
                .isLessThan(100_000);
    }

    @Test
    @DisplayName("Test 2: GET /books?page=0&size=500000")
    void testMaxSizeValidation() throws Exception {
        mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "500000"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test 3: GET /books?page=-1 rejette page négative")
    void testNegativePageValidation() throws Exception {
        mockMvc.perform(get("/books")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test 4: Petite page < Grande page (réduction du payload)")
    void testPayloadReduction() throws Exception {
        String smallPage = mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "5"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String largePage = mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "10"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        org.assertj.core.api.Assertions.assertThat(smallPage.length())
                .isLessThan(largePage.length());
    }
}


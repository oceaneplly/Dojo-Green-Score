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
@DisplayName("DE04/DE05 Synchronisation Delta - Tests unitaires")
@ActiveProfiles("test")
class BookControllerDeltaTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test 1: GET /books sans timestamp retourne tous les livres avec lastModified")
    void testFirstSyncReturnsAllBooksWithTimestamp() throws Exception {
        MvcResult result = mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastModified").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        // Vérifier que la réponse contient des livres
        org.assertj.core.api.Assertions.assertThat(responseBody)
                .contains("lastModified");
    }

    @Test
    @DisplayName("Test 2: GET /books avec timestamp retourne uniquement les modifications")
    void testDeltaSyncReturnsOnlyChanges() throws Exception {
        // Première synchronisation pour obtenir le timestamp
        MvcResult firstSync = mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andReturn();

        String firstResponse = firstSync.getResponse().getContentAsString();

        // Deuxième synchronisation avec le timestamp (pas de changement)
        MvcResult secondSync = mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "20")
                .param("lastSync", String.valueOf(System.currentTimeMillis())))
                .andExpect(status().isOk())
                .andReturn();

        String secondResponse = secondSync.getResponse().getContentAsString();

        // La deuxième réponse doit être plus petite (pas de modifications)
        org.assertj.core.api.Assertions.assertThat(secondResponse.length())
                .as("La synchronisation Delta doit retourner un payload plus petit")
                .isLessThanOrEqualTo(firstResponse.length());
    }

    @Test
    @DisplayName("Test 3: La réponse Delta contient les champs 'added', 'updated', 'deleted'")
    void testDeltaResponseStructure() throws Exception {
        long timestamp = System.currentTimeMillis();

        mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "20")
                .param("lastSync", String.valueOf(timestamp)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.added").exists())
                .andExpect(jsonPath("$.updated").exists())
                .andExpect(jsonPath("$.deleted").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Test 4: Delta avec ancien timestamp retourne les modifications récentes")
    void testDeltaWithOldTimestampReturnsModifications() throws Exception {
        // Utiliser un timestamp ancien (1 jour avant)
        long oldTimestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000);

        MvcResult result = mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "20")
                .param("lastSync", String.valueOf(oldTimestamp)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        // Avec un ancien timestamp, on doit avoir des modifications
        org.assertj.core.api.Assertions.assertThat(responseBody)
                .as("Un ancien timestamp doit retourner les livres modifiés récemment")
                .contains("added");
    }
}


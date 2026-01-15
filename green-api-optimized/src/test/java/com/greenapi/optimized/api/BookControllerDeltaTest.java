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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("DE05 Synchronisation Delta - Tests unitaires")
@ActiveProfiles("test")
class BookControllerDeltaTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test 1: GET /books retourne des livres avec last_modified")
    void testFirstSyncReturnsAllBooksWithTimestamp() throws Exception {
        MvcResult result = mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        // Vérifier que la réponse contient des livres avec lastModified
        org.assertj.core.api.Assertions.assertThat(responseBody)
                .contains("\"content\"", "last_modified");
    }

    @Test
    @DisplayName("Test 2: GET /books/delta?timestamp=TIMESTAMP retourne une liste vide si aucune modification")
    void testDeltaSyncReturnsOnlyChanges() throws Exception {
        // Récupérer le timestamp actuel (futur pour tous les livres)
        long timestamp = System.currentTimeMillis();

        // Appeler la route delta avec le timestamp actuel
        // Aucun livre n'a été modifié après ce timestamp, donc la liste doit être vide
        MvcResult deltaSync = mockMvc.perform(get("/books/delta")
                        .param("timestamp", String.valueOf(timestamp)))
                .andExpect(status().isOk())
                .andReturn();

        String deltaResponse = deltaSync.getResponse().getContentAsString();

        // La réponse Delta doit être une liste vide car aucun livre n'a été modifié après ce timestamp
        org.assertj.core.api.Assertions.assertThat(deltaResponse)
                .contains("[]");
    }

    @Test
    @DisplayName("Test 3: Delta avec ancien timestamp retourne tous les livres")
    void testDeltaWithOldTimestampReturnsModifications() throws Exception {
        // Utiliser un timestamp ancien (1 jour avant)
        long oldTimestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000);

        MvcResult result = mockMvc.perform(get("/books/delta")
                        .param("timestamp", String.valueOf(oldTimestamp)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        // Avec un ancien timestamp, on doit avoir tous les livres (tous créés après)
        org.assertj.core.api.Assertions.assertThat(responseBody)
                .contains("last_modified");
    }

    @Test
    @DisplayName("Test 4: Delta avec timestamp futur retourne une liste vide")
    void testDeltaWithFutureTimestampReturnsEmpty() throws Exception {
        // Utiliser un timestamp futur
        long futureTimestamp = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

        MvcResult result = mockMvc.perform(get("/books/delta")
                        .param("timestamp", String.valueOf(futureTimestamp)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        // Avec un timestamp futur, aucun livre n'a été modifié après
        org.assertj.core.api.Assertions.assertThat(responseBody)
                .contains("[]");
    }

    @Test
    @DisplayName("Test 5: PUT modifie un livre et Delta détecte la modification")
    void testPutModifiesBookAndDeltaDetectsChange() throws Exception {
        // Étape 1 : Récupérer le timestamp avant la modification
        long timestampBeforeModification = System.currentTimeMillis();

        // Attendre un peu pour assurer que le nouveau lastModified sera différent
        Thread.sleep(100);

        // Étape 2 : Modifier un livre avec PUT
        String updatePayload = "{\"title\": \"Titre modifié\", \"author\": \"Auteur modifié\"}";

        MvcResult putResult = mockMvc.perform(put("/books/1")
                        .contentType("application/json")
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andReturn();

        String modifiedBookResponse = putResult.getResponse().getContentAsString();

        // Vérifier que le livre a été modifié
        org.assertj.core.api.Assertions.assertThat(modifiedBookResponse)
                .contains("\"title\":\"Titre modifié\"", "\"author\":\"Auteur modifié\"");

        // Étape 3 : Appeler Delta avec le timestamp d'avant la modification
        MvcResult deltaResult = mockMvc.perform(get("/books/delta")
                        .param("timestamp", String.valueOf(timestampBeforeModification)))
                .andExpect(status().isOk())
                .andReturn();

        String deltaResponse = deltaResult.getResponse().getContentAsString();

        // Vérifier que le livre modifié est dans la réponse Delta
        org.assertj.core.api.Assertions.assertThat(deltaResponse)
                .contains("\"id\":1", "\"title\":\"Titre modifié\"", "\"author\":\"Auteur modifié\"");
    }

}

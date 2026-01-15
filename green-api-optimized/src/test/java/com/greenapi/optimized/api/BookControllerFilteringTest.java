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
@DisplayName("DE08 Filtrage - Tests unitaires")
@ActiveProfiles("test")
class BookControllerFilteringTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test 1: GET /books sans filtrage retourne une réponse valide")
    void testDefaultFieldsReturnsValidResponse() throws Exception {
        var response = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = response.getResponse().getContentAsString();

        // Vérifier que les champs par défaut (id et title) sont présents dans la réponse
        org.assertj.core.api.Assertions.assertThat(responseBody)
                .contains("\"id\"");

        org.assertj.core.api.Assertions.assertThat(responseBody)
                .contains("\"title\"");
    }

    @Test
    @DisplayName("Test 2: GET /books?fields=id,title retourne seulement ces champs")
    void testFilterSpecificFields() throws Exception {
        var response = mockMvc.perform(get("/books")
                .param("fields", "id,title"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = response.getResponse().getContentAsString();

        org.assertj.core.api.Assertions.assertThat(responseBody)
                .contains("\"id\"");

        org.assertj.core.api.Assertions.assertThat(responseBody)
                .contains("\"title\"");

        org.assertj.core.api.Assertions.assertThat(responseBody)
                .doesNotContain("\"author\"");
    }

    @Test
    @DisplayName("Test 3: GET /books?fields=id,title retourne moins de données")
    void testFilteringReducesPayload() throws Exception {
        // Requête sans filtrage
        String fullResponse = mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "10"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Requête avec filtrage
        String filteredResponse = mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "10")
                .param("fields", "id,title"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // La réponse filtrée doit être plus petite
        org.assertj.core.api.Assertions.assertThat(filteredResponse.length())
                .isLessThan(fullResponse.length());
    }

    @Test
    @DisplayName("Test 4: Réponse filtrée < Réponse non filtrée (5 vs 10 champs)")
    void testPayloadReductionComparison() throws Exception {
        // Requête avec few fields
        String fewFields = mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "10")
                .param("fields", "id"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Requête avec many fields
        String manyFields = mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "10")
                .param("fields", "id,title,author,published_date,pages"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Few fields doit être plus petit que many fields
        org.assertj.core.api.Assertions.assertThat(fewFields.length())
                .isLessThan(manyFields.length());
    }
}


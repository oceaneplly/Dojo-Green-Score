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
@DisplayName("DE01 Compression Gzip - Tests unitaires")
@ActiveProfiles("test")
class BookControllerCompressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test 1: GET /books avec Accept-Encoding: gzip retourne Content-Encoding: gzip")
    void testGzipCompressionIsEnabled() throws Exception {
        mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "20")
                .header("Accept-Encoding", "gzip"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Encoding", "gzip"));
    }

    @Test
    @DisplayName("Test 2: GET /books sans Accept-Encoding retourne une réponse non compressée")
    void testNoCompressionWithoutAcceptEncoding() throws Exception {
        mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Content-Encoding"));
    }

    @Test
    @DisplayName("Test 3: La réponse compressée est plus petite que la réponse non compressée")
    void testCompressedResponseIsSmallerThanUncompressed() throws Exception {
        // Requête sans compression
        byte[] uncompressedResponse = mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "50"))
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        byte[] compressedResponse = mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "50")
                .header("Accept-Encoding", "gzip"))
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        org.assertj.core.api.Assertions.assertThat(compressedResponse.length)
                .as("La réponse compressée doit être au moins 50% plus petite")
                .isLessThan(uncompressedResponse.length / 2);
    }

    @Test
    @DisplayName("Test 4: Taux de compression d'au moins 60% pour grandes réponses")
    void testCompressionRatioIsAtLeast60Percent() throws Exception {
        // Grande réponse pour maximiser l'efficacité de la compression
        byte[] uncompressedResponse = mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "100"))
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        byte[] compressedResponse = mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "100")
                .header("Accept-Encoding", "gzip"))
                .andExpect(header().string("Content-Encoding", "gzip"))
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        // Calculer le taux de compression
        double compressionRatio = 1.0 - ((double) compressedResponse.length / uncompressedResponse.length);

        org.assertj.core.api.Assertions.assertThat(compressionRatio)
                .as("Le taux de compression doit être d'au moins 60%")
                .isGreaterThanOrEqualTo(0.60);
    }
}


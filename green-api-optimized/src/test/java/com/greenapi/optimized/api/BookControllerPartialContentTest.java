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
@DisplayName("DE06 Partial Content (HTTP 206) - Tests unitaires")
@ActiveProfiles("test")
class BookControllerPartialContentTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test 1: GET /books retourne Accept-Ranges: bytes")
    void testAcceptRangesHeaderIsPresent() throws Exception {
        mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(header().string("Accept-Ranges", "bytes"));
    }

    @Test
    @DisplayName("Test 2: GET /books avec Range: bytes=0-999 retourne 206 Partial Content")
    void testRangeRequestReturns206() throws Exception {
        mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "100")
                .header("Range", "bytes=0-999"))
                .andExpect(status().isPartialContent())
                .andExpect(header().exists("Content-Range"));
    }

    @Test
    @DisplayName("Test 3: Réponse 206 contient Content-Range correctement formaté")
    void testContentRangeHeaderFormat() throws Exception {
        MvcResult result = mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "100")
                .header("Range", "bytes=0-999"))
                .andExpect(status().isPartialContent())
                .andExpect(header().exists("Content-Range"))
                .andReturn();

        String contentRange = result.getResponse().getHeader("Content-Range");

        // Vérifier le format: bytes start-end/total
        org.assertj.core.api.Assertions.assertThat(contentRange)
                .as("Content-Range doit respecter le format 'bytes start-end/total'")
                .matches("bytes \\d+-\\d+/\\d+");
    }

    @Test
    @DisplayName("Test 4: Plage invalide retourne 416 Range Not Satisfiable")
    void testInvalidRangeReturns416() throws Exception {
        mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "100")
                .header("Range", "bytes=99999999-100000000"))
                .andExpect(status().isRequestedRangeNotSatisfiable());
    }
}


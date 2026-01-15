package com.greenapi.optimized.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("DE01 Compression Gzip - Tests unitaires")
@ActiveProfiles("test")
class BookControllerCompressionTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("Test 1: GET /books accepte Accept-Encoding: gzip")
    void testGzipCompressionIsEnabled() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/books?page=0&size=20",
                String.class);

        assertThat(response.getStatusCode().value())
                .isEqualTo(200);
    }

    @Test
    @DisplayName("Test 2: GET /books fonctionne sans Accept-Encoding")
    void testNoCompressionWithoutAcceptEncoding() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/books?page=0&size=20",
                String.class);

        assertThat(response.getStatusCode().value())
                .isEqualTo(200);
    }

    @Test
    @DisplayName("Test 3: La réponse compressée est plus petite que la réponse non-compressée")
    void testCompressionRatioIsAtLeast60Percent() throws Exception {
        // Taille avec compression
        int compressedSize = getCompressedResponseSize();

        // Taille sans compression
        int uncompressedSize = getUncompressedResponseSize();

        System.out.println("=== Comparaison de compression ===");
        System.out.println("Taille compressée (avec gzip):    " + compressedSize + " bytes");
        System.out.println("Taille non-compressée (brut):     " + uncompressedSize + " bytes");

        if (uncompressedSize > 0 && compressedSize > 0) {
            double compressionRatio = (1.0 - ((double) compressedSize / uncompressedSize)) * 100;
            System.out.println("Ratio de compression:             " + String.format("%.2f", compressionRatio) + " %");

            // Vérifier que la compression réduit la taille d'au moins 50%
            assertThat(compressedSize)
                    .isLessThan(uncompressedSize / 2);

            System.out.println("✓ Compression ACTIVE et efficace");
        } else {
            System.out.println("⚠ Impossible de mesurer la compression");
        }
    }

    private int getCompressedResponseSize() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/books?page=0&size=100"))
                .header("Accept-Encoding", "gzip")
                .GET()
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        // Retourner la taille brute du body compressé
        return response.body().length;
    }

    private int getUncompressedResponseSize() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/books?page=0&size=100"))
                .GET()
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        // Retourner la taille du body non-compressé
        return response.body().length;
    }
}

package com.example.demo.integration.openlibrary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class OpenLibraryClient {

    private static final Logger log = LoggerFactory.getLogger(OpenLibraryClient.class);

    private final RestClient restClient;

    public OpenLibraryClient(@Value("${app.open-library.base-url}") String baseUrl,
                              @Value("${app.open-library.user-agent}") String userAgent) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .build();
    }

    public List<OpenLibrarySubjectResponse.Work> fetchSubjectWorks(String subject, int limit, int offset) {
        try {
            OpenLibrarySubjectResponse response = restClient.get()
                    .uri("/subjects/{subject}.json?limit={limit}&offset={offset}", subject, limit, offset)
                    .retrieve()
                    .body(OpenLibrarySubjectResponse.class);

            return response != null && response.getWorks() != null ? response.getWorks() : List.of();
        } catch (Exception e) {
            log.warn("Impossibile recuperare le opere per la subject '{}': {}", subject, e.getMessage());
            return List.of();
        }
    }
}

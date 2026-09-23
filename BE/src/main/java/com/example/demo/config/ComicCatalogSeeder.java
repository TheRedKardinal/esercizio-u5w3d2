package com.example.demo.config;

import com.example.demo.integration.openlibrary.OpenLibraryClient;
import com.example.demo.integration.openlibrary.OpenLibrarySubjectResponse;
import com.example.demo.model.Item;
import com.example.demo.repository.ItemRepository;
import com.example.demo.service.PriceGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Importa il catalogo fumetti da Open Library una sola volta all'avvio
 * (se la tabella items è vuota) e lo salva nel DB, così l'API esterna
 * non viene richiamata ad ogni richiesta.
 */
@Component
@Order(2)
public class ComicCatalogSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ComicCatalogSeeder.class);

    private final OpenLibraryClient openLibraryClient;
    private final ItemRepository itemRepository;
    private final PriceGeneratorService priceGeneratorService;

    private final List<String> subjects;
    private final int limitPerPage;
    private final int pagesPerSubject;
    private final int maxItems;

    public ComicCatalogSeeder(OpenLibraryClient openLibraryClient,
                               ItemRepository itemRepository,
                               PriceGeneratorService priceGeneratorService,
                               @Value("${app.open-library.subjects}") List<String> subjects,
                               @Value("${app.open-library.limit}") int limitPerPage,
                               @Value("${app.open-library.pages}") int pagesPerSubject,
                               @Value("${app.open-library.max-items}") int maxItems) {
        this.openLibraryClient = openLibraryClient;
        this.itemRepository = itemRepository;
        this.priceGeneratorService = priceGeneratorService;
        this.subjects = subjects;
        this.limitPerPage = limitPerPage;
        this.pagesPerSubject = pagesPerSubject;
        this.maxItems = maxItems;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (itemRepository.count() > 0) {
            return;
        }

        Map<String, Item> itemsByKey = new LinkedHashMap<>();

        for (String subject : subjects) {
            for (int page = 0; page < pagesPerSubject && itemsByKey.size() < maxItems; page++) {
                int offset = page * limitPerPage;
                List<OpenLibrarySubjectResponse.Work> works = openLibraryClient.fetchSubjectWorks(subject, limitPerPage, offset);

                for (OpenLibrarySubjectResponse.Work work : works) {
                    if (itemsByKey.size() >= maxItems) {
                        break;
                    }
                    if (work.getKey() == null || work.getTitle() == null || work.getCoverId() == null) {
                        continue;
                    }
                    itemsByKey.computeIfAbsent(work.getKey(), key -> toItem(work));
                }
            }
        }

        if (itemsByKey.isEmpty()) {
            log.warn("Nessun fumetto importato da Open Library: il catalogo resta vuoto");
            return;
        }

        itemRepository.saveAll(itemsByKey.values());
        log.info("Importati {} fumetti da Open Library", itemsByKey.size());
    }

    private Item toItem(OpenLibrarySubjectResponse.Work work) {
        Item item = new Item(work.getTitle(), priceGeneratorService.generate());
        item.setOpenLibraryKey(work.getKey());
        item.setCoverUrl("https://covers.openlibrary.org/b/id/" + work.getCoverId() + "-M.jpg");
        item.setStock(priceGeneratorService.generateStock());

        if (work.getAuthors() != null && !work.getAuthors().isEmpty()) {
            item.setAuthor(work.getAuthors().get(0).getName());
        }

        return item;
    }
}

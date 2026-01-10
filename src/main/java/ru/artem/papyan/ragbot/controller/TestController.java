package ru.artem.papyan.ragbot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.artem.papyan.ragbot.domain.FandomPageParseResponse;
import ru.artem.papyan.ragbot.domain.util.Pair;
import ru.artem.papyan.ragbot.preprocessing.*;
import ru.artem.papyan.ragbot.scraping.FandomScrapingService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final FandomScrapingService scrapingService;
    private final RawDataCollector rawDataCollector;
    private final RawDataCleaner rawDataCleaner;
    private final CleanDataDereferencer cleanDataDereferencer;
    private final CleanDataFictionalFilterer fictionalFilterer;
    private final FilteredDataMasker filteredDataMasker;
    private final CleanDataReplacer cleanDataReplacer;

    @GetMapping("/fetch")
    public Map<String, Object> fetchPagesId() {
        List<Pair<Long, String>> pageIds = scrapingService.fetchAllPageIds();

        return Map.of(
                "amount", pageIds.size(),
                "pages", pageIds
        );
    }

    @GetMapping("/parse")
    public Map<String, Object> parsePage(@RequestParam("pageId") Long pageId) {
        FandomPageParseResponse result = scrapingService.parsePage(pageId);

        return Map.of(
                "body", result,
                "pageContent", result.parse().text().content()
        );
    }

    @GetMapping("/collect-raw")
    public Map<String, Object> collectRawPages() throws IOException {
        long result = rawDataCollector.collectAndSaveRawData();

        return Map.of("collected", result);
    }

    @GetMapping("/clean-raw")
    public Map<String, Object> cleanRawPages() throws IOException {
        long result = rawDataCleaner.processRawDataFiles();

        return Map.of("cleaned", result);
    }

    @GetMapping("/dereference-clean")
    public Map<String, Object> dereferenceCleanPages() {
        long result = cleanDataDereferencer.transform();

        return Map.of("dereferenced", result);
    }

    @GetMapping("/filter-fictional")
    public Map<String, Object> filterNonFictional() {
        long result = fictionalFilterer.filter();

        return Map.of("filtered", result);
    }

    @GetMapping("/mask-filtered")
    public Map<String, Object> maskFiltered() {
        long result = filteredDataMasker.transform();

        return Map.of("masked", result);
    }

    @GetMapping("/replace-clean")
    public Map<String, Object> replaceClean() {
        long result = cleanDataReplacer.transform();

        return Map.of("replaced", result);
    }
}

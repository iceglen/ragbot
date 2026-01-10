package ru.artem.papyan.ragbot.preprocessing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.artem.papyan.ragbot.config.EnvConfig;
import ru.artem.papyan.ragbot.domain.FandomPageParseResponse;
import ru.artem.papyan.ragbot.domain.util.Pair;
import ru.artem.papyan.ragbot.scraping.FandomScrapingService;
import ru.artem.papyan.ragbot.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class RawDataCollector {
    private final EnvConfig envConfig;
    private final ExecutorService executorService;
    private final FandomScrapingService scrapingService;

    /**
     * Collects page IDs and titles, fetches parsed content concurrently using virtual threads,
     * and saves each page's text content to a separate text file in the './raw' directory.
     * The directory is created if it doesn't exist.
     *
     * @return number of successfully saved pages
     * @throws IOException if directory creation or file writing fails
     */
    public long collectAndSaveRawData() throws IOException {
        log.info("Fetching all page IDs and titles...");
        List<Pair<Long, String>> pageIdsWithTitles = scrapingService.fetchAllPageIds();
        log.info("Found {} pages to process", pageIdsWithTitles.size());

        Path rawDir = Path.of(envConfig.getRawDataDir());
        Files.createDirectories(rawDir);
        log.info("Created/verified directory: {}", rawDir.toAbsolutePath());

        // Clean directory if it already contains files
        FileSystemUtils.cleanDirectoryContents(rawDir);

        List<CompletableFuture<Boolean>> futures = pageIdsWithTitles.stream()
                .map(pair -> CompletableFuture.supplyAsync(() -> {
                    Long pageId = pair.key();
                    String title = pair.value();
                    log.debug("Parsing page {} ({})", pageId, title);
                    FandomPageParseResponse parsed = scrapingService.parsePage(pageId);
                    if (parsed == null || parsed.parse() == null || parsed.parse().text() == null) {
                        log.warn("Skipping page {} ({}) - no parsed content", pageId, title);
                        return false;
                    }
                    String content = parsed.parse().text().content();
                    if (content == null || content.isBlank()) {
                        log.warn("Skipping page {} ({}) - empty content", pageId, title);
                        return false;
                    }
                    String safeFileName = sanitizeFileName(title) + ".txt";
                    Path filePath = rawDir.resolve(safeFileName);
                    try {
                        Files.writeString(filePath, content);
                        log.debug("Saved file: {}", filePath);
                        return true;
                    } catch (IOException e) {
                        log.error("Failed to write file {}: {}", filePath, e.getMessage());
                        throw new RuntimeException(e);
                    }
                }, executorService))
                .toList();

        // Wait for all futures to complete, collect results
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long successCount = futures.stream()
                .filter(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();
        log.info("Saved {} pages out of {}", successCount, pageIdsWithTitles.size());
        return successCount;
    }

    private String sanitizeFileName(String title) {
        // Replace characters that are invalid in filenames with underscore
        // Keep letters, digits, spaces, hyphens, underscores
        return title.replaceAll("[\\/:*?\"<>|]", "_");
    }
}

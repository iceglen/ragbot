package ru.artem.papyan.ragbot.preprocessing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.artem.papyan.ragbot.config.EnvConfig;
import ru.artem.papyan.ragbot.util.DirectoryComparator;
import ru.artem.papyan.ragbot.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawDataCleaner {

    private final EnvConfig envConfig;

    public long processRawDataFiles() throws IOException {
        Path rawDir = Path.of(envConfig.getRawDataDir());
        Path cleanDir = Path.of(envConfig.getCleanDataDir());
        Path tmpDir = Path.of(envConfig.getCleanTmpDir());
        List<String> contentExclusions = envConfig.getContentExclusions();

        if (contentExclusions == null || contentExclusions.isEmpty()) {
            log.warn("No content exclusions configured, skipping");
            return 0;
        }

        if (!Files.exists(rawDir) || !Files.isDirectory(rawDir)) {
            log.warn("Raw data directory does not exist: {}", rawDir);
            return 0;
        }

        // Create clean directory if it doesn't exist
        Files.createDirectories(cleanDir);
        Files.createDirectories(tmpDir);

        // Clean directory contents
        FileSystemUtils.cleanDirectoryContents(tmpDir);

        long processedCount = 0;
        try (var stream = Files.list(rawDir)) {
            var files = stream.filter(path -> path.toString().endsWith(".txt")).toList();
            log.info("Found {} .txt files in {}", files.size(), rawDir);

            for (Path rawFile : files) {
                try {
                    String htmlContent = Files.readString(rawFile);
                    Document htmlDocument = Jsoup.parse(htmlContent);
                    String cleanedText = htmlDocument.wholeText();

                    boolean isSkippingFile = false;
                    for (String exclusion : contentExclusions) {
                        if (cleanedText.toLowerCase().contains(exclusion)) {
                            log.debug("Skipping file with exclusion: {}", rawFile.getFileName());
                            isSkippingFile = true;
                        }
                    }

                    if (!isSkippingFile) {
                        Path outputFile = tmpDir.resolve(rawFile.getFileName());
                        Files.writeString(outputFile, cleanedText);
                        log.debug("Processed file: {} -> {}", rawFile.getFileName(), outputFile);
                        processedCount++;
                    }
                } catch (IOException e) {
                    log.error("Failed to process file {}: {}", rawFile, e.getMessage());
                }
            }
        }

        if (isUpdateNeeded(cleanDir, tmpDir)) {
            FileSystemUtils.cleanDirectoryContents(cleanDir);
            FileSystemUtils.copyDirectoryContents(tmpDir, cleanDir);

            FileSystemUtils.cleanDirectoryContents(tmpDir);
            Files.delete(tmpDir);
        } else {
            FileSystemUtils.cleanDirectoryContents(tmpDir);
            Files.delete(tmpDir);

            return 0;
        }

        log.info("Processed {} files out of total found", processedCount);
        return processedCount;
    }

    private boolean isUpdateNeeded(Path cleanDir, Path tmpDir) throws IOException {
        if (FileSystemUtils.isEmpty(cleanDir)) {
            return true;
        }

        return DirectoryComparator.needsUpdate(cleanDir, tmpDir);
    }
}

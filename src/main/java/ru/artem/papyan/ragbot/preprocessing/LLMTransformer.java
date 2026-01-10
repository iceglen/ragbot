package ru.artem.papyan.ragbot.preprocessing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import ru.artem.papyan.ragbot.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RequiredArgsConstructor
public abstract class LLMTransformer {

    private final ChatClient chatClient;

    protected String sendPrompt(String prompt, String title, String text) {
        return chatClient.prompt(String.format(prompt, title, text)).call().content();
    }

    protected long applyTransformPrompt(String processingPrompt, Path src, Path dst) throws IOException {
        if (!Files.exists(src) || !Files.isDirectory(src)) {
            log.warn("Source data directory does not exist: {}", src);
            return 0;
        }

        // Create destination directory if it doesn't exist
        Files.createDirectories(dst);

        // Clean destination directory contents
        FileSystemUtils.cleanDirectoryContents(dst);

        long processedCount = 0;
        try (var stream = Files.list(src)) {
            var files = stream.filter(path -> path.toString().endsWith(".txt")).toList();
            log.info("Found {} .txt files in {}", files.size(), src);

            for (Path cleanFile : files) {
                try {
                    String title = parseFilename(cleanFile);
                    String textContent = Files.readString(cleanFile);
                    String transformedText = sendPrompt(processingPrompt, title, textContent);
                    Path outputFile = dst.resolve(cleanFile.getFileName());
                    Files.writeString(outputFile, transformedText);
                    log.info("Processed file: {} -> {}", cleanFile.getFileName(), outputFile);
                    processedCount++;
                } catch (Exception e) {
                    log.error("Failed to process file {}: {}", cleanFile, e.getMessage());
                }
            }
        }
        log.info("Processed {} files out of total found", processedCount);
        return processedCount;
    }

    private String parseFilename(Path file) {
        if (file == null) {
            return null;
        }

        Path fileNamePath = file.getFileName();
        if (fileNamePath == null) {
            return null;
        }

        String fileName = fileNamePath.toString();
        int lastDotIndex = fileName.lastIndexOf('.');

        if (lastDotIndex <= 0) {
            return fileName;
        }

        return fileName.substring(0, lastDotIndex);
    }

    public abstract long transform();
}

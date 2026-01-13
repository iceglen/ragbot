package ru.artem.papyan.ragbot.processing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileUrlResource;
import org.springframework.stereotype.Service;
import ru.artem.papyan.ragbot.config.EnvConfig;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentProcessingService {
    private final EnvConfig envConfig;
    private final TokenTextSplitter splitter;

    private final VectorStore vectorStore;

    public void processDocuments() {
        log.info("Reading files");

        Path textDir = Path.of(envConfig.getReplacedDataDir());

        List<String> paths;
        try (Stream<Path> stream = Files.list(textDir)) {
            paths = stream.map(Path::toAbsolutePath).map(Path::toString).toList();
        } catch (Exception e) {
            log.warn("error while reading files: {}", e.getMessage());
            paths = Collections.emptyList();
        }

        for (String path : paths) {
            log.info("Processing document file: {}", path);

            List<Document> documents;
            try {
                documents = this.readFile(path);
            } catch (MalformedURLException e) {
                log.warn("error while reading file {}: {}", path, e.getMessage());
                continue;
            }

            List<Document> chunks = splitter.split(documents);

            this.composeFileMetadata(path, chunks);

            log.info("Saving document to vector store");

            vectorStore.add(chunks);

            log.info("File saved to vector store");
        }
    }

    private List<Document> readFile(String path) throws MalformedURLException {
        var reader = new TextReader(new FileUrlResource(path));
        reader.getCustomMetadata().put("source", path);
        return reader.get();
    }

    private void composeFileMetadata(String path, List<Document> chunks) {
        for (int i = 0; i < chunks.size(); i++) {
            Document chunk = chunks.get(i);
            chunk.getMetadata().put("chunk_id", i);
            chunk.getMetadata().put("source_document", path);
            chunk.getMetadata().put("position", i);
        }
    }
}

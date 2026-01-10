package ru.artem.papyan.ragbot.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for file system operations.
 */
@Slf4j
public final class FileSystemUtils {

    private FileSystemUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Recursively deletes all contents of a directory, leaving the directory itself empty.
     * If the directory does not exist or is not a directory, does nothing.
     *
     * @param dir the directory to clean
     * @throws IOException if an I/O error occurs
     */
    public static void cleanDirectoryContents(Path dir) throws IOException {
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            return;
        }
        try (var stream = Files.list(dir)) {
            var children = stream.toList();
            if (children.isEmpty()) {
                log.debug("Directory {} is already empty", dir);
                return;
            }
            log.info("Cleaning directory {} ({} items found)", dir, children.size());
            for (Path child : children) {
                deleteRecursively(child);
            }
            log.info("Directory cleaned successfully");
        }
    }

    /**
     * Recursively deletes a file or directory.
     * If the path is a directory, its contents are deleted first.
     *
     * @param path the file or directory to delete
     * @throws IOException if an I/O error occurs
     */
    public static void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (var stream = Files.list(path)) {
                for (Path child : stream.toList()) {
                    deleteRecursively(child);
                }
            }
        }
        Files.delete(path);
        log.trace("Deleted: {}", path);
    }
}
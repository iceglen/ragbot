package ru.artem.papyan.ragbot.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for comparing directories.
 */
@Slf4j
public final class DirectoryComparator {

    private DirectoryComparator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Compares two directories to determine if they differ in file count, file names, or file content.
     * Returns {@code true} if the directories differ (i.e., an update is needed), {@code false} if they are identical.
     *
     * @param dir1 first directory
     * @param dir2 second directory
     * @return {@code true} if directories differ, {@code false} if they are identical
     * @throws IllegalArgumentException if either path is not a directory
     * @throws IOException if an I/O error occurs
     */
    public static boolean needsUpdate(Path dir1, Path dir2) throws IOException {
        log.debug("Comparing directories: {} and {}", dir1, dir2);

        // Ensure both paths exist and are directories
        if (!Files.exists(dir1) || !Files.isDirectory(dir1)) {
            throw new IllegalArgumentException("First path is not an existing directory: " + dir1);
        }
        if (!Files.exists(dir2) || !Files.isDirectory(dir2)) {
            throw new IllegalArgumentException("Second path is not an existing directory: " + dir2);
        }

        // Collect immediate files (excluding subdirectories) from both directories
        Set<Path> files1 = getImmediateFiles(dir1);
        Set<Path> files2 = getImmediateFiles(dir2);

        log.debug("Directory {} contains {} files", dir1, files1.size());
        log.debug("Directory {} contains {} files", dir2, files2.size());

        // Compare file counts
        if (files1.size() != files2.size()) {
            log.info("Directories differ in file count: {} vs {}", files1.size(), files2.size());
            return true;
        }

        // Compare file names (filenames only, not full paths)
        Set<String> names1 = files1.stream()
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toSet());
        Set<String> names2 = files2.stream()
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toSet());

        if (!names1.equals(names2)) {
            log.info("Directories differ in file names: {} vs {}", names1, names2);
            return true;
        }

        // Compare content of each file
        for (Path file1 : files1) {
            Path file2 = dir2.resolve(file1.getFileName());
            if (Files.isDirectory(file1) || Files.isDirectory(file2)) {
                // Should not happen because we filtered out directories, but guard anyway
                continue;
            }
            if (filesDiffer(file1, file2)) {
                log.info("Files differ: {} and {}", file1, file2);
                return true;
            }
        }

        log.debug("Directories are identical");
        return false;
    }

    private static Set<Path> getImmediateFiles(Path dir) throws IOException {
        try (var stream = Files.list(dir)) {
            return stream.filter(Files::isRegularFile)
                    .collect(Collectors.toSet());
        }
    }

    private static boolean filesDiffer(Path file1, Path file2) throws IOException {
        if (Files.size(file1) != Files.size(file2)) {
            return true;
        }
        // Use Files.mismatch for efficient byte-by-byte comparison
        return Files.mismatch(file1, file2) != -1;
    }
}
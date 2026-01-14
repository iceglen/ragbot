package ru.artem.papyan.ragbot.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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

    /**
     * Checks if the given path is an empty directory.
     * Returns {@code false} if the path does not exist, is not a directory,
     * or cannot be read.
     *
     * @param path the path to check
     * @return {@code true} if the path is an empty directory, {@code false} otherwise
     */
    public static boolean isEmpty(Path path) {
        if (path == null) {
            throw new NullPointerException("Path cannot be null");
        }
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            return false;
        }
        try (var stream = Files.list(path)) {
            return stream.findAny().isEmpty();
        } catch (IOException e) {
            log.warn("Unable to list directory contents: {}", path, e);
            return false;
        }
    }

    /**
     * Recursively copies a file or directory.
     * If source is a directory, copies its contents recursively to target,
     * preserving the relative directory structure.
     * If target's parent directories do not exist, they will be created.
     *
     * @param source the source path (file or directory)
     * @param target the target path (file or directory)
     * @param options copy options (e.g., {@link StandardCopyOption#REPLACE_EXISTING})
     * @throws IOException if an I/O error occurs
     */
    public static void copy(Path source, Path target, CopyOption... options) throws IOException {
        if (source == null || target == null) {
            throw new NullPointerException("Source and target cannot be null");
        }
        if (source.equals(target)) {
            log.trace("Source and target are the same, skipping copy: {}", source);
            return;
        }
        if (!Files.exists(source)) {
            throw new IOException("Source does not exist: " + source);
        }
        if (Files.isDirectory(source)) {
            Path sourceAbs = source.toAbsolutePath().normalize();
            Path targetAbs = target.toAbsolutePath().normalize();
            if (targetAbs.startsWith(sourceAbs)) {
                throw new IOException("Cannot copy a directory into its own subdirectory: source=" + source + ", target=" + target);
            }
            if (!Files.exists(target)) {
                Files.createDirectories(target);
            } else if (!Files.isDirectory(target)) {
                throw new IOException("Target exists but is not a directory: " + target);
            }
            log.trace("Copying directory: {} -> {}", source, target);
            try (var stream = Files.list(source)) {
                for (Path child : stream.toList()) {
                    copy(child, target.resolve(child.getFileName()), options);
                }
            }
        } else {
            Path parent = target.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            Files.copy(source, target, options);
            log.trace("Copied: {} -> {}", source, target);
        }
    }

    /**
     * Copies all files and directories from source directory to target directory.
     * The contents of source directory are copied directly into target directory,
     * preserving the relative paths within source.
     * If target directory does not exist, it will be created.
     *
     * @param sourceDir the source directory
     * @param targetDir the target directory
     * @param options copy options (e.g., {@link StandardCopyOption#REPLACE_EXISTING})
     * @throws IOException if an I/O error occurs
     */
    public static void copyDirectoryContents(Path sourceDir, Path targetDir, CopyOption... options) throws IOException {
        if (sourceDir == null || targetDir == null) {
            throw new NullPointerException("Source and target directories cannot be null");
        }
        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            throw new IOException("Source is not an existing directory: " + sourceDir);
        }
        Path sourceAbs = sourceDir.toAbsolutePath().normalize();
        Path targetAbs = targetDir.toAbsolutePath().normalize();
        if (targetAbs.startsWith(sourceAbs)) {
            throw new IOException("Cannot copy a directory into its own subdirectory: source=" + sourceDir + ", target=" + targetDir);
        }
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        } else if (!Files.isDirectory(targetDir)) {
            throw new IOException("Target exists but is not a directory: " + targetDir);
        }
        log.trace("Copying directory contents: {} -> {}", sourceDir, targetDir);
        try (var stream = Files.list(sourceDir)) {
            for (Path child : stream.toList()) {
                copy(child, targetDir.resolve(child.getFileName()), options);
            }
        }
    }
}

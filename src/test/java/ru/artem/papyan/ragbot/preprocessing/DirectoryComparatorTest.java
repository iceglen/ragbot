package ru.artem.papyan.ragbot.preprocessing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.artem.papyan.ragbot.util.DirectoryComparator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryComparatorTest {

    @TempDir
    Path tempDir;

    @Test
    void identicalEmptyDirectories_shouldReturnFalse() throws IOException {
        Path dir1 = tempDir.resolve("dir1");
        Path dir2 = tempDir.resolve("dir2");
        Files.createDirectory(dir1);
        Files.createDirectory(dir2);

        assertFalse(DirectoryComparator.needsUpdate(dir1, dir2));
    }

    @Test
    void differentFileCount_shouldReturnTrue() throws IOException {
        Path dir1 = tempDir.resolve("dir1");
        Path dir2 = tempDir.resolve("dir2");
        Files.createDirectory(dir1);
        Files.createDirectory(dir2);

        Files.writeString(dir1.resolve("file1.txt"), "content");
        // dir2 empty

        assertTrue(DirectoryComparator.needsUpdate(dir1, dir2));
    }

    @Test
    void sameCountDifferentNames_shouldReturnTrue() throws IOException {
        Path dir1 = tempDir.resolve("dir1");
        Path dir2 = tempDir.resolve("dir2");
        Files.createDirectory(dir1);
        Files.createDirectory(dir2);

        Files.writeString(dir1.resolve("file1.txt"), "content");
        Files.writeString(dir2.resolve("file2.txt"), "content");

        assertTrue(DirectoryComparator.needsUpdate(dir1, dir2));
    }

    @Test
    void sameNamesDifferentContent_shouldReturnTrue() throws IOException {
        Path dir1 = tempDir.resolve("dir1");
        Path dir2 = tempDir.resolve("dir2");
        Files.createDirectory(dir1);
        Files.createDirectory(dir2);

        Files.writeString(dir1.resolve("file.txt"), "content1");
        Files.writeString(dir2.resolve("file.txt"), "content2");

        assertTrue(DirectoryComparator.needsUpdate(dir1, dir2));
    }

    @Test
    void identicalFiles_shouldReturnFalse() throws IOException {
        Path dir1 = tempDir.resolve("dir1");
        Path dir2 = tempDir.resolve("dir2");
        Files.createDirectory(dir1);
        Files.createDirectory(dir2);

        Files.writeString(dir1.resolve("file.txt"), "same content");
        Files.writeString(dir2.resolve("file.txt"), "same content");

        assertFalse(DirectoryComparator.needsUpdate(dir1, dir2));
    }

    @Test
    void subdirectoriesIgnored() throws IOException {
        Path dir1 = tempDir.resolve("dir1");
        Path dir2 = tempDir.resolve("dir2");
        Files.createDirectory(dir1);
        Files.createDirectory(dir2);

        // Create subdirectory in dir1 with a file
        Path sub1 = dir1.resolve("sub");
        Files.createDirectory(sub1);
        Files.writeString(sub1.resolve("inner.txt"), "ignored");

        // Create a regular file in dir2 with same name as subdirectory
        Files.writeString(dir2.resolve("sub"), "file named sub");

        // Subdirectories are ignored, so dir1 has zero regular files, dir2 has one regular file
        // Therefore they differ
        assertTrue(DirectoryComparator.needsUpdate(dir1, dir2));
    }

    @Test
    void nonExistingDirectory_throwsException() {
        Path dir1 = tempDir.resolve("dir1");
        Path dir2 = tempDir.resolve("dir2");
        assertThrows(IllegalArgumentException.class,
                () -> DirectoryComparator.needsUpdate(dir1, dir2));
    }

    @Test
    void notDirectory_throwsException() throws IOException {
        Path file = tempDir.resolve("file.txt");
        Files.writeString(file, "content");
        Path dir = tempDir.resolve("dir");
        Files.createDirectory(dir);

        assertThrows(IllegalArgumentException.class,
                () -> DirectoryComparator.needsUpdate(file, dir));
    }
}
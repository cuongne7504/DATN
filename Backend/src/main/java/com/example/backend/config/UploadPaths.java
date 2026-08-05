package com.example.backend.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolve upload directories for both Backend/uploads and project-root/uploads.
 */
public final class UploadPaths {

    private UploadPaths() {}

    public static List<Path> candidateDirs() {
        List<Path> dirs = new ArrayList<>();
        Path local = Paths.get("uploads").toAbsolutePath().normalize();
        Path sibling = Paths.get("..", "uploads").toAbsolutePath().normalize();
        dirs.add(local);
        if (!sibling.equals(local)) {
            dirs.add(sibling);
        }
        return dirs;
    }

    /** Preferred writable directory for new uploads. */
    public static Path resolveWritableDir() {
        Path sibling = Paths.get("..", "uploads").toAbsolutePath().normalize();
        if (Files.isDirectory(sibling)) {
            return sibling;
        }
        Path local = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(local);
        } catch (Exception ignored) {
            // fall through
        }
        return local;
    }
}

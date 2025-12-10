package org.exp.reportservice.cucumber;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.stream.Stream;

public class TargetFileFinder {

    /**
     * Search recursively under the `target` directory for a file with the given name.
     * Returns an Optional\<File\> with the first match.
     */
    public static Optional<File> findInTarget(String fileName) throws IOException {
        Path targetDir = Paths.get("target");
        if (!Files.exists(targetDir) || !Files.isDirectory(targetDir)) {
            return Optional.empty();
        }

        try (Stream<Path> stream = Files.walk(targetDir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().equals(fileName))
                    .findFirst()
                    .map(Path::toFile);
        }
    }
}

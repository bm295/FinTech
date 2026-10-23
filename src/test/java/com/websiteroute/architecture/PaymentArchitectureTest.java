package com.websiteroute.architecture;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class PaymentArchitectureTest {
    private static final Path PAYMENT_SOURCE = Path.of("src/main/java/com/websiteroute/payment");
    private static final Pattern IMPORT = Pattern.compile(
            "(?m)^\\s*import\\s+(?:static\\s+)?([^;\\s]+)\\s*;");
    private static final List<String> FORBIDDEN_APPLICATION_IMPORTS = List.of(
            "com.websiteroute.payment.adapter.",
            "com.websiteroute.presentation.",
            "org.springframework.web.",
            "org.springframework.http.");

    @Test
    void domainSourcesImportOnlyJavaPackages() throws IOException {
        for (Path source : javaSourcesIn("domain")) {
            assertThat(importsIn(source))
                    .as("Domain imports in %s", source)
                    .allMatch(importedType -> importedType.startsWith("java."));
        }
    }

    @Test
    void applicationSourcesDoNotImportAdaptersOrWebTransportTypes() throws IOException {
        for (Path source : javaSourcesIn("application")) {
            assertThat(importsIn(source))
                    .as("Application imports in %s", source)
                    .noneMatch(importedType -> FORBIDDEN_APPLICATION_IMPORTS.stream()
                            .anyMatch(importedType::startsWith));
        }
    }

    private static List<Path> javaSourcesIn(String layer) throws IOException {
        Path directory = PAYMENT_SOURCE.resolve(layer);
        assertThat(directory).as("Payment %s source directory", layer).isDirectory();
        List<Path> sources;
        try (Stream<Path> paths = Files.walk(directory)) {
            sources = paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .sorted()
                    .toList();
        }
        assertThat(sources).as("Java sources in %s", directory).isNotEmpty();
        return sources;
    }

    private static List<String> importsIn(Path source) throws IOException {
        return IMPORT.matcher(Files.readString(source)).results()
                .map(match -> match.group(1))
                .toList();
    }
}

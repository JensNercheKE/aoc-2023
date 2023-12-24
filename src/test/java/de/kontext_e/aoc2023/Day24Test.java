package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class Day24Test {
    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day23test.txt");
        var lines = Files.readAllLines(path);
    }
}

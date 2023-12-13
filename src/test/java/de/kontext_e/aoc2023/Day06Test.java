package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day06Test {
    @Test
    void test() throws IOException {
        final var result = processFile("src/test/resources/day06test.txt", this::parseLine);

        assertEquals(288, result);
    }

    @Test
    void testWithKerning() throws IOException {
        final var result = processFile("src/test/resources/day06test.txt", this::parseLineWithKerning);

        assertEquals(71503, result);
    }

    @Test
    void testWithInput() throws IOException {
        final var result = processFile("src/test/resources/day06input.txt", this::parseLine);

        assertEquals(2756160, result);
    }

    @Test
    void testWithKerningWithInput() throws IOException {
        final var result = processFile("src/test/resources/day06input.txt", this::parseLineWithKerning);

        assertEquals(34788142, result);
    }

    private long processFile(String pathString, Function<String, List<Long>> f) throws IOException {
        Path path = Paths.get(pathString);
        var lines = Files.readAllLines(path);
        List<Long> times = f.apply(lines.get(0));
        List<Long> distances = f.apply(lines.get(1));

        long result = 1;
        for (int i = 0; i < times.size(); i++) {
            var waysToWin = calculateWaysToWin(times.get(i), distances.get(i));
            result *= waysToWin;
        }
        return result;
    }

    private long calculateWaysToWin(Long time, Long currentRecord) {
        long ways = 0;

        for (long buttonHoldTime = 1; buttonHoldTime < time; buttonHoldTime++) {
            var distance = calculateDistance(buttonHoldTime, time);
            if (distance > currentRecord) {
                ways++;
            }
        }

        return ways;
    }

    private long calculateDistance(long buttonHoldTime, Long time) {
        var travelTime = time - buttonHoldTime;
        var distance = travelTime * buttonHoldTime;
        return distance;
    }

    private List<Long> parseLine(String line) {
        List<Long> numbers = new ArrayList<>();
        var numberPart = line.substring(line.indexOf(":") + 1);
        final var splitted = numberPart.split(" ");
        for (String s : splitted) {
            if (s != null && s.isEmpty() == false) {
                numbers.add(Long.parseLong(s));
            }
        }

        return numbers;
    }

    private List<Long> parseLineWithKerning(String line) {
        List<Long> numbers = new ArrayList<>();
        var numberPart = line.substring(line.indexOf(":") + 1);
        final var kerned = numberPart.replaceAll(" ","");
        numbers.add(Long.parseLong(kerned));
        return numbers;
    }
}

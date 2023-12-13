package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day09Test {

    @Test
    void test() throws IOException {
        Path path = Paths.get("src/test/resources/day09test.txt");
        var lines = Files.readAllLines(path);

        long sum = 0;
        for (String line : lines) {
            sum += extrapolate(line);
        }

        assertEquals(2, sum);
    }

    @Test
    void testInput() throws IOException {
        Path path = Paths.get("src/test/resources/day09input.txt");
        var lines = Files.readAllLines(path);

        long sum = 0;
        for (String line : lines) {
            sum += extrapolate(line);
        }

        assertEquals(1062, sum);
    }

    @Test
    void testExtrapolate() {
        final var history = "10  13  16  21  30  45";
        var extrapolated = extrapolate(history);
        assertEquals(5, extrapolated);
    }

    @Test
    void testExtrapolate1() {
        final var history = "7 15 32 57 98 176 332 653 1352 2972 6842 16010 37046 83402 181521 381725 777249 1536841 2959392 5563435 10230470";
        var extrapolated = extrapolate(history);
        assertEquals(18429424, extrapolated);
    }

    private long extrapolate(String history) {
        var iterations = new ArrayList<List<Long>>();

        var current = toLongs(history);
        iterations.add(current);

        while(true) {
            current = diffs(current);
            iterations.add(current);
            if (allZeros(current)) {
                break;
            }
        }


        var valueToAppend = 0L;
        for (int i = iterations.size() - 2; i>= 0; i--) {
            var iteration = iterations.get(i);
            var firstOfIteration = iteration.get(0);
            valueToAppend = firstOfIteration - valueToAppend;
            iteration.add(0, valueToAppend);
        }

        return valueToAppend;
    }

    private boolean allZeros(List<Long> next) {
        for (Long l : next) {
            if (l != 0) {
                return false;
            }
        }
        return true;
    }

    private List<Long> diffs(List<Long> inputs) {
        List<Long> longs = new ArrayList<>();
        for (int i = 0; i < inputs.size() - 1; i++) {
            longs.add(inputs.get(i + 1) - inputs.get(i));
        }
        return longs;
    }

    public static List<Long> toLongs(String input) {
        List<Long> longs = new ArrayList<>();
        var splitted = input.split(" ");
        for (var split : splitted) {
            if (split != null && split.isEmpty() == false) {
                longs.add(Long.parseLong(split));
            }
        }
        return longs;
    }
}

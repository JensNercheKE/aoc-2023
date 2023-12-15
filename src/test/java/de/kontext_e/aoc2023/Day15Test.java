package de.kontext_e.aoc2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day15Test {
    private final List<Box> boxes = new ArrayList<>();

    @BeforeEach
    void before() {
        boxes.clear();
        for (int i = 0; i < 256; i++) {
            boxes.add(new Box(i+1));
        }
    }

    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day15test.txt");
        var lines = Files.readAllLines(path);

        final var sum = calculateOverallHash(lines);
        final var focusingPower = calculateFocusingPower();

        assertEquals(1320, sum);
        assertEquals(145, focusingPower);
    }

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day15input.txt");
        var lines = Files.readAllLines(path);

        final var sum = calculateOverallHash(lines);
        final var focusingPower = calculateFocusingPower();

        assertEquals(514025, sum);
        assertEquals(244461, focusingPower);
    }

    @Test
    void testHash() {
        var h = hash("HASH");
        assertEquals(52, h);
    }

    @Test
    void testGetLabel() {
        assertEquals("rrc", getLabel("rrc=7"));
        assertEquals("khxvh", getLabel("khxvh-"));
    }

    private long calculateOverallHash(List<String> lines) {
        var sequence = lines.get(0);
        var splitted = sequence.split(",");
        var sum = 0L;
        for (String s : splitted) {
            sum += hash(s);

            var label = getLabel(s);
            var labelHash = hash(label);
            var box = boxes.get(labelHash);
            box.performOperation(s);
        }
        return sum;
    }

    private long calculateFocusingPower() {
        var sum = 0L;
        for (var box : boxes) {
            sum += box.calculateFocusingPower();
        }
        return sum;
    }

    private String getLabel(String in) {
        var end = in.indexOf("=");
        if (end == -1) {
            end = in.indexOf("-");
        }
        return in.substring(0, end);
    }

    private int hash(String s) {
        var hash = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            hash += c;
            hash *= 17;
            hash %= 256;
        }
        return hash;
    }

    private class Box {
        final List<String> lenses = new LinkedList<>();
        private final int boxNumber;

        private Box(int boxNumber) {
            this.boxNumber = boxNumber;
        }

        public void performOperation(String in) {
            var operation = "";
            if (in.contains("=")) {
                operation = "=";
            }
            if (in.contains("-")) {
                operation = "-";
            }

            if ("=".equals(operation)) {
                final var pos = position(getLabel(in));
                if (pos != -1) {
                    lenses.remove(pos);
                    lenses.add(pos, in);
                } else {
                    lenses.add(in);
                }
            }

            if ("-".equals(operation)) {
                final var pos = position(getLabel(in));
                if (pos != -1) {
                    lenses.remove(pos);
                }
            }
        }

        private int position(String label) {
            for (int i = 0; i < lenses.size(); i++) {
                if (getLabel(lenses.get(i)).equals(label)) {
                    return i;
                }
            }

            return -1;
        }

        public long calculateFocusingPower() {
            long sum = 0L;

            for (int i = 0; i < lenses.size(); i++) {
                var lensPower = boxNumber * (i + 1) * getFocalLength(lenses.get(i));
                sum += lensPower;
            }

            return sum;
        }

        private int getFocalLength(String lens) {
            var fl = lens.substring(lens.indexOf("=")+1);
            return Integer.parseInt(fl);
        }

        @Override
        public String toString() {
            return lenses.toString();
        }
    }
}

package de.kontext_e.aoc2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Day24Test {
    private final List<Hailstone> hailstones = new ArrayList<>();
    private TestArea testArea;

    @BeforeEach
    void setUp() {
        hailstones.clear();
    }

    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day24test.txt");
        var lines = Files.readAllLines(path);
        toHailstones(lines);
        testArea = new TestArea(7, 27);
        var result = countPathsCross();
        assertEquals(2L, result);
    }

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day24input.txt");
        var lines = Files.readAllLines(path);
        toHailstones(lines);
        testArea = new TestArea(200000000000000L, 400000000000000L);
        var result = countPathsCross();

        assertEquals(31921L, result);
    }

    private long countPathsCross() {
        long result = 0;

        for (int i = 0; i < hailstones.size() - 1; i++) {
            for (int k = i + 1; k < hailstones.size(); k++) {
                if (hailstones.get(i).isInTestArea(hailstones.get(k))) {
                    result++;
                }
            }
        }

        return result;
    }

    private void toHailstones(List<String> lines) {
        for (var line : lines) {
            hailstones.add(new Hailstone(line));
        }
        System.out.println("# hailstones: "+hailstones.size());
    }

    @Test
    void parse() {
        var hailstoneA = new Hailstone("19, 13, 30 @ -2,  1, -2");
        var hailstoneB = new Hailstone("18, 19, 22 @ -1, -1, -2");

        assertEquals(-0.5, hailstoneA.a);
        assertEquals(22.5, hailstoneA.b);
        assertEquals(1.0, hailstoneB.a);
        assertEquals(1.0, hailstoneB.b);

        var intersection = hailstoneA.intersect(hailstoneB);
        assertEquals(14.333, intersection.x, 0.001);
        assertEquals(15.333, intersection.y, 0.001);

        assertTrue(hailstoneA.isInFuture(intersection));
        assertTrue(hailstoneB.isInFuture(intersection));
    }

    @ParameterizedTest
    @CsvSource({
            "'19, 13, 30 @ -2, 1, -2', '20, 19, 15 @ 1, -5, -3', false, true",
            "'19, 13, 30 @ -2, 1, -2', '18, 19, 22 @ -1, -1, -2', true, true",
            "'20, 25, 34 @ -2, -2, -4', '20, 19, 15 @ 1, -5, -3', true, false",
            "'18, 19, 22 @ -1, -1, -2', '20, 19, 15 @ 1, -5, -3', false, false"
    })
    void testIntersections(String a, String b, boolean expectedA, boolean expectedB) {
        var hailstoneA = new Hailstone(a);
        var hailstoneB = new Hailstone(b);
        var intersection = hailstoneA.intersect(hailstoneB);
        assertEquals(expectedA, hailstoneA.isInFuture(intersection));
        assertEquals(expectedB, hailstoneB.isInFuture(intersection));
    }

    @ParameterizedTest
    @CsvSource({
            "'19, 13, 30 @ -2, 1, -2', '20, 19, 15 @ 1, -5, -3', false",
            "'18, 19, 22 @ -1, -1, -2', '20, 25, 34 @ -2, -2, -4', true"
    })
    void isParallel(String a, String b, boolean expectedResult) {
        var hailstoneA = new Hailstone(a);
        var hailstoneB = new Hailstone(b);

        assertEquals(expectedResult, hailstoneA.isParallel(hailstoneB));
    }

    @ParameterizedTest
    @CsvSource({
            "'19, 13, 30 @ -2, 1, -2', '18, 19, 22 @ -1, -1, -2', true",
            "'19, 13, 30 @ -2, 1, -2', '20, 25, 34 @ -2, -2, -4', true",
            "'19, 13, 30 @ -2, 1, -2', '12, 31, 28 @ -1, -2, -1', false",
            "'19, 13, 30 @ -2, 1, -2', '20, 19, 15 @ 1, -5, -3', false",
            "'18, 19, 22 @ -1, -1, -2', '20, 25, 34 @ -2, -2, -4', false",
    })
    void isInTestArea(String a, String b, boolean expectedResult) {
        var hailstoneA = new Hailstone(a);
        var hailstoneB = new Hailstone(b);
        testArea = new TestArea(7, 27);

        assertEquals(expectedResult, hailstoneA.isInTestArea(hailstoneB));
    }

    private class Hailstone {
        Position position;
        Velocity velocity;
        double a;
        double b;

        public Hailstone(String line) {
            var parts = line.split("@");
            createPostion(parts[0]);
            createVelocity(parts[1]);
            a = (double) velocity.vy/velocity.vx;
            b = (double) position.y - a * position.x;
        }

        private void createPostion(String coordinates) {
            var splitted =  coordinates.split(",");
            var x = Long.parseLong(splitted[0].trim());
            var y = Long.parseLong(splitted[1].trim());
            var z = Long.parseLong(splitted[2].trim());
            position = new Position(x, y, z);
        }
        private void createVelocity(String coordinates) {
            var splitted =  coordinates.split(",");
            var x = Integer.parseInt(splitted[0].trim());
            var y = Integer.parseInt(splitted[1].trim());
            var z = Integer.parseInt(splitted[2].trim());
            velocity = new Velocity(x, y, z);
        }

        public Intersection intersect(Hailstone other) {
            double x = this.a - other.a;
            double y = other.b - this.b;
            x = y / x;
            y = a * x + b;

            return new Intersection(x, y);
        }

        public boolean isInFuture(Intersection intersection) {
            if(intersection == null) return false;
            if(intersection.x > position.x && velocity.vx > 0) return true;
            if(intersection.x < position.x && velocity.vx < 0) return true;
            if(intersection.x == position.x && intersection.y == position.y) return true;
            return false;
        }

        public boolean isParallel(Hailstone other) {
            return (double)this.velocity.vx / other.velocity.vx == (double)this.velocity.vy / other.velocity.vy;
        }

        public boolean isInTestArea(Hailstone other) {
            if(isParallel(other)) return false;

            var intersection = intersect(other);
            if(isInFuture(intersection) == false) return false;
            if(other.isInFuture(intersection) == false) return false;

            if(testArea == null) throw new IllegalStateException("testArea is null");

            return testArea.contains(intersection);
        }
    }

    record Position(long x, long y, long z) {}
    record Velocity(int vx, int vy, int vz) {}
    record Intersection(double x, double y) {}

    record TestArea(long min, long max) {
        public boolean contains(Intersection intersection) {
            return intersection.x >= min
                    && intersection.x <= max
                    && intersection.y >= min
                    && intersection.y <= max;
        }
    }
}


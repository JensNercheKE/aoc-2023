package de.kontext_e.aoc2019;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Day18Test {
    static int width = 0;
    static int height = 0;

    @Test
    void testAnsiEscapeSequences() {
        System.out.println("\033[31;1mHello\033[0m");
    }

    @Test
    void intro() {
        // coordinates of all keys
        // steps between all pairs of keys
        // keys blocked by doors
        // find all possible paths
        // select path with the shortest length

        var lines = Arrays.stream(example1.split("\n")).map(String::trim).toList();
        var field = toField(lines);
        printField(field);
    }

    private void printField(char[][] field) {
        for(int y = 0; y < field[0].length; y++) {
            for (int x = 0; x < field.length; x++) {
                System.out.print(field[x][y]);
            }
            System.out.println("");
        }
    }

    private char[][] toField(List<String> lines) {
        width = lines.get(0).length();
        height = lines.size();
        final var field = new char[width][height];
        for (int y = 0; y < height; y++) {
            var line = lines.get(y);
            for (int x = 0; x < width; x++) {
                field[x][y] = line.charAt(x);
            }
        }
        return field;
    }

    private record Coordinate(long x, long y) {
        private static int scale = 1;

        public List<Coordinate> getNeighbors() {
            List<Coordinate> neighbors = new ArrayList<>();
            if(x > 0) neighbors.add(new Coordinate(x - scale, y));
            if(x < width - scale) neighbors.add(new Coordinate(x + scale, y));
            if(y > 0) neighbors.add(new Coordinate(x, y - scale));
            if(y < height - scale) neighbors.add(new Coordinate(x, y + scale));
            return neighbors;
        }
        public boolean isWest(Coordinate neighbor) {
            return y == neighbor.y && x == neighbor.x + scale;
        }

        public boolean isEast(Coordinate neighbor) {
            return y == neighbor.y && x == neighbor.x - scale;
        }

        public boolean isNorth(Coordinate neighbor) {
            return x == neighbor.x && y == neighbor.y + scale;
        }

        public boolean isSouth(Coordinate neighbor) {
            return x == neighbor.x && y == neighbor.y - scale;
        }

    }


    String example1 =
            """
                    #########
                    #b.A.@.a#
                    #########
                    """;
}

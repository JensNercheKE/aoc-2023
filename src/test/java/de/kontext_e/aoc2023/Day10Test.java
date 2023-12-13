package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day10Test {
    private Coordinate start = new Coordinate(-1,-1);
    private static int width = 0;
    private static int height = 0;
    private long count = 0;
    private Character[][] field;

    @Test
    void test() throws IOException {
        Path path = Paths.get("src/test/resources/day10test.txt");
        var lines = Files.readAllLines(path);

        start = findStart(lines);
        width = lines.get(0).length();
        height = lines.size();
        field = new Character[width][height];

        var length = follow(lines);
        var distance = length/2;
        assertEquals(23, distance);

        markReachableTilesFromOutside();
        printField(field);
    }

    private void markReachableTilesFromOutside() {
        var maxIterations = Math.max(width, height) * 2;
        for(int iteration = 0; iteration < maxIterations; iteration++) {
            {
                // oben
                int y = iteration;
                for (int x = iteration; x < width - iteration; x++) {
                    if (field[x][y] == null && isReachable(x, y)) {
                        field[x][y] = 'O';
                    }
                }
            }

            {
                // unten
                int y = height - 1 - iteration;
                for (int x = iteration; x < width - iteration; x++) {
                    if (field[x][y] == null && isReachable(x, y)) {
                        field[x][y] = 'O';
                    }
                }
            }

            {
                // links
                int x = iteration;
                for (int y = iteration; y < height - iteration; y++) {
                    if (field[x][y] == null && isReachable(x, y)) {
                        field[x][y] = 'O';
                    }
                }
            }

            {
                // rechts
                int x = width - 1 - iteration;
                for (int y = iteration; y < height - iteration; y++) {
                    if (field[x][y] == null && isReachable(x, y)) {
                        field[x][y] = 'O';
                    }
                }
            }
        }

    }

    private boolean isReachable(int x, int y) {
        if(x == 0) return true;
        if(x == width - 1) return true;
        if(y == 0) return true;
        if(y == height - 1) return true;

        var current = new Coordinate(x, y);
        var neighbors = current.getNeighbors();
        for (Coordinate neighbor : neighbors) {
            final var tile = field[(int) neighbor.x()][(int) neighbor.y()];
            if (tile != null && tile == 'O') {
                return true;
            }
        }


        return false;
    }

    @Test
    void testWithInput() throws IOException {
        Path path = Paths.get("src/test/resources/day10input.txt");
        var lines = Files.readAllLines(path);

        start = findStart(lines);
        width = lines.get(0).length();
        height = lines.size();

        var length = follow(lines);
        var distance = length/2;
        assertEquals(6897, distance);
    }

    private long follow(List<String> lines) {
        Coordinate previous = new Coordinate(-1,-1);
        Coordinate current = start;

        while(true) {
            // one step
            var neighbors = current.getNeighbors();
            for (Coordinate neighbor : neighbors) {
                if (isConnected(current, neighbor, lines) && neighbor.equals(previous) == false) {
                    field[(int) current.x()][(int) current.y()] = pipeAt(current, lines);
                    count++;
                    previous = current;
                    current = neighbor;
                    break;
                }
            }
            if (current.equals(start)) {
                return count;
            }
        }
    }

    private boolean isConnected(Coordinate current, Coordinate neighbor, List<String> lines) {
        var currentPipe = pipeAt(current, lines);
        var neighborPipe = pipeAt(neighbor, lines);
        return isCompatible(current, neighbor, currentPipe, neighborPipe);
    }

    private boolean isCompatible(Coordinate current, Coordinate neighbor, Character currentPipe, Character neighborPipe) {
        if (current.isWest(neighbor)
                && connectsWest(currentPipe)
                && neighbor.isEast(current)
                && connectsEast(neighborPipe)) {
            return true;
        }
        if (current.isEast(neighbor)
                && connectsEast(currentPipe)
                && neighbor.isWest(current)
                && connectsWest(neighborPipe)) {
            return true;
        }
        if (current.isNorth(neighbor)
                && connectsNorth(currentPipe)
                && neighbor.isSouth(current)
                && connectsSouth(neighborPipe)) {
            return true;
        }
        if (current.isSouth(neighbor)
                && connectsSouth(currentPipe)
                && neighbor.isNorth(current)
                && connectsNorth(neighborPipe)) {
            return true;
        }

        return false;
    }

    private boolean connectsEast(Character pipe) {
        if(pipe.equals('-')) return true;
        if(pipe.equals('F')) return true;
        if(pipe.equals('L')) return true;
        if(pipe.equals('S')) return true;
        return false;
    }

    private boolean connectsWest(Character pipe) {
        if(pipe.equals('-')) return true;
        if(pipe.equals('J')) return true;
        if(pipe.equals('7')) return true;
        if(pipe.equals('S')) return true;
        return false;
    }
    private boolean connectsNorth(Character pipe) {
        if(pipe.equals('|')) return true;
        if(pipe.equals('J')) return true;
        if(pipe.equals('L')) return true;
        if(pipe.equals('S')) return true;
        return false;
    }
    private boolean connectsSouth(Character pipe) {
        if(pipe.equals('|')) return true;
        if(pipe.equals('F')) return true;
        if(pipe.equals('7')) return true;
        if(pipe.equals('S')) return true;
        return false;
    }
    private Character pipeAt(Coordinate coordinate, List<String> lines) {
        var line = lines.get((int) coordinate.y());
        return line.charAt((int) coordinate.x());
    }

    private Coordinate findStart(List<String> lines) {
        long lineNo = 0L;
        for (String line : lines) {
            var index = line.indexOf('S');
            if (index != -1) {
                return new Coordinate(index, lineNo);
            }
            lineNo++;
        }

        return null;
    }

    private record Coordinate(long x, long y) {

        public List<Coordinate> getNeighbors() {
            List<Coordinate> neighbors = new ArrayList<>();
            if(x > 0) neighbors.add(new Coordinate(x - 1, y));
            if(x < width - 1) neighbors.add(new Coordinate(x + 1, y));
            if(y > 0) neighbors.add(new Coordinate(x, y - 1));
            if(y < height - 1) neighbors.add(new Coordinate(x, y + 1));
            return neighbors;
        }
        public boolean isWest(Coordinate neighbor) {
            return y == neighbor.y && x == neighbor.x + 1;
        }

        public boolean isEast(Coordinate neighbor) {
            return y == neighbor.y && x == neighbor.x - 1;
        }

        public boolean isNorth(Coordinate neighbor) {
            return x == neighbor.x && y == neighbor.y + 1;
        }

        public boolean isSouth(Coordinate neighbor) {
            return x == neighbor.x && y == neighbor.y - 1;
        }

    }

    private void printField(Character[][] field) {
        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (field[x][y] == null) {
                    System.out.print(".");
                } else {
                    System.out.print(field[x][y]);
                }
            }
            System.out.println("");
        }
    }
}

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

class Day23Test {
    char[][] field;
    int[][] maxForTiles;
    private int width;
    private int height;
    private Coordinate start;
    private Coordinate end;
    private final List<Hike> hikes = new ArrayList<>();
    private int maxStepsAtEnd = 0;

    @BeforeEach
    void setUp() {
        width = 0;
        height = 0;
        start = new Coordinate(-1, -1);
        end = new Coordinate(-1, -1);
        hikes.clear();
        maxStepsAtEnd = 0;
    }

    // (1,0) (1,1) (2,1) (3,1) (4,1) (5,1) (6,1) (7,1) (7,2) (7,3) (6,3) (5,3) (4,3) (3,3) (3,4) (3,5) (4,5) (5,5) (6,5) (7,5) (7,6) (7,7) (7,8) (7,9) (7,10) (7,11) (8,11) (9,11)
    // (9,10) (9,9) (9,8) (9,7) (9,6) (9,5) (9,4) (9,3) (10,3) (11,3) (11,4) (11,5) (11,6) (11,7) (12,7) (13,7) (14,7) (15,7) (16,7) (17,7) (17,8) (17,9) (16,9) (15,9) (14,9) (13,9)
    // (12,9) (11,9) (11,10) (11,11) (12,11) (13,11) (13,12) (13,13) (12,13) (11,13) (10,13) (9,13) (9,14) (9,15) (8,15) (7,15) (7,14) (7,13) (6,13) (5,13) (5,12) (5,11) (4,11) (3,11)
    // (3,12) (3,13) (2,13) (1,13) (1,12) (1,11) (1,10) (1,9) (2,9) (3,9) (4,9) (5,9) (5,8) (5,7) (4,7) (3,7) (3,6)
    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day23test.txt");
        var lines = Files.readAllLines(path);
        toField(lines);
        determineStartAndEnd();

        walkMaze();

        printField();
        var max = maxStepsAtEnd - 1;
        assertEquals(154, max);
    }


    // max so far 5695
    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day23input.txt");
        var lines = Files.readAllLines(path);
        toField(lines);
        determineStartAndEnd();

        walkMaze();

        var max = maxStepsAtEnd - 1;
        assertEquals(2106, max);
    }

    private void walkMaze() {
        hikes.add(new Hike(start.x, start.y));
        var numberOfParallel = 20;
        while(hikes.size() > 0) {
            final var hikesCopy = new ArrayList<>(hikes);
            for (int i = 0; i < Math.min(numberOfParallel, hikesCopy.size()); i++) {
                var hike = hikesCopy.get(i);
                hike.step();
            }
            checkCopies();
        }
    }

    private void checkCopies() {
        final var hikesCopy = new ArrayList<>(hikes);
        for (Hike hike : hikesCopy) {
            hike.checkIfStillNeeded();
        }

    }

    private class Hike {
        int x;
        int y;
        List<Coordinate> steps = new ArrayList<>();

        public Hike(long x, long y) {
            this.x = (int) x;
            this.y = (int) y;
            steps.add(new Coordinate(x, y));
        }

        public void checkIfStillNeeded() {
            if (steps.size() < maxForTiles[x][y]) {
                //System.out.printf("Hiker (%d,%d) cannot win anymore, remove\n", x, y);
                hikes.remove(this);
            }
        }

        public void step() {
            if (steps.size() < maxForTiles[x][y]) {
                //System.out.printf("Hiker (%d,%d) cannot win anymore, remove\n", x, y);
                hikes.remove(this);
                return;
            }

            if(end.equals(x, y)) {
                System.out.println("Hiker reach end. Steps: "+steps.size()+" max: "+maxStepsAtEnd+" alive hikers "+hikes.size());
                if (steps.size() > maxStepsAtEnd) {
                    maxStepsAtEnd = steps.size();

                    var counter = 0;
                    for (Coordinate step : steps) {
                        maxForTiles[(int) step.x][(int) step.y] = counter;
                        counter++;
                    }

                }
                hikes.remove(this);
                return;
            }

            var options = countOptions();
            if (options.size() == 0) {
                //System.out.printf("Hiker (%d,%d) reached dead end\n", x, y);
                //printSteps();
                hikes.remove(this);
                return;
            }
            if (options.size() == 1) {
                var o = options.get(0);
                this.x = (int) o.x;
                this.y = (int) o.y;
                steps.add(o);
            }
            if (options.size() > 1) {
                for (int i = 1; i < options.size(); i++) {
                    hikes.add(copy(options.get(i)));
                }

                var o = options.get(0);
                this.x = (int) o.x;
                this.y = (int) o.y;
                steps.add(o);
            }

            //System.out.printf("Hiker (%d,%d): Steps until here: %d\n", x, y, steps.size());
        }

        private void printSteps() {
            for (Coordinate step : steps) {
                System.out.printf("(%d,%d) ", step.x, step.y);
            }
            System.out.println();
        }

        private Hike copy(Coordinate coordinate) {
            final var hike = new Hike(coordinate.x, coordinate.y);
            hike.steps.addAll(0, this.steps);
            return hike;
        }

        private List<Coordinate> countOptions() {
            List<Coordinate> options = new LinkedList<>();
            checkNorth(options);
            checkEast(options);
            checkSouth(options);
            checkWest(options);
            return options;
        }

        private void checkNorth(List<Coordinate> options) {
            if(y == 0) return;
            int oy = y - 1;
            if(field[x][oy] == '#') return;
            //if(field[x][oy] == 'v') return;
            final var coordinate = new Coordinate(x, oy);
            if(steps.contains(coordinate)) return;
            options.add(coordinate);
        }

        private void checkSouth(List<Coordinate> options) {
            if(y == height - 1) return;
            int oy = y + 1;
            if(field[x][oy] == '#') return;
            //if(field[x][oy] == '^') return;
            final var coordinate = new Coordinate(x, oy);
            if(steps.contains(coordinate)) return;
            options.add(coordinate);
        }

        private void checkEast(List<Coordinate> options) {
            if(x == width - 1) return;
            int ox = x + 1;
            if(field[ox][y] == '#') return;
            //if(field[ox][y] == '<') return;
            final var coordinate = new Coordinate(ox, y);
            if(steps.contains(coordinate)) return;
            options.add(coordinate);
        }

        private void checkWest(List<Coordinate> options) {
            if(x == 0) return;
            int ox = x - 1;
            if(field[ox][y] == '#') return;
            //if(field[ox][y] == '>') return;
            final var coordinate = new Coordinate(ox, y);
            if(steps.contains(coordinate)) return;
            options.add(coordinate);
        }

        public boolean isCurrentPosition(int x, int y) {
            return this.x == x && this.y == y;
        }
    }

    private void determineStartAndEnd() {
        for (int x = 0; x < width; x++) {
            if (field[x][0] == '.') {
                start = new Coordinate(x, 0);
            }
        }
        for (int x = 0; x < width; x++) {
            if (field[x][height-1] == '.') {
                end = new Coordinate(x, height-1);
            }
        }
    }


    private void printField() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(start.equals(new Coordinate(x, y))) {
                    System.out.print('S');
                } else if(end.equals(new Coordinate(x, y))) {
                    System.out.print('E');
                } else if(locatesHiker(x, y)) {
                    System.out.print('H');
                } else {
                    System.out.print(field[x][y]);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private boolean locatesHiker(int x, int y) {
        for (Hike hike : hikes) {
            if(hike.isCurrentPosition(x, y)) return true;
        }

        return false;
    }

    private void toField(List<String> lines) {
        width = lines.get(0).length();
        height = lines.size();
        field = new char[width][height];
        maxForTiles = new int[width][height];

        for (int y = 0; y < height; y++) {
            var line = lines.get(y);
            for (int x = 0; x < width; x++) {
                field[x][y] = line.charAt(x);
            }
        }
    }

    private record Coordinate(long x, long y) {
        public boolean equals(int x, int y) {
            return this.x == x && this.y == y;
        }
    }
    
}

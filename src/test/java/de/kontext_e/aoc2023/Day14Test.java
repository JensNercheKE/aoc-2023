package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day14Test {
    char[][] field;
    private int width;
    private int height;

    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day14test.txt");
        var lines = Files.readAllLines(path);
        width = lines.get(0).length();
        height = lines.size();
        field = new char[width][height];
        toField(lines);
        tiltNorth();
        var load = calculateLoad();
        assertEquals(136, load);

        var totalLoad = spinCycle();
        assertEquals(64, totalLoad);
    }

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day14input.txt");
        var lines = Files.readAllLines(path);
        width = lines.get(0).length();
        height = lines.size();
        field = new char[width][height];
        toField(lines);
        tiltNorth();
        var load = calculateLoad();
        assertEquals(107142, load);

        var totalLoad = spinCycle();
        assertEquals(104815, totalLoad);
    }

    private long calculateLoad() {
        long load = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (field[x][y] == 'O') {
                    load += height - y;
                }
            }
        }

        return load;
    }

    private long spinCycle() {
        for(int i = 0; i < 1000; i++) {
            tiltNorth();
            tiltWest();
            tiltSouth();
            tiltEast();
        }

        return calculateLoad();
    }

    private void tiltNorth() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rollNorth(x, y);
            }
        }
    }
    private void tiltWest() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rollWest(x, y);
            }
        }
    }
    private void tiltSouth() {
        for (int y = height-1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                rollSouth(x, y);
            }
        }
    }
    private void tiltEast() {
        for (int y = 0; y < height; y++) {
            for (int x = width-1; x >= 0; x--) {
                rollEast(x, y);
            }
        }
    }

    private void rollNorth(int x, int y) {
        if(field[x][y] != 'O') return;
        for (int i = y; i > 0; i--) {
            if (field[x][i - 1] == '.') {
                field[x][i - 1] = 'O';
                field[x][i] = '.';
            } else {
                return;
            }
        }
    }
    private void rollWest(int x, int y) {
        if(field[x][y] != 'O') return;
        for (int i = x; i > 0; i--) {
            if (field[i-1][y] == '.') {
                field[i-1][y] = 'O';
                field[i][y] = '.';
            } else {
                return;
            }
        }
    }
    private void rollSouth(int x, int y) {
        if(field[x][y] != 'O') return;
        for (int i = y; i < height-1; i++) {
            if (field[x][i + 1] == '.') {
                field[x][i + 1] = 'O';
                field[x][i] = '.';
            } else {
                return;
            }
        }
    }
    private void rollEast(int x, int y) {
        if(field[x][y] != 'O') return;
        for (int i = x; i < width-1; i++) {
            if (field[i+1][y] == '.') {
                field[i+1][y] = 'O';
                field[i][y] = '.';
            } else {
                return;
            }
        }
    }

    private void printField() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(field[x][y]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private void toField(List<String> lines) {
        for (int y = 0; y < height; y++) {
            var line = lines.get(y);
            for (int x = 0; x < width; x++) {
                field[x][y] = line.charAt(x);
            }
        }
    }
}

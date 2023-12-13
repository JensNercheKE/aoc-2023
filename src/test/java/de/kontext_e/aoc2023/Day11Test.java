package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day11Test {
    static char[][] universe;
    int width;
    int height;
    static int expanseFactor = 1000000;

    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day11test.txt");
        var lines = Files.readAllLines(path);
        var image = wrap(lines);

        width = lines.get(0).length();
        height = image.size();

        universe = expand(image);
        var galaxies = findGalaxies(universe);

        long sum = 0;
        for (int i = 0; i < galaxies.size() - 1; i++) {
            for (int k = i + 1; k < galaxies.size(); k++) {
                sum += galaxies.get(i).pathTo(galaxies.get(k));
            }
        }

        assertEquals(82000210, sum);
    }

    @Test
    void testInput() throws IOException {
        Path path = Paths.get("src/test/resources/day11input.txt");
        var lines = Files.readAllLines(path);
        var image = wrap(lines);

        width = lines.get(0).length();
        height = image.size();

        universe = expand(image);
        var galaxies = findGalaxies(universe);

        long sum = 0;
        for (int i = 0; i < galaxies.size() - 1; i++) {
            for (int k = i + 1; k < galaxies.size(); k++) {
                sum += galaxies.get(i).pathTo(galaxies.get(k));
            }
        }

        assertEquals(746207878188L, sum);
    }

    private List<Coordinate> findGalaxies(char[][] universe) {
        List<Coordinate> galaxies = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (universe[x][y] == '#') {
                    galaxies.add(new Coordinate(x, y));
                }
            }
        }

        return galaxies;
    }

    char[][] expand(List<StringWrapper> lines) {
        char[][] universe = new char[width][height];

        // expand vertically
        int y = 0;
        for (var line : lines) {
            if (isEmpty(line)) {
                for (int x = 0; x < width; x++) {
                    universe[x][y] = '-';
                }
            } else {
                for (int x = 0; x < width; x++) {
                    universe[x][y] = line.charAt(x);
                }
            }
            y++;
        }

        // expand horizontically
        for (int x = 0; x < width; x++) {
            if (isEmpty(x, lines)) {
                for (y = 0; y < height; y++) {
                    if(universe[x][y] == '-') universe[x][y] = 'x';
                    else universe[x][y] = '|';
                }
                x++;
            }
        }

        return universe;
    }

    boolean isEmpty(int col, List<StringWrapper> lines) {
        for (var line : lines) {
            if(line.charAt(col) != '.') return false;
        }

        return true;
    }

    boolean isEmpty(StringWrapper row) {
        for (int i = 0; i < row.length(); i++) {
            if(row.charAt(i) != '.') return false;
        }
        return true;
    }

    List<StringWrapper> wrap(List<String> lines) {
        List<StringWrapper> wrapped = new ArrayList<>();
        for (String line : lines) {
            wrapped.add(new StringWrapper(line));
        }
        return wrapped;
    }

    @Test
    void testWrapperInsert() {
        var wrapped = new StringWrapper("abc");

        wrapped.insert(1, '1');

        assertEquals("a1bc", wrapped.toString());
    }

    @Test
    void testWrapperReplace() {
        var wrapped = new StringWrapper("abc");

        wrapped.replace(2, '1');

        assertEquals("ab1", wrapped.toString());
    }

    private record Coordinate(long x, long y) {
        public long pathTo(Coordinate coordinate) {
            long distance = 0;
            int deltaX = 1;
            int deltaY = 1;
            if(coordinate.x < x) deltaX = -1;
            if(coordinate.y < y) deltaX = -1;

            var currentX = x;
            while(currentX != coordinate.x) {
                currentX += deltaX;
                char c = universe[(int) currentX][(int) y];
                if (c == '|') distance += expanseFactor;
                else distance += 1;
            }

            var currentY = y;
            while (currentY != coordinate.y) {
                currentY += deltaY;
                char c = universe[(int) currentX][(int) currentY];
                if (c == '-') distance += expanseFactor;
                else distance += 1;
            }

            return distance;
        }
    }

    private void printField(char[][] field) {
        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(field[x][y]);
            }
            System.out.println("");
        }
    }

}

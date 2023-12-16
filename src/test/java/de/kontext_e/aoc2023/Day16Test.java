package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day16Test {
    Tile[][] field;
    private int width;
    private int height;
    private final Tile nullTile = new DefaultTile();
    private static boolean showEnergized = false;

    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day16test.txt");
        var lines = Files.readAllLines(path);
        width = lines.get(0).length();
        height = lines.size();
        field = new Tile[width][height];
        toTiles(lines);
        connectTiles();

        field[0][0].fromLeft();
        var count = countEnergized();
        assertEquals(46, count);

        final var max = findBestConfiguration();
        assertEquals(51, max);
    }

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day16input.txt");
        var lines = Files.readAllLines(path);
        width = lines.get(0).length();
        height = lines.size();
        field = new Tile[width][height];
        toTiles(lines);
        connectTiles();

        field[0][0].fromLeft();
        var count = countEnergized();
        assertEquals(8323, count);

        final var max = findBestConfiguration();
        assertEquals(8491, max);
    }

    private long findBestConfiguration() {
        var max = 0L;
        for (int i = 0; i < width; i++) {
            clearField();
            field[i][0].toDown();
            var countVariant = countEnergized();
            if(countVariant > max) max = countVariant;
        }
        for (int i = 0; i < width; i++) {
            clearField();
            field[i][height-1].toTop();
            var countVariant = countEnergized();
            if(countVariant > max) max = countVariant;
        }
        for (int i = 0; i < height; i++) {
            clearField();
            field[0][i].toRight();
            var countVariant = countEnergized();
            if(countVariant > max) max = countVariant;
        }
        for (int i = 0; i < height; i++) {
            clearField();
            field[width-1][i].toLeft();
            var countVariant = countEnergized();
            if(countVariant > max) max = countVariant;
        }
        return max;
    }

    private void clearField() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                field[x][y].clear();
            }
        }
    }

    private long countEnergized() {
        long counter = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(field[x][y].isEnergized()) counter++;
            }
        }

        return counter;
    }

    private void connectTiles() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var tile = field[x][y];

                if (y == 0) {
                    tile.setUpNeighbor(nullTile);
                } else {
                    tile.setUpNeighbor(field[x][y - 1]);
                }
                if (y == height - 1) {
                    tile.setDownNeighbor(nullTile);
                } else {
                    tile.setDownNeighbor(field[x][y + 1]);
                }
                if (x == 0) {
                    tile.setLeftNeighbor(nullTile);
                } else {
                    tile.setLeftNeighbor(field[x-1][y]);
                }
                if (x == width - 1) {
                    tile.setRightNeighbor(nullTile);
                } else {
                    tile.setRightNeighbor(field[x+1][y]);
                }
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

    private void toTiles(List<String> lines) {
        for (int y = 0; y < height; y++) {
            var line = lines.get(y);
            for (int x = 0; x < width; x++) {
                field[x][y] = createTile(line.charAt(x));
            }
        }
    }

    private Tile createTile(char c) {
        return switch (c) {
            case '|' -> new VerticalSplitter();
            case '-' -> new HorizontalSplitter();
            case '\\' -> new BackslashMirror();
            case '/' -> new SlashMirror();
            case '.' -> new EmptySpace();
            default -> new DefaultTile();
        };
    }

    private interface Tile {
        void setUpNeighbor(Tile tile);
        void setDownNeighbor(Tile tile);
        void setRightNeighbor(Tile tile);
        void setLeftNeighbor(Tile tile);

        void fromLeft();
        void fromRight();
        void fromTop();
        void fromDown();

        void toLeft();
        void toRight();
        void toTop();
        void toDown();


        boolean isEnergized();

        void clear();
    }

    private abstract class AbstractTile implements Tile {
        Tile up;
        Tile down;
        Tile right;
        Tile left;
        boolean beamToRight = false;
        boolean beamToLeft = false;
        boolean beamToTop = false;
        boolean beamToDown = false;

        @Override
        public boolean isEnergized() {
            return beamToRight || beamToLeft || beamToDown || beamToTop;
        }

        @Override
        public void clear() {
            beamToRight = false;
            beamToLeft = false;
            beamToTop = false;
            beamToDown = false;
        }

        @Override
        public void setUpNeighbor(Tile tile) {
            up = tile;
        }

        @Override
        public void setDownNeighbor(Tile tile) {
            down = tile;
        }

        @Override
        public void setRightNeighbor(Tile tile) {
            right = tile;
        }

        @Override
        public void setLeftNeighbor(Tile tile) {
            left = tile;
        }

        @Override
        public void toLeft() {
            if (beamToLeft == false) {
                beamToLeft = true;
                left.fromRight();
            }
        }

        @Override
        public void toRight() {
            if (beamToRight == false) {
                beamToRight = true;
                right.fromLeft();
            }
        }

        @Override
        public void toTop() {
            if (beamToTop == false) {
                beamToTop = true;
                up.fromDown();
            }
        }

        @Override
        public void toDown() {
            if (beamToDown == false) {
                beamToDown = true;
                down.fromTop();
            }
        }
    }

    private class DefaultTile extends AbstractTile {
        @Override
        public void fromLeft() {
        }

        @Override
        public void fromRight() {
        }

        @Override
        public void fromTop() {
        }

        @Override
        public void fromDown() {
        }

        @Override
        public String toString() {
            return "";
        }
    }

    private class VerticalSplitter extends AbstractTile {
        @Override
        public void fromLeft() {
            toTop();
            toDown();
        }

        @Override
        public void fromRight() {
            toTop();
            toDown();
        }

        @Override
        public void fromTop() {
            toDown();
        }

        @Override
        public void fromDown() {
            toTop();
        }

        @Override
        public String toString() {
            if(showEnergized && isEnergized()) return "#";
            return "|";
        }
    }

    private class HorizontalSplitter extends AbstractTile {
        @Override
        public void fromLeft() {
            toRight();
        }

        @Override
        public void fromRight() {
            toLeft();
        }

        @Override
        public void fromTop() {
            toLeft();
            toRight();
        }

        @Override
        public void fromDown() {
            toLeft();
            toRight();
        }

        @Override
        public String toString() {
            if(showEnergized && isEnergized()) return "#";
            return "-";
        }
    }

    private class BackslashMirror extends AbstractTile {
        @Override
        public void fromLeft() {
            toDown();
        }

        @Override
        public void fromRight() {
            toTop();
        }

        @Override
        public void fromTop() {
            toRight();
        }

        @Override
        public void fromDown() {
            toLeft();
        }

        @Override
        public String toString() {
            if(showEnergized && isEnergized()) return "#";
            return "\\";
        }
    }

    private class SlashMirror extends AbstractTile {
        @Override
        public void fromLeft() {
            toTop();
        }

        @Override
        public void fromRight() {
            toDown();
        }

        @Override
        public void fromTop() {
            toLeft();
        }

        @Override
        public void fromDown() {
            toRight();
        }

        @Override
        public String toString() {
            if(showEnergized && isEnergized()) return "#";
            return "/";
        }
    }

    private class EmptySpace extends AbstractTile {

        @Override
        public void fromLeft() {
            toRight();
        }

        @Override
        public void fromRight() {
            toLeft();
        }

        @Override
        public void fromTop() {
            toDown();
        }

        @Override
        public void fromDown() {
            toTop();
        }

        @Override
        public String toString() {
            if(showEnergized && isEnergized()) return "#";
            return ".";
        }
    }
}

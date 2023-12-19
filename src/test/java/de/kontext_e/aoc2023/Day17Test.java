package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class Day17Test {
    Tile[][] field;
    private int width;
    private int height;
    private final Tile nullTile = new DefaultTile();

    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day17test.txt");
        var lines = Files.readAllLines(path);
        width = lines.get(0).length();
        height = lines.size();
        field = new Tile[width][height];
        System.out.println("Width: "+width+" Height: "+height);
        toTiles(lines);
        connectTiles();
        field[width - 1][height - 1].setEndTile(true);
        System.out.println("Sum X(0)     : " + sumX(0));
        System.out.println("Sum X(height): " + sumX(height - 1));
        System.out.println("Sum y(0)     : " + sumY(0));
        System.out.println("Sum y(widht) : " + sumY(width - 1));

        clearField();
        field[1][0].fromLeft(0, 0);
        var variant1 = field[width - 1][height - 1].getMinHeatLoss();
        System.out.println(variant1);

/*
        clearField();
        field[0][1].fromTop(0, 0);
        var variant2 = field[width - 1][height - 1].getMinHeatLoss();
        System.out.println(variant2);
*/
    }

    private long globalMaxHeatLoss = 600;

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day17input.txt");
        var lines = Files.readAllLines(path);
        width = lines.get(0).length();
        height = lines.size();
        field = new Tile[width][height];
        System.out.println("Width: "+width+" Height: "+height);
        toTiles(lines);
        connectTiles();
        field[width - 1][height - 1].setEndTile(true);
        System.out.println("Sum X(0)     : " + sumX(0));
        System.out.println("Sum X(height): " + sumX(height - 1));
        System.out.println("Sum y(0)     : " + sumY(0));
        System.out.println("Sum y(widht) : " + sumY(width - 1));

        clearField();
        field[1][0].fromLeft(0, 0);
        var variant1 = field[width - 1][height - 1].getMinHeatLoss();
        System.out.println(variant1);
/*
        clearField();
        field[0][1].fromTop(0, 0);
        var variant2 = field[width - 1][height - 1].getMinHeatLoss();
        System.out.println(variant2);
*/
    }

    private long sumY(int x) {
        long sum = 0;
        for (int y = 0; y < height; y++) {
            sum += field[x][y].getHeatLoss();
        }
        return sum;
    }

    private long sumX(int y) {
        long sum = 0;
        for (int x = 0; x < width; x++) {
            sum += field[x][y].getHeatLoss();
        }
        return sum;
    }

    private void clearField() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                field[x][y].clear();
            }
        }
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
        return new HeatLossTile(Integer.parseInt(""+c));
    }

    private interface Tile {
        void setUpNeighbor(Tile tile);
        void setDownNeighbor(Tile tile);
        void setRightNeighbor(Tile tile);
        void setLeftNeighbor(Tile tile);

        void fromLeft(long heatLoss, int sameDirection);
        void fromRight(long heatLoss, int sameDirection);
        void fromTop(long heatLoss, int sameDirection);
        void fromDown(long heatLoss, int sameDirection);

        void toLeft(long heatLoss, int sameDirection);
        void toRight(long heatLoss, int sameDirection);
        void toTop(long heatLoss, int sameDirection);
        void toDown(long heatLoss, int sameDirection);

        void clear();

        long getMinHeatLoss();

        long getHeatLoss();

        void setEndTile(boolean endTile);
    }

    private abstract class AbstractTile implements Tile {
        Tile up;
        Tile down;
        Tile right;
        Tile left;
        boolean endTile = false;

        @Override
        public void setEndTile(boolean endTile) {
            this.endTile = endTile;
        }

        @Override
        public void clear() {
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
        public void toLeft(long heatLoss, int sameDirection) {
            left.fromRight(heatLoss, sameDirection);
        }

        @Override
        public void toRight(long heatLoss, int sameDirection) {
            right.fromLeft(heatLoss, sameDirection);
        }

        @Override
        public void toTop(long heatLoss, int sameDirection) {
            up.fromDown(heatLoss, sameDirection);
        }

        @Override
        public void toDown(long heatLoss, int sameDirection) {
            down.fromTop(heatLoss, sameDirection);
        }
    }

    private class HeatLossTile extends AbstractTile {
        private static final int MAX_SAME_DIRECTION = 2;
        private static final int TOLERANCE = 10;
        private final int heatLoss;
        long minHeatLoss = 100_000_000L;


        public HeatLossTile(int heatLoss) {
            this.heatLoss = heatLoss;
        }

        @Override
        public void fromLeft(long heatLoss, int sameDirection) {
            if(heatLoss > globalMaxHeatLoss) return;

            var newHeatLoss = heatLoss + this.heatLoss;

            if (endTile) {
                System.out.println("Heat loss: "+newHeatLoss);
                globalMaxHeatLoss = newHeatLoss;
            }

            if (newHeatLoss < minHeatLoss) {
                minHeatLoss = newHeatLoss;
            }
            if(newHeatLoss < minHeatLoss+TOLERANCE) {
                if(sameDirection < MAX_SAME_DIRECTION) {
                    toRight(newHeatLoss, sameDirection+1);
                }
                toDown(newHeatLoss, 0);
                toTop(newHeatLoss, 0);
            }
        }

        @Override
        public void fromRight(long heatLoss, int sameDirection) {
            if(heatLoss > globalMaxHeatLoss) return;

            var newHeatLoss = heatLoss + this.heatLoss;

            if (endTile) {
                System.out.println("Heat loss: "+newHeatLoss);
                globalMaxHeatLoss = newHeatLoss;
            }

            if (newHeatLoss < minHeatLoss) {
                minHeatLoss = newHeatLoss;
            }
            if(newHeatLoss < minHeatLoss+TOLERANCE) {
                toDown(newHeatLoss, 0);
                toTop(newHeatLoss, 0);
                if(sameDirection < MAX_SAME_DIRECTION) {
                    toLeft(newHeatLoss, sameDirection+1);
                }
            }
        }

        @Override
        public void fromTop(long heatLoss, int sameDirection) {
            if(heatLoss > globalMaxHeatLoss) return;

            var newHeatLoss = heatLoss + this.heatLoss;

            if (endTile) {
                System.out.println("Heat loss: "+newHeatLoss);
                globalMaxHeatLoss = newHeatLoss;
            }

            if (newHeatLoss < minHeatLoss) {
                minHeatLoss = newHeatLoss;
            }
            if(newHeatLoss < minHeatLoss+TOLERANCE) {
                if(sameDirection < MAX_SAME_DIRECTION) {
                    toDown(newHeatLoss, sameDirection+1);
                }
                toLeft(newHeatLoss, 0);
                toRight(newHeatLoss, 0);
            }
        }

        @Override
        public void fromDown(long heatLoss, int sameDirection) {
            if(heatLoss > globalMaxHeatLoss) return;

            var newHeatLoss = heatLoss + this.heatLoss;

            if (endTile) {
                System.out.println("Heat loss: "+newHeatLoss);
                globalMaxHeatLoss = newHeatLoss;
            }

            if (newHeatLoss < minHeatLoss) {
                minHeatLoss = newHeatLoss;
            }
            if(newHeatLoss < minHeatLoss+TOLERANCE) {
                toRight(newHeatLoss, 0);
                toLeft(newHeatLoss, 0);
                if(sameDirection < MAX_SAME_DIRECTION) {
                    toTop(newHeatLoss, sameDirection+1);
                }
            }
        }

        @Override
        public long getHeatLoss() {
            return heatLoss;
        }

        @Override
        public long getMinHeatLoss() {
            return minHeatLoss;
        }

        @Override
        public void clear() {
            minHeatLoss = 100_000_000L;
        }
        @Override
        public String toString() {
            return ""+heatLoss;
        }
    }

    private class DefaultTile extends AbstractTile {
        @Override
        public void fromLeft(long heatLoss, int sameDirection) {}

        @Override
        public void fromRight(long heatLoss, int sameDirection) {}

        @Override
        public void fromTop(long heatLoss, int sameDirection) {}

        @Override
        public void fromDown(long heatLoss, int sameDirection) {}

        @Override
        public long getMinHeatLoss() {
            return Long.MAX_VALUE;
        }

        @Override
        public long getHeatLoss() {
            return 0;
        }

        @Override
        public String toString() {
            return "";
        }
    }
}


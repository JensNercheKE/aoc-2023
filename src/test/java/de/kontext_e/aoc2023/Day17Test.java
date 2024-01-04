package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

class Day17Test {
    Tile[][] field;
    private int width;
    private int height;
    private final Tile nullTile = new DefaultTile();
    private final List<Hike> openSet = new LinkedList<>();
    int[][] map;

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

        executeAStar();

        System.out.println("OpenSet: "+openSet);
    }

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day17input.txt");
        var lines = Files.readAllLines(path);
        width = lines.get(0).length();
        height = lines.size();
        field = new Tile[width][height];
        System.out.println("Width: "+width+" Height: "+height);
        toTiles(lines);
        //printField();

        connectTiles();
        field[width - 1][height - 1].setEndTile(true);

        executeAStar();

        System.out.println("OpenSet: "+openSet);

        // 920 too high
        // 927
    }

    private void executeAStar() {
        clearField();
        openSet.clear();

        final var startPoint = new Hike(field[0][0]);
        openSet.add(startPoint);
        startPoint.setMinHeatLoss(0);
        startPoint.calculateF();

        var lowestHeatLost = Integer.MAX_VALUE;
        while(openSet.isEmpty() == false)
        {
            var hike = selectLowestF();
            if(hike == null) break;
            if(hike.isEnd()) {
                if (hike.getMinHeatLoss() < lowestHeatLost) {
                    lowestHeatLost = hike.getMinHeatLoss();
                }
                System.out.println("found cheapest path: "+" with heat loss "+hike.getMinHeatLoss()+" hike length: "+hike.length()+" global lowest: "+lowestHeatLost+" open set: "+openSet.size());
            }
            process(hike);
        }
    }

    private void process(Hike hike) {
        openSet.remove(hike);
        var neighbors = hike.getNeighbors();
        for (var neighbor : neighbors) {
            if(neighbor.isInBorder(16) == false) continue;

            // dont go in circles
            if(hike.contains(neighbor)) continue;

            var tentativeGScore = hike.getMinHeatLoss() + neighbor.getHeatLoss();
            if(tentativeGScore <= (neighbor.getMinHeatLoss()) + 2) {
                if(tentativeGScore < neighbor.getMinHeatLoss()) {
                    neighbor.setMinHeatLoss(tentativeGScore);
                }

                Hike copy = hike.copy();
                copy.addTile(neighbor);
                copy.setMinHeatLoss((int) tentativeGScore);
                copy.calculateF();
                addToOpenSet(hike, copy);
            }
        }
    }

    private void addToOpenSet(Hike hike, Hike copy) {
        openSet.add(copy);
    }

    private int shortestDistance = Integer.MAX_VALUE;
    private Hike selectLowestF() {
        if(openSet.isEmpty()) return null;
        Hike result = null;
        Hike highestF = openSet.get(0);

        for (var hike : openSet) {
            if (result == null) {
                result = hike;
                continue;
            }
            if (hike.getF() < result.getF()) {
                result = hike;
            }
            if (hike.getF() > highestF.getF()) {
                highestF = hike;
            }
        }

        if (openSet.size() > 5000) {
            openSet.remove(highestF);
        }

        if (result.estimateDistance() < shortestDistance) {
            shortestDistance = result.estimateDistance();
            System.out.printf("# open set: %5d lowest F: %d highest F: %d shortest distance to target: %d\n", openSet.size(), result.getF(), highestF.getF(), shortestDistance);
        }

        //System.out.printf("# open set: %5d lowest F: %d highest F: %d shortest distance to target: %d\n", openSet.size(), result.getF(), highestF, shortestDistance);
        return result;
    }

    private class Hike {
        int f;
        List<Tile> path = new LinkedList<>();
        private int heatLoss;

        @Override
        public String toString() {
            return "f="+f+" l="+path.size()+" loss="+heatLoss;
        }

        public Hike() {}

        public Hike(Tile tile) {
            path.add(tile);
        }

        public Hike copy() {
            Hike theCopy = new Hike();
            theCopy.f = f;
            theCopy.path.addAll(path);
            theCopy.heatLoss = heatLoss;
            return theCopy;
        }

        public int getF() {
            return f;
        }

        public void setMinHeatLoss(int heatLoss) {
            this.heatLoss = heatLoss;
        }

        public void calculateF() {
            f = 0;
            for (var tile : path) {
                f += (int) tile.getHeatLoss();
            }
            f += (int) (estimateDistance() * 3);
        }

        int estimateDistance() {
            var last = path.get(path.size()-1);
            return (width - last.getX()) + (height - last.getY());
        }

        public boolean isEnd() {
            return path.get(path.size()-1).isEndTile();
        }

        public int getMinHeatLoss() {
            return heatLoss;
        }

        public String getPath() {
            var p = "";
            for (int i = 0; i < path.size()-1;i++) {
                var cameFrom = path.get(i);
                var now = path.get(i+1);
                char cameFromDirection;
                if (cameFrom.getY() == now.getY()) {
                    if (cameFrom.getX() < now.getX()) {
                        cameFromDirection = '>';
                    } else {
                        cameFromDirection = '<';
                    }
                } else {
                    if (cameFrom.getY() < now.getY()) {
                        cameFromDirection = 'v';
                    } else {
                        cameFromDirection = '^';
                    }
                }
                p += cameFromDirection;
            }
            return p;
        }

        public List<Tile> getNeighbors() {
            return path.get(path.size()-1).getNeighbors(getPath());
        }

        public void addTile(Tile neighbor) {
            path.add(neighbor);
        }

        public boolean contains(Tile neighbor) {
            return path.contains(neighbor);
        }

        public int length() {
            return path.size();
        }
    }

    private long globalMaxHeatLoss = 600;

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
                final var heatLoss = field[x][y].getHeatLoss();
                if (heatLoss < 4) {
                    System.out.print(heatLoss);
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private void toTiles(List<String> lines) {
        for (int y = 0; y < height; y++) {
            var line = lines.get(y);
            for (int x = 0; x < width; x++) {
                field[x][y] = createTile(x, y, line.charAt(x));
            }
        }
    }

    private Tile createTile(int x, int y, char c) {
        return new HeatLossTile(x, y, Integer.parseInt(""+c));
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

        void calculateF();

        int getF();

        boolean isEndTile();

        void setMinHeatLoss(long minHeatLoss);

        List<Tile> getNeighbors(String path);

        void setCameFrom(Tile tile);

        int getX();
        int getY();

        String getPath();

        boolean isInBorder(int borderWidth);
    }

    private class HeatLossTile extends AbstractTile {

        private static final int MAX_SAME_DIRECTION = 2;
        private static final int TOLERANCE = 10;
        private final int heatLoss;
        int f = 0;


        public HeatLossTile(int x, int y, int heatLoss) {
            this.x = x;
            this.y = y;
            this.heatLoss = heatLoss;
        }

        @Override
        public void fromLeft(long heatLoss, int sameDirection) {
            if(heatLoss > globalMaxHeatLoss) return;

            var newHeatLoss = heatLoss + this.heatLoss;

            if (endTile) {
                //System.out.println("Heat loss: "+newHeatLoss);
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
                //System.out.println("Heat loss: "+newHeatLoss);
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
                //System.out.println("Heat loss: "+newHeatLoss);
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
                //System.out.println("Heat loss: "+newHeatLoss);
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
        public void calculateF() {
            f = (int) (minHeatLoss + estimateDistance());
        }

        private int estimateDistance() {
            return ((width - x) + (height - y)) * 0;
        }

        public int getF() {
            return f;
        }

        @Override
        public long getMinHeatLoss() {
            return minHeatLoss;
        }

        @Override
        public void clear() {
            super.clear();
            f = 0;
        }
        @Override
        public String toString() {
            return ""+heatLoss+"/"+minHeatLoss+" ("+x+","+y+")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HeatLossTile that = (HeatLossTile) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private abstract class AbstractTile implements Tile {
        Tile up;
        Tile down;
        Tile right;
        Tile left;
        boolean endTile = false;
        Tile cameFrom;
        char cameFromDirection = ' ';
        long minHeatLoss = 100_000_000L; // gScore
        int x = -1;
        int y = -1;

        @Override
        public String getPath() {
            if(cameFrom == null) return "";
            return cameFrom.getPath()+cameFromDirection;
        }

        @Override
        public boolean isInBorder(int borderWidth) {
            if(x < borderWidth) return true;
            if(y > (height - borderWidth)) return true;
            return false;
        }

        @Override
        public long getMinHeatLoss() {
            return minHeatLoss;
        }

        @Override
        public void setMinHeatLoss(long minHeatLoss) {
            this.minHeatLoss = minHeatLoss;
        }

        @Override
        public List<Tile> getNeighbors(String path) {
            final var neighbors = new ArrayList<Tile>();
            if(up != null) neighbors.add(up);
            if(down != null) neighbors.add(down);
            if(left != null) neighbors.add(left);
            if(right != null) neighbors.add(right);

            if (path.endsWith(">")) {
                neighbors.remove(left);
            }
            if (path.endsWith("<")) {
                neighbors.remove(right);
            }
            if (path.endsWith("v")) {
                neighbors.remove(up);
            }
            if (path.endsWith("^")) {
                neighbors.remove(down);
            }

            if (path.endsWith(">>>")) {
                neighbors.remove(right);
            }
            if (path.endsWith("<<<")) {
                neighbors.remove(left);
            }
            if (path.endsWith("vvv")) {
                neighbors.remove(down);
            }
            if (path.endsWith("^^^")) {
                neighbors.remove(up);
            }
            return neighbors;
        }

        public Tile getCameFrom() {
            return cameFrom;
        }

        @Override
        public void setCameFrom(Tile cameFrom) {
            this.cameFrom = cameFrom;
            if (cameFrom.getY() == y) {
                if (cameFrom.getX() < x) {
                    cameFromDirection = '>';
                } else {
                    cameFromDirection = '<';
                }
            } else {
                if (cameFrom.getY() < y) {
                    cameFromDirection = 'v';
                } else {
                    cameFromDirection = '^';
                }
            }
        }

        @Override
        public void setEndTile(boolean endTile) {
            this.endTile = endTile;
        }

        @Override
        public boolean isEndTile() {
            return endTile;
        }

        @Override
        public void clear() {
            minHeatLoss = 100_000_000L;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
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
        public void calculateF() {

        }

        @Override
        public void clear() {
            super.clear();
        }

        @Override
        public int getF() {
            return Integer.MAX_VALUE;
        }

        @Override
        public String toString() {
            return "";
        }
    }
}


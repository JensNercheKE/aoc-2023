package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day18Test {
    Tile[][] field;
    private static int width;
    private static int height;
    private final Tile nullTile = new DefaultTile();
    long currentX = 0;
    long currentY = 0;
    List<Coordinate> coordinates = new LinkedList<>();

    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day18test.txt");
        var lines = Files.readAllLines(path);

        width = 15;
        height = 15;
        currentX = 3;
        currentY = 3;

        var dugOut = process(lines);
        assertEquals(62, dugOut);
        printField();
        //createCoordinates(lines);
    }

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day18input.txt");
        var lines = Files.readAllLines(path);

        width = 500;
        height = 650;
        currentX = 140;
        currentY = 400;

        var dugOut = process(lines);

        assertEquals(45159, dugOut);
    }

    private long process(List<String> lines) {

        field = new Tile[width][height];
        System.out.println("Width: "+width+" Height: "+height);
        toTiles();
        connectTiles();
        digOut(lines);
        markReachableTilesFromOutside();
        final var countFromField = countInside();

        coordinates.clear();
        // The digger starts in a 1 meter cube hole in the ground
        coordinates.add(new Coordinate(currentX, currentY));

        // They then dig the specified number of meters
        for (String line : lines) {
            final var splitted = line.split(" ");
            var direction = splitted[0];
            var count = Long.parseLong(splitted[1]);
            switch (direction) {
                case "R" -> {
                    // right
                    currentX += count;
                    coordinates.add(new Coordinate(currentX, currentY));
                }
                case "D" -> {
                    // down
                    currentY += count;
                    coordinates.add(new Coordinate(currentX, currentY));
                }
                case "L" -> {
                    // left
                    currentX -= count;
                    coordinates.add(new Coordinate(currentX, currentY));
                }
                case "U" -> {
                    // up
                    currentY -= count;
                    coordinates.add(new Coordinate(currentX, currentY));
                }
                default -> {}
            }
        }

        long minX = Long.MAX_VALUE;
        long maxX = Long.MIN_VALUE;
        long minY = Long.MAX_VALUE;
        long maxY = Long.MIN_VALUE;
        for (Coordinate coordinate : coordinates) {
            if (minX > coordinate.x()) {
                minX = coordinate.x();
            }
            if (maxX < coordinate.x()) {
                maxX = coordinate.x();
            }
            if (maxY < coordinate.y()) {
                maxY = coordinate.y();
            }
            if (minY > coordinate.y()) {
                minY = coordinate.y();
            }
        }
        System.out.printf("min x = %d min y = %d max x = %d max y = %d\n", minX, minY, maxX, maxY);

        var countFromRay = 0L;
        for (var y = minY; y <= maxY; y++) {
            boolean inside = false;
            var insideStart = -1L;

            for(var raystart = minX - 2; raystart < maxX;) {
                var nextEdge = findNextEdge(raystart, y);
                if (nextEdge != null) {
                    System.out.println("Next edge: " + nextEdge);
                    if (nextEdge.isHorizontal()) {
                        if (inside) {
                            countFromRay += nextEdge.start().x() - insideStart;
                        }
                        countFromRay += nextEdge.length();
                        raystart = nextEdge.end().x() + 1;
                        System.out.println("New count X: " + countFromRay);
                        inside = isInside(nextEdge.end().x() + 1, y);
                        insideStart = nextEdge.end.x() + 1;
                        System.out.println("Becomes inside: "+inside);
                    } else {
                        if (inside == false) {
                            inside = true;
                            insideStart = nextEdge.start.x();
                        } else {
                            countFromRay += nextEdge.end.x() - insideStart + 1;
                            System.out.println("New count Y: " + countFromRay);
                            inside = false;
                        }
                        raystart = nextEdge.end().x() + 1;
                    }
                } else {
                    raystart = maxX;
                }
            }
        }


        return countFromField;
    }

    private Edge findNextEdge(long rayX, long y) {
        Coordinate nextStart = new Coordinate(10000, 10000);
        Coordinate nextEnd = new Coordinate(10000, 10000);

        for (int i = 0; i < coordinates.size() - 1; i++) {
            var start = coordinates.get(i);
            var end = coordinates.get(i+1);

            if (start.y() == end.y()) {
                // horizontal edge
                if (start.y() == y) {
                    // on same line
                    if (start.x() > rayX || end.x() > rayX) {
                        // right side from ray
                        if (start.x() < nextStart.x()) {
                            // more left than current
                            nextStart = start;
                            nextEnd = end;
                        }
                        if (end.x() < nextEnd.x()) {
                            // could go from right to left
                            nextStart = start;
                            nextEnd = end;
                        }
                    }
                }
            }

            if (start.x() == end.x()) {
                // vertical edge
                if ((start.y() < y && end.y() > y)
                        || (start.y() > y && end.y() < y)) {
                    // crossing the ray
                    if (start.x() > rayX) {
                        // right side from ray
                        if (start.x() < nextStart.x() || nextStart.x() > nextEnd.x()) {
                            // more left than current
                            nextStart = start;
                            nextEnd = end;
                        }
                    }
                }
            }
        }

        if (nextStart.x() != 10000) {
            if (nextStart.x() > nextEnd.x()) {
                var tmp = nextStart;
                nextStart = nextEnd;
                nextEnd = tmp;
            }
            return new Edge(nextStart, nextEnd);
        }

        return null;
    }

    private record Edge(Coordinate start, Coordinate end) {

        public boolean isHorizontal() {
            return start.y() == end.y();
        }

        public long length() {
            if (isHorizontal()) {
                return end.x() - start.x() + 1;
            } else {
                return end.y() - start.y() + 1;
            }
        }
    }

    private void createCoordinates(List<String> lines) {
        coordinates.clear();
        // The digger starts in a 1 meter cube hole in the ground
        coordinates.add(new Coordinate(currentX, currentY));

        // They then dig the specified number of meters
        for (String line : lines) {
            try {
                final var splitted = line.split(" ");
                var colorCode = splitted[2];
                var direction = colorCode.substring(colorCode.length() - 2, colorCode.length() - 1);
                var count = Long.parseLong(colorCode.substring(2, colorCode.length() - 2), 16);

                switch (direction) {
                    case "0" -> {
                        // right
                        currentX += count;
                        coordinates.add(new Coordinate(currentX, currentY));
                    }
                    case "1" -> {
                        // down
                        currentY += count;
                        coordinates.add(new Coordinate(currentX, currentY));
                    }
                    case "2" -> {
                        // left
                        currentX -= count;
                        coordinates.add(new Coordinate(currentX, currentY));
                    }
                    case "3" -> {
                        // up
                        currentY -= count;
                        coordinates.add(new Coordinate(currentX, currentY));
                    }
                    default -> {}
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }

        long minX = Long.MAX_VALUE;
        long maxX = Long.MIN_VALUE;
        long minY = Long.MAX_VALUE;
        long maxY = Long.MIN_VALUE;
        for (Coordinate coordinate : coordinates) {
            if (minX > coordinate.x()) {
                minX = coordinate.x();
            }
            if (maxX < coordinate.x()) {
                maxX = coordinate.x();
            }
            if (maxY < coordinate.y()) {
                maxY = coordinate.y();
            }
            if (minY > coordinate.y()) {
                minY = coordinate.y();
            }
        }
        System.out.printf("min x = %d min y = %d max x = %d max y = %d\n", minX, minY, maxX, maxY);

        long insideCounter = 0;
        for(long y = minY - 1; y < maxY + 1; y++) {
            for (long x = minX - 1; x < maxX + 1; x++) {
                final var inside = isInside(x, y);

                if (inside == true) {
                    insideCounter++;
                }
            }
            System.out.println("y: "+y);
        }
        System.out.println("Inside counter: "+insideCounter);
    }

    private boolean isInside(long x, long y) {
        boolean inside = false;
        for (int i = 0; i < coordinates.size() - 1; i++) {
            var first = coordinates.get(i);
            var second = coordinates.get(i + 1);
            var testy = y;
            var testx = x;
            var vertxi = first.x();
            var vertyi = first.y();
            var vertxj = second.x();
            var vertyj = second.y();

            if ( ((vertyi>testy) != (vertyj>testy)) &&
                    (testx < (vertxj-vertxi) * (testy-vertyi) / (vertyj-vertyi) + vertxi) )
                inside = !inside;
        }
        return inside;
    }

    private long countInside() {
        var filled = -1;
        while(filled != 0) {
            filled = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (field[x][y].isOutside() == false && field[x][y].isDugOut() == false && isReachable(x, y)) {
                        field[x][y].markAsOutside();
                        filled++;
                    }
                }
            }
            if (filled == 0) {
                break;
            }
        }

        long counter = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (field[x][y].isOutside() == false) {
                    counter++;
                }
            }
        }
        System.out.println(counter);
        return counter;
    }

    private void markReachableTilesFromOutside() {
        {
            // oben
            int y = 0;
            for (int x = 0; x < width; x++) {
                if (field[x][y].isDugOut() == false && isReachable(x, y)) {
                    field[x][y].markAsOutside();
                }
            }
        }

        {
            // unten
            int y = height - 1;
            for (int x = 0; x < width; x++) {
                if (field[x][y].isDugOut() == false && isReachable(x, y)) {
                    field[x][y].markAsOutside();
                }
            }
        }

        {
            // links
            int x = 0;
            for (int y = 0; y < height; y++) {
                if (field[x][y].isDugOut() == false && isReachable(x, y)) {
                    field[x][y].markAsOutside();
                }
            }
        }

        {
            // rechts
            int x = width - 1;
            for (int y = 0; y < height; y++) {
                if (field[x][y].isDugOut() == false && isReachable(x, y)) {
                    field[x][y].markAsOutside();
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
            if (tile != null && tile.isOutside()) {
                return true;
            }
        }


        return false;
    }
    private record Coordinate(long x, long y) {
        static int scale = 1;
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

    private void digOut(List<String> lines) {
        // The digger starts in a 1 meter cube hole in the ground
        field[(int) currentX][(int) currentY].digOut();

        // They then dig the specified number of meters
        for (String line : lines) {
            try {
                final var splitted = line.split(" ");
                var direction = splitted[0];
                var count = Integer.parseInt(splitted[1]);
                var colorCode = splitted[2];
                dig(direction, count);
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new RuntimeException(line + " current x: "+currentX+" current y: "+currentY);
            }
        }
    }

    void dig(String direction, int count) {
        switch (direction) {
            case "R" -> digRight(count);
            case "L" -> digLeft(count);
            case "U" -> digUp(count);
            case "D" -> digDown(count);
            default -> {}
        }
    }

    private void digRight(int count) {
        for(int i = 0; i < count; i++) {
            currentX++;
            field[(int) currentX][(int) currentY].digOut();
        }
    }
    private void digLeft(int count) {
        for(int i = 0; i < count; i++) {
            currentX--;
            field[(int) currentX][(int) currentY].digOut();
        }
    }
    private void digUp(int count) {
        for(int i = 0; i < count; i++) {
            currentY--;
            field[(int) currentX][(int) currentY].digOut();
        }
    }
    private void digDown(int count) {
        for(int i = 0; i < count; i++) {
            currentY++;
            field[(int) currentX][(int) currentY].digOut();
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

    private void toTiles() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                field[x][y] = createTile();
            }
        }
    }

    private Tile createTile() {
        return new GroundTile();
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

        void digOut();

        boolean isDugOut();

        void markAsOutside();

        boolean isOutside();
    }

    private abstract class AbstractTile implements Tile {
        Tile up;
        Tile down;
        Tile right;
        Tile left;
        boolean dugOut = false;
        boolean outside = false;

        @Override
        public void digOut() {
            dugOut = true;
        }

        @Override
        public boolean isDugOut() {
            return dugOut;
        }

        @Override
        public void markAsOutside() {
            outside = true;
        }

        @Override
        public boolean isOutside() {
            return outside;
        }

        @Override
        public void clear() {
            dugOut = false;
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

    private class GroundTile extends AbstractTile {
        @Override
        public void fromLeft(long heatLoss, int sameDirection) {}

        @Override
        public void fromRight(long heatLoss, int sameDirection) {}

        @Override
        public void fromTop(long heatLoss, int sameDirection) {}

        @Override
        public void fromDown(long heatLoss, int sameDirection) {}

        @Override
        public String toString() {
            if(outside) return "O";
            if(dugOut) return "#";

            return ".";
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
        public String toString() {
            return "";
        }
    }

}

package de.kontext_e.aoc2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day18Test {
    Tile[][] field;
    private static int width;
    private static int height;
    private final Tile nullTile = new DefaultTile();
    long currentX = 0;
    long currentY = 0;
    long minX = Long.MAX_VALUE;
    long maxX = Long.MIN_VALUE;
    long minY = Long.MAX_VALUE;
    long maxY = Long.MIN_VALUE;
    List<Coordinate> coordinates = new LinkedList<>();
    private final List<Edge> edges = new ArrayList<>();
    private final List<Integer> lengths = new ArrayList<>();

    @BeforeEach
    void setUp() {
        width = 0;
        height = 0;
        currentX = 0;
        currentY = 0;
        coordinates.clear();
        minX = Long.MAX_VALUE;
        maxX = Long.MIN_VALUE;
        minY = Long.MAX_VALUE;
        maxY = Long.MIN_VALUE;
        edges.clear();
        lengths.clear();
    }

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
        //printField();
        //createCoordinates(lines);

        createCoordinates2(lines);
        drawTrenches();
        countWithRays();

    }

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day18input.txt");
        var lines = Files.readAllLines(path);

        width = 500;
        height = 650;
        currentX = 140;
        currentY = 400;
/*

        var dugOut = process(lines);
        assertEquals(45159, dugOut);
*/

        createCoordinates2(lines);
        toEdges();
//        drawTrenches();
        countWithRays();
        System.out.println("horizontal lines detected: "+horizontalLinesDetected);
        var count = 0;
        for (Edge edge : edges) {
            if(edge.isHorizontal()) {
                count++;
                if (isNotDetected(edge)) {
                    System.out.println("Edge was not detected: "+edge);
                }
            }
  //          System.out.println("Horizontal edge in line "+edge.start.y);
        }
        System.out.println("horizontal lines counted : "+count);


/*
        currentX = 3029000;
        currentY = 13536600;
        createCoordinates(lines);
        toEdges();
        var ggt = ggt(lengths);
        System.out.println("GGT = "+ggt);
*/

    }

    private boolean isNotDetected(Edge edge) {
        for (Edge edge1 : horizontalEdgesDetected) {
            if(edge.equals(edge1)) return false;
        }

        return true;
    }

    private void countWithRays() {
        horizontalEdgesDetected.clear();
        horizontalLinesDetected = 0;
        var countFromRay = 0L;
        for (var y = minY; y <= maxY; y++) {
            boolean inside = false;
            var insideStart = -1L;

            if (y == 300) {
                System.out.println("hier");
            }

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
                        System.out.println("New count X: " + countFromRay+" on y "+nextEdge.end.y);
                        horizontalLinesDetected++;
                        horizontalEdgesDetected.add(nextEdge);
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
    }

    @Test
    void introWithBulgeRemoval() throws IOException {
        Path path = Paths.get("src/test/resources/day18test.txt");
        var lines = Files.readAllLines(path);

        width = 15;
        height = 15;
        currentX = 3;
        currentY = 3;

        var dugOut = process(lines);
        assertEquals(62, dugOut);
        //printField();
        //createCoordinates(lines);

        createCoordinates2(lines);
        drawTrenches();

        var bulge = lookForBulge(0);
        if(bulge.index != -1) {
            removeBulge(bulge);
            drawTrenches();
        }

        bulge = lookForBulge(bulge.index + 1);
        if(bulge.index != -1) {
            removeBulge(bulge);
            drawTrenches();
        }

        bulge = lookForBulge(bulge.index + 1);
        if(bulge.index != -1) {
            removeBulge(bulge);
            drawTrenches();
        }

    }

    private void toEdges() {
        edges.clear();
        for (int i = 0; i < coordinates.size() - 1; i++) {
            final var edge = new Edge(coordinates.get(i), coordinates.get(i + 1));
            edges.add(edge);
            lengths.add((int) edge.length());
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
        width = (int) (maxX - minX + 1);
        height = (int) (maxY - minY + 1);
        System.out.printf("min x = %d min y = %d max x = %d max y = %d width = %d height = %d\n", minX, minY, maxX, maxY, width, height);
    }

    private void removeBulge(Bulge bulge) {
        int i = bulge.index;
        var edge1 = new Edge(coordinates.get(i), coordinates.get(i + 1));
        var edge2 = new Edge(coordinates.get(i+1), coordinates.get(i + 2));
        var edge3 = new Edge(coordinates.get(i+2), coordinates.get(i + 3));

        if ("ESW".equalsIgnoreCase(bulge.type)) {
            System.out.println("Remove ESW bulge starting at "+i);
            coordinates.remove(edge2.start);
            coordinates.remove(edge2.end);
            if (edge1.length() > edge3.length()) {
                coordinates.remove(edge3.start);
                coordinates.remove(edge3.end);
                coordinates.add(i + 1, new Coordinate(edge3.end.x, edge1.start.y));
            } else {
                coordinates.remove(edge1.start);
                coordinates.add(i, new Coordinate(edge1.start.x, edge3.start.y));
            }
        }
        if ("WNE".equalsIgnoreCase(bulge.type)) {
            System.out.printf("Remove %s bulge starting at %d\n", bulge.type, i);
            coordinates.remove(edge2.start);
            coordinates.remove(edge2.end);
            if (edge1.length() > edge3.length()) {
                coordinates.remove(edge3.start);
                coordinates.remove(edge3.end);
            } else {
                coordinates.remove(edge1.start);
                coordinates.remove(edge1.end);
            }
        }
    }

    private Bulge lookForBulge(int startIndex) {
        for (int i = startIndex; i < coordinates.size() - 3; i++) {
            var edge1 = new Edge(coordinates.get(i), coordinates.get(i + 1));
            var edge2 = new Edge(coordinates.get(i+1), coordinates.get(i + 2));
            var edge3 = new Edge(coordinates.get(i+2), coordinates.get(i + 3));
            var candidate = ""+edge1.direction()+edge2.direction()+edge3.direction();
            if("ESW".equals(candidate)) return new Bulge(candidate, i);
            if("SWN".equals(candidate)) return new Bulge(candidate, i);
            if("WNE".equals(candidate)) return new Bulge(candidate, i);
            if("NES".equals(candidate)) return new Bulge(candidate, i);
        }
        return new Bulge("", -1);
    }

    private record Bulge(String type, int index) { }

    private void createCoordinates2(List<String> lines) {
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

        width = (int) (maxX - minX + 1);
        height = (int) (maxY - minY + 1);

        System.out.printf("min x = %d min y = %d max x = %d max y = %d width = %d height = %d\n", minX, minY, maxX, maxY, width, height);
    }

    private void drawTrenches() {
        final var arrayWidth = (int) (width);
        final var arrayHeight = (int) (height);
        char[][] canvas = new char[arrayWidth][arrayHeight];
        for(int i = 0; i < arrayWidth; i++) {
            Arrays.fill(canvas[i], '.');
        }

        int cx = (int) coordinates.get(0).x();
        int cy = (int) coordinates.get(0).y();
        cx -= minX;
        cy -= minY;
        canvas[cx][cy] = '#';
        for (int i = 0; i < coordinates.size() - 1; i++) {
            var edge = new Edge(coordinates.get(i), coordinates.get(i + 1));
            var delta = 1;
            if(edge.isBackwards()) delta = -1;
            for (int count = 0; count < edge.length() - 1; count++) {
                if (edge.isHorizontal()) {
                    cx += delta;
                } else {
                    cy += delta;
                }
                canvas[cx][cy] = '#';
            }
        }

        for (int y = 0; y < arrayHeight; y++) {
            for (int x = 0; x < arrayWidth; x++) {
                System.out.print(canvas[x][y]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private long process(List<String> lines) {

        field = new Tile[width][height];
        System.out.println("Width: "+width+" Height: "+height);
        toTiles();
        connectTiles();
        digOut(lines);
        markReachableTilesFromOutside();
        return countInside();
    }

    private List<Edge> horizontalEdgesDetected = new ArrayList<>();

    private int horizontalLinesDetected = 0;
    private Edge findNextEdge(long rayX, long y) {
        Coordinate nextStart = new Coordinate(100_000_000, 100_000_000);
        Coordinate nextEnd = new Coordinate(100_000_000, 100_000_000);

        for (int i = 0; i < coordinates.size() - 1; i++) {
            var start = coordinates.get(i);
            var end = coordinates.get(i+1);

            if (start.y() == end.y()) {
                // horizontal edge
                if (start.y() == y) {
                    if (y == 300) {
                        System.out.println("hier");
                    }
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
                        if (start.x() < nextStart.x() || start.x() < nextEnd.x()) {
                            // more left than current
                            nextStart = start;
                            nextEnd = end;
                            return createAscendingEdge(nextStart, nextEnd);
                        }
                    }
                }
            }
        }

        if (nextStart.x() != 100_000_000) {
            return createAscendingEdge(nextStart, nextEnd);
        }

        return null;
    }

    private static Edge createAscendingEdge(Coordinate nextStart, Coordinate nextEnd) {
        if (nextStart.x() > nextEnd.x()) {
            var tmp = nextStart;
            nextStart = nextEnd;
            nextEnd = tmp;
        }
        return new Edge(nextStart, nextEnd);
    }

    private record Edge(Coordinate start, Coordinate end) {

        public boolean isHorizontal() {
            return start.y() == end.y();
        }

        public long length() {
            if (isHorizontal()) {
                return Math.abs(end.x() - start.x()) + 1;
            } else {
                return Math.abs(end.y() - start.y()) + 1;
            }
        }

        public boolean isBackwards() {
            if (isHorizontal()) {
                return end.x < start.x;
            } else {
                return end.y < start.y;
            }
        }

        public char direction() {
            if (isHorizontal()) {
                if (isBackwards()) return 'W';
                else return 'E';
            } else {
                if(isBackwards()) return 'N';
                else return 'S';
            }
        }

        public boolean isPerpendicular(Edge edge2) {
            return isHorizontal() && !edge2.isHorizontal()
                    || !isHorizontal() && edge2.isHorizontal();
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge edge = (Edge) o;
            return Objects.equals(start, edge.start) && Objects.equals(end, edge.end)
                    || Objects.equals(start, edge.end) && Objects.equals(end, edge.start);
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }

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
    private class Coordinate {
        long x;
        long y;
        public Coordinate(long x, long y) {
            this.x = x;
            this.y = y;
        }

        public long x() {
            return x;
        }

        public long y() {
            return y;
        }
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


        public void updateX(long x) {
            this.x = x;
        }

        public void updateY(long y) {
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coordinate that = (Coordinate) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
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
    public static int ggt(List<Integer> numbers) {
        if(numbers == null || numbers.size() < 2) throw new IllegalArgumentException("Illegal numbers: " + numbers);

        final var a = numbers.get(0);
        final var b = numbers.get(1);
        var x = Day08Test.ggt(a, b);
        if (numbers.size() > 2) {
            for (int i = 2; i  < numbers.size(); i++) {
                final var b1 = numbers.get(i);
                x = Day08Test.ggt(x, b1);
            }
        }
        return (int) x;
    }
}
